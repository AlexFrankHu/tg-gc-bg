import request from '@/utils/request'

// 查询账号分组列表
export function listAccountGroup(query) {
  return request({
    url: '/tg/accountGroup/list',
    method: 'get',
    params: query
  })
}

// 查询所有可用分组(下拉)
export function listEnabledAccountGroup() {
  return request({
    url: '/tg/accountGroup/enabledList',
    method: 'get'
  })
}

// 查询账号分组详情
export function getAccountGroup(id) {
  return request({
    url: '/tg/accountGroup/' + id,
    method: 'get'
  })
}

// 新增账号分组
export function addAccountGroup(data) {
  return request({
    url: '/tg/accountGroup',
    method: 'post',
    data: data
  })
}

// 修改账号分组
export function updateAccountGroup(data) {
  return request({
    url: '/tg/accountGroup',
    method: 'put',
    data: data
  })
}

// 给分组下的账号分配好友
export function assignContactsByGroup(data) {
  return request({
    url: '/tg/import/assignContactsByGroup',
    method: 'post',
    data: data
  })
}
