import { createRouter, createWebHistory } from 'vue-router'
import Home from '../views/Home.vue'
import AI from '../views/AI.vue'
import About from '../views/About.vue'
import Assets from '../views/Assets.vue'
import AssetList from '../views/AssetList.vue'
import Trades from '../views/Trades.vue'
import TradeRegistration from '../views/TradeRegistration.vue'

const routes = [
  {
    path: '/',
    name: 'Home',
    component: Home
  },
  {
    path: '/ai',
    name: 'AI',
    component: AI
  },
  {
    path: '/assets',
    name: 'Assets',
    component: Assets
  },
  {
    path: '/asset-list',
    name: 'AssetList',
    component: AssetList
  },
  {
    path: '/trades',
    name: 'Trades',
    component: Trades
  },
  {
    path: '/trade-registration',
    name: 'TradeRegistration',
    component: TradeRegistration
  },
  {
    path: '/about',
    name: 'About',
    component: About
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

export default router 