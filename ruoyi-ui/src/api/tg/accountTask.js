import request from '@/utils/request'

export function listAccountTask(query) {
  return request({ url: '/tg/accountTask/list', method: 'get', params: query })
}

export function delAccountTask(ids) {
  return request({ url: '/tg/accountTask/' + ids, method: 'delete' })
}

export function clearFinishedAccountTask() {
  return request({ url: '/tg/accountTask/clearFinished', method: 'delete' })
}
