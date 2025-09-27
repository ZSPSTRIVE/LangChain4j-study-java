import request from '../utils/request'

// 生成关卡
export const generateLevel = (data) => {
  return request.post('/level/generate', data)
}

// 获取关卡详情
export const getLevelDetail = (id) => {
  return request.get(`/level/${id}`)
}

// 分页获取精选关卡列表
export const getFeaturedLevels = (current = 1, pageSize = 10) => {
  return request.get('/level/featured', {
    params: { current, pageSize }
  })
}