<template>
  <div class="result-page">
    <div class="bg-decoration"></div>
    
    <header class="result-header">
      <div>
        <h1>{{ itinerary.title || 'AI 定制行程' }}</h1>
        <p>总预算：¥{{ itinerary.totalBudget || 0 }}</p>
      </div>
      <button class="back-btn" @click="router.push('/create-itinerary')">重新定制</button>
    </header>

    <section class="timeline" v-if="loading" style="text-align:center;padding:60px 0;color:#62756e;">
      加载行程中...
    </section>

    <section class="timeline" v-else-if="error" style="text-align:center;padding:60px 0;color:#62756e;">
      <p>行程加载失败</p>
      <button class="back-btn" @click="router.push('/home')">返回首页</button>
    </section>

    <section class="timeline" v-else-if="itinerary.timeline?.length">
      <article class="day-card" v-for="day in itinerary.timeline" :key="day.day">
        <div class="day-number">第 {{ day.day }} 天</div>
        
        <div class="item-row" v-for="slot in slots" :key="slot.key">
          <div class="slot-dot"></div>
          <div class="item-title">{{ slot.label }}</div>
          <div class="item-content">{{ day[slot.key] }}</div>
          <button class="swap-btn" @click="swapPlan(day.day, slot.key)">换一换</button>
        </div>
      </article>
    </section>

    <section v-else class="empty">暂无行程，请先生成行程。</section>

    <section class="recommendations" v-if="itinerary.timeline?.length">
      <div class="recommend-section">
        <h3 class="section-title">🏨 酒店推荐</h3>
        <div class="recommend-list">
          <div class="recommend-item" v-for="(hotel, index) in hotelRecommendations" :key="index">
            <img :src="hotel.image" :alt="hotel.name" class="recommend-img" />
            <div class="recommend-info">
              <h4>{{ hotel.name }}</h4>
              <p class="recommend-desc">{{ hotel.desc }}</p>
              <p class="recommend-price">¥{{ hotel.price }}起/晚</p>
            </div>
          </div>
        </div>
      </div>

      <div class="recommend-section">
        <h3 class="section-title">🍽️ 美食推荐</h3>
        <div class="recommend-list">
          <div class="recommend-item" v-for="(food, index) in foodRecommendations" :key="index">
            <img :src="food.image" :alt="food.name" class="recommend-img" />
            <div class="recommend-info">
              <h4>{{ food.name }}</h4>
              <p class="recommend-desc">{{ food.desc }}</p>
              <p class="recommend-price">人均 ¥{{ food.price }}</p>
            </div>
          </div>
        </div>
      </div>
    </section>

    <footer class="actions">
      <button class="action-btn" @click="router.push('/create-itinerary')">重新定制</button>
      <button class="action-btn" @click="saveItinerary">保存行程</button>
      <button class="action-btn" @click="exportPdf">导出 PDF</button>
    </footer>

    <div class="footer-decoration">
      <svg viewBox="0 0 200 100" class="luggage-icon">
        <rect x="30" y="40" width="40" height="50" rx="5" fill="#1a5a45" opacity="0.6"/>
        <rect x="40" y="25" width="20" height="20" rx="3" fill="#1a5a45" opacity="0.6"/>
        <rect x="130" y="55" width="30" height="35" rx="4" fill="#1a5a45" opacity="0.5"/>
        <rect x="135" y="40" width="20" height="20" rx="2" fill="#1a5a45" opacity="0.5"/>
      </svg>
      <span class="ai-badge">AI生成，仅供参考</span>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { showToast } from '../utils/toast';
import { useRouter, useRoute } from 'vue-router'
import { modifyItineraryApi } from '../api/ai'
import { getItineraryDetailApi, exportItineraryPdfApi, saveItineraryApi } from '../api/itinerary'

const router = useRouter()
const route = useRoute()

// 行程数据由后端 API 提供，通过 route query 中的 id 获取
const loading = ref(true)
const error = ref(false)
const itinerary = reactive({
  id: '',
  title: '',
  totalBudget: 0,
  fromCity: '',
  toCity: '',
  days: 0,
  timeline: []
})

// 酒店和美食推荐数据由后端 API 提供
const hotelRecommendations = ref([])
const foodRecommendations = ref([])

const slots = [
  { key: 'morning', label: '上午' },
  { key: 'afternoon', label: '下午' },
  { key: 'dinner', label: '晚餐' },
  { key: 'hotel', label: '住宿' }
]

// 生成模拟推荐数据
function generateMockRecommendations() {
  return {
    hotels: [
      { name: `${itinerary.toCity}五星豪华酒店`, desc: '位于市中心，设施齐全，服务一流', price: 888, image: 'https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=luxury%20hotel%20room%20modern%20interior%20elegant%20design&image_size=square_hd' },
      { name: `${itinerary.toCity}商务精选酒店`, desc: '交通便利，性价比高，适合商务出行', price: 458, image: 'https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=business%20hotel%20comfortable%20room%20clean%20modern&image_size=square_hd' },
      { name: `${itinerary.toCity}特色民宿`, desc: '独具风情，体验当地文化与生活', price: 288, image: 'https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=traditional%20chinese%20style%20guesthouse%20cozy%20warm&image_size=square_hd' }
    ],
    foods: [
      { name: `${itinerary.toCity}特色餐厅`, desc: '品尝正宗当地美食，回味无穷', price: 188, image: 'https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=delicious%20chinese%20cuisine%20traditional%20dishes%20food%20photography&image_size=square_hd' },
      { name: `${itinerary.toCity}小吃街`, desc: '地道小吃聚集地，价格实惠种类多', price: 68, image: 'https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=chinese%20street%20food%20market%20colorful%20snacks&image_size=square_hd' },
      { name: `${itinerary.toCity}私房菜馆`, desc: '环境雅致，菜品精致，值得一试', price: 268, image: 'https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=elegant%20chinese%20restaurant%20fine%20dining%20atmosphere&image_size=square_hd' }
    ]
  }
}

// 页面加载时从后端获取行程详情（包含推荐数据）
onMounted(async () => {
  const id = route.query.id
  if (!id) {
    error.value = true
    loading.value = false
    return
  }
  
  // 尝试从 localStorage 获取模拟数据
  const mockItinerary = localStorage.getItem('mockItinerary')
  if (mockItinerary) {
    try {
      const data = JSON.parse(mockItinerary)
      if (data.id === id) {
        Object.assign(itinerary, data)
        const recommendations = generateMockRecommendations()
        hotelRecommendations.value = recommendations.hotels
        foodRecommendations.value = recommendations.foods
        localStorage.removeItem('mockItinerary')
        loading.value = false
        return
      }
    } catch (e) {
      // 解析失败，继续尝试后端API
    }
  }
  
  // 从后端获取行程详情
  try {
    const res = await getItineraryDetailApi(id)
    const data = res?.data || res
    Object.assign(itinerary, data)
    hotelRecommendations.value = data.hotelRecommendations || []
    foodRecommendations.value = data.foodRecommendations || []
  } catch (e) {
    // 后端请求失败，使用模拟推荐数据
    showToast('使用模拟推荐数据', 'info')
    const recommendations = generateMockRecommendations()
    hotelRecommendations.value = recommendations.hotels
    foodRecommendations.value = recommendations.foods
  } finally {
    loading.value = false
  }
})

// "换一换"：调用 AI 修改接口替换某个时段的内容，失败时提示错误并保持原内容
async function swapPlan(dayIndex, slotKey) {
  const day = itinerary.timeline.find(item => item.day === dayIndex)
  if (!day) return

  try {
    const res = await modifyItineraryApi({
      itineraryId: itinerary.id,
      day: dayIndex,
      slot: slotKey,
      current: day[slotKey]
    })
    const newContent = res?.data?.content || res?.content
    if (newContent) {
      day[slotKey] = newContent
    } else {
      showToast('未获取到替换内容，请重试', 'error')
    }
  } catch (e) {
    showToast('换一换失败，请检查网络后重试', 'error')
  }
}

// 保存行程到后端
async function saveItinerary() {
  try {
    await saveItineraryApi(itinerary)
    showToast('行程已保存', 'success')
  } catch (e) {
    showToast('保存失败，请重试', 'error')
  }
}

// 下载 Blob 文件的工具函数
function downloadBlob(blob, fileName) {
  const url = URL.createObjectURL(blob)
  const a = document.createElement('a')
  a.href = url
  a.download = fileName
  a.click()
  URL.revokeObjectURL(url)
}

// 导出 PDF：调用后端导出接口获取 PDF 文件流，失败时提示错误
async function exportPdf() {
  try {
    const blob = await exportItineraryPdfApi(itinerary.id)
    if (blob instanceof Blob) {
      downloadBlob(blob, `${itinerary.title || '行程'}.pdf`)
    } else {
      showToast('导出失败：未获取到有效文件', 'error')
    }
  } catch (e) {
    showToast('导出 PDF 失败，请重试', 'error')
  }
}
</script>

<style scoped>
.result-page {
  min-height: 100vh;
  background: linear-gradient(180deg, #f0f9f4 0%, #e6f5ec 100%);
  padding: 24px;
  position: relative;
  overflow: hidden;
}

.bg-decoration {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  pointer-events: none;
  background-image: 
    radial-gradient(circle at 20% 30%, rgba(26, 90, 69, 0.05) 0%, transparent 50%),
    radial-gradient(circle at 80% 70%, rgba(26, 90, 69, 0.05) 0%, transparent 50%);
}

.bg-decoration::before {
  content: '';
  position: absolute;
  top: 10%;
  left: 10%;
  right: 10%;
  height: 40px;
  background: url("data:image/svg+xml,%3Csvg xmlns='http://www.w3.org/2000/svg' width='100' height='20' viewBox='0 0 100 20'%3E%3Cpath d='M0 10 Q25 5 50 10 T100 10' stroke='%231a5a45' stroke-width='1' fill='none' opacity='0.1'/%3E%3Ccircle cx='50' cy='10' r='3' fill='%231a5a45' opacity='0.1'/%3E%3Ccircle cx='25' cy='7' r='2' fill='%231a5a45' opacity='0.1'/%3E%3Ccircle cx='75' cy='7' r='2' fill='%231a5a45' opacity='0.1'/%3E%3C/svg%3E") repeat-x;
  background-size: 100px 20px;
}

.result-header {
  max-width: 1200px;
  margin: 0 auto;
  display: flex;
  justify-content: space-between;
  align-items: center;
  color: #184d3b;
  position: relative;
  z-index: 10;
}

.result-header h1 {
  font-size: 28px;
  margin: 0;
  font-weight: 700;
}

.result-header p {
  margin: 4px 0 0;
  font-size: 14px;
  color: #4d6760;
}

.back-btn {
  border: none;
  border-radius: 999px;
  background: #1a5a45;
  color: #fff;
  padding: 10px 20px;
  cursor: pointer;
  font-size: 14px;
  font-weight: 500;
}

.timeline {
  max-width: 1200px;
  margin: 30px auto 0;
  display: flex;
  gap: 20px;
  position: relative;
  z-index: 10;
}

.day-card {
  flex: 1;
  background: #fff;
  border-radius: 16px;
  padding: 20px;
  box-shadow: 0 4px 20px rgba(16, 75, 52, 0.08);
  position: relative;
}

.day-card::before {
  content: '';
  position: absolute;
  left: 20px;
  top: 60px;
  bottom: 20px;
  width: 2px;
  background: linear-gradient(180deg, #c5e8db 0%, #e8f5ef 100%);
}

.day-number {
  font-size: 18px;
  font-weight: 700;
  color: #1a5a45;
  margin-bottom: 16px;
  padding-bottom: 12px;
  border-bottom: 2px solid #c5e8db;
}

.item-row {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 12px 0;
  position: relative;
}

.slot-dot {
  width: 10px;
  height: 10px;
  border-radius: 50%;
  background: #1a5a45;
  flex-shrink: 0;
  position: relative;
  z-index: 1;
}

.item-title {
  color: #4d6760;
  font-weight: 600;
  width: 50px;
  flex-shrink: 0;
  font-size: 14px;
}

.item-content {
  flex: 1;
  color: #1d302a;
  font-size: 14px;
}

.swap-btn {
  border: none;
  border-radius: 8px;
  padding: 6px 10px;
  background: #e8f5ef;
  color: #1a5a45;
  cursor: pointer;
  font-size: 12px;
  flex-shrink: 0;
}

.recommendations {
  max-width: 1200px;
  margin: 30px auto 0;
  position: relative;
  z-index: 10;
}

.recommend-section {
  margin-bottom: 24px;
}

.section-title {
  font-size: 18px;
  font-weight: 700;
  color: #1a5a45;
  margin: 0 0 16px;
}

.recommend-list {
  display: flex;
  gap: 16px;
}

.recommend-item {
  flex: 1;
  background: #fff;
  border-radius: 12px;
  overflow: hidden;
  box-shadow: 0 4px 12px rgba(16, 75, 52, 0.06);
  display: flex;
  flex-direction: column;
}

.recommend-img {
  width: 100%;
  height: 120px;
  object-fit: cover;
}

.recommend-info {
  padding: 12px;
}

.recommend-info h4 {
  margin: 0 0 4px;
  font-size: 15px;
  color: #1a5a45;
}

.recommend-desc {
  margin: 0 0 6px;
  font-size: 12px;
  color: #62756e;
  line-height: 1.4;
}

.recommend-price {
  margin: 0;
  font-size: 13px;
  color: #1a5a45;
  font-weight: 600;
}

.actions {
  max-width: 1200px;
  margin: 30px auto 0;
  display: flex;
  gap: 16px;
  position: relative;
  z-index: 10;
}

.action-btn {
  flex: 1;
  border: none;
  border-radius: 12px;
  padding: 14px 20px;
  background: #1a5a45;
  color: #fff;
  cursor: pointer;
  font-size: 15px;
  font-weight: 500;
}

.footer-decoration {
  position: absolute;
  right: 30px;
  bottom: 30px;
  display: flex;
  align-items: flex-end;
  gap: 10px;
  pointer-events: none;
  opacity: 0.5;
}

.luggage-icon {
  width: 100px;
  height: 50px;
}

.ai-badge {
  font-size: 12px;
  color: #1a5a45;
  margin-bottom: 5px;
}

.empty {
  max-width: 980px;
  margin: 20px auto;
  background: #fff;
  border-radius: 14px;
  padding: 22px;
  color: #62756e;
  position: relative;
  z-index: 10;
}

@media (max-width: 768px) {
  .result-page {
    padding: 16px;
  }

  .timeline {
    flex-direction: column;
  }

  .recommend-list {
    flex-direction: column;
  }

  .actions {
    flex-direction: column;
  }

  .footer-decoration {
    display: none;
  }
}
</style>