<template>
  <div class="admin-page">
    <div class="page-header">
      <h2>景点管理</h2>
      <button class="add-btn" @click="openForm(null)">+ 新增景点</button>
    </div>

    <div class="table-wrapper">
      <table class="data-table">
        <thead>
          <tr>
            <th>名称</th>
            <th>类型</th>
            <th>票价</th>
            <th>城市</th>
            <th>地址</th>
            <th>操作</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="item in list" :key="item.attractionId">
            <td>{{ item.name }}</td>
            <td>{{ item.type }}</td>
            <td>¥{{ item.price ?? '-' }}</td>
            <td>{{ item.city || '-' }}</td>
            <td>{{ item.address || '-' }}</td>
            <td class="action-cell">
              <button class="action-btn edit" @click="openForm(item)">编辑</button>
              <button class="action-btn delete" @click="handleDelete(item)">删除</button>
            </td>
          </tr>
          <tr v-if="!list.length && !loading">
            <td colspan="6" class="empty-row">暂无数据</td>
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

    <!-- 新增/编辑弹窗 -->
    <div v-if="formVisible" class="modal-overlay" @click.self="formVisible = false">
      <div class="form-modal">
        <h3>{{ isEdit ? '编辑景点' : '新增景点' }}</h3>
        <form @submit.prevent="submitForm" class="attr-form">
          <div class="form-row">
            <label>名称</label>
            <input v-model="form.name" required />
          </div>
          <div class="form-row">
            <label>城市</label>
            <input v-model="form.city" placeholder="如 长沙" />
          </div>
          <div class="form-row">
            <label>类型</label>
            <select v-model="form.type">
              <option value="">请选择</option>
              <option value="历史古迹">历史古迹</option>
              <option value="自然风光">自然风光</option>
              <option value="美食街区">美食街区</option>
              <option value="主题乐园">主题乐园</option>
              <option value="博物馆">博物馆</option>
            </select>
          </div>
          <div class="form-row">
            <label>票价</label>
            <input v-model.number="form.price" type="number" min="0" placeholder="0为免费" />
          </div>
          <div class="form-row">
            <label>开放时间</label>
            <input v-model="form.openTime" placeholder="如 08:00-18:00" />
          </div>
          <div class="form-row">
            <label>地址</label>
            <input v-model="form.address" />
          </div>
          <div class="form-row">
            <label>介绍</label>
            <textarea v-model="form.description" rows="3"></textarea>
          </div>
          <div class="form-row two-col">
            <div>
              <label>经度</label>
              <input v-model.number="form.longitude" type="number" step="0.000001" />
            </div>
            <div>
              <label>纬度</label>
              <input v-model.number="form.latitude" type="number" step="0.000001" />
            </div>
          </div>
          <div class="form-actions">
            <button type="button" class="cancel-btn" @click="formVisible = false">取消</button>
            <button type="submit" class="submit-btn">保存</button>
          </div>
        </form>
      </div>
    </div>

    <!-- 删除确认弹窗 -->
    <div v-if="confirmVisible" class="modal-overlay" @click.self="confirmVisible = false">
      <div class="confirm-box">
        <h3>删除确认</h3>
        <p>确认删除景点"{{ deleteTarget?.name }}"吗？此操作不可恢复。</p>
        <div class="confirm-actions">
          <button class="cancel-btn" @click="confirmVisible = false">取消</button>
          <button class="ok-btn danger" @click="confirmDelete">确认删除</button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
/**
 * 景点管理页面（后台）
 *
 * 【架构说明】
 * 本页面不包含任何硬编码数据，所有景点数据均来自后端接口。
 * 增删改操作均调用后端 API，操作失败时向用户显示错误提示。
 *
 * 接口定义见 src/api/admin.js，包含完整的请求参数和响应格式注释。
 */
import { computed, onMounted, ref } from 'vue'
import { showToast } from '../../utils/toast';
import { adminAttractionListApi, addAttractionApi, updateAttractionApi, deleteAttractionApi } from '../../api/admin'

// ===== 列表与分页（数据由后端接口返回） =====
const list = ref([])         // 当前页的景点列表
const loading = ref(false)   // 是否正在加载
const pageNum = ref(1)       // 当前页码
const pageSize = ref(10)     // 每页条数
const total = ref(0)         // 总条数（后端返回）
const totalPages = computed(() => Math.ceil(total.value / pageSize.value) || 1)

// ===== 新增/编辑表单 =====
const formVisible = ref(false)
const isEdit = ref(false)
const form = ref({
  attractionId: null,
  name: '',
  city: '',
  type: '',
  price: null,
  openTime: '',
  address: '',
  description: '',
  longitude: null,
  latitude: null
})

// ===== 删除确认弹窗 =====
const confirmVisible = ref(false)
const deleteTarget = ref(null)

onMounted(() => loadList())

/**
 * 加载景点列表（服务端分页）
 */
async function loadList() {
  loading.value = true
  try {
    const res = await adminAttractionListApi({ pageNum: pageNum.value, pageSize: pageSize.value })
    list.value = res?.list || res?.records || res?.rows || []
    total.value = res?.total ?? 0
  } catch (e) {
    list.value = []
    total.value = 0
    console.error('[AdminAttraction] 加载失败:', e)
    showToast('景点数据加载失败，请检查后端服务是否正常', 'error')
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
 * 打开新增/编辑表单
 */
function openForm(item) {
  if (item) {
    isEdit.value = true
    form.value = { ...item }
  } else {
    isEdit.value = false
    form.value = { attractionId: null, name: '', city: '', type: '', price: null, openTime: '', address: '', description: '', longitude: null, latitude: null }
  }
  formVisible.value = true
}

/**
 * 提交表单（新增或编辑）
 * 操作成功后关闭弹窗并刷新列表；失败时弹窗提示用户。
 */
async function submitForm() {
  try {
    if (isEdit.value) {
      await updateAttractionApi(form.value)
    } else {
      await addAttractionApi(form.value)
    }
    formVisible.value = false
    loadList()
  } catch (e) {
    console.error('[AdminAttraction] 保存失败:', e)
    showToast(isEdit.value ? '编辑失败，请稍后重试' : '新增失败，请稍后重试', 'error')
  }
}

/**
 * 打开删除确认弹窗
 */
function handleDelete(item) {
  deleteTarget.value = item
  confirmVisible.value = true
}

/**
 * 确认删除
 * 删除成功后关闭弹窗并刷新列表；失败时弹窗提示用户。
 */
async function confirmDelete() {
  try {
    await deleteAttractionApi({ id: deleteTarget.value.attractionId })
    confirmVisible.value = false
    loadList()
  } catch (e) {
    console.error('[AdminAttraction] 删除失败:', e)
    showToast('删除失败，请稍后重试', 'error')
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

.action-cell {
  display: flex;
  gap: 8px;
}

.action-btn {
  padding: 6px 14px;
  border: none;
  border-radius: 6px;
  cursor: pointer;
  font-size: 13px;
}

.action-btn.edit {
  background: #e8f5ef;
  color: #1a5a45;
}

.action-btn.edit:hover {
  background: #d4edda;
}

.action-btn.delete {
  background: #fff2f0;
  color: #ff4d4f;
}

.action-btn.delete:hover {
  background: #ffccc7;
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
  max-width: 520px;
  max-height: 85vh;
  overflow-y: auto;
}

.form-modal h3 {
  margin: 0 0 20px;
  color: #0f5132;
}

.attr-form {
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
.form-row select,
.form-row textarea {
  padding: 10px 12px;
  border: 1px solid #d7e7df;
  border-radius: 8px;
  font-size: 14px;
  outline: none;
}

.form-row input:focus,
.form-row select:focus,
.form-row textarea:focus {
  border-color: #2d8a6e;
}

.form-row.two-col {
  flex-direction: row;
  gap: 16px;
}

.form-row.two-col>div {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 6px;
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

.ok-btn.danger {
  background: #ff4d4f;
  color: #fff;
}

.ok-btn.danger:hover {
  background: #ff7875;
}
</style>
