<template>
  <div>
    <h1 class="text-2xl font-bold mb-4">Post Management</h1>

    <!-- 게시글 테이블 -->
    <table class="post-table">
      <thead>
        <tr>
          <th>Title</th>
          <th>Author</th>
          <th>Date</th>
          <th>Status</th>
          <th>Actions</th>
        </tr>
      </thead>
      <tbody>
        <!-- 미답변 게시글 -->
        <tr
          v-for="post in unansweredPosts"
          :key="post.id"
          class="unanswered-row"
        >
          <td>{{ post.title }}</td>
          <td>{{ post.author }}</td>
          <td>{{ post.date }}</td>
          <td>
            <span class="status-badge unanswered">Unanswered</span>
          </td>
          <td>
            <button class="reply-btn" @click="viewPost(post.id)">Reply</button>
          </td>
        </tr>

        <!-- 답변 완료 게시글 -->
        <tr v-for="post in answeredPosts" :key="post.id" class="answered-row">
          <td>{{ post.title }}</td>
          <td>{{ post.author }}</td>
          <td>{{ post.date }}</td>
          <td>
            <span class="status-badge answered">Answered</span>
          </td>
          <td>
            <button class="view-reply-btn" @click="viewPost(post.id)">
              View
            </button>
          </td>
        </tr>
      </tbody>
    </table>

    <!-- 게시글 상세보기 및 답변 작성 -->
    <div v-if="selectedPost" class="post-detail">
      <h2 class="text-xl font-bold mb-2">Post Details</h2>
      <p><strong>Title:</strong> {{ selectedPost.title }}</p>
      <p><strong>Author:</strong> {{ selectedPost.author }}</p>
      <p><strong>Date:</strong> {{ selectedPost.date }}</p>
      <p class="mt-4"><strong>Content:</strong></p>
      <p class="content-box">{{ selectedPost.content }}</p>

      <!-- 답변 작성 -->
      <div class="reply-section mt-4">
        <h3 class="text-lg font-bold">Write a Reply</h3>
        <textarea
          v-model="replyText"
          placeholder="Type your reply here..."
          class="reply-input"
        ></textarea>
        <div class="reply-actions">
          <button class="cancel-btn" @click="cancelReply">Cancel</button>
          <button class="save-btn" @click="submitReply">Submit</button>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: "PostManagement",
  data() {
    return {
      posts: [
        {
          id: 1,
          title: "Question 1",
          author: "Alice",
          date: "2025-01-20",
          status: "Unanswered",
          content: "This is a detailed question content.",
        },
        {
          id: 2,
          title: "Question 2",
          author: "Bob",
          date: "2025-01-19",
          status: "Answered",
          content: "Another detailed question content.",
          reply: "Thank you for your question!",
        },
      ],
      selectedPost: null,
      replyText: "",
    };
  },
  computed: {
    unansweredPosts() {
      return this.posts.filter((post) => post.status === "Unanswered");
    },
    answeredPosts() {
      return this.posts.filter((post) => post.status === "Answered");
    },
  },
  methods: {
    viewPost(postId) {
      this.selectedPost = this.posts.find((post) => post.id === postId);
      this.replyText = this.selectedPost.reply || "";
    },
    cancelReply() {
      this.selectedPost = null;
      this.replyText = "";
    },
    submitReply() {
      if (this.selectedPost) {
        this.selectedPost.reply = this.replyText;
        this.selectedPost.status = "Answered";
        this.selectedPost = null;
        this.replyText = "";
      }
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

.status-badge.unanswered {
  background: #ff9800;
  color: white;
  padding: 5px 10px;
  border-radius: 12px;
}

.status-badge.answered {
  background: #4caf50;
  color: white;
  padding: 5px 10px;
  border-radius: 12px;
}

.reply-btn,
.view-reply-btn {
  background: #2196f3;
  color: white;
  padding: 5px 10px;
  border: none;
  border-radius: 5px;
  cursor: pointer;
}

.reply-btn:hover,
.view-reply-btn:hover {
  background: #1976d2;
}

.post-detail {
  margin-top: 20px;
  background: white;
  padding: 20px;
  border-radius: 12px;
  box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
}

.content-box {
  background: #f9f9f9;
  padding: 10px;
  border-radius: 8px;
  border: 1px solid #ddd;
  margin-top: 10px;
}

.reply-section {
  margin-top: 20px;
}

.reply-input {
  width: 100%;
  height: 100px;
  padding: 10px;
  border: 1px solid #ddd;
  border-radius: 4px;
  margin-top: 10px;
}

.reply-actions {
  display: flex;
  justify-content: flex-end;
  margin-top: 10px;
}

.cancel-btn {
  background: #f44336;
  color: white;
  padding: 5px 10px;
  border: none;
  border-radius: 5px;
  cursor: pointer;
  margin-right: 10px;
}

.cancel-btn:hover {
  background: #d32f2f;
}

.save-btn {
  background: #4caf50;
  color: white;
  padding: 5px 10px;
  border: none;
  border-radius: 5px;
  cursor: pointer;
}

.save-btn:hover {
  background: #388e3c;
}
</style>
