import request from '../utils/request'

// 提交答案
export const submitAnswer = (data) => {
  return request.post('/user-level/submit', data)
}

// 获取闯关详情
export const getUserLevelDetail = (id) => {
  return request.get(`/user-level/${id}`)
}

// 获取闯关历史
export const getUserLevelHistory = () => {
  return request.get('/user-level/history')
}
