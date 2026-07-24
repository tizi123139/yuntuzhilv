import axios from 'axios'
import router from '../router'

// baseURL 对应后端统一前缀 /api
const service = axios.create({
  baseURL: '/api',
  timeout: 10000,
  headers: {
    'Content-Type': 'application/json'
  }
})

// 请求拦截：自动携带token
service.interceptors.request.use(config => {
  const token = localStorage.getItem('token')
  if (token) {
    config.headers = config.headers || {}
    config.headers.token = token
  }
  return config
})

// 统一提示函数
function showMessage(msg, type = 'error') {
  const div = document.createElement('div')
  div.className = `global-message global-message-${type}`
  div.textContent = msg
  div.style.cssText = `
    position: fixed; top: 20px; left: 50%; transform: translateX(-50%);
    padding: 12px 24px; border-radius: 8px; font-size: 14px; z-index: 9999;
    box-shadow: 0 4px 12px rgba(0,0,0,0.15); animation: fadeIn 0.3s;
    ${type === 'error' ? 'background:#fff2f0;color:#ff4d4f;border:1px solid #ffccc7' :
      type === 'warning' ? 'background:#fffbe6;color:#faad14;border:1px solid #ffe58f' :
      'background:#f6ffed;color:#52c41a;border:1px solid #b7eb8f'}
  `
  document.body.appendChild(div)
  setTimeout(() => {
    div.style.animation = 'fadeOut 0.3s'
    setTimeout(() => document.body.removeChild(div), 300)
  }, 3000)
}

// 添加全局动画样式
if (!document.getElementById('global-message-style')) {
  const style = document.createElement('style')
  style.id = 'global-message-style'
  style.textContent = `
    @keyframes fadeIn { from { opacity: 0; transform: translate(-50%, -10px); } to { opacity: 1; transform: translate(-50%, 0); } }
    @keyframes fadeOut { from { opacity: 1; } to { opacity: 0; } }
  `
  document.head.appendChild(style)
}

// 响应拦截：统一处理状态码
service.interceptors.response.use(
  res => {
    // 文件流下载：自动触发浏览器下载
    if (res.config.responseType === 'blob') {
      const disposition = res.headers?.['content-disposition'] || ''
      const filename = disposition.includes('filename=')
        ? decodeURIComponent(disposition.split('filename=')[1].replace(/"/g, ''))
        : 'download'
      const url = window.URL.createObjectURL(new Blob([res.data]))
      const a = document.createElement('a')
      a.href = url
      a.download = filename
      a.click()
      window.URL.revokeObjectURL(url)
      return res
    }

    const data = res?.data ?? {}

    // 401：清除token，跳转登录
    if (data.code === 401) {
      localStorage.removeItem('token')
      localStorage.removeItem('role')
      showMessage('登录已过期，请重新登录', 'warning')
      setTimeout(() => router.push('/BLhome'), 1500)
      return Promise.reject(new Error('Unauthorized'))
    }

    // 403：区分角色处理
    if (data.code === 403) {
      const role = localStorage.getItem('role')
      if (role === 'user' || !role) {
        showMessage('无权限访问，请登录管理员账号', 'warning')
        setTimeout(() => router.push('/BLhome'), 1500)
      } else {
        showMessage('账号非管理员，无法访问后台', 'warning')
      }
      return Promise.reject(new Error('Forbidden'))
    }

    // 500：服务器异常
    if (data.code === 500) {
      showMessage(data.message || '服务器异常，请稍后重试', 'error')
      return Promise.reject(new Error('Server Error'))
    }

    // 非200成功码：弹窗提示message
    if (data.code !== 200 && data.code !== undefined) {
      showMessage(data.message || '操作失败', 'error')
      return Promise.reject(new Error(data.message || 'Error'))
    }

    // 成功 code=200：返回data字段，兼容无code字段的直返数据
    return data.data !== undefined ? data.data : data
  },
  err => {
    if (!err.config?.silent) {
      const msg = err.response?.status === 504 ? '请求超时，请检查网络' : '网络请求失败'
      showMessage(msg, 'error')
    }
    return Promise.reject(err)
  }
)

export default service