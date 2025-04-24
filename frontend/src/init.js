import Vue from 'vue'
import router from './router'
import { createPinia, PiniaVuePlugin } from 'pinia'
import VueCompositionAPI from '@vue/composition-api'
import ElementUI from 'element-ui'
import 'element-ui/lib/theme-chalk/index.css'
import '@/assets/css/global.css'
import '@/assets/css/theme/index.css'
import App from './App.vue'
import request from "@/utils/request"
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

// 创建 Vue 实例
const app = new Vue({
  router,
  pinia,
  render: h => h(App)
})

// 挂载实例
app.$mount('#app')

// 导出实例供其他模块使用
export default app 