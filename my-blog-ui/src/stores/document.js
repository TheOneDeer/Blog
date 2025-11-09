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
  const loading = ref(false)
  
  // actions
  async function fetchDocuments(reset = false) {
    if (reset) page.value = 1
    loading.value = true
    try {
      const response = await documentApi.getList(page.value, pageSize.value)
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
    loading,
    fetchDocuments,
    fetchDocumentById,
    uploadDocument,
    updateDocument,
    deleteDocument,
    setPage
  }
})

