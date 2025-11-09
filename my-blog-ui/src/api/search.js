import request from './request'

export const searchApi = {
  // 搜索文档
  search(keyword, page = 1, pageSize = 10) {
    return request.get(`/search?keyword=${encodeURIComponent(keyword)}&page=${page}&pageSize=${pageSize}`)
  }
}

