<template>
  <header class="header">
    <nav class="navbar">
      <div class="logo">
        <router-link to="/home">Material Kit 2 PRO</router-link>
      </div>
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
            <span class="dropdown-item" @click="scrollToSection('notice-section')">공지사항</span>
            <span class="dropdown-item" @click="scrollToSection('faq-section')">FAQ</span>
            <span class="dropdown-item" @click="scrollToSection('chatbot-section')">문의하기</span>
          </div>
        </div>
      </div>
      <div class="my-page">
        <button class="my-page-btn">MY PAGE</button>
      </div>
    </nav>
  </header>
</template>

<script>
export default {
  name: 'Header',
  data() {
    return {
      isDropdownOpen: false
    }
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
    // Method to scroll to the specified section
    scrollToSection(sectionId) {
      const section = document.getElementById(sectionId);
      if (section) {
        section.scrollIntoView({
          behavior: 'smooth',
          block: 'start'
        });
      }
      this.isDropdownOpen = false; // Close the dropdown after selection
    }
  },
  mounted() {
    document.addEventListener('click', this.closeDropdown);
  },
  beforeUnmount() {
    document.removeEventListener('click', this.closeDropdown);
  }
}
</script>

<style scoped>
.header {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  background-color: white;
  border-bottom: 1px solid #eee;
  z-index: 1000;
}

.navbar {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0.8rem 2rem;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.logo a {
  text-decoration: none;
  color: #344767;
  font-weight: 600;
  font-size: 1.1rem;
}

.nav-items {
  display: flex;
  gap: 2rem;
  overflow: visible;
}

.nav-item {
  display: flex;
  align-items: center;
  gap: 0.5rem;
  text-decoration: none;
  color: #344767;
  font-size: 0.9rem;
  cursor: pointer;
}

.nav-item .material-icons-round {
  font-size: 1.2rem;
  transition: transform 0.3s ease;
}

.dropdown {
  position: relative;
  z-index: 1001;
}

.dropdown-trigger {
  display: flex;
  align-items: center;
  gap: 0.5rem;
}

.dropdown-menu {
  position: absolute;
  top: calc(100% + 5px);
  left: 50%;
  transform: translateX(-50%);
  min-width: 120px;
  background-color: white;
  border-radius: 0.5rem;
  box-shadow: 0 2px 15px rgba(0,0,0,0.1);
  padding: 0.5rem 0;
  z-index: 1002;
  display: block;
}

.dropdown-item {
  display: block;
  padding: 0.7rem 1.5rem;
  color: #344767;
  text-decoration: none;
  font-size: 0.9rem;
  text-align: center;
  transition: all 0.3s ease;
}

.dropdown-item:hover {
  background-color: #f8f9fa;
  color: #5CB494;
}

.my-page-btn {
  padding: 0.5rem 1.5rem;
  border: none;
  border-radius: 0.5rem;
  background-color: #fff;
  color: #344767;
  font-size: 0.9rem;
  cursor: pointer;
  transition: background-color 0.3s ease;
}

.my-page-btn:hover {
  background-color: #f8f9fa;
}

.nav-item.active, 
.nav-item:hover {
  color: #5CB494;
}

.rotate {
  transform: rotate(180deg);
}

.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.3s ease;
}

.fade-enter-from,
.fade-leave-to {
  opacity: 0;
}
</style>
