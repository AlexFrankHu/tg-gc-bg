<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryRef" :inline="true" class="mb8">
      <el-form-item label="账号" prop="accountPhone">
        <el-input v-model="queryParams.accountPhone" placeholder="请输入账号" clearable style="width: 200px" @keyup.enter="handleQuery" />
      </el-form-item>
      <el-form-item label="状态" prop="status">
        <el-select v-model="queryParams.status" placeholder="全部" clearable style="width: 120px">
          <el-option label="待办" value="pending" />
          <el-option label="成功" value="success" />
          <el-option label="失败" value="failed" />
          <el-option label="跳过" value="skipped" />
        </el-select>
      </el-form-item>
      <el-form-item label="时间">
        <el-date-picker
          v-model="dateRange"
          style="width: 340px"
          value-format="YYYY-MM-DD HH:mm:ss"
          type="datetimerange"
          range-separator="-"
          start-placeholder="开始时间"
          end-placeholder="结束时间"
          :default-time="[new Date(2000, 1, 1, 0, 0, 0), new Date(2000, 1, 1, 23, 59, 59)]"
        />
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="Search" @click="handleQuery">搜索</el-button>
        <el-button icon="Refresh" @click="resetQuery">重置</el-button>
        <el-button type="info" plain icon="Download" @click="handleExport">导出</el-button>
      </el-form-item>
    </el-form>

    <el-table v-loading="loading" :data="logList">
      <el-table-column label="记录ID" align="center" prop="id" width="80" />
      <el-table-column label="账号批次" align="center" prop="accountBatchTitle" min-width="120" :show-overflow-tooltip="true" />
      <el-table-column label="账号" align="center" prop="accountPhone" min-width="140" />
      <el-table-column label="账号状态" align="center" prop="accountStatus" width="100">
        <template #default="scope">
          <el-tag type="success" v-if="scope.row.accountStatus === 'online'">在线</el-tag>
          <el-tag type="info" v-else-if="scope.row.accountStatus === 'offline'">离线</el-tag>
          <el-tag type="danger" v-else-if="scope.row.accountStatus === 'failed'">失败</el-tag>
          <el-tag type="warning" v-else-if="scope.row.accountStatus === 'banned'">封禁</el-tag>
          <el-tag type="info" v-else-if="scope.row.accountStatus === 'waiting'">等待</el-tag>
          <el-tag type="info" v-else>{{ scope.row.accountStatus || '-' }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="好友批次" align="center" prop="contactBatchTitle" min-width="120" :show-overflow-tooltip="true" />
      <el-table-column label="好友号码/用户名" align="center" min-width="140">
        <template #default="scope">
          {{ scope.row.contactPhone || scope.row.contactUsername || '' }}
        </template>
      </el-table-column>
      <el-table-column label="状态" align="center" prop="status" width="100">
        <template #default="scope">
          <el-tag type="success" v-if="scope.row.status === 'success'">成功</el-tag>
          <el-tag type="danger" v-else-if="scope.row.status === 'failed'">失败</el-tag>
          <el-tag type="warning" v-else-if="scope.row.status === 'skipped'">跳过</el-tag>
          <el-tag type="" v-else-if="scope.row.status === 'pending'">待办</el-tag>
          <el-tag type="info" v-else>{{ scope.row.status }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="添加次数" align="center" prop="retryCount" width="100" />
      <el-table-column label="备注" align="center" prop="remark" min-width="180" :show-overflow-tooltip="true" />
      <el-table-column label="时间" align="center" prop="createTime" width="180" />
    </el-table>

    <pagination
      v-show="total > 0"
      :total="total"
      v-model:page="queryParams.pageNum"
      v-model:limit="queryParams.pageSize"
      @pagination="getList"
    />
  </div>
</template>

<script setup name="ContactAssignLog">
import { listContactAssignLog } from "@/api/tg/import";

const { proxy } = getCurrentInstance();

const logList = ref([]);
const loading = ref(true);
const total = ref(0);
const dateRange = ref([]);

const queryRef = ref(null);
const queryParams = ref({
  pageNum: 1,
  pageSize: 20,
  accountPhone: undefined,
  status: undefined,
});

function getList() {
  loading.value = true;
  const params = { ...queryParams.value };
  if (dateRange.value && dateRange.value.length === 2) {
    params['params[beginCreateTime]'] = dateRange.value[0];
    params['params[endCreateTime]'] = dateRange.value[1];
  }
  listContactAssignLog(params).then(response => {
    logList.value = response.rows;
    total.value = response.total;
    loading.value = false;
  });
}

function handleQuery() {
  queryParams.value.pageNum = 1;
  getList();
}

function resetQuery() {
  queryParams.value.accountPhone = undefined;
  queryParams.value.status = undefined;
  dateRange.value = [];
  handleQuery();
}

function handleExport() {
  proxy.$modal.confirm('是否确认导出所有好友分配日志数据？').then(() => {
    const params = { ...queryParams.value };
    delete params.pageNum;
    delete params.pageSize;
    if (dateRange.value && dateRange.value.length === 2) {
      params['params[beginCreateTime]'] = dateRange.value[0];
      params['params[endCreateTime]'] = dateRange.value[1];
    }
    proxy.download('tg/import/contactAssignLog/export', params, '好友分配日志.xlsx');
  }).catch(() => {});
}

getList();
</script>
