import request from '@/utils/request'

// IP组列表
export function listProxyGroup(query) {
  return request({ url: '/tg/proxy/group/list', method: 'get', params: query })
}

// 所有IP组(下拉)
export function listAllProxyGroup() {
  return request({ url: '/tg/proxy/group/all', method: 'get' })
}

// 修改IP组
export function updateProxyGroup(data) {
  return request({ url: '/tg/proxy/group', method: 'put', data: data })
}

// 删除IP组
export function delProxyGroup(ids) {
  return request({ url: '/tg/proxy/group/' + ids, method: 'delete' })
}

// 导入代理IP
export function importProxy(data) {
  return request({
    url: '/tg/proxy/import',
    method: 'post',
    headers: { 'Content-Type': 'multipart/form-data' },
    data: data,
    timeout: 60000,
  })
}

// IP代理列表
export function listProxyIp(query) {
  return request({ url: '/tg/proxy/ip/list', method: 'get', params: query })
}

// 查看IP代理详情
export function getProxyIpDetail(id) {
  return request({ url: '/tg/proxy/ip/' + id, method: 'get' })
}

// 删除IP代理
export function delProxyIp(ids) {
  return request({ url: '/tg/proxy/ip/' + ids, method: 'delete' })
}

// 测试代理IP
export function testProxyIp(id) {
  return request({ url: '/tg/proxy/ip/test/' + id, method: 'post', timeout: 60000 })
}

// 修改IP代理状态
export function changeProxyIpStatus(data) {
  return request({ url: '/tg/proxy/ip/status', method: 'put', data: data })
}

// 导入ipfly代理
export function importIpflyProxy(data) {
  return request({
    url: '/tg/proxy/importIpfly',
    method: 'post',
    headers: { 'Content-Type': 'multipart/form-data' },
    data: data,
    timeout: 60000,
  })
}
