<template>
  <div class="admin-page">
    <div class="page-header">
      <h2>满意度调查管理</h2>
      <el-button type="primary" @click="showAssignDialog">发放问卷</el-button>
    </div>

    <el-card class="search-card">
      <el-form inline>
        <el-form-item label="状态">
          <el-select v-model="queryStatus" placeholder="全部" clearable style="width:150px" @change="fetchList">
            <el-option value="pending" label="待填写" />
            <el-option value="completed" label="已提交" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="fetchList">搜索</el-button>
          <el-button @click="queryStatus = ''; fetchList()">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <el-card>
      <el-table v-loading="loading" :data="tableData" stripe empty-text="暂无记录">
        <el-table-column prop="id" label="ID" width="60" />
        <el-table-column prop="surveyCode" label="问卷编码" width="110" />
        <el-table-column prop="username" label="发放用户" width="120" />
        <el-table-column prop="companyName" label="企业名称" min-width="150" show-overflow-tooltip />
        <el-table-column label="状态" width="80">
          <template #default="{ row }">
            <el-tag :type="row.status === 'completed' ? 'success' : 'warning'" size="small">
              {{ row.status === 'completed' ? '已提交' : '待填写' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="assignedAt" label="发放时间" width="160" />
        <el-table-column prop="submittedAt" label="提交时间" width="160" />
        <el-table-column label="操作" width="160" fixed="right">
          <template #default="{ row }">
            <el-button size="small" type="primary" link @click="copyLink(row.surveyCode)">复制链接</el-button>
            <el-button size="small" @click="openDetail(row.id)">查看</el-button>
          </template>
        </el-table-column>
      </el-table>
      <el-pagination
        v-model:current-page="page" v-model:page-size="size" :total="total"
        layout="total, sizes, prev, pager, next" :page-sizes="[10, 20, 50]"
        @change="fetchList" class="pagination"
      />
    </el-card>

    <!-- 发放弹窗 -->
    <el-dialog v-model="assignVisible" title="发放问卷" width="400px">
      <el-form label-width="80px">
        <el-form-item label="选择用户">
          <el-select v-model="assignUserId" filterable placeholder="搜索用户" style="width:100%">
            <el-option v-for="u in users" :key="u.id" :label="u.username + ' (' + (u.nickname || '-') + ')'" :value="u.id" />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="assignVisible = false">取消</el-button>
        <el-button type="primary" :loading="assigning" @click="handleAssign">确认发放</el-button>
      </template>
    </el-dialog>

    <!-- 详情弹窗 -->
    <el-dialog v-model="detailVisible" title="问卷详情" width="700px">
      <template v-if="detail">
        <el-descriptions :column="2" border style="margin-bottom:20px">
          <el-descriptions-item label="企业名称">{{ detail.companyName || '-' }}</el-descriptions-item>
          <el-descriptions-item label="所属领域">{{ detail.industry || '-' }}</el-descriptions-item>
          <el-descriptions-item label="企业人数">{{ detail.employeeCount || '-' }}</el-descriptions-item>
          <el-descriptions-item label="联系人">{{ detail.contactName || '-' }}</el-descriptions-item>
          <el-descriptions-item label="联系电话">{{ detail.contactPhone || '-' }}</el-descriptions-item>
          <el-descriptions-item label="状态">
            <el-tag :type="detail.status === 'completed' ? 'success' : 'warning'" size="small">{{ detail.status === 'completed' ? '已提交' : '待填写' }}</el-tag>
          </el-descriptions-item>
        </el-descriptions>
        <template v-if="detail.status === 'completed'">
          <el-table :data="scoreRows" stripe size="small">
            <el-table-column prop="label" label="题目" />
            <el-table-column prop="score" label="得分" width="80" align="center" />
          </el-table>
          <div v-if="detail.q11" style="margin-top:16px;padding:12px;background:#f5f6fa;border-radius:8px">
            <strong>11、如何增进服务满意度：</strong>{{ detail.q11 }}
          </div>
        </template>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue'
import { ElMessage } from 'element-plus'
import request from '@/utils/request'

const loading = ref(false)
const tableData = ref([])
const total = ref(0)
const page = ref(1)
const size = ref(10)
const queryStatus = ref('')

const assignVisible = ref(false)
const assignUserId = ref(null)
const assigning = ref(false)
const users = ref([])

const detailVisible = ref(false)
const detail = ref(null)

const scoreLabels = [
  '1.办公空间及环境', '2.会议室/水吧/书吧等公共场所', '3.物业服务人员能力',
  '4.各类活动对企业帮助', '5.人员培训/创业指导', '6.投融资对接服务', '7.信息服务/管理咨询',
  '8.服务计划及实施及时性', '9.服务质量与效果', '10.临时和应急工作反应能力'
]
const scoreRows = computed(() => {
  if (!detail.value) return []
  return scoreLabels.map((label, i) => ({ label, score: detail.value['q' + (i + 1)] || '-' }))
})

const fetchList = async () => {
  loading.value = true
  try {
    const res = await request.get('/admin/survey/list', { params: { page: page.value, size: size.value, status: queryStatus.value || undefined } })
    const d = res.data
    tableData.value = d.records || []
    total.value = d.total || 0
  } finally { loading.value = false }
}

const showAssignDialog = async () => {
  try {
    const res = await request.get('/admin/users', { params: { page: 1, size: 200 } })
    users.value = res.data.records || []
    assignUserId.value = null
    assignVisible.value = true
  } catch { ElMessage.error('加载用户失败') }
}

const handleAssign = async () => {
  if (!assignUserId.value) { ElMessage.warning('请选择用户'); return }
  assigning.value = true
  try {
    const res = await request.post('/admin/survey/assign', { userId: assignUserId.value })
    const code = res.data.surveyCode
    ElMessage.success('发放成功，问卷编码：' + code)
    assignVisible.value = false
    fetchList()
  } catch { ElMessage.error('发放失败') }
  finally { assigning.value = false }
}

const copyLink = (code) => {
  const link = window.location.origin + '/survey/' + code
  navigator.clipboard.writeText(link).then(() => ElMessage.success('链接已复制'))
}

const openDetail = async (id) => {
  try {
    const res = await request.get('/admin/survey/' + id)
    detail.value = res.data
    detailVisible.value = true
  } catch { ElMessage.error('获取详情失败') }
}

fetchList()
</script>
