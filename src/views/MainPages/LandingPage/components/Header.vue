<template>
  <nav class="navbar navbar-expand-lg position-sticky z-index-sticky top-0">
    <div class="container px-0">
      <router-link class="navbar-brand font-weight-bolder ms-sm-3" to="/">
        Material Kit 2 PRO
      </router-link>
      <div class="collapse navbar-collapse" id="navigation">
        <ul class="navbar-nav me-auto">
          <li class="nav-item">
            <a class="nav-link" @click="scrollToSection('service-section')">
              서비스 소개
            </a>
          </li>
          <li class="nav-item">
            <a class="nav-link" @click="scrollToSection('ai-feature-section')">
              AI 기능
            </a>
          </li>
          <li class="nav-item">
            <a class="nav-link" @click="scrollToSection('pricing-section')">
              요금제
            </a>
          </li>
          <li class="nav-item">
            <a class="nav-link" @click="scrollToSection('gallery-section')">
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
  methods: {
    scrollToSection(sectionId) {
      const target = document.getElementById(sectionId);
      if (target) {
        target.scrollIntoView({ behavior: "smooth" }); // 부드럽게 스크롤링
      }
    },
  },
};
</script>

<style scoped>
.navbar {
  padding: 0.5rem 2rem;
  height: 60px;
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
  cursor: pointer; /* 링크처럼 보이도록 포인터 추가 */
}

.nav-link:hover {
  opacity: 1;
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
