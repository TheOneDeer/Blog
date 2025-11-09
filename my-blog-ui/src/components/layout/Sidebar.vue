<template>
  <el-aside class="app-sidebar" width="250px">
    <div class="sidebar-header">
      <div class="home-link" @click="handleHome">
        <el-icon><House /></el-icon>
        <span>知识仓库</span>
      </div>
    </div>
    
    <el-menu
      :default-active="activeCategoryId"
      class="category-menu"
      @select="handleCategorySelect"
    >
      <el-menu-item index="0" :class="{ 'is-active': activeCategoryId === '0' }">
        <el-icon><Document /></el-icon>
        <span>全部文档</span>
      </el-menu-item>
      
      <el-menu-item
        v-for="category in categories"
        :key="category.id"
        :index="String(category.id)"
        :class="{ 'is-active': activeCategoryId === String(category.id) }"
      >
        <el-icon><FolderOpened /></el-icon>
        <span>{{ category.name }}</span>
      </el-menu-item>
    </el-menu>
    
    <div v-if="categories.length === 0 && !loading" class="empty-categories">
      <el-empty description="暂无分类" :image-size="80">
        <el-button type="primary" size="small" @click="$router.push('/categories')">
          去创建
        </el-button>
      </el-empty>
    </div>
  </el-aside>
</template>

<script>
import { computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { House, Document, FolderOpened } from '@element-plus/icons-vue'
import { useCategoryStore } from '@/stores/category'

export default {
  name: 'AppSidebar',
  components: {
    House,
    Document,
    FolderOpened
  },
  setup() {
    const route = useRoute()
    const router = useRouter()
    const categoryStore = useCategoryStore()
    
    const categories = computed(() => categoryStore.categories)
    const loading = computed(() => categoryStore.loading)
    
    const activeCategoryId = computed(() => {
      const categoryId = route.query.categoryId
      return categoryId ? String(categoryId) : '0'
    })
    
    onMounted(async () => {
      await categoryStore.fetchCategories()
    })
    
    function handleHome() {
      router.push('/')
    }
    
    function handleCategorySelect(categoryId) {
      const currentPath = route.path
      if (categoryId === '0') {
        // 全部文档
        router.push({ path: currentPath, query: {} })
      } else {
        // 按分类筛选
        router.push({ path: currentPath, query: { categoryId } })
      }
    }
    
    return {
      categories,
      loading,
      activeCategoryId,
      handleHome,
      handleCategorySelect
    }
  }
}
</script>

<style scoped>
.app-sidebar {
  background: #fafafa;
  border-right: 1px solid #e4e7ed;
  height: calc(100vh - 60px);
  overflow-y: auto;
  position: fixed;
  left: 0;
  top: 60px;
  z-index: 999;
}

.sidebar-header {
  padding: 16px 20px;
  border-bottom: 1px solid #e4e7ed;
  background: white;
}

.home-link {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 16px;
  font-weight: 600;
  color: #303133;
  cursor: pointer;
  transition: color 0.3s;
}

.home-link:hover {
  color: #409eff;
}

.category-menu {
  border: none;
  background: transparent;
  padding: 8px 0;
}

.category-menu :deep(.el-menu-item) {
  height: 44px;
  line-height: 44px;
  margin: 4px 8px;
  border-radius: 4px;
  color: #606266;
}

.category-menu :deep(.el-menu-item:hover) {
  background-color: #ecf5ff;
  color: #409eff;
}

.category-menu :deep(.el-menu-item.is-active) {
  background-color: #fff5f5;
  color: #f56c6c;
  font-weight: 500;
  border-left: 3px solid #f56c6c;
}

.category-menu :deep(.el-menu-item .el-icon) {
  margin-right: 8px;
  font-size: 16px;
}

.empty-categories {
  padding: 20px;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .app-sidebar {
    display: none;
  }
}
</style>

