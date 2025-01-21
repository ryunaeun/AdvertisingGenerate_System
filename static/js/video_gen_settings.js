// Resolution and Frame Settings Constants
const RESOLUTION_LIMITS = {
    '16:9': {
        'landscape': { width: 960, height: 544, default_width: 848, default_height: 480 },
        'portrait': { width: 544, height: 960, default_width: 480, default_height: 848 }
    },
    '4:3': {
        'landscape': { width: 832, height: 624, default_width: 768, default_height: 576 },
        'portrait': { width: 624, height: 832, default_width: 576, default_height: 768 }
    },
    '1:1': {
        'landscape': { width: 720, height: 720, default_width: 636, default_height: 636 },
        'portrait': { width: 720, height: 720, default_width: 636, default_height: 636 }
    }
};

// Resolution Control Elements
const widthSlider = document.getElementById('widthSlider');
const widthInput = document.getElementById('widthInput');
const heightSlider = document.getElementById('heightSlider');
const heightInput = document.getElementById('heightInput');
const maxWidthSpan = document.getElementById('maxWidth');
const maxHeightSpan = document.getElementById('maxHeight');
const heightGroup = document.getElementById('heightGroup');

// Frame Length Control Elements
const frameLengthSlider = document.getElementById('frameLengthSlider');
const frameLengthInput = document.getElementById('frameLengthInput');

// Utility Functions
function getAspectRatio() {
    return document.querySelector('.aspect-ratio-btn.active').getAttribute('data-ratio');
}

function getOrientation() {
    const orientationInput = document.getElementById('orientationInput');
    return orientationInput.value === 'horizontal' ? 'landscape' : 'portrait';
}

function getTargetResolution() {
    const ratio = getAspectRatio();
    const orientation = getOrientation();
    const isLandscape = orientation === 'landscape';

    switch(ratio) {
        case '16:9':
            return isLandscape ? '1920×1080' : '1080×1920';
        case '4:3':
            return isLandscape ? '1600×1200' : '1200×1600';
        case '1:1':
            return '1440×1440';
        default:
            return isLandscape ? '1920×1080' : '1080×1920';
    }
}

// Update Functions
function updateUpscaleInfo() {
    const targetResolution = document.getElementById('targetResolution');
    if (document.getElementById('enableUpscale').checked) {
        targetResolution.textContent = getTargetResolution();
    } else {
        targetResolution.textContent = '-';
    }
}

function updateResolutionLimits(isOrientationChange = false) {
    const ratio = getAspectRatio();
    const orientation = getOrientation();
    const limits = RESOLUTION_LIMITS[ratio][orientation];
    const previousWidth = parseInt(widthInput.value);
    const previousHeight = parseInt(heightInput.value);

    heightGroup.style.display = ratio === '1:1' ? 'none' : 'block';

    widthSlider.max = limits.width;
    heightSlider.max = limits.height;
    maxWidthSpan.textContent = `Max: ${limits.width}px`;
    maxHeightSpan.textContent = `Max: ${limits.height}px`;

    const widthLabel = document.querySelector('label[for="widthSlider"]');
    widthLabel.textContent = ratio === '1:1' ? 'Size:' : 'Width:';

    if (ratio === '1:1') {
        // Use default values for 1:1 ratio
        widthSlider.value = limits.default_width;
        widthInput.value = limits.default_width;
        heightSlider.value = limits.default_width;
        heightInput.value = limits.default_width;
    } else if (isOrientationChange) {
        // Swap width/height when orientation changes
        let newWidth, newHeight;

        if (orientation === 'landscape') {
            newWidth = Math.min(previousHeight, limits.width);
            newHeight = Math.min(previousWidth, limits.height);
        } else {
            newWidth = Math.min(previousHeight, limits.width);
            newHeight = Math.min(previousWidth, limits.height);
        }

        // Adjust to multiples of 16
        newWidth = Math.round(newWidth / 16) * 16;
        newHeight = Math.round(newHeight / 16) * 16;

        // Apply limits
        newWidth = Math.min(newWidth, limits.width);
        newHeight = Math.min(newHeight, limits.height);

        widthSlider.value = newWidth;
        widthInput.value = newWidth;
        heightSlider.value = newHeight;
        heightInput.value = newHeight;
    } else {
        // Use default values for ratio change or initial load
        widthSlider.value = limits.default_width;
        widthInput.value = limits.default_width;
        heightSlider.value = limits.default_height;
        heightInput.value = limits.default_height;
    }

    updateUpscaleInfo();
}

function updateDimensions(width, height, source) {
    const ratio = getAspectRatio();
    const orientation = getOrientation();
    const limits = RESOLUTION_LIMITS[ratio][orientation];

    if (ratio === '1:1') {
        const value = Math.min(width, limits.width);
        const roundedValue = Math.round(value / 16) * 16;
        widthSlider.value = roundedValue;
        widthInput.value = roundedValue;
        heightSlider.value = roundedValue;
        heightInput.value = roundedValue;
        return;
    }

    let newWidth = width;
    let newHeight = height;

    if (source === 'width') {
        const ratioValues = ratio.split(':').map(Number);
        if (orientation === 'landscape') {
            newHeight = Math.round((width * ratioValues[1]) / ratioValues[0] / 16) * 16;
        } else {
            newHeight = Math.round((width * ratioValues[0]) / ratioValues[1] / 16) * 16;
        }
    } else if (source === 'height') {
        const ratioValues = ratio.split(':').map(Number);
        if (orientation === 'landscape') {
            newWidth = Math.round((height * ratioValues[0]) / ratioValues[1] / 16) * 16;
        } else {
            newWidth = Math.round((height * ratioValues[1]) / ratioValues[0] / 16) * 16;
        }
    }

    // Apply limits
    if (newWidth > limits.width) {
        newWidth = Math.floor(limits.width / 16) * 16;
        newHeight = orientation === 'landscape' ? 
            Math.round((newWidth * limits.height) / limits.width / 16) * 16 : 
            Math.round((newWidth * limits.width) / limits.height / 16) * 16;
    }
    if (newHeight > limits.height) {
        newHeight = Math.floor(limits.height / 16) * 16;
        newWidth = orientation === 'landscape' ? 
            Math.round((newHeight * limits.width) / limits.height / 16) * 16 : 
            Math.round((newHeight * limits.height) / limits.width / 16) * 16;
    }

    widthSlider.value = newWidth;
    widthInput.value = newWidth;
    heightSlider.value = newHeight;
    heightInput.value = newHeight;
}

// Frame Length Control Functions
function updateFrameLength(value) {
    let adjustedValue = Math.floor((parseInt(value) - 1) / 4) * 4 + 1;
    if (adjustedValue < 1) adjustedValue = 1;
    if (adjustedValue > 129) adjustedValue = 129;
    return adjustedValue;
}

// Event Listeners for Aspect Ratio Buttons
document.querySelectorAll('.aspect-ratio-btn').forEach(button => {
    button.addEventListener('click', () => {
        // Remove active class from all buttons
        document.querySelectorAll('.aspect-ratio-btn').forEach(btn => {
            btn.classList.remove('active');
        });
        
        // Add active class to clicked button
        button.classList.add('active');
        
        // Update the resolution limits
        updateResolutionLimits();
    });
});

// Event Listeners for Orientation Buttons
document.querySelectorAll('.orientation-btn').forEach(button => {
    button.addEventListener('click', () => {
        // Remove active class from all buttons
        document.querySelectorAll('.orientation-btn').forEach(btn => {
            btn.classList.remove('active');
        });
        
        // Add active class to clicked button
        button.classList.add('active');
        
        // Update hidden input value
        const orientationInput = document.getElementById('orientationInput');
        orientationInput.value = button.getAttribute('data-orientation');
        
        // Update the resolution limits
        updateResolutionLimits(true);
    });
});

document.getElementById('enableUpscale')
    .addEventListener('change', updateUpscaleInfo);

// Resolution Control Event Listeners
widthSlider.addEventListener('input', () => 
    updateDimensions(parseInt(widthSlider.value), parseInt(heightInput.value), 'width'));
widthInput.addEventListener('input', () => 
    updateDimensions(parseInt(widthInput.value), parseInt(heightInput.value), 'width'));
heightSlider.addEventListener('input', () => 
    updateDimensions(parseInt(widthInput.value), parseInt(heightSlider.value), 'height'));
heightInput.addEventListener('input', () => 
    updateDimensions(parseInt(widthInput.value), parseInt(heightInput.value), 'height'));

// Frame Length Control Event Listeners
frameLengthSlider.addEventListener('input', function() {
    const value = updateFrameLength(this.value);
    this.value = value;
    frameLengthInput.value = value;
});

frameLengthInput.addEventListener('input', function() {
    const value = updateFrameLength(this.value);
    this.value = value;
    frameLengthSlider.value = value;
});

document.getElementById('enableUpscale').addEventListener('click', function() {
    const enabled = this.getAttribute('data-enabled') === 'true';
    this.setAttribute('data-enabled', !enabled);
    this.textContent = enabled ? 'OFF' : 'ON';
    
    // Safari에서의 스타일 변경을 위해 클래스 토글 추가
    if (!enabled) {
        this.style.backgroundColor = '#5CB494';
        this.style.borderColor = '#5CB494';
    } else {
        this.style.backgroundColor = '#333';
        this.style.borderColor = '#404040';
    }
    
    // Target Resolution 업데이트
    const targetResolution = document.getElementById('targetResolution');
    targetResolution.textContent = !enabled ? '- '+getTargetResolution() : '-';
    targetResolution.style.display = 'inline';
});
// Initialize settings on page load
updateResolutionLimits();