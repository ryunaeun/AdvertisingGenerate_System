<template>
    <div class="login-container">
      <div class="row h-100">
        <!-- 왼쪽 이미지 섹션 -->
        <div class="col-md-6 illustration-section">
          <img src="@/assets/developer-illustration.jpg" alt="Developer Workspace" class="illustration-image">
        </div>
        
        <!-- 오른쪽 로그인 폼 섹션 -->
        <div class="col-md-6 login-form-section">
          <div class="login-form-container">
            <h2 class="welcome-text">Welcome</h2>
            <form @submit.prevent="handleLogin" class="login-form">
              <div class="form-group mb-3">
                <input 
                  type="email" 
                  class="form-control" 
                  placeholder="이메일을 입력하세요"
                  v-model="email"
                >
              </div>
              <div class="form-group mb-3">
                <input 
                  type="password" 
                  class="form-control" 
                  placeholder="비밀번호를 입력하세요"
                  v-model="password"
                >
              </div>
              <div class="form-check mb-3">
                <input type="checkbox" class="form-check-input" id="rememberMe" v-model="rememberMe">
                <label class="form-check-label" for="rememberMe">아이디 저장하기</label>
              </div>
              <button type="submit" class="btn btn-success w-100">로그인</button>
              
              <div class="social-login mt-4">
                <button class="btn btn-outline-secondary w-100 mb-2">
                  <img src="@/assets/facebook-icon.png" alt="Facebook"> Facebook으로 시작
                </button>
                <button class="btn btn-outline-secondary w-100 mb-2">
                  <img src="@/assets/kakao-icon.png" alt="Kakao"> 카카오로 시작
                </button>
                <button class="btn btn-outline-secondary w-100 mb-2">
                  <img src="@/assets/naver-icon.png" alt="Naver"> 네이버로 시작
                </button>
                <button class="btn btn-outline-secondary w-100">
                  <img src="@/assets/google-icon.png" alt="Google"> Google로 시작
                </button>
              </div>
              
              <div class="mt-3 text-center">
                <a href="#" class="text-muted">아직 회원이 아니신가요?</a> | 
                <a href="#" class="text-muted">비밀번호 찾기</a>
              </div>
            </form>
          </div>
        </div>
      </div>
    </div>
  </template>
  
  <script>
  export default {
    name: 'LoginPage',
    data() {
      return {
        email: '',
        password: '',
        rememberMe: false
      }
    },
    methods: {
      handleLogin() {
        // 로그인 로직 구현
        console.log('Login attempt:', this.email, this.password)
      }
    }
  }
  </script>
  
  <style scoped>
  .login-container {
    height: 100vh;
    overflow: hidden;
  }
  
  .illustration-section {
    background-color: #f5f5f5;
    padding: 2rem;
    display: flex;
    align-items: center;
    justify-content: center;
  }
  
  .illustration-image {
    max-width: 100%;
    height: auto;
  }
  
  .login-form-section {
    display: flex;
    align-items: center;
    justify-content: center;
    padding: 2rem;
  }
  
  .login-form-container {
    width: 100%;
    max-width: 400px;
    padding: 2rem;
  }
  
  .welcome-text {
    color: #40A681;
    font-size: 24px;
    font-weight: 600;
    margin-bottom: 2rem;
    text-align: center;
  }
  
  .form-control {
    padding: 0.75rem;
    border-radius: 8px;
    border: 1px solid #e0e0e0;
  }
  
  .btn-success {
    background-color: #40A681;
    border: none;
    padding: 0.75rem;
    border-radius: 8px;
  }
  
  .social-login img {
    width: 20px;
    height: 20px;
    margin-right: 8px;
  }
  
  .btn-outline-secondary {
    border-radius: 8px;
    padding: 0.75rem;
    display: flex;
    align-items: center;
    justify-content: center;
  }
  
  a {
    text-decoration: none;
    font-size: 0.9rem;
  }
  </style>
  