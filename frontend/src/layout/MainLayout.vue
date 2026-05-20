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
        <el-menu-item index="/apply/residency">
          <el-icon><EditPen /></el-icon>
          <span>入驻申请</span>
        </el-menu-item>
        <el-menu-item index="/enterprise-auth">
          <el-icon><Stamp /></el-icon>
          <span>企业认证</span>
        </el-menu-item>
        <el-sub-menu index="/admin" v-if="hasAnyAdminPermission">
          <template #title>
            <el-icon><Setting /></el-icon>
            <span>内容管理</span>
          </template>
          <el-menu-item index="/admin/parks" v-if="hasPermission('park:view')">园区管理</el-menu-item>
          <el-menu-item index="/admin/articles" v-if="hasPermission('article:view')">资讯管理</el-menu-item>
          <el-menu-item index="/admin/banners" v-if="hasPermission('banner:view')">轮播图管理</el-menu-item>
          <el-menu-item index="/admin/services" v-if="hasPermission('service:view')">服务管理</el-menu-item>
          <el-menu-item index="/admin/users" v-if="hasPermission('user:view')">用户管理</el-menu-item>
          <el-menu-item index="/admin/residency" v-if="hasPermission('residency:view')">入驻审批</el-menu-item>
          <el-menu-item index="/admin/enterprise" v-if="hasPermission('enterprise:view')">企业认证审核</el-menu-item>
          <el-menu-item index="/admin/floating-menu" v-if="hasPermission('floating:view')">悬浮菜单</el-menu-item>
          <el-menu-item index="/admin/notifications" v-if="hasPermission('notification:view')">消息管理</el-menu-item>
          <el-menu-item index="/admin/roles" v-if="hasPermission('role:view')">角色管理</el-menu-item>
        </el-sub-menu>
      </el-menu>
      <div class="header-right">
        <NotificationBell />
        <span class="header-user">
          <el-icon><UserFilled /></el-icon>
          <span>{{ auth.state.user?.nickname || auth.state.user?.username || '管理员' }}</span>
        </span>
        <el-button text type="danger" @click="logout">退出登录</el-button>
      </div>
    </header>
    <main class="main-content">
      <router-view v-slot="{ Component }">
        <transition name="page-fade" mode="out-in">
          <component :is="Component" />
        </transition>
      </router-view>
    </main>
    <FloatingMenu />
  </div>
</template>

<script setup>
import { computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { OfficeBuilding, Grid, Setting, Document, EditPen, UserFilled, Stamp } from '@element-plus/icons-vue'
import { useAuth } from '@/utils/auth'
import permission from '@/directives/permission'
import NotificationBell from '@/components/NotificationBell.vue'
import FloatingMenu from '@/components/FloatingMenu.vue'

const route = useRoute()
const router = useRouter()
const auth = useAuth()

permission.setAuth(auth)

const hasPermission = (code) => auth.hasPermission(code)

const hasAnyAdminPermission = computed(() => {
  const p = auth.state.permissions
  return p.some(c => c.startsWith('park:') || c.startsWith('article:') || c.startsWith('banner:') ||
    c.startsWith('service:') || c.startsWith('user:') || c.startsWith('residency:') ||
    c.startsWith('floating:') || c.startsWith('role:') || c.startsWith('enterprise:') || c.startsWith('notification:'))
})

const activeMenu = computed(() => {
  const p = route.path
  if (p === '/' || p.startsWith('/park-overview')) return '/park-overview'
  if (p.startsWith('/apply/residency')) return '/apply/residency'
  if (p.startsWith('/enterprise-auth')) return '/enterprise-auth'
  if (p.startsWith('/services') || p.startsWith('/apply')) return '/services'
  if (p.startsWith('/admin')) return '/admin'
  if (p.startsWith('/articles')) return '/articles'
  return '/park-overview'
})

onMounted(async () => {
  await auth.loadAuth()
  permission.setAuth(auth)
})

const logout = () => {
  localStorage.removeItem('token')
  auth.clearAuth()
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
  background: linear-gradient(135deg, #0E42D2 0%, #165DFF 60%, #4080FF 100%);
  display: flex;
  align-items: center;
  padding: 0 24px;
  flex-shrink: 0;
  box-shadow: 0 2px 8px rgba(14, 66, 210, 0.25);
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
  background: rgba(255, 255, 255, 0.2);
  backdrop-filter: blur(4px);
  display: flex;
  align-items: center;
  justify-content: center;
  color: #fff;
  flex-shrink: 0;
}
.logo-text {
  font-size: 16px;
  font-weight: 700;
  color: #fff;
  white-space: nowrap;
}
.header-menu {
  flex: 1;
  min-width: 0;
  border-bottom: none !important;
  height: var(--header-height);
  --el-menu-bg-color: transparent;
  --el-menu-hover-bg-color: rgba(255, 255, 255, 0.1);
  --el-menu-text-color: rgba(255, 255, 255, 0.85);
  --el-menu-active-color: #fff;
}
.header-menu :deep(.el-menu-item),
.header-menu :deep(.el-sub-menu__title) {
  height: var(--header-height);
  line-height: var(--header-height);
  border-bottom: none !important;
  color: rgba(255, 255, 255, 0.85);
}
.header-menu :deep(.el-menu-item:hover),
.header-menu :deep(.el-sub-menu__title:hover) {
  color: #fff;
  background: rgba(255, 255, 255, 0.1);
}
.header-menu :deep(.el-menu-item.is-active) {
  color: #fff;
  border-bottom: 2px solid #fff !important;
}
.header-menu :deep(.el-sub-menu.is-active .el-sub-menu__title) {
  color: #fff;
  border-bottom: 2px solid #fff !important;
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
  color: rgba(255, 255, 255, 0.9);
}
.header-user .el-icon { color: rgba(255, 255, 255, 0.9); }
.header-right :deep(.el-button) { color: rgba(255, 255, 255, 0.8); }
.header-right :deep(.el-button:hover) { color: #fff; }
.main-content {
  flex: 1;
  padding: 28px;
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
