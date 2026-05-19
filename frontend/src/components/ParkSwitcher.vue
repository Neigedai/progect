<template>
  <div class="park-switcher" v-loading="loading">
    <span class="label">选择园区：</span>
    <el-select
      v-model="selectedId"
      @change="handleSwitch"
      placeholder="请选择园区"
      size="large"
    >
      <el-option
        v-for="p in parkList"
        :key="p.id"
        :label="p.parkName"
        :value="p.id"
      />
    </el-select>
  </div>
</template>

<script setup>
import { ref, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { getParkList } from '@/api/park'
import { ElMessage } from 'element-plus'

const route = useRoute()
const router = useRouter()
const parkList = ref([])
const selectedId = ref(null)
const loading = ref(false)

const emit = defineEmits(['change'])

const fetchParks = async () => {
  loading.value = true
  try {
    const res = await getParkList()
    parkList.value = res.data || []
    if (parkList.value.length > 0) {
      const qParkId = route.query.parkId
      const exists = parkList.value.find(p => String(p.id) === String(qParkId))
      selectedId.value = exists ? Number(qParkId) : parkList.value.find(p => p.isDefault)?.id || parkList.value[0]?.id
      if (selectedId.value) {
        emit('change', selectedId.value)
      }
    }
  } catch {
    ElMessage.error('加载园区列表失败')
  } finally {
    loading.value = false
  }
}

const handleSwitch = (val) => {
  router.push({ query: { ...route.query, parkId: val } })
  emit('change', val)
}

watch(() => route.query.parkId, (newVal) => {
  if (newVal && parkList.value.find(p => String(p.id) === String(newVal))) {
    selectedId.value = Number(newVal)
    emit('change', selectedId.value)
  }
})

fetchParks()
</script>

<style scoped>
.park-switcher {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 16px 0;
}
.label {
  font-size: 14px;
  color: #606266;
  white-space: nowrap;
}
</style>
