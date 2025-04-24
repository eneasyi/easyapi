import Vue from 'vue'
import App from './App.vue'
import router from './router'
import ElementUI from 'element-ui'
import 'element-ui/lib/theme-chalk/index.css'
import '@/assets/css/global.css'
import '@/assets/css/theme/index.css'
import request from "@/utils/request"
import { createPinia, PiniaVuePlugin } from 'pinia'
import VueCompositionAPI from '@vue/composition-api'

//引入echarts
import * as echarts from 'echarts'

// 初始化插件
Vue.use(VueCompositionAPI)
Vue.use(PiniaVuePlugin)
Vue.use(ElementUI, {size: "small"})

// 设置全局属性
Vue.prototype.$echarts = echarts
Vue.prototype.$request = request
Vue.prototype.$baseUrl = process.env.VUE_APP_BASEURL
Vue.config.productionTip = false

// 创建 pinia 实例
const pinia = createPinia()
export const piniaInstance = pinia

// 创建并挂载 Vue 实例
const app = new Vue({
  router,
  pinia,
  render: h => h(App)
}).$mount('#app')

// 导出实例供其他模块使用
export default app
