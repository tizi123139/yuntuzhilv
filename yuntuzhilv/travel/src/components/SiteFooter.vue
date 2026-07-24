<template>
  <footer class="site-footer">
    <div class="footer-inner">
      <!-- 品牌区 -->
      <div class="footer-brand">
        <div class="footer-logo">WayCloud 云途智行</div>
        <p class="footer-tagline">AI 驱动的智能旅行规划平台<br />让每一段旅程都恰到好处</p>
        <div class="footer-social">
          <button class="social-icon" title="微信公众号" @click="showQrCode = true">
            <svg viewBox="0 0 24 24" fill="currentColor" width="18" height="18">
              <path
                d="M8.691 2.188C3.891 2.188 0 5.476 0 9.53c0 2.212 1.17 4.203 3.002 5.55a.59.59 0 0 1 .213.665l-.39 1.48c-.019.07-.048.141-.048.213 0 .163.13.295.29.295a.326.326 0 0 0 .167-.054l1.903-1.114a.864.864 0 0 1 .717-.098 10.16 10.16 0 0 0 2.837.403c.276 0 .543-.027.811-.05a6.127 6.127 0 0 1-.253-1.726c0-3.573 3.26-6.467 7.278-6.467.197 0 .39.013.584.025C16.16 4.842 12.78 2.188 8.691 2.188zm-2.6 4.408c.56 0 1.015.46 1.015 1.024 0 .565-.455 1.024-1.015 1.024-.56 0-1.014-.459-1.014-1.024 0-.564.454-1.024 1.014-1.024zm5.218 0c.56 0 1.015.46 1.015 1.024 0 .565-.455 1.024-1.015 1.024-.56 0-1.014-.459-1.014-1.024 0-.564.454-1.024 1.014-1.024zM16.527 9.1c-3.455 0-6.263 2.503-6.263 5.586 0 3.083 2.808 5.586 6.263 5.586a7.29 7.29 0 0 0 2.14-.318.637.637 0 0 1 .536.074l1.427.836a.258.258 0 0 0 .124.041.215.215 0 0 0 .217-.221c0-.054-.022-.107-.036-.159l-.292-1.11a.443.443 0 0 1 .159-.498c1.38-1.01 2.255-2.514 2.255-4.231 0-3.083-2.808-5.586-6.263-5.586h-.267zm-2.063 3.26c.42 0 .762.344.762.768s-.342.768-.762.768a.765.765 0 0 1-.762-.768c0-.424.341-.768.762-.768zm4.127 0c.42 0 .762.344.762.768s-.342.768-.762.768a.765.765 0 0 1-.762-.768c0-.424.341-.768.762-.768z" />
            </svg>
          </button>
        </div>
      </div>

      <!-- 快速导航 -->
      <div class="footer-links">
        <h4>快速导航</h4>
        <ul>
          <li><a @click.prevent="go('/home')">首页</a></li>
          <li><a @click.prevent="go('/attractions')">景点浏览</a></li>
          <li><a @click.prevent="go('/hotels')">酒店查询</a></li>
          <li><a @click.prevent="go('/traffic')">交通查询</a></li>
          <li><a @click.prevent="go('/create-itinerary')">AI 行程规划</a></li>
        </ul>
      </div>

      <!-- 关于与法律 -->
      <div class="footer-links">
        <h4>关于与法律</h4>
        <ul>
          <li><a href="https://www.scuec.edu.cn/jky/index.htm" target="_blank" rel="noopener noreferrer">关于我们</a></li>
          <li><a @click.prevent="showPrivacyPolicy = true">隐私政策</a></li>
          <li><a @click.prevent="showServiceTerms = true">服务条款</a></li>
          <li><a @click.prevent="showContactUs = true">联系我们</a></li>
          <li><a @click.prevent="showFeedback = true">意见反馈</a></li>
        </ul>
      </div>

      <!-- 友情链接 -->
      <div class="footer-links">
        <h4>友情链接</h4>
        <ul>
          <li><a href="https://www.ctrip.com" target="_blank" rel="noopener">携程旅行</a></li>
          <li><a href="https://www.mafengwo.cn" target="_blank" rel="noopener">马蜂窝</a></li>
          <li><a href="https://www.qunar.com" target="_blank" rel="noopener">去哪儿</a></li>
          <li><a href="https://www.weather.com.cn" target="_blank" rel="noopener">中国天气网</a></li>
          <li><a href="https://www.mnr.gov.cn" target="_blank" rel="noopener">文旅部官网</a></li>
        </ul>
      </div>
    </div>

    <!-- 底栏 -->
    <div class="footer-bottom">
      <p>&copy; {{ currentYear }} WayCloud 云途智行 &mdash; 让旅行更智能</p>
      <!-- <p class="footer-icp">ICP备案号：<a href="#">京ICP备XXXXXXXX号</a></p> -->
    </div>

    <!-- 微信公众号二维码弹窗 -->
    <Teleport to="body">
      <!-- 二维码弹窗 -->
      <Transition name="qr-fade">
        <div v-if="showQrCode" class="qr-overlay" @click.self="showQrCode = false">
          <div class="qr-modal">
            <button class="qr-close" @click="showQrCode = false">&times;</button>
            <h3 class="qr-title">关注云途智行公众号</h3>
            <p class="qr-desc">微信扫一扫，获取最新旅行资讯与专属优惠</p>
            <img src="/YunTuEWM.jpg" alt="微信公众号二维码" class="qr-image" />
          </div>
        </div>
      </Transition>

      <!-- 隐私政策弹窗 -->
      <Transition name="qr-fade">
        <div v-if="showPrivacyPolicy" class="qr-overlay" @click.self="showPrivacyPolicy = false">
          <div class="qr-modal policy-modal">
            <button class="qr-close" @click="showPrivacyPolicy = false">&times;</button>
            <h3 class="qr-title">隐私政策</h3>
            <div class="policy-content">
              <p>欢迎使用云途智行（以下简称"我们"）！我们重视您的隐私保护，致力于为您提供安全、可靠的服务。</p>
              <h4>一、我们收集的信息</h4>
              <p>我们可能收集您的个人信息，包括但不限于：姓名、手机号、邮箱、位置信息、浏览记录等。这些信息仅用于提供更好的服务体验。</p>
              <h4>二、信息的使用</h4>
              <p>我们将根据您的授权使用收集的信息，用于：提供个性化推荐、处理预订请求、发送服务通知等。</p>
              <h4>三、信息保护</h4>
              <p>我们采用业界标准的安全措施保护您的信息，防止未经授权的访问、使用或披露。</p>
              <h4>四、第三方服务</h4>
              <p>我们可能会使用第三方服务来提升我们的服务，这些服务可能会收集您的信息。</p>
              <h4>五、政策更新</h4>
              <p>我们可能会不定期更新本政策，更新后的政策将在网站上公布。</p>
            </div>
          </div>
        </div>
      </Transition>

      <!-- 服务条款弹窗 -->
      <Transition name="qr-fade">
        <div v-if="showServiceTerms" class="qr-overlay" @click.self="showServiceTerms = false">
          <div class="qr-modal policy-modal">
            <button class="qr-close" @click="showServiceTerms = false">&times;</button>
            <h3 class="qr-title">服务条款</h3>
            <div class="policy-content">
              <p>欢迎使用云途智行服务！请仔细阅读以下服务条款。</p>
              <h4>一、服务内容</h4>
              <p>我们提供智能旅行规划、景点推荐、酒店预订、交通查询等服务。</p>
              <h4>二、用户义务</h4>
              <p>您应提供真实、准确的信息，遵守相关法律法规，不得滥用我们的服务。</p>
              <h4>三、服务变更</h4>
              <p>我们保留随时变更、暂停或终止服务的权利，无需事先通知用户。</p>
              <h4>四、免责声明</h4>
              <p>我们对服务的准确性、可靠性不作担保，用户应自行承担使用风险。</p>
              <h4>五、争议解决</h4>
              <p>如发生争议，双方应协商解决；协商不成的，可向有管辖权的法院提起诉讼。</p>
            </div>
          </div>
        </div>
      </Transition>

      <!-- 联系我们弹窗 -->
      <Transition name="qr-fade">
        <div v-if="showContactUs" class="qr-overlay" @click.self="showContactUs = false">
          <div class="qr-modal contact-modal">
            <button class="qr-close" @click="showContactUs = false">&times;</button>
            <h3 class="qr-title">联系我们</h3>
            <div class="contact-list">
              <div class="contact-item">
                <span class="contact-name">Yu</span>
                <span class="contact-phone">11111111111</span>
              </div>
              <div class="contact-item">
                <span class="contact-name">Wu</span>
                <span class="contact-phone">22222222222</span>
              </div>
              <div class="contact-item">
                <span class="contact-name">Cai</span>
                <span class="contact-phone">33333333333</span>
              </div>
              <div class="contact-item">
                <span class="contact-name">Wang</span>
                <span class="contact-phone">44444444444</span>
              </div>
            </div>
          </div>
        </div>
      </Transition>

      <!-- 意见反馈弹窗 -->
      <Transition name="qr-fade">
        <div v-if="showFeedback" class="qr-overlay" @click.self="showFeedback = false">
          <div class="qr-modal feedback-modal">
            <button class="qr-close" @click="showFeedback = false">&times;</button>
            <h3 class="qr-title">意见反馈</h3>
            <div v-if="feedbackSubmitted" class="feedback-success">
              <svg viewBox="0 0 24 24" fill="none" stroke="#2d8a6e" stroke-width="2" width="48" height="48">
                <polyline points="20 6 9 17 4 12" />
              </svg>
              <p>提交成功！感谢您的反馈。</p>
            </div>
            <form v-else class="feedback-form" @submit.prevent="submitFeedback">
              <textarea v-model="feedbackContent" placeholder="请输入您的意见或建议..." rows="5" required></textarea>
              <div class="feedback-buttons">
                <button type="button" class="feedback-btn feedback-cancel" @click="showFeedback = false">关闭</button>
                <button type="submit" class="feedback-btn feedback-submit" :disabled="feedbackLoading">
              {{ feedbackLoading ? '提交中...' : '提交' }}
            </button>
              </div>
            </form>
          </div>
        </div>
      </Transition>
    </Teleport>
  </footer>
</template>

<script setup>
import { computed, ref } from 'vue'
import { useRouter } from 'vue-router'
import { showToast } from '../utils/toast'
import { submitFeedbackApi } from '../api/admin'

const router = useRouter()
const currentYear = computed(() => new Date().getFullYear())
const showQrCode = ref(false)

// 弹窗控制变量
const showPrivacyPolicy = ref(false)
const showServiceTerms = ref(false)
const showContactUs = ref(false)
const showFeedback = ref(false)

// 意见反馈表单
const feedbackContent = ref('')
const feedbackSubmitted = ref(false)
const feedbackLoading = ref(false)

// 提交意见反馈
async function submitFeedback() {
  if (!feedbackContent.value.trim()) {
    showToast('请输入反馈内容', 'warning')
    return
  }

  feedbackLoading.value = true
  try {
    // 获取用户信息（已登录用户携带）
    const userId = localStorage.getItem('userId') || 0
    const username = localStorage.getItem('username') || ''

    await submitFeedbackApi({
      userId: parseInt(userId),
      content: feedbackContent.value.trim(),
      username
    })

    // 提交成功
    feedbackSubmitted.value = true
    feedbackContent.value = ''
    showToast('提交成功！感谢您的反馈', 'success')

    // 3秒后关闭弹窗
    setTimeout(() => {
      showFeedback.value = false
      feedbackSubmitted.value = false
    }, 3000)
  } catch (e) {
    console.error('[SiteFooter] 反馈提交失败:', e)
    showToast('提交失败，请稍后重试', 'error')
  } finally {
    feedbackLoading.value = false
  }
}

function go(path) {
  router.push(path)
}
</script>

<style scoped>
.site-footer {
  background: #0b3d28;
  color: rgba(255, 255, 255, 0.78);
}

.footer-inner {
  max-width: 1280px;
  margin: 0 auto;
  padding: 48px 24px 36px;
  display: grid;
  grid-template-columns: 1.6fr 1fr 1fr 1fr;
  gap: 40px;
}

/* 品牌区 */
.footer-brand {
  padding-right: 24px;
}

.footer-logo {
  font-size: 20px;
  font-weight: 800;
  color: #fff;
  margin-bottom: 12px;
  letter-spacing: 1px;
}

.footer-tagline {
  font-size: 14px;
  line-height: 1.8;
  color: rgba(255, 255, 255, 0.6);
  margin: 0 0 20px;
}

.footer-social {
  display: flex;
  gap: 12px;
}

.social-icon {
  width: 36px;
  height: 36px;
  border-radius: 50%;
  background: rgba(255, 255, 255, 0.1);
  display: inline-flex;
  align-items: center;
  justify-content: center;
  color: rgba(255, 255, 255, 0.65);
  transition: all 0.25s;
  text-decoration: none;
  border: none;
  cursor: pointer;
  padding: 0;
}

.social-icon:hover {
  background: #2d8a6e;
  color: #fff;
  transform: translateY(-2px);
}

/* 链接列 */
.footer-links h4 {
  font-size: 15px;
  font-weight: 700;
  color: #fff;
  margin: 0 0 16px;
  position: relative;
  padding-bottom: 10px;
}

.footer-links h4::after {
  content: '';
  position: absolute;
  left: 0;
  bottom: 0;
  width: 24px;
  height: 2px;
  background: #2d8a6e;
  border-radius: 1px;
}

.footer-links ul {
  list-style: none;
  margin: 0;
  padding: 0;
}

.footer-links li {
  margin-bottom: 10px;
}

.footer-links a {
  font-size: 14px;
  color: rgba(255, 255, 255, 0.6);
  text-decoration: none;
  transition: color 0.2s;
  cursor: pointer;
}

.footer-links a:hover {
  color: #7dd3b5;
}

/* 底栏 */
.footer-bottom {
  border-top: 1px solid rgba(255, 255, 255, 0.08);
  max-width: 1280px;
  margin: 0 auto;
  padding: 18px 24px;
  display: flex;
  justify-content: space-between;
  align-items: center;
  flex-wrap: wrap;
  gap: 8px;
}

.footer-bottom p {
  margin: 0;
  font-size: 13px;
  color: rgba(255, 255, 255, 0.4);
}

.footer-icp a {
  color: rgba(255, 255, 255, 0.4);
  text-decoration: none;
  transition: color 0.2s;
}

.footer-icp a:hover {
  color: rgba(255, 255, 255, 0.7);
}

/* 响应式 */
@media (max-width: 980px) {
  .footer-inner {
    grid-template-columns: 1fr 1fr;
    gap: 32px;
  }

  .footer-brand {
    grid-column: 1 / -1;
    padding-right: 0;
  }
}

@media (max-width: 640px) {
  .footer-inner {
    grid-template-columns: 1fr;
    gap: 28px;
    padding: 32px 16px 24px;
  }

  .footer-bottom {
    flex-direction: column;
    text-align: center;
    padding: 14px 16px;
  }
}

/* 二维码弹窗 */
.qr-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.55);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 99999;
}

.qr-modal {
  background: #fff;
  border-radius: 16px;
  padding: 36px 32px 28px;
  width: 90%;
  max-width: 340px;
  position: relative;
  box-shadow: 0 20px 60px rgba(0, 0, 0, 0.3);
  text-align: center;
}

.qr-close {
  position: absolute;
  top: 12px;
  right: 14px;
  background: none;
  border: none;
  font-size: 26px;
  color: #999;
  cursor: pointer;
  line-height: 1;
  transition: color 0.2s;
}

.qr-close:hover {
  color: #2d8a6e;
}

.qr-title {
  margin: 0 0 8px;
  font-size: 18px;
  font-weight: 700;
  color: #0f5132;
}

.qr-desc {
  margin: 0 0 20px;
  font-size: 14px;
  color: #666;
  line-height: 1.6;
}

.qr-image {
  width: 200px;
  height: 200px;
  object-fit: contain;
  border-radius: 8px;
  border: 1px solid #eee;
}

/* 弹窗过渡动画 */
.qr-fade-enter-active,
.qr-fade-leave-active {
  transition: opacity 0.25s ease;
}

.qr-fade-enter-from,
.qr-fade-leave-to {
  opacity: 0;
}

/* 政策弹窗样式 */
.policy-modal {
  max-width: 500px;
  text-align: left;
}

.policy-content {
  max-height: 400px;
  overflow-y: auto;
  padding-right: 8px;
}

.policy-content p {
  margin: 0 0 12px;
  font-size: 14px;
  color: #666;
  line-height: 1.7;
}

.policy-content h4 {
  margin: 16px 0 8px;
  font-size: 15px;
  font-weight: 600;
  color: #0f5132;
}

.policy-content h4:first-child {
  margin-top: 0;
}

/* 联系我们弹窗样式 */
.contact-modal {
  max-width: 360px;
}

.contact-list {
  margin-top: 16px;
}

.contact-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 12px 0;
  border-bottom: 1px solid #f0f0f0;
}

.contact-item:last-child {
  border-bottom: none;
}

.contact-name {
  
  font-size: 20px;
  color: #333;
  font-weight: 500;
}

.contact-phone {
  font-size: 16px;
  color: #2d8a6e;
  font-weight: 600;
}

/* 意见反馈弹窗样式 */
.feedback-modal {
  max-width: 400px;
}

.feedback-form {
  margin-top: 16px;
}

.feedback-form textarea {
  width: 100%;
  padding: 12px;
  border: 1px solid #d1e5dc;
  border-radius: 8px;
  font-size: 14px;
  resize: none;
  box-sizing: border-box;
  font-family: inherit;
}

.feedback-form textarea:focus {
  outline: none;
  border-color: #51b891;
}

.feedback-form textarea::placeholder {
  color: #9cb5ac;
}

.feedback-buttons {
  display: flex;
  gap: 12px;
  margin-top: 16px;
}

.feedback-btn {
  flex: 1;
  padding: 10px;
  border: none;
  border-radius: 8px;
  font-size: 14px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.2s;
}

.feedback-cancel {
  background: #f0f0f0;
  color: #666;
}

.feedback-cancel:hover {
  background: #e0e0e0;
}

.feedback-submit {
  background: linear-gradient(135deg, #2d8a6e 0%, #1a5a45 100%);
  color: #fff;
}

.feedback-submit:hover {
  opacity: 0.9;
}

.feedback-submit:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}

.feedback-success {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 24px 0;
  text-align: center;
}

.feedback-success p {
  margin: 16px 0 0;
  font-size: 16px;
  color: #2d8a6e;
  font-weight: 600;
}
</style>
