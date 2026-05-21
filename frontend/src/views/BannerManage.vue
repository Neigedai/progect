<template>
  <div class="admin-page">
    <div class="page-header">
      <h2>轮播图管理</h2>
      <el-button type="primary" @click="openDialog()">新增轮播图</el-button>
    </div>

    <el-card class="search-card">
      <el-form :model="query" inline>
        <el-form-item label="标题">
          <el-input v-model="query.title" placeholder="输入标题搜索" clearable style="width:220px" @keyup.enter="fetchList" />
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="query.status" placeholder="全部" clearable style="width:180px">
            <el-option :value="1" label="启用" />
            <el-option :value="0" label="禁用" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="fetchList">搜索</el-button>
          <el-button @click="resetQuery">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <el-card>
      <el-table v-loading="loading" :data="tableData" stripe empty-text="暂无轮播图数据">
        <el-table-column prop="id" label="ID" width="60" />
        <el-table-column label="缩略图" width="120">
          <template #default="{ row }">
            <el-image :src="row.imageUrl" style="width: 80px; height: 45px; border-radius: 4px" fit="cover" />
          </template>
        </el-table-column>
        <el-table-column prop="title" label="标题" min-width="140" />
        <el-table-column prop="sortOrder" label="排序" width="70" />
        <el-table-column label="状态" width="80">
          <template #default="{ row }">
            <el-switch :model-value="row.status === 1" @change="handleStatusChange(row)" />
          </template>
        </el-table-column>
        <el-table-column prop="beginTime" label="生效开始" width="160" />
        <el-table-column prop="endTime" label="生效结束" width="160" />
        <el-table-column label="操作" width="180" fixed="right">
          <template #default="{ row }">
            <el-button size="small" @click="openDialog(row)">编辑</el-button>
            <el-popconfirm title="确定删除该轮播图？" @confirm="handleDelete(row.id)">
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

    <el-dialog v-model="dialogVisible" :title="isEdit ? '编辑轮播图' : '新增轮播图'" width="600px" destroy-on-close>
      <el-form ref="formRef" :model="form" :rules="rules" label-width="100px">
        <el-form-item label="标题" prop="title">
          <el-input v-model="form.title" />
        </el-form-item>
        <el-form-item label="图片" prop="imageUrl">
          <el-input v-model="form.imageUrl" placeholder="输入图片路径或上传" />
          <upload-btn @uploaded="url => form.imageUrl = url" />
        </el-form-item>
        <el-form-item label="排序" prop="sortOrder">
          <el-input-number v-model="form.sortOrder" :min="0" />
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-switch v-model="form.status" :active-value="1" :inactive-value="0" />
        </el-form-item>
        <el-form-item label="生效时间范围" prop="timeRange">
          <el-date-picker
            v-model="form.timeRange"
            type="datetimerange"
            range-separator="至"
            start-placeholder="开始时间"
            end-placeholder="结束时间"
            value-format="YYYY-MM-DD HH:mm:ss"
            style="width: 100%"
          />
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
import { getBannerList, getBannerDetail, createBanner, updateBanner, deleteBanner, updateBannerStatus } from '@/api/admin'
import UploadBtn from '@/components/UploadBtn.vue'

const loading = ref(false)
const submitting = ref(false)
const tableData = ref([])
const total = ref(0)
const dialogVisible = ref(false)
const isEdit = ref(false)
const formRef = ref(null)
const editId = ref(null)

const query = reactive({ page: 1, size: 10, title: '', status: null })

const form = reactive({
  title: '', imageUrl: '', sortOrder: 0, status: 1, timeRange: null
})

const rules = {
  title: [{ required: true, message: '请输入标题', trigger: 'blur' }],
  imageUrl: [{ required: true, message: '请输入图片URL', trigger: 'blur' }]
}

const fetchList = async () => {
  loading.value = true
  try {
    const res = await getBannerList(query)
    tableData.value = res.data.records
    total.value = res.data.total
  } finally {
    loading.value = false
  }
}

const resetQuery = () => {
  query.title = ''
  query.status = null
  query.page = 1
  fetchList()
}

const openDialog = async (row) => {
  Object.assign(form, {
    title: '', imageUrl: '', sortOrder: 0, status: 1, timeRange: null
  })
  if (row) {
    isEdit.value = true
    editId.value = row.id
    const res = await getBannerDetail(row.id)
    const data = res.data
    Object.assign(form, {
      title: data.title,
      imageUrl: data.imageUrl,
      linkUrl: data.linkUrl,
      sortOrder: data.sortOrder,
      status: data.status,
      timeRange: data.beginTime && data.endTime ? [data.beginTime, data.endTime] : null
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
      imageUrl: form.imageUrl,
      sortOrder: form.sortOrder,
      status: form.status,
      beginTime: form.timeRange ? form.timeRange[0] : null,
      endTime: form.timeRange ? form.timeRange[1] : null
    }
    if (isEdit.value) {
      await updateBanner(editId.value, payload)
    } else {
      await createBanner(payload)
    }
    ElMessage.success(isEdit.value ? '更新成功' : '新增成功')
    dialogVisible.value = false
    fetchList()
  } finally {
    submitting.value = false
  }
}

const handleDelete = async (id) => {
  await deleteBanner(id)
  ElMessage.success('删除成功')
  fetchList()
}

const handleStatusChange = async (row) => {
  const newStatus = row.status === 1 ? 0 : 1
  await updateBannerStatus(row.id, newStatus)
  row.status = newStatus
  ElMessage.success('状态更新成功')
}

fetchList()
</script>

<style scoped>
.admin-page { padding-bottom: 24px; }
.admin-page .el-card { border-radius: var(--radius-lg); }
</style>
