/**
 * 全局 Toast 通知系统
 * 用于替代原生 alert()，提供从顶部滑入的通知条
 *
 * 使用方式：
 *   import { showToast } from '@/utils/toast'
 *   showToast('操作成功', 'success')
 *   showToast('操作失败', 'error')
 *   showToast('提示信息', 'info')
 *   showToast('警告内容', 'warning')
 */
import { ref } from 'vue'

// 全局响应式 toast 列表，供 ToastContainer 组件消费
export const toasts = ref([])

let nextId = 0

/**
 * 显示一条 Toast 通知
 * @param {string} message - 通知内容
 * @param {'success'|'error'|'info'|'warning'} type - 通知类型
 * @param {number} duration - 自动关闭时间（毫秒），默认 3000
 */
export function showToast(message, type = 'info', duration = 3000) {
  const id = nextId++
  toasts.value.push({ id, message, type })
  setTimeout(() => {
    toasts.value = toasts.value.filter(t => t.id !== id)
  }, duration)
}

// 快捷方法
export const toastSuccess = (msg, duration) => showToast(msg, 'success', duration)
export const toastError = (msg, duration) => showToast(msg, 'error', duration)
export const toastInfo = (msg, duration) => showToast(msg, 'info', duration)
export const toastWarning = (msg, duration) => showToast(msg, 'warning', duration)
