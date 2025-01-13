<template>
    <div class="board-page">
      <Header />
      <div class="container py-5">
        <div class="row">
          <div class="col-12">
            <!-- 공지사항 카드 -->
            <div class="card mb-4">
                <div class="card-header pb-0">
                    <nav aria-label="breadcrumb">
                    <ol class="breadcrumb bg-transparent mb-0 pb-0 pt-1 px-0">
                        <li class="breadcrumb-item">
                            <router-link to="/home" class="text-dark">
                                <i class="material-icons-round">home</i>
                            </router-link>
                        </li>
                        <li class="breadcrumb-item"><router-link to="/board" class="text-dark">게시판</router-link></li>
                        <li class="breadcrumb-item active" aria-current="page">공지사항</li>
                    </ol>
                </nav>
            </div>
              <div class="card-body px-0 pb-0">
                <div class="table-responsive">
                  <table class="table table-flush" id="products-list">
                    <thead class="thead-light">
                      <tr>
                        <th class="text-left text-secondary text-sm font-weight-semibold">번호</th>
                        <th class="text-left text-secondary text-sm font-weight-semibold">제목</th>
                        <th class="text-left text-secondary text-sm font-weight-semibold">날짜</th>
                      </tr>
                    </thead>
                    <tbody>
  <tr v-for="(item, index) in boardItems" 
      :key="index" 
      :class="{ 'table-alternate': index % 2 === 1 }">
    <td class="text-sm text-dark">{{ item.id }}</td>
    <td class="text-sm text-dark">{{ item.title }}</td>
    <td class="text-sm text-dark">{{ item.date }}</td>
  </tr>
</tbody>
                  </table>
                </div>
              </div>
            </div>
  
            <!-- FAQ 카드 -->
            <div class="card">
                <div class="card-header pb-0">
                    <nav aria-label="breadcrumb">
                        <ol class="breadcrumb bg-transparent mb-0 pb-0 pt-1 px-0">
                            <li class="breadcrumb-item">
                            <router-link to="/home" class="text-dark">
                                <i class="material-icons-round">home</i>
                            </router-link>
                        </li>
                            <li class="breadcrumb-item"><router-link to="/board" class="text-dark">게시판</router-link></li>
                            <li class="breadcrumb-item active" aria-current="page">FAQ</li>
                        </ol>
                    </nav>
                </div>
              <div class="card-body">
                <div class="accordion" id="accordionFAQ">
                  <div class="accordion-item" v-for="(faq, index) in faqItems" :key="index">
                    <h2 class="accordion-header" :id="'heading' + index">
                      <button 
                        class="accordion-button collapsed" 
                        type="button" 
                        data-bs-toggle="collapse" 
                        :data-bs-target="'#collapse' + index"
                        :aria-expanded="false" 
                        :aria-controls="'collapse' + index"
                      >
                        {{ faq.question }}
                      </button>
                    </h2>
                    <div 
                      :id="'collapse' + index" 
                      class="accordion-collapse collapse"
                      :aria-labelledby="'heading' + index" 
                      data-bs-parent="#accordionFAQ"
                    >
                      <div class="accordion-body text-sm text-secondary">
                        {{ faq.answer }}
                      </div>
                    </div>
                  </div>
                </div>
              </div>
            </div>
  
            <!-- 챗봇 섹션 -->
            <div class="card mt-4">
                <div class="card-header pb-0">
                    <nav aria-label="breadcrumb">
                        <ol class="breadcrumb bg-transparent mb-0 pb-0 pt-1 px-0">
                            <li class="breadcrumb-item">
                            <router-link to="/home" class="text-dark">
                                <i class="material-icons-round">home</i>
                            </router-link>
                        </li>
                            <li class="breadcrumb-item">
                                <router-link to="/board" class="text-dark">게시판</router-link>
                            </li>
                            <li class="breadcrumb-item active" aria-current="page">문의사항</li>
                        </ol>
                    </nav>
                </div>
              <div class="card-body">
                <div class="chat-container">
                  <div class="chat-messages" ref="chatMessages">
                    <div v-for="(message, index) in messages" :key="index" 
                         :class="['message', message.sender === 'user' ? 'user-message' : 'bot-message']">
                      <div class="message-content">
                        {{ message.text }}
                      </div>
                      <div class="message-time">
                        {{ message.time }}
                      </div>
                    </div>
                  </div>
                  <div class="chat-input">
                    <div class="input-group">
                      <input 
                        type="text" 
                        class="form-control"
                        v-model="newMessage"
                        @keyup.enter="sendMessage"
                        placeholder="메시지를 입력하세요..."
                      >
                      <button 
                        class="btn custom-button mb-0"
                        @click="sendMessage"
                      >
                        전송
                      </button>
                    </div>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </template>
  
  
  <script>
  import Header from '../HomePage/components/Header.vue'
  
  export default {
    name: "BoardPage",
    components: {
      Header
    },
    data() {
      return {
        boardItems: [
          {
            id: 1,
            title: '[안내] 메이커스랩 리뉴얼 작업 차질(이미지&소개카드&이미지)',
            date: '2025.01.07 13:30'
          },
          {
            id: 2,
            title: '[안내] 메이커스랩 리뉴얼 작업 차질(이미지&소개카드&이미지)',
            date: '2025.01.07 13:30'
          },
          {
            id: 3,
            title: '[안내] 메이커스랩 리뉴얼 작업 차질(이미지&소개카드&이미지)',
            date: '2025.01.07 13:30'
          }
        ],
        faqItems: [
          {
            question: 'What is a Payment Gateway?',
            answer: 'A payment gateway is a merchant service that processes credit card payments for ecommerce sites and traditional brick and mortar stores.'
          },
          {
            question: 'Do I need to pay to Instapay even when there is no transaction going on in my business?',
            answer: 'No, you only pay for actual transactions processed through the gateway.'
          },
          {
            question: 'What platforms does Instapay payment gateway support?',
            answer: 'Instapay supports multiple platforms including web, mobile, and in-store payment solutions.'
          },
          {
            question: 'Does Instapay provide international payments support?',
            answer: 'Yes, Instapay supports international payments across multiple currencies.'
          },
          {
            question: 'Is there any setup fee or annual maintainance fee that I need to pay regularly?',
            answer: 'Please contact our support team for detailed information about fees and charges.'
          }
        ],
        messages: [
        {
          text: '안녕하세요! 무엇을 도와드릴까요?',
          sender: 'bot',
          time: new Date().toLocaleTimeString()
        }
      ],
      newMessage: ''
    }
  },

  methods: {
    async sendMessage() {
      if (!this.newMessage.trim()) return;

      // 사용자 메시지 추가
      this.messages.push({
        text: this.newMessage,
        sender: 'user',
        time: new Date().toLocaleTimeString()
      });

      const userMessage = this.newMessage;
      this.newMessage = '';

      // 챗봇 응답 처리 (여기에 실제 ChatGPT API 호출 로직 추가 필요)
      setTimeout(() => {
        this.messages.push({
          text: '죄송합니다. 현재 챗봇 서비스 준비 중입니다.',
          sender: 'bot',
          time: new Date().toLocaleTimeString()
        });
      }, 1000);

      // 스크롤을 최신 메시지로 이동
      this.$nextTick(() => {
        const chatMessages = this.$refs.chatMessages;
        chatMessages.scrollTop = chatMessages.scrollHeight;
      });
    }
    }
  }

</script>

  
  <style scoped>
  .board-page {
    padding-top: 70px;
    background-color: #f8f9fa;
    min-height: 100vh;
  }
  
  .table-flush td {
  padding: 1rem 1.5rem;
  border-bottom: 1px solid #e9ecef;
}

    .table-flush thead th {
  padding: 1rem 1.5rem;
  border-bottom: 1px solid #e9ecef;
}

.table-alternate {
  background-color: #F3FAF7;
}

.text-secondary {
  color: #67748e !important;
}
  
.accordion-button:not(.collapsed) {
  background-color: #F3FAF7;
  color: #344767;
}
  
  .accordion-button:focus {
    box-shadow: none;
    border-color: rgba(0, 0, 0, 0.125);
  }
  .chat-container {
  height: 400px;
  display: flex;
  flex-direction: column;
}

.chat-messages {
  flex: 1;
  overflow-y: auto;
  padding: 1rem;
  display: flex;
  flex-direction: column;
  gap: 1rem;
}

.message {
  max-width: 70%;
  padding: 0.8rem 1rem;
  border-radius: 1rem;
  margin-bottom: 0.5rem;
}

.user-message {
  align-self: flex-end;
  background-color: #7b809a;
  color: white;
}

.bot-message {
  align-self: flex-start;
  background-color: #f8f9fa;
  color: #344767;
}

.message-content {
  margin-bottom: 0.3rem;
}

.message-time {
  font-size: 0.7rem;
  opacity: 0.7;
  text-align: right;
}

.chat-input {
  padding: 1rem;
  border-top: 1px solid #e9ecef;
}

.input-group .form-control {
  border-right: none;
}

.input-group .btn {
  margin: 0;
  border-radius: 0 0.5rem 0.5rem 0;
}

.custom-button {
  background-color: #5CB494;
  color: white;
}

.custom-button:hover {
  background-color: #4a9077;
}

  </style>
  