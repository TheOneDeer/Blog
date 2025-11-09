import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import { userApi } from '@/api/user'

export const useUserStore = defineStore('user', () => {
  // state
  const token = ref(localStorage.getItem('token'))
  const userInfo = ref(null)
  const loading = ref(false)
  
  // getters
  const isAuthenticated = computed(() => !!token.value)
  
  // actions
  async function login(credentials) {
    loading.value = true
    try {
      const response = await userApi.login(credentials)
      token.value = response.data.token
      localStorage.setItem('token', token.value)
      await getUserInfo()
      return response
    } finally {
      loading.value = false
    }
  }
  
  async function register(data) {
    loading.value = true
    try {
      const response = await userApi.register(data)
      return response
    } finally {
      loading.value = false
    }
  }
  
  async function getUserInfo() {
    if (!token.value) return null
    
    loading.value = true
    try {
      const response = await userApi.getUserInfo()
      userInfo.value = response.data
      return response.data
    } finally {
      loading.value = false
    }
  }
  
  function logout() {
    token.value = null
    userInfo.value = null
    localStorage.removeItem('token')
  }
  
  return {
    token,
    userInfo,
    loading,
    isAuthenticated,
    login,
    register,
    getUserInfo,
    logout
  }
})
