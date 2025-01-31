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
        <!-- 게시글 데이터 -->
        <tr v-for="post in posts" :key="post.id">
          <td>{{ post.title }}</td>
          <td>{{ post.author }}</td>
          <td>{{ post.date }}</td>
          <td>
            <span
              :class="{
                'status-badge': true,
                unanswered: post.status === 'Unanswered',
                answered: post.status === 'Answered',
              }"
            >
              {{ post.status }}
            </span>
          </td>
          <td>
            <button class="view-reply-btn" @click="viewPost(post.id)">
              View
            </button>
          </td>
        </tr>
      </tbody>
    </table>
  </div>
</template>

<script>
export default {
  name: "PostManagement",
  data() {
    return {
      // 하드코딩된 게시글 데이터
      posts: [
        {
          id: 1,
          title: "How to integrate Vue with REST API?",
          author: "John Doe",
          date: "2025-01-28",
          status: "Unanswered",
        },
        {
          id: 2,
          title: "Best practices for component communication in Vue",
          author: "Jane Smith",
          date: "2025-01-27",
          status: "Answered",
        },
      ],
    };
  },
  methods: {
    viewPost(postId) {
      // 특정 게시글 보기 로직
      alert(`게시글 ID: ${postId}`);
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
</style>
