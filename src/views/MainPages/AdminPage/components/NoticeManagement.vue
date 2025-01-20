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
    async fetchNotices() {
      try {
        const response = await fetch("https://api.example.com/notices"); // GET 관리자(공지사항 조회)
        const data = await response.json();
        this.notices = data.sort((a, b) => new Date(b.date) - new Date(a.date)); // 최신 공지사항 정렬
      } catch (error) {
        console.error("Failed to fetch notices:", error);
      }
    },
    async submitNotice() {
      if (!this.newNotice.title || !this.newNotice.content) {
        alert("Please fill in all fields.");
        return;
      }

      try {
        const response = await fetch("https://api.example.com/notices", {
          method: "POST", // POST 관리자(공지사항 쓰기)
          headers: {
            "Content-Type": "application/json",
          },
          body: JSON.stringify({
            title: this.newNotice.title,
            content: this.newNotice.content,
            date: new Date().toISOString(),
          }),
        });

        if (!response.ok) {
          throw new Error("Failed to submit notice.");
        }

        // 성공적으로 작성 후 공지사항 목록 갱신
        this.fetchNotices();
        this.newNotice.title = "";
        this.newNotice.content = "";
      } catch (error) {
        console.error("Failed to submit notice:", error);
      }
    },
    formatDate(dateString) {
      const options = { year: "numeric", month: "long", day: "numeric" };
      return new Date(dateString).toLocaleDateString(undefined, options);
    },
  },
  created() {
    this.fetchNotices();
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
