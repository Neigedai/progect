<template>
  <div class="admin-page">
    <div class="page-header">
      <h2>消息管理</h2>
      <el-button type="primary" @click="openDialog()">发布公告</el-button>
    </div>

    <el-card class="search-card">
      <el-form inline>
        <el-form-item label="类型">
          <el-select v-model="queryType" placeholder="全部" clearable style="width:180px" @change="fetchList">
            <el-option value="system" label="系统公告" />
            <el-option value="business" label="业务通知" />
          </el-select>
        </el-form-item>
        <el-form-item label="标题">
          <el-input v-model="queryKeyword" placeholder="输入标题关键字" clearable style="width:220px" @keyup.enter="fetchList" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="fetchList">搜索</el-button>
          <el-button @click="queryType = ''; queryKeyword = ''; fetchList()">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <el-card>
      <el-table v-loading="loading" :data="filteredData" stripe empty-text="暂无消息">
        <el-table-column prop="id" label="ID" width="60" />
        <el-table-column label="类型" width="100">
          <template #default="{ row }">
            <el-tag :type="row.type === 'system' ? 'warning' : 'primary'" size="small">
              {{ row.type === 'system' ? '系统公告' : '业务通知' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="title" label="标题" min-width="180" show-overflow-tooltip />
        <el-table-column prop="content" label="内容" min-width="220" show-overflow-tooltip />
        <el-table-column label="接收用户" width="100">
          <template #default="{ row }">
            <span v-if="row.userId === 0">全员</span>
            <span v-else>{{ row.userId }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" width="160" />
        <el-table-column label="操作" width="100" fixed="right">
          <template #default="{ row }">
            <el-popconfirm title="确定删除该消息？" @confirm="handleDelete(row.id)">
              <template #reference>
                <el-button size="small" type="danger">删除</el-button>
              </template>
            </el-popconfirm>
          </template>
        </el-table-column>
      </el-table>
      <el-pagination
        v-model:current-page="page"
        v-model:page-size="size"
        :total="total"
        layout="total, sizes, prev, pager, next"
        :page-sizes="[10, 20, 50]"
        @change="fetchList"
        class="pagination"
      />
    </el-card>

    <el-dialog v-model="dialogVisible" title="发布公告" width="600px" destroy-on-close>
      <el-form ref="formRef" :model="form" :rules="rules" label-width="100px">
        <el-form-item label="类型" prop="type">
          <el-radio-group v-model="form.type">
            <el-radio value="system">系统公告</el-radio>
            <el-radio value="business">业务通知</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="标题" prop="title">
          <el-input v-model="form.title" placeholder="请输入消息标题" />
        </el-form-item>
        <el-form-item label="内容" prop="content">
          <el-input v-model="form.content" type="textarea" :rows="4" placeholder="请输入消息内容" />
        </el-form-item>
        <el-form-item label="发送方式" prop="sendType">
          <el-radio-group v-model="form.sendType">
            <el-radio value="all">全员发送</el-radio>
            <el-radio value="user">指定用户</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item v-if="form.sendType === 'user'" label="选择用户" prop="userId">
          <el-select
            v-model="form.userId"
            filterable
            remote
            :remote-method="searchUsers"
            :loading="userSearchLoading"
            placeholder="输入用户名/昵称搜索"
            style="width: 100%"
          >
            <el-option v-for="u in userOptions" :key="u.id" :label="u.nickname ? `${u.nickname}（${u.username}）` : u.username" :value="u.id" />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="submitting" @click="handleSubmit">发布</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, computed } from 'vue'
import { ElMessage } from 'element-plus'
import { getNotificationList, createNotification, deleteNotification } from '@/api/notification'
import request from '@/utils/request'

const loading = ref(false)
const submitting = ref(false)
const tableData = ref([])
const total = ref(0)
const page = ref(1)
const size = ref(10)
const queryType = ref('')
const queryKeyword = ref('')
const dialogVisible = ref(false)
const formRef = ref(null)

const userSearchLoading = ref(false)
const userOptions = ref([])

const filteredData = computed(() => {
  if (!queryKeyword.value) return tableData.value
  const kw = queryKeyword.value.toLowerCase()
  return tableData.value.filter(r => r.title?.toLowerCase().includes(kw) || r.content?.toLowerCase().includes(kw))
})

const form = reactive({
  type: 'system',
  title: '',
  content: '',
  sendType: 'all',
  userId: null
})

const rules = {
  type: [{ required: true, message: '请选择类型', trigger: 'change' }],
  title: [{ required: true, message: '请输入标题', trigger: 'blur' }],
  content: [{ required: true, message: '请输入内容', trigger: 'blur' }],
  sendType: [{ required: true, message: '请选择发送方式', trigger: 'change' }]
}

const searchUsers = async (query) => {
  if (!query) { userOptions.value = []; return }
  userSearchLoading.value = true
  try {
    const res = await request.get('/admin/users', { params: { keyword: query, page: 1, size: 20 } })
    userOptions.value = res.data?.records || res.data || []
  } catch {
    userOptions.value = []
  } finally {
    userSearchLoading.value = false
  }
}

const fetchList = async () => {
  loading.value = true
  try {
    const res = await getNotificationList({
      page: page.value,
      size: size.value,
      type: queryType.value || undefined
    })
    tableData.value = res.data?.records || []
    total.value = res.data?.total || 0
  } finally {
    loading.value = false
  }
}

const openDialog = () => {
  Object.assign(form, { type: 'system', title: '', content: '', sendType: 'all', userId: null })
  userOptions.value = []
  dialogVisible.value = true
}

const handleSubmit = async () => {
  try { await formRef.value.validate() } catch { return }
  if (form.sendType === 'user' && !form.userId) {
    ElMessage.warning('请选择用户')
    return
  }
  submitting.value = true
  try {
    await createNotification({
      type: form.type,
      title: form.title,
      content: form.content,
      userId: form.sendType === 'all' ? 0 : form.userId
    })
    ElMessage.success('发布成功')
    dialogVisible.value = false
    fetchList()
  } finally {
    submitting.value = false
  }
}

const handleDelete = async (id) => {
  await deleteNotification(id)
  ElMessage.success('删除成功')
  fetchList()
}

fetchList()
</script>

<style scoped>
.admin-page { padding-bottom: 24px; }
.admin-page .el-card { border-radius: var(--radius-lg); }
</style>
