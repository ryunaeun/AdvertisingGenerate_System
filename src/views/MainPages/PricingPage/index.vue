<template>
  <div class="pricing-page">
    <Header />
    <div class="container py-5" style="margin-top: 80px">
      <nav aria-label="breadcrumb">
        <ol class="breadcrumb bg-transparent mb-4">
          <li class="breadcrumb-item">
            <router-link to="/home" class="text-dark">
              <i class="material-icons-round">home</i>
            </router-link>
          </li>
          <li class="breadcrumb-item active" aria-current="page">요금제</li>
        </ol>
      </nav>

      <!-- Pricing Section -->
      <div class="pricing-container">
        <!-- Basic Plan -->
        <div class="plan-card">
          <div class="card-body">
            <h5 class="card-title">Basic Plan</h5>
            <p class="card-subtitle text-muted">소규모 기업에 최적화된 플랜</p>
            <span class="price">$399</span>
            <ul class="list-unstyled">
              <li>
                <img
                  src="@/assets/img/check.png"
                  alt="check"
                  class="check-icon"
                />
                무제한 업데이트 제공
              </li>
              <li>
                <img
                  src="@/assets/img/check.png"
                  alt="check"
                  class="check-icon"
                />
                30일 환불 보장
              </li>
              <li>
                <img
                  src="@/assets/img/check.png"
                  alt="check"
                  class="check-icon"
                />
                온라인 지원 서비스
              </li>
              <li>
                <img
                  src="@/assets/img/check.png"
                  alt="check"
                  class="check-icon"
                />
                전담 지원팀의 맞춤형 지원
              </li>
            </ul>
            <button class="btn btn-primary action-button">연간 구독</button>
          </div>
        </div>
        <!-- Enterprise Plan -->
        <div class="plan-card">
          <div class="card-body">
            <h5 class="card-title">Enterprise Plan</h5>
            <p class="card-subtitle text-muted">
              대규모 기업 및 고급 사용자를 위한 프리미엄 플랜
            </p>
            <span class="price">영업팀 문의</span>
            <ul class="list-unstyled">
              <li>
                <img
                  src="@/assets/img/check.png"
                  alt="check"
                  class="check-icon"
                />
                맞춤형 초기 세팅 컨설팅 포함
              </li>
              <li>
                <img
                  src="@/assets/img/check.png"
                  alt="check"
                  class="check-icon"
                />
                비즈니스 맞춤형 플러그인 제공
              </li>
              <li>
                <img
                  src="@/assets/img/check.png"
                  alt="check"
                  class="check-icon"
                />
                전문가와 1:1 컨설팅 (최대 3회)
              </li>
              <li>
                <img
                  src="@/assets/img/check.png"
                  alt="check"
                  class="check-icon"
                />
                성능 분석 리포트 및 최적화 가이드 제공
              </li>
            </ul>
            <button class="btn btn-success action-button">
              기업 도입 문의
            </button>
          </div>
        </div>
      </div>

      <!-- Blog Section -->
      <div class="blog-container" style="background-color: #f8f9fa">
        <div class="blog-content">
          <div class="blog-card" v-for="(blog, index) in blogs" :key="index">
            <div
              class="blog-image"
              :style="{ backgroundImage: `url(${blog.image})` }"
            ></div>
            <h5 class="blog-title">{{ blog.title }}</h5>
            <p class="blog-description">{{ blog.description }}</p>
            <div class="blog-footer">
              <span class="read-more">Read More</span>
              <i class="bi bi-arrow-right-circle"></i>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import Header from "../HomePage/components/Header.vue";

export default {
  name: "PricingPage",
  components: {
    Header,
  },
  data() {
    return {
      blogs: [
        {
          title: "Rover raised $65 million",
          description:
            "Finding temporary housing for your dog should be as easy as renting an Airbnb.",
          image: "./src/assets/img/ai.jpeg",
        },
        {
          title: "MateLabs machine learning",
          description:
            "If you’ve ever wanted to train a machine learning model and integrate it with IFTTT, you now can.",
          image: "./src/assets/img/ai.jpeg",
        },
        {
          title: "MateLabs machine learning",
          description:
            "If you’ve ever wanted to train a machine learning model and integrate it with IFTTT, you now can.",
          image: "./src/assets/img/ai.jpeg",
        },
      ],
    };
  },
};
</script>

<style scoped>
/* 기존 스타일 유지 */
.pricing-page {
  background-color: #f8f9fa;
  min-height: 100vh;
}

.pricing-container {
  width: 100%;
  background-color: #f0f2f5;
  border-radius: 12px;
  padding: 50px 40px;
}

.plan-card {
  margin-bottom: 20px;
  background-color: #f8f9fa;
  border-radius: 12px;
  padding: 20px;
}

.card-body {
  position: relative;
}

.card-title {
  font-family: "Roboto Slab", sans-serif;
  font-size: 30px;
  font-weight: 700;
  color: #344767;
}

.card-subtitle {
  font-size: 16px;
  color: #7b809a;
}

.price {
  font-family: "Roboto Slab", sans-serif;
  font-size: 41px;
  font-weight: 700;
  color: #344767;
  position: absolute;
  right: 10%;
  top: 10%;
}

.list-unstyled {
  margin-top: 20px;
}

.check-icon {
  width: 16px;
  height: 16px;
  vertical-align: middle;
  margin-right: 8px;
}

.action-button {
  position: absolute;
  right: 10%;
  bottom: 10%;
  padding: 10px 20px;
  font-size: 16px;
  border: none;
  border-radius: 9px;
}

.btn-primary.action-button {
  background-color: #40a681; /* 원래 기본 연간 구독 버튼 색상 */
  color: #ffffff;
}

.btn-success.action-button {
  background-color: #40a681; /* 원래 기본 기업 도입 문의 버튼 색상 */
  color: #ffffff;
}

.blog-container {
  margin-top: 60px;
  padding: 40px;
  background-color: #f0f2f5;
  border-radius: 12px;
}

.blog-header {
  font-family: "Roboto Slab", sans-serif;
  font-size: 24px;
  font-weight: 700;
  color: #344767;
  text-align: center;
  margin-bottom: 20px;
}

.blog-content {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(300px, 1fr));
  gap: 20px;
}

.blog-card {
  background-color: #ffffff;
  border-radius: 12px;
  padding: 20px;
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
}

.blog-image {
  width: 100%;
  height: 150px;
  background-size: cover;
  background-position: center;
  border-radius: 12px;
  margin-bottom: 15px;
}

.blog-title {
  font-family: "Roboto Slab", sans-serif;
  font-size: 18px;
  font-weight: 700;
  color: #344767;
  margin-bottom: 10px;
}

.blog-description {
  font-size: 14px;
  color: #7b809a;
  margin-bottom: 15px;
}

.blog-footer {
  display: flex;
  align-items: center;
  gap: 8px;
}

.read-more {
  font-size: 14px;
  color: #40a681;
  cursor: pointer;
}

.bi-arrow-right-circle {
  color: #40a681;
  font-size: 16px;
}

@media (max-width: 768px) {
  .price {
    position: relative;
    right: 0;
    top: 0;
    margin-bottom: 15px;
  }

  .action-button {
    position: relative;
    right: 0;
    bottom: 0;
    margin-top: 15px;
  }

  .plan-card {
    text-align: center;
  }
}
</style>
