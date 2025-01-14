
<script setup>
import apiClient from "@/api/axiosClient";
import { onMounted } from "vue";
import { useRouter } from "vue-router";

const router = useRouter();

onMounted(async () => {
  const refreshToken = localStorage.getItem("refreshToken");

  if (refreshToken) {
    try {
      // Refresh Token으로 Access Token 갱신
      const response = await apiClient.post("/refresh-token", { refreshToken });
      const newAccessToken = response.data.accessToken;

      // Access Token 저장
      sessionStorage.setItem("accessToken", newAccessToken);

      console.log("자동 로그인 성공, 사용자 정보 확인 중...");

      // 현재 사용자 정보 확인
      const userResponse = await apiClient.get("/current-user");
      const userRole = userResponse.data.role;

      // 사용자 역할에 따라 라우팅
      if (userRole === "ROLE_ADMIN") {
        router.push("/admin");
      } else {
        router.push("/home");
      }
    } catch (error) {
      console.error("Access Token 갱신 실패:", error);

      // Refresh Token 제거 및 로그인 페이지로 이동
      localStorage.removeItem("refreshToken");
      sessionStorage.removeItem("accessToken");
      router.push("/login"); // 실패 시 /login으로 이동
    }
  } else {
    console.log("Refresh Token 없음, 로그인 필요");
    router.push("/login");
  }
});
</script>

<template>
  <router-view />
</template>
