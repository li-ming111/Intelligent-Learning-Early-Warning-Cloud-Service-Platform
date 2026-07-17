<template>
  <view class="page">
    <view class="blue-bar">
      <text class="bar-title">成绩申诉</text>
      <text class="bar-sub">对有异议的成绩申请复核</text>
    </view>

    <view class="card">
      <text class="card-title">提交申诉</text>
      <picker :value="courseIdx" :range="courseNames" @change="courseIdx=Number($event.detail.value)" class="picker">
        <view class="picker-row">
          <text class="picker-text">{{ courseNames[courseIdx] || '选择课程' }}</text>
          <text class="picker-arrow">›</text>
        </view>
      </picker>
      <textarea v-model="reason" class="textarea" placeholder="请输入申诉理由..." :maxlength="200" />
      <text class="counter">{{ reason.length }}/200</text>
      <button class="btn" @tap="doSubmit" :disabled="!reason||!courseIdx">提交申诉</button>
    </view>

    <view class="card" style="margin-top: 12rpx;">
      <view class="card-hd">
        <text class="card-title">我的申诉</text>
        <text class="card-count">({{ list.length }})</text>
      </view>
      <view v-if="list.length">
        <view v-for="a in list" :key="a.id" class="appeal-row">
          <view class="appeal-left">
            <text class="appeal-name">{{ a.courseName||'课程'+a.courseId }}</text>
            <text class="appeal-date">{{ (a.createdAt||'').substring(0,10) }}</text>
          </view>
          <text :class="['appeal-status', statusClass(a.status)]">{{ statusText(a.status) }}</text>
        </view>
      </view>
      <view v-else class="empty-sm">暂无申诉记录</view>
    </view>
  </view>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { studentAPI } from '@/services/api.js'

const courseIdx = ref(0)
const reason = ref('')
const courses = ref([])
const list = ref([])
const courseNames = computed(() => courses.value.map(c => c.courseName||c.name))

onMounted(() => { loadData() })

const loadData = async () => {
  try {
    const uid = uni.getStorageSync('userId'); if (!uid) return
    const [scoreRes, appealRes] = await Promise.all([studentAPI.getScores(uid), studentAPI.getAppeals(uid)])
    const scores = Array.isArray(scoreRes) ? scoreRes : (scoreRes?.data || [])
    courses.value = [...new Map(scores.map(s => [s.courseId||s.course_id, {id:s.courseId||s.course_id, name:s.courseName||s.course_name, courseName:s.courseName||s.course_name}])).values()]
    list.value = Array.isArray(appealRes) ? appealRes : (appealRes?.data || [])
  } catch (e) {}
}

const doSubmit = async () => {
  if (!reason.value) { uni.showToast({title:'请输入申诉理由',icon:'none'}); return }
  try {
    await studentAPI.submitAppeal({ courseId: courses.value[courseIdx.value-1]?.id, reason: reason.value, studentId: uni.getStorageSync('userId') })
    uni.showToast({title:'提交成功',icon:'success'}); reason.value = ''; courseIdx.value = 0; loadData()
  } catch (e) { uni.showToast({title:'提交失败',icon:'none'}) }
}

const statusClass = (s) => s===0 ? 'pending' : s===1 ? 'pass' : 'reject'
const statusText = (s) => s===0 ? '待处理' : s===1 ? '已通过' : '已驳回'
</script>

<style scoped>
.page { min-height: 100vh; padding: 0 24rpx 24rpx; }
.blue-bar { background: linear-gradient(135deg, #3b5ce8 0%, #5b7cf0 100%); margin: 0 -24rpx 20rpx; padding: 44rpx 28rpx 36rpx; }
.bar-title { display: block; font-size: 34rpx; font-weight: 700; color: #fff; }
.bar-sub { display: block; font-size: 22rpx; color: rgba(255,255,255,0.7); margin-top: 6rpx; }

.card { background: #fff; border-radius: 14rpx; padding: 24rpx; border: 2rpx solid #f0f0f2; }
.card-hd { display: flex; align-items: baseline; gap: 8rpx; margin-bottom: 16rpx; }
.card-title { font-size: 28rpx; font-weight: 600; color: #1d1d1f; display: block; margin-bottom: 16rpx; }
.card-count { font-size: 24rpx; color: #aeaeb2; }

.picker { height: 80rpx; background: #f5f5f7; border-radius: 12rpx; padding: 0 20rpx; display: flex; align-items: center; margin-bottom: 16rpx; }
.picker-row { width: 100%; display: flex; justify-content: space-between; align-items: center; }
.picker-text { font-size: 26rpx; color: #1d1d1f; }
.picker-arrow { font-size: 28rpx; color: #aeaeb2; }

.textarea { width: 100%; height: 200rpx; background: #f5f5f7; border-radius: 12rpx; padding: 20rpx; font-size: 26rpx; box-sizing: border-box; color: #1d1d1f; }
.counter { text-align: right; font-size: 22rpx; color: #aeaeb2; display: block; margin: 8rpx 0 20rpx; }

.btn { width: 100%; height: 80rpx; line-height: 80rpx; background: #3b5ce8; color: #fff; border: none; border-radius: 12rpx; font-size: 28rpx; font-weight: 600; }
.btn[disabled] { opacity: 0.5; }

.appeal-row { display: flex; justify-content: space-between; align-items: center; padding: 16rpx 0; border-bottom: 2rpx solid #f5f5f7; }
.appeal-left { flex: 1; }
.appeal-name { font-size: 26rpx; color: #1d1d1f; display: block; margin-bottom: 4rpx; }
.appeal-date { font-size: 22rpx; color: #aeaeb2; }
.appeal-status { font-size: 24rpx; font-weight: 600; }
.appeal-status.pending { color: #f59e0b; }
.appeal-status.pass { color: #22c55e; }
.appeal-status.reject { color: #ef4444; }

.empty-sm { text-align: center; padding: 40rpx; color: #aeaeb2; font-size: 24rpx; }
</style>
