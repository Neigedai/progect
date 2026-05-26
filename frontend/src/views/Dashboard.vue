<template>
  <div class="dashboard">
    <div class="dash-topbar">
      <div class="dash-title-row">
        <h1>园区运营数据大屏</h1>
        <span class="dash-subtitle">Park Operations Dashboard</span>
      </div>
      <div class="dash-topbar-right">
        <span class="dash-time">{{ now }}</span>
        <el-button class="back-btn" @click="$router.push('/park-overview')" round>
          <el-icon><ArrowLeft /></el-icon>
          返回系统
        </el-button>
      </div>
    </div>

    <div class="dash-body">
      <div class="kpi-row">
        <div class="kpi-card">
          <div class="kpi-icon" style="--glow: #3b82f6"><el-icon :size="28"><OfficeBuilding /></el-icon></div>
          <div class="kpi-info">
            <div class="kpi-num">{{ stats.enterpriseCount || 0 }}</div>
            <div class="kpi-label">认证企业</div>
          </div>
        </div>
        <div class="kpi-card">
          <div class="kpi-icon" style="--glow: #22c55e"><el-icon :size="28"><EditPen /></el-icon></div>
          <div class="kpi-info">
            <div class="kpi-num">{{ stats.residencyCount || 0 }}</div>
            <div class="kpi-label">入驻申请</div>
          </div>
        </div>
        <div class="kpi-card">
          <div class="kpi-icon" style="--glow: #f59e0b"><el-icon :size="28"><Grid /></el-icon></div>
          <div class="kpi-info">
            <div class="kpi-num">{{ stats.serviceAppCount || 0 }}</div>
            <div class="kpi-label">服务申请</div>
          </div>
        </div>
        <div class="kpi-card">
          <div class="kpi-icon" style="--glow: #8b5cf6"><el-icon :size="28"><UserFilled /></el-icon></div>
          <div class="kpi-info">
            <div class="kpi-num">{{ stats.userCount || 0 }}</div>
            <div class="kpi-label">注册用户</div>
          </div>
        </div>
        <div class="kpi-card">
          <div class="kpi-icon" style="--glow: #06b6d4"><el-icon :size="28"><Setting /></el-icon></div>
          <div class="kpi-info">
            <div class="kpi-num">{{ stats.serviceItemCount || 0 }}</div>
            <div class="kpi-label">在架服务</div>
          </div>
        </div>
      </div>

      <div class="chart-grid">
        <div class="chart-card">
          <div class="chart-card-header">企业标签分布</div>
          <div ref="pieTagChart" class="chart-dom"></div>
        </div>
        <div class="chart-card">
          <div class="chart-card-header">入驻行业分布</div>
          <div ref="barIndustryChart" class="chart-dom"></div>
        </div>
        <div class="chart-card wide">
          <div class="chart-card-header">入驻申请趋势（近6个月）</div>
          <div ref="lineTrendChart" class="chart-dom"></div>
        </div>
        <div class="chart-card">
          <div class="chart-card-header">热门服务 Top5</div>
          <div ref="barServiceChart" class="chart-dom"></div>
        </div>
        <div class="chart-card">
          <div class="chart-card-header">入驻申请状态</div>
          <div ref="pieResidencyStatusChart" class="chart-dom"></div>
        </div>
        <div class="chart-card">
          <div class="chart-card-header">用户注册趋势</div>
          <div ref="lineUserChart" class="chart-dom"></div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, onBeforeUnmount } from 'vue'
import * as echarts from 'echarts'
import { OfficeBuilding, EditPen, Grid, UserFilled, Setting, ArrowLeft } from '@element-plus/icons-vue'
import request from '@/utils/request'

const now = ref('')
let timer = null
let charts = []
const stats = reactive({})

const pieTagChart = ref(null)
const barIndustryChart = ref(null)
const lineTrendChart = ref(null)
const barServiceChart = ref(null)
const pieResidencyStatusChart = ref(null)
const lineUserChart = ref(null)

const darkText = '#94a3b8'
const colors = ['#3b82f6', '#22c55e', '#f59e0b', '#ef4444', '#8b5cf6', '#06b6d4', '#ec4899', '#f97316']

function initCharts() {
  charts.forEach(c => c?.dispose())
  charts = []

  function baseOpt() {
    return {
      backgroundColor: 'transparent',
      textStyle: { color: darkText, fontSize: 12 },
      grid: { top: 16, right: 16, bottom: 16, left: 40 }
    }
  }

  if (stats.enterpriseByTag) {
    const c = echarts.init(pieTagChart.value)
    c.setOption({ ...baseOpt(), tooltip: { trigger: 'item' },
      series: [{ type: 'pie', radius: ['50%', '75%'], center: ['50%', '52%'], label: { color: darkText, fontSize: 11 },
        data: stats.enterpriseByTag, itemStyle: { borderColor: '#0A0E27', borderWidth: 3, borderRadius: 4 } }], color: colors })
    charts.push(c)
  }

  if (stats.residencyByIndustry) {
    const c = echarts.init(barIndustryChart.value)
    c.setOption({ ...baseOpt(), tooltip: { trigger: 'axis' },
      xAxis: { type: 'category', data: stats.residencyByIndustry.map(i => i.name), axisLabel: { color: darkText, fontSize: 10 }, axisLine: { lineStyle: { color: '#1e293b' } } },
      yAxis: { type: 'value', axisLabel: { color: darkText }, splitLine: { lineStyle: { color: '#1e293b' } } },
      series: [{ type: 'bar', data: stats.residencyByIndustry.map(i => i.value), barWidth: 24, itemStyle: { borderRadius: [6, 6, 0, 0] } }], color: colors })
    charts.push(c)
  }

  if (stats.residencyTrend) {
    const c = echarts.init(lineTrendChart.value)
    c.setOption({ ...baseOpt(), tooltip: { trigger: 'axis' },
      xAxis: { type: 'category', data: stats.residencyTrend.map(i => i.month), axisLabel: { color: darkText }, axisLine: { lineStyle: { color: '#1e293b' } } },
      yAxis: { type: 'value', axisLabel: { color: darkText }, splitLine: { lineStyle: { color: '#1e293b' } } },
      series: [{ type: 'line', data: stats.residencyTrend.map(i => i.count), smooth: true, symbolSize: 6, lineStyle: { width: 2 },
        areaStyle: { color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [{ offset: 0, color: 'rgba(59,130,246,0.25)' }, { offset: 1, color: 'rgba(59,130,246,0.01)' }]) } }], color: ['#3b82f6'] })
    charts.push(c)
  }

  if (stats.serviceTop) {
    const data = [...stats.serviceTop].reverse()
    const c = echarts.init(barServiceChart.value)
    c.setOption({ ...baseOpt(), tooltip: { trigger: 'axis' },
      xAxis: { type: 'value', axisLabel: { color: darkText }, splitLine: { lineStyle: { color: '#1e293b' } } },
      yAxis: { type: 'category', data: data.map(i => i.name), axisLabel: { color: darkText, fontSize: 11 }, axisLine: { lineStyle: { color: '#1e293b' } } },
      series: [{ type: 'bar', data: data.map(i => i.value), barWidth: 16, itemStyle: { borderRadius: [0, 6, 6, 0] } }], color: colors })
    charts.push(c)
  }

  if (stats.residencyByStatus) {
    const c = echarts.init(pieResidencyStatusChart.value)
    const statusColors = { pending: '#f59e0b', approved: '#22c55e', rejected: '#ef4444' }
    c.setOption({ ...baseOpt(), tooltip: { trigger: 'item' },
      series: [{ type: 'pie', radius: ['40%', '70%'], center: ['50%', '52%'], label: { color: darkText, fontSize: 11 },
        data: stats.residencyByStatus.map(i => {
          const label = i.name === 'pending' ? '待审批' : i.name === 'approved' ? '已通过' : '已驳回'
          return { name: label, value: i.value, itemStyle: { color: statusColors[i.name] || '#94a3b8' } }
        }), itemStyle: { borderColor: '#0A0E27', borderWidth: 3, borderRadius: 4 } }] })
    charts.push(c)
  }

  if (stats.userTrend) {
    const c = echarts.init(lineUserChart.value)
    c.setOption({ ...baseOpt(), tooltip: { trigger: 'axis' },
      xAxis: { type: 'category', data: stats.userTrend.map(i => i.month), axisLabel: { color: darkText }, axisLine: { lineStyle: { color: '#1e293b' } } },
      yAxis: { type: 'value', axisLabel: { color: darkText }, splitLine: { lineStyle: { color: '#1e293b' } } },
      series: [{ type: 'line', data: stats.userTrend.map(i => i.count), smooth: true, symbolSize: 6, lineStyle: { width: 2 },
        areaStyle: { color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [{ offset: 0, color: 'rgba(34,197,94,0.25)' }, { offset: 1, color: 'rgba(34,197,94,0.01)' }]) } }], color: ['#22c55e'] })
    charts.push(c)
  }
}

function tickTime() {
  now.value = new Date().toLocaleString('zh-CN', { hour12: false })
}

async function fetchStats() {
  try {
    const res = await request.get('/admin/dashboard/stats')
    Object.assign(stats, res.data)
    initCharts()
  } catch { /* */ }
}

onMounted(() => {
  tickTime()
  timer = setInterval(tickTime, 1000)
  fetchStats()
})

onBeforeUnmount(() => {
  clearInterval(timer)
  charts.forEach(c => c?.dispose())
})

window.addEventListener('resize', () => charts.forEach(c => c?.resize()))
</script>

<style scoped>
/* OLED dark theme */
.dashboard {
  width: 100vw;
  height: 100vh;
  overflow: hidden;
  background: #0A0E27;
  color: #e2e8f0;
  display: flex;
  flex-direction: column;
  font-family: 'Fira Sans', 'PingFang SC', 'Microsoft YaHei', sans-serif;
}

/* Top bar */
.dash-topbar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 0 36px;
  height: 64px;
  flex-shrink: 0;
  background: rgba(255,255,255,0.02);
  border-bottom: 1px solid rgba(59,130,246,0.15);
  box-shadow: 0 2px 20px rgba(59,130,246,0.06);
}
.dash-title-row { display: flex; align-items: baseline; gap: 16px; }
.dash-title-row h1 {
  font-size: 22px; font-weight: 700; letter-spacing: 3px; margin: 0;
  background: linear-gradient(90deg, #60a5fa, #38bdf8);
  -webkit-background-clip: text; -webkit-text-fill-color: transparent;
}
.dash-subtitle { font-size: 12px; color: #475569; letter-spacing: 2px; text-transform: uppercase; }
.dash-topbar-right { display: flex; align-items: center; gap: 20px; }
.dash-time { font-size: 16px; color: #64748b; font-variant-numeric: tabular-nums; letter-spacing: 1px; }
.back-btn {
  --el-button-bg-color: rgba(59,130,246,0.12);
  --el-button-border-color: rgba(59,130,246,0.25);
  --el-button-text-color: #93c5fd;
  --el-button-hover-bg-color: rgba(59,130,246,0.2);
  --el-button-hover-border-color: rgba(59,130,246,0.4);
  --el-button-hover-text-color: #bfdbfe;
}

/* Body fills remaining space */
.dash-body {
  flex: 1;
  padding: 16px 28px;
  display: flex;
  flex-direction: column;
  gap: 14px;
  overflow: hidden;
}

/* KPI row */
.kpi-row {
  display: grid;
  grid-template-columns: repeat(5, 1fr);
  gap: 14px;
  flex-shrink: 0;
}
.kpi-card {
  background: rgba(255,255,255,0.03);
  border: 1px solid rgba(59,130,246,0.12);
  border-radius: 12px;
  padding: 16px 20px;
  display: flex;
  align-items: center;
  gap: 14px;
  backdrop-filter: blur(10px);
  transition: border-color 0.3s, box-shadow 0.3s;
}
.kpi-card:hover {
  border-color: rgba(59,130,246,0.35);
  box-shadow: 0 0 20px rgba(59,130,246,0.08);
}
.kpi-icon {
  width: 52px; height: 52px; border-radius: 12px;
  background: rgba(59,130,246,0.1);
  display: flex; align-items: center; justify-content: center;
  color: var(--glow);
  flex-shrink: 0;
}
.kpi-num { font-size: 32px; font-weight: 700; line-height: 1.1; color: #f1f5f9; }
.kpi-label { font-size: 13px; color: #64748b; margin-top: 2px; }

/* Chart grid */
.chart-grid {
  flex: 1;
  display: grid;
  grid-template-columns: 1fr 1fr 1fr 1fr;
  grid-template-rows: 1fr 1fr;
  gap: 12px;
  min-height: 0;
}
.chart-card {
  background: rgba(255,255,255,0.02);
  border: 1px solid rgba(255,255,255,0.05);
  border-radius: 12px;
  padding: 12px 14px 8px;
  display: flex;
  flex-direction: column;
  min-height: 0;
}
.chart-card.wide { grid-column: span 2; }
.chart-card-header {
  font-size: 13px; font-weight: 600; color: #94a3b8;
  margin-bottom: 2px; flex-shrink: 0;
  letter-spacing: 0.5px;
}
.chart-dom { flex: 1; min-height: 0; }
</style>
