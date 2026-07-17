<template>
  <div class="counselor-classes">
    <div class="page-header">
      <h1>班级管理</h1>
      <p>查看班级信息、学生名单、预警概览</p>
    </div>

    <!-- 班级列表 -->
    <el-card style="margin-bottom: 20px;">
      <template #header>
        <div class="card-header">班级列表</div>
      </template>
      <el-table :data="classList" stripe>
        <el-table-column prop="className" label="班级名称" width="120"></el-table-column>
        <el-table-column prop="studentCount" label="学生数" width="80"></el-table-column>
        <el-table-column prop="major" label="专业" width="120"></el-table-column>
        <el-table-column prop="year" label="年级" width="80"></el-table-column>
        <el-table-column label="预警情况" width="150">
          <template #default="{ row }">
            <span style="color: #f56c6c;">{{ row.redWarnings }} </span>
            <span style="color: #2563eb;">{{ row.yellowWarnings }}</span>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" size="small" link @click="viewClassDetail(row)">详情</el-button>
            <el-button type="info" size="small" link @click="viewStudents(row)">学生</el-button>
            <el-button type="warning" size="small" link @click="viewWarnings(row)">预警</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <!-- 班级对比 -->
    <el-card style="margin-bottom: 20px;">
      <template #header>
        <div class="card-header">班级预警对比</div>
      </template>
      <div ref="comparisonChart" style="height: 300px;"></div>
    </el-card>

    <!-- 班级详情对话框 -->
    <el-dialog v-model="detailDialogVisible" title="班级详情" width="600px">
      <div v-if="selectedClass">
        <p><strong>班级:</strong> {{ selectedClass.className }}</p>
        <p><strong>专业:</strong> {{ selectedClass.major }}</p>
        <p><strong>年级:</strong> {{ selectedClass.year }}</p>
        <p><strong>学生总数:</strong> {{ selectedClass.studentCount }}</p>
        <p><strong>及格率:</strong> {{ selectedClass.passRate }}%</p>
        <p><strong>学分达标率:</strong> {{ selectedClass.creditRate }}%</p>
      </div>
    </el-dialog>

    <!-- 班级学生对话框 -->
    <el-dialog v-model="studentsDialogVisible" title="班级学生" width="700px">
      <el-table :data="classStudents" stripe>
        <el-table-column prop="studentName" label="姓名" width="100"></el-table-column>
        <el-table-column prop="studentId" label="学号" width="120"></el-table-column>
        <el-table-column prop="gpa" label="GPA" width="80"></el-table-column>
        <el-table-column label="预警状态" width="100">
          <template #default="{ row }">
            <el-tag type="danger" v-if="row.warningLevel === 'red'">红色</el-tag>
            <el-tag type="warning" v-else-if="row.warningLevel === 'blue'">黄色</el-tag>
            <el-tag type="success" v-else>正常</el-tag>
          </template>
        </el-table-column>
      </el-table>
    </el-dialog>

    <!-- 班级预警对话框 -->
    <el-dialog v-model="warningsDialogVisible" title="班级预警" width="700px">
      <el-table :data="classWarnings" stripe>
        <el-table-column prop="studentName" label="学生" width="100"></el-table-column>
        <el-table-column prop="warningReason" label="预警原因" width="200"></el-table-column>
        <el-table-column label="级别" width="80">
          <template #default="{ row }">
            <el-tag :type="row.warningLevel === 'red' ? 'danger' : row.warningLevel === 'blue' ? 'warning' : 'info'">
              {{ row.warningLevel }}
            </el-tag>
          </template>
        </el-table-column>
      </el-table>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { counselorAPI } from '@/api/index'
import { getUserId } from '@/utils/userUtils'

const classList = ref([])
const selectedClass = ref(null)
const classStudents = ref([])
const classWarnings = ref([])
const detailDialogVisible = ref(false)
const studentsDialogVisible = ref(false)
const warningsDialogVisible = ref(false)
const comparisonChart = ref(null)

onMounted(() => {
  loadClasses()
  initChart()
})

const loadClasses = async () => {
  try {
    const counselorId = localStorage.getItem('counselorId') || getUserId()
    if (!counselorId) return
    
    // 获取班级列表
    const response = await counselorAPI.getClasses(counselorId)
    classList.value = response.data || []
    
    // 获取班级对比数据或使用発载数据
    initChart()
  } catch (error) {
    console.error('加载班级列表失败:', error)
  }
}

const initChart = () => {
  if (comparisonChart.value) {
    const chart = echarts.init(comparisonChart.value)
    const option = {
      xAxis: { type: 'category', data: ['一班', '二班', '三班'] },
      yAxis: { type: 'value' },
      series: [
        {
          name: '红色预警',
          data: [2, 1, 3],
          type: 'bar',
          itemStyle: { color: '#f56c6c' }
        },
        {
          name: '黄色预警',
          data: [5, 3, 7],
          type: 'bar',
          itemStyle: { color: '#2563eb' }
        }
      ]
    }
    chart.setOption(option)
  }
}

const viewClassDetail = (row) => {
  selectedClass.value = row
  detailDialogVisible.value = true
}

const viewStudents = async (row) => {
  try {
    const response = await counselorAPI.getClassStudents(row.id)
    classStudents.value = response.data || []
  } catch (error) {
    console.error('加载班级学生失败:', error)
  }
  studentsDialogVisible.value = true
}

const viewWarnings = async (row) => {
  try {
    const response = await counselorAPI.getClassWarningOverview(row.id)
    classWarnings.value = response.data || []
  } catch (error) {
    console.error('加载班级预警失败:', error)
  }
  warningsDialogVisible.value = true
}
</script>

<style scoped>
.counselor-classes {
  padding: 20px;
}

.page-header {
  margin-bottom: 20px;
}

.page-header h1 {
  margin: 0 0 8px 0;
  font-size: 24px;
  color: #333;
}

.page-header p {
  margin: 0;
  color: #999;
  font-size: 14px;
}

.card-header {
  font-size: 16px;
  font-weight: bold;
  color: #333;
}
</style>
