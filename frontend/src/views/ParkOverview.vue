<template>
  <div class="park-overview" v-loading="loading">
    <!-- P0: 园区切换器 -->
    <ParkSwitcher @change="onParkChange" />

    <template v-if="parkInfo">
      <!-- 园区基础信息 -->
      <div class="park-hero">
        <div class="park-hero-content">
          <h2 class="park-name">{{ parkInfo.parkName }}</h2>
          <p class="park-address" v-if="parkInfo.address">
            <el-icon><Location /></el-icon> {{ parkInfo.address }}
          </p>
          <p class="park-desc" v-if="parkInfo.description">{{ parkInfo.description }}</p>
        </div>
      </div>

      <!-- P1: 荣誉资质轮播 -->
      <HonorCarousel :list="honors" :loading="loading" />

      <!-- P1: 区位交通 -->
      <div class="section transport-section" v-if="parkInfo.transportInfo">
        <h3 class="section-title">区位交通</h3>
        <div class="transport-content">
          <p>{{ parkInfo.transportInfo }}</p>
        </div>
      </div>

      <!-- P1: 园区规划图文 -->
      <div class="section plan-section" v-if="parkInfo.planImage || parkInfo.planDescription">
        <h3 class="section-title">园区规划</h3>
        <div class="plan-content">
          <el-image
            v-if="parkInfo.planImage"
            :src="parkInfo.planImage"
            fit="contain"
            class="plan-image"
            lazy
          >
            <template #error>
              <div class="image-placeholder">
                <el-icon :size="48"><PictureFilled /></el-icon>
              </div>
            </template>
          </el-image>
          <p class="plan-desc" v-if="parkInfo.planDescription">{{ parkInfo.planDescription }}</p>
        </div>
      </div>

      <!-- P0: 配套设施卡片列表 -->
      <FacilityList :list="facilities" :loading="loading" />
    </template>

    <!-- 空数据保护 -->
    <el-empty v-if="!loading && !parkInfo" description="暂无园区数据" />
  </div>
</template>

<script setup>
import { ref, watch, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { getParkList, getParkOverview } from '@/api/park'
import { ElMessage } from 'element-plus'
import { Location, PictureFilled } from '@element-plus/icons-vue'
import ParkSwitcher from '@/components/ParkSwitcher.vue'
import FacilityList from '@/components/FacilityList.vue'
import HonorCarousel from '@/components/HonorCarousel.vue'

const route = useRoute()
const router = useRouter()
const loading = ref(false)
const parkInfo = ref(null)
const facilities = ref([])
const honors = ref([])

const fetchOverview = async (parkId) => {
  if (!parkId) return
  loading.value = true
  try {
    const res = await getParkOverview(parkId)
    if (res.data) {
      parkInfo.value = res.data.parkInfo
      facilities.value = res.data.facilities || []
      honors.value = res.data.honors || []
    }
  } catch {
    parkInfo.value = null
    ElMessage.error('加载园区概况失败')
  } finally {
    loading.value = false
  }
}

const onParkChange = (parkId) => {
  fetchOverview(parkId)
}

onMounted(async () => {
  // 获取轮播图

  const qParkId = route.query.parkId
  if (qParkId) {
    fetchOverview(qParkId)
  } else {
    // 无参数时获取默认园区
    try {
      const res = await getParkList()
      const parks = res.data || []
      if (parks.length > 0) {
        const def = parks.find(p => p.isDefault) || parks[0]
        router.push({ query: { parkId: def.id } })
        fetchOverview(def.id)
      }
    } catch {
      // 静默处理
    }
  }
})

watch(() => route.query.parkId, (newVal, oldVal) => {
  if (newVal && newVal !== oldVal) {
    fetchOverview(newVal)
  }
})
</script>

<style scoped>
.park-overview {
  max-width: 1200px;
  margin: 0 auto;
}
.park-hero {
  background: linear-gradient(135deg, #0F172A 0%, #1E3A5F 40%, #165DFF 100%);
  border-radius: var(--radius-xl);
  padding: 56px 48px;
  margin-bottom: 40px;
  color: #fff;
  box-shadow: var(--shadow-xl);
  position: relative;
  overflow: hidden;
}
.park-hero::after {
  content: '';
  position: absolute;
  right: -40px;
  top: -40px;
  width: 200px;
  height: 200px;
  border-radius: 50%;
  background: rgba(255, 255, 255, 0.03);
}
.park-name {
  font-size: 32px;
  font-weight: 700;
  margin: 0 0 16px 0;
  font-family: 'Poppins', sans-serif;
  letter-spacing: -0.02em;
  color: #fff;
}
.park-address {
  font-size: 14px;
  margin: 0 0 10px 0;
  opacity: 0.8;
  display: flex;
  align-items: center;
  gap: 6px;
}
.park-desc {
  font-size: 15px;
  line-height: 1.8;
  margin: 20px 0 0 0;
  opacity: 0.85;
  max-width: 700px;
}
.section {
  margin-bottom: 44px;
}
.section-title {
  font-size: 18px;
  font-weight: 600;
  color: var(--text-primary);
  margin-bottom: 20px;
  padding-left: 12px;
  border-left: 4px solid var(--primary);
}
.transport-content {
  background: #fff;
  border-radius: var(--radius-lg);
  padding: 24px;
  box-shadow: var(--shadow-sm);
  border: 1px solid var(--border);
}
.transport-content p {
  font-size: 14px;
  line-height: 1.8;
  color: var(--text-secondary);
  margin: 0;
}
.plan-content {
  display: flex;
  flex-direction: column;
  gap: 20px;
}
.plan-image {
  max-width: 100%;
  max-height: 400px;
  border-radius: var(--radius-lg);
  box-shadow: var(--shadow-sm);
}
.plan-desc {
  font-size: 14px;
  line-height: 1.8;
  color: var(--text-secondary);
  background: #fff;
  border-radius: var(--radius-lg);
  padding: 24px;
  box-shadow: var(--shadow-sm);
  border: 1px solid var(--border);
  margin: 0;
}
.image-placeholder {
  display: flex;
  align-items: center;
  justify-content: center;
  height: 200px;
  background: var(--bg-page);
  border-radius: var(--radius);
  color: var(--text-placeholder);
}
@media (max-width: 768px) {
  .park-hero { padding: 32px 24px; }
  .park-name { font-size: 24px; }
}
</style>
