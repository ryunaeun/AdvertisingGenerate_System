// Global Constants
const MAX_RESOLUTION = 2048;

// Global Variables
let isCustomMode = false;
let isUpscaleMode = false;
let isVertical = true; // 세로 모드로 시작

// Cache DOM Elements
const widthSlider = document.getElementById('widthSlider');
const widthInput = document.getElementById('widthInput');
const heightSlider = document.getElementById('heightSlider');
const heightInput = document.getElementById('heightInput');
const maxWidthSpan = document.getElementById('maxWidth');
const maxHeightSpan = document.getElementById('maxHeight');
const heightGroup = document.getElementById('heightGroup');
const upscaleBtn = document.getElementById('upscaleBtn');
const upscaleControls = document.getElementById('upscaleControls');
const upscaleSlider = document.getElementById('upscaleSlider');
const upscaleInput = document.getElementById('upscaleInput');
const exchangeBtn = document.getElementById('exchangeBtn');
const negativePromptToggle = document.getElementById('negativePromptToggle');
const batchSizeInput = document.getElementById('batchSize');
const currentResolutionSpan = document.getElementById('currentResolution');
const stepsSlider = document.getElementById('stepsSlider');
const stepsInput = document.getElementById('stepsInput');
const cfgSlider = document.getElementById('cfgSlider');
const cfgInput = document.getElementById('cfgInput');

// Utility Functions
function updateAspectRatioButtons() {
    document.querySelectorAll('.aspect-ratio-btn').forEach(button => {
        const ratioH = button.getAttribute('data-ratio-h');
        const ratioV = button.getAttribute('data-ratio-v');
        
        // custom 버튼은 텍스트 변경하지 않음
        if (ratioH !== 'custom') {
            button.textContent = isVertical ? ratioV : ratioH;
        }
    });
}

function getRatioValues(button) {
    const ratio = isVertical ? 
        button.getAttribute('data-ratio-v') : 
        button.getAttribute('data-ratio-h');
    
    if (ratio === 'custom') return { w: 0, h: 0 };
    
    const [first, second] = ratio.split(':').map(Number);
    return {
        w: isVertical ? first : second,
        h: isVertical ? second : first
    };
}

function updateResolutionLimits() {
    widthSlider.max = MAX_RESOLUTION;
    heightSlider.max = MAX_RESOLUTION;
    maxWidthSpan.textContent = `Max: ${MAX_RESOLUTION}px`;
    maxHeightSpan.textContent = `Max: ${MAX_RESOLUTION}px`;
}

// Resolution Display Update
function updateResolutionDisplay() {
    let width = parseInt(widthSlider.value);
    let height = parseInt(heightSlider.value);
    
    if (isUpscaleMode) {
        const scale = parseFloat(upscaleSlider.value);
        width = Math.round(width * scale);
        height = Math.round(height * scale);
    }

    currentResolutionSpan.textContent = `${width} x ${height} ${isVertical ? 'V' : 'H'}`;
}

// Resolution Adjustment Functions
function adjustResolution(button, sourceElement = null) {
    if (isCustomMode || button.getAttribute('data-ratio-h') === 'custom') return;
    
    const { w, h } = getRatioValues(button);
    
    if (w === h) { // 1:1 비율
        const size = parseInt(widthSlider.value);
        widthSlider.value = widthInput.value = size;
        heightSlider.value = heightInput.value = size;
        
        heightGroup.style.display = 'none';
        document.querySelector('label[for="widthSlider"]').textContent = 'Size:';
    } else {
        heightGroup.style.display = 'block';
        document.querySelector('label[for="widthSlider"]').textContent = 'Width:';
        
        // 슬라이더 조작 시
        if (sourceElement) {
            if (sourceElement === widthSlider || sourceElement === widthInput) {
                // 너비가 변경되면 높이 조정
                const width = parseInt(widthSlider.value);
                const newHeight = Math.round((width * h / w) / 8) * 8;
                heightSlider.value = heightInput.value = Math.min(newHeight, MAX_RESOLUTION);
            } else {
                // 높이가 변경되면 너비 조정
                const height = parseInt(heightSlider.value);
                const newWidth = Math.round((height * w / h) / 8) * 8;
                widthSlider.value = widthInput.value = Math.min(newWidth, MAX_RESOLUTION);
            }
        } 
        // 비율 버튼 클릭 시
        else {
            const currentWidth = parseInt(widthSlider.value);
            const newHeight = Math.round((currentWidth * h / w) / 8) * 8;
            
            if (newHeight <= MAX_RESOLUTION) {
                heightSlider.value = heightInput.value = newHeight;
            } else {
                const newWidth = Math.round((MAX_RESOLUTION * w / h) / 8) * 8;
                widthSlider.value = widthInput.value = newWidth;
                heightSlider.value = heightInput.value = MAX_RESOLUTION;
            }
        }
    }
    
    updateResolutionDisplay();
}

// Exchange Function
function handleExchange() {
    const currentWidth = parseInt(widthSlider.value);
    const currentHeight = parseInt(heightSlider.value);

    // 방향 전환
    isVertical = !isVertical;

    // 차원 교환
    widthSlider.value = currentHeight;
    widthInput.value = currentHeight;
    heightSlider.value = currentWidth;
    heightInput.value = currentWidth;
    
    // 비율 버튼 텍스트 업데이트
    updateAspectRatioButtons();

    updateResolutionDisplay();
}

// Check and update exchange button visibility
function updateExchangeButtonVisibility(ratio) {
    if (ratio === '1:1') {
        exchangeBtn.style.display = 'none';
    } else {
        exchangeBtn.style.display = 'block';
    }
}

// Grid Functions
function updateImageGrid(count) {
    const grid = document.getElementById('imagesGrid');
    grid.className = 'images-grid';

    if (count === 1) {
        grid.classList.add('single');
    } else if (count === 2) {
        grid.classList.add(isVertical ? 'vertical-split' : 'horizontal-split');
    } else {
        grid.classList.add('grid-four');
    }

    for (let i = 1; i <= 4; i++) {
        const cell = document.querySelector(`.image-cell:nth-child(${i})`);
        if (i <= count) {
            cell.style.display = 'flex';
            const image = document.getElementById(`outputImage-${i}`);
            const loading = document.getElementById(`loading-${i}`);
            const download = document.getElementById(`downloadLink-${i}`);

            image.style.display = 'none';
            loading.style.display = 'none';
            download.style.display = 'none';
        } else {
            cell.style.display = 'none';
        }
    }
}

// Initialize Resolution
function initializeResolution() {
    const initialWidth = 960;
    const initialHeight = 1280;
    
    // 슬라이더 값 설정
    widthSlider.value = initialWidth;
    widthInput.value = initialWidth;
    heightSlider.value = initialHeight;
    heightInput.value = initialHeight;
    
    // 슬라이더 상태 초기화 - 추가
    widthSlider.dispatchEvent(new Event('input'));
    heightSlider.dispatchEvent(new Event('input'));
    
    isVertical = true;
    
    // height 그룹 표시 상태 명시적 설정 - 추가
    heightGroup.style.display = 'block';
    
    updateResolutionDisplay();
}

// Event Handlers
function initializeEventListeners() {
    // Negative Prompt Toggle
    negativePromptToggle.addEventListener('click', function() {
        const container = document.getElementById('negativePromptContainer');
        const chevron = this.querySelector('.chevron');
        container.classList.toggle('visible');
        chevron.classList.toggle('rotated');
    });

    // Upscale Controls
    upscaleBtn.addEventListener('click', function() {
        isUpscaleMode = !isUpscaleMode;
        this.classList.toggle('active');
        upscaleControls.style.display = isUpscaleMode ? 'block' : 'none';
        updateResolutionDisplay();
    });

    // Exchange Button
    exchangeBtn.addEventListener('click', handleExchange);

    // Aspect Ratio Buttons
    document.querySelectorAll('.aspect-ratio-btn').forEach(button => {
        button.addEventListener('click', () => {
            document.querySelectorAll('.aspect-ratio-btn').forEach(btn => {
                btn.classList.remove('active');
            });
            button.classList.add('active');
            
            const ratio = button.getAttribute('data-ratio-h');
            updateExchangeButtonVisibility(ratio);
            
            if (ratio === 'custom') {
                isCustomMode = true;
                heightGroup.style.display = 'block';
                document.querySelector('label[for="widthSlider"]').textContent = 'Width:';
            } else {
                isCustomMode = false;
                adjustResolution(button);
            }
        });
    });

    // Upscale Slider and Input
    upscaleSlider.addEventListener('input', function() {
        upscaleInput.value = this.value;
        updateResolutionDisplay();
    });

    upscaleInput.addEventListener('input', function() {
        let value = parseFloat(this.value);
        value = Math.min(Math.max(value, 1.05), 4);
        this.value = value;
        upscaleSlider.value = value;
        updateResolutionDisplay();
    });

    // CFG Slider and Input
    cfgSlider.addEventListener('input', function() {
        cfgInput.value = this.value;
    });

    cfgInput.addEventListener('input', function() {
        let value = parseFloat(this.value);
        value = Math.min(Math.max(value, 0.0), 15.0);
        this.value = value.toFixed(1);
        cfgSlider.value = value;
    });

    // Resolution Controls
    [widthSlider, widthInput, heightSlider, heightInput].forEach(element => {
        element.addEventListener('input', function() {
            const roundedValue = Math.round(this.value / 8) * 8;
            this.value = roundedValue;
            
            if (this === widthSlider || this === widthInput) {
                widthSlider.value = widthInput.value = roundedValue;
            } else {
                heightSlider.value = heightInput.value = roundedValue;
            }
            
            // 비율 유지가 필요한 경우에만 adjustResolution 호출
            if (!isCustomMode) {
                const activeButton = document.querySelector('.aspect-ratio-btn.active');
                if (activeButton) {
                    adjustResolution(activeButton, this); // this를 sourceElement로 전달
                }
            }
            
            // Custom 모드일 때는 단순히 방향만 체크
            if (isCustomMode) {
                const currentWidth = parseInt(widthSlider.value);
                const currentHeight = parseInt(heightSlider.value);

                if (currentWidth !== currentHeight) {
                    const isCurrentVertical = currentHeight > currentWidth;
                    if (isCurrentVertical !== isVertical) {
                        isVertical = isCurrentVertical;
                        updateAspectRatioButtons();
                    }
                }
            }
            
            updateResolutionDisplay();
        });
    });

    // Batch Size
    batchSizeInput.addEventListener('input', function() {
        let count = parseInt(this.value);
        count = Math.min(Math.max(count, 1), 4);
        this.value = count;
        updateImageGrid(count);
    });

    // Steps Slider and Input
    stepsSlider.addEventListener('input', function() {
        stepsInput.value = this.value;
    });

    stepsInput.addEventListener('input', function() {
        let value = parseInt(this.value);
        value = Math.min(Math.max(value, 1), 150);
        this.value = value;
        stepsSlider.value = value;
    });
}
// Initialize
function initialize() {
    updateResolutionLimits();
    initializeResolution();
    updateAspectRatioButtons(); // 초기 비율 텍스트 설정
    initializeEventListeners();
    exchangeBtn.style.display = 'block';
    updateImageGrid(1);
}

// Wait for DOM to be ready
document.addEventListener('DOMContentLoaded', initialize);