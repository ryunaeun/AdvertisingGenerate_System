// 현재 페이지의 프롬프트 입력 요소 ID 가져오기
function getPromptElementId() {
    // URL을 기반으로 현재 페이지 확인
    const currentPath = window.location.pathname;
    // video_gen 페이지면 'prompt', 그 외(prompt_gen)면 'generatedPrompt' 반환
    return currentPath.includes('video_gen') ? 'prompt' : 'generatedPrompt';
}

// 프롬프트 저장 함수
function savePrompt() {
    showSavePromptDialog();
}

// 프롬프트 불러오기 함수
function loadPrompt() {
    showLoadPromptDialog();
}

// 저장 다이얼로그 표시
function showSavePromptDialog() {
    const dialog = document.createElement('div');
    dialog.className = 'modal';
    dialog.innerHTML = `
        <div class="modal-content">
            <h2>Save Prompt</h2>
            <input type="text" id="saveFileName" placeholder="Enter file name">
            <div class="modal-buttons">
                <button onclick="savePromptFile()">Save</button>
                <button onclick="closeModal()">Cancel</button>
            </div>
        </div>
    `;
    document.body.appendChild(dialog);
}

// 불러오기 다이얼로그 표시
function showLoadPromptDialog() {
    const dialog = document.createElement('div');
    dialog.className = 'modal';
    dialog.innerHTML = `
        <div class="modal-content">
            <h2>Load Prompt</h2>
            <div id="loadPromptContent">
                <p>Loading prompt files...</p>
            </div>
            <div class="modal-buttons">
                <button onclick="loadPromptFile()">Load</button>
                <button onclick="closeModal()">Cancel</button>
            </div>
        </div>
    `;
    document.body.appendChild(dialog);
    loadPromptFiles();
}

// 모달 닫기
function closeModal() {
    const modal = document.querySelector('.modal');
    if (modal) {
        modal.remove();
    }
}

// 프롬프트 파일 저장
async function savePromptFile() {
    const fileName = document.getElementById('saveFileName').value;
    if (!fileName) {
        alert("Please enter a file name.");
        return;
    }

    const promptElementId = getPromptElementId();
    const promptElement = document.getElementById(promptElementId);
    
    if (!promptElement) {
        console.error(`Prompt element with id '${promptElementId}' not found`);
        alert('Error: Could not find prompt input element');
        return;
    }

    const promptContent = promptElement.value;
    const userId = document.getElementById('userId').value;

    try {
        const response = await fetch('/save_prompt', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify({
                userId: userId,
                fileName: fileName,
                content: promptContent
            })
        });

        const data = await response.json();
        
        if (response.ok) {
            alert('Prompt saved successfully!');
            closeModal();
        } else {
            throw new Error(data.error || 'Failed to save prompt');
        }
    } catch (error) {
        console.error('Error saving prompt:', error);
        alert('Error saving prompt: ' + error.message);
    }
}

// 프롬프트 파일 불러오기
async function loadPromptFile() {
    const selectedFile = document.getElementById('loadFileSelect')?.value;
    if (!selectedFile) {
        alert("Please select a file to load.");
        return;
    }

    const userId = document.getElementById('userId').value;

    try {
        const response = await fetch(`/load_prompt?userId=${userId}&fileName=${selectedFile}`);
        const data = await response.json();
        
        if (response.ok && data.content) {
            const promptElementId = getPromptElementId();
            const promptElement = document.getElementById(promptElementId);
            
            if (!promptElement) {
                console.error(`Prompt element with id '${promptElementId}' not found`);
                alert('Error: Could not find prompt input element');
                return;
            }

            promptElement.value = data.content;
            alert("Prompt loaded successfully!");
            closeModal();
        } else {
            throw new Error(data.error || "Failed to load prompt content.");
        }
    } catch (error) {
        console.error('Error loading prompt file:', error);
        alert("Error loading prompt file: " + error.message);
    }
}

// 저장된 프롬프트 파일 목록 불러오기
async function loadPromptFiles() {
    const userId = document.getElementById('userId').value;

    try {
        const response = await fetch(`/load_prompts?userId=${userId}`);
        const data = await response.json();
        
        const loadPromptContent = document.getElementById('loadPromptContent');
        if (data.files && data.files.length > 0) {
            loadPromptContent.innerHTML = `
                <select id="loadFileSelect" class="prompt-select">
                    ${data.files.map(file => `<option value="${file}">${file}</option>`).join('')}
                </select>
            `;
        } else {
            loadPromptContent.innerHTML = '<p>No saved prompts found.</p>';
            // Disable the Load button if no files are available
            const loadButton = document.querySelector('.modal-buttons button:first-child');
            if (loadButton) {
                loadButton.disabled = true;
            }
        }
    } catch (error) {
        console.error('Error loading prompt files:', error);
        document.getElementById('loadPromptContent').innerHTML = 
            '<p>Error loading prompt files. Please try again.</p>';
    }
}