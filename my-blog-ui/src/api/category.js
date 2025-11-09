import request from './request'

export const categoryApi = {
  // 创建分类
  create(data) {
    return request.post('/categories', data)
  },
  
  // 获取分类列表
  getList() {
    return request.get('/categories')
  },
  
  // 更新分类
  update(id, data) {
    return request.put(`/categories/${id}`, data)
  },
  
  // 删除分类
  delete(id) {
    return request.delete(`/categories/${id}`)
  }
}

