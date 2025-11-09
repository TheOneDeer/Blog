<template>
  <el-container class="app-layout">
    <el-header class="app-header">
      <div class="header-left">
        <div class="logo" @click="$router.push('/')">
          <el-icon :size="24"><Document /></el-icon>
          <span>个人博客系统</span>
        </div>
      </div>
      
      <div class="header-center">
        <SearchBar />
      </div>
      
      <div class="header-right">
        <el-button @click="$router.push('/documents/upload')" type="primary">
          <el-icon><Plus /></el-icon>
          上传文档
        </el-button>
        <el-button @click="$router.push('/categories')">
          <el-icon><FolderOpened /></el-icon>
          分类管理
        </el-button>
        
        <el-dropdown @command="handleCommand" trigger="click">
          <div class="user-menu">
            <el-avatar :size="32" :src="userInfo?.avatar">
              {{ userInfo?.username?.charAt(0)?.toUpperCase() }}
            </el-avatar>
            <span class="username">{{ userInfo?.username }}</span>
            <el-icon><ArrowDown /></el-icon>
          </div>
          <template #dropdown>
            <el-dropdown-menu>
              <el-dropdown-item command="logout">
                <el-icon><SwitchButton /></el-icon>
                退出登录
              </el-dropdown-item>
            </el-dropdown-menu>
          </template>
        </el-dropdown>
      </div>
    </el-header>
    
    <el-container class="app-body">
      <Sidebar />
      <el-main class="app-main">
        <router-view v-slot="{ Component }">
          <transition name="fade" mode="out-in">
            <component :is="Component" />
          </transition>
        </router-view>
      </el-main>
    </el-container>
  </el-container>
</template>

<script>
import { computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessageBox } from 'element-plus'
import {
  Document,
  Plus,
  FolderOpened,
  ArrowDown,
  SwitchButton
} from '@element-plus/icons-vue'
import { useUserStore } from '@/stores/user'
import SearchBar from '@/components/common/SearchBar.vue'
import Sidebar from './Sidebar.vue'

export default {
  name: 'AppLayout',
  components: {
    Document,
    Plus,
    FolderOpened,
    ArrowDown,
    SwitchButton,
    SearchBar,
    Sidebar
  },
  setup() {
    const router = useRouter()
    const userStore = useUserStore()
    
    const userInfo = computed(() => userStore.userInfo)
    
    onMounted(async () => {
      // 如果用户信息不存在，尝试获取
      if (!userInfo.value && userStore.isAuthenticated) {
        await userStore.getUserInfo()
      }
    })
    
    function handleCommand(command) {
      if (command === 'logout') {
        ElMessageBox.confirm('确定要退出登录吗？', '提示', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        }).then(() => {
          userStore.logout()
          router.push('/login')
        }).catch(() => {})
      }
    }
    
    return {
      userInfo,
      handleCommand
    }
  }
}
</script>

<style scoped>
.app-layout {
  min-height: 100vh;
  background: #f5f7fa;
}

.app-header {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  height: 60px;
  background: white;
  border-bottom: 1px solid #e4e7ed;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 20px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.05);
  z-index: 1000;
}

.header-left {
  display: flex;
  align-items: center;
}

.logo {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 18px;
  font-weight: 600;
  color: #409eff;
  cursor: pointer;
  transition: color 0.3s;
}

.logo:hover {
  color: #66b1ff;
}

.header-center {
  flex: 1;
  display: flex;
  justify-content: center;
  padding: 0 20px;
}

.header-right {
  display: flex;
  align-items: center;
  gap: 12px;
}

.user-menu {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 4px 12px;
  border-radius: 4px;
  cursor: pointer;
  transition: background-color 0.3s;
}

.user-menu:hover {
  background-color: #f5f7fa;
}

.username {
  font-size: 14px;
  color: #303133;
  font-weight: 500;
}

.app-body {
  margin-top: 60px;
  min-height: calc(100vh - 60px);
}

.app-main {
  margin-left: 250px;
  padding: 0;
  min-height: calc(100vh - 60px);
  background: #f5f7fa;
}

.app-main > * {
  padding: 20px;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .app-main {
    margin-left: 0;
  }
}

/* 页面切换动画 */
.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.3s ease;
}

.fade-enter-from,
.fade-leave-to {
  opacity: 0;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .app-header {
    padding: 0 10px;
  }
  
  .header-center {
    display: none;
  }
  
  .header-right {
    gap: 8px;
  }
  
  .header-right .el-button {
    padding: 8px 12px;
  }
  
  .header-right .el-button span {
    display: none;
  }
  
  .username {
    display: none;
  }
}
</style>

