<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryRef" :inline="true" v-show="showSearch">
      <el-form-item label="手机号" prop="phone">
        <el-input v-model="queryParams.phone" placeholder="请输入手机号" clearable style="width: 160px" @keyup.enter="handleQuery" />
      </el-form-item>
      <el-form-item label="任务类型" prop="taskType">
        <el-select v-model="queryParams.taskType" placeholder="全部" clearable style="width: 130px">
          <el-option label="修改昵称" value="nickname" />
          <el-option label="修改头像" value="avatar" />
          <el-option label="修改2FA" value="twofa" />
        </el-select>
      </el-form-item>
      <el-form-item label="状态" prop="status">
        <el-select v-model="queryParams.status" placeholder="全部" clearable style="width: 120px">
          <el-option label="待执行" value="pending" />
          <el-option label="成功" value="success" />
          <el-option label="失败" value="failed" />
        </el-select>
      </el-form-item>
      <el-form-item label="来源" prop="source">
        <el-select v-model="queryParams.source" placeholder="全部" clearable style="width: 120px">
          <el-option label="单个账号" value="single" />
          <el-option label="按批次" value="batch" />
          <el-option label="按分组" value="group" />
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="Search" @click="handleQuery">搜索</el-button>
        <el-button icon="Refresh" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button type="danger" plain icon="Delete" :disabled="multiple" @click="handleDelete" v-hasPermi="['tg:accountTask:remove']">删除</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="warning" plain icon="Delete" @click="handleClearFinished" v-hasPermi="['tg:accountTask:remove']">清除已完成记录</el-button>
      </el-col>
      <right-toolbar v-model:showSearch="showSearch" @queryTable="getList" />
    </el-row>

    <el-table v-loading="loading" :data="list" @selection-change="handleSelectionChange" :border="true">
      <el-table-column type="selection" width="50" align="center" />
      <el-table-column label="ID" align="center" prop="id" width="90" />
      <el-table-column label="手机号" align="center" prop="phone" width="130" />
      <el-table-column label="账号ID" align="center" prop="accountId" width="90" />
      <el-table-column label="任务类型" align="center" prop="taskType" width="100">
        <template #default="scope">{{ typeText(scope.row.taskType) }}</template>
      </el-table-column>
      <el-table-column label="状态" align="center" prop="status" width="90">
        <template #default="scope">
          <el-tag :type="statusTag(scope.row.status)" size="small">{{ statusText(scope.row.status) }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="失败原因" align="center" prop="errorReason" min-width="220" show-overflow-tooltip />
      <el-table-column label="来源" align="center" prop="source" width="90">
        <template #default="scope">{{ sourceText(scope.row.source) }}</template>
      </el-table-column>
      <el-table-column label="批次号/分组ID" align="center" prop="sourceRef" width="150" show-overflow-tooltip />
      <el-table-column label="节点" align="center" prop="nodeId" width="120" show-overflow-tooltip />
      <el-table-column label="创建时间" align="center" prop="createTime" width="170" />
      <el-table-column label="更新时间" align="center" prop="updateTime" width="170" />
    </el-table>

    <pagination v-show="total > 0" :total="total" v-model:page="queryParams.pageNum" v-model:limit="queryParams.pageSize" @pagination="getList" />
  </div>
</template>

<script setup name="AccountTask">
import { ref, getCurrentInstance, onMounted } from 'vue'
import { listAccountTask, delAccountTask, clearFinishedAccountTask } from '@/api/tg/accountTask'

const { proxy } = getCurrentInstance()

const loading = ref(false)
const showSearch = ref(true)
const list = ref([])
const total = ref(0)
const ids = ref([])
const multiple = ref(true)

const queryParams = ref({ pageNum: 1, pageSize: 20, phone: undefined, taskType: undefined, status: undefined, source: undefined })

const typeText = t => ({ nickname: '修改昵称', avatar: '修改头像', twofa: '修改2FA' }[t] || t)
const sourceText = s => ({ single: '单个账号', batch: '按批次', group: '按分组' }[s] || s)
const statusText = s => ({ pending: '待执行', success: '成功', failed: '失败' }[s] || s)
const statusTag = s => ({ pending: 'info', success: 'success', failed: 'danger' }[s] || '')

function getList() {
  loading.value = true
  listAccountTask(queryParams.value).then(res => {
    list.value = res.rows
    total.value = res.total
  }).finally(() => { loading.value = false })
}

function handleQuery() { queryParams.value.pageNum = 1; getList() }
function resetQuery() { proxy.resetForm('queryRef'); handleQuery() }
function handleSelectionChange(selection) {
  ids.value = selection.map(i => i.id)
  multiple.value = !selection.length
}

function handleDelete() {
  proxy.$modal.confirm('是否确认删除选中的 ' + ids.value.length + ' 条记录？').then(() => delAccountTask(ids.value.join(','))).then(() => {
    proxy.$modal.msgSuccess('删除成功')
    getList()
  }).catch(() => {})
}
function handleClearFinished() {
  proxy.$modal.confirm('是否确认清除所有已完成(成功/失败)的任务记录？').then(() => clearFinishedAccountTask()).then(res => {
    proxy.$modal.msgSuccess(res.msg || '已清除')
    getList()
  }).catch(() => {})
}

onMounted(getList)
</script>
