<template>
  <div class="article-list" v-loading="loading">
    <h2 class="page-title">资讯与政策</h2>

    <div class="filter-bar">
      <el-radio-group v-model="query.type" @change="fetchList">
        <el-radio-button value="">全部</el-radio-button>
        <el-radio-button value="news">园区动态</el-radio-button>
        <el-radio-button value="policy">政策文章</el-radio-button>
      </el-radio-group>
      <el-input v-model="query.keyword" placeholder="搜索标题..." clearable style="width: 240px; margin-left: 16px"
        @keyup.enter="fetchList" />
    </div>

    <el-card v-for="a in tableData" :key="a.id" class="article-card" shadow="hover" @click="goDetail(a.id)">
      <div class="article-card-body">
        <el-image v-if="a.coverImage" :src="a.coverImage" class="cover" fit="cover" />
        <div class="article-info">
          <h3 class="article-title">
            <el-tag :type="a.type === 'policy' ? 'warning' : 'info'" size="small" class="type-tag">
              {{ a.type === 'policy' ? '政策' : '动态' }}
            </el-tag>
            {{ a.title }}
          </h3>
          <p class="article-summary" v-if="a.summary">{{ a.summary }}</p>
          <div class="article-meta">
            <span>{{ a.author }}</span>
            <span v-if="a.source">来源: {{ a.source }}</span>
            <span>{{ a.publishTime?.substring(0, 10) }}</span>
          </div>
        </div>
      </div>
    </el-card>

    <el-empty v-if="!loading && tableData.length === 0" description="暂无文章" />

    <el-pagination
      v-model:current-page="query.page"
      v-model:page-size="query.size"
      :total="total"
      layout="total, prev, pager, next"
      :page-sizes="[10, 20]"
      @change="fetchList"
      class="pagination"
    />
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { getArticleList } from '@/api/public'

const router = useRouter()
const loading = ref(false)
const tableData = ref([])
const total = ref(0)
const query = reactive({ page: 1, size: 10, type: '', keyword: '' })

const fetchList = async () => {
  loading.value = true
  try {
    const res = await getArticleList(query)
    tableData.value = res.data.records
    total.value = res.data.total
  } finally { loading.value = false }
}

const goDetail = (id) => { router.push(`/articles/${id}`) }

fetchList()
</script>

<style scoped>
.article-list { max-width: 900px; margin: 0 auto; }
.page-title {
  font-size: 22px;
  font-weight: 700;
  color: var(--text-primary);
  margin-bottom: 20px;
}
.filter-bar { display: flex; align-items: center; margin-bottom: 20px; flex-wrap: wrap; gap: 12px; }
.article-card {
  margin-bottom: 16px;
  cursor: pointer;
  border-radius: var(--radius);
  transition: transform 0.2s, box-shadow 0.2s;
}
.article-card:hover { transform: translateY(-2px); box-shadow: var(--shadow-lg); }
.article-card-body { display: flex; gap: 20px; }
.cover { width: 200px; height: 130px; border-radius: var(--radius); flex-shrink: 0; }
.article-info { flex: 1; min-width: 0; }
.article-title {
  font-size: 17px;
  font-weight: 600;
  color: var(--text-primary);
  margin: 0 0 10px;
  display: flex;
  align-items: center;
  gap: 8px;
}
.type-tag { flex-shrink: 0; }
.article-summary { color: var(--text-regular); font-size: 14px; line-height: 1.6; margin-bottom: 10px; }
.article-meta { font-size: 12px; color: var(--text-secondary); display: flex; gap: 16px; }
.pagination { margin-top: 20px; display: flex; justify-content: flex-end; }
@media (max-width: 768px) {
  .article-card-body { flex-direction: column; }
  .cover { width: 100%; height: 180px; }
  .filter-bar { flex-direction: column; align-items: flex-start; }
}
</style>
