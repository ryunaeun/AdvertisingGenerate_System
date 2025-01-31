<script setup>
import { onMounted } from 'vue';
import Aos from 'aos';

onMounted(() => {
  Aos.init({
    offset: 120,
    duration: 600,
    easing: 'ease-in-out',
    once: true,
  });
});
</script>

<template>
  <div>
    <Header />
    <div class="landing-page">
      <!-- Hero Section -->
      <section class="page-section hero-section">
        <div class="hero-overlay">
          <div class="hero-content">
            <h1 class="hero-title display-4 fw-bold animate fade-in">
              광고의 모든 것<br />
              여기에서 쉽고 빠르게
            </h1>
            <button 
              class="btn btn-primary mt-3 animate fade-in"
              @click="goToLogin"
            >
              무료로 시작하기
            </button>
          </div>
        </div>
      </section>

      <!-- Description Section -->
      <section class="page-section description-section">
        <div data-aos="fade-up" class="content text-center">
          <h2 class="description-title animate slide-up">
            내 모든 광고를 한번에 만들고 한번에 확인하세요.
          </h2>
          <p class="description-text mt-3 animate slide-up">
            이제껏 경험 못했던 쉽고 편리한 광고 서비스,
          </p>
          <p class="description-text mt-3 animate slide-up">
            OO와 함께 새로운 일상을 설계하세요.
          </p>
        </div>
      </section>

      <!-- 서비스 소개 Section -->
      <section section id="service-section" class="page-section service-section">
        <div data-aos="fade-up" class="content text-center">
          <!-- 큰 문구 -->
          <h2 class="section-title display-4 fw-bold">
            한 번의 클릭으로 수십 개의<br />
            광고를 제작해보세요
          </h2>
          <p class="section-text mt-3">
            원클릭 광고 스토리 생성 · 남녀노소 맞춤형 광고  · 광고 송출 플랫폼 분석
          </p>

          <!-- 동영상 삽입 -->
          <div class="video-container mt-5">
            <video
              controls
              class="service-video"
              :src="videoUrl"
              alt="서비스 소개 동영상"
            >
              동영상을 재생할 수 없는 경우, 브라우저를 확인해주세요.
            </video>
          </div>
        </div>
      </section>

      <section id="ai-feature-section" class="page-section ai-feature-section">
        <div class="content">
          <h2 data-aos="fade-up" class="section-title">AI가 광고를 바꾸는 방법</h2>
          <p data-aos="fade-up" class="section-text">
            딥러닝과 자연어 처리 기술로 광고 제작을 자동화하고, 사용자 행동 분석을 기반으로 맞춤형 캠페인을 추천합니다.
          </p>

          <!-- Image and Text Layout -->
          <div class="feature-container">
            <div v-for="(feature, index) in ai_features" :key="index" data-aos="fade-up" class="feature-item" :class="{ 'reverse': index % 2 !== 0 }">
              <div class="feature-image">
                <img :src="feature.image" :alt="feature.alt" />
              </div>
              <div class="feature-text">
                <h3>{{ feature.title }}</h3>
                <p>{{ feature.description }}</p>
              </div>
            </div>
          </div>
        </div>
      </section>

      <!-- 요금제 미리보기 Section -->
      <section id="pricing-section" class="page-section pricing-section">
        <div data-aos="fade-up" class="content text-center">
          <h2 class="section-title">요금제 미리보기</h2>
          <div class="pricing-cards">
            <div
              class="pricing-card"
              v-for="plan in pricingPlans"
              :key="plan.id"
            >
              <h3>{{ plan.name }}</h3>
              <p class="price">{{ plan.price }}</p>
              <ul>
                <li v-for="feature in plan.features" :key="feature">
                  {{ feature }}
                </li>
              </ul>
              <!-- 버튼 클래스명 변경 -->
              <button
                class="action-button action-button-outline-primary"
                @click="navigateToPricing"
              >
                시작하기
              </button>
            </div>
          </div>
        </div>
      </section>

      <!-- 예시 갤러리 Section -->
      <section id="gallery-section" class="page-section gallery-section">
        <div data-aos="fade-up" class="content text-center">
          <h2 class="section-title">예시 갤러리</h2>
          <p class="section-text">
            우리 서비스로 제작된 멋진 광고 캠페인을 확인해보세요.
          </p>
          <div class="gallery-container">
            <div class="gallery-item" v-for="image in galleryImages" :key="image.id">
              <img :src="image.url" :alt="image.alt" />
            </div>
          </div>
        </div>
      </section>

      <!-- Executive Team Section -->
      <section class="page-section team-section">
        <div data-aos="fade-up" class="executive-team">
          <h1>The Executive Team</h1>
          <p class="description">AI개발자 대구/경북 8반 22조</p>
          <div class="team-container">
            <div v-for="member in team" :key="member.id" class="team-card">
              <img :src="member.image" :alt="member.name" class="team-photo" />
              <div class="team-info">
                <h3>{{ member.name }}</h3>
                <p class="role">{{ member.role }}</p>
              </div>
            </div>
          </div>
        </div>
      </section>

      <!-- Footer Section -->
      <footer class="footer-section py-4 bg-dark text-white">
        <div class="container text-center">
          <p>&copy; 2025 광고 플랫폼. 모든 권리 보유.</p>
        </div>
      </footer>
    </div>
  </div>
</template>

<script>
import Header from './components/Header.vue';

export default {
  name: "LandingPage",
  components: {
    Header
  },
  methods: {
    scrollToSection(sectionId) {
      const target = document.getElementById(sectionId);
      if (target) {
        target.scrollIntoView({ behavior: 'smooth' }); // 부드럽게 스크롤
      }
    },
    goToLogin() {
      this.$router.push('/login'); // 로그인 페이지로 이동
    },
    navigateToPricing() {
      this.$router.push('/pricing'); // Vue Router 사용 시
      // 또는 location.href = '/pricing'; // Vue Router를 사용하지 않을 경우
    }
  },
  data() {
    return {
      ai_features: [
        {
          image: '/images/ai_feature1.png',
          alt: 'Feature 1',
          title: '원하는 광고 타겟과 스토리를 간편하게',
          description: '원하는 광고 노출층을 키워드로 간편하게 선택합니다. 선택된 키워드를 바탕으로 광고의 스토리를 AI로 작성해줍니다.',
        },
        {
          image: '/images/ai_feature2.png',
          alt: 'Feature 2',
          title: '만들고 싶은 광고 스타일을 미리',
          description: '광고의 초안을 썸네일로 확인해보세요. 추천 이미지 중 마음에 드시는 썸네일로 영상 제작을 시작합니다.',
        },
        {
          image: '/images/ai_feature3.png',
          alt: 'Feature 3',
          title: '내 스타일대로 광고 수정',
          description: 'AI로 만들어진 광고 스토리에 나만의 스토리를 추가해보세요.',
        },
        {
          image: '/images/ai_feature4.png',
          alt: 'Feature 4',
          title: '광고 제작',
          description: 'AI가 당신의 스토리로 광고를 만들어 드립니다.',
        },
      ],
      videoUrl: '/video/service_mp4.mp4',
      features: [
        { id: 1, title: "효율적인 광고 제작", description: "최소 시간으로 최대 효과를 얻으세요." },
        { id: 2, title: "실시간 데이터 분석", description: "성과를 실시간으로 확인하고 최적화하세요." },
      ],
      pricingPlans: [
        {
          id: 1,
          name: "Basic Plan",
          price: "$399/월",
          features: ["기본 광고 제작", "제한된 분석"],
        },
        {
          id: 2,
          name: "Enterprise Plan",
          price: "영업팀 문의",
          features: ["모든 기능 사용 가능", "우선 지원"],
        },
      ],
      galleryImages: [
        { id: 1, url: "/video/example1.gif", alt: "Gallery Image 1" },
        { id: 2, url: "/video/example2.gif", alt: "Gallery Image 2" },
        { id: 3, url: "/video/example3.gif", alt: "Gallery Image 3" },
      ],
      team: [
        {
          id: 1,
          name: "강성현",
          role: "백엔드",
          image: '/images/Profile.png',
        },
        {
          id: 2,
          name: "김유중",
          role: "프론트엔드",
          image: '/images/ujoong.jpg',
        },
        {
          id: 3,
          name: "류나은",
          role: "프론트엔드",
          image: '/images/Profile.png',
        },
        {
          id: 4,
          name: "박정석",
          role: "백엔드",
          image: '/images/Profile.png',
        },
        {
          id: 5,
          name: "변재연",
          role: "프론트엔드",
          image: '/images/jaeyeon.jpeg',
        },
        {
          id: 6,
          name: "서정호",
          role: "AI개발",
          image: '/images/Profile.png',
        },
      ],
    };
  },
};
</script>

<style>
.landing-page {
  overflow: auto;
}

.page-section {
  height: 100vh;
  display: flex;
  justify-content: center;
  align-items: center;
  text-align: center;
  padding: 20px;
}

.hero-section {
  background: url('../../../assets/img/illustrations/LandingPage_gif2.gif') no-repeat center center/cover;
  position: relative;
}

.hero-title {
  font-family: 'Arial', sans-serif; /* Hero section 제목의 글꼴 */
  font-size: 3rem; /* Hero section 제목의 글자 크기 */
  font-weight: bold; /* Hero section 제목의 글자 굵기 */
  color: black; /* Hero section 제목의 글자 색상 */
  text-shadow: none; /* 텍스트 그림자 제거 */
  margin-bottom: 20px; /* 제목 아래 여백 */
}

/* Hero Section Button Styles */
.hero-section .btn-primary {
  background-color: black; /* 버튼 배경색 (핑크색) */
  border: none; /* 버튼 테두리 제거 */
  color: white; /* 버튼 텍스트 색상 */
  font-size: 1.25rem; /* 버튼 텍스트 크기 */
  padding: 10px 20px; /* 버튼 내부 여백 */
  border-radius: 8px; /* 버튼 테두리 둥글게 */
  box-shadow: none; /* 버튼 그림자 제거 */
}

.hero-section .btn-primary:hover {
  background-color: black;
  box-shadow: none; 
}

.hero-section .btn-primary:active {
  background-color: black;
  box-shadow: none; 
}

.hero-section .btn-primary:focus {
  outline: none; /* 버튼 포커스 효과 제거 */
}

.description-section {
  background-color: #f8f9fa;
  color: black;
}

.description-title {
  font-family: 'Arial', sans-serif;
  font-size: 3rem;
  font-weight: bold;
  color: black;
}

.description-text {
  font-family: 'Arial', sans-serif;
  font-size: 3rem;
  font-weight: bold;
  color: black;
}

.service-section {
  padding: 60px 20px;
  text-align: center;
  background-color: #f9f9f9;
}

.service-section .section-title {
  font-size: 2.5rem;
  line-height: 1.4;
  margin-bottom: 20px;
}

.service-section .section-text {
  font-size: 1.2rem;
  color: #666;
}

.video-container {
  max-width: 800px;
  margin: 0 auto;
}

.service-video {
  width: 100%;
  height: auto;
  border-radius: 10px;
  box-shadow: 0 4px 10px rgba(0, 0, 0, 0.1);
}
.ai-feature-section {
  padding: 4rem 2rem;
  background-color: #f9f9f9;
  min-height: 250vh; /* 섹션 높이를 늘려줍니다. */
}

.section-title {
  font-size: 2.5rem;
  margin-bottom: 1.5rem;
  text-align: center;
}

.section-text {
  font-size: 1.2rem;
  text-align: center;
  margin-bottom: 3rem;
}

.feature-container {
  display: flex;
  flex-direction: column;
  gap: 2rem;
  margin-top: 2rem; /* 내용 간 간격 추가 */
}

.feature-item {
  display: flex;
  align-items: center;
  gap: 2rem;
}

.feature-item.reverse {
  flex-direction: row-reverse;
}

.feature-image img {
  width: 100%;
  max-width: 400px;
  border-radius: 10px;
  box-shadow: 0 4px 10px rgba(0, 0, 0, 0.1);
}

.feature-text {
  max-width: 600px;
}

.feature-text h3 {
  font-size: 1.8rem;
  margin-bottom: 1rem;
}

.feature-text p {
  font-size: 1rem;
  line-height: 1.6;
  color: #555;
}

.team-container {
  display: flex;
  justify-content: center;
  flex-wrap: wrap;
  gap: 20px;
}

.team-card {
  background: #ffffff;
  color: #333333;
  border-radius: 10px;
  padding: 20px;
  width: 200px;
  box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
  text-align: left;
}

.team-photo {
  width: 100%;
  border-radius: 10px;
}

.team-info {
  margin-top: 15px;
}

.role {
  color: #0077ff;
  font-weight: bold;
  margin-bottom: 10px;
}

.statistics-section {
  background: #e9ecef;
}

.stat-title {
  font-family: 'Arial', sans-serif;
  font-size: 2rem;
  font-weight: bold;
}

.stat-text {
  font-family: 'Arial', sans-serif;
  font-size: 1rem;
}

.footer-section {
  background-color: #343a40;
  color: #fff;
}

.animate {
  opacity: 0;
  transform: translateY(20px);
  transition: all 0.5s ease-out;
}

.animate.fade-in {
  opacity: 1;
  transform: translateY(0);
}

.animate.slide-up {
  opacity: 1;
  transform: translateY(0);
}

.animate.zoom-in {
  opacity: 1;
  transform: scale(1);
}

.pricing-section {
  padding: 60px 20px;
  background-color: #f9f9f9;
}

.pricing-cards {
  display: flex;
  justify-content: center;
  flex-wrap: wrap;
  gap: 20px;
}

.pricing-card {
  background: #fff;
  border: 1px solid #ddd;
  border-radius: 8px;
  box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
  padding: 20px;
  width: 280px;
  text-align: center;
  transition: transform 0.3s, box-shadow 0.3s;
}

.pricing-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 8px 15px rgba(0, 0, 0, 0.15);
}

.pricing-card h3 {
  font-size: 1.5rem;
  color: #333;
  margin-bottom: 10px;
}

.pricing-card .price {
  font-size: 1.25rem;
  color: #5CB494;
  margin-bottom: 15px;
}

.pricing-card ul {
  list-style: none;
  padding: 0;
  margin-bottom: 20px;
}

.pricing-card ul li {
  font-size: 0.9rem;
  color: #555;
  margin: 5px 0;
}

/* 기존 btn -> action-button 으로 변경 */
.action-button {
  display: inline-block;
  padding: 10px 20px;
  font-size: 1rem;
  color: #5CB494;
  border: 2px solid #5CB494;
  border-radius: 5px;
  background-color: transparent;
  transition: background-color 0.3s, color 0.3s;
}

.action-button:hover {
  background-color: #5CB494;
  color: #fff;
}

.gallery-container {
  display: flex; /* 아이템을 가로로 정렬 */
  flex-wrap: wrap; /* 아이템이 화면에 맞게 줄바꿈되도록 설정 */
  justify-content: center; /* 아이템들을 중앙 정렬 */
  gap: 30px; /* 아이템 간격 조정 */
  padding: 20px;
}

.gallery-item {
  width: 25%; /* 각 아이템의 고정 너비 설정 */
  height: 25%; /* 각 아이템의 고정 높이 설정 */
  overflow: hidden; /* 이미지가 넘어가지 않도록 설정 */
  border-radius: 8px; /* 아이템에 부드러운 모서리 추가 */
  box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1); /* 가벼운 그림자 추가 */
  transition: transform 0.3s, box-shadow 0.3s; /* 호버 효과 */
}

.gallery-item img {
  width: 100%; /* 이미지가 부모 요소에 맞게 조정 */
  height: 100%; /* 이미지가 부모 요소 높이에 맞게 조정 */
  object-fit: cover; /* 이미지가 비율을 유지하며 영역에 맞게 조정 */
}

.gallery-item:hover {
  transform: scale(1.05); /* 호버 시 확대 효과 */
  box-shadow: 0 8px 15px rgba(0, 0, 0, 0.15); /* 호버 시 그림자 강조 */
}

</style>
