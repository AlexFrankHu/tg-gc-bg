import request from '@/utils/request'

export function listChatMessage(query) {
  return request({
    url: '/tg/chatMessage/list',
    method: 'get',
    params: query
  })
}

export function getChatMessage(id) {
  return request({
    url: '/tg/chatMessage/' + id,
    method: 'get'
  })
}

export function delChatMessage(ids) {
  return request({
    url: '/tg/chatMessage/' + ids,
    method: 'delete'
  })
}

export function exportChatMessage(data) {
  return request({
    url: '/tg/chatMessage/export',
    method: 'post',
    data: data,
    responseType: 'blob'
  })
}
