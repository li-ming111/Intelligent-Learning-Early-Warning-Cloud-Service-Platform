<template>
  <view class="page">
    <view class="header">
      <text class="title">消息中心</text>
      <view class="header-acts" v-if="messages.length">
        <text class="act-text" @tap="markAllRead">全部已读</text>
        <text class="act-text danger" @tap="clearAll">清空</text>
      </view>
    </view>

    <scroll-view v-if="messages.length" scroll-y class="list">
      <view v-for="m in messages" :key="m.id" class="msg-card" :class="{unread: !m.read}" @tap="readMsg(m)">
        <view class="msg-left">
          <view class="msg-av" :style="{background: m.read ? '#f1f5f9' : '#e0e7ff'}">
            <text class="ma-icon">{{ m.read ? '✉' : '🔔' }}</text>
          </view>
          <view class="msg-body">
            <view class="msg-top">
              <text class="msg-title">{{ m.title || '系统通知' }}</text>
              <text class="msg-time">{{ fmtTime(m.createdAt || m.createTime) }}</text>
            </view>
            <text class="msg-preview">{{ m.content || '暂无内容' }}</text>
          </view>
        </view>
        <view v-if="!m.read" class="msg-dot"></view>
      </view>
    </scroll-view>
    <view v-else class="empty">
      <text class="empty-icon">💬</text>
      <text class="empty-text">暂无消息</text>
    </view>

    <!-- 消息详情弹窗 -->
    <view v-if="selectedMsg" class="modal-mask" @tap="selectedMsg = null">
      <view class="modal-card" @tap.stop>
        <text class="modal-title">{{ selectedMsg.title || '系统通知' }}</text>
        <text class="modal-time">{{ fmtFullTime(selectedMsg.createdAt || selectedMsg.createTime) }}</text>
        <view class="modal-divide"></view>
        <text class="modal-content">{{ selectedMsg.content || '暂无内容' }}</text>
        <button class="modal-btn" @tap="selectedMsg = null">知道了</button>
      </view>
    </view>
  </view>
</template>

<script>
import { apiClient } from '@/services/api.js'

export default {
  data() { return { messages: [], selectedMsg: null } },
  onShow() { this.loadData() },
  methods: {
    async loadData() {
      try {
        this.messages = [
          { id: 1, title: '成绩录入提醒', content: '本学期成绩录入截止日期为7月15日，请尽快完成成绩录入工作。', createdAt: '2026-07-05T09:00:00', read: false },
          { id: 2, title: '系统升级通知', content: '系统将于本周六凌晨进行维护升级，届时可能无法登录，请提前做好准备。', createdAt: '2026-07-03T14:30:00', read: false },
          { id: 3, title: '教学检查通知', content: '下周一将进行教学材料检查，请准备好教案、课件和成绩册。', createdAt: '2026-07-01T08:00:00', read: true },
          { id: 4, title: '学期总结提交', content: '请于学期结束前提交教学工作总结报告。', createdAt: '2026-06-28T16:00:00', read: true }
        ]
      } catch (e) {}
    },
    readMsg(m) {
      if (!m.read) {
        m.read = true
        this.$forceUpdate()
      }
      this.selectedMsg = m
    },
    markAllRead() {
      this.messages.forEach(m => { m.read = true })
      this.$forceUpdate()
    },
    clearAll() {
      uni.showModal({
        title: '确认清空',
        content: '清空后无法恢复，确认清空所有消息？',
        success: (r) => {
          if (r.confirm) { this.messages = [] }
        }
      })
    },
    fmtTime(d) {
      if (!d) return ''
      const now = new Date(); const t = new Date(d)
      const diff = now - t
      if (diff < 86400000) return t.toTimeString().substring(0, 5)
      if (diff < 604800000) {
        const days = ['日','一','二','三','四','五','六']
        return `周${days[t.getDay()]}`
      }
      return `${t.getMonth() + 1}/${t.getDate()}`
    },
    fmtFullTime(d) {
      if (!d) return ''
      const t = new Date(d)
      return `${t.getFullYear()}-${String(t.getMonth()+1).padStart(2,'0')}-${String(t.getDate()).padStart(2,'0')} ${String(t.getHours()).padStart(2,'0')}:${String(t.getMinutes()).padStart(2,'0')}`
    }
  }
}
</script>

<style scoped>
.page { padding: 20rpx; background: #f0f2f8; min-height: 100vh; }
.header { display: flex; justify-content: space-between; align-items: flex-end; margin-bottom: 18rpx; }
.title { font-size: 36rpx; font-weight: 800; color: #1e293b; }
.header-acts { display: flex; gap: 24rpx; }
.act-text { font-size: 24rpx; color: #2563eb; font-weight: 500; }
.act-text.danger { color: #ef4444; }

.list { max-height: calc(100vh - 120rpx); }
.msg-card { background: #fff; border-radius: 14rpx; padding: 20rpx; margin-bottom: 10rpx; display: flex; align-items: center; justify-content: space-between; }
.msg-card.unread { background: #f8faff; border-left: 4rpx solid #2563eb; }
.msg-left { display: flex; align-items: flex-start; gap: 14rpx; flex: 1; min-width: 0; }
.msg-av { width: 64rpx; height: 64rpx; border-radius: 14rpx; display: flex; align-items: center; justify-content: center; flex-shrink: 0; }
.ma-icon { font-size: 30rpx; }
.msg-body { flex: 1; min-width: 0; }
.msg-top { display: flex; justify-content: space-between; align-items: baseline; margin-bottom: 6rpx; }
.msg-title { font-size: 26rpx; font-weight: 600; color: #1e293b; }
.msg-time { font-size: 20rpx; color: #94a3b8; flex-shrink: 0; margin-left: 8rpx; }
.msg-preview { font-size: 22rpx; color: #64748b; overflow: hidden; text-overflow: ellipsis; white-space: nowrap; display: block; }
.msg-dot { width: 12rpx; height: 12rpx; border-radius: 50%; background: #2563eb; flex-shrink: 0; margin-left: 12rpx; }

.empty { text-align: center; padding: 120rpx 0; }
.empty-icon { font-size: 64rpx; display: block; margin-bottom: 16rpx; }
.empty-text { font-size: 26rpx; color: #94a3b8; }

.modal-mask { position: fixed; inset: 0; background: rgba(0,0,0,0.5); z-index: 999; display: flex; align-items: center; justify-content: center; padding: 40rpx; }
.modal-card { background: #fff; border-radius: 20rpx; padding: 36rpx; width: 100%; max-width: 600rpx; }
.modal-title { font-size: 32rpx; font-weight: 700; color: #1e293b; display: block; }
.modal-time { font-size: 22rpx; color: #94a3b8; display: block; margin-top: 8rpx; }
.modal-divide { height: 1rpx; background: #e2e8f0; margin: 20rpx 0; }
.modal-content { font-size: 28rpx; color: #475569; line-height: 1.6; display: block; }
.modal-btn { margin-top: 28rpx; width: 100%; height: 80rpx; background: linear-gradient(135deg, #2563eb, #4f46e5); color: #fff; border: none; border-radius: 14rpx; font-size: 28rpx; font-weight: 600; display: flex; align-items: center; justify-content: center; }
</style>
