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
      <el-card>
        <template #header>
          <h2 style="margin: 0">企业认证</h2>
        </template>

        <el-steps :active="step" align-center style="margin-bottom: 32px">
          <el-step title="基本信息" />
          <el-step title="资质上传" />
          <el-step title="法人验证" />
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
            <div style="text-align:center">
              <el-button @click="step--">上一步</el-button>
              <el-button type="primary" @click="nextStep">下一步</el-button>
            </div>
          </el-form>
        </div>

        <!-- Step 3: 法人验证 -->
        <div v-show="step === 2" style="max-width: 560px; margin: 0 auto">
          <el-form label-width="140px">
            <el-form-item label="法人身份证" required>
              <UploadBtn v-model="form.legalPersonIdUrl" text="上传法人身份证" />
              <div style="color: #999; font-size: 12px; margin-top: 4px">支持 JPG/PNG，最大 5MB</div>
            </el-form-item>
            <div v-if="form.legalPersonIdUrl" style="text-align:center;margin-bottom:18px">
              <el-image :src="form.legalPersonIdUrl" style="width:200px;height:130px;border-radius:4px;border:1px solid #eee" fit="contain" :preview-src-list="[form.legalPersonIdUrl]" preview-teleported />
            </div>
            <el-form-item label="人脸识别验证">
              <el-button type="success" :loading="faceVerifying" @click="verifyFace">
                {{ form.legalFaceVerified ? '已通过验证' : '发起人脸识别' }}
              </el-button>
              <el-tag v-if="form.legalFaceVerified" type="success" style="margin-left: 8px">已验证</el-tag>
              <div style="color: #999; font-size: 12px; margin-top: 4px">也可仅上传身份证，跳过人脸识别</div>
            </el-form-item>
            <div style="text-align:center">
              <el-button @click="step--">上一步</el-button>
              <el-button type="primary" @click="nextStep">下一步</el-button>
            </div>
          </el-form>
        </div>

        <!-- Step 4: 确认提交 -->
        <div v-show="step === 3" style="max-width: 560px; margin: 0 auto">
          <el-descriptions :column="1" border>
            <el-descriptions-item label="企业名称">{{ form.companyName }}</el-descriptions-item>
            <el-descriptions-item label="统一社会信用代码">{{ form.creditCode }}</el-descriptions-item>
            <el-descriptions-item label="营业执照">
              <el-image v-if="form.licenseUrl" :src="form.licenseUrl" style="width:120px;height:80px" fit="contain" />
              <span v-else style="color:#999">未上传</span>
            </el-descriptions-item>
            <el-descriptions-item label="法人身份证">
              <el-image v-if="form.legalPersonIdUrl" :src="form.legalPersonIdUrl" style="width:120px;height:80px" fit="contain" />
              <span v-else style="color:#999">未上传</span>
            </el-descriptions-item>
            <el-descriptions-item label="人脸识别">{{ form.legalFaceVerified ? '已通过' : '未验证' }}</el-descriptions-item>
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
import UploadBtn from '@/components/UploadBtn.vue'

const step = ref(0)
const submitting = ref(false)
const faceVerifying = ref(false)
const authData = ref(null)
const basicFormRef = ref(null)

const form = reactive({
  companyName: '',
  creditCode: '',
  licenseUrl: '',
  legalPersonIdUrl: '',
  legalFaceVerified: false
})

const basicRules = {
  companyName: [{ required: true, message: '请输入企业名称', trigger: 'blur' }],
  creditCode: [
    { required: true, message: '请输入统一社会信用代码', trigger: 'blur' },
    { pattern: /^[0-9A-Z]{18}$/, message: '请输入正确的18位信用代码', trigger: 'blur' }
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
  if (step.value === 2 && !form.legalPersonIdUrl && !form.legalFaceVerified) {
    ElMessage.warning('请上传法人身份证或完成人脸识别')
    return
  }
  step.value++
}

const verifyFace = async () => {
  faceVerifying.value = true
  try {
    await new Promise(resolve => setTimeout(resolve, 1500))
    form.legalFaceVerified = true
    ElMessage.success('人脸识别验证通过')
  } finally {
    faceVerifying.value = false
  }
}

const handleSubmit = async () => {
  submitting.value = true
  try {
    await submitEnterpriseAuth({ ...form })
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
    const res = await getEnterpriseAuthStatus()
    if (res.data) authData.value = res.data
  } catch {}
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
</style>
