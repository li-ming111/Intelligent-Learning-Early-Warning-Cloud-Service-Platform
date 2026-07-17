<template>
  <div class="cn-page">
    <div class="cn-head">
      <h1>通知中心</h1>
      <p>向学生发送通知和管理消息历史</p>
    </div>

    <el-card style="margin-bottom:20px">
      <template #header><span class="cn-card-title">创建新通知</span></template>
      <el-form :model="form" label-width="100px">
        <el-form-item label="通知类型">
          <el-select v-model="form.type" placeholder="选择通知类型" style="width:200px">
            <el-option label="预警通知" value="warning" />
            <el-option label="重要通知" value="important" />
            <el-option label="普通通知" value="normal" />
          </el-select>
        </el-form-item>
        <el-form-item label="接收范围">
          <el-select v-model="form.recipients" multiple placeholder="选择接收学生" style="width:300px">
            <el-option label="全部学生" value="all" />
            <el-option label="预警学生" value="warning-students" />
          </el-select>
        </el-form-item>
        <el-form-item label="通知标题">
          <el-input v-model="form.title" placeholder="输入通知标题" />
        </el-form-item>
        <el-form-item label="通知内容">
          <el-input v-model="form.content" type="textarea" :rows="4" placeholder="输入通知内容" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" :loading="sending" @click="sendNotification">发送通知</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <el-card>
      <template #header><span class="cn-card-title">通知历史</span></template>
      <el-empty v-if="history.length === 0" description="暂无通知记录" />
      <el-table v-else :data="history" stripe>
        <el-table-column prop="title" label="标题" width="200" />
        <el-table-column label="类型" width="120">
          <template #default="{ row }">
            <el-tag :type="row.type === 'warning' ? 'danger' : ''" size="small">{{ row.type }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="recipientCount" label="接收人数" width="100" />
        <el-table-column prop="createdAt" label="发送时间" width="180" />
        <el-table-column label="操作" width="150">
          <template #default>
            <el-button type="primary" size="small" link>查看详情</el-button>
            <el-button type="danger" size="small" link>删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { counselorAPI } from '@/api/index'

const form = ref({ type: 'normal', recipients: [], title: '', content: '' })
const history = ref([])
const sending = ref(false)

const getCounselorId = () => { const uid = localStorage.getItem('userId'); return uid ? parseInt(uid) : null }

onMounted(async () => {
  try {
    const uid = getCounselorId(); if (!uid) return
    const res = await counselorAPI.getNotificationHistory(uid)
    if (res) {
      history.value = Array.isArray(res.list) ? res.list : (Array.isArray(res) ? res : [])
    }
  } catch (e) { console.error(e) }
})

const sendNotification = async () => {
  if (!form.value.title || !form.value.content) { ElMessage.error('请填写通知标题和内容'); return }
  sending.value = true
  try {
    const uid = getCounselorId()
    await counselorAPI.notifyStudents({
      counselorId: uid,
      title: form.value.title,
      content: form.value.content,
      type: form.value.type,
      recipients: form.value.recipients
    })
    ElMessage.success('通知已发送')
    form.value = { type: 'normal', recipients: [], title: '', content: '' }
    // 重新加载历史
    const res = await counselorAPI.getNotificationHistory(uid)
    history.value = Array.isArray(res.list) ? res.list : (Array.isArray(res) ? res : [])
  } catch (e) { ElMessage.error('发送失败: ' + (e.message || '网络错误')) }
  finally { sending.value = false }
}
</script>

<style scoped>
.cn-page { padding: 24px; }
.cn-head { margin-bottom: 20px; }
.cn-head h1 { margin: 0 0 4px; font-size: 22px; font-weight: 700; color: #1e3a5f; }
.cn-head p { margin: 0; font-size: 13px; color: #94a3b8; }
.cn-card-title { font-size: 15px; font-weight: 600; color: #1e293b; }
:deep(.el-card) { border-radius: 12px; box-shadow: 0 1px 3px rgba(0,0,0,.04); }
:deep(.el-card__header) { padding: 14px 20px; border-bottom: 1px solid #f1f5f9; }
:deep(.el-card__body) { padding: 20px; }
</style>
