import request from '@/utils/request'

// 查询系统配置列表
export function listSystemConfig(query) {
  return request({
    url: '/tg/systemConfig/list',
    method: 'get',
    params: query
  })
}

// 获取系统配置详情
export function getSystemConfig(id) {
  return request({
    url: '/tg/systemConfig/' + id,
    method: 'get'
  })
}

// 修改配置值(仅可修改值)
export function updateSystemConfig(data) {
  return request({
    url: '/tg/systemConfig',
    method: 'put',
    data: data
  })
}
