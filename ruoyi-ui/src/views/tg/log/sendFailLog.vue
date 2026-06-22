<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryRef" :inline="true" v-show="showSearch">
      <el-form-item label="账号" prop="phone">
        <el-input v-model="queryParams.phone" placeholder="请输入账号" clearable style="width: 200px" @keyup.enter="handleQuery" />
      </el-form-item>
      <el-form-item label="好友昵称" prop="friendNickname">
        <el-input v-model="queryParams.friendNickname" placeholder="请输入好友昵称" clearable style="width: 200px" @keyup.enter="handleQuery" />
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="Search" @click="handleQuery">搜索</el-button>
        <el-button icon="Refresh" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button icon="Refresh" @click="getList">刷新</el-button>
      </el-col>
    </el-row>

    <el-table v-loading="loading" :data="logList">
      <el-table-column label="ID" align="center" prop="id" width="70" />
      <el-table-column label="账号" align="center" prop="phone" min-width="130" />
      <el-table-column label="账号昵称" align="center" prop="nickname" min-width="110" :show-overflow-tooltip="true" />
      <el-table-column label="好友昵称" align="center" prop="friendNickname" min-width="110" :show-overflow-tooltip="true" />
      <el-table-column label="好友号码" align="center" prop="friendPhone" min-width="130" />
      <el-table-column label="消息类型" align="center" prop="contentType" width="90" />
      <el-table-column label="发送内容" align="center" prop="content" min-width="200" :show-overflow-tooltip="true" />
      <el-table-column label="失败原因" align="center" prop="errorReason" min-width="200" :show-overflow-tooltip="true" />
      <el-table-column label="发送时间" align="center" prop="sendTime" width="170" />
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

<script setup name="SendFailLog">
import { listSendFailLog } from "@/api/tg/import";

const showSearch = ref(true);
const logList = ref([]);
const loading = ref(true);
const total = ref(0);

const queryParams = ref({
  pageNum: 1,
  pageSize: 20,
  phone: undefined,
  friendNickname: undefined,
});

function getList() {
  loading.value = true;
  listSendFailLog(queryParams.value).then(response => {
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
  queryParams.value.phone = undefined;
  queryParams.value.friendNickname = undefined;
  handleQuery();
}

getList();
</script>
