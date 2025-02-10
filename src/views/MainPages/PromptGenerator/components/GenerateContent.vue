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
      <div class="loading" :style="{ display: isLoading ? 'block' : 'none' }"></div>
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
      <textarea 
        id="generatedPrompt" 
        v-model="promptContent"
        placeholder="Generated prompt will appear here..."
      ></textarea>
    </div>

    <!-- 버튼 영역 -->
    <div class="button-group">
      <button 
        class="prompt-to-video-btn"
        @click="handleVideoGeneration"
        :disabled="!promptContent || isLoading"
      >
        비디오 생성 페이지
      </button>
      <button 
        class="example-image-btn"
        @click="generateExampleImages"
        :disabled="!promptContent || isLoading"
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

  methods: {
    // 외부에서 프롬프트 내용과 추천 정보를 설정할 수 있는 메서드
    setPromptContent(content, recommendations = null) {
      this.promptContent = content;
      this.recommendations = recommendations;
    },

    handleVideoGeneration() {
      sessionStorage.setItem('videoPrompt', this.promptContent);
      this.$router.push('/txt2vid-generator');
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

/* 추천 정보 스타일 */
.recommendations-section {
  margin: 10px 0;  /* 20px에서 10px로 수정 */
}

.recommendation-card {
  background-color: #f8f9fa;
  border: 1px solid #e9ecef;
  border-radius: 8px;
  padding: 10px;  /* 20px에서 10px로 수정 */
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

.recommendation-value:empty::before,
.recommendation-value:contains('미생성') {
  color: #adb5bd;
  font-style: italic;
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

.loading {
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
}

.form-group {
  margin-bottom: 15px;
}

.form-group label {
  display: block;
  margin-bottom: 5px;
}

.form-group textarea {
  width: 100%;
  min-height: 250px;
  padding: 12px;
  border: 1px solid var(--input-border);
  border-radius: 4px;
  resize: vertical;
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
}

.button-group button:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}
</style>