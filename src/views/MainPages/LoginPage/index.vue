<template>
    <div class="login-container">
      <div class="row h-100">
        <!-- 왼쪽 이미지 섹션 -->
        <div class="col-md-6 illustration-section">
          <img src="../../../assets/img/illustrations/login.jpg" alt="Developer Workspace" class="illustration-image">
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
              <div class="form-check form-switch mb-3">
                <input type="checkbox" class="form-check-input" id="rememberMe" v-model="rememberMe" role="switch">
    <label class="form-check-label" for="rememberMe">아이디 저장하기</label>
  </div>
              <button type="submit" class="btn btn-success w-100">로그인</button>
              <div class="divider-container mt-3 mb-3">
  <div class="divider-line"></div>
  <span class="divider-text">또는</span>
  <div class="divider-line"></div>
</div>
              <div class="social-login mt-4">
                <button class="btn btn-outline-secondary w-100 mb-2">
                  <img src="../../../assets/img/logos/login/facebook.png" alt="Facebook"> Facebook으로 시작
                </button>
                <button class="btn btn-outline-secondary w-100 mb-2">
                  <img src="../../../assets/img/logos/login/kakao.png" alt="Kakao"> 카카오로 시작
                </button>
                <button class="btn btn-outline-secondary w-100 mb-2">
                  <img src="../../../assets/img/logos/login/naver.png" alt="Naver"> 네이버로 시작
                </button>
                <button class="btn btn-outline-secondary w-100">
                  <img src="../../../assets/img/logos/login/google.png" alt="Google"> Google로 시작
                </button>
              </div>
              
              <div class="mt-3 text-center">
                <div class="mb-2">
                    <span class="text-muted">아직 회원이 아니신가요?</span> | 
                    <router-link to="/register" class="text-success fw-bold text-decoration-none">회원가입</router-link>
                </div>
                <div>
                    <span class="text-muted">로그인이 실패하나요?</span> | 
                    <a href="#" class="text-success fw-bold text-decoration-none">비밀번호 찾기</a>
                </div>
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
    display: flex;
    align-items: center;
    justify-content: left;
  }
  
  .illustration-image {
    max-width: 100%;
    height: 100vh;
  }
  
  .login-form-section {
    display: flex;
    align-items: center;
    justify-content: center;
    padding-right: 100px;
    background-color: #f5f5f5;
  }
  
  .login-form-container {
    background-color: white;
    width: 100%;
    max-width: 400px;
    padding: 2rem;
    border-radius: 15px;
    box-shadow: 0 2px 15px rgba(0, 0, 0, 0.1);
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
    font-size: 1.1rem;
    font-weight: 500;
    margin: 0;
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
.mb-3{
    margin-bottom: 10px !important;
}
  
  .mt-3.text-center {
  font-size: 0.8rem;
}

.mt-4 {
    margin-top: 0 !important;
}
.text-success.fw-bold.text-decoration-none {
  font-size: 0.8rem;
}

  .text-success {
  color: #40A681 !important;
}

.form-switch .form-check-input {
  width: 2.5em;
  cursor: pointer;
}

.form-check-input:checked {
  background-color: #40A681 !important;
  border-color: #40A681 !important;
}

.form-check-label {
  cursor: pointer;
  color: #666;
  font-size: 0.9rem;
}

.divider-container {
  display: flex;
  align-items: center;
  text-align: center;
}

.divider-line {
  flex: 1;
  border-bottom: 1px solid #e0e0e0;
}

.divider-text {
  color: #666;
  padding: 0 10px;
  font-size: 0.9rem;
}
  </style>
  