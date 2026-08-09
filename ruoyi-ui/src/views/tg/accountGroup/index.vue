<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryRef" :inline="true">
      <el-form-item label="组名称" prop="groupName">
        <el-input v-model="queryParams.groupName" placeholder="请输入组名称" clearable style="width: 200px" @keyup.enter="handleQuery" />
      </el-form-item>
      <el-form-item label="是否可用" prop="enabled">
        <el-select v-model="queryParams.enabled" placeholder="全部" clearable style="width: 140px">
          <el-option label="可用" :value="1" />
          <el-option label="不可用" :value="0" />
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="Search" @click="handleQuery">搜索</el-button>
        <el-button icon="Refresh" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button type="primary" plain icon="Plus" v-hasPermi="['tg:accountGroup:add']" @click="handleAdd">新增分组</el-button>
      </el-col>
    </el-row>

    <el-table v-loading="loading" :data="groupList">
      <el-table-column label="组ID" align="center" prop="id" width="90" />
      <el-table-column label="组名称" align="center" prop="groupName" :show-overflow-tooltip="true" />
      <el-table-column label="账号数" align="center" prop="accountCount" width="100" />
      <el-table-column label="是否可用" align="center" prop="enabled" width="110">
        <template #default="scope">
          <el-tag v-if="scope.row.enabled === 1" type="success">可用</el-tag>
          <el-tag v-else type="info">不可用</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="创建时间" align="center" prop="createTime" width="180" />
      <el-table-column label="操作" align="center" width="200" class-name="small-padding fixed-width">
        <template #default="scope">
          <el-button link type="primary" icon="Edit" v-hasPermi="['tg:accountGroup:edit']" @click="handleUpdate(scope.row)">修改</el-button>
          <el-button link type="success" icon="UserFilled" v-hasPermi="['tg:accountGroup:assign']" @click="handleAssignContacts(scope.row)">添加好友</el-button>
        </template>
      </el-table-column>
    </el-table>

    <pagination v-show="total > 0" :total="total" v-model:page="queryParams.pageNum" v-model:limit="queryParams.pageSize" @pagination="getList" />

    <!-- 新增/修改对话框 -->
    <el-dialog :title="dialogTitle" v-model="dialogVisible" width="420px" append-to-body>
      <el-form :model="form" :rules="rules" ref="groupFormRef" label-width="90px">
        <el-form-item label="组名称" prop="groupName">
          <el-input v-model="form.groupName" placeholder="请输入组名称" maxlength="100" />
        </el-form-item>
        <el-form-item label="是否可用" prop="enabled">
          <el-radio-group v-model="form.enabled">
            <el-radio :value="1">可用</el-radio>
            <el-radio :value="0">不可用</el-radio>
          </el-radio-group>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="submitLoading" @click="submitForm">确定</el-button>
      </template>
    </el-dialog>

    <!-- 添加好友对话框 -->
    <el-dialog v-model="assignContactVisible" title="添加好友" width="500px" append-to-body>
      <el-form label-width="120px">
        <el-form-item label="账号分组">
          <span>{{ assignContactRow ? assignContactRow.groupName : '' }}</span>
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
  </div>
</template>

<script setup name="AccountGroup">
import { listAccountGroup, addAccountGroup, updateAccountGroup, assignContactsByGroup } from "@/api/tg/accountGroup";
import { listAllContactImportBatch } from "@/api/tg/contactImport";

const { proxy } = getCurrentInstance();

const groupList = ref([]);
const loading = ref(true);
const total = ref(0);

const queryParams = ref({
  pageNum: 1,
  pageSize: 10,
  groupName: undefined,
  enabled: undefined,
});

const dialogVisible = ref(false);
const dialogTitle = ref("");
const submitLoading = ref(false);
const form = ref({ id: undefined, groupName: "", enabled: 1 });
const rules = {
  groupName: [{ required: true, message: "组名称不能为空", trigger: "blur" }],
};

const assignContactVisible = ref(false);
const assignContactBatchNo = ref("");
const assignContactLoading = ref(false);
const assignContactRow = ref(null);
const contactBatchOptions = ref([]);
const assignMode = ref("fixed");
const assignFixedCount = ref(1);
const addMethod = ref("one_by_one");
const contactType = ref("real");

function getList() {
  loading.value = true;
  listAccountGroup(queryParams.value).then(response => {
    groupList.value = response.rows;
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
  queryParams.value.enabled = undefined;
  handleQuery();
}

function handleAdd() {
  form.value = { id: undefined, groupName: "", enabled: 1 };
  dialogTitle.value = "新增账号分组";
  dialogVisible.value = true;
}

function handleUpdate(row) {
  form.value = { id: row.id, groupName: row.groupName, enabled: row.enabled };
  dialogTitle.value = "修改账号分组";
  dialogVisible.value = true;
}

function submitForm() {
  proxy.$refs["groupFormRef"].validate(valid => {
    if (!valid) return;
    submitLoading.value = true;
    const req = form.value.id != null ? updateAccountGroup(form.value) : addAccountGroup(form.value);
    req.then(res => {
      if (res.code === 200) {
        proxy.$modal.msgSuccess(form.value.id != null ? "修改成功" : "新增成功");
        dialogVisible.value = false;
        getList();
      } else {
        proxy.$modal.msgError(res.msg || "操作失败");
      }
    }).finally(() => {
      submitLoading.value = false;
    });
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
    groupId: assignContactRow.value.id,
    contactBatchNo: assignContactBatchNo.value,
    mode: assignMode.value,
    fixedCount: assignMode.value === 'fixed' ? assignFixedCount.value : null,
    addMethod: contactType.value === 'fake' ? 'one_by_one' : addMethod.value,
    contactType: contactType.value,
  };
  assignContactsByGroup(data).then(res => {
    if (res.code === 200) {
      const d = res.data;
      proxy.$modal.msgSuccess(`好友已分配 ${d.totalAssigned} 个，待定时器自动添加`);
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

getList();
</script>
