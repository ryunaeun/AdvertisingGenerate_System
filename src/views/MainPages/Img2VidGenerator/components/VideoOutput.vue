<template>
  <div class="videos-grid" :class="gridClass">
    <div 
      v-for="(url, index) in videoUrls" 
      :key="index"
      class="video-cell"
      :class="{ 'active': index < videoCount }"
    >
      <!-- 로딩 스피너 -->
      <div 
        v-if="isLoading(index)"
        class="loading-spinner"
      >
        <div class="spinner"></div>
        <span class="loading-text">Generating video...</span>
      </div>

      <!-- 비디오 플레이어 -->
      <video 
        v-if="url"
        :id="`outputVideo-${index + 1}`"
        class="output-video" 
        controls 
        autoplay 
        loop 
        muted
        @loadeddata="handleVideoLoaded(index)"
        @error="handleVideoError(index)"
      >
        <source :src="url" type="video/mp4">
        Your browser does not support the video tag.
      </video>

      <!-- 다운로드 링크 -->
      <a 
        v-if="url"
        :id="`downloadLink-${index + 1}`"
        class="download-link" 
        :href="url"
        download
        @click="downloadVideo($event, index)"
      >
        Download Video
      </a>
    </div>
  </div>
</template>

<script>
export default {
  name: 'VideoOutput',
  
  props: {
    videoCount: {
      type: Number,
      default: 1
    },
    videoUrls: {
      type: Array,
      default: () => []
    },
    loadingVideos: {
      type: Array,
      default: () => []
    }
  },
  
  methods: {
    isLoading(index) {
      return this.loadingVideos[index] === true;
    },

    handleVideoLoaded(index) {
      // 비디오 로드 완료 이벤트 부모 컴포넌트에 전달
      this.$emit('video-loaded', index);
      console.log(`Video ${index + 1} loaded successfully`);
    },

    handleVideoError(index) {
      // 비디오 로드 에러 이벤트 부모 컴포넌트에 전달
      this.$emit('video-error', index);
      console.error(`Failed to load video ${index + 1}`);
    },

    downloadVideo(event, index) {
      // 다운로드 파일명 설정
      const filename = this.videoUrls[index].split('/').pop();
      event.target.download = filename;
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
  }
}
</script>

<style scoped>
/* 비디오 그리드 레이아웃 */
.videos-grid {
  display: grid;
  gap: 20px;
  width: 100%;
  height: 100%;;
  min-height: 400px;
}

.videos-grid.single {
  grid-template-columns: 1fr;
}

.videos-grid.horizontal-split {
  grid-template-columns: 1fr;
  grid-template-rows: 1fr 1fr;
}

.videos-grid.grid-four {
  grid-template-columns: repeat(2, 1fr);
  grid-template-rows: repeat(2, 1fr);
}

/* 비디오 셀 */
.video-cell {
  position: relative;
  background-color: var(--background-primary);
  border-radius: 8px;
  overflow: hidden;
  display: none;
  min-height: 300px;
}

.video-cell.active {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
}

/* 로딩 스피너 */
.loading-spinner {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  background-color: rgba(0, 0, 0, 0.1);
  backdrop-filter: blur(2px);
  z-index: 10;
}

.spinner {
  width: 50px;
  height: 50px;
  border: 4px solid rgba(92, 180, 148, 0.2);
  border-top: 4px solid var(--accent-primary);
  border-radius: 50%;
  animation: spin 1s linear infinite;
}

@keyframes spin {
  0% { transform: rotate(0deg); }
  100% { transform: rotate(360deg); }
}

.loading-text {
  margin-top: 16px;
  color: var(--text-primary);
  font-size: 14px;
  font-weight: 500;
}

/* 비디오 플레이어 */
.output-video {
  width: 100%;
  height: 100%;
  object-fit: contain;
  background-color: black;
}

/* 다운로드 링크 */
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
  transition: all 0.2s ease;
}

.download-link:hover {
  background-color: var(--accent-primary);
  color: white;
}

/* 반응형 디자인 */
@media (max-width: 1200px) {
  .videos-grid.horizontal-split {
    grid-template-columns: 1fr;
    grid-template-rows: repeat(2, 1fr);
  }
}

@media (max-width: 768px) {
  .video-cell {
    min-height: 200px;
  }
}
</style>