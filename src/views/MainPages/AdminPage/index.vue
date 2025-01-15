<template>
  <div class="container">
    <div class="row">
      <div class="col-12">
        <h3>User Information - Membership management</h3>

        <div class="d-flex justify-content-end mb-3">
          <button class="btn btn-primary me-2" @click="goToUserPage">Go to User Page</button>
          <button class="btn btn-danger" @click="logout">Logout</button>
        </div>

        <div class="table-responsive">
          <table class="table align-items-center mb-0">
            <thead>
              <tr>
                <th class="text-uppercase text-secondary text-xxs font-weight-bolder opacity-7">User</th>
                <th class="text-uppercase text-secondary text-xxs font-weight-bolder opacity-7 ps-2">Name</th>
                <th class="text-uppercase text-secondary text-xxs font-weight-bolder opacity-7 ps-2">Status</th>
                <th class="text-uppercase text-secondary text-xxs font-weight-bolder opacity-7 ps-2">Actions</th>
              </tr>
            </thead>
            <tbody>
              <tr v-for="user in users" :key="user.id">
                <td>
                  <div class="d-flex px-2 py-1">
                    <div>
                      <MaterialAvatar :img="user.avatar" size="sm" />
                    </div>
                    <div class="d-flex flex-column justify-content-center ms-3">
                      <h6 class="mb-0 text-sm">{{ user.firstName }}</h6>
                    </div>
                  </div>
                </td>
                <td>
                  <p class="text-sm font-weight-bold mb-0">{{ user.lastName }}</p>
                </td>
                <td>
                  <MaterialBadge 
                    :color="user.status === 'active' ? 'success' : 'danger'"
                    :content="user.status"
                    variant="gradient"
                  />
                </td>
                <td>
                  <MaterialButton
                    :color="user.status === 'active' ? 'info' : 'success'"
                    variant="gradient"
                    size="sm"
                    class="mb-0"
                    @click="toggleStatus(user)"
                  >
                    {{ user.status === 'active' ? 'Deactivate' : 'Activate' }}
                  </MaterialButton>
                </td>
              </tr>
            </tbody>
          </table>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { ref } from 'vue';
import apiClient from "@/api/axiosClient";
import MaterialAvatar from "@/components/MaterialAvatar.vue";
import MaterialBadge from "@/components/MaterialBadge.vue";
import MaterialButton from "@/components/MaterialButton.vue";

export default {
  name: 'AdminPage',
  components: {
    MaterialAvatar,
    MaterialBadge,
    MaterialButton
  },
  setup(_, { router }) {
    const users = ref([
      {
        id: 1,
        firstName: 'Hector Hugo',
        lastName: 'Garcia',
        status: 'active',
        avatar: '/img/team-1.jpg'
      },
      {
        id: 2,
        firstName: 'Fernanda',
        lastName: 'Vargas',
        status: 'active',
        avatar: '/img/team-2.jpg'
      },
      {
        id: 3,
        firstName: 'Francisco',
        lastName: 'Espina',
        status: 'inactive',
        avatar: '/img/team-3.jpg'
      },
      // 나머지 사용자 데이터 추가
    ]);

    const toggleStatus = (user) => {
      user.status = user.status === 'active' ? 'inactive' : 'active';
    };

    const logout = async () => {
      try {
        await apiClient.post("/logout");
        sessionStorage.removeItem("accessToken");
        localStorage.removeItem("refreshToken");
        router.push({ name: "loginPage" });
        alert("로그아웃 되었습니다.");
      } catch (error) {
        console.error("로그아웃 실패:", error);
        alert("로그아웃 중 문제가 발생했습니다.");
      }
    };

    return {
      users,
      toggleStatus,
      logout
    };
  }
};
</script>

<style scoped>
.table-responsive {
  margin-top: 20px;
}
</style>
