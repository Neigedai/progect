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
      <el-form ref="formRef" :model="form" :rules="rules" label-width="120px" style="max-width:640px">
        <el-form-item label="联系人" prop="contactName">
          <el-input v-model="form.contactName" placeholder="请输入联系人姓名" />
        </el-form-item>
        <el-form-item label="联系电话" prop="contactPhone">
          <el-input v-model="form.contactPhone" placeholder="请输入联系电话" />
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
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { submitResidencyApplication } from '@/api/residency'

const router = useRouter()
const formRef = ref(null)
const submitting = ref(false)

const form = reactive({
  contactName: '',
  contactPhone: '',
  area: '',
  industryType: '',
  expectedEntryDate: '',
  additionalInfo: ''
})

const rules = {
  contactName: [{ required: true, message: '请输入联系人', trigger: 'blur' }],
  contactPhone: [
    { required: true, message: '请输入联系电话', trigger: 'blur' },
    { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号', trigger: 'blur' }
  ],
  area: [{ required: true, message: '请输入面积需求', trigger: 'blur' }],
  industryType: [{ required: true, message: '请选择行业类型', trigger: 'change' }]
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
    router.push('/park-overview')
  } catch {
    ElMessage.error('提交失败，请稍后重试')
  } finally {
    submitting.value = false
  }
}
</script>

<style scoped>
.apply-residency {
  max-width: 800px;
  margin: 0 auto;
}
.form-card {
  border-radius: var(--radius-lg);
}
.card-header {
  display: flex;
  align-items: center;
}
@media (max-width: 768px) {
  .apply-residency { padding: 0 8px; }
}
</style>
