import { defineStore } from 'pinia'
import { ref } from 'vue'
import { documentApi } from '@/api/document'

export const useDocumentStore = defineStore('document', () => {
  // state
  const documents = ref([])
  const currentDocument = ref(null)
  const total = ref(0)
  const page = ref(1)
  const pageSize = ref(10)
  const categoryId = ref(null)
  const loading = ref(false)
  
  // actions
  async function fetchDocuments(reset = false, filterCategoryId = null) {
    if (reset) page.value = 1
    categoryId.value = filterCategoryId
    loading.value = true
    try {
      // 如果指定了分类，尝试使用分类参数（如果后端支持）
      // 否则加载所有文档，前端筛选
      const response = await documentApi.getList(page.value, pageSize.value, categoryId.value)
      documents.value = response.data.documents
      total.value = response.data.total
    } finally {
      loading.value = false
    }
  }
  
  // 获取所有文档（用于前端筛选）
  async function fetchAllDocuments() {
    loading.value = true
    try {
      // 获取较大的页面大小以加载更多文档
      const response = await documentApi.getList(1, 1000, null)
      documents.value = response.data.documents
      total.value = response.data.total
    } finally {
      loading.value = false
    }
  }
  
  async function fetchDocumentById(id) {
    loading.value = true
    try {
      const response = await documentApi.getById(id)
      currentDocument.value = response.data
      return response.data
    } finally {
      loading.value = false
    }
  }
  
  async function uploadDocument(formData) {
    loading.value = true
    try {
      const response = await documentApi.upload(formData)
      await fetchDocuments(true) // 刷新列表
      return response.data
    } finally {
      loading.value = false
    }
  }
  
  async function updateDocument(id, data) {
    loading.value = true
    try {
      const response = await documentApi.update(id, data)
      await fetchDocuments() // 刷新列表
      return response.data
    } finally {
      loading.value = false
    }
  }
  
  async function deleteDocument(id) {
    loading.value = true
    try {
      await documentApi.delete(id)
      await fetchDocuments() // 刷新列表
    } finally {
      loading.value = false
    }
  }
  
  function setPage(newPage) {
    page.value = newPage
  }
  
  return {
    documents,
    currentDocument,
    total,
    page,
    pageSize,
    categoryId,
    loading,
    fetchDocuments,
    fetchAllDocuments,
    fetchDocumentById,
    uploadDocument,
    updateDocument,
    deleteDocument,
    setPage
  }
})

