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
      <button class="fetch-btn" @click="fetchConversations">Fetch Data</button>
    </div>

    <!-- 대화 테이블 -->
    <div class="overflow-x-auto">
      <table
        class="conversation-table mt-2 w-full min-w-[440px] border-collapse border-0"
      >
        <thead class="system-xs-medium-uppercase text-text-tertiary">
          <tr>
            <td
              class="pl-2 pr-1 w-5 rounded-l-lg bg-background-section-burn whitespace-nowrap"
            ></td>
            <td
              class="pl-3 py-1.5 bg-background-section-burn whitespace-nowrap"
            >
              요약
            </td>
            <td
              class="pl-3 py-1.5 bg-background-section-burn whitespace-nowrap"
            >
              엔드 유저 또는 계정
            </td>
            <td
              class="pl-3 py-1.5 bg-background-section-burn whitespace-nowrap"
            >
              상태
            </td>
            <td
              class="pl-3 py-1.5 bg-background-section-burn whitespace-nowrap"
            >
              메시지 수
            </td>
            <td
              class="pl-3 py-1.5 bg-background-section-burn whitespace-nowrap"
            >
              업데이트 시간
            </td>
            <td
              class="pl-3 py-1.5 rounded-r-lg bg-background-section-burn whitespace-nowrap"
            >
              생성 시간
            </td>
          </tr>
        </thead>
        <tbody class="text-text-secondary system-sm-regular">
          <tr
            v-for="conversation in conversations"
            :key="conversation.id"
            class="border-b border-divider-subtle hover:bg-background-default-hover cursor-pointer"
            @click="openModal(conversation.id)"
          >
            <td class="h-4"></td>
            <td class="p-3 pr-2 w-[160px]" style="max-width: 300px">
              <div
                class="text-text-secondary system-sm-regular overflow-hidden text-ellipsis whitespace-nowrap"
              >
                {{ conversation.name }}
              </div>
            </td>
            <td class="p-3 pr-2">
              <div
                class="text-text-secondary system-sm-regular overflow-hidden text-ellipsis whitespace-nowrap"
              >
                {{
                  conversation.from_end_user_session_id ||
                  conversation.from_account_name ||
                  "N/A"
                }}
              </div>
            </td>
            <td class="p-3 pr-2 w-[160px]" style="max-width: 300px">
              <div
                class="inline-flex items-center gap-1 system-xs-semibold-uppercase"
              >
                <div
                  class="w-2 h-2 border border-solid rounded-[3px]"
                  :class="
                    conversation.status === 'normal'
                      ? 'bg-green-500'
                      : 'bg-red-500'
                  "
                ></div>
                <span>{{ conversation.status }}</span>
              </div>
            </td>
            <td class="p-3 pr-2" style="max-width: 100px">
              <div
                class="text-text-secondary system-sm-regular overflow-hidden text-ellipsis whitespace-nowrap"
              >
                {{ conversation.message_count }}
              </div>
            </td>
            <td class="w-[160px] p-3 pr-2">
              {{ formatDate(conversation.updated_at) }}
            </td>
            <td class="w-[160px] p-3 pr-2">
              {{ formatDate(conversation.created_at) }}
            </td>
          </tr>
        </tbody>
      </table>
    </div>

    <!-- 더 보기 버튼 -->
    <button v-if="hasMore" class="load-more-btn" @click="fetchConversations">
      Load More
    </button>

    <!-- 채팅 로그 모달 -->
    <div v-if="isModalOpen" class="modal-overlay" @click.self="closeModal">
      <div class="modal-content">
        <h2 class="text-xl font-semibold mb-4">Chat Logs</h2>

        <!-- 로딩 상태 -->
        <div v-if="isLoading" class="loading-state">Loading chat logs...</div>

        <!-- 채팅 로그 -->
        <div v-else>
          <div v-for="log in chatLogs" :key="log.id" class="chat-log">
            <p><strong>Query:</strong> {{ log.query }}</p>
            <p><strong>Answer:</strong> {{ log.answer }}</p>
            <p><strong>Timestamp:</strong> {{ formatDate(log.created_at) }}</p>
            <hr />
          </div>
        </div>

        <!-- 닫기 버튼 -->
        <button class="close-btn" @click="closeModal">Close</button>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: "ConversationManagement",
  data() {
    return {
      conversationId: "",
      userId: "",
      conversations: [],
      chatLogs: [],
      hasMore: false,
      nextPage: 1,
      limit: 10,
      isModalOpen: false,
      isLoading: false,
    };
  },
  methods: {
    async fetchConversations() {
      const apiUrl = `http://121.176.214.36/console/api/apps/f41fe500-6dc6-4b0a-8658-f08cf038b6e9/chat-conversations?page=${this.nextPage}&limit=${this.limit}&sort_by=-created_at&annotation_status=all`;

      try {
        const response = await fetch(apiUrl, {
          headers: {
            Authorization:
              "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VyX2lkIjoiYjk1ZmE2MGEtZWFkNS00YWRmLWE0NmItYWYxMjVjZTcwZmQ1IiwiZXhwIjo0ODkxNzMzMDY4LCJpc3MiOiJTRUxGX0hPU1RFRCIsInN1YiI6IkNvbnNvbGUgQVBJIFBhc3Nwb3J0In0.mwJEw5S1krLYoXuoQOkxvlmoeMccalIeqQmtL-6q4t8",
          },
        });
        const result = await response.json();

        this.conversations = [...this.conversations, ...result.data];
        this.hasMore = result.has_more;
        this.nextPage += 1;
      } catch (error) {
        console.error("Error fetching conversations:", error);
        alert("Failed to fetch conversations. Please try again.");
      }
    },
    async fetchChatLogs(conversationId) {
      const apiUrl = `http://121.176.214.36/console/api/apps/f41fe500-6dc6-4b0a-8658-f08cf038b6e9/chat-messages?conversation_id=${conversationId}&limit=10`;

      this.isLoading = true;
      try {
        const response = await fetch(apiUrl, {
          headers: {
            Authorization:
              "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VyX2lkIjoiYjk1ZmE2MGEtZWFkNS00YWRmLWE0NmItYWYxMjVjZTcwZmQ1IiwiZXhwIjo0ODkxNzMzMDY4LCJpc3MiOiJTRUxGX0hPU1RFRCIsInN1YiI6IkNvbnNvbGUgQVBJIFBhc3Nwb3J0In0.mwJEw5S1krLYoXuoQOkxvlmoeMccalIeqQmtL-6q4t8",
          },
        });
        const result = await response.json();
        this.chatLogs = result.data;
      } catch (error) {
        console.error("Error fetching chat logs:", error);
        alert("Failed to fetch chat logs. Please check your network.");
      } finally {
        this.isLoading = false;
      }
    },
    openModal(conversationId) {
      this.chatLogs = [];
      this.isModalOpen = true;
      this.fetchChatLogs(conversationId);
    },
    closeModal() {
      this.isModalOpen = false;
    },
    formatDate(timestamp) {
      const date = new Date(timestamp * 1000);
      return date.toLocaleString();
    },
  },
  mounted() {
    this.fetchConversations();
  },
};
</script>

<style scoped>
/* 기본 스타일 */
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

.conversation-table {
  background: white;
  padding: 20px;
  border-radius: 12px;
  box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
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

.close-btn {
  background: #f44336;
  color: white;
  padding: 10px 15px;
  border: none;
  border-radius: 5px;
  cursor: pointer;
  margin-top: 20px;
}

.close-btn:hover {
  background: #d32f2f;
}

.loading-state {
  text-align: center;
  font-size: 16px;
  color: #999;
}
</style>
