import { createRouter, createWebHistory } from 'vue-router'
import { useUserStore } from '@/stores/user'

const routes = [
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/views/auth/Login.vue'),
    meta: { title: '登录', requiresAuth: false }
  },
  {
    path: '/register',
    name: 'Register',
    component: () => import('@/views/auth/Register.vue'),
    meta: { title: '注册', requiresAuth: false }
  },
  {
    path: '/',
    component: () => import('@/components/layout/AppLayout.vue'),
    meta: { requiresAuth: true },
    children: [
      {
        path: '',
        name: 'DocumentList',
        component: () => import('@/views/document/DocumentList.vue'),
        meta: { title: '文档列表' }
      },
      {
        path: 'documents/upload',
        name: 'DocumentUpload',
        component: () => import('@/views/document/DocumentUpload.vue'),
        meta: { title: '上传文档' }
      },
      {
        path: 'documents/:id',
        name: 'DocumentDetail',
        component: () => import('@/views/document/DocumentDetail.vue'),
        meta: { title: '文档详情' }
      },
      {
        path: 'categories',
        name: 'CategoryManager',
        component: () => import('@/views/category/CategoryManager.vue'),
        meta: { title: '分类管理' }
      },
      {
        path: 'search',
        name: 'SearchResult',
        component: () => import('@/views/search/SearchResult.vue'),
        meta: { title: '搜索结果' }
      }
    ]
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

// 路由守卫
router.beforeEach((to, from, next) => {
  const userStore = useUserStore()
  
  // 需要认证的页面（包括父路由）
  if (to.meta.requiresAuth && !userStore.isAuthenticated) {
    next({ name: 'Login', query: { redirect: to.fullPath } })
    return
  }
  
  // 已登录用户访问登录/注册页
  if ((to.name === 'Login' || to.name === 'Register') && userStore.isAuthenticated) {
    next('/')
    return
  }
  
  // 设置页面标题
  if (to.meta.title) {
    document.title = `${to.meta.title} - 个人博客系统`
  }
  
  next()
})

export default router
