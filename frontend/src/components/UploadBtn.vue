<template>
  <span style="margin-left:8px">
    <input ref="fileInput" type="file" accept="image/*" style="display:none" @change="handleFile" />
    <el-button size="small" @click="fileInput.click()">上传</el-button>
  </span>
</template>

<script setup>
import { ref } from 'vue'
import { ElMessage } from 'element-plus'
import { uploadImage } from '@/api/admin'

const emit = defineEmits(['uploaded'])

const fileInput = ref(null)

const handleFile = async (e) => {
  const file = e.target.files[0]
  if (!file) return
  if (!file.type.startsWith('image/')) {
    ElMessage.error('仅支持图片文件')
    fileInput.value.value = ''
    return
  }
  if (file.size > 5 * 1024 * 1024) {
    ElMessage.error('文件大小不能超过 5MB')
    fileInput.value.value = ''
    return
  }
  try {
    const res = await uploadImage(file)
    emit('uploaded', res.data.url)
    ElMessage.success('上传成功')
  } catch {
    // error handled by interceptor
  }
  fileInput.value.value = ''
}
</script>
