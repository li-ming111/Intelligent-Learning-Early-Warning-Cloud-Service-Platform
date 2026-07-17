<template>
  <view class="page">
    <view class="header">
      <text class="title">数据分析</text>
      <text class="sub">预警趋势与统计</text>
    </view>

    <!-- 统计卡片 -->
    <view class="stats-grid">
      <view class="st-card">
        <view class="st-icon si-red">⚠</view>
        <text class="st-val red">{{ metrics.insufficient }}%</text>
        <text class="st-lbl">学分不足率</text>
      </view>
      <view class="st-card">
        <view class="st-icon si-blue">⏱</view>
        <text class="st-val">{{ metrics.efficiency }}天</text>
        <text class="st-lbl">平均处理</text>
      </view>
      <view class="st-card">
        <view class="st-icon si-green">✅</view>
        <text class="st-val green">{{ metrics.assistance }}%</text>
        <text class="st-lbl">帮扶完成率</text>
      </view>
      <view class="st-card">
        <view class="st-icon si-purple">🎓</view>
        <text class="st-val purple">{{ metrics.achievement }}%</text>
        <text class="st-lbl">学分达标率</text>
      </view>
    </view>

    <!-- 预警分布 -->
    <view class="card">
      <text class="card-title">预警等级分布</text>
      <view v-if="warnDist.length" class="dist-list">
        <view v-for="w in warnDist" :key="w.warning_level" class="dist-row">
          <view class="dr-label-wrap">
            <view class="dr-dot" :style="{background: w.warning_level >= 3 ? '#ef4444' : w.warning_level >= 2 ? '#f59e0b' : '#3b82f6'}"></view>
            <text class="dr-label" :style="{color: w.warning_level >= 3 ? '#ef4444' : w.warning_level >= 2 ? '#f59e0b' : '#3b82f6'}">
              {{ w.warning_level >= 3 ? '严重预警' : w.warning_level >= 2 ? '中度预警' : '轻度预警' }}
            </text>
          </view>
          <view class="dr-bar-wrap">
            <view class="dr-bar" :style="{width: maxPct(w.count) + '%', background: w.warning_level >= 3 ? '#ef4444' : w.warning_level >= 2 ? '#f59e0b' : '#3b82f6'}"></view>
          </view>
          <text class="dr-count">{{ w.count || 0 }}条</text>
        </view>
      </view>
      <view v-else class="empty-sm">暂无预警数据</view>
    </view>

    <!-- 班级快速概览 -->
    <view class="card">
      <text class="card-title">班级快速概览</text>
      <view v-if="classSummary.length" class="summary-grid">
        <view v-for="c in classSummary" :key="c.className" class="sum-item">
          <text class="sum-name">{{ c.className }}</text>
          <text class="sum-val" :class="(c.warningRate || 0) > 30 ? 'danger' : 'safe'">{{ c.warningRate || 0 }}%</text>
          <text class="sum-lbl">预警率</text>
        </view>
      </view>
      <view v-else class="empty-sm">暂无班级数据</view>
    </view>
  </view>
</template>

<script>
import { counselorAPI } from '@/services/api.js'

export default {
  data() {
    return {
      metrics: { insufficient: 0, efficiency: 0, assistance: 0, achievement: 0 },
      warnDist: [],
      classSummary: []
    }
  },
  onShow() { this.loadData() },
  methods: {
    async loadData() {
      try {
        const uid = uni.getStorageSync('counselorId') || uni.getStorageSync('userId'); if (!uid) return
        const [ir, dis, er, cr, cs] = await Promise.all([
          counselorAPI.getCreditInsufficientRate(uid).catch(() => null),
          counselorAPI.getWarningDistribution(uid).catch(() => []),
          counselorAPI.getHandlingEfficiency(uid).catch(() => null),
          counselorAPI.getAssistanceCompletion(uid).catch(() => null),
          counselorAPI.getClassSummary(uid).catch(() => [])
        ])
        if (ir) this.metrics.insufficient = Math.round(ir.insufficientRate || 0)
        if (er) this.metrics.efficiency = Math.round(er.avg_handle_days || 0)
        if (cr) this.metrics.assistance = Math.round(cr.completionRate || 0)
        this.metrics.achievement = 100 - (this.metrics.insufficient || 0)
        this.warnDist = Array.isArray(dis) ? dis : (dis?.data || [])
        this.classSummary = Array.isArray(cs) ? cs : (cs?.data || [])
      } catch (e) {}
    },
    maxPct(count) {
      if (!this.warnDist.length) return 0
      const max = Math.max(...this.warnDist.map(w => w.count || 0), 1)
      return Math.max(5, (count || 0) / max * 100)
    }
  }
}
</script>

<style scoped>
.page { padding: 20rpx; background: #f0f2f8; min-height: 100vh; }
.header { margin-bottom: 16rpx; }
.title { font-size: 36rpx; font-weight: 800; color: #1e293b; display: block; }
.sub { font-size: 24rpx; color: #94a3b8; }

.stats-grid { display: grid; grid-template-columns: 1fr 1fr 1fr 1fr; gap: 12rpx; margin-bottom: 18rpx; }
.st-card { background: #fff; border-radius: 14rpx; padding: 18rpx 8rpx; text-align: center; }
.st-icon { width: 48rpx; height: 48rpx; border-radius: 12rpx; display: flex; align-items: center; justify-content: center; margin: 0 auto 8rpx; font-size: 22rpx; }
.si-red { background: #fef2f2; } .si-blue { background: #eff6ff; } .si-green { background: #ecfdf5; } .si-purple { background: #eff6ff; }
.st-val { font-size: 28rpx; font-weight: 800; color: #1e293b; display: block; }
.st-val.red { color: #ef4444; } .st-val.green { color: #16a34a; } .st-val.purple { color: #2563eb; }
.st-lbl { font-size: 20rpx; color: #94a3b8; }

.card { background: #fff; border-radius: 16rpx; padding: 24rpx; margin-bottom: 16rpx; }
.card-title { font-size: 28rpx; font-weight: 700; color: #1e293b; display: block; margin-bottom: 16rpx; }

.dist-list { display: flex; flex-direction: column; gap: 14rpx; }
.dist-row { display: flex; align-items: center; gap: 12rpx; }
.dr-label-wrap { display: flex; align-items: center; gap: 8rpx; width: 140rpx; flex-shrink: 0; }
.dr-dot { width: 10rpx; height: 10rpx; border-radius: 50%; }
.dr-label { font-size: 24rpx; font-weight: 600; }
.dr-bar-wrap { flex: 1; height: 24rpx; background: #f1f5f9; border-radius: 12rpx; overflow: hidden; }
.dr-bar { height: 100%; border-radius: 12rpx; min-width: 4rpx; transition: width 0.6s ease; }
.dr-count { font-size: 24rpx; font-weight: 600; color: #64748b; width: 56rpx; text-align: right; flex-shrink: 0; }

.summary-grid { display: grid; grid-template-columns: 1fr 1fr 1fr; gap: 12rpx; }
.sum-item { background: #f8fafc; border-radius: 12rpx; padding: 16rpx; text-align: center; }
.sum-name { font-size: 22rpx; color: #64748b; display: block; margin-bottom: 6rpx; }
.sum-val { font-size: 28rpx; font-weight: 700; display: block; }
.sum-val.danger { color: #ef4444; } .sum-val.safe { color: #16a34a; }
.sum-lbl { font-size: 18rpx; color: #94a3b8; }

.empty-sm { text-align: center; padding: 36rpx; color: #94a3b8; font-size: 24rpx; }
</style>
