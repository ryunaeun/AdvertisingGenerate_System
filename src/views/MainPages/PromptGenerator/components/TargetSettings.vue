<template>
  <div class="target-settings-wrapper">
    <div class="settings-update">
      <input type="file" id="settingsFile" accept=".xlsx, .xls, .csv" style="display: none;">
      <button 
        class="update-settings-btn"
        @click="handleUpdateClick"
        :disabled="isLoading"
      >
        {{ isLoading ? '업로드중...' : '설정 업데이트' }}
      </button>
    </div>

    <div class="target-settings">
      <h2>광고 타겟을 설정해 주세요!</h2>
      <div class="settings-grid">
        <div v-for="(setting, key) in targetSettings" :key="key" class="setting-item">
          <div class="input-group">
            <label :for="setting.id">{{ setting.label }}:</label>
            <select 
              :id="setting.id" 
              class="setting-select"
              v-model="selectedSettings[key]"
              @change="emitSettingsChange"
            >
              <option 
                v-for="option in setting.options" 
                :key="option.value" 
                :value="option.value"
              >
                {{ option.label }}
              </option>
            </select>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: 'TargetSettings',
  props: {
    serverUrl: {
      type: String,
      required: true
    }
  },
  data() {
    return {
      isLoading: false,
      targetSettings: {},
      selectedSettings: {
        gender: '',
        ageGroup: '',
        productCategory: '',
        seasonEvent: '',
        adTone: ''
      }
    }
  },
  methods: {
    async loadSettings() {
      try {
        const response = await fetch(`${this.serverUrl}/static/config/target_settings.json?t=${Date.now()}`, {
          method: 'GET',
          headers: {
            'Accept': 'application/json',
          }
        });
        
        if (!response.ok) {
          throw new Error('설정 로드 실패');
        }
        
        const data = await response.json();
        this.targetSettings = data.targetSettings;
        this.initializeDefaultValues();
      } catch (error) {
        console.error('타겟 설정 로드 오류:', error);
        throw error;
      }
    },

    initializeDefaultValues() {
      Object.keys(this.targetSettings).forEach(key => {
        // 각 설정의 첫 번째 옵션을 기본값으로 설정
        if (this.targetSettings[key].options && this.targetSettings[key].options.length > 0) {
          this.selectedSettings[key] = this.targetSettings[key].options[0].value;
        }
      });
      // 설정이 변경되었음을 부모 컴포넌트에 알림
      this.emitSettingsChange();
    },

    handleUpdateClick() {
      document.getElementById('settingsFile').click();
    },
    
    async handleFileChange(e) {
      if (e.target.files.length === 0) return;
      const file = e.target.files[0];
      await this.handleFileUpload(file);
      e.target.value = '';
    },
    
    async handleFileUpload(file) {
      this.isLoading = true;
      
      try {
        const formData = new FormData();
        formData.append('file', file);
        
        const response = await fetch(`${this.serverUrl}/update_target_settings`, {
          method: 'POST',
          body: formData
        });
        
        const result = await response.json();
        
        if (!response.ok) {
          throw new Error(result.error || '설정 업데이트 실패');
        }
        
        await this.loadSettings();
        alert('설정이 성공적으로 업데이트되었습니다!');
        
      } catch (error) {
        console.error('설정 업데이트 오류:', error);
        alert('설정 업데이트 중 오류 발생: ' + error.message);
      } finally {
        this.isLoading = false;
      }
    },

    emitSettingsChange() {
      this.$emit('settings-change', this.selectedSettings);
    }
  },
  watch: {
    serverUrl: {
      immediate: true,
      handler(newUrl) {
        if (newUrl) {
          this.loadSettings();
        }
      }
    }
  },
  async mounted() {
    if (this.serverUrl) {
      try {
        await this.loadSettings();
        const fileInput = document.getElementById('settingsFile');
        if (fileInput) {
          fileInput.addEventListener('change', this.handleFileChange);
        }
      } catch (error) {
        console.error('타겟 설정 초기화 오류:', error);
      }
    }
  }
}
</script>

<style scoped>
.target-settings-wrapper {
  width: 100%;
}

.settings-update {
  margin-bottom: 20px;
}

.target-settings {
  margin-top: 20px;
}

.target-settings h2 {
  margin-bottom: 20px;
  color: #344767;
  font-size: 1.5rem;
}

.settings-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 20px;
}

.setting-item {
  display: flex;
  align-items: center;
  gap: 10px;
}

.setting-item label {
  min-width: 80px;
  text-align: left;
  margin-bottom: 0;
  color: #344767;
  font-weight: 500;
  flex-shrink: 0; /* 라벨 너비 고정 */
}

.setting-select {
  flex: 1;
  min-width: 0; /* 오버플로우 방지 */
  padding: 8px 12px;
  border: 1px solid var(--input-border);
  border-radius: 4px;
  background-color: white;
  color: #344767;
  font-size: 14px;
  transition: border-color 0.3s, box-shadow 0.3s;
}

.setting-select:hover {
  border-color: #5CB494;
}

.setting-select:focus {
  outline: none;
  border-color: #5CB494;
  box-shadow: 0 0 0 2px rgba(92, 180, 148, 0.2);
}

.update-settings-btn {
  padding: 8px 16px;
  background-color: #5CB494;
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  font-size: 14px;
  font-weight: 500;
  transition: all 0.3s ease;
}

.update-settings-btn:hover {
  background-color: #4a9077;
  transform: translateY(-1px);
}

.update-settings-btn:active {
  transform: translateY(1px);
}

.update-settings-btn:disabled {
  opacity: 0.5;
  cursor: not-allowed;
  transform: none;
}

/* 반응형 디자인을 위한 미디어 쿼리 */
@media (max-width: 992px) {
  .settings-grid {
    grid-template-columns: 1fr;
  }

  .setting-item label {
    min-width: 100px;
  }
}

@media (max-width: 576px) {
  .setting-item {
    flex-direction: column;
    align-items: flex-start;
    gap: 5px;
  }

  .setting-item label {
    min-width: auto;
    margin-bottom: 5px;
  }

  .setting-select {
    width: 100%;
  }
}
</style>