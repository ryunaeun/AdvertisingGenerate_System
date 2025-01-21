<template>
  <div>
    <h1 class="text-2xl font-bold mb-4">Dashboard</h1>
    <div class="stats">
      <div class="stat">
        <h2>{{ unansweredCount }}</h2>
        <p>Unanswered Posts</p>
      </div>
      <div class="stat">
        <h2>{{ totalPosts }}</h2>
        <p>Total Posts</p>
      </div>
      <div class="stat">
        <h2>{{ averageResponseTime }} mins</h2>
        <p>Average Response Time</p>
      </div>
    </div>
    <div class="charts">
      <div class="chart">
        <h3>Posts Status</h3>
        <!-- 차트 자리 -->
      </div>
      <div class="chart">
        <h3>User Activity</h3>
        <!-- 차트 자리 -->
      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: "Dashboard",
  data() {
    return {
      posts: [
        { id: 1, title: "Question 1", status: "Unanswered" },
        { id: 2, title: "Question 2", status: "Answered" },
        { id: 3, title: "Question 3", status: "Unanswered" },
      ],
    };
  },
  computed: {
    unansweredCount() {
      return this.posts.filter((post) => post.status === "Unanswered").length;
    },
    totalPosts() {
      return this.posts.length;
    },
    averageResponseTime() {
      return 15; // 예제 값
    },
  },
};
</script>

<style scoped>
.stats {
  display: flex;
  gap: 20px;
}

.stat {
  flex: 1;
  background: white;
  padding: 20px;
  border-radius: 12px;
  text-align: center;
  box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
}

.charts {
  margin-top: 20px;
  display: flex;
  gap: 20px;
}
.chart {
  flex: 1;
  background: white;
  padding: 20px;
  border-radius: 12px;
  text-align: center;
  box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
}
</style>
