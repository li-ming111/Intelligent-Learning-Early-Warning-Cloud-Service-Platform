<template>
  <view class="page">
    <view class="header">
      <text class="title">成绩管理</text>
      <text class="sub">录入与修改学生成绩</text>
    </view>

    <!-- 筛选栏 -->
    <view class="filter-bar">
      <view class="search-wrap">
        <text class="search-icon">🔍</text>
        <input v-model="keyword" class="search-inp" placeholder="搜索学生姓名或学号" @confirm="applyFilter" />
        <text v-if="keyword" class="clear-btn" @tap="keyword='';applyFilter()">✕</text>
      </view>
      <picker :value="courseIdx" :range="courseNames" @change="onCourseChange">
        <view class="picker-btn">
          <text class="picker-label">{{ courseNames[courseIdx] || '选择课程' }}</text>
          <text class="picker-arrow">▾</text>
        </view>
      </picker>
    </view>

    <!-- 课程选中后的统计 -->
    <view v-if="courseIdx > 0" class="mini-stats">
      <view class="ms-item"><text class="ms-val">{{ filtered.length }}</text><text class="ms-lbl">学生数</text></view>
      <view class="ms-div"></view>
      <view class="ms-item"><text class="ms-val" :class="avgClass">{{ avgScore }}</text><text class="ms-lbl">平均分</text></view>
      <view class="ms-div"></view>
      <view class="ms-item"><text class="ms-val green">{{ passRate }}%</text><text class="ms-lbl">及格率</text></view>
    </view>

    <!-- 列表 -->
    <scroll-view v-if="filtered.length" scroll-y class="list">
      <view v-for="(s, i) in filtered" :key="s.id || i" class="score-card">
        <view class="sc-left">
          <view class="sc-av" :style="{background:avColor(i)}">{{ (s.studentName || '?')[0] }}</view>
          <view class="sc-info">
            <text class="sc-name">{{ s.studentName || s.student_name }}</text>
            <text class="sc-id">学号: {{ s.studentId || s.student_id }}</text>
          </view>
        </view>
        <view class="sc-edit">
          <input v-model.number="s.score" class="score-inp" type="digit" placeholder="分数" @blur="autoGrade(s)" />
          <text class="sc-grade" :class="gradeCls(s.score)">{{ gradeText(s.score) }}</text>
        </view>
        <button class="sc-save" size="mini" @click="doSave(s, i)">保存</button>
      </view>
    </scroll-view>
    <view v-else class="empty">
      <text class="empty-icon">📋</text>
      <text class="empty-text">请选择课程后查看学生成绩</text>
    </view>

    <view v-if="filtered.length" class="batch-row">
      <button class="batch-btn" @click="doBatchSave">批量保存</button>
    </view>

    <teacher-tab-bar current="pages/teacher/scores" />
  </view>
</template>

<script>
import { teacherAPI } from '@/services/api.js'

export default {
  data() {
    return {
      keyword: '',
      courseIdx: 0,
      courses: [],
      list: [],
      changedIndexes: new Set()
    }
  },
  computed: {
    courseNames() { return ['全部课程', ...this.courses.map(c => c.courseName || c.name)] },
    filtered() {
      let arr = this.list
      if (this.courseIdx > 0) {
        const cid = this.courses[this.courseIdx - 1]?.id
        if (cid) arr = arr.filter(s => s.courseId === cid || this.courseIdx <= 1)
      }
      if (this.keyword) {
        const kw = this.keyword.toLowerCase()
        arr = arr.filter(s => (s.studentName || '').includes(kw) || String(s.studentId || '').includes(kw))
      }
      return arr
    },
    avgScore() {
      const valid = this.filtered.filter(s => s.score != null && s.score !== '' && !isNaN(s.score))
      return valid.length ? (valid.reduce((a, b) => a + Number(b.score), 0) / valid.length).toFixed(1) : '--'
    },
    passRate() {
      const valid = this.filtered.filter(s => s.score != null && s.score !== '' && !isNaN(s.score))
      return valid.length ? Math.round(valid.filter(s => Number(s.score) >= 60).length / valid.length * 100) : '--'
    },
    avgClass() { const a = this.avgScore; return a === '--' ? '' : a >= 80 ? 'good' : a >= 60 ? 'ok' : 'bad' }
  },
  onShow() { this.loadCourses(); this.loadAllScores() },
  methods: {
    async loadCourses() {
      try {
        const uid = uni.getStorageSync('teacherId') || uni.getStorageSync('userId'); if (!uid) return
        const res = await teacherAPI.getCourses(uid)
        this.courses = Array.isArray(res) ? res : (res?.data || [])
      } catch (e) {}
    },
    async loadAllScores() {
      try {
        const uid = uni.getStorageSync('teacherId') || uni.getStorageSync('userId'); if (!uid) return
        const enroll = await teacherAPI.getEnrollments(uid)
        const arr = Array.isArray(enroll) ? enroll : (enroll?.data || [])
        this.list = arr.map(e => ({
          id: e.studentId, studentId: e.studentId,
          studentName: e.studentName, courseId: e.courseId,
          courseName: e.courseName, score: e.score
        }))
      } catch (e) {}
    },
    onCourseChange(e) { this.courseIdx = Number(e.detail.value) },
    autoGrade(s) {
      if (s.score != null && s.score !== '' && !isNaN(s.score)) {
        this.changedIndexes.add(this.filtered.indexOf(s))
      }
    },
    applyFilter() {},
    async doSave(s, i) {
      const sc = Number(s.score)
      if (isNaN(sc) || sc < 0 || sc > 100) { uni.showToast({ title: '请输入0-100的分数', icon: 'none' }); return }
      try {
        const uid = uni.getStorageSync('userId')
        await teacherAPI.updateScore(s.id, { score: sc, modifiedBy: String(uid) })
        uni.showToast({ title: '已保存', icon: 'success', duration: 1000 })
        this.changedIndexes.delete(i)
      } catch (e) { uni.showToast({ title: '保存失败', icon: 'none' }) }
    },
    async doBatchSave() {
      const toSave = Array.from(this.changedIndexes).map(i => this.filtered[i]).filter(s => s.score != null)
      if (!toSave.length) { uni.showToast({ title: '无待保存记录', icon: 'none' }); return }
      try {
        const uid = uni.getStorageSync('userId')
        await Promise.all(toSave.map(s => teacherAPI.updateScore(s.id, { score: Number(s.score), modifiedBy: String(uid) })))
        uni.showToast({ title: `已保存${toSave.length}条`, icon: 'success' })
        this.changedIndexes.clear()
      } catch (e) { uni.showToast({ title: '部分保存失败', icon: 'none' }) }
    },
    avColor(i) { const cs = ['#2563eb','#7c3aed','#16a34a','#f59e0b','#ef4444','#0891b2']; return cs[i % cs.length] },
    gradeCls(s) { return !s && s !== 0 ? '' : s >= 90 ? 'g-a' : s >= 80 ? 'g-b' : s >= 70 ? 'g-c' : s >= 60 ? 'g-d' : 'g-f' },
    gradeText(s) { return !s && s !== 0 ? '' : s >= 90 ? 'A' : s >= 80 ? 'B' : s >= 70 ? 'C' : s >= 60 ? 'D' : 'F' }
  }
}
</script>

<style scoped>
.page { padding: 20rpx; background: #f0f2f8; min-height: 100vh; padding-bottom: 120rpx; }
.header { margin-bottom: 20rpx; }
.title { font-size: 36rpx; font-weight: 800; color: #1e293b; display: block; }
.sub { font-size: 24rpx; color: #94a3b8; margin-top: 4rpx; display: block; }

.filter-bar { display: flex; gap: 14rpx; margin-bottom: 16rpx; }
.search-wrap { flex: 1; position: relative; display: flex; align-items: center; background: #fff; border-radius: 14rpx; padding: 0 16rpx; }
.search-icon { font-size: 28rpx; margin-right: 8rpx; }
.search-inp { flex: 1; height: 72rpx; font-size: 26rpx; }
.clear-btn { font-size: 26rpx; color: #94a3b8; padding: 4rpx; }
.picker-btn { background: #fff; border-radius: 14rpx; height: 72rpx; padding: 0 20rpx; display: flex; align-items: center; gap: 8rpx; white-space: nowrap; }
.picker-label { font-size: 24rpx; color: #475569; max-width: 140rpx; overflow: hidden; text-overflow: ellipsis; white-space: nowrap; }
.picker-arrow { font-size: 20rpx; color: #94a3b8; }

.mini-stats { background: #fff; border-radius: 14rpx; padding: 18rpx 24rpx; display: flex; align-items: center; margin-bottom: 14rpx; }
.ms-item { flex: 1; text-align: center; }
.ms-val { font-size: 28rpx; font-weight: 800; color: #1e293b; display: block; }
.ms-val.good { color: #16a34a; } .ms-val.ok { color: #f59e0b; } .ms-val.bad { color: #ef4444; }
.ms-val.green { color: #16a34a; }
.ms-lbl { font-size: 20rpx; color: #94a3b8; }
.ms-div { width: 2rpx; height: 32rpx; background: #e2e8f0; }

.list { max-height: calc(100vh - 380rpx); }
.score-card { background: #fff; border-radius: 14rpx; padding: 18rpx 20rpx; margin-bottom: 10rpx; display: flex; align-items: center; gap: 12rpx; }
.sc-left { display: flex; align-items: center; gap: 12rpx; flex: 1; min-width: 0; }
.sc-av { width: 64rpx; height: 64rpx; border-radius: 50%; color: #fff; font-size: 26rpx; font-weight: 700; display: flex; align-items: center; justify-content: center; flex-shrink: 0; }
.sc-info { min-width: 0; }
.sc-name { font-size: 26rpx; font-weight: 600; color: #1e293b; display: block; }
.sc-id { font-size: 20rpx; color: #94a3b8; display: block; }
.sc-edit { display: flex; align-items: center; gap: 6rpx; }
.score-inp { width: 90rpx; height: 56rpx; background: #f1f5f9; border-radius: 10rpx; text-align: center; font-size: 26rpx; font-weight: 700; color: #1e293b; }
.sc-grade { font-size: 22rpx; font-weight: 700; min-width: 36rpx; text-align: center; }
.g-a { color: #16a34a; } .g-b { color: #2563eb; } .g-c { color: #f59e0b; } .g-d { color: #ea580c; } .g-f { color: #ef4444; }
.sc-save { background: linear-gradient(135deg, #2563eb, #4f46e5); color: #fff; border: none; font-size: 22rpx; border-radius: 10rpx; padding: 0 20rpx; height: 54rpx; line-height: 54rpx; }

.empty { text-align: center; padding: 100rpx 0; }
.empty-icon { font-size: 64rpx; display: block; margin-bottom: 16rpx; }
.empty-text { font-size: 26rpx; color: #94a3b8; }

.batch-row { margin-top: 20rpx; }
.batch-btn { width: 100%; height: 80rpx; background: #fff; border: 2rpx solid #2563eb; border-radius: 14rpx; color: #2563eb; font-size: 28rpx; font-weight: 600; display: flex; align-items: center; justify-content: center; }
</style>
