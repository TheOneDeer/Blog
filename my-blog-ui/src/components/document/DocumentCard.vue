<template>
  <el-card
    :body-style="{ padding: '0' }"
    class="document-card"
    shadow="hover"
  >
    <div class="card-thumbnail" @click="handleView">
      <img
        v-if="document.thumbnail"
        :src="document.thumbnail"
        :alt="document.title"
      />
      <div v-else class="thumbnail-placeholder">
        <el-icon :size="48"><Document /></el-icon>
        <span>{{ getFileTypeIcon() }}</span>
      </div>
    </div>
    
    <div class="card-content">
      <h3 class="card-title" @click="handleView">{{ document.title }}</h3>
      <p class="card-description">{{ document.description || '暂无描述' }}</p>
      
      <div class="card-meta">
        <el-tag v-if="categoryName" size="small" type="info">{{ categoryName }}</el-tag>
        <span class="file-size">{{ formatFileSize(document.fileSize) }}</span>
        <span class="upload-time">{{ formatTime(document.createdAt) }}</span>
      </div>
      
      <div class="card-actions">
        <el-button type="primary" size="small" @click.stop="handleView">
          查看
        </el-button>
        <el-button size="small" @click.stop="handleEdit">
          编辑
        </el-button>
        <el-button type="danger" size="small" @click.stop="handleDelete">
          删除
        </el-button>
      </div>
    </div>
  </el-card>
</template>

<script>
import { computed } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Document } from '@element-plus/icons-vue'
import { formatFileSize, formatTime } from '@/utils/format'
import { useDocumentStore } from '@/stores/document'
import { useCategoryStore } from '@/stores/category'

export default {
  name: 'DocumentCard',
  components: {
    Document
  },
  props: {
    document: {
      type: Object,
      required: true
    }
  },
  emits: ['view', 'edit', 'delete'],
  setup(props, { emit }) {
    const router = useRouter()
    const documentStore = useDocumentStore()
    const categoryStore = useCategoryStore()
    
    const categoryName = computed(() => {
      if (!props.document.categoryId) return ''
      const category = categoryStore.categories.find(c => c.id === props.document.categoryId)
      return category ? category.name : ''
    })
    
    function getFileTypeIcon() {
      const ext = props.document.fileOriginalName?.split('.').pop()?.toLowerCase()
      const iconMap = {
        'pdf': 'PDF',
        'docx': 'DOCX',
        'doc': 'DOC',
        'txt': 'TXT'
      }
      return iconMap[ext] || 'FILE'
    }
    
    function handleView() {
      emit('view', props.document.id)
      router.push(`/documents/${props.document.id}`)
    }
    
    function handleEdit() {
      emit('edit', props.document.id)
      router.push(`/documents/${props.document.id}?edit=true`)
    }
    
    async function handleDelete() {
      try {
        await ElMessageBox.confirm(
          `确定要删除文档"${props.document.title}"吗？`,
          '删除确认',
          {
            confirmButtonText: '确定',
            cancelButtonText: '取消',
            type: 'warning'
          }
        )
        
        await documentStore.deleteDocument(props.document.id)
        ElMessage.success('删除成功')
        emit('delete', props.document.id)
      } catch (error) {
        if (error !== 'cancel') {
          ElMessage.error('删除失败')
        }
      }
    }
    
    return {
      categoryName,
      formatFileSize,
      formatTime,
      getFileTypeIcon,
      handleView,
      handleEdit,
      handleDelete
    }
  }
}
</script>

<style scoped>
.document-card {
  cursor: pointer;
  transition: transform 0.3s, box-shadow 0.3s;
  height: 100%;
  display: flex;
  flex-direction: column;
}

.document-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
}

.card-thumbnail {
  width: 100%;
  height: 180px;
  background: #f5f7fa;
  display: flex;
  align-items: center;
  justify-content: center;
  overflow: hidden;
}

.card-thumbnail img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.thumbnail-placeholder {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  color: #909399;
  gap: 8px;
}

.card-content {
  padding: 16px;
  flex: 1;
  display: flex;
  flex-direction: column;
}

.card-title {
  margin: 0 0 8px 0;
  font-size: 16px;
  font-weight: 600;
  color: #303133;
  cursor: pointer;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.card-title:hover {
  color: #409eff;
}

.card-description {
  margin: 0 0 12px 0;
  font-size: 14px;
  color: #606266;
  line-height: 1.5;
  overflow: hidden;
  text-overflow: ellipsis;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  flex: 1;
}

.card-meta {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 12px;
  font-size: 12px;
  color: #909399;
}

.file-size,
.upload-time {
  margin-left: auto;
}

.card-actions {
  display: flex;
  gap: 8px;
  margin-top: auto;
}
</style>

