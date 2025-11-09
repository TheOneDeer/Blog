import { defineStore } from 'pinia'
import { ref } from 'vue'
import { searchApi } from '@/api/search'

export const useSearchStore = defineStore('search', () => {
  const results = ref([])
  const keyword = ref('')
  const total = ref(0)
  const page = ref(1)
  const pageSize = ref(10)
  const loading = ref(false)
  
  async function search(searchKeyword, reset = false) {
    if (reset) page.value = 1
    keyword.value = searchKeyword
    loading.value = true
    try {
      const response = await searchApi.search(keyword.value, page.value, pageSize.value)
      results.value = response.data.documents
      total.value = response.data.total
    } finally {
      loading.value = false
    }
  }
  
  function setPage(newPage) {
    page.value = newPage
  }
  
  function clearResults() {
    results.value = []
    keyword.value = ''
    total.value = 0
    page.value = 1
  }
  
  return {
    results,
    keyword,
    total,
    page,
    pageSize,
    loading,
    search,
    setPage,
    clearResults
  }
})

