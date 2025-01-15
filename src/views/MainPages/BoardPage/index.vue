<template>
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
          <div class="card mt-4" id="inquiry-section">
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
            <div class="card-body">
              <form @submit.prevent="submitInquiry">
                <div class="input-group input-group-static mb-4">
                  <label>제목</label>
                  <input type="text" class="form-control" v-model="inquiry.title" placeholder="ex) 제목">
                </div>

                <div class="input-group input-group-static mb-4">
                  <label>문의내용</label>
                  <textarea class="form-control" rows="4" v-model="inquiry.content"></textarea>
                </div>

                <div class="input-group input-group-static mb-4">
                  <label>이메일 정보</label>
                  <input type="email" class="form-control" v-model="inquiry.email" placeholder="ex) michael@creative-tim.com">
                  <button type="button" class="btn bg-gradient-light ms-2" @click="addEmail">
                    <i class="material-icons">add</i> 이메일 추가
                  </button>
                </div>

                <div class="d-flex justify-content-end">
                  <button type="submit" class="btn bg-gradient-success">
                    문의하기
                  </button>
                </div>
              </form>
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
        inquiry: {
        title: '',
        content: '',
        email: ''
      },
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
    submitInquiry() {
      // 문의하기 제출 로직 구현
      console.log('문의 제출:', this.inquiry)
    },
    addEmail() {
      // 이메일 추가 로직 구현
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
  .input-group-static {
  position: relative;
}

.input-group-static label {
  color: #344767;
  font-size: 0.875rem;
  font-weight: 500;
  margin-bottom: 0.5rem;
}

.form-control {
  border: 1px solid #d2d6da;
  padding: 0.75rem;
  line-height: 1.4;
  font-size: 0.875rem;
  border-radius: 0.5rem;
  background-color: #fff;
}

.form-control:focus {
  border-color: #5CB494;
  box-shadow: 0 0 0 2px rgba(92, 180, 148, 0.25);
}

textarea.form-control {
  min-height: 120px;
  resize: vertical;
}

.btn {
  text-transform: none;
  font-weight: 500;
  padding: 0.75rem 1.5rem;
}

.bg-gradient-success {
  background-image: linear-gradient(310deg, #5CB494 0%, #4a9077 100%);
  color: #fff;
}

.bg-gradient-light {
  background-image: linear-gradient(310deg, #e9ecef 0%, #dee2e6 100%);
  color: #344767;
}

  </style>
  