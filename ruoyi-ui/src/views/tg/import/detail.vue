<template>
  <div class="app-container">
    <el-page-header @back="goBack" style="margin-bottom: 15px;">
      <template #content>
        <span style="font-size: 16px; font-weight: bold;">导入详情 - {{ batchNo }}</span>
      </template>
    </el-page-header>

    <el-form :model="queryParams" ref="queryRef" :inline="true" v-show="showSearch">
      <el-form-item label="手机号" prop="phone">
        <el-input
          v-model="queryParams.phone"
          placeholder="请输入手机号"
          clearable
          style="width: 200px"
          @keyup.enter="handleQuery"
        />
      </el-form-item>
      <el-form-item label="状态" prop="status">
        <el-select v-model="queryParams.status" placeholder="请选择" clearable style="width: 160px">
          <el-option label="等待登录" value="waiting" />
          <el-option label="已登录" value="online" />
          <el-option label="登录失败" value="failed" />
          <el-option label="已注销" value="banned" />
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="Search" @click="handleQuery">搜索</el-button>
        <el-button icon="Refresh" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <el-table v-loading="loading" :data="accountList">
      <el-table-column label="ID" align="center" prop="id" width="60" />
      <el-table-column label="手机号" align="center" prop="phone" width="160" />
      <el-table-column label="状态" align="center" prop="status" width="120">
        <template #default="scope">
          <el-tag :type="statusTagType(scope.row.status)">{{ statusText(scope.row.status) }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="昵称" align="center" prop="nickname" min-width="120" :show-overflow-tooltip="true">
        <template #default="scope">
          <span>{{ scope.row.nickname || '-' }}</span>
        </template>
      </el-table-column>
      <el-table-column label="用户名" align="center" prop="username" min-width="120" :show-overflow-tooltip="true">
        <template #default="scope">
          <span v-if="scope.row.username">@{{ scope.row.username }}</span>
          <span v-else>-</span>
        </template>
      </el-table-column>
      <el-table-column label="TG用户ID" align="center" prop="tgUserId" width="150">
        <template #default="scope">
          <span>{{ scope.row.tgUserId || '-' }}</span>
        </template>
      </el-table-column>
      <el-table-column label="失败原因" align="center" prop="reason" min-width="200" :show-overflow-tooltip="true">
        <template #default="scope">
          <span style="color: #f56c6c;" v-if="scope.row.reason">{{ scope.row.reason }}</span>
          <span v-else>-</span>
        </template>
      </el-table-column>
      <el-table-column label="登录时间" align="center" prop="loginTime" width="180">
        <template #default="scope">
          <span>{{ scope.row.loginTime || '-' }}</span>
        </template>
      </el-table-column>
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

<script setup name="ImportDetail">
import { listImportAccount } from "@/api/tg/import";

const route = useRoute();
const router = useRouter();
const { proxy } = getCurrentInstance();

const batchNo = ref(route.query.batchNo || '');
const accountList = ref([]);
const loading = ref(true);
const showSearch = ref(true);
const total = ref(0);

const queryParams = ref({
  pageNum: 1,
  pageSize: 10,
  batchNo: batchNo.value,
  phone: undefined,
  status: undefined,
});

function statusTagType(status) {
  if (status === "online") return "success";
  if (status === "waiting") return "warning";
  if (status === "failed") return "danger";
  if (status === "banned") return "danger";
  return "info";
}

function statusText(status) {
  if (status === "online") return "已登录";
  if (status === "waiting") return "等待登录";
  if (status === "failed") return "登录失败";
  if (status === "banned") return "已注销";
  return status || "未知";
}

function getList() {
  loading.value = true;
  listImportAccount(queryParams.value).then(response => {
    accountList.value = response.rows;
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
  queryParams.value.status = undefined;
  handleQuery();
}

function goBack() {
  router.push('/tg/import');
}

getList();
</script>
