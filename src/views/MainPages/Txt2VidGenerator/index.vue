<template>
  <div class="prompt-generator">
    <Header />
    <div class="main-container">
      <div class="prompt-gen-container">
        <h1>광고 영상 생성하기</h1>
        <div class="form-group">
          <!-- 프롬프트 입력 및 저장/불러오기 -->
          <label for="prompt">프롬프트 입력:</label>
          <textarea 
            id="prompt" 
            v-model="generatedPrompt"
            name="prompt" 
            placeholder="Enter your prompt here..." 
            rows="6"
          >man drinking coffee in cafe bright morning</textarea>
          
          <!-- 프롬프트 생성 페이지로 이동하는 버튼 -->
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
              <label for="videoCount">영상 개수:</label>
              <div class="input-button-wrapper">
                <select v-model="videoCount" id="videoCount" name="videoCount">
                  <option value="1">1</option>
                  <option value="2">2</option>
                  <option value="3">3</option>
                  <option value="4">4</option>
                </select>
                <button 
                  id="generateBtn" 
                  @click="generateVideos"
                  :disabled="isLoading"
                >광고 영상 생성하기</button>
              </div>
            </div>
          </div>
        </div>
      </div>

      <div class="preview-container">
        <!-- 비디오 출력 영역 -->
        <div class="videos-grid" :class="gridClass" id="videosGrid">
          <div 
            v-for="index in videoCount" 
            :key="index"
            class="video-cell"
            :style="{ display: index <= videoCount ? 'flex' : 'none' }"
          >
            <div 
              class="loading" 
              :id="`loading-${index}`"
              v-show="loadingVideos[index-1]"
            ></div>
            <video 
              :id="`outputVideo-${index}`"
              class="output-video" 
              v-show="videoUrls[index-1]"
              controls 
              autoplay 
              loop 
              muted
            >
              <source :src="videoUrls[index-1]" type="video/mp4">
              Your browser does not support the video tag.
            </video>
            <a 
              :id="`downloadLink-${index}`"
              class="download-link" 
              v-show="videoUrls[index-1]"
              :href="videoUrls[index-1]" 
              download
              @click="downloadVideo($event, index-1)"
            >Download Video</a>
          </div>
        </div>
        <div v-if="errorMessage" class="error-message">{{ errorMessage }}</div>
        
        <PathSettings @path-change="handlePathChange" />
      </div>
    </div>
  </div>
</template>

<script>
import Header from "../HomePage/components/Header.vue";
import PathSettings from './components/PathSettings.vue'
import PromptSaveLoad from './components/PromptSaveLoad.vue'
import VideoSettings from './components/VideoSettings.vue'

export default {
  name: 'Txt2VidGenerator',
  
  components: {
    Header,
    PathSettings,
    PromptSaveLoad,
    VideoSettings
  },
  
  data() {
    return {
      serverUrls: [
        'http://125.181.20.252:8888',
        'http://192.168.219.101:8888'
      ],
      currentServerUrl: null,
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
      // 현재 프롬프트를 sessionStorage에 저장
      console.log('Saving prompt:', this.generatedPrompt); // 로그 추가
      sessionStorage.setItem('videoPrompt', this.generatedPrompt);
      // 프롬프트 생성 페이지로 이동
      this.$router.push('/prompt-generator');
    },

    handlePromptLoaded(content) {
      console.log('Prompt loaded:', content);
      this.generatedPrompt = content;
    },

    handlePathChange(pathData) {
      this.savePath = pathData;
      console.log('Save path updated:', this.savePath);
    },

    handleVideoSettingsChange(settings) {
      this.videoSettings = settings;
      console.log('Video settings updated:', settings);
    },

    async generateVideos() {
      if (!this.generatedPrompt.trim()) {
        this.errorMessage = 'Please enter a prompt';
        return;
      }

      if (!this.videoSettings) {
        this.errorMessage = 'Video settings not initialized';
        return;
      }

      this.errorMessage = '';
      this.isLoading = true;
      
      // 비디오 배열 초기화
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
        const requestData = {
          prompt: this.generatedPrompt.trim(),
          useRandomSeed: true,
          frameLength: this.videoSettings.frameLength,
          width: this.videoSettings.width,
          height: this.videoSettings.height,
          enableUpscale: this.videoSettings.enableUpscale,
          savePath: this.savePath.fullPath
        };

        const response = await fetch(`${this.currentServerUrl}/generate`, {
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
        
        // 비디오 로드 확인
        await this.waitForVideoLoad(videoUrl, index);
        
        this.videoUrls[index] = videoUrl;
        this.loadingVideos[index] = false;

      } catch (error) {
        console.error(`Error generating video ${index + 1}:`, error);
        this.errorMessage = `Error generating video ${index + 1}: ${error.message}`;
        this.loadingVideos[index] = false;
        throw error;
      }
    },

    async waitForVideoLoad(videoUrl, index) {
      return new Promise((resolve, reject) => {
        const video = document.getElementById(`outputVideo-${index + 1}`);
        if (!video) {
          reject(new Error('Video element not found'));
          return;
        }

        video.onloadeddata = () => resolve();
        video.onerror = () => reject(new Error('Failed to load video'));

        video.src = videoUrl;
      });
    },

    downloadVideo(event, index) {
      const filename = this.videoUrls[index].split('/').pop();
      event.target.download = filename;
    }
  },

  watch: {
    videoCount(newCount) {
      // 비디오 카운트 변경시 배열 초기화
      this.videoUrls = new Array(Number(newCount)).fill('');
      this.loadingVideos = new Array(Number(newCount)).fill(false);
    }
  },

  async mounted() {
    try {
      await this.findAvailableServer();
      
      // sessionStorage에서 전달된 프롬프트 확인
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
/* 기존 CSS 파일들 import */
@import '@/assets/css/common_styles.css';
@import '@/assets/css/video_gen_styles.css';
@import '@/assets/css/prompt_save_load.css';

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
  flex: 0 0 38%;  /* 너비 고정 */
  background-color: var(--background-secondary);
  border-radius: 8px;
  box-shadow: 0 2px 4px var(--shadow-color);
  padding: 20px;
  height: fit-content;  /* 내용물에 맞게 높이 조절 */
  max-height: calc(100vh - 120px);  /* 최대 높이 제한 */
  overflow-y: auto;  /* 내용이 넘칠 경우 스크롤 */
}

.form-group {
  margin-bottom: 20px;  /* 컴포넌트 간 여백 */
  width: 100%;
}

/* 프롬프트 관련 스타일 */
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

/* 프롬프트 생성 버튼 */
.prompt-gen-btn {
  width: 100%;
  padding: 10px 20px;
  background-color: #3498db;  /* 기본 파란색 */
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  margin-top: 10px;
  transition: all 0.3s ease;
}

.prompt-gen-btn:hover {
  background-color: #2980b9;  /* hover시 진한 파란색으로 변경 */
  opacity: 0.9;
  transform: translateY(-1px);
}

.prompt-gen-btn:active {
  transform: translateY(1px);
}

/* 프롬프트 저장/불러오기 버튼과 비디오 설정 사이 여백 */
.prompt-actions {
  margin-bottom: 30px;  /* 여백 증가 */
}

/* 비디오 컨트롤 그룹 스타일 */
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

#videoCount {
  flex: 0 0 auto;
  width: 4rem;
  min-width: 0;
  padding: 8px;
  text-align: center;
  background-color: var(--input-background);
  color: var(--text-primary);
  border: 1px solid var(--border-color);
  border-radius: 4px;
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

/* 미리보기 컨테이너 */
.preview-container {
  flex: 1;  /* 남은 공간 차지 */
  background-color: var(--background-secondary);
  border-radius: 8px;
  box-shadow: 0 2px 4px var(--shadow-color);
  padding: 20px;
  display: flex;
  flex-direction: column;
}

/* 비디오 그리드 스타일 */
.videos-grid {
  display: grid;
  gap: 20px;
  width: 100%;
  flex: 1;
  min-height: 400px;
}

.videos-grid.single {
  grid-template-columns: 1fr;
}

.videos-grid.horizontal-split {
  grid-template-columns: repeat(2, 1fr);
}

.videos-grid.grid-four {
  grid-template-columns: repeat(2, 1fr);
  grid-template-rows: repeat(2, 1fr);
}

/* 비디오 셀 스타일 */
.video-cell {
  position: relative;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  background-color: var(--background-primary);
  border-radius: 8px;
  overflow: hidden;
  aspect-ratio: 16/9;
}

.video-cell .loading {
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
}

.video-cell .output-video {
  width: 100%;
  height: 100%;
  object-fit: contain;
}

.download-link {
  position: absolute;
  bottom: 10px;
  left: 50%;
  transform: translateX(-50%);
  background-color: var(--background-secondary);
  padding: 5px 10px;
  border-radius: 4px;
  z-index: 1;
  color: var(--accent-primary);
  text-decoration: none;
}

.download-link:hover {
  text-decoration: underline;
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

  .videos-grid.horizontal-split {
    grid-template-columns: 1fr;
    grid-template-rows: repeat(2, 1fr);
  }
}

@media (max-width: 768px) {
  .video-cell {
    aspect-ratio: auto;
    min-height: 200px;
  }
}
</style>