<template>
  <div class="video-settings">
    <h3 class="settings-toggle" @click="toggleSettings">
      해상도 및 프레임 설정 
      <span class="current-settings">
        {{ displayResolution }}×{{ frameLength }}f
        <span v-if="enableUpscale" class="upscale-indicator">(Upscaled)</span>
      </span>
      <span class="toggle-icon" :class="{ 'rotated': showSettings }">▼</span>
    </h3>
    
    <div class="settings-content" v-show="showSettings">
      <div class="form-group resolution-group">
        <div class="orientation-and-upscale-group">
          <div class="radio-settings">
            <!-- Aspect Ratio -->
            <div class="aspect-ratio-group">
              <label class="section-label">해상도 비율:</label>
              <div class="aspect-ratio-buttons">
                <button 
                  v-for="ratio in aspectRatios"
                  :key="ratio"
                  type="button" 
                  class="aspect-ratio-btn" 
                  :class="{ active: selectedAspectRatio === ratio }"
                  @click="setAspectRatio(ratio)"
                >{{ ratio }}</button>
              </div>
            </div>

            <!-- Orientation -->
            <div class="orientation-group">
              <label class="section-label">영상 종횡비:</label>
              <div class="orientation-buttons">
                <button 
                  v-for="orient in orientations"
                  :key="orient.value"
                  type="button" 
                  class="orientation-btn" 
                  :class="{ active: orientation === orient.value }"
                  @click="setOrientation(orient.value)"
                >{{ orient.label }}</button>
              </div>
            </div>
            
            <!-- Upscale Settings -->
            <div class="upscale-settings">
              <label class="section-label">업스케일 설정:</label>
              <div class="upscale-info">
                <button 
                  type="button" 
                  class="upscale-btn"
                  :class="{ active: enableUpscale }"
                  @click="toggleUpscale"
                >{{ enableUpscale ? 'ON' : 'OFF' }}</button>
                <span id="targetResolution">{{ targetResolution }}</span>
              </div>
            </div>
          </div>
          
          <div class="vertical-divider"></div>

          <!-- Dimension Controls -->
          <div class="dimension-controls">
            <!-- Width/Size Control -->
            <div class="dimension-group">
              <div class="dimension-label">
                <label :for="selectedAspectRatio === '1:1' ? 'sizeSlider' : 'widthSlider'">
                  {{ selectedAspectRatio === '1:1' ? 'Size:' : '가로:' }}
                </label>
                <span>Max: {{ currentLimits.width }}px</span>
              </div>
              <div class="slider-container">
                <input 
                  type="range" 
                  :id="selectedAspectRatio === '1:1' ? 'sizeSlider' : 'widthSlider'"
                  v-model.number="width"
                  :min="64" 
                  :max="currentLimits.width" 
                  step="16"
                  class="slider"
                  @input="updateDimensions('width')"
                >
                <input 
                  type="number" 
                  v-model.number="width"
                  :min="64" 
                  step="16"
                  @input="updateDimensions('width')"
                >
              </div>
            </div>
            
            <!-- Height Control -->
            <div class="dimension-group" v-show="selectedAspectRatio !== '1:1'">
              <div class="dimension-label">
                <label for="heightSlider">세로:</label>
                <span>Max: {{ currentLimits.height }}px</span>
              </div>
              <div class="slider-container">
                <input 
                  type="range" 
                  id="heightSlider"
                  v-model.number="height"
                  :min="64" 
                  :max="currentLimits.height" 
                  step="16"
                  class="slider"
                  @input="updateDimensions('height')"
                >
                <input 
                  type="number" 
                  v-model.number="height"
                  :min="64" 
                  step="16"
                  @input="updateDimensions('height')"
                >
              </div>
            </div>
            
            <!-- Frame Length Control -->
            <div class="dimension-group frame-length-group">
              <div class="dimension-label">
                <label for="frameLengthSlider">프레임:</label>
                <span>Max: 129</span>
              </div>
              <div class="slider-container">
                <input 
                  type="range" 
                  id="frameLengthSlider"
                  v-model.number="frameLength"
                  min="1" 
                  max="129" 
                  step="4"
                  class="slider"
                  @input="updateFrameLength"
                >
                <input 
                  type="number" 
                  v-model.number="frameLength"
                  min="1"
                  max="129"
                  @input="updateFrameLength"
                >
              </div>
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
        { value: 'landscape', label: '가로 영상' },
        { value: 'portrait', label: '세로 영상' }
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
          return this.orientation === 'landscape' ? '1920×1080' : '1080×1920';
      }
    },

    displayResolution() {
      if (this.enableUpscale) {
        switch(this.selectedAspectRatio) {
          case '16:9':
            return this.orientation === 'landscape' ? '1920×1080' : '1080×1920';
          case '4:3':
            return this.orientation === 'landscape' ? '1600×1200' : '1200×1600';
          case '1:1':
            return '1440×1440';
          default:
            return this.orientation === 'landscape' ? '1920×1080' : '1080×1920';
        }
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
        // Swap width/height when orientation changes
        const previousWidth = this.width;
        const previousHeight = this.height;

        let newWidth, newHeight;
        if (this.orientation === 'landscape') {
          newWidth = Math.min(previousHeight, limits.width);
          newHeight = Math.min(previousWidth, limits.height);
        } else {
          newWidth = Math.min(previousHeight, limits.width);
          newHeight = Math.min(previousWidth, limits.height);
        }

        // Adjust to multiples of 16
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

        // Apply limits
        this.width = Math.min(this.width, this.currentLimits.width);
        this.height = Math.min(this.height, this.currentLimits.height);
      }
      
      this.emitSettings();
    },

    updateFrameLength() {
      // Adjust to valid frame length (1 + multiples of 4)
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
    // 초기 설정값 emit
    this.updateResolutionLimits();
    this.emitSettings();
  }
}
</script>

<style scoped>
.video-settings {
  margin: 20px 0;
  width: 100%;
}

.settings-toggle {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 12px 16px;
  background-color: var(--background-primary);
  border-radius: 8px;
  cursor: pointer;
  user-select: none;
  color: var(--text-primary);
  margin: 0;
  transition: background-color 0.2s ease;
  font-size: 16px;
}

.settings-toggle:hover {
  background-color: var(--accent-primary);
  color: white;
}

.current-settings {
  margin-left: auto;
  font-size: 14px;
  color: var(--accent-primary);
  font-weight: normal;
  display: flex;
  align-items: center;
  gap: 6px;
}

.upscale-indicator {
  font-size: 12px;
  color: var(--text-secondary);
  font-style: italic;
}

.toggle-icon {
  margin-left: 8px;
  transition: transform 0.3s ease;
  font-size: 14px;
}

.toggle-icon.rotated {
  transform: rotate(-180deg);
}

.settings-content {
  margin-top: 10px;
  transition: all 0.3s ease;
}

.form-group.resolution-group {
  background-color: var(--background-primary);
  border-radius: 8px;
  padding: 20px;
  margin: 0;
  width: 100%;
  box-sizing: border-box;
}

.orientation-and-upscale-group {
  display: flex;
  gap: 20px;
  margin-bottom: 15px;
}

.radio-settings {
  flex: 0 0 auto;
  min-width: 200px;
}

.section-label {
  display: block;
  margin-bottom: 8px;
  color: var(--text-primary);
  font-weight: bold;
}

/* Aspect Ratio Buttons */
.aspect-ratio-buttons {
  display: flex;
  gap: 8px;
  margin-bottom: 15px;
}

.aspect-ratio-btn {
  padding: 8px 12px;
  background-color: var(--input-background);
  border: 1px solid var(--border-color);
  border-radius: 4px;
  cursor: pointer;
  color: var(--text-primary);
  transition: all 0.2s ease;
}

/* Orientation Buttons */
.orientation-buttons {
  display: flex;
  gap: 8px;
  margin-bottom: 15px;
}

.orientation-btn {
  flex: 1;
  padding: 8px 12px;
  background-color: var(--input-background);
  border: 1px solid var(--border-color);
  border-radius: 4px;
  cursor: pointer;
  color: var(--text-primary);
  transition: all 0.2s ease;
}

/* Upscale Button */
.upscale-settings {
  margin-top: 15px;
}

.upscale-info {
  display: flex;
  align-items: center;
  gap: 12px;
}

.upscale-btn {
  padding: 8px 16px;
  background-color: var(--input-background);
  border: 1px solid var(--border-color);
  border-radius: 4px;
  cursor: pointer;
  color: var(--text-primary);
  transition: all 0.2s ease;
}

#targetResolution {
  color: var(--accent-primary);
  font-weight: bold;
}

/* Active State for Buttons */
.active {
  background-color: var(--accent-primary) !important;
  border-color: var(--accent-primary) !important;
  color: white !important;
}

/* Divider */
.vertical-divider {
  width: 1px;
  align-self: stretch;
  background-color: var(--border-color);
  margin: 0 10px;
}

/* Dimension Controls */
.dimension-controls {
  flex: 1;
  min-width: 0;
}

.dimension-group {
  margin-bottom: 15px;
}

.dimension-label {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 8px;
}

.slider-container {
  display: flex;
  align-items: center;
  gap: 10px;
}

/* Slider Styles */
.slider {
  flex: 1;
  height: 8px;
  -webkit-appearance: none;
  background: var(--input-border);
  border-radius: 4px;
  outline: none;
}

.slider::-webkit-slider-thumb {
  -webkit-appearance: none;
  width: 16px;
  height: 16px;
  background: var(--accent-primary);
  border-radius: 50%;
  cursor: pointer;
}

.slider::-moz-range-thumb {
  width: 16px;
  height: 16px;
  background: var(--accent-primary);
  border-radius: 50%;
  cursor: pointer;
  border: none;
}

/* Number Input Styles */
.slider-container input[type="number"] {
  width: 70px;
  text-align: center;
  padding: 6px;
  border: 1px solid var(--border-color);
  border-radius: 4px;
  background-color: var(--input-background);
  color: var(--text-primary);
}

/* Hover Effects */
.aspect-ratio-btn:hover,
.orientation-btn:hover,
.upscale-btn:hover {
  background-color: var(--background-secondary);
}

/* Responsive Styles */
@media (max-width: 1200px) {
  .orientation-and-upscale-group {
    flex-direction: column;
  }

  .radio-settings {
    width: 100%;
  }

  .vertical-divider {
    display: none;
  }

  .dimension-controls {
    width: 100%;
  }
}

@media (max-width: 768px) {
  .aspect-ratio-buttons,
  .orientation-buttons {
    flex-wrap: wrap;
  }

  .aspect-ratio-btn,
  .orientation-btn {
    flex: 1;
    min-width: 60px;
  }
}
</style>