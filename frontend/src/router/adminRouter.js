import ACCESS_ENUM from '@/access/accessEnum'

export default [
  {
    path: '403',
    name: 'NoAuth',
    meta: { name: '无权限', access: ACCESS_ENUM.NOT_LOGIN, isMenu: false },
    component: () => import('../views/manager/403')
  },
  {
    path: 'home',
    name: 'Home',
    meta: { name: '系统首页', access: ACCESS_ENUM.ADMIN, isMenu: true },
    component: () => import('../views/manager/Home')
  },
  {
    path: 'user',
    name: 'User',
    meta: { name: '用户管理', access: ACCESS_ENUM.ADMIN, isMenu: true },
    component: () => import('../views/manager/User')
  },
  {
    path: 'adminPerson',
    name: 'AdminPerson',
    meta: { name: '个人信息', access: ACCESS_ENUM.ADMIN, isMenu: true },
    component: () => import('../views/manager/AdminPerson')
  },
  {
    path: 'interfaceInfo',
    name: 'InterfaceInfo',
    meta: { name: '接口管理', access: ACCESS_ENUM.ADMIN, isMenu: true },
    component: () => import('../views/manager/InterfaceInfo')
  },
  {
    path: 'interfaceCategory',
    name: 'InterfaceCategory',
    meta: { name: '接口分类', access: ACCESS_ENUM.ADMIN, isMenu: true },
    component: () => import('../views/manager/InterfaceCategory')
  },
] 