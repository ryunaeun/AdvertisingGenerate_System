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
          @click="scrollToSection('basic-info')">
          기본 정보
        </button>
        <button 
          class="tab-item" 
          :class="{ active: activeTab === 'subscription-info' }" 
          @click="scrollToSection('subscription-info')">
          결제 정보
        </button>
      </nav>
      
      <!-- 선 추가 -->
      <hr class="divider" />
      
      <!-- 콘텐츠 -->
      <section id="basic-info" class="tab-content basic-info">
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
              이메일: {{ email }}
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
          <!-- ✅ 비밀번호 변경 모달 -->
          <div v-if="isPasswordModalOpen" class="modal-overlay-password">
            <div class="modal-content-password">
              <h2>비밀번호 변경</h2>

              <!-- 새 비밀번호 입력 -->
              <div class="password-field">
                <label for="newPassword">새 비밀번호</label>
                <div class="password-input-container">
                  <input
                    :type="showNewPassword ? 'text' : 'password'"
                    id="newPassword"
                    v-model="newPassword"
                    placeholder="새로운 비밀번호 입력"
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
                    :type="showConfirmPassword ? 'text' : 'password'"
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
            <p><strong>가입일:</strong> {{ createdAt }}</p>
            <p><strong>마지막 접속:</strong> {{ nowAt }}</p>
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

              <!-- 업로드된 파일 표시 -->
              <div class="business-certificate-container">
                <div class="upload-box">
                  <p v-if="!businessFileName" class="placeholder-text">사업자 등록증 파일 미등록</p>
                  <p v-else class="file-name">업로드된 파일: {{ businessFileName }}</p>
                </div>

                <!-- 파일 업로드 버튼 -->
                <button class="upload-button" @click="triggerFileUpload">+ 파일 업로드</button>
              </div>

              <!-- 숨겨진 파일 입력 필드 -->
              <input 
                type="file"
                id="business-file"
                ref="businessFileInput"
                @change="handleBusinessFileChange"
                accept=".pdf,.jpg,.jpeg,.png"
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
              v-model="tempEmail"
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
    <!-- 현재 구독 정보 -->
    <section id="subscription-info" class="custom-card modern-card hover-highlight">
        <h3>현재 구독 중인 요금제</h3>
        <div class="custom-card-content centered">
            <div class="subscription-details">
                <h4 class="plan-title">{{ billing }}</h4>
                <p class="subtitle">잔여 무료 체험 기간: <strong class="highlight">{{ billingDate }}</strong></p>
            </div>
            <div class="cta">
                <p class="description">더 많은 기능이 필요하신가요?<br />
                    지금 바로 구독하고 모든 기능을 사용해보세요!</p>
                <router-link to="/pricing" class="custom-button primary-button">요금제 보러 가기</router-link>
            </div>
        </div>
    </section>

    <!-- 기본 결제 수단 -->
    <section id="paymentSection" class="custom-card modern-card hover-highlight">
        <h3>기본 결제 수단</h3>
        <div class="custom-card-content centered">
            <div>
                <p class="subtitle">결제 수단을 등록해 주세요.</p>
                <p class="description">한 번만 등록해 놓으면 쉽게 사용 가능합니다.</p>
            </div>
            <button class="custom-button primary-button">결제 수단 등록하기</button>
        </div>
    </section>

    <!-- 최근 청구 항목 -->
    <section id="recentBillingSection" class="custom-card modern-card hover-highlight">
        <h3>최근 청구 항목</h3>
        <div class="custom-card-content centered">
            <p class="subtitle">최근 청구 항목이 없습니다.</p>
        </div>
    </section>

  </template>
  
  <script>
  import Header from "../HomePage/components/Header.vue";
  import apiClient from "@/api/axiosClient"; // ✅ 기존 axios 대신 apiClient 사용
  
  export default {
    name: "MyPage",
    components: {
      Header,
    },
    data() {
      return {
        activeTab: "basic",
        profileImage: "",
        companyName: "",
        businessNumber: "",
        businessFileName: "",
        isModalOpen: false,
        email: "",
        tempEmail: "",
        isPasswordModalOpen: false, // ✅ 비밀번호 변경 모달 상태
        newPassword: "",
        confirmPassword: "",
        showNewPassword: false,
        showConfirmPassword: false,
        nickname: "",
        tempNickname: "",
        isNicknameEditModalOpen: false,
        createdAt: "",
        nowAt: "",
        tempCompanyName: "",
        tempBusinessNumber: "",
        isCompanyNameEditDialogOpen: false,
        isBusinessNumberEditDialogOpen: false,
        billing:"",
        billingDate:"",
      };
    },
    methods: {
      // ✅ 사용자 기본 정보 가져오기 (Spring Boot API 호출)
      async fetchUserInfo() {
        try {
          const response = await apiClient.get("/user-info/personal");
          const userData = response.data;
  
          this.nickname = userData.username;
          this.email = userData.email;
          this.createdAt = this.formatDate(userData.createdAt);
          this.nowAt = this.formatDate(userData.nowAt);
          this.companyName = userData.companyName;
          this.businessNumber = userData.businessNumber;
          this.businessFileName = userData.businessFilePath;
        } catch (error) {
          console.error("사용자 정보를 가져오는 중 오류 발생:", error);
          alert("사용자 정보를 불러오지 못했습니다.");
        }
      },
       // ✅ 결제 정보 가져오기
      async fetchPaymentInfo() {
        try {
          const response = await apiClient.get("/user-info/payment");
          this.billing = response.data.billing || "무료 체험"; // billing 값이 없으면 기본값
          this.billingDate = this.formatDate(response.data.billingDate);
        } catch (error) {
          console.error("결제 정보 가져오기 실패:", error);
          alert("결제 정보를 불러오지 못했습니다.");
        }
      },
    
  
      // ✅ 날짜 형식 변환 (YYYY-MM-DD → YYYY.MM.DD)
      formatDate(dateString) {
        if (!dateString) return "";
        return new Date(dateString).toLocaleDateString("ko-KR", {
          year: "numeric",
          month: "2-digit",
          day: "2-digit",
        }).replace(/\./g, ".");
      },
  
      // ✅ 닉네임 수정 (API 연동)
      async confirmNicknameEdit() {
        if (!this.tempNickname.trim()) {
          alert("새 닉네임을 입력해주세요.");
          return;
        }
        if (!confirm("정말로 수정하시겠습니까?")) return;
  
        try {
          await apiClient.put("/user-info/update/text", { username: this.tempNickname.trim() });
  
          this.nickname = this.tempNickname.trim(); // UI 업데이트
          this.isNicknameEditModalOpen = false;
          alert("닉네임이 성공적으로 변경되었습니다.");
        } catch (error) {
          console.error("닉네임 수정 오류:", error);
          alert("닉네임 변경에 실패했습니다.");
        }
      },
  
      // ✅ 닉네임 수정 다이얼로그 열기
      openNicknameEditModal() {
        this.tempNickname = this.nickname; // 기존 닉네임 저장
        this.isNicknameEditModalOpen = true;
      },

      // ✅ 닫기
      closeNicknameEditModal() {
        this.isNicknameEditModalOpen = false;
      },

      // ✅ 회사명 수정 다이얼로그 열기
      openCompanyNameEditDialog() {
        this.tempCompanyName = this.companyName; // 기존 회사명 저장
        this.isCompanyNameEditDialogOpen = true;
      },

      // ✅ 닫기
      closeCompanyNameEditDialog() {
        this.isCompanyNameEditDialogOpen = false;
      },

      // ✅ 사업자 등록번호 수정 다이얼로그 열기
      openBusinessNumberEditDialog() {
        this.tempBusinessNumber = this.businessNumber; // 기존 사업자번호 저장
        this.isBusinessNumberEditDialogOpen = true;
      },

      // ✅ 닫기
      closeBusinessNumberEditDialog() {
        this.isBusinessNumberEditDialogOpen = false;
      },
      // ✅ 회사명 수정 (API 연동)
      async confirmCompanyNameEdit() {
        if (!this.tempCompanyName.trim()) {
          alert("새 회사명을 입력해주세요.");
          return;
        }
        if (!confirm("정말로 수정하시겠습니까?")) return;
  
        try {
          await apiClient.put("/user-info/update/text", { companyName: this.tempCompanyName.trim() });
  
          this.companyName = this.tempCompanyName.trim(); // UI 업데이트
          this.isCompanyNameEditDialogOpen = false;
          alert("회사명이 성공적으로 변경되었습니다.");
        } catch (error) {
          console.error("회사명 수정 오류:", error);
          alert("회사명 변경에 실패했습니다.");
        }
      },
  
      // ✅ 사업자 번호 수정 (API 연동)
      async confirmBusinessNumberEdit() {
        if (!this.tempBusinessNumber.trim()) {
          alert("새 사업자 번호를 입력해주세요.");
          return;
        }
        if (!confirm("정말로 수정하시겠습니까?")) return;
  
        try {
          await apiClient.put("/user-info/update/text", { businessNumber: this.tempBusinessNumber.trim() });
  
          this.businessNumber = this.tempBusinessNumber.trim(); // UI 업데이트
          this.isBusinessNumberEditDialogOpen = false;
          alert("사업자 번호가 성공적으로 변경되었습니다.");
        } catch (error) {
          console.error("사업자 번호 수정 오류:", error);
          alert("사업자 번호 변경에 실패했습니다.");
        }
      },
  
      // ✅ 비밀번호 변경 모달 열기
      openPasswordModal() {
        this.isPasswordModalOpen = true;
      },
  
      // ✅ 비밀번호 변경 모달 닫기
      closePasswordModal() {
        this.isPasswordModalOpen = false;
      },
  
      // ✅ 비밀번호 변경 API 요청
      async updatePassword() {
        if (!this.newPassword || !this.confirmPassword) {
          alert("모든 필드를 입력해주세요.");
          return;
        }
  
        if (this.newPassword !== this.confirmPassword) {
          alert("비밀번호가 일치하지 않습니다.");
          return;
        }
  
        if (!this.validatePassword(this.newPassword)) {
          alert("비밀번호는 8자 이상, 대문자, 소문자, 숫자, 특수문자를 포함해야 합니다.");
          return;
        }
  
        try {
          // ✅ 백엔드 API로 비밀번호 변경 요청
          const response = await apiClient.put("/user-info/update-password", {
            newPassword: this.newPassword,
            confirmPassword: this.confirmPassword, // 백엔드와 맞추기 위해 추가
          });
  
          alert(response.data); // 성공 메시지 출력
          this.newPassword = "";
          this.confirmPassword = "";
          this.closePasswordModal();
        } catch (error) {
          console.error("비밀번호 변경 오류:", error);
  
          if (error.response && error.response.data) {
            alert(`비밀번호 변경 실패: ${error.response.data}`);
          } else {
            alert("비밀번호 변경 중 오류가 발생했습니다.");
          }
        }
      },
  
      // ✅ 비밀번호 가시성 토글 (눈 아이콘 클릭)
      toggleNewPasswordVisibility() {
        this.showNewPassword = !this.showNewPassword;
      },
  
      toggleConfirmPasswordVisibility() {
        this.showConfirmPassword = !this.showConfirmPassword;
      },
  
      // ✅ 비밀번호 유효성 검사
      validatePassword(password) {
        const passwordRegex =
          /^(?=.*[A-Z])(?=.*[a-z])(?=.*\d)(?=.*[@$!%*?&])[A-Za-z\d@$!%*?&]{8,}$/;
        return passwordRegex.test(password);
      },
  
        selectFile() {
          this.$refs.fileInput.click();
        },

        triggerFileUpload() {
          this.$refs.businessFileInput.click();
        },

        async handleBusinessFileChange(event) {
          const file = event.target.files[0];
          if (!file) return;

          // 🔹 파일 크기 제한 (10MB)
          const maxSize = 10 * 1024 * 1024;
          if (file.size > maxSize) {
            alert("파일 크기는 10MB를 초과할 수 없습니다.");
            return;
          }

          // 🔹 허용된 파일 확장자 확인 (PDF, JPG, PNG)
          const allowedTypes = ["application/pdf", "image/jpeg", "image/png"];
          if (!allowedTypes.includes(file.type)) {
            alert("PDF, JPEG, PNG 파일만 업로드 가능합니다.");
            return;
          }

          this.businessFileName = file.name;
          alert(`파일 "${file.name}"이 업로드되었습니다.`);

          // 🔹 FormData 생성
          const formData = new FormData();
          formData.append("businessFile", file);

          try {
            // 🔹 파일 업로드 요청 (PUT 요청)
            const response = await apiClient.put("/user-info/update/file", formData, {
              headers: { "Content-Type": "multipart/form-data" },
            });

            alert(response.data); // 성공 메시지 출력
          } catch (error) {
            console.error("파일 업로드 실패:", error);
            alert(error.response?.data || "파일 업로드 중 오류가 발생했습니다.");
          }
        },
      openModal() {
        this.isModalOpen = true;
      },
      closeModal() {
        this.isModalOpen = false;
      },
      confirmDelete() {

        if (!this.tempEmail.trim()) {
          alert("회원 탈퇴를 위해 이메일을 입력해주세요.");
          return;
        }

        if (this.tempEmail.trim() !== this.email.trim()) {
          alert("입력한 이메일이 기존 이메일과 일치하지 않습니다.");
          return;
        }

        if (!confirm("정말로 계정을 삭제하시겠습니까? 이 작업은 되돌릴 수 없습니다.")) {
          return;
        }

        apiClient.delete("/user-info/delete", {
          params: { email: this.tempEmail } 
        })
        .then(response => {
          alert(response.data);
          sessionStorage.removeItem("accessToken");
          localStorage.removeItem("refreshToken");
          this.$router.push("/login");
        })
        .catch(error => {
          console.error("회원 탈퇴 요청 실패:", error);

          if (error.response) {
            alert(error.response.data || "회원 탈퇴 중 오류가 발생했습니다.");
          } else {
            alert("서버에 연결할 수 없습니다. 네트워크 상태를 확인해주세요.");
          }
        });
      },
      scrollToSection(sectionId) {
        const section = document.getElementById(sectionId);
        if (section) {
          section.scrollIntoView({ behavior: "smooth" });
          this.activeTab = sectionId === "basic-info" ? "basic" : "subscription-info";
        }
      },
    },
    mounted() {
      this.fetchUserInfo(); // ✅ 페이지 로드 시 API 호출
      this.fetchPaymentInfo();
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
  background: #5CB494;
  color: white;
}

.dialog-buttons button:last-child {
  background: #ddd;
  color: #333;
}
.payment-info-page {
  padding: 20px;
  font-family: Arial, sans-serif;
  color: #333;
}

.custom-card {
  background: #fff;
  border: 1px solid #ddd;
  border-radius: 8px;
  padding: 20px;
  margin-bottom: 20px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

.custom-card-content {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.subscription-details {
  max-width: 60%;
}

.credit-info {
  display: flex;
  align-items: center;
  margin-top: 10px;
}

.credit-info span:first-child {
  margin-right: 10px;
}

.credit-remaining {
  font-weight: bold;
}

.cta {
    display: flex;
    flex-direction: column; /* 세로 배치 */
    align-items: center; /* 버튼을 중앙 정렬 */
    text-align: center; /* 기본적으로 중앙 정렬 */
}

.description {
    text-align: right; /* 텍스트를 우측 정렬 */
    width: 100%; /* 부모 요소의 전체 너비 사용 */
}

    /* Centering the content */
    .container {
        display: flex;
        flex-direction: column;
        align-items: center;
        justify-content: center;
        width: 100%;
        min-height: 100vh;
        padding: 30px 0;
        background: linear-gradient(135deg, #f8f9fa, #e3e6eb);
    }

    .custom-card {
        background: #fff;
        border-radius: 16px;
        box-shadow: 0px 8px 20px rgba(0, 0, 0, 0.15);
        padding: 40px;
        max-width: 700px;
        width: 90%;
        margin: 30px auto;
        transition: all 0.3s ease-in-out;
        border: 1px solid #d1d5db;
        text-align: center;
    }

    .modern-card {
        padding: 50px;
        box-shadow: 0px 12px 25px rgba(0, 0, 0, 0.15);
        border: none;
    }

    .hover-highlight:hover {
        border: 3px solid #1c64f2;
        box-shadow: 0px 10px 25px rgba(28, 100, 242, 0.3);
        transform: scale(1.03);
        transition: all 0.3s ease-in-out;
    }

    .custom-card h3 {
        font-size: 1.5rem;
        font-weight: bold;
        margin-bottom: 20px;
        color: #222;
        text-align: center;
    }

    .custom-card-content {
        display: flex;
        flex-direction: column;
        align-items: center;
        justify-content: center;
        width: 100%;
    }

    .plan-title {
        font-size: 1.8rem;
        font-weight: bold;
        color: #1c64f2;
    }

    .subtitle {
        font-size: 1.2rem;
        color: #495057;
    }

    .highlight {
        background-color: #fce38a;
        padding: 5px 10px;
        border-radius: 6px;
        font-weight: bold;
    }
    
    .custom-button {
        padding: 14px 20px;
        border-radius: 10px;
        font-size: 1rem;
        cursor: pointer;
        transition: background 0.3s, transform 0.2s;
        margin-top: 20px;
    }

    .primary-button {
        background-color: #1c64f2;
        color: white;
        border: none;
    }

    .primary-button:hover {
        background-color: #1558c1;
        transform: translateY(-3px);
    }
    /* 전체 레이아웃 설정 */
.mypage {
  width: 80%;
  max-width: 900px;
  margin: 0 auto;
  font-family: 'Noto Sans KR', sans-serif;
  color: #333;
}

.page-title {
  font-size: 24px;
  font-weight: bold;
  margin-bottom: 20px;
}

.tabs {
  display: flex;
  border-bottom: 2px solid #ddd;
}

.tab-item {
  flex: 1;
  text-align: center;
  padding: 10px 0;
  cursor: pointer;
  font-size: 16px;
  border: none;
  background: none;
  font-weight: 500;
}

.tab-item.active {
  color: #007aff;
  border-bottom: 2px solid #007aff;
}

.divider {
  margin: 20px 0;
  border: none;
  border-top: 1px solid #ddd;
}

/* 기본 정보 카드 스타일 */
.info-card {
  background: #fff;
  padding: 20px;
  border-radius: 8px;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
}

.info-text p {
  font-size: 14px;
  margin: 5px 0;
}

.nickname-section {
  display: flex;
  align-items: center;
  font-size: 18px;
  font-weight: bold;
}

.edit-icon {
  margin-left: 10px;
  cursor: pointer;
  font-size: 20px;
  color: #007aff;
}

.password-change-button {
  background: none;
  border: none;
  color: #007aff;
  cursor: pointer;
}

.horizontal-info {
  display: flex;
  justify-content: space-between; /* 가로로 간격 조절 */
  width: 40%; /* 가로로 넓이 꽉 채우기 */
  margin-top: 1rem;
  margin-bottom: 1rem;
}

.field {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin: 10px 0;
  padding-bottom: 10px;
  border-bottom: 1px solid #eee;
  width: 100%;
  max-width: 600px;
}
.field label {
  white-space: nowrap; /* 줄바꿈 방지 */
  min-width: 150px; /* 라벨 길이 조정 */
}

.info-value {
  font-size: 14px;
  color: #555;
}

/* 버튼 스타일 */
.delete-account-btn {
  display: block;
  width: 100%;
  background: #ff3b30;
  color: white;
  padding: 10px;
  text-align: center;
  border-radius: 5px;
  margin-top: 20px;
  border: none;
  cursor: pointer;
}

dialog {
  position: fixed;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  background: white;
  padding: 20px;
  border-radius: 8px;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
}
  </style>
  