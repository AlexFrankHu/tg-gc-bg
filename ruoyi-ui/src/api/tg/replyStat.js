import request from '@/utils/request'

// 回复率统计
export function listReplyStat(query) {
  return request({
    url: '/tg/replyStat/list',
    method: 'get',
    params: query
  })
}
