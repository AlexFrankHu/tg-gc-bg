<template>
  <div class="app-container">
    <!-- 搜索区域 -->
    <el-form :model="queryParams" ref="queryRef" :inline="true" v-show="showSearch">
      <el-form-item label="标题" prop="title">
        <el-input v-model="queryParams.title" placeholder="请输入标题" clearable style="width: 200px" @keyup.enter="handleQuery" />
      </el-form-item>
      <el-form-item label="状态" prop="isEnabled">
        <el-select v-model="queryParams.isEnabled" placeholder="全部" clearable style="width: 120px">
          <el-option label="启用" :value="1" />
          <el-option label="停用" :value="0" />
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="Search" @click="handleQuery">搜索</el-button>
        <el-button icon="Refresh" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <!-- 操作按钮 -->
    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button type="primary" icon="Plus" @click="handleAdd">新增</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="danger" icon="Delete" :disabled="multiple" @click="handleDelete">批量删除</el-button>
      </el-col>
      <right-toolbar v-model:showSearch="showSearch" @queryTable="getList" />
    </el-row>

    <!-- 表格 -->
    <el-table v-loading="loading" :data="greetingList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="ID" align="center" prop="id" width="60" />
      <el-table-column label="标题" align="center" prop="title" min-width="150" :show-overflow-tooltip="true" />
      <el-table-column label="问候语内容" align="center" prop="content" min-width="300" :show-overflow-tooltip="true" />
      <el-table-column label="图片" align="center" width="100">
        <template #default="scope">
          <el-image v-if="scope.row.imagePath" :src="baseUrl + scope.row.imagePath" style="width: 50px; height: 50px" fit="cover" :preview-src-list="[baseUrl + scope.row.imagePath]" />
          <span v-else>-</span>
        </template>
      </el-table-column>
      <el-table-column label="状态" align="center" width="80">
        <template #default="scope">
          <el-tag :type="scope.row.isEnabled === 1 ? 'success' : 'danger'" size="small">
            {{ scope.row.isEnabled === 1 ? '启用' : '停用' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="创建时间" align="center" prop="createTime" width="170" />
      <el-table-column label="操作" align="center" width="180">
        <template #default="scope">
          <el-button link type="primary" icon="Edit" @click="handleUpdate(scope.row)">修改</el-button>
          <el-button link type="danger" icon="Delete" @click="handleDelete(scope.row)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <!-- 分页 -->
    <pagination
      v-show="total > 0"
      :total="total"
      v-model:page="queryParams.pageNum"
      v-model:limit="queryParams.pageSize"
      @pagination="getList"
    />

    <!-- 新增/修改对话框 -->
    <el-dialog :title="dialogTitle" v-model="dialogVisible" width="600px" append-to-body>
      <el-form ref="formRef" :model="form" :rules="rules" label-width="100px">
        <el-form-item label="标题" prop="title">
          <el-input v-model="form.title" placeholder="请输入标题/分类" maxlength="200" />
        </el-form-item>
        <el-form-item label="问候语内容" prop="content">
          <el-input v-model="form.content" type="textarea" :rows="6" placeholder="请输入问候语内容" />
        </el-form-item>
        <el-form-item label="图片">
          <el-upload
            class="greeting-uploader"
            :action="uploadUrl"
            :headers="uploadHeaders"
            :show-file-list="false"
            :on-success="handleUploadSuccess"
            accept="image/*"
          >
            <el-image v-if="form.imagePath" :src="baseUrl + form.imagePath" style="width: 120px; height: 120px" fit="cover" />
            <el-icon v-else class="greeting-uploader-icon"><Plus /></el-icon>
          </el-upload>
          <el-button v-if="form.imagePath" link type="danger" @click="form.imagePath = ''" style="margin-left: 10px">删除图片</el-button>
        </el-form-item>
        <el-form-item label="是否启用" prop="isEnabled">
          <el-radio-group v-model="form.isEnabled">
            <el-radio :value="1">启用</el-radio>
            <el-radio :value="0">停用</el-radio>
          </el-radio-group>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="submitLoading" @click="submitForm">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup name="Greeting">
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus } from '@element-plus/icons-vue'
import { getToken } from '@/utils/auth'
import { listGreeting, getGreeting, addGreeting, updateGreeting, delGreeting } from '@/api/tg/greeting'

const baseUrl = import.meta.env.VITE_APP_BASE_API
const uploadUrl = baseUrl + '/common/upload'
const uploadHeaders = { Authorization: 'Bearer ' + getToken() }

const loading = ref(false)
const showSearch = ref(true)
const greetingList = ref([])
const total = ref(0)
const ids = ref([])
const multiple = ref(true)
const dialogVisible = ref(false)
const dialogTitle = ref('')
const submitLoading = ref(false)
const formRef = ref(null)

const queryParams = ref({
  pageNum: 1,
  pageSize: 20,
  title: undefined,
  isEnabled: undefined,
})

const form = ref({})
const rules = {
  content: [{ required: true, message: '请输入问候语内容', trigger: 'blur' }],
}

function getList() {
  loading.value = true
  listGreeting(queryParams.value).then(res => {
    greetingList.value = res.rows
    total.value = res.total
  }).finally(() => {
    loading.value = false
  })
}

function handleQuery() {
  queryParams.value.pageNum = 1
  getList()
}

function resetQuery() {
  queryParams.value = { pageNum: 1, pageSize: 20, title: undefined, isEnabled: undefined }
  getList()
}

function handleSelectionChange(selection) {
  ids.value = selection.map(item => item.id)
  multiple.value = !selection.length
}

function resetForm() {
  form.value = {
    id: undefined,
    title: undefined,
    content: undefined,
    state: undefined,
    isEnabled: 1,
    sortOrder: 0,
    imagePath: undefined,
    remark: undefined,
  }
}

function handleUploadSuccess(res) {
  if (res.code === 200) {
    form.value.imagePath = res.fileName
    ElMessage.success('图片上传成功')
  } else {
    ElMessage.error(res.msg || '上传失败')
  }
}

function handleAdd() {
  resetForm()
  dialogTitle.value = '新增问候语'
  dialogVisible.value = true
}

function handleUpdate(row) {
  resetForm()
  getGreeting(row.id).then(res => {
    form.value = res.data
    dialogTitle.value = '修改问候语'
    dialogVisible.value = true
  })
}

function submitForm() {
  formRef.value.validate(valid => {
    if (!valid) return
    submitLoading.value = true
    const action = form.value.id ? updateGreeting(form.value) : addGreeting(form.value)
    action.then(() => {
      ElMessage.success(form.value.id ? '修改成功' : '新增成功')
      dialogVisible.value = false
      getList()
    }).finally(() => {
      submitLoading.value = false
    })
  })
}

function handleDelete(row) {
  const deleteIds = row.id ? [row.id] : ids.value
  ElMessageBox.confirm('确认删除选中的问候语数据？', '提示', { type: 'warning' }).then(() => {
    delGreeting(deleteIds.join(',')).then(() => {
      ElMessage.success('删除成功')
      getList()
    })
  }).catch(() => {})
}

onMounted(() => {
  getList()
})
</script>

<style scoped>
.greeting-uploader {
  border: 1px dashed #d9d9d9;
  border-radius: 6px;
  cursor: pointer;
  width: 120px;
  height: 120px;
  display: flex;
  align-items: center;
  justify-content: center;
}
.greeting-uploader-icon {
  font-size: 28px;
  color: #8c939d;
}
</style>
