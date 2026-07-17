<template>
  <view class="page">
    <view class="blue-bar">
      <text class="bar-title">消息中心</text>
      <text class="bar-sub">系统通知与提醒</text>
    </view>

    <!-- 操作栏 -->
    <view class="actions" v-if="list.length > 0">
      <text class="act-btn" @tap="markAllRead">全部已读</text>
      <text class="act-btn del" @tap="clearAll">清空</text>
    </view>

    <!-- 消息列表 -->
    <view class="card" v-if="list.length > 0">
      <view
        v-for="m in list"
        :key="m.id"
        :class="['msg-item', { unread: !m.read }]"
        @tap="readMsg(m)"
      >
        <view class="msg-left">
          <view class="msg-dot" v-if="!m.read"></view>
          <view class="msg-info">
            <text class="msg-title">{{ m.title }}</text>
            <text class="msg-content">{{ m.content }}</text>
          </view>
        </view>
        <view class="msg-right">
          <text class="msg-time">{{ formatTime(m.createTime || m.createdAt) }}</text>
          <text class="msg-del" @tap.stop="delMsg(m)">删除</text>
        </view>
      </view>
    </view>

    <!-- 空状态 -->
    <view v-else class="card empty">
      <text class="empty-icon">📭</text>
      <text class="empty-text">暂无消息</text>
    </view>
  </view>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { studentAPI } from '@/services/api.js'

const list = ref([])

const load = async () => {
  const uid = uni.getStorageSync('userId')
  if (!uid) return
  try {
    const res = await studentAPI.getMessages(uid)
    const arr = Array.isArray(res) ? res : (res?.data || [])
    list.value = arr.map(m => ({
      ...m,
      read: m.isRead === 1 || m.read === true || m.status === 1
    }))
  } catch (e) {
    // 无后端时用本地模拟
    const saved = uni.getStorageSync('local_messages')
    if (saved) list.value = JSON.parse(saved)
  }
}

const readMsg = (m) => {
  m.read = true
  saveLocals()
}

const markAllRead = () => {
  list.value.forEach(m => m.read = true)
  saveLocals()
}

const delMsg = (m) => {
  list.value = list.value.filter(i => i.id !== m.id)
  saveLocals()
}

const clearAll = () => {
  uni.showModal({
    title: '提示',
    content: '确定清空所有消息？',
    success: (r) => {
      if (r.confirm) { list.value = []; saveLocals() }
    }
  })
}

const saveLocals = () => {
  uni.setStorageSync('local_messages', JSON.stringify(list.value.slice(0, 50)))
}

const formatTime = (t) => {
  if (!t) return ''
  const d = new Date(t)
  const now = new Date()
  const diff = now - d
  if (diff < 3600000) return Math.floor(diff / 60000) + '分钟前'
  if (diff < 86400000) return Math.floor(diff / 3600000) + '小时前'
  return d.toLocaleDateString('zh-CN')
}

onMounted(() => { load() })
</script>

<style scoped>
.page { min-height: 100vh; padding: 0 24rpx 24rpx; }

.blue-bar {
  background: linear-gradient(135deg, #3b5ce8 0%, #5b7cf0 100%);
  margin: 0 -24rpx 20rpx;
  padding: 44rpx 28rpx 36rpx;
}
.bar-title { display: block; font-size: 34rpx; font-weight: 700; color: #fff; }
.bar-sub { display: block; font-size: 22rpx; color: rgba(255,255,255,0.7); margin-top: 6rpx; }

.actions {
  display: flex; justify-content: flex-end; gap: 32rpx;
  padding: 0 0 16rpx;
}
.act-btn { font-size: 24rpx; color: #3b5ce8; font-weight: 500; }
.act-btn.del { color: #ef4444; }

.card { background: #fff; border-radius: 14rpx; overflow: hidden; }

.msg-item {
  display: flex; justify-content: space-between; align-items: center;
  padding: 24rpx 20rpx; border-bottom: 1rpx solid #f5f5f7;
  transition: background .15s;
}
.msg-item:last-child { border-bottom: none; }
.msg-item.unread { background: #eef1ff; }

.msg-left { display: flex; align-items: flex-start; gap: 12rpx; flex: 1; overflow: hidden; }
.msg-dot { width: 12rpx; height: 12rpx; border-radius: 50%; background: #3b5ce8; margin-top: 10rpx; flex-shrink: 0; }
.msg-info { flex: 1; overflow: hidden; }
.msg-title { display: block; font-size: 28rpx; font-weight: 600; color: #1d1d1f; }
.msg-content { display: block; font-size: 24rpx; color: #86868b; margin-top: 6rpx; overflow: hidden; text-overflow: ellipsis; white-space: nowrap; }

.msg-right { display: flex; flex-direction: column; align-items: flex-end; gap: 8rpx; flex-shrink: 0; margin-left: 16rpx; }
.msg-time { font-size: 20rpx; color: #aeaeb2; }
.msg-del { font-size: 20rpx; color: #ef4444; padding: 4rpx 8rpx; }

.empty { padding: 100rpx 24rpx; text-align: center; }
.empty-icon { font-size: 60rpx; display: block; margin-bottom: 16rpx; }
.empty-text { font-size: 26rpx; color: #aeaeb2; }
</style>
