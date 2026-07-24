<template>
  <div id="app">
    <router-view />
    <!-- 全局 Toast 通知条，从顶部滑入 -->
    <ToastContainer />
    <!-- 全站页脚：登录页和管理后台不显示 -->
    <SiteFooter v-if="showFooter" />
  </div>
</template>

<script setup>
import { computed } from 'vue'
import { useRoute } from 'vue-router'
import ToastContainer from './components/ToastContainer.vue'
import SiteFooter from './components/SiteFooter.vue'

const route = useRoute()

/** 不显示页脚的路径前缀列表 */
const hideFooterPaths = ['/BLhome', '/admin-login', '/admin']

const showFooter = computed(() => {
  return !hideFooterPaths.some(p => route.path === p || route.path.startsWith(p + '/'))
})
</script>

<style scoped>
</style>
