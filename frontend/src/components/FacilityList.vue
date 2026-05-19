<template>
  <div class="facility-list">
    <h3 class="section-title">配套设施</h3>
    <el-empty v-if="!loading && list.length === 0" description="暂无配套设施数据" />
    <el-row :gutter="20" v-loading="loading">
      <el-col :md="8" :sm="12" :xs="24" v-for="f in list" :key="f.id" style="margin-bottom: 20px">
        <el-card shadow="hover" :body-style="{ padding: '20px' }">
          <div class="facility-card">
            <el-image
              v-if="f.image"
              :src="f.image"
              fit="cover"
              class="facility-image"
              lazy
            >
              <template #error>
                <div class="image-placeholder">
                  <el-icon :size="40"><PictureFilled /></el-icon>
                </div>
              </template>
            </el-image>
            <img v-else-if="f.icon" :src="f.icon" class="facility-icon" />
            <div class="facility-info">
              <span class="facility-name">{{ f.facilityName }}</span>
              <span class="facility-desc">{{ f.description }}</span>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { PictureFilled } from '@element-plus/icons-vue'

defineProps({
  list: { type: Array, default: () => [] },
  loading: { type: Boolean, default: false }
})
</script>

<style scoped>
.facility-card {
  display: flex;
  align-items: flex-start;
  gap: 16px;
}
.facility-image {
  width: 120px;
  height: 90px;
  border-radius: var(--radius);
  flex-shrink: 0;
}
.image-placeholder {
  width: 120px;
  height: 90px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: var(--bg-page);
  border-radius: var(--radius);
  color: var(--text-placeholder);
}
.facility-icon {
  width: 40px;
  height: 40px;
  flex-shrink: 0;
}
.facility-info {
  display: flex;
  flex-direction: column;
  gap: 8px;
  min-width: 0;
}
.facility-name {
  font-size: 16px;
  font-weight: 600;
  color: var(--text-primary);
}
.facility-desc {
  font-size: 13px;
  color: var(--text-secondary);
  line-height: 1.5;
}
</style>
