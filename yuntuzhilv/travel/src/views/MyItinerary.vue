<template>
  <div class="itinerary-page">
    <div class="bg-decoration"></div>
    <div class="bg-circle bg-circle-1"></div>
    <div class="bg-circle bg-circle-2"></div>
    <div class="bg-circle bg-circle-3"></div>

    <div class="page-container">
      <header class="page-header">
        <div class="header-left">
          <button class="back-arrow" @click="router.push('/home')" title="返回首页">
            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
              <line x1="19" y1="12" x2="5" y2="12"/>
              <polyline points="12 19 5 12 12 5"/>
            </svg>
          </button>
          <div class="header-content">
            <div class="header-icon">
              <svg width="32" height="32" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                <path d="M12 2L2 7l10 5 10-5-10-5z" />
                <path d="M2 17l10 5 10-5" />
                <path d="M2 12l10 5 10-5" />
              </svg>
            </div>
            <div>
              <h1>我的行程</h1>
              <p class="subtitle">管理你的旅行计划</p>
            </div>
          </div>
        </div>
        <button class="create-btn" @click="router.push('/create-itinerary')">
          <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
            <line x1="12" y1="5" x2="12" y2="19" />
            <line x1="5" y1="12" x2="19" y2="12" />
          </svg>
          <span>新建行程</span>
        </button>
      </header>

      <section class="stats-row">
        <div class="stat-card">
          <div class="stat-icon blue">
            <svg width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
              <path d="M21 16V8a2 2 0 0 0-1-1.73l-7-4a2 2 0 0 0-2 0l-7 4A2 2 0 0 0 3 8v8a2 2 0 0 0 1 1.73l7 4a2 2 0 0 0 2 0l7-4A2 2 0 0 0 21 16z" />
              <polyline points="3.27 6.96 12 12.01 20.73 6.96" />
              <line x1="12" y1="22.08" x2="12" y2="12" />
            </svg>
          </div>
          <div class="stat-info">
            <div class="stat-value">{{ list.length }}</div>
            <div class="stat-label">总行程数</div>
          </div>
        </div>
        <div class="stat-card">
          <div class="stat-icon green">
            <svg width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
              <path d="M20 13V6a2 2 0 0 0-2-2H6a2 2 0 0 0-2 2v7m16 0v5a2 2 0 0 1-2 2H6a2 2 0 0 1-2-2v-5m16 0h-2.586a1 1 0 0 0-.707.293l-2.414 2.414a1 1 0 0 1-.707.293h-3.172a1 1 0 0 1-.707-.293l-2.414-2.414A1 1 0 0 0 6.586 13H4" />
            </svg>
          </div>
          <div class="stat-info">
            <div class="stat-value">¥{{ totalBudget }}</div>
            <div class="stat-label">总预算</div>
          </div>
        </div>
        <div class="stat-card">
          <div class="stat-icon orange">
            <svg width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
              <circle cx="12" cy="12" r="10" />
              <polyline points="12 6 12 12 16 14" />
            </svg>
          </div>
          <div class="stat-info">
            <div class="stat-value">{{ totalDays }}</div>
            <div class="stat-label">总天数</div>
          </div>
        </div>
      </section>

      <section class="itinerary-list" v-if="list.length">
        <div class="list-header">
          <h2>行程列表</h2>
          <div class="filter-tabs">
            <button 
              v-for="tab in tabs" 
              :key="tab.value"
              :class="['filter-tab', { active: activeTab === tab.value }]"
              @click="activeTab = tab.value"
            >
              {{ tab.label }}
            </button>
          </div>
        </div>

        <div class="cards-grid">
          <article class="itinerary-card" v-for="item in filteredList" :key="item.id">
            <div class="card-header">
              <div class="card-tag" :class="getStatusClass(item.status)">
                {{ getStatusText(item.status) }}
              </div>
              <div class="card-date">{{ item.createTime || formatDate(new Date()) }}</div>
            </div>
            
            <div class="card-body">
              <h3 class="card-title">{{ item.title }}</h3>
              
              <div class="trip-info">
                <div class="location-row">
                  <div class="location-item">
                    <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                      <circle cx="12" cy="12" r="10" />
                      <circle cx="12" cy="12" r="3" />
                    </svg>
                    <span>{{ item.startCity || '出发地' }}</span>
                  </div>
                  <div class="arrow-icon">
                    <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                      <line x1="5" y1="12" x2="19" y2="12" />
                      <polyline points="12 5 19 12 12 19" />
                    </svg>
                  </div>
                  <div class="location-item">
                    <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                      <path d="M21 10c0 7-9 13-9 13s-9-6-9-13a9 9 0 0 1 18 0z" />
                      <circle cx="12" cy="10" r="3" />
                    </svg>
                    <span>{{ item.destination || '目的地' }}</span>
                  </div>
                </div>
              </div>

              <div class="card-meta">
                <div class="meta-item">
                  <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                    <circle cx="12" cy="12" r="10" />
                    <polyline points="12 6 12 12 16 14" />
                  </svg>
                  <span>{{ item.days }} 天</span>
                </div>
                <div class="meta-item">
                  <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                    <line x1="12" y1="1" x2="12" y2="23" />
                    <path d="M17 5H9.5a3.5 3.5 0 0 0 0 7h5a3.5 3.5 0 0 1 0 7H6" />
                  </svg>
                  <span>{{ item.people || 2 }} 人</span>
                </div>
                <div class="meta-item price">
                  <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                    <line x1="12" y1="1" x2="12" y2="23" />
                    <path d="M17 5H9.5a3.5 3.5 0 0 0 0 7h5a3.5 3.5 0 0 1 0 7H6" />
                  </svg>
                  <span>¥{{ item.totalBudget }}</span>
                </div>
              </div>

              <div class="card-tags">
                <span v-for="tag in item.tags?.slice(0, 3) || []" :key="tag" class="card-tag-sm">{{ tag }}</span>
              </div>
            </div>

            <div class="card-footer">
              <button class="action-btn primary" @click="viewDetail(item)">
                <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                  <path d="M15 12a3 3 0 1 1-6 0 3 3 0 0 1 6 0z" />
                  <path d="M2.458 12C3.732 7.943 7.523 5 12 5c4.478 0 8.268 2.943 9.542 7-1.274 4.057-5.064 7-9.542 7-4.477 0-8.268-2.943-9.542-7z" />
                </svg>
                <span>查看详情</span>
              </button>
              <button 
                v-if="item.status?.toLowerCase() !== 'completed'"
                class="action-btn success" 
                @click="completeItem(item)"
              >
                <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                  <polyline points="20 6 9 17 4 12" />
                </svg>
                <span>已完成</span>
              </button>
              <button 
                v-else
                class="action-btn" 
                disabled
              >
                <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                  <polyline points="20 6 9 17 4 12" />
                </svg>
                <span>已完成</span>
              </button>
              <button class="action-btn" @click="editItem(item)">
                <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                  <path d="M11 4H4a2 2 0 0 0-2 2v14a2 2 0 0 0 2 2h14a2 2 0 0 0 2-2v-7" />
                  <path d="M18.5 2.5a2.121 2.121 0 0 1 3 3L12 15l-4 1 1-4 9.5-9.5z" />
                </svg>
                <span>编辑</span>
              </button>
              <button class="action-btn" @click="removeItem(item)">
                <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                  <polyline points="3 6 5 6 21 6" />
                  <path d="M19 6v14a2 2 0 0 1-2 2H7a2 2 0 0 1-2-2V6m3 0V4a2 2 0 0 1 2-2h4a2 2 0 0 1 2 2v2" />
                </svg>
                <span>删除</span>
              </button>
              <button class="action-btn outline" @click="bookItem(item)">
                <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                  <path d="M16 11V7a4 4 0 0 0-8 0v4M5 9h14l1 12H4L5 9z" />
                </svg>
                <span>预订</span>
              </button>
            </div>
          </article>
        </div>
      </section>

      <div class="empty-state" v-else>
        <div class="empty-icon">
          <svg width="64" height="64" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5">
            <path d="M12 2L2 7l10 5 10-5-10-5z" />
            <path d="M2 17l10 5 10-5" />
            <path d="M2 12l10 5 10-5" />
          </svg>
        </div>
        <h3>暂无行程计划</h3>
        <p>快去创建你的专属旅行计划吧</p>
        <button class="empty-btn" @click="router.push('/create-itinerary')">
          创建行程
        </button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { onMounted, ref, computed } from 'vue'
import { showToast } from '../utils/toast';
import { useRouter } from 'vue-router'
import { bookItineraryApi, deleteItineraryApi, myItineraryListApi, archiveItineraryApi } from '../api/itinerary'

const router = useRouter()
const list = ref([])
const activeTab = ref('all')

const tabs = [
  { label: '全部', value: 'all' },
  { label: '进行中', value: 'active' },
  { label: '已完成', value: 'completed' }
]

const filteredList = computed(() => {
  if (activeTab.value === 'all') return list.value
  return list.value.filter(item => {
    const status = (item.status || '').toLowerCase()
    return status === activeTab.value
  })
})

const totalBudget = computed(() => {
  return list.value.reduce((sum, item) => sum + (item.totalBudget || 0), 0)
})

const totalDays = computed(() => {
  return list.value.reduce((sum, item) => sum + (item.days || 0), 0)
})

function formatDate(date) {
  const d = new Date(date)
  return `${d.getFullYear()}-${String(d.getMonth() + 1).padStart(2, '0')}-${String(d.getDate()).padStart(2, '0')}`
}

function getStatusText(status) {
  const map = {
    ACTIVE: '进行中',
    active: '进行中',
    completed: '已完成',
    planned: '待出发'
  }
  return map[status] || '待出发'
}

function getStatusClass(status) {
  const map = {
    ACTIVE: 'status-active',
    active: 'status-active',
    completed: 'status-completed',
    planned: 'status-planned'
  }
  return map[status] || 'status-planned'
}

// 纯 API 消费：从后端获取当前用户的行程列表
onMounted(async () => {
  try {
    const res = await myItineraryListApi()
    list.value = res?.records || res?.data?.list || res?.list || []
  } catch (e) {
    list.value = []
    showToast('获取行程列表失败，请重试', 'error')
  }
})

// 查看详情：携带行程 ID 跳转结果页，结果页通过 API 获取完整数据
function viewDetail(item) {
  router.push(`/itinerary-result?id=${item.itineraryId}`)
}

// 编辑：携带表单数据跳转至创建页，通过 query 预填
function editItem(item) {
  router.push({
    path: '/create-itinerary',
    query: {
      fromCity: item.startCity,
      toCity: item.destination,
      days: item.days,
      budget: item.totalBudget
    }
  })
}

// 删除：调用后端删除接口，成功后从本地列表移除
async function removeItem(item) {
  const ok = confirm(`确认删除”${item.title}”吗？`)
  if (!ok) return

  try {
    await deleteItineraryApi(item.itineraryId)
    list.value = list.value.filter(v => v.itineraryId !== item.itineraryId)
  } catch (e) {
    showToast('删除失败，请重试', 'error')
  }
}

// 预订：调用后端预订接口
async function bookItem(item) {
  try {
    const res = await bookItineraryApi(item.itineraryId)
    showToast(res?.message || `预订成功：${item.title}`, 'success')
  } catch (e) {
    showToast('预订失败，请重试', 'error')
  }
}

// 标记行程为已完成
async function completeItem(item) {
  const ok = confirm(`确认标记"${item.title}"为已完成吗？`)
  if (!ok) return

  try {
    await archiveItineraryApi(item.itineraryId)
    item.status = 'completed'
    item.isArchived = 1
    showToast('已标记为完成', 'success')
  } catch (e) {
    showToast('操作失败，请重试', 'error')
  }
}
</script>

<style scoped>
.itinerary-page {
  min-height: 100vh;
  background: linear-gradient(180deg, #f0fdf4 0%, #f8fafc 100%);
  position: relative;
  overflow: hidden;
}

.bg-decoration {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  height: 300px;
  background: linear-gradient(135deg, rgba(81, 184, 145, 0.1) 0%, rgba(45, 138, 110, 0.05) 100%);
  pointer-events: none;
}

.bg-circle {
  position: absolute;
  border-radius: 50%;
  background: rgba(81, 184, 145, 0.08);
  pointer-events: none;
}

.bg-circle-1 {
  width: 400px;
  height: 400px;
  top: -100px;
  right: -100px;
}

.bg-circle-2 {
  width: 200px;
  height: 200px;
  bottom: 100px;
  left: -50px;
}

.bg-circle-3 {
  width: 150px;
  height: 150px;
  top: 200px;
  left: 200px;
}

.page-container {
  position: relative;
  z-index: 1;
  max-width: 1200px;
  margin: 0 auto;
  padding: 40px 24px;
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 32px;
}

.header-content {
  display: flex;
  align-items: center;
  gap: 16px;
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

.header-icon {
  width: 48px;
  height: 48px;
  background: linear-gradient(135deg, #51b891 0%, #2d8a6e 100%);
  border-radius: 14px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #fff;
}

.page-header h1 {
  font-size: 28px;
  font-weight: 700;
  color: #0f5132;
  margin: 0;
}

.subtitle {
  font-size: 14px;
  color: #6b7d76;
  margin: 4px 0 0;
}

.create-btn {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 12px 20px;
  background: linear-gradient(135deg, #2d8a6e 0%, #1a5a45 100%);
  color: #fff;
  border: none;
  border-radius: 12px;
  font-size: 14px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.3s;
  box-shadow: 0 4px 12px rgba(45, 138, 110, 0.3);
}

.create-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 6px 20px rgba(45, 138, 110, 0.4);
}

.stats-row {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 16px;
  margin-bottom: 32px;
}

.stat-card {
  background: #fff;
  border-radius: 16px;
  padding: 20px;
  display: flex;
  align-items: center;
  gap: 16px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.05);
}

.stat-icon {
  width: 48px;
  height: 48px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.stat-icon.blue {
  background: linear-gradient(135deg, #dbeafe 0%, #bfdbfe 100%);
  color: #2563eb;
}

.stat-icon.green {
  background: linear-gradient(135deg, #dcfce7 0%, #bbf7d0 100%);
  color: #16a34a;
}

.stat-icon.orange {
  background: linear-gradient(135deg, #ffedd5 0%, #fed7aa 100%);
  color: #f97316;
}

.stat-value {
  font-size: 24px;
  font-weight: 700;
  color: #0f5132;
}

.stat-label {
  font-size: 13px;
  color: #6b7d76;
}

.itinerary-list {
  margin-top: 24px;
}

.list-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.list-header h2 {
  font-size: 18px;
  font-weight: 600;
  color: #0f5132;
  margin: 0;
}

.filter-tabs {
  display: flex;
  gap: 8px;
  background: #fff;
  padding: 4px;
  border-radius: 10px;
}

.filter-tab {
  padding: 8px 16px;
  border: none;
  border-radius: 8px;
  background: transparent;
  color: #6b7d76;
  font-size: 13px;
  cursor: pointer;
  transition: all 0.2s;
}

.filter-tab.active {
  background: linear-gradient(135deg, #51b891 0%, #2d8a6e 100%);
  color: #fff;
}

.cards-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 20px;
}

.itinerary-card {
  background: #fff;
  border-radius: 20px;
  padding: 24px;
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.06);
  border: 2px solid rgba(81, 184, 145, 0.1);
  transition: all 0.3s;
}

.itinerary-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 12px 32px rgba(0, 0, 0, 0.1);
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
}

.card-tag {
  padding: 4px 12px;
  border-radius: 20px;
  font-size: 12px;
  font-weight: 500;
}

.status-active {
  background: rgba(34, 197, 94, 0.1);
  color: #16a34a;
}

.status-completed {
  background: rgba(148, 163, 184, 0.1);
  color: #64748b;
}

.status-planned {
  background: rgba(249, 115, 22, 0.1);
  color: #f97316;
}

.card-date {
  font-size: 12px;
  color: #9cb5ac;
}

.card-title {
  font-size: 18px;
  font-weight: 600;
  color: #0f5132;
  margin: 0 0 12px;
}

.trip-info {
  margin-bottom: 16px;
}

.location-row {
  display: flex;
  align-items: center;
  gap: 8px;
}

.location-item {
  display: flex;
  align-items: center;
  gap: 6px;
  color: #6b7d76;
  font-size: 14px;
}

.location-item:first-child {
  color: #2d8a6e;
}

.location-item:last-child {
  color: #f97316;
}

.arrow-icon {
  color: #51b891;
}

.card-meta {
  display: flex;
  gap: 20px;
  margin-bottom: 12px;
}

.meta-item {
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: 13px;
  color: #6b7d76;
}

.meta-item.price {
  color: #f97316;
  font-weight: 600;
}

.card-tags {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
  margin-bottom: 16px;
}

.card-tag-sm {
  padding: 4px 10px;
  background: rgba(81, 184, 145, 0.1);
  color: #2d8a6e;
  border-radius: 12px;
  font-size: 12px;
}

.card-footer {
  display: flex;
  gap: 8px;
  padding-top: 16px;
  border-top: 1px solid #f0fdf4;
}

.action-btn {
  display: flex;
  align-items: center;
  gap: 6px;
  padding: 8px 14px;
  border: none;
  border-radius: 10px;
  font-size: 13px;
  cursor: pointer;
  transition: all 0.2s;
}

.action-btn.primary {
  background: linear-gradient(135deg, #51b891 0%, #2d8a6e 100%);
  color: #fff;
}

.action-btn.primary:hover {
  box-shadow: 0 4px 12px rgba(81, 184, 145, 0.4);
}

.action-btn.success {
  background: linear-gradient(135deg, #34d399 0%, #10b981 100%);
  color: #fff;
}

.action-btn.success:hover {
  box-shadow: 0 4px 12px rgba(16, 185, 129, 0.4);
}

.action-btn.success:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

.action-btn:disabled {
  opacity: 0.45;
  cursor: not-allowed;
}

.action-btn:not(.primary):not(.outline):not(.success) {
  background: rgba(81, 184, 145, 0.1);
  color: #2d8a6e;
}

.action-btn:not(.primary):not(.outline):not(.success):hover {
  background: rgba(81, 184, 145, 0.2);
}

.action-btn.outline {
  background: transparent;
  color: #2d8a6e;
  border: 1px solid #51b891;
}

.action-btn.outline:hover {
  background: rgba(81, 184, 145, 0.1);
}

.empty-state {
  text-align: center;
  padding: 60px 20px;
}

.empty-icon {
  width: 120px;
  height: 120px;
  margin: 0 auto 24px;
  background: rgba(81, 184, 145, 0.08);
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #51b891;
}

.empty-state h3 {
  font-size: 20px;
  font-weight: 600;
  color: #0f5132;
  margin: 0 0 8px;
}

.empty-state p {
  font-size: 14px;
  color: #6b7d76;
  margin: 0 0 24px;
}

.empty-btn {
  padding: 12px 32px;
  background: linear-gradient(135deg, #51b891 0%, #2d8a6e 100%);
  color: #fff;
  border: none;
  border-radius: 12px;
  font-size: 15px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.3s;
}

.empty-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 6px 20px rgba(81, 184, 145, 0.4);
}

@media (max-width: 768px) {
  .page-container {
    padding: 24px 16px;
  }

  .page-header {
    flex-direction: column;
    align-items: flex-start;
    gap: 16px;
  }

  .stats-row {
    grid-template-columns: 1fr;
  }

  .cards-grid {
    grid-template-columns: 1fr;
  }

  .card-footer {
    flex-wrap: wrap;
  }
}
</style>