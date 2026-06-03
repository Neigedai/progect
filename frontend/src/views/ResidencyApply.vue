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
      <el-form v-if="showForm" ref="formRef" :model="form" :rules="rules" label-width="140px" style="max-width:640px">
        <el-form-item label="入驻地点" prop="location">
          <el-select v-model="form.location" placeholder="请选择入驻地点" style="width:100%">
            <el-option label="沈阳市皇姑区（网易沈阳数字产业中心）" value="沈阳市皇姑区（网易沈阳数字产业中心）" />
            <el-option label="沈阳市自贸区" value="沈阳市自贸区" />
            <el-option label="沈阳市沈河区（马官桥街道办事处）" value="沈阳市沈河区（马官桥街道办事处）" />
            <el-option label="沈阳市浑南区" value="沈阳市浑南区" />
            <el-option label="沈阳市铁西区" value="沈阳市铁西区" />
            <el-option label="沈阳市沈北新区" value="沈阳市沈北新区" />
            <el-option label="辽阳市文圣区（网易辽阳联合创新中心）" value="辽阳市文圣区（网易辽阳联合创新中心）" />
            <el-option label="锦州市滨海新区" value="锦州市滨海新区" />
          </el-select>
        </el-form-item>

        <el-form-item label="企业名称" prop="companyName">
          <el-input v-model="form.companyName" placeholder="请输入企业全称" />
        </el-form-item>

        <el-form-item label="营业执照" prop="businessLicenseUrl">
          <el-input v-model="form.businessLicenseUrl" placeholder="上传营业执照" readonly />
          <upload-btn @uploaded="url => form.businessLicenseUrl = url" />
        </el-form-item>

        <el-form-item label="法人姓名" prop="legalPersonName">
          <el-input v-model="form.legalPersonName" placeholder="请输入法人姓名" />
        </el-form-item>

        <el-form-item label="本人手机号" prop="legalPersonPhone">
          <el-input v-model="form.legalPersonPhone" disabled />
        </el-form-item>

        <el-form-item label="法人身份证正面" prop="legalPersonIdFront">
          <el-input v-model="form.legalPersonIdFront" placeholder="上传法人身份证正面" readonly />
          <upload-btn @uploaded="url => form.legalPersonIdFront = url" />
        </el-form-item>

        <el-form-item label="法人身份证反面" prop="legalPersonIdBack">
          <el-input v-model="form.legalPersonIdBack" placeholder="上传法人身份证反面" readonly />
          <upload-btn @uploaded="url => form.legalPersonIdBack = url" />
        </el-form-item>

        <el-form-item label="应急联系人" prop="emergencyContactName">
          <el-input v-model="form.emergencyContactName" placeholder="请输入应急联系人姓名" />
        </el-form-item>

        <el-form-item label="应急联系人手机号" prop="emergencyContactPhone">
          <el-input v-model="form.emergencyContactPhone" placeholder="请输入应急联系人手机号" maxlength="11" />
        </el-form-item>

        <el-form-item label="企业类型" prop="enterpriseType">
          <el-radio-group v-model="form.enterpriseType" @change="onEnterpriseTypeChange">
            <el-radio :value="1">科技类</el-radio>
            <el-radio :value="2">游戏动漫类</el-radio>
            <el-radio :value="3">电商贸易类</el-radio>
            <el-radio :value="4">咨询服务及其他</el-radio>
          </el-radio-group>
        </el-form-item>

        <el-form-item label="企业赛道" prop="enterpriseTrack">
          <el-select v-model="form.enterpriseTrack" placeholder="请选择企业赛道" style="width:100%">
            <template v-if="form.enterpriseType === 1">
              <el-option label="硬科技" value="硬科技" />
              <el-option label="数智科技" value="数智科技" />
              <el-option label="消费升级" value="消费升级" />
              <el-option label="航空低空" value="航空低空" />
              <el-option label="生物医药" value="生物医药" />
              <el-option label="新材料" value="新材料" />
              <el-option label="新能源" value="新能源" />
              <el-option label="其他" value="其他" />
            </template>
            <template v-else-if="form.enterpriseType === 2">
              <el-option label="游戏开发" value="游戏开发" />
              <el-option label="游戏制作" value="游戏制作" />
              <el-option label="动漫制作" value="动漫制作" />
              <el-option label="动漫原创" value="动漫原创" />
              <el-option label="赛事活动" value="赛事活动" />
              <el-option label="技术服务" value="技术服务" />
              <el-option label="其他" value="其他" />
            </template>
            <template v-else-if="form.enterpriseType === 3">
              <el-option label="跨境电商" value="跨境电商" />
              <el-option label="国内电商" value="国内电商" />
              <el-option label="传统贸易" value="传统贸易" />
              <el-option label="供应链" value="供应链" />
              <el-option label="技术服务" value="技术服务" />
              <el-option label="其他" value="其他" />
            </template>
            <template v-else-if="form.enterpriseType === 4">
              <el-option label="建筑工程" value="建筑工程" />
              <el-option label="咨询服务" value="咨询服务" />
              <el-option label="人力派遣" value="人力派遣" />
              <el-option label="生产制造" value="生产制造" />
              <el-option label="技术服务" value="技术服务" />
              <el-option label="其他" value="其他" />
            </template>
          </el-select>
        </el-form-item>

        <el-form-item prop="agreed">
          <el-checkbox v-model="form.agreed">
            本人自愿申请入驻，并知悉上述企业开办流程及法律规定，以上信息均真实有效，特此承诺！
          </el-checkbox>
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
        <el-table-column prop="companyName" label="企业名称" width="150" />
        <el-table-column label="企业类型" width="110">
          <template #default="{ row }">
            {{ enterpriseTypeLabel(row.enterpriseType) }}
          </template>
        </el-table-column>
        <el-table-column prop="enterpriseTrack" label="赛道" width="100" />
        <el-table-column label="状态" width="90">
          <template #default="{ row }">
            <el-tag :type="row.status === 'approved' ? 'success' : row.status === 'rejected' ? 'danger' : 'warning'" size="small">
              {{ row.status === 'approved' ? '已通过' : row.status === 'rejected' ? '已驳回' : '待审核' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="驳回原因" width="160">
          <template #default="{ row }">
            <span v-if="row.status === 'rejected'" :style="{ color: '#f56c6c', fontWeight: 500 }">{{ row.reviewComment || '-' }}</span>
            <span v-else :style="{ color: '#67c23a' }">{{ row.reviewComment || '-' }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="提交时间" min-width="160" />
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
  location: '',
  companyName: '',
  businessLicenseUrl: '',
  legalPersonName: auth.user?.nickname || auth.user?.username || '',
  legalPersonPhone: auth.user?.phone || '',
  legalPersonIdFront: '',
  legalPersonIdBack: '',
  emergencyContactName: '',
  emergencyContactPhone: '',
  enterpriseType: null,
  enterpriseTrack: '',
  agreed: false
})

const enterpriseTypeLabels = { 1: '科技类', 2: '游戏动漫类', 3: '电商贸易类', 4: '咨询服务及其他' }
const enterpriseTypeLabel = (v) => enterpriseTypeLabels[v] || ''

const rules = {
  location: [{ required: true, message: '请选择入驻地点', trigger: 'change' }],
  companyName: [{ required: true, message: '请输入企业名称', trigger: 'blur' }],
  legalPersonName: [{ required: true, message: '请输入法人姓名', trigger: 'blur' }],
  legalPersonPhone: [
    { required: true, message: '请输入法人联系电话', trigger: 'blur' },
    { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号', trigger: 'blur' }
  ],
  emergencyContactName: [{ required: true, message: '请输入应急联系人姓名', trigger: 'blur' }],
  emergencyContactPhone: [
    { required: true, message: '请输入应急联系人手机号', trigger: 'blur' },
    { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号', trigger: 'blur' }
  ],
  enterpriseType: [{ required: true, message: '请选择企业类型', trigger: 'change' }],
  enterpriseTrack: [{ required: true, message: '请选择企业赛道', trigger: 'change' }],
  agreed: [{
    validator: (rule, value, callback) => {
      if (!value) callback(new Error('请阅读并同意申请承诺'))
      else callback()
    },
    trigger: 'change'
  }]
}

const onEnterpriseTypeChange = () => {
  form.enterpriseTrack = ''
}

const fetchMyList = async () => {
  try {
    const res = await getMyApplications({ page: 1, size: 50 }, { silent: true })
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
    await submitResidencyApplication({ ...form, agreed: undefined })
    ElMessage.success('入驻申请已提交')
    showForm.value = false
    form.companyName = ''
    form.businessLicenseUrl = ''
    form.legalPersonIdFront = ''
    form.legalPersonIdBack = ''
    form.emergencyContactName = ''
    form.emergencyContactPhone = ''
    form.enterpriseType = null
    form.enterpriseTrack = ''
    form.agreed = false
    form.location = ''
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
