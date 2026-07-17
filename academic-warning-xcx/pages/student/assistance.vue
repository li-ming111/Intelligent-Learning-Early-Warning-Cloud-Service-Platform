<template>
  <view class="page">
    <view class="blue-bar">
      <text class="bar-title">帮扶计划</text>
      <text class="bar-sub">学业帮扶进度跟踪</text>
    </view>

    <!-- 统计 -->
    <view class="stat-row" v-if="plans.length > 0">
      <view class="stat-box">
        <text class="stat-num">{{ plans.length }}</text>
        <text class="stat-lbl">全部</text>
      </view>
      <view class="stat-box active">
        <text class="stat-num blue">{{ stats.active }}</text>
        <text class="stat-lbl">进行中</text>
      </view>
      <view class="stat-box done">
        <text class="stat-num green">{{ stats.done }}</text>
        <text class="stat-lbl">已完成</text>
      </view>
    </view>

    <!-- 列表 -->
    <view v-if="plans.length > 0" class="list">
      <view v-for="plan in plans" :key="plan.id" class="card">
        <view class="card-left" :class="statusBg(plan.status)"></view>
        <view class="card-body">
          <!-- 标题行 -->
          <view class="card-head">
            <text class="card-title">{{ plan.title }}</text>
            <view :class="['tag', statusClass(plan.status)]">{{ statusText(plan.status) }}</view>
          </view>
          <text class="card-desc">{{ plan.description || plan.goal }}</text>

          <!-- 进度条 -->
          <view class="prog-row">
            <view class="prog-track">
              <view class="prog-fill" :class="progColor(plan.progress)" :style="{ width: (plan.progress || 0) + '%' }"></view>
            </view>
            <text class="prog-pct">{{ plan.progress || 0 }}%</text>
          </view>

          <!-- 日期 + 操作 -->
          <view class="card-foot">
            <text class="foot-date">{{ fmtDate(plan.startDate) }} — {{ fmtDate(plan.endDate) }}</text>
            <view class="foot-actions">
              <text class="act-link" @tap="viewDetail(plan)">详情</text>
              <text v-if="plan.status !== 'completed'" class="act-link primary" @tap="openReport(plan)">更新</text>
            </view>
          </view>
        </view>
      </view>
    </view>

    <!-- 空状态 -->
    <view v-else class="empty">
      <text class="empty-icon">📋</text>
      <text class="empty-title">暂无帮扶计划</text>
      <text class="empty-sub">辅导员暂未为您制定帮扶计划</text>
    </view>

    <!-- 详情弹窗 -->
    <view v-if="showDetail" class="overlay" @tap="showDetail = false">
      <view class="modal" @tap.stop>
        <view class="modal-hd">
          <text class="modal-title">{{ selectedPlan?.title }}</text>
          <text class="modal-close" @tap="showDetail = false">✕</text>
        </view>
        <view class="modal-bd">
          <view class="mb-row">
            <text class="mb-lbl">状态</text>
            <view :class="['tag', statusClass(selectedPlan?.status)]">{{ statusText(selectedPlan?.status) }}</view>
          </view>
          <view class="mb-row">
            <text class="mb-lbl">帮扶目标</text>
            <text class="mb-val">{{ selectedPlan?.goal || selectedPlan?.description }}</text>
          </view>
          <view class="mb-row">
            <text class="mb-lbl">执行周期</text>
            <text class="mb-val">{{ fmtDate(selectedPlan?.startDate) }} — {{ fmtDate(selectedPlan?.endDate) }}</text>
          </view>
          <view class="mb-row">
            <text class="mb-lbl">完成进度</text>
            <view class="prog-row">
              <view class="prog-track">
                <view class="prog-fill" :class="progColor(selectedPlan?.progress)" :style="{ width: (selectedPlan?.progress || 0) + '%' }"></view>
              </view>
              <text class="prog-pct">{{ selectedPlan?.progress || 0 }}%</text>
            </view>
          </view>
        </view>
        <button class="m-btn" @tap="showDetail = false">关闭</button>
      </view>
    </view>

    <!-- 更新进度弹窗 -->
    <view v-if="showReport" class="overlay" @tap="showReport = false">
      <view class="modal" @tap.stop>
        <view class="modal-hd">
          <text class="modal-title">更新进度</text>
          <text class="modal-close" @tap="showReport = false">✕</text>
        </view>
        <view class="modal-bd">
          <text class="slider-val">{{ reportProgress }}%</text>
          <slider
            :value="reportProgress"
            min="0" max="100"
            show-value
            block-size="20"
            activeColor="#3b5ce8"
            backgroundColor="#e5e5ea"
            @change="onSlider"
            style="width:100%;margin:16rpx 0;"
          />
          <view class="slider-marks">
            <text>0%</text>
            <text>25%</text>
            <text>50%</text>
            <text>75%</text>
            <text>100%</text>
          </view>
        </view>
        <view class="modal-ft">
          <button class="m-btn outline" @tap="showReport = false">取消</button>
          <button class="m-btn" @tap="confirmReport">确认更新</button>
        </view>
      </view>
    </view>
  </view>
</template>

<script>
import { studentAPI } from '@/services/api.js'

export default {
  data() {
    return {
      plans: [],
      showDetail: false,
      showReport: false,
      selectedPlan: null,
      reportProgress: 0,
      reportPlanId: null
    }
  },
  computed: {
    stats() {
      const active = this.plans.filter(p => p.status === 'active').length
      const done = this.plans.filter(p => p.status === 'completed').length
      return { active, done }
    }
  },
  onLoad() {
    this.load()
  },
  methods: {
    async load() {
      const uid = uni.getStorageSync('userId')
      if (!uid) return
      try {
        const res = await studentAPI.getAssistancePlans(uid)
        this.plans = Array.isArray(res) ? res : (res?.data || [])
      } catch (e) {}
    },
    viewDetail(plan) {
      this.selectedPlan = plan
      this.showDetail = true
    },
    openReport(plan) {
      this.reportPlanId = plan.id
      this.reportProgress = plan.progress || 0
      this.showReport = true
    },
    onSlider(e) {
      this.reportProgress = e.detail.value
    },
    async confirmReport() {
      try {
        await studentAPI.updatePlanProgress(this.reportPlanId, this.reportProgress)
        const plan = this.plans.find(p => p.id === this.reportPlanId)
        if (plan) {
          plan.progress = this.reportProgress
          if (this.reportProgress >= 100) plan.status = 'completed'
        }
        this.showReport = false
        uni.showToast({ title: '已更新', icon: 'success' })
      } catch (e) {
        uni.showToast({ title: '更新失败', icon: 'none' })
      }
    },
    statusClass(s) {
      return s === 'active' ? 'active' : s === 'completed' ? 'done' : 'pending'
    },
    statusText(s) {
      return s === 'active' ? '进行中' : s === 'completed' ? '已完成' : '待开始'
    },
    statusBg(s) {
      return s === 'active' ? 'bg-blue' : s === 'completed' ? 'bg-green' : 'bg-gray'
    },
    progColor(p) {
      const v = Number(p) || 0
      return v >= 100 ? 'green' : v >= 50 ? 'blue' : 'blue'
    },
    fmtDate(d) {
      if (!d) return ''
      return String(d).substring(0, 10)
    }
  }
}
</script>

<style scoped>
.page { min-height: 100vh; padding: 0 24rpx 24rpx; }

.blue-bar {
  background: linear-gradient(135deg, #3b5ce8 0%, #5b7cf0 100%);
  margin: 0 -24rpx 16rpx; padding: 44rpx 28rpx 36rpx;
}
.bar-title { display: block; font-size: 34rpx; font-weight: 700; color: #fff; }
.bar-sub { display: block; font-size: 22rpx; color: rgba(255,255,255,0.7); margin-top: 6rpx; }

/* 统计 */
.stat-row { display: flex; gap: 12rpx; margin-bottom: 16rpx; }
.stat-box { flex:1; background:#fff; border-radius:14rpx; padding:20rpx 8rpx; text-align:center; }
.stat-num { display:block; font-size:36rpx; font-weight:700; color:#1d1d1f; }
.stat-num.blue { color:#3b5ce8; }
.stat-num.green { color:#22c55e; }
.stat-lbl { display:block; font-size:20rpx; color:#86868b; margin-top:4rpx; }

/* 列表 */
.list { display:flex; flex-direction:column; gap:12rpx; }

/* 卡片 */
.card { background:#fff; border-radius:14rpx; display:flex; overflow:hidden; }
.card-left { width:8rpx; flex-shrink:0; }
.card-left.bg-blue { background:#3b5ce8; }
.card-left.bg-green { background:#22c55e; }
.card-left.bg-gray { background:#e5e5ea; }
.card-body { flex:1; padding:20rpx 20rpx 16rpx 16rpx; }
.card-head { display:flex; align-items:center; gap:10rpx; margin-bottom:6rpx; }
.card-title { flex:1; font-size:28rpx; font-weight:600; color:#1d1d1f; }
.tag { font-size:20rpx; padding:4rpx 12rpx; border-radius:8rpx; font-weight:500; flex-shrink:0; }
.tag.active { background:#eef1ff; color:#3b5ce8; }
.tag.done { background:#f0fdf4; color:#22c55e; }
.tag.pending { background:#f5f5f7; color:#86868b; }
.card-desc { display:block; font-size:24rpx; color:#86868b; line-height:34rpx; margin-bottom:12rpx; }

/* 进度 */
.prog-row { display:flex; align-items:center; gap:10rpx; margin-bottom:8rpx; }
.prog-track { flex:1; height:8rpx; background:#f0f0f2; border-radius:4rpx; overflow:hidden; }
.prog-fill { height:100%; border-radius:4rpx; transition:width 0.4s; }
.prog-fill.blue { background:#3b5ce8; }
.prog-fill.green { background:#22c55e; }
.prog-pct { font-size:20rpx; font-weight:600; color:#86868b; min-width:40rpx; }

.card-foot { display:flex; justify-content:space-between; align-items:center; }
.foot-date { font-size:20rpx; color:#aeaeb2; }
.foot-actions { display:flex; gap:20rpx; }
.act-link { font-size:22rpx; color:#86868b; }
.act-link.primary { color:#3b5ce8; font-weight:500; }

/* 空 */
.empty { padding:80rpx 24rpx; text-align:center; }
.empty-icon { font-size:60rpx; display:block; margin-bottom:16rpx; }
.empty-title { display:block; font-size:28rpx; font-weight:600; color:#1d1d1f; margin-bottom:8rpx; }
.empty-sub { display:block; font-size:24rpx; color:#aeaeb2; }

/* 弹窗 */
.overlay { position:fixed; top:0;left:0;right:0;bottom:0; background:rgba(0,0,0,0.4); display:flex; align-items:center; justify-content:center; z-index:9999; }
.modal { width:86%; background:#fff; border-radius:20rpx; padding:32rpx; }
.modal-hd { display:flex; justify-content:space-between; align-items:center; margin-bottom:24rpx; padding-bottom:16rpx; border-bottom:1rpx solid #f0f0f2; }
.modal-title { font-size:28rpx; font-weight:700; color:#1d1d1f; }
.modal-close { font-size:28rpx; color:#aeaeb2; padding:8rpx; }
.modal-bd { margin-bottom:24rpx; }
.mb-row { margin-bottom:18rpx; }
.mb-lbl { display:block; font-size:24rpx; color:#86868b; margin-bottom:6rpx; }
.mb-val { display:block; font-size:26rpx; color:#1d1d1f; line-height:38rpx; }

.slider-val { display:block; text-align:center; font-size:40rpx; font-weight:700; color:#3b5ce8; }
.slider-marks { display:flex; justify-content:space-between; font-size:20rpx; color:#aeaeb2; padding:0 4rpx; }

.modal-ft { display:flex; gap:16rpx; }
.modal-ft .m-btn { flex:1; }
.m-btn { height:80rpx; line-height:80rpx; background:#3b5ce8; color:#fff; border:none; border-radius:12rpx; font-size:28rpx; font-weight:600; text-align:center; }
.m-btn.outline { background:#f5f5f7; color:#1d1d1f; }
</style>
