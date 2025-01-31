<template>
  <div>
    <h1 class="text-2xl font-bold mb-4">Notice Management</h1>

    <!-- 공지사항 작성 -->
    <div class="notice-form">
      <h2 class="text-lg font-bold mb-2">Write a New Notice</h2>
      <input
        type="text"
        v-model="newNotice.title"
        placeholder="Notice Title"
        class="input-title"
      />
      <textarea
        v-model="newNotice.content"
        placeholder="Notice Content"
        class="input-content"
      ></textarea>
      <button class="submit-btn" @click="submitNotice">Submit</button>
    </div>

    <hr class="my-8" />

    <!-- 공지사항 목록 -->
    <div class="notice-list">
      <h2 class="text-lg font-bold mb-4">All Notices</h2>
      <table class="notice-table">
        <thead>
          <tr>
            <th>Title</th>
            <th>Date</th>
            <th>Content</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="notice in notices" :key="notice.id">
            <td>{{ notice.title }}</td>
            <td>{{ formatDate(notice.date) }}</td>
            <td>{{ notice.content }}</td>
          </tr>
        </tbody>
      </table>
    </div>
  </div>
</template>

<script>
import axios from "axios";

export default {
  name: "NoticeManagement",
  data() {
    return {
      newNotice: {
        title: "",
        content: "",
      },
      notices: [],
    };
  },
  methods: {
    // 공지사항 목록 가져오기
    async fetchNotices() {
      try {
        const accessToken = sessionStorage.getItem("accessToken");
        const response = await axios.get(
          "http://43.201.26.71:8080/api/admin/notice",
          {
            headers: {
              Authorization: `Bearer ${accessToken}`,
              Accept: "application/json",
            },
            params: {
              page: 0,
              size: 20,
              isDescending: false,
            },
          }
        );
        console.log("Fetched Notices:", response.data);
        this.notices = response.data.content; // 공지사항 목록 저장
      } catch (error) {
        console.error("Failed to fetch notices:", error);
      }
    },
    // 새로운 공지사항 작성
    async submitNotice() {
      if (!this.newNotice.title || !this.newNotice.content) {
        alert("Please fill in all fields.");
        return;
      }

      try {
        const accessToken = sessionStorage.getItem("accessToken");
        const response = await axios.post(
          "http://43.201.26.71:8080/api/admin/notice",
          {
            title: this.newNotice.title,
            content: this.newNotice.content,
          },
          {
            headers: {
              Authorization: `Bearer ${accessToken}`,
              Accept: "application/json",
              "Content-Type": "application/json",
            },
          }
        );
        console.log("Notice Submitted:", response.data);
        alert("Notice successfully submitted!");
        this.newNotice.title = "";
        this.newNotice.content = "";
        this.fetchNotices(); // 작성 후 공지사항 목록 새로고침
      } catch (error) {
        console.error("Failed to submit notice:", error);
        alert("Failed to submit the notice. Please try again.");
      }
    },
    // 날짜 형식 변환
    formatDate(dateString) {
      const options = { year: "numeric", month: "long", day: "numeric" };
      return new Date(dateString).toLocaleDateString(undefined, options);
    },
  },
  created() {
    this.fetchNotices(); // 컴포넌트 생성 시 공지사항 로드
  },
};
</script>

<style scoped>
.notice-form {
  background: white;
  padding: 20px;
  border-radius: 12px;
  box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
  margin-bottom: 20px;
}

.input-title,
.input-content {
  width: 100%;
  padding: 10px;
  margin-bottom: 10px;
  border: 1px solid #ddd;
  border-radius: 4px;
}

.input-content {
  height: 100px;
}

.submit-btn {
  background: #2196f3;
  color: white;
  padding: 10px 15px;
  border: none;
  border-radius: 5px;
  cursor: pointer;
}

.submit-btn:hover {
  background: #1976d2;
}

.notice-list {
  background: white;
  padding: 20px;
  border-radius: 12px;
  box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
}

.notice-table {
  width: 100%;
  border-collapse: collapse;
  background: white;
  border-radius: 12px;
}

.notice-table th,
.notice-table td {
  padding: 10px;
  text-align: left;
  border-bottom: 1px solid #ddd;
}

.notice-table th {
  background: #f5f5f5;
}
</style>
