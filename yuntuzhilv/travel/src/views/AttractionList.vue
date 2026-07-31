<template>
  <div class="page">
    <header class="header">
      <h1>景点浏览</h1>
      <button @click="router.push('/services')">返回常用服务</button>
    </header>

    <section class="filters">
      <input v-model.trim="query.city" placeholder="按城市筛选，例如：杭州" />
      <select v-model="query.type">
        <option value="">全部类型</option>
        <option value="历史古迹">历史古迹</option>
        <option value="自然风光">自然风光</option>
        <option value="美食街区">美食街区</option>
      </select>
      <button @click="handleFilter" :disabled="loading">
        {{ loading ? '加载中...' : '筛选' }}
      </button>
    </section>

    <!-- 加载状态提示 -->
    <div v-if="loading" class="status-tip">正在加载景点数据...</div>

    <!-- 错误状态提示 -->
    <div v-else-if="loadError" class="status-tip error">{{ loadError }}</div>

    <!-- 空状态提示（无筛选结果或后端暂无数据） -->
    <div v-else-if="list.length === 0" class="status-tip">
      {{ (query.city || query.type) ? '没有找到符合条件的景点' : '暂无景点数据，请等待后端上传' }}
    </div>

    <!-- 景点列表 -->
    <section class="list" v-else>
      <article class="card" v-for="item in list" :key="item.attractionId">
        <h3>{{ item.name }}</h3>
        <p>{{ item.city }} · {{ item.type }}</p>
        <p class="card-desc">{{ item.description }}</p>
        <div class="card-footer">
          <span class="price">{{ item.price || '免费' }}</span>
          <span class="rating">★ {{ item.rating || '4.0' }}</span>
        </div>
        <button @click="showDetail(item)">查看详情</button>
      </article>
    </section>

    <section class="pagination" v-if="total > pageSize">
      <button @click="handlePageChange(currentPage - 1)" :disabled="currentPage <= 1">上一页</button>
      <span v-for="page in totalPages" :key="page" :class="{ active: currentPage === page }"
        @click="handlePageChange(page)">{{ page }}</span>
      <button @click="handlePageChange(currentPage + 1)" :disabled="currentPage >= totalPages">下一页</button>
    </section>

    <div v-if="detailVisible" class="modal" @click.self="detailVisible = false">
      <div class="modal-inner">
        <h2>{{ detail.name }}</h2>
        <p class="detail-meta">{{ detail.city }} · {{ detail.type }}</p>
        <div class="detail-info">
          <div class="info-item">
            <span>★</span>
            <span>{{ detail.rating || '4.0' }}</span>
          </div>
          <div class="info-item">
            <span>🎫</span>
            <span>{{ detail.price || '免费' }}</span>
          </div>
          <div class="info-item">
            <span>⏰</span>
            <span>{{ detail.openTime || '全天开放' }}</span>
          </div>
        </div>
        <div class="detail-section">
          <span>📍</span>
          <span>详细地址：{{ detail.address }}</span>
        </div>
        <div class="detail-section">
          <span>📝</span>
          <span>{{ detail.description }}</span>
        </div>
        <button @click="detailVisible = false">关闭</button>
      </div>
    </div>
  </div>
</template>

<script setup>
/**
 * 景点浏览页面
 *
 * 【架构说明】
 * 本页面不包含任何硬编码数据，所有景点数据均来自后端接口。
 * 数据流：用户操作 → 调用 API → 后端返回数据 → 渲染列表
 * 若后端未部署，页面会显示空状态或错误提示，不会降级到假数据。
 *
 * 接口定义见 src/api/attraction.js，包含完整的请求参数和响应格式注释。
 */
import { computed, onMounted, reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import { attractionListApi, attractionDetailApi } from '../api/attraction'

const router = useRouter()

// ===== 筛选条件 =====
const query = reactive({ city: '', type: '' })

// ===== 列表数据（由后端接口返回） =====
const list = ref([])         // 当前页的景点列表
const total = ref(0)         // 符合条件的总条数（后端返回，用于分页）
const currentPage = ref(1)   // 当前页码
const pageSize = 6           // 每页条数（与后端约定的 pageSize 一致）

// ===== UI 状态 =====
const loading = ref(false)   // 是否正在加载数据
const loadError = ref('')    // 加载失败时的错误信息

// 详情弹窗
const detailVisible = ref(false)
const detail = ref({})

// 计算总页数（基于后端返回的 total）
const totalPages = computed(() => Math.ceil(total.value / pageSize))

onMounted(() => loadList())

/**
 * 加载景点列表
 * 每次调用都会向后端发起请求，携带筛选条件和分页参数。
 * 筛选或翻页时重新调用此函数。
 */
async function loadList() {
  loading.value = true
  loadError.value = ''

  try {
    // 向后端请求数据，传入筛选条件 + 分页参数
    const res = await attractionListApi({
      city: query.city,
      type: query.type,
      pageNum: currentPage.value,
      pageSize
    })

    // 后端返回 MyBatis-Plus Page 对象：{ records: [], total: number }
    list.value = res?.records || res?.list || []
    total.value = res?.total ?? 0
  } catch (e) {
    // 请求失败：清空列表，显示错误提示
    list.value = []
    total.value = 0
    loadError.value = '数据加载失败，请稍后重试'
    console.error('[AttractionList] 加载失败:', e)
  } finally {
    loading.value = false
  }
}

/**
 * 点击"筛选"按钮：重置到第1页，重新加载
 */
function handleFilter() {
  currentPage.value = 1
  loadList()
}

/**
 * 翻页：更新页码后重新加载（服务端分页）
 */
function handlePageChange(page) {
  if (page < 1 || page > totalPages.value) return
  currentPage.value = page
  loadList()
}

/**
 * 查看详情：调用后端详情接口获取完整数据
 */
async function showDetail(item) {
  try {
    const data = await attractionDetailApi(item.attractionId)
    detail.value = data
    detailVisible.value = true
  } catch (e) {
    // 接口失败时降级使用列表数据
    detail.value = item
    detailVisible.value = true
  }
}
</script>

<style scoped>
.page {
  min-height: 100vh;
  background: linear-gradient(135deg, #e8f5f0 0%, #f5fbf8 50%, #fef9f0 100%);
  padding: 24px;
}

.header {
  max-width: 960px;
  margin: 0 auto;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.header button,
.filters button,
.card button,
.modal-inner button {
  border: none;
  border-radius: 8px;
  padding: 8px 12px;
  background: #1a5a45;
  color: #fff;
  cursor: pointer;
}

.filters {
  max-width: 960px;
  margin: 14px auto 0;
  display: grid;
  grid-template-columns: 1fr 180px 100px;
  gap: 10px;
}

.filters input,
.filters select {
  border: 2px solid #c5e8db;
  border-radius: 10px;
  padding: 10px;
  background: rgba(255, 255, 255, 0.8);
}

.list {
  max-width: 960px;
  margin: 16px auto 0;
  display: grid;
  grid-template-columns: repeat(3, minmax(0, 1fr));
  gap: 16px;
}

.card {
  background: #fff;
  border-radius: 16px;
  padding: 16px;
  border: 2px solid #c5e8db;
  box-shadow: 0 4px 16px rgba(26, 90, 69, 0.1);
  position: relative;
  overflow: hidden;
}

.card::before {
  content: '';
  position: absolute;
  top: 0;
  right: 0;
  width: 60px;
  height: 60px;
  background: linear-gradient(135deg, rgba(197, 232, 219, 0.5) 0%, transparent 100%);
  border-radius: 0 16px 0 50%;
}

.card h3 {
  color: #1a5a45;
  margin-bottom: 8px;
  font-size: 18px;
  position: relative;
  z-index: 1;
}

.card p {
  color: #56736a;
  margin-bottom: 10px;
  font-size: 14px;
  position: relative;
  z-index: 1;
}

.card-desc {
  color: #6b7280;
  font-size: 13px;
  line-height: 1.5;
  margin-bottom: 12px;
  display: -webkit-box;
  -webkit-line-clamp: 3;
  line-clamp: 3;
  -webkit-box-orient: vertical;
  overflow: hidden;
  position: relative;
  z-index: 1;
}

.card-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 12px;
  position: relative;
  z-index: 1;
}

.card-footer .price {
  color: #dc2626;
  font-weight: 600;
  font-size: 14px;
}

.card-footer .rating {
  color: #f0c76d;
  font-weight: 600;
  font-size: 14px;
}

.card button {
  position: relative;
  z-index: 1;
}

.pagination {
  max-width: 960px;
  margin: 24px auto 0;
  display: flex;
  justify-content: center;
  align-items: center;
  gap: 12px;
}

.pagination button {
  border: 1px solid #d7e7df;
  border-radius: 8px;
  padding: 8px 16px;
  background: #fff;
  color: #1a5a45;
  cursor: pointer;
}

.pagination button:hover:not(:disabled) {
  background: #f5fbf8;
}

.pagination button:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

.pagination span {
  width: 36px;
  height: 36px;
  line-height: 36px;
  text-align: center;
  border-radius: 8px;
  cursor: pointer;
  color: #56736a;
}

.pagination span.active {
  background: #1a5a45;
  color: #fff;
}

.status-tip {
  max-width: 960px;
  margin: 24px auto;
  text-align: center;
  color: #6b7c76;
  font-size: 14px;
  padding: 24px;
  background: #fff;
  border-radius: 12px;
  border: 1px dashed #c5e8db;
}

.status-tip.error {
  color: #dc2626;
  border-color: #fecaca;
  background: #fef2f2;
}

.modal {
  position: fixed;
  inset: 0;
  background: rgba(0, 0, 0, .5);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1000;
}

.modal-inner {
  width: 92%;
  max-width: 500px;
  background: #fff;
  border-radius: 14px;
  padding: 24px;
}

.modal-inner h2 {
  color: #1a5a45;
  font-size: 24px;
  margin-bottom: 8px;
}

.detail-meta {
  color: #56736a;
  font-size: 16px;
  margin-bottom: 16px;
}

.detail-info {
  display: flex;
  gap: 24px;
  margin-bottom: 20px;
}

.info-item {
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: 14px;
  color: #56736a;
}

.info-item span:first-child {
  font-size: 16px;
}

.detail-section {
  display: flex;
  align-items: flex-start;
  gap: 8px;
  margin-bottom: 12px;
  font-size: 14px;
  color: #56736a;
  line-height: 1.6;
}

.detail-section span:first-child {
  font-size: 16px;
  flex-shrink: 0;
}

@media (max-width: 900px) {
  .page {
    padding: 16px;
  }

  .filters {
    grid-template-columns: 1fr;
  }

  .list {
    grid-template-columns: 1fr;
  }
}
</style>
