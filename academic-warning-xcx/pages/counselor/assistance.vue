<template>
  <view class="page">
    <view class="header">
      <text class="title">帮扶计划</text>
      <text class="sub">学生学业帮扶管理</text>
    </view>

    <!-- 统计 -->
    <view class="stats-row">
      <view class="st-card" :class="{active: activeTab==='all'}" @tap="activeTab='all'">
        <text class="st-num">{{ list.length }}</text><text class="st-lbl">全部</text>
      </view>
      <view class="st-card" :class="{active: activeTab==='1'}" @tap="activeTab='1'">
        <text class="st-num green">{{ active }}</text><text class="st-lbl">进行中</text>
      </view>
      <view class="st-card" :class="{active: activeTab==='2'}" @tap="activeTab='2'">
        <text class="st-num blue">{{ completed }}</text><text class="st-lbl">已完成</text>
      </view>
    </view>

    <scroll-view v-if="filtered.length" scroll-y class="list">
      <view v-for="p in filtered" :key="p.id" class="card">
        <view class="card-left-strip" :style="{background: p.status === 1 ? '#16a34a' : p.status === 2 ? '#2563eb' : '#f59e0b'}"></view>
        <view class="card-body">
          <view class="card-top">
            <view class="av">{{ (p.studentName || '学')[0] }}</view>
            <view class="info">
              <text class="pname">{{ p.studentName || '学生' + p.studentId }}</text>
              <text class="ptitle">{{ p.title || '帮扶计划' }}</text>
              <text class="pdesc">{{ p.description || '' }}</text>
            </view>
            <text class="pstatus" :style="{color: statusColor(p.status)}">{{ statusText(p.status) }}</text>
          </view>
          <view class="prog-row">
            <view class="prog-label">进度</view>
            <view class="prog-track">
              <view class="prog-fill" :style="{width: (p.progressPercentage || 0) + '%', background: p.status === 2 ? '#16a34a' : 'linear-gradient(90deg,#2563eb,#3b82f6)'}"></view>
            </view>
            <text class="prog-pct">{{ p.progressPercentage || 0 }}%</text>
          </view>
          <view class="card-acts">
            <text class="act-link" @tap="updateProgress(p)">更新进度</text>
            <text class="act-link" @tap="viewDetail(p)">详情</text>
          </view>
        </view>
      </view>
    </scroll-view>
    <view v-else class="empty">
      <text class="empty-icon">🎯</text>
      <text class="empty-text">暂无帮扶计划</text>
    </view>

    <counselor-tab-bar current="pages/counselor/assistance" />

    <!-- 更新进度弹窗 -->
    <view v-if="showUpdate" class="modal-mask" @tap="showUpdate = null">
      <view class="modal-card" @tap.stop>
        <text class="modal-title">更新帮扶进度</text>
        <text class="modal-student">{{ editingPlan?.studentName }}</text>
        <view class="prog-big">
          <text class="prog-big-val">{{ updateProgress }}</text>
          <text class="prog-big-pct">%</text>
        </view>
        <slider :value="updateProgress" @change="updateProgress = $event.detail.value" min="0" max="100" step="5" activeColor="#2563eb" backgroundColor="#e2e8f0" block-size="24" style="width:100%" />
        <view class="prog-scale">
          <text>0</text><text>25</text><text>50</text><text>75</text><text>100</text>
        </view>
        <view class="modal-btns">
          <button class="mb-cancel" @tap="showUpdate = null">取消</button>
          <button class="mb-confirm" @tap="doUpdate">确认更新</button>
        </view>
      </view>
    </view>
  </view>
</template>

<script>
import { counselorAPI } from '@/services/api.js'

export default {
  data() {
    return {
      activeTab: 'all',
      list: [],
      showUpdate: false,
      editingPlan: null,
      updateProgress: 0
    }
  },
  computed: {
    filtered() {
      if (this.activeTab === 'all') return this.list
      return this.list.filter(p => p.status === Number(this.activeTab))
    },
    active() { return this.list.filter(p => p.status === 1).length },
    completed() { return this.list.filter(p => p.status === 2).length }
  },
  onShow() { this.loadData() },
  methods: {
    async loadData() {
      try {
        const uid = uni.getStorageSync('counselorId') || uni.getStorageSync('userId'); if (!uid) return
        const res = await counselorAPI.getCounselorPlans(uid)
        const arr = Array.isArray(res) ? res : (res?.data || [])
        this.list = arr.map(p => ({
          ...p,
          title: (p.content || '').substring(0, 18) || `计划#${p.id}`,
          description: (p.content || '').substring(19, 60),
          progressPercentage: p.progress || 0
        }))
      } catch (e) {}
    },
    statusColor(s) { return s === 1 ? '#16a34a' : s === 2 ? '#2563eb' : '#f59e0b' },
    statusText(s) { return s === 1 ? '进行中' : s === 2 ? '已完成' : '待开始' },
    updateProgress(p) {
      this.editingPlan = p
      this.updateProgress = p.progressPercentage || 0
      this.showUpdate = true
    },
    async doUpdate() {
      try {
        await counselorAPI.updatePlanProgress(this.editingPlan.id, { progress: this.updateProgress })
        uni.showToast({ title: '已更新', icon: 'success' })
        this.showUpdate = false
        this.loadData()
      } catch (e) { uni.showToast({ title: '更新失败', icon: 'none' }) }
    },
    viewDetail(p) {
      uni.showModal({
        title: p.title || '帮扶计划',
        content: `学生: ${p.studentName}\n进度: ${p.progressPercentage || 0}%\n状态: ${this.statusText(p.status)}\n${p.description || ''}`,
        showCancel: false
      })
    }
  }
}
</script>

<style scoped>
.page { padding: 20rpx; background: #f0f2f8; min-height: 100vh; padding-bottom: 120rpx; }
.header { margin-bottom: 16rpx; }
.title { font-size: 36rpx; font-weight: 800; color: #1e293b; display: block; }
.sub { font-size: 24rpx; color: #94a3b8; }

.stats-row { display: grid; grid-template-columns: 1fr 1fr 1fr; gap: 14rpx; margin-bottom: 18rpx; }
.st-card { background: #fff; border-radius: 14rpx; padding: 20rpx; text-align: center; border: 2rpx solid transparent; }
.st-card.active { border-color: #2563eb; background: #eff6ff; }
.st-num { font-size: 32rpx; font-weight: 800; color: #1e293b; display: block; }
.st-num.green { color: #16a34a; } .st-num.blue { color: #2563eb; }
.st-lbl { font-size: 20rpx; color: #94a3b8; }

.list { max-height: calc(100vh - 260rpx); }
.card { background: #fff; border-radius: 14rpx; margin-bottom: 14rpx; display: flex; overflow: hidden; box-shadow: 0 1rpx 4rpx rgba(0,0,0,0.02); }
.card-left-strip { width: 6rpx; flex-shrink: 0; }
.card-body { flex: 1; padding: 18rpx 20rpx; }
.card-top { display: flex; align-items: flex-start; gap: 12rpx; margin-bottom: 14rpx; }
.av { width: 56rpx; height: 56rpx; border-radius: 50%; background: linear-gradient(135deg, #7c3aed, #a78bfa); color: #fff; font-size: 24rpx; font-weight: 700; display: flex; align-items: center; justify-content: center; flex-shrink: 0; }
.info { flex: 1; min-width: 0; }
.pname { font-size: 26rpx; font-weight: 600; color: #1e293b; display: block; }
.ptitle { font-size: 22rpx; color: #64748b; display: block; margin-top: 2rpx; }
.pdesc { font-size: 20rpx; color: #94a3b8; display: block; margin-top: 2rpx; overflow: hidden; text-overflow: ellipsis; white-space: nowrap; }
.pstatus { font-size: 22rpx; font-weight: 600; flex-shrink: 0; }

.prog-row { display: flex; align-items: center; gap: 10rpx; margin-bottom: 12rpx; }
.prog-label { font-size: 20rpx; color: #94a3b8; width: 48rpx; }
.prog-track { flex: 1; height: 10rpx; background: #f1f5f9; border-radius: 5rpx; overflow: hidden; }
.prog-fill { height: 100%; border-radius: 5rpx; transition: width 0.5s ease; }
.prog-pct { font-size: 20rpx; font-weight: 600; color: #64748b; width: 56rpx; text-align: right; }

.card-acts { display: flex; gap: 24rpx; }
.act-link { font-size: 24rpx; color: #7c3aed; font-weight: 500; }

.empty { text-align: center; padding: 100rpx 0; }
.empty-icon { font-size: 56rpx; display: block; margin-bottom: 12rpx; }
.empty-text { font-size: 26rpx; color: #94a3b8; }

.modal-mask { position: fixed; inset: 0; background: rgba(0,0,0,0.5); z-index: 999; display: flex; align-items: center; justify-content: center; padding: 40rpx; }
.modal-card { background: #fff; border-radius: 20rpx; padding: 36rpx; width: 100%; max-width: 560rpx; }
.modal-title { font-size: 30rpx; font-weight: 700; color: #1e293b; display: block; }
.modal-student { font-size: 24rpx; color: #94a3b8; display: block; margin-top: 6rpx; margin-bottom: 20rpx; }
.prog-big { text-align: center; margin-bottom: 20rpx; }
.prog-big-val { font-size: 72rpx; font-weight: 800; color: #7c3aed; }
.prog-big-pct { font-size: 32rpx; color: #94a3b8; margin-left: 4rpx; }
.prog-scale { display: flex; justify-content: space-between; padding: 0 10rpx; margin-bottom: 24rpx; }
.prog-scale text { font-size: 20rpx; color: #cbd5e1; }
.modal-btns { display: flex; gap: 16rpx; margin-top: 24rpx; }
.mb-cancel { flex: 1; height: 80rpx; background: #f1f5f9; color: #64748b; border: none; border-radius: 14rpx; font-size: 28rpx; display: flex; align-items: center; justify-content: center; }
.mb-confirm { flex: 1; height: 80rpx; background: linear-gradient(135deg, #7c3aed, #6366f1); color: #fff; border: none; border-radius: 14rpx; font-size: 28rpx; font-weight: 600; display: flex; align-items: center; justify-content: center; }
</style>
