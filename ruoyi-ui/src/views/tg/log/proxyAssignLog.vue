<template>
  <div class="app-container">
    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button icon="Refresh" @click="getList">刷新</el-button>
      </el-col>
    </el-row>

    <el-table v-loading="loading" :data="logList">
      <el-table-column label="账号批次" align="center" prop="accountBatchTitle" min-width="120" :show-overflow-tooltip="true" />
      <el-table-column label="账号" align="center" prop="accountPhone" min-width="140" />
      <el-table-column label="IP组" align="center" prop="proxyGroupTitle" min-width="120" :show-overflow-tooltip="true" />
      <el-table-column label="代理地址" align="center" prop="proxyUrl" min-width="200" :show-overflow-tooltip="true" />
      <el-table-column label="节点ID" align="center" prop="nodeId" min-width="160" :show-overflow-tooltip="true" />
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

<script setup name="ProxyAssignLog">
import { listProxyAssignLog } from "@/api/tg/import";

const logList = ref([]);
const loading = ref(true);
const total = ref(0);

const queryParams = ref({
  pageNum: 1,
  pageSize: 20,
});

function getList() {
  loading.value = true;
  listProxyAssignLog(queryParams.value).then(response => {
    logList.value = response.rows;
    total.value = response.total;
    loading.value = false;
  });
}

getList();
</script>
