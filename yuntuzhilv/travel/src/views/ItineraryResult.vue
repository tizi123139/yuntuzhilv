<template>
  <div class="result-page">
    <div class="bg-decoration"></div>

    <header class="result-header">
      <div>
        <h1>{{ itinerary.title || 'AI 定制行程' }}</h1>
        <p>目的地：{{ itinerary.destination }} · 共 {{ itinerary.days }} 天 · 预算 ¥{{ itinerary.totalBudget || 0 }}</p>
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
    <section class="timeline" v-else-if="itinerary.dayPlans?.length">
      <article class="day-card" v-for="day in itinerary.dayPlans" :key="day.dayNumber">
        <div class="day-number">第 {{ day.dayNumber }} 天</div>

        <div class="item-row" v-for="(item, itemIndex) in day.items" :key="itemIndex">
          <div class="slot-dot"></div>
          <div class="item-main">
            <div class="item-title">
              <span class="type-badge" :class="'type-' + getItemTypeLabel(item.itemName, item.startTime, item.endTime)">
                {{ getItemTypeLabel(item.itemName, item.startTime, item.endTime) }}
              </span>
            </div>
            <div class="item-content">
              <div class="item-name">{{ stripTypeName(item.itemName) }}</div>
              <div class="item-time" v-if="item.startTime || item.endTime">
                {{ item.startTime || '--' }} - {{ item.endTime || '--' }}
              </div>
            </div>
          </div>
          <div class="item-side">
            <div class="item-price" v-if="item.itemPrice">¥{{ item.itemPrice }}</div>
            <button
              class="swap-btn"
              :class="{ 'swap-btn-loading': swappingKey === `${day.dayNumber}-${itemIndex}` }"
              :disabled="swappingKey === `${day.dayNumber}-${itemIndex}`"
              @click="swapItem(day.dayNumber, itemIndex, item.itemName)"
            >
              <span v-if="swappingKey === `${day.dayNumber}-${itemIndex}`">更换中...</span>
              <span v-else>换一换</span>
            </button>
          </div>
        </div>
      </article>
    </section>
    <section v-else class="empty">暂无行程，请先生成行程。</section>

    <!-- 推荐模块：两排各3张卡片 -->
    <section class="recommend-section" v-if="hotelRecommendations.length || recommendAttractions.length">
      <div class="recommend-block" v-if="recommendAttractions.length">
        <h3 class="recommend-title">🏛 推荐景点</h3>
        <div class="recommend-grid recommend-grid-3">
          <div class="recommend-card" v-for="(attr, index) in recommendAttractions.slice(0, 3)" :key="'attr-' + index">
            <div class="card-image">
              <img v-if="attr.image" :src="attr.image" :alt="attr.name" />
              <span v-else class="card-image-placeholder">🏛</span>
            </div>
            <div class="card-body">
              <div class="card-header">
                <span class="card-name">{{ attr.name }}</span>
                <span class="card-price">{{ attr.price == 0 ? '免费' : '¥' + attr.price }}</span>
              </div>
              <div class="card-desc">{{ attr.desc }}</div>
            </div>
          </div>
        </div>
      </div>

      <div class="recommend-block" v-if="hotelRecommendations.length">
        <h3 class="recommend-title">🏨 推荐酒店</h3>
        <div class="recommend-grid recommend-grid-3">
          <div class="recommend-card hotel-card" v-for="(hotel, index) in hotelRecommendations.slice(0, 3)" :key="'hotel-' + index">
            <div class="card-image">
              <img v-if="hotel.image" :src="hotel.image" :alt="hotel.name" />
              <span v-else class="card-image-placeholder">🏨</span>
            </div>
            <div class="card-body">
              <div class="card-header">
                <span class="card-name">{{ hotel.name }}</span>
                <span class="card-price">¥{{ hotel.price }}/晚</span>
              </div>
              <div class="card-desc">{{ hotel.desc }}</div>
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
        <rect x="30" y="40" width="40" height="50" rx="5" fill="#1a5a45" opacity="0.6" />
        <rect x="40" y="25" width="20" height="20" rx="3" fill="#1a5a45" opacity="0.6" />
        <rect x="130" y="55" width="30" height="35" rx="4" fill="#1a5a45" opacity="0.5" />
        <rect x="135" y="40" width="20" height="20" rx="2" fill="#1a5a45" opacity="0.5" />
      </svg>
      <span class="ai-badge">AI生成，仅供参考</span>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { showToast } from '../utils/toast';
import { useRouter, useRoute } from 'vue-router'
import { getItineraryDetailApi, exportItineraryPdfApi, saveItineraryApi } from '../api/itinerary'
import { modifyItineraryApi } from '../api/ai'
import { attractionListApi } from '../api/attraction'
import { hotelListApi } from '../api/hotel'
const router = useRouter()
const route = useRoute()

// 根据 itemName 和开始时间返回类型标签
function getItemTypeLabel(itemName, startTime, endTime) {
  const name = (itemName || '').trim()
  
  // 先匹配 "关键词:名称" 格式（如 "交通:高铁"、"早餐:知味观"）
  const prefixMatch = name.match(/^(交通|早餐|午餐|晚餐|景点|住宿)[：:]/)
  if (prefixMatch) return prefixMatch[1]
  
  // 根据名称关键词判断
  if (/酒店|住宿|民宿|宾馆|客栈|旅馆/.test(name)) return '住宿'
  if (/高铁|火车|动车|飞机|航班|交通|巴士|地铁|出租车|公交/.test(name)) return '交通'
  if (/早餐|早茶|早饭/.test(name)) return '早餐'
  if (/午餐|午饭|中饭/.test(name)) return '午餐'
  if (/晚餐|晚饭|夜宵|宵夜/.test(name)) return '晚餐'
  if (/景点|景区|公园|博物馆|塔|楼|寺|庙|园|阁|故居|纪念馆|岛|湖|山/.test(name)) return '景点'

  // 根据时间判断
  if (!startTime) return '安排'
  const hour = parseInt(startTime.split(':')[0], 10)
  if (isNaN(hour)) return '安排'
  
  // 住宿：跨天或结束时间在次日
  if (endTime && /次日|第二天/.test(endTime)) return '住宿'
  
  if (hour >= 5 && hour < 9) return '早餐'
  if (hour >= 9 && hour < 12) return '上午'
  if (hour >= 12 && hour < 14) return '午餐'
  if (hour >= 14 && hour < 18) return '下午'
  if (hour >= 18 && hour < 22) return '晚餐'
  return '晚上'
}

// 从 itemName 中去除类型前缀用于显示
function stripTypeName(itemName) {
  if (!itemName) return ''
  return itemName.replace(/^(交通|早餐|午餐|晚餐|景点|住宿)[：:]\s*/, '').trim()
}
// 行程数据由后端 API 提供，通过 route query 中的 id 获取
const loading = ref(true)
const error = ref(false)
const itinerary = reactive({
  itineraryId: '',
  title: '',
  destination: '',
  startCity: '',
  totalBudget: 0,
  days: 0,
  totalCost: 0,
  interests: [],
  startDate: '',
  endDate: '',
  dayPlans: []
})
// 推荐数据
const hotelRecommendations = ref([])
const recommendAttractions = ref([])

// 加载推荐景点和酒店
async function loadRecommendations() {
  const invalidKeywords = ['酒店', '景点', '宾馆', '旅馆', '民宿']
  // 优先目的地，兜底起点城市
  const cities = [itinerary.destination, itinerary.startCity]
    .filter(c => c && !invalidKeywords.some(k => c.includes(k)))
  
  if (!cities.length) return

  for (const city of cities) {
    try {
      const [attrRes, hotelRes] = await Promise.all([
        attractionListApi({ city, pageNum: 1, pageSize: 3 }),
        hotelListApi({ city, pageNum: 1, pageSize: 3 })
      ])
      const attrData = attrRes?.records || attrRes?.list || (Array.isArray(attrRes) ? attrRes : [])
      const hotelData = hotelRes?.records || hotelRes?.list || (Array.isArray(hotelRes) ? hotelRes : [])
      const attrs = attrData.map(a => ({
        id: a.id,
        name: a.name,
        desc: a.description || a.address || '',
        price: Number(a.price) || 0,
        image: a.imgUrl || ''
      }))
      const hotels = hotelData.map(h => ({
        id: h.id,
        name: h.name,
        desc: h.description || h.facilities || h.address || '',
        price: Number(h.price) || 0,
        image: h.imgUrl || ''
      }))
      // 两个列表都有数据才使用，否则尝试下一个城市
      if (attrs.length || hotels.length) {
        recommendAttractions.value = attrs
        hotelRecommendations.value = hotels
        return
      }
    } catch (e) {
      console.warn(`加载 ${city} 推荐数据失败:`, e)
    }
  }
}

// 页面加载时从后端获取行程详情
onMounted(async () => {
  const id = route.query.id
  if (!id) {
    error.value = true
    loading.value = false
    return
  }

  try {
    const res = await getItineraryDetailApi(id)
    const data = res?.data || res
    Object.assign(itinerary, data)
    // 获取详情成功后，加载目的地的推荐景点和酒店
    await loadRecommendations()
  } catch (e) {
    console.warn('获取行程详情失败:', e)
    // 失败时使用模拟推荐数据兜底
    recommendAttractions.value = [
      { name: '黄鹤楼', desc: '江南三大名楼之一，登高俯瞰长江与武汉城区风光', price: 70, image: '' },
      { name: '东湖风景区', desc: '国内最大城中湖，适合骑行休闲', price: 0, image: '' },
      { name: '武汉博物馆', desc: '馆藏丰富，了解武汉历史文化', price: 0, image: '' }
    ]
    hotelRecommendations.value = [
      { name: '武汉光谷凯悦酒店', desc: '高端商务酒店，紧邻光谷商圈，配套泳池健身房', price: 688, image: '' },
      { name: '黄鹤楼观景民宿', desc: '开窗可眺望黄鹤楼，短途出行性价比高', price: 228, image: '' },
      { name: '东湖宾馆', desc: '坐拥东湖美景，环境清幽，适合休闲度假', price: 458, image: '' }
    ]
  } finally {
    loading.value = false
  }
})

// 保存行程
async function saveItinerary() {
  try {
    await saveItineraryApi(itinerary)
    showToast('行程已保存', 'success')
  } catch (e) {
    showToast('保存失败，请重试', 'error')
  }
}
// 下载Blob
function downloadBlob(blob, fileName) {
  const url = URL.createObjectURL(blob)
  const a = document.createElement('a')
  a.href = url
  a.download = fileName
  a.click()
  URL.revokeObjectURL(url)
}
// 导出PDF（request.js 拦截器已自动处理文件下载）
async function exportPdf() {
  try {
    const id = itinerary.itineraryId || route.query.id
    await exportItineraryPdfApi(id)
    showToast('PDF 导出成功', 'success')
  } catch (e) {
    showToast('导出 PDF 失败，请重试', 'error')
  }
}

// 换一换功能
const swappingKey = ref('')

async function swapItem(dayNumber, itemIndex, itemName) {
  const key = `${dayNumber}-${itemIndex}`
  if (swappingKey.value === key) return

  swappingKey.value = key
  try {
    const id = itinerary.itineraryId || route.query.id
    const displayName = stripTypeName(itemName)
    const typeLabel = getItemTypeLabel(itemName)
    const modifications = `请替换第${dayNumber}天的"${displayName}"（${typeLabel}）项目，提供一个新的、不同的替代方案，保持行程其他部分不变。`
    const res = await modifyItineraryApi(id, { modifications })
    const data = res?.data || res
    if (data && data.dayPlans) {
      itinerary.dayPlans = data.dayPlans
      if (data.totalCost !== undefined) {
        itinerary.totalCost = data.totalCost
      }
      if (data.title) {
        itinerary.title = data.title
      }
      showToast('已为您更换项目', 'success')
    }
  } catch (e) {
    console.error('换一换失败:', e)
    showToast('更换失败，请重试', 'error')
  } finally {
    swappingKey.value = ''
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
  align-items: flex-start;
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
  margin-top: 6px;
}
.item-main {
  flex: 1;
  min-width: 0;
}
.item-title {
  color: #4d6760;
  font-weight: 600;
  font-size: 14px;
  margin-bottom: 4px;
}
.type-badge {
  display: inline-block;
  padding: 2px 10px;
  border-radius: 12px;
  font-size: 12px;
  font-weight: 600;
  line-height: 1.4;
}
.type-住宿 { background: #fef3c7; color: #92400e; }
.type-交通 { background: #dbeafe; color: #1e40af; }
.type-早餐 { background: #fce7f3; color: #9d174d; }
.type-午餐 { background: #d1fae5; color: #065f46; }
.type-晚餐 { background: #ede9fe; color: #5b21b6; }
.type-景点 { background: #dcfce7; color: #166534; }
.type-上午 { background: #e0f2fe; color: #075985; }
.type-下午 { background: #fef9c3; color: #854d0e; }
.type-晚上 { background: #e9d5ff; color: #4c1d95; }
.type-安排 { background: #f1f5f9; color: #475569; }
.item-content {
  color: #1d302a;
  font-size: 14px;
}
.item-name {
  font-weight: 600;
  color: #184d3b;
  font-size: 15px;
}
.item-time {
  margin-top: 4px;
  font-size: 12px;
  color: #8a9b93;
}
.item-side {
  display: flex;
  flex-direction: column;
  align-items: flex-end;
  gap: 6px;
  flex-shrink: 0;
  padding-left: 8px;
}
.item-price {
  font-weight: 700;
  color: #d97706;
  font-size: 15px;
  white-space: nowrap;
}
.swap-btn {
  border: 1px solid #c5e8db;
  border-radius: 8px;
  padding: 5px 12px;
  background: #e8f5ef;
  color: #1a5a45;
  cursor: pointer;
  font-size: 12px;
  font-weight: 500;
  flex-shrink: 0;
  transition: all 0.2s;
  white-space: nowrap;
}
.swap-btn:hover:not(:disabled) {
  background: #1a5a45;
  color: #fff;
  border-color: #1a5a45;
}
.swap-btn:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}
.swap-btn-loading {
  background: #f5f5f5 !important;
  color: #999 !important;
  border-color: #ddd !important;
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
  border-radius: 14;
  padding: 22px;
  color: #62756e;
  position: relative;
  z-index: 10;
}

/* 推荐模块 */
.recommend-section {
  max-width: 1200px;
  margin: 30px auto 60px;
  display: flex;
  flex-direction: column;
  gap: 24px;
  position: relative;
  z-index: 10;
}

.recommend-block {
  background: #fff;
  border-radius: 16px;
  padding: 20px;
  box-shadow: 0 4px 20px rgba(16, 75, 52, 0.08);
}

.recommend-title {
  font-size: 18px;
  font-weight: 700;
  color: #1a5a45;
  margin: 0 0 16px;
  padding-bottom: 12px;
  border-bottom: 2px solid #c5e8db;
}

.recommend-grid {
  display: grid;
  gap: 16px;
}

.recommend-grid-3 {
  grid-template-columns: repeat(3, 1fr);
}

.recommend-card {
  background: linear-gradient(135deg, #f0f9f4 0%, #e8f5ef 100%);
  border-radius: 12px;
  overflow: hidden;
  border: 1px solid #c5e8db;
  transition: transform 0.2s, box-shadow 0.2s;
  display: flex;
  flex-direction: column;
}

.recommend-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 6px 20px rgba(26, 90, 0.15);
}

.recommend-card.hotel-card {
  background: linear-gradient(135deg, #fef7f0 0%, #fdf2e9 100%);
  border-color: #e8d5c4;
}

.card-image {
  width: 100%;
  height: 140px;
  background: linear-gradient(135deg, #c5e8db 0%, #a8d5c2 100%);
  display: flex;
  align-items: center;
  justify-content: center;
  overflow: hidden;
  flex-shrink: 0;
}

.card-image img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.card-image-placeholder {
  font-size: 48px;
}

.hotel-card .card-image {
  background: linear-gradient(135deg, #f5dcc4 0%, #e8c9a8 100%);
}

.card-body {
  padding: 14px 16px 16px;
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.card-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.card-name {
  font-size: 15px;
  font-weight: 600;
  color: #1d302a;
}

.card-price {
  font-size: 14px;
  font-weight: 600;
  color: #1a5a45;
}

.hotel-card .card-price {
  color: #c47a3a;
}

.card-desc {
  font-size: 13px;
  color: #4d6760;
  line-height: 1.5;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.hotel-card .card-desc {
  color: #6b5844;
}

@media (max-width: 768px) {
  .result-page {
    padding: 16px;
  }
  .timeline {
    flex-direction: column;
  }
  .actions {
    flex-direction: column;
  }
  .footer-decoration {
    display: none;
  }
  .recommend-grid-3 {
    grid-template-columns: repeat(2, 1fr);
  }
}

@media (max-width: 480px) {
  .recommend-grid-3 {
    grid-template-columns: 1fr;
  }
}
</style>