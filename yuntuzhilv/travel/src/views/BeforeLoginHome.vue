<template>
  <div class="home">
    <nav class="navbar">
      <div class="nav-container">
        <div class="logo">WayCloud</div>
        <div class="nav-actions">
          <router-link to="/admin-login" class="nav-btn admin-entry">
            <span class="btn-label">管理员登录</span>
            <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"
              stroke-linecap="round" stroke-linejoin="round">
              <path d="M2 10l10-5 10 5-10 5z" />
              <path d="M6 12v3c0 2 3 3 6 3s6-1 6-3v-3" />
              <path d="M22 10v6" />
            </svg>
          </router-link>
          <button class="nav-btn user-entry" @click="showAuthModal = true">
            <span class="btn-label">用户登录</span>
            <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
              <path d="M16 21v-2a4 4 0 0 0-4-4H5a4 4 0 0 0-4 4v2" />
              <circle cx="8.5" cy="7" r="4" />
              <path d="M20 8v6M23 11h-6" />
            </svg>
          </button>

        </div>
      </div>
    </nav>

    <div class="hero-section">
      <div class="hero-content">
        <h1 class="welcome-text">欢迎使用</h1>
        <div class="main-title-row">
          <span class="brand-name">云途智行</span>
          <span class="subtitle-text">——AI智能行程规划平台</span>
        </div>
        <p class="subtitle">云端智绘行程，轻赴万里风光</p>
      </div>

      <div class="floating-images">
        <div class="image-card card-1" @click="openOfficialWebsite('https://yuyue2026.tamgw.beijing.gov.cn/web/index.html#/index')">
          <img
            src="https://coresg-normal.trae.ai/api/ide/v1/text_to_image?prompt=Tiananmen%20Beijing%2C%20beautiful%20architecture%2C%20high%20quality%20photo&image_size=square_hd"
            alt="天安门" />
          <span class="place-name">天安门</span>
          <button class="go-btn" @click.stop="goToItinerary('北京')">去这里</button>
        </div>
        <div class="image-card card-2" @click="openOfficialWebsite('http://www.cnhhl.com/')">
          <img
            src="https://coresg-normal.trae.ai/api/ide/v1/text_to_image?prompt=Yellow%20Crane%20Tower%2C%20Wuhan%2C%20beautiful%20architecture%2C%20high%20quality%20photo&image_size=square_hd"
            alt="黄鹤楼" />
          <span class="place-name">黄鹤楼</span>
          <button class="go-btn" @click.stop="goToItinerary('武汉')">去这里</button>
        </div>
        <div class="image-card card-3" @click="openOfficialWebsite('https://www.orientalpearltower.com/#/')">
          <img
            src="https://coresg-normal.trae.ai/api/ide/v1/text_to_image?prompt=Oriental%20Pearl%20Tower%2C%20Shanghai%2C%20beautiful%20architecture%2C%20high%20quality%20photo&image_size=square_hd"
            alt="东方明珠塔" />
          <span class="place-name">东方明珠塔</span>
          <button class="go-btn" @click.stop="goToItinerary('上海')">去这里</button>
        </div>
      </div>
    </div>

    <!-- 登录/注册弹窗 -->
    <div v-if="showAuthModal" class="modal-overlay" @click.self="showAuthModal = false">
      <div class="modal-content">
        <button class="modal-close" @click="showAuthModal = false">&times;</button>

        <div class="tabs">
          <button :class="['tab', { active: activeTab === 'login' }]" @click="activeTab = 'login'">登录</button>
          <button :class="['tab', { active: activeTab === 'register' }]" @click="activeTab = 'register'">注册</button>
        </div>

        <!-- 登录表单 -->
        <form v-if="activeTab === 'login'" class="auth-form" @submit.prevent="handleLogin">
          <div class="form-group">
            <div class="input-wrapper">
              <span class="input-icon">
                <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                  <path d="M16 21v-2a4 4 0 0 0-4-4H5a4 4 0 0 0-4 4v2" />
                  <circle cx="8.5" cy="7" r="4" />
                  <path d="M20 8v6M23 11h-6" />
                </svg>
              </span>
              <input type="text" v-model="loginForm.username" placeholder="账号/手机号/邮箱" required />
              <span v-if="loginForm.username" class="clear-icon" @click="loginForm.username = ''">
                &times;
              </span>
            </div>
          </div>
          <div class="form-group">
            <div class="input-wrapper">
              <span class="input-icon">
                <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                  <rect x="3" y="11" width="18" height="11" rx="2" ry="2" />
                  <path d="M7 11V7a5 5 0 0 1 10 0v4" />
                </svg>
              </span>
              <input :type="showPassword ? 'text' : 'password'" v-model="loginForm.password" placeholder="密码"
                required />
              <span class="eye-icon" @click="showPassword = !showPassword">
                <svg v-if="!showPassword" width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor"
                  stroke-width="2">
                  <path d="M1 12s4-8 11-8 11 8 11 8-4 8-11 8-11-8-11-8z" />
                  <circle cx="12" cy="12" r="3" />
                </svg>
                <svg v-else width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor"
                  stroke-width="2">
                  <path
                    d="M17.94 17.94A10.07 10.07 0 0 1 12 20c-7 0-11-8-11-8a18.45 18.45 0 0 1 5.06-5.94M9.9 4.24A9.12 9.12 0 0 1 12 4c7 0 11 8 11 8a18.5 18.5 0 0 1-2.16 3.19m-6.72-1.07a3 3 0 1 1-4.24-4.24" />
                  <line x1="1" y1="1" x2="23" y2="23" />
                </svg>
              </span>
            </div>
          </div>
          <div class="forgot-password">
            <a href="#">忘记密码？</a>
          </div>
          <button type="submit" class="submit-btn">登录</button>

          <div class="other-login">
            <p>其他登录方式</p>
            <div class="social-icons">
              <button class="social-icon wechat" @click="handleWechatLogin" type="button">
                <svg width="24" height="24" viewBox="0 0 24 24" fill="currentColor">
                  <path
                    d="M8.691 2.188C3.891 2.188 0 5.476 0 9.53c0 2.212 1.17 4.203 3.002 5.55a.59.59 0 0 1 .213.665l-.39 1.48c-.019.07-.048.141-.048.213 0 .163.13.295.29.295a.326.326 0 0 0 .167-.054l1.903-1.114a.864.864 0 0 1 .717-.098 10.16 10.16 0 0 0 2.837.403c.276 0 .543-.027.811-.05-.857-2.578.157-4.972 1.932-6.446 1.703-1.415 3.882-1.98 5.853-1.838-.576-3.583-4.196-6.348-8.596-6.348zM5.785 5.991c.642 0 1.162.529 1.162 1.18a1.17 1.17 0 0 1-1.162 1.178A1.17 1.17 0 0 1 4.623 7.17c0-.651.52-1.18 1.162-1.18zm5.813 0c.642 0 1.162.529 1.162 1.18a1.17 1.17 0 0 1-1.162 1.178 1.17 1.17 0 0 1-1.162-1.178c0-.651.52-1.18 1.162-1.18zm5.34 2.867c-1.797-.052-3.746.512-5.28 1.786-1.72 1.428-2.687 3.72-1.78 6.22.942 2.453 3.666 4.229 6.884 4.229.826 0 1.622-.12 2.361-.336a.722.722 0 0 1 .598.082l1.584.926a.272.272 0 0 0 .14.047c.134 0 .241-.111.241-.247 0-.06-.023-.12-.038-.177l-.327-1.233a.49.49 0 0 1 .176-.553C23.183 18.528 24 16.894 24 15.086c0-3.245-3.098-6.176-7.062-6.228zm-3.087 2.94c.535 0 .969.44.969.983a.976.976 0 0 1-.969.983.976.976 0 0 1-.969-.983c0-.542.434-.983.97-.983zm5.812 0c.535 0 .969.44.969.983a.976.976 0 0 1-.969.983.976.976 0 0 1-.969-.983c0-.542.434-.983.97-.983z" />
                </svg>
              </button>
              <button class="social-icon qq" @click="handleQQLogin" type="button">
                <svg width="24" height="24" viewBox="0 0 24 24" fill="currentColor">
                  <path
                    d="M12.003 2c-2.265 0-6.29 1.364-6.29 7.325v1.195S3.55 14.96 3.55 17.474c0 .665.17 1.025.28 1.025.114 0 .903-.36.903-.36s-.085.737-.085 1.193c0 .457.424.92.424.92s-1.185.34-1.185 1.273c0 .935 5.153.67 8.116.67 2.96 0 8.113.265 8.113-.67 0-.934-1.185-1.274-1.185-1.274s.426-.463.426-.92c0-.456-.084-1.193-.084-1.193s.789.36.903.36c.112 0 .28-.36.28-1.025 0-2.516-2.166-6.953-2.166-6.953V9.325C18.29 3.364 14.268 2 12.003 2z" />
                </svg>
              </button>
              <button class="social-icon email" @click="handleEmailLoginClick" type="button">
                <svg width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                  <path d="M4 4h16c1.1 0 2 .9 2 2v12c0 1.1-.9 2-2 2H4c-1.1 0-2-.9-2-2V6c0-1.1.9-2 2-2z" />
                  <polyline points="22,6 12,13 2,6" />
                </svg>
              </button>
            </div>
          </div>
        </form>

        <!-- 注册表单 -->
        <form v-if="activeTab === 'register'" class="auth-form" @submit.prevent="handleRegister">
          <div class="form-group">
            <div class="input-wrapper">
              <span class="input-icon">
                <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                  <path d="M16 21v-2a4 4 0 0 0-4-4H5a4 4 0 0 0-4 4v2" />
                  <circle cx="8.5" cy="7" r="4" />
                  <path d="M20 8v6M23 11h-6" />
                </svg>
              </span>
              <input type="text" v-model="registerForm.username" placeholder="用户名" required />
            </div>
          </div>
          <div class="form-group">
            <div class="input-wrapper">
              <span class="input-icon">
                <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                  <path d="M4 4h16c1.1 0 2 .9 2 2v12c0 1.1-.9 2-2 2H4c-1.1 0-2-.9-2-2V6c0-1.1.9-2 2-2z" />
                  <polyline points="22,6 12,13 2,6" />
                </svg>
              </span>
              <input type="email" v-model="registerForm.email" placeholder="邮箱" required />
            </div>
          </div>
          <div class="form-group">
            <div class="input-wrapper phone-wrapper">
              <span class="input-icon">
                <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                  <rect x="5" y="2" width="14" height="20" rx="2" ry="2" />
                  <line x1="12" y1="18" x2="12.01" y2="18" />
                </svg>
              </span>
              <span class="country-code">+86</span>
              <input type="tel" v-model="registerForm.phone" placeholder="手机号" required />
            </div>
          </div>
          <div class="form-group">
            <div class="input-wrapper code-wrapper">
              <span class="input-icon">
                <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                  <rect x="3" y="11" width="18" height="11" rx="2" ry="2" />
                  <path d="M7 11V7a5 5 0 0 1 10 0v4" />
                </svg>
              </span>
              <input type="text" v-model="registerForm.code" placeholder="验证码" required maxlength="6" />
              <button type="button" class="get-code-btn" @click="getCode" :disabled="codeCountdown > 0">
                {{ codeCountdown > 0 ? `${codeCountdown}s` : '获取验证码' }}
              </button>
            </div>
          </div>
          <div class="form-group">
            <div class="input-wrapper">
              <span class="input-icon">
                <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                  <rect x="3" y="11" width="18" height="11" rx="2" ry="2" />
                  <path d="M7 11V7a5 5 0 0 1 10 0v4" />
                </svg>
              </span>
              <input :type="showRegPassword ? 'text' : 'password'" v-model="registerForm.password" placeholder="密码"
                required />
              <span class="eye-icon" @click="showRegPassword = !showRegPassword">
                <svg v-if="!showRegPassword" width="20" height="20" viewBox="0 0 24 24" fill="none"
                  stroke="currentColor" stroke-width="2">
                  <path d="M1 12s4-8 11-8 11 8 11 8-4 8-11 8-11-8-11-8z" />
                  <circle cx="12" cy="12" r="3" />
                </svg>
                <svg v-else width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor"
                  stroke-width="2">
                  <path
                    d="M17.94 17.94A10.07 10.07 0 0 1 12 20c-7 0-11-8-11-8a18.45 18.45 0 0 1 5.06-5.94M9.9 4.24A9.12 9.12 0 0 1 12 4c7 0 11 8 11 8a18.5 18.5 0 0 1-2.16 3.19m-6.72-1.07a3 3 0 1 1-4.24-4.24" />
                  <line x1="1" y1="1" x2="23" y2="23" />
                </svg>
              </span>
            </div>
          </div>
          <div class="form-group">
            <div class="input-wrapper">
              <span class="input-icon">
                <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                  <rect x="3" y="11" width="18" height="11" rx="2" ry="2" />
                  <path d="M7 11V7a5 5 0 0 1 10 0v4" />
                </svg>
              </span>
              <input :type="showRegConfirmPassword ? 'text' : 'password'" v-model="registerForm.confirmPassword"
                placeholder="确认密码" required />
              <span class="eye-icon" @click="showRegConfirmPassword = !showRegConfirmPassword">
                <svg v-if="!showRegConfirmPassword" width="20" height="20" viewBox="0 0 24 24" fill="none"
                  stroke="currentColor" stroke-width="2">
                  <path d="M1 12s4-8 11-8 11 8 11 8-4 8-11 8-11-8-11-8z" />
                  <circle cx="12" cy="12" r="3" />
                </svg>
                <svg v-else width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor"
                  stroke-width="2">
                  <path
                    d="M17.94 17.94A10.07 10.07 0 0 1 12 20c-7 0-11-8-11-8a18.45 18.45 0 0 1 5.06-5.94M9.9 4.24A9.12 9.12 0 0 1 12 4c7 0 11 8 11 8a18.5 18.5 0 0 1-2.16 3.19m-6.72-1.07a3 3 0 1 1-4.24-4.24" />
                  <line x1="1" y1="1" x2="23" y2="23" />
                </svg>
              </span>
            </div>
          </div>
          <div class="agreement">
            <label class="checkbox-label">
              <input type="checkbox" v-model="registerForm.agree" required />
              <span class="checkmark"></span>
              <span class="agreement-text">
                我已阅读并同意
                <a href="#">用户协议</a>
                和
                <a href="#">隐私政策</a>
              </span>
            </label>
          </div>
          <button type="submit" class="submit-btn">注册</button>
        </form>
      </div>
    </div>

    <!-- 邮箱验证码登录弹窗 -->
    <div v-if="showEmailDialog" class="modal-overlay" @click.self="showEmailDialog = false">
      <div class="modal-content email-modal">
        <button class="modal-close" @click="showEmailDialog = false">&times;</button>
        <h3 class="email-modal-title">邮箱验证码登录</h3>

        <!-- 第一步：输入邮箱，发送验证码 -->
        <div v-if="!emailCodeSent" class="auth-form">
          <div class="form-group">
            <div class="input-wrapper">
              <span class="input-icon">
                <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                  <path d="M4 4h16c1.1 0 2 .9 2 2v12c0 1.1-.9 2-2 2H4c-1.1 0-2-.9-2-2V6c0-1.1.9-2 2-2z" />
                  <polyline points="22,6 12,13 2,6" />
                </svg>
              </span>
              <input type="email" v-model="emailLoginForm.email" placeholder="请输入邮箱地址" />
            </div>
          </div>
          <button type="button" class="submit-btn" :disabled="emailLoading" @click="handleSendEmailCode">
            {{ emailLoading ? '发送中...' : '发送验证码' }}
          </button>
        </div>

        <!-- 第二步：输入验证码，完成登录 -->
        <div v-else class="auth-form">
          <p class="email-sent-tip">验证码已发送至 <strong>{{ emailLoginForm.email }}</strong></p>
          <div class="form-group">
            <div class="input-wrapper">
              <span class="input-icon">
                <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                  <rect x="3" y="11" width="18" height="11" rx="2" ry="2" />
                  <path d="M7 11V7a5 5 0 0 1 10 0v4" />
                </svg>
              </span>
              <input type="text" v-model="emailLoginForm.code" placeholder="请输入 6 位验证码" maxlength="6" />
            </div>
          </div>
          <button type="button" class="submit-btn" :disabled="emailLoading" @click="handleEmailLogin">
            {{ emailLoading ? '登录中...' : '登录' }}
          </button>
          <button type="button" class="resend-btn" :disabled="emailCountdown > 0" @click="handleSendEmailCode">
            {{ emailCountdown > 0 ? `${emailCountdown}s 后可重发` : '重新发送' }}
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import { showToast } from '../utils/toast';
import { useRouter } from 'vue-router';
import {
  loginApi,
  registerApi,
  sendCodeApi,
  /* 社交登录接口 */
  getWechatAuthUrlApi,
  wechatLoginApi,
  getQQAuthUrlApi,
  qqLoginApi,
  sendEmailCodeApi,
  emailLoginApi
} from '../api/user';

const router = useRouter();
const showAuthModal = ref(false);
const activeTab = ref('login');
const showPassword = ref(false);
const showRegPassword = ref(false);
const showRegConfirmPassword = ref(false);
const codeCountdown = ref(0);

// 存储登录后的目标页面信息
const pendingRoute = ref(null);

const loginForm = ref({
  username: '',
  password: ''
});

const registerForm = ref({
  username: '',
  email: '',
  phone: '',
  code: '',
  password: '',
  confirmPassword: '',
  agree: false
});

/* ========================================================
 * 社交登录状态
 * ======================================================== */

/** 邮箱验证码登录弹窗是否显示 */
const showEmailDialog = ref(false);

/** 邮箱登录表单：email + 验证码 */
const emailLoginForm = ref({ email: '', code: '' });

/** 邮箱验证码是否已发送（控制弹窗内两步切换） */
const emailCodeSent = ref(false);

/** 邮箱验证码重发倒计时 */
const emailCountdown = ref(0);

/** 社交登录通用加载态 */
const emailLoading = ref(false);

/* ========================================================
 * 打开景点官网（新标签页）
 * ======================================================== */
const openOfficialWebsite = (url) => {
  window.open(url, '_blank', 'noopener,noreferrer')
}

/* ========================================================
 * 跳转到行程定制页面并预填目的地城市
 * 如果未登录，先弹出登录弹窗，登录成功后再跳转
 * ======================================================== */
const goToItinerary = (city) => {
  const token = localStorage.getItem('token')
  if (token) {
    // 已登录，直接跳转
    router.push({ path: '/create-itinerary', query: { toCity: city } })
  } else {
    // 未登录，存储目标页面并弹出登录弹窗
    pendingRoute.value = { path: '/create-itinerary', query: { toCity: city } }
    showAuthModal.value = true
    activeTab.value = 'login'
  }
}

/* ========================================================
 * 页面加载时检查 URL 是否携带 OAuth 回调 code
 * 微信/QQ 扫码后，后端回调会在 URL 上附带 ?code=xxx
 * 前端在此自动用 code 换取 token，完成登录
 * ======================================================== */
onMounted(async () => {
  const params = new URLSearchParams(window.location.search);
  const code = params.get('code');
  const source = params.get('source'); // 'wechat' | 'qq'

  if (!code || !source) return;

  try {
    let res;
    if (source === 'wechat') {
      res = await wechatLoginApi(code);
    } else if (source === 'qq') {
      res = await qqLoginApi(code);
    }
    const data = res?.data || res;
    if (data?.token) {
      localStorage.setItem('token', data.token);
      localStorage.setItem('role', data.role || 'user');
      localStorage.setItem('username', data.username || '');
      if (data.userId) localStorage.setItem('userId', String(data.userId));
      // 清除 URL 上的 code / source 参数
      window.history.replaceState({}, '', window.location.pathname);
      showToast('登录成功！', 'success');
      
      // 如果有登录前的目标页面，跳转到目标页面，否则跳转到首页
      if (pendingRoute.value) {
        router.replace(pendingRoute.value)
        pendingRoute.value = null
      } else {
        router.replace('/home')
      }
    }
  } catch (e) {
    showToast(`${source === 'wechat' ? '微信' : 'QQ'}登录失败，请重试`, 'error');
  }
});

/* ========================================================
 * 账号密码登录（原有逻辑，TODO 保留待后端对接）
 * ======================================================== */
const handleLogin = async () => {
  //--- mock 登录（后端就绪后删除此段） ---
  showToast(`登录成功！欢迎 ${loginForm.value.username}`, 'success');
  showAuthModal.value = false;
  try {
    localStorage.setItem('username', loginForm.value.username || '')
    localStorage.setItem('token', 'demo-token')
    localStorage.setItem('role', 'user')
  } catch (e) { }
  loginForm.value = { username: '', password: '' };
  if (pendingRoute.value) {
    router.replace(pendingRoute.value)
    pendingRoute.value = null
  } else {
    router.replace('/home')
  }

  // // --- 真实接口 ---
  // try {
  //   const data = await loginApi(loginForm.value)
  //   localStorage.setItem('token', data.token)
  //   localStorage.setItem('role', data.user?.role || 'user')
  //   localStorage.setItem('username', data.user?.username || loginForm.value.username)
  //   if (data.user?.userId) localStorage.setItem('userId', data.user.userId)
  //   showToast(`登录成功！欢迎 ${data.user?.username || loginForm.value.username}`, 'success')
  //   showAuthModal.value = false
  //   loginForm.value = { username: '', password: '' }
  //   if (pendingRoute.value) {
  //     router.replace(pendingRoute.value)
  //     pendingRoute.value = null
  //   } else {
  //     router.replace(data.user?.role === 'admin' ? '/admin' : '/home')
  //   }
  // } catch (e) {
  //   showToast('登录失败，请检查账号密码', 'error')
  // }
};

/* ========================================================
 * 短信验证码（原有逻辑，TODO 保留待后端对接）
 * ======================================================== */
const getCode = async () => {
  if (!registerForm.value.phone) {
    showToast('请先输入手机号', 'warning');
    return;
  }
  // --- mock（后端就绪后替换） ---
  showToast('验证码已发送', 'success');
  // --- 真实接口 ---
  // try {
  //   await sendCodeApi({ phone: registerForm.value.phone })
  //   showToast('验证码已发送', 'success')
  // } catch (e) {
  //   alert('验证码发送失败')
  //   return
  // }
  codeCountdown.value = 60;
  const timer = setInterval(() => {
    codeCountdown.value--;
    if (codeCountdown.value <= 0) {
      clearInterval(timer);
    }
  }, 1000);
};

/* ========================================================
 * 注册（原有逻辑，TODO 保留待后端对接）
 * ======================================================== */
const handleRegister = async () => {
  if (!registerForm.value.agree) {
    showToast('请先同意用户协议和隐私政策', 'warning');
    return;
  }
  if (registerForm.value.password !== registerForm.value.confirmPassword) {
    showToast('两次密码输入不一致！', 'warning');
    return;
  }

  // --- mock 注册（后端就绪后删除此段） ---
  // showToast(`注册成功！欢迎 ${registerForm.value.username}`, 'success');
  // showAuthModal.value = false;
  // try {
  //   localStorage.setItem('username', registerForm.value.username || '')
  //   localStorage.setItem('token', 'demo-token')
  //   localStorage.setItem('role', 'user')
  // } catch (e) { }
  // registerForm.value = {
  //   username: '', email: '', phone: '', code: '', password: '', confirmPassword: '', agree: false
  // };
  // if (pendingRoute.value) {
  //   router.push(pendingRoute.value)
  //   pendingRoute.value = null
  // } else {
  //   router.push('/home')
  // }

  // --- 真实接口 ---
  try {
    await registerApi({
      username: registerForm.value.username,
      email: registerForm.value.email,
      password: registerForm.value.password
    })
    showToast(`注册成功！欢迎 ${registerForm.value.username}`, 'success')
    showAuthModal.value = false
    registerForm.value = {
      username: '', email: '', phone: '', code: '', password: '', confirmPassword: '', agree: false
    };
    if (pendingRoute.value) {
      router.push(pendingRoute.value)
      pendingRoute.value = null
    } else {
      router.push('/home')
    }
  } catch (e) {
    showToast('注册失败，请稍后重试', 'error')
  }
};

/* ========================================================
 * 微信扫码登录
 * 流程：
 *   1. 调用 getWechatAuthUrlApi 获取微信授权页 URL
 *   2. 跳转至微信授权页，用户扫码
 *   3. 微信回调后端 → 后端重定向回前端页面，URL 带 ?code=xxx&source=wechat
 *   4. onMounted 中自动检测 code，调用 wechatLoginApi 完成登录
 * ======================================================== */
const handleWechatLogin = async () => {
  try {
    const redirectUrl = encodeURIComponent(window.location.origin + window.location.pathname);
    const res = await getWechatAuthUrlApi(redirectUrl);
    const data = res?.data || res;
    if (data?.authUrl) {
      // 跳转到微信扫码登录页
      window.location.href = data.authUrl;
    }
  } catch (e) {
    showToast('获取微信登录二维码失败，请稍后重试', 'error');
  }
};

/* ========================================================
 * QQ 扫码登录
 * 流程同微信登录，区别在于 source=qq
 * ======================================================== */
const handleQQLogin = async () => {
  try {
    const redirectUrl = encodeURIComponent(window.location.origin + window.location.pathname);
    const res = await getQQAuthUrlApi(redirectUrl);
    const data = res?.data || res;
    if (data?.authUrl) {
      // 跳转到 QQ 授权登录页
      window.location.href = data.authUrl;
    }
  } catch (e) {
    showToast('获取 QQ 登录授权页失败，请稍后重试', 'error');
  }
};

/* ========================================================
 * 邮箱验证码登录 — 点击邮箱图标，打开邮箱登录弹窗
 * ======================================================== */
const handleEmailLoginClick = () => {
  // 重置邮箱登录状态
  emailLoginForm.value = { email: '', code: '' };
  emailCodeSent.value = false;
  emailCountdown.value = 0;
  showEmailDialog.value = true;
};

/* ========================================================
 * 邮箱验证码登录 — 发送验证码
 * 调用 sendEmailCodeApi，后端向指定邮箱发送 6 位验证码
 * 发送成功后切换到第二步（输入验证码），并启动重发倒计时
 * ======================================================== */
const handleSendEmailCode = async () => {
  const email = emailLoginForm.value.email?.trim();
  if (!email) {
    showToast('请输入邮箱地址', 'warning');
    return;
  }
  // 简单邮箱格式校验
  if (!/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(email)) {
    showToast('请输入正确的邮箱格式', 'warning');
    return;
  }

  emailLoading.value = true;
  try {
    await sendEmailCodeApi(email);
    emailCodeSent.value = true;
    showToast('验证码已发送至您的邮箱，请查收', 'success');

    // 启动 60 秒重发倒计时
    emailCountdown.value = 60;
    const timer = setInterval(() => {
      emailCountdown.value--;
      if (emailCountdown.value <= 0) {
        clearInterval(timer);
      }
    }, 1000);
  } catch (e) {
    showToast('验证码发送失败，请稍后重试', 'error');
  } finally {
    emailLoading.value = false;
  }
};

/* ========================================================
 * 邮箱验证码登录 — 用邮箱 + 验证码换取 token 完成登录
 * 调用 emailLoginApi，成功后写入 localStorage 并跳转首页
 * ======================================================== */
const handleEmailLogin = async () => {
  const { email, code } = emailLoginForm.value;
  if (!code || code.length !== 6) {
    showToast('请输入 6 位验证码', 'warning');
    return;
  }

  emailLoading.value = true;
  try {
    const res = await emailLoginApi(email, code);
    const data = res?.data || res;
    if (data?.token) {
      localStorage.setItem('token', data.token);
      localStorage.setItem('role', data.role || 'user');
      localStorage.setItem('username', data.username || '');
      if (data.userId) localStorage.setItem('userId', String(data.userId));
      showEmailDialog.value = false;
      showToast(`登录成功！欢迎 ${data.username || email}`, 'success');
      
      // 如果有登录前的目标页面，跳转到目标页面，否则跳转到首页
      if (pendingRoute.value) {
        router.replace(pendingRoute.value)
        pendingRoute.value = null
      } else {
        router.replace('/home')
      }
    } else {
      showToast('登录异常，请稍后重试', 'error');
    }
  } catch (e) {
    showToast('验证码错误或已过期，请重新获取', 'error');
  } finally {
    emailLoading.value = false;
  }
};
</script>

<style scoped>
.home {
  min-height: 100vh;
  background: linear-gradient(135deg, #2d8a6e 0%, #1a5a45 100%);
}

.navbar {
  background-color: #0F5132;
  padding: 12px 0;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
  animation: slideDown 0.6s ease-out;
}

@keyframes slideDown {
  from {
    transform: translateY(-100%);
    opacity: 0;
  }

  to {
    transform: translateY(0);
    opacity: 1;
  }
}

.nav-container {
  max-width: 1200px;
  margin: 0 auto;
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 0 24px;
}

.logo {
  font-size: 18px;
  color: white;
  font-weight: 500;
  transition: all 0.3s ease;
}

.logo:hover {
  transform: scale(1.05);
  filter: brightness(1.2);
  text-decoration: none;
}

.nav-actions {
  display: flex;
  gap: 16px;
}

.nav-btn {
  background: none;
  border: none;
  color: white;
  cursor: pointer;
  padding: 8px 14px;
  display: flex;
  align-items: center;
  gap: 6px;
  border-radius: 8px;
  transition: all 0.3s ease;
  animation: fadeInRight 0.6s ease-out;
  text-decoration: none;
  font-size: 14px;
}

.btn-label {
  white-space: nowrap;
  font-weight: 500;
  letter-spacing: 0.5px;
}

.nav-btn:nth-child(1) {
  animation-delay: 0.1s;
}

.nav-btn:nth-child(2) {
  animation-delay: 0.2s;
}

.nav-btn:nth-child(3) {
  animation-delay: 0.3s;
}

@keyframes fadeInRight {
  from {
    transform: translateX(20px);
    opacity: 0;
  }

  to {
    transform: translateX(0);
    opacity: 1;
  }
}

.nav-btn:hover {
  background: rgba(255, 255, 255, 0.1);
  transform: scale(1.1);
  color: #D3D3D3;
}

.nav-btn:active {
  transform: scale(0.95);
}

.admin-entry {

  border-radius: 20px;
  padding: 6px 14px;
}

.admin-entry:hover {
  border-color: rgba(255, 255, 255, 0.5);
}

.hero-section {
  position: relative;
  min-height: calc(100vh - 60px);
  display: flex;
  align-items: center;
  padding: 0 80px;
  overflow: hidden;
  background: url('https://coresg-normal.trae.ai/api/ide/v1/text_to_image?prompt=tropical%20coastal%20road%20aerial%20view%2C%20lush%20green%20forest%2C%20blue%20ocean%2C%20beautiful%20travel%20destination&image_size=landscape_16_9') center/cover no-repeat;
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

.hero-section::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: linear-gradient(135deg, rgba(45, 138, 110, 0.7) 0%, rgba(26, 90, 69, 0.8) 100%);
}

.hero-content {
  position: relative;
  z-index: 10;
  color: white;
  max-width: 800px;
}

.welcome-text {
  font-size: 30px;
  font-weight: 900;
  margin-bottom: 12px;
  opacity: 0.95;
  animation: fadeInUp 0.8s ease-out;
  font-family: 'Noto Sans SC', 'Microsoft YaHei', 'SimHei', sans-serif;
  letter-spacing: 6px;
  text-transform: uppercase;
}

.main-title-row {
  display: flex;
  align-items: baseline;
  margin-bottom: 20px;
  animation: fadeInUp 0.8s ease-out 0.2s both;
  white-space: nowrap;
}

.brand-name {
  font-size: 76px;
  font-weight: 900;
  text-shadow: 4px 4px 8px rgba(0, 0, 0, 0.4);
  font-family: 'Noto Sans SC', 'Microsoft YaHei', 'SimHei', sans-serif;
  letter-spacing: 4px;
}

.subtitle-text {
  font-size: 36px;
  font-weight: 700;
  text-shadow: 2px 2px 4px rgba(0, 0, 0, 0.4);
  font-family: 'Noto Sans SC', 'Microsoft YaHei', 'SimHei', sans-serif;
  letter-spacing: 2px;
  margin-left: 12px;
}

.subtitle {
  font-size: 28px;
  opacity: 0.95;
  animation: fadeInUp 0.8s ease-out 0.4s both;
  font-family: 'Noto Sans SC', 'Microsoft YaHei', 'SimHei', sans-serif;
  letter-spacing: 6px;
  font-weight: 700;
}

@keyframes fadeInUp {
  from {
    transform: translateY(40px);
    opacity: 0;
  }

  to {
    transform: translateY(0);
    opacity: 0.9;
  }
}

.floating-images {
  position: absolute;
  right: 150px;
  top: 40%;
  transform: translateY(-50%);
  z-index: 5;
}

.image-card {
  position: absolute;
  border-radius: 12px;
  overflow: hidden;
  box-shadow: 0 10px 30px rgba(0, 0, 0, 0.3);
  cursor: pointer;
  transition: all 0.4s ease;
  text-decoration: none;
  color: inherit;
}

.image-card:hover {
  transform: scale(1.05) rotate(0deg) !important;
  box-shadow: 0 20px 50px rgba(0, 0, 0, 0.4);
  z-index: 10 !important;
}

.image-card img {
  width: 180px;
  height: 240px;
  object-fit: cover;
  display: block;
  transition: transform 0.5s ease;
}

.image-card:hover img {
  transform: scale(1.1);
}

.place-name {
  position: absolute;
  bottom: 40px;
  left: 0;
  right: 0;
  background: linear-gradient(to top, rgba(0, 0, 0, 0.7), transparent);
  color: white;
  padding: 15px 12px 8px;
  font-size: 14px;
  font-weight: 500;
  transform: translateY(10px);
  opacity: 0;
  transition: all 0.3s ease;
}

.image-card:hover .place-name {
  transform: translateY(0);
  opacity: 1;
}

.go-btn {
  position: absolute;
  bottom: 10px;
  left: 50%;
  transform: translateX(-50%);
  background: linear-gradient(135deg, #2d8a6e 0%, #1a5a45 100%);
  color: white;
  border: none;
  border-radius: 20px;
  padding: 6px 16px;
  font-size: 12px;
  font-weight: 500;
  cursor: pointer;
  opacity: 0;
  transition: all 0.3s ease;
  box-shadow: 0 2px 8px rgba(45, 138, 110, 0.4);
}

.image-card:hover .go-btn {
  opacity: 1;
}

.go-btn:hover {
  transform: translateX(-50%) scale(1.05);
  box-shadow: 0 4px 12px rgba(45, 138, 110, 0.5);
}

.card-1 {
  right: 120px;
  top: -160px;
  transform: rotate(5deg);
  z-index: 3;
  animation: float1 6s ease-in-out infinite, slideInRight 0.8s ease-out 0.3s both;
}

@keyframes float1 {

  0%,
  100% {
    transform: rotate(5deg) translateY(0px);
  }

  50% {
    transform: rotate(3deg) translateY(-20px);
  }
}

.card-2 {
  right: 20px;
  top: 40px;
  transform: rotate(-3deg);
  z-index: 2;
  animation: float2 7s ease-in-out infinite, slideInRight 0.8s ease-out 0.5s both;
}

@keyframes float2 {

  0%,
  100% {
    transform: rotate(-3deg) translateY(0px);
  }

  50% {
    transform: rotate(-1deg) translateY(-15px);
  }
}

.card-3 {
  right: 220px;
  top: 80px;
  transform: rotate(8deg);
  z-index: 1;
  animation: float3 8s ease-in-out infinite, slideInRight 0.8s ease-out 0.7s both;
}

@keyframes float3 {

  0%,
  100% {
    transform: rotate(8deg) translateY(0px);
  }

  50% {
    transform: rotate(6deg) translateY(-25px);
  }
}

@keyframes slideInRight {
  from {
    transform: translateX(100px) rotate(0deg);
    opacity: 0;
  }

  to {
    opacity: 1;
  }
}

.music-btn {
  position: absolute;
  bottom: 30px;
  right: 30px;
  z-index: 20;
  background: white;
  border-radius: 50px;
  padding: 12px 20px;
  display: flex;
  align-items: center;
  gap: 10px;
  box-shadow: 0 4px 15px rgba(0, 0, 0, 0.2);
  color: #2d8a6e;
  cursor: pointer;
  animation: pulse 2s ease-in-out infinite, fadeInUp 0.8s ease-out 1s both;
  transition: all 0.3s ease;
}

.music-btn:hover {
  transform: scale(1.05);
  box-shadow: 0 6px 25px rgba(0, 0, 0, 0.3);
}

@keyframes pulse {

  0%,
  100% {
    box-shadow: 0 4px 15px rgba(0, 0, 0, 0.2);
  }

  50% {
    box-shadow: 0 4px 25px rgba(45, 138, 110, 0.5);
  }
}

.music-btn span {
  font-size: 14px;
  font-weight: 500;
}

.music-btn svg {
  animation: music 1s ease-in-out infinite;
}

@keyframes music {

  0%,
  100% {
    transform: rotate(0deg);
  }

  25% {
    transform: rotate(-10deg);
  }

  75% {
    transform: rotate(10deg);
  }
}

/* 登录/注册弹窗样式 */
.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.6);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1000;
  animation: fadeIn 0.3s ease-out;
}

@keyframes fadeIn {
  from {
    opacity: 0;
  }

  to {
    opacity: 1;
  }
}

.modal-content {
  background: white;
  border-radius: 16px;
  padding: 40px;
  width: 100%;
  max-width: 420px;
  position: relative;
  box-shadow: 0 20px 60px rgba(0, 0, 0, 0.3);
  animation: slideUp 0.3s ease-out;
}

@keyframes slideUp {
  from {
    transform: translateY(30px);
    opacity: 0;
  }

  to {
    transform: translateY(0);
    opacity: 1;
  }
}

.modal-close {
  position: absolute;
  top: 16px;
  right: 16px;
  background: none;
  border: none;
  font-size: 28px;
  color: #999;
  cursor: pointer;
  transition: color 0.3s;
  line-height: 1;
}

.modal-close:hover {
  color: #2d8a6e;
}

.tabs {
  display: flex;
  margin-bottom: 32px;
  border-bottom: 2px solid #f0f0f0;
}

.tab {
  flex: 1;
  padding: 12px 0;
  background: none;
  border: none;
  font-size: 18px;
  font-weight: 500;
  color: #999;
  cursor: pointer;
  transition: all 0.3s;
  position: relative;
}

.tab:hover {
  color: #2d8a6e;
}

.tab.active {
  color: #2d8a6e;
}

.tab.active::after {
  content: '';
  position: absolute;
  bottom: -2px;
  left: 0;
  right: 0;
  height: 2px;
  background: #2d8a6e;
}

.auth-form {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.form-group {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.form-group label {
  font-size: 14px;
  font-weight: 500;
  color: #333;
}

.input-wrapper {
  position: relative;
  display: flex;
  align-items: center;
}

.input-icon,
.eye-icon,
.clear-icon {
  position: absolute;
  color: #999;
  cursor: pointer;
  transition: color 0.3s;
}

.input-icon {
  left: 16px;
}

.eye-icon {
  right: 16px;
}

.clear-icon {
  right: 40px;
  font-size: 20px;
  line-height: 1;
}

.input-icon:hover,
.eye-icon:hover,
.clear-icon:hover {
  color: #2d8a6e;
}

.input-wrapper input {
  width: 100%;
  padding: 12px 48px;
  border: 2px solid #e5e5e5;
  border-radius: 8px;
  font-size: 16px;
  transition: all 0.3s;
  outline: none;
}

.input-wrapper input:focus {
  border-color: #2d8a6e;
  box-shadow: 0 0 0 3px rgba(45, 138, 110, 0.1);
}

.forgot-password {
  text-align: right;
  margin-top: -8px;
}

.forgot-password a {
  color: #666;
  text-decoration: none;
  font-size: 14px;
  transition: color 0.3s;
}

.forgot-password a:hover {
  color: #2d8a6e;
}

.other-login {
  margin-top: 24px;
  text-align: center;
}

.other-login p {
  color: #999;
  font-size: 14px;
  margin-bottom: 16px;
}

.social-icons {
  display: flex;
  justify-content: center;
  gap: 16px;
}

.social-icon {
  width: 48px;
  height: 48px;
  border-radius: 50%;
  border: none;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  transition: all 0.3s;
}

.social-icon:hover {
  transform: scale(1.1);
}

.social-icon.wechat {
  background: #07c160;
  color: white;
}

.social-icon.qq {
  background: #12b7f5;
  color: white;
}

.social-icon.email {
  background: #ff6b6b;
  color: white;
}

.phone-wrapper .country-code {
  position: absolute;
  left: 48px;
  color: #666;
  font-size: 16px;
  border-right: 1px solid #e5e5e5;
  padding-right: 12px;
}

.phone-wrapper input {
  padding-left: 100px !important;
}

.code-wrapper {
  display: flex;
  gap: 12px;
}

.code-wrapper input {
  flex: 1;
}

.get-code-btn {
  padding: 12px 20px;
  background: linear-gradient(135deg, #2d8a6e 0%, #1a5a45 100%);
  color: white;
  border: none;
  border-radius: 8px;
  font-size: 14px;
  cursor: pointer;
  white-space: nowrap;
  transition: all 0.3s;
}

.get-code-btn:hover:not(:disabled) {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(45, 138, 110, 0.3);
}

.get-code-btn:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}

.agreement {
  margin-top: -8px;
}

.checkbox-label {
  display: flex;
  align-items: flex-start;
  gap: 8px;
  cursor: pointer;
  font-size: 14px;
}

.checkbox-label input[type="checkbox"] {
  display: none;
}

.checkmark {
  width: 18px;
  height: 18px;
  border: 2px solid #e5e5e5;
  border-radius: 4px;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all 0.3s;
  flex-shrink: 0;
  margin-top: 2px;
}

.checkbox-label input[type="checkbox"]:checked+.checkmark {
  background: linear-gradient(135deg, #2d8a6e 0%, #1a5a45 100%);
  border-color: #2d8a6e;
}

.checkbox-label input[type="checkbox"]:checked+.checkmark::after {
  content: '✓';
  color: white;
  font-size: 12px;
  font-weight: bold;
}

.agreement-text {
  color: #666;
  line-height: 1.5;
}

.agreement-text a {
  color: #2d8a6e;
  text-decoration: none;
  transition: color 0.3s;
}

.agreement-text a:hover {
  color: #1a5a45;
  text-decoration: underline;
}

.submit-btn {
  margin-top: 12px;
  padding: 14px;
  background: linear-gradient(135deg, #2d8a6e 0%, #1a5a45 100%);
  color: white;
  border: none;
  border-radius: 8px;
  font-size: 16px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s;
}

.submit-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 8px 20px rgba(45, 138, 110, 0.4);
}

.submit-btn:active {
  transform: translateY(0);
}

/* 邮箱验证码登录弹窗 */
.email-modal {
  max-width: 400px;
}

.email-modal-title {
  text-align: center;
  font-size: 20px;
  font-weight: 600;
  color: #333;
  margin-bottom: 24px;
}

.email-sent-tip {
  text-align: center;
  color: #666;
  font-size: 14px;
  margin-bottom: 8px;
}

.email-sent-tip strong {
  color: #2d8a6e;
}

.resend-btn {
  background: none;
  border: none;
  color: #2d8a6e;
  font-size: 14px;
  cursor: pointer;
  text-align: center;
  padding: 8px 0;
  transition: color 0.3s;
}

.resend-btn:hover:not(:disabled) {
  color: #1a5a45;
  text-decoration: underline;
}

.resend-btn:disabled {
  color: #999;
  cursor: not-allowed;
}
</style>
