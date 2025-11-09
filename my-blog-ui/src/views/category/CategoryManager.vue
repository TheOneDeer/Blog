<template>
  <div class="category-manager-page">
    <div class="page-header">
      <h2>分类管理</h2>
      <el-button type="primary" @click="handleCreate">
        <el-icon><Plus /></el-icon>
        新建分类
      </el-button>
    </div>
    
    <!-- 加载状态 -->
    <div v-if="loading && categories.length === 0" class="loading-container">
      <el-skeleton :rows="6" animated />
    </div>
    
    <!-- 分类列表 -->
    <el-card v-else-if="categories.length > 0">
      <el-table :data="categories" style="width: 100%">
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="name" label="分类名称" min-width="150">
          <template #default="scope">
            <el-tag type="info">{{ scope.row.name }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="description" label="描述" min-width="200" show-overflow-tooltip />
        <el-table-column prop="parentId" label="父分类ID" width="120">
          <template #default="scope">
            <span v-if="scope.row.parentId === 0 || !scope.row.parentId">-</span>
            <span v-else>{{ scope.row.parentId }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="sortOrder" label="排序" width="100" />
        <el-table-column prop="createdAt" label="创建时间" width="180">
          <template #default="scope">
            {{ formatTime(scope.row.createdAt) }}
          </template>
        </el-table-column>
        <el-table-column label="操作" width="180" fixed="right">
          <template #default="scope">
            <el-button size="small" @click="handleEdit(scope.row)">
              编辑
            </el-button>
            <el-button
              type="danger"
              size="small"
              @click="handleDelete(scope.row)"
            >
              删除
            </el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>
    
    <!-- 空状态 -->
    <el-empty v-else description="还没有分类，快去创建吧！">
      <el-button type="primary" @click="handleCreate">新建分类</el-button>
    </el-empty>
    
    <!-- 创建/编辑对话框 -->
    <el-dialog
      v-model="dialogVisible"
      :title="dialogTitle"
      width="500px"
    >
      <el-form
        ref="categoryFormRef"
        :model="categoryForm"
        :rules="categoryRules"
        label-width="100px"
      >
        <el-form-item label="分类名称" prop="name">
          <el-input
            v-model="categoryForm.name"
            placeholder="请输入分类名称"
            maxlength="50"
            show-word-limit
          />
        </el-form-item>
        <el-form-item label="分类描述" prop="description">
          <el-input
            v-model="categoryForm.description"
            type="textarea"
            :rows="3"
            placeholder="请输入分类描述（可选）"
            maxlength="200"
            show-word-limit
          />
        </el-form-item>
        <el-form-item label="父分类" prop="parentId">
          <el-select
            v-model="categoryForm.parentId"
            placeholder="请选择父分类（可选）"
            clearable
            style="width: 100%"
          >
            <el-option
              v-for="cat in availableParentCategories"
              :key="cat.id"
              :label="cat.name"
              :value="cat.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="排序顺序" prop="sortOrder">
          <el-input-number
            v-model="categoryForm.sortOrder"
            :min="0"
            :max="999"
            placeholder="排序顺序"
            style="width: 100%"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="loading" @click="handleSubmit">
          确定
        </el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script>
import { ref, computed, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus } from '@element-plus/icons-vue'
import { useCategoryStore } from '@/stores/category'
import { formatTime } from '@/utils/format'

export default {
  name: 'CategoryManagerPage',
  components: {
    Plus
  },
  setup() {
    const categoryStore = useCategoryStore()
    
    const categoryFormRef = ref(null)
    const dialogVisible = ref(false)
    const isEdit = ref(false)
    const currentCategoryId = ref(null)
    
    const categories = computed(() => categoryStore.categories)
    const loading = computed(() => categoryStore.loading)
    
    const categoryForm = ref({
      name: '',
      description: '',
      parentId: 0,
      sortOrder: 0
    })
    
    const categoryRules = {
      name: [
        { required: true, message: '请输入分类名称', trigger: 'blur' },
        { max: 50, message: '分类名称长度不能超过50字符', trigger: 'blur' }
      ]
    }
    
    const dialogTitle = computed(() => {
      return isEdit.value ? '编辑分类' : '新建分类'
    })
    
    // 可选的父分类（排除当前编辑的分类，避免循环引用）
    const availableParentCategories = computed(() => {
      if (isEdit.value && currentCategoryId.value) {
        return categories.value.filter(cat => cat.id !== currentCategoryId.value)
      }
      return categories.value
    })
    
    onMounted(async () => {
      await categoryStore.fetchCategories()
    })
    
    function handleCreate() {
      isEdit.value = false
      currentCategoryId.value = null
      categoryForm.value = {
        name: '',
        description: '',
        parentId: 0,
        sortOrder: 0
      }
      dialogVisible.value = true
    }
    
    function handleEdit(category) {
      isEdit.value = true
      currentCategoryId.value = category.id
      categoryForm.value = {
        name: category.name,
        description: category.description || '',
        parentId: category.parentId || 0,
        sortOrder: category.sortOrder || 0
      }
      dialogVisible.value = true
    }
    
    async function handleSubmit() {
      try {
        await categoryFormRef.value.validate()
        
        const formData = {
          name: categoryForm.value.name,
          description: categoryForm.value.description || undefined,
          parentId: categoryForm.value.parentId || 0,
          sortOrder: categoryForm.value.sortOrder || 0
        }
        
        if (isEdit.value) {
          await categoryStore.updateCategory(currentCategoryId.value, formData)
          ElMessage.success('更新成功')
        } else {
          await categoryStore.createCategory(formData)
          ElMessage.success('创建成功')
        }
        
        dialogVisible.value = false
      } catch (error) {
        if (error.message) {
          ElMessage.error(error.message)
        }
      }
    }
    
    async function handleDelete(category) {
      try {
        await ElMessageBox.confirm(
          `确定要删除分类"${category.name}"吗？`,
          '删除确认',
          {
            confirmButtonText: '确定',
            cancelButtonText: '取消',
            type: 'warning'
          }
        )
        
        await categoryStore.deleteCategory(category.id)
        ElMessage.success('删除成功')
      } catch (error) {
        if (error !== 'cancel') {
          ElMessage.error('删除失败')
        }
      }
    }
    
    return {
      categories,
      loading,
      dialogVisible,
      categoryForm,
      categoryRules,
      categoryFormRef,
      dialogTitle,
      availableParentCategories,
      formatTime,
      handleCreate,
      handleEdit,
      handleSubmit,
      handleDelete
    }
  }
}
</script>

<style scoped>
.category-manager-page {
  max-width: 1400px;
  margin: 0 auto;
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.page-header h2 {
  margin: 0;
  color: #303133;
}

.loading-container {
  padding: 20px;
}
</style>

