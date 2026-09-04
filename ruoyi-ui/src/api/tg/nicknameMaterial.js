import request from '@/utils/request'

export function listNicknameMaterial(query) {
  return request({ url: '/tg/nicknameMaterial/list', method: 'get', params: query })
}

export function importNicknameMaterial(data) {
  return request({
    url: '/tg/nicknameMaterial/import',
    method: 'post',
    headers: { 'Content-Type': 'multipart/form-data' },
    data: data,
    timeout: 120000,
  })
}

export function delNicknameMaterial(ids) {
  return request({ url: '/tg/nicknameMaterial/' + ids, method: 'delete' })
}

export function clearNicknameMaterial() {
  return request({ url: '/tg/nicknameMaterial/clear', method: 'delete' })
}
