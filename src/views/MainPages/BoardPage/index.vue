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
