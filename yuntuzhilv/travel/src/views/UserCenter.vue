<template>
  <div class="user-center-page">
    <div class="page-header">
      <div class="header-left">
        <button class="back-arrow" @click="router.push('/home')" title="返回首页">
          <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round"
            stroke-linejoin="round">
            <line x1="19" y1="12" x2="5" y2="12" />
            <polyline points="12 19 5 12 12 5" />
          </svg>
        </button>
        <h1>个人中心</h1>
      </div>
      <button class="logout-btn" @click="logout">退出登录</button>
    </div>

    <div class="main-content">
      <div class="top-section">
        <div class="bg-decoration">
          <svg class="decor-path" viewBox="0 0 1000 200">
            <path d="M0,100 Q200,50 400,100 T800,100 T1000,100" fill="none" stroke="#51b891" stroke-width="3" />
            <circle cx="150" cy="80" r="6" fill="#51b891" />
            <circle cx="350" cy="120" r="6" fill="#51b891" />
            <circle cx="550" cy="70" r="8" fill="#f0c76d" />
            <circle cx="750" cy="110" r="6" fill="#51b891" />
            <circle cx="900" cy="90" r="8" fill="#f0c76d" />
            <circle cx="50" cy="150" r="4" fill="#51b891" opacity="0.6" />
            <circle cx="100" cy="60" r="3" fill="#51b891" opacity="0.5" />
            <circle cx="850" cy="150" r="5" fill="#f0c76d" opacity="0.6" />
            <circle cx="950" cy="50" r="4" fill="#f0c76d" opacity="0.5" />
          </svg>
          <svg class="decor-mountain" viewBox="0 0 200 100">
            <path d="M0,100 L50,30 L100,70 L150,20 L200,100 Z" fill="none" stroke="#51b891" stroke-width="2"
              opacity="0.6" />
            <path d="M30,100 L60,50 L90,100 Z" fill="#d8f2e8" />
            <path d="M120,100 L160,40 L200,100 Z" fill="#d8f2e8" />
          </svg>
          <svg class="decor-cloud" viewBox="0 0 100 60">
            <ellipse cx="50" cy="35" rx="40" ry="15" fill="#fff" opacity="0.8" />
            <ellipse cx="35" cy="30" rx="25" ry="18" fill="#fff" opacity="0.8" />
            <ellipse cx="65" cy="30" rx="25" ry="18" fill="#fff" opacity="0.8" />
          </svg>
          <svg class="decor-bag" viewBox="0 0 60 80">
            <rect x="15" y="20" width="30" height="40" rx="5" fill="none" stroke="#f0c76d" stroke-width="2" />
            <rect x="20" y="30" width="20" height="30" fill="#fff" opacity="0.5" />
            <line x1="22" y1="50" x2="38" y2="50" stroke="#f0c76d" stroke-width="2" />
            <line x1="22" y1="55" x2="38" y2="55" stroke="#f0c76d" stroke-width="2" />
            <path d="M30,20 L30,10 L40,10 L40,15" fill="none" stroke="#f0c76d" stroke-width="2" />
          </svg>
        </div>

        <div class="user-info-section">
          <div class="user-card">
            <div class="avatar-wrapper">
              <div class="avatar">
                {{ avatarInitial }}
              </div>
              <div class="level-badge">{{ userLevel }}</div>
            </div>
            <div class="user-details">
              <h2 class="username">{{ displayName }}</h2>
              <p class="user-desc">{{ userBio }}</p>
            </div>
          </div>

          <div class="stats-row">
            <div class="stat-card">
              <div class="stat-icon">
                <svg viewBox="0 0 24 24" fill="none" stroke="#51b891" stroke-width="2">
                  <path
                    d="M19 14c1.49-1.46 3-3.21 3-5.5A5.5 5.5 0 0 0 16.5 3c-1.76 0-3 .5-4.5 2-1.5-1.5-2.74-2-4.5-2A5.5 5.5 0 0 0 2 8.5c0 2.3 1.5 4.05 3 5.5l7 7Z" />
                </svg>
              </div>
              <div class="stat-info">
                <span class="stat-value">{{ favoritesCount }}</span>
                <span class="stat-label">收藏</span>
              </div>
            </div>
            <div class="stat-card">
              <div class="stat-icon">
                <svg viewBox="0 0 24 24" fill="none" stroke="#51b891" stroke-width="2">
                  <path
                    d="M9 5H7a2 2 0 0 0-2 2v12a2 2 0 0 0 2 2h10a2 2 0 0 0 2-2V7a2 2 0 0 0-2-2h-2M9 5a2 2 0 0 0 2 2h2a2 2 0 0 0 2-2M9 5a2 2 0 0 1 2-2h2a2 2 0 0 1 2 2m-3 7h3m-3 4h3m-6-4h.01M9 16h.01" />
                </svg>
              </div>
              <div class="stat-info">
                <span class="stat-value">{{ tripsCount }}</span>
                <span class="stat-label">行程</span>
              </div>
            </div>
            <div class="stat-card">
              <div class="stat-icon">
                <svg viewBox="0 0 24 24" fill="none" stroke="#f0c76d" stroke-width="2">
                  <path
                    d="M12 8c-1.657 0-3 .895-3 2s1.343 2 3 2 3 .895 3 2-1.343 2-3 2m0-8c1.11 0 2.08.402 2.599 1M12 8V7m0 1v8m0 0v1m0-1c-1.11 0-2.08-.402-2.599-1M21 12a9 9 0 1 1-18 0 9 9 0 0 1 18 0z" />
                </svg>
              </div>
              <div class="stat-info">
                <span class="stat-value">{{ points }}</span>
                <span class="stat-label">积分</span>
              </div>
            </div>
            <div class="stat-card stat-card--clickable" @click="router.push('/my-footprints')">
              <div class="stat-icon">
                <svg viewBox="0 0 24 24" fill="none" stroke="#51b891" stroke-width="2">
                  <path d="M21 10c0 7-9 13-9 13s-9-6-9-13a9 9 0 0 1 18 0z" />
                  <circle cx="12" cy="10" r="3" />
                </svg>
              </div>
              <div class="stat-info">
                <span class="stat-value">{{ footprintsCount }}</span>
                <span class="stat-label">我的足迹</span>
              </div>
            </div>
          </div>
        </div>
      </div>

      <div class="member-banner">
        <div class="banner-content">
          <div class="banner-icon">
            <svg viewBox="0 0 24 24" fill="none" stroke="#fff" stroke-width="2">
              <path d="M16 21v-2a4 4 0 0 0-4-4H6a4 4 0 0 0-4 4v2" />
              <circle cx="9" cy="7" r="4" />
              <path d="M22 21v-2a4 4 0 0 0-3-3.87" />
              <path d="M16 3.13a4 4 0 0 1 0 7.75" />
            </svg>
          </div>
          <div class="banner-text">
            <h3>会员权益</h3>
            <p>解锁专属路线与PDF导出</p>
          </div>
        </div>
        <button class="banner-btn">立即查看</button>
      </div>

      <div class="content-grid">
        <div class="left-panel">
          <div class="action-grid">
            <button class="action-card" @click="router.push('/my-itinerary')">
              <div class="action-icon">
                <svg viewBox="0 0 24 24" fill="none" stroke="#1a5a45" stroke-width="2">
                  <path
                    d="M9 5H7a2 2 0 0 0-2 2v12a2 2 0 0 0 2 2h10a2 2 0 0 0 2-2V7a2 2 0 0 0-2-2h-2M9 5a2 2 0 0 0 2 2h2a2 2 0 0 0 2-2M9 5a2 2 0 0 1 2-2h2a2 2 0 0 1 2 2" />
                </svg>
              </div>
              <span class="action-text">我的行程</span>
            </button>
            <button class="action-card" @click="router.push('/attractions')">
              <div class="action-icon">
                <svg viewBox="0 0 24 24" fill="none" stroke="#1a5a45" stroke-width="2">
                  <path
                    d="M19 14c1.49-1.46 3-3.21 3-5.5A5.5 5.5 0 0 0 16.5 3c-1.76 0-3 .5-4.5 2-1.5-1.5-2.74-2-4.5-2A5.5 5.5 0 0 0 2 8.5c0 2.3 1.5 4.05 3 5.5l7 7Z" />
                </svg>
              </div>
              <span class="action-text">收藏景点</span>
            </button>
            <button class="action-card">
              <div class="action-icon">
                <svg viewBox="0 0 24 24" fill="none" stroke="#1a5a45" stroke-width="2">
                  <path d="M16 11V7a4 4 0 0 0-8 0v4M5 9h14l1 12H4L5 9z" />
                </svg>
              </div>
              <span class="action-text">优惠券</span>
            </button>
            <button class="action-card">
              <div class="action-icon">
                <svg viewBox="0 0 24 24" fill="none" stroke="#1a5a45" stroke-width="2">
                  <path d="M8 21h8a2 2 0 0 0 2-2V5a2 2 0 0 0-2-2H8a2 2 0 0 0-2 2v14a2 2 0 0 0 2 2z" />
                  <path d="M12 10v4M10 12h4" />
                </svg>
              </div>
              <span class="action-text">客服帮助</span>
            </button>
          </div>
        </div>

        <div class="right-panel">
          <div class="settings-list">
            <div class="setting-item">
              <div class="setting-icon">
                <svg viewBox="0 0 24 24" fill="none" stroke="#51b891" stroke-width="2">
                  <path
                    d="M21 12a9 9 0 0 0-9-9 9.75 9.75 0 0 0-6.74 2.74L3 8m9 9a9 9 0 0 0 9-9 9.75 9.75 0 0 0-6.74-2.74L15 16" />
                </svg>
              </div>
              <span class="setting-text">账号安全</span>
              <span class="setting-arrow">></span>
            </div>
            <div class="setting-item">
              <div class="setting-icon">
                <svg viewBox="0 0 24 24" fill="none" stroke="#51b891" stroke-width="2">
                  <path d="M12 22s8-4 8-10V5l-8-3-8 3v7c0 6 8 10 8 10z" />
                </svg>
              </div>
              <span class="setting-text">隐私设置</span>
              <span class="setting-arrow">></span>
            </div>
            <div class="setting-item">
              <div class="setting-icon">
                <svg viewBox="0 0 24 24" fill="none" stroke="#51b891" stroke-width="2">
                  <path d="M21 15a2 2 0 0 1-2 2H7l-4 4V5a2 2 0 0 1 2-2h14a2 2 0 0 1 2 2z" />
                </svg>
              </div>
              <span class="setting-text">通知提醒</span>
              <span class="setting-arrow">></span>
            </div>
            <div class="setting-item">
              <div class="setting-icon">
                <svg viewBox="0 0 24 24" fill="none" stroke="#51b891" stroke-width="2">
                  <path
                    d="M4 7v10c0 2.21 3.582 4 8 4s8-1.79 8-4V7M4 7c0 2.21 3.582 4 8 4s8-1.79 8-4M4 7c0-2.21 3.582-4 8-4s8 1.79 8 4m0 5c0 2.21-3.582 4-8 4s-8-1.79-8-4" />
                </svg>
              </div>
              <span class="setting-text">清除缓存</span>
              <span class="setting-arrow">></span>
            </div>
          </div>
        </div>
      </div>

      <div class="footer-settings">
        <div class="setting-item">
          <div class="setting-icon">
            <svg viewBox="0 0 24 24" fill="none" stroke="#51b891" stroke-width="2">
              <path
                d="M10.325 4.317c.426-1.756 2.924-1.756 3.35 0a1.724 1.724 0 0 0 2.573 1.066c1.543-.94 3.31.826 2.37 2.37a1.724 1.724 0 0 0 1.065 2.572c1.756.426 1.756 2.924 0 3.35a1.724 1.724 0 0 0-1.066 2.573c.94 1.543-.826 3.31-2.37 2.37a1.724 1.724 0 0 0-2.572 1.065c-.426 1.756-2.924 1.756-3.35 0a1.724 1.724 0 0 0-2.573-1.066c-1.543.94-3.31-.826-2.37-2.37a1.724 1.724 0 0 0-1.065-2.572c-1.756-.426-1.756-2.924 0-3.35a1.724 1.724 0 0 0 1.066-2.573c-.94-1.543.826-3.31 2.37-2.37.996.608 2.296.07 2.572-1.065z" />
              <circle cx="12" cy="12" r="3" />
            </svg>
          </div>
          <span class="setting-text">系统设置</span>
          <span class="setting-arrow">></span>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
/**
 * UserCenter.vue — 个人中心
 *
 * 纯 API 消费者：页面加载时调用 getUserInfoApi 获取用户资料与统计摘要，
 * 不保留任何 mock 数据或 localStorage 回退。
 *
 * 接口定义见 src/api/user.js → getUserInfoApi()，含完整响应字段注释。
 */
import { computed, onMounted, ref } from 'vue'
import { useRouter } from 'vue-router'
import { getUserInfoApi } from '../api/user'
import { showToast } from '../utils/toast'

const router = useRouter()

// ===== 响应式状态 =====
const loading = ref(false)
const displayName = ref('')
const userLevel = ref('')
const userBio = ref('')
const favoritesCount = ref(0)
const tripsCount = ref(0)
const points = ref(0)
const footprintsCount = ref(0)

const avatarInitial = computed(() => (displayName.value ? displayName.value.charAt(0).toUpperCase() : 'U'))

// ===== 页面加载时拉取用户信息 =====
onMounted(async () => {
  loading.value = true
  try {
    const res = await getUserInfoApi()
    const data = res?.data || res
    displayName.value = data.username || ''
    userLevel.value = data.level || ''
    userBio.value = data.bio || ''
    favoritesCount.value = data.favoritesCount ?? 0
    tripsCount.value = data.tripsCount ?? 0
    points.value = data.points ?? 0
    footprintsCount.value = data.footprintsCount ?? 0
  } catch (e) {
    console.error('[UserCenter] 获取用户信息失败:', e)
    showToast('获取用户信息失败，请重试', 'error')
  } finally {
    loading.value = false
  }
})

function logout() {
  localStorage.removeItem('token')
  localStorage.removeItem('username')
  router.push('/home')
}
</script>

<style scoped>
.user-center-page {
  min-height: 100vh;
  background: linear-gradient(180deg, #e8f8f0 0%, #fef9f3 100%);
}

.page-header {
  max-width: 1200px;
  margin: 0 auto;
  padding: 24px 32px;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.page-header h1 {
  font-size: 28px;
  font-weight: 800;
  color: #1a5a45;
  margin: 0;
}

.header-left {
  display: flex;
  align-items: center;
  gap: 12px;
}

.back-arrow {
  width: 40px;
  height: 40px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: rgba(26, 90, 69, 0.08);
  border: none;
  border-radius: 12px;
  color: #1a5a45;
  cursor: pointer;
  transition: all 0.25s;
  flex-shrink: 0;
}

.back-arrow svg {
  width: 20px;
  height: 20px;
}

.back-arrow:hover {
  background: rgba(26, 90, 69, 0.16);
  transform: translateX(-2px);
}

.logout-btn {
  padding: 10px 20px;
  background: linear-gradient(135deg, #f0c76d 0%, #d7942b 100%);
  color: #27321f;
  border: none;
  border-radius: 999px;
  font-size: 14px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s;
}

.logout-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(240, 199, 109, 0.4);
}

.main-content {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 32px 40px;
}

.top-section {
  position: relative;
  background: linear-gradient(135deg, #d8f2e8 0%, #fff9f0 50%, #ffe8d6 100%);
  border-radius: 24px;
  padding: 32px;
  overflow: hidden;
  margin-bottom: 24px;
}

.bg-decoration {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  pointer-events: none;
  overflow: hidden;
}

.decor-path {
  position: absolute;
  top: 20px;
  left: 0;
  width: 100%;
  height: 100px;
}

.decor-mountain {
  position: absolute;
  right: 50px;
  bottom: -20px;
  width: 150px;
  height: 80px;
}

.decor-cloud {
  position: absolute;
  top: 40px;
  right: 200px;
  width: 80px;
  height: 50px;
}

.decor-bag {
  position: absolute;
  right: 30px;
  top: 30px;
  width: 50px;
  height: 60px;
}

.user-info-section {
  position: relative;
  z-index: 1;
}

.user-card {
  display: flex;
  align-items: center;
  gap: 20px;
  margin-bottom: 24px;
}

.avatar-wrapper {
  position: relative;
}

.avatar {
  width: 80px;
  height: 80px;
  border-radius: 50%;
  background: linear-gradient(135deg, #2d8a6e 0%, #1a5a45 100%);
  color: #fff;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 32px;
  font-weight: 700;
  box-shadow: 0 8px 24px rgba(45, 138, 110, 0.3);
}

.level-badge {
  position: absolute;
  bottom: -8px;
  left: 50%;
  transform: translateX(-50%);
  background: linear-gradient(135deg, #51b891 0%, #2d8a6e 100%);
  color: #fff;
  padding: 4px 12px;
  border-radius: 999px;
  font-size: 12px;
  font-weight: 600;
}

.user-details h2 {
  margin: 0 0 8px;
  font-size: 24px;
  font-weight: 700;
  color: #1a5a45;
}

.user-details p {
  margin: 0;
  font-size: 14px;
  color: #6f8279;
}

.stats-row {
  display: flex;
  gap: 20px;
}

.stat-card {
  flex: 1;
  background: #fff;
  border-radius: 16px;
  padding: 20px;
  display: flex;
  align-items: center;
  gap: 16px;
  box-shadow: 0 4px 16px rgba(15, 81, 50, 0.08);
  transition: all 0.3s;
}

.stat-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 8px 24px rgba(15, 81, 50, 0.12);
}

.stat-card--clickable {
  cursor: pointer;
}

.stat-card--clickable:hover {
  border: 2px solid #51b891;
}

.stat-icon {
  width: 48px;
  height: 48px;
  background: linear-gradient(135deg, #e9f6ef 0%, #d8f2e8 100%);
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.stat-icon svg {
  width: 24px;
  height: 24px;
}

.stat-info {
  display: flex;
  flex-direction: column;
}

.stat-value {
  font-size: 24px;
  font-weight: 800;
  color: #d7942b;
}

.stat-label {
  font-size: 14px;
  color: #6f8279;
}

.member-banner {
  background: linear-gradient(90deg, #f0c76d 0%, #f9a84d 50%, #f0c76d 100%);
  border-radius: 16px;
  padding: 20px 24px;
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;
}

.banner-content {
  display: flex;
  align-items: center;
  gap: 16px;
}

.banner-icon {
  width: 48px;
  height: 48px;
  background: rgba(255, 255, 255, 0.3);
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.banner-icon svg {
  width: 28px;
  height: 28px;
}

.banner-text h3 {
  margin: 0 0 4px;
  font-size: 18px;
  font-weight: 700;
  color: #27321f;
}

.banner-text p {
  margin: 0;
  font-size: 13px;
  color: rgba(39, 50, 31, 0.8);
}

.banner-btn {
  padding: 10px 24px;
  background: #fff;
  color: #d7942b;
  border: none;
  border-radius: 999px;
  font-size: 14px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s;
}

.banner-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(255, 255, 255, 0.4);
}

.content-grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 24px;
  margin-bottom: 24px;
}

.action-grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 16px;
}

.action-card {
  background: #fff;
  border-radius: 16px;
  padding: 24px;
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 12px;
  border: 2px solid #e9f6ef;
  cursor: pointer;
  transition: all 0.3s;
}

.action-card:hover {
  border-color: #51b891;
  transform: translateY(-4px);
  box-shadow: 0 8px 24px rgba(81, 184, 145, 0.15);
}

.action-icon {
  width: 56px;
  height: 56px;
  background: linear-gradient(135deg, #e9f6ef 0%, #d8f2e8 100%);
  border-radius: 16px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.action-icon svg {
  width: 28px;
  height: 28px;
}

.action-text {
  font-size: 16px;
  font-weight: 600;
  color: #1a5a45;
}

.right-panel {
  background: #fff;
  border-radius: 16px;
  padding: 24px;
  border: 2px solid #e9f6ef;
}

.settings-list {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.setting-item {
  display: flex;
  align-items: center;
  gap: 16px;
  padding: 16px 0;
  border-bottom: 1px solid #f0f7f4;
  cursor: pointer;
  transition: all 0.3s;
}

.setting-item:last-child {
  border-bottom: none;
}

.setting-item:hover {
  background: #f9fdfb;
}

.setting-icon {
  width: 36px;
  height: 36px;
  background: linear-gradient(135deg, #e9f6ef 0%, #d8f2e8 100%);
  border-radius: 10px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.setting-icon svg {
  width: 20px;
  height: 20px;
}

.setting-text {
  flex: 1;
  font-size: 15px;
  color: #1a5a45;
  font-weight: 500;
}

.setting-arrow {
  font-size: 18px;
  color: #b8c7bf;
}

.footer-settings {
  background: #fff;
  border-radius: 16px;
  padding: 24px;
  border: 2px solid #e9f6ef;
}

.footer-settings .setting-item {
  border-bottom: none;
}

@media (max-width: 768px) {
  .page-header {
    padding: 16px 20px;
  }

  .page-header h1 {
    font-size: 22px;
  }

  .main-content {
    padding: 0 20px 32px;
  }

  .top-section {
    padding: 20px;
  }

  .user-card {
    margin-bottom: 16px;
  }

  .avatar {
    width: 64px;
    height: 64px;
    font-size: 24px;
  }

  .stats-row {
    flex-direction: column;
    gap: 12px;
  }

  .content-grid {
    grid-template-columns: 1fr;
    gap: 16px;
  }

  .action-grid {
    grid-template-columns: 1fr;
  }

  .member-banner {
    flex-direction: column;
    gap: 16px;
    text-align: center;
  }
}
</style>