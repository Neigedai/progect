<template>
  <div>
  <div class="service-detail" v-loading="loading">
    <!-- 面包屑 -->
    <el-breadcrumb separator="/" class="breadcrumb">
      <el-breadcrumb-item :to="{ path: '/services' }">企业服务大厅</el-breadcrumb-item>
      <el-breadcrumb-item>{{ detail ? detail.serviceName : '详情' }}</el-breadcrumb-item>
    </el-breadcrumb>

    <el-empty v-if="!loading && !detail" description="服务不存在" />

    <template v-if="detail">
      <!-- 基础信息 -->
      <div class="info-section">
        <h2 class="service-title">{{ detail.serviceName }}</h2>
        <div class="meta-row" v-if="detail.applicableEnterprise || detail.categoryName">
          <el-tag v-if="detail.categoryName" type="primary">{{ detail.categoryName }}</el-tag>
          <span v-if="detail.applicableEnterprise" class="applicable">
            适用企业：{{ detail.applicableEnterprise }}
          </span>
        </div>
        <p class="detail-desc" v-if="detail.detailDesc">{{ detail.detailDesc }}</p>
      </div>

      <!-- 流程步骤 -->
      <div class="steps-section" v-if="detail.steps && detail.steps.length > 0">
        <h3 class="section-title">办理流程</h3>
        <el-steps :active="null" direction="vertical" align-center>
          <el-step
            v-for="(s, i) in detail.steps"
            :key="i"
            :title="'第' + (i + 1) + '步：' + s.title"
            :description="s.desc"
          />
        </el-steps>
      </div>

      <!-- 费用说明 -->
      <div class="price-section" v-if="detail.priceInfo">
        <h3 class="section-title">费用说明</h3>
        <el-descriptions :column="1" border>
          <el-descriptions-item v-if="detail.priceInfo.price" label="服务费用">
            {{ detail.priceInfo.price }}
          </el-descriptions-item>
          <el-descriptions-item v-if="detail.priceInfo.subsidy" label="政府补贴">
            {{ detail.priceInfo.subsidy }}
          </el-descriptions-item>
          <el-descriptions-item v-if="detail.priceInfo.note" label="备注">
            {{ detail.priceInfo.note }}
          </el-descriptions-item>
        </el-descriptions>
      </div>

      <!-- P1 占位: 服务商资质信息 -->
      <div class="provider-section" v-if="false">
        <h3 class="section-title">服务商信息</h3>
        <!-- TODO P1: 关联 service_provider 表展示服务商资质、联系方式 -->
      </div>

      <!-- 底部立即办理按钮 -->
      <div class="action-bar">
        <el-button type="primary" size="large" @click="showApplyDialog">立即办理</el-button>
      </div>
    </template>
  </div>

  <el-dialog v-model="applyDialogVisible" title="立即办理" width="400px" align-center destroy-on-close>
    <div style="text-align:center">
      <img v-if="applyQrUrl" :src="applyQrUrl" alt="办理二维码" style="max-width:220px;border-radius:8px;margin-bottom:24px" />
      <p v-if="!applyQrUrl" style="color:#999;margin-bottom:24px">暂无办理入口</p>
    </div>
    <el-form ref="applyFormRef" :model="applyForm" :rules="applyRules" label-width="90px" style="margin-top:8px">
      <el-form-item label="联系人" prop="contactName">
        <el-input v-model="applyForm.contactName" placeholder="请输入联系人姓名" />
      </el-form-item>
      <el-form-item label="联系电话" prop="contactPhone">
        <el-input v-model="applyForm.contactPhone" placeholder="请输入联系电话" />
      </el-form-item>
    </el-form>
    <template #footer>
      <el-button @click="applyDialogVisible = false">取消</el-button>
      <el-button type="primary" :loading="applySubmitting" @click="handleApplySubmit">提交申请</el-button>
    </template>
  </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { getServiceDetail, getFloatingMenuItems, submitServiceApplication } from '@/api/service'
import { ElMessage } from 'element-plus'
import { useAuth } from '@/utils/auth'

const route = useRoute()
const router = useRouter()
const { state: auth } = useAuth()
const loading = ref(false)
const detail = ref(null)
const applyDialogVisible = ref(false)
const applyQrUrl = ref('')
const applySubmitting = ref(false)
const applyFormRef = ref(null)

const applyForm = reactive({
  contactName: '',
  contactPhone: ''
})

const applyRules = {
  contactName: [{ required: true, message: '请输入联系人', trigger: 'blur' }],
  contactPhone: [
    { required: true, message: '请输入联系电话', trigger: 'blur' },
    { pattern: /^1[3-9]\d{9}$/, message: '手机号格式不正确', trigger: 'blur' }
  ]
}

const fetchDetail = async () => {
  const id = route.params.id
  if (!id) return
  loading.value = true
  try {
    const res = await getServiceDetail(id)
    detail.value = res.data
  } catch {
    detail.value = null
    ElMessage.error('加载服务详情失败')
  } finally {
    loading.value = false
  }
}

const showApplyDialog = async () => {
  applyForm.contactName = auth.user?.nickname || auth.user?.username || ''
  applyForm.contactPhone = auth.user?.phone || ''
  try {
    const res = await getFloatingMenuItems()
    const items = res.data || []
    const applyItem = items.find(i => i.title === '立即办理')
    if (applyItem?.imageUrl) {
      applyQrUrl.value = applyItem.imageUrl
    } else {
      applyQrUrl.value = ''
    }
    applyDialogVisible.value = true
  } catch {
    applyQrUrl.value = ''
    applyDialogVisible.value = true
  }
}

const handleApplySubmit = async () => {
  try {
    await applyFormRef.value.validate()
  } catch {
    return
  }
  if (!detail.value) return
  applySubmitting.value = true
  try {
    await submitServiceApplication({
      serviceId: detail.value.id,
      contactName: applyForm.contactName,
      contactPhone: applyForm.contactPhone
    })
    ElMessage.success('申请已提交')
    applyDialogVisible.value = false
  } catch {
    // error handled by interceptor
  } finally {
    applySubmitting.value = false
  }
}

onMounted(fetchDetail)
</script>

<style scoped>
.service-detail {
  max-width: 860px;
  margin: 0 auto;
}
.breadcrumb {
  margin-bottom: 24px;
}
.service-title {
  font-size: 26px;
  font-weight: 700;
  color: var(--text-primary);
  margin: 0 0 16px 0;
}
.meta-row {
  display: flex;
  align-items: center;
  gap: 16px;
  margin-bottom: 24px;
}
.applicable {
  font-size: 13px;
  color: var(--text-regular);
}
.detail-desc {
  font-size: 15px;
  line-height: 1.8;
  color: var(--text-primary);
  margin: 0 0 32px 0;
}
.section-title {
  font-size: 18px;
  font-weight: 600;
  color: var(--text-primary);
  margin: 32px 0 16px 0;
  padding-left: 12px;
  border-left: 4px solid var(--primary);
}
.price-section {
  margin-bottom: 32px;
}
.action-bar {
  position: sticky;
  bottom: 0;
  background: #fff;
  padding: 20px 0;
  margin-top: 40px;
  border-top: 1px solid var(--border);
  text-align: center;
}
@media (max-width: 768px) {
  .service-detail { padding: 0 8px; }
  .service-title { font-size: 22px; }
}
</style>
