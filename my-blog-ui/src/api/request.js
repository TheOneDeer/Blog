import axios from 'axios'
import { ElMessage } from 'element-plus'
import router from '@/router'
import { useUserStore } from '@/stores/user'

// 创建 axios 实例
const request = axios.create({
  baseURL: 'http://localhost:9080/api/v1',
  timeout: 30000
})

// 请求拦截器
request.interceptors.request.use(
  config => {
    // 从 localStorage 获取 token
    const token = localStorage.getItem('token')
    if (token) {
      config.headers.Authorization = `Bearer ${token}`
    }
    return config
  },
  error => {
    return Promise.reject(error)
  }
)

// 响应拦截器
request.interceptors.response.use(
  response => {
    const { code, message } = response.data
    
    // 如果 code 不是 200，表示请求失败
    if (code !== 200) {
      // 如果 code 是 401，表示登录过期
      if (code === 401) {
        handleUnauthorized()
        return Promise.reject(new Error(message || '登录已过期'))
      }
      ElMessage.error(message || '请求失败')
      return Promise.reject(new Error(message))
    }
    
    return response.data
  },
  error => {
    // 处理错误响应
    if (error.response) {
      const status = error.response.status
      const { message } = error.response.data || {}
      
      switch (status) {
        case 401:
          // 处理401未授权（登录过期）
          handleUnauthorized(message || '登录已过期，请重新登录')
          break
        case 403:
          ElMessage.error('无权限访问')
          break
        case 404:
          ElMessage.error('资源不存在')
          break
        case 500:
          ElMessage.error(message || '服务器错误')
          break
        default:
          ElMessage.error(message || error.response.data?.message || '网络错误')
      }
    } else {
      ElMessage.error('网络连接失败')
    }
    
    return Promise.reject(error)
  }
)

/**
 * 处理未授权（登录过期）
 */
function handleUnauthorized(message) {
  const userStore = useUserStore()
  
  // 清除用户状态
  userStore.logout()
  
  // 显示提示信息
  ElMessage.error(message || '登录已过期，请重新登录')
  
  // 跳转到登录页
  router.push({
    name: 'Login',
    query: { redirect: router.currentRoute.value.fullPath }
  })
}

export default request
