<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryRef" :inline="true" v-show="showSearch">
      <el-form-item label="IP组" prop="groupNo">
        <el-select v-model="queryParams.groupNo" placeholder="全部IP组" clearable style="width: 200px">
          <el-option v-for="g in groupOptions" :key="g.groupNo" :label="g.title || g.groupNo" :value="g.groupNo" />
        </el-select>
      </el-form-item>
      <el-form-item label="状态" prop="status">
        <el-select v-model="queryParams.status" placeholder="全部" clearable>
          <el-option label="可用" value="active" />
          <el-option label="已过期" value="expired" />
          <el-option label="已废弃" value="disabled" />
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="Search" @click="handleQuery">搜索</el-button>
        <el-button icon="Refresh" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button type="danger" icon="Delete" :disabled="multiple" @click="handleDelete">删除</el-button>
      </el-col>
    </el-row>

    <el-table v-loading="loading" :data="ipList" @selection-change="handleSelectionChange" style="width: 100%" :header-cell-style="{whiteSpace: 'nowrap'}" :cell-style="{whiteSpace: 'nowrap'}">
      <el-table-column type="selection" width="50" align="center" />
      <el-table-column label="ID" prop="id" width="60" />
      <el-table-column label="归属IP组" prop="groupTitle" min-width="120" />
      <el-table-column label="协议" prop="protocol" width="80" />
      <el-table-column label="最大绑定数" prop="maxBindable" width="100" align="center" />
      <el-table-column label="用户名" prop="username" min-width="130">
        <template #default="scope">
          <span>{{ scope.row.username || '-' }}</span>
        </template>
      </el-table-column>
      <el-table-column label="当前绑定" prop="currentBindCount" width="90" align="center" />
      <el-table-column label="历史绑定" prop="historyBindCount" width="90" align="center" />
      <el-table-column label="状态" prop="status" width="100">
        <template #default="scope">
          <el-dropdown trigger="click" @command="(cmd) => handleChangeStatus(scope.row, cmd)">
            <el-tag :type="scope.row.status === 'active' ? 'success' : scope.row.status === 'expired' ? 'warning' : 'danger'" size="small" style="cursor:pointer">
              {{ scope.row.status === 'active' ? '可用' : scope.row.status === 'expired' ? '已过期' : '已废弃' }}
              <el-icon class="el-icon--right"><arrow-down /></el-icon>
            </el-tag>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item command="active" :disabled="scope.row.status === 'active'">可用</el-dropdown-item>
                <el-dropdown-item command="expired" :disabled="scope.row.status === 'expired'">已过期</el-dropdown-item>
                <el-dropdown-item command="disabled" :disabled="scope.row.status === 'disabled'">已废弃</el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="150" fixed="right">
        <template #default="scope">
          <el-button link type="primary" @click="handleView(scope.row)">查看</el-button>
          <el-button link type="primary" @click="handleTest(scope.row)">测试</el-button>
          <el-button link type="danger" @click="handleDelete(scope.row)">删除</el-button>
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

    <!-- 查看IP详情对话框 -->
    <el-dialog v-model="viewVisible" title="IP代理详情" width="500px" append-to-body>
      <el-descriptions :column="1" border v-if="viewData">
        <el-descriptions-item label="ID">{{ viewData.id }}</el-descriptions-item>
        <el-descriptions-item label="归属IP组">{{ viewData.groupTitle }}</el-descriptions-item>
        <el-descriptions-item label="协议">{{ viewData.protocol }}</el-descriptions-item>
        <el-descriptions-item label="主机">{{ viewData.host }}</el-descriptions-item>
        <el-descriptions-item label="端口">{{ viewData.port }}</el-descriptions-item>
        <el-descriptions-item label="用户名">{{ viewData.username || '-' }}</el-descriptions-item>
        <el-descriptions-item label="密码">{{ viewData.password || '-' }}</el-descriptions-item>
        <el-descriptions-item label="完整代理URL">
          <span style="word-break:break-all">{{ viewData.proxyUrl }}</span>
        </el-descriptions-item>
        <el-descriptions-item label="最大绑定数">{{ viewData.maxBindable }}</el-descriptions-item>
        <el-descriptions-item label="当前绑定数">{{ viewData.currentBindCount }}</el-descriptions-item>
        <el-descriptions-item label="历史绑定数">{{ viewData.historyBindCount }}</el-descriptions-item>
        <el-descriptions-item label="状态">{{ viewData.status }}</el-descriptions-item>
      </el-descriptions>
    </el-dialog>

    <!-- 测试代理对话框 -->
    <el-dialog v-model="testVisible" title="测试代理IP" width="500px" append-to-body>
      <div v-loading="testLoading" element-loading-text="正在测试代理连通性...">
        <el-descriptions :column="1" border v-if="testResult">
          <el-descriptions-item label="连通性">
            <el-tag :type="testResult.connected ? 'success' : 'danger'" size="small">
              {{ testResult.connected ? '连通' : '不通' }}
            </el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="延迟">
            {{ testResult.latency_ms != null ? testResult.latency_ms + ' ms' : '-' }}
          </el-descriptions-item>
          <el-descriptions-item label="真实IP地址">{{ testResult.real_ip || '-' }}</el-descriptions-item>
          <el-descriptions-item label="归属地" v-if="testResult.geo">
            {{ testResult.geo.country }} {{ testResult.geo.region }} {{ testResult.geo.city }}
          </el-descriptions-item>
          <el-descriptions-item label="ISP" v-if="testResult.geo">
            {{ testResult.geo.isp || '-' }}
          </el-descriptions-item>
          <el-descriptions-item label="组织" v-if="testResult.geo">
            {{ testResult.geo.org || '-' }}
          </el-descriptions-item>
          <el-descriptions-item label="错误" v-if="testResult.error">
            <span style="color:red">{{ testResult.error }}</span>
          </el-descriptions-item>
        </el-descriptions>
        <el-empty v-if="!testLoading && !testResult" description="点击测试按钮开始测试" />
      </div>
    </el-dialog>
  </div>
</template>

<script setup name="ProxyIp">
import { listProxyIp, getProxyIpDetail, delProxyIp, testProxyIp, changeProxyIpStatus } from "@/api/tg/proxy";
import { listAllProxyGroup } from "@/api/tg/proxy";

const { proxy } = getCurrentInstance();
const route = useRoute();

const ipList = ref([]);
const loading = ref(true);
const showSearch = ref(true);
const ids = ref([]);
const multiple = ref(true);
const total = ref(0);
const groupOptions = ref([]);

const viewVisible = ref(false);
const viewData = ref(null);
const testVisible = ref(false);
const testLoading = ref(false);
const testResult = ref(null);

const data = reactive({
  queryParams: {
    pageNum: 1,
    pageSize: 10,
    groupNo: route.query.groupNo || undefined,
    status: undefined,
  },
});
const { queryParams } = toRefs(data);

function getList() {
  loading.value = true;
  listProxyIp(queryParams.value).then(res => {
    ipList.value = res.rows;
    total.value = res.total;
    loading.value = false;
  });
}

function loadGroups() {
  listAllProxyGroup().then(res => {
    if (res.code === 200) groupOptions.value = res.data || [];
  });
}

function handleQuery() { queryParams.value.pageNum = 1; getList(); }
function resetQuery() { proxy.resetForm("queryRef"); queryParams.value.groupNo = undefined; handleQuery(); }
function handleSelectionChange(sel) { ids.value = sel.map(i => i.id); multiple.value = !sel.length; }

function handleView(row) {
  getProxyIpDetail(row.id).then(res => {
    viewData.value = res.data;
    viewVisible.value = true;
  });
}

function handleTest(row) {
  testResult.value = null;
  testLoading.value = true;
  testVisible.value = true;
  testProxyIp(row.id).then(res => {
    let data = res.data;
    if (typeof data === 'string') {
      try { data = JSON.parse(data); } catch(e) { /* ignore */ }
    }
    testResult.value = data;
  }).catch(err => {
    testResult.value = { connected: false, error: err.message || String(err) };
  }).finally(() => {
    testLoading.value = false;
  });
}

function handleChangeStatus(row, newStatus) {
  const statusLabels = { active: '可用', expired: '已过期', disabled: '已废弃' };
  proxy.$modal.confirm(`确认将该IP状态修改为「${statusLabels[newStatus]}」？`).then(() => {
    return changeProxyIpStatus({ id: row.id, status: newStatus });
  }).then(() => {
    proxy.$modal.msgSuccess("修改成功");
    getList();
  }).catch(() => {});
}

function handleDelete(row) {
  const delIds = row.id ? [row.id] : ids.value;
  proxy.$modal.confirm("确认删除选中的IP代理？").then(() => {
    return delProxyIp(delIds);
  }).then(() => {
    getList();
    proxy.$modal.msgSuccess("删除成功");
  }).catch(() => {});
}

loadGroups();
getList();
</script>
