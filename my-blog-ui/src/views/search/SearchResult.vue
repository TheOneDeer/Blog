<template>
  <div class="search-result-page">
    <div class="page-header">
      <div>
        <h2>搜索结果</h2>
        <div class="search-info" v-if="keyword">
          <span>关键词：<strong>{{ keyword }}</strong></span>
          <span v-if="total > 0">共找到 <strong>{{ total }}</strong> 条结果</span>
        </div>
      </div>
    </div>
    
    <!-- 加载状态 -->
    <div v-if="loading && results.length === 0" class="loading-container">
      <el-skeleton :rows="6" animated />
    </div>
    
    <!-- 搜索结果 -->
    <div v-else-if="results.length > 0" class="document-grid">
      <DocumentCard
        v-for="doc in results"
        :key="doc.id"
        :document="doc"
        @view="handleView"
        @edit="handleEdit"
        @delete="handleDelete"
      />
    </div>
    
    <!-- 空状态 -->
    <el-empty v-else-if="keyword" description="没有找到相关文档">
      <el-button type="primary" @click="$router.push('/')">返回文档列表</el-button>
    </el-empty>
    
    <!-- 未搜索状态 -->
    <el-empty v-else description="请输入搜索关键词">
      <el-button type="primary" @click="$router.push('/')">返回文档列表</el-button>
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
import { computed, onMounted, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useSearchStore } from '@/stores/search'
import DocumentCard from '@/components/document/DocumentCard.vue'

export default {
  name: 'SearchResultPage',
  components: {
    DocumentCard
  },
  setup() {
    const route = useRoute()
    const router = useRouter()
    const searchStore = useSearchStore()
    
    const results = computed(() => searchStore.results)
    const keyword = computed(() => searchStore.keyword || route.query.keyword)
    const total = computed(() => searchStore.total)
    const pageSize = computed(() => searchStore.pageSize)
    const currentPage = computed({
      get: () => searchStore.page,
      set: (val) => searchStore.setPage(val)
    })
    const loading = computed(() => searchStore.loading)
    
    onMounted(async () => {
      const searchKeyword = route.query.keyword
      if (searchKeyword) {
        await searchStore.search(searchKeyword)
      }
    })
    
    // 监听路由变化
    watch(
      () => route.query.keyword,
      async (newKeyword) => {
        if (newKeyword) {
          await searchStore.search(newKeyword, true)
        } else {
          searchStore.clearResults()
        }
      }
    )
    
    function handlePageChange(page) {
      searchStore.setPage(page)
      if (keyword.value) {
        searchStore.search(keyword.value)
      }
    }
    
    function handleView(id) {
      router.push(`/documents/${id}`)
    }
    
    function handleEdit(id) {
      router.push(`/documents/${id}?edit=true`)
    }
    
    function handleDelete() {
      // 删除后刷新搜索结果
      if (keyword.value) {
        searchStore.search(keyword.value)
      }
    }
    
    return {
      results,
      keyword,
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
.search-result-page {
  max-width: 1400px;
  margin: 0 auto;
}

.page-header {
  margin-bottom: 20px;
}

.page-header h2 {
  margin: 0 0 12px 0;
  color: #303133;
}

.search-info {
  display: flex;
  gap: 20px;
  color: #606266;
  font-size: 14px;
}

.search-info strong {
  color: #409eff;
  font-weight: 600;
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

