// Target Settings 관련 함수
function getTargetSettings() {
    return {
        gender: document.getElementById('targetGender').value,
        ageGroup: document.getElementById('targetAge').value,
        productCategory: document.getElementById('productCategory').value,
        seasonEvent: document.getElementById('seasonEvent').value,
        adTone: document.getElementById('adTone').value
    };
}

function buildPromptFromSettings(settings, additionalRequests) {
    const requestData = {
        gender: settings.gender,
        ageGroup: settings.ageGroup,
        productCategory: settings.productCategory,
        seasonEvent: settings.seasonEvent,
        adTone: settings.adTone,
        additionalRequests: additionalRequests
    };

    return JSON.stringify(requestData);
}

async function generatePrompt() {
    const inputPrompt = document.getElementById('inputPrompt').value;
    const generatedPrompt = document.getElementById('generatedPrompt');
    const generatePromptBtn = document.getElementById('generatePromptBtn');
    
    generatePromptBtn.disabled = true;
    
    try {
        const targetSettings = getTargetSettings();
        const fullPrompt = buildPromptFromSettings(targetSettings, inputPrompt);

        // GPT API를 통한 프롬프트 생성 요청
        const response = await fetch('/generate_prompt', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            },
            body: fullPrompt
        });

        const data = await response.json();
        
        if (!response.ok) {
            throw new Error(data.error || 'Failed to generate prompt');
        }

        // 생성된 프롬프트를 텍스트 영역에 표시
        generatedPrompt.value = data.generated_prompt;
        
        // 생성된 프롬프트를 이용하여 예제 이미지 생성
        await generateExampleImages(data.generated_prompt);
    } catch (error) {
        console.error('Error:', error);
        alert('Error generating prompt: ' + error.message);
    } finally {
        generatePromptBtn.disabled = false;
    }
}

async function generateExampleImages(prompt) {
    if (!prompt) {
        prompt = document.getElementById('generatedPrompt').value.trim();
    }
    
    if (!prompt) {
        alert('Please enter a prompt first');
        return;
    }

    const loadingIndicator = document.getElementById('loadingIndicator');
    const previewImages = document.querySelectorAll('.preview-image');
    
    previewImages.forEach(img => {
        img.style.display = 'none';
    });
    
    loadingIndicator.style.display = 'block';
	try {
        const data = {
            prompt: prompt
        };

        const response = await generateWithPath('/generate_examples', data);

        response.image_paths.forEach((path, index) => {
            const img = document.getElementById(`preview${index + 1}`);
            img.src = `/output/${path}`;
            img.style.display = 'block';
        });

    } catch (error) {
        alert('Error generating images: ' + error.message);
    } finally {
        loadingIndicator.style.display = 'none';
    }
}

function goToVideoGeneration() {
    const prompt = document.getElementById('generatedPrompt').value.trim();
    if (prompt) {
        sessionStorage.setItem('videoPrompt', prompt);
    }
    window.location.href = '/video_gen';
}

// 페이지 로드 시 초기화 및 전달된 프롬프트 처리
window.addEventListener('load', function() {
    // Video Gen 페이지에서 전달된 프롬프트가 있는지 확인
    const transferredPrompt = sessionStorage.getItem('transferredPrompt');
    if (transferredPrompt) {
        const generatedPromptElement = document.getElementById('generatedPrompt');
        if (generatedPromptElement) {
            generatedPromptElement.value = transferredPrompt;
            // 프롬프트를 불러온 후 sessionStorage에서 삭제
            sessionStorage.removeItem('transferredPrompt');
            // Video Gen 페이지에서 돌아온 경우에는 예제 이미지 생성하지 않음
        }
    }

    // 이전 프롬프트 복원 (로컬 스토리지에서)
    const previousPrompt = localStorage.getItem('previousPrompt');
    if (previousPrompt && !transferredPrompt) {  // 전달된 프롬프트가 없을 때만 이전 프롬프트 복원
        document.getElementById('generatedPrompt').value = previousPrompt;
        localStorage.removeItem('previousPrompt');
    }
    
    // 경로 설정 초기화
    initializePath();
});

window.addEventListener('load', function() {
    // TargetSettingsHandler 초기화
    window.targetSettingsHandler = new TargetSettingsHandler();
    window.targetSettingsHandler.loadSettings();  // 초기 설정 로드

    // Video Gen 페이지에서 전달된 프롬프트가 있는지 확인
    const transferredPrompt = sessionStorage.getItem('transferredPrompt');
    if (transferredPrompt) {
        const generatedPromptElement = document.getElementById('generatedPrompt');
        if (generatedPromptElement) {
            generatedPromptElement.value = transferredPrompt;
            sessionStorage.removeItem('transferredPrompt');
        }
    }

    // 이전 프롬프트 복원
    const previousPrompt = localStorage.getItem('previousPrompt');
    if (previousPrompt && !transferredPrompt) {
        document.getElementById('generatedPrompt').value = previousPrompt;
        localStorage.removeItem('previousPrompt');
    }
    
    // 경로 설정 초기화
    initializePath();
});