<template>
    <header class="header">
      <nav class="navbar">
        <div class="logo">
          <router-link to="/">Material Kit 2 PRO</router-link>
        </div>
        <div class="nav-items">
          <span>새로 만들기</span>
          <router-link to="/gallery" class="nav-item">
            <span>내 갤러리</span>
          </router-link>
          <router-link to="/pricing" class="nav-item">
            <span>요금제</span>
          </router-link>
          <router-link to="/board" class="nav-item">
            <span>게시판</span>
            <div class="dropdown-icon">▼</div>
          </router-link>
        </div>
  
        <div class="my-page">
          <button class="my-page-btn">MY PAGE</button>
        </div>
      </nav>
    </header>
  </template>  
  
  <script>
  export default {
    name: 'Header'
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
  }
  
  .nav-item {
    display: flex;
    align-items: center;
    gap: 0.3rem;
    cursor: pointer;
    color: #344767;
    font-size: 0.9rem;
  }
  
  .dropdown-icon {
    font-size: 0.7rem;
    color: #666;
  }
  
  .my-page-btn {
    padding: 0.5rem 1rem;
    border: none;
    border-radius: 0.5rem;
    background-color: #fff;
    color: #344767;
    font-size: 0.9rem;
    cursor: pointer;
  }
  
  .my-page-btn:hover {
    background-color: #f8f9fa;
  }
  </style>
  