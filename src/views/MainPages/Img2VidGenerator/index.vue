<template>
  <div class="prompt-generator">
    <Header />
    <div class="main-container">
      <div class="prompt-gen-container">
        <h1>이미지로 광고 만들기</h1>
        <div class="form-group">
          <!-- 이미지 업로드 섹션 -->
          <div class="image-upload-section">
            <label for="image-upload">이미지 업로드:</label>
            <div class="image-upload-container">
              <div 
                class="image-preview"
                :class="{ 'has-image': selectedImage }"
              >
                <img 
                  v-if="selectedImage" 
                  :src="selectedImage" 
                  alt="Selected image"
                  @click="openImageSelector"
                />
                <div 
                  v-else 
                  class="upload-placeholder"
                  @click="openImageSelector"
                >
                  <span class="material-icons-round">add_photo_alternate</span>
                  <span>클릭하여 이미지 선택</span>
                </div>
              </div>
              <input
                type="file"
                id="image-upload"
                ref="imageInput"
                accept="image/*"
                @change="handleImageUpload"
                style="display: none"
              >
            </div>
          </div>

          <!-- 프롬프트 입력 및 저장/불러오기 -->
          <label for="prompt">프롬프트 입력:</label>
          <textarea 
            id="prompt" 
            v-model="generatedPrompt"
            name="prompt" 
            placeholder="Enter your prompt here..." 
            rows="3"
          >man drinking coffee in cafe bright morning</textarea>
          
          <button 
            class="prompt-gen-btn"
            @click="goToPromptGenerator"
          >
            프롬프트 생성으로
          </button>
          
          <PromptSaveLoad 
            v-if="currentServerUrl"
            :server-url="currentServerUrl"
            :user-id="savePath.userId"
            :prompt-content="generatedPrompt"
            @prompt-loaded="handlePromptLoaded"
          />

          <!-- 비디오 설정 컴포넌트 -->
          <VideoSettings
            @settings-change="handleVideoSettingsChange"
          />

          <!-- 비디오 생성 컨트롤 -->
          <div class="form-group video-controls-group">
            <div class="input-row">
              <div class="input-button-wrapper">
                <button 
                  id="generateBtn" 
                  @click="generateVideos"
                  :disabled="isLoading || !selectedImage"
                >영상 생성하기</button>
              </div>
            </div>
          </div>
        </div>
      </div>

      <div class="preview-container">
        <!-- VideoOutput 컴포넌트로 교체 -->
        <VideoOutput
          :video-count="videoCount"
          :video-urls="videoUrls"
          :loading-videos="loadingVideos"
          @video-loaded="handleVideoLoaded"
          @video-error="handleVideoError"
        />
        
        <div v-if="errorMessage" class="error-message">{{ errorMessage }}</div>
        
        <div class="bottom-controls">
          <PathSettings @path-change="handlePathChange" />
          <router-link to="/result" class="confirm-btn">
            결과 확인
          </router-link>
        </div>
      </div>
    </div>
  </div>
</template>

# Img2VidGenerator/index.vue의 script 부분

<script>
import Header from "../HomePage/components/Header.vue";
import PathSettings from './components/PathSettings.vue'
import PromptSaveLoad from './components/PromptSaveLoad.vue'
import VideoSettings from './components/VideoSettings.vue'
import VideoOutput from './components/VideoOutput.vue'

export default {
  name: 'Img2VidGenerator',
  
  components: {
    Header,
    PathSettings,
    PromptSaveLoad,
    VideoSettings,
    VideoOutput
  },
  
  data() {
    return {
      serverUrls: [
        'http://125.181.20.252:8888',
        'http://192.168.219.101:8888'
      ],
      currentServerUrl: null,
      selectedImage: null,
      uploadedImagePath: null,
      generatedPrompt: 'man drinking coffee in cafe bright morning',
      savePath: {
        userId: 'KTaivle',
        subPath: 'videos',
        fullPath: 'KTaivle\\videos'
      },
      videoSettings: null,
      videoCount: 1,
      videoUrls: [],
      loadingVideos: [],
      isLoading: false,
      errorMessage: ''
    }
  },

  computed: {
    gridClass() {
      switch(Number(this.videoCount)) {
        case 1: return 'single';
        case 2: return 'horizontal-split';
        case 3:
        case 4: return 'grid-four';
        default: return 'single';
      }
    }
  },

  methods: {
    openImageSelector() {
      this.$refs.imageInput.click();
    },

    async handleImageUpload(event) {
      const file = event.target.files[0];
      if (file) {
        try {
          // 이미지 파일 미리보기 생성
          this.selectedImage = URL.createObjectURL(file);
          
          // FormData 생성
          const formData = new FormData();
          formData.append('image', file);
          formData.append('userId', this.savePath.userId);
          formData.append('subPath', 'images');  // 이미지 저장을 위한 서브 경로

          // 서버에 이미지 업로드
          const response = await fetch(`${this.currentServerUrl}/upload_image`, {
            method: 'POST',
            body: formData
          });

          if (!response.ok) {
            throw new Error('이미지 업로드 실패');
          }

          const data = await response.json();
          if (data.success) {
            this.uploadedImagePath = data.path;
            console.log('Image uploaded successfully:', this.uploadedImagePath);
          } else {
            throw new Error(data.error || '이미지 업로드 실패');
          }
        } catch (error) {
          console.error('이미지 업로드 오류:', error);
          this.errorMessage = `이미지 업로드 중 오류가 발생했습니다: ${error.message}`;
          // 업로드 실패 시 미리보기 이미지와 경로 초기화
          this.selectedImage = null;
          this.uploadedImagePath = null;
        }
      }
    },

    async findAvailableServer() {
      for (const url of this.serverUrls) {
        try {
          const response = await fetch(`${url}/static/config/target_settings.json`, {
            method: 'HEAD',
            headers: {
              'Accept': 'application/json',
            },
            mode: 'cors',
            timeout: 5000
          });
          
          if (response.ok) {
            this.currentServerUrl = url;
            console.log('Connected to server:', url);
            return url;
          }
        } catch (error) {
          console.log(`Failed to connect to ${url}:`, error);
        }
      }
      throw new Error('사용 가능한 서버를 찾을 수 없습니다');
    },

    goToPromptGenerator() {
      sessionStorage.setItem('videoPrompt', this.generatedPrompt);
      this.$router.push('/prompt-generator');
    },

    handlePromptLoaded(content) {
      this.generatedPrompt = content;
    },

    handlePathChange(pathData) {
      this.savePath = pathData;
    },

    handleVideoSettingsChange(settings) {
      this.videoSettings = settings;
    },

    handleVideoLoaded(index) {
      console.log(`Video ${index + 1} loaded successfully`);
      this.loadingVideos[index] = false;
    },

    handleVideoError(index) {
      console.error(`Failed to load video ${index + 1}`);
      this.loadingVideos[index] = false;
      this.errorMessage = `Failed to load video ${index + 1}`;
    },

    async generateVideos() {
      if (!this.selectedImage) {
        this.errorMessage = '이미지를 선택해주세요';
        return;
      }

      if (!this.generatedPrompt.trim()) {
        this.errorMessage = '프롬프트를 입력해주세요';
        return;
      }

      if (!this.videoSettings) {
        this.errorMessage = '비디오 설정이 초기화되지 않았습니다';
        return;
      }

      this.errorMessage = '';
      this.isLoading = true;
      this.videoUrls = new Array(Number(this.videoCount)).fill('');
      this.loadingVideos = new Array(Number(this.videoCount)).fill(true);

      try {
        for (let i = 0; i < this.videoCount; i++) {
          await this.generateSingleVideo(i);
        }
      } catch (error) {
        console.error('Error generating videos:', error);
        this.errorMessage = error.message;
      } finally {
        this.isLoading = false;
      }
    },

    async generateSingleVideo(index) {
      try {
        if (!this.uploadedImagePath) {
          throw new Error('이미지를 먼저 업로드해주세요');
        }

        const requestData = {
          prompt: this.generatedPrompt.trim(),
          imagePath: this.uploadedImagePath,
          useRandomSeed: true,
          frameLength: this.videoSettings.frameLength,
          width: this.videoSettings.width,
          height: this.videoSettings.height,
          savePath: this.savePath.fullPath
        };

        const response = await fetch(`${this.currentServerUrl}/generate_ltxv`, {
          method: 'POST',
          headers: {
            'Content-Type': 'application/json'
          },
          body: JSON.stringify(requestData)
        });

        if (!response.ok) {
          throw new Error('Failed to generate video');
        }

        const data = await response.json();
        if (!data.success) {
          throw new Error(data.error || 'Video generation failed');
        }

        const videoUrl = `${this.currentServerUrl}/output/${this.savePath.userId}/${data.folder}/${data.filename}`;
        this.videoUrls[index] = videoUrl;

      } catch (error) {
        console.error(`Error generating video ${index + 1}:`, error);
        this.errorMessage = `Error generating video ${index + 1}: ${error.message}`;
        this.loadingVideos[index] = false;
        throw error;
      }
    }
  },

  watch: {
    videoCount(newCount) {
      this.videoUrls = new Array(Number(newCount)).fill('');
      this.loadingVideos = new Array(Number(newCount)).fill(false);
    }
  },

  async mounted() {
    try {
      await this.findAvailableServer();
      
      const transferredPrompt = sessionStorage.getItem('videoPrompt');
      if (transferredPrompt) {
        this.generatedPrompt = transferredPrompt;
        sessionStorage.removeItem('videoPrompt');
      }
    } catch (error) {
      console.error('서버 연결 실패:', error);
      alert('서버에 연결할 수 없습니다. 나중에 다시 시도해주세요.');
    }
  }
}
</script>

<style>
@import '@/assets/css/common_styles.css';
@import '@/assets/css/video_gen_styles.css';
@import '@/assets/css/prompt_save_load.css';

/* 기본 레이아웃 스타일 */
.prompt-generator {
  min-height: 100vh;
}

.main-container {
  padding-top: 90px;
  display: flex;
  gap: 20px;
  max-width: 1600px;
  margin: 0 auto;
}

.prompt-gen-container {
  flex: 0 0 38%;
  background-color: var(--background-secondary);
  border-radius: 8px;
  box-shadow: 0 2px 4px var(--shadow-color);
  padding: 20px;
  height: fit-content;
  max-height: calc(100vh - 120px);
  overflow-y: auto;
}

/* 미리보기 컨테이너 */
.preview-container {
  flex: 1;
  background-color: var(--background-secondary);
  border-radius: 8px;
  box-shadow: 0 2px 4px var(--shadow-color);
  padding: 20px;
  display: flex;
  flex-direction: column;
}

/* 이미지 업로드 관련 스타일 */
.image-upload-section {
  margin-bottom: 20px;
}

.image-upload-container {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 10px;
}

.image-preview {
  width: 100%;
  height: 200px;
  border: 2px dashed var(--border-color);
  border-radius: 8px;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  overflow: hidden;
  transition: all 0.3s ease;
}

.image-preview:hover {
  border-color: var(--accent-primary);
}

.image-preview.has-image {
  border-style: solid;
}

.image-preview img {
  width: 100%;
  height: 100%;
  object-fit: contain;
}

.upload-placeholder {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 8px;
  color: var(--text-secondary);
}

.upload-placeholder .material-icons-round {
  font-size: 48px;
}

/* 폼 그룹 스타일 */
.form-group {
  margin-bottom: 20px;
  width: 100%;
}

.form-group label {
  display: block;
  margin-bottom: 8px;
  font-weight: bold;
  color: var(--text-primary);
}

.form-group textarea {
  width: 100%;
  padding: 12px;
  border: 1px solid var(--border-color);
  border-radius: 4px;
  background-color: var(--input-background);
  color: var(--text-primary);
  font-size: 14px;
  line-height: 1.5;
  resize: vertical;
  min-height: 100px;
}

/* 버튼 스타일 */
.prompt-gen-btn {
  width: 100%;
  padding: 10px 20px;
  background-color: #3498db;
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  margin-top: 10px;
  transition: all 0.3s ease;
}

.prompt-gen-btn:hover {
  background-color: #2980b9;
  opacity: 0.9;
  transform: translateY(-1px);
}

.prompt-gen-btn:active {
  transform: translateY(1px);
}

/* 비디오 컨트롤 스타일 */
.video-controls-group {
  margin-top: 20px;
  margin-bottom: 15px;
}

.input-row {
  display: flex;
  align-items: center;
  gap: 10px;
  width: 100%;
}

.input-button-wrapper {
  flex: 1;
  display: flex;
  align-items: center;
  gap: 10px;
  min-width: 0;
}

#generateBtn {
  flex: 1;
  padding: 10px 20px;
  background-color: var(--accent-primary);
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  font-size: 16px;
  transition: background-color 0.2s ease;
}

#generateBtn:hover {
  background-color: var(--accent-primary-hover);
}

#generateBtn:disabled {
  background-color: var(--accent-disabled);
  cursor: not-allowed;
}

/* 에러 메시지 스타일 */
.error-message {
  color: var(--error-color);
  margin-top: 10px;
  padding: 10px;
  border-radius: 4px;
  background-color: var(--error-background, #fff3f3);
}

/* 제목 스타일 */
h1 {
  color: var(--text-primary);
  margin-bottom: 20px;
  font-size: 24px;
}

/* 하단 컨트롤 스타일 */
.bottom-controls {
  display: flex;
  align-items: center;
  justify-content: flex-end;
  width: 100%;
  padding-top: 10px;
}

.confirm-btn {
  padding: 10px;
  margin-left: 20px;
  background-color: #5CB494;
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  font-size: 20px;
  font-weight: 500;
  text-decoration: none;
  height: 43px;
  width: 200px;
  transition: background-color 0.3s;
  display: flex;
  justify-content: center;
  align-items: center;
}

.confirm-btn:hover {
  background-color: #45a049;
  color: white;
}

/* 반응형 스타일 */
@media (max-width: 1200px) {
  .main-container {
    flex-direction: column;
    padding: 90px 20px 20px;
  }

  .prompt-gen-container {
    flex: none;
    width: 100%;
    max-height: none;
  }

  .preview-container {
    min-height: 400px;
  }
}

@media (max-width: 768px) {
  .image-preview {
    height: 150px;
  }
  
  .prompt-gen-btn,
  .save-prompt-btn,
  .load-prompt-btn {
    width: 100%;
    margin: 5px 0;
  }
  
  .bottom-controls {
    flex-direction: column;
    gap: 10px;
  }
  
  .confirm-btn {
    width: 100%;
    margin-left: 0;
  }
}
</style>