import ACCESS_ENUM from '@/access/accessEnum'

export default [
  {
    path: 'home',
    name: 'Home',
    meta: { name: '首页', access: ACCESS_ENUM.NOT_LOGIN, isMenu: true },
    component: () => import('../views/front/Home')
  },
  {
    path: 'person',
    name: 'Person',
    meta: { name: '个人中心', access: ACCESS_ENUM.USER, isMenu: false },
    component: () => import('../views/front/Person')
  },
  {
    path: 'about',
    name: 'About',
    meta: { name: '关于', access: ACCESS_ENUM.NOT_LOGIN, isMenu: true },
    component: () => import('../views/front/About')
  },
] 