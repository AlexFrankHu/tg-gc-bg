<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryRef" :inline="true" v-show="showSearch">
      <el-form-item label="账号" prop="accountPhone">
        <el-input v-model="queryParams.accountPhone" placeholder="请输入账号" clearable style="width: 180px" @keyup.enter="handleQuery" />
      </el-form-item>
      <el-form-item label="好友" prop="friendNickname">
        <el-input v-model="queryParams.friendNickname" placeholder="昵称/手机号" clearable style="width: 160px" @keyup.enter="handleQuery" />
      </el-form-item>
      <el-form-item label="结果" prop="sendResult">
        <el-select v-model="queryParams.sendResult" placeholder="全部" clearable style="width: 120px">
          <el-option label="成功" value="success" />
          <el-option label="失败" value="failed" />
          <el-option label="无回复" value="no_reply" />
          <el-option label="API错误" value="api_error" />
        </el-select>
      </el-form-item>
      <el-form-item label="触发方式" prop="triggerType">
        <el-select v-model="queryParams.triggerType" placeholder="全部" clearable style="width: 120px">
          <el-option label="收到消息" value="incoming" />
          <el-option label="定时轮询" value="polling" />
        </el-select>
      </el-form-item>
      <el-form-item label="时间">
        <el-date-picker
          v-model="dateRange"
          type="datetimerange"
          value-format="YYYY-MM-DD HH:mm:ss"
          range-separator="-"
          start-placeholder="开始时间"
          end-placeholder="结束时间"
          style="width: 360px"
        />
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="Search" @click="handleQuery">搜索</el-button>
        <el-button icon="Refresh" @click="resetQuery">重置</el-button>
        <el-button type="info" plain icon="Download" @click="handleExport">导出</el-button>
      </el-form-item>
    </el-form>

    <el-table v-loading="loading" :data="logList" :table-layout="'auto'">
      <el-table-column label="ID" align="center" prop="id" width="70" />
      <el-table-column label="账号" align="center" prop="accountPhone" min-width="130" />
      <el-table-column label="账号昵称" align="center" prop="accountNickname" min-width="110" :show-overflow-tooltip="true" />
      <el-table-column label="好友昵称" align="center" prop="friendNickname" min-width="110" :show-overflow-tooltip="true" />
      <el-table-column label="好友号码" align="center" prop="friendPhone" min-width="130" />
      <el-table-column label="触发方式" align="center" prop="triggerType" width="100">
        <template #default="scope">
          <el-tag type="primary" v-if="scope.row.triggerType === 'incoming'">收到消息</el-tag>
          <el-tag type="warning" v-else-if="scope.row.triggerType === 'polling'">定时轮询</el-tag>
          <el-tag type="info" v-else>{{ scope.row.triggerType }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="State" align="center" prop="state" width="70" />
      <el-table-column label="请求参数" align="center" prop="requestParams" min-width="200" :show-overflow-tooltip="true" />
      <el-table-column label="聊天上下文" align="center" prop="chatContext" min-width="200" :show-overflow-tooltip="true" />
      <el-table-column label="回复内容" align="center" prop="replyContent" min-width="200" :show-overflow-tooltip="true" />
      <el-table-column label="发送结果" align="center" prop="sendResult" width="100">
        <template #default="scope">
          <el-tag type="success" v-if="scope.row.sendResult === 'success'">成功</el-tag>
          <el-tag type="danger" v-else-if="scope.row.sendResult === 'failed'">失败</el-tag>
          <el-tag type="warning" v-else-if="scope.row.sendResult === 'no_reply'">无回复</el-tag>
          <el-tag type="info" v-else-if="scope.row.sendResult === 'api_error'">API错误</el-tag>
          <el-tag type="info" v-else>{{ scope.row.sendResult }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="错误原因" align="center" prop="errorReason" min-width="200" :show-overflow-tooltip="true" />
      <el-table-column label="节点ID" align="center" prop="nodeId" min-width="160" :show-overflow-tooltip="true" />
      <el-table-column label="时间" align="center" prop="createTime" width="170" />
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

<script setup name="AutoReplyLog">
import { listAutoReplyLog } from "@/api/tg/import";

const { proxy } = getCurrentInstance();

const showSearch = ref(true);
const logList = ref([]);
const loading = ref(true);
const total = ref(0);
const dateRange = ref([]);

const queryParams = ref({
  pageNum: 1,
  pageSize: 20,
  accountPhone: undefined,
  friendNickname: undefined,
  sendResult: undefined,
  triggerType: undefined,
});

function getList() {
  loading.value = true;
  listAutoReplyLog(proxy.addDateRange(queryParams.value, dateRange.value)).then(response => {
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
  dateRange.value = [];
  queryParams.value.accountPhone = undefined;
  queryParams.value.friendNickname = undefined;
  queryParams.value.sendResult = undefined;
  queryParams.value.triggerType = undefined;
  handleQuery();
}

function handleExport() {
  proxy.$modal.confirm('是否确认导出当前筛选条件的自动回复日志数据？').then(() => {
    const params = { ...proxy.addDateRange(queryParams.value, dateRange.value) };
    delete params.pageNum;
    delete params.pageSize;
    proxy.download('tg/import/autoReplyLog/export', params, '自动回复日志.xlsx', { timeout: 300000 });
  }).catch(() => {});
}

getList();
</script>
