<template>
  <div class="home">
    <div class="banner-section" v-if="banners.length > 0">
      <el-carousel :interval="5000" height="360px" arrow="hover">
        <el-carousel-item v-for="b in banners" :key="b.id">
          <a v-if="b.linkUrl" :href="b.linkUrl" class="banner-link">
            <el-image :src="b.imageUrl" fit="cover" class="banner-image" />
          </a>
          <el-image v-else :src="b.imageUrl" fit="cover" class="banner-image" />
        </el-carousel-item>
      </el-carousel>
    </div>

    <div class="welcome-section">
      <h1 class="welcome-title">欢迎使用园区服务运营系统</h1>
      <p class="welcome-desc">一站式园区服务管理平台，助力园区数字化转型</p>

      <el-row :gutter="20" class="quick-links">
        <el-col :md="8" :sm="12" :xs="24">
          <el-card shadow="hover" class="quick-card" @click="$router.push('/park-overview')">
            <div class="card-icon" style="background:#E8F3FF"><el-icon :size="28" color="#165DFF"><OfficeBuilding /></el-icon></div>
            <h3>园区概况</h3>
            <p>查看园区基础信息、配套设施及荣誉资质</p>
          </el-card>
        </el-col>
        <el-col :md="8" :sm="12" :xs="24">
          <el-card shadow="hover" class="quick-card" @click="$router.push('/services')">
            <div class="card-icon" style="background:#E8FFEA"><el-icon :size="28" color="#00B42A"><Grid /></el-icon></div>
            <h3>企业服务大厅</h3>
            <p>浏览政策申报、财税代办等企业服务</p>
          </el-card>
        </el-col>
        <el-col :md="8" :sm="12" :xs="24">
          <el-card shadow="hover" class="quick-card" @click="$router.push('/articles')">
            <div class="card-icon" style="background:#FFF7E8"><el-icon :size="28" color="#FF7D00"><Document /></el-icon></div>
            <h3>资讯与政策</h3>
            <p>获取园区最新动态及政策通知</p>
          </el-card>
        </el-col>
      </el-row>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { getActiveBanners } from '@/api/public'
import { OfficeBuilding, Grid, Document } from '@element-plus/icons-vue'

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
  border-radius: var(--radius-lg);
  overflow: hidden;
  margin-bottom: 32px;
  box-shadow: var(--shadow-lg);
}
.banner-link { display: block; }
.banner-image { width: 100%; height: 360px; }
.welcome-section { text-align: center; }
.welcome-title {
  font-size: 28px;
  font-weight: 700;
  color: var(--text-primary);
  margin: 0 0 8px;
}
.welcome-desc {
  font-size: 15px;
  color: var(--text-secondary);
  margin: 0 0 36px;
}
.quick-links { max-width: 900px; margin: 0 auto; }
.quick-card {
  cursor: pointer;
  text-align: center;
  padding: 8px;
  border-radius: var(--radius-lg);
  transition: transform 0.2s, box-shadow 0.2s;
}
.quick-card:hover {
  transform: translateY(-4px);
  box-shadow: var(--shadow-lg);
}
.quick-card :deep(.el-card__body) { padding: 28px 20px; }
.card-icon {
  width: 56px;
  height: 56px;
  border-radius: 14px;
  display: flex;
  align-items: center;
  justify-content: center;
  margin: 0 auto 16px;
}
.quick-card h3 { font-size: 17px; margin: 0 0 8px; color: var(--text-primary); }
.quick-card p { font-size: 13px; color: var(--text-secondary); margin: 0; line-height: 1.5; }
@media (max-width: 768px) {
  .banner-section { border-radius: 0; }
  .banner-image { height: 200px; }
  .welcome-title { font-size: 22px; }
  .quick-links { padding: 0 8px; }
}
</style>
