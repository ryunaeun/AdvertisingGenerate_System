// Path Setting Functions
function validateSavePath(userId, subPath) {
    if (!userId || !subPath) {
        throw new Error('Please enter both User ID and Save Path');
    }
    return `${userId}\\${subPath}`;
}

async function generateWithPath(endpoint, data) {
    const userId = document.getElementById('userId').value.trim();
    const subPath = document.getElementById('subPath').value.trim();
    
    try {
        const savePath = validateSavePath(userId, subPath);
        
        const response = await fetch(endpoint, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify({
                ...data,
                savePath: savePath
            })
        });

        if (!response.ok) {
            const responseData = await response.json();
            throw new Error(responseData.error || 'Failed to generate');
        }

        return await response.json();
    } catch (error) {
        throw new Error('Error generating content: ' + error.message);
    }
}

// Get default subPath based on page type
function getDefaultSubPath() {
    // Check if current page is video_gen
    const isVideoGen = window.location.pathname.includes('video_gen');
    return isVideoGen ? 'videos' : 'examples';
}

// Initialize Path from Storage
function initializePath() {
    const savedUserId = localStorage.getItem('userId') || 'KTaivle';
    const defaultSubPath = getDefaultSubPath();
    const savedSubPath = localStorage.getItem('subPath') || defaultSubPath;
    
    const userIdInput = document.getElementById('userId');
    const subPathInput = document.getElementById('subPath');
    
    if (userIdInput) userIdInput.value = savedUserId;
    if (subPathInput) subPathInput.value = savedSubPath;
}

// Save Path to Storage
function savePath() {
    const userId = document.getElementById('userId').value.trim();
    const subPath = document.getElementById('subPath').value.trim();
    
    localStorage.setItem('userId', userId);
    localStorage.setItem('subPath', subPath);
}

// Add event listeners to inputs for auto-saving
document.addEventListener('DOMContentLoaded', function() {
    const userIdInput = document.getElementById('userId');
    const subPathInput = document.getElementById('subPath');

    if (userIdInput && subPathInput) {
        userIdInput.addEventListener('change', savePath);
        subPathInput.addEventListener('change', savePath);
    }
});