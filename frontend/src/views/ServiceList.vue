<template>
  <div class="service-list">
    <!-- 顶部分类标签 -->
    <div class="filter-bar">
      <el-radio-group
        v-model="currentCategoryId"
        @change="onFilterChange"
        size="large"
      >
        <el-radio-button :value="null">全部</el-radio-button>
        <el-radio-button
          v-for="c in categories"
          :key="c.id"
          :value="c.id"
        >{{ c.categoryName }}</el-radio-button>
      </el-radio-group>
    </div>

    <!-- 搜索框 -->
    <div class="search-bar">
      <el-input
        v-model="keyword"
        placeholder="搜索服务名称或简介..."
        clearable
        @clear="onFilterChange"
        @keyup.enter="onFilterChange"
      >
        <template #append>
          <el-button :icon="Search" @click="onFilterChange" />
        </template>
      </el-input>
    </div>

    <!-- 服务卡片网格 -->
    <div v-loading="loading">
      <el-empty v-if="!loading && items.length === 0" description="暂无服务数据" />
      <el-row :gutter="20" v-else>
        <el-col :md="8" :sm="12" :xs="24" v-for="item in items" :key="item.id" style="margin-bottom: 20px">
          <el-card shadow="hover" class="service-card" @click="goDetail(item.id)">
            <h4 class="card-title">{{ item.serviceName }}</h4>
            <el-tag v-if="item.applicableEnterprise" size="small" type="info" class="card-tag">
              {{ item.applicableEnterprise }}
            </el-tag>
            <p class="card-summary">{{ item.summary }}</p>
          </el-card>
        </el-col>
      </el-row>
    </div>

    <!-- 分页 -->
    <div class="pagination-bar" v-if="total > 0">
      <el-pagination
        v-model:current-page="page"
        v-model:page-size="size"
        :total="total"
        :page-sizes="[6, 9, 12]"
        layout="total, sizes, prev, pager, next"
        @change="fetchItems"
        background
      />
    </div>

  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { Search } from '@element-plus/icons-vue'
import { getCategories, getServiceItems } from '@/api/service'
import { ElMessage } from 'element-plus'

const route = useRoute()
const router = useRouter()

const loading = ref(false)
const categories = ref([])
const items = ref([])
const currentCategoryId = ref(null)
const keyword = ref('')
const page = ref(1)
const size = ref(9)
const total = ref(0)

const fetchCategories = async () => {
  try {
    const res = await getCategories()
    categories.value = res.data || []
  } catch {
    ElMessage.error('加载分类失败')
  }
}

const fetchItems = async () => {
  loading.value = true
  try {
    const params = { page: page.value, size: size.value }
    if (currentCategoryId.value) params.categoryId = currentCategoryId.value
    if (keyword.value) params.keyword = keyword.value
    const res = await getServiceItems(params)
    const data = res.data
    items.value = data.records || []
    total.value = data.total || 0
  } catch {
    ElMessage.error('加载服务列表失败')
  } finally {
    loading.value = false
  }
}

const onFilterChange = () => {
  page.value = 1
  router.push({ query: { ...route.query, categoryId: currentCategoryId.value, keyword: keyword.value || undefined, page: 1 } })
  fetchItems()
}

const goDetail = (id) => {
  router.push(`/services/${id}`)
}

onMounted(() => {
  const q = route.query
  if (q.categoryId) currentCategoryId.value = Number(q.categoryId)
  if (q.keyword) keyword.value = q.keyword
  if (q.page) page.value = Number(q.page)
  fetchCategories()
  fetchItems()
})
</script>

<style scoped>
.service-list {
  max-width: 1100px;
  margin: 0 auto;
}
.filter-bar {
  margin-bottom: 20px;
}
.search-bar {
  margin-bottom: 24px;
  max-width: 400px;
}
.service-card {
  cursor: pointer;
  transition: transform 0.2s, box-shadow 0.2s;
  height: 100%;
  border-radius: var(--radius);
}
.service-card:hover {
  transform: translateY(-3px);
  box-shadow: var(--shadow-lg);
}
.card-title {
  font-size: 17px;
  font-weight: 600;
  color: var(--text-primary);
  margin: 0 0 10px 0;
}
.card-tag {
  margin-bottom: 10px;
}
.card-summary {
  font-size: 13px;
  color: var(--text-secondary);
  line-height: 1.6;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
  margin: 0;
}
.pagination-bar {
  display: flex;
  justify-content: center;
  margin-top: 28px;
}
@media (max-width: 768px) {
  .service-list { padding: 0 8px; }
}
</style>
