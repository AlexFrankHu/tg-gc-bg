<template>
  <div class="app-container">
    <el-page-header @back="goBack" :title="'返回'" :content="'过滤记录 - ' + (route.query.title || route.query.batchNo || '')" />
    <div style="margin-top: 20px;">
      <el-row :gutter="10" class="mb8">
        <el-col :span="1.5">
          <el-select v-model="queryParams.filterType" placeholder="过滤类型" clearable style="width: 140px" @change="handleQuery">
            <el-option label="全部" value="" />
            <el-option label="废弃" value="废弃" />
            <el-option label="重复" value="重复" />
          </el-select>
        </el-col>
        <el-col :span="1.5">
          <el-button icon="Refresh" @click="handleQuery">刷新</el-button>
        </el-col>
      </el-row>

      <el-table v-loading="loading" :data="filteredList" border>
        <el-table-column label="序号" type="index" width="70" align="center" />
        <el-table-column label="手机号码" align="center" prop="phone" min-width="180" />
        <el-table-column label="过滤类型" align="center" prop="filterType" width="120">
          <template #default="scope">
            <el-tag v-if="scope.row.filterType === '废弃'" type="danger">废弃</el-tag>
            <el-tag v-else type="warning">重复</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="记录时间" align="center" prop="createTime" width="180" />
      </el-table>

      <pagination
        v-show="total > 0"
        :total="total"
        v-model:page="queryParams.pageNum"
        v-model:limit="queryParams.pageSize"
        @pagination="getList"
      />
    </div>
  </div>
</template>

<script setup name="ContactImportFiltered">
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { listContactImportFiltered } from '@/api/tg/contactImport'

const route = useRoute()
const router = useRouter()
const loading = ref(false)
const filteredList = ref([])
const total = ref(0)
const queryParams = ref({ pageNum: 1, pageSize: 20, filterType: '' })

function getList() {
  const batchNo = route.query.batchNo
  if (!batchNo) return
  loading.value = true
  listContactImportFiltered({ batchNo, ...queryParams.value }).then(res => {
    filteredList.value = res.rows
    total.value = res.total
  }).finally(() => {
    loading.value = false
  })
}

function handleQuery() {
  queryParams.value.pageNum = 1
  getList()
}

function goBack() {
  router.push('/tg/contactImport')
}

onMounted(() => {
  getList()
})
</script>
