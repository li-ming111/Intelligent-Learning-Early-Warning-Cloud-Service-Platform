<template>
  <view class="page">
    <view class="header">
      <text class="title">课程管理</text>
      <text class="sub">{{ courses.length }} 门课程</text>
    </view>

    <scroll-view v-if="courses.length" scroll-y class="list">
      <view v-for="c in courses" :key="c.courseId || c.id" class="card">
        <view class="card-top">
          <view class="cv" :style="{background:avGrad((c.courseId||c.id)||1)}">{{ (c.courseName || c.name || '课')[0] }}</view>
          <view class="cinfo">
            <text class="cname">{{ c.courseName || c.name }}</text>
            <view class="ctags">
              <text class="ctag" :class="c.type === '选修' ? 'tag-elective' : 'tag-required'">{{ c.type || '必修' }}</text>
              <text class="ccr">{{ c.credits || '--' }} 学分</text>
            </view>
          </view>
        </view>
        <view class="card-stats">
          <view class="cs-item">
            <text class="cs-val">{{ c.enrollmentCount || 0 }}</text>
            <text class="cs-lbl">选课人数</text>
          </view>
          <view class="cs-div"></view>
          <view class="cs-item">
            <text class="cs-val" :class="(c.passRate || 0) >= 80 ? 'good' : 'warn'">{{ c.passRate || '--' }}{{ c.passRate ? '%' : '' }}</text>
            <text class="cs-lbl">及格率</text>
          </view>
          <view class="cs-div"></view>
          <view class="cs-item">
            <text class="cs-val">{{ c.className || '--' }}</text>
            <text class="cs-lbl">授课班级</text>
          </view>
        </view>
        <view class="card-foot">
          <text class="foot-link" @tap="viewDetail(c)">查看详情</text>
          <text class="foot-link" @tap="goScores(c)">管理成绩</text>
        </view>
      </view>
    </scroll-view>
    <view v-else class="empty">
      <text class="empty-icon">📖</text>
      <text class="empty-text">暂无课程数据</text>
    </view>

    <teacher-tab-bar current="pages/teacher/courses" />
  </view>
</template>

<script>
import { teacherAPI } from '@/services/api.js'

export default {
  data() { return { courses: [] } },
  onShow() { this.loadData() },
  methods: {
    async loadData() {
      try {
        const uid = uni.getStorageSync('teacherId') || uni.getStorageSync('userId'); if (!uid) return
        const res = await teacherAPI.getCourses(uid)
        this.courses = Array.isArray(res) ? res : (res?.data || [])
      } catch (e) {}
    },
    avGrad(id) {
      const cs = ['#2563eb,#1d4ed8','#7c3aed,#6d28d9','#16a34a,#15803d','#f59e0b,#d97706','#ef4444,#dc2626','#0891b2,#0e7490']
      return `linear-gradient(135deg,${cs[(id||1) % cs.length]})`
    },
    goScores(c) { uni.navigateTo({ url: '/pages/teacher/scores' }) },
    viewDetail(c) {
      uni.showModal({
        title: c.courseName || c.name,
        content: `类型: ${c.type || '必修'}\n学分: ${c.credits || '--'}\n选课人数: ${c.enrollmentCount || 0}\n${c.description || ''}`,
        showCancel: false
      })
    }
  }
}
</script>

<style scoped>
.page { padding: 20rpx; background: #f0f2f8; min-height: 100vh; padding-bottom: 120rpx; }
.header { margin-bottom: 20rpx; display: flex; justify-content: space-between; align-items: flex-end; }
.title { font-size: 36rpx; font-weight: 800; color: #1e293b; }
.sub { font-size: 24rpx; color: #94a3b8; }
.list { max-height: calc(100vh - 140rpx); }
.card { background: #fff; border-radius: 18rpx; padding: 24rpx; margin-bottom: 16rpx; box-shadow: 0 2rpx 8rpx rgba(0,0,0,0.03); }
.card-top { display: flex; align-items: center; gap: 16rpx; margin-bottom: 18rpx; }
.cv { width: 80rpx; height: 80rpx; border-radius: 18rpx; color: #fff; font-size: 32rpx; font-weight: 700; display: flex; align-items: center; justify-content: center; flex-shrink: 0; }
.cinfo { flex: 1; }
.cname { font-size: 30rpx; font-weight: 700; color: #1e293b; display: block; }
.ctags { display: flex; gap: 10rpx; margin-top: 6rpx; align-items: center; }
.ctag { font-size: 20rpx; padding: 3rpx 14rpx; border-radius: 8rpx; font-weight: 500; }
.tag-required { background: #e0e7ff; color: #2563eb; }
.tag-elective { background: #fef3c7; color: #d97706; }
.ccr { font-size: 20rpx; color: #94a3b8; }
.card-stats { display: flex; align-items: center; background: #f8fafc; border-radius: 12rpx; padding: 16rpx; margin-bottom: 16rpx; }
.cs-item { flex: 1; text-align: center; }
.cs-val { font-size: 26rpx; font-weight: 700; color: #1e293b; display: block; margin-bottom: 2rpx; }
.cs-val.good { color: #16a34a; }
.cs-val.warn { color: #f59e0b; }
.cs-lbl { font-size: 20rpx; color: #94a3b8; }
.cs-div { width: 2rpx; height: 36rpx; background: #e2e8f0; }
.card-foot { display: flex; gap: 24rpx; }
.foot-link { font-size: 24rpx; color: #2563eb; font-weight: 500; padding: 8rpx 0; }

.empty { text-align: center; padding: 120rpx 0; }
.empty-icon { font-size: 72rpx; display: block; margin-bottom: 16rpx; }
.empty-text { font-size: 26rpx; color: #94a3b8; }
</style>
