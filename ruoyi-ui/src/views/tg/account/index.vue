<template>
  <div class="app-container">
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
      <el-form-item label="昵称" prop="nickname">
        <el-input
          v-model="queryParams.nickname"
          placeholder="请输入昵称"
          clearable
          style="width: 200px"
          @keyup.enter="handleQuery"
        />
      </el-form-item>
      <el-form-item label="批次号" prop="batchNo">
        <el-input
          v-model="queryParams.batchNo"
          placeholder="请输入批次号"
          clearable
          style="width: 200px"
          @keyup.enter="handleQuery"
        />
      </el-form-item>
      <el-form-item label="状态" prop="status">
        <el-select v-model="queryParams.status" placeholder="请选择" clearable style="width: 200px">
          <el-option label="在线" value="online" />
          <el-option label="离线" value="offline" />
          <el-option label="等待登录" value="waiting" />
          <el-option label="登录中(代理)" value="login1" />
          <el-option label="登录中(无代理)" value="login2" />
          <el-option label="已限制" value="restricted" />
          <el-option label="已注销" value="banned" />
        </el-select>
      </el-form-item>
      <el-form-item label="节点ID" prop="nodeId">
        <el-input
          v-model="queryParams.nodeId"
          placeholder="请输入节点ID"
          clearable
          style="width: 200px"
          @keyup.enter="handleQuery"
        />
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="Search" @click="handleQuery">搜索</el-button>
        <el-button icon="Refresh" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button type="success" icon="Connection" @click="showBatchLoginDialog" v-hasPermi="['tg:account:edit']">批量登录</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="warning" icon="SwitchButton" @click="showBatchLogoutDialog" v-hasPermi="['tg:account:edit']">批量登出</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="success" plain @click="handleAllAccountAutoReply(true)" v-hasPermi="['tg:account:edit']">全部开启自动回复</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="warning" plain @click="handleAllAccountAutoReply(false)" v-hasPermi="['tg:account:edit']">全部关闭自动回复</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="danger" plain icon="Delete" :disabled="multiple" @click="handleDelete" v-hasPermi="['tg:account:remove']">删除</el-button>
      </el-col>
      <right-toolbar v-model:showSearch="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="accountList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="ID" align="center" prop="id" width="60" />
      <el-table-column label="手机号" align="center" prop="phone" width="160" />
      <el-table-column label="批次" align="center" prop="batchTitle" min-width="120" :show-overflow-tooltip="true">
        <template #default="scope">
          <span>{{ scope.row.batchTitle || '-' }}</span>
        </template>
      </el-table-column>
      <el-table-column label="TG昵称" align="center" prop="nickname" :show-overflow-tooltip="true" min-width="120" />
      <el-table-column label="TG用户名" align="center" prop="username" :show-overflow-tooltip="true" min-width="120">
        <template #default="scope">
          <span v-if="scope.row.username">@{{ scope.row.username }}</span>
          <span v-else>-</span>
        </template>
      </el-table-column>
      <el-table-column label="TG用户ID" align="center" prop="tgUserId" width="140" />
      <el-table-column label="国家" align="center" prop="country" width="120" :show-overflow-tooltip="true">
        <template #default="scope">
          <span>{{ scope.row.country || '-' }}</span>
        </template>
      </el-table-column>
      <el-table-column label="代理IP" align="center" min-width="120" :show-overflow-tooltip="true">
        <template #default="scope">
          <el-tag v-if="scope.row.proxyGroupTitle" type="success" size="small">{{ scope.row.proxyGroupTitle }}</el-tag>
          <el-tag v-else-if="scope.row.proxyUrl" type="warning" size="small">手动配置</el-tag>
          <span v-else>-</span>
        </template>
      </el-table-column>
      <el-table-column label="自动回复" align="center" min-width="100">
        <template #default="scope">
          <el-tag :type="scope.row.autoReply ? 'success' : 'danger'" size="small">{{ scope.row.autoReply ? '开启' : '关闭' }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="是否被限制" align="center" min-width="100">
        <template #default="scope">
          <el-tag :type="scope.row.isRestricted ? 'danger' : 'success'" size="small">{{ scope.row.isRestricted ? '已限制' : '正常' }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="状态" align="center" prop="status" width="100">
        <template #default="scope">
          <el-tag :type="statusTagType(scope.row.status)">{{ statusText(scope.row.status) }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="节点ID" align="center" prop="nodeId" width="140" show-overflow-tooltip />
      <el-table-column label="消息总数" align="center" prop="totalMsgCount" width="90">
        <template #default="scope">
          <span>{{ scope.row.totalMsgCount || 0 }}</span>
        </template>
      </el-table-column>
      <el-table-column label="发送总数" align="center" prop="sentMsgCount" width="90">
        <template #default="scope">
          <span style="color: #67c23a">{{ scope.row.sentMsgCount || 0 }}</span>
        </template>
      </el-table-column>
      <el-table-column label="接收总数" align="center" prop="recvMsgCount" width="90">
        <template #default="scope">
          <span style="color: #409eff">{{ scope.row.recvMsgCount || 0 }}</span>
        </template>
      </el-table-column>
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width" width="680">
        <template #default="scope">
          <el-button link type="success" @click="handleLogin(scope.row)" v-if="scope.row.status !== 'online'" v-hasPermi="['tg:account:edit']">登录</el-button>
          <el-button link type="info" @click="handleLoginNoProxy(scope.row)" v-if="scope.row.status !== 'online'" v-hasPermi="['tg:account:edit']">无代理登录</el-button>
          <el-button link type="warning" @click="handleLogout(scope.row)" v-if="scope.row.status === 'online'" v-hasPermi="['tg:account:edit']">登出</el-button>
          <!-- <el-button link type="primary" @click="handleWebClient(scope.row)" v-if="scope.row.status === 'online'">网页端</el-button> -->
          <el-dropdown trigger="click" @command="(cmd) => handleProxyCommand(cmd, scope.row)">
            <el-button link type="primary">代理IP<el-icon class="el-icon--right"><arrow-down /></el-icon></el-button>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item command="view">查看代理IP</el-dropdown-item>
                <el-dropdown-item command="auto">自动选择代理IP</el-dropdown-item>
                <el-dropdown-item command="manual">手动选择代理IP</el-dropdown-item>
                <el-dropdown-item command="config">手动配置代理IP</el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
          <el-button v-if="scope.row.autoReply" link type="danger" @click="handleToggleAccountAutoReply(scope.row)" v-hasPermi="['tg:account:edit']">关闭自动回复</el-button>
          <el-button v-else link type="success" @click="handleToggleAccountAutoReply(scope.row)" v-hasPermi="['tg:account:edit']">开启自动回复</el-button>
          <el-button link type="success" icon="Download" @click="handleExportChat(scope.row)">导出聊天记录</el-button>
          <el-button link type="primary" icon="Delete" @click="handleDelete(scope.row)" v-if="scope.row.status !== 'online'" v-hasPermi="['tg:account:remove']">删除</el-button>
        </template>
      </el-table-column>
      <el-table-column label="最后登录时间" align="center" prop="lastLoginTime" width="180">
        <template #default="scope">
          <span>{{ scope.row.lastLoginTime || '-' }}</span>
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

    <!-- 批量登录对话框 -->
    <el-dialog v-model="batchLoginVisible" title="批量登录" width="450px" append-to-body>
      <el-form label-width="80px">
        <el-form-item label="选择批次">
          <el-select v-model="selectedBatchNo" placeholder="请选择要登录的批次" style="width: 100%">
            <el-option key="__all__" label="全部（登录所有未登录的账号）" value="__all__" />
            <el-option
              v-for="batch in batchOptions"
              :key="batch.batchNo"
              :label="batch.title || batch.batchNo"
              :value="batch.batchNo"
            />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="batchLoginVisible = false">取消</el-button>
        <el-button type="primary" :loading="batchLoginLoading" @click="submitBatchLogin" :disabled="!selectedBatchNo">确认登录</el-button>
      </template>
    </el-dialog>

    <!-- 批量登出对话框 -->
    <el-dialog v-model="batchLogoutVisible" title="批量登出" width="450px" append-to-body>
      <el-form label-width="80px">
        <el-form-item label="选择批次">
          <el-select v-model="selectedLogoutBatchNo" placeholder="请选择要登出的批次" style="width: 100%">
            <el-option key="__all__" label="全部（登出所有在线账号）" value="__all__" />
            <el-option
              v-for="batch in batchOptions"
              :key="'lo_' + batch.batchNo"
              :label="batch.title || batch.batchNo"
              :value="batch.batchNo"
            />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="batchLogoutVisible = false">取消</el-button>
        <el-button type="warning" :loading="batchLogoutLoading" @click="submitBatchLogout" :disabled="!selectedLogoutBatchNo">确认登出</el-button>
      </template>
    </el-dialog>

    <!-- 查看代理IP对话框 -->
    <el-dialog v-model="viewProxyVisible" title="查看代理IP" width="500px" append-to-body>
      <el-descriptions :column="1" border>
        <el-descriptions-item label="IP组">{{ viewProxyData.proxyGroupTitle || '-' }}</el-descriptions-item>
        <el-descriptions-item label="协议">{{ viewProxyData.proxyProtocol || '-' }}</el-descriptions-item>
        <el-descriptions-item label="主机">{{ viewProxyData.proxyHost || '-' }}</el-descriptions-item>
        <el-descriptions-item label="端口">{{ viewProxyData.proxyPort || '-' }}</el-descriptions-item>
        <el-descriptions-item label="用户名">{{ viewProxyData.proxyUsername || '-' }}</el-descriptions-item>
        <el-descriptions-item label="密码">{{ viewProxyData.proxyPassword || '-' }}</el-descriptions-item>
        <el-descriptions-item label="完整URL">{{ viewProxyData.proxyUrl || '未配置代理' }}</el-descriptions-item>
      </el-descriptions>
    </el-dialog>

    <!-- 自动选择代理IP对话框 -->
    <el-dialog v-model="autoProxyVisible" title="自动选择代理IP" width="450px" append-to-body>
      <el-form label-width="80px">
        <el-form-item label="IP组">
          <el-select v-model="autoProxyGroupNo" placeholder="请选择IP组" style="width: 100%">
            <el-option
              v-for="g in proxyGroupOptions"
              :key="g.groupNo"
              :label="g.title"
              :value="g.groupNo"
            />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="autoProxyVisible = false">取消</el-button>
        <el-button type="primary" :loading="autoProxyLoading" @click="submitAutoProxy" :disabled="!autoProxyGroupNo">确定</el-button>
      </template>
    </el-dialog>

    <!-- 手动选择代理IP对话框 -->
    <el-dialog v-model="manualProxyVisible" title="手动选择代理IP" width="450px" append-to-body>
      <el-form label-width="80px">
        <el-form-item label="IP组">
          <el-select v-model="manualProxyGroupNo" placeholder="请选择IP组" style="width: 100%" @change="onManualGroupChange">
            <el-option
              v-for="g in proxyGroupOptions"
              :key="g.groupNo"
              :label="g.title"
              :value="g.groupNo"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="代理IP" v-if="manualProxyGroupNo">
          <el-select v-model="manualProxyIpId" placeholder="请选择代理IP" style="width: 100%">
            <el-option
              v-for="ip in manualIpOptions"
              :key="ip.id"
              :label="ip.protocol + '://' + ip.host + ':' + ip.port + ' (' + (ip.currentBindCount || 0) + '/' + ip.maxBindable + ')'"
              :value="ip.id"
            />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="manualProxyVisible = false">取消</el-button>
        <el-button type="primary" :loading="manualProxyLoading" @click="submitManualProxy" :disabled="!manualProxyIpId">确定</el-button>
      </template>
    </el-dialog>

    <!-- 手动配置代理IP对话框 -->
    <el-dialog v-model="configProxyVisible" title="手动配置代理IP" width="450px" append-to-body>
      <el-form label-width="80px">
        <el-form-item label="协议">
          <el-select v-model="configProxyForm.protocol" placeholder="请选择" style="width: 100%">
            <el-option label="socks5" value="socks5" />
            <el-option label="socks4" value="socks4" />
            <el-option label="http" value="http" />
          </el-select>
        </el-form-item>
        <el-form-item label="主机">
          <el-input v-model="configProxyForm.host" placeholder="例如: 1.2.3.4" />
        </el-form-item>
        <el-form-item label="端口">
          <el-input v-model="configProxyForm.port" placeholder="例如: 1080" type="number" />
        </el-form-item>
        <el-form-item label="用户名">
          <el-input v-model="configProxyForm.username" placeholder="可选" />
        </el-form-item>
        <el-form-item label="密码">
          <el-input v-model="configProxyForm.password" placeholder="可选" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="configProxyVisible = false">取消</el-button>
        <el-button type="primary" :loading="configProxyLoading" @click="submitConfigProxy" :disabled="!configProxyForm.host || !configProxyForm.port">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup name="Account">
import { listAccount, getAccount, delAccount, triggerLogin, loginNoProxy, logoutAccount, getWsToken, loginBatch, logoutBatch, getProxyInfo, autoSelectProxy, manualSelectProxy, configProxy, updateAccountAutoReply, updateAllAccountAutoReply } from "@/api/tg/account";
import { listAllBatch } from "@/api/tg/import";
import { listAllProxyGroup, listProxyIp } from "@/api/tg/proxy";
import { ArrowDown } from '@element-plus/icons-vue';

const { proxy } = getCurrentInstance();

const accountList = ref([]);
const loading = ref(true);
const showSearch = ref(true);
const ids = ref([]);
const single = ref(true);
const multiple = ref(true);
const total = ref(0);

const batchLoginVisible = ref(false);
const batchLoginLoading = ref(false);
const selectedBatchNo = ref("");
const batchOptions = ref([]);

const batchLogoutVisible = ref(false);
const batchLogoutLoading = ref(false);
const selectedLogoutBatchNo = ref("");

// Proxy dialogs
const currentProxyAccountId = ref(null);
const proxyGroupOptions = ref([]);

const viewProxyVisible = ref(false);
const viewProxyData = ref({});

const autoProxyVisible = ref(false);
const autoProxyGroupNo = ref("");
const autoProxyLoading = ref(false);

const manualProxyVisible = ref(false);
const manualProxyGroupNo = ref("");
const manualProxyIpId = ref(null);
const manualIpOptions = ref([]);
const manualProxyLoading = ref(false);

const configProxyVisible = ref(false);
const configProxyForm = ref({ protocol: "socks5", host: "", port: "", username: "", password: "" });
const configProxyLoading = ref(false);

const data = reactive({
  queryParams: {
    pageNum: 1,
    pageSize: 10,
    phone: undefined,
    nickname: undefined,
    batchNo: undefined,
    status: undefined,
    nodeId: undefined,
  },
});

const { queryParams } = toRefs(data);

function statusTagType(status) {
  if (status === "online") return "success";
  if (status === "offline") return "info";
  if (status === "banned") return "danger";
  if (status === "waiting") return "warning";
  if (status === "failed") return "danger";
  if (status === "login1") return "warning";
  if (status === "login2") return "warning";
  if (status === "restricted") return "danger";
  return "info";
}

function statusText(status) {
  if (status === "online") return "在线";
  if (status === "offline") return "离线";
  if (status === "banned") return "已注销";
  if (status === "waiting") return "等待登录";
  if (status === "failed") return "登录失败";
  if (status === "login1") return "登录中(代理)";
  if (status === "login2") return "登录中(无代理)";
  if (status === "restricted") return "已限制";
  return status || "未知";
}

function getList() {
  loading.value = true;
  listAccount(queryParams.value).then(response => {
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
  proxy.resetForm("queryRef");
  handleQuery();
}

function handleSelectionChange(selection) {
  ids.value = selection.map(item => item.id);
  single.value = selection.length !== 1;
  multiple.value = !selection.length;
}

function handleDelete(row) {
  const accountIds = row.id ? [row.id] : ids.value;
  proxy.$modal.confirm('是否确认删除选中的账号？').then(function () {
    return delAccount(accountIds);
  }).then(() => {
    getList();
    proxy.$modal.msgSuccess("删除成功");
  }).catch(() => {});
}

function handleLogout(row) {
  if (row.status !== "online") {
    proxy.$modal.msgWarning("该账号未登录");
    return;
  }
  proxy.$modal.confirm('确认要登出账号 ' + row.phone + ' 吗？').then(() => {
    logoutAccount(row.id).then(() => {
      proxy.$modal.msgSuccess("已登出");
      getList();
    });
  }).catch(() => {});
}

function handleLogin(row) {
  if (row.status === "online") {
    proxy.$modal.msgWarning("该账号已登录");
    return;
  }
  proxy.$modal.confirm('确认要登录账号 ' + row.phone + ' 吗？').then(() => {
    triggerLogin(row.id).then(() => {
      proxy.$modal.msgSuccess("已触发登录，请稍候刷新查看结果");
      setTimeout(() => { getList(); }, 3000);
    });
  }).catch(() => {});
}

function handleLoginNoProxy(row) {
  if (row.status === "online") {
    proxy.$modal.msgWarning("该账号已登录");
    return;
  }
  proxy.$modal.confirm('确认要【无代理】登录账号 ' + row.phone + ' 吗？此操作不使用代理，仅用于测试。').then(() => {
    loginNoProxy(row.id).then(() => {
      proxy.$modal.msgSuccess("已触发无代理登录，请稍候刷新查看结果");
      setTimeout(() => { getList(); }, 3000);
    });
  }).catch(() => {});
}

function showBatchLoginDialog() {
  selectedBatchNo.value = "";
  batchLoginVisible.value = true;
  listAllBatch().then(res => {
    if (res.code === 200) {
      batchOptions.value = res.data || [];
    }
  });
}

function submitBatchLogin() {
  if (!selectedBatchNo.value) return;
  batchLoginLoading.value = true;
  const batchNo = selectedBatchNo.value === '__all__' ? 'all' : selectedBatchNo.value;
  loginBatch(batchNo).then(res => {
    proxy.$modal.msgSuccess("批量登录请求已发送，请稍候刷新查看结果");
    batchLoginVisible.value = false;
    setTimeout(() => { getList(); }, 5000);
  }).catch(err => {
    proxy.$modal.msgError("批量登录失败: " + (err.message || err));
  }).finally(() => {
    batchLoginLoading.value = false;
  });
}

function showBatchLogoutDialog() {
  selectedLogoutBatchNo.value = "";
  batchLogoutVisible.value = true;
  listAllBatch().then(res => {
    if (res.code === 200) {
      batchOptions.value = res.data || [];
    }
  });
}

function submitBatchLogout() {
  if (!selectedLogoutBatchNo.value) return;
  batchLogoutLoading.value = true;
  const batchNo = selectedLogoutBatchNo.value === '__all__' ? 'all' : selectedLogoutBatchNo.value;
  logoutBatch(batchNo).then(res => {
    proxy.$modal.msgSuccess("批量登出请求已发送，请稍候刷新查看结果");
    batchLogoutVisible.value = false;
    setTimeout(() => { getList(); }, 3000);
  }).catch(err => {
    proxy.$modal.msgError("批量登出失败: " + (err.message || err));
  }).finally(() => {
    batchLogoutLoading.value = false;
  });
}

// Proxy operations
function loadProxyGroups() {
  listAllProxyGroup().then(res => {
    if (res.code === 200) {
      proxyGroupOptions.value = res.data || [];
    }
  });
}

function handleProxyCommand(command, row) {
  currentProxyAccountId.value = row.id;
  if (command === 'view') {
    viewProxyVisible.value = true;
    getProxyInfo(row.id).then(res => {
      if (res.code === 200) {
        viewProxyData.value = res.data || {};
      }
    });
  } else if (command === 'auto') {
    autoProxyGroupNo.value = "";
    autoProxyVisible.value = true;
    loadProxyGroups();
  } else if (command === 'manual') {
    manualProxyGroupNo.value = "";
    manualProxyIpId.value = null;
    manualIpOptions.value = [];
    manualProxyVisible.value = true;
    loadProxyGroups();
  } else if (command === 'config') {
    configProxyForm.value = { protocol: "socks5", host: "", port: "", username: "", password: "" };
    configProxyVisible.value = true;
  }
}

function submitAutoProxy() {
  if (!autoProxyGroupNo.value || !currentProxyAccountId.value) return;
  autoProxyLoading.value = true;
  autoSelectProxy(currentProxyAccountId.value, autoProxyGroupNo.value).then(res => {
    proxy.$modal.msgSuccess("代理IP分配成功");
    autoProxyVisible.value = false;
    getList();
  }).catch(err => {
    proxy.$modal.msgError(err.msg || "分配失败");
  }).finally(() => {
    autoProxyLoading.value = false;
  });
}

function onManualGroupChange(groupNo) {
  manualProxyIpId.value = null;
  manualIpOptions.value = [];
  if (groupNo) {
    listProxyIp({ groupNo: groupNo, pageNum: 1, pageSize: 1000 }).then(res => {
      manualIpOptions.value = res.rows || [];
    });
  }
}

function submitManualProxy() {
  if (!manualProxyIpId.value || !currentProxyAccountId.value) return;
  manualProxyLoading.value = true;
  manualSelectProxy(currentProxyAccountId.value, manualProxyIpId.value).then(res => {
    proxy.$modal.msgSuccess("代理IP绑定成功");
    manualProxyVisible.value = false;
    getList();
  }).catch(err => {
    proxy.$modal.msgError(err.msg || "绑定失败");
  }).finally(() => {
    manualProxyLoading.value = false;
  });
}

function submitConfigProxy() {
  if (!configProxyForm.value.host || !configProxyForm.value.port || !currentProxyAccountId.value) return;
  configProxyLoading.value = true;
  configProxy(currentProxyAccountId.value, configProxyForm.value).then(res => {
    proxy.$modal.msgSuccess("代理IP配置成功");
    configProxyVisible.value = false;
    getList();
  }).catch(err => {
    proxy.$modal.msgError(err.msg || "配置失败");
  }).finally(() => {
    configProxyLoading.value = false;
  });
}

async function handleWebClient(row) {
  try {
    const res = await getWsToken(row.id);
    if (res.code !== 200 || !res.data) {
      proxy.$modal.msgError("获取token失败");
      return;
    }
    let tokenData = res.data;
    if (typeof tokenData === 'string') {
      try { tokenData = JSON.parse(tokenData); } catch(e) { tokenData = { token: tokenData }; }
    }
    const token = tokenData.token || tokenData;
    const routeData = proxy.$router.resolve({ path: '/tg/webclient', query: { id: row.id, phone: row.phone, username: row.username || '', token: token } });
    window.open(routeData.href, '_blank');
  } catch (e) {
    proxy.$modal.msgError("获取token失败: " + (e.message || e));
  }
}

function handleToggleAccountAutoReply(row) {
  const newVal = !row.autoReply;
  const action = newVal ? '开启' : '关闭';
  proxy.$modal.confirm('确认要' + action + '该账号的自动回复吗？').then(() => {
    updateAccountAutoReply(row.id, newVal).then(() => {
      proxy.$modal.msgSuccess(action + '成功');
      row.autoReply = newVal;
    });
  }).catch(() => {});
}

function handleAllAccountAutoReply(autoReply) {
  const action = autoReply ? '开启' : '关闭';
  proxy.$modal.confirm('确认要' + action + '所有账号的自动回复吗？').then(() => {
    updateAllAccountAutoReply(autoReply).then(() => {
      proxy.$modal.msgSuccess(action + '成功');
      getList();
    });
  }).catch(() => {});
}

function handleExportChat(row) {
  proxy.download('tg/chatMessage/export', {
    tgAccountId: row.id,
  }, `聊天记录_${row.phone}_${new Date().getTime()}.xlsx`);
}

getList();
</script>
