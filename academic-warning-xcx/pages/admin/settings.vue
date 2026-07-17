<template>
  <view class="page">
    <view class="header"><text class="title">系统设置</text><text class="sub">参数与功能配置</text></view>

    <view class="card">
      <text class="card-title">预警参数</text>
      <view class="set-row" v-for="s in warningSettings" :key="s.key">
        <text class="set-label">{{ s.label }}</text>
        <view class="set-val">
          <text class="sv-num">{{ s.value }}</text>
          <text class="sv-unit">{{ s.unit }}</text>
        </view>
      </view>
    </view>

    <view class="card">
      <text class="card-title">系统功能</text>
      <view class="toggle-row" v-for="t in toggles" :key="t.key">
        <text class="toggle-label">{{ t.label }}</text>
        <switch :checked="t.enabled" color="#2563eb" />
      </view>
    </view>

    <view class="card">
      <text class="card-title">数据维护</text>
      <button class="maint-btn" @tap="clearCache">清除缓存</button>
      <button class="maint-btn danger" @tap="rebuildIndex">重建索引</button>
    </view>

    <view class="version">版本 v1.0.0</view>
  </view>
</template>

<script>
export default {
  data() {
    return {
      warningSettings: [
        { key:'failCount', label:'挂科数预警阈值', value:2, unit:'门' },
        { key:'gpaLow', label:'GPA 下限', value:2.0, unit:'分' },
        { key:'absenceRate', label:'缺勤率预警', value:20, unit:'%' },
        { key:'scanInterval', label:'扫描间隔', value:30, unit:'分钟' }
      ],
      toggles: [
        { key:'autoWarning', label:'自动预警扫描', enabled:true },
        { key:'emailNotify', label:'邮件通知', enabled:true },
        { key:'smsNotify', label:'短信通知', enabled:false },
        { key:'scoreAudit', label:'成绩修改审计', enabled:true }
      ]
    }
  },
  methods: {
    clearCache() { uni.showToast({ title:'缓存已清除', icon:'success' }) },
    rebuildIndex() {
      uni.showModal({
        title:'确认', content:'重建索引可能需要几分钟，确认继续？',
        success: (r) => { if (r.confirm) uni.showToast({ title:'索引重建中...', icon:'none' }) }
      })
    }
  }
}
</script>

<style scoped>
.page { padding:20rpx; background:#f0f2f8; min-height:100vh; }
.header { margin-bottom:20rpx; } .title { font-size:36rpx; font-weight:800; color:#1e293b; display:block; } .sub { font-size:24rpx; color:#94a3b8; }

.card { background:#fff; border-radius:16rpx; padding:24rpx; margin-bottom:16rpx; }
.card-title { font-size:28rpx; font-weight:700; color:#1e293b; display:block; margin-bottom:18rpx; }

.set-row { display:flex; justify-content:space-between; align-items:center; padding:16rpx 0; border-bottom:1rpx solid #f1f5f9; }
.set-row:last-child { border-bottom:none; }
.set-label { font-size:26rpx; color:#475569; }
.set-val { display:flex; align-items:baseline; gap:4rpx; }
.sv-num { font-size:26rpx; font-weight:700; color:#1e293b; }
.sv-unit { font-size:20rpx; color:#94a3b8; }

.toggle-row { display:flex; justify-content:space-between; align-items:center; padding:16rpx 0; border-bottom:1rpx solid #f1f5f9; }
.toggle-row:last-child { border-bottom:none; }
.toggle-label { font-size:26rpx; color:#475569; }

.maint-btn { width:100%; height:80rpx; background:#f1f5f9; color:#475569; border:none; border-radius:12rpx; font-size:26rpx; display:flex; align-items:center; justify-content:center; margin-bottom:12rpx; }
.maint-btn.danger { background:#fef2f2; color:#ef4444; }

.version { text-align:center; padding:32rpx; font-size:22rpx; color:#cbd5e1; }
</style>
