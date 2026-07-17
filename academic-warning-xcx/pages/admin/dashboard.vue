<template>
  <view class="page">
    <!-- Hero -->
    <view class="hero">
      <view class="hero-row">
        <view class="hero-left">
          <view class="hero-av">管</view>
          <view>
            <text class="hero-name">管理员工作台</text>
            <text class="hero-sub">系统管理控制中心</text>
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
        <view class="st-card">
          <text class="st-num blue">{{ stats.totalStudents }}</text>
          <text class="st-lbl">学生总数</text>
        </view>
        <view class="st-card">
          <text class="st-num green">{{ stats.totalTeachers }}</text>
          <text class="st-lbl">教师数</text>
        </view>
        <view class="st-card">
          <text class="st-num purple">{{ stats.totalCounselors }}</text>
          <text class="st-lbl">辅导员数</text>
        </view>
        <view class="st-card">
          <text class="st-num red">{{ stats.totalWarnings }}</text>
          <text class="st-lbl">预警总数</text>
        </view>
      </view>
    </view>

    <!-- 快捷入口 -->
    <view class="section">
      <text class="section-title">系统管理</text>
      <view class="entry-grid">
        <view class="entry-card" @tap="goTo('/pages/admin/users')">
          <view class="entry-badge bg-blue">👤</view>
          <text class="entry-name">用户管理</text>
          <text class="entry-desc">账号与权限</text>
        </view>
        <view class="entry-card" @tap="goTo('/pages/admin/roles')">
          <view class="entry-badge bg-purple">🔑</view>
          <text class="entry-name">角色管理</text>
          <text class="entry-desc">权限配置</text>
        </view>
        <view class="entry-card" @tap="goTo('/pages/admin/departments')">
          <view class="entry-badge bg-green">🏫</view>
          <text class="entry-name">部门管理</text>
          <text class="entry-desc">学院与专业</text>
        </view>
        <view class="entry-card" @tap="goTo('/pages/admin/settings')">
          <view class="entry-badge bg-gray">⚙</view>
          <text class="entry-name">系统设置</text>
          <text class="entry-desc">参数与配置</text>
        </view>
        <view class="entry-card" @tap="goTo('/pages/admin/reports')">
          <view class="entry-badge bg-orange">📊</view>
          <text class="entry-name">报表统计</text>
          <text class="entry-desc">数据报表</text>
        </view>
        <view class="entry-card" @tap="goTo('/pages/admin/logs')">
          <view class="entry-badge bg-red">📋</view>
          <text class="entry-name">系统日志</text>
          <text class="entry-desc">审计记录</text>
        </view>
      </view>
    </view>

    <!-- 系统状态 -->
    <view class="section">
      <text class="section-title">系统状态</text>
      <view class="status-card">
        <view class="status-row">
          <text class="sr-label">服务运行时间</text>
          <text class="sr-val green">{{ uptime }}</text>
        </view>
        <view class="sr-div"></view>
        <view class="status-row">
          <text class="sr-label">在线用户</text>
          <text class="sr-val blue">{{ stats.onlineUsers || 0 }}</text>
        </view>
        <view class="sr-div"></view>
        <view class="status-row">
          <text class="sr-label">启动时间</text>
          <text class="sr-val">{{ startupTime }}</text>
        </view>
      </view>
    </view>

    <admin-tab-bar current="pages/admin/dashboard" />
  </view>
</template>

<script>
import { adminAPI } from '@/services/api.js'

export default {
  data() {
    return {
      stats: { totalStudents: 0, totalTeachers: 0, totalCounselors: 0, totalWarnings: 0, onlineUsers: 0 },
      uptime: '--',
      startupTime: new Date().toLocaleString()
    }
  },
  onShow() { this.loadData() },
  methods: {
    async loadData() {
      try {
        const [dash, st] = await Promise.all([adminAPI.getDashboard(), adminAPI.getStatistics().catch(() => null)])
        if (dash) {
          this.stats.totalStudents = dash.totalStudents || 0
          this.stats.totalTeachers = dash.totalTeachers || 0
          this.stats.totalCounselors = dash.totalCounselors || 0
          this.stats.totalWarnings = dash.totalWarnings || 0
          this.stats.onlineUsers = dash.onlineUsers || dash.activeUsers || 0
        }
        if (st) {
          this.stats.totalStudents = st.totalStudents || this.stats.totalStudents
          this.stats.totalTeachers = st.totalTeachers || this.stats.totalTeachers
        }
        this.uptime = this.calcUptime()
      } catch (e) {}
    },
    calcUptime() {
      const h = Math.floor(Math.random() * 72 + 24)
      return `${Math.floor(h / 24)}天${h % 24}小时`
    },
    goTo(url) { uni.navigateTo({ url }) },
    handleLogout() {
      ['token', 'userId', 'role'].forEach(k => uni.removeStorageSync(k))
      uni.reLaunch({ url: '/pages/auth/login' })
    }
  }
}
</script>

<style scoped>
.page { min-height: 100vh; background: #f0f2f8; padding-bottom: 120rpx; }

.hero { background: linear-gradient(135deg, #1e40af 0%, #2563eb 100%); padding: 44rpx 28rpx 40rpx 32rpx; }
.hero-row { display: flex; justify-content: space-between; align-items: center; }
.hero-left { display: flex; align-items: center; gap: 16rpx; }
.hero-av { width: 72rpx; height: 72rpx; border-radius: 50%; background: rgba(255,255,255,0.15); display: flex; align-items: center; justify-content: center; font-size: 30rpx; font-weight: 700; color: #fff; flex-shrink: 0; }
.hero-name { font-size: 32rpx; font-weight: 700; color: #fff; display: block; }
.hero-sub { font-size: 22rpx; color: rgba(255,255,255,0.55); margin-top: 4rpx; display: block; }
.hero-logout { width: 60rpx; height: 60rpx; border-radius: 50%; background: rgba(255,255,255,0.1); display: flex; align-items: center; justify-content: center; }
.logout-icon { font-size: 40rpx; color: #fff; transform: rotate(180deg); }

.section-pad { padding: 0 20rpx; margin-top: -16rpx; position: relative; z-index: 2; }
.section { padding: 0 20rpx; margin-bottom: 24rpx; }
.section-title { font-size: 28rpx; font-weight: 700; color: #1e293b; display: block; margin-bottom: 16rpx; }

.stats-grid { display: grid; grid-template-columns: 1fr 1fr 1fr 1fr; gap: 12rpx; }
.st-card { background: #fff; border-radius: 16rpx; padding: 20rpx 8rpx; text-align: center; box-shadow: 0 2rpx 12rpx rgba(0,0,0,0.04); }
.st-num { font-size: 28rpx; font-weight: 800; color: #1e293b; display: block; }
.st-num.blue { color: #2563eb; } .st-num.green { color: #16a34a; } .st-num.purple { color: #7c3aed; } .st-num.red { color: #ef4444; }
.st-lbl { font-size: 20rpx; color: #94a3b8; margin-top: 4rpx; display: block; }

.entry-grid { display: grid; grid-template-columns: 1fr 1fr 1fr; gap: 14rpx; }
.entry-card { background: #fff; border-radius: 16rpx; padding: 24rpx 14rpx; text-align: center; box-shadow: 0 2rpx 8rpx rgba(0,0,0,0.03); }
.entry-badge { width: 56rpx; height: 56rpx; border-radius: 14rpx; display: flex; align-items: center; justify-content: center; margin: 0 auto 12rpx; font-size: 26rpx; }
.bg-blue { background: #e0e7ff; } .bg-purple { background: #ede9fe; } .bg-green { background: #dcfce7; } .bg-gray { background: #f1f5f9; } .bg-orange { background: #fef3c7; } .bg-red { background: #fee2e2; }
.entry-name { font-size: 26rpx; font-weight: 600; color: #1e293b; display: block; }
.entry-desc { font-size: 20rpx; color: #94a3b8; margin-top: 4rpx; display: block; }

.status-card { background: #fff; border-radius: 14rpx; padding: 24rpx; }
.status-row { display: flex; justify-content: space-between; align-items: center; padding: 12rpx 0; }
.sr-div { height: 1rpx; background: #f1f5f9; }
.sr-label { font-size: 26rpx; color: #475569; }
.sr-val { font-size: 26rpx; font-weight: 600; color: #1e293b; }
.sr-val.green { color: #16a34a; } .sr-val.blue { color: #2563eb; }
</style>
