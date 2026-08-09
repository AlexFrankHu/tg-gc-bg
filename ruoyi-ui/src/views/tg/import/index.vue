<template>
  <div class="app-container">
    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-upload
          ref="uploadRef"
          :auto-upload="false"
          :show-file-list="false"
          accept=".zip"
          :on-change="handleFileChange"
        >
          <el-button type="primary" icon="Upload">导入账号</el-button>
        </el-upload>
      </el-col>
      <el-col :span="1.5">
        <el-button icon="Refresh" @click="getList">刷新</el-button>
      </el-col>
    </el-row>

    <el-table v-loading="loading" :data="batchList">
      <el-table-column label="批次标题" align="center" min-width="180">
        <template #default="scope">
          <span
            style="cursor: pointer; color: #409eff;"
            @click="handleEditTitle(scope.row)"
          >{{ scope.row.title || '(未命名)' }}</span>
        </template>
      </el-table-column>
      <el-table-column label="批次号" align="center" prop="batchNo" min-width="200" :show-overflow-tooltip="true" />
      <el-table-column label="文件名" align="center" prop="fileName" min-width="180" :show-overflow-tooltip="true" />
      <el-table-column label="导入账号数" align="center" prop="totalCount" width="110" />
      <el-table-column label="登录成功" align="center" prop="successCount" width="100">
        <template #default="scope">
          <el-tag type="success" v-if="scope.row.successCount > 0">{{ scope.row.successCount }}</el-tag>
          <span v-else>0</span>
        </template>
      </el-table-column>
      <el-table-column label="登录失败" align="center" prop="failedCount" width="100">
        <template #default="scope">
          <el-tag type="danger" v-if="scope.row.failedCount > 0">{{ scope.row.failedCount }}</el-tag>
          <span v-else>0</span>
        </template>
      </el-table-column>
      <el-table-column label="等待登录" align="center" prop="waitingCount" width="100">
        <template #default="scope">
          <el-tag type="warning" v-if="scope.row.waitingCount > 0">{{ scope.row.waitingCount }}</el-tag>
          <span v-else>0</span>
        </template>
      </el-table-column>
      <el-table-column label="导入时间" align="center" prop="importTime" width="180" />
      <el-table-column label="操作" align="center" width="200">
        <template #default="scope">
          <el-button link type="primary" @click="handleDetail(scope.row)">导入详情</el-button>
          <el-button link type="success" @click="handleBatchProxy(scope.row)">配置代理IP</el-button>
          <el-button link type="warning" @click="handleAssignContacts(scope.row)">添加好友</el-button>
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

    <!-- 上传确认对话框 -->
    <el-dialog v-model="uploadDialogVisible" title="导入账号" width="450px" append-to-body>
      <el-form label-width="80px">
        <el-form-item label="批次标题">
          <el-input v-model="importTitle" placeholder="请输入批次备注标题" maxlength="200" />
        </el-form-item>
        <el-form-item label="指定节点">
          <el-select v-model="importNodeId" placeholder="默认不指定(自动分配)" clearable filterable style="width: 100%">
            <el-option
              v-for="n in nodeOptions"
              :key="n.nodeId"
              :label="n.nodeId + (n.publicIp ? ' (' + n.publicIp + (n.nodePort ? ':' + n.nodePort : '') + ')' : '') + (n.nodeType ? ' [' + n.nodeType + ']' : '')"
              :value="n.nodeId"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="文件名">
          <span>{{ selectedFile ? selectedFile.name : '' }}</span>
        </el-form-item>
        <el-form-item label="文件大小">
          <span>{{ selectedFile ? formatSize(selectedFile.size) : '' }}</span>
        </el-form-item>
      </el-form>
      <p style="color: #909399; font-size: 12px; text-align: center;">
        支持zip格式文件，文件内包含 .json + .session 成对文件
      </p>
      <template #footer>
        <el-button @click="uploadDialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="uploading" @click="submitUpload">确认导入</el-button>
      </template>
    </el-dialog>

    <!-- 批量配置代理IP对话框 -->
    <el-dialog v-model="batchProxyVisible" title="批量配置代理IP" width="450px" append-to-body>
      <el-form label-width="80px">
        <el-form-item label="批次">
          <span>{{ batchProxyRow ? (batchProxyRow.title || batchProxyRow.batchNo) : '' }}</span>
        </el-form-item>
        <el-form-item label="IP组">
          <el-select v-model="batchProxyGroupNo" placeholder="请选择IP组" style="width: 100%">
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
        <el-button @click="batchProxyVisible = false">取消</el-button>
        <el-button type="primary" :loading="batchProxyLoading" @click="submitBatchProxy" :disabled="!batchProxyGroupNo">确定</el-button>
      </template>
    </el-dialog>

    <!-- 添加好友对话框 -->
    <el-dialog v-model="assignContactVisible" title="添加好友" width="500px" append-to-body>
      <el-form label-width="120px">
        <el-form-item label="账号批次">
          <span>{{ assignContactRow ? (assignContactRow.title || assignContactRow.batchNo) : '' }}</span>
        </el-form-item>
        <el-form-item label="好友批次">
          <el-select v-model="assignContactBatchNo" placeholder="请选择好友批次" style="width: 100%">
            <el-option
              v-for="b in contactBatchOptions"
              :key="b.batchNo"
              :label="(b.importType === 'username' ? '[用户名] ' : '[手机号] ') + (b.title || b.batchNo)"
              :value="b.batchNo"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="好友类型">
          <el-radio-group v-model="contactType">
            <el-radio value="real">添加好友</el-radio>
            <el-radio value="fake">添加伪好友</el-radio>
          </el-radio-group>
          <div v-if="contactType === 'fake'" style="color: #909399; font-size: 12px; line-height: 1.5;">
            伪好友: 仅解析手机号获取用户信息后入库, 不加入TG联系人, 之后直接发消息
          </div>
        </el-form-item>
        <el-form-item label="添加方式" v-if="contactType === 'real'">
          <el-radio-group v-model="addMethod">
            <el-radio value="one_by_one">逐个添加</el-radio>
            <el-radio value="batch_import">联系人导入</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="分配方式">
          <el-radio-group v-model="assignMode">
            <el-radio value="average">平均分配</el-radio>
            <el-radio value="fixed">固定分配</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="每个账号分配" v-if="assignMode === 'fixed'">
          <el-input-number v-model="assignFixedCount" :min="1" :max="9999" />
          <span style="margin-left: 8px;">个好友</span>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="assignContactVisible = false">取消</el-button>
        <el-button type="primary" :loading="assignContactLoading" @click="submitAssignContacts" :disabled="!assignContactBatchNo">确定</el-button>
      </template>
    </el-dialog>

    <!-- 编辑标题对话框 -->
    <el-dialog v-model="editTitleVisible" title="修改批次标题" width="400px" append-to-body>
      <el-input v-model="editTitleValue" placeholder="请输入新的批次标题" maxlength="200" />
      <template #footer>
        <el-button @click="editTitleVisible = false">取消</el-button>
        <el-button type="primary" :loading="editTitleLoading" @click="submitEditTitle">确认</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup name="Import">
import { listBatch, importAccounts, updateBatchTitle, assignContacts } from "@/api/tg/import";
import { listAllProxyGroup } from "@/api/tg/proxy";
import { batchAssignProxy } from "@/api/tg/account";
import { listAllContactImportBatch } from "@/api/tg/contactImport";
import { listNode } from "@/api/tg/node";

const { proxy } = getCurrentInstance();
const router = useRouter();

const batchList = ref([]);
const loading = ref(true);
const total = ref(0);
const uploading = ref(false);
const uploadDialogVisible = ref(false);
const selectedFile = ref(null);
const importTitle = ref("");
const importNodeId = ref("");
const nodeOptions = ref([]);

const batchProxyVisible = ref(false);
const batchProxyGroupNo = ref("");
const batchProxyLoading = ref(false);
const batchProxyRow = ref(null);
const proxyGroupOptions = ref([]);

const editTitleVisible = ref(false);
const editTitleValue = ref("");
const editTitleLoading = ref(false);
const editTitleRow = ref(null);

const assignContactVisible = ref(false);
const assignContactBatchNo = ref("");
const assignContactLoading = ref(false);
const assignContactRow = ref(null);
const contactBatchOptions = ref([]);
const assignMode = ref("fixed");
const assignFixedCount = ref(1);
const addMethod = ref("one_by_one");
const contactType = ref("real");

const queryParams = ref({
  pageNum: 1,
  pageSize: 10,
});

function getList() {
  loading.value = true;
  listBatch(queryParams.value).then(response => {
    batchList.value = response.rows;
    total.value = response.total;
    loading.value = false;
  });
}

function handleFileChange(file) {
  if (!file || !file.raw) return;
  if (!file.raw.name.toLowerCase().endsWith('.zip')) {
    proxy.$modal.msgError("仅支持zip格式文件");
    return;
  }
  selectedFile.value = file.raw;
  importTitle.value = "";
  importNodeId.value = "";
  loadNodeOptions();
  uploadDialogVisible.value = true;
}

function loadNodeOptions() {
  listNode({ pageNum: 1, pageSize: 1000 }).then(res => {
    nodeOptions.value = res.rows || [];
  });
}

function submitUpload() {
  if (!selectedFile.value) return;
  uploading.value = true;
  const formData = new FormData();
  formData.append('file', selectedFile.value);
  if (importTitle.value) {
    formData.append('title', importTitle.value);
  }
  if (importNodeId.value) {
    formData.append('nodeId', importNodeId.value);
  }
  importAccounts(formData).then(res => {
    if (res.code === 200) {
      let msg = "导入成功，共 " + (res.data.totalCount || 0) + " 个账号";
      if (res.data.skippedCount > 0) {
        msg += "（跳过 " + res.data.skippedCount + " 个已有账号）";
      }
      proxy.$modal.msgSuccess(msg);
      uploadDialogVisible.value = false;
      selectedFile.value = null;
      importTitle.value = "";
      importNodeId.value = "";
      getList();
    } else {
      proxy.$modal.msgError(res.msg || "导入失败");
    }
  }).catch(err => {
    proxy.$modal.msgError("导入失败: " + (err.message || err));
  }).finally(() => {
    uploading.value = false;
  });
}

function handleEditTitle(row) {
  editTitleRow.value = row;
  editTitleValue.value = row.title || "";
  editTitleVisible.value = true;
}

function submitEditTitle() {
  if (!editTitleRow.value) return;
  editTitleLoading.value = true;
  updateBatchTitle({ id: editTitleRow.value.id, title: editTitleValue.value }).then(res => {
    if (res.code === 200) {
      proxy.$modal.msgSuccess("修改成功");
      editTitleVisible.value = false;
      getList();
    } else {
      proxy.$modal.msgError(res.msg || "修改失败");
    }
  }).catch(err => {
    proxy.$modal.msgError("修改失败: " + (err.message || err));
  }).finally(() => {
    editTitleLoading.value = false;
  });
}

function handleDetail(row) {
  router.push({ path: '/tg/import/detail', query: { batchNo: row.batchNo } });
}

function handleBatchProxy(row) {
  batchProxyRow.value = row;
  batchProxyGroupNo.value = "";
  batchProxyVisible.value = true;
  listAllProxyGroup().then(res => {
    if (res.code === 200) {
      proxyGroupOptions.value = res.data || [];
    }
  });
}

function submitBatchProxy() {
  if (!batchProxyRow.value || !batchProxyGroupNo.value) return;
  batchProxyLoading.value = true;
  batchAssignProxy(batchProxyRow.value.batchNo, batchProxyGroupNo.value).then(res => {
    proxy.$modal.msgSuccess(res.msg || "批量分配完成");
    batchProxyVisible.value = false;
  }).catch(err => {
    proxy.$modal.msgError(err.msg || "批量分配失败");
  }).finally(() => {
    batchProxyLoading.value = false;
  });
}

function handleAssignContacts(row) {
  assignContactRow.value = row;
  assignContactBatchNo.value = "";
  assignMode.value = "fixed";
  assignFixedCount.value = 1;
  addMethod.value = "one_by_one";
  contactType.value = "real";
  assignContactVisible.value = true;
  listAllContactImportBatch().then(res => {
    if (res.code === 200) {
      contactBatchOptions.value = res.data || [];
    }
  });
}

function submitAssignContacts() {
  if (!assignContactRow.value || !assignContactBatchNo.value) return;
  assignContactLoading.value = true;
  const data = {
    accountBatchNo: assignContactRow.value.batchNo,
    contactBatchNo: assignContactBatchNo.value,
    mode: assignMode.value,
    fixedCount: assignMode.value === 'fixed' ? assignFixedCount.value : null,
    addMethod: contactType.value === 'fake' ? 'one_by_one' : addMethod.value,
    contactType: contactType.value,
  };
  assignContacts(data).then(res => {
    if (res.code === 200) {
      const d = res.data;
      let msg = contactType.value !== 'fake' && addMethod.value === 'batch_import'
        ? `好友已分配 ${d.totalAssigned} 个，批量导入完成（成功${d.batchImported || 0}个，失败${d.batchFailed || 0}个）`
        : `好友已分配 ${d.totalAssigned} 个，待定时器自动添加`;
      proxy.$modal.msgSuccess(msg);
      assignContactVisible.value = false;
    } else {
      proxy.$modal.msgError(res.msg || "分配失败");
    }
  }).catch(err => {
    proxy.$modal.msgError("分配失败: " + (err.message || err));
  }).finally(() => {
    assignContactLoading.value = false;
  });
}

function formatSize(bytes) {
  if (!bytes) return '0 B';
  const k = 1024;
  const sizes = ['B', 'KB', 'MB', 'GB'];
  const i = Math.floor(Math.log(bytes) / Math.log(k));
  return parseFloat((bytes / Math.pow(k, i)).toFixed(2)) + ' ' + sizes[i];
}

getList();
</script>
