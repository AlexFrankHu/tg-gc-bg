<template>
  <div class="tg-app">
    <!-- Left Sidebar: Chat List -->
    <div class="tg-sidebar">
      <div class="tg-sidebar-header">
        <div class="tg-account-info">
          <span class="tg-account-name">{{ accountUsername || accountPhone }}</span>
        </div>
        <div class="tg-search-box">
          <input v-model="searchQuery" placeholder="搜索" class="tg-search-input" />
        </div>
      </div>
      <div class="tg-chat-list" ref="chatListRef">
        <div
          v-for="chat in filteredChats"
          :key="chat.chatId"
          class="tg-chat-item"
          :class="{ active: currentChatId === chat.chatId }"
          @click="selectChat(chat)"
        >
          <div class="tg-chat-avatar">
            <span>{{ getAvatarText(chat.title) }}</span>
          </div>
          <div class="tg-chat-info">
            <div class="tg-chat-top">
              <span class="tg-chat-name">{{ chat.title }}</span>
              <span class="tg-chat-time">{{ formatTime(chat.lastMessageDate) }}</span>
            </div>
            <div class="tg-chat-bottom">
              <span class="tg-chat-preview">{{ chat.lastMessagePreview || '' }}</span>
              <span class="tg-chat-badge" v-if="chat.unreadCount > 0">{{ chat.unreadCount > 99 ? '99+' : chat.unreadCount }}</span>
            </div>
          </div>
        </div>
        <div v-if="filteredChats.length === 0 && !loadingChats" class="tg-empty">暂无会话</div>
        <div v-if="loadingChats" class="tg-loading">加载中...</div>
      </div>
    </div>

    <!-- Center: Chat Area -->
    <div class="tg-main" v-if="currentChat">
      <div class="tg-main-header">
        <div class="tg-main-header-info">
          <span class="tg-main-chat-name">{{ currentChat.title }}</span>
          <span class="tg-main-chat-type">{{ chatTypeLabel(currentChat.type) }}</span>
        </div>
        <div class="tg-main-header-actions">
          <button v-if="isGroupChat" class="tg-icon-btn" @click="toggleMembers" :class="{ active: showMembers }">
            <span>👥</span>
          </button>
        </div>
      </div>
      <div class="tg-messages" ref="messagesRef" @scroll="onMessagesScroll">
        <div v-if="loadingHistory" class="tg-loading-more">加载更多...</div>
        <div
          v-for="msg in currentMessages"
          :key="msg.messageId"
          class="tg-message"
          :class="{ outgoing: msg.outgoing, incoming: !msg.outgoing }"
        >
          <div class="tg-message-bubble">
            <div class="tg-message-sender" v-if="!msg.outgoing && isGroupChat">{{ msg.senderName || '未知用户' }}</div>
            <div class="tg-message-media" v-if="msg.media">
              <img v-if="msg.media.kind === 'photo' && msg.media.fileId" :src="mediaUrl(msg.media.fileId)" class="tg-message-photo" loading="lazy" />
              <div v-else-if="msg.media.kind === 'sticker'" class="tg-message-sticker">
                <img v-if="msg.media.fileId" :src="mediaUrl(msg.media.fileId)" class="tg-sticker-img" loading="lazy" />
                <span v-else>{{ msg.media.stickerEmoji || '🏷️' }}</span>
              </div>
              <div v-else class="tg-message-file">
                <span>📎 {{ msg.media.fileName || msg.media.kind }}</span>
              </div>
            </div>
            <div class="tg-message-text" v-if="msg.textPreview">{{ msg.textPreview }}</div>
            <div class="tg-message-time">{{ formatMessageTime(msg.date) }}</div>
          </div>
        </div>
        <div v-if="currentMessages.length === 0 && !loadingHistory" class="tg-empty-messages">暂无消息</div>
      </div>
      <div class="tg-input-area">
        <textarea
          v-model="inputText"
          class="tg-input-text"
          placeholder="输入消息..."
          @keydown.enter.exact.prevent="sendMessage"
          rows="1"
          ref="inputRef"
        ></textarea>
        <button class="tg-send-btn" @click="sendMessage" :disabled="!inputText.trim()">发送</button>
      </div>
    </div>
    <div class="tg-main tg-no-chat" v-else>
      <div class="tg-no-chat-content">
        <p v-if="wsConnected">选择一个会话开始聊天</p>
        <p v-else-if="wsConnecting">正在连接...</p>
        <p v-else>连接断开，请刷新页面</p>
      </div>
    </div>

    <!-- Right Sidebar: Members (for groups) -->
    <div class="tg-members" v-if="showMembers && isGroupChat">
      <div class="tg-members-header">
        <span>成员列表 ({{ members.length }})</span>
        <button class="tg-close-btn" @click="showMembers = false">✕</button>
      </div>
      <div class="tg-members-list">
        <div v-for="member in members" :key="member.userId" class="tg-member-item">
          <div class="tg-member-avatar">
            <span>{{ getAvatarText((member.firstName || '') + ' ' + (member.lastName || '')) }}</span>
          </div>
          <div class="tg-member-info">
            <span class="tg-member-name">{{ (member.firstName || '') + ' ' + (member.lastName || '') }}</span>
            <span class="tg-member-username" v-if="member.username">@{{ member.username }}</span>
          </div>
        </div>
        <div v-if="members.length === 0" class="tg-empty">暂无成员数据</div>
      </div>
    </div>

    <!-- Connection status -->
    <div class="tg-status-bar" v-if="statusMessage">
      <span>{{ statusMessage }}</span>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, onUnmounted, nextTick, watch } from 'vue'
import { useRoute } from 'vue-router'

const route = useRoute()
const accountId = ref(null)
const accountPhone = ref('')
const accountUsername = ref('')

// WebSocket
let ws = null
let requestIdCounter = 0
const wsConnected = ref(false)
const wsConnecting = ref(false)
const statusMessage = ref('')
let pingTimer = null
let reconnectTimer = null
const pendingRequests = new Map()

// Chat list
const chats = ref([])
const loadingChats = ref(false)
const searchQuery = ref('')
const currentChatId = ref(null)
const currentChat = ref(null)

// Messages
const currentMessages = ref([])
const loadingHistory = ref(false)
const oldestMessageId = ref(0)
const hasMoreHistory = ref(true)
const inputText = ref('')
const messagesRef = ref(null)
const inputRef = ref(null)

// Members
const showMembers = ref(false)
const members = ref([])

// Auth
let clientToken = null

const filteredChats = computed(() => {
  if (!searchQuery.value) return chats.value
  const q = searchQuery.value.toLowerCase()
  return chats.value.filter(c => c.title && c.title.toLowerCase().includes(q))
})

const isGroupChat = computed(() => {
  return currentChat.value && (currentChat.value.type === 'basic_group' || currentChat.value.type === 'supergroup')
})

const TG_SERVER_BASE = window.location.origin + '/tg-telethon-api'
const WS_BASE = (window.location.protocol === 'https:' ? 'wss://' : 'ws://') + window.location.host + '/tg-telethon-ws/ws/client'

function generateRequestId() {
  return 'req-' + (++requestIdCounter) + '-' + Date.now()
}

function initToken() {
  const token = route.query.token
  if (token) {
    clientToken = token
    return true
  }
  statusMessage.value = '缺少连接凭证，请从账号管理页面点击网页端按钮进入'
  return false
}

function connectWebSocket() {
  if (ws) {
    ws.close()
    ws = null
  }
  wsConnecting.value = true
  statusMessage.value = '正在连接WebSocket...'

  ws = new WebSocket(WS_BASE + '?token=' + clientToken)

  ws.onopen = () => {
    wsConnected.value = true
    wsConnecting.value = false
    statusMessage.value = ''

    // Switch to the target TG account
    sendWsRequest('tg.account.switch', { tgAccountId: parseInt(accountId.value) })

    // Start ping
    pingTimer = setInterval(() => {
      if (ws && ws.readyState === WebSocket.OPEN) {
        ws.send(JSON.stringify({ type: 'ping' }))
      }
    }, 30000)

    // Load chat list
    loadChatList()
  }

  ws.onmessage = (event) => {
    try {
      const envelope = JSON.parse(event.data)
      handleWsMessage(envelope)
    } catch (e) {
      console.error('WS parse error', e)
    }
  }

  ws.onclose = () => {
    wsConnected.value = false
    wsConnecting.value = false
    if (pingTimer) { clearInterval(pingTimer); pingTimer = null }
    statusMessage.value = '连接已断开'
    // Auto reconnect after 3s
    reconnectTimer = setTimeout(() => {
      if (!wsConnected.value) {
        connectWebSocket()
      }
    }, 3000)
  }

  ws.onerror = () => {
    statusMessage.value = '连接错误'
  }
}

function sendWsRequest(type, data, callback) {
  if (!ws || ws.readyState !== WebSocket.OPEN) return null
  const requestId = generateRequestId()
  const msg = { type, requestId, data: data || {} }
  if (callback) pendingRequests.set(requestId, callback)
  ws.send(JSON.stringify(msg))
  return requestId
}

function handleWsMessage(envelope) {
  const { type, requestId, data } = envelope

  // Handle pending request callbacks
  if (requestId && pendingRequests.has(requestId)) {
    const callback = pendingRequests.get(requestId)
    pendingRequests.delete(requestId)
    callback(data, type)
  }

  switch (type) {
    case 'auth.login.success':
      break
    case 'tg.account.list':
      break
    case 'tg.account.switch.ok':
      break
    case 'tg.chat.list':
      if (data && data.chats) {
        chats.value = data.chats.sort((a, b) => (b.lastMessageDate || 0) - (a.lastMessageDate || 0))
        loadingChats.value = false
      }
      break
    case 'tg.chat.history':
      if (data && data.chatId === currentChatId.value) {
        handleHistoryResponse(data.messages || [])
      }
      break
    case 'tg.message.send.result':
      if (data && data.message && data.chatId === currentChatId.value) {
        appendMessage(data.message)
      }
      break
    case 'tg.message.new':
      handleNewMessage(data)
      break
    case 'tg.message.read.update':
      if (data && data.chatId) {
        const chat = chats.value.find(c => c.chatId === data.chatId)
        if (chat && data.unreadCount !== undefined) {
          chat.unreadCount = data.unreadCount
        }
      }
      break
    case 'tg.message.edited':
      break
    case 'tg.message.deleted':
      if (data && data.chatId === currentChatId.value && data.messageIds) {
        currentMessages.value = currentMessages.value.filter(m => !data.messageIds.includes(m.messageId))
      }
      break
    case 'tg.chat.members':
      if (data && data.members) {
        members.value = data.members
      }
      break
    case 'tg.contact.list':
      break
    case 'pong':
    case 'server.ping':
      break
    case 'system.error':
      console.error('WS error:', data)
      break
  }
}

function loadChatList() {
  loadingChats.value = true
  sendWsRequest('tg.chat.list', { tgAccountId: parseInt(accountId.value), limit: 100 })
}

function selectChat(chat) {
  currentChatId.value = chat.chatId
  currentChat.value = chat
  currentMessages.value = []
  oldestMessageId.value = 0
  hasMoreHistory.value = true
  showMembers.value = false
  members.value = []

  loadHistory()

  // Mark as read
  if (chat.unreadCount > 0) {
    sendWsRequest('tg.message.mark-read', {
      tgAccountId: parseInt(accountId.value),
      chatId: chat.chatId,
      lastMessageId: 0
    })
    chat.unreadCount = 0
  }

  // Load members for group chats
  if (chat.type === 'basic_group' || chat.type === 'supergroup') {
    loadMembers(chat.chatId)
  }

  nextTick(() => {
    if (inputRef.value) inputRef.value.focus()
  })
}

function loadHistory(older) {
  if (loadingHistory.value) return
  loadingHistory.value = true

  sendWsRequest('tg.chat.history', {
    tgAccountId: parseInt(accountId.value),
    chatId: currentChatId.value,
    fromMessageId: older ? oldestMessageId.value : 0,
    limit: 50
  })
}

function handleHistoryResponse(messages) {
  loadingHistory.value = false
  if (messages.length === 0) {
    hasMoreHistory.value = false
    return
  }

  if (oldestMessageId.value === 0) {
    // First load - these are the newest messages
    currentMessages.value = messages.sort((a, b) => a.messageId - b.messageId)
    if (messages.length > 0) {
      oldestMessageId.value = messages.reduce((min, m) => m.messageId < min ? m.messageId : min, messages[0].messageId)
    }
    nextTick(() => scrollToBottom())
  } else {
    // Loading older messages
    const sorted = messages.sort((a, b) => a.messageId - b.messageId)
    currentMessages.value = [...sorted, ...currentMessages.value]
    if (messages.length > 0) {
      oldestMessageId.value = sorted[0].messageId
    }
    if (messages.length < 50) {
      hasMoreHistory.value = false
    }
  }
}

function appendMessage(msg) {
  // Avoid duplicates
  if (currentMessages.value.find(m => m.messageId === msg.messageId)) return
  currentMessages.value.push(msg)
  nextTick(() => scrollToBottom())
}

function handleNewMessage(data) {
  if (!data) return

  // Update chat list
  const chatIndex = chats.value.findIndex(c => c.chatId === data.chatId)
  if (chatIndex >= 0) {
    const chat = chats.value[chatIndex]
    chat.lastMessageDate = data.date
    chat.lastMessagePreview = data.textPreview || data.contentType
    if (data.chatId !== currentChatId.value) {
      chat.unreadCount = (chat.unreadCount || 0) + 1
    }
    // Move to top
    chats.value.splice(chatIndex, 1)
    chats.value.unshift(chat)
  } else {
    // New chat not in list yet - add it
    chats.value.unshift({
      chatId: data.chatId,
      title: data.senderName || data.chatId.toString(),
      type: 'private',
      unreadCount: data.chatId !== currentChatId.value ? 1 : 0,
      lastMessageDate: data.date,
      lastMessagePreview: data.textPreview || data.contentType
    })
  }

  // Add to current chat
  if (data.chatId === currentChatId.value && data.tgAccountId === parseInt(accountId.value)) {
    const msg = {
      messageId: data.messageId,
      chatId: data.chatId,
      senderUserId: data.senderUserId,
      senderName: data.senderName,
      outgoing: data.outgoing,
      date: data.date,
      contentType: data.contentType,
      textPreview: data.textPreview,
      media: data.media
    }
    appendMessage(msg)

    // Mark as read
    sendWsRequest('tg.message.mark-read', {
      tgAccountId: parseInt(accountId.value),
      chatId: data.chatId,
      lastMessageId: data.messageId
    })
  }
}

function loadMembers(chatId) {
  sendWsRequest('tg.chat.members', {
    tgAccountId: parseInt(accountId.value),
    chatId: chatId,
    limit: 200
  })
}

function sendMessage() {
  const text = inputText.value.trim()
  if (!text || !currentChatId.value) return

  sendWsRequest('tg.message.send', {
    tgAccountId: parseInt(accountId.value),
    chatId: currentChatId.value,
    text: text
  })

  inputText.value = ''
  nextTick(() => {
    if (inputRef.value) inputRef.value.focus()
  })
}

function scrollToBottom() {
  if (messagesRef.value) {
    messagesRef.value.scrollTop = messagesRef.value.scrollHeight
  }
}

function onMessagesScroll() {
  if (!messagesRef.value || !hasMoreHistory.value || loadingHistory.value) return
  if (messagesRef.value.scrollTop < 100) {
    loadHistory(true)
  }
}

function toggleMembers() {
  showMembers.value = !showMembers.value
  if (showMembers.value && members.value.length === 0 && currentChatId.value) {
    loadMembers(currentChatId.value)
  }
}

function mediaUrl(fileId) {
  return TG_SERVER_BASE + '/api/client/tg/file?tgAccountId=' + accountId.value + '&fileId=' + fileId + '&token=' + clientToken
}

function getAvatarText(name) {
  if (!name) return '?'
  const parts = name.trim().split(/\s+/)
  if (parts.length >= 2) return (parts[0][0] + parts[1][0]).toUpperCase()
  return name[0].toUpperCase()
}

function chatTypeLabel(type) {
  switch (type) {
    case 'private': return '私聊'
    case 'basic_group': return '群组'
    case 'supergroup': return '超级群组'
    case 'channel': return '频道'
    default: return ''
  }
}

function formatTime(timestamp) {
  if (!timestamp) return ''
  const d = new Date(timestamp * 1000)
  const now = new Date()
  const today = new Date(now.getFullYear(), now.getMonth(), now.getDate())
  const msgDate = new Date(d.getFullYear(), d.getMonth(), d.getDate())

  if (msgDate.getTime() === today.getTime()) {
    return d.getHours().toString().padStart(2, '0') + ':' + d.getMinutes().toString().padStart(2, '0')
  }
  const yesterday = new Date(today)
  yesterday.setDate(yesterday.getDate() - 1)
  if (msgDate.getTime() === yesterday.getTime()) {
    return '昨天'
  }
  return (d.getMonth() + 1) + '/' + d.getDate()
}

function formatMessageTime(timestamp) {
  if (!timestamp) return ''
  const d = new Date(timestamp * 1000)
  return d.getHours().toString().padStart(2, '0') + ':' + d.getMinutes().toString().padStart(2, '0')
}

onMounted(async () => {
  accountId.value = route.query.id
  accountPhone.value = route.query.phone || route.query.phoneNum || ''
  accountUsername.value = route.query.username || ''

  if (!accountId.value) {
    statusMessage.value = '缺少账号参数'
    return
  }

  if (initToken()) {
    connectWebSocket()
  }
})

onUnmounted(() => {
  if (ws) { ws.close(); ws = null }
  if (pingTimer) { clearInterval(pingTimer); pingTimer = null }
  if (reconnectTimer) { clearTimeout(reconnectTimer); reconnectTimer = null }
})
</script>

<style scoped>
* {
  margin: 0;
  padding: 0;
  box-sizing: border-box;
}

.tg-app {
  display: flex;
  height: 100vh;
  width: 100vw;
  font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, 'Helvetica Neue', Arial, sans-serif;
  background: #0e1621;
  color: #fff;
  overflow: hidden;
}

/* Sidebar */
.tg-sidebar {
  width: 320px;
  min-width: 320px;
  background: #17212b;
  display: flex;
  flex-direction: column;
  border-right: 1px solid #0e1621;
}

.tg-sidebar-header {
  padding: 8px 12px;
  border-bottom: 1px solid #0e1621;
}

.tg-account-info {
  padding: 8px 4px;
  font-size: 14px;
  font-weight: 600;
  color: #fff;
}

.tg-search-box {
  padding: 4px 0;
}

.tg-search-input {
  width: 100%;
  padding: 8px 12px;
  border: none;
  border-radius: 20px;
  background: #242f3d;
  color: #fff;
  font-size: 14px;
  outline: none;
}

.tg-search-input::placeholder {
  color: #6c7883;
}

.tg-chat-list {
  flex: 1;
  overflow-y: auto;
}

.tg-chat-list::-webkit-scrollbar {
  width: 6px;
}
.tg-chat-list::-webkit-scrollbar-track {
  background: transparent;
}
.tg-chat-list::-webkit-scrollbar-thumb {
  background: #3a4654;
  border-radius: 3px;
}

.tg-chat-item {
  display: flex;
  align-items: center;
  padding: 8px 12px;
  cursor: pointer;
  transition: background 0.15s;
}

.tg-chat-item:hover {
  background: #202b36;
}

.tg-chat-item.active {
  background: #2b5278;
}

.tg-chat-avatar {
  width: 48px;
  height: 48px;
  border-radius: 50%;
  background: #5288c1;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 18px;
  font-weight: 600;
  flex-shrink: 0;
  color: #fff;
}

.tg-chat-info {
  flex: 1;
  min-width: 0;
  margin-left: 12px;
}

.tg-chat-top {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 4px;
}

.tg-chat-name {
  font-size: 15px;
  font-weight: 500;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.tg-chat-time {
  font-size: 12px;
  color: #6c7883;
  flex-shrink: 0;
  margin-left: 8px;
}

.tg-chat-bottom {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.tg-chat-preview {
  font-size: 13px;
  color: #6c7883;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  flex: 1;
}

.tg-chat-badge {
  background: #3390ec;
  color: #fff;
  font-size: 12px;
  padding: 2px 6px;
  border-radius: 10px;
  min-width: 20px;
  text-align: center;
  flex-shrink: 0;
  margin-left: 8px;
}

/* Main Chat Area */
.tg-main {
  flex: 1;
  display: flex;
  flex-direction: column;
  background: #0e1621;
  min-width: 0;
}

.tg-no-chat {
  align-items: center;
  justify-content: center;
}

.tg-no-chat-content {
  color: #6c7883;
  font-size: 16px;
}

.tg-main-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 12px 20px;
  background: #17212b;
  border-bottom: 1px solid #0e1621;
}

.tg-main-header-info {
  display: flex;
  align-items: center;
  gap: 8px;
}

.tg-main-chat-name {
  font-size: 16px;
  font-weight: 600;
}

.tg-main-chat-type {
  font-size: 12px;
  color: #6c7883;
}

.tg-icon-btn {
  background: none;
  border: none;
  color: #6c7883;
  cursor: pointer;
  padding: 6px 10px;
  border-radius: 6px;
  font-size: 18px;
  transition: all 0.15s;
}

.tg-icon-btn:hover, .tg-icon-btn.active {
  background: #242f3d;
  color: #fff;
}

/* Messages */
.tg-messages {
  flex: 1;
  overflow-y: auto;
  padding: 12px 20px;
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.tg-messages::-webkit-scrollbar {
  width: 6px;
}
.tg-messages::-webkit-scrollbar-track {
  background: transparent;
}
.tg-messages::-webkit-scrollbar-thumb {
  background: #3a4654;
  border-radius: 3px;
}

.tg-message {
  display: flex;
  max-width: 65%;
}

.tg-message.outgoing {
  align-self: flex-end;
}

.tg-message.incoming {
  align-self: flex-start;
}

.tg-message-bubble {
  padding: 8px 12px;
  border-radius: 12px;
  max-width: 100%;
  word-wrap: break-word;
}

.tg-message.outgoing .tg-message-bubble {
  background: #2b5278;
  border-bottom-right-radius: 4px;
}

.tg-message.incoming .tg-message-bubble {
  background: #182533;
  border-bottom-left-radius: 4px;
}

.tg-message-sender {
  font-size: 13px;
  font-weight: 600;
  color: #5288c1;
  margin-bottom: 2px;
}

.tg-message-text {
  font-size: 14px;
  line-height: 1.4;
  white-space: pre-wrap;
}

.tg-message-time {
  font-size: 11px;
  color: #6c7883;
  text-align: right;
  margin-top: 2px;
}

.tg-message-photo {
  max-width: 300px;
  max-height: 300px;
  border-radius: 8px;
  margin-bottom: 4px;
}

.tg-message-sticker {
  font-size: 80px;
}

.tg-sticker-img {
  max-width: 200px;
  max-height: 200px;
}

.tg-message-file {
  color: #5288c1;
  font-size: 13px;
}

.tg-message-media {
  margin-bottom: 4px;
}

/* Input Area */
.tg-input-area {
  display: flex;
  align-items: center;
  padding: 8px 16px;
  background: #17212b;
  border-top: 1px solid #0e1621;
  gap: 8px;
}

.tg-input-text {
  flex: 1;
  padding: 10px 14px;
  border: none;
  border-radius: 20px;
  background: #242f3d;
  color: #fff;
  font-size: 14px;
  outline: none;
  resize: none;
  min-height: 40px;
  max-height: 120px;
  font-family: inherit;
}

.tg-input-text::placeholder {
  color: #6c7883;
}

.tg-send-btn {
  background: #3390ec;
  color: #fff;
  border: none;
  border-radius: 50%;
  width: 40px;
  height: 40px;
  cursor: pointer;
  font-size: 14px;
  font-weight: 600;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
  transition: background 0.15s;
}

.tg-send-btn:hover {
  background: #2b7fd4;
}

.tg-send-btn:disabled {
  background: #3a4654;
  cursor: not-allowed;
}

/* Members Sidebar */
.tg-members {
  width: 280px;
  min-width: 280px;
  background: #17212b;
  border-left: 1px solid #0e1621;
  display: flex;
  flex-direction: column;
}

.tg-members-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 14px 16px;
  border-bottom: 1px solid #0e1621;
  font-size: 15px;
  font-weight: 600;
}

.tg-close-btn {
  background: none;
  border: none;
  color: #6c7883;
  cursor: pointer;
  font-size: 16px;
  padding: 4px 8px;
  border-radius: 4px;
}

.tg-close-btn:hover {
  background: #242f3d;
  color: #fff;
}

.tg-members-list {
  flex: 1;
  overflow-y: auto;
}

.tg-member-item {
  display: flex;
  align-items: center;
  padding: 8px 16px;
  cursor: pointer;
  transition: background 0.15s;
}

.tg-member-item:hover {
  background: #202b36;
}

.tg-member-avatar {
  width: 36px;
  height: 36px;
  border-radius: 50%;
  background: #5288c1;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 14px;
  font-weight: 600;
  flex-shrink: 0;
}

.tg-member-info {
  margin-left: 10px;
  display: flex;
  flex-direction: column;
}

.tg-member-name {
  font-size: 14px;
}

.tg-member-username {
  font-size: 12px;
  color: #6c7883;
}

/* Status */
.tg-status-bar {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  background: #3390ec;
  color: #fff;
  text-align: center;
  padding: 6px;
  font-size: 13px;
  z-index: 1000;
}

/* Utilities */
.tg-empty, .tg-empty-messages {
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 40px;
  color: #6c7883;
  font-size: 14px;
}

.tg-loading, .tg-loading-more {
  text-align: center;
  padding: 16px;
  color: #6c7883;
  font-size: 13px;
}
</style>
