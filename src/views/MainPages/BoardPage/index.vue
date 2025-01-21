<template>
<<<<<<< HEAD
    <div class="board-page">
      <Header />
      <div class="container py-5">
        <div class="row">
          <div class="col-12">
            <!-- 내 문의함 카드 -->
            <div class="card mb-4" id="my-inquiries-section">
              <div class="card-header d-flex justify-content-between align-items-center">
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
                    <li class="breadcrumb-item active" aria-current="page">내 문의함</li>
                  </ol>
                </nav>
                <button class="btn custom-button" @click="openNewInquiryModal">+ New</button>
              </div>
              <div class="card-body px-0 pb-0">
                <div class="table-responsive">
                  <table class="table table-flush" id="my-questions-list">
                    <thead class="thead-light">
                      <tr>
                        <th class="text-left text-secondary text-sm font-weight-semibold">번호</th>
                        <th class="text-left text-secondary text-sm font-weight-semibold">제목</th>
                        <th class="text-left text-secondary text-sm font-weight-semibold">상태</th>
                        <th class="text-left text-secondary text-sm font-weight-semibold">날짜</th>
                        <th class="text-left text-secondary text-sm font-weight-semibold"></th>
                      </tr>
                    </thead>
                    <tbody>
                      <tr v-for="question in myQuestions" :key="question.id">
                        <td>{{ question.id }}</td>
                        <td>
                          <!-- 제목 클릭 시 모달 열기 -->
                          <a href="#" @click.prevent="viewInquiryDetails(question)">{{ question.title }}</a>
                        </td>
                        <td>{{ question.status }}</td>
                        <td>{{ question.date }}</td>
                        <td>
                          <button 
                            class="btn btn-sm btn-outline-danger" 
                            @click="deleteQuestion(question.id)"
                            title="삭제하기">
                            X
                          </button>
                        </td>
                      </tr>
                    </tbody>
                  </table>
                </div>
              </div>


              <!-- 모달 -->
              <div v-if="selectedQuestion" class="inquiry-modal-overlay" @click.self="closeInquiryModal">
                <div class="inquiry-modal-container">
                  <div class="inquiry-modal-header">
                    <h5 class="modal-title">문의 상세</h5>
                    <button type="button" class="btn-close" @click="closeInquiryModal"></button>
                  </div>
                  <div class="inquiry-modal-body">
                    <div class="inquiry-modal-content-section">
                      <h6>제목</h6>
                      <p>{{ selectedQuestion.title }}</p>
                    </div>
                    <hr />
                    <div class="inquiry-modal-content-section">
                      <h6>내용</h6>
                      <p>{{ selectedQuestion.content }}</p>
                    </div>
                    <hr />
                    <div class="inquiry-modal-content-section">
                      <h6>작성 날짜</h6>
                      <p>{{ selectedQuestion.date }}</p>
                    </div>
                    <hr />
                    <div class="inquiry-modal-content-section">
                      <h6>상태</h6>
                      <p>{{ selectedQuestion.status }}</p>
                    </div>
                  </div>
                  <div class="inquiry-modal-footer">
                    <button type="button" class="btn btn-secondary" @click="closeInquiryModal">닫기</button>
                  </div>
                </div>
              </div>
            </div>
            <!-- 공지사항 카드 -->
            <div class="card mb-4" id="notice-section">
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
=======
  <div class="board-page">
    <Header />
    <div class="container py-5">
      <div class="row">
        <div class="col-12">
          <!-- 공지사항 카드 -->
          <div class="card mb-4" id="notice-section">
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
>>>>>>> 40e8279ec2bb1e2743ba1f5bbf559c018691921a
                    <tr v-for="(item, index) in boardItems" 
                        :key="index" 
                        :class="{ 'table-alternate': index % 2 === 1 }">
                      <td class="text-sm text-dark">{{ item.id }}</td>
                      <td class="text-sm text-dark">{{ item.title }}</td>
                      <td class="text-sm text-dark">{{ item.date }}</td>
                    </tr>
                  </tbody>
<<<<<<< HEAD
                  </table>
                </div>
=======
                </table>
>>>>>>> 40e8279ec2bb1e2743ba1f5bbf559c018691921a
              </div>
            </div>
          </div>

           <!-- FAQ 카드 -->
           <div class="card" id="faq-section">
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

          <!-- 1:1 문의하기 섹션 -->
          <div class="card md-4" id="inquiry-section">
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
                  <li class="breadcrumb-item active" aria-current="page">1:1 문의하기</li>
                </ol>
              </nav>
            </div>
            <div class="card-body p-0">
    <div class="inquiry-container">
      <div class="illustration-wrapper">
        <img src="@/assets/img/inquiry.jpg" alt="Inquiry Illustration" class="illustration-image">
      </div>
      <div class="form-wrapper">
        <h2 class="inquiry-title">1 : 1   문의하기</h2>
                  <form @submit.prevent="submitInquiry">
                    <div class="form-group">
                      <label>제목</label>
                      <input 
                        type="text" 
                        class="form-control" 
                        placeholder="제목을 입력해주세요"
                        v-model="inquiry.title"
                      >
                    </div>
                    <div class="form-group">
                      <label>문의내용</label>
                      <textarea 
                        class="form-control" 
                        rows="6"
                        placeholder="문의내용을 입력해주세요"
                        v-model="inquiry.content"
                      ></textarea>
                    </div>
                    <div class="form-group">
                      <label>파일 첨부</label>
                      <div class="file-upload-group">
                        <input 
                          type="text" 
                          class="form-control" 
                          placeholder="파일을 선택해주세요" 
                          readonly
                          v-model="selectedFileName"
                        >
                        <button 
                          type="button" 
                          class="btn btn-upload"
                          @click="$refs.fileInput.click()"
                        >
                          <i class="material-icons">add</i>
                          파일 업로드
                        </button>
                        <input 
                          type="file" 
                          ref="fileInput" 
                          @change="handleFileChange" 
                          style="display: none"
                        >
                      </div>
                    </div>
                    <button type="submit" class="btn btn-submit">
                      문의하기
                    </button>
                  </form>
                </div>
              </div>
              <!-- New Inquiry Modal -->
              <div v-if="isModalOpen" class="modal-overlay">
                <div class="modal-container">
                  <div class="modal-header">
                    <h5>1:1 문의</h5>
                    <button class="close-button" @click="closeNewInquiryModal">&times;</button>
                  </div>
                  <div class="modal-body">
                    <div class="form-group">
                      <label for="inquiry-title">제목</label>
                      <input type="text" id="inquiry-title" v-model="newInquiry.title" class="form-control" placeholder="제목을 입력해주세요" />
                    </div>
                    <div class="form-group">
                      <label for="inquiry-content">내용</label>
                      <textarea id="inquiry-content" v-model="newInquiry.content" class="form-control" rows="5" placeholder="내용을 입력해주세요"></textarea>
                    </div>
                  </div>
                  <div class="modal-footer">
                    <button class="btn btn-secondary" @click="closeNewInquiryModal">취소</button>
                    <button class="btn custom-button" @click="submitNewInquiry">등록</button>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
<<<<<<< HEAD
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
        myQuestions: [
          {
            id: 1,
            title: 'Spring Boot Container 추가배포 중 에러 문의',
            status: '완료',
            date: '2024.12.23 16:19',
            content: 'Spring Boot 애플리케이션을 Docker 컨테이너로 배포하는 과정에서 발생하는 오류에 대한 상세 문의입니다.',
          },
          {
            id: 2,
            title: 'API 응답 속도 개선 방안',
            status: '진행 중',
            date: '2024.12.20 10:45',
            content: 'API 호출 시 응답 속도가 느려지는 문제에 대한 해결 방안을 요청드립니다.',
          },
        ],
        selectedQuestion: null,
        newInquiry: {
            title: '',
            content: '',
          },
        isModalOpen: false,
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
=======
  </div>
</template>

<script>
import Header from '../HomePage/components/Header.vue'

export default {
  name: "BoardPage",
  components: {
    Header,
  },
  data() {
    return {
      inquiry: {
        title: '',
        content: '',
        file: null
      },
      selectedFileName: '',
      boardItems: [
>>>>>>> 40e8279ec2bb1e2743ba1f5bbf559c018691921a
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
      ]
    }
  },
  created() {
    this.openedFaq = new Array(this.faqItems.length).fill(false);
  },
  methods: {
<<<<<<< HEAD
    viewInquiryDetails(question) {
      this.selectedQuestion = question;
    },
    // 문의 삭제
    deleteQuestion(questionId) {
      if (confirm('정말로 삭제하시겠습니까?')) {
        this.myQuestions = this.myQuestions.filter((question) => question.id !== questionId);
        alert('문의가 삭제되었습니다.');
      }
    },
    closeInquiryModal() {
      this.selectedQuestion = null;
    },
    openNewInquiryModal() {
      this.isModalOpen = true;
    },
    closeNewInquiryModal() {
      this.isModalOpen = false;
      this.newInquiry = { title: '', content: '' };
    },
    submitNewInquiry() {
      if (!this.newInquiry.title || !this.newInquiry.content) {
        alert('모든 항목을 입력해주세요.');
        return;
      }

      this.myQuestions.push({
        id: this.myQuestions.length + 1,
        title: this.newInquiry.title,
        content: this.newInquiry.content,
        date: new Date().toLocaleString('ko-KR', {
          year: 'numeric',
          month: '2-digit',
          day: '2-digit',
          hour: '2-digit',
          minute: '2-digit',
          second: '2-digit',
        }),
        status: '대기 중',
      });
      this.closeNewInquiryModal();
    },
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
=======
    handleFileChange(event) {
      const file = event.target.files[0]
      if (file) {
        this.inquiry.file = file
        this.selectedFileName = file.name
      }
    },
    submitInquiry() {
      console.log('문의 제출:', this.inquiry)
    },
    toggleFaq(index) {
      this.openedFaq = this.openedFaq.map((item, i) => i === index ? !item : false);
>>>>>>> 40e8279ec2bb1e2743ba1f5bbf559c018691921a
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

.card {
  position: relative;
  background: white;
  border-radius: 15px;
  box-shadow: 0 20px 27px 0 rgba(0, 0, 0, 0.05);
  overflow: hidden;
  margin-top: 3rem;
}

.card-body {
  padding: 0;
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

.inquiry-container {
  display: flex;
  align-items: stretch;
  padding: 6rem;
  background-color: #f8f9fa;
}

.illustration-wrapper {
  flex: 1.2;
  border-radius: 15px;
  overflow: hidden;
  box-shadow: 0 10px 20px rgba(0, 0, 0, 0.1);
  margin-left: 30px;
  margin: 0.5rem;
}

.illustration-image {
  width: 100%;
  height: 100%;
  object-fit: cover;
  border-radius: 15px;
}

.form-wrapper {
  flex: 1;
  padding: 3rem;
  background: white;
  border-radius: 15px;
  box-shadow: 0 10px 20px rgba(0, 0, 0, 0.05);
  margin: 3rem;
  margin-left: -30px;
}

.form-group {
  margin-bottom: 1.5rem;
}

.form-group label {
  display: block;
  color: #344767;
  font-size: 0.875rem;
  margin-bottom: 0.5rem;
  font-weight: 500;
}

.form-control {
  width: 100%;
  padding: 0.75rem;
  border: 1px solid #d2d6da;
  border-radius: 0.5rem;
  font-size: 0.875rem;
  transition: all 0.2s ease;
}

.form-control:focus {
  border-color: #40c4aa;
  box-shadow: 0 0 0 2px rgba(64, 196, 170, 0.25);
}

.file-upload-group {
  display: flex;
  align-items: center;
  gap: 0.5rem;
}

.file-upload-group input[type="text"],
.file-upload-group button {
  height: 45px;
  box-sizing: border-box;
  font-size: 0.9rem;
  line-height: 1.2;
  padding: 0.75rem 1rem;
}

.file-upload-group input[type="text"] {
  flex: 1;
  border: 1px solid #e0e0e0;
  border-radius: 8px;
}

.btn-upload {
  display: inline-flex;
  align-items: center;
  gap: 0.5rem;
  background: #40c4aa;
  color: white;
  border: none;
  border-radius: 8px;
  cursor: pointer;
  white-space: nowrap;
  margin-bottom: 0px;
  transition: background 0.2s ease;
}

.btn-upload:hover {
  background: #35a892;
}
.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: rgba(0, 0, 0, 0.5);
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 1000;
}

.modal-container {
  background: white;
  width: 500px;
  border-radius: 8px;
  overflow: hidden;
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.2);
}

.modal-header {
  padding: 1rem;
  background: #f5f5f5;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.modal-body {
  padding: 1rem;
}

.modal-footer {
  padding: 1rem;
  display: flex;
  justify-content: flex-end;
  gap: 1rem;
  background: #f5f5f5;
}

.close-button {
  background: none;
  border: none;
  font-size: 1.5rem;
  cursor: pointer;
}

.custom-button {
  background-color: #5CB494;
  color: white;
}

.custom-button:hover {
  background-color: #4a9077;
}

.btn.active {
  background-color: #5CB494;
  color: white;
}
/* 모달 배경 */
.inquiry-modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: rgba(0, 0, 0, 0.5);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1050;
}

/* 모달 컨테이너 */
.inquiry-modal-container {
  background: #fff;
  width: 50%; /* 원하는 크기로 조정 가능 */
  max-width: 800px;
  border-radius: 8px;
  overflow: hidden;
  box-shadow: 0 4px 10px rgba(0, 0, 0, 0.25);
  display: flex;
  flex-direction: column;
}

/* 모달 헤더 */
.inquiry-modal-header {
  padding: 16px;
  background: #f5f5f5;
  border-bottom: 1px solid #ddd;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.inquiry-modal-header h5 {
  margin: 0;
  font-size: 1.25rem;
}

.inquiry-modal-header .btn-close {
  background: none;
  border: none;
  font-size: 1.5rem;
  cursor: pointer;
}

/* 모달 본문 */
.inquiry-modal-body {
  padding: 16px;
  flex-grow: 1;
  overflow-y: auto;
}

.inquiry-modal-content-section h6 {
  margin: 0;
  font-weight: bold;
  font-size: 1rem;
}

.inquiry-modal-content-section p {
  margin: 4px 0 0;
  font-size: 0.9rem;
  color: #333;
}

hr {
  border: 0;
  border-top: 1px solid #ddd;
  margin: 16px 0;
}

/* 모달 푸터 */
.inquiry-modal-footer {
  padding: 16px;
  background: #f5f5f5;
  text-align: right;
}

/*삭제 버튼튼*/ 
.btn-outline-danger {
  color: #ff4d4d;
  border: 1px solid #ff4d4d;
  font-size: 0.8rem;
  padding: 2px 8px;
  border-radius: 4px;
  cursor: pointer;
}

.btn-outline-danger:hover {
  background: #ff4d4d;
  color: #fff;
}
</style>

<<<<<<< HEAD
  
=======
.btn-upload i {
  font-size: 1.2rem;
}

.btn-submit {
  width: 100%;
  padding: 0.875rem;
  background: #40c4aa;
  color: white;
  border: none;
  border-radius: 0.5rem;
  font-size: 0.875rem;
  font-weight: 500;
  margin-top: 0.5rem;
  cursor: pointer;
  transition: background 0.2s ease;
}

.btn-submit:hover {
  background: #35a892;
}

.inquiry-title {
  color: #344767;
  font-size: 1.5rem;
  font-weight: 600;
  margin-bottom: 2rem;
}

@media (max-width: 992px) {
  .inquiry-container {
    flex-direction: column;
    padding: 1.5rem;
  }
  
  .illustration-wrapper {
    height: 300px;
  }
  
  .form-wrapper {
    margin: 0;
    padding: 1.5rem;
  }
}

@media (max-width: 576px) {
  .file-upload-group {
    flex-direction: column;
  }
  
  .btn-upload {
    width: 100%;
    justify-content: center;
  }
  
  .illustration-wrapper {
    height: 200px;
  }
  
  .inquiry-container {
    padding: 1rem;
  }
}

</style>
>>>>>>> 40e8279ec2bb1e2743ba1f5bbf559c018691921a
