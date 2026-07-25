<template>
  <div class="app-container">
    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-upload
          ref="uploadPhoneRef"
          :auto-upload="false"
          :show-file-list="false"
          accept=".xlsx"
          :on-change="(file) => handleFileChange(file, 'phone')"
        >
          <el-button type="primary" icon="Upload">导入好友(手机号)</el-button>
        </el-upload>
      </el-col>
      <el-col :span="1.5">
        <el-upload
          ref="uploadUsernameRef"
          :auto-upload="false"
          :show-file-list="false"
          accept=".xlsx"
          :on-change="(file) => handleFileChange(file, 'username')"
        >
          <el-button type="success" icon="Upload">导入好友(用户名)</el-button>
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
      <el-table-column label="导入类型" align="center" width="120">
        <template #default="scope">
          <el-tag type="primary" v-if="scope.row.importType === 'phone'">手机号</el-tag>
          <el-tag type="success" v-else-if="scope.row.importType === 'username'">用户名</el-tag>
          <el-tag v-else>手机号</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="批次号" align="center" prop="batchNo" min-width="160" :show-overflow-tooltip="true" />
      <el-table-column label="文件名" align="center" prop="fileName" min-width="180" :show-overflow-tooltip="true" />
      <el-table-column label="导入数量" align="center" prop="totalCount" width="90" />
      <el-table-column label="使用数量" align="center" prop="usedCount" width="90">
        <template #default="scope">
          <span style="color: #67c23a; font-weight: bold;">{{ scope.row.usedCount || 0 }}</span>
        </template>
      </el-table-column>
      <el-table-column label="等待数量" align="center" prop="waitingCount" width="90">
        <template #default="scope">
          <span style="color: #e6a23c; font-weight: bold;">{{ scope.row.waitingCount || 0 }}</span>
        </template>
      </el-table-column>
      <el-table-column label="无效数量" align="center" prop="invalidCount" width="90">
        <template #default="scope">
          <span style="color: #f56c6c; font-weight: bold;">{{ scope.row.invalidCount || 0 }}</span>
        </template>
      </el-table-column>
      <el-table-column label="剩余数量" align="center" width="90">
        <template #default="scope">
          <span style="color: #409eff; font-weight: bold;">{{ scope.row.remainCount != null ? scope.row.remainCount : ((scope.row.totalCount || 0) - (scope.row.usedCount || 0) - (scope.row.waitingCount || 0) - (scope.row.invalidCount || 0)) }}</span>
        </template>
      </el-table-column>
      <el-table-column label="导入时间" align="center" prop="importTime" width="180" />
      <el-table-column label="操作" align="center" width="240">
        <template #default="scope">
          <el-button link type="primary" @click="handleDetail(scope.row)">导入详情</el-button>
          <el-button link type="warning" @click="handleFiltered(scope.row)">过滤记录</el-button>
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

    <!-- 上传确认对话框 -->
    <el-dialog v-model="uploadDialogVisible" :title="uploadDialogTitle" width="450px" append-to-body>
      <el-form label-width="80px">
        <el-form-item label="批次标题">
          <el-input v-model="importTitle" placeholder="请输入批次备注标题" maxlength="200" />
        </el-form-item>
        <el-form-item label="文件名">
          <span>{{ selectedFile ? selectedFile.name : '' }}</span>
        </el-form-item>
        <el-form-item label="文件大小">
          <span>{{ selectedFile ? formatSize(selectedFile.size) : '' }}</span>
        </el-form-item>
      </el-form>
      <p style="color: #909399; font-size: 12px; text-align: center;">
        {{ currentImportType === 'phone' ? '支持xlsx格式文件，文件第一列为手机号码' : '支持xlsx格式文件，文件第一列为好友用户名' }}
      </p>
      <template #footer>
        <el-button @click="uploadDialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="uploading" @click="submitUpload">确认导入</el-button>
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

<script setup name="ContactImport">
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { listContactImportBatch, importContacts, importContactsByUsername, updateContactImportBatchTitle, deleteContactImportBatch } from '@/api/tg/contactImport'

const router = useRouter()
const loading = ref(false)
const batchList = ref([])
const total = ref(0)
const queryParams = ref({ pageNum: 1, pageSize: 10 })

// Upload state
const uploadDialogVisible = ref(false)
const selectedFile = ref(null)
const importTitle = ref('')
const uploading = ref(false)
const currentImportType = ref('phone')

const uploadDialogTitle = computed(() => {
  return currentImportType.value === 'phone' ? '导入好友(手机号)' : '导入好友(用户名)'
})

// Edit title state
const editTitleVisible = ref(false)
const editTitleValue = ref('')
const editTitleRow = ref(null)
const editTitleLoading = ref(false)

function getList() {
  loading.value = true
  listContactImportBatch(queryParams.value).then(res => {
    batchList.value = res.rows
    total.value = res.total
  }).finally(() => {
    loading.value = false
  })
}

function handleFileChange(file, type) {
  selectedFile.value = file.raw
  importTitle.value = ''
  currentImportType.value = type
  uploadDialogVisible.value = true
}

function submitUpload() {
  if (!selectedFile.value) return
  uploading.value = true
  const formData = new FormData()
  formData.append('file', selectedFile.value)
  if (importTitle.value) {
    formData.append('title', importTitle.value)
  }
  const apiFn = currentImportType.value === 'phone' ? importContacts : importContactsByUsername
  const typeLabel = currentImportType.value === 'phone' ? '号码' : '用户名'
  apiFn(formData).then(res => {
    const d = res.data || {}
    let msg = `导入成功，共 ${d.totalCount} 个${typeLabel}`
    if (currentImportType.value === 'phone' && ((d.discardCount || 0) > 0 || (d.duplicateCount || 0) > 0)) {
      msg += `；已过滤 废弃(在账号列表) ${d.discardCount || 0} 个、重复 ${d.duplicateCount || 0} 个`
    }
    ElMessage.success(msg)
    uploadDialogVisible.value = false
    selectedFile.value = null
    getList()
  }).catch(() => {
    ElMessage.error('导入失败')
  }).finally(() => {
    uploading.value = false
  })
}

function handleDetail(row) {
  router.push({ path: '/tg/contactImport/detail', query: { batchNo: row.batchNo, title: row.title || row.batchNo, importType: row.importType || 'phone' } })
}

function handleFiltered(row) {
  router.push({ path: '/tg/contactImport/filtered', query: { batchNo: row.batchNo, title: row.title || row.batchNo } })
}

function handleEditTitle(row) {
  editTitleRow.value = row
  editTitleValue.value = row.title || ''
  editTitleVisible.value = true
}

function submitEditTitle() {
  if (!editTitleRow.value) return
  editTitleLoading.value = true
  updateContactImportBatchTitle({ id: editTitleRow.value.id, title: editTitleValue.value }).then(() => {
    ElMessage.success('修改成功')
    editTitleVisible.value = false
    getList()
  }).finally(() => {
    editTitleLoading.value = false
  })
}

function handleDelete(row) {
  ElMessageBox.confirm(`确认删除批次「${row.title || row.batchNo}」?`, '提示', { type: 'warning' }).then(() => {
    deleteContactImportBatch(row.id).then(() => {
      ElMessage.success('删除成功')
      getList()
    })
  }).catch(() => {})
}

function formatSize(bytes) {
  if (bytes < 1024) return bytes + ' B'
  if (bytes < 1048576) return (bytes / 1024).toFixed(1) + ' KB'
  return (bytes / 1048576).toFixed(1) + ' MB'
}

onMounted(() => {
  getList()
})
</script>
