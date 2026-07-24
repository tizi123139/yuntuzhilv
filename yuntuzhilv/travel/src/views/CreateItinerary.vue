<template>
  <div class="wizard-page">
    <div class="bg-decoration">
      <div class="cloud cloud-1"></div>
      <div class="cloud cloud-2"></div>
      <div class="cloud cloud-3"></div>
      <div class="cloud cloud-4"></div>
      <svg class="ink-wave ink-wave-1" viewBox="0 0 400 100" fill="none">
        <path d="M0 50Q50 30 100 50T200 50T300 50T400 50V100H0Z" fill="rgba(167,218,196,0.15)"/>
      </svg>
      <svg class="ink-wave ink-wave-2" viewBox="0 0 400 100" fill="none">
        <path d="M0 60Q60 40 120 60T240 60T360 60T400 60V100H0Z" fill="rgba(197,232,219,0.1)"/>
      </svg>
      <svg class="ink-mountain ink-mountain-1" viewBox="0 0 200 100" fill="none">
        <path d="M0 100L40 60L70 80L100 30L130 70L160 40L200 100Z" fill="rgba(167,218,196,0.2)"/>
      </svg>
      <svg class="ink-mountain ink-mountain-2" viewBox="0 0 150 80" fill="none">
        <path d="M0 80L30 45L60 60L90 25L120 50L150 80Z" fill="rgba(197,232,219,0.15)"/>
      </svg>
      <span class="float-dot float-dot-1"></span>
      <span class="float-dot float-dot-2"></span>
      <span class="float-dot float-dot-3"></span>
      <span class="float-dot float-dot-4"></span>
      <svg class="brush-stroke brush-stroke-1" viewBox="0 0 200 80" fill="none">
        <path d="M0 40Q50 10 100 30T200 20" stroke="rgba(167,218,196,0.3)" stroke-width="8" stroke-linecap="round"/>
      </svg>
      <svg class="brush-stroke brush-stroke-2" viewBox="0 0 150 60" fill="none">
        <path d="M150 30Q100 50 50 20T0 40" stroke="rgba(197,232,219,0.25)" stroke-width="6" stroke-linecap="round"/>
      </svg>
    </div>

    <header class="page-header">
      <div class="header-content">
        <div class="title-row">
          <svg class="title-icon" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
            <path d="M12 2L2 7l10 5 10-5-10-5z"/>
            <path d="M2 17l10 5 10-5"/>
            <path d="M2 12l10 5 10-5"/>
          </svg>
          <h1>行程定制向导</h1>
        </div>
        <p class="subtitle">填写出行信息，AI自动匹配景点、酒店、交通方案，生成专属多日旅行计划</p>
      </div>
      <button class="back-btn" @click="router.push('/home')">返回首页</button>
    </header>

    <div class="main-content">
      <div class="wizard-card">
        <h2 class="card-title">定制您的专属旅程</h2>
        <div class="card-body">
          <form class="form-grid" @submit.prevent="handleGenerate">
            <div class="form-item">
              <div class="input-icon">
                <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                  <circle cx="12" cy="12" r="10"/>
                  <path d="M12 6v6l4 2"/>
                </svg>
              </div>
              <div class="input-content">
                <label class="form-label">出发地</label>
                <input v-model.trim="form.fromCity" type="text" placeholder="例如：北京" required />
              </div>
            </div>
            <div class="form-item">
              <div class="input-icon">
                <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                  <path d="M20 10c0 6-8 12-8 12s-8-6-8-12a8 8 0 0116 0z"/>
                  <circle cx="12" cy="10" r="3"/>
                </svg>
              </div>
              <div class="input-content">
                <label class="form-label">目的地</label>
                <input v-model.trim="form.toCity" type="text" placeholder="例如：成都" required />
              </div>
            </div>
            <div class="form-item">
              <div class="input-icon">
                <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                  <rect x="3" y="4" width="18" height="18" rx="2" ry="2"/>
                  <line x1="16" y1="2" x2="16" y2="6"/>
                  <line x1="8" y1="2" x2="8" y2="6"/>
                  <line x1="3" y1="10" x2="21" y2="10"/>
                </svg>
              </div>
              <div class="input-content">
                <label class="form-label">游玩天数</label>
                <input v-model.number="form.days" type="number" min="1" max="15" required />
              </div>
            </div>
            <div class="form-item">
              <div class="input-icon">
                <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                  <line x1="12" y1="1" x2="12" y2="23"/>
                  <path d="M17 5H9.5a3.5 3.5 0 000 7h5a3.5 3.5 0 010 7H6"/>
                </svg>
              </div>
              <div class="input-content">
                <label class="form-label">人均预算（元）</label>
                <input v-model.number="form.budget" type="number" min="300" step="100" required />
              </div>
            </div>
            <div class="form-item">
              <div class="input-icon">
                <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                  <path d="M22 11.08V12a10 10 0 11-5.93-9.14"/>
                  <polyline points="22 4 12 14.01 9 11.01"/>
                </svg>
              </div>
              <div class="input-content">
                <label class="form-label">兴趣标签（可多选）</label>
                <div class="tags">
                  <button
                    v-for="tag in tags"
                    :key="tag"
                    type="button"
                    :class="['tag', { active: form.interests.includes(tag) }]"
                    @click="toggleTag(tag)"
                  >
                    {{ tag }}
                  </button>
                </div>
              </div>
            </div>
          </form>
        </div>

        <div class="card-image">
          <img src="https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=beautiful%20landscape%20with%20green%20mountains%20and%20white%20clouds%20blue%20sky%20nature%20scenery%20travel%20photography&image_size=portrait_4_3" alt="风景插画" />
        </div>
      </div>
    </div>

    <div class="footer-actions">
      <button class="primary-btn" @click="handleGenerate" :disabled="loading">
        <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
          <path d="M13 2L3 14h9l-1 8 10-12h-9l1-8z"/>
        </svg>
        <span>{{ loading ? 'AI 生成中...' : '生成AI旅行规划' }}</span>
      </button>
    </div>

    <div class="footer-note">
      <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" width="16" height="16">
        <path d="M12 20h9M16.5 3.5a2.121 2.121 0 013 3L7 19l-4 1 1-4L16.5 3.5z" stroke-linecap="round" stroke-linejoin="round"/>
      </svg>
      <span>生成行程后，可自由调整景点、更换酒店，并支持行程归档与PDF导出</span>
    </div>
  </div>
</template>

<script setup>
import { reactive, ref, onMounted } from 'vue'
import { showToast } from '../utils/toast';
import { useRouter, useRoute } from 'vue-router'
import { generateItineraryApi } from '../api/ai'

const router = useRouter()
const route = useRoute()
const loading = ref(false)

const tags = ['历史古迹', '自然风光', '美食', '购物', '亲子']
const form = reactive({
  fromCity: '',
  toCity: '',
  days: 3,
  budget: 2000,
  interests: ['自然风光']
})

// 编辑模式：从「我的行程」跳转过来时，通过 query 预填表单
onMounted(() => {
  if (route.query.fromCity) form.fromCity = route.query.fromCity
  if (route.query.toCity) form.toCity = route.query.toCity
  if (route.query.days) form.days = Number(route.query.days)
  if (route.query.budget) form.budget = Number(route.query.budget)
})

function toggleTag(tag) {
  const index = form.interests.indexOf(tag)
  if (index >= 0) {
    form.interests.splice(index, 1)
  } else {
    form.interests.push(tag)
  }
}

// 生成模拟行程数据（后端未部署时使用）
function generateMockItinerary() {
  const days = form.days || 3
  const timeline = []
  
  for (let i = 1; i <= days; i++) {
    timeline.push({
      day: i,
      morning: `第${i}天上午：游览${form.toCity}著名景点`,
      afternoon: `第${i}天下午：深度体验${form.toCity}文化风情`,
      dinner: `第${i}天晚餐：品尝${form.toCity}特色美食`,
      hotel: `${form.toCity}星级酒店住宿`
    })
  }
  
  return {
    id: Date.now().toString(),
    title: `${form.fromCity}到${form.toCity}${days}日游`,
    totalBudget: form.budget * days,
    fromCity: form.fromCity,
    toCity: form.toCity,
    days: days,
    interests: form.interests,
    timeline: timeline
  }
}

// 纯 API 消费：调用 AI 生成接口，成功则携带行程 ID 跳转结果页，失败则使用模拟数据
async function handleGenerate() {
  if (!form.interests.length) {
    showToast('请至少选择一个兴趣标签', 'warning')
    return
  }

  loading.value = true
  try {
    const res = await generateItineraryApi({ ...form })
    const itinerary = res?.data || res
    if (!itinerary?.id) {
      showToast('使用模拟数据生成行程', 'info')
      // 使用模拟数据
      const mockItinerary = generateMockItinerary()
      // 将模拟数据存储到 localStorage，供结果页使用
      localStorage.setItem('mockItinerary', JSON.stringify(mockItinerary))
      // 携带行程 ID 跳转结果页
      router.push(`/itinerary-result?id=${mockItinerary.id}`)
      return
    }
    // 携带行程 ID 跳转结果页，结果页通过 ID 从后端获取完整数据
    router.push(`/itinerary-result?id=${itinerary.id}`)
  } catch (e) {
    showToast('使用模拟数据生成行程', 'info')
    // 使用模拟数据
    const mockItinerary = generateMockItinerary()
    // 将模拟数据存储到 localStorage，供结果页使用
    localStorage.setItem('mockItinerary', JSON.stringify(mockItinerary))
    router.push(`/itinerary-result?id=${mockItinerary.id}`)
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.wizard-page {
  min-height: 100vh;
  padding: 20px;
  position: relative;
  overflow: hidden;
  background: url('https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=beautiful%20mountain%20landscape%20sunrise%20misty%20valley%20green%20hills%20rocky%20peaks%20nature%20photography&image_size=landscape_16_9') center/cover no-repeat;
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

.wizard-page::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: linear-gradient(135deg, rgba(45, 138, 110, 0.4) 0%, rgba(26, 90, 69, 0.55) 100%);
  z-index: 0;
}

.bg-decoration {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  pointer-events: none;
}

.cloud {
  position: absolute;
  background: rgba(255, 255, 255, 0.5);
  border-radius: 50%;
  filter: blur(8px);
}

.cloud::before,
.cloud::after {
  content: '';
  position: absolute;
  background: inherit;
  border-radius: 50%;
}

.cloud-1 {
  width: 120px;
  height: 40px;
  top: 15%;
  left: 10%;
  animation: cloudFloat 25s linear infinite;
}

.cloud-1::before {
  width: 50px;
  height: 50px;
  top: -25px;
  left: 15px;
}

.cloud-1::after {
  width: 40px;
  height: 40px;
  top: -20px;
  left: 55px;
}

.cloud-2 {
  width: 100px;
  height: 35px;
  top: 25%;
  right: 15%;
  animation: cloudFloat 30s linear infinite reverse;
}

.cloud-2::before {
  width: 45px;
  height: 45px;
  top: -22px;
  left: 10px;
}

.cloud-2::after {
  width: 35px;
  height: 35px;
  top: -18px;
  left: 45px;
}

.cloud-3 {
  width: 80px;
  height: 30px;
  top: 40%;
  left: 40%;
  animation: cloudFloat 35s linear infinite;
  animation-delay: -10s;
}

.cloud-3::before {
  width: 35px;
  height: 35px;
  top: -18px;
  left: 8px;
}

.cloud-3::after {
  width: 30px;
  height: 30px;
  top: -15px;
  left: 38px;
}

.cloud-4 {
  width: 90px;
  height: 32px;
  top: 60%;
  right: 30%;
  animation: cloudFloat 28s linear infinite reverse;
  animation-delay: -5s;
}

.cloud-4::before {
  width: 40px;
  height: 40px;
  top: -20px;
  left: 12px;
}

.cloud-4::after {
  width: 32px;
  height: 32px;
  top: -16px;
  left: 42px;
}

@keyframes cloudFloat {
  0% { transform: translateX(-100px); }
  100% { transform: translateX(100vw); }
}

.ink-wave {
  position: absolute;
  opacity: 0.8;
}

.ink-wave-1 {
  bottom: 0;
  left: -50%;
  width: 100%;
  height: 200px;
  animation: waveMove 20s ease-in-out infinite;
}

.ink-wave-2 {
  bottom: 0;
  left: -30%;
  width: 90%;
  height: 180px;
  animation: waveMove 25s ease-in-out infinite reverse;
}

@keyframes waveMove {
  0%, 100% { transform: translateX(0) scaleY(1); }
  50% { transform: translateX(50px) scaleY(1.1); }
}

.ink-mountain {
  position: absolute;
  opacity: 0.6;
}

.ink-mountain-1 {
  bottom: 0;
  left: -20px;
  width: 300px;
  height: 150px;
  animation: mountainPulse 8s ease-in-out infinite;
}

.ink-mountain-2 {
  bottom: 0;
  right: -10px;
  width: 250px;
  height: 120px;
  animation: mountainPulse 10s ease-in-out infinite reverse;
}

@keyframes mountainPulse {
  0%, 100% { opacity: 0.4; transform: scaleX(1); }
  50% { opacity: 0.6; transform: scaleX(1.05); }
}

.float-dot {
  position: absolute;
  background: rgba(197, 232, 219, 0.3);
  border-radius: 50%;
  animation: floatUp 15s ease-in-out infinite;
}

.float-dot-1 {
  width: 20px;
  height: 20px;
  bottom: 20%;
  left: 20%;
}

.float-dot-2 {
  width: 15px;
  height: 15px;
  bottom: 30%;
  right: 25%;
  animation-delay: -5s;
}

.float-dot-3 {
  width: 25px;
  height: 25px;
  bottom: 40%;
  left: 50%;
  animation-delay: -10s;
}

.float-dot-4 {
  width: 18px;
  height: 18px;
  bottom: 50%;
  right: 40%;
  animation-delay: -3s;
}

@keyframes floatUp {
  0%, 100% { transform: translateY(0) scale(1); opacity: 0.3; }
  50% { transform: translateY(-30px) scale(1.2); opacity: 0.5; }
}

.deco-luggage {
  position: absolute;
  bottom: 50px;
  left: 30px;
  width: 40px;
  height: 50px;
}

.deco-camera {
  position: absolute;
  bottom: 80px;
  right: 50px;
  width: 50px;
  height: 40px;
}

.deco-pin {
  position: absolute;
  top: 30%;
  right: 80px;
  width: 30px;
  height: 40px;
}

.page-header {
  max-width: 1200px;
  margin: 0 auto;
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  position: relative;
  z-index: 10;
  padding-top: 20px;
}

.header-content h1 {
  color: #fff;
  margin: 0;
  font-size: 28px;
}

.title-row {
  display: flex;
  align-items: center;
  gap: 10px;
}

.title-icon {
  width: 32px;
  height: 32px;
  color: #fff;
}

.subtitle {
  color: rgba(255, 255, 255, 0.8);
  font-size: 14px;
  margin: 8px 0 0;
}

.back-btn {
  border: none;
  border-radius: 10px;
  padding: 10px 14px;
  background: rgba(255, 255, 255, 0.2);
  color: #fff;
  cursor: pointer;
  border: 1px solid rgba(255, 255, 255, 0.3);
}

.main-content {
  max-width: 1200px;
  margin: 12px auto 0;
  position: relative;
  z-index: 10;
}

.wizard-card {
  background: rgba(255, 255, 255, 0.95);
  border-radius: 20px;
  border: 2px solid rgba(255, 255, 255, 0.3);
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.2);
  padding: 20px;
  display: flex;
  gap: 20px;
  max-height: calc(100vh - 140px);
  overflow-y: auto;
}

.card-title {
  font-size: 18px;
  color: #1a5a45;
  margin: 0 0 12px;
  padding-bottom: 8px;
  border-bottom: 2px solid #c5e8db;
}

.card-body {
  flex: 1;
}

.form-grid {
  display: flex;
  flex-direction: column;
  gap: 0;
}

.form-item {
  display: flex;
  align-items: flex-start;
  gap: 10px;
  margin-bottom: 10px;
  padding: 10px;
  background: rgba(232, 245, 240, 0.3);
  border-radius: 10px;
}

.input-icon {
  flex-shrink: 0;
  width: 40px;
  height: 40px;
  background: linear-gradient(135deg, #c5e8db, #a7dac4);
  border-radius: 10px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #1a5a45;
}

.input-icon svg {
  width: 20px;
  height: 20px;
}

.input-content {
  flex: 1;
}

.form-label {
  display: block;
  font-size: 14px;
  color: #2b3c35;
  margin-bottom: 6px;
}

input {
  width: 100%;
  border: 2px solid #c5e8db;
  border-radius: 8px;
  padding: 10px 12px;
  background: #fff;
  box-sizing: border-box;
  font-size: 14px;
}

input:focus {
  outline: none;
  border-color: #1a5a45;
}

input::placeholder {
  color: #9cb8ae;
}

.tags {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}

.tag {
  border: 1px solid #95c7b0;
  background: #fff;
  color: #1a5a45;
  border-radius: 999px;
  padding: 6px 12px;
  cursor: pointer;
  font-size: 13px;
}

.tag.active {
  border-color: #1a5a45;
  background: #1a5a45;
  color: #fff;
}

.card-image {
  flex-shrink: 0;
  width: 350px;
  height: 380px;
}

.card-image img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  border-radius: 16px;
  border: 3px solid rgba(255, 255, 255, 0.8);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
}

.footer-actions {
  max-width: 1200px;
  margin: 12px auto 0;
  display: flex;
  gap: 24px;
  position: relative;
  z-index: 10;
}

.primary-btn {
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 10px;
  border: none;
  border-radius: 12px;
  padding: 16px 32px;
  background: linear-gradient(135deg, #1a5a45, #2d7d5e);
  color: #fff;
  font-size: 16px;
  cursor: pointer;
  box-shadow: 0 6px 20px rgba(26, 90, 69, 0.3);
  transition: all 0.3s;
}

.primary-btn:hover:not(:disabled) {
  transform: translateY(-2px);
  box-shadow: 0 8px 24px rgba(26, 90, 69, 0.4);
}

.primary-btn:disabled {
  opacity: 0.7;
  cursor: not-allowed;
}

.primary-btn svg {
  width: 20px;
  height: 20px;
}



.loading {
  display: flex;
  gap: 6px;
  margin-top: 10px;
}

.loading span {
  width: 9px;
  height: 9px;
  border-radius: 50%;
  background: #1a5a45;
  animation: dot 1s infinite;
}

.loading span:nth-child(2) {
  animation-delay: 0.15s;
}

.loading span:nth-child(3) {
  animation-delay: 0.3s;
}

@keyframes dot {
  0%,
  100% { transform: translateY(0); opacity: 0.5; }
  50% { transform: translateY(-4px); opacity: 1; }
}

.footer-note {
  max-width: 700px;
  margin: 12px auto 0;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  color: rgba(255, 255, 255, 0.8);
  font-size: 13px;
  background: rgba(255, 255, 255, 0.15);
  padding: 10px 20px;
  border-radius: 20px;
  position: relative;
  z-index: 10;
}

@media (max-width: 900px) {
  .wizard-page {
    padding: 12px;
  }

  .wizard-card {
    flex-direction: column;
    max-height: calc(100vh - 180px);
  }

  .card-image {
    width: 100%;
    height: 180px;
  }

  .page-header {
    flex-direction: column;
    align-items: flex-start;
    gap: 12px;
  }

  .footer-actions {
    flex-direction: column;
  }

  .primary-btn {
    width: 100%;
  }
}
</style>
