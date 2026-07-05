<template>
  <div class="app-container">
    <el-table v-loading="loading" :data="configList" border>
      <el-table-column label="配置名称" align="center" prop="configName" min-width="200" />
      <el-table-column label="配置项" align="center" prop="configKey" min-width="200" />
      <el-table-column label="当前状态" align="center" min-width="160">
        <template #default="scope">
          <el-switch
            v-model="scope.row.enabled"
            active-text="开启"
            inactive-text="关闭"
            :disabled="!hasEditPerm"
            @change="(val) => handleToggle(scope.row, val)"
          />
        </template>
      </el-table-column>
      <el-table-column label="更新时间" align="center" prop="updateTime" min-width="180" />
    </el-table>
  </div>
</template>

<script setup name="TgSystemConfig">
import { listSystemConfig, updateSystemConfig } from "@/api/tg/systemConfig";

const { proxy } = getCurrentInstance();

const loading = ref(true);
const configList = ref([]);
const hasEditPerm = proxy.$auth ? proxy.$auth.hasPermi('tg:systemConfig:edit') : true;

function getList() {
  loading.value = true;
  listSystemConfig().then(response => {
    configList.value = (response.rows || []).map(item => ({
      ...item,
      enabled: String(item.configValue) === '1'
    }));
    loading.value = false;
  });
}

function handleToggle(row, val) {
  const text = val ? '开启' : '关闭';
  proxy.$modal.confirm('确认将【' + row.configName + '】设置为 "' + text + '" 吗？').then(() => {
    return updateSystemConfig({ id: row.id, configValue: val ? '1' : '0' });
  }).then(() => {
    proxy.$modal.msgSuccess('修改成功');
    getList();
  }).catch(() => {
    // revert switch on cancel/failure
    row.enabled = !val;
  });
}

getList();
</script>
