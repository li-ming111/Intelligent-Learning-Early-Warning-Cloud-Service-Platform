<template>
  <view class="page">
    <view class="blue-bar">
      <text class="bar-title">个人设置</text>
      <text class="bar-sub">管理账户与偏好</text>
    </view>

    <!-- 个人信息 -->
    <view class="section">
      <text class="sec-title">个人信息</text>
      <view class="card">
        <view class="info-row">
          <text class="info-label">姓名</text>
          <text class="info-val">{{ profile.name || '未设置' }}</text>
        </view>
        <view class="info-row">
          <text class="info-label">学号</text>
          <text class="info-val">{{ profile.studentNo || username }}</text>
        </view>
        <view class="info-row">
          <text class="info-label">班级</text>
          <text class="info-val">{{ profile.className || '未设置' }}</text>
        </view>
        <view class="info-row last">
          <text class="info-label">学院</text>
          <text class="info-val">{{ profile.collegeName || '未设置' }}</text>
        </view>
      </view>
    </view>

    <!-- 修改密码 -->
    <view class="section">
      <text class="sec-title">修改密码</text>
      <view class="card">
        <view class="field">
          <text class="field-label">旧密码</text>
          <input v-model="pwdForm.oldPwd" :password="!showOld" class="field-input" placeholder="输入旧密码" placeholder-class="ph" />
          <text class="eye" @tap="showOld=!showOld">{{ showOld ? '闭' : '开' }}</text>
        </view>
        <view class="field">
          <text class="field-label">新密码</text>
          <input v-model="pwdForm.newPwd" :password="!showNew" class="field-input" placeholder="输入新密码" placeholder-class="ph" />
          <text class="eye" @tap="showNew=!showNew">{{ showNew ? '闭' : '开' }}</text>
        </view>
        <view class="field">
          <text class="field-label">确认密码</text>
          <input v-model="pwdForm.confirmPwd" :password="true" class="field-input" placeholder="再次输入新密码" placeholder-class="ph" />
        </view>
        <view class="pwd-tips">
          <text class="tips-item">密码长度 6-20 位</text>
          <text class="tips-item">建议包含字母和数字</text>
        </view>
        <button class="btn" @tap="changePwd">保存密码</button>
      </view>
    </view>

    <!-- 通用 -->
    <view class="section">
      <text class="sec-title">通用</text>
      <view class="card">
        <view class="menu-row" @tap="clearCache">
          <text class="menu-label">清理缓存</text>
          <text class="menu-val">{{ cacheSize }}</text>
        </view>
        <view class="menu-row last">
          <text class="menu-label">版本</text>
          <text class="menu-val">v1.0.0</text>
        </view>
      </view>
    </view>
  </view>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { studentAPI, apiClient } from '@/services/api.js'

const username = computed(() => uni.getStorageSync('username') || '')
const profile = ref({})
const pwdForm = ref({ oldPwd: '', newPwd: '', confirmPwd: '' })
const showOld = ref(false)
const showNew = ref(false)

const loadProfile = async () => {
  const uid = uni.getStorageSync('userId')
  if (!uid) return
  try {
    const res = await studentAPI.getStudentByUserId(uid)
    if (res) profile.value = res
  } catch (e) {}
}

const changePwd = async () => {
  const { oldPwd, newPwd, confirmPwd } = pwdForm.value
  if (!oldPwd) { uni.showToast({ title: '请输入旧密码', icon: 'none' }); return }
  if (!newPwd || newPwd.length < 6) { uni.showToast({ title: '新密码至少6位', icon: 'none' }); return }
  if (newPwd !== confirmPwd) { uni.showToast({ title: '两次密码不一致', icon: 'none' }); return }
  try {
    const uid = uni.getStorageSync('userId')
    await apiClient.changePassword({ userId: uid, oldPassword: oldPwd, newPassword: newPwd })
    uni.showToast({ title: '修改成功', icon: 'success' })
    pwdForm.value = { oldPwd: '', newPwd: '', confirmPwd: '' }
  } catch (e) {
    uni.showToast({ title: e.message || '修改失败', icon: 'none' })
  }
}

const clearCache = () => {
  uni.showModal({
    title: '提示',
    content: '确定清理本地缓存？',
    success: (r) => {
      if (r.confirm) {
        uni.clearStorageSync()
        uni.showToast({ title: '已清理', icon: 'success' })
        setTimeout(() => { cacheSize.value = '0 KB' }, 500)
      }
    }
  })
}

const cacheSize = ref('计算中...')
const calcCache = () => {
  try {
    const info = uni.getStorageInfoSync()
    const kb = Math.round((info.currentSize || 0) / 1024 * 10) / 10
    cacheSize.value = kb > 1024 ? Math.round(kb / 1024 * 10) / 10 + ' MB' : kb + ' KB'
  } catch (e) { cacheSize.value = '未知' }
}

onMounted(() => { loadProfile(); calcCache() })
</script>

<style scoped>
.page { min-height: 100vh; padding: 0 24rpx 24rpx; }

.blue-bar {
  background: linear-gradient(135deg, #3b5ce8 0%, #5b7cf0 100%);
  margin: 0 -24rpx 20rpx;
  padding: 44rpx 28rpx 36rpx;
}
.bar-title { display: block; font-size: 34rpx; font-weight: 700; color: #fff; }
.bar-sub { display: block; font-size: 22rpx; color: rgba(255,255,255,0.7); margin-top: 6rpx; }

.section { margin-bottom: 24rpx; }
.sec-title { display: block; font-size: 26rpx; font-weight: 600; color: #86868b; margin-bottom: 12rpx; }

.card { background: #fff; border-radius: 14rpx; padding: 4rpx 0; overflow: hidden; }

.info-row, .menu-row {
  display: flex; justify-content: space-between; align-items: center;
  padding: 24rpx 24rpx; border-bottom: 1rpx solid #f5f5f7;
}
.info-row.last, .menu-row.last { border-bottom: none; }
.info-label, .menu-label { font-size: 28rpx; color: #1d1d1f; }
.info-val { font-size: 26rpx; color: #86868b; }
.menu-val { font-size: 24rpx; color: #aeaeb2; }

/* 修改密码 */
.field {
  position: relative; padding: 0 24rpx; margin-bottom: 20rpx;
}
.field:first-child { padding-top: 20rpx; }
.field-label { display: block; font-size: 24rpx; font-weight: 500; color: #86868b; margin-bottom: 8rpx; }
.field-input {
  width: 100%; height: 80rpx; background: #f5f5f7; border-radius: 10rpx;
  padding: 0 60rpx 0 20rpx; font-size: 28rpx; color: #1d1d1f; box-sizing: border-box;
}
.ph { color: #aeaeb2; font-size: 26rpx; }
.eye {
  position: absolute; right: 44rpx; bottom: 22rpx;
  font-size: 22rpx; color: #3b5ce8; z-index: 2;
}

.pwd-tips { display: flex; gap: 16rpx; padding: 0 24rpx 20rpx; }
.tips-item { font-size: 20rpx; color: #aeaeb2; }

.btn {
  margin: 0 24rpx 20rpx; height: 80rpx; line-height: 80rpx;
  background: #3b5ce8; color: #fff; font-size: 28rpx; font-weight: 600;
  border-radius: 10rpx; border: none; text-align: center;
}
</style>
