<template>
  <div class="admin-page">
    <div class="page-header">
      <h2>数据统计</h2>
    </div>

    <div class="stats-grid">
      <!-- 热门景点排行 -->
      <div class="stats-card">
        <h3>热门景点排行</h3>
        <div class="chart-area">
          <div v-if="hotAttractions.length" class="bar-chart">
            <div class="bar-item" v-for="(item, idx) in hotAttractions" :key="idx">
              <span class="bar-label">{{ item.name }}</span>
              <div class="bar-track">
                <div class="bar-fill" :style="{ width: barWidth(item.heat || item.count || 0, maxAttrHeat) }">
                  <span class="bar-value">{{ item.heat || item.count }}</span>
                </div>
              </div>
            </div>
          </div>
          <div v-else class="empty-chart">暂无数据</div>
        </div>
      </div>

      <!-- 热门城市排行 -->
      <div class="stats-card">
        <h3>热门目的地城市</h3>
        <div class="chart-area">
          <div v-if="hotCities.length" class="city-rank">
            <div class="rank-item" v-for="(city, index) in hotCities" :key="city.name">
              <span :class="['rank-no', { top: index < 3 }]">{{ index + 1 }}</span>
              <span class="rank-name">{{ city.name }}</span>
              <div class="rank-bar-wrap">
                <div class="rank-bar" :style="{ width: barWidth(city.count, maxCityCount) }"></div>
              </div>
              <span class="rank-value">{{ city.count }}</span>
            </div>
          </div>
          <div v-else class="empty-chart">暂无数据</div>
        </div>
      </div>
    </div>

    <!-- 热门城市可视化分析 -->
    <div class="analysis-section">
      <div class="section-header">
        <h3>热门城市可视化分析</h3>
        <!-- 城市选择器 -->
        <div class="city-selector">
          <label>选择城市：</label>
          <select v-model="selectedCity" class="city-select">
            <option v-for="city in availableCities" :key="city" :value="city">{{ city }}</option>
          </select>
        </div>
      </div>
      
      <div class="analysis-grid">
        <!-- 酒店选择占比3D饼状图 -->
        <div class="analysis-card">
          <h4>{{ selectedCity }}酒店选择占比</h4>
          <div v-if="hotelSelectionData.length" class="pie-chart-container-3d">
            <div class="pie-3d">
              <svg viewBox="0 0 200 200" class="pie-chart-3d">
                <g v-for="(item, index) in hotelPieData" :key="item.name" class="pie-slice-group">
                  <circle
                    :cx="100"
                    :cy="100"
                    r="65"
                    fill="none"
                    :stroke="item.color"
                    stroke-width="38"
                    :stroke-dasharray="item.dashArray"
                    :stroke-dashoffset="item.dashOffset"
                    :transform="`rotate(-90 100 100)`"
                    class="pie-segment-3d"
                  />
                  <!-- 3D效果层 -->
                  <circle
                    :cx="100"
                    :cy="100"
                    r="65"
                    fill="none"
                    :stroke="item.highlightColor"
                    stroke-width="38"
                    :stroke-dasharray="item.dashArray"
                    :stroke-dashoffset="item.dashOffset"
                    :transform="`rotate(-90 100 100)`"
                    class="pie-segment-3d-highlight"
                  />
                </g>
              </svg>
              <!-- 3D阴影效果 -->
              <div class="pie-shadow"></div>
            </div>
            <div class="pie-center-3d">
              <span class="pie-total-3d">{{ hotelTotal }}%</span>
              <span class="pie-label-3d">酒店占比</span>
            </div>
          </div>
          <div v-else class="empty-chart">暂无数据</div>
          <div v-if="hotelSelectionData.length" class="pie-legend">
            <div v-for="item in hotelPieData" :key="item.name" class="legend-item">
              <span class="legend-color" :style="{ background: item.color }"></span>
              <span class="legend-name">{{ item.name }}</span>
              <span class="legend-value">{{ item.value }}%</span>
            </div>
          </div>
        </div>

        <!-- 景点选择占比3D饼状图 -->
        <div class="analysis-card">
          <h4>{{ selectedCity }}景点选择占比</h4>
          <div v-if="attractionSelectionData.length" class="pie-chart-container-3d">
            <div class="pie-3d">
              <svg viewBox="0 0 200 200" class="pie-chart-3d">
                <g v-for="(item, index) in attractionPieData" :key="item.name" class="pie-slice-group">
                  <circle
                    :cx="100"
                    :cy="100"
                    r="65"
                    fill="none"
                    :stroke="item.color"
                    stroke-width="38"
                    :stroke-dasharray="item.dashArray"
                    :stroke-dashoffset="item.dashOffset"
                    :transform="`rotate(-90 100 100)`"
                    class="pie-segment-3d"
                  />
                  <!-- 3D效果层 -->
                  <circle
                    :cx="100"
                    :cy="100"
                    r="65"
                    fill="none"
                    :stroke="item.highlightColor"
                    stroke-width="38"
                    :stroke-dasharray="item.dashArray"
                    :stroke-dashoffset="item.dashOffset"
                    :transform="`rotate(-90 100 100)`"
                    class="pie-segment-3d-highlight"
                  />
                </g>
              </svg>
              <!-- 3D阴影效果 -->
              <div class="pie-shadow"></div>
            </div>
            <div class="pie-center-3d">
              <span class="pie-total-3d">{{ attractionTotal }}%</span>
              <span class="pie-label-3d">景点占比</span>
            </div>
          </div>
          <div v-else class="empty-chart">暂无数据</div>
          <div v-if="attractionSelectionData.length" class="pie-legend">
            <div v-for="item in attractionPieData" :key="item.name" class="legend-item">
              <span class="legend-color" :style="{ background: item.color }"></span>
              <span class="legend-name">{{ item.name }}</span>
              <span class="legend-value">{{ item.value }}%</span>
            </div>
          </div>
        </div>

        <!-- 热门城市访问量趋势图（五条折线） -->
        <div class="analysis-card full-width">
          <h4>热门城市访问量趋势</h4>
          <div v-if="trendData.cities.length" class="line-chart-container">
            <svg viewBox="0 0 800 250" class="line-chart-multi">
              <!-- 网格线 -->
              <line v-for="i in 5" :key="'grid-' + i" x1="60" :y1="i * 45" x2="780" :y2="i * 45" stroke="#e8f2ec" stroke-width="1" />
              <!-- Y轴标签 -->
              <text v-for="i in 5" :key="'y-label-' + i" x="50" :y="i * 45 + 5" text-anchor="end" fill="#999" font-size="12">{{ (5 - i) * 25 }}%</text>
              <!-- X轴标签 -->
              <text v-for="(month, index) in trendMonths" :key="'x-label-' + month" :x="getXPosition(index)" y="240" text-anchor="middle" fill="#999" font-size="12">{{ month }}</text>
              <!-- 折线和数据点 -->
              <g v-for="(city, cityIndex) in topCities" :key="'city-' + city.name">
                <polyline 
                  :points="getTrendLinePoints(city.name)" 
                  fill="none" 
                  :stroke="city.color" 
                  stroke-width="2.5" 
                  stroke-linecap="round" 
                  stroke-linejoin="round" 
                />
                <circle 
                  v-for="(point, index) in getTrendPoints(city.name)" 
                  :key="'point-' + city.name + '-' + index" 
                  :cx="point.x" 
                  :cy="point.y" 
                  r="5" 
                  fill="#fff" 
                  :stroke="city.color" 
                  stroke-width="2" 
                />
                <circle 
                  v-for="(point, index) in getTrendPoints(city.name)" 
                  :key="'point-inner-' + city.name + '-' + index" 
                  :cx="point.x" 
                  :cy="point.y" 
                  r="2.5" 
                  :fill="city.color" 
                />
              </g>
              <!-- 图例 -->
              <g class="line-chart-legend">
                <rect v-for="(city, index) in topCities" :key="'legend-rect-' + city.name" :x="getLegendX(index)" y="10" width="12" height="12" :fill="city.color" rx="2" />
                <text v-for="(city, index) in topCities" :key="'legend-text-' + city.name" :x="getLegendX(index) + 18" y="20" fill="#666" font-size="12">{{ city.name }}</text>
              </g>
            </svg>
          </div>
          <div v-else class="empty-chart">暂无数据</div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
/**
 * 数据统计页面（后台）
 *
 * 【架构说明】
 * 本页面数据均来自后端接口。
 * 若后端未部署或请求失败，图表区域显示"暂无数据"。
 *
 * 接口定义见 src/api/admin.js，包含完整的请求参数和响应格式注释。
 */
import { computed, onMounted, ref, watch } from 'vue'
import { 
  hotAttractionStatsApi, 
  hotCityStatsApi,
  hotelSelectionRatioApi,
  attractionSelectionRatioApi,
  cityTrendApi
} from '../../api/admin'

// ===== 统计数据（由后端接口返回） =====
const hotAttractions = ref([])  // 热门景点排行
const hotCities = ref([])       // 热门城市排行

// 计算柱状图最大值（用于百分比宽度计算）
const maxAttrHeat = computed(() => Math.max(...hotAttractions.value.map(i => i.heat || i.count || 0), 1))
const maxCityCount = computed(() => Math.max(...hotCities.value.map(i => i.count || 0), 1))

/**
 * 计算柱状图宽度百分比
 */
function barWidth(value, max) {
  return Math.max((value / max) * 100, 5) + '%'
}

// ===== 可视化分析数据 =====

// 可选城市列表（从后端热门城市排行获取）
const availableCities = computed(() => {
  return hotCities.value.slice(0, 5).map(city => city.name)
})

// 选中的城市
const selectedCity = ref('')

// 酒店选择占比数据（由后端接口返回）
const hotelSelectionData = ref([])

// 景点选择占比数据（由后端接口返回）
const attractionSelectionData = ref([])

// 趋势数据（由后端接口返回）
const trendData = ref({ months: [], cities: [] })

// 城市颜色配置
const cityColors = {
  '北京': '#51b891',
  '上海': '#f5a623',
  '杭州': '#9b59b6',
  '成都': '#3498db',
  '西安': '#e74c3c'
}

// 热门城市（用于趋势图，从后端趋势数据获取）
const topCities = computed(() => {
  return trendData.value.cities.map(city => ({
    name: city.name,
    color: cityColors[city.name] || '#2d8a6e'
  }))
})

// 趋势月份（从后端数据获取）
const trendMonths = computed(() => {
  return trendData.value.months.length > 0 ? trendData.value.months : []
})

// 酒店颜色配置
const hotelColors = ['#51b891', '#7ed6bc', '#a8e6cf', '#d5f5eb']

// 酒店选择占比饼状图数据（使用后端返回的数据）
const hotelPieData = computed(() => {
  const data = hotelSelectionData.value
  const total = data.reduce((sum, item) => sum + item.value, 0) || 1
  const circumference = 2 * Math.PI * 65
  let offset = 0
  return data.map((item, index) => {
    const percent = item.value / total
    const dashArray = `${percent * circumference} ${circumference}`
    const dashOffset = -offset
    offset += percent * circumference
    return {
      ...item,
      color: hotelColors[index % hotelColors.length],
      highlightColor: adjustColorBrightness(hotelColors[index % hotelColors.length], 20),
      dashArray,
      dashOffset
    }
  })
})

const hotelTotal = computed(() => {
  return hotelSelectionData.value.reduce((sum, item) => sum + item.value, 0)
})

// 景点颜色配置
const attractionColors = ['#f5a623', '#ffc876', '#ffe0b2', '#fff3e0']

// 景点选择占比饼状图数据（使用后端返回的数据）
const attractionPieData = computed(() => {
  const data = attractionSelectionData.value
  const total = data.reduce((sum, item) => sum + item.value, 0) || 1
  const circumference = 2 * Math.PI * 65
  let offset = 0
  return data.map((item, index) => {
    const percent = item.value / total
    const dashArray = `${percent * circumference} ${circumference}`
    const dashOffset = -offset
    offset += percent * circumference
    return {
      ...item,
      color: attractionColors[index % attractionColors.length],
      highlightColor: adjustColorBrightness(attractionColors[index % attractionColors.length], 20),
      dashArray,
      dashOffset
    }
  })
})

const attractionTotal = computed(() => {
  return attractionSelectionData.value.reduce((sum, item) => sum + item.value, 0)
})

// 获取趋势点坐标（使用后端返回的数据）
function getTrendPoints(cityName) {
  const cityData = trendData.value.cities.find(c => c.name === cityName)
  const data = cityData?.data || []
  const monthCount = trendMonths.value.length
  const xStep = monthCount > 1 ? (720 / (monthCount - 1)) : 120
  return data.map((item, index) => ({
    x: 60 + index * xStep,
    y: 220 - (item / 100) * 180
  }))
}

// 获取趋势线点
function getTrendLinePoints(cityName) {
  return getTrendPoints(cityName).map(p => `${p.x},${p.y}`).join(' ')
}

// 获取X轴位置（用于动态计算月份标签位置）
function getXPosition(index) {
  const monthCount = trendMonths.value.length
  const xStep = monthCount > 1 ? (720 / (monthCount - 1)) : 120
  return 60 + index * xStep
}

// 获取图例X轴位置
function getLegendX(index) {
  const cityCount = topCities.value.length
  const step = cityCount > 1 ? (720 / (cityCount - 1)) : 120
  return 60 + index * step
}

// 调整颜色亮度
function adjustColorBrightness(color, amount) {
  const hex = color.replace('#', '')
  const r = Math.min(255, parseInt(hex.substring(0, 2), 16) + amount)
  const g = Math.min(255, parseInt(hex.substring(2, 4), 16) + amount)
  const b = Math.min(255, parseInt(hex.substring(4, 6), 16) + amount)
  return `#${r.toString(16).padStart(2, '0')}${g.toString(16).padStart(2, '0')}${b.toString(16).padStart(2, '0')}`
}

/**
 * 页面加载时并行请求统计数据
 * 任一接口失败不影响另一个，失败的图表显示"暂无数据"
 */
onMounted(async () => {
  // 请求热门景点数据
  try {
    const res = await hotAttractionStatsApi()
    hotAttractions.value = res?.list || res || []
  } catch (e) {
    hotAttractions.value = []
    console.error('[AdminStats] 热门景点统计加载失败:', e)
  }

  // 请求热门城市数据
  try {
    const res = await hotCityStatsApi()
    hotCities.value = res?.list || res || []
  } catch (e) {
    hotCities.value = []
    console.error('[AdminStats] 热门城市统计加载失败:', e)
  }

  // 初始化选中城市为第一个热门城市（如果有数据）
  if (availableCities.value.length > 0) {
    selectedCity.value = availableCities.value[0]
  }

  // 加载饼图数据和趋势数据
  await loadPieData(selectedCity.value)
  await loadTrendData()
})

/**
 * 加载饼图数据（酒店和景点选择占比）
 */
async function loadPieData(city) {
  if (!city) {
    hotelSelectionData.value = []
    attractionSelectionData.value = []
    return
  }

  // 加载酒店选择占比数据
  try {
    const res = await hotelSelectionRatioApi({ city })
    hotelSelectionData.value = res?.list || res || []
  } catch (e) {
    hotelSelectionData.value = []
    console.error('[AdminStats] 酒店选择占比加载失败:', e)
  }

  // 加载景点选择占比数据
  try {
    const res = await attractionSelectionRatioApi({ city })
    attractionSelectionData.value = res?.list || res || []
  } catch (e) {
    attractionSelectionData.value = []
    console.error('[AdminStats] 景点选择占比加载失败:', e)
  }
}

/**
 * 加载趋势数据
 */
async function loadTrendData() {
  try {
    const res = await cityTrendApi()
    trendData.value = res?.data || res || { months: [], cities: [] }
  } catch (e) {
    trendData.value = { months: [], cities: [] }
    console.error('[AdminStats] 城市趋势数据加载失败:', e)
  }
}

/**
 * 监听城市选择变化，重新加载饼图数据
 */
watch(selectedCity, async (newCity) => {
  if (newCity) {
    await loadPieData(newCity)
  }
})
</script>

<style scoped>
.admin-page {
  background: #fff;
  border-radius: 16px;
  padding: 28px;
  box-shadow: 0 4px 16px rgba(15, 81, 50, 0.06);
}

.page-header {
  margin-bottom: 24px;
}

.page-header h2 {
  margin: 0;
  color: #0f5132;
  font-size: 22px;
}

.stats-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 24px;
}

.stats-card {
  background: #f8fcfa;
  border-radius: 14px;
  padding: 24px;
  border: 1px solid #e8f2ec;
}

.stats-card h3 {
  margin: 0 0 20px;
  color: #0f5132;
  font-size: 18px;
}

.empty-chart {
  text-align: center;
  color: #999;
  padding: 40px 0;
}

/* 柱状图 */
.bar-chart {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.bar-item {
  display: flex;
  align-items: center;
  gap: 12px;
}

.bar-label {
  width: 80px;
  font-size: 14px;
  color: #333;
  text-align: right;
  flex-shrink: 0;
}

.bar-track {
  flex: 1;
  height: 28px;
  background: #e8f2ec;
  border-radius: 6px;
  overflow: hidden;
}

.bar-fill {
  height: 100%;
  background: linear-gradient(90deg, #2d8a6e, #51b891);
  border-radius: 6px;
  display: flex;
  align-items: center;
  justify-content: flex-end;
  padding-right: 8px;
  transition: width 0.6s ease;
  min-width: 40px;
}

.bar-value {
  color: #fff;
  font-size: 12px;
  font-weight: 600;
}

/* 城市排行 */
.city-rank {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.rank-item {
  display: flex;
  align-items: center;
  gap: 12px;
}

.rank-no {
  width: 28px;
  height: 28px;
  border-radius: 50%;
  display: inline-flex;
  align-items: center;
  justify-content: center;
  background: #e8f5ef;
  color: #1a5a45;
  font-weight: 700;
  font-size: 13px;
  flex-shrink: 0;
}

.rank-no.top {
  background: linear-gradient(135deg, #2d8a6e, #1a5a45);
  color: #fff;
}

.rank-name {
  width: 60px;
  font-size: 14px;
  font-weight: 600;
  color: #333;
  flex-shrink: 0;
}

.rank-bar-wrap {
  flex: 1;
  height: 20px;
  background: #e8f2ec;
  border-radius: 4px;
  overflow: hidden;
}

.rank-bar {
  height: 100%;
  background: linear-gradient(90deg, #51b891, #2d8a6e);
  border-radius: 4px;
  transition: width 0.6s ease;
}

.rank-value {
  width: 50px;
  font-size: 14px;
  color: #666;
  text-align: right;
  flex-shrink: 0;
}

@media (max-width: 900px) {
  .stats-grid {
    grid-template-columns: 1fr;
  }
}

/* ===== 可视化分析板块样式 ===== */

.analysis-section {
  margin-top: 24px;
}

.section-header {
  margin-bottom: 20px;
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.section-header h3 {
  margin: 0;
  color: #0f5132;
  font-size: 20px;
}

/* 城市选择器 */
.city-selector {
  display: flex;
  align-items: center;
  gap: 10px;
}

.city-selector label {
  font-size: 14px;
  color: #666;
}

.city-select {
  padding: 8px 16px;
  border: 1px solid #d1e5dc;
  border-radius: 8px;
  background: rgba(255, 255, 255, 0.8);
  color: #333;
  font-size: 14px;
  cursor: pointer;
  min-width: 120px;
}

.city-select:focus {
  outline: none;
  border-color: #51b891;
}

.analysis-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 24px;
}

.analysis-card {
  background: #f8fcfa;
  border-radius: 14px;
  padding: 24px;
  border: 1px solid #e8f2ec;
}

.analysis-card.full-width {
  grid-column: span 2;
}

.analysis-card h4 {
  margin: 0 0 20px;
  color: #1a5a45;
  font-size: 16px;
}

/* 3D饼状图容器 */
.pie-chart-container-3d {
  position: relative;
  width: 200px;
  height: 200px;
  margin: 0 auto 20px;
  perspective: 500px;
}

.pie-3d {
  position: relative;
  width: 100%;
  height: 100%;
  transform-style: preserve-3d;
  transform: rotateX(30deg) rotateY(0deg);
}

.pie-chart-3d {
  width: 100%;
  height: 100%;
}

.pie-segment-3d {
  transition: all 0.5s ease;
  filter: drop-shadow(2px 2px 4px rgba(0,0,0,0.1));
}

.pie-segment-3d-highlight {
  transition: all 0.5s ease;
  stroke-width: 42;
  opacity: 0.3;
  filter: drop-shadow(4px 4px 8px rgba(0,0,0,0.2));
}

.pie-shadow {
  position: absolute;
  bottom: -10px;
  left: 50%;
  transform: translateX(-50%);
  width: 120px;
  height: 15px;
  background: radial-gradient(ellipse at center, rgba(0,0,0,0.3) 0%, rgba(0,0,0,0) 70%);
  border-radius: 50%;
}

.pie-center-3d {
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%) rotateX(-30deg);
  text-align: center;
  z-index: 10;
}

.pie-total-3d {
  display: block;
  font-size: 24px;
  font-weight: 700;
  color: #2d8a6e;
}

.pie-label-3d {
  display: block;
  font-size: 12px;
  color: #999;
}

/* 饼状图图例 */
.pie-legend {
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.legend-item {
  display: flex;
  align-items: center;
  gap: 10px;
}

.legend-color {
  width: 12px;
  height: 12px;
  border-radius: 3px;
  box-shadow: 1px 1px 3px rgba(0,0,0,0.2);
}

.legend-name {
  flex: 1;
  font-size: 14px;
  color: #333;
}

.legend-value {
  font-size: 14px;
  font-weight: 600;
  color: #2d8a6e;
}

/* 折线图容器 */
.line-chart-container {
  overflow-x: auto;
  padding-bottom: 10px;
}

.line-chart-multi {
  width: 100%;
  min-width: 700px;
  height: 250px;
}

@media (max-width: 900px) {
  .analysis-grid {
    grid-template-columns: 1fr;
  }
  
  .analysis-card.full-width {
    grid-column: span 1;
  }
  
  .section-header {
    flex-direction: column;
    align-items: flex-start;
    gap: 10px;
  }
}
</style>
