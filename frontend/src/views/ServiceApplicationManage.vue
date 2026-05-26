<template>
  <div class="admin-page">
    <div class="page-header">
      <h2>服务申请管理</h2>
    </div>

    <el-card class="search-card">
      <el-form inline>
        <el-form-item label="联系人">
          <el-input v-model="queryContact" placeholder="输入联系人" clearable style="width:160px" @keyup.enter="fetchList" />
        </el-form-item>
        <el-form-item label="服务名称">
          <el-input v-model="queryServiceName" placeholder="输入服务名称" clearable style="width:180px" @keyup.enter="fetchList" />
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="queryStatus" placeholder="全部" clearable style="width:140px" @change="fetchList">
            <el-option value="pending" label="待处理" />
            <el-option value="contacted" label="已联系" />
            <el-option value="closed" label="已结束" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="fetchList">搜索</el-button>
          <el-button @click="queryContact = ''; queryServiceName = ''; queryStatus = ''; fetchList()">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <el-card>
      <el-table v-loading="loading" :data="tableData" stripe empty-text="暂无申请记录">
        <el-table-column prop="id" label="ID" width="60" />
        <el-table-column prop="contactName" label="联系人" width="100" />
        <el-table-column prop="contactPhone" label="联系电话" width="130" />
        <el-table-column prop="serviceName" label="申请服务" min-width="160" show-overflow-tooltip />
        <el-table-column prop="username" label="申请人" width="120" />
        <el-table-column label="状态" width="90">
          <template #default="{ row }">
            <el-tag :type="statusTagType(row.status)" size="small">
              {{ statusLabel(row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="提交时间" width="160" />
        <el-table-column label="操作" width="80" fixed="right">
          <template #default="{ row }">
            <el-button size="small" type="primary" link @click="openEdit(row)">编辑</el-button>
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

    <el-dialog v-model="editVisible" title="编辑申请" width="420px" destroy-on-close>
      <el-form ref="editFormRef" :model="editForm" label-width="80px">
        <el-form-item label="联系人">
          <el-input v-model="editForm.contactName" disabled />
        </el-form-item>
        <el-form-item label="联系电话">
          <el-input v-model="editForm.contactPhone" disabled />
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="editForm.status" style="width:100%">
            <el-option value="pending" label="待处理" />
            <el-option value="contacted" label="已联系" />
            <el-option value="closed" label="已结束" />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="editVisible = false">取消</el-button>
        <el-button type="primary" :loading="editSubmitting" @click="handleEditSubmit">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { getServiceApplications, updateServiceApplication } from '@/api/admin'
import { ElMessage } from 'element-plus'

const loading = ref(false)
const tableData = ref([])
const page = ref(1)
const size = ref(10)
const total = ref(0)
const queryContact = ref('')
const queryServiceName = ref('')
const queryStatus = ref('')

const editVisible = ref(false)
const editSubmitting = ref(false)
const editFormRef = ref(null)
const editForm = reactive({ id: null, contactName: '', contactPhone: '', status: '' })

const statusLabel = (s) => ({ pending: '待处理', contacted: '已联系', closed: '已结束' }[s] || s)
const statusTagType = (s) => ({ pending: 'warning', contacted: '', closed: 'info' }[s] || '')

const fetchList = async () => {
  loading.value = true
  try {
    const res = await getServiceApplications({
      page: page.value,
      size: size.value,
      contactName: queryContact.value || undefined,
      serviceName: queryServiceName.value || undefined,
      status: queryStatus.value || undefined
    })
    const d = res.data
    tableData.value = d.records || []
    total.value = d.total || 0
  } catch {
    ElMessage.error('加载失败')
  } finally {
    loading.value = false
  }
}

const openEdit = (row) => {
  editForm.id = row.id
  editForm.contactName = row.contactName
  editForm.contactPhone = row.contactPhone
  editForm.status = row.status
  editVisible.value = true
}

const handleEditSubmit = async () => {
  editSubmitting.value = true
  try {
    await updateServiceApplication(editForm.id, {
      contactName: editForm.contactName,
      contactPhone: editForm.contactPhone,
      status: editForm.status
    })
    ElMessage.success('保存成功')
    editVisible.value = false
    fetchList()
  } catch {
    // handled by interceptor
  } finally {
    editSubmitting.value = false
  }
}

fetchList()
</script>
