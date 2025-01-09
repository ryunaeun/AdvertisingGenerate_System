<template>
    <div class="landing-page">
      <div class="container py-5">
        <h1 class="text-center mb-4">Landing Page</h1>
        <div class="card">
          <div class="card-body">
            <h5 class="card-title">랜딩 페이지</h5>
            <p class="card-text">이곳은 비로그인시에 가장 먼저뜨는 화면입니다.</p>
            <router-link to="/home" class="btn bg-gradient-info">
              홈(로그인후)으로 돌아가기
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
  export default {
    name: "LandingPage"
  }
  </script>
  