<template>
  <div class="admin-page">
    <div class="page-header">
      <h2>角色管理</h2>
      <el-button type="primary" @click="openDialog()">新增角色</el-button>
    </div>

    <el-card class="search-card">
      <el-form inline>
        <el-form-item label="角色名称">
          <el-input v-model="queryName" placeholder="输入角色名称搜索" clearable style="width:220px" @keyup.enter="applyFilter" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="applyFilter">搜索</el-button>
          <el-button @click="queryName = ''; tableData = allData">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <el-card>
      <el-table v-loading="loading" :data="tableData" stripe empty-text="暂无角色数据">
        <el-table-column prop="id" label="ID" width="60" />
        <el-table-column prop="roleName" label="角色名称" width="140" />
        <el-table-column prop="roleCode" label="角色编码" width="140" />
        <el-table-column prop="description" label="描述" min-width="180" />
        <el-table-column label="用户" min-width="200">
          <template #default="{ row }">
            <template v-if="roleUsersMap[row.id]?.length">
              <el-tag v-for="u in roleUsersMap[row.id]" :key="u.id" size="small" style="margin: 2px 4px 2px 0">
                {{ u.nickname || u.username }}
              </el-tag>
            </template>
            <span v-else style="color: #999">暂无用户</span>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" width="160" />
        <el-table-column label="操作" width="280" fixed="right">
          <template #default="{ row }">
            <el-button size="small" @click="openPermDialog(row)">分配权限</el-button>
            <el-button size="small" type="primary" @click="openUserDialog(row)">编辑用户</el-button>
            <el-button size="small" @click="openDialog(row)">编辑</el-button>
            <el-popconfirm title="确定删除该角色？" @confirm="handleDelete(row.id)">
              <template #reference>
                <el-button size="small" type="danger">删除</el-button>
              </template>
            </el-popconfirm>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <el-dialog v-model="dialogVisible" :title="isEdit ? '编辑角色' : '新增角色'" width="480px" destroy-on-close>
      <el-form ref="formRef" :model="form" :rules="rules" label-width="100px">
        <el-form-item label="角色名称" prop="roleName">
          <el-input v-model="form.roleName" />
        </el-form-item>
        <el-form-item label="角色编码" prop="roleCode">
          <el-input v-model="form.roleCode" :disabled="isEdit" />
        </el-form-item>
        <el-form-item label="描述">
          <el-input v-model="form.description" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="submitting" @click="handleSubmit">保存</el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="permDialogVisible" title="分配权限" width="600px">
      <el-tree
        ref="treeRef"
        :data="permTree"
        show-checkbox
        node-key="id"
        :props="{ label: 'permissionName', children: 'children' }"
        :default-checked-keys="checkedPermIds"
        default-expand-all
      />
      <template #footer>
        <el-button @click="permDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="savePerms">保存权限</el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="userDialogVisible" :title="`编辑角色用户 - ${editRoleName}`" width="480px">
      <p style="margin-bottom: 12px; color: #666; font-size: 13px">勾选属于该角色的用户：</p>
      <el-checkbox-group v-model="selectedUserIds" v-loading="userDialogLoading">
        <el-checkbox v-for="u in allUsers" :key="u.id" :value="u.id" style="display: block; margin-bottom: 6px">
          {{ u.nickname || u.username }} <span v-if="u.nickname" style="color: #999; font-size: 12px">({{ u.username }})</span>
        </el-checkbox>
      </el-checkbox-group>
      <template #footer>
        <el-button @click="userDialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="userDialogSaving" @click="saveRoleUsers">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { ElMessage } from 'element-plus'
import request from '@/utils/request'

const loading = ref(false)
const queryName = ref('')
const allData = ref([])
const submitting = ref(false)
const tableData = ref([])
const dialogVisible = ref(false)
const permDialogVisible = ref(false)
const isEdit = ref(false)
const formRef = ref(null)
const treeRef = ref(null)
const editId = ref(null)
const permRoleId = ref(null)
const allPerms = ref([])
const checkedPermIds = ref([])
const roleUsersMap = ref({})

const userDialogVisible = ref(false)
const userDialogLoading = ref(false)
const userDialogSaving = ref(false)
const editRoleName = ref('')
const editRoleId = ref(null)
const allUsers = ref([])
const selectedUserIds = ref([])

const form = reactive({ roleName: '', roleCode: '', description: '' })
const rules = {
  roleName: [{ required: true, message: '请输入角色名称', trigger: 'blur' }],
  roleCode: [{ required: true, message: '请输入角色编码', trigger: 'blur' }]
}

const buildTree = (perms, parentId = 0) => {
  return perms
    .filter(p => (p.parentId || 0) === parentId)
    .sort((a, b) => a.sortOrder - b.sortOrder)
    .map(p => ({ ...p, children: buildTree(perms, p.id) }))
}
const permTree = ref([])

const applyFilter = () => {
  if (!queryName.value) { tableData.value = allData.value; return }
  tableData.value = allData.value.filter(r => r.roleName?.includes(queryName.value) || r.roleCode?.includes(queryName.value))
}

const fetchList = async () => {
  loading.value = true
  try {
    const res = await request.get('/admin/perm/roles')
    tableData.value = res.data || []
    allData.value = res.data || []
    const map = {}
    for (const role of tableData.value) {
      const r = await request.get(`/admin/perm/roles/${role.id}/users`)
      map[role.id] = r.data || []
    }
    roleUsersMap.value = map
  } finally {
    loading.value = false
  }
}

const fetchPerms = async () => {
  const res = await request.get('/admin/perm/permissions')
  allPerms.value = res.data || []
  permTree.value = buildTree(allPerms.value)
}

const openDialog = async (row) => {
  Object.assign(form, { roleName: '', roleCode: '', description: '' })
  if (row) {
    isEdit.value = true
    editId.value = row.id
    Object.assign(form, { roleName: row.roleName, roleCode: row.roleCode, description: row.description })
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
      await request.put(`/admin/perm/roles/${editId.value}`, { ...form })
    } else {
      await request.post('/admin/perm/roles', { ...form })
    }
    ElMessage.success(isEdit.value ? '更新成功' : '新增成功')
    dialogVisible.value = false
    fetchList()
  } finally {
    submitting.value = false
  }
}

const handleDelete = async (id) => {
  await request.delete(`/admin/perm/roles/${id}`)
  ElMessage.success('删除成功')
  fetchList()
}

const openPermDialog = async (row) => {
  permRoleId.value = row.id
  await fetchPerms()
  const res = await request.get(`/admin/perm/roles/${row.id}/permissions`)
  checkedPermIds.value = (res.data || []).map(p => p.id)
  permDialogVisible.value = true
}

const savePerms = async () => {
  const ids = treeRef.value.getCheckedKeys()
  const halfIds = treeRef.value.getHalfCheckedKeys()
  await request.put(`/admin/perm/roles/${permRoleId.value}/permissions`, { permissionIds: [...ids, ...halfIds] })
  ElMessage.success('权限已保存')
  permDialogVisible.value = false
}

const openUserDialog = async (row) => {
  editRoleId.value = row.id
  editRoleName.value = row.roleName
  userDialogVisible.value = true
  userDialogLoading.value = true
  try {
    const [usersRes, roleUsersRes] = await Promise.all([
      request.get('/admin/users', { params: { page: 1, size: 9999 } }),
      request.get(`/admin/perm/roles/${row.id}/users`)
    ])
    allUsers.value = usersRes.data?.records || usersRes.data || []
    selectedUserIds.value = (roleUsersRes.data || []).map(u => u.id)
  } finally {
    userDialogLoading.value = false
  }
}

const saveRoleUsers = async () => {
  userDialogSaving.value = true
  try {
    await request.put(`/admin/perm/roles/${editRoleId.value}/users`, { userIds: selectedUserIds.value })
    ElMessage.success('用户已保存')
    userDialogVisible.value = false
    fetchList()
  } finally {
    userDialogSaving.value = false
  }
}

fetchList()
</script>

<style scoped>
.admin-page { padding-bottom: 24px; }
.admin-page .el-card { border-radius: var(--radius-lg); }
</style>
