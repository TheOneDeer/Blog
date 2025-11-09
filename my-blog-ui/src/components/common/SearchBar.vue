<template>
  <div class="search-bar">
    <el-input
      v-model="searchKeyword"
      placeholder="搜索文档..."
      clearable
      @input="handleInput"
      @keyup.enter="handleSearch"
      @clear="handleClear"
    >
      <template #prefix>
        <el-icon><Search /></el-icon>
      </template>
      <template #suffix>
        <el-button
          v-if="searchKeyword"
          type="primary"
          size="small"
          @click="handleSearch"
        >
          搜索
        </el-button>
      </template>
    </el-input>
  </div>
</template>

<script>
import { ref, watch } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { Search } from '@element-plus/icons-vue'
import { debounce } from 'lodash-es'

export default {
  name: 'SearchBar',
  components: {
    Search
  },
  setup() {
    const router = useRouter()
    const route = useRoute()
    const searchKeyword = ref(route.query.keyword || '')
    
    // 防抖搜索（500ms）
    const debouncedSearch = debounce((keyword) => {
      if (keyword.trim()) {
        router.push({
          path: '/search',
          query: { keyword: keyword.trim() }
        })
      }
    }, 500)
    
    function handleInput() {
      // 实时搜索（防抖）
      if (searchKeyword.value.trim()) {
        debouncedSearch(searchKeyword.value)
      }
    }
    
    function handleSearch() {
      if (searchKeyword.value.trim()) {
        router.push({
          path: '/search',
          query: { keyword: searchKeyword.value.trim() }
        })
      }
    }
    
    function handleClear() {
      searchKeyword.value = ''
      if (route.path === '/search') {
        router.push('/')
      }
    }
    
    // 监听路由变化，同步搜索关键词
    watch(
      () => route.query.keyword,
      (newKeyword) => {
        if (newKeyword !== searchKeyword.value) {
          searchKeyword.value = newKeyword || ''
        }
      }
    )
    
    return {
      searchKeyword,
      handleInput,
      handleSearch,
      handleClear
    }
  }
}
</script>

<style scoped>
.search-bar {
  width: 300px;
}

.search-bar :deep(.el-input__wrapper) {
  padding-right: 8px;
}
</style>

