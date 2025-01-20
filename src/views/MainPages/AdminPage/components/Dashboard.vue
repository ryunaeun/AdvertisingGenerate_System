<template>
  <div class="dashboard">
    <h1 class="text-2xl font-bold mb-4">Dashboard</h1>
    <div
      class="stats grid grid-cols-1 md:grid-cols-2 xl:grid-cols-3 gap-4 mb-8"
    >
      <div class="stat bg-white shadow-md p-4 rounded-lg">
        <h2 class="text-lg font-bold">총 메시지 수</h2>
        <p class="text-2xl">{{ totalMessages }}</p>
      </div>
      <div class="stat bg-white shadow-md p-4 rounded-lg">
        <h2 class="text-lg font-bold">평균 세션 상호작용 수</h2>
        <p class="text-2xl">{{ averageInteractions }}</p>
      </div>
      <div class="stat bg-white shadow-md p-4 rounded-lg">
        <h2 class="text-lg font-bold">활성 사용자 수</h2>
        <p class="text-2xl">{{ totalActiveUsers }}</p>
      </div>
      <div class="stat bg-white shadow-md p-4 rounded-lg">
        <h2 class="text-lg font-bold">토큰 사용량</h2>
        <p class="text-2xl">{{ totalTokens }} (USD {{ totalTokenCost }})</p>
      </div>
    </div>
    <div class="charts grid grid-cols-1 md:grid-cols-2 xl:grid-cols-3 gap-4">
      <!-- 총 메시지 수 -->
      <div class="chart bg-white shadow-md p-4 rounded-lg">
        <h3 class="text-lg font-bold mb-4">총 메시지 수</h3>
        <div ref="messageChart" style="width: 100%; height: 300px"></div>
      </div>
      <!-- 평균 세션 상호작용 수 -->
      <div class="chart bg-white shadow-md p-4 rounded-lg">
        <h3 class="text-lg font-bold mb-4">평균 세션 상호작용 수</h3>
        <div ref="interactionChart" style="width: 100%; height: 300px"></div>
      </div>
      <!-- 활성 사용자 수 -->
      <div class="chart bg-white shadow-md p-4 rounded-lg">
        <h3 class="text-lg font-bold mb-4">활성 사용자 수</h3>
        <div ref="activeUserChart" style="width: 100%; height: 300px"></div>
      </div>
    </div>
  </div>
</template>

<script>
import * as echarts from "echarts";

export default {
  name: "Dashboard",
  data() {
    return {
      messagesData: [],
      interactionsData: [],
      endUsersData: [],
      tokenCostsData: [],
      totalMessages: 0,
      averageInteractions: 0,
      totalActiveUsers: 0,
      totalTokens: 0,
      totalTokenCost: 0,
    };
  },
  async mounted() {
    await Promise.all([
      this.fetchData("messagesData", "/statistics/daily-messages"),
      this.fetchData(
        "interactionsData",
        "/statistics/average-session-interactions"
      ),
      this.fetchData("endUsersData", "/statistics/daily-end-users"),
      this.fetchData("tokenCostsData", "/statistics/token-costs"),
    ]);
    this.calculateStats();
    this.initCharts();
  },
  methods: {
    async fetchData(key, endpoint) {
      const response = await fetch(
        `http://121.176.214.36/console/api/apps/f41fe500-6dc6-4b0a-8658-f08cf038b6e9${endpoint}`,
        {
          headers: {
            Authorization:
              "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VyX2lkIjoiYjk1ZmE2MGEtZWFkNS00YWRmLWE0NmItYWYxMjVjZTcwZmQ1IiwiZXhwIjoxNzM4MDQ2MDIxLCJpc3MiOiJTRUxGX0hPU1RFRCIsInN1YiI6IkNvbnNvbGUgQVBJIFBhc3Nwb3J0In0.Jt672rKxh0RSjF9w4ezSwSQaVFu3_dkEdGoHPm_QHO4",
          },
        }
      );
      const result = await response.json();
      this[key] = result.data;
    },
    calculateStats() {
      this.totalMessages = this.messagesData.reduce(
        (sum, item) => sum + item.message_count,
        0
      );
      this.averageInteractions = (
        this.interactionsData.reduce(
          (sum, item) => sum + item.interactions,
          0
        ) / this.interactionsData.length
      ).toFixed(2);
      this.totalActiveUsers = this.endUsersData.reduce(
        (sum, item) => sum + item.terminal_count,
        0
      );
      this.totalTokens = this.tokenCostsData.reduce(
        (sum, item) => sum + item.token_count,
        0
      );
      this.totalTokenCost = this.tokenCostsData
        .reduce((sum, item) => sum + parseFloat(item.total_price), 0)
        .toFixed(4);
    },
    initCharts() {
      this.createChart(
        this.$refs.messageChart,
        "총 메시지 수",
        this.messagesData.map((item) => item.date),
        this.messagesData.map((item) => item.message_count)
      );
      this.createChart(
        this.$refs.interactionChart,
        "평균 세션 상호작용 수",
        this.interactionsData.map((item) => item.date),
        this.interactionsData.map((item) => item.interactions)
      );
      this.createChart(
        this.$refs.activeUserChart,
        "활성 사용자 수",
        this.endUsersData.map((item) => item.date),
        this.endUsersData.map((item) => item.terminal_count)
      );
    },
    createChart(el, title, xData, yData) {
      const chart = echarts.init(el);
      const options = {
        title: { text: title, left: "center" },
        tooltip: { trigger: "axis" },
        xAxis: { type: "category", data: xData },
        yAxis: { type: "value" },
        series: [
          {
            data: yData,
            type: "line",
            smooth: true,
            areaStyle: {},
          },
        ],
      };
      chart.setOption(options);
    },
  },
};
</script>

<style scoped>
.dashboard {
  padding: 20px;
  background-color: #f9fafb;
}
.stats {
  margin-bottom: 20px;
}
.stat {
  text-align: center;
}
.chart {
  padding: 20px;
}
</style>
