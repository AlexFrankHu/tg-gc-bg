<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryRef" :inline="true" v-show="showSearch" label-width="80px">
      <el-form-item label="所属账号" prop="accountPhone">
        <el-input v-model="queryParams.accountPhone" placeholder="请输入账号手机号" clearable style="width: 200px" @keyup.enter="handleQuery" />
      </el-form-item>
      <el-form-item label="昵称" prop="nickname">
        <el-input v-model="queryParams.nickname" placeholder="请输入昵称" clearable style="width: 160px" @keyup.enter="handleQuery" />
      </el-form-item>
      <el-form-item label="用户名" prop="username">
        <el-input v-model="queryParams.username" placeholder="请输入用户名" clearable style="width: 160px" @keyup.enter="handleQuery" />
      </el-form-item>
      <el-form-item label="手机号" prop="phoneNumber">
        <el-input v-model="queryParams.phoneNumber" placeholder="请输入手机号" clearable style="width: 160px" @keyup.enter="handleQuery" />
      </el-form-item>
      <el-form-item label="TG用户ID" prop="userId">
        <el-input v-model="queryParams.userId" placeholder="请输入TG用户ID" clearable style="width: 160px" @keyup.enter="handleQuery" />
      </el-form-item>
      <el-form-item label="用户类型" prop="userType">
        <el-select v-model="queryParams.userType" placeholder="全部" clearable style="width: 120px">
          <el-option label="普通用户" value="regular" />
          <el-option label="机器人" value="bot" />
          <el-option label="已注销" value="deleted" />
        </el-select>
      </el-form-item>
      <el-form-item label="添加时间">
        <el-date-picker
          v-model="dateRange"
          style="width: 340px"
          value-format="YYYY-MM-DD HH:mm:ss"
          type="datetimerange"
          range-separator="-"
          start-placeholder="开始时间"
          end-placeholder="结束时间"
          :default-time="[new Date(2000, 1, 1, 0, 0, 0), new Date(2000, 1, 1, 23, 59, 59)]"
        />
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="Search" @click="handleQuery">搜索</el-button>
        <el-button icon="Refresh" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button type="danger" plain icon="Delete" :disabled="multiple" @click="handleDelete" v-hasPermi="['tg:contact:remove']">删除</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="success" plain @click="handleAllAutoReply(true)" v-hasPermi="['tg:contact:edit']">全部开启自动回复</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="warning" plain @click="handleAllAutoReply(false)" v-hasPermi="['tg:contact:edit']">全部关闭自动回复</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="info" plain icon="Download" @click="handleExport">导出</el-button>
      </el-col>
      <right-toolbar v-model:showSearch="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <div class="table-scroll-wrapper">
      <el-table v-loading="loading" :data="contactList" @selection-change="handleSelectionChange" :table-layout="'auto'">
        <el-table-column type="selection" width="55" align="center" />
        <el-table-column label="所属账号" align="center" min-width="160">
          <template #default="scope">
            {{ getAccountPhone(scope.row.tgAccountId) }}
          </template>
        </el-table-column>
        <el-table-column label="TG用户ID" align="center" prop="userId" min-width="140" />
        <el-table-column label="昵称" align="center" prop="nickname" :show-overflow-tooltip="true" min-width="120" />
        <el-table-column label="用户名" align="center" prop="username" min-width="130">
          <template #default="scope">
            <span v-if="scope.row.username">@{{ scope.row.username }}</span>
            <span v-else>-</span>
          </template>
        </el-table-column>
        <el-table-column label="手机号" align="center" prop="phoneNumber" min-width="150" />
        <el-table-column label="类型" align="center" prop="userType" min-width="90">
          <template #default="scope">
            <el-tag v-if="scope.row.userType === 'regular'" type="success">普通</el-tag>
            <el-tag v-else-if="scope.row.userType === 'bot'" type="warning">机器人</el-tag>
            <el-tag v-else-if="scope.row.userType === 'deleted'" type="danger">已注销</el-tag>
            <el-tag v-else type="info">{{ scope.row.userType }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="互为好友" align="center" min-width="90">
          <template #default="scope">
            <el-tag :type="scope.row.isMutual ? 'success' : 'info'" size="small">{{ scope.row.isMutual ? '是' : '否' }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="Premium" align="center" min-width="90">
          <template #default="scope">
            <el-tag v-if="scope.row.isPremium" type="warning" size="small">Premium</el-tag>
            <span v-else>-</span>
          </template>
        </el-table-column>
        <el-table-column label="最后在线" align="center" prop="lastOnlineTime" min-width="170" />
        <el-table-column label="最后发送" align="center" prop="lastSendTime" min-width="170">
          <template #default="scope">
            <span>{{ scope.row.lastSendTime || '-' }}</span>
          </template>
        </el-table-column>
        <el-table-column label="最后接收" align="center" prop="lastReceiveTime" min-width="170">
          <template #default="scope">
            <span>{{ scope.row.lastReceiveTime || '-' }}</span>
          </template>
        </el-table-column>
        <el-table-column label="消息总数" align="center" prop="totalMsgCount" min-width="90">
          <template #default="scope">
            <span>{{ scope.row.totalMsgCount || 0 }}</span>
          </template>
        </el-table-column>
        <el-table-column label="账号发送数" align="center" prop="accountSentCount" min-width="100">
          <template #default="scope">
            <span style="color: #67c23a">{{ scope.row.accountSentCount || 0 }}</span>
          </template>
        </el-table-column>
        <el-table-column label="好友发送数" align="center" prop="friendSentCount" min-width="100">
          <template #default="scope">
            <span style="color: #409eff">{{ scope.row.friendSentCount || 0 }}</span>
          </template>
        </el-table-column>
        <el-table-column label="自动回复" align="center" min-width="100">
          <template #default="scope">
            <el-tag :type="scope.row.autoReply ? 'success' : 'danger'" size="small">{{ scope.row.autoReply ? '开启' : '关闭' }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="添加时间" align="center" prop="createTime" min-width="170" />
        <el-table-column label="更新时间" align="center" prop="updateTime" min-width="170" />
        <el-table-column label="操作" align="center" fixed="right" min-width="480">
          <template #default="scope">
            <div style="white-space: nowrap; display: flex; justify-content: center; gap: 4px;">
              <el-button link type="primary" icon="View" @click="handleView(scope.row)" v-hasPermi="['tg:contact:query']">详情</el-button>
              <el-button link type="primary" icon="ChatDotRound" @click="handleChatHistory(scope.row)">聊天记录</el-button>
              <el-button link type="success" icon="Download" @click="handleExportChat(scope.row)">导出聊天记录</el-button>
              <el-button v-if="scope.row.autoReply" link type="danger" @click="handleToggleAutoReply(scope.row)" v-hasPermi="['tg:contact:edit']">关闭自动回复</el-button>
              <el-button v-else link type="success" @click="handleToggleAutoReply(scope.row)" v-hasPermi="['tg:contact:edit']">开启自动回复</el-button>
              <el-button link type="warning" @click="handleSendGreeting(scope.row)">发送问候语</el-button>
            </div>
          </template>
        </el-table-column>
      </el-table>
    </div>

    <pagination v-show="total > 0" :total="total" v-model:page="queryParams.pageNum" v-model:limit="queryParams.pageSize" @pagination="getList" />

    <!-- 详情对话框 -->
    <el-dialog title="好友详情" v-model="detailOpen" width="600px" append-to-body>
      <el-descriptions :column="2" border>
        <el-descriptions-item label="TG用户ID">{{ detail.userId }}</el-descriptions-item>
        <el-descriptions-item label="昵称">{{ detail.nickname }}</el-descriptions-item>
        <el-descriptions-item label="名">{{ detail.firstName }}</el-descriptions-item>
        <el-descriptions-item label="姓">{{ detail.lastName }}</el-descriptions-item>
        <el-descriptions-item label="用户名">{{ detail.username ? '@' + detail.username : '-' }}</el-descriptions-item>
        <el-descriptions-item label="手机号">{{ detail.phoneNumber || '-' }}</el-descriptions-item>
        <el-descriptions-item label="用户类型">{{ detail.userType }}</el-descriptions-item>
        <el-descriptions-item label="互为好友">{{ detail.isMutual ? '是' : '否' }}</el-descriptions-item>
        <el-descriptions-item label="机器人">{{ detail.isBot ? '是' : '否' }}</el-descriptions-item>
        <el-descriptions-item label="Premium">{{ detail.isPremium ? '是' : '否' }}</el-descriptions-item>
        <el-descriptions-item label="已验证">{{ detail.isVerified ? '是' : '否' }}</el-descriptions-item>
        <el-descriptions-item label="限制原因">{{ detail.restrictionReason || '-' }}</el-descriptions-item>
        <el-descriptions-item label="简介" :span="2">{{ detail.bio || '-' }}</el-descriptions-item>
        <el-descriptions-item label="最后在线">{{ detail.lastOnlineTime || '-' }}</el-descriptions-item>
        <el-descriptions-item label="入库时间">{{ detail.createTime }}</el-descriptions-item>
      </el-descriptions>
      <template #footer>
        <el-button @click="detailOpen = false">关 闭</el-button>
      </template>
    </el-dialog>

    <!-- 聊天记录对话框 -->
    <el-dialog :title="chatDialogTitle" v-model="chatOpen" width="750px" append-to-body>
      <div class="chat-container" ref="chatContainerRef">
        <div v-if="chatLoading" class="chat-loading">
          <el-icon class="is-loading"><Loading /></el-icon>
          <span>加载中...</span>
        </div>
        <div v-else-if="chatMessages.length === 0" class="chat-empty">
          暂无聊天记录
        </div>
        <div v-else class="chat-messages">
          <div v-if="chatTotal > chatMessages.length" class="chat-load-more-top">
            <el-button link type="primary" @click="loadMoreChat" :loading="chatLoadingMore">加载更早的消息</el-button>
          </div>
          <div v-for="msg in chatMessages" :key="msg.id" class="chat-message" :class="{ 'outgoing': msg.isOutgoing, 'incoming': !msg.isOutgoing }">
            <div class="msg-sender">{{ msg.isOutgoing ? getAccountPhone(chatContact.tgAccountId) : chatContact.nickname }}</div>
            <div class="msg-bubble">
              <img v-if="msg.contentType === 'photo' && msg.mediaFileId" :src="getMediaUrl(chatContact.tgAccountId, msg.mediaFileId, msg.chatId, msg.messageId)" class="msg-photo" loading="lazy" @click="previewImage(getMediaUrl(chatContact.tgAccountId, msg.mediaFileId, msg.chatId, msg.messageId))" />
              <img v-else-if="msg.contentType === 'photo' && msg.textContent && msg.textContent.startsWith('/profile/')" :src="msg.textContent" class="msg-photo" loading="lazy" @click="previewImage(msg.textContent)" />
              <div v-if="msg.contentType === 'sticker' && msg.mediaFileId" class="msg-sticker">
                <img :src="getMediaUrl(chatContact.tgAccountId, msg.mediaFileId, msg.chatId, msg.messageId)" class="sticker-img" loading="lazy" />
              </div>
              <div v-if="msg.contentType === 'video'" class="msg-media-tag">[视频]</div>
              <div v-if="msg.contentType === 'voice'" class="msg-media-tag">[语音]</div>
              <div v-if="msg.contentType === 'document'" class="msg-media-tag">[文件{{ msg.mediaFileName ? ': ' + msg.mediaFileName : '' }}]</div>
              <div v-if="msg.textContent && !(msg.contentType === 'photo' && msg.textContent.startsWith('/profile/'))" class="msg-content">{{ msg.textContent }}</div>
              <div v-if="!msg.textContent && msg.contentType === 'other'" class="msg-content">[{{ msg.contentType }}]</div>
              <div class="msg-time">{{ msg.sendTime }}</div>
            </div>
          </div>
        </div>
      </div>
      <template #footer>
        <el-button @click="chatOpen = false">关 闭</el-button>
      </template>
    </el-dialog>

    <!-- 图片预览 -->
    <el-image-viewer v-if="previewVisible" :url-list="[previewUrl]" @close="previewVisible = false" />

    <!-- 发送问候语对话框 -->
    <el-dialog title="发送问候语" v-model="greetingOpen" width="500px" append-to-body>
      <el-form label-width="80px">
        <el-form-item label="账号">
          <span>{{ greetingForm.accountPhone }}</span>
        </el-form-item>
        <el-form-item label="好友">
          <span>{{ greetingForm.friendName }}</span>
        </el-form-item>
        <el-form-item label="问候语">
          <el-select v-model="greetingForm.greetingId" placeholder="请选择问候语" style="width: 100%" @change="handleGreetingChange">
            <el-option v-for="item in greetingOptions" :key="item.id" :label="item.title + ' - ' + item.content.substring(0, 30)" :value="item.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="预览" v-if="greetingForm.greetingId">
          <div style="background: #f5f5f5; padding: 10px; border-radius: 4px; width: 100%;">
            <div>{{ selectedGreeting.content }}</div>
            <el-image v-if="selectedGreeting.imagePath" :src="baseUrl + selectedGreeting.imagePath" style="max-width: 200px; max-height: 200px; margin-top: 8px" fit="cover" />
          </div>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="greetingOpen = false">取消</el-button>
        <el-button type="primary" :loading="greetingSending" @click="doSendGreeting">发送</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup name="Contact">
import { listContact, getContact, delContact, updateAutoReply, updateAllAutoReply, sendGreeting } from "@/api/tg/contact";
import { listChatMessage } from "@/api/tg/chatMessage";
import { listGreeting } from "@/api/tg/greeting";
import { listAccount, getWsToken } from "@/api/tg/account";
import { Loading, Download } from '@element-plus/icons-vue';

const baseUrl = import.meta.env.VITE_APP_BASE_API;

const { proxy } = getCurrentInstance();

const contactList = ref([]);
const accountList = ref([]);
const loading = ref(true);
const showSearch = ref(true);
const ids = ref([]);
const multiple = ref(true);
const total = ref(0);
const detailOpen = ref(false);
const detail = ref({});

// 聊天记录相关
const chatOpen = ref(false);
const chatLoading = ref(false);
const chatLoadingMore = ref(false);
const chatMessages = ref([]);
const chatTotal = ref(0);
const chatContact = ref({});
const chatDialogTitle = ref('');
const chatContainerRef = ref(null);
const chatPageNum = ref(1);



// 图片预览
const previewVisible = ref(false);
const previewUrl = ref('');

const dateRange = ref([]);

const queryParams = ref({
  pageNum: 1,
  pageSize: 10,
  accountPhone: undefined,
  nickname: undefined,
  username: undefined,
  phoneNumber: undefined,
  userId: undefined,
  userType: undefined,
});

function getList() {
  loading.value = true;
  const params = { ...queryParams.value };
  if (dateRange.value && dateRange.value.length === 2) {
    params['params[beginCreateTime]'] = dateRange.value[0];
    params['params[endCreateTime]'] = dateRange.value[1];
  }
  listContact(params).then(response => {
    contactList.value = response.rows;
    total.value = response.total;
    loading.value = false;
  });
}

function getAccountList() {
  listAccount({ pageNum: 1, pageSize: 1000 }).then(response => {
    accountList.value = response.rows;
  });
}

function getAccountPhone(accountId) {
  const account = accountList.value.find(a => a.id === accountId);
  return account ? account.phone : accountId;
}

function handleQuery() {
  queryParams.value.pageNum = 1;
  getList();
}

function resetQuery() {
  proxy.resetForm("queryRef");
  dateRange.value = [];
  handleQuery();
}

function handleExport() {
  proxy.$modal.confirm('是否确认导出所有好友数据？').then(() => {
    const params = { ...queryParams.value };
    delete params.pageNum;
    delete params.pageSize;
    if (dateRange.value && dateRange.value.length === 2) {
      params['params[beginCreateTime]'] = dateRange.value[0];
      params['params[endCreateTime]'] = dateRange.value[1];
    }
    proxy.download('tg/contact/export', params, '好友列表.xlsx');
  }).catch(() => {});
}

function handleSelectionChange(selection) {
  ids.value = selection.map(item => item.id);
  multiple.value = !selection.length;
}

function handleView(row) {
  getContact(row.id).then(response => {
    detail.value = response.data;
    detailOpen.value = true;
  });
}

function getMediaUrl(tgAccountId, fileId, chatId, messageId) {
  if (!fileId || !mediaToken.value) return '';
  let url = '/tg-telethon-api/api/client/tg/file?tgAccountId=' + tgAccountId + '&fileId=' + fileId + '&token=' + mediaToken.value;
  if (chatId) url += '&chatId=' + chatId;
  if (messageId) url += '&messageId=' + messageId;
  return url;
}

function previewImage(url) {
  previewUrl.value = url;
  previewVisible.value = true;
}

const mediaToken = ref('');

async function ensureToken() {
  if (mediaToken.value) return;
  // Get token for the first available account
  const accounts = accountList.value.filter(a => a.status === 'online');
  if (accounts.length > 0) {
    try {
      const res = await getWsToken(accounts[0].id);
      if (res.code === 200 && res.data) {
        let tokenData = res.data;
        if (typeof tokenData === 'string') {
          try { tokenData = JSON.parse(tokenData); } catch(e) { tokenData = { token: tokenData }; }
        }
        mediaToken.value = tokenData.token || tokenData;
      }
    } catch (e) {
      console.warn('获取媒体token失败', e);
    }
  }
}

async function handleChatHistory(row) {
  chatContact.value = row;
  const accountPhone = getAccountPhone(row.tgAccountId);
  chatDialogTitle.value = accountPhone + ' 与 ' + (row.nickname || row.userId) + ' 的聊天记录';
  chatMessages.value = [];
  chatTotal.value = 0;
  chatPageNum.value = 1;
  chatOpen.value = true;
  chatLoading.value = true;

  await ensureToken();

  listChatMessage({
    tgAccountId: row.tgAccountId,
    chatId: row.userId,
    pageNum: 1,
    pageSize: 500,
  }).then(response => {
    chatMessages.value = (response.rows || []).reverse();
    chatTotal.value = response.total || 0;
    chatLoading.value = false;
    nextTick(() => {
      scrollChatToBottom();
    });
  }).catch(() => {
    chatLoading.value = false;
  });
}

function loadMoreChat() {
  chatLoadingMore.value = true;
  chatPageNum.value++;
  const prevScrollHeight = chatContainerRef.value ? chatContainerRef.value.scrollHeight : 0;
  listChatMessage({
    tgAccountId: chatContact.value.tgAccountId,
    chatId: chatContact.value.userId,
    pageNum: chatPageNum.value,
    pageSize: 500,
  }).then(response => {
    const older = (response.rows || []).reverse();
    chatMessages.value = [...older, ...chatMessages.value];
    chatLoadingMore.value = false;
    nextTick(() => {
      if (chatContainerRef.value) {
        const newScrollHeight = chatContainerRef.value.scrollHeight;
        chatContainerRef.value.scrollTop = newScrollHeight - prevScrollHeight;
      }
    });
  }).catch(() => {
    chatLoadingMore.value = false;
  });
}

function scrollChatToBottom() {
  if (chatContainerRef.value) {
    chatContainerRef.value.scrollTop = chatContainerRef.value.scrollHeight;
  }
}

function handleDelete(row) {
  const deleteIds = row.id || ids.value;
  proxy.$modal.confirm('确认删除选中的好友记录吗？').then(() => {
    return delContact(deleteIds);
  }).then(() => {
    getList();
    proxy.$modal.msgSuccess("删除成功");
  }).catch(() => {});
}

function handleToggleAutoReply(row) {
  const newVal = !row.autoReply;
  const action = newVal ? '开启' : '关闭';
  proxy.$modal.confirm('确认要' + action + '该好友的自动回复吗？').then(() => {
    updateAutoReply(row.id, newVal).then(() => {
      proxy.$modal.msgSuccess(action + '成功');
      row.autoReply = newVal;
    });
  }).catch(() => {});
}

function handleAllAutoReply(autoReply) {
  const action = autoReply ? '开启' : '关闭';
  proxy.$modal.confirm('确认要' + action + '所有好友的自动回复吗？').then(() => {
    updateAllAutoReply(autoReply).then(() => {
      proxy.$modal.msgSuccess(action + '成功');
      getList();
    });
  }).catch(() => {});
}

function handleExportChat(row) {
  const accountPhone = getAccountPhone(row.tgAccountId);
  const friendName = row.nickname || row.userId;
  proxy.download('tg/chatMessage/export', {
    tgAccountId: row.tgAccountId,
    chatId: row.userId,
  }, `聊天记录_${accountPhone}_${friendName}_${new Date().getTime()}.xlsx`);
}

// 发送问候语相关
const greetingOpen = ref(false);
const greetingSending = ref(false);
const greetingOptions = ref([]);
const greetingForm = ref({ accountId: null, userId: null, accountPhone: '', friendName: '', greetingId: null });
const selectedGreeting = ref({});

function handleSendGreeting(row) {
  greetingForm.value = {
    accountId: row.tgAccountId,
    userId: row.userId,
    accountPhone: getAccountPhone(row.tgAccountId),
    friendName: row.nickname || row.userId,
    greetingId: null
  };
  selectedGreeting.value = {};
  greetingOpen.value = true;
  listGreeting({ pageNum: 1, pageSize: 200, isEnabled: 1 }).then(res => {
    greetingOptions.value = res.rows || [];
  });
}

function handleGreetingChange(id) {
  const g = greetingOptions.value.find(item => item.id === id);
  selectedGreeting.value = g || {};
}

function doSendGreeting() {
  if (!greetingForm.value.greetingId) {
    proxy.$modal.msgWarning('请选择问候语');
    return;
  }
  greetingSending.value = true;
  sendGreeting({
    accountId: greetingForm.value.accountId,
    userId: greetingForm.value.userId,
    greetingId: greetingForm.value.greetingId
  }).then(res => {
    proxy.$modal.msgSuccess('发送成功');
    greetingOpen.value = false;
  }).catch(() => {}).finally(() => {
    greetingSending.value = false;
  });
}

getAccountList();
getList();
</script>

<style scoped>
.table-scroll-wrapper {
  overflow-x: auto;
}
.table-scroll-wrapper :deep(.el-table) {
  width: max-content;
  min-width: 100%;
}
.chat-container {
  height: 500px;
  overflow-y: auto;
  padding: 10px;
  background: #f5f5f5;
  border-radius: 8px;
}
.chat-loading, .chat-empty {
  display: flex;
  align-items: center;
  justify-content: center;
  height: 100%;
  color: #999;
  gap: 8px;
}
.chat-messages {
  display: flex;
  flex-direction: column;
  gap: 12px;
}
.chat-load-more-top {
  text-align: center;
  padding: 8px 0;
}
.chat-message {
  display: flex;
  flex-direction: column;
  max-width: 70%;
}
.chat-message.incoming {
  align-self: flex-start;
}
.chat-message.outgoing {
  align-self: flex-end;
}
.msg-sender {
  font-size: 12px;
  color: #999;
  margin-bottom: 2px;
}
.chat-message.outgoing .msg-sender {
  text-align: right;
}
.msg-bubble {
  padding: 8px 12px;
  border-radius: 12px;
  word-break: break-word;
}
.chat-message.incoming .msg-bubble {
  background: #fff;
  border: 1px solid #e8e8e8;
  border-top-left-radius: 4px;
}
.chat-message.outgoing .msg-bubble {
  background: #95ec69;
  border-top-right-radius: 4px;
}
.msg-content {
  font-size: 14px;
  line-height: 1.5;
}
.msg-photo {
  max-width: 300px;
  max-height: 300px;
  border-radius: 8px;
  cursor: pointer;
  display: block;
  margin-bottom: 4px;
}
.msg-sticker {
  margin-bottom: 4px;
}
.sticker-img {
  max-width: 150px;
  max-height: 150px;
}
.msg-media-tag {
  color: #666;
  font-size: 13px;
  font-style: italic;
  margin-bottom: 4px;
}
.msg-time {
  font-size: 11px;
  color: #999;
  margin-top: 4px;
  text-align: right;
}
</style>
