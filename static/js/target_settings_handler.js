// target_settings_handler.js

class TargetSettingsHandler {
    constructor() {
        if (window.targetSettingsHandlerInstance) {
            return window.targetSettingsHandlerInstance;
        }
        
        this.settings = null;
        this.initializeUpdateButton();
        window.targetSettingsHandlerInstance = this;
        console.log('TargetSettingsHandler initialized');
    }

    initializeUpdateButton() {
        const updateBtn = document.getElementById('updateSettingsBtn');
        const fileInput = document.getElementById('settingsFile');
        
        if (!updateBtn || !fileInput) {
            console.error('Required elements not found');
            return;
        }

        console.log('Setting up event listeners');
        
        // 기존 이벤트 리스너 제거
        updateBtn.removeEventListener('click', this._handleButtonClick);
        fileInput.removeEventListener('change', this._handleFileChange);
        
        // 새로운 이벤트 리스너 등록
        this._handleButtonClick = () => {
            console.log('Update button clicked');
            fileInput.click();
        };
        
        this._handleFileChange = (e) => {
            console.log('File selected:', e.target.files[0]);
            if (e.target.files.length > 0) {
                this.handleFileUpload(e.target.files[0]);
            }
            // 파일 input 초기화
            fileInput.value = '';
        };
        
        updateBtn.addEventListener('click', this._handleButtonClick);
        fileInput.addEventListener('change', this._handleFileChange);
    }

    async handleFileUpload(file) {
        const updateBtn = document.getElementById('updateSettingsBtn');
        const originalText = updateBtn.textContent;
        
        try {
            console.log('Starting file upload...'); // 디버깅을 위한 로그
            updateBtn.textContent = 'Uploading...';
            updateBtn.disabled = true;
            
            const formData = new FormData();
            formData.append('file', file);
            
            const response = await fetch('/update_target_settings', {
                method: 'POST',
                body: formData
            });
            
            const result = await response.json();
            console.log('Upload result:', result); // 디버깅을 위한 로그
            
            if (!response.ok) {
                throw new Error(result.error || 'Failed to update settings');
            }
            
            // Reload settings and update UI
            await this.loadSettings();
            
            alert('Settings updated successfully!');
            
        } catch (error) {
            console.error('Error updating settings:', error);
            alert('Error updating settings: ' + error.message);
        } finally {
            updateBtn.textContent = originalText;
            updateBtn.disabled = false;
        }
    }

    async loadSettings() {
        try {
            const response = await fetch('/static/config/target_settings.json?t=' + Date.now());
            this.settings = await response.json();
            this.initializeSelects();
            console.log('Settings loaded:', this.settings); // 디버깅을 위한 로그
        } catch (error) {
            console.error('Error loading target settings:', error);
        }
    }

    initializeSelects() {
        if (!this.settings) {
            console.log('No settings available');
            return;
        }

        console.log('Initializing selects with settings:', this.settings); // 디버깅을 위한 로그

        Object.entries(this.settings.targetSettings).forEach(([key, setting]) => {
            const select = document.getElementById(setting.id);
            if (!select) {
                console.log(`Select element not found for ${setting.id}`);
                return;
            }

            // Store current value
            const currentValue = select.value;

            // Clear existing options
            select.innerHTML = '';

            // Add new options
            setting.options.forEach(option => {
                const optionElement = document.createElement('option');
                optionElement.value = option.value;
                optionElement.textContent = option.label;
                select.appendChild(optionElement);
            });

            // Restore previous value if it exists in new options
            if (setting.options.some(opt => opt.value === currentValue)) {
                select.value = currentValue;
            }
        });
    }

    getCurrentSettings() {
        const settings = {};
        if (!this.settings) return settings;

        Object.values(this.settings.targetSettings).forEach(setting => {
            const select = document.getElementById(setting.id);
            if (select) {
                settings[setting.id] = select.value;
            }
        });

        return settings;
    }

    getOptionsForCategory(category) {
        if (!this.settings?.targetSettings[category]) {
            return [];
        }
        return this.settings.targetSettings[category].options;
    }

    getLabelForValue(category, value) {
        const options = this.getOptionsForCategory(category);
        const option = options.find(opt => opt.value === value);
        return option ? option.label : '';
    }
}

// Export the class and create a single instance
window.TargetSettingsHandler = TargetSettingsHandler;
window.targetSettingsHandler = new TargetSettingsHandler();

// 페이지 로드 시 설정 초기화
document.addEventListener('DOMContentLoaded', () => {
    window.targetSettingsHandler.loadSettings();
});