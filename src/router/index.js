import { createRouter, createWebHistory } from "vue-router";
import LandingPage from "../views/MainPages/LandingPage/index.vue";
import HomePage from "../views/MainPages/HomePage/index.vue";
import GalleryPage from "../views/MainPages/GalleryPage/index.vue";
import BoardPage from "../views/MainPages/BoardPage/index.vue";
import PricingPage from "../views/MainPages/PricingPage/index.vue";
import RegisterPage from "../views/MainPages/RegisterPage/index.vue";
import LoginPage from "../views/MainPages/LoginPage/index.vue";
import AdminPage from "../views/MainPages/AdminPage/index.vue";
import MyPage from "../views/MainPages/MyPage/index.vue";
import PromptGenerator from "../views/MainPages/PromptGenerator/index.vue";
import Txt2VidGenerator from "../views/MainPages/Txt2VidGenerator/index.vue";
import AnalysisPage from "../views/MainPages/AnalysisPage/index.vue";
import ResultPage from "../views/MainPages/ResultPage/index.vue";


import apiClient from "@/api/axiosClient";
const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: "/",
      name: "landing",
      component: LandingPage,
    },
    {
      path: "/home",
      name: "home",
      component: HomePage,
      meta: { requiresAuth: true }, // 인증이 필요한 경로 표시
    },
    {
      path: "/gallery",
      name: "gallery",
      component: GalleryPage,
    },
    {
      path: "/pricing",
      name: "pricing",
      component: PricingPage,
    },
    {
      path: "/board",
      name: "board",
      component: BoardPage,
    },
    {
      path: "/mypage",
      name: "mypage",
      component: MyPage,
      meta: { requiresAuth: true },
    },
    {
      path: "/login",
      name: "login",
      component: LoginPage,
    },
    {
      path: "/register",
      name: "register",
      component: RegisterPage,
    },
    {
      path: "/admin",
      name: "admin",
      component: AdminPage,
      meta: { requiresAuth: true, requiresRole: "ROLE_ADMIN" }, // 관리자 권한 필요
    },
    {
      path: "/analysis", // "/analysis" 경로로 이동 시 AnalysisPage 렌더링
      name: "analysis",
      component: AnalysisPage,
    },

    {
      path: '/prompt-generator',
      name: 'promptGenerator',
      component: PromptGenerator,
    },
    {
      path: '/txt2vid-generator',
      name: 'txt2vid',
      component: Txt2VidGenerator,
    },
    {
      path: '/result',
      name: 'result',
      component: ResultPage,
    },
  ],
});

router.beforeEach(async (to, from, next) => {
  const accessToken = sessionStorage.getItem("accessToken");
  const refreshToken = localStorage.getItem("refreshToken");

  if (to.matched.some((record) => record.meta.requiresAuth)) {
    if (!accessToken && refreshToken) {
      try {
        console.log("🔄 Refresh Token으로 Access Token 갱신 시도...");

        // 🔹 Refresh Token 요청 (이제 새로운 Refresh Token도 받아옴)
        const response = await apiClient.post("/refresh-token", { refreshToken });
        const newAccessToken = response.data.accessToken;
        const newRefreshToken = response.data.refreshToken;

        // 🔹 Vue에 갱신된 토큰 저장
        sessionStorage.setItem("accessToken", newAccessToken);
        localStorage.setItem("refreshToken", newRefreshToken);

        console.log("✅ Access Token 갱신 성공");

        // 🔹 사용자 정보 요청
        const userResponse = await apiClient.get("/user-info/personal");
        console.log("✅ 사용자 정보 확인 완료:", userResponse.data);

        const userRole = userResponse.data.role;
        if (userRole === "ROLE_ADMIN" && to.name !== "admin") {
          return next({ name: "admin" });
        } 
        if (userRole !== "ROLE_USER" && to.name !== "home") {
          return next({ name: "home" });
        }

        return next();
      } catch (error) {
        console.error("❌ Access Token 갱신 실패:", error);
        localStorage.removeItem("refreshToken");
        sessionStorage.removeItem("accessToken");
        alert("세션이 만료되었습니다. 다시 로그인해주세요.");
        return next({ name: "login" });
      }
    } else if (!accessToken) {
      console.warn("🚨 Refresh Token 없음, 로그인 페이지로 이동");
      return next({ name: "login" });
    }
  }
  next();
});

export default router;
