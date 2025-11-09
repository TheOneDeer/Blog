import { defineStore } from 'pinia'
import { ref } from 'vue'
import { categoryApi } from '@/api/category'

export const useCategoryStore = defineStore('category', () => {
  const categories = ref([])
  const loading = ref(false)
  
  async function fetchCategories() {
    loading.value = true
    try {
      const response = await categoryApi.getList()
      categories.value = response.data
    } finally {
      loading.value = false
    }
  }
  
  async function createCategory(data) {
    loading.value = true
    try {
      await categoryApi.create(data)
      await fetchCategories()
    } finally {
      loading.value = false
    }
  }
  
  async function updateCategory(id, data) {
    loading.value = true
    try {
      await categoryApi.update(id, data)
      await fetchCategories()
    } finally {
      loading.value = false
    }
  }
  
  async function deleteCategory(id) {
    loading.value = true
    try {
      await categoryApi.delete(id)
      await fetchCategories()
    } finally {
      loading.value = false
    }
  }
  
  return {
    categories,
    loading,
    fetchCategories,
    createCategory,
    updateCategory,
    deleteCategory
  }
})

