<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryRef" :inline="true" label-width="80px">
      <el-form-item label="开始日期" prop="startDate">
        <el-date-picker
          v-model="queryParams.startDate"
          type="date"
          value-format="YYYY-MM-DD"
          placeholder="选择开始日期"
          :clearable="false"
          :disabled-date="disabledDate"
          style="width: 200px"
        />
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="Search" @click="handleQuery">查询</el-button>
        <el-button icon="Refresh" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <el-alert
      title="默认查询最近7天数据，最多查询最近30天。回复率数据实时统计。"
      type="info"
      :closable="false"
      show-icon
      style="margin-bottom: 12px"
    />

    <el-table v-loading="loading" :data="statList" border :table-layout="'auto'">
      <el-table-column label="统计日期" align="center" prop="statDate" min-width="120" />
      <el-table-column label="账号数" align="center" prop="accountCount" min-width="100" />
      <el-table-column label="添加人数" align="center" prop="totalAddCount" min-width="110" />
      <el-table-column label="平均添加数" align="center" min-width="110">
        <template #default="scope">{{ toNumber(scope.row.addPerCount) }}</template>
      </el-table-column>
      <el-table-column label="发送人数" align="center" prop="totalSendCount" min-width="110" />
      <el-table-column label="添加发送率" align="center" min-width="120">
        <template #default="scope">{{ toPercent(scope.row.sendRatio) }}</template>
      </el-table-column>
      <el-table-column label="回复人数" align="center" prop="sendReplyCount" min-width="110" />
      <el-table-column label="发送回复率" align="center" min-width="120">
        <template #default="scope">{{ toPercent(scope.row.sendReplyRatio) }}</template>
      </el-table-column>
      <el-table-column label="添加回复率" align="center" min-width="120">
        <template #default="scope">{{ toPercent(scope.row.addReplyRatio) }}</template>
      </el-table-column>
    </el-table>
    <el-empty v-if="!loading && statList.length === 0" description="暂无数据" />
  </div>
</template>

<script setup name="ReplyStat">
import { listReplyStat } from "@/api/tg/replyStat";

const { proxy } = getCurrentInstance();

const loading = ref(false);
const statList = ref([]);

// 最近7天的开始日期（今天往前6天）
function defaultStartDate() {
  const d = new Date();
  d.setDate(d.getDate() - 6);
  return formatDate(d);
}

function formatDate(d) {
  const y = d.getFullYear();
  const m = String(d.getMonth() + 1).padStart(2, "0");
  const day = String(d.getDate()).padStart(2, "0");
  return `${y}-${m}-${day}`;
}

const queryParams = reactive({
  startDate: defaultStartDate()
});

// 只允许选择最近30天内、且不晚于今天的日期
function disabledDate(time) {
  const now = new Date();
  const today = new Date(now.getFullYear(), now.getMonth(), now.getDate());
  const min = new Date(today);
  min.setDate(min.getDate() - 29);
  return time.getTime() > today.getTime() || time.getTime() < min.getTime();
}

function toNumber(v) {
  if (v === null || v === undefined || v === "") return "-";
  const n = Number(v);
  if (isNaN(n)) return "-";
  return n.toFixed(2);
}

function toPercent(v) {
  if (v === null || v === undefined || v === "") return "-";
  const n = Number(v);
  if (isNaN(n)) return "-";
  return (n * 100).toFixed(2) + "%";
}

function getList() {
  loading.value = true;
  listReplyStat({ startDate: queryParams.startDate })
    .then((res) => {
      statList.value = res.data || [];
    })
    .finally(() => {
      loading.value = false;
    });
}

function handleQuery() {
  getList();
}

function resetQuery() {
  queryParams.startDate = defaultStartDate();
  getList();
}

getList();
</script>
