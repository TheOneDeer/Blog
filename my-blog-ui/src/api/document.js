import request from './request'

export const documentApi = {
  // 上传文档
  upload(formData) {
    return request.post('/documents/upload', formData, {
      headers: { 'Content-Type': 'multipart/form-data' }
    })
  },
  
  // 获取文档列表
  getList(page = 1, pageSize = 10) {
    return request.get(`/documents?page=${page}&pageSize=${pageSize}`)
  },
  
  // 获取文档详情
  getById(id) {
    return request.get(`/documents/${id}`)
  },
  
  // 更新文档
  update(id, data) {
    return request.put(`/documents/${id}`, data)
  },
  
  // 删除文档
  delete(id) {
    return request.delete(`/documents/${id}`)
  }
}

