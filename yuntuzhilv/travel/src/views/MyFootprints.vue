<template>
  <div class="footprints-page">
    <div class="page-header">
      <div class="header-left">
        <button class="back-arrow" @click="router.push('/user-center')" title="返回个人中心">
          <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round"
            stroke-linejoin="round">
            <line x1="19" y1="12" x2="5" y2="12" />
            <polyline points="12 19 5 12 12 5" />
          </svg>
        </button>
        <h1>我的足迹</h1>
      </div>
    </div>

    <div class="main-content">
      <!-- 统计概览 -->
      <div class="summary-bar">
        <div class="summary-item">
          <span class="summary-value">{{ totalCities }}</span>
          <span class="summary-label">到访城市</span>
        </div>
        <div class="summary-divider"></div>
        <div class="summary-item">
          <span class="summary-value">{{ totalFootprints }}</span>
          <span class="summary-label">足迹总数</span>
        </div>
      </div>

      <!-- 加载中 -->
      <div v-if="loading" class="loading-state">
        <div class="loading-spinner"></div>
        <p>正在加载足迹数据...</p>
      </div>

      <!-- 空状态 -->
      <div v-else-if="cityList.length === 0" class="empty-state">
        <div class="empty-icon">
          <svg viewBox="0 0 24 24" fill="none" stroke="#b8c7bf" stroke-width="1.5">
            <path d="M21 10c0 7-9 13-9 13s-9-6-9-13a9 9 0 0 1 18 0z" />
            <circle cx="12" cy="10" r="3" />
          </svg>
        </div>
        <h3>还没有足迹记录</h3>
        <p>去探索景点，开始记录你的旅行足迹吧</p>
        <button class="explore-btn" @click="router.push('/attractions')">去探索</button>
      </div>

      <!-- 城市卡片列表 -->
      <div v-else class="city-grid">
        <div v-for="city in cityList" :key="city.cityId" class="city-card">
          <div class="city-header">
            <div class="city-name-block">
              <span class="city-pin-icon">
                <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                  <path d="M21 10c0 7-9 13-9 13s-9-6-9-13a9 9 0 0 1 18 0z" />
                  <circle cx="12" cy="10" r="3" />
                </svg>
              </span>
              <h3 class="city-name">{{ city.cityName }}</h3>
            </div>
            <span class="visit-count">到访 {{ city.visitCount }} 次</span>
          </div>

          <div class="city-date-range">
            {{ formatDate(city.firstVisitDate) }} — {{ formatDate(city.lastVisitDate) }}
          </div>

          <div class="attractions-list">
            <div v-for="spot in city.attractions" :key="spot.attractionId" class="spot-item">
              <img v-if="spot.image" :src="spot.image" :alt="spot.name" class="spot-image" />
              <div v-else class="spot-image spot-image--placeholder">
                <svg viewBox="0 0 24 24" fill="none" stroke="#b8c7bf" stroke-width="1.5">
                  <rect x="3" y="3" width="18" height="18" rx="2" ry="2" />
                  <circle cx="8.5" cy="8.5" r="1.5" />
                  <polyline points="21 15 16 10 5 21" />
                </svg>
              </div>
              <div class="spot-info">
                <span class="spot-name">{{ spot.name }}</span>
                <span class="spot-date">{{ formatDate(spot.visitDate) }}</span>
                <div v-if="spot.rating" class="spot-rating">
                  <span v-for="s in 5" :key="s" :class="['star', { filled: s <= spot.rating }]">★</span>
                </div>
              </div>
              <p v-if="spot.comment" class="spot-comment">"{{ spot.comment }}"</p>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
/**
 * MyFootprints.vue — 我的足迹页面
 *
 * 纯 API 消费者：页面加载时调用 getFootprintListApi 获取用户足迹数据，
 * 以城市卡片形式展示到访过的城市及其中游览的景点。
 *
 * 接口定义见 src/api/footprint.js → getFootprintListApi()，含完整响应字段注释。
 */
import { onMounted, ref } from 'vue'
import { useRouter } from 'vue-router'
import { getFootprintListApi } from '../api/footprint'
import { showToast } from '../utils/toast'

const router = useRouter()

// ===== 响应式状态 =====
const loading = ref(false)
const totalCities = ref(0)
const totalFootprints = ref(0)
const cityList = ref([])

// ===== 页面加载时拉取足迹数据 =====
onMounted(async () => {
  loading.value = true
  try {
    const res = await getFootprintListApi()
    const data = res?.data || res
    totalCities.value = data.totalCities ?? 0
    totalFootprints.value = data.totalFootprints ?? 0
    cityList.value = data.list || []
  } catch (e) {
    console.error('[MyFootprints] 获取足迹数据失败:', e)
    showToast('获取足迹数据失败，请重试', 'error')
  } finally {
    loading.value = false
  }
})

/**
 * 将 ISO 日期字符串格式化为简短的中文日期
 * @param {string} isoStr - ISO 格式日期，如 "2025-06-15T00:00:00Z"
 * @returns {string} 格式化后的日期，如 "2025.06.15"
 */
function formatDate(isoStr) {
  if (!isoStr) return ''
  const d = new Date(isoStr)
  const y = d.getFullYear()
  const m = String(d.getMonth() + 1).padStart(2, '0')
  const day = String(d.getDate()).padStart(2, '0')
  return `${y}.${m}.${day}`
}
</script>

<style scoped>
.footprints-page {
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

.main-content {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 32px 40px;
}

/* ===== 统计概览 ===== */
.summary-bar {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 40px;
  background: linear-gradient(135deg, #d8f2e8 0%, #fff9f0 50%, #ffe8d6 100%);
  border-radius: 20px;
  padding: 28px 32px;
  margin-bottom: 32px;
}

.summary-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 6px;
}

.summary-value {
  font-size: 36px;
  font-weight: 800;
  color: #d7942b;
}

.summary-label {
  font-size: 14px;
  color: #6f8279;
  font-weight: 500;
}

.summary-divider {
  width: 1px;
  height: 40px;
  background: rgba(26, 90, 69, 0.15);
}

/* ===== 加载状态 ===== */
.loading-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 16px;
  padding: 80px 0;
  color: #6f8279;
}

.loading-spinner {
  width: 36px;
  height: 36px;
  border: 3px solid #d8f2e8;
  border-top-color: #2d8a6e;
  border-radius: 50%;
  animation: spin 0.8s linear infinite;
}

@keyframes spin {
  to { transform: rotate(360deg); }
}

/* ===== 空状态 ===== */
.empty-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 12px;
  padding: 80px 0;
  text-align: center;
}

.empty-icon {
  width: 80px;
  height: 80px;
  margin-bottom: 8px;
}

.empty-icon svg {
  width: 100%;
  height: 100%;
}

.empty-state h3 {
  margin: 0;
  font-size: 20px;
  font-weight: 700;
  color: #1a5a45;
}

.empty-state p {
  margin: 0;
  font-size: 14px;
  color: #6f8279;
}

.explore-btn {
  margin-top: 12px;
  padding: 12px 32px;
  background: linear-gradient(135deg, #2d8a6e, #1a5a45);
  color: #fff;
  border: none;
  border-radius: 999px;
  font-size: 15px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s;
}

.explore-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 6px 16px rgba(45, 138, 110, 0.3);
}

/* ===== 城市卡片网格 ===== */
.city-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(340px, 1fr));
  gap: 24px;
}

.city-card {
  background: #fff;
  border-radius: 20px;
  padding: 24px;
  box-shadow: 0 4px 16px rgba(15, 81, 50, 0.08);
  transition: all 0.3s;
}

.city-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 8px 28px rgba(15, 81, 50, 0.14);
}

.city-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 8px;
}

.city-name-block {
  display: flex;
  align-items: center;
  gap: 8px;
}

.city-pin-icon {
  width: 24px;
  height: 24px;
  color: #2d8a6e;
}

.city-pin-icon svg {
  width: 100%;
  height: 100%;
}

.city-name {
  margin: 0;
  font-size: 20px;
  font-weight: 700;
  color: #1a5a45;
}

.visit-count {
  font-size: 13px;
  color: #6f8279;
  background: #e9f6ef;
  padding: 4px 12px;
  border-radius: 999px;
  font-weight: 500;
}

.city-date-range {
  font-size: 13px;
  color: #8fa69a;
  margin-bottom: 20px;
  padding-left: 32px;
}

/* ===== 景点列表 ===== */
.attractions-list {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.spot-item {
  display: flex;
  gap: 12px;
  align-items: flex-start;
  flex-wrap: wrap;
}

.spot-image {
  width: 64px;
  height: 64px;
  border-radius: 12px;
  object-fit: cover;
  flex-shrink: 0;
}

.spot-image--placeholder {
  background: #f0f7f4;
  display: flex;
  align-items: center;
  justify-content: center;
}

.spot-image--placeholder svg {
  width: 28px;
  height: 28px;
}

.spot-info {
  display: flex;
  flex-direction: column;
  gap: 4px;
  flex: 1;
  min-width: 0;
}

.spot-name {
  font-size: 15px;
  font-weight: 600;
  color: #1a5a45;
}

.spot-date {
  font-size: 12px;
  color: #8fa69a;
}

.spot-rating {
  display: flex;
  gap: 2px;
}

.star {
  font-size: 14px;
  color: #d1e8dd;
}

.star.filled {
  color: #f0c76d;
}

.spot-comment {
  width: 100%;
  margin: 4px 0 0;
  font-size: 13px;
  color: #6f8279;
  font-style: italic;
  line-height: 1.5;
  padding-left: 76px;
}

/* ===== 响应式 ===== */
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

  .summary-bar {
    padding: 20px;
    gap: 24px;
  }

  .summary-value {
    font-size: 28px;
  }

  .city-grid {
    grid-template-columns: 1fr;
    gap: 16px;
  }

  .city-card {
    padding: 20px;
  }
}
</style>
