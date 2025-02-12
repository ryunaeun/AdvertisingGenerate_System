<template>
  <div class="prompt-generation">
    <div class="input-section">
      <label for="inputPrompt">추가 요청 사항:</label>
      <textarea 
        id="inputPrompt" 
        class="input-prompt" 
        v-model="inputPrompt"
        placeholder="Enter your text here..."
      ></textarea>
      <button 
        class="generate-prompt-btn" 
        @click="generatePrompt"
        :disabled="isGenerating"
      >
        {{ isGenerating ? '프롬프트 생성중...' : '프롬프트 생성하기' }}
      </button>
    </div>
  </div>
</template>

<script>
export default {
  name: 'PromptGeneration',
  
  props: {
    serverUrl: {
      type: String,
      required: true
    },
    targetSettings: {
      type: Object,
      required: true
    },
    userId: {
      type: String,
      required: true
    }
  },

  data() {
    return {
      inputPrompt: '',
      isGenerating: false
    }
  },

  methods: {
    async generatePrompt() {
      if (this.isGenerating) return;
      
      // 생성 시작 상태 설정 및 이벤트 발생
      this.isGenerating = true;
      this.$emit('generation-start');
      
      try {
        // 프롬프트 생성 요청 데이터 준비
        const requestData = {
          userId: this.userId,
          gender: this.targetSettings.gender || '',
          ageGroup: this.targetSettings.ageGroup || '',
          productCategory: this.targetSettings.productCategory || '',
          seasonEvent: this.targetSettings.seasonEvent || '',
          adTone: this.targetSettings.adTone || '',
          additionalRequests: this.inputPrompt
        };

        console.log('Generating prompt with data:', requestData);

        // 프롬프트 생성 요청
        const response = await fetch(`${this.serverUrl}/generate_prompt`, {
          method: 'POST',
          headers: {
            'Content-Type': 'application/json',
            'Accept': 'application/json'
          },
          body: JSON.stringify(requestData)
        });

        if (!response.ok) {
          throw new Error(`Server responded with ${response.status}`);
        }

        const data = await response.json();

        if (data.success && data.generated_prompt) {
          // 생성된 프롬프트를 부모 컴포넌트에 전달
          this.$emit('prompt-generated', data.generated_prompt, data.recommendations);
          
          // 이미지 생성 시작 알림
          this.$emit('start-image-generation');
        } else {
          throw new Error(data.error || '프롬프트 생성 실패');
        }
      } catch (error) {
        console.error('프롬프트 생성 오류:', error);
        alert('프롬프트 생성 중 오류 발생: ' + error.message);
      } finally {
        // 생성 완료 상태 설정 및 이벤트 발생
        this.isGenerating = false;
        this.$emit('generation-complete');
      }
    },

    // 외부에서 입력 프롬프트를 설정할 수 있는 메서드
    setInputPrompt(prompt) {
      this.inputPrompt = prompt;
    }
  }
}
</script>

<style scoped>
.input-section {
  width: 100%;
  margin-bottom: 20px;
}

.input-prompt {
  width: 100%;
  min-height: 150px;
  padding: 12px;
  border: 1px solid var(--input-border);
  border-radius: 4px;
  resize: vertical;
  margin-bottom: 10px;
}

.generate-prompt-btn {
  width: 100%;
  padding: 10px 20px;
  background-color: #5CB494;
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  transition: all 0.3s ease;
}

.generate-prompt-btn:hover:not(:disabled) {
  opacity: 0.9;
  transform: translateY(-1px);
}

.generate-prompt-btn:active:not(:disabled) {
  transform: translateY(1px);
}

.generate-prompt-btn:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}
</style>