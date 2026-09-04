import request from '@/utils/request'

export function listAvatarMaterial(query) {
  return request({ url: '/tg/avatarMaterial/list', method: 'get', params: query })
}

export function importAvatarMaterial(data) {
  return request({
    url: '/tg/avatarMaterial/import',
    method: 'post',
    headers: { 'Content-Type': 'multipart/form-data' },
    data: data,
    timeout: 300000,
  })
}

export function delAvatarMaterial(ids) {
  return request({ url: '/tg/avatarMaterial/' + ids, method: 'delete' })
}
