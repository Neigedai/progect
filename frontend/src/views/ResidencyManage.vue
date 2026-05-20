<template>
  <div class="admin-page">
    <div class="page-header">
      <h2>入驻申请管理</h2>
    </div>

    <el-card class="search-card">
      <el-form inline>
        <el-form-item label="联系人">
          <el-input v-model="queryContact" placeholder="输入联系人" clearable style="width:160px" @keyup.enter="fetchList" />
        </el-form-item>
        <el-form-item label="行业类型">
          <el-select v-model="queryIndustry" placeholder="全部" clearable style="width:180px" @change="fetchList">
            <el-option value="人工智能" label="人工智能" />
            <el-option value="生物医药" label="生物医药" />
            <el-option value="新材料" label="新材料" />
            <el-option value="智能制造" label="智能制造" />
            <el-option value="现代服务" label="现代服务" />
            <el-option value="IT" label="IT" />
          </el-select>
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="queryStatus" placeholder="全部" clearable style="width:180px" @change="fetchList">
            <el-option value="pending" label="待审批" />
            <el-option value="approved" label="已通过" />
            <el-option value="rejected" label="已驳回" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="fetchList">搜索</el-button>
          <el-button @click="queryContact = ''; queryIndustry = ''; queryStatus = ''; fetchList()">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <el-card>
      <el-table v-loading="loading" :data="tableData" stripe empty-text="暂无入驻申请">
        <el-table-column prop="id" label="ID" width="60" />
        <el-table-column prop="contactName" label="联系人" width="100" />
        <el-table-column prop="contactPhone" label="联系电话" width="120" />
        <el-table-column prop="area" label="面积需求" width="120" />
        <el-table-column prop="industryType" label="行业类型" width="100" />
        <el-table-column prop="expectedEntryDate" label="预计入驻" width="110" />
        <el-table-column prop="additionalInfo" label="其他信息" min-width="150" show-overflow-tooltip />
        <el-table-column prop="createTime" label="提交时间" width="160" />
        <el-table-column label="操作" width="80" fixed="right">
          <template #default="{ row }">
            <el-button size="small" @click="openDetail(row)">查看</el-button>
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

    <el-dialog v-model="dialogVisible" title="申请详情" width="600px">
      <el-descriptions :column="2" border>
        <el-descriptions-item label="联系人">{{ detail.contactName }}</el-descriptions-item>
        <el-descriptions-item label="联系电话">{{ detail.contactPhone }}</el-descriptions-item>
        <el-descriptions-item label="面积需求">{{ detail.area }}</el-descriptions-item>
        <el-descriptions-item label="行业类型">{{ detail.industryType }}</el-descriptions-item>
        <el-descriptions-item label="预计入驻时间">{{ detail.expectedEntryDate }}</el-descriptions-item>
        <el-descriptions-item label="其他信息" :span="2">{{ detail.additionalInfo || '-' }}</el-descriptions-item>
        <el-descriptions-item label="提交时间">{{ detail.createTime }}</el-descriptions-item>
      </el-descriptions>
      <template #footer>
        <el-button @click="dialogVisible = false">关闭</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { ElMessage } from 'element-plus'
import { getResidencyApplications, getResidencyApplicationDetail } from '@/api/residency'

const loading = ref(false)
const queryContact = ref('')
const queryIndustry = ref('')
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
    const res = await getResidencyApplications({ page: page.value, size: size.value, contactName: queryContact.value || undefined, industryType: queryIndustry.value || undefined, status: queryStatus.value || undefined })
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

fetchList()
</script>

<style scoped>
.admin-page { padding-bottom: 24px; }
.admin-page .el-card { border-radius: var(--radius-lg); }
</style>
