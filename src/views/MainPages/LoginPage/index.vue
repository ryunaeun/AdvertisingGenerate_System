<template>
  <div class="login-container">
    <div class="row h-100">
      <!-- 왼쪽 이미지 섹션 -->
      <div class="col-md-6 illustration-section">
        <img src="../../../assets/img/illustrations/login.jpg" alt="Developer Workspace" class="illustration-image" />
      </div>

      <!-- 오른쪽 로그인 폼 섹션 -->
      <div class="col-md-6 login-form-section">
        <div class="login-form-container">
          <router-link  to="/">
            <img src="../../../assets/img/logos/garo-logo.png" alt="AdVi Logo" height="50" class="btn-brand">
          </router-link>
          <form @submit.prevent="login" class="login-form">
            <div class="form-group mb-3">
              <input type="email" class="form-control" placeholder="이메일을 입력하세요" v-model="email" />
            </div>
            <div class="form-group mb-3">
              <input type="password" class="form-control" placeholder="비밀번호를 입력하세요" v-model="password" />
            </div>
            <div class="form-check form-switch mb-3">
              <input type="checkbox" class="form-check-input" id="rememberMe" v-model="rememberMe" />
              <label class="form-check-label" for="rememberMe">아이디 저장하기</label>
            </div>
            <button type="submit" class="btn btn-success w-100">로그인</button>
            <div class="divider-container mt-3 mb-3">
              <div class="divider-line"></div>
              <span class="divider-text">또는</span>
              <div class="divider-line"></div>
            </div>
            <div class="social-login mt-4">
              <button type="button" class="btn btn-outline-secondary w-100 mb-2"
                onclick="alert('소셜 로그인 기능은 현재 준비 중입니다.')">
                <img src="../../../assets/img/logos/login/kakao.png" alt="Kakao" />
                카카오로 시작
              </button>
              <button type="button" class="btn btn-outline-secondary w-100 mb-2"
                onclick="alert('소셜 로그인 기능은 현재 준비 중입니다.')">
                <img src="../../../assets/img/logos/login/naver.png" alt="Naver" />
                네이버로 시작
              </button>
              <button type="button" class="btn btn-outline-secondary w-100" 
                onclick="alert('소셜 로그인 기능은 현재 준비 중입니다.')">
                <img src="../../../assets/img/logos/login/google.png" alt="Google" />
                Google로 시작
              </button>
            </div>

            <div class="mt-3 text-center">
              <div class="mb-2">
                <span class="text-muted">아직 회원이 아니신가요?</span> |
                <router-link to="/register" class="text-success fw-bold text-decoration-none">회원가입</router-link>
              </div>
              <div>
                <span class="text-muted">로그인이 실패하나요?</span> |
                <a @click="showSearchModal" class="text-success fw-bold text-decoration-none">비밀번호 찾기</a>
              </div>
            </div>
          </form>
        </div>
      </div>
    </div>
  </div>
  <!-- 비밀번호찾기 모달 -->
  <div class="modal fade" id="searchModal" tabindex="-1">
    <div class="modal-dialog modal-dialog-centered">
      <div class="modal-content">
        <div class="modal-header">
          <h5 class="modal-title">비밀번호 찾기</h5>
          <button type="button" class="close-button" @click="closeSearchModal">
            <i class="fas fa-times"></i>
          </button>
        </div>
        <div class="modal-body">
          <div class="form-group">
            <label class="form-label">이메일 주소</label>
            <div class="input-wrapper">
              <input type="email" class="form-control" placeholder="hello@example.com" v-model="modalEmail"
                @input="validateModalEmail" />
              <button type="button" class="btn-action" @click="requestResetPassword"
                :disabled="!isEmailValid || isResetCodeSent">
                인증 요청
              </button>
              <span class="validation-message" :class="{
                error: !isEmailValid && modalEmail,
                success: isEmailValid && modalEmail,
              }">
                {{ emailMessage }}
              </span>
            </div>
          </div>

          <div class="form-group">
            <label class="form-label">인증 번호</label>
            <div class="input-wrapper">
              <input type="text" class="form-control" placeholder="인증번호 6자리를 입력해주세요" v-model="verificationCode"
                maxlength="6" />
              <button type="button" class="btn-action" @click="verifyResetCode">
                본인 인증
              </button>
            </div>
            <span class="resend-link" @click="requestResetPassword">인증번호를 받지 못하셨나요?</span>
          </div>
        </div>
        <div class="modal-footer">
          <button type="button" class="custom-confirm-btn" @click="confirmSearch"
            :disabled="!isVerificationCodeValid">
            인증하기
          </button>
        </div>
      </div>
    </div>
  </div>
  <!-- 비밀번호 재설정 모달 -->
  <div class="modal fade" id="resetModal" tabindex="-1">
    <div class="modal-dialog modal-dialog-centered">
      <div class="modal-content">
        <div class="modal-header">
          <h5 class="modal-title">비밀번호 재설정</h5>
          <button type="button" class="close-button" @click="closeResetModal">
            <i class="fas fa-times"></i>
          </button>
        </div>
        <div class="modal-body">
          <div class="form-group">
            <label class="form-label">비밀번호</label>
            <div class="input-wrapper">
              <input :type="showPassword ? 'text' : 'password'" class="form-control" placeholder="password"
                v-model="modalPassword" @input="validateModalPassword" />
              <i class="material-icons password-toggle" @click="togglePassword">
                {{ showPassword ? "visibility_off" : "visibility" }}
              </i>
            </div>
            <span class="validation-message" :class="{
              error: !isPasswordValid && modalPassword,
              success: isPasswordValid && modalPassword,
            }">
              {{ passwordMessage }}
            </span>
          </div>

          <div class="form-group">
            <label class="form-label">비밀번호 확인</label>
            <div class="input-wrapper">
              <input :type="showConfirmPassword ? 'text' : 'password'" class="form-control" placeholder="password"
                v-model="confirmPassword" @input="validateConfirmPassword" />
              <i class="material-icons password-toggle" @click="toggleConfirmPassword">
                {{ showConfirmPassword ? "visibility_off" : "visibility" }}
              </i>
            </div>
            <span class="validation-message" :class="{
              error: !isConfirmPasswordValid && confirmPassword,
              success: isConfirmPasswordValid && confirmPassword,
            }">
              {{ confirmPasswordMessage }}
            </span>
          </div>
        </div>
        <div class="modal-footer">
          <button type="button" class="btn custom-confirm-btn" @click="resetPassword"
            :disabled="!isPasswordValid || !isConfirmPasswordValid">
            변경하기
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
// import axios from "axios";
import apiClient from "@/api/axiosClient";
export default {
  name: "LoginPage",
  data() {
    return {
      email: "",
      modalEmail: "",
      password: "",
      modalPassword: "",
      confirmPassword: "",
      rememberMe: false,
      isModalOpen: false,
      isSearchModalOpen: false,
      isEmailValid: false,
      emailMessage: "",
      isResetModalOpen: false,
      showPassword: false,
      showConfirmPassword: false,
      isPasswordValid: false,
      isConfirmPasswordValid: false,
      passwordMessage: "",
      confirmPasswordMessage: "",
      isResetCodeSent: false,
      verificationCode: "",
      isVerificationCodeValid: false,
      rememberMe: false,
    };
  },
  mounted() {
    // 페이지 로드 시 저장된 이메일 불러오기
    const savedEmail = localStorage.getItem("savedEmail");
    if (savedEmail) {
      this.email = savedEmail;
      this.rememberMe = true;
    }
  },
  methods: {
    showSearchModal() {
      this.isSearchModalOpen = true;
      const modal = document.getElementById("searchModal");
      modal.classList.add("show");
      modal.style.display = "block";
      this.isModalOpen = true;
      document.body.classList.add("modal-open");
      const backdrop = document.createElement("div");
      backdrop.className = "modal-backdrop fade show";
      document.body.appendChild(backdrop);
    },
    closeSearchModal() {
      this.isSearchModalOpen = false;
      const modal = document.getElementById("searchModal");
      modal.classList.remove("show");
      modal.style.display = "none";
      this.isModalOpen = false;
      document.body.classList.remove("modal-open");
      this.removeBackdrop();
    },
    validateEmail() {
      const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
      this.isEmailValid = emailRegex.test(this.email);
      this.emailMessage = this.isEmailValid
        ? "올바른 이메일 형식입니다."
        : "올바른 이메일 주소를 입력해주세요.";
    },
    validateModalEmail() {
      const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
      this.isEmailValid = emailRegex.test(this.modalEmail);
      this.emailMessage = this.isEmailValid
        ? "올바른 이메일 형식입니다."
        : "올바른 이메일 주소를 입력해주세요.";
    },
    async requestResetPassword() {
      try {
        const response = await apiClient.post(
          "/request-reset-password",
          null,
          {
            params: { email: this.modalEmail },
          }
        );
        alert(response.data);
        this.isResetCodeSent = true;
      } catch (error) {
        if (error.response) {
          alert(error.response.data);
        } else {
          alert("오류가 발생했습니다. 다시 시도해주세요.");
        }
      }
    },
    async verifyResetCode() {
      if (!this.verificationCode) {
        alert("인증번호를 입력해주세요.");
        return;
      }
      try {
        const response = await apiClient.post(
          "/verify-reset-code",
          {
            email: this.modalEmail,
            verificationCode: this.verificationCode,
          }
        );
        alert(response.data);
        if (response.data === "인증 번호 확인이 완료되었습니다.") {
          this.isVerificationCodeValid = true;
        }
      } catch (error) {
        if (error.response) {
          alert(error.response.data);
        } else {
          alert("오류가 발생했습니다. 다시 시도해주세요.");
        }
      }
    },

    confirmSearch() {
      if (this.isVerificationCodeValid) {
        this.showResetModal();
      } else {
        alert("인증번호를 먼저 확인해주세요.");
      }
    },
    async resetPassword() {
      if (!this.modalPassword || !this.confirmPassword) {
        alert("비밀번호를 입력해주세요.");
        return;
      }

      if (!this.isPasswordValid) {
        alert("올바른 비밀번호 형식이 아닙니다.");
        return;
      }

      if (!this.isConfirmPasswordValid) {
        alert("비밀번호가 일치하지 않습니다.");
        return;
      }

      try {
        const response = await apiClient.post(
          "/reset-password",
          {
            email: this.modalEmail,
            newPassword: this.password,
          }
        );
        alert(response.data);
        this.closeResetModal(); // 비밀번호 변경 성공 시 모달 닫기
      } catch (error) {
        if (error.response) {
          alert(error.response.data);
        } else {
          alert("오류가 발생했습니다. 다시 시도해주세요.");
        }
      }
    },
    async resendVerificationCode() {
      try {
        const response = await apiClient.post(
          "/resend-verification-code",
          null,
          {
            params: { email: this.modalEmail },
          }
        );
        alert(response.data);
      } catch (error) {
        if (error.response) {
          alert(error.response.data);
        } else {
          alert("오류가 발생했습니다. 다시 시도해주세요.");
        }
      }
    },
    async verifyResetCode() {
      if (!this.verificationCode) {
        alert("인증번호를 입력해주세요.");
        return;
      }
      try {
        const response = await apiClient.post(
          "/verify-reset-code",
          {
            email: this.modalEmail,
            verificationCode: this.verificationCode,
          }
        );
        alert(response.data);
        if (response.data === "인증 번호 확인이 완료되었습니다.") {
          this.isVerificationCodeValid = true;
        }
      } catch (error) {
        if (error.response) {
          alert(error.response.data);
        } else {
          alert("오류가 발생했습니다. 다시 시도해주세요.");
        }
      }
    },
    showResetModal() {
      this.closeSearchModal();
      this.isResetModalOpen = true;
      const modal = document.getElementById("resetModal");
      modal.classList.add("show");
      modal.style.display = "block";
      this.isModalOpen = true;
      document.body.classList.add("modal-open");
      const existingBackdrop = document.querySelector(".modal-backdrop");
      if (existingBackdrop) {
        existingBackdrop.remove();
      }

      // 새 백드롭을 추가합니다.
      const backdrop = document.createElement("div");
      backdrop.className = "modal-backdrop fade show";
      document.body.appendChild(backdrop);
    },
    closeResetModal() {
      this.isResetModalOpen = false;
      const modal = document.getElementById("resetModal");
      modal.classList.remove("show");
      modal.style.display = "none";
      this.isModalOpen = false;
      document.body.classList.remove("modal-open");
      this.removeBackdrop();
    },
    removeBackdrop() {
      const backdrop = document.querySelector(".modal-backdrop");
      if (backdrop) {
        backdrop.remove();
      }
    },
    validatePassword() {
      const passwordRegex =
        /^(?=.*[a-z])(?=.*[A-Z])(?=.*[!@#$%^&*])[A-Za-z\d!@#$%^&*]{8,}$/;
      this.isPasswordValid = passwordRegex.test(this.password);
      this.passwordMessage = this.isPasswordValid
        ? "올바른 비밀번호 형식입니다."
        : "비밀번호는 8자 이상, 대문자, 소문자, 특수문자를 포함해야 합니다.";
    },
    validateModalPassword() {
      const passwordRegex =
        /^(?=.*[a-z])(?=.*[A-Z])(?=.*[!@#$%^&*])[A-Za-z\d!@#$%^&*]{8,}$/;
      this.isPasswordValid = passwordRegex.test(this.modalPassword);
      this.passwordMessage = this.isPasswordValid
        ? "올바른 비밀번호 형식입니다."
        : "비밀번호는 8자 이상, 대문자, 소문자, 특수문자를 포함해야 합니다.";
    },
    validateConfirmPassword() {
      this.isConfirmPasswordValid = this.modalPassword === this.confirmPassword;
      this.confirmPasswordMessage = this.isConfirmPasswordValid
        ? "비밀번호가 일치합니다."
        : "비밀번호가 일치하지 않습니다.";
    },
    togglePassword() {
      this.showPassword = !this.showPassword;
    },
    toggleConfirmPassword() {
      this.showConfirmPassword = !this.showConfirmPassword;
    },
    async login() {
      try {
        const response = await apiClient.post(
          "/login",
          {
            email: this.email,
            password: this.password,
          }
        );

        if (this.rememberMe) {
          localStorage.setItem("savedEmail", this.email);
        } else {
          localStorage.removeItem("savedEmail");
        }

        console.log("로그인 성공:", response.data);

        // Refresh Token과 Access Token 저장
        sessionStorage.setItem("accessToken", response.data.accessToken);
        localStorage.setItem("refreshToken", response.data.refreshToken);

        // Refresh Token 저장 확인
        const storedRefreshToken = localStorage.getItem("refreshToken");
        if (!storedRefreshToken) {
          console.error("Refresh Token 저장 실패");
          throw new Error("Refresh Token 저장에 실패했습니다.");
        }

        // 권한에 따라 페이지 이동
        const payload = JSON.parse(
          atob(response.data.accessToken.split(".")[1])
        );
        const userRole = payload.role;

        if (userRole === "ROLE_ADMIN") {
          this.$router.push("/admin");
        } else {
          this.$router.push("/home");
        }
      } catch (error) {
        console.error("로그인 실패:", error);
        alert("로그인에 실패했습니다. 다시 시도해주세요.");
      }
    },
    async resendVerificationCode() {
      try {
        const response = await apiClient.post(
          "/resend-verification-code",
          null,
          {
            params: { email: this.email },
          }
        );
        alert(response.data);
      } catch (error) {
        if (error.response) {
          alert(error.response.data);
        } else {
          alert("오류가 발생했습니다. 다시 시도해주세요.");
        }
      }
    },
  },
  watch: {
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
    modalPassword() {
      this.validateModalPassword();
      if (this.confirmPassword) {
        this.validateConfirmPassword();
      }
    },
    confirmPassword() {
      this.validateConfirmPassword();
    },
  },
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

.btn-brand {
  margin-bottom: 2rem;
  display: block;
  margin-left: auto;
  margin-right: auto;
}

.form-control {
  padding: 0.75rem;
  border-radius: 8px;
  border: 1px solid #e0e0e0;
}

.btn-success {
  background-color: #40a681;
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

.mb-3 {
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
  color: #40a681 !important;
  cursor: pointer;
}

.form-switch .form-check-input {
  width: 2.5em;
  cursor: pointer;
  margin-top: 5px;
}

.form-check-input:checked {
  background-color: #40a681 !important;
  border-color: #40a681 !important;
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

.form-group {
  margin-bottom: 1rem;
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
  display: flex;
  align-items: center;
}

.form-control {
  flex: 1; /* 입력창이 가능한 공간을 차지하도록 설정 */
  border: none !important;
  border-bottom: 1px solid #e0e0e0 !important; /* 하단 테두리만 표시 */
  padding: 0.5rem 0;
  background: transparent;
  font-size: 0.875rem;
  border-radius: 0;
  margin-bottom : 0px !important; 
}

.form-control:focus {
  outline: none;
  border-bottom-color: #40a681;
}

.form-control::placeholder {
  color: #999;
}

.btn-action {
  margin-left: 10px; /* 입력창과 버튼 사이 간격 */
  background: none; /* 배경 제거 */
  border: 1px solid #40a681; /* 테두리만 표시 */
  color: #40a681; /* 텍스트 색상 */
  padding: 0.5rem 1rem;
  border-radius: 4px; /* 버튼 모서리를 둥글게 처리 */
  font-size: 0.875rem;
  cursor: pointer;
}

.btn-action:hover {
  background-color: rgba(64, 166, 129, 0.1); /* 호버 시 약간의 배경색 추가 */
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
  color: #40a681;
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
  background-color: #40a681;
  color: white;
  border: none;
  padding: 0.5rem 1.5rem;
  border-radius: 0.5rem;
}

.custom-confirm-btn:hover {
  background-color: #40a681;
  opacity: 0.9;
}

.clickable {
  cursor: pointer;
  color: #40a681;
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

.modal-header {
  border-bottom: 0;
}

.modal-footer {
  border-top: 0;
  justify-content: center;
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
  color: #40a681;
}

.close-button:focus {
  outline: none;
}

.modal-content {
  padding: 1rem 1.5rem;
}
</style>
