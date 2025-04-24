import Vue from 'vue'
import { defineStore } from 'pinia'
import request from '@/utils/request'
import ACCESS_ENUM from '@/access/accessEnum'
import router from '@/router'

export const useUserStore = defineStore('user', {
  state: () => ({
    userInfo: JSON.parse(localStorage.getItem('userInfo')) || {
      userName: "未登录",
      userRole: ACCESS_ENUM.NOT_LOGIN
    },
    isLogin: !!localStorage.getItem('userInfo'),
    isLoading: false
  }),

  actions: {
    setUserInfo(user) {
      const userInfo = {
        ...user,
        userName: user.userName || '新用户',
        userRole: user.userRole
      }
      this.userInfo = userInfo
      this.isLogin = true
      localStorage.setItem('userInfo', JSON.stringify(userInfo))
    },

    async login(loginForm) {
      try {
        const res = await request.post('/api/user/login', loginForm)
        if (res.code === 0 && res.data) {
          this.setUserInfo(res.data)
          return res
        } else {
          return {
            code: res?.code || -1,
            message: res?.message || '登录失败',
            data: null
          }
        }
      } catch (error) {
        this.clearUserInfo()
        return {
          code: -1,
          message: error?.message || '登录失败',
          data: null
        }
      }
    },

    async logout() {
      try {
        await request.post('/api/user/logout')

        // 如果当前在需要登录的页面，先跳转到首页
        if (router.currentRoute.meta?.access !== ACCESS_ENUM.NOT_LOGIN) {
          await router.push('/')
        }

        this.clearUserInfo()
        return true
      } catch (error) {
        console.error('退出登录失败:', error)
        Vue.prototype.$message.error('退出登录失败')
        throw error
      }
    },

    async fetchUserInfo() {
      if (this.isLoading) {
        return
      }

      this.isLoading = true
      try {
        console.log('开始获取用户信息')
        const res = await request.get('/api/user/get/login')
        console.log('获取用户信息响应:', res)
        if (res.code === 0 && res.data) {
          this.setUserInfo(res.data)
          return res.data
        } else {
          Vue.prototype.$message.error(res.message || '登录失败')
        }
        this.clearUserInfo()
        throw new Error(res.message || '获取用户信息失败')
      } catch (error) {
        console.error('获取用户信息错误:', error)
        this.clearUserInfo()
        throw error
      } finally {
        this.isLoading = false
      }
    },

    clearUserInfo() {
      this.userInfo = {
        userName: "未登录",
        userRole: ACCESS_ENUM.NOT_LOGIN
      }
      this.isLogin = false
      localStorage.removeItem('userInfo')
    }
  },

  getters: {
    getUserInfo: (state) => state.userInfo,
    getIsLogin: (state) => state.isLogin,
    getUserRole: (state) => state.userInfo?.userRole || ACCESS_ENUM.NOT_LOGIN
  }
}) 