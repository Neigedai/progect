<template>
  <el-popover
    placement="bottom-end"
    :width="380"
    trigger="click"
    @show="onPopoverShow"
  >
    <template #reference>
      <el-badge :value="unreadCount || ''" :hidden="!unreadCount" :max="99" class="bell-badge">
        <el-icon :size="20" class="bell-icon"><Bell /></el-icon>
      </el-badge>
    </template>

    <div class="notif-panel">
      <div class="notif-header">
        <span class="notif-title">消息通知</span>
        <el-button v-if="unreadCount > 0" link type="primary" size="small" @click="handleReadAll">全部已读</el-button>
      </div>

      <el-tabs v-model="activeType" class="notif-tabs" @tab-change="fetchList">
        <el-tab-pane label="全部" name="" />
        <el-tab-pane label="业务通知" name="business" />
        <el-tab-pane label="系统公告" name="system" />
      </el-tabs>

      <div class="notif-list" v-loading="loading">
        <div
          v-for="item in list"
          :key="item.id"
          class="notif-item"
          :class="{ 'is-unread': item.isRead === 0 }"
          @click="handleClick(item)"
        >
          <div class="notif-dot" v-if="item.isRead === 0" />
          <div class="notif-body">
            <div class="notif-item-title">
              <el-tag v-if="item.type === 'system'" size="small" type="warning" style="margin-right:6px">公告</el-tag>
              <span>{{ item.title }}</span>
            </div>
            <div class="notif-item-content" v-if="item.content">{{ item.content }}</div>
            <div class="notif-item-time">{{ formatTime(item.createTime) }}</div>
          </div>
        </div>
        <el-empty v-if="!loading && list.length === 0" description="暂无消息" :image-size="60" />
      </div>

      <div class="notif-footer" v-if="total > pageSize">
        <el-pagination
          v-model:current-page="page"
          :page-size="pageSize"
          :total="total"
          layout="prev, next"
          small
          @current-change="fetchList"
        />
      </div>
    </div>
  </el-popover>
</template>

<script setup>
import { ref, onMounted, onUnmounted } from 'vue'
import { Bell } from '@element-plus/icons-vue'
import { getNotifications, getUnreadCount, markRead, markAllRead } from '@/api/notification'

const unreadCount = ref(0)
const list = ref([])
const loading = ref(false)
const page = ref(1)
const pageSize = 8
const total = ref(0)
const activeType = ref('')
let timer = null

const fetchUnread = async () => {
  try {
    const res = await getUnreadCount()
    unreadCount.value = res.data?.count || 0
  } catch {}
}

const fetchList = async () => {
  loading.value = true
  try {
    const res = await getNotifications({
      page: page.value,
      size: pageSize,
      type: activeType.value || undefined
    })
    list.value = res.data?.records || []
    total.value = res.data?.total || 0
  } finally {
    loading.value = false
  }
}

const handleClick = async (item) => {
  if (item.isRead === 0) {
    try {
      await markRead(item.id)
      item.isRead = 1
      unreadCount.value = Math.max(0, unreadCount.value - 1)
    } catch {}
  }
}

const handleReadAll = async () => {
  try {
    await markAllRead()
    list.value.forEach(n => { n.isRead = 1 })
    unreadCount.value = 0
  } catch {}
}

const onPopoverShow = () => {
  page.value = 1
  fetchList()
}

const formatTime = (t) => {
  if (!t) return ''
  const d = new Date(t)
  const now = new Date()
  const diff = now - d
  if (diff < 60000) return '刚刚'
  if (diff < 3600000) return Math.floor(diff / 60000) + '分钟前'
  if (diff < 86400000) return Math.floor(diff / 3600000) + '小时前'
  return `${d.getMonth() + 1}/${d.getDate()} ${String(d.getHours()).padStart(2, '0')}:${String(d.getMinutes()).padStart(2, '0')}`
}

onMounted(() => {
  fetchUnread()
  timer = setInterval(fetchUnread, 30000)
})

onUnmounted(() => {
  if (timer) clearInterval(timer)
})
</script>

<style scoped>
.bell-badge { line-height: 1; }
.bell-icon { cursor: pointer; color: rgba(255, 255, 255, 0.9); transition: color 0.15s; }
.bell-icon:hover { color: #fff; }
.notif-panel { margin: -12px; }
.notif-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 12px 16px 0;
}
.notif-title { font-size: 16px; font-weight: 600; color: var(--text-primary); }
.notif-tabs { padding: 0 16px; }
.notif-tabs :deep(.el-tabs__header) { margin-bottom: 8px; }
.notif-list {
  max-height: 360px;
  overflow-y: auto;
  padding: 0 8px 8px;
}
.notif-item {
  display: flex;
  gap: 10px;
  padding: 10px 8px;
  border-radius: var(--radius-sm);
  cursor: pointer;
  transition: background 0.15s;
  position: relative;
}
.notif-item:hover { background: var(--primary-bg); }
.notif-item.is-unread .notif-item-title { font-weight: 600; }
.notif-dot {
  width: 8px;
  height: 8px;
  border-radius: 50%;
  background: var(--primary);
  flex-shrink: 0;
  margin-top: 6px;
}
.notif-body { flex: 1; min-width: 0; }
.notif-item-title {
  font-size: 14px;
  color: var(--text-primary);
  display: flex;
  align-items: center;
  line-height: 1.4;
}
.notif-item-content {
  font-size: 12px;
  color: var(--text-secondary);
  margin-top: 4px;
  line-height: 1.5;
  overflow: hidden;
  text-overflow: ellipsis;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
}
.notif-item-time {
  font-size: 11px;
  color: var(--text-placeholder);
  margin-top: 4px;
}
.notif-footer {
  text-align: center;
  padding: 8px 16px 12px;
  border-top: 1px solid var(--border-light);
}
</style>
