<template>
  <div class="admin-page">
    <div class="page-header">
      <h2>入驻申请审核</h2>
    </div>

    <el-card class="search-card">
      <el-form inline>
        <el-form-item label="企业名称">
          <el-input v-model="queryCompany" placeholder="输入企业名称" clearable style="width:180px" @keyup.enter="fetchList" />
        </el-form-item>
        <el-form-item label="法人姓名">
          <el-input v-model="queryName" placeholder="输入法人姓名" clearable style="width:160px" @keyup.enter="fetchList" />
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="queryStatus" placeholder="全部" clearable style="width:140px" @change="fetchList">
            <el-option value="pending" label="待审批" />
            <el-option value="approved" label="已通过" />
            <el-option value="rejected" label="已驳回" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="fetchList">搜索</el-button>
          <el-button @click="queryName = ''; queryCompany = ''; queryStatus = ''; fetchList()">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <el-card>
      <el-table v-loading="loading" :data="tableData" stripe empty-text="暂无入驻申请">
        <el-table-column prop="id" label="ID" width="60" />
        <el-table-column prop="companyName" label="企业名称" min-width="150" />
        <el-table-column prop="legalPersonName" label="法人姓名" width="100" />
        <el-table-column prop="legalPersonPhone" label="法人电话" width="130" />
        <el-table-column prop="location" label="入驻地点" width="120" />
        <el-table-column label="企业类型" width="100">
          <template #default="{ row }">
            {{ ['','科技类','游戏动漫类','电商贸易类','咨询服务及其他'][row.enterpriseType] || '' }}
          </template>
        </el-table-column>
        <el-table-column label="状态" width="90">
          <template #default="{ row }">
            <el-tag :type="row.status === 'approved' ? 'success' : row.status === 'rejected' ? 'danger' : 'warning'" size="small">
              {{ row.status === 'approved' ? '已通过' : row.status === 'rejected' ? '已驳回' : '待审批' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="提交时间" width="160" />
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="{ row }">
            <template v-if="row.status === 'pending'">
              <el-button size="small" @click="openDetail(row)">查看</el-button>
              <el-button size="small" type="success" @click="handleReview(row, 'approved')">通过</el-button>
              <el-button size="small" type="danger" @click="handleReview(row, 'rejected')">驳回</el-button>
            </template>
            <el-button v-else size="small" @click="openDetail(row)">查看</el-button>
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

    <el-dialog v-model="dialogVisible" title="申请详情" width="680px">
      <el-descriptions :column="2" border>
        <el-descriptions-item label="入驻地点">{{ detail.location }}</el-descriptions-item>
        <el-descriptions-item label="企业名称">{{ detail.companyName }}</el-descriptions-item>
        <el-descriptions-item label="法人姓名">{{ detail.legalPersonName }}</el-descriptions-item>
        <el-descriptions-item label="法人电话">{{ detail.legalPersonPhone }}</el-descriptions-item>
        <el-descriptions-item label="企业类型">
          {{ ['','科技类','游戏动漫类','电商贸易类','咨询服务及其他'][detail.enterpriseType] || '-' }}
        </el-descriptions-item>
        <el-descriptions-item label="企业赛道">{{ detail.enterpriseTrack || '-' }}</el-descriptions-item>
        <el-descriptions-item label="应急联系人">{{ detail.emergencyContactName }}</el-descriptions-item>
        <el-descriptions-item label="应急联系人电话">{{ detail.emergencyContactPhone }}</el-descriptions-item>
        <el-descriptions-item label="提交时间">{{ detail.createTime }}</el-descriptions-item>
        <el-descriptions-item label="审核意见" :span="2">
          <span :style="{ color: detail.status === 'rejected' ? '#f56c6c' : '#67c23a', fontWeight: 500 }">{{ detail.reviewComment || '-' }}</span>
        </el-descriptions-item>
        <el-descriptions-item label="营业执照" :span="2">
          <el-image v-if="detail.businessLicenseUrl" :src="detail.businessLicenseUrl" style="max-width:300px;max-height:200px" fit="contain" :preview-src-list="[detail.businessLicenseUrl]" preview-teleported />
          <span v-else>-</span>
        </el-descriptions-item>
        <el-descriptions-item label="法人身份证正面" :span="2">
          <el-image v-if="detail.legalPersonIdFront" :src="detail.legalPersonIdFront" style="max-width:300px;max-height:200px" fit="contain" :preview-src-list="[detail.legalPersonIdFront]" preview-teleported />
          <span v-else>-</span>
        </el-descriptions-item>
        <el-descriptions-item label="法人身份证反面" :span="2">
          <el-image v-if="detail.legalPersonIdBack" :src="detail.legalPersonIdBack" style="max-width:300px;max-height:200px" fit="contain" :preview-src-list="[detail.legalPersonIdBack]" preview-teleported />
          <span v-else>-</span>
        </el-descriptions-item>
      </el-descriptions>
      <template #footer>
        <template v-if="detail.status === 'pending'">
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
import { getResidencyApplications, getResidencyApplicationDetail, approveResidencyApplication } from '@/api/residency'

const loading = ref(false)
const queryName = ref('')
const queryCompany = ref('')
const queryStatus = ref('')
const tableData = ref([])
const total = ref(0)
const page = ref(1)
const size = ref(10)

const dialogVisible = ref(false)
const detail = ref({})

const fetchList = async () => {
  loading.value = true
  try {
    const res = await getResidencyApplications({ page: page.value, size: size.value, contactName: queryName.value || undefined, status: queryStatus.value || undefined })
    const data = res.data
    tableData.value = data.records || []
    total.value = data.total || 0
  } finally {
    loading.value = false
  }
}

const openDetail = async (row) => {
  try {
    const res = await getResidencyApplicationDetail(row.id)
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
      return approveResidencyApplication(row.id, { status, reviewComment: value || '' })
    })
  } catch {
    return
  }
  ElMessage.success(status === 'approved' ? '已通过' : '已驳回')
  fetchList()
}

fetchList()
</script>

<style scoped>
.admin-page { padding-bottom: 24px; }
.admin-page .el-card { border-radius: var(--radius-lg); }
</style>
