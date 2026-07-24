<template>
  <div class="admin-page">
    <div class="page-header">
      <h2>意见反馈管理</h2>
      <div class="filter-bar">
        <select v-model="query.status" @change="handleFilterChange">
          <option value="">全部</option>
          <option value="0">待处理</option>
          <option value="1">已处理</option>
        </select>
        <button @click="handleFilterChange">筛选</button>
      </div>
    </div>

    <div class="table-wrapper">
      <table class="data-table">
        <thead>
          <tr>
            <th>反馈ID</th>
            <th>用户名</th>
            <th>用户ID</th>
            <th>反馈内容</th>
            <th>状态</th>
            <th>提交时间</th>
            <th>操作</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="item in list" :key="item.feedbackId">
            <td>{{ item.feedbackId }}</td>
            <td>{{ item.username || '未登录用户' }}</td>
            <td>{{ item.userId || '-' }}</td>
            <td class="content-cell" @click="openDetail(item)">
              <span class="content-text">{{ item.content }}</span>
              <span class="view-detail">查看详情</span>
            </td>
            <td>
              <span :class="['status-tag', item.status === 1 ? 'active' : 'pending']">
                {{ item.status === 1 ? '已处理' : '待处理' }}
              </span>
            </td>
            <td>{{ item.createTime || '-' }}</td>
            <td>
              <button 
                v-if="item.status === 0" 
                class="action-btn success" 
                @click="handleProcess(item)"
                :disabled="processingId === item.feedbackId"
              >
                {{ processingId === item.feedbackId ? '处理中...' : '标记已处理' }}
              </button>
              <span v-else class="processed-text">已处理</span>
            </td>
          </tr>
          <tr v-if="!list.length && !loading">
            <td colspan="7" class="empty-row">暂无数据</td>
          </tr>
        </tbody>
      </table>
    </div>

    <!-- 分页 -->
    <div class="pagination" v-if="total > 0">
      <button :disabled="pageNum <= 1" @click="changePage(pageNum - 1)">上一页</button>
      <span class="page-info">第 {{ pageNum }} / {{ totalPages }} 页，共 {{ total }} 条</span>
      <button :disabled="pageNum >= totalPages" @click="changePage(pageNum + 1)">下一页</button>
    </div>

    <!-- 详情弹窗 -->
    <div v-if="detailVisible" class="modal-overlay" @click.self="detailVisible = false">
      <div class="detail-modal">
        <button class="modal-close" @click="detailVisible = false">&times;</button>
        <h3 class="detail-title">反馈详情</h3>
        <div class="detail-content">
          <div class="detail-item">
            <span class="detail-label">反馈ID：</span>
            <span class="detail-value">{{ currentDetail?.feedbackId }}</span>
          </div>
          <div class="detail-item">
            <span class="detail-label">用户名：</span>
            <span class="detail-value">{{ currentDetail?.username || '未登录用户' }}</span>
          </div>
          <div class="detail-item">
            <span class="detail-label">用户ID：</span>
            <span class="detail-value">{{ currentDetail?.userId || '-' }}</span>
          </div>
          <div class="detail-item">
            <span class="detail-label">状态：</span>
            <span :class="['detail-value', 'status-tag', currentDetail?.status === 1 ? 'active' : 'pending']">
              {{ currentDetail?.status === 1 ? '已处理' : '待处理' }}
            </span>
          </div>
          <div class="detail-item">
            <span class="detail-label">提交时间：</span>
            <span class="detail-value">{{ currentDetail?.createTime || '-' }}</span>
          </div>
          <div class="detail-item full-content">
            <span class="detail-label">反馈内容：</span>
            <div class="detail-text">{{ currentDetail?.content }}</div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
/**
 * 意见反馈管理页面（后台）
 *
 * 【架构说明】
 * 本页面展示用户提交的意见反馈列表，支持分页浏览、状态筛选和详情查看。
 * 所有数据来自后端接口，操作调用后端 API。
 */
import { computed, onMounted, ref } from 'vue'
import { showToast } from '../../utils/toast'
import { feedbackListApi, updateFeedbackStatusApi } from '../../api/admin'

// ===== 列表与分页 =====
const list = ref([])
const loading = ref(false)
const pageNum = ref(1)
const pageSize = ref(10)
const total = ref(0)
const totalPages = computed(() => Math.ceil(total.value / pageSize.value) || 1)

// ===== 查询条件 =====
const query = ref({ status: '' })

// ===== 处理状态 =====
const processingId = ref(null)

// ===== 详情弹窗 =====
const detailVisible = ref(false)
const currentDetail = ref(null)

onMounted(() => loadList())

/**
 * 加载反馈列表（服务端分页，支持状态筛选）
 */
async function loadList() {
  loading.value = true
  try {
    const params = {
      pageNum: pageNum.value,
      pageSize: pageSize.value
    }
    if (query.value.status !== '') {
      params.status = parseInt(query.value.status)
    }
    const res = await feedbackListApi(params)
    list.value = res?.list || res?.records || res?.rows || []
    total.value = res?.total ?? 0
  } catch (e) {
    list.value = []
    total.value = 0
    console.error('[AdminFeedback] 加载失败:', e)
    showToast('反馈数据加载失败，请检查后端服务是否正常', 'error')
  } finally {
    loading.value = false
  }
}

/**
 * 翻页
 */
function changePage(p) {
  pageNum.value = p
  loadList()
}

/**
 * 筛选条件变化
 */
function handleFilterChange() {
  pageNum.value = 1
  loadList()
}

/**
 * 标记反馈为已处理
 */
async function handleProcess(item) {
  processingId.value = item.feedbackId
  try {
    await updateFeedbackStatusApi({
      feedbackId: item.feedbackId,
      status: 1
    })
    item.status = 1
    showToast('标记成功', 'success')
  } catch (e) {
    console.error('[AdminFeedback] 状态更新失败:', e)
    showToast('操作失败，请稍后重试', 'error')
  } finally {
    processingId.value = null
  }
}

/**
 * 打开详情弹窗
 */
function openDetail(item) {
  currentDetail.value = item
  detailVisible.value = true
}
</script>

<style scoped>
.admin-page {
  background: #fff;
  border-radius: 16px;
  padding: 28px;
  box-shadow: 0 4px 16px rgba(15, 81, 50, 0.06);
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
  flex-wrap: wrap;
  gap: 12px;
}

.page-header h2 {
  margin: 0;
  color: #0f5132;
  font-size: 22px;
}

.filter-bar {
  display: flex;
  gap: 8px;
}

.filter-bar select {
  padding: 10px 14px;
  border: 1px solid #d7e7df;
  border-radius: 8px;
  font-size: 14px;
  outline: none;
  background: #fff;
  color: #333;
}

.filter-bar select:focus {
  border-color: #2d8a6e;
}

.filter-bar button {
  padding: 10px 18px;
  background: #1a5a45;
  color: #fff;
  border: none;
  border-radius: 8px;
  cursor: pointer;
  font-size: 14px;
}

.table-wrapper {
  overflow-x: auto;
}

.data-table {
  width: 100%;
  border-collapse: collapse;
}

.data-table th {
  background: #f0f7f3;
  color: #0f5132;
  padding: 14px 16px;
  text-align: left;
  font-weight: 600;
  font-size: 14px;
  white-space: nowrap;
}

.data-table td {
  padding: 14px 16px;
  border-bottom: 1px solid #eef5f0;
  font-size: 14px;
  color: #333;
}

.data-table tr:hover {
  background: #f8fcfa;
}

.content-cell {
  max-width: 300px;
  overflow: hidden;
  cursor: pointer;
  position: relative;
}

.content-text {
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
  line-height: 1.6;
  color: #555;
}

.view-detail {
  display: block;
  font-size: 12px;
  color: #2d8a6e;
  margin-top: 4px;
}

.status-tag {
  padding: 4px 12px;
  border-radius: 999px;
  font-size: 12px;
  font-weight: 600;
}

.status-tag.pending {
  background: #fff7e6;
  color: #fa8c16;
}

.status-tag.active {
  background: #e8f5ef;
  color: #1a5a45;
}

.action-btn {
  padding: 6px 14px;
  border: none;
  border-radius: 6px;
  cursor: pointer;
  font-size: 13px;
}

.action-btn:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}

.action-btn.success {
  background: #e8f5ef;
  color: #1a5a45;
}

.action-btn.success:hover {
  background: #d4edda;
}

.processed-text {
  font-size: 13px;
  color: #999;
}

.empty-row {
  text-align: center;
  color: #999;
  padding: 32px !important;
}

.pagination {
  display: flex;
  justify-content: center;
  align-items: center;
  gap: 16px;
  margin-top: 20px;
}

.pagination button {
  padding: 8px 16px;
  border: 1px solid #d7e7df;
  border-radius: 8px;
  background: #fff;
  color: #1a5a45;
  cursor: pointer;
}

.pagination button:disabled {
  opacity: 0.4;
  cursor: not-allowed;
}

.page-info {
  font-size: 14px;
  color: #666;
}

/* 详情弹窗 */
.modal-overlay {
  position: fixed;
  inset: 0;
  background: rgba(0, 0, 0, 0.45);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1000;
}

.detail-modal {
  background: #fff;
  border-radius: 14px;
  padding: 28px;
  width: 90%;
  max-width: 500px;
  position: relative;
}

.modal-close {
  position: absolute;
  top: 12px;
  right: 14px;
  background: none;
  border: none;
  font-size: 26px;
  color: #999;
  cursor: pointer;
  line-height: 1;
}

.modal-close:hover {
  color: #2d8a6e;
}

.detail-title {
  margin: 0 0 20px;
  color: #0f5132;
  font-size: 18px;
}

.detail-content {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.detail-item {
  display: flex;
  align-items: flex-start;
  gap: 8px;
}

.detail-item.full-content {
  flex-direction: column;
}

.detail-label {
  font-size: 14px;
  color: #666;
  font-weight: 500;
  white-space: nowrap;
}

.detail-value {
  font-size: 14px;
  color: #333;
}

.detail-text {
  font-size: 14px;
  color: #333;
  line-height: 1.8;
  padding: 12px;
  background: #f8fcfa;
  border-radius: 8px;
  margin-top: 4px;
  white-space: pre-wrap;
}
</style>