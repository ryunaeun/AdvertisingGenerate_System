<template>
  <div>
    <h1 class="text-2xl font-bold mb-4">Notice Management</h1>

    <!-- 공지사항 작성 -->
    <div class="notice-form">
      <h2 class="text-lg font-bold mb-2">Write a New Notice</h2>
      <input
        type="text"
        v-model="newNotice.title"
        placeholder="Notice Title"
        class="input-title"
      />
      <textarea
        v-model="newNotice.content"
        placeholder="Notice Content"
        class="input-content"
      ></textarea>
      <button class="submit-btn" @click="submitNotice">Submit</button>
    </div>

    <hr class="my-8" />

    <!-- 공지사항 목록 -->
    <div class="notice-list">
      <h2 class="text-lg font-bold mb-4">All Notices</h2>
      <table class="notice-table">
        <thead>
          <tr>
            <th>ID</th>
            <th>Title</th>
            <th>Content</th>
            <th>Created At</th>
            <th>Actions</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="notice in notices" :key="notice.noticeId">
            <td>{{ notice.noticeId }}</td>
            <td>{{ notice.title }}</td>
            <td>{{ notice.content }}</td>
            <td>{{ formatDate(notice.createdAt) }}</td>
            <td>
              <button class="edit-btn" @click="openEditModal(notice)">
                Edit
              </button>
              <button class="delete-btn" @click="deleteNotice(notice.noticeId)">
                Delete
              </button>
            </td>
          </tr>
        </tbody>
      </table>
    </div>

    <!-- 수정 모달 -->
    <div
      v-if="isEditModalOpen"
      class="modal-overlay"
      @click.self="closeEditModal"
    >
      <div class="modal-content">
        <h2 class="text-xl font-bold mb-4">Edit Notice</h2>
        <input
          type="text"
          v-model="editNotice.title"
          placeholder="Notice Title"
          class="input-title"
        />
        <textarea
          v-model="editNotice.content"
          placeholder="Notice Content"
          class="input-content"
        ></textarea>
        <button class="submit-btn" @click="updateNotice">Save Changes</button>
        <button class="close-btn" @click="closeEditModal">Cancel</button>
      </div>
    </div>
  </div>
</template>

<script>
import axios from "axios";

export default {
  name: "NoticeManagement",
  data() {
    return {
      newNotice: {
        title: "",
        content: "",
      },
      notices: [],
      isEditModalOpen: false,
      editNotice: {
        noticeId: null,
        title: "",
        content: "",
      },
    };
  },
  methods: {
    // 공지사항 목록 가져오기
    async fetchNotices() {
      try {
        const accessToken = sessionStorage.getItem("accessToken");
        const response = await axios.get(
          "http://aivle-advi.com:8080/api/admin/notice",
          {
            headers: {
              Authorization: `Bearer ${accessToken}`,
              Accept: "application/json",
            },
            params: { page: 0, size: 20, isDescending: false },
          }
        );
        this.notices = response.data; // 공지사항 목록 저장
      } catch (error) {
        console.error("Failed to fetch notices:", error);
        alert("공지사항 목록을 불러오는 데 실패했습니다.");
      }
    },

    // 새로운 공지사항 작성
    async submitNotice() {
      if (!this.newNotice.title || !this.newNotice.content) {
        alert("Please fill in all fields.");
        return;
      }

      try {
        const accessToken = sessionStorage.getItem("accessToken");
        await axios.post(
          "http://aivle-advi.com:8080/api/admin/notice/write",
          {
            title: this.newNotice.title,
            content: this.newNotice.content,
          },
          {
            headers: {
              Authorization: `Bearer ${accessToken}`,
              Accept: "application/json",
              "Content-Type": "application/json",
            },
          }
        );
        alert("Notice successfully submitted!");
        this.newNotice.title = "";
        this.newNotice.content = "";
        this.fetchNotices();
      } catch (error) {
        console.error("Failed to submit notice:", error);
        alert("공지사항 작성에 실패했습니다.");
      }
    },

    // 공지사항 삭제 기능 추가
    async deleteNotice(noticeId) {
      if (!confirm("Are you sure you want to delete this notice?")) return;

      try {
        const accessToken = sessionStorage.getItem("accessToken");
        await axios.delete(
          `http://aivle-advi.com:8080/api/admin/notice/${noticeId}`,
          {
            headers: {
              Authorization: `Bearer ${accessToken}`,
              Accept: "application/json",
            },
          }
        );

        alert("Notice successfully deleted!");
        this.notices = this.notices.filter(
          (notice) => notice.noticeId !== noticeId
        );
      } catch (error) {
        console.error("Failed to delete notice:", error);
        alert("공지사항 삭제에 실패했습니다.");
      }
    },

    // 공지사항 수정 모달 열기
    openEditModal(notice) {
      this.isEditModalOpen = true;
      this.editNotice = { ...notice };
    },

    // 공지사항 수정 API 호출
    async updateNotice() {
      if (!this.editNotice.title || !this.editNotice.content) {
        alert("Please fill in all fields.");
        return;
      }

      try {
        const accessToken = sessionStorage.getItem("accessToken");
        await axios.post(
          `http://aivle-advi.com:8080/api/admin/notice/${this.editNotice.noticeId}/update`,
          {
            title: this.editNotice.title,
            content: this.editNotice.content,
          },
          {
            headers: {
              Authorization: `Bearer ${accessToken}`,
              Accept: "application/json",
              "Content-Type": "application/json",
            },
          }
        );
        alert("Notice successfully updated!");
        this.isEditModalOpen = false;
        this.fetchNotices();
      } catch (error) {
        console.error("Failed to update notice:", error);
        alert("공지사항 수정에 실패했습니다.");
      }
    },

    // 수정 모달 닫기
    closeEditModal() {
      this.isEditModalOpen = false;
    },

    // 날짜 형식 변환
    formatDate(dateString) {
      if (!dateString) return "N/A";
      const options = { year: "numeric", month: "2-digit", day: "2-digit" };
      return new Date(dateString).toLocaleDateString(undefined, options);
    },
  },
  created() {
    this.fetchNotices();
  },
};
</script>

<style scoped>
.delete-btn {
  background: #f44336;
  color: white;
  padding: 5px 10px;
  border: none;
  border-radius: 5px;
  cursor: pointer;
  margin-left: 5px;
}

.delete-btn:hover {
  background: #d32f2f;
}
</style>

<style scoped>
.notice-form {
  background: white;
  padding: 20px;
  border-radius: 12px;
  box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
  margin-bottom: 20px;
}

.input-title,
.input-content {
  width: 100%;
  padding: 10px;
  margin-bottom: 10px;
  border: 1px solid #ddd;
  border-radius: 4px;
}

.input-content {
  height: 100px;
}

.submit-btn {
  background: #2196f3;
  color: white;
  padding: 10px 15px;
  border: none;
  border-radius: 5px;
  cursor: pointer;
}

.submit-btn:hover {
  background: #1976d2;
}

.notice-list {
  background: white;
  padding: 20px;
  border-radius: 12px;
  box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
}

.notice-table {
  width: 100%;
  border-collapse: collapse;
  background: white;
  border-radius: 12px;
}

.notice-table th,
.notice-table td {
  padding: 10px;
  text-align: left;
  border-bottom: 1px solid #ddd;
}

.edit-btn {
  background: #ffa726;
  color: white;
  padding: 5px 10px;
  border: none;
  border-radius: 5px;
  cursor: pointer;
}

.edit-btn:hover {
  background: #fb8c00;
}

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
  max-width: 500px;
  width: 90%;
}

.close-btn {
  background: #f44336;
  color: white;
  padding: 5px 10px;
  border: none;
  border-radius: 5px;
  cursor: pointer;
  margin-top: 10px;
}

.close-btn:hover {
  background: #d32f2f;
}
</style>
