<template>
  <div class="app-container">
    <el-page-header @back="goBack" :title="'返回'" :content="'导入详情 - ' + (route.query.title || route.query.batchNo || '')" />
    <div style="margin-top: 20px;">
      <el-row :gutter="10" class="mb8">
        <el-col :span="1.5">
          <el-button icon="Refresh" @click="getList">刷新</el-button>
        </el-col>
      </el-row>

      <el-table v-loading="loading" :data="recordList">
        <el-table-column label="序号" type="index" width="60" align="center" />
        <el-table-column v-if="isUsername" label="用户名" align="center" prop="username" min-width="180" />
        <el-table-column v-else label="手机号码" align="center" prop="phone" min-width="180" />
        <el-table-column label="导入时间" align="center" prop="createTime" width="180" />
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

<script setup name="ContactImportDetail">
import { ref, computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { listContactImportRecord } from '@/api/tg/contactImport'

const route = useRoute()
const router = useRouter()
const loading = ref(false)
const recordList = ref([])
const total = ref(0)
const queryParams = ref({ pageNum: 1, pageSize: 20 })

const isUsername = computed(() => route.query.importType === 'username')

function getList() {
  const batchNo = route.query.batchNo
  if (!batchNo) return
  loading.value = true
  listContactImportRecord({ batchNo, ...queryParams.value }).then(res => {
    recordList.value = res.rows
    total.value = res.total
  }).finally(() => {
    loading.value = false
  })
}

function goBack() {
  router.push('/tg/contactImport')
}

onMounted(() => {
  getList()
})
</script>
