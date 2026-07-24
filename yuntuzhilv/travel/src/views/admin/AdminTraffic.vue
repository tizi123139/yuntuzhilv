<template>
  <div class="admin-page">
    <div class="page-header">
      <h2>交通管理</h2>
      <button class="add-btn" @click="formVisible = true">+ 新增交通</button>
    </div>

    <div class="table-wrapper">
      <table class="data-table">
        <thead>
          <tr>
            <th>出发城市</th>
            <th>目的城市</th>
            <th>交通方式</th>
            <th>费用</th>
            <th>时长</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="(item, idx) in list" :key="idx">
            <td>{{ item.fromCity }}</td>
            <td>{{ item.toCity }}</td>
            <td>{{ item.type }}</td>
            <td>¥{{ item.price }}</td>
            <td>{{ item.duration }}</td>
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

    <!-- 新增弹窗 -->
    <div v-if="formVisible" class="modal-overlay" @click.self="formVisible = false">
      <div class="form-modal">
        <h3>新增交通线路</h3>
        <form @submit.prevent="submitForm" class="traffic-form">
          <div class="form-row">
            <label>出发城市</label>
            <input v-model="form.fromCity" required placeholder="如：北京" />
          </div>
          <div class="form-row">
            <label>目的城市</label>
            <input v-model="form.toCity" required placeholder="如：上海" />
          </div>
          <div class="form-row">
            <label>交通方式</label>
            <select v-model="form.type" required>
              <option value="">请选择</option>
              <option value="高铁">高铁</option>
              <option value="飞机">飞机</option>
              <option value="大巴">大巴</option>
              <option value="轮船">轮船</option>
            </select>
          </div>
          <div class="form-row">
            <label>费用（元）</label>
            <input v-model.number="form.price" type="number" min="0" required />
          </div>
          <div class="form-row">
            <label>时长</label>
            <input v-model="form.duration" required placeholder="如：4h 30m" />
          </div>
          <div class="form-actions">
            <button type="button" class="cancel-btn" @click="formVisible = false">取消</button>
            <button type="submit" class="submit-btn">保存</button>
          </div>
        </form>
      </div>
    </div>
  </div>
</template>

<script setup>
/**
 * 交通管理页面（后台）
 *
 * 【架构说明】
 * 本页面不包含任何硬编码数据，所有交通线路数据均来自后端接口。
 * 新增操作调用后端 API，操作失败时向用户显示错误提示。
 *
 * 接口定义见 src/api/admin.js，包含完整的请求参数和响应格式注释。
 */
import { computed, onMounted, ref } from 'vue'
import { showToast } from '../../utils/toast';
import { adminTrafficListApi, addTrafficApi } from '../../api/admin'

// ===== 列表与分页（数据由后端接口返回） =====
const list = ref([])
const loading = ref(false)
const pageNum = ref(1)
const pageSize = ref(10)
const total = ref(0)
const totalPages = computed(() => Math.ceil(total.value / pageSize.value) || 1)

// ===== 新增表单 =====
const formVisible = ref(false)
const form = ref({
  fromCity: '', toCity: '', type: '', price: null, duration: ''
})

onMounted(() => loadList())

/**
 * 加载交通线路列表（服务端分页）
 */
async function loadList() {
  loading.value = true
  try {
    const res = await adminTrafficListApi({ pageNum: pageNum.value, pageSize: pageSize.value })
    list.value = res?.list || res?.records || res?.rows || []
    total.value = res?.total ?? 0
  } catch (e) {
    list.value = []
    total.value = 0
    console.error('[AdminTraffic] 加载失败:', e)
    showToast('交通数据加载失败，请检查后端服务是否正常', 'error')
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
 * 提交新增交通线路
 * 操作成功后关闭弹窗并刷新列表；失败时弹窗提示用户。
 */
async function submitForm() {
  try {
    await addTrafficApi(form.value)
    formVisible.value = false
    form.value = { fromCity: '', toCity: '', type: '', price: null, duration: '' }
    loadList()
  } catch (e) {
    console.error('[AdminTraffic] 新增失败:', e)
    showToast('新增失败，请稍后重试', 'error')
  }
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

.add-btn {
  padding: 10px 20px;
  background: #1a5a45;
  color: #fff;
  border: none;
  border-radius: 8px;
  cursor: pointer;
  font-size: 14px;
}

.add-btn:hover {
  background: #0f5132;
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

.form-modal {
  background: #fff;
  border-radius: 16px;
  padding: 32px;
  width: 92%;
  max-width: 480px;
}

.form-modal h3 {
  margin: 0 0 20px;
  color: #0f5132;
}

.traffic-form {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.form-row {
  display: flex;
  flex-direction: column;
  gap: 6px;
}

.form-row label {
  font-size: 14px;
  color: #555;
  font-weight: 500;
}

.form-row input,
.form-row select {
  padding: 10px 12px;
  border: 1px solid #d7e7df;
  border-radius: 8px;
  font-size: 14px;
  outline: none;
}

.form-row input:focus,
.form-row select:focus {
  border-color: #2d8a6e;
}

.form-actions {
  display: flex;
  gap: 12px;
  justify-content: flex-end;
  margin-top: 8px;
}

.cancel-btn {
  padding: 10px 24px;
  border: 1px solid #ddd;
  border-radius: 8px;
  background: #fff;
  cursor: pointer;
}

.submit-btn {
  padding: 10px 24px;
  border: none;
  border-radius: 8px;
  background: #1a5a45;
  color: #fff;
  cursor: pointer;
}
</style>
