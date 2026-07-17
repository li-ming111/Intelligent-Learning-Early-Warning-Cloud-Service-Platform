<template>
  <view class="page">
    <view class="hero">
      <view class="hero-row">
        <view class="hero-left">
          <view class="hero-av">辅</view>
          <view>
            <text class="hero-name">{{ name }}</text>
            <text class="hero-role">辅导员 · 工作台</text>
          </view>
        </view>
        <view class="hero-logout" @tap="handleLogout">
          <text class="logout-icon">⇤</text>
        </view>
      </view>
    </view>

    <!-- 统计 -->
    <view class="section-pad">
      <view class="stats-grid">
        <view class="st-card" @tap="goTo('/pages/counselor/students')">
          <text class="st-num">{{ stats.totalStudents }}</text>
          <text class="st-lbl">负责学生</text>
        </view>
        <view class="st-card" @tap="goTo('/pages/counselor/warnings')">
          <text class="st-num red">{{ stats.totalWarnings }}</text>
          <text class="st-lbl">预警学生</text>
        </view>
        <view class="st-card" @tap="goTo('/pages/counselor/assistance')">
          <text class="st-num green">{{ stats.assistancePlans }}</text>
          <text class="st-lbl">帮扶计划</text>
        </view>
        <view class="st-card">
          <text class="st-num">{{ stats.totalClasses }}</text>
          <text class="st-lbl">管理班级</text>
        </view>
      </view>
    </view>

    <!-- 快捷入口 -->
    <view class="section">
      <text class="section-title">学生工作</text>
      <view class="entry-grid">
        <view class="entry-card" @tap="goTo('/pages/counselor/students')">
          <view class="entry-badge badge-green">👥</view>
          <text class="entry-name">学生管理</text>
          <text class="entry-desc">班级学生列表</text>
        </view>
        <view class="entry-card" @tap="goTo('/pages/counselor/warnings')">
          <view class="entry-badge badge-red">⚠</view>
          <text class="entry-name">预警处理</text>
          <text class="entry-desc">学业预警管理</text>
        </view>
        <view class="entry-card" @tap="goTo('/pages/counselor/assistance')">
          <view class="entry-badge badge-blue">🎯</view>
          <text class="entry-name">帮扶计划</text>
          <text class="entry-desc">制定与跟踪</text>
        </view>
        <view class="entry-card" @tap="goTo('/pages/counselor/analytics/index')">
          <view class="entry-badge badge-purple">📈</view>
          <text class="entry-name">数据分析</text>
          <text class="entry-desc">趋势与统计</text>
        </view>
        <view class="entry-card" @tap="goTo('/pages/counselor/notifications/index')">
          <view class="entry-badge badge-orange">📣</view>
          <text class="entry-name">发送通知</text>
          <text class="entry-desc">消息与模板</text>
        </view>
      </view>
    </view>

    <!-- 待办任务 -->
    <view class="section">
      <text class="section-title">待办任务</text>
      <view v-if="tasks.length" class="task-list">
        <view v-for="t in tasks" :key="t.id" class="task-card">
          <view class="task-dot" :class="t.priority"></view>
          <text class="task-text">{{ t.title }}</text>
          <text class="task-tag" :class="t.priority">{{ t.priorityName }}</text>
        </view>
      </view>
      <view v-else class="empty-box">
        <text class="empty-icon">✅</text>
        <text class="empty-text">暂无待办任务</text>
      </view>
    </view>

    <counselor-tab-bar current="pages/counselor/dashboard" />
  </view>
</template>

<script>
import { counselorAPI } from '@/services/api.js'

export default {
  data() {
    return {
      name: '辅导员',
      stats: { totalStudents: 0, totalWarnings: 0, assistancePlans: 0, totalClasses: 0 },
      tasks: []
    }
  },
  onShow() { this.loadData() },
  methods: {
    async loadData() {
      try {
        const uid = uni.getStorageSync('counselorId') || uni.getStorageSync('userId'); if (!uid) return
        const dash = await counselorAPI.getDashboard(uid)
        if (dash) {
          this.name = dash.counselorName || '辅导员'
          this.stats = {
            totalStudents: dash.totalStudents || 0,
            totalWarnings: dash.totalWarnings || 0,
            assistancePlans: dash.assistancePlans || dash.totalAssistance || 0,
            totalClasses: dash.totalClasses || 0
          }
        }
        try {
          const res = await counselorAPI.getPendingTasks(uid)
          const arr = Array.isArray(res) ? res : (res?.data || [])
          this.tasks = arr.map(t => ({
            id: t.id, title: t.title || t.name || '待处理事项',
            priority: t.priority || 'normal',
            priorityName: t.priority === 'high' ? '紧急' : t.priority === 'medium' ? '中等' : '普通'
          }))
        } catch (e) { this.tasks = [] }
      } catch (e) { console.error(e) }
    },
    goTo(url) { uni.navigateTo({ url }) },
    handleLogout() {
      ['token','userId','role','userName','counselorId'].forEach(k => uni.removeStorageSync(k))
      uni.reLaunch({ url: '/pages/auth/login' })
    }
  }
}
</script>

<style scoped>
.page { min-height: 100vh; background: #f0f2f8; padding-bottom: 120rpx; }

.hero { background: linear-gradient(135deg, #2563eb 0%, #3b82f6 100%); padding: 44rpx 28rpx 40rpx 32rpx; }
.hero-row { display: flex; justify-content: space-between; align-items: center; }
.hero-left { display: flex; align-items: center; gap: 16rpx; }
.hero-av { width: 72rpx; height: 72rpx; border-radius: 50%; background: rgba(255,255,255,0.2); display: flex; align-items: center; justify-content: center; font-size: 30rpx; font-weight: 700; color: #fff; flex-shrink: 0; }
.hero-name { font-size: 32rpx; font-weight: 700; color: #fff; display: block; }
.hero-role { font-size: 22rpx; color: rgba(255,255,255,0.65); margin-top: 4rpx; display: block; }
.hero-logout { width: 60rpx; height: 60rpx; border-radius: 50%; background: rgba(255,255,255,0.15); display: flex; align-items: center; justify-content: center; }
.logout-icon { font-size: 40rpx; color: #fff; transform: rotate(180deg); }

.section-pad { padding: 0 20rpx; margin-top: -16rpx; position: relative; z-index: 2; }
.section { padding: 0 20rpx; margin-bottom: 24rpx; }
.section-title { font-size: 28rpx; font-weight: 700; color: #1e293b; display: block; margin-bottom: 16rpx; }

.stats-grid { display: grid; grid-template-columns: 1fr 1fr 1fr 1fr; gap: 12rpx; }
.st-card { background: #fff; border-radius: 16rpx; padding: 20rpx 8rpx; text-align: center; box-shadow: 0 2rpx 12rpx rgba(0,0,0,0.04); }
.st-num { font-size: 28rpx; font-weight: 800; color: #1e293b; display: block; }
.st-num.red { color: #ef4444; } .st-num.green { color: #16a34a; }
.st-lbl { font-size: 20rpx; color: #94a3b8; margin-top: 4rpx; display: block; }

.entry-grid { display: grid; grid-template-columns: 1fr 1fr 1fr; gap: 14rpx; }
.entry-card { background: #fff; border-radius: 16rpx; padding: 24rpx 14rpx; text-align: center; box-shadow: 0 2rpx 8rpx rgba(0,0,0,0.03); }
.entry-badge { width: 56rpx; height: 56rpx; border-radius: 14rpx; display: flex; align-items: center; justify-content: center; margin: 0 auto 12rpx; font-size: 26rpx; }
.badge-green { background: #dcfce7; } .badge-red { background: #fee2e2; } .badge-blue { background: #e0e7ff; } .badge-purple { background: #ede9fe; } .badge-orange { background: #fef3c7; }
.entry-name { font-size: 26rpx; font-weight: 600; color: #1e293b; display: block; }
.entry-desc { font-size: 20rpx; color: #94a3b8; margin-top: 4rpx; display: block; }

.task-list { display: flex; flex-direction: column; gap: 10rpx; }
.task-card { background: #fff; border-radius: 12rpx; padding: 18rpx 20rpx; display: flex; align-items: center; gap: 12rpx; box-shadow: 0 1rpx 4rpx rgba(0,0,0,0.02); }
.task-dot { width: 8rpx; height: 8rpx; border-radius: 50%; flex-shrink: 0; background: #94a3b8; }
.task-dot.high { background: #ef4444; } .task-dot.medium { background: #f59e0b; }
.task-text { flex: 1; font-size: 26rpx; color: #1e293b; }
.task-tag { font-size: 20rpx; padding: 3rpx 14rpx; border-radius: 20rpx; font-weight: 500; flex-shrink: 0; background: #f1f5f9; color: #64748b; }
.task-tag.high { background: #fef2f2; color: #ef4444; } .task-tag.medium { background: #fffbeb; color: #d97706; }

.empty-box { background: #fff; border-radius: 14rpx; padding: 48rpx; text-align: center; }
.empty-icon { font-size: 48rpx; display: block; margin-bottom: 12rpx; }
.empty-text { font-size: 24rpx; color: #94a3b8; }
</style>
