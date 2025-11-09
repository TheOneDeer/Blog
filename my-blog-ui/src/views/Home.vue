<template>
  <div class="home">
    <el-container>
      <el-header>
        <h1>个人博客系统</h1>
      </el-header>
      <el-main>
        <el-card>
          <template #header>
            <span>欢迎使用个人博客系统</span>
          </template>
          <p>这是一个基于 Vue3 + TypeScript + Vite + Pinia + Element Plus 的个人博客系统。</p>
          <el-divider />
          <p>当前阶段：Phase 1 - 项目初始化</p>
          <el-tag type="success">✓ 依赖安装完成</el-tag>
          <el-tag type="success">✓ 项目结构搭建完成</el-tag>
          <el-tag type="info">进行中：配置基础功能...</el-tag>
        </el-card>
      </el-main>
    </el-container>
  </div>
</template>

<script>
export default {
  name: 'HomePage'
}
</script>

<style scoped>
.home {
  min-height: 100vh;
  background: #f5f7fa;
}

.el-header {
  background: white;
  display: flex;
  align-items: center;
  box-shadow: 0 2px 4px rgba(0,0,0,0.1);
}

.el-main {
  padding: 20px;
}

.el-card {
  max-width: 800px;
  margin: 0 auto;
}

.el-tag {
  margin-right: 10px;
  margin-top: 10px;
}
</style>
