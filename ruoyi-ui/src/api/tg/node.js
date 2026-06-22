import request from '@/utils/request'

// 查询节点列表
export function listNode(query) {
  return request({
    url: '/tg/node/list',
    method: 'get',
    params: query
  })
}

// 查询节点详细
export function getNode(nodeId) {
  return request({
    url: '/tg/node/' + nodeId,
    method: 'get'
  })
}

// 修改节点信息
export function updateNode(data) {
  return request({
    url: '/tg/node',
    method: 'put',
    data: data
  })
}
