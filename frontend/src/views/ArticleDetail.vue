<template>
  <div class="article-detail" v-loading="loading">
    <template v-if="article">
      <div class="article-header">
        <el-tag :type="article.type === 'policy' ? 'warning' : ''" size="small">
          {{ article.type === 'policy' ? '政策文章' : '园区动态' }}
        </el-tag>
        <h1 class="title">{{ article.title }}</h1>
        <div class="meta">
          <span>{{ article.author }}</span>
          <span v-if="article.source">来源: {{ article.source }}</span>
          <span>{{ article.publishTime?.substring(0, 10) }}</span>
          <span>浏览 {{ article.viewCount }}</span>
        </div>
      </div>
      <el-image v-if="article.coverImage" :src="article.coverImage" class="cover" fit="cover" />
      <video v-if="article.videoUrl" :src="article.videoUrl" class="video" controls />
      <div class="content" v-html="renderedContent"></div>
    </template>
    <el-empty v-if="!loading && !article" description="文章不存在或已下架" />
    <div class="back-link">
      <el-button text @click="$router.push('/articles')">&larr; 返回列表</el-button>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import { getArticleDetail } from '@/api/public'

const route = useRoute()
const loading = ref(false)
const article = ref(null)

const renderedContent = computed(() => {
  if (!article.value?.content) return ''
  // Simple Markdown-like rendering
  let html = article.value.content
  html = html.replace(/### (.+)/g, '<h4>$1</h4>')
  html = html.replace(/## (.+)/g, '<h3>$1</h3>')
  html = html.replace(/\*\*(.+?)\*\*/g, '<strong>$1</strong>')
  html = html.replace(/\n- (.+)/g, '\n<li>$1</li>')
  html = html.replace(/(<li>.*<\/li>)/s, '<ul>$1</ul>')
  html = html.replace(/\n/g, '<br>')
  return html
})

onMounted(async () => {
  loading.value = true
  try {
    const res = await getArticleDetail(route.params.id)
    article.value = res.data
  } finally { loading.value = false }
})
</script>

<style scoped>
.article-detail { max-width: 800px; margin: 0 auto; }
.article-header { margin-bottom: 28px; }
.title { font-size: 28px; font-weight: 700; color: var(--text-primary); margin: 14px 0; line-height: 1.4; }
.meta { font-size: 13px; color: var(--text-secondary); display: flex; gap: 16px; flex-wrap: wrap; }
.cover { width: 100%; max-height: 400px; border-radius: var(--radius); margin-bottom: 28px; }
.video { width: 100%; max-height: 500px; border-radius: var(--radius); margin-bottom: 28px; background: #000; }
.content { font-size: 15px; line-height: 1.9; color: var(--text-primary); }
.content :deep(h3) { font-size: 18px; margin: 24px 0 12px; }
.content :deep(h4) { font-size: 16px; margin: 20px 0 10px; }
.content :deep(strong) { color: var(--text-primary); }
.content :deep(li) { margin-bottom: 6px; }
.back-link { margin-top: 48px; padding-top: 20px; border-top: 1px solid var(--border); }
@media (max-width: 768px) {
  .title { font-size: 22px; }
}
</style>
