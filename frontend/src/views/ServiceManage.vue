<template>
  <div class="admin-page">
    <div class="page-header">
      <h2>服务管理</h2>
    </div>

    <el-tabs v-model="activeTab">
      <!-- ====== 服务分类 ====== -->
      <el-tab-pane label="服务分类" name="categories">
        <div style="margin-bottom:12px">
          <el-button type="primary" size="small" @click="openCategoryDialog()">新增分类</el-button>
        </div>
        <el-card>
          <el-table :data="categoryList" stripe size="small" v-loading="catLoading">
            <el-table-column prop="id" label="ID" width="60" />
            <el-table-column prop="categoryName" label="分类名称" />
            <el-table-column prop="code" label="编码" width="120" />
            <el-table-column prop="sortOrder" label="排序" width="70" />
            <el-table-column label="操作" width="140">
              <template #default="{ row }">
                <el-button size="small" text @click="openCategoryDialog(row)">编辑</el-button>
                <el-popconfirm title="确定删除？" @confirm="handleDeleteCategory(row.id)">
                  <template #reference>
                    <el-button size="small" text type="danger">删除</el-button>
                  </template>
                </el-popconfirm>
              </template>
            </el-table-column>
          </el-table>
        </el-card>
      </el-tab-pane>

      <!-- ====== 服务项目 ====== -->
      <el-tab-pane label="服务项目" name="items">
        <div style="margin-bottom:12px;display:flex;gap:12px;align-items:center">
          <el-button type="primary" size="small" @click="openItemDialog()">新增服务</el-button>
          <el-select v-model="itemQuery.categoryId" placeholder="分类筛选" clearable size="small" style="width:180px" @change="fetchItems">
            <el-option v-for="c in categoryList" :key="c.id" :value="c.id" :label="c.categoryName" />
          </el-select>
          <el-select v-model="itemQuery.status" placeholder="状态" clearable size="small" style="width:140px" @change="fetchItems">
            <el-option :value="1" label="上架" />
            <el-option :value="0" label="下架" />
          </el-select>
          <el-input v-model="itemQuery.keyword" placeholder="搜索服务名称" clearable size="small" style="width:220px" @keyup.enter="fetchItems" />
          <el-button size="small" @click="fetchItems">搜索</el-button>
        </div>
        <el-card>
          <el-table :data="itemTableData" stripe size="small" v-loading="itemLoading">
            <el-table-column prop="id" label="ID" width="60" />
            <el-table-column prop="serviceName" label="服务名称" min-width="180" show-overflow-tooltip />
            <el-table-column label="分类" width="100">
              <template #default="{ row }">
                {{ categoryMap[row.categoryId] || '-' }}
              </template>
            </el-table-column>
            <el-table-column prop="applicableEnterprise" label="适用企业" width="120" show-overflow-tooltip />
            <el-table-column label="状态" width="80">
              <template #default="{ row }">
                <el-switch :model-value="row.status === 1" @change="handleItemStatusChange(row)" />
              </template>
            </el-table-column>
            <el-table-column prop="sortOrder" label="排序" width="60" />
            <el-table-column label="操作" width="140">
              <template #default="{ row }">
                <el-button size="small" text @click="openItemDialog(row)">编辑</el-button>
                <el-popconfirm title="确定删除？" @confirm="handleDeleteItem(row.id)">
                  <template #reference>
                    <el-button size="small" text type="danger">删除</el-button>
                  </template>
                </el-popconfirm>
              </template>
            </el-table-column>
          </el-table>
          <el-pagination
            v-model:current-page="itemQuery.page"
            v-model:page-size="itemQuery.size"
            :total="itemTotal"
            layout="total, prev, pager, next"
            :page-sizes="[10, 20]"
            @change="fetchItems"
            class="pagination"
          />
        </el-card>
      </el-tab-pane>
    </el-tabs>

    <!-- 分类弹窗 -->
    <el-dialog v-model="catDialogVisible" :title="catEditId ? '编辑分类' : '新增分类'" width="480px" destroy-on-close>
      <el-form ref="catFormRef" :model="catForm" :rules="catRules" label-width="80px">
        <el-form-item label="名称" prop="categoryName">
          <el-input v-model="catForm.categoryName" />
        </el-form-item>
        <el-form-item label="编码" prop="code">
          <el-input v-model="catForm.code" />
        </el-form-item>
        <el-form-item label="排序" prop="sortOrder">
          <el-input-number v-model="catForm.sortOrder" :min="0" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="catDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleCategorySubmit">保存</el-button>
      </template>
    </el-dialog>

    <!-- 服务项目弹窗 -->
    <el-dialog v-model="itemDialogVisible" :title="itemEditId ? '编辑服务' : '新增服务'" width="800px" destroy-on-close>
      <el-form ref="itemFormRef" :model="itemForm" :rules="itemRules" label-width="100px">
        <el-divider content-position="left">基本信息</el-divider>
        <el-form-item label="服务名称" prop="serviceName">
          <el-input v-model="itemForm.serviceName" />
        </el-form-item>
        <el-form-item label="分类" prop="categoryId">
          <el-select v-model="itemForm.categoryId" placeholder="选择分类">
            <el-option v-for="c in categoryList" :key="c.id" :value="c.id" :label="c.categoryName" />
          </el-select>
        </el-form-item>
        <el-form-item label="适用企业" prop="applicableEnterprise">
          <el-input v-model="itemForm.applicableEnterprise" placeholder="如：科技型企业、初创企业" />
        </el-form-item>
        <el-form-item label="摘要" prop="summary">
          <el-input v-model="itemForm.summary" type="textarea" :rows="2" />
        </el-form-item>
        <el-form-item label="详情描述" prop="detailDesc">
          <el-input v-model="itemForm.detailDesc" type="textarea" :rows="3" />
        </el-form-item>
        <el-divider content-position="left">办理流程</el-divider>
        <el-form-item label="办理流程">
          <div v-for="(s, i) in itemForm.stepsList" :key="i" style="display:flex;gap:8px;margin-bottom:8px">
            <span style="line-height:32px;flex-shrink:0">第{{ i+1 }}步</span>
            <el-input v-model="s.title" placeholder="步骤标题" size="small" style="flex:1" />
            <el-input v-model="s.desc" placeholder="步骤描述" size="small" style="flex:2" />
            <el-button size="small" type="danger" :icon="Delete" circle @click="itemForm.stepsList.splice(i,1)" />
          </div>
          <el-button size="small" @click="itemForm.stepsList.push({title:'',desc:''})">+ 添加步骤</el-button>
        </el-form-item>
        <el-form-item label="费用说明">
          <div style="display:flex;flex-direction:column;gap:8px;width:100%">
            <el-input v-model="itemForm.priceInfo.price" placeholder="服务费用，如：8000-15000元" size="small" />
          </div>
        </el-form-item>
        <el-form-item label="政府补贴">
          <div style="display:flex;flex-direction:column;gap:8px;width:100%">
            <el-input v-model="itemForm.priceInfo.subsidy" placeholder="政府补贴，如：可申请奖励10-50万元" size="small" />
          </div>
        </el-form-item>
        <el-form-item label="备注">
          <div style="display:flex;flex-direction:column;gap:8px;width:100%">
            <el-input v-model="itemForm.priceInfo.note" placeholder="备注" size="small" />
          </div>
        </el-form-item>
        <el-form-item label="排序" prop="sortOrder">
          <el-input-number v-model="itemForm.sortOrder" :min="0" />
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-switch v-model="itemForm.status" :active-value="1" :inactive-value="0" active-text="上架" inactive-text="下架" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="itemDialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="itemSubmitting" @click="handleItemSubmit">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { Delete } from '@element-plus/icons-vue'
import request from '@/utils/request'

const activeTab = ref('categories')

// ====== 分类 ======
const catLoading = ref(false)
const categoryList = ref([])
const catDialogVisible = ref(false)
const catEditId = ref(null)
const catFormRef = ref(null)
const catForm = reactive({ categoryName: '', code: '', sortOrder: 0 })
const catRules = {
  categoryName: [{ required: true, message: '请输入分类名称', trigger: 'blur' }],
  code: [{ required: true, message: '请输入编码', trigger: 'blur' }]
}

const categoryMap = computed(() => {
  const m = {}
  categoryList.value.forEach(c => { m[c.id] = c.categoryName })
  return m
})

const fetchCategories = async () => {
  catLoading.value = true
  try {
    const res = await request.get('/admin/services/categories')
    categoryList.value = res.data || []
  } finally { catLoading.value = false }
}

const openCategoryDialog = (row) => {
  Object.assign(catForm, { categoryName: '', code: '', sortOrder: 0 })
  catEditId.value = row ? row.id : null
  if (row) Object.assign(catForm, row)
  catDialogVisible.value = true
}

const handleCategorySubmit = async () => {
  try { await catFormRef.value.validate() } catch { return }
  if (catEditId.value) {
    await request.put(`/admin/services/categories/${catEditId.value}`, catForm)
  } else {
    await request.post('/admin/services/categories', catForm)
  }
  ElMessage.success(catEditId.value ? '更新成功' : '新增成功')
  catDialogVisible.value = false
  fetchCategories()
}

const handleDeleteCategory = async (id) => {
  await request.delete(`/admin/services/categories/${id}`)
  ElMessage.success('删除成功')
  fetchCategories()
}

// ====== 服务项目 ======
const itemLoading = ref(false)
const itemSubmitting = ref(false)
const itemTableData = ref([])
const itemTotal = ref(0)
const itemDialogVisible = ref(false)
const itemEditId = ref(null)
const itemFormRef = ref(null)
const itemQuery = reactive({ page: 1, size: 10, categoryId: null, keyword: '', status: null })

const itemForm = reactive({
  serviceName: '', categoryId: null, applicableEnterprise: '', summary: '', detailDesc: '',
  stepsList: [], priceInfo: { price: '', subsidy: '', note: '' }, sortOrder: 0, status: 1
})
const itemRules = {
  serviceName: [{ required: true, message: '请输入服务名称', trigger: 'blur' }],
  categoryId: [{ required: true, message: '请选择分类', trigger: 'change' }]
}

const fetchItems = async () => {
  itemLoading.value = true
  try {
    const res = await request.get('/admin/services/items', { params: itemQuery })
    itemTableData.value = res.data.records
    itemTotal.value = res.data.total
  } finally { itemLoading.value = false }
}

const openItemDialog = async (row) => {
  Object.assign(itemForm, {
    serviceName: '', categoryId: null, applicableEnterprise: '', summary: '', detailDesc: '',
    stepsList: [], priceInfo: { price: '', subsidy: '', note: '' }, sortOrder: 0, status: 1
  })
  itemEditId.value = null
  if (row) {
    itemEditId.value = row.id
    const res = await request.get(`/admin/services/items/${row.id}`)
    const data = res.data
    Object.assign(itemForm, {
      serviceName: data.serviceName,
      categoryId: data.categoryId,
      applicableEnterprise: data.applicableEnterprise || '',
      summary: data.summary || '',
      detailDesc: data.detailDesc || '',
      sortOrder: data.sortOrder,
      status: data.status
    })
    try { itemForm.stepsList = JSON.parse(data.steps || '[]') } catch { itemForm.stepsList = [] }
    try { itemForm.priceInfo = JSON.parse(data.priceInfo || '{}') } catch { itemForm.priceInfo = { price: '', subsidy: '', note: '' } }
    if (!itemForm.priceInfo.price) itemForm.priceInfo.price = ''
    if (!itemForm.priceInfo.subsidy) itemForm.priceInfo.subsidy = ''
    if (!itemForm.priceInfo.note) itemForm.priceInfo.note = ''
  }
  itemDialogVisible.value = true
}

const handleItemSubmit = async () => {
  try { await itemFormRef.value.validate() } catch { return }
  itemSubmitting.value = true
  try {
    const payload = {
      serviceName: itemForm.serviceName,
      categoryId: itemForm.categoryId,
      applicableEnterprise: itemForm.applicableEnterprise,
      summary: itemForm.summary,
      detailDesc: itemForm.detailDesc,
      steps: JSON.stringify(itemForm.stepsList.filter(s => s.title || s.desc)),
      priceInfo: JSON.stringify(itemForm.priceInfo),
      sortOrder: itemForm.sortOrder,
      status: itemForm.status
    }
    if (itemEditId.value) {
      await request.put(`/admin/services/items/${itemEditId.value}`, payload)
    } else {
      await request.post('/admin/services/items', payload)
    }
    ElMessage.success(itemEditId.value ? '更新成功' : '新增成功')
    itemDialogVisible.value = false
    fetchItems()
  } finally { itemSubmitting.value = false }
}

const handleDeleteItem = async (id) => {
  await request.delete(`/admin/services/items/${id}`)
  ElMessage.success('删除成功')
  fetchItems()
}

const handleItemStatusChange = async (row) => {
  const newStatus = row.status === 1 ? 0 : 1
  await request.patch(`/admin/services/items/${row.id}/status`, null, { params: { status: newStatus } })
  row.status = newStatus
  ElMessage.success('状态更新成功')
}

onMounted(() => {
  fetchCategories()
  fetchItems()
})
</script>

<style scoped>
.admin-page { padding-bottom: 24px; }
.admin-page .el-card { border-radius: var(--radius-lg); }
</style>
