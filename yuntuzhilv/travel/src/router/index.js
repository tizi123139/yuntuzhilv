import { createRouter, createWebHistory } from 'vue-router'
import BeforeLoginHome from '../views/BeforeLoginHome.vue'
import Home from '../views/Home.vue'
import UserCenter from '../views/UserCenter.vue'
import CreateItinerary from '../views/CreateItinerary.vue'
import ItineraryResult from '../views/ItineraryResult.vue'
import MyItinerary from '../views/MyItinerary.vue'
import AttractionList from '../views/AttractionList.vue'
import HotelList from '../views/HotelList.vue'
import TrafficList from '../views/TrafficList.vue'
import CommonServices from '../views/CommonServices.vue'
import MyFootprints from '../views/MyFootprints.vue'
import AdminLogin from '../views/AdminLogin.vue'
import AdminLayout from '../views/AdminLayout.vue'
import AdminUser from '../views/admin/AdminUser.vue'
import AdminAttraction from '../views/admin/AdminAttraction.vue'
import AdminHotel from '../views/admin/AdminHotel.vue'
import AdminTraffic from '../views/admin/AdminTraffic.vue'
import AdminStats from '../views/admin/AdminStats.vue'
import AdminLog from '../views/admin/AdminLog.vue'
import AdminFeedback from '../views/admin/AdminFeedback.vue'

const router = createRouter({
  history: createWebHistory(),
  routes: [
    {
      path: '/',
      redirect: '/BLhome'
    },
    {
      path: '/BLhome',
      name: 'BeforeLoginHome',
      component: BeforeLoginHome
    },
    {
      path: '/home',
      name: 'Home',
      meta: { requiresAuth: true },
      component: Home
    },
    {
      path: '/create-itinerary',
      name: 'CreateItinerary',
      meta: { requiresAuth: true },
      component: CreateItinerary
    },
    {
      path: '/itinerary-result',
      name: 'ItineraryResult',
      meta: { requiresAuth: true },
      component: ItineraryResult
    },
    {
      path: '/my-itinerary',
      name: 'MyItinerary',
      meta: { requiresAuth: true },
      component: MyItinerary
    },
    {
      path: '/services',
      name: 'CommonServices',
      component: CommonServices
    },
    {
      path: '/attractions',
      name: 'AttractionList',
      component: AttractionList
    },
    {
      path: '/hotels',
      name: 'HotelList',
      component: HotelList
    },
    {
      path: '/traffic',
      name: 'TrafficList',
      component: TrafficList
    },
    {
      path: '/user-center',
      name: 'UserCenter',
      meta: { requiresAuth: true },
      component: UserCenter
    },
    {
      path: '/my-footprints',
      name: 'MyFootprints',
      meta: { requiresAuth: true },
      component: MyFootprints
    },
    {
      path: '/admin-login',
      name: 'AdminLogin',
      component: AdminLogin
    },
    {
      path: '/admin',
      name: 'AdminLayout',
      meta: { requiresAuth: true, requiresAdmin: true },
      component: AdminLayout,
      children: [
        { path: '', redirect: '/admin/users' },
        { path: 'users', name: 'AdminUser', component: AdminUser },
        { path: 'attractions', name: 'AdminAttraction', component: AdminAttraction },
        { path: 'hotels', name: 'AdminHotel', component: AdminHotel },
        { path: 'traffic', name: 'AdminTraffic', component: AdminTraffic },
        { path: 'stats', name: 'AdminStatsDetail', component: AdminStats },
        { path: 'logs', name: 'AdminLog', component: AdminLog },
        { path: 'feedback', name: 'AdminFeedback', component: AdminFeedback }
      ]
    }
  ]
})

router.beforeEach((to, from, next) => {
  const token = localStorage.getItem('token')
  const role = localStorage.getItem('role')

  if (!token && to.meta.requiresAuth) {
    next('/BLhome')
    return
  }

  if (token && to.path === '/BLhome') {
    next((role || '').toLowerCase() === 'admin' ? '/admin' : '/home')
    return
  }

  if (to.meta.requiresAdmin && (role || '').toLowerCase() !== 'admin') {
    next((role || '').toLowerCase() === 'user' ? '/home' : '/BLhome')
    return
  }

  next()
})

export default router
