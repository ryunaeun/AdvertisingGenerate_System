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
    <form @submit.prevent="register" class="login-form">
      <div class="form-group">
  <label class="form-label">아이디</label>
  <div class="input-wrapper">
    <input 
      type="text" 
      class="form-control" 
      placeholder="아이디" 
      v-model="username"
      @input="validateUsername"
    >
    <button type="button" class="btn-action" @click="checkUsernameDuplicate">중복 확인</button>
  </div>
  <span class="validation-message" :class="{ 'error': !isUsernameValid && username, 'success': isUsernameValid && username }">
    {{ usernameMessage }}
  </span>
</div>

<div class="form-group">
  <label class="form-label">이메일 주소</label>
  <div class="input-wrapper">
    <input 
      type="email" 
      class="form-control" 
      placeholder="hello@creative-tim.com"
      v-model="email"
      @input="validateEmail"
    >
    <button type="button" class="btn-action" @click="requestVerificationCode"
    :disabled="!isEmailValid || isVerificationCodeSent">인증 요청</button>
  </div>
  <span class="validation-message" :class="{ 'error': !isEmailValid && email, 'success': isEmailValid && email }">
    {{ emailMessage }}
  </span>
</div>

<div class="form-group" v-if="isVerificationCodeSent">
  <label class="form-label">인증 번호</label>
  <div class="input-wrapper">
    <input 
      type="text" 
      class="form-control" 
      placeholder="인증번호 6자리를 입력해주세요"
      v-model="verificationCode"
      maxlength="6"
    >
    <button type="button" class="btn-action" @click="verifyCode">본인 인증</button>
  </div>
  <span class="resend-link" @click="requestVerificationCode">인증번호를 받지 못하셨나요?</span>
  <span class="validation-message" :class="{ 'success': isVerificationCodeValid }">
    {{ isVerificationCodeValid ? '인증이 완료되었습니다.' : '' }}
  </span>
</div>

<div class="form-group">
  <label class="form-label">비밀번호</label>
  <div class="input-wrapper">
    <input 
      :type="showPassword ? 'text' : 'password'" 
      class="form-control" 
      placeholder="password"
      v-model="password"
      @input="validatePassword"
    >
    <i 
      class="material-icons password-toggle"
      @click="togglePassword"
    >
      {{ showPassword ? 'visibility_off' : 'visibility' }}
    </i>
  </div>
  <span class="validation-message" :class="{ 'error': !isPasswordValid && password, 'success': isPasswordValid && password }">
    {{ passwordMessage }}
  </span>
</div>

<div class="form-group">
  <label class="form-label">비밀번호 확인</label>
  <div class="input-wrapper">
    <input 
      :type="showConfirmPassword ? 'text' : 'password'" 
      class="form-control" 
      placeholder="password"
      v-model="confirmPassword"
      @input="validateConfirmPassword"
    >
    <i 
      class="material-icons password-toggle"
      @click="toggleConfirmPassword"
    >
      {{ showConfirmPassword ? 'visibility_off' : 'visibility' }}
    </i>
  </div>
  <span class="validation-message" :class="{ 'error': !isConfirmPasswordValid && confirmPassword, 'success': isConfirmPasswordValid && confirmPassword }">
    {{ confirmPasswordMessage }}
  </span>
</div>

      <div class="form-group">
        <label class="form-label">회사명</label>
        <div class="input-wrapper">
          <input type="text" class="form-control" placeholder="Company Name" v-model="companyName">
        </div>
      </div>

      <div class="form-group">
        <label class="form-label">사업자등록번호</label>
        <div class="input-wrapper">
          <input type="text" class="form-control" placeholder="10자리를 입력해주세요" v-model="businessNumber">
        </div>
      </div>

      <div class="form-group">
  <label class="form-label">사업자 등록증 파일 업로드</label>
  <div class="d-flex align-items-center file-upload">
    <input 
      type="text" 
      class="form-control me-2" 
      :placeholder="selectedFileName || '파일을 선택해주세요'" 
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
      class="btn-outline-secondary" 
      @click="$refs.fileInput.click()"
    >
      파일 업로드
    </button>
  </div>
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
    <!-- 이용약관 모달 -->
    <div class="modal fade" id="termsModal" tabindex="-1">
      <div class="modal-dialog modal-dialog-centered">
        <div class="modal-content">
          <div class="modal-header">
            <h5 class="modal-title">이용약관</h5>
            <button type="button" class="close-button" @click="closeTermsModal">
              <i class="fas fa-times"></i>
            </button>
          </div>
          <div class="modal-body">
            <h6>제1조 (목적)</h6>
            <p>본 약관은 ooo(이하 "회사")가 제공하는 서비스의 이용조건 및 절차, 회사와 회원 간의 권리, 의무 및 책임사항을 규정함을 목적으로 합니다.</p>
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
            <button type="button" class="close-button" @click="closePrivacyModal">
              <i class="fas fa-times"></i>
            </button>
          </div>
          <div class="modal-body">
            <h6>1. 개인정보의 수집 및 이용 목적</h6>
            <p>회사는 다음의 목적을 위하여 개인정보를 처리합니다. 처리하고 있는 개인정보는 다음의 목적 이외의 용도로는 이용되지 않으며...</p>
          </div>
          <div class="modal-footer">
            <button type="button" class="btn custom-confirm-btn" @click="closePrivacyModal">확인</button>
          </div>
        </div>
      </div>
    </div>
  </div>
  </div>
</template>

<script>
import apiClient from "@/api/axiosClient";

export default {
  name: "RegisterPage",
  data() {
    return {
      username: "",
      email: "",
      password: "",
      confirmPassword: "",
      companyName: "",
      businessNumber: "",
      businessFile: null,
      selectedFile: null,
      selectedFileName: "",
      isModalOpen: false,
      isTermsModalOpen: false,
      isPrivacyModalOpen: false,
      isUsernameValid: false,
      isEmailValid: false,
      isPasswordValid: false,
      isConfirmPasswordValid: false,
      isBusinessNumberValid: false,
      usernameMessage: "",
      emailMessage: "",
      passwordMessage: "",
      confirmPasswordMessage: "",
      businessNumberMessage: "",
      verificationCode: "",
      showPassword: false,
      showConfirmPassword: false,
      isVerificationCodeSent: false,
      isVerificationCodeValid: false,
    };
  },
  methods: {
    validateUsername() {
      const usernameRegex = /^[a-zA-Z]{3,20}$/;
      this.isUsernameValid = usernameRegex.test(this.username);
      this.usernameMessage = this.isUsernameValid ? 
        "올바른 아이디 형식입니다." : 
        "아이디는 3~20자의 영문자만 사용 가능합니다.";
    },
    validateEmail() {
      const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
      this.isEmailValid = emailRegex.test(this.email);
      this.emailMessage = this.isEmailValid ? 
        "올바른 이메일 형식입니다." : 
        "올바른 이메일 주소를 입력해주세요.";
    },
    validatePassword() {
      const passwordRegex = /^(?=.*[a-z])(?=.*[A-Z])(?=.*[!@#$%^&*])[A-Za-z\d!@#$%^&*]{8,}$/;
      this.isPasswordValid = passwordRegex.test(this.password);
      this.passwordMessage = this.isPasswordValid ? 
        "올바른 비밀번호 형식입니다." : 
        "비밀번호는 8자 이상, 대문자, 소문자, 특수문자를 포함해야 합니다.";
    },
    validateConfirmPassword() {
      this.isConfirmPasswordValid = this.password === this.confirmPassword;
      this.confirmPasswordMessage = this.isConfirmPasswordValid ?
        "비밀번호가 일치합니다." :
        "비밀번호가 일치하지 않습니다.";
    },
    validateBusinessNumber() {
      const businessNumberRegex = /^\d{10}$/;
      this.isBusinessNumberValid = businessNumberRegex.test(this.businessNumber);
      this.businessNumberMessage = this.isBusinessNumberValid ?
        "올바른 사업자등록번호 형식입니다." :
        "사업자등록번호는 10자리 숫자여야 합니다.";
    },
    togglePassword() {
      this.showPassword = !this.showPassword;
    },
    toggleConfirmPassword() {
      this.showConfirmPassword = !this.showConfirmPassword;
    },
    register() {
      if (!this.isVerificationCodeValid) {
        alert("이메일 인증을 완료해주세요.");
        return;
      }

      const formData = new FormData();
      formData.append('email', this.email);
      formData.append('password', this.password);
      formData.append('username', this.username);
      if (this.companyName){
        formData.append('companyName', this.companyName);
      }
      if (this.businessNumber) {
        formData.append('businessNumber', this.businessNumber);
      }
      formData.append('verificationCode', this.verificationCode);
      if (this.selectedFile) {
        formData.append('businessFile', this.selectedFile);
      }

      console.log("🚀 FormData Entries:", [...formData.entries()]);

      apiClient.post("/register-full", formData, {
        headers: {
          'Content-Type': 'multipart/form-data'
        }
      })
      .then((response) => {
        alert(response.data);
        this.$router.push("/login");
      })
      .catch((error) => {
        console.error("회원가입 실패:", error);
        alert(error.response?.data || "회원가입 중 오류가 발생했습니다.");
      });
    },
    handleFileUpload(event) {
      const file = event.target.files[0];
      if (file) {
        this.selectedFile = file;
        this.selectedFileName = file.name;

        const maxSize = 10 * 1024 * 1024;
        if (file.size > maxSize) {
          alert("파일 크기는 10MB를 초과할 수 없습니다.");
          this.resetFileInput();
          return;
        }

        const allowedTypes = ["application/pdf", "image/jpeg", "image/png"];
        if (!allowedTypes.includes(file.type)) {
          alert("PDF, JPEG, JPG, PNG 파일만 업로드 가능합니다.");
          this.resetFileInput();
          return;
        }
      }
    },
    resetFileInput() {
      this.selectedFile = null;
      this.selectedFileName = "";
      this.$refs.fileInput.value = "";
    },
    showTermsModal() {
      this.isTermsModalOpen = true;
      const modal = document.getElementById("termsModal");
      modal.classList.add("show");
      modal.style.display = "block";
      this.isModalOpen = true;
      document.body.classList.add("modal-open");
      const backdrop = document.createElement("div");
      backdrop.className = "modal-backdrop fade show";
      document.body.appendChild(backdrop);
    },
    closeTermsModal() {
      this.isTermsModalOpen = false;
      const modal = document.getElementById("termsModal");
      modal.classList.remove("show");
      modal.style.display = "none";
      this.isModalOpen = false;
      document.body.classList.remove("modal-open");
      const backdrop = document.querySelector(".modal-backdrop");
      if (backdrop) {
        backdrop.remove();
      }
    },
    showPrivacyModal() {
      this.isPrivacyModalOpen = true;
      const modal = document.getElementById("privacyModal");
      modal.classList.add("show");
      modal.style.display = "block";
      this.isModalOpen = true;
      document.body.classList.add("modal-open");
      const backdrop = document.createElement("div");
      backdrop.className = "modal-backdrop fade show";
      document.body.appendChild(backdrop);
    },
    closePrivacyModal() {
      this.isPrivacyModalOpen = false;
      const modal = document.getElementById("privacyModal");
      modal.classList.remove("show");
      modal.style.display = "none";
      this.isModalOpen = false;
      document.body.classList.remove("modal-open");
      const backdrop = document.querySelector(".modal-backdrop");
      if (backdrop) {
        backdrop.remove();
      }
    },
    checkUsernameDuplicate() {
      if (!this.username) {
        this.usernameMessage = "아이디를 입력해주세요.";
        return;
      }
      
      apiClient.get(`/check-username?username=${this.username}`)
        .then(response => {
          if (response.data.isDuplicate) {
            this.isUsernameValid = false;
            this.usernameMessage = "이미 사용 중인 아이디입니다.";
          } else {
            this.isUsernameValid = true;
            this.usernameMessage = "사용 가능한 아이디입니다.";
          }
        })
        .catch(error => {
          console.error("아이디 중복 확인 실패:", error);
          this.usernameMessage = "아이디 중복 확인 중 오류가 발생했습니다.";
        });
    },
    requestVerificationCode() {
      if (!this.email || !this.isEmailValid) {
        alert("유효한 이메일 주소를 입력해주세요.");
        return;
      }
      apiClient.post("/send-verification-code", {
        email: this.email,
        username: this.username
      })
      .then(response => {
        alert(response.data);
        this.isVerificationCodeSent = true;
      })
      .catch(error => {
        if (error.response) {
          alert(error.response.data);
        } else {
          alert("인증 코드 요청 중 오류가 발생했습니다.");
        }
        console.error("인증 코드 요청 실패:", error);
      });
    },
    verifyCode() {
      if (!this.verificationCode) {
        alert("인증 코드를 입력해주세요.");
        return;
      }

        apiClient.post("/register", {
          email: this.email,
          verificationCode: this.verificationCode
        })
        .then(response => {
          this.isVerificationCodeValid = true;

          // ✅ 응답 메시지만 출력
          if (response.data && response.data.message) {
            alert(response.data.message);
          } else {
            alert("인증이 완료되었습니다."); // 백업 메시지
          }
        })
        .catch(error => {
          if (error.response) {
            alert(error.response.data || "인증 코드 확인 중 서버 오류가 발생했습니다.");
          } else if (error.request) {
            alert("서버에 연결할 수 없습니다. 네트워크 연결을 확인해주세요.");
          } else {
            alert("인증 코드 확인 요청 중 오류가 발생했습니다.");
          }
          console.error("인증 코드 확인 실패:", error);
        });
    }
  },
  watch: {
    username() {
      this.validateUsername();
    },
    email() {
      this.validateEmail();
    },
    password() {
      this.validatePassword();
      if (this.confirmPassword) {
        this.validateConfirmPassword();
      }
    },
    confirmPassword() {
      this.validateConfirmPassword();
    },
    businessNumber() {
      this.validateBusinessNumber();
    }
  }
};
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
  padding-right: 80px;
  background-color: #f5f5f5;
}

.login-form-container {
  background-color: white;
  width: 100%;
  max-width: 500px;
  padding: 2rem;
  border-radius: 15px;
  box-shadow: 0 2px 15px rgba(0, 0, 0, 0.1);
  max-height: 90vh; /* 뷰포트 높이의 90% */
  overflow-y: auto; /* 세로 스크롤 추가 */
}

/* 스크롤바 스타일링 */
.login-form-container::-webkit-scrollbar {
  width: 6px;
}

.login-form-container::-webkit-scrollbar-track {
  background: #f1f1f1;
  border-radius: 3px;
}

.login-form-container::-webkit-scrollbar-thumb {
  background: #40A681;
  border-radius: 3px;
}

.login-form-container::-webkit-scrollbar-thumb:hover {
  background: #358f6f;
}

.welcome-text {
  color: #40A681;
  font-size: 24px;
  font-weight: 600;
  margin-bottom: 2rem;
  text-align: center;
}

.form-group {
  margin-bottom: 1.0rem;
  position: relative;
}

.form-label {
  display: block;
  font-size: 0.875rem;
  color: #333;
  margin-bottom: 0rem;
}

.input-wrapper {
  position: relative;
  width: 100%;
}

.form-control {
  width: 100%;
  border: none;
  border-bottom: 1px solid #e0e0e0;
  padding: 0.5rem 0;
  background: transparent;
  font-size: 0.875rem;
  border-radius: 0;
}

.form-control:focus {
  outline: none;
  border-bottom-color: #40A681;
}

.form-control::placeholder {
  color: #999;
  font-size: 0.875rem;
}

.btn-action {
  position: absolute;
  right: 0;
  top: -5px;
  background: none;
  border: 1px solid #40A681;
  color: #40A681;
  padding: 0.25rem 0.75rem;
  border-radius: 4px;
  font-size: 0.875rem;
  cursor: pointer;
}

.verification-text {
  color: #40A681;
  font-size: 0.75rem;
  margin-top: 0.25rem;
  cursor: pointer;
  text-decoration: underline;
}

.btn {
  padding: 0.375rem 0.75rem;
  font-size: 0.875rem;
}

.btn-outline-success {
  color: #40A681;
  border-color: #40A681;
}

.btn-outline-secondary {
  height: 38px;
  white-space: nowrap;
  font-size: 0.9rem;
  border-radius: 8px;
  padding: 0.75rem;
  display: flex;
  align-items: center;
  justify-content: center;
}

.btn-success {
  width: 100%;
  background-color: #40A681;
  border: none;
  padding: 0.75rem;
  border-radius: 8px;
  color: white;
  font-size: 1rem;
  margin-top: 1.5rem;
}

.text-success {
  color: #40A681 !important;
}

.modal-body {
  max-height: 70vh;
  overflow-y: auto;
  margin-left: 0.5rem;
  margin-right: 0.5rem;
}

.modal-body h6 {
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

.clickable {
  cursor: pointer;
  color: #40A681;
  text-decoration: underline;
}

.clickable:hover {
  opacity: 0.8;
}

.password-toggle {
  position: absolute;
  right: 0;
  top: 50%;
  transform: translateY(-50%);
  cursor: pointer;
  color: #999;
}

.validation-message {
  display: block;
  font-size: 0.75rem;
  margin-top: 0.25rem;
}

.validation-message.error {
  color: #dc3545;
}

.validation-message.success {
  color: #40A681;
}


.resend-link {
  display: block;
  color: #dc3545;
  font-size: 0.75rem;
  margin-top: 0.5rem;
  cursor: pointer;
  text-decoration: underline;
}

.resend-link:hover {
  opacity: 0.8;
}



.file-upload {
  width: 100%;
}

.btn-outline-secondary {
  border: 1px solid #666;
  color: #666;
  padding: 0.375rem 0.75rem;
  font-size: 0.875rem;
  border-radius: 4px;
  white-space: nowrap;
  height: 38px;
}

.form-control {
  border-radius: 0;
  margin-right: 8px;
}

.form-label {
  display: block;
  margin-bottom: 0.5rem;
}

.password-toggle {
  position: absolute;
  right: 0;
  top: 50%;
  transform: translateY(-50%);
  cursor: pointer;
  color: #999;
  font-size: 20px;
  padding: 4px;
}

.password-toggle:hover {
  color: #666;
}

.close-button {
  background-color: transparent;
  border: none;
  color: #344767;
  font-size: 1.25rem;
  padding: 0.5rem;
  cursor: pointer;
  transition: color 0.15s ease-in-out;
}

.close-button:hover {
  color: #40A681;
}

.close-button:focus {
  outline: none;
}

.form-control[readonly] {
    border-radius: 6px;
    padding-left: 10px;
}
</style>

  