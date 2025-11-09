# 个人博客系统 - API接口文档

> 版本：v1.0  
> 创建时间：2025-10-26  
> 后端服务地址：http://localhost:8080  
> 本文档供前端工程师参考使用

---

## 目录

1. [快速开始](#快速开始)
2. [认证方式](#认证方式)
3. [统一响应格式](#统一响应格式)
4. [接口列表](#接口列表)
5. [错误码说明](#错误码说明)
6. [请求示例](#请求示例)

---

## 快速开始

### 基础信息

- **接口地址**：`http://localhost:8080`
- **接口前缀**：`/api/v1`
- **数据格式**：JSON
- **字符编码**：UTF-8

### 认证方式

大部分接口需要在请求头中携带JWT Token：

```
Authorization: Bearer YOUR_TOKEN
```

获取Token：通过 [用户登录接口](#2-用户登录) 获取。

---

## 统一响应格式

所有接口返回统一的JSON格式：

```json
{
  "code": 200,
  "message": "success",
  "data": {}
}
```

**字段说明**：

| 字段 | 类型 | 说明 |
|------|------|------|
| code | Integer | 状态码，200表示成功，其他表示失败 |
| message | String | 提示信息 |
| data | Object | 响应数据，可能为null |

**状态码说明**：

- `200` - 请求成功
- `400` - 参数错误
- `401` - 未登录
- `403` - 无权限
- `404` - 资源不存在
- `500` - 服务器错误

---

## 接口列表

### 1. 用户管理模块

#### 1.1 用户注册

**接口地址**：`POST /api/v1/users/register`

**请求头**：无需认证

**请求参数**：

```json
{
  "username": "string",  // 用户名，3-50字符
  "password": "string",  // 密码，至少8位
  "email": "string"      // 邮箱地址
}
```

**响应示例**：

```json
{
  "code": 200,
  "message": "注册成功",
  "data": {
    "id": 1,
    "username": "testuser",
    "email": "test@example.com",
    "avatar": null,
    "createdAt": "2025-10-26T10:00:00"
  }
}
```

**错误响应**：

```json
{
  "code": 1002,
  "message": "用户名已存在"
}
```

---

#### 1.2 用户登录

**接口地址**：`POST /api/v1/users/login`

**请求头**：无需认证

**请求参数**：

```json
{
  "username": "string",
  "password": "string"
}
```

**响应示例**：

```json
{
  "code": 200,
  "message": "登录成功",
  "data": {
    "token": "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiIxIiwiaWF0IjoxN...",
    "expiresIn": 3600
  }
}
```

**错误响应**：

```json
{
  "code": 1001,
  "message": "用户名或密码错误"
}
```

---

#### 1.3 获取当前用户信息

**接口地址**：`GET /api/v1/users/info`

**请求头**：
```
Authorization: Bearer YOUR_TOKEN
```

**响应示例**：

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "id": 1,
    "username": "testuser",
    "email": "test@example.com",
    "avatar": null,
    "createdAt": "2025-10-26T10:00:00"
  }
}
```

---

### 2. 文档管理模块

#### 2.1 上传文档

**接口地址**：`POST /api/v1/documents/upload`

**请求头**：
```
Authorization: Bearer YOUR_TOKEN
Content-Type: multipart/form-data
```

**请求参数**（Form Data）：

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| file | File | 是 | 文件对象，支持PDF、DOCX、DOC、TXT，最大100MB |
| title | String | 是 | 文档标题 |
| description | String | 否 | 文档描述 |
| categoryId | Long | 否 | 分类ID |

**响应示例**：

```json
{
  "code": 200,
  "message": "上传成功",
  "data": {
    "id": 1,
    "title": "学习笔记",
    "description": "这是一份学习笔记",
    "fileName": "bb614629-4cb9-463b-82ee-ca42b32db9d7.txt",
    "fileOriginalName": "note.txt",
    "fileUrl": "http://localhost:8080/files/bb614629...",
    "fileType": "text/plain",
    "fileSize": 1024,
    "thumbnail": null,
    "categoryId": 1,
    "viewCount": 0,
    "downloadCount": 0,
    "createdAt": "2025-10-26T10:00:00",
    "updatedAt": "2025-10-26T10:00:00"
  }
}
```

**错误响应**：

```json
{
  "code": 400,
  "message": "文件大小不能超过100MB"
}
```

---

#### 2.2 获取文档列表

**接口地址**：`GET /api/v1/documents`

**请求头**：
```
Authorization: Bearer YOUR_TOKEN
```

**请求参数**（Query）：

| 参数 | 类型 | 必填 | 默认值 | 说明 |
|------|------|------|--------|------|
| page | Integer | 否 | 1 | 页码 |
| pageSize | Integer | 否 | 10 | 每页数量 |

**响应示例**：

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "total": 100,
    "page": 1,
    "pageSize": 10,
    "documents": [
      {
        "id": 1,
        "title": "学习笔记",
        "description": "这是一份学习笔记",
        "fileName": "note.txt",
        "fileOriginalName": "note.txt",
        "fileUrl": "http://localhost:8080/files/xxx.txt",
        "fileType": "text/plain",
        "fileSize": 1024,
        "thumbnail": null,
        "categoryId": 1,
        "viewCount": 10,
        "downloadCount": 5,
        "createdAt": "2025-10-26T10:00:00",
        "updatedAt": "2025-10-26T11:00:00"
      }
    ]
  }
}
```

---

#### 2.3 获取文档详情

**接口地址**：`GET /api/v1/documents/{id}`

**请求头**：
```
Authorization: Bearer YOUR_TOKEN
```

**路径参数**：

| 参数 | 类型 | 说明 |
|------|------|------|
| id | Long | 文档ID |

**响应示例**：

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "id": 1,
    "title": "学习笔记",
    "description": "这是一份学习笔记",
    "fileName": "note.txt",
    "fileOriginalName": "note.txt",
    "fileUrl": "http://localhost:8080/files/xxx.txt",
    "fileType": "text/plain",
    "fileSize": 1024,
    "thumbnail": null,
    "categoryId": 1,
    "viewCount": 10,
    "downloadCount": 5,
    "createdAt": "2025-10-26T10:00:00",
    "updatedAt": "2025-10-26T11:00:00"
  }
}
```

---

#### 2.4 更新文档信息

**接口地址**：`PUT /api/v1/documents/{id}`

**请求头**：
```
Authorization: Bearer YOUR_TOKEN
Content-Type: application/json
```

**路径参数**：`id` - 文档ID

**请求参数**：

```json
{
  "title": "更新后的标题",
  "description": "更新后的描述",
  "categoryId": 1
}
```

**响应示例**：

```json
{
  "code": 200,
  "message": "更新成功",
  "data": {
    "id": 1,
    "title": "更新后的标题",
    "description": "更新后的描述",
    ...
  }
}
```

---

#### 2.5 删除文档

**接口地址**：`DELETE /api/v1/documents/{id}`

**请求头**：
```
Authorization: Bearer YOUR_TOKEN
```

**路径参数**：`id` - 文档ID

**响应示例**：

```json
{
  "code": 200,
  "message": "删除成功",
  "data": null
}
```

---

### 3. 分类管理模块

#### 3.1 创建分类

**接口地址**：`POST /api/v1/categories`

**请求头**：
```
Authorization: Bearer YOUR_TOKEN
Content-Type: application/json
```

**请求参数**：

```json
{
  "name": "技术文档",        // 分类名称，必填
  "description": "技术相关",  // 分类描述，可选
  "parentId": 0,            // 父分类ID，0为顶级分类，可选
  "sortOrder": 1            // 排序顺序，可选
}
```

**响应示例**：

```json
{
  "code": 200,
  "message": "创建成功",
  "data": {
    "id": 1,
    "name": "技术文档",
    "description": "技术相关",
    "parentId": 0,
    "sortOrder": 1,
    "createdAt": "2025-10-26T10:00:00",
    "updatedAt": "2025-10-26T10:00:00"
  }
}
```

---

#### 3.2 获取分类列表

**接口地址**：`GET /api/v1/categories`

**请求头**：
```
Authorization: Bearer YOUR_TOKEN
```

**响应示例**：

```json
{
  "code": 200,
  "message": "success",
  "data": [
    {
      "id": 1,
      "name": "技术文档",
      "description": "技术相关",
      "parentId": 0,
      "sortOrder": 1,
      "createdAt": "2025-10-26T10:00:00",
      "updatedAt": "2025-10-26T10:00:00"
    },
    {
      "id": 2,
      "name": "学习笔记",
      "description": "学习相关",
      "parentId": 0,
      "sortOrder": 2,
      "createdAt": "2025-10-26T10:05:00",
      "updatedAt": "2025-10-26T10:05:00"
    }
  ]
}
```

---

#### 3.3 更新分类

**接口地址**：`PUT /api/v1/categories/{id}`

**请求头**：
```
Authorization: Bearer YOUR_TOKEN
Content-Type: application/json
```

**路径参数**：`id` - 分类ID

**请求参数**：

```json
{
  "name": "更新后的名称",
  "description": "更新后的描述",
  "parentId": 0,
  "sortOrder": 1
}
```

**响应示例**：

```json
{
  "code": 200,
  "message": "更新成功",
  "data": {
    "id": 1,
    "name": "更新后的名称",
    "description": "更新后的描述",
    ...
  }
}
```

---

#### 3.4 删除分类

**接口地址**：`DELETE /api/v1/categories/{id}`

**请求头**：
```
Authorization: Bearer YOUR_TOKEN
```

**路径参数**：`id` - 分类ID

**响应示例**：

```json
{
  "code": 200,
  "message": "删除成功",
  "data": null
}
```

---

### 4. 搜索模块

#### 4.1 搜索文档

**接口地址**：`GET /api/v1/search`

**请求头**：
```
Authorization: Bearer YOUR_TOKEN
```

**请求参数**（Query）：

| 参数 | 类型 | 必填 | 默认值 | 说明 |
|------|------|------|--------|------|
| keyword | String | 是 | - | 搜索关键词 |
| page | Integer | 否 | 1 | 页码 |
| pageSize | Integer | 否 | 10 | 每页数量 |

**响应示例**：

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "total": 5,
    "page": 1,
    "pageSize": 10,
    "keyword": "学习",
    "documents": [
      {
        "id": 1,
        "title": "学习笔记",
        "description": "这是一份学习笔记",
        "fileUrl": "http://localhost:8080/files/xxx.txt",
        "fileSize": 1024,
        "createdAt": "2025-10-26T10:00:00"
      }
    ]
  }
}
```

---

## 错误码说明

### 用户相关错误

| 错误码 | 说明 |
|--------|------|
| 1001 | 用户不存在或密码错误 |
| 1002 | 用户名已存在 |
| 1004 | 用户已被禁用 |
| 1003 | 密码错误 |

### 文档相关错误

| 错误码 | 说明 |
|--------|------|
| 2001 | 文档不存在 |
| 2002 | 文档上传失败 |
| 2003 | 不支持的文件类型 |
| 2004 | 文件大小超限（最大100MB） |

### 分类相关错误

| 错误码 | 说明 |
|--------|------|
| 3001 | 分类不存在 |
| 3002 | 分类已存在 |

### 认证相关错误

| 错误码 | 说明 |
|--------|------|
| 4001 | Token无效 |
| 4002 | Token已过期 |
| 4003 | 无权访问 |

### 通用错误

| 错误码 | 说明 |
|--------|------|
| 200 | 成功 |
| 400 | 参数错误 |
| 401 | 未登录 |
| 403 | 无权限 |
| 404 | 资源不存在 |
| 500 | 服务器错误 |

---

## 请求示例

### JavaScript (axios)

```javascript
import axios from 'axios';

const API_BASE_URL = 'http://localhost:8080/api/v1';

// 1. 用户注册
async function register(username, password, email) {
  const response = await axios.post(`${API_BASE_URL}/users/register`, {
    username,
    password,
    email
  });
  return response.data;
}

// 2. 用户登录
async function login(username, password) {
  const response = await axios.post(`${API_BASE_URL}/users/login`, {
    username,
    password
  });
  // 保存token
  localStorage.setItem('token', response.data.data.token);
  return response.data;
}

// 3. 上传文档
async function uploadDocument(file, title, description, categoryId) {
  const formData = new FormData();
  formData.append('file', file);
  formData.append('title', title);
  formData.append('description', description);
  if (categoryId) {
    formData.append('categoryId', categoryId);
  }

  const token = localStorage.getItem('token');
  const response = await axios.post(
    `${API_BASE_URL}/documents/upload`,
    formData,
    {
      headers: {
        'Authorization': `Bearer ${token}`
      }
    }
  );
  return response.data;
}

// 4. 获取文档列表
async function getDocumentList(page = 1, pageSize = 10) {
  const token = localStorage.getItem('token');
  const response = await axios.get(
    `${API_BASE_URL}/documents?page=${page}&pageSize=${pageSize}`,
    {
      headers: {
        'Authorization': `Bearer ${token}`
      }
    }
  );
  return response.data;
}

// 5. 搜索文档
async function searchDocuments(keyword, page = 1, pageSize = 10) {
  const token = localStorage.getItem('token');
  const response = await axios.get(
    `${API_BASE_URL}/search?keyword=${keyword}&page=${page}&pageSize=${pageSize}`,
    {
      headers: {
        'Authorization': `Bearer ${token}`
      }
    }
  );
  return response.data;
}
```

### Vue.js 示例

```vue
<template>
  <div>
    <el-upload
      :action="uploadUrl"
      :headers="uploadHeaders"
      :data="uploadData"
      @success="handleUploadSuccess"
    >
      <el-button type="primary">上传文档</el-button>
    </el-upload>
    
    <el-table :data="documents" style="width: 100%">
      <el-table-column prop="title" label="标题" />
      <el-table-column prop="description" label="描述" />
      <el-table-column prop="fileSize" label="大小" />
      <el-table-column label="操作">
        <template #default="scope">
          <el-button @click="viewDocument(scope.row)">查看</el-button>
          <el-button @click="deleteDocument(scope.row.id)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import axios from 'axios';

const uploadUrl = 'http://localhost:8080/api/v1/documents/upload';
const uploadHeaders = {
  'Authorization': `Bearer ${localStorage.getItem('token')}`
};
const uploadData = {};
const documents = ref([]);

onMounted(() => {
  loadDocuments();
});

async function loadDocuments() {
  const token = localStorage.getItem('token');
  const response = await axios.get(
    'http://localhost:8080/api/v1/documents',
    {
      headers: { 'Authorization': `Bearer ${token}` }
    }
  );
  documents.value = response.data.data.documents;
}

function handleUploadSuccess(response) {
  console.log('上传成功', response);
  loadDocuments();
}

async function deleteDocument(id) {
  const token = localStorage.getItem('token');
  await axios.delete(
    `http://localhost:8080/api/v1/documents/${id}`,
    {
      headers: { 'Authorization': `Bearer ${token}` }
    }
  );
  loadDocuments();
}
</script>
```

---

## 注意事项

### 1. Token过期处理

Token默认有效期为1小时（3600秒）。如果Token过期，需要重新登录获取新的Token。

### 2. 文件上传限制

- **支持格式**：PDF、DOCX、DOC、TXT
- **文件大小**：最大100MB
- **上传路径**：文件存储在服务器 `D:/upload` 目录

### 3. 分页参数

- `page`：页码，从1开始
- `pageSize`：每页数量，建议10-20

### 4. 权限说明

- 用户只能操作自己的文档和分类
- 无法访问其他用户的数据

### 5. 时间格式

所有时间字段使用ISO 8601格式：`2025-10-26T10:00:00`

---

## 技术支持

如有任何问题，请联系后端开发团队。

**文档版本**：v1.0  
**最后更新**：2025-10-26
