<template>
  <div class="scores-page">
    <!-- 顶部工具栏 -->
    <div class="toolbar">
      <div class="toolbar-left">
        <el-select v-model="selectedSemester" placeholder="全部学期" clearable style="width:200px">
          <el-option v-for="s in availableSemesters" :key="s" :label="s" :value="s" />
        </el-select>
      </div>
      <div class="toolbar-right">
        <span class="total-label">共 {{ scoresList.length }} 门课程</span>
      </div>
    </div>

    <!-- 统计卡片行 -->
    <div class="stat-row">
      <div class="stat-card gpa">
        <div class="stat-icon">📊</div>
        <div class="stat-body">
          <div class="stat-value">{{ currentGpa }}</div>
          <div class="stat-label">当前GPA</div>
        </div>
        <div class="stat-tag" :class="gpaTagClass">{{ gpaLevel }}</div>
      </div>
      <div class="stat-card best">
        <div class="stat-icon">🏆</div>
        <div class="stat-body">
          <div class="stat-value">{{ maxGpa }}</div>
          <div class="stat-label">历史最高</div>
        </div>
      </div>
      <div class="stat-card avg">
        <div class="stat-icon"><svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" width="20" height="20"><polyline points="22 12 18 12 15 21 9 3 6 12 2 12"/></svg></div>
        <div class="stat-body">
          <div class="stat-value">{{ avgGpa }}</div>
          <div class="stat-label">全周期均分</div>
        </div>
      </div>
      <div class="stat-card fail" :class="{ danger: failedCourses > 0 }">
        <div class="stat-icon"><svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" width="20" height="20"><path d="M10.29 3.86L1.82 18a2 2 0 001.71 3h16.94a2 2 0 001.71-3L13.71 3.86a2 2 0 00-3.42 0z"/><line x1="12" y1="9" x2="12" y2="13"/><line x1="12" y1="17" x2="12.01" y2="17"/></svg></div>
        <div class="stat-body">
          <div class="stat-value">{{ failedCourses }}</div>
          <div class="stat-label">挂科数</div>
        </div>
        <div class="stat-tag warn" v-if="failedCourses > 0">{{ warnLevel }}</div>
      </div>
    </div>

    <!-- 图表区 -->
    <div class="chart-row">
      <div class="chart-card">
        <div class="chart-title">GPA 趋势</div>
        <div ref="gpaChartRef" class="chart-box"></div>
      </div>
      <div class="chart-card">
        <div class="chart-title">成绩分布</div>
        <div ref="distChartRef" class="chart-box"></div>
      </div>
    </div>

    <!-- 成绩表格 -->
    <div class="table-card">
      <div class="table-header">
        <span>{{ selectedSemester || '全部学期' }} 成绩明细</span>
        <el-button size="small" type="primary" plain @click="exportScores">导出 Excel</el-button>
      </div>
      <el-table :data="scoresList" stripe :default-sort="{prop:'semester',order:'descending'}" row-class-name="score-row">
        <el-table-column prop="semester" label="学期" width="130" sortable>
          <template #default="{ row }">
            <el-tag size="small" effect="plain" type="info">{{ semesterLabel(row.semester) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="courseName" label="课程名称" min-width="160" />
        <el-table-column prop="credits" label="学分" width="65" align="center" />
        <el-table-column prop="scoreTotal" label="总分" width="75" align="center" sortable>
          <template #default="{ row }">
            <span :class="scoreClass(row.scoreTotal)">{{ row.scoreTotal }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="gradePoint" label="绩点" width="65" align="center" />
        <el-table-column prop="grade" label="等级" width="65" align="center">
          <template #default="{ row }">
            <el-tag :type="gradeTagType(row.grade)" size="small" effect="dark">{{ row.grade }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="状态" width="75" align="center">
          <template #default="{ row }">
            <span v-if="row.scoreTotal >= 60" class="pass-dot">✔ 通过</span>
            <span v-else class="fail-dot">✘ 挂科</span>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="100" align="center" fixed="right">
          <template #default="{ row }">
            <div class="action-btns">
              <el-button link type="primary" size="small" @click="openDetail(row)">详情</el-button>
              <el-button link type="danger" size="small" @click="openAppeal(row)" v-if="row.scoreTotal < 60">申诉</el-button>
            </div>
          </template>
        </el-table-column>
      </el-table>
    </div>

    <!-- 课程详情弹窗 -->
    <el-dialog v-model="detailVisible" title="成绩详情" width="420px" :close-on-click-modal="true">
      <div class="detail-card" v-if="detailRow">
        <div class="detail-header">
          <span class="detail-course">{{ detailRow.courseName }}</span>
          <el-tag size="small">{{ semesterLabel(detailRow.semester) }}</el-tag>
        </div>
        <div class="detail-grid">
          <div class="detail-item">
            <span class="detail-label">总分</span>
            <span class="detail-val" :class="scoreClass(detailRow.scoreTotal)">{{ detailRow.scoreTotal }}</span>
          </div>
          <div class="detail-item">
            <span class="detail-label">绩点</span>
            <span class="detail-val">{{ detailRow.gradePoint }}</span>
          </div>
          <div class="detail-item">
            <span class="detail-label">等级</span>
            <el-tag :type="gradeTagType(detailRow.grade)" size="small">{{ detailRow.grade }}</el-tag>
          </div>
          <div class="detail-item">
            <span class="detail-label">学分</span>
            <span class="detail-val">{{ detailRow.credits }}</span>
          </div>
          <div class="detail-item">
            <span class="detail-label">平时分</span>
            <span class="detail-val">{{ detailRow.regularScore || '-' }}</span>
          </div>
          <div class="detail-item">
            <span class="detail-label">期末分</span>
            <span class="detail-val">{{ detailRow.finalScore || '-' }}</span>
          </div>
        </div>
      </div>
    </el-dialog>

    <!-- 申诉弹窗 -->
    <el-dialog v-model="appealVisible" title="成绩申诉" width="460px">
      <el-form label-width="80px">
        <el-form-item label="课程">
          <span>{{ appealForm.courseName }}</span>
        </el-form-item>
        <el-form-item label="当前成绩">
          <span class="score-red">{{ appealForm.currentScore }} 分</span>
        </el-form-item>
        <el-form-item label="申诉原因" required>
          <el-select v-model="appealForm.reason" placeholder="选择原因" style="width:100%">
            <el-option label="成绩录入错误" value="entry_error" />
            <el-option label="缓考未同步" value="makeup_not_sync" />
            <el-option label="阅卷有误" value="grading_issue" />
            <el-option label="其他原因" value="other" />
          </el-select>
        </el-form-item>
        <el-form-item label="详情说明" required>
          <el-input v-model="appealForm.description" type="textarea" :rows="3" placeholder="请详细描述申诉理由" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="appealVisible = false">取消</el-button>
        <el-button type="primary" @click="submitAppeal">提交申诉</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted, watch, nextTick } from 'vue'
import * as echarts from 'echarts'
import { ElMessage } from 'element-plus'
import { studentAPI } from '@/api/index'
import { getUserId } from '@/utils/userUtils'

// ---- 学期编号 → "2023-2024-1" 格式 ----
const entranceYear = computedEntranceYear()

function computedEntranceYear() {
  const sid = localStorage.getItem('studentId') || localStorage.getItem('username') || '2023'
  const y = parseInt(sid.substring(0, 4))
  return (!isNaN(y) && y >= 2000) ? y : 2023
}

function semesterLabel(n) {
  if (n === null || n === undefined) return '-'
  const num = parseInt(n)
  if (isNaN(num) || num < 1) return n
  const y = entranceYear + Math.floor((num - 1) / 2)
  const half = (num - 1) % 2 + 1
  return `${y}-${y + 1}-${half}`
}

/** 将 "2025-2026-2" 转为后端需要的 semester 参数 "2025-2026-2" */
function toSemesterParam(label) {
  if (!label) return null
  return label  // 直接传完整标签，后端解析 year=2025, semester=2
}

// ---- 响应式数据 ----
const selectedSemester = ref('')
const availableSemesters = ref([])
const scoresList = ref([])
const historyGpa = ref([])
const currentGpa = ref('0.00')
const maxGpa = ref('0.00')
const avgGpa = ref('0.00')
const failedCourses = ref(0)
const gpaLevel = ref('--')
const gpaTagClass = ref('')
const warnLevel = ref('')
const detailVisible = ref(false)
const detailRow = ref(null)
const appealVisible = ref(false)
const appealForm = ref({ courseName: '', currentScore: '', reason: '', description: '' })
const gpaChartRef = ref(null)
const distChartRef = ref(null)
let gpaChart = null
let distChart = null

// ---- 生命周期 ----
onMounted(async () => {
  await loadScores()
  await loadHistoryBenchmark()
  nextTick(() => { renderCharts() })
})

watch(selectedSemester, () => loadScores())

// ---- 数据加载 ----
async function loadScores() {
  const uid = getUserId()
  if (!uid) return
  scoresList.value = []
  try {
    // 将 "2024-2025-1" 格式转为数字学期号传给后端
    const semParam = selectedSemester.value ? toSemesterParam(selectedSemester.value) : null
    const res = await studentAPI.getScores(uid, semParam)
    if (Array.isArray(res) && res.length) {
      scoresList.value = res.map(s => ({
        ...s,
        grade: gradeLabel(s.scoreTotal),
        status: s.scoreTotal >= 60 ? 'pass' : 'fail'
      }))
      if (!selectedSemester.value) {
        const set = new Set()
        scoresList.value.forEach(s => {
          if (s.year && s.semester) {
            set.add(`${s.year}-${s.year + 1}-${s.semester}`)
          }
        })
        availableSemesters.value = Array.from(set).sort((a, b) => b.localeCompare(a))
      }
    }
    calcStats()
    nextTick(renderCharts)
  } catch (e) {
    console.error('加载成绩失败', e)
  }
}

async function loadHistoryBenchmark() {
  const uid = getUserId()
  if (!uid) return
  try {
    const res = await studentAPI.getHistoryBenchmark(uid)
    historyGpa.value = Array.isArray(res) ? res : (res?.data?.data || [])
  } catch (e) {
    historyGpa.value = []
  }
}

// ---- 统计计算 ----
function calcStats() {
  const list = scoresList.value
  if (list.length) {
    let gpaSum = 0, creditSum = 0
    list.forEach(c => {
      const gp = c.gradePoint || 0
      const cr = c.credits || 0
      gpaSum += gp * cr
      creditSum += cr
    })
    const gpa = creditSum > 0 ? gpaSum / creditSum : 0
    currentGpa.value = gpa.toFixed(2)
    if (gpa >= 3.5) { gpaLevel.value = '优秀'; gpaTagClass.value = 'excellent' }
    else if (gpa >= 3.0) { gpaLevel.value = '良好'; gpaTagClass.value = 'good' }
    else if (gpa >= 2.0) { gpaLevel.value = '中等'; gpaTagClass.value = 'mid' }
    else { gpaLevel.value = '需改进'; gpaTagClass.value = 'bad' }
  }
  // history
  const h = historyGpa.value
  if (h?.length) {
    const sorted = [...h].sort((a, b) => (b.studentGpa || 0) - (a.studentGpa || 0))
    maxGpa.value = (sorted[0]?.studentGpa || 0).toFixed(2)
    avgGpa.value = (h.reduce((s, x) => s + (x.studentGpa || 0), 0) / h.length).toFixed(2)
  } else {
    maxGpa.value = currentGpa.value
    avgGpa.value = currentGpa.value
  }
  const fc = list.filter(c => c.scoreTotal < 60).length
  failedCourses.value = fc
  warnLevel.value = fc >= 6 ? '严重预警' : fc >= 3 ? '中度预警' : fc > 0 ? '轻度预警' : ''
}

// ---- 图表渲染 ----
function renderCharts() {
  renderGpaChart()
  renderDistChart()
}

function renderGpaChart() {
  const dom = gpaChartRef.value
  if (!dom) return
  if (!gpaChart) gpaChart = echarts.init(dom)
  const data = historyGpa.value?.length
    ? historyGpa.value
        .filter(x => x.semester)
        .map(x => [semesterLabel(x.semester), Number(x.studentGpa || 0)])
        .sort((a, b) => a[0].localeCompare(b[0]))
    : scoresList.value.length
      ? (() => {
          const map = {}
          scoresList.value.forEach(s => {
            const lb = semesterLabel(s.semester)
            if (!map[lb]) map[lb] = { sum: 0, cr: 0 }
            map[lb].sum += (s.gradePoint || 0) * (s.credits || 0)
            map[lb].cr += (s.credits || 0)
          })
          return Object.entries(map).map(([lb, v]) => [lb, v.cr > 0 ? +(v.sum / v.cr).toFixed(2) : 0]).sort((a, b) => a[0].localeCompare(b[0]))
        })()
      : []
  if (!data.length) { gpaChart.setOption({ title: { text: '暂无数据', left: 'center', top: 'center', textStyle: { color: '#999', fontSize: 13 } } }); return }

  const gpaVals = data.map(d => d[1])
  const yMin = Math.floor(Math.min(...gpaVals, 2.0) * 2) / 2
  gpaChart.setOption({
    tooltip: {
      trigger: 'axis', backgroundColor: '#fff', borderColor: '#eee',
      textStyle: { color: '#333', fontSize: 12 },
      formatter: (p) => `<strong>${p[0].name}</strong><br/>GPA: <b style="color:#7c3aed;font-size:16px">${p[0].value}</b>`
    },
    grid: { left: 50, right: 25, top: 30, bottom: 45 },
    xAxis: { type: 'category', data: data.map(d => d[0]), axisLabel: { fontSize: 11, color: '#666' }, axisTick: { show: false }, boundaryGap: false },
    yAxis: { type: 'value', min: Math.min(yMin, 1.5), max: 4, interval: 0.5, name: 'GPA', nameTextStyle: { fontSize: 11 } },
    series: [{
      type: 'line', smooth: true, data: gpaVals,
      lineStyle: { color: '#7c3aed', width: 3 },
      symbol: 'circle', symbolSize: 8,
      itemStyle: { color: '#7c3aed', borderColor: '#fff', borderWidth: 2 },
      areaStyle: { color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
        { offset: 0, color: 'rgba(124,58,237,0.25)' },
        { offset: 0.5, color: 'rgba(124,58,237,0.08)' },
        { offset: 1, color: 'rgba(124,58,237,0.01)' }
      ])},
      label: { show: true, position: 'top', fontSize: 10, color: '#7c3aed', fontWeight: 600 },
      markLine: {
        silent: true, symbol: 'none',
        data: [
          { yAxis: 2.0, label: { formatter: '及格线', fontSize: 10, color: '#ef6666' }, lineStyle: { color: '#fca5a5', type: 'dashed', width: 1.5 } },
          { yAxis: 3.0, label: { formatter: '良好线', fontSize: 10, color: '#f59e0b' }, lineStyle: { color: '#fcd34d', type: 'dashed', width: 1.5 } }
        ]
      }
    }]
  })
}

function renderDistChart() {
  const dom = distChartRef.value
  if (!dom) return
  if (!distChart) distChart = echarts.init(dom)
  const list = scoresList.value
  if (!list.length) { distChart.setOption({ title: { text: '暂无数据', left: 'center', top: 'center', textStyle: { color: '#999', fontSize: 13 } } }); return }
  const buckets = { '优秀(≥90)': 0, '良好(80-89)': 0, '中等(70-79)': 0, '及格(60-69)': 0, '不及格(<60)': 0 }
  list.forEach(s => {
    const sc = s.scoreTotal || 0
    if (sc >= 90) buckets['优秀(≥90)']++
    else if (sc >= 80) buckets['良好(80-89)']++
    else if (sc >= 70) buckets['中等(70-79)']++
    else if (sc >= 60) buckets['及格(60-69)']++
    else buckets['不及格(<60)']++
  })
  const items = Object.entries(buckets).filter(([_, v]) => v > 0)
  const colors = { '优秀(≥90)': '#16a34a', '良好(80-89)': '#7c3aed', '中等(70-79)': '#3b82f6', '及格(60-69)': '#f59e0b', '不及格(<60)': '#ef4444' }
  distChart.setOption({
    tooltip: { trigger: 'item', formatter: (p) => `<strong style="font-size:14px">${p.name}</strong><br/>${p.value}门 (${p.percent}%)` },
    legend: { bottom: 0, textStyle: { fontSize: 11, color: '#666' } },
    series: [{
      type: 'pie', radius: ['45%', '75%'], center: ['50%', '45%'],
      data: items.map(([n, v]) => ({ name: n, value: v, itemStyle: { color: colors[n] } })),
      label: { show: false },
      emphasis: { label: { show: true, fontSize: 14, fontWeight: 'bold' }, itemStyle: { shadowBlur: 10, shadowColor: 'rgba(0,0,0,0.15)' } }
    }]
  })
}

// ---- 辅助函数 ----
function gradeLabel(score) {
  const s = Number(score || 0)
  if (s >= 90) return 'A'
  if (s >= 80) return 'A-'
  if (s >= 70) return 'B'
  if (s >= 60) return 'C'
  return 'D'
}
function scoreClass(s) { return Number(s || 0) >= 60 ? 'score-ok' : 'score-ng' }
function gradeTagType(g) {
  switch (g) { case 'A': case 'A-': return 'success'; case 'B': return ''; case 'C': return 'warning'; default: return 'danger' }
}

// ---- 申诉 ----
function openAppeal(row) {
  appealForm.value = { courseName: row.courseName, currentScore: row.scoreTotal, reason: '', description: '' }
  appealVisible.value = true
}
function openDetail(row) { detailRow.value = row; detailVisible.value = true }
async function submitAppeal() {
  if (!appealForm.value.reason || !appealForm.value.description) {
    ElMessage.warning('请完善申诉信息'); return
  }
  try {
    await studentAPI.submitAppeal({
      studentId: localStorage.getItem('userId'),
      reason: appealForm.value.reason,
      description: appealForm.value.description
    })
    ElMessage.success('申诉已提交')
    appealVisible.value = false
  } catch (e) { ElMessage.error('提交失败') }
}

// ---- 导出 ----
async function exportScores() {
  try {
    await studentAPI.exportScoresExcel(getUserId())
    ElMessage.success('导出成功')
  } catch (e) { ElMessage.error('导出失败') }
}
</script>

<style scoped>
.scores-page { padding: 20px; min-height: 100vh; background: #f5f7fb; }
.toolbar { display: flex; justify-content: space-between; align-items: center; margin-bottom: 16px; }
.toolbar-right .total-label { font-size: 13px; color: #71717a; }

/* 统计卡片 */
.stat-row { display: grid; grid-template-columns: repeat(4, 1fr); gap: 14px; margin-bottom: 16px; }
.stat-card { background: #fff; border-radius: 12px; padding: 18px; display: flex; align-items: center; gap: 12px; box-shadow: 0 1px 3px rgba(0,0,0,.06); transition: transform .2s; }
.stat-card:hover { transform: translateY(-2px); }
.stat-icon { font-size: 28px; }
.stat-body { flex: 1; }
.stat-value { font-size: 26px; font-weight: 700; color: #18181b; }
.stat-label { font-size: 12px; color: #71717a; margin-top: 2px; }
.stat-tag { font-size: 11px; padding: 2px 8px; border-radius: 10px; font-weight: 600; }
.stat-tag.excellent { background: #dcfce7; color: #16a34a; }
.stat-tag.good { background: #dbeafe; color: #2563eb; }
.stat-tag.mid { background: #fef9c3; color: #ca8a04; }
.stat-tag.bad { background: #fee2e2; color: #dc2626; }
.stat-tag.warn { background: #fee2e2; color: #dc2626; }
.stat-card.danger { border-left: 3px solid #ef4444; }
.stat-card.fail .stat-value { color: #ef4444; }

/* 图表 */
.chart-row { display: grid; grid-template-columns: 1fr 1fr; gap: 14px; margin-bottom: 16px; }
.chart-card { background: #fff; border-radius: 12px; padding: 16px; box-shadow: 0 1px 3px rgba(0,0,0,.06); }
.chart-title { font-size: 14px; font-weight: 600; color: #18181b; margin-bottom: 8px; }
.chart-box { width: 100%; height: 320px; }

/* 表格 */
.table-card { background: #fff; border-radius: 12px; padding: 16px; box-shadow: 0 1px 3px rgba(0,0,0,.06); }
.table-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 12px; font-size: 15px; font-weight: 600; color: #18181b; }
/* 表头文字和图标在一行 */
:deep(.el-table th .cell) { display: flex; align-items: center; justify-content: center; white-space: nowrap; }
:deep(.el-table th .cell .el-table__sort-icon) { margin-left: 4px; flex-shrink: 0; }
.score-ok { color: #16a34a; font-weight: 600; }
.score-ng { color: #ef4444; font-weight: 600; }
.pass-dot { color: #16a34a; font-size: 12px; }
.fail-dot { color: #ef4444; font-size: 12px; }
.action-btns { display: inline-flex; gap: 4px; align-items: center; white-space: nowrap; }
.action-btns .el-button + .el-button { margin-left: 0 !important; }
.score-ng-row { background: #fef2f2 !important; }

/* 详情弹窗 */
.detail-card { padding: 8px 0; }
.detail-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 16px; }
.detail-course { font-size: 16px; font-weight: 600; }
.detail-grid { display: grid; grid-template-columns: 1fr 1fr; gap: 12px; }
.detail-item { display: flex; flex-direction: column; gap: 4px; padding: 10px; background: #f8f9fb; border-radius: 8px; }
.detail-label { font-size: 11px; color: #71717a; }
.detail-val { font-size: 16px; font-weight: 600; color: #18181b; }

.score-red { color: #ef4444; font-weight: 600; }

@media (max-width: 768px) {
  .stat-row { grid-template-columns: 1fr 1fr; }
  .chart-row { grid-template-columns: 1fr; }
}
</style>
