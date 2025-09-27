import request from '../utils/request'

// 获取薪资排行榜
export const getSalaryRanking = (limit = 30) => {
  return request.get('/ranking/salary', {
    params: { limit }
  })
}
