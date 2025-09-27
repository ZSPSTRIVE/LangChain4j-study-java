import request from '../utils/request'

// 分页查询关卡列表
export const listLevels = (data) => {
  return request.post('/admin/level/list', data)
}

// 添加关卡
export const addLevel = (data) => {
  return request.post('/admin/level/add', data)
}

// 更新关卡
export const updateLevel = (data) => {
  return request.post('/admin/level/update', data)
}

// 删除关卡
export const deleteLevel = (id) => {
  return request.post(`/admin/level/delete/${id}`)
}

// 设置关卡为精选
export const setLevelFeatured = (id) => {
  return request.post(`/admin/level/setFeatured/${id}`)
}

// 取消关卡精选
export const cancelLevelFeatured = (id) => {
  return request.post(`/admin/level/cancelFeatured/${id}`)
}

// 批量设置关卡优先级
export const setLevelPriority = (ids, priority) => {
  return request.post('/admin/level/setPriority', null, {
    params: { ids, priority }
  })
}
