<template>
  <div class="home-page">
    <Header />

    <div class="container">
      <section class="recent-designs">
        <h2 class="section-title">기존 디자인 불러오기</h2>
        <div class="designs-grid">
          <div v-for="(design, index) in recentDesigns" 
               :key="index" 
               class="design-card">
            <div class="design-image">
              <img :src="design.image" :alt="design.title">
            </div>
            <div class="design-content">
              <h3>{{ design.title }}</h3>
              <p>{{ design.description }}</p>
            </div>
          </div>
        </div>
      </section>

      <section class="new-design">
        <h2 class="section-title">새로운 디자인 만들기</h2>
        <div class="design-options">
          <div class="option-card" @click="goToPromptGen">
            <div class="icon">
              <span class="material-icons-round">description</span>
            </div>
            <h3>맞춤 광고 만들기</h3>
            <p>상품 정보부터 타겟 광고 설정까지<br>모든 기능을 한번에 사용하여 광고를 만들어 보세요.</p>
          </div>
          
          <div class="option-card" @click="goToTxt2VidGenerator">
            <div class="icon">
              <span class="material-icons-round">edit_note</span>
            </div>
            <h3>프롬프트로 광고 만들기</h3>
            <p>자신만의 광고 스토리로<br>광고를 만들어 보세요.</p>
          </div>
          
          <div class="option-card">
            <div class="icon">
              <span class="material-icons-round">image</span>
            </div>
            <h3>이미지로 광고 만들기</h3>
            <p>자신의 상품을 통해 나만의<br>광고를 만들어 보세요.</p>
          </div>
          
          <div class="option-card">
            <div class="icon">
              <span class="material-icons-round">style</span>
            </div>
            <h3>광고 스토리 만들기</h3>
            <p>몇 가지 키워드를 통해<br>나만의 광고 스토리를 만들어 보세요.</p>
          </div>
        </div>
      </section>
      <div class="help-links">
      <p class="help-text">저희 서비스가 처음이신가요?</p>
      <div class="link-group">
        <router-link to="/board#chatbot-section" class="help-link">챗봇 바로가기</router-link>
        <router-link to="/board#faq-section" class="help-link">FAQ 바로가기</router-link>
      </div>
    </div>
    </div>
  </div>
</template>

<script>
import Header from './components/Header.vue'

export default {
  name: "HomePage",
  components: {
    Header
  },
  data() {
    return {
      recentDesigns: [
        {
          title: "Rover raised $65 mil",
          description: "Finding temporary housing for your dog",
          image: "../../../assets/img/examples/testimonial-6-2.jpg"
        },
        {
          title: "MateLabs machine learning",
          description: "If you've ever wanted to train a machine learning",
          image: "../../../assets/img/examples/testimonial-6-3.jpg"
        }
        // 추가 디자인 카드들...
      ]
    }
  },
  methods: {
    scrollToSection(sectionId) {
      const element = document.getElementById(sectionId);
      if (element) {
        element.scrollIntoView({ behavior: 'smooth' });
      }
    },
    goToPromptGen() {
      // 새 탭에서 Flask 서버의 프롬프트 생성 페이지 열기
      this.$router.push('/prompt-generator');
    },
    goToTxt2VidGenerator() {
      // 새 탭에서 Flask 서버의 비디오 생성 페이지 열기
      this.$router.push('/txt2vid-generator');
    },
  }
}
</script>

<style scoped>
.home-page {
  padding-top: 70px;
  background-color: #f8f9fa;
}

.container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 2rem;
  padding-top: 4rem;
}

.section-title {
  font-size: 1.2rem;
  color: #344767;
  margin-bottom: 1.5rem;
  padding: 0.5rem;
  background-color: #e8f3f0;
}

.design-options {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 1.5rem;
  margin-top: 2rem;
}

.option-card {
  background: white;
  padding: 2rem 1.5rem;
  border-radius: 0.5rem;
  text-align: center;
  transition: transform 0.3s ease;
  cursor: pointer;
}

.option-card:hover {
  transform: translateY(-5px);
}

.icon {
  margin-bottom: 1rem;
}

.icon .material-icons-round {
  font-size: 2rem;
  color: #5CB494;
}

.option-card h3 {
  font-size: 1.1rem;
  color: #344767;
  margin-bottom: 0.5rem;
}

.option-card p {
  font-size: 0.9rem;
  color: #67748e;
  line-height: 1.5;
}

.recent-designs {
  margin-bottom: 3rem;
}

.designs-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(250px, 1fr));
  gap: 1.5rem;
}

.design-card {
  background: white;
  border-radius: 0.5rem;
  overflow: hidden;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.08);
  transition: transform 0.3s ease;
  cursor: pointer;
}

.design-card:hover {
  transform: translateY(-5px);
}

.design-image {
  width: 100%;
  height: 160px;
  overflow: hidden;
}

.design-image img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.design-content {
  padding: 1rem;
}

.design-content h3 {
  font-size: 1rem;
  color: #344767;
  margin-bottom: 0.5rem;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.design-content p {
  font-size: 0.875rem;
  color: #67748e;
  line-height: 1.5;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.designs-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(250px, 1fr));
  gap: 1.5rem;
  margin-top: 1rem;
}

.help-links {
  margin-top: 3rem;
  text-align: center;
  padding: 1.5rem;
  background-color: #fff;
  border-radius: 0.75rem;
  box-shadow: 0 4px 6px -1px rgba(0, 0, 0, 0.1);
}

.help-text {
  color: #344767;
  font-size: 1rem;
  margin-bottom: 1rem;
  font-weight: 500;
}

.link-group {
  display: flex;
  justify-content: center;
  gap: 1.5rem;
}

.help-link {
  display: inline-flex;
  align-items: center;
  padding: 0.5rem 1rem;
  font-size: 0.875rem;
  font-weight: 500;
  color: #344767;
  background-color: #fff;
  border: 1px solid #e9ecef;
  border-radius: 0.5rem;
  transition: all 0.15s ease-in;
  text-decoration: none;
}

.help-link:hover {
  color: #fff;
  background-color: #5CB494;
  border-color: #5CB494;
  transform: translateY(-1px);
  box-shadow: 0 3px 5px -1px rgba(92, 180, 148, 0.2);
}
</style>
