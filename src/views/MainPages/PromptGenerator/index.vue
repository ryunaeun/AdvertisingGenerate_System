<template>
  <div class="prompt-generator">
    <Header />
    <div class="main-container">
      <div class="prompt-gen-container">
        <h1>프롬프트 작성하기</h1>
        <div class="form-group">
          <TargetSettings 
            v-if="currentServerUrl"
            :server-url="currentServerUrl"
            @settings-change="handleSettingsChange"
          />
          
          <PromptGeneration
            :server-url="currentServerUrl"
            :target-settings="currentSettings"
            @prompt-generated="handlePromptGenerated"
            @start-image-generation="handleStartImageGeneration"
            ref="promptGeneration"
          />
        </div>
      </div>

      <div class="preview-container">
        <GenerateContent 
          :server-url="currentServerUrl"
          :save-path="savePath"
          ref="generateContent"
        />
        
        <PromptSaveLoad 
          v-if="currentServerUrl"
          :server-url="currentServerUrl"
          :user-id="savePath.userId"
          :prompt-content="generatedPrompt"
          @prompt-loaded="handlePromptLoaded"
        />
        
        <PathSettings @path-change="handlePathChange" />
      </div>
    </div>
  </div>
</template>

<script>
import Header from "../HomePage/components/Header.vue";
import TargetSettings from './components/TargetSettings.vue'
import PathSettings from './components/PathSettings.vue'
import PromptSaveLoad from './components/PromptSaveLoad.vue'
import GenerateContent from './components/GenerateContent.vue'
import PromptGeneration from './components/PromptGeneration.vue'
import '@/assets/css/common_styles.css'
import '@/assets/css/prompt_gen_styles.css'
import '@/assets/css/prompt_save_load.css'

export default {
  name: 'PromptGenerator',
  components: {
    Header,
    TargetSettings,
    PathSettings,
    PromptSaveLoad,
    GenerateContent,
    PromptGeneration
  },
  
  data() {
    return {
      serverUrls: [
        'http://125.181.20.252:8888',
        'http://192.168.219.101:8888'
      ],
      currentServerUrl: null,
      currentSettings: null,
      savePath: {
        userId: 'KTaivle',
        subPath: 'examples',
        fullPath: 'KTaivle\\examples'
      },
      generatedPrompt: '',
      isLoading: false
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

    handleSettingsChange(settings) {
      this.currentSettings = settings;
      console.log('Updated settings:', settings);
    },

    handlePromptLoaded(content) {
      this.generatedPrompt = content;
      // GenerateContent 컴포넌트의 프롬프트 내용 업데이트
      if (this.$refs.generateContent) {
        this.$refs.generateContent.setPromptContent(content);
      }
    },

    handlePathChange(pathData) {
      this.savePath = pathData;
      console.log('Save path updated:', this.savePath);
    },

    handlePromptGenerated(prompt) {
      this.generatedPrompt = prompt;
      // GenerateContent 컴포넌트의 프롬프트 내용 업데이트
      if (this.$refs.generateContent) {
        this.$refs.generateContent.setPromptContent(prompt);
      }
    },

    handleStartImageGeneration() {
      // 프롬프트 생성 후 자동으로 이미지 생성 시작
      if (this.$refs.generateContent) {
        this.$refs.generateContent.generateExampleImages();
      }
    }
  },

  async mounted() {
    try {
      await this.findAvailableServer();
      // sessionStorage에서 전달된 프롬프트 확인
      const videoPrompt = sessionStorage.getItem('videoPrompt');
      console.log('Checking for video prompt in mounted:', videoPrompt); // 디버깅용 로그
      if (videoPrompt && this.$refs.generateContent) {
        this.$refs.generateContent.setPromptContent(videoPrompt);
        sessionStorage.removeItem('videoPrompt');
      }
    } catch (error) {
      console.error('서버 연결 실패:', error);
      alert('서버에 연결할 수 없습니다. 나중에 다시 시도해주세요.');
    }
  }
}
</script>

<style scoped>
.prompt-generator {
 min-height: 100vh;
}

.main-container {
 padding-top: 90px;
}

.button-group {
 display: flex;
 gap: 10px;
 margin-top: 0px;
 margin-bottom: 10px;
}

/* 공통 버튼 스타일 */
.button-group button,
.generate-prompt-btn {
 padding: 10px 20px;
 border: none;
 border-radius: 4px;
 cursor: pointer;
 color: white;
 transition: background-color 0.3s ease, transform 0.2s ease;
 font-size: 1rem;
}

.button-group button {
 flex: 1;
}

/* 각 버튼별 색상 */
.generate-prompt-btn {
 width: 100%;
 margin-top: 10px;
 background-color: #5CB494;
}

.prompt-to-video-btn {
 background-color: #5CB494;
}

.example-image-btn {
 background-color: #3498db;
}

.save-prompt-btn {
 background-color: #8A2BE2;
}

.load-prompt-btn {
 background-color: #FF69B4;
}

/* 호버 효과 */
.button-group button:hover,
.generate-prompt-btn:hover {
 opacity: 0.9;
 transform: translateY(-1px);
}

/* 활성화 효과 */
.button-group button:active,
.generate-prompt-btn:active {
 transform: translateY(1px);
}
</style>