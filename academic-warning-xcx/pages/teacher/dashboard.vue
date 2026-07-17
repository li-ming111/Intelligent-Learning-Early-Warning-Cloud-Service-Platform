<template>
  <view class="page">
    <!-- Hero -->
    <view class="hero">
      <view class="hero-row">
        <view class="hero-left">
          <view class="hero-av">师</view>
          <view>
            <text class="hero-name">{{ teacherName }}</text>
            <text class="hero-role">教师 · 欢迎回来</text>
          </view>
        </view>
        <view class="hero-logout" @tap="handleLogout">
          <text class="logout-icon">⇤</text>
        </view>
      </view>
    </view>

    <!-- 统计卡片 -->
    <view class="section-pad">
      <view class="stats-row">
        <view class="stat-card">
          <view class="stat-icon sc-blue">📚</view>
          <text class="stat-num">{{ stats.totalCourses }}</text>
          <text class="stat-label">授课课程</text>
        </view>
        <view class="stat-card">
          <view class="stat-icon sc-green">👨‍🎓</view>
          <text class="stat-num">{{ stats.totalStudents }}</text>
          <text class="stat-label">学生人数</text>
        </view>
        <view class="stat-card">
          <view class="stat-icon sc-red">⚠</view>
          <text class="stat-num red">{{ stats.totalWarnings }}</text>
          <text class="stat-label">预警学生</text>
        </view>
        <view class="stat-card">
          <view class="stat-icon sc-purple">📊</view>
          <text class="stat-num">{{ stats.avgScore || '--' }}</text>
          <text class="stat-label">平均分</text>
        </view>
      </view>
    </view>

    <!-- 快捷入口 -->
    <view class="section">
      <text class="section-title">教学管理</text>
      <view class="entry-grid">
        <view class="entry-card" @tap="goTo('/pages/teacher/scores')">
          <view class="entry-badge badge-blue"><text class="eb-icon">✏</text></view>
          <text class="entry-name">成绩管理</text>
          <text class="entry-desc">录入与修改成绩</text>
        </view>
        <view class="entry-card" @tap="goTo('/pages/teacher/students')">
          <view class="entry-badge badge-green"><text class="eb-icon">👥</text></view>
          <text class="entry-name">学生管理</text>
          <text class="entry-desc">查看班级学生</text>
        </view>
        <view class="entry-card" @tap="goTo('/pages/teacher/courses')">
          <view class="entry-badge badge-orange"><text class="eb-icon">📖</text></view>
          <text class="entry-name">课程管理</text>
          <text class="entry-desc">我的授课课程</text>
        </view>
        <view class="entry-card" @tap="goTo('/pages/teacher/warnings')">
          <view class="entry-badge badge-red"><text class="eb-icon">🔔</text></view>
          <text class="entry-name">预警处理</text>
          <text class="entry-desc">学生学业预警</text>
        </view>
        <view class="entry-card" @tap="goTo('/pages/teacher/analysis/index')">
          <view class="entry-badge badge-purple"><text class="eb-icon">📈</text></view>
          <text class="entry-name">数据分析</text>
          <text class="entry-desc">成绩与趋势</text>
        </view>
        <view class="entry-card" @tap="goTo('/pages/teacher/messages/index')">
          <view class="entry-badge badge-cyan"><text class="eb-icon">💬</text></view>
          <text class="entry-name">消息中心</text>
          <text class="entry-desc">查看通知消息</text>
        </view>
      </view>
    </view>

    <!-- 最近预警 -->
    <view class="section">
      <view class="section-hd">
        <text class="section-title">最近预警</text>
        <text class="section-more" @tap="goTo('/pages/teacher/warnings')">查看全部 →</text>
      </view>
      <view v-if="recentWarnings.length" class="warn-list">
        <view v-for="w in recentWarnings" :key="w.id" class="warn-card" :style="{borderLeftColor:w.color}">
          <view class="warn-status" :style="{background:w.bg,color:w.color}">{{ w.levelText }}</view>
          <view class="warn-body">
            <text class="warn-student">{{ w.studentName }}</text>
            <text class="warn-desc">{{ w.description || '学业预警' }}</text>
          </view>
          <text class="warn-date">{{ w.date }}</text>
        </view>
      </view>
      <view v-else class="empty-box">
        <text class="empty-icon">✅</text>
        <text class="empty-text">暂无预警记录</text>
      </view>
    </view>

    <teacher-tab-bar current="pages/teacher/dashboard" />
  </view>
</template>

<script>
import { teacherAPI } from '@/services/api.js'

export default {
  data() {
    return {
      teacherName: '教师',
      stats: { totalCourses: 0, totalStudents: 0, totalWarnings: 0, avgScore: '--' },
      recentWarnings: []
    }
  },
  onShow() { this.loadData() },
  methods: {
    async loadData() {
      try {
        const uid = uni.getStorageSync('teacherId') || uni.getStorageSync('userId'); if (!uid) return
        const dash = await teacherAPI.getDashboard(uid)
        if (dash) {
          this.teacherName = dash.teacherName || '教师'
          this.stats.totalCourses = dash.totalCourses || 0
          this.stats.totalStudents = dash.totalStudents || 0
        }
        const warns = await teacherAPI.getWarnings(uid)
        const arr = Array.isArray(warns) ? warns : (warns?.data || [])
        this.stats.totalWarnings = arr.length
        this.recentWarnings = arr.slice(0, 3).map(w => {
          const wl = w.warningLevel
          const lv = wl === '严重' || wl >= 3 ? 3 : wl === '中度' || wl === 2 ? 2 : 1
          return {
            id: w.id,
            studentName: w.studentName || '未知',
            description: w.description || w.title || '',
            levelText: lv >= 3 ? '严重' : lv >= 2 ? '中度' : '轻度',
            color: lv >= 3 ? '#ef4444' : lv >= 2 ? '#f59e0b' : '#3b82f6',
            bg: lv >= 3 ? '#fef2f2' : lv >= 2 ? '#fffbeb' : '#eff6ff',
            date: (w.createdAt || '').substring(0, 10)
          }
        })
      } catch (e) { console.error(e) }
    },
    goTo(url) { uni.navigateTo({ url }) },
    handleLogout() {
      ['token','userId','role','userName','teacherId'].forEach(k => uni.removeStorageSync(k))
      uni.reLaunch({ url: '/pages/auth/login' })
    }
  }
}
</script>

<style scoped>
.page { min-height: 100vh; background: #f0f2f8; padding-bottom: 120rpx; }

/* Hero */
.hero { background: linear-gradient(135deg, #2563eb 0%, #4f46e5 100%); padding: 44rpx 28rpx 40rpx 32rpx; }
.hero-row { display: flex; justify-content: space-between; align-items: center; }
.hero-left { display: flex; align-items: center; gap: 16rpx; }
.hero-av { width: 72rpx; height: 72rpx; border-radius: 50%; background: rgba(255,255,255,0.2); display: flex; align-items: center; justify-content: center; font-size: 30rpx; font-weight: 700; color: #fff; flex-shrink: 0; }
.hero-name { font-size: 32rpx; font-weight: 700; color: #fff; display: block; }
.hero-role { font-size: 22rpx; color: rgba(255,255,255,0.65); margin-top: 4rpx; display: block; }
.hero-logout { width: 60rpx; height: 60rpx; border-radius: 50%; background: rgba(255,255,255,0.15); display: flex; align-items: center; justify-content: center; }
.logout-icon { font-size: 40rpx; color: #fff; transform: rotate(180deg); }

.section-pad { padding: 0 20rpx; margin-top: -16rpx; position: relative; z-index: 2; }
.section { padding: 0 20rpx; margin-bottom: 24rpx; }

/* 统计卡片 */
.stats-row { display: grid; grid-template-columns: 1fr 1fr 1fr 1fr; gap: 12rpx; }
.stat-card { background: #fff; border-radius: 16rpx; padding: 20rpx 8rpx; text-align: center; box-shadow: 0 2rpx 12rpx rgba(0,0,0,0.04); }
.stat-icon { width: 52rpx; height: 52rpx; border-radius: 14rpx; display: flex; align-items: center; justify-content: center; margin: 0 auto 10rpx; font-size: 24rpx; }
.sc-blue { background: #eff6ff; } .sc-green { background: #ecfdf5; } .sc-red { background: #fef2f2; } .sc-purple { background: #f5f3ff; }
.stat-num { font-size: 28rpx; font-weight: 800; color: #1e293b; display: block; }
.stat-num.red { color: #ef4444; }
.stat-label { font-size: 20rpx; color: #94a3b8; margin-top: 4rpx; display: block; }

/* 快捷入口 */
.section-title { font-size: 28rpx; font-weight: 700; color: #1e293b; display: block; margin-bottom: 16rpx; }
.section-hd { display: flex; justify-content: space-between; align-items: center; margin-bottom: 16rpx; }
.section-more { font-size: 24rpx; color: #2563eb; }
.entry-grid { display: grid; grid-template-columns: 1fr 1fr 1fr; gap: 14rpx; }
.entry-card { background: #fff; border-radius: 16rpx; padding: 24rpx 14rpx; text-align: center; box-shadow: 0 2rpx 8rpx rgba(0,0,0,0.03); }
.entry-badge { width: 56rpx; height: 56rpx; border-radius: 14rpx; display: flex; align-items: center; justify-content: center; margin: 0 auto 12rpx; }
.badge-blue { background: #e0e7ff; } .badge-green { background: #dcfce7; } .badge-orange { background: #fef3c7; } .badge-red { background: #fee2e2; } .badge-purple { background: #ede9fe; } .badge-cyan { background: #cffafe; }
.eb-icon { font-size: 26rpx; }
.entry-name { font-size: 26rpx; font-weight: 600; color: #1e293b; display: block; }
.entry-desc { font-size: 20rpx; color: #94a3b8; margin-top: 4rpx; display: block; }

/* 预警卡片 */
.warn-list { display: flex; flex-direction: column; gap: 12rpx; }
.warn-card { background: #fff; border-radius: 14rpx; padding: 18rpx 20rpx; display: flex; align-items: center; gap: 14rpx; border-left: 6rpx solid; box-shadow: 0 1rpx 6rpx rgba(0,0,0,0.02); }
.warn-status { font-size: 20rpx; padding: 4rpx 14rpx; border-radius: 20rpx; font-weight: 600; flex-shrink: 0; }
.warn-body { flex: 1; min-width: 0; }
.warn-student { font-size: 26rpx; font-weight: 600; color: #1e293b; display: block; }
.warn-desc { font-size: 22rpx; color: #64748b; display: block; overflow: hidden; text-overflow: ellipsis; white-space: nowrap; }
.warn-date { font-size: 20rpx; color: #94a3b8; flex-shrink: 0; }

.empty-box { background: #fff; border-radius: 14rpx; padding: 48rpx; text-align: center; }
.empty-icon { font-size: 48rpx; display: block; margin-bottom: 12rpx; }
.empty-text { font-size: 24rpx; color: #94a3b8; }
</style>
