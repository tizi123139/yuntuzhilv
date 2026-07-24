<template>
  <div class="service-page">
    <div class="bg-decoration">
      <span class="dot dot-1"></span>
      <span class="dot dot-2"></span>
      <span class="dot dot-3"></span>
      <span class="dot dot-4"></span>
      <span class="dot dot-5"></span>
      <span class="dot dot-6"></span>
    </div>

    <header class="header">
      <h1>常用服务</h1>
      <button @click="router.push('/home')">返回首页</button>
    </header>

    <section class="cards">
      <article class="service-card" @click="router.push('/attractions')">
        <div class="icon-wrapper">
          <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
            <path d="M8 21l4-10 4 10M5 17l5-10 5 10" stroke-linecap="round" stroke-linejoin="round"/>
            <circle cx="12" cy="5" r="2"/>
          </svg>
        </div>
        <h2>景点浏览</h2>
        <p>按城市 / 类型筛选景点，查看详情。</p>
      </article>

      <article class="service-card" @click="router.push('/hotels')">
        <div class="icon-wrapper">
          <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
            <path d="M3 21h18" stroke-linecap="round"/>
            <path d="M5 21V7a2 2 0 012-2h10a2 2 0 012 2v14" stroke-linecap="round" stroke-linejoin="round"/>
            <path d="M9 21V3a2 2 0 012-2h2a2 2 0 012 2v18" stroke-linecap="round" stroke-linejoin="round"/>
            <path d="M7 10h10" stroke-linecap="round"/>
          </svg>
        </div>
        <h2>酒店查询</h2>
        <p>按目的地、星级和价格筛选酒店，快速对比。</p>
      </article>

      <article class="service-card" @click="router.push('/traffic')">
        <div class="icon-wrapper">
          <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
            <rect x="3" y="11" width="18" height="8" rx="2" stroke-linecap="round"/>
            <circle cx="7" cy="15" r="1" fill="currentColor"/>
            <circle cx="17" cy="15" r="1" fill="currentColor"/>
            <path d="M5 11V6a3 3 0 013-3h8a3 3 0 013 3v5" stroke-linecap="round" stroke-linejoin="round"/>
            <path d="M15 19h2" stroke-linecap="round"/>
            <path d="M6 19h2" stroke-linecap="round"/>
          </svg>
        </div>
        <h2>交通查询</h2>
        <p>输入起止城市，推荐交通方案。</p>
      </article>
    </section>

    <section class="inspiration">
      <h3>今日旅行灵感</h3>
      <div class="tags">
        <button class="tag-btn">杭州西湖慢行</button>
        <button class="tag-btn">北京古迹路线</button>
        <button class="tag-btn">成都美食街区</button>
        <button class="tag-btn">西安历史之旅</button>
      </div>
    </section>

    <section class="cta">
      <button @click="router.push('/create-itinerary')">开始定制行程</button>
    </section>
  </div>
</template>

<script setup>
import { useRouter } from 'vue-router'

const router = useRouter()
</script>

<style scoped>
.service-page {
  min-height: 100vh;
  background: radial-gradient(circle at 20% 10%, #d9f2e8, #edf9f4 38%, #f7fcfa 70%);
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
}

.dot {
  position: absolute;
  background: rgba(197, 232, 219, 0.4);
  border-radius: 50%;
}

.dot-1 {
  width: 120px;
  height: 120px;
  top: 15%;
  left: 5%;
}

.dot-2 {
  width: 80px;
  height: 80px;
  top: 30%;
  right: 10%;
}

.dot-3 {
  width: 100px;
  height: 100px;
  bottom: 20%;
  left: 15%;
}

.dot-4 {
  width: 60px;
  height: 60px;
  bottom: 35%;
  right: 5%;
}

.dot-5 {
  width: 40px;
  height: 40px;
  top: 60%;
  right: 30%;
}

.dot-6 {
  width: 50px;
  height: 50px;
  top: 80%;
  left: 40%;
}

.header {
  max-width: 980px;
  margin: 0 auto;
  display: flex;
  justify-content: space-between;
  align-items: center;
  position: relative;
  z-index: 1;
}

.header button {
  border: none;
  border-radius: 10px;
  padding: 10px 14px;
  background: #1a5a45;
  color: #fff;
  cursor: pointer;
}

.cards {
  max-width: 980px;
  margin: 18px auto 0;
  display: grid;
  grid-template-columns: repeat(3, minmax(0, 1fr));
  gap: 16px;
  position: relative;
  z-index: 1;
}

.service-card {
  background: #fff;
  border-radius: 18px;
  padding: 20px;
  box-shadow: 0 10px 30px rgba(26, 90, 69, 0.12);
  cursor: pointer;
  transition: all 0.3s ease;
  position: relative;
}

.service-card::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  height: 3px;
  background: linear-gradient(90deg, #1a5a45, #5cb870);
  border-radius: 18px 18px 0 0;
}

.service-card:hover {
  transform: translateY(-6px);
  box-shadow: 0 16px 40px rgba(26, 90, 69, 0.18);
}

.icon-wrapper {
  width: 48px;
  height: 48px;
  background: linear-gradient(135deg, #e8f5f0, #d9f2e8);
  border-radius: 14px;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-bottom: 14px;
  color: #1a5a45;
}

.icon-wrapper svg {
  width: 24px;
  height: 24px;
}

.service-card h2 {
  color: #1a5a45;
  margin-bottom: 8px;
  font-size: 18px;
}

.service-card p {
  color: #56736a;
  line-height: 1.5;
  font-size: 14px;
}

.inspiration {
  max-width: 700px;
  margin: 36px auto 0;
  background: linear-gradient(135deg, rgba(197, 232, 219, 0.6), rgba(232, 245, 240, 0.6));
  border-radius: 24px;
  padding: 24px;
  text-align: center;
  position: relative;
  z-index: 1;
  backdrop-filter: blur(10px);
}

.inspiration h3 {
  color: #1a5a45;
  margin-bottom: 16px;
  font-size: 18px;
}

.tags {
  display: flex;
  flex-wrap: wrap;
  justify-content: center;
  gap: 12px;
}

.tag-btn {
  border: none;
  background: rgba(255, 255, 255, 0.8);
  color: #1a5a45;
  padding: 8px 16px;
  border-radius: 20px;
  cursor: pointer;
  font-size: 14px;
  transition: all 0.2s;
}

.tag-btn:hover {
  background: #fff;
  transform: scale(1.05);
}

.cta {
  max-width: 980px;
  margin: 32px auto 0;
  text-align: center;
  position: relative;
  z-index: 1;
}

.cta button {
  border: none;
  background: linear-gradient(135deg, #1a5a45, #2d7d5e);
  color: #fff;
  padding: 14px 48px;
  border-radius: 30px;
  font-size: 18px;
  cursor: pointer;
  box-shadow: 0 8px 24px rgba(26, 90, 69, 0.3);
  transition: all 0.3s;
}

.cta button:hover {
  transform: translateY(-2px);
  box-shadow: 0 12px 32px rgba(26, 90, 69, 0.4);
}

@media (max-width: 900px) {
  .service-page {
    padding: 16px;
  }

  .cards {
    grid-template-columns: 1fr;
  }

  .cta button {
    padding: 14px 32px;
  }
}
</style>
