<template>
  <view class="page">
    <view class="header"><text class="title">系统日志</text><text class="sub">操作审计记录</text></view>

    <view class="filter-bar">
      <picker :value="typeIdx" :range="typeOptions" @change="typeIdx=Number($event.detail.value)">
        <view class="picker-btn"><text class="pl">{{ typeOptions[typeIdx] }}</text><text class="pa">▾</text></view>
      </picker>
    </view>

    <scroll-view v-if="logs.length" scroll-y class="list">
      <view v-for="l in logs" :key="l.id" class="card">
        <view class="card-head">
          <text class="lh-user">{{ l.username || '未知' }}</text>
          <text class="lh-time">{{ fmtTime(l.createdAt || l.time) }}</text>
        </view>
        <text class="lh-action">{{ l.action || l.description }}</text>
        <view class="lh-meta">
          <text class="lh-ip" v-if="l.ip">IP: {{ l.ip }}</text>
          <text class="lh-type">{{ l.type || '操作' }}</text>
        </view>
      </view>
    </scroll-view>
    <view v-else class="empty"><text class="empty-icon">📋</text><text class="empty-text">暂无日志记录</text></view>
  </view>
</template>

<script>
import { adminAPI } from '@/services/api.js'

export default {
  data() { return { typeIdx: 0, typeOptions: ['全部','登录','操作','错误'], logs: [] } },
  onShow() { this.loadData() },
  methods: {
    async loadData() {
      try {
        const res = await adminAPI.getLogs()
        this.logs = Array.isArray(res) ? res : (res?.data || [])
      } catch (e) {}
    },
    fmtTime(d) {
      if (!d) return ''
      const t = new Date(d)
      return `${t.getMonth()+1}/${t.getDate()} ${String(t.getHours()).padStart(2,'0')}:${String(t.getMinutes()).padStart(2,'0')}`
    }
  }
}
</script>

<style scoped>
.page { padding:20rpx; background:#f0f2f8; min-height:100vh; }
.header { margin-bottom:20rpx; } .title { font-size:36rpx; font-weight:800; color:#1e293b; display:block; } .sub { font-size:24rpx; color:#94a3b8; }
.filter-bar { margin-bottom:18rpx; }
.picker-btn { background:#fff; border-radius:14rpx; height:72rpx; padding:0 24rpx; display:inline-flex; align-items:center; gap:8rpx; }
.pl { font-size:24rpx; color:#475569; } .pa { font-size:20rpx; color:#94a3b8; }
.list { max-height:calc(100vh - 240rpx); }
.card { background:#fff; border-radius:12rpx; padding:18rpx 20rpx; margin-bottom:10rpx; }
.card-head { display:flex; justify-content:space-between; margin-bottom:8rpx; }
.lh-user { font-size:26rpx; font-weight:600; color:#1e293b; } .lh-time { font-size:20rpx; color:#94a3b8; }
.lh-action { font-size:24rpx; color:#475569; display:block; }
.lh-meta { display:flex; gap:20rpx; margin-top:8rpx; }
.lh-ip { font-size:20rpx; color:#94a3b8; } .lh-type { font-size:20rpx; padding:2rpx 12rpx; background:#f1f5f9; border-radius:6rpx; color:#64748b; }
.empty { text-align:center; padding:120rpx 0; } .empty-icon { font-size:64rpx; display:block; margin-bottom:16rpx; } .empty-text { font-size:26rpx; color:#94a3b8; }
</style>
