<template>
  <div class="admin-login">
    <div class="login-box">
      <div class="login-header">
        <h1>管理员登录</h1>
        <p>WayCloud 云途智行</p>
      </div>
      <form @submit.prevent="handleLogin" class="login-form">
        <div class="form-group">
          <input
            type="text"
            v-model="form.username"
            placeholder="管理员账号"
            required
          />
        </div>
        <div class="form-group">
          <input
            :type="showPassword ? 'text' : 'password'"
            v-model="form.password"
            placeholder="密码"
            required
          />
          <span class="eye-icon" @click="showPassword = !showPassword">
            <svg v-if="!showPassword" width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
              <path d="M1 12s4-8 11-8 11 8 11 8-4 8-11 8-11-8-11-8z" />
              <circle cx="12" cy="12" r="3" />
            </svg>
            <svg v-else width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
              <path d="M17.94 17.94A10.07 10.07 0 0 1 12 20c-7 0-11-8-11-8a18.45 18.45 0 0 1 5.06-5.94M9.9 4.24A9.12 9.12 0 0 1 12 4c7 0 11 8 11 8a18.5 18.5 0 0 1-2.16 3.19m-6.72-1.07a3 3 0 1 1-4.24-4.24" />
              <line x1="1" y1="1" x2="23" y2="23" />
            </svg>
          </span>
        </div>
        <button type="submit" class="login-btn" :disabled="loading">
          {{ loading ? '登录中...' : '登录' }}
        </button>
      </form>
      <div class="login-footer">
        <router-link to="/BLhome">返回前台首页</router-link>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { showToast } from '../utils/toast';
import { useRouter } from 'vue-router'
// 引入登录接口
import { loginApi } from '../api/admin'

const router = useRouter()
const loading = ref(false)
const showPassword = ref(false)
const form = ref({
  username: '',
  password: ''
})

async function handleLogin() {
  if (!form.value.username || !form.value.password) {
    showToast('请输入账号和密码', 'warning')
    return
  }
  loading.value = true
  try {
    // 调用后端真实登录接口
    const res = await loginApi(form.value)
    // 响应拦截器已解包，res 直接是 LoginVO：{token, user:{role,userId,...}}
    localStorage.setItem('token', res.token)
    localStorage.setItem('role', res.user?.role || 'admin')
    localStorage.setItem('username', form.value.username)
    if (res.user?.userId) localStorage.setItem('userId', res.user.userId)

    showToast(`管理员登录成功，欢迎 ${form.value.username}`, 'success')
    router.replace('/admin')
  } catch (e) {
    console.error('登录失败', e)
    showToast('账号密码错误或账号已禁用', 'error')
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.admin-login {
  position: relative;
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 20px;
  background: url('/adminLoginBackground.jpg') center/cover no-repeat;
  animation: backgroundZoom 30s ease-in-out infinite;
}

@keyframes backgroundZoom {
  0%,
  100% {
    background-size: 110% 110%;
  }
  50% {
    background-size: 130% 130%;
  }
}

.admin-login::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: linear-gradient(135deg, rgba(45, 138, 110, 0.5) 0%, rgba(26, 90, 69, 0.6) 100%);
}

.login-box {
  position: relative;
  z-index: 10;
  background: rgba(255, 255, 255, 0.85);
  border-radius: 24px;
  border: 3px solid rgba(255, 255, 255, 0.6);
  padding: 56px 48px;
  width: 100%;
  max-width: 420px;
  box-shadow: 0 20px 60px rgba(0, 0, 0, 0.2);
  backdrop-filter: blur(10px);
}

.login-header {
  text-align: center;
  margin-bottom: 36px;
}

.login-header h1 {
  font-size: 28px;
  color: #0f5132;
  margin: 0 0 8px;
  font-weight: 700;
  letter-spacing: 2px;
}

.login-header p {
  color: #6b7d76;
  font-size: 14px;
  margin: 0;
}

.login-form {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.form-group {
  position: relative;
}

.form-group input {
  width: 100%;
  padding: 15px 48px 15px 16px;
  border: 2px solid #d1e5dc;
  border-radius: 12px;
  font-size: 15px;
  outline: none;
  transition: all 0.3s;
  background: rgba(255, 255, 255, 0.7);
}

.form-group input:focus {
  border-color: #51b891;
  box-shadow: 0 0 0 3px rgba(81, 184, 145, 0.15);
}

.form-group input::placeholder {
  color: #9cb5ac;
}

.eye-icon {
  position: absolute;
  right: 16px;
  top: 50%;
  transform: translateY(-50%);
  color: #9cb5ac;
  cursor: pointer;
  transition: color 0.3s;
}

.eye-icon:hover {
  color: #51b891;
}

.login-btn {
  margin-top: 8px;
  padding: 16px;
  background: linear-gradient(135deg, #2d8a6e 0%, #1a5a45 100%);
  color: #fff;
  border: none;
  border-radius: 12px;
  font-size: 16px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s;
  letter-spacing: 2px;
}

.login-btn:hover:not(:disabled) {
  transform: translateY(-2px);
  box-shadow: 0 8px 20px rgba(45, 138, 110, 0.4);
}

.login-btn:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}

.login-footer {
  margin-top: 28px;
  text-align: center;
}

.login-footer a {
  color: #51b891;
  text-decoration: none;
  font-size: 14px;
  transition: color 0.3s;
}

.login-footer a:hover {
  color: #2d8a6e;
}
</style>