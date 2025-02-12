<template>
  <div class="video-settings">
    <div class="settings-header" @click="toggleSettings">
      <h3>해상도 및 프레임 설정</h3>
      <div class="settings-info">
        <span class="resolution-text">해상도: {{ width }} × 프레임 길이: {{ frameLength }}f</span>
        <span class="toggle-icon" :class="{ 'is-open': showSettings }">▼</span>
      </div>
    </div>
    
    <div v-show="showSettings" class="settings-container">
      <div class="settings-layout">
        <div class="slider-section">
          <div class="slider-group">
            <div class="slider-header">
              <label>Size:</label>
              <span>Max: 960px</span>
            </div>
            <div class="slider-controls">
              <input 
                type="range" 
                v-model.number="width"
                :min="720" 
                :max="960" 
                step="16"
                @input="updateDimensions"
              >
              <input 
                type="number" 
                v-model.number="width"
                :min="720" 
                :max="960"
                step="16"
                @input="updateDimensions"
              >
            </div>
          </div>

          <div class="slider-group">
            <div class="slider-header">
              <label>프레임:</label>
              <span>Max: 129</span>
            </div>
            <div class="slider-controls">
              <input 
                type="range" 
                v-model.number="frameLength"
                :min="1" 
                :max="129" 
                step="4"
                @input="updateFrameLength"
              >
              <input 
                type="number" 
                v-model.number="frameLength"
                :min="1" 
                :max="129"
                @input="updateFrameLength"
              >
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: 'VideoSettings',
  
  data() {
    return {
      showSettings: false,
      width: 800,
      frameLength: 73
    }
  },

  methods: {
    toggleSettings() {
      this.showSettings = !this.showSettings;
    },

    updateDimensions() {
      const roundedValue = Math.round(this.width / 16) * 16;
      this.width = Math.min(Math.max(roundedValue, 720), 960);
      this.emitSettings();
    },

    updateFrameLength() {
      this.frameLength = Math.floor((this.frameLength - 1) / 4) * 4 + 1;
      if (this.frameLength < 1) this.frameLength = 1;
      if (this.frameLength > 129) this.frameLength = 129;
      this.emitSettings();
    },

    emitSettings() {
      this.$emit('settings-change', {
        width: this.width,
        height: this.width,
        frameLength: this.frameLength
      });
    }
  },

  mounted() {
    this.emitSettings();
  }
}
</script>

<style scoped>
.video-settings {
  width: 100%;
  background: var(--background-primary);
  border-radius: 8px;
  overflow: hidden;
}

.settings-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 15px 20px;
  cursor: pointer;
  background: var(--background-primary);
  border-bottom: 1px solid var(--border-color);
}

.settings-header h3 {
  margin: 0;
  font-size: 16px;
  color: var(--text-primary);
}

.settings-info {
  display: flex;
  align-items: center;
  gap: 8px;
}

.resolution-text {
  color: var(--accent-primary);
  font-weight: 500;
}

.toggle-icon {
  transition: transform 0.3s ease;
}

.toggle-icon.is-open {
  transform: rotate(180deg);
}

.settings-container {
  padding: 20px;
  background: var(--background-secondary);
}

.settings-layout {
  display: flex;
  gap: 20px;
}

.slider-section {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 20px;
}

/* Slider Styles */
.slider-group {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.slider-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.slider-controls {
  display: flex;
  align-items: center;
  gap: 12px;
}

input[type="range"] {
  flex: 1;
  height: 4px;
  -webkit-appearance: none;
  background: var(--border-color);
  border-radius: 2px;
  outline: none;
}

input[type="range"]::-webkit-slider-thumb {
  -webkit-appearance: none;
  width: 16px;
  height: 16px;
  border-radius: 50%;
  background: var(--accent-primary);
  cursor: pointer;
  border: none;
}

input[type="range"]::-moz-range-thumb {
  width: 16px;
  height: 16px;
  border-radius: 50%;
  background: var(--accent-primary);
  cursor: pointer;
  border: none;
}

input[type="number"] {
  width: 80px;
  padding: 4px;
  border: 1px solid var(--border-color);
  border-radius: 4px;
  background: var(--background-primary);
  color: var(--text-primary);
  text-align: center;
}

/* Responsive Styles */
@media (max-width: 768px) {
  .settings-layout {
    flex-direction: column;
  }

  .slider-section {
    width: 100%;
  }

  .slider-controls {
    flex-direction: column;
    gap: 8px;
  }

  input[type="number"] {
    width: 100%;
  }
}
</style>