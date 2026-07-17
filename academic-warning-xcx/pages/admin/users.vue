<template>
  <view class="page">
    <view class="header"><text class="title">用户管理</text><text class="sub">管理系统所有用户</text></view>

    <view class="search-bar">
      <view class="search-wrap">
        <text class="search-icon">🔍</text>
        <input v-model="keyword" class="search-inp" placeholder="搜索用户名或姓名" @confirm="loadData" />
      </view>
      <picker :value="roleIdx" :range="roleOptions" @change="roleIdx=Number($event.detail.value);loadData()">
        <view class="picker-btn"><text class="pl">{{ roleOptions[roleIdx] }}</text><text class="pa">▾</text></view>
      </picker>
    </view>

    <scroll-view v-if="users.length" scroll-y class="list">
      <view v-for="u in users" :key="u.id" class="card">
        <view class="av" :style="{background:roleGrad(u.role)}">{{ (u.username || '?')[0].toUpperCase() }}</view>
        <view class="info">
          <text class="uname">{{ u.username }}</text>
          <text class="urole" :style="{color:roleColor(u.role)}">{{ roleName(u.role) }}</text>
        </view>
        <view class="acts">
          <text class="act-btn edit" @tap="editUser(u)">编辑</text>
          <text class="act-btn del" @tap="delUser(u)">删除</text>
        </view>
      </view>
    </scroll-view>
    <view v-else class="empty"><text class="empty-icon">👤</text><text class="empty-text">暂无用户数据</text></view>

    <admin-tab-bar current="pages/admin/users" />
  </view>
</template>

<script>
import { adminAPI } from '@/services/api.js'

export default {
  data() {
    return { keyword: '', roleIdx: 0, roleOptions: ['全部角色','学生','教师','辅导员','管理员'], users: [] }
  },
  onShow() { this.loadData() },
  methods: {
    async loadData() {
      try {
        const params = {}
        if (this.roleIdx > 0) params.role = this.roleIdx
        if (this.keyword) params.keyword = this.keyword
        const res = await adminAPI.getUsers(params)
        this.users = Array.isArray(res) ? res : (res?.data || [])
      } catch (e) {}
    },
    roleName(r) {
      const map = { 1:'学生', 2:'教师', 3:'辅导员', 4:'管理员', 5:'学院管理员' }
      return map[String(r)] || map[r] || '未知'
    },
    roleColor(r) {
      const map = { 1:'#2563eb', 2:'#16a34a', 3:'#7c3aed', 4:'#1e293b', 5:'#0891b2' }
      return map[String(r)] || '#94a3b8'
    },
    roleGrad(r) {
      const map = { 1:'linear-gradient(135deg,#2563eb,#3b82f6)', 2:'linear-gradient(135deg,#16a34a,#22c55e)', 3:'linear-gradient(135deg,#7c3aed,#a78bfa)', 4:'linear-gradient(135deg,#1e293b,#475569)', 5:'linear-gradient(135deg,#0f766e,#14b8a6)' }
      return map[String(r)] || 'linear-gradient(135deg,#94a3b8,#cbd5e1)'
    },
    editUser(u) { uni.showModal({ title:'编辑用户', content:`用户名: ${u.username}\n角色: ${this.roleName(u.role)}`, showCancel:false }) },
    delUser(u) {
      uni.showModal({
        title: '确认删除', content: `确定删除用户 "${u.username}"？`,
        success: async (r) => {
          if (r.confirm) {
            try { await adminAPI.deleteUser(u.id); uni.showToast({title:'已删除'}); this.loadData() }
            catch (e) { uni.showToast({title:'删除失败',icon:'none'}) }
          }
        }
      })
    }
  }
}
</script>

<style scoped>
.page { padding:20rpx; background:#f0f2f8; min-height:100vh; padding-bottom:120rpx; }
.header { margin-bottom:20rpx; } .title { font-size:36rpx; font-weight:800; color:#1e293b; display:block; } .sub { font-size:24rpx; color:#94a3b8; }
.search-bar { display:flex; gap:14rpx; margin-bottom:18rpx; }
.search-wrap { flex:1; position:relative; display:flex; align-items:center; background:#fff; border-radius:14rpx; padding:0 16rpx; }
.search-icon { font-size:28rpx; margin-right:8rpx; } .search-inp { flex:1; height:72rpx; font-size:26rpx; }
.picker-btn { background:#fff; border-radius:14rpx; height:72rpx; padding:0 20rpx; display:flex; align-items:center; gap:8rpx; }
.pl { font-size:24rpx; color:#475569; } .pa { font-size:20rpx; color:#94a3b8; }
.list { max-height:calc(100vh - 240rpx); }
.card { background:#fff; border-radius:14rpx; padding:18rpx 20rpx; margin-bottom:10rpx; display:flex; align-items:center; gap:14rpx; }
.av { width:64rpx; height:64rpx; border-radius:50%; color:#fff; font-size:26rpx; font-weight:700; display:flex; align-items:center; justify-content:center; flex-shrink:0; }
.info { flex:1; } .uname { font-size:26rpx; font-weight:600; color:#1e293b; display:block; } .urole { font-size:22rpx; font-weight:500; display:block; margin-top:2rpx; }
.acts { display:flex; gap:16rpx; } .act-btn { font-size:24rpx; padding:8rpx 16rpx; border-radius:8rpx; font-weight:500; }
.edit { color:#2563eb; background:#eff6ff; } .del { color:#ef4444; background:#fef2f2; }
.empty { text-align:center; padding:120rpx 0; } .empty-icon { font-size:64rpx; display:block; margin-bottom:16rpx; } .empty-text { font-size:26rpx; color:#94a3b8; }
</style>
