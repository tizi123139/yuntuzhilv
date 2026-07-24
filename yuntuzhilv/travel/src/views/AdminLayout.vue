<template>
  <div class="admin-layout">
    <aside class="sidebar">
      <div class="logo-card">
        <div class="logo">后台管理</div>
        <div class="brand">WayCloud</div>
      </div>
      <nav class="nav-menu">
        <router-link to="/admin/users" class="nav-item" active-class="active">
          <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
            <path d="M17 21v-2a4 4 0 0 0-4-4H5a4 4 0 0 0-4 4v2" />
            <circle cx="9" cy="7" r="4" />
            <path d="M23 21v-2a4 4 0 0 0-3-3.87" />
            <path d="M16 3.13a4 4 0 0 1 0 7.75" />
          </svg>
          <span>用户管理</span>
          <span class="arrow" v-if="$route.path === '/admin/users'">›</span>
        </router-link>
        <router-link to="/admin/attractions" class="nav-item" active-class="active">
          <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
            <path d="M21 10c0 7-9 13-9 13s-9-6-9-13a9 9 0 0 1 18 0z" />
            <circle cx="12" cy="10" r="3" />
          </svg>
          <span>景点管理</span>
          <span class="arrow" v-if="$route.path === '/admin/attractions'">›</span>
        </router-link>
        <router-link to="/admin/hotels" class="nav-item" active-class="active">
          <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
            <path d="M3 9l9-7 9 7v11a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2z" />
            <polyline points="9 22 9 12 15 12 15 22" />
          </svg>
          <span>酒店管理</span>
          <span class="arrow" v-if="$route.path === '/admin/hotels'">›</span>
        </router-link>
        <router-link to="/admin/traffic" class="nav-item" active-class="active">
          <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
            <path d="M18.92 6.01C18.72 5.42 18.16 5 17.5 5h-11c-.66 0-1.21.42-1.42 1.01L3 12v8c0 .55.45 1 1 1h1c.55 0 1-.45 1-1v-1h12v1c0 .55.45 1 1 1h1c.55 0 1-.45 1-1v-8l-2.08-5.99zM6.5 16c-.83 0-1.5-.67-1.5-1.5S5.67 13 6.5 13s1.5.67 1.5 1.5S7.33 16 6.5 16zm11 0c-.83 0-1.5-.67-1.5-1.5s.67-1.5 1.5-1.5 1.5.67 1.5 1.5-.67 1.5-1.5 1.5zM5 11l1.5-4.5h11L19 11H5z" />
          </svg>
          <span>交通管理</span>
          <span class="arrow" v-if="$route.path === '/admin/traffic'">›</span>
        </router-link>
        <router-link to="/admin/stats" class="nav-item" active-class="active">
          <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
            <line x1="18" y1="20" x2="18" y2="10" />
            <line x1="12" y1="20" x2="12" y2="4" />
            <line x1="6" y1="20" x2="6" y2="14" />
          </svg>
          <span>数据统计</span>
          <span class="arrow" v-if="$route.path === '/admin/stats'">›</span>
        </router-link>
        <router-link to="/admin/logs" class="nav-item" active-class="active">
          <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
            <path d="M14 2H6a2 2 0 0 0-2 2v16a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2V8z" />
            <polyline points="14 2 14 8 20 8" />
            <line x1="16" y1="13" x2="8" y2="13" />
            <line x1="16" y1="17" x2="8" y2="17" />
          </svg>
          <span>操作日志</span>
          <span class="arrow" v-if="$route.path === '/admin/logs'">›</span>
        </router-link>
        <router-link to="/admin/feedback" class="nav-item" active-class="active">
          <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
            <path d="M21 15a2 2 0 0 1-2 2H7l-4 4V5a2 2 0 0 1 2-2h14a2 2 0 0 1 2 2z" />
          </svg>
          <span>意见反馈</span>
          <span class="arrow" v-if="$route.path === '/admin/feedback'">›</span>
        </router-link>
      </nav>
      <div class="sidebar-footer">
        <button class="logout-btn" @click="handleLogout">
          <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
            <path d="M9 21H5a2 2 0 0 1-2-2V5a2 2 0 0 1 2-2h4" />
            <polyline points="16 17 21 12 16 7" />
            <line x1="21" y1="12" x2="9" y2="12" />
          </svg>
          <span>退出登录</span>
        </button>
      </div>
    </aside>
    <main class="main-content">
      <router-view />
    </main>
  </div>
</template>

<script setup>
import { useRouter } from 'vue-router'
import { showToast } from '../utils/toast'

const router = useRouter()

function handleLogout() {
  localStorage.removeItem('token')
  localStorage.removeItem('role')
  localStorage.removeItem('username')
  localStorage.removeItem('userId')
  showToast('已退出登录', 'success')
  router.replace('/BLhome')
}
</script>

<style scoped>
.admin-layout {
  display: flex;
  min-height: 100vh;
  background: #f5f7fa;
}

.sidebar {
  width: 240px;
  background: linear-gradient(180deg, #d8f2e8 0%, #f1fbf6 100%);
  display: flex;
  flex-direction: column;
  box-shadow: 2px 0 12px rgba(0, 0, 0, 0.05);
  padding: 20px;
}

.logo-card {
  background: #fff;
  border-radius: 16px;
  padding: 20px 24px;
  margin-bottom: 35px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.05);
}

.logo {
  font-size: 20px;
  font-weight: 700;
  color: #0f5132;
  margin-bottom: 4px;
}

.brand {
  font-size: 13px;
  color: #6b7d76;
  letter-spacing: 1px;
}

.nav-menu {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.nav-item {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 14px 16px;
  color: #6b7d76;
  text-decoration: none;
  transition: all 0.2s;
  border-radius: 12px;
  position: relative;
  background: #fff;
}

.nav-item:hover {
  background: rgba(81, 184, 145, 0.15);
  color: #2d8a6e;
}

.nav-item.active {
  background: linear-gradient(135deg, #51b891 0%, #2d8a6e 100%);
  color: #fff;
}

.arrow {
  margin-left: auto;
  font-size: 20px;
  font-weight: 300;
}

.sidebar-footer {
  padding-top: 20px;
  margin-top: auto;
}

.logout-btn {
  width: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  padding: 14px;
  background: #fff;
  color: #2d8a6e;
  border: none;
  border-radius: 12px;
  cursor: pointer;
  transition: all 0.2s;
  font-size: 14px;
  font-weight: 500;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.05);
}

.logout-btn:hover {
  background: #f0fdf4;
}

.main-content {
  flex: 1;
  padding: 32px;
  overflow-y: auto;
}

@media (max-width: 768px) {
  .sidebar {
    width: 200px;
  }
  .main-content {
    padding: 20px;
  }
}
</style>