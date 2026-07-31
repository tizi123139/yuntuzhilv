<template>
  <div class="page">
    <header class="header">
      <h1>酒店浏览</h1>
      <button @click="router.push('/services')">返回常用服务</button>
    </header>

    <section class="filters">
      <select v-model="query.star">
        <option value="">全部星级</option>
        <option value="5">五星</option>
        <option value="4">四星</option>
        <option value="3">三星</option>
      </select>
      <input v-model.number="query.maxPrice" type="number" placeholder="最高价格" />
      <button @click="handleFilter" :disabled="loading">
        {{ loading ? '加载中...' : '筛选' }}
      </button>
    </section>

    <!-- 加载状态提示 -->
    <div v-if="loading" class="status-tip">正在加载酒店数据...</div>

    <!-- 错误状态提示 -->
    <div v-else-if="loadError" class="status-tip error">{{ loadError }}</div>

    <!-- 空状态提示 -->
    <div v-else-if="list.length === 0" class="status-tip">
      {{ (query.star || query.maxPrice) ? '没有找到符合条件的酒店' : '暂无酒店数据，请等待后端上传' }}
    </div>

    <!-- 酒店列表 -->
    <section class="list" v-else>
      <article class="card" v-for="item in list" :key="item.hotelId">
        <h3>{{ item.name }}</h3>
        <p class="card-location">{{ item.city }} · {{ item.address }}</p>
        <p class="card-desc">{{ item.description }}</p>
        <div class="card-footer">
          <span class="star">{{ item.star }} 星</span>
          <span class="price">¥{{ item.price }}/晚</span>
        </div>
        <div class="card-facilities" v-if="item.facilities">
          <span v-for="f in item.facilities.split('、').slice(0, 3)" :key="f" class="tag">{{ f }}</span>
        </div>
        <button @click="showDetail(item)">查看详情</button>
      </article>
    </section>

    <!-- 分页控件（仅当总数超过每页条数时显示） -->
    <section class="pagination" v-if="total > pageSize">
      <button @click="handlePageChange(currentPage - 1)" :disabled="currentPage <= 1">上一页</button>
      <span v-for="page in totalPages" :key="page" :class="{ active: currentPage === page }"
        @click="handlePageChange(page)">{{ page }}</span>
      <button @click="handlePageChange(currentPage + 1)" :disabled="currentPage >= totalPages">下一页</button>
    </section>

    <!-- 详情弹窗 -->
    <div v-if="detailVisible" class="modal" @click.self="detailVisible = false">
      <div class="modal-inner">
        <h3>{{ detail.name }}</h3>
        <p class="detail-location">{{ detail.city }} · {{ detail.address }}</p>
        <div class="detail-info">
          <div class="info-item">
            <span>★</span>
            <span>{{ detail.star }} 星</span>
          </div>
          <div class="info-item">
            <span>💰</span>
            <span>¥{{ detail.price }}/晚</span>
          </div>
          <div class="info-item" v-if="detail.phone">
            <span>📞</span>
            <span>{{ detail.phone }}</span>
          </div>
        </div>
        <div class="detail-section">
          <span>📝</span>
          <span>{{ detail.description }}</span>
        </div>
        <div class="detail-section" v-if="detail.facilities">
          <span>🏨</span>
          <span>设施：{{ detail.facilities }}</span>
        </div>
        <div class="detail-section" v-if="detail.imgUrl">
          <img :src="detail.imgUrl" class="detail-img" alt="酒店图片" />
        </div>
        <button @click="detailVisible = false">关闭</button>
      </div>
    </div>
  </div>
</template>

<script setup>
/**
 * 酒店浏览页面
 *
 * 【架构说明】
 * 本页面不包含任何硬编码数据，所有酒店数据均来自后端接口。
 * 数据流：用户操作 → 调用 API → 后端返回数据 → 渲染列表
 * 若后端未部署，页面会显示空状态或错误提示，不会降级到假数据。
 *
 * 接口定义见 src/api/hotel.js，包含完整的请求参数和响应格式注释。
 */
import { computed, onMounted, reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import { hotelDetailApi, hotelListApi } from '../api/hotel'

const router = useRouter()

// ===== 筛选条件 =====
const query = reactive({ star: '', maxPrice: null })

// ===== 列表数据（由后端接口返回） =====
const list = ref([])         // 当前页的酒店列表
const total = ref(0)         // 符合条件的总条数（后端返回，用于分页）
const currentPage = ref(1)   // 当前页码
const pageSize = 6           // 每页条数（与后端约定一致）

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
 * 加载酒店列表
 * 每次调用都会向后端发起请求，携带筛选条件和分页参数。
 */
async function loadList() {
  loading.value = true
  loadError.value = ''

  try {
    // 向后端请求数据，传入筛选条件 + 分页参数
    const res = await hotelListApi({
      star: query.star ? Number(query.star) : undefined,
      maxPrice: query.maxPrice || undefined,
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
    console.error('[HotelList] 加载失败:', e)
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
 * 查看酒店详情
 * 调用详情接口获取更完整的信息（如设施、图片等）
 * 若详情接口失败，降级使用列表项已有的数据
 */
async function showDetail(item) {
  try {
    const id = item.hotelId || item.id
    const data = await hotelDetailApi(id)
    detail.value = data || item
  } catch (e) {
    // 详情接口失败时，使用列表中已有的数据展示
    detail.value = item
    console.warn('[HotelList] 详情接口失败，使用列表数据:', e)
  }
  detailVisible.value = true
}
</script>

<style scoped>
.page {
  min-height: 100vh;
  background: #f5fbf8;
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
  grid-template-columns: 180px 1fr 100px;
  gap: 10px;
}

.filters input,
.filters select {
  border: 1px solid #d7e7df;
  border-radius: 10px;
  padding: 10px;
}

.list {
  max-width: 960px;
  margin: 16px auto 0;
  display: grid;
  grid-template-columns: repeat(3, minmax(0, 1fr));
  gap: 12px;
}

.card {
  background: #fff;
  border-radius: 14px;
  padding: 14px;
  box-shadow: 0 8px 18px rgba(22, 76, 55, .08);
}

.card h3 {
  color: #1a5a45;
  margin-bottom: 8px;
}

.card p {
  color: #56736a;
  margin-bottom: 10px;
}

.card-location {
  font-size: 13px;
  color: #8a9e96;
}

.card-desc {
  font-size: 13px;
  color: #6b7280;
  line-height: 1.5;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.card-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 8px;
}

.card-footer .star {
  color: #f0c76d;
  font-weight: 600;
  font-size: 14px;
}

.card-footer .price {
  color: #dc2626;
  font-weight: 600;
  font-size: 14px;
}

.card-facilities {
  display: flex;
  gap: 6px;
  margin-bottom: 10px;
  flex-wrap: wrap;
}

.card-facilities .tag {
  font-size: 11px;
  padding: 2px 8px;
  background: #e8f5f0;
  color: #1a5a45;
  border-radius: 4px;
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
}

.modal-inner {
  width: 92%;
  max-width: 500px;
  background: #fff;
  border-radius: 14px;
  padding: 24px;
}

.modal-inner h3 {
  color: #1a5a45;
  font-size: 20px;
  margin-bottom: 8px;
}

.detail-location {
  color: #8a9e96;
  font-size: 14px;
  margin-bottom: 12px;
}

.detail-info {
  display: flex;
  gap: 20px;
  margin-bottom: 16px;
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

.detail-img {
  width: 100%;
  max-height: 200px;
  object-fit: cover;
  border-radius: 8px;
  margin-top: 4px;
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
