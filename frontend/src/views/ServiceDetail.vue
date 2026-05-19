<template>
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
        <el-button type="primary" size="large" @click="goApply">立即办理</el-button>
      </div>
    </template>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { getServiceDetail } from '@/api/service'
import { ElMessage } from 'element-plus'

const route = useRoute()
const router = useRouter()
const loading = ref(false)
const detail = ref(null)

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

const goApply = () => {
  router.push(`/apply/${route.params.id}`)
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
