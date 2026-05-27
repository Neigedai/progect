<template>
  <span class="upload-btn">
    <input ref="fileInput" type="file" :accept="accept" style="display:none" @change="handleFile" />
    <el-button size="small" :icon="Upload" @click="fileInput.click()">{{ text || '上传' }}</el-button>
  </span>
</template>

<script setup>
import { ref } from 'vue'
import { ElMessage } from 'element-plus'
import { Upload } from '@element-plus/icons-vue'
import { uploadImage, uploadVideo } from '@/api/admin'

const props = defineProps({
  modelValue: { type: String, default: '' },
  text: { type: String, default: '' },
  accept: { type: String, default: 'image/*' }
})
const emit = defineEmits(['update:modelValue', 'uploaded'])

const fileInput = ref(null)

const videoExts = ['mp4', 'webm', 'mov', 'avi', 'mkv', 'flv']
const imageExts = ['jpg', 'jpeg', 'png', 'gif', 'webp', 'bmp', 'svg']

const handleFile = async (e) => {
  const file = e.target.files[0]
  if (!file) return
  const ext = file.name.split('.').pop().toLowerCase()
  const isVideo = props.accept.startsWith('video')
  const validType = isVideo
    ? (file.type.startsWith('video/') || videoExts.includes(ext))
    : (file.type.startsWith('image/') || imageExts.includes(ext))
  if (!validType) {
    ElMessage.error(isVideo ? '仅支持视频文件' : '仅支持图片文件')
    fileInput.value.value = ''
    return
  }
  const maxSize = isVideo ? 100 * 1024 * 1024 : 5 * 1024 * 1024
  if (file.size > maxSize) {
    ElMessage.error('文件大小不能超过 ' + (isVideo ? '100MB' : '5MB'))
    fileInput.value.value = ''
    return
  }
  try {
    const res = isVideo ? await uploadVideo(file) : await uploadImage(file)
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
