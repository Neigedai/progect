<template>
  <div class="admin-page">
    <div class="page-header">
      <h2>园区管理</h2>
      <el-button type="primary" @click="openDialog()">新增园区</el-button>
    </div>

    <el-card class="search-card">
      <el-form :model="query" inline>
        <el-form-item label="园区名称">
          <el-input v-model="query.parkName" placeholder="输入名称搜索" clearable @keyup.enter="fetchList" />
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="query.status" placeholder="全部" clearable>
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
      <el-table v-loading="loading" :data="tableData" stripe>
        <el-table-column prop="id" label="ID" width="60" />
        <el-table-column prop="parkName" label="园区名称" min-width="140" />
        <el-table-column prop="sortOrder" label="排序" width="70" />
        <el-table-column label="状态" width="80">
          <template #default="{ row }">
            <el-switch :model-value="row.status === 1" @change="handleStatusChange(row)" />
          </template>
        </el-table-column>
        <el-table-column prop="address" label="地址" min-width="180" show-overflow-tooltip />
        <el-table-column label="操作" width="180" fixed="right">
          <template #default="{ row }">
            <el-button size="small" @click="openDialog(row)">编辑</el-button>
            <el-popconfirm title="确定删除该园区？" @confirm="handleDelete(row.id)">
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

    <el-dialog v-model="dialogVisible" :title="isEdit ? '编辑园区' : '新增园区'" width="800px" destroy-on-close @opened="onDialogOpened">
      <el-tabs v-model="activeTab" @tab-change="onTabChange">
        <el-tab-pane label="基础信息" name="basic">
          <el-form ref="formRef" :model="form" :rules="rules" label-width="100px">
            <el-form-item label="园区名称" prop="parkName">
              <el-input v-model="form.parkName" />
            </el-form-item>
            <el-form-item label="Logo URL" prop="logo">
              <el-input v-model="form.logo" placeholder="输入图片路径或上传" />
              <upload-btn @uploaded="url => form.logo = url" />
            </el-form-item>
            <el-form-item label="默认园区" prop="isDefault">
              <el-switch v-model="form.isDefault" :active-value="1" :inactive-value="0" />
            </el-form-item>
            <el-form-item label="排序" prop="sortOrder">
              <el-input-number v-model="form.sortOrder" :min="0" />
            </el-form-item>
            <el-form-item label="状态" prop="status">
              <el-switch v-model="form.status" :active-value="1" :inactive-value="0" />
            </el-form-item>
            <el-form-item label="地址" prop="address">
              <el-input v-model="form.address" />
            </el-form-item>
            <el-form-item label="园区简介" prop="description">
              <el-input v-model="form.description" type="textarea" :rows="3" />
            </el-form-item>
            <el-form-item label="规划图" prop="planImage">
              <el-input v-model="form.planImage" />
              <upload-btn @uploaded="url => form.planImage = url" />
            </el-form-item>
            <el-form-item label="规划说明" prop="planDescription">
              <el-input v-model="form.planDescription" type="textarea" :rows="2" />
            </el-form-item>
            <el-form-item label="交通信息" prop="transportInfo">
              <el-input v-model="form.transportInfo" type="textarea" :rows="2" />
            </el-form-item>
            <el-form-item label="宣传视频" prop="videoUrl">
              <el-input v-model="form.videoUrl" placeholder="MP4 路径或外链" />
            </el-form-item>
          </el-form>
        </el-tab-pane>

        <el-tab-pane label="配套设施" name="facilities" :disabled="!isEdit">
          <div style="margin-bottom:12px">
            <el-button size="small" type="primary" @click="openFacilityDialog()">添加设施</el-button>
          </div>
          <el-table :data="facilityList" size="small" :empty-text="isEdit ? '暂无设施' : '请先保存园区基础信息'">
            <el-table-column prop="facilityName" label="名称" />
            <el-table-column prop="icon" label="图标URL" show-overflow-tooltip />
            <el-table-column prop="image" label="图片URL" show-overflow-tooltip />
            <el-table-column prop="description" label="描述" show-overflow-tooltip />
            <el-table-column prop="sortOrder" label="排序" width="60" />
            <el-table-column label="操作" width="120">
              <template #default="{ row }">
                <el-button size="small" text @click="openFacilityDialog(row)">编辑</el-button>
                <el-popconfirm title="确定删除？" @confirm="handleDeleteFacility(row.id)">
                  <template #reference>
                    <el-button size="small" text type="danger">删除</el-button>
                  </template>
                </el-popconfirm>
              </template>
            </el-table-column>
          </el-table>
        </el-tab-pane>

        <el-tab-pane label="荣誉资质" name="honors" :disabled="!isEdit">
          <div style="margin-bottom:12px">
            <el-button size="small" type="primary" @click="openHonorDialog()">添加荣誉</el-button>
          </div>
          <el-table :data="honorList" size="small" :empty-text="isEdit ? '暂无荣誉' : '请先保存园区基础信息'">
            <el-table-column prop="honorName" label="荣誉名称" />
            <el-table-column prop="image" label="图片URL" show-overflow-tooltip />
            <el-table-column prop="awardYear" label="颁发年份" width="90" />
            <el-table-column prop="sortOrder" label="排序" width="60" />
            <el-table-column label="操作" width="120">
              <template #default="{ row }">
                <el-button size="small" text @click="openHonorDialog(row)">编辑</el-button>
                <el-popconfirm title="确定删除？" @confirm="handleDeleteHonor(row.id)">
                  <template #reference>
                    <el-button size="small" text type="danger">删除</el-button>
                  </template>
                </el-popconfirm>
              </template>
            </el-table-column>
          </el-table>
        </el-tab-pane>
      </el-tabs>

      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="submitting" @click="handleSubmit">保存</el-button>
      </template>
    </el-dialog>

    <!-- 设施编辑弹窗 -->
    <el-dialog v-model="facilityDialogVisible" :title="facilityEditId ? '编辑设施' : '新增设施'" width="500px" destroy-on-close>
      <el-form ref="facilityFormRef" :model="facilityForm" :rules="facilityRules" label-width="80px">
        <el-form-item label="名称" prop="facilityName">
          <el-input v-model="facilityForm.facilityName" />
        </el-form-item>
        <el-form-item label="图标URL" prop="icon">
          <el-input v-model="facilityForm.icon" />
        </el-form-item>
        <el-form-item label="图片URL" prop="image">
          <el-input v-model="facilityForm.image" />
          <upload-btn @uploaded="url => facilityForm.image = url" />
        </el-form-item>
        <el-form-item label="描述" prop="description">
          <el-input v-model="facilityForm.description" type="textarea" :rows="2" />
        </el-form-item>
        <el-form-item label="排序" prop="sortOrder">
          <el-input-number v-model="facilityForm.sortOrder" :min="0" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="facilityDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleFacilitySubmit">保存</el-button>
      </template>
    </el-dialog>

    <!-- 荣誉编辑弹窗 -->
    <el-dialog v-model="honorDialogVisible" :title="honorEditId ? '编辑荣誉' : '新增荣誉'" width="500px" destroy-on-close>
      <el-form ref="honorFormRef" :model="honorForm" :rules="honorRules" label-width="80px">
        <el-form-item label="荣誉名称" prop="honorName">
          <el-input v-model="honorForm.honorName" />
        </el-form-item>
        <el-form-item label="图片URL" prop="image">
          <el-input v-model="honorForm.image" />
          <upload-btn @uploaded="url => honorForm.image = url" />
        </el-form-item>
        <el-form-item label="颁发年份" prop="awardYear">
          <el-input v-model="honorForm.awardYear" placeholder="如 2024" />
        </el-form-item>
        <el-form-item label="排序" prop="sortOrder">
          <el-input-number v-model="honorForm.sortOrder" :min="0" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="honorDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleHonorSubmit">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { ElMessage } from 'element-plus'
import {
  getParkList, getParkDetail, createPark, updatePark, deletePark, updateParkStatus,
  getFacilities, createFacility, updateFacility, deleteFacility,
  getHonors, createHonor, updateHonor, deleteHonor
} from '@/api/admin'
import UploadBtn from '@/components/UploadBtn.vue'

const loading = ref(false)
const submitting = ref(false)
const tableData = ref([])
const total = ref(0)
const dialogVisible = ref(false)
const isEdit = ref(false)
const editId = ref(null)
const activeTab = ref('basic')
const formRef = ref(null)

const query = reactive({ page: 1, size: 10, parkName: '', status: null })

const form = reactive({
  parkName: '', logo: '', isDefault: 0, sortOrder: 0, status: 1,
  address: '', description: '', planImage: '', planDescription: '', transportInfo: '', videoUrl: ''
})

const rules = {
  parkName: [{ required: true, message: '请输入园区名称', trigger: 'blur' }]
}

// ====== 设施 ======
const facilityList = ref([])
const facilityDialogVisible = ref(false)
const facilityEditId = ref(null)
const facilityFormRef = ref(null)
const facilityForm = reactive({ facilityName: '', icon: '', image: '', description: '', sortOrder: 0 })
const facilityRules = { facilityName: [{ required: true, message: '请输入设施名称', trigger: 'blur' }] }

// ====== 荣誉 ======
const honorList = ref([])
const honorDialogVisible = ref(false)
const honorEditId = ref(null)
const honorFormRef = ref(null)
const honorForm = reactive({ honorName: '', image: '', awardYear: '', sortOrder: 0 })
const honorRules = { honorName: [{ required: true, message: '请输入荣誉名称', trigger: 'blur' }] }

const fetchList = async () => {
  loading.value = true
  try {
    const res = await getParkList(query)
    tableData.value = res.data.records
    total.value = res.data.total
  } finally { loading.value = false }
}

const resetQuery = () => {
  query.parkName = ''
  query.status = null
  query.page = 1
  fetchList()
}

const onDialogOpened = () => { activeTab.value = 'basic' }

const onTabChange = (tab) => {
  if (tab === 'facilities') loadFacilities()
  else if (tab === 'honors') loadHonors()
}

const openDialog = async (row) => {
  Object.assign(form, {
    parkName: '', logo: '', isDefault: 0, sortOrder: 0, status: 1,
    address: '', description: '', planImage: '', planDescription: '', transportInfo: '', videoUrl: ''
  })
  facilityList.value = []
  honorList.value = []
  if (row) {
    isEdit.value = true
    editId.value = row.id
    const res = await getParkDetail(row.id)
    Object.assign(form, res.data)
  } else {
    isEdit.value = false
    editId.value = null
  }
  dialogVisible.value = true
}

const loadFacilities = async () => {
  if (!editId.value) return
  const res = await getFacilities(editId.value)
  facilityList.value = res.data || []
}

const loadHonors = async () => {
  if (!editId.value) return
  const res = await getHonors(editId.value)
  honorList.value = res.data || []
}

const handleSubmit = async () => {
  try { await formRef.value.validate() } catch { return }
  submitting.value = true
  try {
    if (isEdit.value) {
      await updatePark(editId.value, form)
    } else {
      await createPark(form)
    }
    ElMessage.success(isEdit.value ? '更新成功' : '新增成功')
    dialogVisible.value = false
    fetchList()
  } finally { submitting.value = false }
}

const handleDelete = async (id) => {
  await deletePark(id)
  ElMessage.success('删除成功')
  fetchList()
}

const handleStatusChange = async (row) => {
  const newStatus = row.status === 1 ? 0 : 1
  await updateParkStatus(row.id, newStatus)
  row.status = newStatus
  ElMessage.success('状态更新成功')
}

const openFacilityDialog = (row) => {
  Object.assign(facilityForm, { facilityName: '', icon: '', image: '', description: '', sortOrder: 0 })
  facilityEditId.value = row ? row.id : null
  if (row) Object.assign(facilityForm, row)
  facilityDialogVisible.value = true
}

const handleFacilitySubmit = async () => {
  try { await facilityFormRef.value.validate() } catch { return }
  if (facilityEditId.value) {
    await updateFacility(editId.value, facilityEditId.value, facilityForm)
  } else {
    await createFacility(editId.value, facilityForm)
  }
  ElMessage.success(facilityEditId.value ? '更新成功' : '新增成功')
  facilityDialogVisible.value = false
  loadFacilities()
}

const handleDeleteFacility = async (id) => {
  await deleteFacility(editId.value, id)
  ElMessage.success('删除成功')
  loadFacilities()
}

const openHonorDialog = (row) => {
  Object.assign(honorForm, { honorName: '', image: '', awardYear: '', sortOrder: 0 })
  honorEditId.value = row ? row.id : null
  if (row) Object.assign(honorForm, row)
  honorDialogVisible.value = true
}

const handleHonorSubmit = async () => {
  try { await honorFormRef.value.validate() } catch { return }
  if (honorEditId.value) {
    await updateHonor(editId.value, honorEditId.value, honorForm)
  } else {
    await createHonor(editId.value, honorForm)
  }
  ElMessage.success(honorEditId.value ? '更新成功' : '新增成功')
  honorDialogVisible.value = false
  loadHonors()
}

const handleDeleteHonor = async (id) => {
  await deleteHonor(editId.value, id)
  ElMessage.success('删除成功')
  loadHonors()
}

fetchList()
</script>

<style scoped>
</style>
