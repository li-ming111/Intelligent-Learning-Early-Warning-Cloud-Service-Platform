<template>
  <view class="page">
    <view class="header"><text class="title">部门管理</text><text class="sub">学院与专业信息</text></view>
    <view v-if="departments.length" class="list">
      <view v-for="d in departments" :key="d.id" class="card">
        <view class="card-head">
          <view class="icon">🏫</view>
          <view class="info">
            <text class="name">{{ d.name || d.collegeName }}</text>
            <text class="sub">{{ d.majorCount || 0 }}个专业 · {{ d.classCount || 0 }}个班级</text>
          </view>
          <text class="arrow">›</text>
        </view>
        <view v-if="d.majors && d.majors.length" class="majors">
          <text v-for="m in d.majors" :key="m.name" class="major-tag">{{ m.name }}</text>
        </view>
      </view>
    </view>
    <view v-else class="empty"><text class="empty-icon">🏫</text><text class="empty-text">暂无部门数据</text></view>
  </view>
</template>

<script>
import { adminAPI } from '@/services/api.js'

export default {
  data() { return { departments: [] } },
  onShow() { this.loadData() },
  methods: {
    async loadData() {
      try {
        const res = await adminAPI.getDepartments()
        this.departments = Array.isArray(res) ? res : (res?.data || [])
      } catch (e) {}
    }
  }
}
</script>

<style scoped>
.page { padding:20rpx; background:#f0f2f8; min-height:100vh; }
.header { margin-bottom:20rpx; } .title { font-size:36rpx; font-weight:800; color:#1e293b; display:block; } .sub { font-size:24rpx; color:#94a3b8; }
.list { display:flex; flex-direction:column; gap:14rpx; }
.card { background:#fff; border-radius:16rpx; padding:24rpx; }
.card-head { display:flex; align-items:center; gap:14rpx; }
.icon { width:64rpx; height:64rpx; border-radius:16rpx; background:#f0fdfa; display:flex; align-items:center; justify-content:center; font-size:30rpx; flex-shrink:0; }
.info { flex:1; } .name { font-size:28rpx; font-weight:600; color:#1e293b; display:block; } .sub { font-size:22rpx; color:#94a3b8; }
.arrow { font-size:36rpx; color:#cbd5e1; }
.majors { display:flex; flex-wrap:wrap; gap:8rpx; margin-top:16rpx; padding-top:16rpx; border-top:1rpx solid #f1f5f9; }
.major-tag { font-size:20rpx; padding:4rpx 14rpx; background:#f1f5f9; border-radius:8rpx; color:#475569; }
.empty { text-align:center; padding:120rpx 0; } .empty-icon { font-size:64rpx; display:block; margin-bottom:16rpx; } .empty-text { font-size:26rpx; color:#94a3b8; }
</style>
