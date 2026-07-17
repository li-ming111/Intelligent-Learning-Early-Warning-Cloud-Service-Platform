<template>
  <view class="page">
    <view class="header"><text class="title">报表统计</text><text class="sub">系统数据汇总</text></view>
    <view class="stats-grid">
      <view class="st-card"><text class="st-num blue">{{ report.studentCount }}</text><text class="st-lbl">学生总数</text></view>
      <view class="st-card"><text class="st-num green">{{ report.teacherCount }}</text><text class="st-lbl">教师总数</text></view>
      <view class="st-card"><text class="st-num purple">{{ report.warningTotal }}</text><text class="st-lbl">预警总数</text></view>
      <view class="st-card"><text class="st-num red">{{ report.warningRate }}%</text><text class="st-lbl">预警率</text></view>
      <view class="st-card"><text class="st-num cyan">{{ report.assistanceCount }}</text><text class="st-lbl">帮扶计划</text></view>
      <view class="st-card"><text class="st-num orange">{{ report.appealCount }}</text><text class="st-lbl">成绩申诉</text></view>
    </view>
  </view>
</template>

<script>
import { adminAPI } from '@/services/api.js'

export default {
  data() {
    return {
      report: { studentCount:0, teacherCount:0, warningTotal:0, warningRate:0, assistanceCount:0, appealCount:0 }
    }
  },
  onShow() { this.loadData() },
  methods: {
    async loadData() {
      try {
        const res = await adminAPI.getReports()
        if (res) Object.assign(this.report, {
          studentCount: res.totalStudents || 0, teacherCount: res.totalTeachers || 0,
          warningTotal: res.totalWarnings || 0, warningRate: Math.round(res.warningRate || 0),
          assistanceCount: res.assistanceCount || 0, appealCount: res.appealCount || 0
        })
      } catch (e) {}
    }
  }
}
</script>

<style scoped>
.page { padding:20rpx; background:#f0f2f8; min-height:100vh; }
.header { margin-bottom:20rpx; } .title { font-size:36rpx; font-weight:800; color:#1e293b; display:block; } .sub { font-size:24rpx; color:#94a3b8; }
.stats-grid { display:grid; grid-template-columns:1fr 1fr; gap:14rpx; }
.st-card { background:#fff; border-radius:16rpx; padding:28rpx; text-align:center; }
.st-num { font-size:40rpx; font-weight:800; display:block; margin-bottom:8rpx; }
.st-num.blue { color:#2563eb; } .st-num.green { color:#16a34a; } .st-num.purple { color:#7c3aed; } .st-num.red { color:#ef4444; } .st-num.cyan { color:#0891b2; } .st-num.orange { color:#f59e0b; }
.st-lbl { font-size:22rpx; color:#94a3b8; }
</style>
