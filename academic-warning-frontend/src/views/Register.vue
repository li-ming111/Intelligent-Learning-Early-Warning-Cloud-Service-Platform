<template>
  <div class="reg-page">
    <div class="reg-card">
      <div class="reg-header">
        <img src="@/assets/xiaohui.jpg" alt="校徽" class="reg-logo">
        <h1>学生注册</h1>
        <p>哈尔滨信息工程学院 · 智能学习预警云服务平台</p>
      </div>

      <form @submit.prevent="handleRegister">
        <div class="rg-field">
          <label>学号</label>
          <el-input v-model="form.studentId" placeholder="请输入10位学号" maxlength="10"
            @input="onStudentIdChange" clearable>
            <template #prefix><el-icon><Postcard /></el-icon></template>
          </el-input>
          <span v-if="form.studentId.length === 10" class="rg-hint ok">✓ 学号有效</span>
        </div>

        <div class="rg-field">
          <label>专业</label>
          <el-input v-model="form.majorName" placeholder="输入学号自动填充" disabled>
            <template #prefix><el-icon><School /></el-icon></template>
          </el-input>
          <span v-if="form.majorName" class="rg-hint">{{ form.collegeName }} | {{ form.majorName }}</span>
        </div>

        <div class="rg-field">
          <label>姓名</label>
          <el-input v-model="form.name" placeholder="请输入真实姓名" clearable>
            <template #prefix><el-icon><User /></el-icon></template>
          </el-input>
        </div>

        <div class="rg-field">
          <label>密码</label>
          <el-input v-model="form.password" type="password" placeholder="请输入密码（至少6位）" show-password>
            <template #prefix><el-icon><Lock /></el-icon></template>
          </el-input>
          <span class="rg-hint dim">默认密码: 123456</span>
        </div>

        <div class="rg-field">
          <label>手机号</label>
          <el-input v-model="form.phone" placeholder="请输入11位手机号" maxlength="11" clearable>
            <template #prefix><el-icon><Phone /></el-icon></template>
          </el-input>
          <span v-if="isValidPhone" class="rg-hint ok">✓ 手机号有效</span>
        </div>

        <div class="rg-field">
          <label>邮箱</label>
          <el-input v-model="form.email" placeholder="请输入邮箱地址" clearable>
            <template #prefix><el-icon><Message /></el-icon></template>
          </el-input>
          <span v-if="isValidEmail" class="rg-hint ok">✓ 邮箱有效</span>
        </div>

        <el-alert v-if="errorMessage" :title="errorMessage" type="error" closable
          @close="errorMessage = ''" style="margin-bottom:16px" />
        <el-alert v-if="successMessage" :title="successMessage" type="success" closable
          @close="successMessage = ''" style="margin-bottom:16px" />

        <el-button type="primary" native-type="submit" class="reg-submit" :loading="loading">
          {{ loading ? '注册中...' : '注册账号' }}
        </el-button>

        <div class="reg-footer">
          <router-link to="/login">← 已有账号？返回登录</router-link>
          <div class="reg-footer-links">
            <router-link to="/teacher-register">教师注册</router-link>
            <router-link to="/counselor-register">辅导员注册</router-link>
          </div>
        </div>
      </form>
    </div>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue'
import { useRouter } from 'vue-router'
import apiClient from '@/api/client'
import { User, Lock, Phone, Message, Postcard, School } from '@element-plus/icons-vue'

const router = useRouter()
const form = ref({
  studentId: '', majorName: '', collegeName: '', majorId: null, collegeId: null,
  name: '', username: '', password: '123456', phone: '', email: ''
})
const loading = ref(false), errorMessage = ref(''), successMessage = ref('')

const isValidPhone = computed(() => /^\d{11}$/.test(form.value.phone))
const isValidEmail = computed(() => /@/.test(form.value.email) && /\./.test(form.value.email))

const onStudentIdChange = async () => {
  if (form.value.studentId.length !== 10) { form.value.majorName = ''; form.value.collegeName = ''; return }
  try {
    const majors = await apiClient.get('/admin/majors')
    if (majors) {
      const majorCode = form.value.studentId.substring(4, 6)
      const major = majors.find(m => m.code === majorCode)
      if (major) {
        form.value.majorName = major.name; form.value.majorId = major.id
        const college = await apiClient.get(`/admin/colleges/${major.collegeId}`)
        if (college) { form.value.collegeName = college.name; form.value.collegeId = major.collegeId }
      } else { form.value.majorName = '（未找到匹配专业）'; form.value.collegeName = '' }
    }
  } catch (e) { form.value.majorName = '（加载失败）' }
}

const handleRegister = async () => {
  errorMessage.value = ''; successMessage.value = ''
  if (!form.value.studentId || form.value.studentId.length !== 10) { errorMessage.value = '请输入有效的10位学号'; return }
  if (!form.value.name) { errorMessage.value = '请输入姓名'; return }
  if (!form.value.password || form.value.password.length < 6) { errorMessage.value = '密码至少需要6位'; return }
  if (!isValidPhone.value) { errorMessage.value = '请输入有效的11位手机号'; return }
  if (!isValidEmail.value) { errorMessage.value = '请输入有效的邮箱地址'; return }
  loading.value = true
  try {
    await apiClient.post('/auth/register/student', {
      studentId: form.value.studentId, name: form.value.name,
      password: form.value.password, phone: form.value.phone, email: form.value.email
    })
    successMessage.value = '注册成功！即将跳转到登录页...'
    setTimeout(() => router.push('/login'), 1200)
  } catch (e) { errorMessage.value = e.message || '注册失败，请重试' }
  finally { loading.value = false }
}
</script>

<style scoped>
.reg-page { min-height: 100vh; background: #f0f4ff; display: flex; align-items: center; justify-content: center; padding: 40px 24px; }
.reg-card { width: 100%; max-width: 460px; background: #fff; border-radius: 20px; padding: 44px 40px;
  box-shadow: 0 4px 24px rgba(0,0,0,.06); }
.reg-header { text-align: center; margin-bottom: 32px; }
.reg-logo { width: 52px; height: 52px; max-width: 52px; border-radius: 14px; margin-bottom: 12px; object-fit: contain;
  background: #eff6ff; padding: 6px; display: inline-block; flex-shrink: 0; }
.reg-header h1 { margin: 0 0 4px; font-size: 24px; font-weight: 700; color: #1e3a5f; }
.reg-header p { margin: 0; font-size: 12px; color: #94a3b8; }
.rg-field { margin-bottom: 16px; }
.rg-field label { display: block; font-size: 13px; font-weight: 600; color: #334155; margin-bottom: 6px; }
.rg-field :deep(.el-input__wrapper) { border-radius: 10px; background: #fff; border: 1.5px solid #e2e8f0; transition: all .3s; box-shadow: none; }
.rg-field :deep(.el-input__wrapper:hover) { border-color: #93c5fd; }
.rg-field :deep(.el-input__wrapper.is-focus) { border-color: #2563eb; box-shadow: 0 0 0 3px rgba(37,99,235,.1); }
.rg-hint { font-size: 11px; margin-top: 4px; display: block; }
.rg-hint.ok { color: #16a34a; }
.rg-hint.dim { color: #94a3b8; }
.reg-submit { width: 100%; height: 46px; font-size: 15px; font-weight: 700; letter-spacing: 1px; border-radius: 10px;
  background: linear-gradient(135deg, #2563eb, #3b82f6); border: none; box-shadow: 0 4px 14px rgba(37,99,235,.3); transition: all .3s; margin-top: 6px; }
.reg-submit:hover { background: linear-gradient(135deg, #1d4ed8, #2563eb); box-shadow: 0 6px 20px rgba(37,99,235,.4); transform: translateY(-1px); }
.reg-footer { text-align: center; margin-top: 20px; }
.reg-footer a { display: inline-block; color: #2563eb; font-size: 13px; text-decoration: none; transition: color .2s; }
.reg-footer a:hover { color: #1d4ed8; }
.reg-footer-links { margin-top: 10px; display: flex; gap: 12px; justify-content: center; }
.reg-footer-links a { font-size: 12px; color: #94a3b8; }
.reg-footer-links a:hover { color: #2563eb; }
</style>
