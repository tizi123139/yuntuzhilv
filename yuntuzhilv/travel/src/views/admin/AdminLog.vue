<template>
  <div class="admin-page">
    <div class="page-header">
      <h2>操作日志</h2>
      <div class="filter-bar">
        <select v-model="query.operatorType" @change="loadList">
          <option value="">全部类型</option>
          <option value="add">新增</option>
          <option value="update">编辑</option>
          <option value="delete">删除</option>
        </select>
      </div>
    </div>

    <div class="table-wrapper">
      <table class="data-table">
        <thead>
          <tr>
            <th>日志ID</th>
            <th>操作类型</th>
            <th>操作内容</th>
            <th>操作人</th>
            <th>操作时间</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="item in list" :key="item.logId">
            <td>{{ item.logId }}</td>
            <td>
              <span :class="['type-tag', item.operatorType]">
                {{ typeLabel(item.operatorType) }}
              </span>
            </td>
            <td>{{ item.content || item.description || '-' }}</td>
            <td>{{ item.operator || '-' }}</td>
            <td>{{ item.createTime || item.operateTime || '-' }}</td>
          </tr>
          <tr v-if="!list.length && !loading">
            <td colspan="5" class="empty-row">暂无日志记录</td>
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
  </div>
</template>

<script setup>
/**
 * 操作日志页面（后台）
 *
 * 【架构说明】
 * 本页面不包含任何硬编码数据，日志数据均来自后端接口。
 * 支持按操作类型筛选和服务端分页。
 *
 * 接口定义见 src/api/admin.js，包含完整的请求参数和响应格式注释。
 */
import { computed, onMounted, ref } from 'vue'
import { showToast } from '../../utils/toast';
import { logListApi } from '../../api/admin'

// ===== 列表与分页（数据由后端接口返回） =====
const list = ref([])
const loading = ref(false)
const query = ref({ operatorType: '' })
const pageNum = ref(1)
const pageSize = ref(10)
const total = ref(0)
const totalPages = computed(() => Math.ceil(total.value / pageSize.value) || 1)

/**
 * 操作类型标签映射
 */
function typeLabel(type) {
  const map = { add: '新增', update: '编辑', delete: '删除' }
  return map[type] || type || '-'
}

onMounted(() => loadList())

/**
 * 加载日志列表（服务端分页，支持操作类型筛选）
 */
async function loadList() {
  loading.value = true
  try {
    const res = await logListApi({
      pageNum: pageNum.value,
      pageSize: pageSize.value,
      operatorType: query.value.operatorType || undefined
    })
    list.value = res?.list || res?.records || res?.rows || []
    total.value = res?.total ?? 0
  } catch (e) {
    list.value = []
    total.value = 0
    console.error('[AdminLog] 加载失败:', e)
    showToast('日志数据加载失败，请检查后端服务是否正常', 'error')
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
}

.page-header h2 {
  margin: 0;
  color: #0f5132;
  font-size: 22px;
}

.filter-bar select {
  padding: 10px 14px;
  border: 1px solid #d7e7df;
  border-radius: 8px;
  font-size: 14px;
  outline: none;
}

.filter-bar select:focus {
  border-color: #2d8a6e;
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

.type-tag {
  padding: 4px 12px;
  border-radius: 999px;
  font-size: 12px;
  font-weight: 600;
}

.type-tag.add {
  background: #e8f5ef;
  color: #1a5a45;
}

.type-tag.update {
  background: #e6f7ff;
  color: #1890ff;
}

.type-tag.delete {
  background: #fff2f0;
  color: #ff4d4f;
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
</style>
