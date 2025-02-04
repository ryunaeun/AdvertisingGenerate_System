<template>
  <div class="form-group save-path-group">
    <div class="path-input-container">
      <div class="path-input-group">
        <label for="userId">User ID:</label>
        <input 
          type="text" 
          id="userId" 
          name="userId" 
          v-model="userId" 
          placeholder="Enter user ID"
          @change="handlePathChange"
        >
      </div>
      <span class="path-separator">\</span>
      <div class="path-input-group">
        <label for="subPath">Save Path:</label>
        <input 
          type="text" 
          id="subPath" 
          name="subPath" 
          v-model="subPath" 
          placeholder="Enter sub path"
          @change="handlePathChange"
        >
      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: 'PathSettings',
  data() {
    return {
      userId: 'KTaivle',  // 기본값
      subPath: 'examples' // 기본값
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
    
    initializePath() {
      // localStorage에서 저장된 값 불러오기
      const savedUserId = localStorage.getItem('userId');
      const savedSubPath = localStorage.getItem('subPath');
      
      if (savedUserId) this.userId = savedUserId;
      if (savedSubPath) this.subPath = savedSubPath;
      
      // 초기값 emit
      this.handlePathChange();
    }
  },
  mounted() {
    this.initializePath();
  }
}
</script>