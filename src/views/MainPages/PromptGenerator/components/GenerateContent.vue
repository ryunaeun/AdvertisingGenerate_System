<template>
  <div class="generate-content">
    <!-- 이미지 프리뷰 영역을 상단으로 이동 -->
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
      isLoading: false,
      previewImages: ['', '', '', '']  // 4개의 이미지 슬롯
    }
  },

  methods: {
    async generateExampleImages() {
      if (!this.promptContent) {
        alert('프롬프트를 먼저 생성해주세요.');
        return;
      }

      this.isLoading = true;
      // 이미지 초기화
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
          // 이미지 경로를 전체 URL로 변환하여 설정
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
    },

    handleVideoGeneration() {
      // 비디오 생성 페이지로 이동하기 전에 현재 프롬프트 저장
      sessionStorage.setItem('videoPrompt', this.promptContent);
      // 비디오 생성 페이지로 이동
      window.location.href = '/txt2vid-generator';
    },

    // 외부에서 프롬프트 내용을 설정할 수 있는 메서드
    setPromptContent(content) {
      console.log('Setting prompt content:', content); // 디버깅용 로그
      this.promptContent = content;
    }
  },
  mounted() {
    // sessionStorage에서 전달된 프롬프트 확인
    const videoPrompt = sessionStorage.getItem('videoPrompt');
    console.log('GenerateContent mounted - video prompt:', videoPrompt); // 디버깅용 로그
    if (videoPrompt) {
      console.log('Setting prompt content in GenerateContent'); // 디버깅용 로그
      this.promptContent = videoPrompt; // 직접 설정
      sessionStorage.removeItem('videoPrompt');
    }
  },
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