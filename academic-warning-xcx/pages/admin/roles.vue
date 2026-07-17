<template>
  <view class="page">
    <view class="header"><text class="title">角色管理</text><text class="sub">系统角色权限配置</text></view>
    <view class="card-list">
      <view v-for="r in roles" :key="r.id" class="card">
        <view class="card-head">
          <view class="ri" :style="{background:r.color}">{{ r.label[0] }}</view>
          <view class="rinfo">
            <text class="rname">{{ r.label }}</text>
            <text class="rcnt">{{ r.userCount || 0 }} 名用户</text>
          </view>
        </view>
        <view class="card-body">
          <text class="perm-title">权限范围</text>
          <view class="perm-tags">
            <text v-for="p in r.permissions" :key="p" class="perm-tag">{{ p }}</text>
          </view>
        </view>
      </view>
    </view>
  </view>
</template>

<script>
import { adminAPI } from '@/services/api.js'

export default {
  data() {
    return {
      roles: [
        { id:1, label:'学生', color:'#2563eb', userCount:0, permissions:['查看成绩','查看预警','查看帮扶计划','提交申诉','查看分析'] },
        { id:2, label:'教师', color:'#16a34a', userCount:0, permissions:['录入成绩','修改成绩','管理课程','处理预警','查看统计'] },
        { id:3, label:'辅导员', color:'#7c3aed', userCount:0, permissions:['学生管理','预警处理','帮扶计划','通知发送','数据分析'] },
        { id:4, label:'管理员', color:'#1d4ed8', userCount:0, permissions:['用户管理','角色管理','部门管理','系统设置','日志查看','数据导出'] },
        { id:5, label:'学院管理员', color:'#2563eb', userCount:0, permissions:['学院统计','班级对比','教师管理','数据导出'] }
      ]
    }
  },
  onShow() { this.loadData() },
  methods: {
    async loadData() {
      try {
        const res = await adminAPI.getRoleStats()
        if (res) {
          const map = {}
          ;(Array.isArray(res) ? res : (res?.data || [])).forEach(r => { map[r.role || r.id] = r.count || r.userCount || 0 })
          this.roles.forEach(r => { r.userCount = map[r.id] || 0 })
        }
      } catch (e) {}
    }
  }
}
</script>

<style scoped>
.page { padding:20rpx; background:#f0f2f8; min-height:100vh; }
.header { margin-bottom:20rpx; } .title { font-size:36rpx; font-weight:800; color:#1e293b; display:block; } .sub { font-size:24rpx; color:#94a3b8; }
.card-list { display:flex; flex-direction:column; gap:14rpx; }
.card { background:#fff; border-radius:16rpx; padding:24rpx; }
.card-head { display:flex; align-items:center; gap:14rpx; margin-bottom:18rpx; }
.ri { width:64rpx; height:64rpx; border-radius:16rpx; color:#fff; font-size:28rpx; font-weight:700; display:flex; align-items:center; justify-content:center; flex-shrink:0; }
.rinfo { flex:1; } .rname { font-size:28rpx; font-weight:600; color:#1e293b; display:block; } .rcnt { font-size:22rpx; color:#94a3b8; }
.card-body {} .perm-title { font-size:22rpx; color:#94a3b8; display:block; margin-bottom:10rpx; }
.perm-tags { display:flex; flex-wrap:wrap; gap:8rpx; }
.perm-tag { font-size:20rpx; padding:4rpx 14rpx; background:#f1f5f9; border-radius:8rpx; color:#475569; }
</style>
