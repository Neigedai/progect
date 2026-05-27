<template>
  <div class="admin-page">
    <div class="page-header">
      <h2>企业认证审核</h2>
    </div>

    <el-card class="search-card">
      <el-form inline>
        <el-form-item label="企业名称">
          <el-input v-model="queryCompanyName" placeholder="输入企业名称" clearable style="width:220px" @keyup.enter="fetchList" />
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="queryStatus" placeholder="全部" clearable style="width:180px" @change="fetchList">
            <el-option label="待审核" value="pending" />
            <el-option label="已通过" value="approved" />
            <el-option label="已驳回" value="rejected" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="fetchList">搜索</el-button>
          <el-button @click="queryStatus = ''; queryCompanyName = ''; fetchList()">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <el-card>
      <el-table v-loading="loading" :data="tableData" stripe empty-text="暂无认证申请">
        <el-table-column prop="id" label="ID" width="60" />
        <el-table-column label="用户信息" width="130">
          <template #default="{ row }">
            <div v-if="row.userNickname">{{ row.userNickname }}</div>
            <div v-if="row.userPhone" style="color:#909399;font-size:12px">{{ row.userPhone }}</div>
            <span v-if="!row.userNickname && !row.userPhone" style="color:#999">用户{{ row.userId }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="companyName" label="企业名称" min-width="160" />
        <el-table-column prop="creditCode" label="信用代码" width="180" />
        <el-table-column label="营业执照" width="90" align="center">
          <template #default="{ row }">
            <el-image v-if="row.licenseUrl" :src="row.licenseUrl" style="width:60px;height:40px" fit="contain" :preview-src-list="[row.licenseUrl]" preview-teleported />
            <span v-else style="color:#999">-</span>
          </template>
        </el-table-column>
        <el-table-column label="法人身份证正面" width="100" align="center">
          <template #default="{ row }">
            <el-image v-if="row.legalPersonIdUrl" :src="row.legalPersonIdUrl" style="width:60px;height:40px" fit="contain" :preview-src-list="[row.legalPersonIdUrl]" preview-teleported />
            <span v-else style="color:#999">-</span>
          </template>
        </el-table-column>
        <el-table-column label="法人身份证反面" width="100" align="center">
          <template #default="{ row }">
            <el-image v-if="row.legalPersonIdBackUrl" :src="row.legalPersonIdBackUrl" style="width:60px;height:40px" fit="contain" :preview-src-list="[row.legalPersonIdBackUrl]" preview-teleported />
            <span v-else style="color:#999">-</span>
          </template>
        </el-table-column>
        <el-table-column label="状态" width="90" align="center">
          <template #default="{ row }">
            <el-tag :type="statusType(row.authStatus)">{{ statusLabel(row.authStatus) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="标签" width="130" align="center">
          <template #default="{ row }">
            <el-select
              v-model="row.tag"
              placeholder="无"
              clearable
              size="small"
              style="width:110px"
              @change="(val) => handleTagChange(row, val)"
            >
              <el-option v-for="t in tagOptions" :key="t" :label="t" :value="t" />
            </el-select>
          </template>
        </el-table-column>
        <el-table-column prop="authSubmittedAt" label="提交时间" width="160" />
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="{ row }">
            <el-button size="small" @click="openDetail(row)">查看</el-button>
            <template v-if="row.authStatus === 'pending'">
              <el-button size="small" type="success" @click="handleReview(row, 'approved')">通过</el-button>
              <el-button size="small" type="danger" @click="handleReview(row, 'rejected')">驳回</el-button>
            </template>
          </template>
        </el-table-column>
      </el-table>
      <el-pagination
        v-model:current-page="page"
        v-model:page-size="size"
        :total="total"
        layout="total, sizes, prev, pager, next"
        :page-sizes="[10, 20, 50]"
        @change="fetchList"
        class="pagination"
      />
    </el-card>

    <el-dialog v-model="dialogVisible" title="认证详情" width="600px">
      <el-descriptions :column="2" border>
        <el-descriptions-item label="企业名称" :span="2">{{ detail.companyName }}</el-descriptions-item>
        <el-descriptions-item label="统一社会信用代码" :span="2">{{ detail.creditCode }}</el-descriptions-item>
        <el-descriptions-item label="营业执照" :span="2">
          <el-image v-if="detail.licenseUrl" :src="detail.licenseUrl" style="max-width:300px;max-height:200px" fit="contain" :preview-src-list="[detail.licenseUrl]" preview-teleported />
          <span v-else>-</span>
        </el-descriptions-item>
        <el-descriptions-item label="法人身份证正面" :span="2">
          <el-image v-if="detail.legalPersonIdUrl" :src="detail.legalPersonIdUrl" style="max-width:300px;max-height:200px" fit="contain" :preview-src-list="[detail.legalPersonIdUrl]" preview-teleported />
          <span v-else>-</span>
        </el-descriptions-item>
        <el-descriptions-item label="法人身份证反面" :span="2">
          <el-image v-if="detail.legalPersonIdBackUrl" :src="detail.legalPersonIdBackUrl" style="max-width:300px;max-height:200px" fit="contain" :preview-src-list="[detail.legalPersonIdBackUrl]" preview-teleported />
          <span v-else>-</span>
        </el-descriptions-item>
        <el-descriptions-item label="状态">
          <el-tag :type="statusType(detail.authStatus)">{{ statusLabel(detail.authStatus) }}</el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="提交时间">{{ detail.authSubmittedAt }}</el-descriptions-item>
        <el-descriptions-item label="审核时间">{{ detail.authReviewedAt || '-' }}</el-descriptions-item>
        <el-descriptions-item label="审核意见" :span="2">{{ detail.reviewComment || '-' }}</el-descriptions-item>
      </el-descriptions>
      <template #footer>
        <template v-if="detail.authStatus === 'pending'">
          <el-button type="success" @click="handleReview(detail, 'approved'); dialogVisible = false">通过</el-button>
          <el-button type="danger" @click="handleReview(detail, 'rejected'); dialogVisible = false">驳回</el-button>
        </template>
        <el-button v-else @click="dialogVisible = false">关闭</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getEnterpriseAuthList, getEnterpriseAuthDetail, reviewEnterpriseAuth, updateEnterpriseTag } from '@/api/enterprise'

const tagOptions = ['重点企业', '初创企业', '瞪羚企业', '独角兽企业', '规上企业']

const loading = ref(false)
const tableData = ref([])
const total = ref(0)
const page = ref(1)
const size = ref(10)
const queryStatus = ref('')
const queryCompanyName = ref('')

const dialogVisible = ref(false)
const detail = ref({})

const statusType = (s) => s === 'approved' ? 'success' : s === 'rejected' ? 'danger' : 'warning'
const statusLabel = (s) => s === 'approved' ? '已通过' : s === 'rejected' ? '已驳回' : '待审核'

const fetchList = async () => {
  loading.value = true
  try {
    const res = await getEnterpriseAuthList({ page: page.value, size: size.value, status: queryStatus.value || undefined, companyName: queryCompanyName.value || undefined })
    const data = res.data
    tableData.value = data.records || []
    total.value = data.total || 0
  } finally {
    loading.value = false
  }
}

const openDetail = async (row) => {
  try {
    const res = await getEnterpriseAuthDetail(row.id)
    detail.value = res.data
    dialogVisible.value = true
  } catch {
    ElMessage.error('获取详情失败')
  }
}

const handleReview = async (row, status) => {
  try {
    await ElMessageBox.prompt('请输入审核意见（选填）', status === 'approved' ? '确认通过' : '确认驳回', {
      confirmButtonText: '确定',
      cancelButtonText: '取消'
    }).then(({ value }) => {
      return reviewEnterpriseAuth(row.id, { status, reviewComment: value || '' })
    })
  } catch {
    return
  }
  ElMessage.success(status === 'approved' ? '已通过' : '已驳回')
  fetchList()
}

const handleTagChange = async (row, val) => {
  try {
    await updateEnterpriseTag(row.id, val || '')
    ElMessage.success('标签已更新')
  } catch {
    row.tag = row.tag
    ElMessage.error('更新标签失败')
  }
}

fetchList()
</script>

<style scoped>
.admin-page { padding-bottom: 24px; }
.admin-page .el-card { border-radius: var(--radius-lg); }
:deep(.el-table__fixed-right) { z-index: 3; }
:deep(.el-table__body tr.current-row > td) { z-index: 0; }
</style>
