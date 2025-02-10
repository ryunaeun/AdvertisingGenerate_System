<template>
  <!-- 숨김 처리된 입력 필드들 -->
  <div class="form-group save-path-group" style="display: none;">
    <div class="path-input-container">
      <div class="path-input-group">
        <input 
          type="hidden" 
          id="userId" 
          name="userId" 
          v-model="userId"
        >
      </div>
      <div class="path-input-group">
        <input 
          type="hidden" 
          id="subPath" 
          name="subPath" 
          v-model="subPath"
        >
      </div>
    </div>
  </div>
</template>

<script>
import apiClient from "@/api/axiosClient";

export default {
  name: 'PathSettings',
  data() {
    return {
      userId: '',
      subPath: 'videos'
    }
  },
  methods: {
    handlePathChange() {
      const pathData = {
        userId: this.userId.trim(),
        subPath: this.subPath.trim(),
        fullPath: `${this.userId.trim()}\\${this.subPath.trim()}`
      };
      this.$emit('path-change', pathData);
      
      // localStorage에 저장
      localStorage.setItem('userId', this.userId);
      localStorage.setItem('subPath', this.subPath);
    },
    
    async initializePath() {
      try {
        // API에서 사용자 정보 가져오기
        const response = await apiClient.get("/user-info/personal");
        const userData = response.data;
        
        // username을 userId로 설정
        this.userId = userData.username;
        
        // localStorage에서 subPath 불러오기
        const savedSubPath = localStorage.getItem('subPath');
        if (savedSubPath) this.subPath = savedSubPath;
        
        // 초기값 emit
        this.handlePathChange();
      } catch (error) {
        console.error("사용자 정보를 가져오는 중 오류 발생:", error);
        // 에러 발생 시 localStorage의 값을 사용
        const savedUserId = localStorage.getItem('userId');
        if (savedUserId) this.userId = savedUserId;
      }
    }
  },
  // 컴포넌트가 생성될 때 자동으로 경로 설정
  created() {
    this.initializePath();
  },
  // 값이 변경될 때마다 자동으로 경로 업데이트
  watch: {
    userId: {
      handler(newValue) {
        if (newValue) {
          this.handlePathChange();
        }
      }
    },
    subPath: {
      handler(newValue) {
        if (newValue) {
          this.handlePathChange();
        }
      }
    }
  }
}
</script>

<style scoped>
.form-group.save-path-group {
  display: none;
}
</style>