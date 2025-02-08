<template>
  <div>
    <h1 class="text-2xl font-bold mb-4">Post Management</h1>

    <!-- 게시글 테이블 -->
    <table class="post-table">
      <thead>
        <tr>
          <th>ID</th>
          <th>Title</th>
          <th>Author</th>
          <th>Date</th>
          <th>Content</th>
          <!-- 문의 내용 추가 -->
          <th>Actions</th>
        </tr>
      </thead>
      <tbody>
        <tr v-for="post in posts" :key="post.boardId">
          <td>{{ post.boardId }}</td>
          <td>{{ post.title }}</td>
          <td>{{ post.email }}</td>
          <td>{{ formatDate(post.createdAt) }}</td>
          <td>{{ post.content }}</td>
          <!-- 문의 내용 표시 -->
          <td>
            <button class="view-reply-btn" @click="openReplyModal(post)">
              Reply
            </button>
            <button class="delete-btn" @click="deletePost(post.boardId)">
              Delete
            </button>
          </td>
        </tr>
      </tbody>
    </table>

    <!-- 문의사항 답변 모달 -->
    <div
      v-if="isReplyModalOpen"
      class="modal-overlay"
      @click.self="closeReplyModal"
    >
      <div class="modal-content">
        <h2 class="text-xl font-semibold mb-4">Reply to Inquiry</h2>

        <!-- 문의 내용 -->
        <div class="inquiry-content">
          <p><strong>Title:</strong> {{ selectedPost.title }}</p>
          <p><strong>Author:</strong> {{ selectedPost.email }}</p>
          <p><strong>Content:</strong> {{ selectedPost.content }}</p>
        </div>

        <!-- 답변 입력 -->
        <textarea
          v-model="replyContent"
          placeholder="Write your reply..."
          class="input-content"
        ></textarea>
        <button class="submit-btn" @click="submitReply">Submit Reply</button>
        <button class="close-btn" @click="closeReplyModal">Close</button>
      </div>
    </div>
  </div>
</template>

<script>
import axios from "axios";

export default {
  name: "PostManagement",
  data() {
    return {
      posts: [], // 문의사항 데이터 저장
      isReplyModalOpen: false, // 모달 상태
      selectedPost: {}, // 선택된 문의사항
      replyContent: "", // 답변 내용
    };
  },
  mounted() {
    this.fetchAllPosts(); // 컴포넌트 로드 시 데이터 가져오기
  },
  methods: {
    async fetchAllPosts() {
      try {
        const accessToken = sessionStorage.getItem("accessToken");
        const response = await axios.get(
          "http://aivle-advi.com:8080/api/admin/users/board",
          {
            headers: {
              Authorization: `Bearer ${accessToken}`,
              Accept: "application/json",
            },
            params: { page: 0, size: 20, sort: "date", isDescending: false },
          }
        );

        console.log("문의사항 데이터:", response.data);

        this.posts = response.data.content.map((post) => ({
          boardId: post.boardId,
          title: post.title,
          email: post.email,
          content: post.content, // 문의 내용 포함
          createdAt: post.createdAt,
        }));
      } catch (error) {
        console.error("문의사항 데이터 로드 실패:", error);
      }
    },

    openReplyModal(post) {
      this.selectedPost = post;
      this.replyContent = "";
      this.isReplyModalOpen = true;
    },

    closeReplyModal() {
      this.isReplyModalOpen = false;
    },

    async submitReply() {
      if (!this.replyContent.trim()) {
        alert("Please write a reply.");
        return;
      }

      try {
        const accessToken = sessionStorage.getItem("accessToken");

        const response = await axios.post(
          "http://aivle-advi.com:8080/api/admin/users/board/reply",
          {
            boardId: this.selectedPost.boardId,
            email: this.selectedPost.email,
            title: this.selectedPost.title,
            content: this.replyContent,
          },
          {
            headers: {
              Authorization: `Bearer ${accessToken}`,
              Accept: "application/json",
              "Content-Type": "application/json",
            },
          }
        );

        console.log("✅ 문의사항 답변 성공:", response.data);
        alert("Reply successfully submitted!");

        this.closeReplyModal();
      } catch (error) {
        console.error("❌ 문의사항 답변 실패:", error.response?.data || error);
        alert("Failed to submit the reply.");
      }
    },

    async deletePost(boardId) {
      if (!confirm("Are you sure you want to delete this post?")) return;

      try {
        const accessToken = sessionStorage.getItem("accessToken");

        const response = await axios.delete(
          `http://aivle-advi.com:8080/api/admin/users/board/${boardId}`,
          {
            headers: {
              Authorization: `Bearer ${accessToken}`,
              Accept: "application/json",
            },
          }
        );

        console.log("✅ 문의사항 삭제 성공:", response.data);
        alert("Post successfully deleted!");

        // 삭제 후 목록 갱신
        this.posts = this.posts.filter((post) => post.boardId !== boardId);
      } catch (error) {
        console.error("❌ 문의사항 삭제 실패:", error.response?.data || error);
        alert("Failed to delete the post.");
      }
    },

    formatDate(dateString) {
      const date = new Date(dateString);
      return date.toLocaleDateString();
    },
  },
};
</script>

<style scoped>
.post-table {
  width: 100%;
  border-collapse: collapse;
  background: white;
  border-radius: 12px;
}

.post-table th,
.post-table td {
  padding: 10px;
  text-align: left;
  border-bottom: 1px solid #ddd;
}

.view-reply-btn {
  background: #2196f3;
  color: white;
  padding: 5px 10px;
  border: none;
  border-radius: 5px;
  cursor: pointer;
}

.view-reply-btn:hover {
  background: #1976d2;
}

.delete-btn {
  background: #f44336;
  color: white;
  padding: 5px 10px;
  border: none;
  border-radius: 5px;
  cursor: pointer;
}

.delete-btn:hover {
  background: #d32f2f;
}

/* 모달 스타일 */
.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: rgba(0, 0, 0, 0.5);
  display: flex;
  align-items: center;
  justify-content: center;
}

.modal-content {
  background: white;
  padding: 20px;
  border-radius: 12px;
  max-width: 600px;
  width: 90%;
}

.inquiry-content {
  background: #f9f9f9;
  padding: 10px;
  margin-bottom: 10px;
  border-radius: 6px;
}

.input-content {
  width: 100%;
  height: 100px;
  padding: 10px;
  margin-bottom: 10px;
  border: 1px solid #ddd;
  border-radius: 4px;
}

.submit-btn,
.close-btn {
  padding: 10px 15px;
  border: none;
  border-radius: 5px;
  cursor: pointer;
}

.submit-btn {
  background: #2196f3;
  color: white;
}

.close-btn {
  background: #f44336;
  color: white;
}

.submit-btn:hover {
  background: #1976d2;
}

.close-btn:hover {
  background: #d32f2f;
}
</style>
