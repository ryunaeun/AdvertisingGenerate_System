<template>
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
                        <a href="#" @click.prevent="viewInquiryDetails(question)">{{ question.title }}</a>
                      </td>
                      <td :style="{ color: question.status === '답변 완료' ? '#5CB494' : '' }">
                          {{ question.status }}
                      </td>
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

            <!-- 문의 상세 모달 -->
<div v-if="selectedQuestion" class="inquiry-modal-overlay" @click.self="closeInquiryModal">
  <div class="inquiry-modal-container">
    <div class="inquiry-modal-header">
      <h5 class="modal-title">문의 상세</h5>
      <button type="button" class="btn-close" @click="closeInquiryModal"></button>
    </div>
    <div class="inquiry-modal-body">
      <div class="inquiry-modal-content-section">
        <h6>제목</h6>
        <input 
          v-if="selectedQuestion.status === '대기 중'"
          type="text"
          class="form-control"
          v-model="editingInquiry.title"
        />
        <p v-else>{{ selectedQuestion.title }}</p>
      </div>
      <hr />
      <div class="inquiry-modal-content-section">
        <h6>내용</h6>
        <textarea 
          v-if="selectedQuestion.status === '대기 중'"
          class="form-control"
          v-model="editingInquiry.content"
          rows="5"
        ></textarea>
        <p v-else>{{ selectedQuestion.content }}</p>
      </div>
      <hr />
      <div class="inquiry-modal-content-section">
        <h6>작성 날짜</h6>
        <p>{{ selectedQuestion.date }}</p>
      </div>
      <hr />
      <div class="inquiry-modal-content-section">
        <h6>상태</h6>
        <p :class="{'text-success': selectedQuestion.status === '답변 완료'}">
          {{ selectedQuestion.status }}
        </p>
      </div>
      <hr v-if="selectedQuestion.status === '답변 완료'" />
      <div v-if="selectedQuestion.status === '답변 완료'" class="inquiry-modal-content-section">
        <h6>답변</h6>
        <div v-for="(reply, index) in selectedQuestion.replies" :key="index" class="reply-item">
          <p>{{ reply.content }}</p>
          <small>{{ reply.createdAt }}</small>
        </div>
      </div>
    </div>
    <div class="modal-footer">
      <button 
        v-if="selectedQuestion.status === '대기 중'" 
        class="btn custom-button" 
        @click="updateInquiry"
      >
        수정
      </button>
      <button class="btn btn-secondary" @click="closeInquiryModal">닫기</button>
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
                    <tr v-for="(item, index) in boardItems" 
                        :key="item.noticeId" 
                        :class="{ 'table-alternate': index % 2 === 1 }"
                        style="cursor: pointer"
                        @click="viewNoticeDetails(item.noticeId)">
                      <td class="text-sm text-dark">{{ item.noticeId }}</td>
                      <td class="text-sm text-dark">{{ item.title }}</td>
                      <td class="text-sm text-dark">{{ formatDate(item.createdAt) }}</td>
                    </tr>
                  </tbody>
                </table>
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
                  <li class="breadcrumb-item">
                    <router-link to="/board" class="text-dark">게시판</router-link>
                  </li>
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
                      :aria-controls="'collapse' + index">
                      {{ faq.question }}
                    </button>
                  </h2>
                  <div 
                    :id="'collapse' + index" 
                    class="accordion-collapse collapse"
                    :aria-labelledby="'heading' + index" 
                    data-bs-parent="#accordionFAQ">
                    <div class="accordion-body text-sm text-secondary">
                      {{ faq.answer }}
                    </div>
                  </div>
                </div>
              </div>
            </div>
          </div>

          <!-- 새 문의 모달 -->
          <div v-if="isModalOpen" class="modal-overlay">
            <div class="modal-container">
              <div class="modal-header">
                <h5>1:1 문의</h5>
                <button class="close-button" @click="closeNewInquiryModal">&times;</button>
              </div>
              <div class="modal-body">
                <div class="form-group">
                  <label for="inquiry-title">제목</label>
                  <input 
                    type="text" 
                    id="inquiry-title" 
                    v-model="newInquiry.title" 
                    class="form-control" 
                    placeholder="제목을 입력해주세요" />
                </div>
                <div class="form-group">
                  <label for="inquiry-content">내용</label>
                  <textarea 
                    id="inquiry-content" 
                    v-model="newInquiry.content" 
                    class="form-control" 
                    rows="5" 
                    placeholder="내용을 입력해주세요"></textarea>
                </div>
              </div>
              <div class="modal-footer">
                <button class="btn btn-secondary" @click="closeNewInquiryModal">취소</button>
                <button class="btn custom-button" @click="submitNewInquiry">등록</button>
              </div>
            </div>
          </div>

          <!-- 공지사항 상세 모달 -->
          <div v-if="selectedNotice" class="modal-overlay">
            <div class="modal-container">
              <div class="modal-header">
                <h5>공지사항 상세</h5>
                <button class="close-button" @click="closeNoticeModal">&times;</button>
              </div>
              <div class="modal-body">
                <div class="notice-detail-section">
                  <div class="detail-item">
                    <h6>제목</h6>
                    <p>{{ selectedNotice.title }}</p>
                  </div>
                  <hr/>
                  <div class="detail-item">
                    <h6>내용</h6>
                    <div class="content-box">
                      <p>{{ selectedNotice.content }}</p>
                    </div>
                  </div>
                  <hr/>
                  <div class="detail-item">
                    <h6>작성일자</h6>
                    <p>{{ formatDate(selectedNotice.createdAt) }}</p>
                  </div>
                </div>
              </div>
              <div class="modal-footer">
                <button class="btn btn-secondary" @click="closeNoticeModal">닫기</button>
              </div>
            </div>
          </div>

        </div>
      </div>
    </div>
  </div>
</template>


<script>
import apiClient from "@/api/axiosClient";
import Header from '../HomePage/components/Header.vue'

export default {
  name: "BoardPage",
  components: {
    Header
  },
  data() {
    return {
      myQuestions: [],
      selectedQuestion: null,
      newInquiry: {
        title: '',
        content: '',
      },
      isEditMode: false,
      editingInquiry: {
        title: '',
        content: ''
      },
      isModalOpen: false,
      boardItems: [],
      faqItems: [
        {
          question: '무료로 AI 광고를 생성할 수 있나요?',
          answer: '네, 무료 플랜을 제공하고 있습니다. 더 많은 기능과 사용량이 필요하시다면 유료 플랜을 고려해보세요.'
        },
        {
          question: '유료 플랜의 종류와 가격은 어떻게 되나요?',
          answer: '일반적으로 Basic, Enterprise Plan을 제공하고 있습니다. 베이직 플랜은 월 원, Basic Plan은 월 199,800원, Enterprise Plan은 영업팀에 문의가 필요합니다. 각 플랜마다 제공되는 기능과 사용량이 다릅니다.'
        },
        {
          question: '각 플랜별로 어떤 기능 차이가 있나요?',
          answer: '무료 플랜은 기본 AI 광고 생성과 제한된 템플릿을 제공합니다. Basic Plan은 더 많은 템플릿과 기본 편집 도구를 제공합니다. Enterprise Plan은 무제한 생성, 고급 AI 기능, 상세 성과 분석, 우선 고객 지원을 제공합니다.'
        },
        {
          question: 'AI가 생성한 광고를 수정할 수 있나요?',
          answer: '네, 모든 플랜에서 AI가 생성한 광고를 수정할 수 있습니다. 다만, 고급 편집 기능은 Enterprise Plan 이상에서 제공됩니다.'
        },
        {
          question: '어떤 광고 형식을 지원하나요?',
          answer: '비디오 광고, 이미지 광고, 광고 스토리 등 다양한 형식을 지원합니다. 구체적인 지원 형식은 선택하신 플랜에 따라 다를 수 있습니다.'
        }
      ],
      selectedNotice: null,
    }
  },
  mounted() {
    this.fetchNotices();
    this.fetchMyInquiries();
  },
  methods: {
    async fetchMyInquiries() {
  try {
    const response = await apiClient.get('/users/myboard', {
      headers: {
        'Authorization': `Bearer ${sessionStorage.getItem("accessToken")}`
      },
      params: {
        page: 0,
        size: 20,
        sort: [],
        isDescending: false
      }
    });

    if (!response.data) {
      console.error('응답 데이터가 없습니다');
      this.myQuestions = [];
      return;
    }

    if (!Array.isArray(response.data)) {
      console.error('응답 데이터 형식이 올바르지 않습니다:', response.data);
      this.myQuestions = [];
      return;
    }
    

    this.myQuestions = response.data.map(inquiry => {
  console.log(response.data)
  return {
    id: inquiry.boardOrder,
    title: inquiry.title,
    content: inquiry.content,
    date: new Date(inquiry.createdAt).toLocaleString('ko-KR'),
    status: inquiry.breply ? '답변 완료' : '대기 중'
  };
});

  } catch (error) {
    console.error('API 호출 중 오류 발생:', error);
    if (error.response) {
      console.error('서버 응답 상태:', error.response.status);
      console.error('서버 응답 데이터:', error.response.data);
    }
    this.myQuestions = [];
    alert('문의 목록을 불러오는데 실패했습니다.');
  }
},

async viewInquiryDetails(question) {
  try {
    const response = await apiClient.get(`/users/myboard/${question.id}`, {
      headers: {
        'Authorization': `Bearer ${sessionStorage.getItem("accessToken")}`
      }
    });
    
    if (!response.data || !response.data.board) {
      throw new Error('Invalid response data structure');
    }

    console.log('Response data:', response.data);
    const hasReplies = response.data.replies && response.data.replies.replyId;
    
    this.selectedQuestion = {
      id: response.data.board.boardOrder || question.id,
      title: response.data.board.title || '',
      content: response.data.board.content || '',
      date: response.data.board.createdAt ? 
        new Date(response.data.board.createdAt).toLocaleString('ko-KR') : '',
      status: hasReplies ? '답변 완료' : '대기 중',
      replies: hasReplies ? [{
        content: response.data.replies.content || '',
        createdAt: response.data.replies.createdAt ? 
          new Date(response.data.replies.createdAt).toLocaleString('ko-KR') : ''
      }] : []
    };

    this.editingInquiry = {
      title: this.selectedQuestion.title,
      content: this.selectedQuestion.content
    };
  } catch (error) {
    console.error('Error details:', error);
    if (error.response) {
      console.error('Server error response:', error.response.data);
      alert(error.response.data);
    } else {
      alert('문의 상세 정보를 불러오는데 실패했습니다.');
    }
  }
},

    async updateInquiry() {
  try {
    await apiClient.post(`/users/myboard/${this.selectedQuestion.id}/update`, {
      title: this.editingInquiry.title,
      content: this.editingInquiry.content
    }, {
      headers: {
        'Authorization': `Bearer ${sessionStorage.getItem("accessToken")}`,
        'Content-Type': 'application/json'
      }
    });
    
    alert('문의가 수정되었습니다.');
    this.closeInquiryModal();
    this.fetchMyInquiries();
  } catch (error) {
    if (error.response) {
      alert(error.response.data);
    } else {
      alert('문의 수정 중 오류가 발생했습니다.');
    }
  }
},

    async deleteQuestion(questionId) {
      if (confirm('정말로 삭제하시겠습니까?')) {
        try {
          await apiClient.delete(`/users/myboard/${questionId}`, {
            headers: {
              'Authorization': `Bearer ${sessionStorage.getItem("accessToken")}`
            }
          });
          
          alert('문의가 삭제되었습니다.');
          this.fetchMyInquiries();
        } catch (error) {
          if (error.response) {
            alert(error.response.data);
          } else {
            alert('문의 삭제에 실패했습니다.');
          }
        }
      }
    },

    closeInquiryModal() {
      this.selectedQuestion = null;
      this.isEditMode = false;
      this.editingInquiry = {
        title: '',
        content: ''
      };
    },

    openNewInquiryModal() {
      this.isModalOpen = true;
    },

    closeNewInquiryModal() {
      this.isModalOpen = false;
      this.newInquiry = {
        title: '',
        content: ''
      };
    },

    async submitNewInquiry() {
      if (!this.newInquiry.title || !this.newInquiry.content) {
        alert('모든 항목을 입력해주세요.');
        return;
      }

      try {
        await apiClient.post('/users/myboard/write', 
          {
            title: this.newInquiry.title,
            content: this.newInquiry.content
          },
          {
            headers: {
              'Authorization': `Bearer ${sessionStorage.getItem("accessToken")}`,
              'Content-Type': 'application/json'
            }
          }
        );
        
        alert('문의가 등록되었습니다.');
        this.closeNewInquiryModal();
        this.fetchMyInquiries();
      } catch (error) {
        if (error.response) {
          alert(error.response.data);
        } else {
          alert('문의 등록 중 오류가 발생했습니다.');
        }
      }
    },

    async fetchNotices() {
      try {
        const response = await apiClient.get('/users/notice', {
          headers: {
            'Authorization': `Bearer ${sessionStorage.getItem("accessToken")}`
          },
          params: {
            page: 0,
            size: 20,
            sort: 'createdAt',
            isDescending: false
          }
        });
        
        if (response.data) {
          this.boardItems = response.data.map(notice => ({
            noticeId: notice.noticeOrder,
            title: notice.title,
            createdAt: notice.createdAt
          }));
        }
      } catch (error) {
        console.error('공지사항 조회 오류:', error);
        if (error.response) {
          console.error('서버 응답:', error.response.data);
          console.error('상태 코드:', error.response.status);
        }
        this.boardItems = [];
      }
    },

    formatDate(dateString) {
      return new Date(dateString).toLocaleString('ko-KR');
    },

    async viewNoticeDetails(noticeId) {
      try {
        const response = await apiClient.get(`/users/notice/${noticeId}`, {
          headers: {
            'Authorization': `Bearer ${sessionStorage.getItem("accessToken")}`
          }
        });
        
        this.selectedNotice = {
          noticeId: response.data.notice.noticeOrder,
          title: response.data.notice.title,
          content: response.data.notice.content,
          createdAt: response.data.notice.createdAt
        };
      } catch (error) {
        console.error('공지사항 상세 조회 오류:', error);
        alert('공지사항을 불러오는데 실패했습니다.');
      }
    },

    closeNoticeModal() {
      this.selectedNotice = null;
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