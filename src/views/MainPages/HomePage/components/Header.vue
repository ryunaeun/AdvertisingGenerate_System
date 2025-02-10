<template>
  <header class="header">
    <nav class="navbar">
      <router-link class="navbar-brand" to="/home">
        <img src="../../../../assets/img/logos/garo-logo.png" alt="AdVi Logo" height="30">
      </router-link>
      <div class="nav-items">
        <router-link to="/home" class="nav-item" :class="{ 'active': $route.path.includes('/home') }">
          <span>새로 만들기</span>
        </router-link>
        <router-link to="/gallery" class="nav-item" :class="{ 'active': $route.path.includes('/gallery') }">
          <span>내 갤러리</span>
        </router-link>
        <router-link to="/pricing" class="nav-item" :class="{ 'active': $route.path.includes('/pricing') }">
          <span>요금제</span>
        </router-link>
        <div class="dropdown">
          <div class="dropdown-trigger" @click="toggleDropdown">
            <router-link to="/board" class="nav-item" :class="{ 'active': $route.path.includes('/board') }">
              <span>게시판</span>
              <span class="material-icons-round" :class="{ 'rotate': isDropdownOpen }">expand_more</span>
            </router-link>
          </div>

          <div class="dropdown-menu show" v-if="isDropdownOpen">
            <span class="dropdown-item" @click="scrollToSection('my-inquiries-section')">내 문의함</span>
            <span class="dropdown-item" @click="scrollToSection('notice-section')">공지사항</span>
            <span class="dropdown-item" @click="scrollToSection('faq-section')">FAQ</span>
          </div>
        </div>
      </div>
      <div class="my-page">
        <button class="my-page-btn" @click="goToMyPage">
          <span class="material-icons-round">person</span>
          <span>MY PAGE</span>
        </button>
      </div>
      <button class="logout-btn" @click="logout">
          <span class="material-icons-round">exit_to_app</span>
          <span>로그아웃</span>
        </button>
    </nav>
  </header>
</template>

<script>
import axios from 'axios';
import apiClient from "@/api/axiosClient";

export default {
  name: 'Header',
  data() {
    return {
      isDropdownOpen: false
    };
  },
  methods: {
    toggleDropdown() {
      this.isDropdownOpen = !this.isDropdownOpen;
    },
    closeDropdown(e) {
      if (!e.target.closest('.dropdown')) {
        this.isDropdownOpen = false;
      }
    },
    scrollToSection(sectionId) {
      const section = document.getElementById(sectionId);
      if (section) {
        section.scrollIntoView({
          behavior: 'smooth',
          block: 'start'
        });
      }
      this.isDropdownOpen = false;
    },
    async logout() {
      try {
        await apiClient.post("/logout");
        sessionStorage.removeItem("accessToken");
        localStorage.removeItem("refreshToken");
        alert("로그아웃 성공");
        this.$router.push("/login");
      } catch (error) {
        console.error("로그아웃 실패:", error);
        alert("로그아웃 중 문제가 발생했습니다.");
      }
    },
    
    goToMyPage() {
      this.$router.push('/mypage'); // 마이페이지로 이동
    },
    async logout() {
      try {
        await apiClient.post("/logout");
        sessionStorage.removeItem("accessToken");
        localStorage.removeItem("refreshToken");
        alert("로그아웃 성공");
        this.$router.push("/login");
      } catch (error) {
        console.error("로그아웃 실패:", error);
        alert("로그아웃 중 문제가 발생했습니다.");
      }
    },
  },
  mounted() {
    document.addEventListener('click', this.closeDropdown);
  },
  beforeUnmount() {
    document.removeEventListener('click', this.closeDropdown);
  }
};
</script>

<style scoped>
.header {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  background-color: #f8f9fa;
  border-bottom: 1px solid #ddd;
  z-index: 1000;
}

.navbar {
  max-width: 100%;
  margin: 0;
  padding: 1rem 0;
  display: flex;
  justify-content: flex-start;
  align-items: center;
}

.logo a {
  text-decoration: none;
  color: #344767;
  font-weight: 700;
  font-size: 1.2rem;
}

.nav-items {
  display: flex;
  gap: 2.5rem;
  overflow: visible;
  margin-left: 4rem;
}

.nav-item {
  display: flex;
  align-items: center;
  gap: 0.5rem;
  text-decoration: none;
  color: #344767;
  font-size: 1rem;
  cursor: pointer;
}

.dropdown-menu {
  position: absolute;
  top: calc(100% + 5px);
  left: 50%;
  transform: translateX(-50%);
  min-width: 140px;
  background-color: #fff;
  border-radius: 0.5rem;
  box-shadow: 0 4px 20px rgba(0,0,0,0.15);
  padding: 0.7rem 0;
}

.dropdown-item {
  padding: 0.8rem 1.5rem;
  color: #344767;
  font-size: 1rem;
  text-align: center;
}

.dropdown-item:hover {
  background-color: #e9f5f2;
  color: #5CB494;
}

.my-page-btn {
  display: flex;
  align-items: center;
  gap: 0.5rem;
  padding: 0.6rem 1.8rem;
  border: 1px solid #5CB494;
  border-radius: 0.5rem;
  background-color: #fff;
  color: #5CB494;
  font-size: 1rem;
  transition: background-color 0.3s ease, color 0.3s ease;
}

.my-page-btn .material-icons-round {
  font-size: 1.2rem;
}

.my-page-btn:hover {
  background-color: #5CB494;
  color: #fff;
}

.nav-item.active, 
.nav-item:hover {
  color: #5CB494;
}

.logo {
  padding-left: 2rem;
}

.my-page {
  margin-left: auto;
  padding-right: 1.5rem;
}

.logout-btn {
  margin-right: 40px;
  padding: 8px 16px;
  background-color: #5CB494;
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  display: flex;
  align-items: center;
}

.logout-btn .material-icons-round {
  margin-right: 5px;
}

.logout-btn:hover {
  background-color: #344767;
}

.navbar-brand{
  padding-left: 50px;
}

.navbar {
  height: 80px; /* 헤더의 높이를 고정값으로 설정 */
  padding: 0; /* 패딩 제거 */
  padding-top: 5px;
}

.logo-image {
  height: 70px; /* 로고 이미지의 최대 높이 설정 */
  width: auto; /* 가로 비율 자동 조정 */
  object-fit: contain; /* 이미지 비율 유지 */
}

.navbar-brand {
  padding-left: 50px;
  margin-top: -7px;
}
</style>

