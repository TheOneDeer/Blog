<template>
  <div class="document-list-page">
    <div class="page-header">
      <div>
        <h2>{{ currentCategoryName }}</h2>
        <p v-if="currentCategoryName !== '全部文档'" class="category-description">
          {{ getCategoryDescription() }}
        </p>
      </div>
    </div>
    
    <!-- 加载状态 -->
    <div v-if="loading && documents.length === 0" class="loading-container">
      <el-skeleton :rows="6" animated />
    </div>
    
    <!-- 文档列表 -->
    <div v-else-if="filteredDocuments.length > 0" class="document-grid">
      <DocumentCard
        v-for="doc in filteredDocuments"
        :key="doc.id"
        :document="doc"
        @view="handleView"
        @edit="handleEdit"
        @delete="handleDelete"
      />
    </div>
    
    <!-- 空状态 -->
    <el-empty v-else-if="documents.length === 0" description="还没有文档，快去上传吧！">
      <el-button type="primary" @click="$router.push('/documents/upload')">
        上传文档
      </el-button>
    </el-empty>
    
    <!-- 分类筛选空状态 -->
    <el-empty v-else description="该分类下暂无文档">
      <el-button @click="$router.push({ query: {} })">查看全部文档</el-button>
    </el-empty>
    
    <!-- 分页 - 暂时隐藏，因为前端筛选不支持分页 -->
    <!-- <div v-if="filteredTotal > 0" class="pagination-container">
      <el-pagination
        v-model:current-page="currentPage"
        :page-size="pageSize"
        :total="filteredTotal"
        layout="total, prev, pager, next, jumper"
        @current-change="handlePageChange"
      />
    </div> -->
  </div>
</template>

<script>
import { computed, onMounted, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useDocumentStore } from '@/stores/document'
import { useCategoryStore } from '@/stores/category'
import DocumentCard from '@/components/document/DocumentCard.vue'

export default {
  name: 'DocumentListPage',
  components: {
    DocumentCard
  },
  setup() {
    const route = useRoute()
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
    
    // 获取当前分类名称
    const currentCategoryName = computed(() => {
      const categoryId = route.query.categoryId
      if (!categoryId) return '全部文档'
      const category = categoryStore.categories.find(c => c.id === parseInt(categoryId))
      return category ? category.name : '全部文档'
    })
    
    async function loadDocuments() {
      // 加载所有文档（用于前端筛选）
      // 使用 fetchDocuments 并设置较大的 pageSize 来加载更多文档
      // 如果文档很多，可以考虑改为后端筛选
      documentStore.pageSize = 1000
      await documentStore.fetchDocuments(true, null)
    }
    
    onMounted(async () => {
      await categoryStore.fetchCategories()
      await loadDocuments()
    })
    
    // 监听路由变化，自动筛选
    watch(
      () => route.query.categoryId,
      () => {
        // 分类筛选在前端进行
      },
      { immediate: true }
    )
    
    // 根据分类筛选文档
    const filteredDocuments = computed(() => {
      const categoryId = route.query.categoryId
      if (!categoryId) {
        return documents.value
      }
      const filterId = parseInt(categoryId)
      return documents.value.filter(doc => doc.categoryId === filterId)
    })
    
    // 筛选后的总数
    const filteredTotal = computed(() => {
      return filteredDocuments.value.length
    })
    
    function handlePageChange(page) {
      documentStore.setPage(page)
      const categoryId = route.query.categoryId ? parseInt(route.query.categoryId) : null
      documentStore.fetchDocuments(false, categoryId)
    }
    
    function handleView(id) {
      router.push(`/documents/${id}`)
    }
    
    function handleEdit(id) {
      router.push(`/documents/${id}?edit=true`)
    }
    
    function handleDelete() {
      // 删除操作在 DocumentCard 组件中处理
      loadDocuments()
    }
    
    function getCategoryDescription() {
      const categoryId = route.query.categoryId
      if (!categoryId) return ''
      const category = categoryStore.categories.find(c => c.id === parseInt(categoryId))
      return category?.description || ''
    }
    
    return {
      documents,
      filteredDocuments,
      total,
      filteredTotal,
      pageSize,
      currentPage,
      loading,
      currentCategoryName,
      handlePageChange,
      handleView,
      handleEdit,
      handleDelete,
      getCategoryDescription
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
  margin: 0 0 8px 0;
  color: #303133;
  font-size: 24px;
  font-weight: 600;
}

.category-description {
  margin: 0;
  color: #606266;
  font-size: 14px;
  line-height: 1.6;
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

