import request from '@/utils/request'

// 查询导入批次列表
export function listBatch(query) {
  return request({
    url: '/tg/import/batch/list',
    method: 'get',
    params: query
  })
}

// 查询所有批次（用于下拉选择）
export function listAllBatch() {
  return request({
    url: '/tg/import/batch/all',
    method: 'get'
  })
}

// 查询导入详情（某批次的账号列表）
export function listImportAccount(query) {
  return request({
    url: '/tg/import/account/list',
    method: 'get',
    params: query
  })
}

// 导入账号（上传zip）
export function importAccounts(data) {
  return request({
    url: '/tg/import/upload',
    method: 'post',
    headers: { 'Content-Type': 'multipart/form-data' },
    data: data
  })
}

// 修改批次标题
export function updateBatchTitle(data) {
  return request({
    url: '/tg/import/batch/title',
    method: 'put',
    data: data
  })
}

// 给账号批次分配好友
export function assignContacts(data) {
  return request({
    url: '/tg/import/assignContacts',
    method: 'post',
    data: data
  })
}

// 查询好友分配日志
export function listContactAssignLog(query) {
  return request({
    url: '/tg/import/contactAssignLog',
    method: 'get',
    params: query
  })
}

// 查询登录日志
export function listLoginLog(query) {
  return request({
    url: '/tg/import/loginLog',
    method: 'get',
    params: query
  })
}

// 查询发送失败日志
export function listSendFailLog(query) {
  return request({
    url: '/tg/import/sendFailLog',
    method: 'get',
    params: query
  })
}

// 查询自动回复日志
export function listAutoReplyLog(query) {
  return request({
    url: '/tg/import/autoReplyLog',
    method: 'get',
    params: query
  })
}

// 导出好友分配日志
export function exportContactAssignLog(query) {
  return request({
    url: '/tg/import/contactAssignLog/export',
    method: 'post',
    params: query,
    responseType: 'blob'
  })
}

// 查询IP分配日志
export function listProxyAssignLog(query) {
  return request({
    url: '/tg/account/proxyAssignLog',
    method: 'get',
    params: query
  })
}
