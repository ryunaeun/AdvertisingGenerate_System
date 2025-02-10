<template>
  <div class="gallery-page">
    <Header />
    <div class="container py-5">
      <!-- Path Settings -->
      <PathSettings @path-change="handlePathChange" />

      <!-- Breadcrumb and Section Header -->
      <div class="breadcrumb-container mb-4">
        <nav aria-label="breadcrumb">
          <ol class="breadcrumb">
            <li class="breadcrumb-item">
              <router-link to="/">홈</router-link>
            </li>
            <li class="breadcrumb-item active" aria-current="page">
              내 갤러리
            </li>
          </ol>
        </nav>
        <h2 class="section-title">내가 만든 영상</h2>
      </div>

      <!-- Search and Filter -->
      <div class="search-filter d-flex align-items-center justify-content-between mb-4">
        <div class="search-bar position-relative">
          <input
            type="text"
            class="form-control"
            placeholder="Search"
            v-model="searchQuery"
            @keyup.enter="handleSearch"
          />
          <i
            class="fas fa-search search-icon position-absolute"
            @click="handleSearch"
          ></i>
        </div>
        <div class="filters d-flex gap-3">
          <!-- 크기순 필터 버튼 -->
          <button
            class="btn btn-outline-secondary me-2 d-flex align-items-center"
            @click="toggleSizeSortOrder"
          >
            크기순
            <i
              class="ms-2"
              :class="sizeSortOrder === 'asc' ? 'fas fa-arrow-up' : 'fas fa-arrow-down'"
            ></i>
          </button>
          <!-- 날짜순 필터 버튼 -->
          <button
            class="btn btn-outline-secondary me-2 d-flex align-items-center"
            @click="toggleDateSortOrder"
          >
            날짜순
            <i
              class="ms-2"
              :class="dateSortOrder === 'desc' ? 'fas fa-arrow-down' : 'fas fa-arrow-up'"
            ></i>
          </button>
          <div class="dropdown">
            <button
              class="btn btn-outline-secondary dropdown-toggle"
              @click="togglePeriodDropdown"
            >
              {{ selectedPeriodLabel }}
            </button>
            <ul v-if="isPeriodDropdownOpen" class="dropdown-menu show">
              <li
                v-for="option in periodOptions"
                :key="option.value"
                class="dropdown-item"
                :class="{ 'selected': selectedPeriod === option.value }"
                @click="filterByPeriod(option.value, option.label)"
              >
                <span>
                  {{ option.label }}
                </span>
                <i v-if="selectedPeriod === option.value" class="fas fa-check text-primary ms-2"></i>
              </li>
            </ul>
          </div>
        </div>
      </div>

      <!-- Loading State -->
      <div v-if="loading" class="text-center my-5">
        <div class="spinner-border text-primary" role="status">
          <span class="visually-hidden">Loading...</span>
        </div>
        <p class="mt-2">갤러리를 불러오는 중...</p>
      </div>

      <!-- Error State -->
      <div v-else-if="error" class="alert alert-danger" role="alert">
        {{ error }}
      </div>

      <!-- Empty State -->
      <div v-else-if="filteredAndSortedItems.length === 0" class="text-center my-5">
        <p>표시할 영상이 없습니다.</p>
      </div>

      <!-- Gallery Grid -->
      <div v-else class="row">
        <div
          class="col-lg-3 col-md-4 col-sm-6 mb-4"
          v-for="(item, index) in filteredAndSortedItems"
          :key="index"
        >
          <div class="card">
            <div class="thumbnail-container">
              <img 
                :src="baseUrl + item.image" 
                class="card-img-top" 
                :alt="item.title"
                @error="handleImageError"
              />
              <div class="video-duration">{{ item.duration }}</div>
            </div>
            <div class="card-body">
              <h5 class="card-title">{{ item.title }}</h5>
              <p class="card-date text-muted">{{ item.createdAt }}</p>
              <div class="tags">
                <span class="badge" @click="handleShare(item.video_url)">
                  <i class="fas fa-share"></i> 공유하기
                </span>
                <span class="badge" @click="handleCheckPrompt(item.title)">
                  <i class="fas fa-check-circle"></i> 프롬프트 확인
                </span>
                <a 
                  :href="baseUrl + '/download/' + encodeURIComponent(userId + '/videos/' + item.filename)"
                  class="badge"
                  download
                >
                  <i class="fas fa-download"></i> 다운로드
                </a>
              </div>
            </div>
          </div>
        </div>
      </div>


      <!-- 공유 모달 -->
      <div v-if="isShareModalOpen" class="share-modal-overlay" @click.self="closeShareModal">
        <div class="share-modal">
          <h4>공유하기</h4>
          <div class="social-icons">
            <button class="social-btn" @click="shareVia('kakao')">
              <img src="../../../assets/img/logos/login/kakao.png" alt="KakaoTalk" />
            </button>
            <button class="social-btn" @click="shareVia('naver')">
              <img src="../../../assets/img/logos/login/naver.png" alt="Naver Mail" />
            </button>
            <button class="social-btn" @click="shareVia('gmail')">
              <img src="../../../assets/img/logos/login/google.png" alt="Gmail" />
            </button>
            <button class="social-btn" @click="shareVia('Facebook')">
              <img src="../../../assets/img/logos/login/facebook.png" alt="Facebook" />
            </button>
          </div>
          <div class="copy-url">
            <input
              type="text"
              class="form-control"
              readonly
              :value="currentShareUrl"
            />
            <button class="btn btn-primary" @click="copyToClipboard">
              URL 복사
            </button>
          </div>
          <button class="btn btn-secondary mt-3" @click="closeShareModal">
            닫기
          </button>
          </div>
        </div>
      </div>
      
    <footer class="footer">
      <div class="footer-container">
        <div class="footer-left">
          <h4>Material Design</h4>
          <ul class="footer-links">
            <li><a href="#">Home</a></li>
            <li><a href="#">About</a></li>
            <li><a href="#">Blog</a></li>
            <li><a href="#">Services</a></li>
          </ul>
          <p>Copyright © 2022 Material Design by Creative Tim.</p>
        </div>
        <div class="footer-right">
          <p>The reward for getting on the stage is fame. The price of fame is you can’t get off the stage.</p>
          <div class="social-icons">
            <a href="#"><i class="fas fa-globe"></i></a>
            <a href="#"><i class="fab fa-twitter"></i></a>
            <a href="#"><i class="fab fa-pinterest"></i></a>
            <a href="#"><i class="fab fa-github"></i></a>
          </div>
        </div>
      </div>
    </footer>
  </div>
</template>

<script>
import Header from "../HomePage/components/Header.vue";
import PathSettings from "./components/PathSettings.vue";
import axios from 'axios';

export default {
  name: "GalleryPage",
  components: {
    Header,
  },
  data() {
    return {
      baseUrl: 'http://125.181.20.252:8888', // 실제 서버 URL로 변경하세요
      isShareModalOpen: false, // 공유 모달 상태
      currentShareUrl: "", // 공유할 URL
      searchQuery: "",
      dateSortOrder: "desc", // 초기 정렬 순서: 최신순
      sizeSortOrder: null, // 초기 크기 정렬 순서: 정렬안함
      isPeriodDropdownOpen: false,
      selectedPeriod: "all", // 선택된 기간 (필터링 로직에서 사용)
      selectedPeriodLabel: "전체 기간", // 선택된 기간의 이름 (UI에 표시됨)
      galleryItems: [],
      userId: 'KTaivle',
      periodOptions: [
        { value: "all", label: "전체 기간" },
        { value: "today", label: "오늘" },
        { value: "week", label: "최근 1주" },
        { value: "month", label: "최근 1달" },
        { value: "year", label: "최근 1년" },
      ],
      loading: false,
      error: null
    
    };
  },
  computed: {
  // 필터링 및 정렬된 데이터
  filteredAndSortedItems() {
    const filtered = this.galleryItems.filter((item) => {
      const itemDate = new Date(item.createdAt);
      const now = new Date();

      // 검색어 필터링
      const matchesSearchQuery = this.searchQuery
        ? item.title.toLowerCase().includes(this.searchQuery.toLowerCase())
        : true;

      // 기간 필터링
      let matchesPeriod = true;
      if (this.selectedPeriod === "today") {
        matchesPeriod = itemDate.toDateString() === now.toDateString();
      } else if (this.selectedPeriod === "week") {
        const oneWeekAgo = new Date();
        oneWeekAgo.setDate(now.getDate() - 7);
        matchesPeriod = itemDate >= oneWeekAgo;
      } else if (this.selectedPeriod === "month") {
        const oneMonthAgo = new Date();
        oneMonthAgo.setMonth(now.getMonth() - 1);
        matchesPeriod = itemDate >= oneMonthAgo;
      } else if (this.selectedPeriod === "year") {
        const oneYearAgo = new Date();
        oneYearAgo.setFullYear(now.getFullYear() - 1);
        matchesPeriod = itemDate >= oneYearAgo;
      }

      return matchesSearchQuery && matchesPeriod;
    });
      
      // 정렬 기준 우선순위: 크기 → 날짜
      if (this.sizeSortOrder) {
        return filtered.sort((a, b) => {
          const timeA = this.convertToSeconds(a.duration);
          const timeB = this.convertToSeconds(b.duration);
          return this.sizeSortOrder === "asc" ? timeA - timeB : timeB - timeA;
        });
      }

      if (this.dateSortOrder) {
        return filtered.sort((a, b) => {
          const dateA = new Date(a.createdAt);
          const dateB = new Date(b.createdAt);
          return this.dateSortOrder === "desc" ? dateB - dateA : dateA - dateB;
        });
      }

      return filtered;
    },
  },
  methods: {
    handleImageError(e) {
      // 이미지 로드 실패시 기본 이미지로 대체
      e.target.src = '/static/default-thumbnail.png';
    },
    async fetchGalleryItems() {
      this.loading = true;
      this.error = null;
      try {
        const response = await axios.get(`${this.baseUrl}/gallery_check`, {
          params: { userId: this.userId }
        });
        
        if (response.data.success) {
          this.galleryItems = response.data.files;
          console.log('Fetched gallery items:', this.galleryItems);
        } else {
          throw new Error(response.data.error || '갤러리 데이터를 불러오는데 실패했습니다.');
        }
      } catch (error) {
        console.error('Error fetching gallery items:', error);
        this.error = error.response?.data?.error || error.message || '갤러리 아이템을 불러오는데 실패했습니다.';
      } finally {
        this.loading = false;
      }
    },
    handlePathChange(pathData) {
      this.userId = pathData.userId;
      this.fetchGalleryItems();
    },
    handleShare(videoUrl) {
      this.currentShareUrl = `${this.baseUrl}${videoUrl}`;
      this.isShareModalOpen = true;
    },
    closeShareModal() {
      this.isShareModalOpen = false;
    },
    copyToClipboard() {
      navigator.clipboard.writeText(this.currentShareUrl).then(() => {
        alert("URL이 복사되었습니다!");
      });
    },
    shareVia(platform) {
      alert(`${platform}로 공유하기 기능은 현재 준비 중입니다.`);
    },
    handleSearch() {
      console.log(`Searching for: ${this.searchQuery}`);
    },
    toggleDateSortOrder() {
      this.sizeSortOrder = null; // 크기 정렬 비활성화
      this.dateSortOrder = this.dateSortOrder === "desc" ? "asc" : "desc";
    },
    toggleSizeSortOrder() {
      this.dateSortOrder = null; // 날짜 정렬 비활성화
      this.sizeSortOrder = this.sizeSortOrder === "asc" ? "desc" : "asc";
    },
    convertToSeconds(duration) {
      const [minutes, seconds] = duration.split(":").map(Number);
      return minutes * 60 + seconds;
    },
    togglePeriodDropdown() {
      this.isPeriodDropdownOpen = !this.isPeriodDropdownOpen;
    },
    filterByPeriod(period, label) {
      this.selectedPeriod = period; // 필터링에 사용
      this.selectedPeriodLabel = label; // 버튼 텍스트 변경
      this.isPeriodDropdownOpen = false; // 드롭다운 닫기
    },
    handleCheckPrompt(title) {
      alert(`${title}의 프롬프트를 확인합니다.`);
    },
    // 드롭다운 외부 클릭 시 닫기
    handleClickOutside(event) {
      if (this.isPeriodDropdownOpen && !event.target.closest('.dropdown')) {
        this.isPeriodDropdownOpen = false;
      }
    }
  },
  mounted() {
    this.fetchGalleryItems();
    // 드롭다운 외부 클릭 이벤트 리스너 추가
    document.addEventListener('click', this.handleClickOutside);
  },
  beforeDestroy() {
    // 컴포넌트 제거 시 이벤트 리스너 제거
    document.removeEventListener('click', this.handleClickOutside);
  }
};
</script>

<style scoped>
.gallery-page {
  padding-top: 70px;
}

/* Breadcrumb Styles */
.breadcrumb-container {
  margin-bottom: 20px;
}

.breadcrumb {
  background: none;
  padding: 0;
  margin-bottom: 10px;
}

.breadcrumb-item a {
  text-decoration: none;
  color: #6c757d;
}

.breadcrumb-item.active {
  color: #000;
  font-weight: bold;
}

/* Section Title */
.section-title {
  font-size: 1.5rem;
  font-weight: bold;
}

/* Search and Filter */
.search-filter {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
}

.search-bar {
  flex: 1;
  max-width: 200px;
  position: relative;
}

.search-bar input {
  padding-right: 35px; /* Add space for the icon */
}

.search-icon {
  top: 50%;
  right: 10px;
  transform: translateY(-50%);
  font-size: 10px;
  color: #6c757d;
  cursor: pointer;
}

.search-icon:hover {
  color: #000;
}

/* Filters */
.filters {
  display: flex;
  gap: 5px; /* 간격을 5px로 좁힘 */
}

.filters .btn {
  font-size: 10px; /* 글자 크기 살짝 줄임 */
  padding: 5px 10px; /* 버튼의 내부 여백 줄임 */
  color: #7b809a;
  margin-right: 5px; /* 버튼 간의 간격 좁힘 */
}

.filters .btn:last-child {
  margin-right: 0; /* 마지막 버튼의 오른쪽 간격 제거 */
}

.filters .btn i {
  transition: transform 0.3s;
}

.dropdown-menu {
  display: block;
  position: absolute;
  background: white;
  border: 1px solid #ddd;
  border-radius: 4px;
  margin-top: 5px;
  z-index: 1000;
}

.dropdown-item {
  padding: 8px 12px; /* 드롭다운 아이템 크기 조정 */
  cursor: pointer;
}

.dropdown-item:hover {
  background-color: #f8f9fa;
}

.dropdown-toggle {
  position: relative;
}

.dropdown-menu.show {
  display: block;
}

.selected {
  color: #b81414;
  font-weight: bold;
}

.dropdown-item .fas {
  margin-left: 5px;
  color: #b81414;
}
/* Gallery Card */
.card {
  border: 1px solid #ddd;
  border-radius: 8px;
  overflow: hidden;
  height: auto; /* 카드의 높이를 동적으로 조정 */
}

.card-img-top {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  object-fit: cover; /* 이미지가 꽉 차게 유지 */
}

.card-body {
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  text-align: center; /* 정보 영역의 세로 길이 축소 */
  padding : 10px;
}

.card-title {
  font-size: 20px; /* 제목 크기 축소 */
  font-weight: bold;
  margin-bottom: 5px;
}

.card-date {
  font-size: 12px;
  margin-bottom: 8px;
  color: #6c757d;
}

.thumbnail-container {
  position: relative;
  width: 100%;
  padding-top : 100%; /* 16:9 비율을 유지 */
  overflow: hidden;
  border-radius: 8px 8px 0 0; /* 상단 모서리 둥글게 */
}
.video-duration {
  position: absolute;
  bottom: 8px;
  right: 8px;
  background: rgba(0, 0, 0, 0.7); /* 검은 배경 */
  color: white; /* 흰색 글씨 */
  font-size: 12px; /* 글씨 크기 */
  padding: 2px 5px;
  border-radius: 3px;
  font-weight: bold;
}

/* Badge Styles */
.tags .badge {
  display: inline-flex;
  align-items: center;
  background-color: #f8f9fa; /* 연한 회색 */
  color: #495057; /* 진한 회색 */
  border: 1px solid #dee2e6; /* 테두리 색상 */
  border-radius: 5px;
  padding: 2px 5px;
  font-size: 10px;
  margin-right: 5px;
  cursor: pointer;
}

.tags .badge:hover {
  background-color: #e9ecef;
}

.tags .badge i {
  margin-right: 2px; /* 아이콘과 텍스트 간격 */
  font-size: 10px;
  color: #6c757d; /* 아이콘 색상 */
}
/*footer*/ 
.footer {
  background-color: #f9f9f9;
  padding: 20px 40px;
  display: flex;
  justify-content: center;
  border-top: 1px solid #e0e0e0;
}

.footer-container {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  width: 100%;
  max-width: 1200px;
}

.footer-left {
  flex: 1;
}

.footer-left h4 {
  font-size: 16px;
  font-weight: bold;
  margin-bottom: 10px;
}

.footer-links {
  list-style: none;
  padding: 0;
  margin: 0 0 10px 0;
}

.footer-links li {
  margin-bottom: 5px;
}

.footer-links a {
  text-decoration: none;
  color: #333;
}

.footer-links a:hover {
  text-decoration: underline;
}

.footer-left p {
  font-size: 14px;
  color: #777;
}

.footer-right {
  flex: 2;
  text-align: right;
}

.footer-right p {
  font-size: 14px;
  margin-bottom: 10px;
  color: #333;
}

.social-icons {
  display: flex;
  justify-content: flex-end;
  gap: 15px;
}

.social-icons a {
  color: #555;
  font-size: 18px;
  text-decoration: none;
}

.social-icons a:hover {
  color: #000;
}

/* 모달 배경 스타일 */
.share-modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: rgba(0, 0, 0, 0.5); /* 반투명 배경 */
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 1050; /* 높은 z-index 값 설정 */
}

/* 모달 콘텐츠 스타일 */
.share-modal {
  background: #fff;
  padding: 20px;
  border-radius: 8px;
  text-align: center;
  max-width: 400px;
  width: 90%;
  box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1); /* 그림자 추가 */
  z-index: 1060; /* 모달 자체의 z-index */
  position: relative; /* 독립적인 레이어로 설정 */
}

/* 소셜 아이콘 컨테이너 */
.social-icons {
  display: flex;
  justify-content: center;
  gap: 20px;
  margin: 20px 0;
}

/* 소셜 아이콘 버튼 스타일 초기화 */
.social-btn {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  padding: 0; /* 기본 여백 제거 */
  background: none; /* 배경 제거 */
  border: none; /* 테두리 제거 */
  cursor: pointer; /* 커서 포인터 */
}

.social-btn img {
  width: 25px;
  height: 25px;
  display: block; /* inline-block의 여백 문제 제거 */
}

/* URL 복사 영역 스타일 */
.copy-url {
  display: flex;
  flex-direction: row; /* 가로로 정렬 */
  gap: 10px; /* 입력칸과 버튼 사이 간격 */
  align-items: center; /* 수평으로 정렬 */
}

/* URL 입력칸 스타일 */
.copy-url input {
  border: none; /* 테두리 제거 */
  border-bottom: 1px solid #ddd; /* 밑줄만 표시 */
  border-radius: 0; /* 둥근 모서리 제거 */
  padding: 5px 10px; /* 좌우 여백 추가 */
  height: 36px; /* 입력칸 높이 */
  font-size: 14px; /* 글자 크기 */
  line-height: normal; /* 텍스트 정렬 */
  outline: none; /* 클릭 시 기본 테두리 제거 */
  flex-grow: 1; /* 입력칸이 가능한 공간을 차지하도록 설정 */
}

/* URL 입력칸 포커스 시 스타일 */
.copy-url input:focus {
  border-bottom: 1px solid #40a681; /* 포커스 시 밑줄 색 변경 */
}

/* URL 복사 버튼 스타일 */
.copy-url button {
  font-size: 14px;
  background-color: #40a681;
  border: none; /* 테두리 제거 */
  color: white;
  padding: 0 12px; /* 좌우 여백 조정 */
  height: 36px; /* 버튼 높이 */
  line-height: 36px; /* 버튼 텍스트의 세로 정렬 */
  border-radius: 4px; /* 버튼의 모서리 둥글게 */
  cursor: pointer; /* 클릭 가능한 커서 */
  outline: none; /* 포커스 테두리 제거 */
  box-shadow: none; /* 그림자 제거 */
  white-space: nowrap; /* 텍스트가 잘리지 않도록 설정 */
  flex-shrink: 0; /* 버튼 크기 축소 방지 */
  margin-bottom: 0px;
}

.copy-url button:hover {
  background-color: #368e70; /* 버튼 호버 시 색상 변경 */
}

.copy-url button:focus {
  outline: none; /* 포커스 상태에서도 테두리 제거 */
  box-shadow: none; /* 포커스 시 그림자 제거 */
}

</style>
