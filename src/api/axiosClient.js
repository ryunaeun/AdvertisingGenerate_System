import axios from "axios";

const apiClient = axios.create({
  baseURL: "http://localhost:8080/api",
});

// 요청 인터셉터
apiClient.interceptors.request.use(
  (config) => {
    const accessToken = sessionStorage.getItem("accessToken");
    if (accessToken) {
      config.headers.Authorization = `Bearer ${accessToken}`;
    }
    return config;
  },
  (error) => Promise.reject(error)
);

// 응답 인터셉터
apiClient.interceptors.response.use(
    (response) => response,
    async (error) => {
      const originalRequest = error.config;
      const refreshToken = localStorage.getItem("refreshToken");
  
      if (error.response && error.response.status === 401 && refreshToken) {
        try {
          const response = await axios.post("/refresh-token", { refreshToken });
          const newAccessToken = response.data.accessToken;
  
          sessionStorage.setItem("accessToken", newAccessToken);
  
          originalRequest.headers.Authorization = `Bearer ${newAccessToken}`;
          return axios(originalRequest); // 요청 재시도
        } catch (refreshError) {
          console.error("Refresh Token 갱신 실패:", refreshError);
  
          // Refresh Token 삭제 및 로그인 페이지로 이동
          localStorage.removeItem("refreshToken");
          sessionStorage.removeItem("accessToken");
          window.location.href = "/login";
          return Promise.reject(refreshError);
        }
      }
  
      return Promise.reject(error);
    }
  );

export default apiClient;