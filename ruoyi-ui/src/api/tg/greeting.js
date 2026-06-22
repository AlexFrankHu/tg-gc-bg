import request from '@/utils/request'

// 查询主动问候语列表
export function listGreeting(query) {
  return request({
    url: '/tg/greeting/list',
    method: 'get',
    params: query
  })
}

// 查询主动问候语详细
export function getGreeting(id) {
  return request({
    url: '/tg/greeting/' + id,
    method: 'get'
  })
}

// 新增主动问候语
export function addGreeting(data) {
  return request({
    url: '/tg/greeting',
    method: 'post',
    data: data
  })
}

// 修改主动问候语
export function updateGreeting(data) {
  return request({
    url: '/tg/greeting',
    method: 'put',
    data: data
  })
}

// 删除主动问候语
export function delGreeting(ids) {
  return request({
    url: '/tg/greeting/' + ids,
    method: 'delete'
  })
}
