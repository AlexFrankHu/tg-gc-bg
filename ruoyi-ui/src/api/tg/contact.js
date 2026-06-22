import request from '@/utils/request'

export function listContact(query) {
  return request({
    url: '/tg/contact/list',
    method: 'get',
    params: query
  })
}

export function getContact(id) {
  return request({
    url: '/tg/contact/' + id,
    method: 'get'
  })
}

export function delContact(ids) {
  return request({
    url: '/tg/contact/' + ids,
    method: 'delete'
  })
}

export function updateAutoReply(id, autoReply) {
  return request({
    url: '/tg/contact/autoReply/' + id + '/' + autoReply,
    method: 'put'
  })
}

export function updateAllAutoReply(autoReply) {
  return request({
    url: '/tg/contact/autoReply/all/' + autoReply,
    method: 'put'
  })
}

export function exportContact(query) {
  return request({
    url: '/tg/contact/export',
    method: 'post',
    params: query,
    responseType: 'blob'
  })
}

export function sendGreeting(data) {
  return request({
    url: '/tg/contact/sendGreeting',
    method: 'post',
    data: data
  })
}
