<template>
  <div class="admin-page">
    <div class="page-header">
      <h2>资讯管理</h2>
      <el-button type="primary" @click="openDialog()">新增文章</el-button>
    </div>

    <el-card class="search-card">
      <el-form :model="query" inline>
        <el-form-item label="标题">
          <el-input v-model="query.title" placeholder="输入标题搜索" clearable @keyup.enter="fetchList" />
        </el-form-item>
        <el-form-item label="类型">
          <el-select v-model="query.type" placeholder="全部" clearable>
            <el-option value="news" label="园区动态" />
            <el-option value="policy" label="政策文章" />
          </el-select>
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="query.status" placeholder="全部" clearable>
            <el-option :value="1" label="已发布" />
            <el-option :value="0" label="草稿" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="fetchList">搜索</el-button>
          <el-button @click="resetQuery">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <el-card>
      <el-table v-loading="loading" :data="tableData" stripe>
        <el-table-column prop="id" label="ID" width="60" />
        <el-table-column prop="title" label="标题" min-width="200" show-overflow-tooltip />
        <el-table-column label="类型" width="90">
          <template #default="{ row }">
            <el-tag :type="row.type === 'policy' ? 'warning' : 'info'" size="small">
              {{ row.type === 'policy' ? '政策' : '动态' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="author" label="作者" width="100" />
        <el-table-column prop="publishTime" label="发布时间" width="160" />
        <el-table-column label="状态" width="100">
          <template #default="{ row }">
            <el-switch :model-value="row.status === 1" @change="handleStatusChange(row)" />
            <span style="margin-left:4px;font-size:12px;color:#909399">{{ row.status === 1 ? '已发布' : '草稿' }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="viewCount" label="浏览" width="70" />
        <el-table-column label="操作" width="180" fixed="right">
          <template #default="{ row }">
            <el-button size="small" @click="openDialog(row)">编辑</el-button>
            <el-popconfirm title="确定删除该文章？" @confirm="handleDelete(row.id)">
              <template #reference>
                <el-button size="small" type="danger">删除</el-button>
              </template>
            </el-popconfirm>
          </template>
        </el-table-column>
      </el-table>

      <el-pagination
        v-model:current-page="query.page"
        v-model:page-size="query.size"
        :total="total"
        layout="total, sizes, prev, pager, next"
        :page-sizes="[10, 20, 50]"
        @change="fetchList"
        class="pagination"
      />
    </el-card>

    <el-dialog v-model="dialogVisible" :title="isEdit ? '编辑文章' : '新增文章'" width="720px" destroy-on-close>
      <el-form ref="formRef" :model="form" :rules="rules" label-width="100px">
        <el-form-item label="标题" prop="title">
          <el-input v-model="form.title" />
        </el-form-item>
        <el-form-item label="类型" prop="type">
          <el-radio-group v-model="form.type">
            <el-radio value="news">园区动态</el-radio>
            <el-radio value="policy">政策文章</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="作者" prop="author">
          <el-input v-model="form.author" />
        </el-form-item>
        <el-form-item label="来源" prop="source">
          <el-input v-model="form.source" />
        </el-form-item>
        <el-form-item label="封面图" prop="coverImage">
          <el-input v-model="form.coverImage" placeholder="输入图片路径或上传" />
          <upload-btn @uploaded="url => form.coverImage = url" />
        </el-form-item>
        <el-form-item label="摘要" prop="summary">
          <el-input v-model="form.summary" type="textarea" :rows="2" />
        </el-form-item>
        <el-form-item label="正文" prop="content">
          <el-input v-model="form.content" type="textarea" :rows="10"
            placeholder="支持 Markdown 格式（P1 替换为富文本编辑器）" />
        </el-form-item>
        <el-form-item label="排序" prop="sortOrder">
          <el-input-number v-model="form.sortOrder" :min="0" />
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-switch v-model="form.status" :active-value="1" :inactive-value="0" active-text="发布" inactive-text="草稿" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="submitting" @click="handleSubmit">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { ElMessage } from 'element-plus'
import { getArticleList, getArticleDetail, createArticle, updateArticle, deleteArticle, updateArticleStatus } from '@/api/admin'
import UploadBtn from '@/components/UploadBtn.vue'

const loading = ref(false)
const submitting = ref(false)
const tableData = ref([])
const total = ref(0)
const dialogVisible = ref(false)
const isEdit = ref(false)
const formRef = ref(null)
const editId = ref(null)

const query = reactive({ page: 1, size: 10, title: '', type: '', status: null })

const form = reactive({
  title: '', type: 'news', author: '', source: '', coverImage: '',
  summary: '', content: '', sortOrder: 0, status: 0
})

const rules = {
  title: [{ required: true, message: '请输入标题', trigger: 'blur' }],
  type: [{ required: true, message: '请选择类型', trigger: 'change' }]
}

const fetchList = async () => {
  loading.value = true
  try {
    const res = await getArticleList(query)
    tableData.value = res.data.records
    total.value = res.data.total
  } finally {
    loading.value = false
  }
}

const resetQuery = () => {
  query.title = ''
  query.type = ''
  query.status = null
  query.page = 1
  fetchList()
}

const openDialog = async (row) => {
  Object.assign(form, {
    title: '', type: 'news', author: '', source: '', coverImage: '',
    summary: '', content: '', sortOrder: 0, status: 0
  })
  if (row) {
    isEdit.value = true
    editId.value = row.id
    const res = await getArticleDetail(row.id)
    Object.assign(form, res.data)
  } else {
    isEdit.value = false
    editId.value = null
  }
  dialogVisible.value = true
}

const handleSubmit = async () => {
  try {
    await formRef.value.validate()
  } catch {
    return
  }
  submitting.value = true
  try {
    if (isEdit.value) {
      await updateArticle(editId.value, form)
    } else {
      await createArticle(form)
    }
    ElMessage.success(isEdit.value ? '更新成功' : '新增成功')
    dialogVisible.value = false
    fetchList()
  } finally {
    submitting.value = false
  }
}

const handleDelete = async (id) => {
  await deleteArticle(id)
  ElMessage.success('删除成功')
  fetchList()
}

const handleStatusChange = async (row) => {
  const newStatus = row.status === 1 ? 0 : 1
  await updateArticleStatus(row.id, newStatus)
  row.status = newStatus
  ElMessage.success('状态更新成功')
}

fetchList()
</script>

<style scoped>
</style>
