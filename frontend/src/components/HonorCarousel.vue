<template>
  <div class="honor-carousel" v-if="!loading && list.length > 0">
    <h3 class="section-title">荣誉资质</h3>
    <el-carousel :interval="4000" type="card" height="200px" arrow="hover">
      <el-carousel-item v-for="h in list" :key="h.id">
        <div class="honor-card">
          <el-image
            v-if="h.image"
            :src="h.image"
            fit="contain"
            class="honor-image"
            lazy
          >
            <template #error>
              <div class="image-placeholder">
                <el-icon :size="40"><PictureFilled /></el-icon>
              </div>
            </template>
          </el-image>
          <div class="honor-info">
            <span class="honor-name">{{ h.honorName }}</span>
            <span class="honor-year" v-if="h.awardYear">{{ h.awardYear }}</span>
          </div>
        </div>
      </el-carousel-item>
    </el-carousel>
  </div>
  <!-- P1 placeholder: 荣誉资质待配置时显示 -->
  <el-empty v-else-if="!loading" description="暂无荣誉资质数据" style="padding: 40px 0" />
</template>

<script setup>
import { PictureFilled } from '@element-plus/icons-vue'

defineProps({
  list: { type: Array, default: () => [] },
  loading: { type: Boolean, default: false }
})
</script>

<style scoped>
.honor-card {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  height: 100%;
  padding: 20px;
  background: var(--bg-page);
  border-radius: var(--radius);
}
.honor-image {
  width: 100px;
  height: 100px;
  margin-bottom: 12px;
}
.image-placeholder {
  width: 100px;
  height: 100px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: var(--border-light);
  border-radius: var(--radius);
  color: var(--text-placeholder);
  margin-bottom: 12px;
}
.honor-info {
  text-align: center;
}
.honor-name {
  display: block;
  font-size: 16px;
  font-weight: 600;
  color: var(--text-primary);
  margin-bottom: 4px;
}
.honor-year {
  font-size: 12px;
  color: var(--text-secondary);
}
</style>
