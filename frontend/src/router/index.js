import Vue from 'vue'
import VueRouter from 'vue-router'
import { useUserStore } from '@/stores/user'
import ACCESS_ENUM from '@/access/accessEnum'
import checkAccess from '@/access/checkAccess'
import { piniaInstance } from '@/main'
import adminRouter from './adminRouter'
import userRouter from './userRouter'

Vue.use(VueRouter)

// 解决导航栏或者底部导航tabBar中的vue-router在3.0版本以上频繁点击菜单报错的问题。
const originalPush = VueRouter.prototype.push
VueRouter.prototype.push = function push(location) {
  return originalPush.call(this, location).catch(err => err)
}

const routes = [
  {
    path: '/admin',
    name: 'Manager',
    redirect: '/admin/home',
    component: () => import('../views/Manager.vue'),
    meta: { access: ACCESS_ENUM.ADMIN },  // 需要管理员权限
    children: adminRouter
  },
  {
    path: '/front',
    name: 'Front',
    redirect: '/front/home',
    component: () => import('../views/Front.vue'),
    meta: { access: ACCESS_ENUM.NOT_LOGIN, isMenu: false },  // 不需要登录
    children: userRouter
  },
  {
    path: '/login',
    name: 'Login',
    meta: { name: '管理员登录', access: ACCESS_ENUM.NOT_LOGIN, isMenu: false },
    component: () => import('../views/Login.vue')
  },
  {
    path: '/404',
    name: '404',
    meta: { name: '404', access: ACCESS_ENUM.NOT_LOGIN, isMenu: false },
    component: () => import('../views/404.vue')
  },
  {
    path: '/500',
    name: '500',
    meta: { name: '500', access: ACCESS_ENUM.NOT_LOGIN, isMenu: false },
    component: () => import('../views/500.vue')
  },
  { path: '*', redirect: '/404' },
  {
    path: '/',
    name: 'home',
    meta: { name: '主页', isMenu: false },
    redirect: '/front/home'
  }
]

const router = new VueRouter({
  mode: 'history',
  base: process.env.BASE_URL,
  routes,
  scrollBehavior(to, from, savedPosition) {
    return { x: 0, y: 0 }
  }
})

// 路由守卫
router.beforeEach(async (to, from, next) => {
  const userStore = useUserStore(piniaInstance)
  console.log('Route navigation started:', to.path)

  const needAccess = to.meta?.access || ACCESS_ENUM.NOT_LOGIN

  let loginUser = userStore.userInfo
  console.log('Current route meta:', to.meta)
  console.log('Initial loginUser:', loginUser)

  // 如果是登录页面，直接放行
  if (to.path === '/login') {
    console.log('Login page, passing through')
    next()
    return
  }

  // 如果没有登录用户信息，尝试获取
  if (!loginUser || !loginUser.userRole) {
    try {
      await userStore.fetchUserInfo()
      loginUser = userStore.userInfo
      console.log('获取到用户信息:', loginUser)
    } catch (error) {
      console.error('获取用户信息失败:', error)
      loginUser = {
        userName: "未登录",
        userRole: ACCESS_ENUM.NOT_LOGIN
      }
    }
  }

  console.log('当前用户角色:', loginUser?.userRole)
  console.log('需要的权限:', needAccess)
  console.log('权限检查结果:', checkAccess(loginUser, needAccess))

  // 如果页面需要登录权限
  if (needAccess !== ACCESS_ENUM.NOT_LOGIN) {
    console.log('Page requires login')
    if (!checkAccess(loginUser, needAccess)) {
      Vue.prototype.$message.warning('请先登录')
    }

    // 未登录或角色是未登录，跳转到登录页
    if (!loginUser || !loginUser.userRole || loginUser.userRole === ACCESS_ENUM.NOT_LOGIN) {
      console.log('User not logged in')
      // 如果是访问管理后台
      if (to.path.startsWith('/admin')) {
        Vue.prototype.$message.warning('请先登录')
        await userStore.logout()
        next(`/login?redirect=${to.fullPath}`)
        return
      }
      // 其他页面直接跳转
      // next(`/login?redirect=${to.fullPath}`)
      return
    }

    // 权限不足，跳转到登录页
    if (!checkAccess(loginUser, needAccess)) {
      console.log('Access denied')
      // 如果是访问管理后台
      if (to.path.startsWith('/admin')) {
        Vue.prototype.$message.error('权限不足，请使用管理员账号登录')
        await userStore.logout()
        next(`/login?redirect=${to.fullPath}`)
        return
      }
      if (to.path.startsWith('/front')) {
        Vue.prototype.$message.error('权限不足，请使用用户账号登录')
        await userStore.logout()
        return
      }
      // 其他页面跳转到 403
      await userStore.logout()
      next('/403')
      return
    }
  }

  console.log('Navigation allowed')
  next()
})

export default router
