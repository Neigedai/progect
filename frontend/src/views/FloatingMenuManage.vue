<template>
  <div class="admin-page">
    <div class="page-header">
      <h2>悬浮菜单管理</h2>
      <el-button type="primary" @click="openDialog()">新增菜单项</el-button>
    </div>

    <el-card class="search-card">
      <el-form inline>
        <el-form-item label="标题">
          <el-input v-model="queryTitle" placeholder="输入标题搜索" clearable style="width:220px" @keyup.enter="fetchList" />
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="queryStatus" placeholder="全部" clearable style="width:180px" @change="fetchList">
            <el-option :value="1" label="启用" />
            <el-option :value="0" label="禁用" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="fetchList">搜索</el-button>
          <el-button @click="queryTitle = ''; queryStatus = ''; fetchList()">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <el-card>
      <el-table v-loading="loading" :data="tableData" stripe empty-text="暂无菜单项">
        <el-table-column prop="id" label="ID" width="60" />
        <el-table-column label="图片预览" width="120">
          <template #default="{ row }">
            <el-image :src="row.imageUrl" style="width: 60px; height: 60px; border-radius: 4px" fit="cover">
              <template #error>无图</template>
            </el-image>
          </template>
        </el-table-column>
        <el-table-column label="图标" width="80">
          <template #default="{ row }">
            <img v-if="row.icon" :src="row.icon" style="width: 28px; height: 28px; object-fit: contain" />
            <span v-else style="color: #ccc">-</span>
          </template>
        </el-table-column>
        <el-table-column prop="title" label="按钮标题" min-width="100" />
        <el-table-column prop="imageUrl" label="图片URL" min-width="200" show-overflow-tooltip />
        <el-table-column prop="sortOrder" label="排序" width="70" />
        <el-table-column label="状态" width="80">
          <template #default="{ row }">
            <el-switch :model-value="row.status === 1" @change="handleStatusChange(row)" />
          </template>
        </el-table-column>
        <el-table-column label="操作" width="180" fixed="right">
          <template #default="{ row }">
            <el-button size="small" @click="openDialog(row)">编辑</el-button>
            <el-popconfirm title="确定删除该菜单项？" @confirm="handleDelete(row.id)">
              <template #reference>
                <el-button size="small" type="danger">删除</el-button>
              </template>
            </el-popconfirm>
          </template>
        </el-table-column>
      </el-table>
      <div class="pagination" v-if="false" />
    </el-card>

    <el-dialog v-model="dialogVisible" :title="isEdit ? '编辑菜单项' : '新增菜单项'" width="480px" destroy-on-close>
      <el-form ref="formRef" :model="form" :rules="rules" label-width="100px">
        <el-form-item label="按钮标题" prop="title">
          <el-input v-model="form.title" placeholder="如：服务指南" />
        </el-form-item>
        <el-form-item label="图标" prop="icon">
          <el-input v-model="form.icon" placeholder="输入SVG/图标路径或上传" />
          <UploadBtn @uploaded="url => form.icon = url" />
        </el-form-item>
        <el-form-item label="弹出图片" prop="imageUrl">
          <el-input v-model="form.imageUrl" placeholder="输入图片路径或上传" />
          <UploadBtn @uploaded="url => form.imageUrl = url" />
        </el-form-item>
        <el-form-item label="排序" prop="sortOrder">
          <el-input-number v-model="form.sortOrder" :min="0" />
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-switch v-model="form.status" :active-value="1" :inactive-value="0" />
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
import {
  getFloatingMenuItems,
  getFloatingMenuItem,
  createFloatingMenuItem,
  updateFloatingMenuItem,
  deleteFloatingMenuItem,
  updateFloatingMenuItemStatus
} from '@/api/admin'
import UploadBtn from '@/components/UploadBtn.vue'

const loading = ref(false)
const queryTitle = ref('')
const queryStatus = ref(null)
const allData = ref([])
const submitting = ref(false)
const tableData = ref([])
const dialogVisible = ref(false)
const isEdit = ref(false)
const formRef = ref(null)
const editId = ref(null)

const form = reactive({
  title: '', icon: '', imageUrl: '', sortOrder: 0, status: 1
})

const rules = {
  title: [{ required: true, message: '请输入按钮标题', trigger: 'blur' }],
  imageUrl: [{ required: true, message: '请输入图片URL', trigger: 'blur' }]
}

const fetchList = async () => {
  loading.value = true
  try {
    const res = await getFloatingMenuItems()
    allData.value = res.data || []
    applyFilter()
  } finally {
    loading.value = false
  }
}

const applyFilter = () => {
  let filtered = allData.value
  if (queryTitle.value) {
    filtered = filtered.filter(i => i.title?.includes(queryTitle.value))
  }
  if (queryStatus.value !== null && queryStatus.value !== '') {
    filtered = filtered.filter(i => i.status === queryStatus.value)
  }
  tableData.value = filtered
}

const openDialog = async (row) => {
  Object.assign(form, { title: '', icon: '', imageUrl: '', sortOrder: 0, status: 1 })
  if (row) {
    isEdit.value = true
    editId.value = row.id
    const res = await getFloatingMenuItem(row.id)
    const data = res.data
    Object.assign(form, {
      title: data.title,
      icon: data.icon || '',
      imageUrl: data.imageUrl,
      sortOrder: data.sortOrder,
      status: data.status
    })
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
    const payload = {
      title: form.title,
      icon: form.icon,
      imageUrl: form.imageUrl,
      sortOrder: form.sortOrder,
      status: form.status
    }
    if (isEdit.value) {
      await updateFloatingMenuItem(editId.value, payload)
    } else {
      await createFloatingMenuItem(payload)
    }
    ElMessage.success(isEdit.value ? '更新成功' : '新增成功')
    dialogVisible.value = false
    fetchList()
  } finally {
    submitting.value = false
  }
}

const handleDelete = async (id) => {
  await deleteFloatingMenuItem(id)
  ElMessage.success('删除成功')
  fetchList()
}

const handleStatusChange = async (row) => {
  const newStatus = row.status === 1 ? 0 : 1
  await updateFloatingMenuItemStatus(row.id, newStatus)
  row.status = newStatus
  ElMessage.success('状态更新成功')
}

fetchList()
</script>

<style scoped>
.admin-page { padding-bottom: 24px; }
.admin-page .el-card { border-radius: var(--radius-lg); }
</style>
