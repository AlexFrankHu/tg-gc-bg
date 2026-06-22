<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryRef" :inline="true" v-show="showSearch">
      <el-form-item label="节点ID" prop="nodeId">
        <el-input
          v-model="queryParams.nodeId"
          placeholder="请输入节点ID"
          clearable
          style="width: 200px"
          @keyup.enter="handleQuery"
        />
      </el-form-item>
      <el-form-item label="公网IP" prop="publicIp">
        <el-input
          v-model="queryParams.publicIp"
          placeholder="请输入公网IP"
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
      <right-toolbar v-model:showSearch="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="nodeList">
      <el-table-column label="节点ID" align="center" prop="nodeId" width="180" />
      <el-table-column label="公网IP" align="center" prop="publicIp" width="160" />
      <el-table-column label="内网IP" align="center" prop="privateIp" width="160" />
      <el-table-column label="历史总账号" align="center" prop="totalAccountCount" width="110" />
      <el-table-column label="在线账号" align="center" prop="onlineAccountCount" width="100">
        <template #default="scope">
          <el-tag :type="scope.row.onlineAccountCount > 0 ? 'success' : 'info'">
            {{ scope.row.onlineAccountCount }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="最大账号数" align="center" prop="maxAccountCount" width="110" />
      <el-table-column label="端口" align="center" prop="nodePort" width="80" />
      <el-table-column label="节点目录" align="center" prop="nodeDir" min-width="200" :show-overflow-tooltip="true" />
      <el-table-column label="运行状态" align="center" width="90">
        <template #default="scope">
          <el-tag :type="isActive(scope.row) ? 'success' : 'danger'">
            {{ isActive(scope.row) ? '在线' : '离线' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="节点状态" align="center" width="90">
        <template #default="scope">
          <el-tag :type="scope.row.nodeStatus === '0' ? 'danger' : 'success'">
            {{ scope.row.nodeStatus === '0' ? '关闭' : '开启' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="最后活跃时间" align="center" prop="lastActiveTime" width="180" />
      <el-table-column label="创建时间" align="center" prop="createTime" width="180" />
      <el-table-column label="操作" align="center" width="120" class-name="small-padding fixed-width">
        <template #default="scope">
          <el-button link type="primary" icon="Edit" @click="handleEdit(scope.row)" v-hasPermi="['tg:node:edit']">修改</el-button>
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

    <!-- 修改节点对话框 -->
    <el-dialog title="修改节点" v-model="open" width="500px" append-to-body>
      <el-form ref="nodeRef" :model="form" :rules="rules" label-width="100px">
        <el-form-item label="节点ID">
          <el-input v-model="form.nodeId" disabled />
        </el-form-item>
        <el-form-item label="公网IP">
          <el-input v-model="form.publicIp" disabled />
        </el-form-item>
        <el-form-item label="最大账号数" prop="maxAccountCount">
          <el-input-number v-model="form.maxAccountCount" :min="0" :max="10000" />
        </el-form-item>
        <el-form-item label="节点状态" prop="nodeStatus">
          <el-radio-group v-model="form.nodeStatus">
            <el-radio value="1">开启</el-radio>
            <el-radio value="0">关闭</el-radio>
          </el-radio-group>
        </el-form-item>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button type="primary" @click="submitForm">确 定</el-button>
          <el-button @click="cancel">取 消</el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup name="Node">
import { listNode, getNode, updateNode } from "@/api/tg/node";

const { proxy } = getCurrentInstance();

const nodeList = ref([]);
const open = ref(false);
const loading = ref(true);
const showSearch = ref(true);
const total = ref(0);

const data = reactive({
  form: {},
  queryParams: {
    pageNum: 1,
    pageSize: 10,
    nodeId: undefined,
    publicIp: undefined,
  },
  rules: {
    maxAccountCount: [
      { required: true, message: "最大账号数不能为空", trigger: "blur" }
    ],
  }
});

const { queryParams, form, rules } = toRefs(data);

/** 判断节点是否在线（2分钟内有活跃） */
function isActive(row) {
  if (!row.lastActiveTime) return false;
  const last = new Date(row.lastActiveTime).getTime();
  const now = new Date().getTime();
  return (now - last) < 2 * 60 * 1000;
}

/** 查询节点列表 */
function getList() {
  loading.value = true;
  listNode(queryParams.value).then(response => {
    nodeList.value = response.rows;
    total.value = response.total;
    loading.value = false;
  });
}

/** 搜索 */
function handleQuery() {
  queryParams.value.pageNum = 1;
  getList();
}

/** 重置 */
function resetQuery() {
  proxy.resetForm("queryRef");
  handleQuery();
}

/** 修改按钮 */
function handleEdit(row) {
  form.value = { ...row };
  open.value = true;
}

/** 提交修改 */
function submitForm() {
  proxy.$refs["nodeRef"].validate(valid => {
    if (valid) {
      updateNode({ nodeId: form.value.nodeId, maxAccountCount: form.value.maxAccountCount, nodeStatus: form.value.nodeStatus }).then(response => {
        proxy.$modal.msgSuccess("修改成功");
        open.value = false;
        getList();
      });
    }
  });
}

/** 取消 */
function cancel() {
  open.value = false;
}

getList();
</script>
