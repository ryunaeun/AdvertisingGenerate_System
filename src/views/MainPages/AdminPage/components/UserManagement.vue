<template>
  <div>
    <h1 class="text-2xl font-bold mb-4">User Management</h1>
    <table class="user-table">
      <thead>
        <tr>
          <th>Name</th>
          <th>Email</th>
          <th>Status</th>
          <th>Actions</th>
        </tr>
      </thead>
      <tbody>
        <tr v-for="user in users" :key="user.id">
          <td>{{ user.name }}</td>
          <td>{{ user.email }}</td>
          <td>
            <span
              :class="['status-badge', user.active ? 'active' : 'inactive']"
            >
              {{ user.active ? "Active" : "Inactive" }}
            </span>
          </td>
          <td>
            <button class="toggle-btn" @click="toggleUserStatus(user.id)">
              {{ user.active ? "Deactivate" : "Activate" }}
            </button>
          </td>
        </tr>
      </tbody>
    </table>
  </div>
</template>

<script>
export default {
  name: "UserManagement",
  data() {
    return {
      users: [
        { id: 1, name: "Alice", email: "alice@example.com", active: true },
        { id: 2, name: "Bob", email: "bob@example.com", active: false },
      ],
    };
  },
  methods: {
    toggleUserStatus(userId) {
      const user = this.users.find((u) => u.id === userId);
      if (user) {
        user.active = !user.active;
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

.status-badge.active {
  background: #4caf50;
  color: white;
  padding: 5px 10px;
  border-radius: 12px;
}

.status-badge.inactive {
  background: #f44336;
  color: white;
  padding: 5px 10px;
  border-radius: 12px;
}

.toggle-btn {
  background: #2196f3;
  color: white;
  padding: 5px 10px;
  border: none;
  border-radius: 5px;
  cursor: pointer;
}

.toggle-btn:hover {
  background: #1976d2;
}
</style>
