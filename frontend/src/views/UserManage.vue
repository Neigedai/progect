<template>
  <div class="admin-page">
    <div class="page-header">
      <h2>用户管理</h2>
      <el-button type="primary" @click="openUserDialog()">新增用户</el-button>
    </div>

    <el-card class="search-card">
      <el-form :model="query" inline>
        <el-form-item label="搜索">
          <el-input v-model="query.keyword" placeholder="用户名/手机号/昵称" clearable style="width:220px" @keyup.enter="fetchList" />
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
      <el-table v-loading="loading" :data="tableData" stripe empty-text="暂无用户数据">
        <el-table-column prop="id" label="ID" width="60" />
        <el-table-column prop="username" label="用户名" width="120" />
        <el-table-column prop="nickname" label="昵称" width="120" />
        <el-table-column prop="phone" label="手机号" width="130" />
        <el-table-column prop="email" label="邮箱" min-width="160" show-overflow-tooltip />
        <el-table-column label="状态" width="80">
          <template #default="{ row }">
            <el-switch :model-value="row.status === 1" @change="handleStatusChange(row)" />
          </template>
        </el-table-column>
        <el-table-column prop="lastLoginTime" label="最后登录" width="160" />
        <el-table-column prop="createTime" label="注册时间" width="160" />
        <el-table-column label="操作" width="240" fixed="right">
          <template #default="{ row }">
            <el-button size="small" @click="openUserDialog(row)">编辑</el-button>
            <el-button size="small" @click="openPasswordDialog(row)">改密</el-button>
            <el-popconfirm title="确定删除该用户？" @confirm="handleDelete(row.id)">
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

    <!-- 用户编辑弹窗 -->
    <el-dialog v-model="dialogVisible" :title="isEdit ? '编辑用户' : '新增用户'" width="480px" destroy-on-close>
      <el-form ref="formRef" :model="form" :rules="rules" label-width="80px">
        <el-form-item label="用户名" prop="username">
          <el-input v-model="form.username" :disabled="isEdit" />
        </el-form-item>
        <el-form-item label="密码" :prop="isEdit ? null : 'password'">
          <el-input v-model="form.password" :placeholder="isEdit ? '留空则不修改' : '请输入密码'" show-password />
        </el-form-item>
        <el-form-item label="昵称" prop="nickname">
          <el-input v-model="form.nickname" />
        </el-form-item>
        <el-form-item label="手机号" prop="phone">
          <el-input v-model="form.phone" />
        </el-form-item>
        <el-form-item label="邮箱" prop="email">
          <el-input v-model="form.email" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="submitting" @click="handleSubmit">保存</el-button>
      </template>
    </el-dialog>

    <!-- 改密弹窗 -->
    <el-dialog v-model="pwdDialogVisible" title="重置密码" width="480px" destroy-on-close>
      <el-form ref="pwdFormRef" :model="pwdForm" :rules="pwdRules" label-width="80px">
        <el-form-item label="新密码" prop="password">
          <el-input v-model="pwdForm.password" show-password />
        </el-form-item>
        <p style="font-size:12px;color:#909399">用户: {{ pwdTarget?.username }}（{{ pwdTarget?.nickname }}）</p>
      </el-form>
      <template #footer>
        <el-button @click="pwdDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handlePasswordReset">确认</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { ElMessage } from 'element-plus'
import request from '@/utils/request'

const loading = ref(false)
const submitting = ref(false)
const tableData = ref([])
const total = ref(0)
const dialogVisible = ref(false)
const isEdit = ref(false)
const formRef = ref(null)
const editId = ref(null)

const query = reactive({ page: 1, size: 10, keyword: '', status: null })

const form = reactive({ username: '', password: '', nickname: '', phone: '', email: '' })
const rules = {
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }, { min: 6, message: '密码至少6位', trigger: 'blur' }]
}

const pwdDialogVisible = ref(false)
const pwdFormRef = ref(null)
const pwdTarget = ref(null)
const pwdForm = reactive({ password: '' })
const pwdRules = {
  password: [{ required: true, message: '请输入新密码', trigger: 'blur' }, { min: 6, message: '密码至少6位', trigger: 'blur' }]
}

const fetchList = async () => {
  loading.value = true
  try {
    const res = await request.get('/admin/users', { params: query })
    tableData.value = res.data.records
    total.value = res.data.total
  } finally { loading.value = false }
}

const resetQuery = () => {
  query.keyword = ''
  query.status = null
  query.page = 1
  fetchList()
}

const openUserDialog = async (row) => {
  Object.assign(form, { username: '', password: '', nickname: '', phone: '', email: '' })
  if (row) {
    isEdit.value = true
    editId.value = row.id
    const res = await request.get(`/admin/users/${row.id}`)
    Object.assign(form, { username: res.data.username, password: '', nickname: res.data.nickname || '', phone: res.data.phone || '', email: res.data.email || '' })
  } else {
    isEdit.value = false
    editId.value = null
  }
  dialogVisible.value = true
}

const handleSubmit = async () => {
  try { await formRef.value.validate() } catch { return }
  submitting.value = true
  try {
    if (isEdit.value) {
      await request.put(`/admin/users/${editId.value}`, form)
    } else {
      await request.post('/admin/users', form)
    }
    ElMessage.success(isEdit.value ? '更新成功' : '新增成功')
    dialogVisible.value = false
    fetchList()
  } finally { submitting.value = false }
}

const handleDelete = async (id) => {
  await request.delete(`/admin/users/${id}`)
  ElMessage.success('删除成功')
  fetchList()
}

const handleStatusChange = async (row) => {
  const newStatus = row.status === 1 ? 0 : 1
  await request.patch(`/admin/users/${row.id}/status`, null, { params: { status: newStatus } })
  row.status = newStatus
  ElMessage.success(newStatus === 1 ? '已启用' : '已禁用')
}

const openPasswordDialog = (row) => {
  pwdTarget.value = row
  pwdForm.password = ''
  pwdDialogVisible.value = true
}

const handlePasswordReset = async () => {
  try { await pwdFormRef.value.validate() } catch { return }
  await request.patch(`/admin/users/${pwdTarget.value.id}/password`, { password: pwdForm.password })
  ElMessage.success('密码重置成功')
  pwdDialogVisible.value = false
}

fetchList()
</script>

<style scoped>
.admin-page { padding-bottom: 24px; }
.admin-page .el-card { border-radius: var(--radius-lg); }
</style>
