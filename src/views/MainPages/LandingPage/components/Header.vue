<template>
    <nav class="navbar navbar-expand-lg position-sticky z-index-sticky top-0">
      <div class="container px-0">
        <router-link class="navbar-brand font-weight-bolder ms-sm-3" to="/">
          Material Kit 2 PRO
        </router-link>
        <div class="collapse navbar-collapse" id="navigation">
          <ul class="navbar-nav me-auto">
            <li class="nav-item">
              <router-link class="nav-link" to="/service">
                서비스 소개
              </router-link>
            </li>
            <li class="nav-item">
              <router-link class="nav-link" to="/ai">
                AI 기능
              </router-link>
            </li>
            <li class="nav-item">
              <router-link class="nav-link" to="/pricing">
                요금제
              </router-link>
            </li>
            <li class="nav-item">
              <router-link class="nav-link" to="/gallery">
                예시 갤러리
              </router-link>
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
        isNavOpen: false
      };
    }
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
  }
  
  .nav-link:hover {
    opacity: 1;
  }
  
  .custom-btn-primary {
    background-color: white !important;
    color: #7B809A !important;
    border: none;
    padding: 0.4rem 1.2rem;
    border-radius: 5px;
    font-size: 0.8rem;
    font-weight: 600;
  }
  
  .custom-btn-success {
    background-color: #40A681 !important;
    color: white !important;
    border: none;
    padding: 0.4rem 1.2rem;
    border-radius: 5px;
    font-size: 0.8rem;
    font-weight: 600;
  }
  
  .custom-btn-primary:hover, .custom-btn-success:hover {
    opacity: 0.9;
  }
  
  .navbar-nav {
    align-items: center;
  }
  
  .nav-item {
    margin: 0 0.2rem;
  }
  </style>
  