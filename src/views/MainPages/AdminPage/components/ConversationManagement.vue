<template>
  <div>
    <h1 class="text-2xl font-bold mb-4">Conversation Management</h1>

    <!-- 검색 필터 -->
    <div class="filter-section mb-6">
      <input
        type="text"
        v-model="conversationId"
        placeholder="Enter Conversation ID"
        class="input-field"
      />
      <input
        type="text"
        v-model="userId"
        placeholder="Enter User ID"
        class="input-field"
      />
      <button class="fetch-btn" @click="fetchMessages">Fetch Messages</button>
    </div>

    <!-- 메시지 리스트 -->
    <div class="message-list">
      <div v-for="message in messages" :key="message.id" class="message-item">
        <div class="user-message" v-if="message.query">
          <strong>User:</strong> {{ message.query }}
        </div>
        <div class="assistant-message" v-if="message.answer">
          <strong>Assistant:</strong> {{ message.answer }}
        </div>
        <div class="message-meta">
          <small
            ><strong>Created At:</strong>
            {{ formatDate(message.created_at) }}</small
          >
        </div>
        <div v-if="message.message_files.length" class="attachments">
          <strong>Attachments:</strong>
          <ul>
            <li v-for="file in message.message_files" :key="file.id">
              <a :href="file.url" target="_blank">{{ file.type }}</a>
            </li>
          </ul>
        </div>
      </div>
    </div>

    <!-- 더 보기 버튼 -->
    <button
      v-if="hasMore"
      class="load-more-btn"
      @click="fetchMessages(nextFirstId)"
    >
      Load More
    </button>
  </div>
</template>

<script>
export default {
  name: "ConversationManagement",
  data() {
    return {
      conversationId: "", // 입력된 대화 ID
      userId: "", // 입력된 사용자 ID
      messages: [], // 메시지 리스트
      limit: 20, // 한 번에 불러올 메시지 수
      hasMore: false, // 추가 메시지가 있는지 여부
      nextFirstId: null, // 다음 페이지의 첫 메시지 ID
    };
  },
  methods: {
    async fetchMessages(firstId = null) {
      if (!this.conversationId || !this.userId) {
        alert("Please enter both Conversation ID and User ID.");
        return;
      }

      try {
        const response = await fetch(
          `http://121.176.214.36/v1/messages?user=${this.userId}&conversation_id=${this.conversationId}&first_id=${firstId}&limit=${this.limit}`,
          {
            method: "GET",
            headers: {
              Authorization: "Bearer YOUR_API_KEY", // 여기에 API 키 입력
            },
          }
        );

        if (!response.ok) {
          throw new Error("Failed to fetch messages");
        }

        const data = await response.json();

        // 메시지 업데이트
        this.messages = firstId ? [...this.messages, ...data.data] : data.data;
        this.hasMore = data.has_more;
        this.nextFirstId =
          data.data.length > 0 ? data.data[data.data.length - 1].id : null;
      } catch (error) {
        console.error("Error fetching messages:", error);
        alert("Failed to fetch messages. Check the console for more details.");
      }
    },
    formatDate(timestamp) {
      const date = new Date(timestamp * 1000);
      return date.toLocaleString();
    },
  },
};
</script>

<style scoped>
.filter-section {
  display: flex;
  gap: 10px;
  margin-bottom: 20px;
}

.input-field {
  padding: 10px;
  border: 1px solid #ddd;
  border-radius: 4px;
  width: 300px;
}

.fetch-btn {
  background: #2196f3;
  color: white;
  padding: 10px 15px;
  border: none;
  border-radius: 5px;
  cursor: pointer;
}

.fetch-btn:hover {
  background: #1976d2;
}

.message-list {
  margin-top: 20px;
  background: white;
  padding: 20px;
  border-radius: 12px;
  box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
}

.message-item {
  border-bottom: 1px solid #ddd;
  padding: 10px 0;
}

.message-item:last-child {
  border-bottom: none;
}

.user-message,
.assistant-message {
  margin-bottom: 5px;
}

.attachments {
  margin-top: 10px;
}

.load-more-btn {
  background: #4caf50;
  color: white;
  padding: 10px 15px;
  border: none;
  border-radius: 5px;
  cursor: pointer;
  margin-top: 20px;
}

.load-more-btn:hover {
  background: #388e3c;
}
</style>
