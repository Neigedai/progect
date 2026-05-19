<template>
  <div class="layout">
    <header class="header">
      <div class="header-logo">
        <div class="logo-icon"><el-icon :size="22"><OfficeBuilding /></el-icon></div>
        <span class="logo-text">园区服务运营系统</span>
      </div>
      <el-menu
        :default-active="activeMenu"
        mode="horizontal"
        router
        :popper-offset="12"
        popper-class="nav-dropdown"
        class="header-menu"
      >
        <el-menu-item index="/">
          <el-icon><HomeFilled /></el-icon>
          <span>首页</span>
        </el-menu-item>
        <el-menu-item index="/park-overview">
          <el-icon><OfficeBuilding /></el-icon>
          <span>园区概况</span>
        </el-menu-item>
        <el-menu-item index="/services">
          <el-icon><Grid /></el-icon>
          <span>企业服务大厅</span>
        </el-menu-item>
        <el-menu-item index="/articles">
          <el-icon><Document /></el-icon>
          <span>资讯与政策</span>
        </el-menu-item>
        <el-sub-menu index="/admin">
          <template #title>
            <el-icon><Setting /></el-icon>
            <span>内容管理</span>
          </template>
          <el-menu-item index="/admin/parks">园区管理</el-menu-item>
          <el-menu-item index="/admin/articles">资讯管理</el-menu-item>
          <el-menu-item index="/admin/banners">轮播图管理</el-menu-item>
          <el-menu-item index="/admin/services">服务管理</el-menu-item>
          <el-menu-item index="/admin/users">用户管理</el-menu-item>
        </el-sub-menu>
      </el-menu>
      <div class="header-spacer"></div>
      <div class="header-right">
        <span class="header-user">
          <el-icon><UserFilled /></el-icon>
          <span>管理员</span>
        </span>
        <el-button text type="danger" @click="logout">退出登录</el-button>
      </div>
    </header>
    <main class="main-content">
      <router-view />
    </main>
  </div>
</template>

<script setup>
import { computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { OfficeBuilding, Grid, Setting, Document, HomeFilled, UserFilled } from '@element-plus/icons-vue'

const route = useRoute()
const router = useRouter()

const activeMenu = computed(() => {
  const p = route.path
  if (p === '/') return '/'
  if (p.startsWith('/park-overview')) return '/park-overview'
  if (p.startsWith('/services') || p.startsWith('/apply')) return '/services'
  if (p.startsWith('/admin')) return '/admin'
  if (p.startsWith('/articles')) return '/articles'
  return '/'
})

const logout = () => {
  localStorage.removeItem('token')
  router.push('/login')
}
</script>

<style scoped>
.layout {
  display: flex;
  flex-direction: column;
  min-height: 100vh;
}
.header {
  height: var(--header-height);
  background: #fff;
  border-bottom: 1px solid var(--border);
  display: flex;
  align-items: center;
  padding: 0 24px;
  flex-shrink: 0;
  box-shadow: var(--shadow-sm);
  z-index: 10;
  overflow: hidden;
}
.header-logo {
  display: flex;
  align-items: center;
  gap: 10px;
  margin-right: 24px;
  flex-shrink: 0;
}
.logo-icon {
  width: 34px;
  height: 34px;
  border-radius: 8px;
  background: var(--primary);
  display: flex;
  align-items: center;
  justify-content: center;
  color: #fff;
  flex-shrink: 0;
}
.logo-text {
  font-size: 16px;
  font-weight: 700;
  color: var(--text-primary);
  white-space: nowrap;
}
.header-menu {
  flex: 0 0 auto;
  border-bottom: none !important;
  height: var(--header-height);
}
.header-menu :deep(.el-menu-item),
.header-menu :deep(.el-sub-menu__title) {
  height: var(--header-height);
  line-height: var(--header-height);
  border-bottom: none !important;
}
.header-spacer {
  flex: 1;
}
.header-right {
  display: flex;
  align-items: center;
  gap: 16px;
  flex-shrink: 0;
}
.header-user {
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: 13px;
  color: var(--text-regular);
}
.main-content {
  flex: 1;
  padding: 24px;
  background: var(--bg-page);
  overflow-y: auto;
}
@media (max-width: 768px) {
  .logo-text { display: none; }
  .header { padding: 0 12px; }
  .header-menu :deep(.el-menu-item span),
  .header-menu :deep(.el-sub-menu__title span) { display: none; }
  .main-content { padding: 16px; }
}
</style>

<style>
.nav-dropdown {
  margin-top: 4px !important;
}
</style>
