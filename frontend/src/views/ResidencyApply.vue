<template>
  <div class="apply-residency">
    <el-card class="form-card">
      <template #header>
        <div class="card-header">
          <el-breadcrumb separator=">">
            <el-breadcrumb-item :to="{ path: '/park-overview' }">园区概况</el-breadcrumb-item>
            <el-breadcrumb-item>在线入驻申请</el-breadcrumb-item>
          </el-breadcrumb>
        </div>
      </template>
      <el-form v-if="showForm" ref="formRef" :model="form" :rules="rules" label-width="130px" style="max-width:640px">
        <el-form-item label="法人姓名" prop="legalPersonName">
          <el-input v-model="form.legalPersonName" placeholder="请输入法人姓名" />
        </el-form-item>
        <el-form-item label="法人联系电话" prop="legalPersonPhone">
          <el-input v-model="form.legalPersonPhone" disabled />
        </el-form-item>
        <el-form-item label="法人身份证正面" prop="legalPersonIdFront">
          <el-input v-model="form.legalPersonIdFront" placeholder="上传法人身份证正面" />
          <upload-btn @uploaded="url => form.legalPersonIdFront = url" />
        </el-form-item>
        <el-form-item label="法人身份证反面" prop="legalPersonIdBack">
          <el-input v-model="form.legalPersonIdBack" placeholder="上传法人身份证反面" />
          <upload-btn @uploaded="url => form.legalPersonIdBack = url" />
        </el-form-item>
        <el-form-item label="面积需求" prop="area">
          <el-input v-model="form.area" placeholder="如：200-500㎡" />
        </el-form-item>
        <el-form-item label="行业类型" prop="industryType">
          <el-select v-model="form.industryType" placeholder="请选择行业类型" style="width:100%">
            <el-option label="信息技术" value="信息技术" />
            <el-option label="生物医药" value="生物医药" />
            <el-option label="智能制造" value="智能制造" />
            <el-option label="新材料" value="新材料" />
            <el-option label="新能源" value="新能源" />
            <el-option label="现代服务" value="现代服务" />
            <el-option label="文化创意" value="文化创意" />
            <el-option label="其他" value="其他" />
          </el-select>
        </el-form-item>
        <el-form-item label="预计入驻时间" prop="expectedEntryDate">
          <el-date-picker
            v-model="form.expectedEntryDate"
            type="date"
            placeholder="请选择日期"
            value-format="YYYY-MM-DD"
            style="width:100%"
          />
        </el-form-item>
        <el-form-item label="其他信息" prop="additionalInfo">
          <el-input
            v-model="form.additionalInfo"
            type="textarea"
            :rows="4"
            placeholder="补充说明（选填）"
          />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" :loading="submitting" @click="handleSubmit">提交申请</el-button>
          <el-button @click="$router.back()">返回</el-button>
        </el-form-item>
      </el-form>

      <div v-if="!showForm && myList.length > 0" style="text-align:center;padding:20px 0">
        <el-button type="primary" @click="showForm = true">填写新的入驻申请</el-button>
      </div>
    </el-card>

    <el-card v-if="myList.length" class="list-card">
      <template #header><h3>我的申请记录</h3></template>
      <el-table :data="myList" stripe>
        <el-table-column prop="id" label="编号" width="70" />
        <el-table-column prop="industryType" label="行业类型" width="110" />
        <el-table-column prop="area" label="面积需求" width="110" />
        <el-table-column prop="expectedEntryDate" label="预计入驻" width="120" />
        <el-table-column label="状态" width="90">
          <template #default="{ row }">
            <el-tag :type="row.status === 'approved' ? 'success' : row.status === 'rejected' ? 'danger' : 'warning'" size="small">
              {{ row.status === 'approved' ? '已通过' : row.status === 'rejected' ? '已驳回' : '待审核' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="提交时间" min-width="160" />
        <el-table-column label="审核意见" min-width="180">
          <template #default="{ row }">
            <span v-if="row.status === 'rejected' && row.reviewComment" style="color:#f56c6c">{{ row.reviewComment }}</span>
            <span v-else-if="row.status === 'approved' && row.reviewComment" style="color:#67c23a">{{ row.reviewComment }}</span>
            <span v-else style="color:#909399">-</span>
          </template>
        </el-table-column>
      </el-table>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { submitResidencyApplication, getMyApplications } from '@/api/residency'
import { useAuth } from '@/utils/auth'
import UploadBtn from '@/components/UploadBtn.vue'

const router = useRouter()
const { state: auth } = useAuth()
const formRef = ref(null)
const submitting = ref(false)
const myList = ref([])
const showForm = ref(false)

const form = reactive({
  legalPersonName: auth.user?.nickname || auth.user?.username || '',
  legalPersonPhone: auth.user?.phone || '',
  legalPersonIdFront: '',
  legalPersonIdBack: '',
  area: '',
  industryType: '',
  expectedEntryDate: '',
  additionalInfo: ''
})

const rules = {
  legalPersonName: [{ required: true, message: '请输入法人姓名', trigger: 'blur' }],
  legalPersonPhone: [
    { required: true, message: '请输入法人联系电话', trigger: 'blur' },
    { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号', trigger: 'blur' }
  ],
  area: [{ required: true, message: '请输入面积需求', trigger: 'blur' }],
  industryType: [{ required: true, message: '请选择行业类型', trigger: 'change' }]
}

const fetchMyList = async () => {
  try {
    const res = await getMyApplications({ page: 1, size: 50 })
    myList.value = res.data.records || []
  } catch { /* */ }
}

const handleSubmit = async () => {
  try {
    await formRef.value.validate()
  } catch {
    return
  }
  submitting.value = true
  try {
    await submitResidencyApplication({ ...form })
    ElMessage.success('入驻申请已提交')
    showForm.value = false
    form.area = ''
    form.industryType = ''
    form.expectedEntryDate = ''
    form.additionalInfo = ''
    form.legalPersonIdFront = ''
    form.legalPersonIdBack = ''
    fetchMyList()
  } catch {
    ElMessage.error('提交失败，请稍后重试')
  } finally {
    submitting.value = false
  }
}

onMounted(async () => {
  await fetchMyList()
  showForm.value = myList.value.length === 0
})
</script>

<style scoped>
.apply-residency {
  max-width: 800px;
  margin: 0 auto;
}
.form-card {
  border-radius: var(--radius-lg);
}
.list-card {
  border-radius: var(--radius-lg);
  margin-top: 20px;
}
.list-card h3 {
  margin: 0;
  font-size: 16px;
}
.card-header {
  display: flex;
  align-items: center;
}
@media (max-width: 768px) {
  .apply-residency { padding: 0 8px; }
}
</style>
