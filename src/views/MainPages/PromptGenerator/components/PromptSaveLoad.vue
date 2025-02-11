<template>
 <div>
   <div class="prompt-actions">
     <button class="save-prompt-btn" @click="showSaveDialog">프롬프트 저장</button>
     <button class="load-prompt-btn" @click="showLoadDialog">프롬프트 불러오기</button>
   </div>

   <!-- 저장 다이얼로그 -->
   <div v-if="showingSaveModal" class="modal" @click.self="closeSaveDialog">
     <div class="modal-content">
       <h2>프롬프트 저장</h2>
       <input 
         type="text" 
         id="saveFileName" 
         v-model="saveFileName" 
         placeholder="파일 이름을 입력하세요"
       >
       <div class="modal-buttons">
         <button class="save-button" @click="savePromptFile">저장</button>
         <button class="cancel-button" @click="closeSaveDialog">취소</button>
       </div>
     </div>
   </div>

   <!-- 불러오기 다이얼로그 -->
   <div v-if="showingLoadModal" class="modal" @click.self="closeLoadDialog">
     <div class="load-modal-container" :class="{ 'shifted': showingPreview }">
       <div class="file-browser-modal">
         <h2>프롬프트 불러오기</h2>
         <div class="file-grid">
           <div 
             v-for="file in promptFiles" 
             :key="file"
             class="file-box"
             :class="{ 'selected': selectedFile === file }"
             @click="selectFile(file)"
             @mouseenter="hoveredFile = file"
             @mouseleave="hoveredFile = null"
           >
             <div class="file-content">
               <span class="material-icons-round file-icon">description</span>
               <span class="file-name">{{ file }}</span>
             </div>
             <div class="file-actions" v-show="hoveredFile === file">
               <button class="action-btn preview-btn" @click="showPreview(file)">
                 <span class="material-icons-round">search</span>
               </button>
               <button class="action-btn delete-btn">
                 <span class="material-icons-round">close</span>
               </button>
             </div>
           </div>
         </div>
         <div class="modal-buttons">
           <button class="load-button" @click="loadPromptFile" :disabled="!selectedFile">
             불러오기
           </button>
           <button class="cancel-button" @click="closeLoadDialog">취소</button>
         </div>
       </div>

       <!-- 프리뷰 모달 -->
       <div v-if="showingPreview" class="preview-modal">
         <div class="preview-header">
           <h3>프롬프트 내용 보기</h3>
         </div>
         <div class="file-info">
           <span class="material-icons-round">description</span>
           <span class="filename">{{ selectedFile }}</span>
         </div>
         <div class="preview-content">
           <pre>{{ previewContent }}</pre>
         </div>
         <div class="preview-footer">
           <div class="modal-buttons">
             <button class="load-button" @click="loadPromptFile" :disabled="!selectedFile">
               불러오기
             </button>
             <button class="cancel-button" @click="closePreview">
               닫기
             </button>
           </div>
         </div>
       </div>
     </div>
   </div>
 </div>
</template>

<script>
export default {
 name: 'PromptSaveLoad',
 props: {
   serverUrl: {
     type: String,
     required: true
   },
   userId: {
     type: String,
     required: true
   },
   promptContent: {
     type: String,
     required: true
   }
 },

 data() {
   return {
     showingSaveModal: false,
     showingLoadModal: false,
     showingPreview: false,
     saveFileName: '',
     selectedFile: '',
     hoveredFile: null,
     promptFiles: [],
     previewContent: ''
   }
 },

 methods: {
   showSaveDialog() {
     this.saveFileName = '';
     this.showingSaveModal = true;
   },

   closeSaveDialog() {
     this.showingSaveModal = false;
     this.saveFileName = '';
   },

   showLoadDialog() {
     this.loadPromptFiles();
     this.showingLoadModal = true;
   },

   closeLoadDialog() {
     this.showingLoadModal = false;
     this.selectedFile = '';
     this.showingPreview = false;
   },

   async selectFile(fileName) {
     this.selectedFile = fileName;
     if (this.showingPreview) {
       await this.loadPreviewContent(fileName);
     }
   },

   async loadPreviewContent(fileName) {
     try {
       const response = await fetch(`${this.serverUrl}/load_prompt?userId=${encodeURIComponent(this.userId)}&fileName=${encodeURIComponent(fileName)}`);
       
       if (!response.ok) {
         throw new Error(`Server responded with ${response.status}`);
       }

       const data = await response.json();
       if (data.success && data.content) {
         this.previewContent = data.content;
       } else {
         throw new Error("프롬프트 내용이 비어있습니다");
       }
     } catch (error) {
       console.error('프롬프트 미리보기 로드 오류:', error);
       alert('프롬프트 미리보기 로드 중 오류 발생: ' + error.message);
     }
   },

   async showPreview(fileName) {
     await this.loadPreviewContent(fileName);
     this.selectedFile = fileName;
     this.showingPreview = true;
   },

   closePreview() {
     this.showingPreview = false;
     this.previewContent = '';
   },

   async loadPromptFiles() {
     try {
       const response = await fetch(`${this.serverUrl}/load_prompts?userId=${encodeURIComponent(this.userId)}`);
       
       if (!response.ok) {
         throw new Error(`Server responded with ${response.status}`);
       }

       const data = await response.json();
       this.promptFiles = data.files || [];
     } catch (error) {
       console.error('프롬프트 목록 로드 오류:', error);
       alert('프롬프트 목록 로드 중 오류 발생: ' + error.message);
       this.promptFiles = [];
     }
   },

   async loadPromptFile() {
     if (!this.selectedFile) {
       alert("불러올 파일을 선택해주세요.");
       return;
     }

     try {
       const response = await fetch(`${this.serverUrl}/load_prompt?userId=${encodeURIComponent(this.userId)}&fileName=${encodeURIComponent(this.selectedFile)}`);

       if (!response.ok) {
         throw new Error(`Server responded with ${response.status}`);
       }

       const data = await response.json();
       if (!data.success) {
         throw new Error(data.error || '프롬프트 불러오기 실패');
       }

       if (data.content) {
         this.$emit('prompt-loaded', data.content);
         alert("프롬프트를 성공적으로 불러왔습니다!");
         this.closeLoadDialog();
       } else {
         throw new Error("프롬프트 내용이 비어있습니다");
       }
     } catch (error) {
       console.error('프롬프트 불러오기 오류:', error);
       alert('프롬프트 불러오기 중 오류 발생: ' + error.message);
     }
   },

   async savePromptFile() {
     if (!this.saveFileName) {
       alert("파일 이름을 입력해주세요.");
       return;
     }

     try {
       const response = await fetch(`${this.serverUrl}/save_prompt`, {
         method: 'POST',
         headers: {
           'Content-Type': 'application/json',
           'Accept': 'application/json'
         },
         body: JSON.stringify({
           userId: this.userId,
           fileName: this.saveFileName,
           content: this.promptContent
         })
       });

       if (!response.ok) {
         throw new Error(`Server responded with ${response.status}`);
       }

       const data = await response.json();
       if (data.success) {
         alert('프롬프트가 성공적으로 저장되었습니다!');
         this.closeSaveDialog();
       } else {
         throw new Error(data.error || '프롬프트 저장 실패');
       }
     } catch (error) {
       console.error('프롬프트 저장 오류:', error);
       alert('프롬프트 저장 중 오류 발생: ' + error.message);
     }
   }
 }
}
</script>

<style scoped>
.modal {
 position: fixed;
 z-index: 1000;
 left: 0;
 top: 0;
 width: 100%;
 height: 100%;
 background-color: rgba(0, 0, 0, 0.5);
 display: flex;
 justify-content: center;
 align-items: center;
}

.load-modal-container {
 display: flex;
 gap: 20px;
 transition: transform 0.3s ease;
}

.load-modal-container.shifted {
 transform: translateX(0px);
}

.modal-content,
.file-browser-modal {
 background-color: white;
 padding: 30px;
 border-radius: 12px;
 width: 800px;
}

.modal-content {
 width: 400px;
}

.file-browser-modal {
 width: 800px;
}

.preview-modal {
 background-color: white;
 padding: 25px;
 border-radius: 12px;
 width: 600px;
 height: 600px;
 display: flex;
 flex-direction: column;
 box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
}

.preview-header {
 display: flex;
 align-items: center;
 margin-bottom: 20px;
}

.preview-header h3 {
 flex: 1;
}

.preview-content {
 flex: 1;
 overflow-y: auto;
 padding: 15px;
 background-color: #f8f9fa;
 border-radius: 8px;
 margin-bottom: 15px;
}

.preview-footer {
 display: flex;
 justify-content: flex-end;
}

.file-info {
 display: flex;
 align-items: center;
 gap: 8px;
 padding: 10px 15px;
 background-color: #f8f9fa;
 border-radius: 8px;
 margin-bottom: 15px;
}

.file-info .material-icons-round {
 color: #5CB494;
 font-size: 20px;
}

.file-info .filename {
 color: #344767;
 font-weight: 500;
}

.preview-content pre {
 white-space: pre-wrap;
 word-wrap: break-word;
 margin: 0;
 font-family: monospace;
}

.file-grid {
 display: grid;
 grid-template-columns: repeat(auto-fill, minmax(140px, 1fr));
 gap: 15px;
 margin: 20px 0;
 max-height: 400px;
 overflow-y: auto;
 padding: 10px;
}

.file-box {
 position: relative;
 background-color: #f8f9fa;
 border: 2px solid #e9ecef;
 border-radius: 8px;
 padding: 15px;
 cursor: pointer;
 transition: all 0.2s ease;
}

.file-box:hover {
 background-color: #e9ecef;
 border-color: #5CB494;
}

.file-box.selected {
 border-color: #5CB494;
 background-color: #e6f3ef;
 box-shadow: 0 0 0 2px rgba(92, 180, 148, 0.2);
}

.file-content {
 display: flex;
 flex-direction: column;
 align-items: center;
 gap: 8px;
}

.file-icon {
 font-size: 24px;
 color: #5CB494;
}

.file-name {
 font-size: 12px;
 text-align: center;
 word-break: break-all;
}

.file-actions {
 position: absolute;
 top: 5px;
 right: 5px;
 display: flex;
 gap: 5px;
}

.action-btn {
 padding: 4px;
 background: white;
 border: 1px solid #ddd;
 border-radius: 4px;
 cursor: pointer;
 display: flex;
 align-items: center;
 justify-content: center;
}

.action-btn .material-icons-round {
 font-size: 16px;
}

.preview-btn {
 color: #5CB494;
}

.delete-btn {
 color: #dc3545;
}

.action-btn:hover {
 background-color: #f8f9fa;
}

.modal-buttons {
 display: flex;
 justify-content: flex-end;
 gap: 10px;
 margin-top: 20px;
}

.modal-buttons button {
 padding: 8px 16px;
 border: none;
 border-radius: 4px;
 cursor: pointer;
 color: white;
}

button:disabled {
 opacity: 0.5;
 cursor: not-allowed;
}

.save-button {
 background-color: #5CB494;
}

.load-button {
 background-color: #5CB494;
}

.cancel-button {
 background-color: #dc3545;
 padding: 8px 16px;
 border: none;
 border-radius: 4px;
 cursor: pointer;
 color: white;
 transition: background-color 0.2s ease;
}

.cancel-button:hover {
 background-color: #c82333;
}

.prompt-actions {
 display: flex;
 gap: 10px;
}

.save-prompt-btn,
.load-prompt-btn {
 flex: 1;
 padding: 8px 16px;
 border: none;
 border-radius: 4px;
 cursor: pointer;
 color: white;
 transition: background-color 0.2s ease;
}

.save-prompt-btn {
 background-color: #5CA8B4 !important;
}

.load-prompt-btn {
 background-color: #B4D4C5 !important;
}

h2, h3 {
 color: #344767;
 margin-top: 0;
 margin-bottom: 20px;
}
</style>