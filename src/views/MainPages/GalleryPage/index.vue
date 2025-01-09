<template>
    <div class="gallery-page">
      <Header />
      <div class="container py-5">
        <h1 class="text-center mb-4">Welcome to gallery Page</h1>
        <div class="card">
          <div class="card-body">
            <h5 class="card-title">갤러리 페이지</h5>
            <p class="card-text">이곳은 내 갤러리 페이지입니다.</p>
            <router-link to="/" class="btn bg-gradient-info">
              랜딩 페이지로 돌아가기
            </router-link>
            <router-link to="/presentation" class="btn bg-gradient-primary">
              예제 페이지 보기
            </router-link>
          </div>
        </div>
      </div>
    </div>
  </template>
  
  <script>
  import Header from '../HomePage/components/Header.vue'
  
  export default {
    name: "GalleryPage",
    components: {
      Header
    }
  }
  </script>
  
  <style scoped>
  .gallery-page {
    padding-top: 70px;
  }
  </style>
  