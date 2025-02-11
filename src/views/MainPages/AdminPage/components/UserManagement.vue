<template>
  <div>
    <h1 class="text-2xl font-bold mb-4">User Management</h1>
    <table class="user-table">
      <thead>
        <tr>
          <th>Name</th>
          <th>Email</th>
          <th>Role</th>
          <th>Billing</th>
          <th>Actions</th>
          <th>Delete</th>
        </tr>
      </thead>
      <tbody>
        <tr v-for="user in users" :key="user.id">
          <td>{{ user.username }}</td>
          <td>{{ user.email }}</td>
          <td>{{ user.role === "ROLE_ADMIN" ? "Admin" : "User" }}</td>
          <td>{{ user.billing }}</td>
          <td>
            <button class="edit-btn" @click="openEditModal(user)">Edit</button>
          </td>
          <td>
            <button class="delete-btn" @click="deleteUser(user.email)">
              Delete
            </button>
          </td>
        </tr>
      </tbody>
    </table>

    <!-- 수정 모달 -->
    <div
      v-if="isEditModalOpen"
      class="modal-overlay"
      @click.self="closeEditModal"
    >
      <div class="modal-content">
        <h2 class="text-xl font-bold mb-4">Edit User</h2>
        <label class="block mb-2">
          Username:
          <input
            type="text"
            v-model="editData.newUsername"
            class="input-field"
            placeholder="Enter new username"
          />
        </label>
        <label class="block mb-2">
          Role:
          <select v-model="editData.role" class="input-field">
            <option value="ROLE_ADMIN">Admin</option>
            <option value="ROLE_USER">User</option>
          </select>
        </label>
        <label class="block mb-4">
          Billing:
          <select v-model="editData.billing" class="input-field">
            <option value="FREE">Free</option>
            <option value="PREMIUM">Premium</option>
          </select>
        </label>
        <button class="save-btn" @click="saveUserChanges">Save Changes</button>
        <button class="close-btn" @click="closeEditModal">Close</button>
      </div>
    </div>
  </div>
</template>

<script>
import axios from "axios";
import apiClient from "../../../../api/axiosClient";

export default {
  name: "UserManagement",
  data() {
    return {
      users: [],
      isEditModalOpen: false,
      editData: {
        username: "",
        newUsername: "",
        role: "ROLE_USER",
        billing: "FREE",
      },
    };
  },
  mounted() {
    this.fetchUsers();
  },
  methods: {
    async fetchUsers() {
      try {
        const accessToken = sessionStorage.getItem("accessToken");
        const response = await apiClient.get(
          "/admin/users",
          {
            headers: {
              Authorization: `Bearer ${accessToken}`,
              Accept: "application/json",
            },
          }
        );
        this.users = response.data.content.map((user) => ({
          id: user.id,
          username: user.username,
          email: user.email,
          role: user.role,
          billing: user.billing,
          verified: user.verified,
        }));
      } catch (error) {
        console.error("Failed to fetch users:", error);
        alert("사용자 목록을 불러오지 못했습니다.");
      }
    },
    openEditModal(user) {
      this.editData.username = user.username;
      this.editData.newUsername = user.username;
      this.editData.role = user.role;
      this.editData.billing = user.billing;
      this.isEditModalOpen = true;
    },
    closeEditModal() {
      this.isEditModalOpen = false;
    },
    async saveUserChanges() {
      try {
        const accessToken = sessionStorage.getItem("accessToken");
        await apiClient.post(
          "/admin/users/update",
          this.editData,
          {
            headers: {
              Authorization: `Bearer ${accessToken}`,
              Accept: "application/json",
            },
          }
        );
        alert("사용자 정보가 수정되었습니다.");
        this.fetchUsers(); // 사용자 목록 갱신
        this.closeEditModal();
      } catch (error) {
        console.error("Failed to update user:", error);
        alert("사용자 정보를 수정하지 못했습니다.");
      }
    },
    async deleteUser(email) {
      if (!confirm("정말로 이 사용자를 삭제하시겠습니까?")) return;

      try {
        const accessToken = sessionStorage.getItem("accessToken");
        await apiClient.post(
          "/admin/users/delete",
          { email },
          {
            headers: {
              Authorization: `Bearer ${accessToken}`,
              Accept: "application/json",
              "Content-Type": "application/json",
            },
          }
        );
        alert("사용자가 삭제되었습니다.");
        this.fetchUsers(); // 사용자 목록 갱신
      } catch (error) {
        console.error("Failed to delete user:", error);
        alert("사용자를 삭제하지 못했습니다.");
      }
    },
  },
};
</script>

<style scoped>
.user-table {
  width: 100%;
  border-collapse: collapse;
  background: white;
  border-radius: 12px;
}

.user-table th,
.user-table td {
  padding: 10px;
  text-align: left;
  border-bottom: 1px solid #ddd;
}

.edit-btn,
.delete-btn {
  background: #2196f3;
  color: white;
  padding: 5px 10px;
  border: none;
  border-radius: 5px;
  cursor: pointer;
}

.edit-btn:hover {
  background: #1976d2;
}

.delete-btn {
  background: #f44336;
}

.delete-btn:hover {
  background: #d32f2f;
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
  width: 100%;
}

.save-btn {
  background: #4caf50;
  color: white;
  padding: 10px 15px;
  border: none;
  border-radius: 5px;
  cursor: pointer;
}

.save-btn:hover {
  background: #388e3c;
}

.close-btn {
  background: #f44336;
  color: white;
  padding: 10px 15px;
  border: none;
  border-radius: 5px;
  cursor: pointer;
  margin-left: 10px;
}

.close-btn:hover {
  background: #d32f2f;
}
</style>
