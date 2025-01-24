<template>
  <nav class="navbar navbar-expand-lg position-sticky z-index-sticky top-0">
    <div class="container px-0">
      <router-link class="navbar-brand" to="/">
        <img src="../../../../assets/img/logos/garo-logo.png" alt="AdVi Logo" height="45">
      </router-link>
      <div class="collapse navbar-collapse" id="navigation">
        <ul class="navbar-nav me-auto">
          <li class="nav-item">
            <a
              class="nav-link"
              :class="{'active': currentSection === 'service-section'}"
              @click="scrollToSection('service-section')"
            >
              서비스 소개
            </a>
          </li>
          <li class="nav-item">
            <a
              class="nav-link"
              :class="{'active': currentSection === 'ai-feature-section'}"
              @click="scrollToSection('ai-feature-section')"
            >
              AI 기능
            </a>
          </li>
          <li class="nav-item">
            <a
              class="nav-link"
              :class="{'active': currentSection === 'pricing-section'}"
              @click="scrollToSection('pricing-section')"
            >
              요금제
            </a>
          </li>
          <li class="nav-item">
            <a
              class="nav-link"
              :class="{'active': currentSection === 'gallery-section'}"
              @click="scrollToSection('gallery-section')"
            >
              예시 갤러리
            </a>
          </li>
        </ul>
        <div class="d-flex">
          <router-link to="/login" class="btn btn-sm custom-btn-primary mb-0 me-1">LOGIN</router-link>
          <router-link to="/register" class="btn btn-sm custom-btn-success mb-0">SIGN UP</router-link>
        </div>
      </div>
    </div>
  </nav>
</template>

<script>
export default {
  name: "NavBar",
  data() {
    return {
      currentSection: '',
    };
  },
  methods: {
    scrollToSection(sectionId) {
      const target = document.getElementById(sectionId);
      if (target) {
        target.scrollIntoView({ behavior: "smooth" });
      }
    },
    handleScroll() {
      const sections = ['service-section', 'ai-feature-section', 'pricing-section', 'gallery-section'];
      let currentSection = '';
      sections.forEach((section) => {
        const element = document.getElementById(section);
        if (element) {
          const rect = element.getBoundingClientRect();
          // 섹션이 화면에 보일 때 currentSection 값을 업데이트
          if (rect.top <= window.innerHeight / 2 && rect.bottom >= 0) {
            currentSection = section;
          }
        }
      });
      this.currentSection = currentSection;
    },
  },
  mounted() {
    window.addEventListener('scroll', this.handleScroll);
  },
  beforeDestroy() {
    window.removeEventListener('scroll', this.handleScroll);
  },
};
</script>

<style scoped>
.navbar {
  padding: 0.5rem 2rem;
  height: 60px;
  background-color: white;
  position: sticky;
  top: 0;
  z-index: 1000;
}

.navbar-brand {
  font-size: 1.2rem;
  font-weight: 600;
}

.nav-link {
  font-size: 0.9rem;
  padding: 0.5rem 1rem !important;
  opacity: 0.9;
  font-weight: 600;
  cursor: pointer;
  transition: opacity 0.3s ease, border-bottom 0.3s ease;
  position: relative;
}

.nav-link.active {
  opacity: 1;
  border-bottom: 2px solid #ff6347; /* 활성화된 링크 밑에 빨간 밑줄 추가 */
}

.nav-link:hover {
  opacity: 1;
  border-bottom: 2px solid #ff6347; /* 호버 시에도 밑줄을 추가하여 효과를 줌 */
}

.custom-btn-primary {
  background-color: white !important;
  color: #7b809a !important;
  border: none;
  padding: 0.4rem 1.2rem;
  border-radius: 5px;
  font-size: 0.8rem;
  font-weight: 600;
}

.custom-btn-success {
  background-color: #40a681 !important;
  color: white !important;
  border: none;
  padding: 0.4rem 1.2rem;
  border-radius: 5px;
  font-size: 0.8rem;
  font-weight: 600;
}

.custom-btn-primary:hover,
.custom-btn-success:hover {
  opacity: 0.9;
}

.navbar-nav {
  align-items: center;
}

.nav-item {
  margin: 0 0.2rem;
}
</style>
