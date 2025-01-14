<template>
    <div class="login-container">
      <div class="row h-100">
        <!-- 왼쪽 이미지 섹션 -->
        <div class="col-md-6 illustration-section">
          <img src="../../../assets/img/illustrations/login.jpg" alt="Developer Workspace" class="illustration-image">
        </div>
        
        <!-- 오른쪽 회원가입 폼 섹션 -->
        <div class="col-md-6 login-form-section">
          <div class="login-form-container">
            <h2 class="welcome-text">회원 가입</h2>
            <form @submit.prevent="handleRegister" class="login-form">
              <div class="form-group mb-3">
                <input 
                  type="email" 
                  class="form-control" 
                  placeholder="이메일 주소"
                  v-model="email"
                >
              </div>
              <div class="form-group mb-3">
                <input 
                  type="password" 
                  class="form-control" 
                  placeholder="비밀번호"
                  v-model="password"
                >
              </div>
              <div class="form-group mb-3">
                <input 
                  type="password" 
                  class="form-control" 
                  placeholder="비밀번호 확인"
                  v-model="passwordConfirm"
                >
              </div>
              <div class="form-group mb-3">
                <input 
                  type="text" 
                  class="form-control" 
                  placeholder="회사명"
                  v-model="companyName"
                >
              </div>
              <div class="form-group mb-3">
                <input 
                  type="text" 
                  class="form-control" 
                  placeholder="사업자등록번호"
                  v-model="businessNumber"
                >
              </div>
              <div class="form-group mb-3 d-flex align-items-center register">
                <input 
                  type="text" 
                  class="form-control me-2" 
                  :placeholder="selectedFileName || '사업자 등록증 파일 업로드'" 
                  readonly
                >
                <input 
                  type="file" 
                  ref="fileInput" 
                  @change="handleFileUpload" 
                  accept=".pdf,.jpg,.jpeg,.png" 
                  style="display: none"
                >
                <button 
                  type="button" 
                  class="btn btn-outline-secondary px-3" 
                  @click="$refs.fileInput.click()"
                >
                  <span>+ 파일 업로드</span>
                </button>
              </div>
              <button type="submit" class="btn btn-success w-100">가입하기</button>
              
              <div class="mt-3 text-center small">
                <p class="text-muted">
                  회원가입을 진행하면 ooo의<br>
                  <span @click="showTermsModal" class="fw-bold clickable">이용약관</span> 및 
                  <span @click="showPrivacyModal" class="fw-bold clickable">개인정보처리방침</span>에<br>
                  동의하게 됩니다.
                </p>
              </div>
            </form>
          </div>
        </div>
      </div>
      <div class="modal fade" id="termsModal" tabindex="-1">
    <div class="modal-dialog modal-dialog-centered">
      <div class="modal-content">
        <div class="modal-header">
          <h5 class="modal-title">이용약관</h5>
          <button type="button" class="btn-close" @click="closeTermsModal"></button>
        </div>
        <div class="modal-body">
          <h6>제1조 (목적)</h6>
          <p>본 약관은 ooo(이하 "회사")가 제공하는 서비스의 이용조건 및 절차, 회사와 회원 간의 권리, 의무 및 책임사항을 규정함을 목적으로 합니다.</p>
          
          <h6>제2조 (용어의 정의)</h6>
          <p>본 약관에서 사용하는 용어의 정의는 다음과 같습니다...</p>
        </div>
        <div class="modal-footer">
        <button type="button" class="btn custom-confirm-btn" @click="closeTermsModal">확인</button>
        </div>
      </div>
    </div>
  </div>

  <!-- 개인정보처리방침 모달 -->
  <div class="modal fade" id="privacyModal" tabindex="-1">
    <div class="modal-dialog modal-dialog-centered">
      <div class="modal-content">
        <div class="modal-header">
          <h5 class="modal-title">개인정보처리방침</h5>
          <button type="button" class="btn-close" @click="closePrivacyModal"></button>
        </div>
        <div class="modal-body">
          <h6>1. 개인정보의 수집 및 이용 목적</h6>
          <p>회사는 다음의 목적을 위하여 개인정보를 처리합니다. 처리하고 있는 개인정보는 다음의 목적 이외의 용도로는 이용되지 않으며...</p>
          
          <h6>2. 개인정보의 처리 및 보유기간</h6>
          <p>회사는 법령에 따른 개인정보 보유·이용기간 또는 정보주체로부터 개인정보를 수집 시에 동의받은 개인정보 보유·이용기간 내에서 개인정보를 처리·보유합니다.</p>
        </div>
        <div class="modal-footer">
            <button type="button" class="btn custom-confirm-btn" @click="closePrivacyModal">확인</button>
        </div>
      </div>
    </div>
  </div>
    </div>
  </template>
  
  
  <script>
    export default {
  name: 'RegisterPage',
  data() {
    return {
      email: '',
      password: '',
      passwordConfirm: '',
      companyName: '',
      businessNumber: '',
      businessFile: null,
      selectedFile: null,
      selectedFileName: '',
      isModalOpen: false,
    isTermsModalOpen: false,
    isPrivacyModalOpen: false
    }
  },
  methods: {
    handleFileUpload(event) {
      const file = event.target.files[0];
      if (file) {
        this.selectedFile = file;
        this.selectedFileName = file.name;
        
        // 파일 크기 체크 (예: 10MB 제한)
        const maxSize = 10 * 1024 * 1024; // 10MB in bytes
        if (file.size > maxSize) {
          alert('파일 크기는 10MB를 초과할 수 없습니다.');
          this.resetFileInput();
          return;
        }
        
        // 파일 형식 체크
        const allowedTypes = ['application/pdf', 'image/jpeg', 'image/png'];
        if (!allowedTypes.includes(file.type)) {
          alert('PDF, JPG, PNG 파일만 업로드 가능합니다.');
          this.resetFileInput();
          return;
        }
      }
    },
    showTermsModal() {
      this.isTermsModalOpen = true;
      const modal = document.getElementById('termsModal')
      modal.classList.add('show')
      modal.style.display = 'block'
      this.isModalOpen = true
      document.body.classList.add('modal-open')
      const backdrop = document.createElement('div')
      backdrop.className = 'modal-backdrop fade show'
      document.body.appendChild(backdrop)
    },
    closeTermsModal() {
      this.isTermsModalOpen = false;
      const modal = document.getElementById('termsModal')
      modal.classList.remove('show')
      modal.style.display = 'none'
      this.isModalOpen = false
      document.body.classList.remove('modal-open')
      const backdrop = document.querySelector('.modal-backdrop')
      if (backdrop) {
        backdrop.remove()
      }
    },
    showPrivacyModal() {
      this.isPrivacyModalOpen = true;
      const modal = document.getElementById('privacyModal')
      modal.classList.add('show')
      modal.style.display = 'block'
      this.isModalOpen = true
      document.body.classList.add('modal-open')
      const backdrop = document.createElement('div')
      backdrop.className = 'modal-backdrop fade show'
      document.body.appendChild(backdrop)
    },
    closePrivacyModal() {
      this.isPrivacyModalOpen = false;
      const modal = document.getElementById('privacyModal')
      modal.classList.remove('show')
      modal.style.display = 'none'
      this.isModalOpen = false
      document.body.classList.remove('modal-open')
      const backdrop = document.querySelector('.modal-backdrop')
      if (backdrop) {
        backdrop.remove()
      }
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
  
  .mt-3.text-center {
  font-size: 0.8rem;
}
.mb-3 {
    margin-bottom: 10px !important;
}
.register {
    margin-top: -10px;
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

.btn-outline-secondary {
  height: 38px;
  white-space: nowrap;
  font-size: 0.9rem;
  margin-top: 15px;
}

.small {
  font-size: 0.8rem;
  line-height: 1.5;
}

.form-group input::placeholder {
  color: #999;
  font-size: 0.9rem;
}

.clickable {
  cursor: pointer;
text-decoration: underline;
}

.clickable:hover {
  opacity: 0.8;
}

.modal-body {
  max-height: 70vh;
  overflow-y: auto;
}

.modal-body h6 {
  margin-top: 1.5rem;
  margin-bottom: 0.5rem;
  color: #333;
}

.modal-body p {
  color: #666;
  line-height: 1.6;
}

.custom-confirm-btn {
  background-color: #40A681;
  color: white;
  border: none;
  padding: 0.5rem 1.5rem;
  border-radius: 0.5rem;
}

.custom-confirm-btn:hover {
  background-color: #40A681;
  opacity: 0.9;
}

  </style>
  