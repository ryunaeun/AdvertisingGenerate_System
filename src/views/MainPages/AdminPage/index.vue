<template>
  <div class="admin-container">
    <!-- Sidebar -->
    <div class="sidebar">
      <h2 class="brand">Admin Panel</h2>
      <ul class="menu">
        <li
          :class="{ active: selectedMenu === 'Dashboard' }"
          @click="selectedMenu = 'Dashboard'"
        >
          Dashboard
        </li>
        <li
          :class="{ active: selectedMenu === 'ConversationManagement' }"
          @click="selectedMenu = 'ConversationManagement'"
        >
          ConversationManagement
        </li>
        <li
          :class="{ active: selectedMenu === 'User Management' }"
          @click="selectedMenu = 'User Management'"
        >
          User Management
        </li>
        <li
          :class="{ active: selectedMenu === 'Post Management' }"
          @click="selectedMenu = 'Post Management'"
        >
          Post Management
        </li>
        <li
          :class="{ active: selectedMenu === 'Notice Management' }"
          @click="selectedMenu = 'Notice Management'"
        >
          Notice Management
        </li>
      </ul>
      <!-- 로그아웃 버튼 -->
      <button class="logout-btn" @click="logout">Logout</button>
    </div>

    <!-- Main Content -->
    <div class="content">
      <header class="header">
        <h1>{{ selectedMenu }}</h1>
      </header>
      <div class="main-content">
        <div v-if="selectedMenu === 'Dashboard'">
          <Dashboard />
        </div>
        <div v-if="selectedMenu === 'ConversationManagement'">
          <ConversationManagement />
        </div>
        <div v-if="selectedMenu === 'User Management'">
          <UserManagement />
        </div>
        <div v-if="selectedMenu === 'Post Management'">
          <PostManagement />
        </div>
        <div v-if="selectedMenu === 'Notice Management'">
          <NoticeManagement />
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import Dashboard from "./components/Dashboard.vue";
import ConversationManagement from "./components/ConversationManagement.vue";
import UserManagement from "./components/UserManagement.vue";
import PostManagement from "./components/PostManagement.vue";
import NoticeManagement from "./components/NoticeManagement.vue";
import axios from "axios";

export default {
  name: "AdminPage",
  data() {
    return {
      selectedMenu: "Dashboard", // 기본 메뉴는 Dashboard
    };
  },
  components: {
    Dashboard,
    ConversationManagement,
    UserManagement,
    PostManagement,
    NoticeManagement, // 공지사항 관리 컴포넌트 추가
  },
  methods: {
    async logout() {
      try {
        // 로그아웃 API 호출
        await axios.post("http://43.201.26.71:8080/api/logout", null, {
          headers: {
            Authorization: `Bearer ${sessionStorage.getItem("accessToken")}`,
            Accept: "application/json",
          },
        });

        // 토큰 제거 및 로그인 페이지로 이동
        sessionStorage.removeItem("accessToken");
        localStorage.removeItem("refreshToken");
        this.$router.push("/login");
      } catch (error) {
        console.error("Logout failed:", error);
        alert("로그아웃에 실패했습니다. 다시 시도해주세요.");
      }
    },
  },
};
</script>

<style scoped>
.admin-container {
  display: flex;
  height: 100vh;
  background-color: #f4f6f9;
  overflow: hidden; /* 전체 화면에서 넘침 방지 */
}

.sidebar {
  width: 250px;
  background: #ffffff;
  box-shadow: 2px 0 8px rgba(0, 0, 0, 0.1);
  padding: 20px;
  display: flex;
  flex-direction: column;
  overflow-y: auto;
}

.brand {
  font-size: 24px;
  font-weight: bold;
  color: #2196f3;
  margin-bottom: 20px;
  text-align: center;
}

.menu {
  list-style: none;
  padding: 0;
  margin: 0;
}

.menu li {
  padding: 12px 20px;
  cursor: pointer;
  border-radius: 8px;
  color: #555;
  margin-bottom: 8px;
  transition: background-color 0.2s, color 0.2s;
}

.menu li:hover {
  background-color: #e3f2fd;
  color: #2196f3;
}

.menu li.active {
  background-color: #2196f3;
  color: white;
  font-weight: bold;
}

.logout-btn {
  margin-top: auto; /* 하단에 위치 */
  padding: 12px 20px;
  border: none;
  background-color: #f44336;
  color: white;
  border-radius: 8px;
  cursor: pointer;
  transition: background-color 0.2s ease-in-out;
}

.logout-btn:hover {
  background-color: #d32f2f;
}

.content {
  flex: 1;
  display: flex;
  flex-direction: column;
  overflow: hidden; /* 넘치는 콘텐츠 방지 */
}

.header {
  background: white;
  padding: 20px;
  box-shadow: 0px 2px 4px rgba(0, 0, 0, 0.1);
  font-size: 24px;
  font-weight: bold;
  color: #333;
}

.main-content {
  flex: 1;
  padding: 20px;
  overflow-y: auto; /* 세로 스크롤 허용 */
}

@media (max-width: 768px) {
  .sidebar {
    display: none; /* 작은 화면에서는 사이드바 숨김 */
  }

  .content {
    margin: 0;
    width: 100%;
  }
}
</style>
