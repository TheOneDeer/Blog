import request from './request'

export const userApi = {
  // 用户注册
  register(data) {
    return request.post('/users/register', data)
  },
  
  // 用户登录
  login(data) {
    return request.post('/users/login', data)
  },
  
  // 获取用户信息
  getUserInfo() {
    return request.get('/users/info')
  }
}
