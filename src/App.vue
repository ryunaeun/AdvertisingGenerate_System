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
