<template>
  <view class="tab-bar" :style="{paddingBottom: safeBottom + 'rpx'}">
    <view v-for="item in tabs" :key="item.page" class="tab-item" :class="{active: current === item.page}" @tap="switchTo(item.page)">
      <view class="tab-icon">{{ item.icon }}</view>
      <text class="tab-text">{{ item.label }}</text>
    </view>
  </view>
</template>

<script>
export default {
  name: 'TeacherTabBar',
  props: {
    current: { type: String, default: 'pages/teacher/dashboard' }
  },
  data() {
    return {
      safeBottom: 0,
      tabs: [
        { page: 'pages/teacher/dashboard', label: '首页', icon: '🏠' },
        { page: 'pages/teacher/scores', label: '成绩', icon: '📝' },
        { page: 'pages/teacher/students', label: '学生', icon: '👨‍🎓' },
        { page: 'pages/teacher/warnings', label: '预警', icon: '⚠' },
        { page: 'pages/teacher/courses', label: '课程', icon: '📚' },
        { page: 'pages/teacher/profile', label: '我的', icon: '👤' }
      ]
    }
  },
  created() {
    try {
      const sys = uni.getSystemInfoSync()
      this.safeBottom = sys.safeAreaInsets ? sys.safeAreaInsets.bottom : 0
    } catch (e) { this.safeBottom = 0 }
  },
  methods: {
    switchTo(page) {
      if (page === this.current) return
      // 教师页不在全局 tabBar 中，用 redirectTo 切换
      uni.redirectTo({ url: '/' + page })
    }
  }
}
</script>

<style scoped>
.tab-bar { position: fixed; left: 0; right: 0; bottom: 0; height: 100rpx; background: #ffffff; border-top: 1rpx solid #e2e8f0; display: flex; align-items: center; z-index: 999; box-shadow: 0 -2rpx 12rpx rgba(0, 0, 0, 0.04); }
.tab-item { flex: 1; display: flex; flex-direction: column; align-items: center; justify-content: center; padding-top: 6rpx; }
.tab-icon { font-size: 36rpx; line-height: 1; color: #94a3b8; }
.tab-text { font-size: 20rpx; color: #94a3b8; margin-top: 4rpx; }
.tab-item.active .tab-icon { color: #2563eb; }
.tab-item.active .tab-text { color: #2563eb; font-weight: 600; }
</style>
