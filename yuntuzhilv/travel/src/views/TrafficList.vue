<template>
  <div class="page">
    <header class="header">
      <h1>交通查询</h1>
      <button @click="router.push('/services')">返回常用服务</button>
    </header>

    <section class="filters">
      <input v-model.trim="query.fromCity" placeholder="出发城市" />
      <input v-model.trim="query.toCity" placeholder="到达城市" />
      <button @click="handleSearch" :disabled="loading">
        {{ loading ? '查询中...' : '查询' }}
      </button>
    </section>

    <!-- 加载状态提示 -->
    <div v-if="loading" class="status-tip">正在查询交通方案...</div>

    <!-- 错误状态提示 -->
    <div v-else-if="loadError" class="status-tip error">{{ loadError }}</div>

    <!-- 未搜索时的初始提示 -->
    <div v-else-if="!hasSearched" class="status-tip">请输入出发城市和到达城市进行查询</div>

    <!-- 搜索后无结果 -->
    <div v-else-if="list.length === 0" class="status-tip">
      没有找到从 {{ query.fromCity }} 到 {{ query.toCity }} 的交通方案
    </div>

    <!-- 交通方案列表 -->
    <section class="list" v-else>
      <article class="card" v-for="item in list" :key="item.id || item.code">
        <h3>{{ item.type }} · {{ item.code }}</h3>
        <p>{{ item.fromCity }} → {{ item.toCity }}</p>
        <p>{{ item.time }} · ¥{{ item.price }}</p>
      </article>
    </section>
  </div>
</template>

<script setup>
/**
 * 交通查询页面
 *
 * 【架构说明】
 * 本页面不包含任何硬编码数据，所有交通方案均来自后端接口。
 * 数据流：用户输入起止城市 → 调用 API → 后端返回方案列表 → 渲染
 * 若后端未部署，页面会显示错误提示，不会降级到假数据。
 *
 * 接口定义见 src/api/traffic.js，包含完整的请求参数和响应格式注释。
 */
import { reactive, ref } from 'vue'
import { showToast } from '../utils/toast';
import { useRouter } from 'vue-router'
import { trafficListApi } from '../api/traffic'

const router = useRouter()

// ===== 查询条件 =====
const query = reactive({ fromCity: '', toCity: '' })

// ===== 列表数据（由后端接口返回） =====
const list = ref([])         // 交通方案列表

// ===== UI 状态 =====
const loading = ref(false)   // 是否正在查询
const loadError = ref('')    // 查询失败时的错误信息
const hasSearched = ref(false) // 是否已经执行过查询（区分"未搜索"和"搜索无结果"）

/**
 * 点击"查询"按钮
 * 校验输入后调用后端接口获取交通方案
 */
async function handleSearch() {
  // 输入校验：两个城市都必须填写
  if (!query.fromCity || !query.toCity) {
    loadError.value = ''
    showToast('请先输入出发城市和到达城市', 'warning')
    return
  }

  loading.value = true
  loadError.value = ''
  hasSearched.value = true

  try {
    // 向后端请求交通方案，传入出发和到达城市
    const res = await trafficListApi({
      fromCity: query.fromCity,
      toCity: query.toCity
    })

    // 从响应中取出方案列表
    list.value = res?.list || res?.data?.list || []
  } catch (e) {
    // 请求失败：清空列表，显示错误提示
    list.value = []
    loadError.value = '查询失败，请稍后重试'
    console.error('[TrafficList] 查询失败:', e)
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.page {
  min-height: 100vh;
  background: #f5fbf8;
  padding: 24px;
}

.header {
  max-width: 960px;
  margin: 0 auto;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.header button,
.filters button {
  border: none;
  border-radius: 8px;
  padding: 8px 12px;
  background: #1a5a45;
  color: #fff;
  cursor: pointer;
}

.filters {
  max-width: 960px;
  margin: 14px auto 0;
  display: grid;
  grid-template-columns: 1fr 1fr 100px;
  gap: 10px;
}

.filters input {
  border: 1px solid #d7e7df;
  border-radius: 10px;
  padding: 10px;
}

.list {
  max-width: 960px;
  margin: 16px auto 0;
  display: grid;
  grid-template-columns: 1fr;
  gap: 12px;
}

.card {
  background: #fff;
  border-radius: 14px;
  padding: 14px;
  box-shadow: 0 8px 18px rgba(22, 76, 55, .08);
}

.card h3 {
  color: #1a5a45;
  margin-bottom: 8px;
}

.card p {
  color: #56736a;
  margin-bottom: 4px;
}

.status-tip {
  max-width: 960px;
  margin: 24px auto;
  text-align: center;
  color: #6b7c76;
  font-size: 14px;
  padding: 24px;
  background: #fff;
  border-radius: 12px;
  border: 1px dashed #c5e8db;
}

.status-tip.error {
  color: #dc2626;
  border-color: #fecaca;
  background: #fef2f2;
}

@media (max-width: 900px) {
  .page {
    padding: 16px;
  }

  .filters {
    grid-template-columns: 1fr;
  }
}
</style>
