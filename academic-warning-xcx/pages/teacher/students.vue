<template>
  <view class="page">
    <view class="header">
      <text class="title">学生管理</text>
      <text class="sub">{{ students.length }} 名学生</text>
    </view>

    <view class="filter-bar">
      <view class="search-wrap">
        <text class="search-icon">🔍</text>
        <input v-model="keyword" class="search-inp" placeholder="搜索姓名或学号" @confirm="applyFilter" />
        <text v-if="keyword" class="clear-btn" @tap="keyword='';applyFilter()">✕</text>
      </view>
      <picker :value="classIdx" :range="classNames" @change="onClassChange">
        <view class="picker-btn">
          <text class="picker-label">{{ classNames[classIdx] || '全部班级' }}</text>
          <text class="picker-arrow">▾</text>
        </view>
      </picker>
    </view>

    <scroll-view v-if="filtered.length" scroll-y class="list">
      <view v-for="s in filtered" :key="s.id || s.studentId" class="card">
        <view class="av" :style="{background:avGrad((s.id||s.studentId)||1)}">{{ (s.name || s.studentName || '?')[0] }}</view>
        <view class="info">
          <view class="name-row">
            <text class="name">{{ s.name || s.studentName }}</text>
            <text class="tag" :class="s.warnLevel >= 3 ? 'tag-red' : s.warnLevel >= 2 ? 'tag-yellow' : 'tag-green'">
              {{ s.warnLevel >= 3 ? '预警' : s.warnLevel >= 2 ? '关注' : '正常' }}
            </text>
          </view>
          <text class="meta">学号: {{ s.studentId || s.studentNo }}</text>
          <text class="meta">班级: {{ s.className || s.class_name || '--' }}</text>
        </view>
        <view class="actions">
          <text class="act-link" @tap="viewDetail(s)">详情</text>
        </view>
      </view>
    </scroll-view>
    <view v-else class="empty">
      <text class="empty-icon">👨‍🎓</text>
      <text class="empty-text">暂无学生数据</text>
    </view>

    <teacher-tab-bar current="pages/teacher/students" />
  </view>
</template>

<script>
import { teacherAPI } from '@/services/api.js'

export default {
  data() {
    return {
      keyword: '',
      classIdx: 0,
      classes: [],
      students: []
    }
  },
  computed: {
    classNames() { return ['全部班级', ...this.classes.map(c => c.name || c.className)] },
    filtered() {
      let arr = this.students
      if (this.classIdx > 0) {
        const cn = this.classes[this.classIdx - 1]?.name
        if (cn) arr = arr.filter(s => (s.className || s.class_name) === cn)
      }
      if (this.keyword) {
        const kw = this.keyword.toLowerCase()
        arr = arr.filter(s => (s.name || s.studentName || '').toLowerCase().includes(kw) || String(s.studentId || '').includes(kw))
      }
      return arr
    }
  },
  onShow() { this.loadData() },
  methods: {
    async loadData() {
      try {
        const uid = uni.getStorageSync('teacherId') || uni.getStorageSync('userId'); if (!uid) return
        const [sRes, cRes] = await Promise.all([teacherAPI.getStudents(uid), teacherAPI.getMyClasses(uid).catch(() => [])])
        this.students = Array.isArray(sRes) ? sRes : (sRes?.data || [])
        this.classes = Array.isArray(cRes) ? cRes : (cRes?.data || [])
      } catch (e) {}
    },
    onClassChange(e) { this.classIdx = Number(e.detail.value) },
    applyFilter() {},
    avGrad(id) {
      const cs = ['#2563eb,#1d4ed8','#7c3aed,#6d28d9','#16a34a,#15803d','#f59e0b,#d97706','#ef4444,#dc2626','#0891b2,#0e7490']
      return `linear-gradient(135deg,${cs[(id||1) % cs.length]})`
    },
    viewDetail(s) {
      uni.showModal({
        title: s.name || s.studentName,
        content: `学号: ${s.studentId || s.studentNo}\n班级: ${s.className || '--'}\n专业: ${s.major || '--'}\n成绩: ${s.avgScore || '--'}`,
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

.filter-bar { display: flex; gap: 14rpx; margin-bottom: 16rpx; }
.search-wrap { flex: 1; position: relative; display: flex; align-items: center; background: #fff; border-radius: 14rpx; padding: 0 16rpx; }
.search-icon { font-size: 28rpx; margin-right: 8rpx; }
.search-inp { flex: 1; height: 72rpx; font-size: 26rpx; }
.clear-btn { font-size: 26rpx; color: #94a3b8; padding: 4rpx; }
.picker-btn { background: #fff; border-radius: 14rpx; height: 72rpx; padding: 0 20rpx; display: flex; align-items: center; gap: 8rpx; white-space: nowrap; }
.picker-label { font-size: 24rpx; color: #475569; max-width: 140rpx; overflow: hidden; text-overflow: ellipsis; white-space: nowrap; }
.picker-arrow { font-size: 20rpx; color: #94a3b8; }

.list { max-height: calc(100vh - 220rpx); }
.card { background: #fff; border-radius: 16rpx; padding: 20rpx; margin-bottom: 12rpx; display: flex; align-items: center; gap: 16rpx; box-shadow: 0 1rpx 4rpx rgba(0,0,0,0.03); }
.av { width: 72rpx; height: 72rpx; border-radius: 50%; color: #fff; font-size: 30rpx; font-weight: 700; display: flex; align-items: center; justify-content: center; flex-shrink: 0; }
.info { flex: 1; min-width: 0; }
.name-row { display: flex; align-items: center; gap: 10rpx; }
.name { font-size: 28rpx; font-weight: 600; color: #1e293b; }
.tag { font-size: 18rpx; padding: 2rpx 12rpx; border-radius: 10rpx; font-weight: 500; }
.tag-green { background: #ecfdf5; color: #16a34a; }
.tag-yellow { background: #fef3c7; color: #d97706; }
.tag-red { background: #fef2f2; color: #ef4444; }
.meta { font-size: 22rpx; color: #94a3b8; display: block; margin-top: 2rpx; }
.actions { display: flex; flex-direction: column; gap: 8rpx; }
.act-link { font-size: 24rpx; color: #2563eb; font-weight: 500; padding: 8rpx 16rpx; }

.empty { text-align: center; padding: 120rpx 0; }
.empty-icon { font-size: 72rpx; display: block; margin-bottom: 16rpx; }
.empty-text { font-size: 26rpx; color: #94a3b8; }
</style>
