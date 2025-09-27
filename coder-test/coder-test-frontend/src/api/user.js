import request from '../utils/request'

// 用户注册
export const register = (data) => {
  return request.post('/user/register', data)
}

// 用户登录
export const login = (data) => {
  return request.post('/user/login', data)
}

// 用户注销
export const logout = () => {
  return request.post('/user/logout')
}

// 获取当前登录用户
export const getCurrentUser = () => {
  return request.get('/user/current')
}
