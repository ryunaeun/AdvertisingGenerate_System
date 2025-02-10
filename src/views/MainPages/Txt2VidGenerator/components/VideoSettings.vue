<template>
  <div class="video-settings">
    <div class="settings-header" @click="toggleSettings">
      <h3>해상도 및 프레임 설정</h3>
      <div class="settings-info">
        <span class="resolution-text">{{ displayResolution }}×{{ frameLength }}f</span>
        <span v-if="enableUpscale" class="upscale-badge">(Upscaled)</span>
        <span class="toggle-icon" :class="{ 'is-open': showSettings }">▼</span>
      </div>
    </div>
    
    <div v-show="showSettings" class="settings-container">
      <div class="settings-layout">
        <!-- Left side - Controls -->
        <div class="controls-section">
          <div class="aspect-ratio-section">
            <label>해상도 비율:</label>
            <div class="button-group">
              <button 
                v-for="ratio in aspectRatios"
                :key="ratio"
                :class="{ active: selectedAspectRatio === ratio }"
                @click="setAspectRatio(ratio)"
              >{{ ratio }}</button>
            </div>
          </div>

          <div class="orientation-section">
            <label>영상 종횡비:</label>
            <div class="button-group">
              <button 
                v-for="orient in orientations"
                :key="orient.value"
                :class="{ active: orientation === orient.value }"
                @click="setOrientation(orient.value)"
              >{{ orient.label }}</button>
            </div>
          </div>

          <div class="upscale-section">
            <label>업스케일:</label>
            <div class="upscale-controls">
              <button 
                :class="{ active: enableUpscale }"
                @click="toggleUpscale"
              >{{ enableUpscale ? 'ON' : 'OFF' }}</button>
              <span class="target-resolution">{{ enableUpscale ? targetResolution : `${width}×${height}` }}</span>
            </div>
          </div>
        </div>

        <!-- Vertical Divider -->
        <div class="vertical-divider"></div>

        <!-- Right side - Sliders -->
        <div class="slider-section">
          <div class="slider-group">
            <div class="slider-header">
              <label>{{ selectedAspectRatio === '1:1' ? 'Size:' : '가로:' }}</label>
              <span>Max: {{ currentLimits.width }}px</span>
            </div>
            <div class="slider-controls">
              <input 
                type="range" 
                v-model.number="width"
                :min="64" 
                :max="currentLimits.width" 
                step="16"
                @input="updateDimensions('width')"
              >
              <input 
                type="number" 
                v-model.number="width"
                :min="64" 
                :max="currentLimits.width"
                step="16"
                @input="updateDimensions('width')"
              >
            </div>
          </div>

          <div v-show="selectedAspectRatio !== '1:1'" class="slider-group">
            <div class="slider-header">
              <label>세로:</label>
              <span>Max: {{ currentLimits.height }}px</span>
            </div>
            <div class="slider-controls">
              <input 
                type="range" 
                v-model.number="height"
                :min="64" 
                :max="currentLimits.height" 
                step="16"
                @input="updateDimensions('height')"
              >
              <input 
                type="number" 
                v-model.number="height"
                :min="64" 
                :max="currentLimits.height"
                step="16"
                @input="updateDimensions('height')"
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

export default {
  name: 'VideoSettings',
  
  data() {
    return {
      showSettings: false,
      aspectRatios: ['16:9', '4:3', '1:1'],
      selectedAspectRatio: '16:9',
      orientations: [
        { value: 'landscape', label: '가로' },
        { value: 'portrait', label: '세로' }
      ],
      orientation: 'landscape',
      width: 848,
      height: 480,
      frameLength: 73,
      enableUpscale: false
    }
  },

  computed: {
    currentLimits() {
      return RESOLUTION_LIMITS[this.selectedAspectRatio][this.orientation];
    },

    targetResolution() {
      if (!this.enableUpscale) return '-';
      
      switch(this.selectedAspectRatio) {
        case '16:9':
          return this.orientation === 'landscape' ? '1920×1080' : '1080×1920';
        case '4:3':
          return this.orientation === 'landscape' ? '1600×1200' : '1200×1600';
        case '1:1':
          return '1440×1440';
        default:
          return '-';
      }
    },

    displayResolution() {
      if (this.enableUpscale) {
        return this.targetResolution.split(',')[0];
      }
      return `${this.width}×${this.height}`;
    }
  },

  methods: {
    toggleSettings() {
      this.showSettings = !this.showSettings;
    },

    setAspectRatio(ratio) {
      this.selectedAspectRatio = ratio;
      this.updateResolutionLimits();
      this.emitSettings();
    },

    setOrientation(orientation) {
      this.orientation = orientation;
      this.updateResolutionLimits(true);
      this.emitSettings();
    },

    toggleUpscale() {
      this.enableUpscale = !this.enableUpscale;
      this.emitSettings();
    },

    updateResolutionLimits(isOrientationChange = false) {
      const limits = this.currentLimits;

      if (this.selectedAspectRatio === '1:1') {
        this.width = limits.default_width;
        this.height = limits.default_width;
      } else if (isOrientationChange) {
        const previousWidth = this.width;
        const previousHeight = this.height;

        let newWidth = Math.min(previousHeight, limits.width);
        let newHeight = Math.min(previousWidth, limits.height);

        this.width = Math.min(Math.round(newWidth / 16) * 16, limits.width);
        this.height = Math.min(Math.round(newHeight / 16) * 16, limits.height);
      } else {
        this.width = limits.default_width;
        this.height = limits.default_height;
      }
    },

    updateDimensions(source) {
      if (this.selectedAspectRatio === '1:1') {
        const value = Math.min(this.width, this.currentLimits.width);
        const roundedValue = Math.round(value / 16) * 16;
        this.width = roundedValue;
        this.height = roundedValue;
      } else {
        const ratioValues = this.selectedAspectRatio.split(':').map(Number);
        
        if (source === 'width') {
          if (this.orientation === 'landscape') {
            this.height = Math.round((this.width * ratioValues[1]) / ratioValues[0] / 16) * 16;
          } else {
            this.height = Math.round((this.width * ratioValues[0]) / ratioValues[1] / 16) * 16;
          }
        } else {
          if (this.orientation === 'landscape') {
            this.width = Math.round((this.height * ratioValues[0]) / ratioValues[1] / 16) * 16;
          } else {
            this.width = Math.round((this.height * ratioValues[1]) / ratioValues[0] / 16) * 16;
          }
        }

        this.width = Math.min(this.width, this.currentLimits.width);
        this.height = Math.min(this.height, this.currentLimits.height);
      }
      
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
        height: this.height,
        frameLength: this.frameLength,
        enableUpscale: this.enableUpscale,
        aspectRatio: this.selectedAspectRatio,
        orientation: this.orientation
      });
    }
  },

  mounted() {
    this.updateResolutionLimits();
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

.upscale-badge {
  color: var(--text-secondary);
  font-size: 12px;
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

.controls-section {
  flex: 0 0 200px;
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.vertical-divider {
  width: 1px;
  background-color: var(--border-color);
  margin: 0 6px;
}

.slider-section {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 20px;
}

/* Button Styles */
.button-group {
  display: flex;
  gap: 8px;
  width: 100%;
}

button {
  flex: 1;
  padding: 4px 16px;
  border: 1px solid var(--border-color);
  border-radius: 4px;
  background: var(--background-primary);
  color: var(--text-primary);
  cursor: pointer;
  transition: all 0.2s ease;
  white-space: nowrap;
}

button:hover {
  background: var(--background-secondary);
}

button.active {
  background: var(--accent-primary);
  color: white;
  border-color: var(--accent-primary);
}

/* Upscale Controls */
.upscale-controls {
  display: flex;
  align-items: center;
  gap: 24px;
}

.upscale-controls button {
  width: 80px;
  flex: none;
}

.target-resolution {
  color: var(--accent-primary);
  font-weight: 500;
  font-size: 14px;
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

  .controls-section {
    flex: none;
    width: 100%;
  }

  .vertical-divider {
    display: none;
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

  /* 모바일에서 버튼 그룹 스타일 조정 */
  .button-group {
    flex-wrap: wrap;
  }

  .button-group button {
    flex: 1 1 calc(50% - 4px);
    min-width: 100px;
  }

  /* 모바일에서 업스케일 컨트롤 조정 */
  .upscale-controls {
    flex-direction: column;
    align-items: flex-start;
    gap: 12px;
  }

  .upscale-controls button {
    width: 100%;
  }
}</style>