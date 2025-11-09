<template>
  <div class="document-upload-page">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>上传文档</span>
          <el-button @click="$router.back()">返回</el-button>
        </div>
      </template>
      
      <el-form
        ref="uploadFormRef"
        :model="uploadForm"
        :rules="uploadRules"
        label-width="100px"
        size="large"
      >
        <el-form-item label="选择文件" required>
          <el-upload
            ref="uploadRef"
            :auto-upload="false"
            :limit="1"
            :on-change="handleFileChange"
            :on-remove="handleFileRemove"
            accept=".pdf,.docx,.doc,.txt"
            drag
          >
            <el-icon class="el-icon--upload"><UploadFilled /></el-icon>
            <div class="el-upload__text">
              将文件拖到此处，或<em>点击上传</em>
            </div>
            <template #tip>
              <div class="el-upload__tip">
                支持 PDF、DOCX、DOC、TXT 格式，最大 100MB
              </div>
            </template>
          </el-upload>
          
          <div v-if="selectedFile" class="file-info">
            <el-icon><Document /></el-icon>
            <span>{{ selectedFile.name }}</span>
            <span class="file-size">({{ formatFileSize(selectedFile.size) }})</span>
          </div>
        </el-form-item>
        
        <el-form-item label="文档标题" prop="title" required>
          <el-input
            v-model="uploadForm.title"
            placeholder="请输入文档标题"
            maxlength="100"
            show-word-limit
          />
        </el-form-item>
        
        <el-form-item label="文档描述" prop="description">
          <el-input
            v-model="uploadForm.description"
            type="textarea"
            :rows="4"
            placeholder="请输入文档描述（可选）"
            maxlength="500"
            show-word-limit
          />
        </el-form-item>
        
        <el-form-item label="文档分类" prop="categoryId">
          <el-select
            v-model="uploadForm.categoryId"
            placeholder="请选择分类（可选）"
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
        
        <el-form-item>
          <el-button
            type="primary"
            :loading="loading"
            @click="handleUpload"
          >
            上传文档
          </el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script>
import { reactive, ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { UploadFilled, Document } from '@element-plus/icons-vue'
import { useDocumentStore } from '@/stores/document'
import { useCategoryStore } from '@/stores/category'
import { formatFileSize } from '@/utils/format'

export default {
  name: 'DocumentUploadPage',
  components: {
    UploadFilled,
    Document
  },
  setup() {
    const router = useRouter()
    const documentStore = useDocumentStore()
    const categoryStore = useCategoryStore()
    
    const uploadFormRef = ref(null)
    const uploadRef = ref(null)
    const selectedFile = ref(null)
    
    const loading = computed(() => documentStore.loading)
    const categories = computed(() => categoryStore.categories)
    
    const uploadForm = reactive({
      title: '',
      description: '',
      categoryId: null
    })
    
    const uploadRules = {
      title: [
        { required: true, message: '请输入文档标题', trigger: 'blur' },
        { max: 100, message: '标题长度不能超过100字符', trigger: 'blur' }
      ]
    }
    
    onMounted(async () => {
      await categoryStore.fetchCategories()
    })
    
    function handleFileChange(file) {
      // 获取实际的文件对象
      const rawFile = file.raw || file
      
      if (!rawFile) {
        return false
      }
      
      // 验证文件类型
      const allowedTypes = ['.pdf', '.docx', '.doc', '.txt']
      const fileName = rawFile.name.toLowerCase()
      const isValidType = allowedTypes.some(type => fileName.endsWith(type))
      
      if (!isValidType) {
        ElMessage.error('不支持的文件类型，仅支持 PDF、DOCX、DOC、TXT 格式')
        uploadRef.value.clearFiles()
        selectedFile.value = null
        return false
      }
      
      // 验证文件大小（100MB）
      const maxSize = 100 * 1024 * 1024
      if (rawFile.size > maxSize) {
        ElMessage.error('文件大小不能超过100MB')
        uploadRef.value.clearFiles()
        selectedFile.value = null
        return false
      }
      
      // 保存文件对象
      selectedFile.value = rawFile
      
      // 如果标题为空，使用文件名（不含扩展名）作为标题
      if (!uploadForm.title) {
        const nameWithoutExt = rawFile.name.replace(/\.[^/.]+$/, '')
        uploadForm.title = nameWithoutExt
      }
      
      return true
    }
    
    function handleFileRemove() {
      selectedFile.value = null
    }
    
    async function handleUpload() {
      // 先检查文件是否已选择
      if (!selectedFile.value) {
        ElMessage.warning('请选择要上传的文件')
        return
      }
      
      // 验证表单（只验证标题等字段）
      try {
        await uploadFormRef.value.validate()
      } catch (error) {
        // 表单验证失败，不继续执行
        return
      }
      
      // 创建 FormData
      const formData = new FormData()
      formData.append('file', selectedFile.value)
      formData.append('title', uploadForm.title)
      if (uploadForm.description) {
        formData.append('description', uploadForm.description)
      }
      if (uploadForm.categoryId) {
        formData.append('categoryId', uploadForm.categoryId.toString())
      }
      
      // 上传文件
      try {
        await documentStore.uploadDocument(formData)
        ElMessage.success('上传成功')
        router.push('/')
      } catch (error) {
        if (error.message) {
          ElMessage.error(error.message)
        } else {
          ElMessage.error('上传失败，请重试')
        }
      }
    }
    
    function handleReset() {
      uploadFormRef.value.resetFields()
      uploadRef.value.clearFiles()
      selectedFile.value = null
    }
    
    return {
      uploadFormRef,
      uploadRef,
      selectedFile,
      uploadForm,
      uploadRules,
      loading,
      categories,
      formatFileSize,
      handleFileChange,
      handleFileRemove,
      handleUpload,
      handleReset
    }
  }
}
</script>

<style scoped>
.document-upload-page {
  max-width: 800px;
  margin: 0 auto;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.file-info {
  margin-top: 10px;
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 8px;
  background: #f5f7fa;
  border-radius: 4px;
}

.file-size {
  color: #909399;
  font-size: 12px;
}
</style>

