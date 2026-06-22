import request from '@/utils/request'

// 查询好友导入批次列表
export function listContactImportBatch(query) {
  return request({
    url: '/tg/contactImport/batch/list',
    method: 'get',
    params: query
  })
}

// 查询所有批次（用于下拉选择）
export function listAllContactImportBatch() {
  return request({
    url: '/tg/contactImport/batch/all',
    method: 'get'
  })
}

// 修改批次标题
export function updateContactImportBatchTitle(data) {
  return request({
    url: '/tg/contactImport/batch/title',
    method: 'put',
    data: data
  })
}

// 删除批次
export function deleteContactImportBatch(ids) {
  return request({
    url: '/tg/contactImport/batch/' + ids,
    method: 'delete'
  })
}

// 查询导入详情（某批次的号码列表）
export function listContactImportRecord(query) {
  return request({
    url: '/tg/contactImport/record/list',
    method: 'get',
    params: query
  })
}

// 导入好友-手机号（上传xlsx）
export function importContacts(data) {
  return request({
    url: '/tg/contactImport/upload',
    method: 'post',
    headers: { 'Content-Type': 'multipart/form-data' },
    data: data
  })
}

// 导入好友-用户名（上传xlsx）
export function importContactsByUsername(data) {
  return request({
    url: '/tg/contactImport/uploadUsername',
    method: 'post',
    headers: { 'Content-Type': 'multipart/form-data' },
    data: data
  })
}
