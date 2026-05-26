<template>
  <div class="enterprise-auth-page">
    <el-card v-if="authData && authData.authStatus === 'approved'" class="status-card">
      <div class="status-success">
        <el-icon :size="48" color="#67c23a"><CircleCheckFilled /></el-icon>
        <h2>企业认证已通过</h2>
        <p>企业名称：{{ authData.companyName }}</p>
      </div>
    </el-card>

    <el-card v-else-if="authData && authData.authStatus === 'pending'" class="status-card">
      <div class="status-pending">
        <el-icon :size="48" color="#e6a23c"><Clock /></el-icon>
        <h2>认证审核中</h2>
        <p>企业名称：{{ authData.companyName }}</p>
        <p>统一社会信用代码：{{ authData.creditCode }}</p>
        <p style="color: #999; font-size: 13px; margin-top: 8px">提交时间：{{ authData.authSubmittedAt }}</p>
      </div>
    </el-card>

    <el-card v-else-if="authData && authData.authStatus === 'rejected'" class="status-card">
      <div class="status-rejected">
        <el-icon :size="48" color="#f56c6c"><CircleCloseFilled /></el-icon>
        <h2>认证已驳回</h2>
        <p>驳回原因：{{ authData.reviewComment || '无' }}</p>
        <el-button type="primary" style="margin-top: 16px" @click="authData = null">重新提交</el-button>
      </div>
    </el-card>

    <template v-else>
      <el-card v-if="approvedResidency && !imported" class="import-card">
        <div class="import-notice">
          <el-icon :size="24" color="#165DFF"><InfoFilled /></el-icon>
          <span>检测到您有已通过的入驻申请（法人：{{ approvedResidency.legalPersonName }}），可快捷带入基本信息</span>
        </div>
        <el-button type="primary" @click="doImport">导入信息</el-button>
      </el-card>

      <el-card>
        <template #header>
          <h2 style="margin: 0">企业认证</h2>
        </template>

        <el-steps :active="step" align-center style="margin-bottom: 32px">
          <el-step title="基本信息" />
          <el-step title="资质上传" />
          <el-step title="提交审核" />
        </el-steps>

        <!-- Step 1: 基本信息 -->
        <div v-show="step === 0" style="max-width: 560px; margin: 0 auto">
          <el-form ref="basicFormRef" :model="form" :rules="basicRules" label-width="140px">
            <el-form-item label="企业名称" prop="companyName">
              <el-input v-model="form.companyName" placeholder="请输入企业全称" />
            </el-form-item>
            <el-form-item label="统一社会信用代码" prop="creditCode">
              <el-input v-model="form.creditCode" placeholder="18位统一社会信用代码" maxlength="18" />
            </el-form-item>
            <el-form-item label="联系人手机号" prop="contactPhone">
              <el-input v-model="form.contactPhone" placeholder="请输入手机号" :disabled="imported" />
              <span v-if="imported" style="color:#999;font-size:12px">已从入驻申请导入</span>
            </el-form-item>
            <div style="text-align:center">
              <el-button type="primary" @click="nextStep">下一步</el-button>
            </div>
          </el-form>
        </div>

        <!-- Step 2: 资质上传 -->
        <div v-show="step === 1" style="max-width: 560px; margin: 0 auto">
          <el-form label-width="140px">
            <el-form-item label="营业执照扫描件" required>
              <UploadBtn v-model="form.licenseUrl" text="上传营业执照" />
              <div style="color: #999; font-size: 12px; margin-top: 4px">支持 JPG/PNG，最大 5MB</div>
            </el-form-item>
            <div v-if="form.licenseUrl" style="text-align:center;margin-bottom:18px">
              <el-image :src="form.licenseUrl" style="width:200px;height:130px;border-radius:4px;border:1px solid #eee" fit="contain" :preview-src-list="[form.licenseUrl]" preview-teleported />
            </div>
            <el-form-item label="法人身份证" required>
              <template v-if="!imported">
                <UploadBtn v-model="form.legalPersonIdUrl" text="上传法人身份证" />
                <div style="color: #999; font-size: 12px; margin-top: 4px">支持 JPG/PNG，最大 5MB</div>
              </template>
              <span v-else style="color:#999;font-size:12px">已从入驻申请导入</span>
            </el-form-item>
            <div v-if="form.legalPersonIdUrl" style="text-align:center;margin-bottom:18px">
              <el-image :src="form.legalPersonIdUrl" style="width:200px;height:130px;border-radius:4px;border:1px solid #eee" fit="contain" :preview-src-list="[form.legalPersonIdUrl]" preview-teleported />
            </div>
            <div style="text-align:center">
              <el-button @click="step--">上一步</el-button>
              <el-button type="primary" @click="nextStep">下一步</el-button>
            </div>
          </el-form>
        </div>

        <!-- Step 3: 确认提交 -->
        <div v-show="step === 2" style="max-width: 560px; margin: 0 auto">
          <el-descriptions :column="1" border>
            <el-descriptions-item label="企业名称">{{ form.companyName }}</el-descriptions-item>
            <el-descriptions-item label="统一社会信用代码">{{ form.creditCode }}</el-descriptions-item>
            <el-descriptions-item label="联系人手机号">
              {{ form.contactPhone || '未填写' }}
              <el-tag v-if="imported" size="small" type="info" style="margin-left:8px">已导入</el-tag>
            </el-descriptions-item>
            <el-descriptions-item label="营业执照">
              <el-image v-if="form.licenseUrl" :src="form.licenseUrl" style="width:120px;height:80px" fit="contain" />
              <span v-else style="color:#999">未上传</span>
            </el-descriptions-item>
            <el-descriptions-item label="法人身份证">
              <el-image v-if="form.legalPersonIdUrl" :src="form.legalPersonIdUrl" style="width:120px;height:80px" fit="contain" />
              <span v-else style="color:#999">未上传</span>
            </el-descriptions-item>
          </el-descriptions>
          <div style="margin-top: 24px; text-align: center">
            <el-button @click="step--">上一步</el-button>
            <el-button type="primary" :loading="submitting" @click="handleSubmit">提交认证</el-button>
          </div>
        </div>
      </el-card>
    </template>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { CircleCheckFilled, CircleCloseFilled, Clock } from '@element-plus/icons-vue'
import { submitEnterpriseAuth, getEnterpriseAuthStatus } from '@/api/enterprise'
import { getMyApplications } from '@/api/residency'
import { useAuth } from '@/utils/auth'
import UploadBtn from '@/components/UploadBtn.vue'

const step = ref(0)
const submitting = ref(false)
const authData = ref(null)
const basicFormRef = ref(null)
const approvedResidency = ref(null)
const imported = ref(false)

const form = reactive({
  companyName: '',
  creditCode: '',
  contactPhone: '',
  licenseUrl: '',
  legalPersonIdUrl: '',
  legalFaceVerified: false
})

const basicRules = {
  companyName: [{ required: true, message: '请输入企业名称', trigger: 'blur' }],
  creditCode: [
    { required: true, message: '请输入统一社会信用代码', trigger: 'blur' },
    { pattern: /^[0-9A-Z]{18}$/, message: '请输入正确的18位信用代码', trigger: 'blur' }
  ],
  contactPhone: [
    { pattern: /^1[3-9]\d{9}$/, message: '手机号格式不正确', trigger: 'blur' }
  ]
}

const nextStep = async () => {
  if (step.value === 0) {
    try { await basicFormRef.value.validate() } catch { return }
  }
  if (step.value === 1 && !form.licenseUrl) {
    ElMessage.warning('请上传营业执照')
    return
  }
  step.value++
}

const doImport = () => {
  const r = approvedResidency.value
  if (!r) return
  form.contactPhone = r.legalPersonPhone || ''
  form.legalPersonIdUrl = r.legalPersonIdFront || ''
  imported.value = true
}

const handleSubmit = async () => {
  submitting.value = true
  try {
    await submitEnterpriseAuth({ ...form, legalFaceVerified: false })
    ElMessage.success('认证申请已提交')
    authData.value = { ...form, authStatus: 'pending', authSubmittedAt: new Date().toLocaleString() }
  } catch {
    ElMessage.error('提交失败')
  } finally {
    submitting.value = false
  }
}

onMounted(async () => {
  try {
    const auth = useAuth()
    await auth.loadAuth()
    if (auth.state.user?.phone) form.contactPhone = auth.state.user.phone
  } catch {}
  try {
    const res = await getEnterpriseAuthStatus()
    if (res.data) authData.value = res.data
  } catch {}
  if (!authData.value) {
    try {
      const res = await getMyApplications({ page: 1, size: 50 })
      const approved = (res.data.records || []).find(r => r.status === 'approved')
      if (approved) approvedResidency.value = approved
    } catch {}
  }
})
</script>

<style scoped>
.enterprise-auth-page {
  max-width: 800px;
  margin: 0 auto;
}
.status-card {
  border-radius: var(--radius-lg);
}
.status-success, .status-pending, .status-rejected {
  text-align: center;
  padding: 32px 0;
}
.status-success h2, .status-pending h2, .status-rejected h2 {
  margin: 16px 0 8px;
}
.import-card {
  border-radius: var(--radius-lg);
  border-color: var(--primary);
  background: var(--primary-light);
  margin-bottom: 16px;
}
.import-notice {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 14px;
  color: var(--text-primary);
  margin-bottom: 12px;
}
</style>
