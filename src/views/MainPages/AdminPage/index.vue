<template>
    <div class="wrapper">
      <!-- Sidebar -->
      <aside class="sidenav navbar navbar-vertical navbar-expand-xs border-0 border-radius-xl my-3 fixed-start ms-3">
        <div class="sidenav-header">
          <div class="user-info p-3">
            <MaterialAvatar :img="userProfile.avatar" size="xl" class="mb-2"/>
            <h6 class="text-sm">{{ userProfile.name }}</h6>
            <p class="text-xs text-secondary mb-0">{{ userProfile.email }}</p>
          </div>
        </div>
  
        <hr class="horizontal dark mt-0">
  
        <div class="collapse navbar-collapse w-auto" id="sidenav-collapse-main">
          <ul class="navbar-nav">
            <li class="nav-item">
              <router-link class="nav-link" to="/dashboard">
                <div class="icon-shape">
                  <i class="fas fa-chart-pie"></i>
                </div>
                <span class="nav-link-text ms-1">Dashboard</span>
              </router-link>
            </li>
  
            <li class="nav-item">
              <router-link class="nav-link active" to="/user-information">
                <div class="icon-shape">
                  <i class="fas fa-user"></i>
                </div>
                <span class="nav-link-text ms-1">User Information</span>
              </router-link>
              <ul class="nav ms-4">
                <li class="nav-item">
                  <router-link class="nav-link" to="/membership-management">
                    <span class="nav-link-text">Membership management</span>
                  </router-link>
                </li>
                <li class="nav-item">
                  <router-link class="nav-link" to="/video-management">
                    <span class="nav-link-text">Video management</span>
                  </router-link>
                </li>
              </ul>
            </li>
  
            <li class="nav-item">
              <router-link class="nav-link" to="/customer-inquiry">
                <div class="icon-shape">
                  <i class="fas fa-question-circle"></i>
                </div>
                <span class="nav-link-text ms-1">Customer Inquiry</span>
              </router-link>
            </li>
  
            <li class="nav-item">
              <router-link class="nav-link" to="/persona">
                <div class="icon-shape">
                  <i class="fas fa-users"></i>
                </div>
                <span class="nav-link-text ms-1">Persona</span>
              </router-link>
            </li>
          </ul>
        </div>
      </aside>
  
      <!-- Main Content -->
      <main class="main-content position-relative max-height-vh-100 h-100 border-radius-lg ms-18">
        <div class="container-fluid py-4">
          <div class="row">
            <div class="col-12">
              <div class="card">
                <div class="card-header pb-0">
                  <h3>User Information - Membership management</h3>
                </div>
                <div class="card-body px-0 pt-0 pb-2">
                  <div class="table-responsive p-0">
                    <table class="table align-items-center mb-0">
                      <thead>
                        <tr>
                          <th class="text-uppercase text-secondary text-xxs font-weight-bolder opacity-7">User</th>
                          <th class="text-uppercase text-secondary text-xxs font-weight-bolder opacity-7 ps-2">Name</th>
                          <th class="text-uppercase text-secondary text-xxs font-weight-bolder opacity-7 ps-2">Status</th>
                          <th class="text-uppercase text-secondary text-xxs font-weight-bolder opacity-7 ps-2">Actions</th>
                          <th class="text-secondary opacity-7"></th>
                        </tr>
                      </thead>
                      <tbody>
                        <tr v-for="user in users" :key="user.id">
                          <td>
                            <div class="d-flex px-2 py-1">
                              <h6 class="mb-0 text-sm">{{ user.firstName }}</h6>
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
                              Activate
                            </MaterialButton>
                          </td>
                          <td>
                            <button class="btn btn-link">
                              <i class="fas fa-ellipsis-v"></i>
                            </button>
                          </td>
                        </tr>
                      </tbody>
                    </table>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>
      </main>
    </div>
  </template>
  
  <script>
  import { ref } from 'vue'
  import MaterialAvatar from "@/components/MaterialAvatar.vue"
  import MaterialBadge from "@/components/MaterialBadge.vue"
  import MaterialButton from "@/components/MaterialButton.vue"
  
  export default {
    name: 'UserManagement',
    components: {
      MaterialAvatar,
      MaterialBadge,
      MaterialButton
    },
    setup() {
      const userProfile = ref({
        name: 'Harinas Elizondo',
        email: 'Manuel@gmail.com',
        avatar: '/img/profile.jpg'
      })
  
      const users = ref([
        {
          id: 1,
          firstName: 'Hector Hugo',
          lastName: 'Garcia',
          status: 'active'
        },
        {
          id: 2,
          firstName: 'Fernanda',
          lastName: 'Vargas',
          status: 'active'
        },
        {
          id: 3,
          firstName: 'Francisco',
          lastName: 'Espina',
          status: 'inactive'
        },
        {
          id: 4,
          firstName: 'Alberto',
          lastName: 'Perez',
          status: 'active'
        },
        {
          id: 5,
          firstName: 'Mauricio',
          lastName: 'Cortes',
          status: 'inactive'
        },
        {
          id: 6,
          firstName: 'Fernando',
          lastName: 'Reyes',
          status: 'active'
        },
        {
          id: 7,
          firstName: 'Juan',
          lastName: 'Cardona',
          status: 'active'
        }
      ])
  
      const toggleStatus = (user) => {
        user.status = user.status === 'active' ? 'inactive' : 'active'
      }
  
      return {
        userProfile,
        users,
        toggleStatus
      }
    }
  }
  </script>
  
  <style scoped>
  .sidenav {
    width: 250px;
    background: white;
    box-shadow: 0 0 2rem 0 rgba(136, 152, 170, .15);
  }
  
  .user-info {
    text-align: center;
  }
  
  .icon-shape {
    width: 32px;
    height: 32px;
    display: inline-flex;
    align-items: center;
    justify-content: center;
  }
  
  .nav-link.active {
    background: #f8f9fa;
    color: #344767;
  }
  
  .nav-link {
    color: #67748e;
    padding: 0.5rem 1rem;
    display: flex;
    align-items: center;
  }
  
  .nav-link:hover {
    background: #f8f9fa;
  }
  
  .main-content {
    margin-left: 250px;
  }
  
  .ms-18 {
    margin-left: 18rem;
  }
  
  .table td {
    white-space: nowrap;
    padding: 0.75rem 1.5rem;
  }
  
  .table th {
    padding: 1rem 1.5rem;
    font-weight: 600;
  }
  </style>
  