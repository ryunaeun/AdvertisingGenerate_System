<template>
  <div class="generate-content">
    <!-- 이미지 프리뷰 영역 -->
    <div class="image-preview-area">
      <img 
        v-for="(image, index) in previewImages" 
        :key="index"
        :src="image"
        class="preview-image"
        :style="{ display: image ? 'block' : 'none' }"
      >
      <!-- 로딩 오버레이 -->
      <div class="loading-overlay" v-if="showLoadingOverlay">
        <div class="loading-content">
          <div class="loading-spinner"></div>
          <p class="loading-message">
            {{ loadingMessage }}
          </p>
        </div>
      </div>
    </div>

    <!-- 광고 추천 정보 섹션 -->
    <div class="recommendations-section">
      <div class="recommendation-card">
        <div class="recommendation-item">
          <span class="recommendation-label">최적 시간대: </span>
          <span class="recommendation-value">{{ recommendations ? recommendations.time : '미생성' }}</span>
        </div>
        <div class="recommendation-item">
          <span class="recommendation-label">추천 광고 유형: </span>
          <span class="recommendation-value">{{ recommendations ? recommendations.adtype : '미생성' }}</span>
        </div>
      </div>
    </div>

    <!-- 프롬프트 입력 및 결과 영역 -->
    <div class="form-group">
      <label for="generatedPrompt">생성된 프롬프트:</label>
      <div class="prompt-input-wrapper">
        <textarea 
          id="generatedPrompt" 
          v-model="promptContent"
          placeholder="Generated prompt will appear here..."
        ></textarea>
      </div>
    </div>

    <!-- 버튼 영역 -->
    <div class="button-group">
      <button 
        class="prompt-to-video-btn"
        @click="handleVideoGeneration"
        :disabled="!promptContent || isLoading || isGeneratingPrompt"
      >
        프롬프트로 광고 생성
      </button>
      <button 
        class="img-to-video-btn"
        @click="handleImgVideoGeneration"
        :disabled="!promptContent || isLoading || isGeneratingPrompt"
      >
        이미지로 광고 생성
      </button>
      <button 
        class="example-image-btn"
        @click="generateExampleImages"
        :disabled="!promptContent || isLoading || isGeneratingPrompt"
      >
        예시 이미지 생성
      </button>
    </div>
  </div>
</template>

<script>
export default {
  name: 'GenerateContent',
  props: {
    serverUrl: {
      type: String,
      required: true
    },
    savePath: {
      type: Object,
      required: true,
      validator: (value) => {
        return value.hasOwnProperty('userId') && value.hasOwnProperty('subPath');
      }
    },
    isGeneratingPrompt: {
      type: Boolean,
      default: false
    }
  },

  data() {
    return {
      promptContent: '',
      recommendations: null,
      isLoading: false,
      previewImages: ['', '', '', '']
    }
  },

  computed: {
    loadingMessage() {
      return this.isGeneratingPrompt 
        ? 'AI가 요청을 분석하여 프롬프트를 생성하는 중입니다'
        : 'AI가 예시 이미지를 생성하는 중입니다';
    },
    
    showLoadingOverlay() {
      return this.isLoading || this.isGeneratingPrompt;
    }
  },

  methods: {
    setPromptContent(content, recommendations = null) {
      this.promptContent = content;
      this.recommendations = recommendations;
    },

    handleVideoGeneration() {
      sessionStorage.setItem('videoPrompt', this.promptContent);
      this.$router.push('/txt2vid-generator');
    },

    handleImgVideoGeneration() {
      sessionStorage.setItem('videoPrompt', this.promptContent);
      this.$router.push('/img2vid-generator');
    },

    async generateExampleImages() {
      if (!this.promptContent) {
        alert('프롬프트를 먼저 생성해주세요.');
        return;
      }

      this.isLoading = true;
      this.previewImages = ['', '', '', ''];

      try {
        const response = await fetch(`${this.serverUrl}/generate_examples`, {
          method: 'POST',
          headers: {
            'Content-Type': 'application/json',
            'Accept': 'application/json'
          },
          body: JSON.stringify({
            prompt: this.promptContent,
            savePath: `${this.savePath.userId}\\${this.savePath.subPath}`
          })
        });

        if (!response.ok) {
          throw new Error(`Server responded with ${response.status}`);
        }

        const data = await response.json();
        
        if (data.success && data.image_paths) {
          this.previewImages = data.image_paths.map(path => 
            `${this.serverUrl}/output/${path}`
          );
        } else {
          throw new Error('이미지 생성 실패');
        }
      } catch (error) {
        console.error('이미지 생성 오류:', error);
        alert('이미지 생성 중 오류 발생: ' + error.message);
      } finally {
        this.isLoading = false;
      }
    }
  }
}
</script>

<style scoped>
.generate-content {
  width: 100%;
}

.image-preview-area {
  position: relative;
  display: flex;
  justify-content: space-between;
  gap: 10px;
  margin-bottom: 20px;
  min-height: 200px;
  background-color: var(--background-primary);
  border-radius: 4px;
  padding: 10px;
}

.preview-image {
  flex: 1;
  max-width: 23%;
  height: 200px;
  border-radius: 4px;
  object-fit: contain;
}

/* 로딩 오버레이 스타일 */
.loading-overlay {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background-color: rgba(255, 255, 255, 0.95);
  display: flex;
  justify-content: center;
  align-items: center;
  border-radius: 4px;
  z-index: 100;
}

.loading-content {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 20px;
  padding: 20px;
}

.loading-spinner {
  width: 50px;
  height: 50px;
  border: 4px solid rgba(92, 180, 148, 0.2);
  border-top: 4px solid #5CB494;
  border-radius: 50%;
  animation: spin 1s linear infinite;
}

.loading-message {
  color: #344767;
  font-size: 16px;
  font-weight: 500;
  text-align: center;
  margin: 0;
  padding: 0 20px;
  white-space: nowrap;
}

@keyframes spin {
  0% { transform: rotate(0deg); }
  100% { transform: rotate(360deg); }
}

/* 추천 정보 스타일 */
.recommendations-section {
  margin: 10px 0;
}

.recommendation-card {
  background-color: #f8f9fa;
  border: 1px solid #e9ecef;
  border-radius: 8px;
  padding: 10px;
  display: flex;
  justify-content: space-around;
  align-items: center;
}

.recommendation-item {
  display: flex;
  flex-direction: row;
  align-items: center;
  gap: 8px;
}

.recommendation-label {
  font-size: 16px;
  color: #6c757d;
  font-weight: 500;
  white-space: nowrap;
}

.recommendation-value {
  font-size: 16px;
  color: #5CB494;
  font-weight: 600;
  margin-left: 4px;
}

.form-group {
  margin-bottom: 15px;
}

.form-group label {
  display: block;
  margin-bottom: 5px;
  font-weight: 500;
}

.prompt-input-wrapper {
  position: relative;
}

textarea {
  width: 100%;
  min-height: 150px;
  padding: 12px;
  border: 1px solid var(--input-border);
  border-radius: 4px;
  resize: vertical;
  font-size: 14px;
  line-height: 1.5;
}

.button-group {
  display: flex;
  gap: 10px;
  margin-bottom: 20px;
}

.button-group button {
  flex: 1;
  padding: 10px 20px;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  color: white;
  transition: all 0.3s ease;
  font-size: 14px;
}

.prompt-to-video-btn {
  background-color: var(--accent-primary);
}

.img-to-video-btn {
  background-color: #4a6cf7;
}

.example-image-btn {
  background-color: #3498db;
}

.button-group button:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

.button-group button:hover:not(:disabled) {
  opacity: 0.9;
}
</style>