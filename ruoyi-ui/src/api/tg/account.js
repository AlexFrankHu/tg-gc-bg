import request from '@/utils/request'

// 查询账号列表
export function listAccount(query) {
  return request({
    url: '/tg/account/list',
    method: 'get',
    params: query
  })
}

// 导出账号列表(按筛选条件)
export function exportAccount(query) {
  return request({
    url: '/tg/account/export',
    method: 'post',
    params: query
  })
}

// 查询账号详细
export function getAccount(id) {
  return request({
    url: '/tg/account/' + id,
    method: 'get'
  })
}

// 删除账号
export function delAccount(id) {
  return request({
    url: '/tg/account/' + id,
    method: 'delete'
  })
}

// 触发登录
export function triggerLogin(id) {
  return request({
    url: '/tg/account/login/' + id,
    method: 'put'
  })
}

// 无代理登录
export function loginNoProxy(id) {
  return request({
    url: '/tg/account/loginNoProxy/' + id,
    method: 'put'
  })
}

// 登出账号
export function logoutAccount(id) {
  return request({
    url: '/tg/account/logout/' + id,
    method: 'put'
  })
}

// 获取网页端token
export function getWsToken(id) {
  return request({
    url: '/tg/account/wsToken/' + id,
    method: 'get'
  })
}

// 批量登录（按批次）
export function loginBatch(batchNo) {
  return request({
    url: '/tg/account/loginBatch/' + batchNo,
    method: 'put'
  })
}

// 账号分组批量登录（按账号分组）
export function loginByGroup(groupId) {
  return request({
    url: '/tg/account/loginByGroup/' + groupId,
    method: 'put'
  })
}

// 批量登出（按批次）
export function logoutBatch(batchNo) {
  return request({
    url: '/tg/account/logoutBatch/' + batchNo,
    method: 'put'
  })
}

// 账号分组登出（按账号分组）
export function logoutByGroup(groupId) {
  return request({
    url: '/tg/account/logoutByGroup/' + groupId,
    method: 'put'
  })
}

// 查看账号代理信息
export function getProxyInfo(id) {
  return request({
    url: '/tg/account/proxy/' + id,
    method: 'get'
  })
}

// 自动选择代理IP
export function autoSelectProxy(id, groupNo) {
  return request({
    url: '/tg/account/proxy/auto/' + id,
    method: 'put',
    params: { groupNo }
  })
}

// 手动选择代理IP
export function manualSelectProxy(id, proxyIpId) {
  return request({
    url: '/tg/account/proxy/manual/' + id,
    method: 'put',
    params: { proxyIpId }
  })
}

// 手动配置代理IP
export function configProxy(id, data) {
  return request({
    url: '/tg/account/proxy/config/' + id,
    method: 'put',
    data: data
  })
}

// 批量为批次分配代理IP
export function batchAssignProxy(batchNo, groupNo) {
  return request({
    url: '/tg/account/proxy/batch/' + batchNo,
    method: 'put',
    params: { groupNo }
  })
}

// 切换单个账号自动回复
export function updateAccountAutoReply(id, autoReply) {
  return request({
    url: '/tg/account/autoReply/' + id + '/' + autoReply,
    method: 'put'
  })
}

// 切换全部账号自动回复
export function updateAllAccountAutoReply(autoReply) {
  return request({
    url: '/tg/account/autoReply/all/' + autoReply,
    method: 'put'
  })
}

// 修改账号限制状态
export function updateAccountRestricted(id, isRestricted) {
  return request({
    url: '/tg/account/restricted/' + id + '/' + isRestricted,
    method: 'put'
  })
}

// 批量修改账号限制状态
export function batchUpdateAccountRestricted(ids, isRestricted) {
  return request({
    url: '/tg/account/restricted/batch',
    method: 'put',
    data: { ids, isRestricted }
  })
}

// 解除所有被限制账号的限制
export function unrestrictAllAccounts() {
  return request({
    url: '/tg/account/restricted/all',
    method: 'put'
  })
}

// 批量设置账号分组
export function batchSetAccountGroup(ids, groupId) {
  return request({
    url: '/tg/account/group/batch',
    method: 'put',
    data: { ids, groupId }
  })
}

// 单个账号任务: 修改昵称/头像/2FA (taskType: nickname/avatar/twofa)
export function createAccountTask(taskType, id) {
  return request({
    url: '/tg/account/task/' + taskType + '/' + id,
    method: 'put'
  })
}

// 按批次下发账号任务
export function createAccountTaskByBatch(taskType, batchNo) {
  return request({
    url: '/tg/account/taskBatch/' + taskType + '/' + batchNo,
    method: 'put',
    timeout: 120000
  })
}

// 按账号分组下发账号任务
export function createAccountTaskByGroup(taskType, groupId) {
  return request({
    url: '/tg/account/taskByGroup/' + taskType + '/' + groupId,
    method: 'put',
    timeout: 120000
  })
}
