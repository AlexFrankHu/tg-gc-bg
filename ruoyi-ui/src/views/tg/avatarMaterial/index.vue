<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryRef" :inline="true" v-show="showSearch">
      <el-form-item label="文件名" prop="fileName">
        <el-input v-model="queryParams.fileName" placeholder="请输入文件名" clearable style="width: 200px" @keyup.enter="handleQuery" />
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="Search" @click="handleQuery">搜索</el-button>
        <el-button icon="Refresh" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button type="primary" icon="Upload" @click="showImportDialog" v-hasPermi="['tg:avatarMaterial:add']">导入头像(zip/图片)</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="danger" plain icon="Delete" :disabled="multiple" @click="handleDelete" v-hasPermi="['tg:avatarMaterial:remove']">删除</el-button>
      </el-col>
      <right-toolbar v-model:showSearch="showSearch" @queryTable="getList" />
    </el-row>

    <el-table v-loading="loading" :data="list" @selection-change="handleSelectionChange" :border="true">
      <el-table-column type="selection" width="50" align="center" />
      <el-table-column label="ID" align="center" prop="id" width="90" />
      <el-table-column label="预览" align="center" width="100">
        <template #default="scope">
          <el-image :src="baseUrl + scope.row.filePath" style="width: 60px; height: 60px" fit="cover" :preview-src-list="[baseUrl + scope.row.filePath]" preview-teleported />
        </template>
      </el-table-column>
      <el-table-column label="文件名" align="center" prop="fileName" min-width="200" show-overflow-tooltip />
      <el-table-column label="路径" align="center" prop="filePath" min-width="220" show-overflow-tooltip />
      <el-table-column label="导入时间" align="center" prop="createTime" width="180" />
    </el-table>

    <pagination v-show="total > 0" :total="total" v-model:page="queryParams.pageNum" v-model:limit="queryParams.pageSize" @pagination="getList" />

    <el-dialog v-model="importVisible" title="导入头像素材" width="500px" append-to-body>
      <el-upload ref="uploadRef" :auto-upload="false" :limit="1" accept=".zip,.jpg,.jpeg,.png,.gif,.bmp" :on-change="handleFileChange" :on-remove="() => (importFile = null)">
        <el-button type="primary">选择 zip 或图片文件</el-button>
        <template #tip>
          <div class="el-upload__tip">zip 压缩包（内含 jpg/jpeg/png/gif/bmp 图片，支持子目录）或单张图片；单张图片不超过 10MB</div>
        </template>
      </el-upload>
      <template #footer>
        <el-button @click="importVisible = false">取消</el-button>
        <el-button type="primary" :loading="importLoading" :disabled="!importFile" @click="submitImport">确认导入</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup name="AvatarMaterial">
import { ref, getCurrentInstance, onMounted } from 'vue'
import { listAvatarMaterial, importAvatarMaterial, delAvatarMaterial } from '@/api/tg/avatarMaterial'

const { proxy } = getCurrentInstance()
const baseUrl = import.meta.env.VITE_APP_BASE_API

const loading = ref(false)
const showSearch = ref(true)
const list = ref([])
const total = ref(0)
const ids = ref([])
const multiple = ref(true)
const importVisible = ref(false)
const importLoading = ref(false)
const importFile = ref(null)

const queryParams = ref({ pageNum: 1, pageSize: 20, fileName: undefined })

function getList() {
  loading.value = true
  listAvatarMaterial(queryParams.value).then(res => {
    list.value = res.rows
    total.value = res.total
  }).finally(() => { loading.value = false })
}

function handleQuery() { queryParams.value.pageNum = 1; getList() }
function resetQuery() { proxy.resetForm('queryRef'); handleQuery() }
function handleSelectionChange(selection) {
  ids.value = selection.map(i => i.id)
  multiple.value = !selection.length
}

function showImportDialog() {
  importFile.value = null
  importVisible.value = true
}
function handleFileChange(file) { importFile.value = file.raw }
function submitImport() {
  if (!importFile.value) return
  importLoading.value = true
  const formData = new FormData()
  formData.append('file', importFile.value)
  importAvatarMaterial(formData).then(res => {
    proxy.$modal.msgSuccess(res.msg || '导入成功')
    importVisible.value = false
    getList()
  }).catch(err => {
    proxy.$modal.msgError('导入失败: ' + (err.message || err))
  }).finally(() => { importLoading.value = false })
}

function handleDelete() {
  proxy.$modal.confirm('是否确认删除选中的 ' + ids.value.length + ' 张头像？').then(() => delAvatarMaterial(ids.value.join(','))).then(() => {
    proxy.$modal.msgSuccess('删除成功')
    getList()
  }).catch(() => {})
}

onMounted(getList)
</script>
