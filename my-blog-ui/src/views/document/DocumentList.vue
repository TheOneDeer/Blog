<template>
  <div class="document-list-page">
    <div class="page-header">
      <h2>我的文档</h2>
    </div>
    
    <!-- 加载状态 -->
    <div v-if="loading && documents.length === 0" class="loading-container">
      <el-skeleton :rows="6" animated />
    </div>
    
    <!-- 文档列表 -->
    <div v-else-if="documents.length > 0" class="document-grid">
      <DocumentCard
        v-for="doc in documents"
        :key="doc.id"
        :document="doc"
        @view="handleView"
        @edit="handleEdit"
        @delete="handleDelete"
      />
    </div>
    
    <!-- 空状态 -->
    <el-empty v-else description="还没有文档，快去上传吧！">
      <el-button type="primary" @click="$router.push('/documents/upload')">
        上传文档
      </el-button>
    </el-empty>
    
    <!-- 分页 -->
    <div v-if="total > 0" class="pagination-container">
      <el-pagination
        v-model:current-page="currentPage"
        :page-size="pageSize"
        :total="total"
        layout="total, prev, pager, next, jumper"
        @current-change="handlePageChange"
      />
    </div>
  </div>
</template>

<script>
import { computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useDocumentStore } from '@/stores/document'
import { useCategoryStore } from '@/stores/category'
import DocumentCard from '@/components/document/DocumentCard.vue'

export default {
  name: 'DocumentListPage',
  components: {
    DocumentCard
  },
  setup() {
    const router = useRouter()
    const documentStore = useDocumentStore()
    const categoryStore = useCategoryStore()
    
    const documents = computed(() => documentStore.documents)
    const total = computed(() => documentStore.total)
    const pageSize = computed(() => documentStore.pageSize)
    const currentPage = computed({
      get: () => documentStore.page,
      set: (val) => documentStore.setPage(val)
    })
    const loading = computed(() => documentStore.loading)
    
    onMounted(async () => {
      await categoryStore.fetchCategories()
      await documentStore.fetchDocuments()
    })
    
    function handlePageChange(page) {
      documentStore.setPage(page)
      documentStore.fetchDocuments()
    }
    
    function handleView(id) {
      router.push(`/documents/${id}`)
    }
    
    function handleEdit(id) {
      router.push(`/documents/${id}?edit=true`)
    }
    
    function handleDelete() {
      // 删除操作在 DocumentCard 组件中处理
      documentStore.fetchDocuments()
    }
    
    return {
      documents,
      total,
      pageSize,
      currentPage,
      loading,
      handlePageChange,
      handleView,
      handleEdit,
      handleDelete
    }
  }
}
</script>

<style scoped>
.document-list-page {
  max-width: 1400px;
  margin: 0 auto;
}

.page-header {
  margin-bottom: 20px;
}

.page-header h2 {
  margin: 0;
  color: #303133;
}

.loading-container {
  padding: 20px;
}

.document-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(300px, 1fr));
  gap: 20px;
  margin-bottom: 20px;
}

.pagination-container {
  display: flex;
  justify-content: center;
  margin-top: 30px;
}

@media (max-width: 768px) {
  .document-grid {
    grid-template-columns: 1fr;
  }
}
</style>

