<template>
  <div class="document-detail-page">
    <el-card v-loading="loading">
      <template #header>
        <div class="card-header">
          <el-button @click="$router.back()">
            <el-icon><ArrowLeft /></el-icon>
            返回
          </el-button>
          <div class="header-actions">
            <el-button @click="handleEdit">编辑</el-button>
            <el-button type="danger" @click="handleDelete">删除</el-button>
          </div>
        </div>
      </template>
      
      <div v-if="document" class="document-info">
        <h1 class="document-title">{{ document.title }}</h1>
        
        <div class="document-meta">
          <el-tag v-if="categoryName" type="info">{{ categoryName }}</el-tag>
          <span class="meta-item">
            <el-icon><Document /></el-icon>
            {{ document.fileOriginalName }}
          </span>
          <span class="meta-item">
            <el-icon><FolderOpened /></el-icon>
            {{ formatFileSize(document.fileSize) }}
          </span>
          <span class="meta-item">
            <el-icon><View /></el-icon>
            浏览 {{ document.viewCount }} 次
          </span>
          <span class="meta-item">
            <el-icon><Download /></el-icon>
            下载 {{ document.downloadCount }} 次
          </span>
          <span class="meta-item">
            <el-icon><Clock /></el-icon>
            {{ formatTime(document.createdAt) }}
          </span>
        </div>
        
        <div v-if="document.description" class="document-description">
          <h3>描述</h3>
          <p>{{ document.description }}</p>
        </div>
        
        <div class="document-actions">
          <el-button type="success" size="large" @click="handleDownload">
            <el-icon><Download /></el-icon>
            下载文档
          </el-button>
        </div>
        
        <!-- 文档内容显示区域 -->
        <div class="document-content">
          <h3>文档内容</h3>
          
          <!-- PDF 预览 -->
          <div v-if="isPdf" class="preview-container">
            <iframe
              :src="document.fileUrl"
              frameborder="0"
              class="preview-iframe"
            ></iframe>
          </div>
          
          <!-- TXT 文本内容 -->
          <div v-else-if="isTxt" class="text-content-container">
            <pre v-if="textContent" class="text-content">{{ textContent }}</pre>
            <el-skeleton v-else :rows="10" animated />
          </div>
          
          <!-- DOCX 文档内容 -->
          <div v-else-if="isDocx" class="docx-content-container">
            <div v-if="htmlContent" class="docx-content" v-html="htmlContent"></div>
            <el-skeleton v-else :rows="10" animated />
          </div>
          
          <!-- DOC 等其他格式 -->
          <div v-else class="unsupported-format">
            <el-alert
              :title="`当前文档格式（${getFileExtension()}）无法在浏览器中直接预览`"
              type="info"
              :closable="false"
              show-icon
            >
              <template #default>
                <p>请点击上方"下载文档"按钮下载后查看</p>
              </template>
            </el-alert>
          </div>
        </div>
      </div>
      
      <el-empty v-else description="文档不存在" />
    </el-card>
    
    <!-- 编辑对话框 -->
    <el-dialog
      v-model="editDialogVisible"
      title="编辑文档"
      width="600px"
    >
      <el-form
        ref="editFormRef"
        :model="editForm"
        :rules="editRules"
        label-width="100px"
      >
        <el-form-item label="文档标题" prop="title">
          <el-input v-model="editForm.title" />
        </el-form-item>
        <el-form-item label="文档描述" prop="description">
          <el-input
            v-model="editForm.description"
            type="textarea"
            :rows="4"
          />
        </el-form-item>
        <el-form-item label="文档分类" prop="categoryId">
          <el-select
            v-model="editForm.categoryId"
            placeholder="请选择分类"
            clearable
            style="width: 100%"
          >
            <el-option
              v-for="category in categories"
              :key="category.id"
              :label="category.name"
              :value="category.id"
            />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="editDialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="loading" @click="handleSaveEdit">
          保存
        </el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script>
import { ref, computed, onMounted, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  ArrowLeft,
  Document,
  FolderOpened,
  View,
  Download,
  Clock
} from '@element-plus/icons-vue'
import { useDocumentStore } from '@/stores/document'
import { useCategoryStore } from '@/stores/category'
import { formatFileSize, formatTime } from '@/utils/format'
import mammoth from 'mammoth'
import axios from 'axios'

export default {
  name: 'DocumentDetailPage',
  components: {
    ArrowLeft,
    Document,
    FolderOpened,
    View,
    Download,
    Clock
  },
  setup() {
    const route = useRoute()
    const router = useRouter()
    const documentStore = useDocumentStore()
    const categoryStore = useCategoryStore()
    
    const editFormRef = ref(null)
    const editDialogVisible = ref(false)
    const textContent = ref('')
    const htmlContent = ref('')
    
    const loading = computed(() => documentStore.loading)
    const document = computed(() => documentStore.currentDocument)
    const categories = computed(() => categoryStore.categories)
    
    const editForm = ref({
      title: '',
      description: '',
      categoryId: null
    })
    
    const editRules = {
      title: [
        { required: true, message: '请输入文档标题', trigger: 'blur' }
      ]
    }
    
    const categoryName = computed(() => {
      if (!document.value?.categoryId) return ''
      const category = categories.value.find(c => c.id === document.value.categoryId)
      return category ? category.name : ''
    })
    
    const isPdf = computed(() => {
      return document.value?.fileType === 'application/pdf' ||
             document.value?.fileUrl?.toLowerCase().endsWith('.pdf')
    })
    
    const isTxt = computed(() => {
      return document.value?.fileType === 'text/plain' ||
             document.value?.fileUrl?.toLowerCase().endsWith('.txt')
    })
    
    const isDocx = computed(() => {
      return document.value?.fileType === 'application/vnd.openxmlformats-officedocument.wordprocessingml.document' ||
             document.value?.fileUrl?.toLowerCase().endsWith('.docx') ||
             document.value?.fileOriginalName?.toLowerCase().endsWith('.docx')
    })
    
    function getFileExtension() {
      if (!document.value?.fileOriginalName) return ''
      const parts = document.value.fileOriginalName.split('.')
      return parts.length > 1 ? parts[parts.length - 1].toUpperCase() : ''
    }
    
    async function loadTextContent() {
      if (!isTxt.value || !document.value?.fileUrl) return
      
      try {
        const response = await fetch(document.value.fileUrl)
        if (response.ok) {
          textContent.value = await response.text()
        } else {
          textContent.value = '无法加载文档内容'
        }
      } catch (error) {
        console.error('加载文本内容失败:', error)
        textContent.value = '加载文档内容失败'
      }
    }
    
    async function loadDocxContent() {
      if (!isDocx.value || !document.value?.fileUrl) return
      
      try {
        // 获取 token
        const token = localStorage.getItem('token')
        
        // 使用 axios 直接获取 ArrayBuffer（不使用封装的 request，避免响应拦截器处理）
        const response = await axios.get(document.value.fileUrl, {
          responseType: 'arraybuffer',
          headers: token ? {
            'Authorization': `Bearer ${token}`
          } : {}
        })
        
        // 使用 mammoth 将 DOCX 转换为 HTML
        const result = await mammoth.convertToHtml({ arrayBuffer: response.data })
        htmlContent.value = result.value
        
        // 如果有警告信息，可以记录到控制台
        if (result.messages.length > 0) {
          console.warn('DOCX 转换警告:', result.messages)
        }
      } catch (error) {
        console.error('加载 DOCX 内容失败:', error)
        htmlContent.value = '<p style="color: #f56c6c;">加载文档内容失败，请尝试下载后查看</p>'
        ElMessage.error('加载文档内容失败')
      }
    }
    
    onMounted(async () => {
      const id = parseInt(route.params.id)
      await Promise.all([
        categoryStore.fetchCategories(),
        documentStore.fetchDocumentById(id)
      ])
      
      // 如果 URL 中有 edit 参数，打开编辑对话框
      if (route.query.edit === 'true') {
        editDialogVisible.value = true
        if (document.value) {
          editForm.value = {
            title: document.value.title,
            description: document.value.description || '',
            categoryId: document.value.categoryId || null
          }
        }
      }
    })
    
    // 监听文档变化，根据文件类型加载内容
    watch(
      () => document.value,
      async (newDoc) => {
        if (!newDoc) return
        
        if (isTxt.value) {
          await loadTextContent()
        } else if (isDocx.value) {
          await loadDocxContent()
        }
      },
      { immediate: true }
    )
    
    function handleEdit() {
      if (document.value) {
        editForm.value = {
          title: document.value.title,
          description: document.value.description || '',
          categoryId: document.value.categoryId || null
        }
        editDialogVisible.value = true
      }
    }
    
    async function handleSaveEdit() {
      try {
        await editFormRef.value.validate()
        const id = parseInt(route.params.id)
        await documentStore.updateDocument(id, editForm.value)
        ElMessage.success('更新成功')
        editDialogVisible.value = false
        await documentStore.fetchDocumentById(id)
      } catch (error) {
        if (error.message) {
          ElMessage.error(error.message)
        }
      }
    }
    
    async function handleDelete() {
      try {
        await ElMessageBox.confirm(
          `确定要删除文档"${document.value.title}"吗？`,
          '删除确认',
          {
            confirmButtonText: '确定',
            cancelButtonText: '取消',
            type: 'warning'
          }
        )
        
        const id = parseInt(route.params.id)
        await documentStore.deleteDocument(id)
        ElMessage.success('删除成功')
        router.push('/')
      } catch (error) {
        if (error !== 'cancel') {
          ElMessage.error('删除失败')
        }
      }
    }
    
    function handleDownload() {
      if (document.value?.fileUrl) {
        window.open(document.value.fileUrl, '_blank')
      }
    }
    
    return {
      loading,
      document,
      categories,
      categoryName,
      isPdf,
      isTxt,
      isDocx,
      textContent,
      htmlContent,
      editDialogVisible,
      editForm,
      editRules,
      editFormRef,
      formatFileSize,
      formatTime,
      getFileExtension,
      handleEdit,
      handleSaveEdit,
      handleDelete,
      handleDownload
    }
  }
}
</script>

<style scoped>
.document-detail-page {
  max-width: 1200px;
  margin: 0 auto;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.document-info {
  padding: 20px 0;
}

.document-title {
  margin: 0 0 20px 0;
  font-size: 24px;
  font-weight: 600;
  color: #303133;
}

.document-meta {
  display: flex;
  flex-wrap: wrap;
  align-items: center;
  gap: 16px;
  margin-bottom: 24px;
  padding-bottom: 20px;
  border-bottom: 1px solid #e4e7ed;
}

.meta-item {
  display: flex;
  align-items: center;
  gap: 4px;
  color: #606266;
  font-size: 14px;
}

.document-description {
  margin-bottom: 24px;
}

.document-description h3 {
  margin: 0 0 12px 0;
  font-size: 16px;
  color: #303133;
}

.document-description p {
  margin: 0;
  color: #606266;
  line-height: 1.6;
}

.document-actions {
  display: flex;
  gap: 12px;
  margin-bottom: 24px;
}

.document-content {
  margin-top: 24px;
  padding-top: 24px;
  border-top: 1px solid #e4e7ed;
}

.document-content h3 {
  margin: 0 0 16px 0;
  font-size: 18px;
  font-weight: 600;
  color: #303133;
}

.preview-container {
  border: 1px solid #e4e7ed;
  border-radius: 4px;
  overflow: hidden;
  background: #f5f7fa;
}

.preview-iframe {
  width: 100%;
  min-height: 600px;
  height: calc(100vh - 400px);
  border: none;
  display: block;
}

.text-content-container {
  border: 1px solid #e4e7ed;
  border-radius: 4px;
  background: #fff;
  padding: 20px;
  max-height: calc(100vh - 400px);
  overflow: auto;
}

.text-content {
  margin: 0;
  font-family: 'Consolas', 'Monaco', 'Courier New', monospace;
  font-size: 14px;
  line-height: 1.6;
  color: #303133;
  white-space: pre-wrap;
  word-wrap: break-word;
}

.docx-content-container {
  border: 1px solid #e4e7ed;
  border-radius: 4px;
  background: #fff;
  padding: 20px;
  max-height: calc(100vh - 400px);
  overflow: auto;
}

.docx-content {
  font-family: 'Microsoft YaHei', 'PingFang SC', 'Helvetica Neue', Arial, sans-serif;
  font-size: 14px;
  line-height: 1.8;
  color: #303133;
}

.docx-content :deep(p) {
  margin: 0 0 12px 0;
}

.docx-content :deep(h1),
.docx-content :deep(h2),
.docx-content :deep(h3),
.docx-content :deep(h4),
.docx-content :deep(h5),
.docx-content :deep(h6) {
  margin: 20px 0 12px 0;
  font-weight: 600;
  color: #303133;
}

.docx-content :deep(ul),
.docx-content :deep(ol) {
  margin: 12px 0;
  padding-left: 30px;
}

.docx-content :deep(li) {
  margin: 6px 0;
}

.docx-content :deep(table) {
  width: 100%;
  border-collapse: collapse;
  margin: 12px 0;
}

.docx-content :deep(table td),
.docx-content :deep(table th) {
  border: 1px solid #e4e7ed;
  padding: 8px;
}

.docx-content :deep(img) {
  max-width: 100%;
  height: auto;
  margin: 12px 0;
}

.unsupported-format {
  padding: 20px;
}
</style>

