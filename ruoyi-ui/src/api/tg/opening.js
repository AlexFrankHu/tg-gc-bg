import request from '@/utils/request'

export function listOpening(query) {
  return request({
    url: '/tg/opening/list',
    method: 'get',
    params: query
  })
}

export function getOpening(id) {
  return request({
    url: '/tg/opening/' + id,
    method: 'get'
  })
}

export function addOpening(data) {
  return request({
    url: '/tg/opening',
    method: 'post',
    data: data
  })
}

export function updateOpening(data) {
  return request({
    url: '/tg/opening',
    method: 'put',
    data: data
  })
}

export function delOpening(ids) {
  return request({
    url: '/tg/opening/' + ids,
    method: 'delete'
  })
}
