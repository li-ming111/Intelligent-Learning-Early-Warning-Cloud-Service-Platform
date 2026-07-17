<template>
  <view class="page">
    <view class="header">
      <text class="title">通知管理</text>
      <text class="sub">发送消息与模板管理</text>
    </view>

    <!-- 通知模板 -->
    <view class="card">
      <text class="card-title">通知模板</text>
      <view class="tpl-list">
        <view v-for="t in templates" :key="t.id" class="tpl-row" @tap="useTemplate(t)">
          <view class="tpl-left">
            <text class="tpl-name">{{ t.name }}</text>
            <text class="tpl-preview">{{ t.content }}</text>
          </view>
          <text class="tpl-use">使用</text>
        </view>
      </view>
    </view>

    <!-- 编辑发送 -->
    <view class="card">
      <text class="card-title">发送通知</text>
      <textarea v-model="msg" class="textarea" placeholder="输入通知内容..." :maxlength="300" auto-height />
      <view class="textarea-foot">
        <text class="cnt-text">{{ msg.length }}/300</text>
        <view class="send-scope">
          <text class="scope-label">发送范围:</text>
          <picker :value="scopeIdx" :range="scopeOptions" @change="scopeIdx = Number($event.detail.value)">
            <text class="scope-val">{{ scopeOptions[scopeIdx] }}</text>
          </picker>
        </view>
      </view>
      <button class="send-btn" :disabled="!msg.trim()" @tap="doSend">
        <text class="send-icon">📣</text>
        <text>立即发送</text>
      </button>
    </view>

    <!-- 发送历史 -->
    <view class="card">
      <view class="card-head">
        <text class="card-title">发送历史</text>
      </view>
      <view v-if="history.length" class="hist-list">
        <view v-for="h in history" :key="h.id" class="hist-row">
          <view class="hist-info">
            <text class="hist-content">{{ h.content }}</text>
            <text class="hist-meta">{{ h.scope || '全班' }} · {{ fmtTime(h.time || h.createdAt) }}</text>
          </view>
          <text class="hist-status" :style="{color: h.sent ? '#16a34a' : '#f59e0b'}">{{ h.sent ? '已发送' : '待发送' }}</text>
        </view>
      </view>
      <view v-else class="empty-sm">暂无发送记录</view>
    </view>
  </view>
</template>

<script>
import { counselorAPI } from '@/services/api.js'

export default {
  data() {
    return {
      msg: '',
      scopeIdx: 0,
      scopeOptions: ['全班学生', '预警学生', '帮扶学生'],
      templates: [
        { id: 1, name: '学业预警通知', content: '尊敬的同学，您的学业存在预警风险，请尽快联系辅导员了解详情并制定提升计划。' },
        { id: 2, name: '新学期提醒', content: '新学期已开始，请同学们做好学习规划，按时出勤，认真完成作业。如有困难请及时联系辅导员。' },
        { id: 3, name: '帮扶计划提醒', content: '您有未完成的帮扶计划任务，请尽快登录系统查看并完成相关要求。如有疑问请联系辅导员。' },
        { id: 4, name: '成绩查询通知', content: '本学期成绩已公布，请同学们登录系统查看。如有疑问可在7个工作日内提出申诉。' }
      ],
      history: []
    }
  },
  onShow() { this.loadHistory() },
  methods: {
    useTemplate(t) { this.msg = t.content },
    async doSend() {
      try {
        const uid = uni.getStorageSync('counselorId') || uni.getStorageSync('userId')
        await counselorAPI.notifyStudents({ counselorId: Number(uid), message: this.msg, scope: this.scopeOptions[this.scopeIdx] })
        this.history.unshift({ id: Date.now(), content: this.msg.substring(0, 40) + '...', scope: this.scopeOptions[this.scopeIdx], time: new Date().toISOString(), sent: true })
        uni.showToast({ title: '发送成功', icon: 'success' })
        this.msg = ''
      } catch (e) { uni.showToast({ title: '发送失败', icon: 'none' }) }
    },
    async loadHistory() {
      try {
        this.history = [
          { id: 1, content: '请同学们注意期末考试成绩已公布...', scope: '全班学生', time: '2026-07-05T10:00:00', sent: true },
          { id: 2, content: '请以下同学尽快联系辅导员处理预警...', scope: '预警学生', time: '2026-07-02T15:30:00', sent: true }
        ]
      } catch (e) {}
    },
    fmtTime(d) {
      if (!d) return ''
      const t = new Date(d)
      return `${t.getMonth()+1}/${t.getDate()} ${String(t.getHours()).padStart(2,'0')}:${String(t.getMinutes()).padStart(2,'0')}`
    }
  }
}
</script>

<style scoped>
.page { padding: 20rpx; background: #f0f2f8; min-height: 100vh; }
.header { margin-bottom: 16rpx; }
.title { font-size: 36rpx; font-weight: 800; color: #1e293b; display: block; }
.sub { font-size: 24rpx; color: #94a3b8; }

.card { background: #fff; border-radius: 16rpx; padding: 24rpx; margin-bottom: 16rpx; }
.card-title { font-size: 28rpx; font-weight: 700; color: #1e293b; display: block; margin-bottom: 16rpx; }
.card-head { display: flex; justify-content: space-between; align-items: center; margin-bottom: 16rpx; }

.tpl-list { display: flex; flex-direction: column; }
.tpl-row { display: flex; align-items: center; gap: 12rpx; padding: 18rpx 0; border-bottom: 1rpx solid #f1f5f9; }
.tpl-row:last-child { border-bottom: none; }
.tpl-left { flex: 1; min-width: 0; }
.tpl-name { font-size: 26rpx; font-weight: 600; color: #1e293b; display: block; }
.tpl-preview { font-size: 22rpx; color: #94a3b8; display: block; overflow: hidden; text-overflow: ellipsis; white-space: nowrap; margin-top: 4rpx; }
.tpl-use { font-size: 24rpx; color: #2563eb; font-weight: 500; flex-shrink: 0; padding: 8rpx 16rpx; background: #eff6ff; border-radius: 10rpx; }

.textarea { width: 100%; min-height: 160rpx; background: #f8fafc; border-radius: 12rpx; padding: 20rpx; font-size: 26rpx; box-sizing: border-box; }
.textarea-foot { display: flex; justify-content: space-between; align-items: center; margin: 12rpx 0 20rpx; }
.cnt-text { font-size: 22rpx; color: #94a3b8; }
.send-scope { display: flex; align-items: center; gap: 8rpx; }
.scope-label { font-size: 22rpx; color: #94a3b8; }
.scope-val { font-size: 24rpx; color: #2563eb; font-weight: 500; }

.send-btn { width: 100%; height: 80rpx; background: linear-gradient(135deg, #2563eb, #3b82f6); color: #fff; border: none; border-radius: 14rpx; font-size: 28rpx; font-weight: 600; display: flex; align-items: center; justify-content: center; gap: 8rpx; }
.send-btn[disabled] { opacity: 0.5; }
.send-icon { font-size: 28rpx; }

.hist-list { display: flex; flex-direction: column; gap: 12rpx; }
.hist-row { display: flex; align-items: center; gap: 12rpx; padding: 16rpx 0; border-bottom: 1rpx solid #f1f5f9; }
.hist-row:last-child { border-bottom: none; }
.hist-info { flex: 1; min-width: 0; }
.hist-content { font-size: 24rpx; color: #1e293b; display: block; overflow: hidden; text-overflow: ellipsis; white-space: nowrap; }
.hist-meta { font-size: 20rpx; color: #94a3b8; display: block; margin-top: 4rpx; }
.hist-status { font-size: 22rpx; font-weight: 500; flex-shrink: 0; }

.empty-sm { text-align: center; padding: 36rpx; color: #94a3b8; font-size: 24rpx; }
</style>
