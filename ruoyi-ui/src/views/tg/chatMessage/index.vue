<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryRef" :inline="true" v-show="showSearch" label-width="80px">
      <el-form-item label="所属账号" prop="phone">
        <el-input v-model="queryParams.phone" placeholder="请输入账号手机号" clearable style="width: 200px" @keyup.enter="handleQuery" />
      </el-form-item>
      <el-form-item label="聊天ID" prop="chatId">
        <el-input v-model="queryParams.chatId" placeholder="请输入聊天ID" clearable style="width: 160px" @keyup.enter="handleQuery" />
      </el-form-item>
      <el-form-item label="消息类型" prop="contentType">
        <el-select v-model="queryParams.contentType" placeholder="全部" clearable style="width: 120px">
          <el-option label="文本" value="text" />
          <el-option label="图片" value="photo" />
          <el-option label="视频" value="video" />
          <el-option label="语音" value="voice" />
          <el-option label="贴纸" value="sticker" />
          <el-option label="文件" value="document" />
          <el-option label="其他" value="other" />
        </el-select>
      </el-form-item>
      <el-form-item label="消息内容" prop="textContent">
        <el-input v-model="queryParams.textContent" placeholder="搜索消息内容" clearable style="width: 200px" @keyup.enter="handleQuery" />
      </el-form-item>
      <el-form-item label="方向" prop="isOutgoing">
        <el-select v-model="queryParams.isOutgoing" placeholder="全部" clearable style="width: 100px">
          <el-option label="发出" :value="true" />
          <el-option label="接收" :value="false" />
        </el-select>
      </el-form-item>
      <el-form-item label="发送时间">
        <el-date-picker v-model="dateRange" style="width: 260px" value-format="YYYY-MM-DD" type="daterange" range-separator="-" start-placeholder="开始日期" end-placeholder="结束日期" />
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="Search" @click="handleQuery">搜索</el-button>
        <el-button icon="Refresh" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button type="danger" plain icon="Delete" :disabled="multiple" @click="handleDelete" v-hasPermi="['tg:chatMessage:remove']">删除</el-button>
      </el-col>
      <right-toolbar v-model:showSearch="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="messageList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="所属账号" align="center" width="160">
        <template #default="scope">
          {{ scope.row.accountPhone || scope.row.tgAccountId }}
        </template>
      </el-table-column>
      <el-table-column label="聊天ID" align="center" prop="chatId" width="160" />
      <el-table-column label="方向" align="center" width="80">
        <template #default="scope">
          <el-tag :type="scope.row.isOutgoing ? 'primary' : 'success'" size="small">
            {{ scope.row.isOutgoing ? '发出' : '接收' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="发送者" align="center" width="140">
        <template #default="scope">
          {{ scope.row.senderName || scope.row.senderUserId || scope.row.senderChatId || '-' }}
        </template>
      </el-table-column>
      <el-table-column label="类型" align="center" prop="contentType" width="90">
        <template #default="scope">
          <el-tag :type="getContentTypeTag(scope.row.contentType)" size="small">{{ getContentTypeLabel(scope.row.contentType) }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="消息内容" align="left" min-width="250">
        <template #default="scope">
          <span v-if="scope.row.contentType === 'text'">{{ scope.row.textContent }}</span>
          <span v-else-if="scope.row.contentType === 'sticker'">{{ scope.row.textContent || '(贴纸)' }}</span>
          <span v-else-if="scope.row.contentType === 'photo'">
            <el-image v-if="scope.row.textContent && scope.row.textContent.startsWith('/profile/')" :src="scope.row.textContent" style="width: 80px; height: 80px; object-fit: cover; cursor: pointer;" :preview-src-list="[scope.row.textContent]" fit="cover" />
            <span v-else>[图片] {{ scope.row.textContent }}</span>
          </span>
          <span v-else-if="scope.row.contentType === 'video'">[视频] {{ scope.row.textContent }}</span>
          <span v-else-if="scope.row.contentType === 'voice'">[语音] {{ scope.row.mediaDuration ? scope.row.mediaDuration + '秒' : '' }}</span>
          <span v-else-if="scope.row.contentType === 'document'">[文件] {{ scope.row.mediaFileName || scope.row.textContent }}</span>
          <span v-else>{{ scope.row.textContent || scope.row.contentType }}</span>
        </template>
      </el-table-column>
      <el-table-column label="发送时间" align="center" prop="sendTime" width="170" />
      <el-table-column label="操作" align="center" width="100">
        <template #default="scope">
          <el-button link type="primary" icon="View" @click="handleView(scope.row)" v-hasPermi="['tg:chatMessage:query']">详情</el-button>
        </template>
      </el-table-column>
    </el-table>

    <pagination v-show="total > 0" :total="total" v-model:page="queryParams.pageNum" v-model:limit="queryParams.pageSize" @pagination="getList" />

    <!-- 详情对话框 -->
    <el-dialog title="消息详情" v-model="detailOpen" width="650px" append-to-body>
      <el-descriptions :column="2" border>
        <el-descriptions-item label="消息ID">{{ detail.messageId }}</el-descriptions-item>
        <el-descriptions-item label="聊天ID">{{ detail.chatId }}</el-descriptions-item>
        <el-descriptions-item label="方向">{{ detail.isOutgoing ? '发出' : '接收' }}</el-descriptions-item>
        <el-descriptions-item label="发送时间">{{ detail.sendTime }}</el-descriptions-item>
        <el-descriptions-item label="发送者ID">{{ detail.senderUserId || detail.senderChatId || '-' }}</el-descriptions-item>
        <el-descriptions-item label="发送者">{{ detail.senderName || '-' }}</el-descriptions-item>
        <el-descriptions-item label="消息类型">{{ getContentTypeLabel(detail.contentType) }}</el-descriptions-item>
        <el-descriptions-item label="入库时间">{{ detail.createTime }}</el-descriptions-item>
        <el-descriptions-item label="消息内容" :span="2">
          <div style="max-height: 200px; overflow-y: auto; word-break: break-all;">
            <el-image v-if="detail.contentType === 'photo' && detail.textContent && detail.textContent.startsWith('/profile/')" :src="detail.textContent" style="max-width: 300px; max-height: 200px;" :preview-src-list="[detail.textContent]" fit="contain" />
            <span v-else>{{ detail.textContent || '-' }}</span>
          </div>
        </el-descriptions-item>
        <template v-if="detail.mediaFileId">
          <el-descriptions-item label="媒体文件ID">{{ detail.mediaFileId }}</el-descriptions-item>
          <el-descriptions-item label="文件大小">{{ detail.mediaFileSize ? formatFileSize(detail.mediaFileSize) : '-' }}</el-descriptions-item>
          <el-descriptions-item label="MIME类型">{{ detail.mediaMimeType || '-' }}</el-descriptions-item>
          <el-descriptions-item label="文件名">{{ detail.mediaFileName || '-' }}</el-descriptions-item>
          <el-descriptions-item label="时长" v-if="detail.mediaDuration">{{ detail.mediaDuration }}秒</el-descriptions-item>
          <el-descriptions-item label="尺寸" v-if="detail.mediaWidth">{{ detail.mediaWidth }} x {{ detail.mediaHeight }}</el-descriptions-item>
        </template>
      </el-descriptions>
      <template #footer>
        <el-button @click="detailOpen = false">关 闭</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup name="ChatMessage">
import { listChatMessage, getChatMessage, delChatMessage } from "@/api/tg/chatMessage";

const { proxy } = getCurrentInstance();

const messageList = ref([]);
const loading = ref(true);
const showSearch = ref(true);
const ids = ref([]);
const multiple = ref(true);
const total = ref(0);
const detailOpen = ref(false);
const detail = ref({});
const dateRange = ref([]);

const queryParams = ref({
  pageNum: 1,
  pageSize: 20,
  phone: undefined,
  chatId: undefined,
  contentType: undefined,
  textContent: undefined,
  isOutgoing: undefined,
});

function getList() {
  loading.value = true;
  const params = { ...queryParams.value };
  if (dateRange.value && dateRange.value.length === 2) {
    params['params[beginSendTime]'] = dateRange.value[0];
    params['params[endSendTime]'] = dateRange.value[1];
  }
  listChatMessage(params).then(response => {
    messageList.value = response.rows;
    total.value = response.total;
    loading.value = false;
  });
}

function getContentTypeLabel(type) {
  const map = { text: '文本', photo: '图片', video: '视频', voice: '语音', sticker: '贴纸', document: '文件', other: '其他' };
  return map[type] || type;
}

function getContentTypeTag(type) {
  const map = { text: '', photo: 'success', video: 'warning', voice: 'info', sticker: 'danger', document: 'primary', other: 'info' };
  return map[type] || 'info';
}

function formatFileSize(bytes) {
  if (bytes < 1024) return bytes + ' B';
  if (bytes < 1024 * 1024) return (bytes / 1024).toFixed(1) + ' KB';
  return (bytes / (1024 * 1024)).toFixed(1) + ' MB';
}

function handleQuery() {
  queryParams.value.pageNum = 1;
  getList();
}

function resetQuery() {
  dateRange.value = [];
  proxy.resetForm("queryRef");
  handleQuery();
}

function handleSelectionChange(selection) {
  ids.value = selection.map(item => item.id);
  multiple.value = !selection.length;
}

function handleView(row) {
  getChatMessage(row.id).then(response => {
    detail.value = response.data;
    detailOpen.value = true;
  });
}

function handleDelete(row) {
  const deleteIds = row.id || ids.value;
  proxy.$modal.confirm('确认删除选中的聊天记录吗？').then(() => {
    return delChatMessage(deleteIds);
  }).then(() => {
    getList();
    proxy.$modal.msgSuccess("删除成功");
  }).catch(() => {});
}

getList();
</script>
