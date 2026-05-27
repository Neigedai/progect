<template>
  <div class="survey-fill">
    <div class="survey-header">
      <h1>{{ currentYear }}年度网易沈阳数字产业中心入驻企业满意度调查表</h1>
      <p class="survey-desc">尊敬的客户：</p>
      <p class="survey-desc survey-body">非常感谢您对网易沈阳数字产业中心的支持与信任，为了更好的为您提供创业环境及科技服务，希望能收到您的宝贵意见与真实想法，进而提升中心的服务能力，同时为您提供更舒适的办公环境。感谢您的合作！</p>
    </div>

    <div v-if="loading" class="survey-loading">加载中...</div>
    <div v-else-if="error" class="survey-error">{{ error }}</div>
    <div v-else-if="submitted" class="survey-submitted">
      <el-icon :size="64" color="#67c23a"><CircleCheckFilled /></el-icon>
      <h2>提交成功</h2>
      <p>感谢您的反馈！</p>
      <div class="thank-text">
        <p>再次感谢您的合作，如果您有任何要求或问题，欢迎随时与我们联系!</p>
        <p>联系电话：13998403064 &nbsp;&nbsp;&nbsp;&nbsp; 传真：024-83863659</p>
      </div>
    </div>

    <el-card v-else class="survey-card">
      <!-- 客户信息 -->
      <h3 class="section-title">客户信息</h3>
      <el-form :model="form" label-width="120px" class="survey-form" @submit.prevent>
        <el-row :gutter="20">
          <el-col :span="12" :xs="24">
            <el-form-item label="入驻企业名称" required>
              <el-input v-model="form.companyName" placeholder="请输入企业名称" />
            </el-form-item>
          </el-col>
          <el-col :span="12" :xs="24">
            <el-form-item label="所属领域" required>
              <el-input v-model="form.industry" placeholder="如：信息技术、生物医药" />
            </el-form-item>
          </el-col>
          <el-col :span="12" :xs="24">
            <el-form-item label="企业人数" required>
              <el-input-number v-model="form.employeeCount" :min="1" placeholder="请输入" />
            </el-form-item>
          </el-col>
          <el-col :span="12" :xs="24">
            <el-form-item label="联系人" required>
              <el-input v-model="form.contactName" placeholder="请输入联系人" />
            </el-form-item>
          </el-col>
          <el-col :span="12" :xs="24">
            <el-form-item label="联系电话" required>
              <el-input v-model="form.contactPhone" placeholder="请输入联系电话" />
            </el-form-item>
          </el-col>
        </el-row>

        <!-- 一、空间服务 -->
        <h3 class="section-title">一、空间服务</h3>
        <div class="q-item">
          <p class="q-text"><span class="required-mark">*</span>1、您对服务基地办公空间及环境的满意程度</p>
          <el-radio-group v-model="form.q1">
            <el-radio :value="10">10分</el-radio>
            <el-radio :value="8">8分</el-radio>
            <el-radio :value="6">6分</el-radio>
            <el-radio :value="4">4分</el-radio>
            <el-radio :value="2">2分</el-radio>
          </el-radio-group>
        </div>
        <div class="q-item">
          <p class="q-text"><span class="required-mark">*</span>2、您对会议室、水吧、书吧等公共服务场所的满意程度</p>
          <el-radio-group v-model="form.q2">
            <el-radio :value="10">10分</el-radio>
            <el-radio :value="8">8分</el-radio>
            <el-radio :value="6">6分</el-radio>
            <el-radio :value="4">4分</el-radio>
            <el-radio :value="2">2分</el-radio>
          </el-radio-group>
        </div>
        <div class="q-item">
          <p class="q-text"><span class="required-mark">*</span>3、您对物业管理服务人员发现问题、组织协调、解决问题的能力满意程度</p>
          <el-radio-group v-model="form.q3">
            <el-radio :value="8">8分</el-radio>
            <el-radio :value="6">6分</el-radio>
            <el-radio :value="4">4分</el-radio>
            <el-radio :value="2">2分</el-radio>
          </el-radio-group>
        </div>

        <!-- 二、活动服务 -->
        <h3 class="section-title">二、活动服务</h3>
        <div class="q-item">
          <p class="q-text"><span class="required-mark">*</span>4、对基地举办的各类活动对自身企业是否有帮助</p>
          <el-radio-group v-model="form.q4">
            <el-radio :value="10">10分</el-radio>
            <el-radio :value="8">8分</el-radio>
            <el-radio :value="6">6分</el-radio>
            <el-radio :value="4">4分</el-radio>
            <el-radio :value="2">2分</el-radio>
          </el-radio-group>
        </div>
        <div class="q-item">
          <p class="q-text"><span class="required-mark">*</span>5、为入驻企业提供人员培训、创业指导等相关活动的满意程度</p>
          <el-radio-group v-model="form.q5">
            <el-radio :value="8">8分</el-radio>
            <el-radio :value="6">6分</el-radio>
            <el-radio :value="4">4分</el-radio>
            <el-radio :value="2">2分</el-radio>
          </el-radio-group>
        </div>
        <div class="q-item">
          <p class="q-text"><span class="required-mark">*</span>6、为入驻企业提供投融资对接服务的满意程度</p>
          <el-radio-group v-model="form.q6">
            <el-radio :value="8">8分</el-radio>
            <el-radio :value="6">6分</el-radio>
            <el-radio :value="4">4分</el-radio>
            <el-radio :value="2">2分</el-radio>
          </el-radio-group>
        </div>
        <div class="q-item">
          <p class="q-text"><span class="required-mark">*</span>7、为入驻企业提供信息服务、管理咨询等服务的满意程度</p>
          <el-radio-group v-model="form.q7">
            <el-radio :value="8">8分</el-radio>
            <el-radio :value="6">6分</el-radio>
            <el-radio :value="4">4分</el-radio>
            <el-radio :value="2">2分</el-radio>
          </el-radio-group>
        </div>

        <!-- 三、服务质量 -->
        <h3 class="section-title">三、服务质量</h3>
        <div class="q-item">
          <p class="q-text"><span class="required-mark">*</span>8、您认为服务基地制定的服务计划及实施是否及时</p>
          <el-radio-group v-model="form.q8">
            <el-radio :value="8">8分</el-radio>
            <el-radio :value="6">6分</el-radio>
            <el-radio :value="4">4分</el-radio>
            <el-radio :value="2">2分</el-radio>
          </el-radio-group>
        </div>
        <div class="q-item">
          <p class="q-text"><span class="required-mark">*</span>9、您认为服务基地提供的服务工作的质量与效果是否满足需求</p>
          <el-radio-group v-model="form.q9">
            <el-radio :value="10">10分</el-radio>
            <el-radio :value="8">8分</el-radio>
            <el-radio :value="6">6分</el-radio>
            <el-radio :value="4">4分</el-radio>
            <el-radio :value="2">2分</el-radio>
          </el-radio-group>
        </div>
        <div class="q-item">
          <p class="q-text"><span class="required-mark">*</span>10、您认为服务基地服务人员在工作现场对临时和应急工作的反应能力</p>
          <el-radio-group v-model="form.q10">
            <el-radio :value="10">10分</el-radio>
            <el-radio :value="8">8分</el-radio>
            <el-radio :value="6">6分</el-radio>
            <el-radio :value="4">4分</el-radio>
            <el-radio :value="2">2分</el-radio>
          </el-radio-group>
        </div>

        <!-- 四、其它 -->
        <h3 class="section-title">四、其它</h3>
        <div class="q-item">
          <p class="q-text">11、如何能增进您对我们服务基地服务的满意度？</p>
          <el-input v-model="form.q11" type="textarea" :rows="4" placeholder="请输入您的建议" />
        </div>

        <div class="submit-bar">
          <el-button type="primary" size="large" :loading="submitting" @click="handleSubmit">提交问卷</el-button>
        </div>
      </el-form>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'
import { CircleCheckFilled } from '@element-plus/icons-vue'
import axios from 'axios'

const route = useRoute()
const currentYear = new Date().getFullYear()
const loading = ref(true)
const error = ref('')
const submitted = ref(false)
const submitting = ref(false)

const form = reactive({
  companyName: '', industry: '', employeeCount: null,
  contactName: '', contactPhone: '',
  q1: null, q2: null, q3: null, q4: null, q5: null, q6: null, q7: null, q8: null, q9: null, q10: null, q11: ''
})

onMounted(async () => {
  const code = route.params.code
  if (!code) { error.value = '链接无效'; loading.value = false; return }
  try {
    const res = await axios.get('/api/survey/' + code)
    if (res.data.code !== 200) { error.value = res.data.message || '问卷不存在'; return }
    if (res.data.data.status === 'completed') { error.value = '该问卷已提交'; return }
  } catch {
    error.value = '问卷链接无效或已失效'
  } finally {
    loading.value = false
  }
})

const handleSubmit = async () => {
  if (!form.companyName) { ElMessage.warning('请填写入驻企业名称'); return }
  if (!form.industry) { ElMessage.warning('请填写所属领域'); return }
  if (!form.employeeCount) { ElMessage.warning('请填写企业人数'); return }
  if (!form.contactName) { ElMessage.warning('请填写联系人'); return }
  if (!form.contactPhone) { ElMessage.warning('请填写联系电话'); return }
  for (let i = 1; i <= 10; i++) {
    if (!form['q' + i]) { ElMessage.warning('请完成第' + i + '题'); return }
  }
  submitting.value = true
  try {
    const res = await axios.post('/api/survey/' + route.params.code + '/submit', form)
    if (res.data.code === 200) {
      submitted.value = true
    } else {
      ElMessage.error(res.data.message)
    }
  } catch {
    ElMessage.error('提交失败')
  } finally {
    submitting.value = false
  }
}
</script>

<style scoped>
.survey-fill { max-width: 860px; margin: 0 auto; padding: 32px 20px; }
.survey-header { text-align: center; margin-bottom: 32px; }
.survey-header h1 { font-size: 22px; font-weight: 700; color: #1a1a1a; }
.survey-desc { font-size: 14px; color: #666; margin-top: 12px; line-height: 1.8; text-align: left; }
.survey-body { text-indent: 2em; margin-top: 0; }
.required-hint { font-size: 13px; font-weight: 400; color: #f56c6c; }
.required-mark { color: #f56c6c; margin-right: 2px; }
.survey-loading, .survey-error, .survey-submitted { text-align: center; padding: 80px 0; }
.survey-error { color: #f56c6c; }
.survey-submitted h2 { margin: 16px 0 8px; }
.thank-text { margin-top: 24px; padding: 20px; background: #f8fafc; border-radius: 8px; font-size: 14px; color: #666; line-height: 1.8; }
.survey-card { border-radius: var(--radius-lg); }
.section-title { font-size: 17px; font-weight: 600; color: #165DFF; margin: 32px 0 16px; padding-bottom: 8px; border-bottom: 2px solid #e8f4ff; }
.q-item { padding: 16px 0; border-bottom: 1px solid #f5f5f5; }
.q-text { font-size: 14px; color: #333; margin: 0 0 12px 0; }
.submit-bar { text-align: center; padding: 32px 0 8px; }
@media (max-width: 768px) {
  .survey-fill { padding: 16px 12px; }
  .survey-header h1 { font-size: 18px; }
  .survey-desc { font-size: 13px; }
  .survey-card :deep(.el-form-item__label) { width: auto !important; display: block; text-align: left; }
  .survey-card :deep(.el-form-item__content) { margin-left: 0 !important; }
  .section-title { font-size: 15px; margin: 24px 0 12px; }
  .q-text { font-size: 13px; }
  .survey-card :deep(.el-radio) { margin-right: 16px; margin-bottom: 6px; height: 36px; display: inline-flex; align-items: center; }
  .survey-card :deep(.el-input-number) { width: 100%; }
}
</style>
