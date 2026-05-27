<template>
  <div class="home">
    <div class="banner-section" v-if="banners.length > 0">
      <el-carousel :interval="5000" height="400px" arrow="hover">
        <el-carousel-item v-for="b in banners" :key="b.id">
          <a v-if="b.linkUrl" :href="b.linkUrl" class="banner-link">
            <el-image :src="b.imageUrl" fit="cover" class="banner-image" />
          </a>
          <el-image v-else :src="b.imageUrl" fit="cover" class="banner-image" />
        </el-carousel-item>
      </el-carousel>
    </div>

    <div class="welcome-section">
      <h1 class="welcome-title">米库产业创新公共服务平台</h1>
      <p class="welcome-desc">一站式园区服务管理平台，助力园区数字化转型</p>
    </div>

    <div class="stats-row">
      <div class="stat-item">
        <div class="stat-value">4</div>
        <div class="stat-label">核心服务</div>
      </div>
      <div class="stat-divider" />
      <div class="stat-item">
        <div class="stat-value">10+</div>
        <div class="stat-label">园区企业</div>
      </div>
      <div class="stat-divider" />
      <div class="stat-item">
        <div class="stat-value">24h</div>
        <div class="stat-label">在线服务</div>
      </div>
    </div>

    <div class="quick-links">
      <div class="quick-card" @click="$router.push('/park-overview')">
        <div class="card-icon" style="background: linear-gradient(135deg, #E8F2FF, #D0E4FF)">
          <el-icon :size="32" color="#165DFF"><OfficeBuilding /></el-icon>
        </div>
        <div class="card-body">
          <h3>园区概况</h3>
          <p>查看园区基础信息、配套设施及荣誉资质</p>
        </div>
      </div>
      <div class="quick-card" @click="$router.push('/services')">
        <div class="card-icon" style="background: linear-gradient(135deg, #E8FFED, #C8FAD6)">
          <el-icon :size="32" color="#16A34A"><Grid /></el-icon>
        </div>
        <div class="card-body">
          <h3>企业服务大厅</h3>
          <p>浏览政策申报、财税代办等企业服务</p>
        </div>
      </div>
      <div class="quick-card" @click="$router.push('/apply/residency')">
        <div class="card-icon" style="background: linear-gradient(135deg, #EDEEFF, #D9DBFF)">
          <el-icon :size="32" color="#6366F1"><EditPen /></el-icon>
        </div>
        <div class="card-body">
          <h3>在线入驻申请</h3>
          <p>提交企业入驻意向，快速获取园区审批反馈</p>
        </div>
      </div>
      <div class="quick-card" @click="$router.push('/articles')">
        <div class="card-icon" style="background: linear-gradient(135deg, #FFF7E8, #FFECD0)">
          <el-icon :size="32" color="#F59E0B"><Document /></el-icon>
        </div>
        <div class="card-body">
          <h3>资讯与政策</h3>
          <p>获取园区最新动态及政策通知</p>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { getActiveBanners } from '@/api/public'
import { OfficeBuilding, Grid, EditPen, Document } from '@element-plus/icons-vue'

const banners = ref([])

onMounted(async () => {
  try {
    const res = await getActiveBanners()
    banners.value = res.data || []
  } catch {
    // silent
  }
})
</script>

<style scoped>
.home { max-width: 1200px; margin: 0 auto; }
.banner-section {
  border-radius: var(--radius-xl);
  overflow: hidden;
  margin-bottom: 48px;
  box-shadow: var(--shadow-xl);
}
.banner-link { display: block; }
.banner-image { width: 100%; height: 400px; }
.welcome-section { text-align: center; padding: 0 20px; margin-bottom: 36px; }
.welcome-title {
  font-size: 32px;
  font-weight: 700;
  color: var(--text-primary);
  margin: 0 0 12px;
  font-family: 'Poppins', sans-serif;
  letter-spacing: -0.02em;
}
.welcome-desc {
  font-size: 16px;
  color: var(--text-secondary);
  margin: 0;
}

.stats-row {
  display: flex;
  justify-content: center;
  align-items: center;
  gap: 0;
  margin-bottom: 48px;
  background: #fff;
  border-radius: var(--radius-xl);
  padding: 28px 0;
  box-shadow: var(--shadow-sm);
  border: 1px solid var(--border);
}
.stat-item {
  flex: 1;
  text-align: center;
  padding: 0 32px;
}
.stat-value {
  font-size: 28px;
  font-weight: 700;
  color: var(--primary);
  font-family: 'Poppins', sans-serif;
}
.stat-label {
  font-size: 13px;
  color: var(--text-secondary);
  margin-top: 4px;
}
.stat-divider {
  width: 1px;
  height: 36px;
  background: var(--border);
}

.quick-links {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 20px;
  max-width: 1000px;
  margin: 0 auto;
}
.quick-card {
  background: #fff;
  border-radius: var(--radius-xl);
  border: 1px solid var(--border);
  padding: 32px 24px 28px;
  cursor: pointer;
  transition: all var(--transition);
  box-shadow: var(--shadow-sm);
  text-align: center;
}
.quick-card:hover {
  transform: translateY(-4px);
  box-shadow: var(--shadow-lg);
  border-color: rgba(22, 93, 255, 0.2);
}
.card-icon {
  width: 64px;
  height: 64px;
  border-radius: 18px;
  display: flex;
  align-items: center;
  justify-content: center;
  margin: 0 auto 18px;
}
.quick-card h3 {
  font-size: 17px;
  margin: 0 0 8px;
  color: var(--text-primary);
  font-weight: 600;
}
.quick-card p {
  font-size: 13px;
  color: var(--text-secondary);
  margin: 0;
  line-height: 1.6;
}

@media (max-width: 768px) {
  .banner-section { border-radius: 0; margin-bottom: 32px; }
  .banner-image { height: 200px; }
  .welcome-title { font-size: 22px; }
  .quick-links { grid-template-columns: repeat(2, 1fr); gap: 12px; }
  .quick-card { padding: 24px 16px; }
  .stats-row { border-radius: var(--radius-lg); padding: 20px 0; }
  .stat-value { font-size: 22px; }
}
@media (max-width: 480px) {
  .quick-links { grid-template-columns: 1fr; }
}
</style>
