<template>
  <div class="admin-page">
    <div class="page-header">
      <h2>用户管理</h2>
      <div class="search-bar">
        <input v-model.trim="query.username" placeholder="搜索用户名" @keyup.enter="loadList" />
        <button @click="loadList">搜索</button>
      </div>
    </div>

    <div class="table-wrapper">
      <table class="data-table">
        <thead>
          <tr>
            <th>用户ID</th>
            <th>用户名</th>
            <th>状态</th>
            <th>创建时间</th>
            <th>操作</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="item in list" :key="item.userId">
            <td>{{ item.userId }}</td>
            <td>{{ item.username }}</td>
            <td>
              <span :class="['status-tag', item.status === 1 ? 'active' : 'disabled']">
                {{ item.status === 1 ? '正常' : '禁用' }}
              </span>
            </td>
            <td>{{ item.createTime || '-' }}</td>
            <td>
              <button :class="['action-btn', item.status === 1 ? 'warn' : 'success']" @click="toggleStatus(item)">
                {{ item.status === 1 ? '禁用' : '启用' }}
              </button>
            </td>
          </tr>
          <tr v-if="!list.length && !loading">
            <td colspan="5" class="empty-row">暂无数据</td>
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

    <!-- 确认弹窗 -->
    <div v-if="confirmVisible" class="modal-overlay" @click.self="confirmVisible = false">
      <div class="confirm-box">
        <h3>操作确认</h3>
        <p>{{ confirmMsg }}</p>
        <div class="confirm-actions">
          <button class="cancel-btn" @click="confirmVisible = false">取消</button>
          <button class="ok-btn" @click="confirmAction">确认</button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
/**
 * 用户管理页面（后台）
 *
 * 【架构说明】
 * 本页面不包含任何硬编码数据，所有用户数据均来自后端接口。
 * 状态切换操作调用后端 API，操作失败时向用户显示错误提示。
 *
 * 接口定义见 src/api/admin.js，包含完整的请求参数和响应格式注释。
 */
import { computed, onMounted, ref } from 'vue'
import { showToast } from '../../utils/toast';
import { adminUserListApi, updateUserStatusApi } from '../../api/admin'

// ===== 列表与分页（数据由后端接口返回） =====
const list = ref([])
const loading = ref(false)
const query = ref({ username: '' })
const pageNum = ref(1)
const pageSize = ref(10)
const total = ref(0)
const totalPages = computed(() => Math.ceil(total.value / pageSize.value) || 1)

// ===== 确认弹窗 =====
const confirmVisible = ref(false)
const confirmMsg = ref('')
const confirmAction = ref(() => { })

onMounted(() => loadList())

/**
 * 加载用户列表（服务端分页，支持用户名搜索）
 */
async function loadList() {
  loading.value = true
  try {
    const res = await adminUserListApi({
      pageNum: pageNum.value,
      pageSize: pageSize.value,
      username: query.value.username || undefined
    })
    list.value = res?.list || res?.records || res?.rows || []
    total.value = res?.total ?? 0
  } catch (e) {
    list.value = []
    total.value = 0
    console.error('[AdminUser] 加载失败:', e)
    showToast('用户数据加载失败，请检查后端服务是否正常', 'error')
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
 * 切换用户启用/禁用状态
 * 先弹出确认框，用户确认后调用后端 API。
 * 操作失败时弹窗提示用户，不修改本地状态。
 */
function toggleStatus(item) {
  const action = item.status === 1 ? '禁用' : '启用'
  confirmMsg.value = `确认${action}用户"${item.username}"吗？`

  confirmAction.value = async () => {
    try {
      await updateUserStatusApi({
        userId: item.userId,
        status: item.status === 1 ? 0 : 1
      })
      // 操作成功后才更新本地状态
      item.status = item.status === 1 ? 0 : 1
      confirmVisible.value = false
    } catch (e) {
      console.error('[AdminUser] 状态切换失败:', e)
      showToast(`${action}操作失败，请稍后重试`, 'error')
    }
  }

  confirmVisible.value = true
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

.search-bar {
  display: flex;
  gap: 8px;
}

.search-bar input {
  padding: 10px 14px;
  border: 1px solid #d7e7df;
  border-radius: 8px;
  font-size: 14px;
  outline: none;
  width: 200px;
}

.search-bar input:focus {
  border-color: #2d8a6e;
}

.search-bar button {
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

.status-tag {
  padding: 4px 12px;
  border-radius: 999px;
  font-size: 12px;
  font-weight: 600;
}

.status-tag.active {
  background: #e8f5ef;
  color: #1a5a45;
}

.status-tag.disabled {
  background: #fff2f0;
  color: #ff4d4f;
}

.action-btn {
  padding: 6px 14px;
  border: none;
  border-radius: 6px;
  cursor: pointer;
  font-size: 13px;
}

.action-btn.warn {
  background: #fff7e6;
  color: #fa8c16;
}

.action-btn.warn:hover {
  background: #ffe7ba;
}

.action-btn.success {
  background: #e8f5ef;
  color: #1a5a45;
}

.action-btn.success:hover {
  background: #d4edda;
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

.modal-overlay {
  position: fixed;
  inset: 0;
  background: rgba(0, 0, 0, 0.45);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1000;
}

.confirm-box {
  background: #fff;
  border-radius: 14px;
  padding: 28px;
  width: 90%;
  max-width: 380px;
  text-align: center;
}

.confirm-box h3 {
  margin: 0 0 12px;
  color: #0f5132;
}

.confirm-box p {
  color: #555;
  margin: 0 0 24px;
}

.confirm-actions {
  display: flex;
  gap: 12px;
  justify-content: center;
}

.cancel-btn {
  padding: 10px 24px;
  border: 1px solid #ddd;
  border-radius: 8px;
  background: #fff;
  cursor: pointer;
}

.ok-btn {
  padding: 10px 24px;
  border: none;
  border-radius: 8px;
  background: #1a5a45;
  color: #fff;
  cursor: pointer;
}
</style>
