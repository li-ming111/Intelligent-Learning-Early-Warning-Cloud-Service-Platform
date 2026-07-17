<template>
  <view class="page">
    <!-- 学期标签 -->
    <scroll-view scroll-x class="term-tabs" show-scrollbar="false">
      <view class="tabs-inner">
        <view
          v-for="(t, idx) in semesters"
          :key="t.value"
          :class="['term-tab', { active: selectedIndex === idx }]"
          @click="selectTerm(idx)"
          hover-class="term-hover"
          :hover-start-time="0"
          :hover-stay-time="100"
        >
          <text>{{ t.label }}</text>
        </view>
      </view>
    </scroll-view>

    <!-- 成绩表卡片 -->
    <view class="table-card">
      <!-- 表头 -->
      <view class="table-header">
        <text class="th th-name">课程名称</text>
        <text class="th th-score">分数</text>
        <text class="th th-gpa">绩点</text>
      </view>

      <!-- 表体 -->
      <scroll-view scroll-y class="table-body" v-if="scores.length > 0">
        <view v-for="c in scores" :key="c.id" class="table-row">
          <text class="td td-name">{{ c.courseName }}</text>
          <text :class="['td', 'td-score', scoreClass(c.scoreTotal)]">{{ scoreText(c.scoreTotal) }}</text>
          <text class="td td-gpa">{{ c.gradePoint || calcGpa(c.scoreTotal) }}</text>
        </view>
      </scroll-view>

      <!-- 空状态 -->
      <view v-else class="empty">
        <text class="empty-text">暂无成绩记录</text>
      </view>
    </view>
  </view>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { studentAPI } from '@/services/api.js'

const selectedIndex = ref(0)
const scores = ref([])

// 学期列表：显示标签 + 传给后端的值
const semesters = ref([
  { label: '全部', value: '' },
  { label: '2025-2026-02', value: '2025-2026-2' },
  { label: '2025-2026-01', value: '2025-2026-1' },
  { label: '2024-2025-02', value: '2024-2025-2' },
  { label: '2024-2025-01', value: '2024-2025-1' },
  { label: '2023-2024-02', value: '2023-2024-2' },
  { label: '2023-2024-01', value: '2023-2024-1' }
])

const scoreClass = (s) => {
  const n = Number(s) || 0
  if (n >= 90) return 'excellent'
  if (n >= 80) return 'good'
  if (n >= 70) return 'medium'
  if (n >= 60) return 'pass'
  return 'fail'
}

const scoreText = (s) => {
  const n = Number(s) || 0
  if (n >= 90) return '优秀'
  return String(n)
}

const calcGpa = (s) => {
  const n = Number(s) || 0
  if (n >= 90) return '4.0'
  if (n >= 85) return '3.7'
  if (n >= 82) return '3.3'
  if (n >= 78) return '3.0'
  if (n >= 75) return '2.7'
  if (n >= 72) return '2.3'
  if (n >= 68) return '2.0'
  if (n >= 64) return '1.5'
  if (n >= 60) return '1.0'
  return '0'
}

const selectTerm = (idx) => {
  selectedIndex.value = idx
  loadScores()
}

const loadScores = async () => {
  try {
    const uid = uni.getStorageSync('userId')
    if (!uid) return
    const sem = semesters.value[selectedIndex.value].value
    const res = await studentAPI.getScores(uid, sem || null)
    const arr = Array.isArray(res) ? res : (res?.data || [])
    scores.value = arr.map(s => ({
      id: s.id || s.courseId,
      courseName: s.courseName || s.course_name || '',
      scoreTotal: Number(s.scoreTotal || s.score_total || s.score || 0),
      gradePoint: s.gradePoint || s.grade_point || null
    }))
  } catch (e) {
    console.error('获取成绩失败:', e)
    uni.showToast({ title: '加载失败', icon: 'none' })
  }
}

onMounted(() => { loadScores() })
</script>

<style scoped>
.page {
  min-height: 100vh;
  background: #f2f3f7;
  padding-top: 16rpx;
}

/* 学期标签 */
.term-tabs {
  width: 100%;
  white-space: nowrap;
  margin-bottom: 16rpx;
  background: #f2f3f7;
}
.tabs-inner {
  display: inline-flex;
  padding: 0 20rpx;
}
.term-tab {
  padding: 20rpx 28rpx;
  position: relative;
}
.term-tab text {
  font-size: 28rpx;
  color: #666;
}
.term-tab.active text {
  color: #e85a4f;
  font-weight: 600;
}
.term-tab.active::after {
  content: '';
  position: absolute;
  bottom: 6rpx;
  left: 50%;
  transform: translateX(-50%);
  width: 40rpx;
  height: 4rpx;
  background: #e85a4f;
  border-radius: 2rpx;
}
.term-hover {
  opacity: 0.7;
}

/* 成绩表卡片 */
.table-card {
  background: #fff;
  border-radius: 16rpx;
  margin: 0 20rpx 20rpx;
  overflow: hidden;
  display: flex;
  flex-direction: column;
  max-height: calc(100vh - 180rpx);
}

/* 表头 */
.table-header {
  display: flex;
  background: #4a90ff;
  padding: 24rpx 0;
  flex-shrink: 0;
}
.th {
  font-size: 28rpx;
  color: #fff;
  text-align: center;
  font-weight: 500;
}
.th-name { flex: 2.2; text-align: left; padding-left: 24rpx; }
.th-score { flex: 1; }
.th-gpa { flex: 1; padding-right: 24rpx; }

/* 表体 */
.table-body {
  flex: 1;
  height: auto;
}
.table-row {
  display: flex;
  padding: 24rpx 0;
  border-bottom: 1rpx solid #f0f0f0;
}
.table-row:last-child {
  border-bottom: none;
}
.td {
  font-size: 28rpx;
  color: #333;
  text-align: center;
}
.td-name {
  flex: 2.2;
  text-align: left;
  padding-left: 24rpx;
  padding-right: 20rpx;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}
.td-score { flex: 1; }
.td-gpa { flex: 1; padding-right: 24rpx; }

.td-score.excellent { color: #f59e0b; font-weight: 600; }
.td-score.good { color: #3b5ce8; }
.td-score.medium { color: #22c55e; }
.td-score.pass { color: #666; }
.td-score.fail { color: #ef4444; }

/* 空状态 */
.empty {
  padding: 80rpx 0;
  text-align: center;
}
.empty-text {
  font-size: 26rpx;
  color: #999;
}
</style>
