<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryRef" :inline="true" v-show="showSearch">
      <el-form-item label="标题" prop="title">
        <el-input v-model="queryParams.title" placeholder="请输入标题" clearable @keyup.enter="handleQuery" />
      </el-form-item>
      <el-form-item label="国家" prop="country">
        <el-select v-model="queryParams.country" placeholder="请选择国家" clearable>
          <el-option v-for="c in countryOptions" :key="c" :label="c" :value="c" />
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="Search" @click="handleQuery">搜索</el-button>
        <el-button icon="Refresh" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button type="primary" icon="Upload" @click="handleImport">导入代理IP</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="success" icon="Upload" @click="handleImportIpfly">导入ipfly代理</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="warning" icon="Upload" @click="handleImportProxy">导入proxy代理</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="info" icon="Upload" @click="handleImportProxysIo">导入proxys.io代理</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="danger" icon="Delete" :disabled="multiple" @click="handleDelete">删除</el-button>
      </el-col>
    </el-row>

    <el-table v-loading="loading" :data="groupList" @selection-change="handleSelectionChange" style="width: 100%" :header-cell-style="{whiteSpace: 'nowrap'}" :cell-style="{whiteSpace: 'nowrap'}">
      <el-table-column type="selection" width="50" align="center" />
      <el-table-column label="ID" prop="id" width="60" />
      <el-table-column label="标题" prop="title" min-width="150">
        <template #default="scope">
          <span style="cursor:pointer;color:#409eff" @click="handleEditGroup(scope.row)">{{ scope.row.title || '-' }}</span>
        </template>
      </el-table-column>
      <el-table-column label="国家" prop="country" width="120" />
      <el-table-column label="IP数量" prop="totalCount" width="80" align="center" />
      <el-table-column label="IP使用数" prop="usedCount" width="90" align="center">
        <template #default="scope">
          <span :style="{color: scope.row.usedCount > 0 ? '#67c23a' : ''}">{{ scope.row.usedCount ?? 0 }}</span>
        </template>
      </el-table-column>
      <el-table-column label="IP未使用数" prop="unusedCount" width="100" align="center">
        <template #default="scope">
          <span :style="{color: scope.row.unusedCount > 0 ? '#409eff' : '#f56c6c'}">{{ scope.row.unusedCount ?? 0 }}</span>
        </template>
      </el-table-column>
      <el-table-column label="IP废弃数" prop="disabledCount" width="90" align="center">
        <template #default="scope">
          <span :style="{color: scope.row.disabledCount > 0 ? '#909399' : ''}">{{ scope.row.disabledCount ?? 0 }}</span>
        </template>
      </el-table-column>
      <el-table-column label="单IP最大绑定" prop="maxBindable" width="110" align="center" />
      <el-table-column label="到期时间" prop="expireTime" width="170" />
      <el-table-column label="导入时间" prop="importTime" width="170" />
      <el-table-column label="操作" width="180" fixed="right">
        <template #default="scope">
          <el-button link type="primary" @click="handleViewIps(scope.row)">IP列表</el-button>
          <el-button link type="primary" @click="handleEditGroup(scope.row)">编辑</el-button>
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

    <!-- 导入对话框 -->
    <el-dialog v-model="importVisible" title="导入代理IP" width="550px" append-to-body>
      <el-form :model="importForm" label-width="120px" :rules="importRules" ref="importFormRef">
        <el-form-item label="标题" prop="title">
          <el-input v-model="importForm.title" placeholder="请输入IP组标题" />
        </el-form-item>
        <el-form-item label="国家" prop="country">
          <el-select v-model="importForm.country" placeholder="请选择国家" filterable style="width: 100%">
            <el-option v-for="c in countryOptions" :key="c" :label="c" :value="c" />
          </el-select>
        </el-form-item>
        <el-form-item label="到期时间" prop="expireTime">
          <el-date-picker v-model="importForm.expireTime" type="datetime" placeholder="选择到期时间" value-format="YYYY-MM-DD HH:mm:ss" style="width: 100%" />
        </el-form-item>
        <el-form-item label="单IP最大绑定数" prop="maxBindable">
          <el-input-number v-model="importForm.maxBindable" :min="1" :max="100" />
        </el-form-item>
        <el-form-item label="代理IP文件" prop="file">
          <el-upload
            ref="uploadRef"
            :auto-upload="false"
            :limit="1"
            accept=".txt,.text"
            :on-change="handleFileChange"
          >
            <el-button type="primary">选择文件</el-button>
            <template #tip>
              <div class="el-upload__tip">
                文本文件，每行一个代理IP，格式: protocol://user:pass@host:port
              </div>
            </template>
          </el-upload>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="importVisible = false">取消</el-button>
        <el-button type="primary" :loading="importLoading" @click="submitImport">确认导入</el-button>
      </template>
    </el-dialog>

    <!-- 导入ipfly代理对话框 -->
    <el-dialog v-model="ipflyVisible" title="导入ipfly代理" width="550px" append-to-body>
      <el-form :model="ipflyForm" label-width="120px" :rules="importRules" ref="ipflyFormRef">
        <el-form-item label="标题" prop="title">
          <el-input v-model="ipflyForm.title" placeholder="请输入IP组标题" />
        </el-form-item>
        <el-form-item label="国家" prop="country">
          <el-select v-model="ipflyForm.country" placeholder="请选择国家" filterable style="width: 100%">
            <el-option v-for="c in countryOptions" :key="c" :label="c" :value="c" />
          </el-select>
        </el-form-item>
        <el-form-item label="到期时间" prop="expireTime">
          <el-date-picker v-model="ipflyForm.expireTime" type="datetime" placeholder="选择到期时间" value-format="YYYY-MM-DD HH:mm:ss" style="width: 100%" />
        </el-form-item>
        <el-form-item label="单IP最大绑定数" prop="maxBindable">
          <el-input-number v-model="ipflyForm.maxBindable" :min="1" :max="100" />
        </el-form-item>
        <el-form-item label="代理IP文件" prop="file">
          <el-upload
            ref="ipflyUploadRef"
            :auto-upload="false"
            :limit="1"
            accept=".txt,.text"
            :on-change="handleIpflyFileChange"
          >
            <el-button type="primary">选择文件</el-button>
            <template #tip>
              <div class="el-upload__tip">
                文本文件，每行一个代理IP，格式: username:password:host:port（默认socks5协议）
              </div>
            </template>
          </el-upload>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="ipflyVisible = false">取消</el-button>
        <el-button type="primary" :loading="ipflyLoading" @click="submitIpfly">确认导入</el-button>
      </template>
    </el-dialog>

    <!-- 导入proxy代理对话框 -->
    <el-dialog v-model="proxyVisible" title="导入proxy代理" width="550px" append-to-body>
      <el-form :model="proxyForm" label-width="120px" :rules="importRules" ref="proxyFormRef">
        <el-form-item label="标题" prop="title">
          <el-input v-model="proxyForm.title" placeholder="请输入IP组标题" />
        </el-form-item>
        <el-form-item label="国家" prop="country">
          <el-select v-model="proxyForm.country" placeholder="请选择国家" filterable style="width: 100%">
            <el-option v-for="c in countryOptions" :key="c" :label="c" :value="c" />
          </el-select>
        </el-form-item>
        <el-form-item label="到期时间" prop="expireTime">
          <el-date-picker v-model="proxyForm.expireTime" type="datetime" placeholder="选择到期时间" value-format="YYYY-MM-DD HH:mm:ss" style="width: 100%" />
        </el-form-item>
        <el-form-item label="单IP最大绑定数" prop="maxBindable">
          <el-input-number v-model="proxyForm.maxBindable" :min="1" :max="100" />
        </el-form-item>
        <el-form-item label="代理IP文件" prop="file">
          <el-upload
            ref="proxyUploadRef"
            :auto-upload="false"
            :limit="1"
            accept=".txt,.text"
            :on-change="handleProxyFileChange"
          >
            <el-button type="primary">选择文件</el-button>
            <template #tip>
              <div class="el-upload__tip">
                文本文件，每行一个代理IP，格式: host:port:username:password（默认socks5协议）
              </div>
            </template>
          </el-upload>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="proxyVisible = false">取消</el-button>
        <el-button type="primary" :loading="proxyLoading" @click="submitProxy">确认导入</el-button>
      </template>
    </el-dialog>

    <!-- 导入proxys.io代理对话框 -->
    <el-dialog v-model="proxysIoVisible" title="导入proxys.io代理" width="550px" append-to-body>
      <el-form :model="proxysIoForm" label-width="120px" :rules="importRules" ref="proxysIoFormRef">
        <el-form-item label="标题" prop="title">
          <el-input v-model="proxysIoForm.title" placeholder="请输入IP组标题" />
        </el-form-item>
        <el-form-item label="国家" prop="country">
          <el-select v-model="proxysIoForm.country" placeholder="请选择国家" filterable style="width: 100%">
            <el-option v-for="c in countryOptions" :key="c" :label="c" :value="c" />
          </el-select>
        </el-form-item>
        <el-form-item label="到期时间" prop="expireTime">
          <el-date-picker v-model="proxysIoForm.expireTime" type="datetime" placeholder="选择到期时间" value-format="YYYY-MM-DD HH:mm:ss" style="width: 100%" />
        </el-form-item>
        <el-form-item label="单IP最大绑定数" prop="maxBindable">
          <el-input-number v-model="proxysIoForm.maxBindable" :min="1" :max="100" />
        </el-form-item>
        <el-form-item label="代理IP文件" prop="file">
          <el-upload
            ref="proxysIoUploadRef"
            :auto-upload="false"
            :limit="1"
            accept=".txt,.text"
            :on-change="handleProxysIoFileChange"
          >
            <el-button type="primary">选择文件</el-button>
            <template #tip>
              <div class="el-upload__tip">
                文本文件，每行一个代理IP，格式: username:password@host:port（默认socks5协议）
              </div>
            </template>
          </el-upload>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="proxysIoVisible = false">取消</el-button>
        <el-button type="primary" :loading="proxysIoLoading" @click="submitProxysIo">确认导入</el-button>
      </template>
    </el-dialog>

    <!-- 编辑IP组对话框 -->
    <el-dialog v-model="editVisible" title="编辑IP组" width="500px" append-to-body>
      <el-form :model="editForm" label-width="120px">
        <el-form-item label="标题">
          <el-input v-model="editForm.title" />
        </el-form-item>
        <el-form-item label="国家">
          <el-select v-model="editForm.country" placeholder="请选择国家" filterable style="width: 100%">
            <el-option v-for="c in countryOptions" :key="c" :label="c" :value="c" />
          </el-select>
        </el-form-item>
        <el-form-item label="到期时间">
          <el-date-picker v-model="editForm.expireTime" type="datetime" value-format="YYYY-MM-DD HH:mm:ss" style="width: 100%" />
        </el-form-item>
        <el-form-item label="单IP最大绑定数">
          <el-input-number v-model="editForm.maxBindable" :min="1" :max="100" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="editVisible = false">取消</el-button>
        <el-button type="primary" @click="submitEditGroup">确认</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup name="ProxyGroup">
import { listProxyGroup, updateProxyGroup, delProxyGroup, importProxy, importIpflyProxy, importProxyFormat, importProxysIo } from "@/api/tg/proxy";

const { proxy } = getCurrentInstance();
const router = useRouter();

const groupList = ref([]);
const loading = ref(true);
const showSearch = ref(true);
const ids = ref([]);
const multiple = ref(true);
const total = ref(0);

const importVisible = ref(false);
const importLoading = ref(false);
const importFile = ref(null);
const ipflyVisible = ref(false);
const ipflyLoading = ref(false);
const ipflyFile = ref(null);
const proxyVisible = ref(false);
const proxyLoading = ref(false);
const proxyFile = ref(null);
const proxysIoVisible = ref(false);
const proxysIoLoading = ref(false);
const proxysIoFile = ref(null);
const editVisible = ref(false);

const countryOptions = [
  "中国", "美国", "英国", "日本", "韩国", "印度", "德国", "法国", "俄罗斯", "加拿大",
  "澳大利亚", "巴西", "新加坡", "马来西亚", "印度尼西亚", "泰国", "越南", "菲律宾",
  "土耳其", "阿联酋", "沙特阿拉伯", "以色列", "南非", "尼日利亚", "埃及", "墨西哥",
  "阿根廷", "智利", "哥伦比亚", "秘鲁", "荷兰", "比利时", "瑞士", "瑞典", "挪威",
  "丹麦", "芬兰", "波兰", "乌克兰", "罗马尼亚", "捷克", "匈牙利", "奥地利", "希腊",
  "葡萄牙", "西班牙", "意大利", "爱尔兰", "新西兰", "巴基斯坦", "孟加拉国", "斯里兰卡",
  "缅甸", "柬埔寨", "老挝", "尼泊尔", "伊朗", "伊拉克", "卡塔尔", "科威特", "约旦",
  "黎巴嫩", "摩洛哥", "突尼斯", "肯尼亚", "坦桑尼亚", "加纳", "乌干达", "埃塞俄比亚",
  "哈萨克斯坦", "乌兹别克斯坦", "白俄罗斯", "格鲁吉亚", "台湾", "香港", "澳门",
  "其他",
];

const data = reactive({
  queryParams: { pageNum: 1, pageSize: 10, title: undefined, country: undefined },
});
const { queryParams } = toRefs(data);

const importForm = reactive({
  title: "",
  country: "",
  expireTime: "2099-12-31 23:59:59",
  maxBindable: 1,
});
const importRules = {
  title: [{ required: true, message: "请输入标题", trigger: "blur" }],
  country: [{ required: true, message: "请选择国家", trigger: "change" }],
};
const ipflyForm = reactive({
  title: "",
  country: "",
  expireTime: "2099-12-31 23:59:59",
  maxBindable: 1,
});
const proxyForm = reactive({
  title: "",
  country: "",
  expireTime: "2099-12-31 23:59:59",
  maxBindable: 1,
});
const proxysIoForm = reactive({
  title: "",
  country: "",
  expireTime: "2099-12-31 23:59:59",
  maxBindable: 1,
});
const editForm = reactive({ id: null, title: "", country: "", expireTime: "", maxBindable: 1 });

function getList() {
  loading.value = true;
  listProxyGroup(queryParams.value).then(res => {
    groupList.value = res.rows;
    total.value = res.total;
    loading.value = false;
  });
}

function handleQuery() { queryParams.value.pageNum = 1; getList(); }
function resetQuery() { proxy.resetForm("queryRef"); handleQuery(); }
function handleSelectionChange(sel) { ids.value = sel.map(i => i.id); multiple.value = !sel.length; }

function handleImport() {
  importForm.title = "";
  importForm.country = "";
  importForm.expireTime = "2099-12-31 23:59:59";
  importForm.maxBindable = 1;
  importFile.value = null;
  importVisible.value = true;
}

function handleFileChange(file) {
  importFile.value = file.raw;
}

function submitImport() {
  proxy.$refs.importFormRef.validate(valid => {
    if (!valid) return;
    if (!importFile.value) {
      proxy.$modal.msgWarning("请选择代理IP文件");
      return;
    }
    importLoading.value = true;
    const formData = new FormData();
    formData.append("file", importFile.value);
    formData.append("title", importForm.title);
    formData.append("country", importForm.country);
    formData.append("expireTime", importForm.expireTime || "");
    formData.append("maxBindable", importForm.maxBindable);
    importProxy(formData).then(res => {
      proxy.$modal.msgSuccess(res.msg || "导入成功");
      importVisible.value = false;
      getList();
    }).catch(err => {
      proxy.$modal.msgError("导入失败: " + (err.message || err));
    }).finally(() => {
      importLoading.value = false;
    });
  });
}

function handleImportIpfly() {
  ipflyForm.title = "";
  ipflyForm.country = "";
  ipflyForm.expireTime = "2099-12-31 23:59:59";
  ipflyForm.maxBindable = 1;
  ipflyFile.value = null;
  ipflyVisible.value = true;
}

function handleIpflyFileChange(file) {
  ipflyFile.value = file.raw;
}

function submitIpfly() {
  proxy.$refs.ipflyFormRef.validate(valid => {
    if (!valid) return;
    if (!ipflyFile.value) {
      proxy.$modal.msgWarning("请选择代理IP文件");
      return;
    }
    ipflyLoading.value = true;
    const formData = new FormData();
    formData.append("file", ipflyFile.value);
    formData.append("title", ipflyForm.title);
    formData.append("country", ipflyForm.country);
    formData.append("expireTime", ipflyForm.expireTime || "");
    formData.append("maxBindable", ipflyForm.maxBindable);
    importIpflyProxy(formData).then(res => {
      proxy.$modal.msgSuccess(res.msg || "导入成功");
      ipflyVisible.value = false;
      getList();
    }).catch(err => {
      proxy.$modal.msgError("导入失败: " + (err.message || err));
    }).finally(() => {
      ipflyLoading.value = false;
    });
  });
}

function handleImportProxy() {
  proxyForm.title = "";
  proxyForm.country = "";
  proxyForm.expireTime = "2099-12-31 23:59:59";
  proxyForm.maxBindable = 1;
  proxyFile.value = null;
  proxyVisible.value = true;
}

function handleProxyFileChange(file) {
  proxyFile.value = file.raw;
}

function submitProxy() {
  proxy.$refs.proxyFormRef.validate(valid => {
    if (!valid) return;
    if (!proxyFile.value) {
      proxy.$modal.msgWarning("请选择代理IP文件");
      return;
    }
    proxyLoading.value = true;
    const formData = new FormData();
    formData.append("file", proxyFile.value);
    formData.append("title", proxyForm.title);
    formData.append("country", proxyForm.country);
    formData.append("expireTime", proxyForm.expireTime || "");
    formData.append("maxBindable", proxyForm.maxBindable);
    importProxyFormat(formData).then(res => {
      proxy.$modal.msgSuccess(res.msg || "导入成功");
      proxyVisible.value = false;
      getList();
    }).catch(err => {
      proxy.$modal.msgError("导入失败: " + (err.message || err));
    }).finally(() => {
      proxyLoading.value = false;
    });
  });
}

function handleImportProxysIo() {
  proxysIoForm.title = "";
  proxysIoForm.country = "";
  proxysIoForm.expireTime = "2099-12-31 23:59:59";
  proxysIoForm.maxBindable = 1;
  proxysIoFile.value = null;
  proxysIoVisible.value = true;
}

function handleProxysIoFileChange(file) {
  proxysIoFile.value = file.raw;
}

function submitProxysIo() {
  proxy.$refs.proxysIoFormRef.validate(valid => {
    if (!valid) return;
    if (!proxysIoFile.value) {
      proxy.$modal.msgWarning("请选择代理IP文件");
      return;
    }
    proxysIoLoading.value = true;
    const formData = new FormData();
    formData.append("file", proxysIoFile.value);
    formData.append("title", proxysIoForm.title);
    formData.append("country", proxysIoForm.country);
    formData.append("expireTime", proxysIoForm.expireTime || "");
    formData.append("maxBindable", proxysIoForm.maxBindable);
    importProxysIo(formData).then(res => {
      proxy.$modal.msgSuccess(res.msg || "导入成功");
      proxysIoVisible.value = false;
      getList();
    }).catch(err => {
      proxy.$modal.msgError("导入失败: " + (err.message || err));
    }).finally(() => {
      proxysIoLoading.value = false;
    });
  });
}

function handleEditGroup(row) {
  editForm.id = row.id;
  editForm.title = row.title;
  editForm.country = row.country;
  editForm.expireTime = row.expireTime;
  editForm.maxBindable = row.maxBindable;
  editVisible.value = true;
}

function submitEditGroup() {
  updateProxyGroup(editForm).then(() => {
    proxy.$modal.msgSuccess("修改成功");
    editVisible.value = false;
    getList();
  });
}

function handleViewIps(row) {
  router.push({ path: '/tg/proxyIp', query: { groupNo: row.groupNo } });
}

function handleDelete(row) {
  const delIds = row.id ? [row.id] : ids.value;
  proxy.$modal.confirm("确认删除选中的IP组？").then(() => {
    return delProxyGroup(delIds);
  }).then(() => {
    getList();
    proxy.$modal.msgSuccess("删除成功");
  }).catch(() => {});
}

getList();
</script>
