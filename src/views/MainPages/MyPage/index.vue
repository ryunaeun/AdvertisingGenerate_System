<template>
    <div class="mypage">
      <!-- Header 추가 -->
      <Header />
      
      <!-- 상단 제목 -->
      <div class="page-title">
        <h1>내 정보</h1>
      </div>
      
      <!-- 탭 메뉴 -->
      <nav class="tabs">
        <button 
          class="tab-item" 
          :class="{ active: activeTab === 'basic' }" 
          @click="activeTab = 'basic'">
          기본 정보
        </button>
        <button 
          class="tab-item" 
          :class="{ active: activeTab === 'payment' }" 
          @click="activeTab = 'payment'">
          결제 정보
        </button>
      </nav>
      
      <!-- 선 추가 -->
      <hr class="divider" />
      
      <!-- 콘텐츠 -->
      <section v-if="activeTab === 'basic'" class="tab-content basic-info">
        <div class="info-card">
          <!-- 사용자 정보 섹션 -->
          <div class="info-text">
            <div class="nickname-section">
              <h2 class="nickname">{{ nickname }}</h2>
              <span class="material-icons-round edit-icon" @click="openNicknameEditModal">
                edit
              </span>
            </div>
            <p>
              아이디: kk6991428@naver.com
              <button @click="openPasswordModal" class="password-change-button">
                비밀번호 변경
              </button>
            </p>
          </div>

          <!-- 닉네임 수정 모달 -->
          <div v-if="isNicknameEditModalOpen" class="nickname-modal-backdrop">
            <div class="nickname-modal">
              <h3>닉네임 수정</h3>
              <p>현재 닉네임: <strong>{{ nickname }}</strong></p>
              <div class="input-container">
                <input 
                  type="text" 
                  v-model="tempNickname" 
                  placeholder="새 닉네임 입력" 
                />
              </div>
              <div class="nickname-modal-buttons">
                <button @click="confirmNicknameEdit">수정</button>
                <button @click="closeNicknameEditModal">취소</button>
              </div>
            </div>
          </div>
          <!-- 비밀번호 변경 모달 -->
          <div v-if="isPasswordModalOpen" class="modal-overlay-password">
            <div class="modal-content-password">
              <h2>비밀번호 변경</h2>
              <!-- 새 비밀번호 입력 -->
              <div class="password-field">
                <label for="newPassword">새 비밀번호</label>
                <div class="password-input-container">
                  <input
                    type="password"
                    id="newPassword"
                    v-model="newPassword"
                    placeholder="새로운 비밀번호를 입력해주세요"
                  />
                  <span @click="toggleNewPasswordVisibility">
                    <i :class="showNewPassword ? 'password-eye-open-icon' : 'password-eye-closed-icon'"></i>
                  </span>
                </div>
              </div>

              <!-- 비밀번호 확인 -->
              <div class="password-field">
                <label for="confirmPassword">비밀번호 확인</label>
                <div class="password-input-container">
                  <input
                    type="password"
                    id="confirmPassword"
                    v-model="confirmPassword"
                    placeholder="비밀번호를 한 번 더 입력해주세요"
                  />
                  <span @click="toggleConfirmPasswordVisibility">
                    <i :class="showConfirmPassword ? 'password-eye-open-icon' : 'password-eye-closed-icon'"></i>
                  </span>
                </div>
              </div>

              <!-- 버튼 -->
              <div class="password-modal-buttons">
                <button @click="closePasswordModal" class="password-cancel-button">취소</button>
                <button @click="updatePassword" class="password-confirm-button">수정 하기</button>
              </div>
            </div>
          </div>

          <!-- 가입일, 최근 수정, 마지막 접속 가로 배치 -->
          <div class="horizontal-info">
            <p><strong>가입일:</strong> 2025.01.07</p>
            <p><strong>마지막 접속:</strong> 2025.01.07</p>
          </div>

          <!-- 사업자 정보 섹션 -->
          <div class="business-info">
            <div class="field">
              <label for="company-name">회사명</label>
              <span class="info-value">{{ companyName }}</span>
              <span class="material-icons-round edit-icon" @click="openCompanyNameEditDialog">
          edit
        </span>
            </div>
            <div class="field">
              <label for="business-number">사업자 등록 번호</label>
              <span class="info-value">{{ businessNumber }}</span>
              <span class="material-icons-round edit-icon" @click="openBusinessNumberEditDialog">
          edit
        </span>
            </div>
            <!-- 회사명 수정 다이얼로그 -->
            <div v-if="isCompanyNameEditDialogOpen" class="dialog-backdrop">
              <div class="dialog">
                <h3>회사명 수정</h3>
                <p>현재 회사명: <strong>{{ companyName }}</strong></p>
                <div class="input-container">
                  <input 
                    type="text" 
                    v-model="tempCompanyName" 
                    placeholder="새 회사명 입력" 
                  />
                </div>
                <div class="dialog-buttons">
                  <button @click="confirmCompanyNameEdit">수정</button>
                  <button @click="closeCompanyNameEditDialog">취소</button>
                </div>
              </div>
            </div>

            <!-- 사업자 등록 번호 수정 다이얼로그 -->
            <div v-if="isBusinessNumberEditDialogOpen" class="dialog-backdrop">
              <div class="dialog">
                <h3>사업자 등록 번호 수정</h3>
                <p>현재 번호: <strong>{{ businessNumber }}</strong></p>
                <div class="input-container">
                  <input 
                    type="text" 
                    v-model="tempBusinessNumber" 
                    placeholder="새 등록 번호 입력" 
                  />
                </div>
                <div class="dialog-buttons">
                  <button @click="confirmBusinessNumberEdit">수정</button>
                  <button @click="closeBusinessNumberEditDialog">취소</button>
                </div>
              </div>
            </div>
            <div class="field">
              <label for="business-file">사업자 등록증</label>
              <!-- 사업자 등록증 네모 칸 -->
              <div class="business-certificate-container">
                <div class="upload-box">
                  <p v-if="!businessFileName" class="placeholder-text">사업자 등록증 파일 미등록</p>
                  <p v-else class="file-name">업로드된 파일: {{ businessFileName }}</p>
                </div>

                <!-- +파일 업로드 버튼 -->
                <button class="upload-button" @click="triggerFileUpload">+ 파일 업로드</button>
              </div>

              <!-- 숨겨진 파일 입력 필드 -->
              <input 
                type="file" 
                id="business-file" 
                ref="businessFileInput" 
                @change="handleBusinessFileChange" 
                hidden 
              />
            </div>

          <!-- 회원탈퇴 버튼 -->
          <button @click="openModal" class="delete-account-btn">회원탈퇴</button>
          </div>
        </div>
      </section>
      
      <!-- 모달 -->
      <div v-if="isModalOpen" class="modal-overlay">
        <div class="modal-content">
          <h2>회원 탈퇴하기</h2>
          <p>
            회원 탈퇴시 아래 항목은 영구적으로 삭제되며 복원할 수 없습니다.
            탈퇴 진행을 위해 가입한 이메일을 입력해주세요.
          </p>
          <ul>
            <li>이메일, 연락처, 결제 내역 등 개인 정보</li>
            <li>작성한 이미지 및 작성한 평생 기록</li>
            <li>업로드 및 보관한 파일</li>
            <li>설정한 이미지 파일</li>
          </ul>
          <!-- 이메일 입력 -->
          <div class="email-field">
            <label for="email">이메일(e-mail)</label>
            <input
              type="email"
              id="email"
              v-model="email"
              placeholder="가입된 이메일 주소를 작성해주세요"
            />
          </div>
          <!-- 버튼 -->
          <div class="modal-buttons">
            <button @click="closeModal" class="cancel-button">취소</button>
            <button @click="confirmDelete" class="confirm-button">탈퇴</button>
          </div>
        </div>
      </div>
    </div>
  </template>
  
  <script>
import Header from "../HomePage/components/Header.vue";

export default {
  name: "MyPage",
  components: {
    Header,
  },
  data() {
    return {
      activeTab: "basic",
      profileImage: "",
      companyName: "", // 회사명
      businessNumber: "", // 사업자 등록 번호
      businessFileName: "", // 업로드된 파일 이름
      isModalOpen: false, // 모달 상태
      email: "", // 입력한 이메일
      isPasswordModalOpen: false, // 비밀번호 변경 모달 상태
      newPassword: "", // 새 비밀번호
      confirmPassword: "", // 비밀번호 확인
      showNewPassword: false, // 새 비밀번호 표시 여부
      showConfirmPassword: false, // 비밀번호 확인 표시 여부
      nickname: "김유중", // 사용자 닉네임 (초기값)
      tempNickname: "", // 수정 시 임시 닉네임
      isNicknameEditModalOpen: false, // 닉네임 수정 모달 열림 여부
      companyName: "ABC 주식회사", // 초기 회사명
      businessNumber: "123-45-67890", // 초기 사업자 등록 번호
      tempCompanyName: "", // 새 회사명
      tempBusinessNumber: "", // 새 사업자 등록 번호
      isCompanyNameEditDialogOpen: false, // 회사명 수정 다이얼로그 열림 여부
      isBusinessNumberEditDialogOpen: false, // 사업자 등록 번호 수정 다이얼로그 열림 여부
    };
  },
  methods: {
    selectFile() {
      this.$refs.fileInput.click(); // 파일 선택 창 열기
    },
    // 파일 업로드 창을 열기 위한 메서드
    triggerFileUpload() {
      this.$refs.businessFileInput.click(); // 숨겨진 파일 입력 필드 클릭
    },
    // 파일 선택 시 처리하는 메서드
    handleBusinessFileChange(event) {
      const file = event.target.files[0];
      if (file) {
        this.businessFileName = file.name; // 업로드된 파일 이름 저장
        alert(`파일 "${file.name}"이 업로드되었습니다.`);
      }
    },
    handleFileChange(event) {
      const file = event.target.files[0];
      if (file) {
        const reader = new FileReader();
        reader.onload = (e) => {
          this.profileImage = e.target.result; // 선택된 이미지 파일을 저장
        };
        reader.readAsDataURL(file); // 파일을 데이터 URL 형식으로 읽기
      }
    },
    openModal() {
      this.isModalOpen = true; // 모달 열기
    },
    closeModal() {
      this.isModalOpen = false; // 모달 닫기
    },
    confirmDelete() {
      if (this.email.trim() === "") {
        alert("이메일을 입력해주세요."); // 이메일 미입력 시 경고
        return;
      }
      alert(`회원 탈퇴가 완료되었습니다. 이메일: ${this.email}`);
      this.closeModal(); // 모달 닫기
    },

    openPasswordModal() {
      this.isPasswordModalOpen = true; // 모달 열기
    },
    closePasswordModal() {
      this.isPasswordModalOpen = false; // 모달 닫기
    },
    toggleNewPasswordVisibility() {
      this.showNewPassword = !this.showNewPassword;
      const input = document.getElementById("newPassword");
      input.type = this.showNewPassword ? "text" : "password";
    },
    toggleConfirmPasswordVisibility() {
      this.showConfirmPassword = !this.showConfirmPassword;
      const input = document.getElementById("confirmPassword");
      input.type = this.showConfirmPassword ? "text" : "password";
    },
    updatePassword() {
      if (this.newPassword === "" || this.confirmPassword === "") {
        alert("모든 필드를 입력해주세요.");
        return;
      }
      if (this.newPassword !== this.confirmPassword) {
        alert("비밀번호가 일치하지 않습니다.");
        return;
      }
      alert("비밀번호가 성공적으로 변경되었습니다.");
      this.closePasswordModal(); // 모달 닫기
    },
    openNicknameEditModal() {
      this.tempNickname = ""; // 새 닉네임 초기화
      this.isNicknameEditModalOpen = true; // 닉네임 수정 모달 열기
    },
    closeNicknameEditModal() {
      this.isNicknameEditModalOpen = false; // 닉네임 수정 모달 닫기
    },
    confirmNicknameEdit() {
      if (this.tempNickname.trim()) {
        if (confirm("정말로 수정하시겠습니까?")) {
          this.nickname = this.tempNickname.trim(); // 닉네임 저장
        }
        this.closeNicknameEditModal(); // 닉네임 수정 모달 닫기
      } else {
        alert("새 닉네임을 입력해주세요.");
      }
    },
    // 회사명 수정 다이얼로그
    openCompanyNameEditDialog() {
      this.tempCompanyName = ""; // 초기화
      this.isCompanyNameEditDialogOpen = true;
    },
    closeCompanyNameEditDialog() {
      this.isCompanyNameEditDialogOpen = false;
    },
    confirmCompanyNameEdit() {
      if (this.tempCompanyName.trim()) {
        if (confirm("정말로 수정하시겠습니까?")) {
          this.companyName = this.tempCompanyName.trim(); // 수정
        }
        this.closeCompanyNameEditDialog();
      } else {
        alert("새 회사명을 입력해주세요.");
      }
    },

    // 사업자 등록 번호 수정 다이얼로그
    openBusinessNumberEditDialog() {
      this.tempBusinessNumber = ""; // 초기화
      this.isBusinessNumberEditDialogOpen = true;
    },
    closeBusinessNumberEditDialog() {
      this.isBusinessNumberEditDialogOpen = false;
    },
    confirmBusinessNumberEdit() {
      if (this.tempBusinessNumber.trim()) {
        if (confirm("정말로 수정하시겠습니까?")) {
          this.businessNumber = this.tempBusinessNumber.trim(); // 수정
        }
        this.closeBusinessNumberEditDialog();
      } else {
        alert("새 등록 번호를 입력해주세요.");
      }
    },
  },
};
</script>

<style scoped>
  /* 전체 레이아웃 */
  .mypage {
    padding: 2rem;
    padding-top: 6rem; /* Header 높이만큼 상단 여백 추가 */
  }
  
  /* 페이지 제목 */
  .page-title {
    text-align: left;
    padding: 1rem 0;
  }
  
  .page-title h1 {
    font-size: 1.8rem;
    font-weight: bold;
    color: #000;
  }
  
  /* 탭 메뉴 */
  .tabs {
    display: flex;
    gap: 1rem;
    margin-bottom: 0.5rem; /* 가로 선과 간격 */
  }
  
  .tab-item {
    padding: 0.5rem 1rem;
    border: none;
    background: none;
    font-size: 1rem;
    cursor: pointer;
    color: #6c757d;
    border-bottom: 2px solid transparent;
    transition: color 0.3s ease, border-color 0.3s ease;
  }
  
  .tab-item.active {
    color: #000;
    border-bottom: 2px solid #000; /* 활성화된 탭 하단선 */
  }
  
  .tab-item:hover {
    color: #000;
  }
  
  /* 가로 선 */
  .divider {
    border: none;
    border-bottom: 1px solid #ddd; /* 얇고 깔끔한 선 */
    margin: 0; /* 기본 마진 제거 */
    margin-bottom: 1rem; /* 선 아래 콘텐츠 간격 */
  }
  
  /* 기본 정보 섹션 */
  .tab-content.basic-info {
    display: flex;
    justify-content: center;
    margin-top: 2rem;
  }
  /* 가로 배치 */
.horizontal-info {
  display: flex;
  justify-content: space-between; /* 가로로 간격 조절 */
  width: 21%; /* 가로로 넓이 꽉 채우기 */
  margin-top: 1rem;
  margin-bottom: 1rem;
}

.horizontal-info p {
  font-size: 0.9rem;
  color: #6c757d;
}
  
  .info-card {
    display: flex;
    flex-direction: column;  /* 카드 내부 콘텐츠가 수직으로 정렬되도록 수정 */
    align-items: center;     /* 카드 내부 콘텐츠를 중앙에 배치 */
    background: #f8f9fa;
    border-radius: 0.5rem;
    padding: 1.5rem;
    box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
    width: 100%;            /* 카드의 너비를 설정하여 중앙 정렬 */
  }
  
  .profile-pic {
    width: 6rem;
    height: 6rem;
    border-radius: 50%;
    background: #e9ecef;
    display: flex;
    justify-content: center;
    align-items: center;
    font-size: 2rem;
    color: #6c757d;
    overflow: hidden;
    cursor: pointer;
    margin-bottom: 1rem; /* 사진과 텍스트 간 간격 추가 */
  }
  
  .profile-pic img {
    width: 100%;
    height: 100%;
    object-fit: cover;
  }
  
  .profile-pic span {
    font-size: 2rem;
  }
  
  /* 텍스트 스타일 */
  .info-text {
    text-align: center;
  }
  
  .info-text h2 {
    font-size: 1.2rem;
    font-weight: bold;
    margin-bottom: 0.5rem;
  }
  
  .info-text p {
    font-size: 0.9rem;
    color: #6c757d;
    margin-bottom: 0.3rem;
  }
  .nickname-section {
  display: flex;
  align-items: center;
  gap: 8px;
  justify-content: center;
}
.nickname {
  margin: 0;
  font-size: 24px;
  font-weight: bold;
  text-align: center;
}

.edit-icon {
  cursor: pointer;
  font-size: 16px;
  margin-left: 8px;
  color: #555;
}

.edit-icon:hover {
  color: #000;
}
/* 닉네임 수정 모달 스타일 */
.nickname-modal-backdrop {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.5);
  display: flex;
  justify-content: center;
  align-items: center;
}

.nickname-modal {
  background: white;
  padding: 30px;
  border-radius: 12px;
  width: 400px;
  box-shadow: 0 6px 10px rgba(0, 0, 0, 0.15);
}

.nickname-modal h3 {
  margin-bottom: 20px;
  font-size: 20px;
  font-weight: bold;
}

.nickname-modal p {
  margin-bottom: 20px;
  font-size: 16px;
}

.input-container {
  position: relative;
  margin-bottom: 30px;
}

.input-container input {
  width: 100%;
  padding: 8px 0;
  border: none;
  border-bottom: 1px solid #ddd; /* 밑줄 */
  font-size: 16px;
  outline: none;
  transition: border-color 0.3s ease;
}

.input-container input:focus {
  border-bottom-color: #007bff;
}

.nickname-modal-buttons {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
}

.nickname-modal-buttons button {
  padding: 8px 12px;
  border: none;
  border-radius: 4px;
  cursor: pointer;
}

.nickname-modal-buttons button:first-child {
  background: #5CB494;
  color: white;
}

.nickname-modal-buttons button:last-child {
  background: #ddd;
  color: #333;
}

.password-change-button {
  background: none;
  border: none;
  color: #007bff; /* 파란색 */
  text-decoration: underline;
  cursor: pointer;
  font-size: 0.8rem;
  padding: 0;
}

.password-change-button:hover {
  color: #0056b3; /* 어두운 파란색 */
  text-decoration: underline;
}
  /* 모달 오버레이 */
.modal-overlay-password {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background-color: rgba(0, 0, 0, 0.5); /* 반투명 검은 배경 */
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 1000;
}

/* 모달 컨텐츠 */
.modal-content-password {
  background: #fff;
  padding: 2rem;
  border-radius: 8px;
  width: 90%;
  max-width: 400px;
  text-align: center;
  box-shadow: 0 4px 10px rgba(0, 0, 0, 0.1);
}

/* 제목 */
.modal-content-password h2 {
  margin: 0 0 1.5rem;
  font-size: 1.5rem;
  color: #333;
}

/* 입력 필드 */
.password-field {
  margin-bottom: 1.5rem;
  text-align: left;
}

.password-field label {
  font-size: 0.9rem;
  color: #555;
  margin-bottom: 0.5rem;
  display: block;
}

.password-input-container {
  position: relative;
}

.password-input-container input {
  width: 100%;
  border: none;
  border-bottom: 1px solid #ddd; /* 밑줄 */
  outline: none;
  padding: 0.5rem 0;
  font-size: 1rem;
}
.password-input-container input:focus {
  border-bottom: 1px solid #007bff; /* 포커스 시 파란색 */
}

.password-input-container span {
  position: absolute;
  top: 50%;
  right: 0.5rem;
  transform: translateY(-50%);
  cursor: pointer;
}

.password-eye-open-icon::before {
  content: "👁️";
}

.password-eye-closed-icon::before {
  content: "👁️‍🗨️";
}

/* 버튼 */
.password-modal-buttons {
  display: flex;
  justify-content: space-between;
  gap: 1rem;
}

.password-cancel-button,
.password-confirm-button {
  flex: 1;
  padding: 0.5rem 1rem;
  border: none;
  border-radius: 4px;
  font-size: 0.9rem;
  cursor: pointer;
}

.password-cancel-button {
  background: #ddd;
  color: #333;
}

.password-cancel-button:hover {
  background: #bbb;
}

.password-confirm-button {
  background: #5CB494;
  color: #fff;
}

.password-confirm-button:hover {
  background: #218838;
}

  /* 사업자 정보 섹션 */
  .business-info {
  display: flex;
  flex-direction: column;
  align-items: center; /* 중앙 정렬 */
  margin-top: 2rem; /* 상단 여백 */
  width: 50%;
  text-align: center; /* 텍스트 정렬 */
}

.field {
  width: 60%; /* 적당한 가로 너비 설정 */
  margin-bottom: 1rem;
}

.field label {
  display: block;
  font-size: 0.9rem;
  font-weight: bold;
  margin-bottom: 0.5rem;
  text-align: left; /* 라벨은 왼쪽 정렬 */
}

.field input[type="text"],
.field input[type="file"] {
  width: 100%;
  border: none; /* 기본 테두리 제거 */
  border-bottom: 1px solid #ddd; /* 하단에만 선 추가 */
  outline: none; /* 클릭 시 기본 파란색 테두리 제거 */
  font-size: 1rem;
  color: #333;
  background: none;
  transition: border-color 0.3s ease;
}

.field input[type="text"]:focus,
.field input[type="file"]:focus {
  border-bottom: 1px solid #007bff; /* 포커스 시 하단 선 색상 변경 */
}

.business-certificate-container {
  display: flex;
  flex-direction: column;
  align-items: flex-start; /* 왼쪽 정렬 */
  gap: 0.5rem; /* 필드 간 간격 추가 */
  width: 100%;
}

.upload-box {
  width: 100%;
  border: none; /* 기본 테두리 제거 */
  border-bottom: 1px solid #ddd; /* 하단에만 선 추가 */
  padding: 0.5rem 0; /* 내부 여백 조정 */
  font-size: 0.9rem;
  color: #6c757d;
  background: none;
  display: flex;
  align-items: center;
  justify-content: space-between; /* 텍스트와 버튼 간격 확보 */
}
.upload-box p {
  margin: 0; /* 기본 마진 제거 */
  color: #6c757d;
}

.placeholder-text {
  color: #aaa; /* 안내 텍스트 색상 */
  font-size: 0.9rem;
}

.file-name {
  font-size: 0.9rem;
  color: #333; /* 파일 이름 색상 */
}

.upload-button {
  background: none;
  color: #007bff;
  font-size: 0.9rem;
  font-weight: bold;
  border: none;
  cursor: pointer;
  padding: 0;
  text-decoration: underline;
}

.upload-button:hover {
  color: #0056b3;
}
  
  /* 결제 정보 섹션 */
  .tab-content.payment-info {
    text-align: center;
    margin-top: 2rem;
    font-size: 1rem;
    color: #6c757d;
  }

 /* 회원탈퇴 버튼 스타일 */
.delete-account-btn {
  padding: 0.7rem 1.5rem;
  background-color: #dc3545;
  color: #fff;
  font-size: 1rem;
  border: none;
  border-radius: 0.3rem;
  cursor: pointer;
  margin-top: 2rem; /* 텍스트 아래 간격 */
}

.delete-account-btn:hover {
  background-color: #c82333;
}

/* 모달 오버레이 */
.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background-color: rgba(0, 0, 0, 0.5); /* 반투명 검은 배경 */
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 1000;
}

/* 모달 컨텐츠 */
.modal-content {
  background: #fff;
  padding: 2rem;
  border-radius: 8px;
  width: 90%;
  max-width: 500px;
  text-align: center;
  box-shadow: 0 4px 10px rgba(0, 0, 0, 0.1);
}

/* 모달 제목 */
.modal-content h2 {
  margin: 0 0 1rem;
  font-size: 1.5rem;
  color: #333;
}

/* 모달 설명 */
.modal-content p {
  font-size: 0.9rem;
  color: #666;
  margin-bottom: 1rem;
  line-height: 1.5;
}

/* 목록 */
.modal-content ul {
  list-style-type: none;
  padding: 0;
  margin: 1rem 0;
  text-align: left;
}

.modal-content ul li {
  font-size: 0.9rem;
  color: #555;
  margin-bottom: 0.5rem;
  padding-left: 1.5rem;
  position: relative;
}

/* 체크 아이콘 */
.modal-content ul li::before {
  content: "✔";
  position: absolute;
  left: 0;
  color: #4caf50;
  font-weight: bold;
  font-size: 1rem;
}

/* 이메일 입력 필드 */
.email-field {
  margin: 1rem 0;
  text-align: left;
}

.email-field label {
  font-size: 0.9rem;
  color: #555;
  margin-bottom: 0.5rem;
  display: block;
}

.email-field input {
  width: 100%;
  border: none;
  border-bottom: 1px solid #ddd; /* 밑줄 */
  outline: none;
  padding: 0.5rem 0;
  font-size: 1rem;
}

.email-field input:focus {
  border-color: #007bff;
}

/* 버튼 */
.modal-buttons {
  display: flex;
  justify-content: space-between;
  gap: 1rem;
}

.cancel-button,
.confirm-button {
  flex: 1;
  padding: 0.5rem 1rem;
  border: none;
  border-radius: 4px;
  font-size: 0.9rem;
  cursor: pointer;
}

.cancel-button {
  background: #ddd;
  color: #333;
}

.cancel-button:hover {
  background: #bbb;
}

.confirm-button {
  background: #dc3545;
  color: #fff;
}

.confirm-button:hover {
  background: #c82333;
}

/* 다이얼로그 스타일 */
.dialog-backdrop {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.5);
  display: flex;
  justify-content: center;
  align-items: center;
}

.dialog {
  background: white;
  padding: 30px;
  border-radius: 12px;
  width: 400px;
  box-shadow: 0 6px 10px rgba(0, 0, 0, 0.15);
}

.dialog h3 {
  margin-bottom: 20px;
  font-size: 20px;
  font-weight: bold;
}

.dialog p {
  margin-bottom: 20px;
  font-size: 16px;
}

.input-container {
  position: relative;
  margin-bottom: 30px;
}

.input-container input {
  width: 100%;
  padding: 8px 0;
  border: none;
  border-bottom: 2px solid #ccc;
  font-size: 16px;
  outline: none;
  transition: border-color 0.3s ease;
}

.input-container input:focus {
  border-bottom-color: #4caf50;
}

.dialog-buttons {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
}

.dialog-buttons button {
  padding: 8px 12px;
  border: none;
  border-radius: 4px;
  cursor: pointer;
}

.dialog-buttons button:first-child {
  background: #4caf50;
  color: white;
}

.dialog-buttons button:last-child {
  background: #ddd;
  color: #333;
}
  </style>
  