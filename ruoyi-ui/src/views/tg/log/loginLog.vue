<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryRef" :inline="true" v-show="showSearch">
      <el-form-item label="账号" prop="phone">
        <el-input v-model="queryParams.phone" placeholder="请输入账号" clearable style="width: 200px" @keyup.enter="handleQuery" />
      </el-form-item>
      <el-form-item label="登录结果" prop="result">
        <el-select v-model="queryParams.result" placeholder="全部" clearable style="width: 150px">
          <el-option label="成功" value="success" />
          <el-option label="失败" value="failed" />
          <el-option label="已注销" value="banned" />
          <el-option label="登出" value="logout" />
        </el-select>
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
      <el-table-column label="ID" align="center" prop="id" width="80" />
      <el-table-column label="账号" align="center" prop="phone" min-width="140" />
      <el-table-column label="昵称" align="center" prop="nickname" min-width="120" :show-overflow-tooltip="true" />
      <el-table-column label="登录结果" align="center" prop="result" width="100">
        <template #default="scope">
          <el-tag type="success" v-if="scope.row.result === 'success'">成功</el-tag>
          <el-tag type="danger" v-else-if="scope.row.result === 'failed'">失败</el-tag>
          <el-tag type="warning" v-else-if="scope.row.result === 'banned'">已注销</el-tag>
          <el-tag type="" v-else-if="scope.row.result === 'logout'">登出</el-tag>
          <el-tag type="info" v-else>{{ scope.row.result }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="错误原因" align="center" prop="reason" min-width="200" :show-overflow-tooltip="true" />
      <el-table-column label="代理信息" align="center" prop="proxyInfo" min-width="200" :show-overflow-tooltip="true" />
      <el-table-column label="节点ID" align="center" prop="nodeId" min-width="160" :show-overflow-tooltip="true" />
      <el-table-column label="登录时间" align="center" prop="loginTime" width="180" />
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

<script setup name="LoginLog">
import { listLoginLog } from "@/api/tg/import";

const showSearch = ref(true);
const logList = ref([]);
const loading = ref(true);
const total = ref(0);

const queryParams = ref({
  pageNum: 1,
  pageSize: 20,
  phone: undefined,
  result: undefined,
});

function getList() {
  loading.value = true;
  listLoginLog(queryParams.value).then(response => {
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
  queryParams.value.result = undefined;
  handleQuery();
}

getList();
</script>
