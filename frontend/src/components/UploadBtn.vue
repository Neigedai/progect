<template>
  <span class="upload-btn">
    <input ref="fileInput" type="file" accept="image/*" style="display:none" @change="handleFile" />
    <el-button size="small" :icon="Upload" @click="fileInput.click()">{{ text || '上传' }}</el-button>
  </span>
</template>

<script setup>
import { ref } from 'vue'
import { ElMessage } from 'element-plus'
import { Upload } from '@element-plus/icons-vue'
import { uploadImage } from '@/api/admin'

const props = defineProps({
  modelValue: { type: String, default: '' },
  text: { type: String, default: '' }
})
const emit = defineEmits(['update:modelValue', 'uploaded'])

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
    emit('update:modelValue', res.data.url)
    emit('uploaded', res.data.url)
    ElMessage.success('上传成功')
  } catch {
    ElMessage.error('上传失败')
  }
  fileInput.value.value = ''
}
</script>

<style scoped>
.upload-btn { display: inline-flex; align-items: center; margin-left: 8px; }
</style>
