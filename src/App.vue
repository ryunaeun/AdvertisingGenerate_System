<script setup>
import apiClient from "@/api/axiosClient";
import { onMounted } from "vue";
import { useRouter } from "vue-router";

const router = useRouter();

onMounted(async () => {
  
  // Dify Chatbot Config 설정
  window.difyChatbotConfig = {
    token: "7UIEk2PEC1qmXWYX",
    baseUrl: "http://121.176.214.36",
  };

  // Dify Chatbot 스크립트 동적 추가
  const script = document.createElement("script");
  script.src = "http://121.176.214.36/embed.min.js";
  script.id = "7UIEk2PEC1qmXWYX";
  script.defer = true;
  document.body.appendChild(script);

  console.log("Dify Chatbot script loaded.");


  const refreshToken = localStorage.getItem("refreshToken");
  console.log("App.vue: RefreshToken 확인:", refreshToken);

  if (refreshToken) {
    try {
      // Refresh Token으로 Access Token 갱신
      const response = await apiClient.post("/refresh-token", { refreshToken });
      const newAccessToken = response.data.accessToken;

      sessionStorage.setItem("accessToken", newAccessToken);

      console.log("자동 로그인 성공, 사용자 정보 확인 중...");
      const userResponse = await apiClient.get("/current-user");
      const userRole = userResponse.data.role;

      if (userRole === "ROLE_ADMIN") {
        router.push("/admin");
      } else {
        router.push("/home");
      }
    } catch (error) {
      console.error("Access Token 갱신 실패:", error);

      localStorage.removeItem("refreshToken");
      sessionStorage.removeItem("accessToken");
      router.push("/");
    }
  } else {
    console.warn("Refresh Token 없음, 로그인 필요");
    router.push("/");
  }
});

</script>

<template>
  <router-view />
</template>
<style>
/* Dify Chatbot Styles */
#dify-chatbot-bubble-button {
  background-color: #1c64f2 !important;
}

#dify-chatbot-bubble-window {
  width: 24rem !important;
  height: 40rem !important;
}
</style>
