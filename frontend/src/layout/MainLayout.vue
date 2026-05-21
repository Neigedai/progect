<template>
  <div class="layout">
    <!-- Top utility bar - white -->
    <div class="top-bar">
      <div class="top-bar-inner">
        <div class="top-left">
          <div class="logo-icon"><el-icon :size="22"><OfficeBuilding /></el-icon></div>
          <span class="logo-text">园区服务运营系统</span>
        </div>
        <div class="top-right">
          <template v-if="auth.state.user">
            <NotificationBell />
            <span class="header-user">
              <el-icon><UserFilled /></el-icon>
              <span>{{ auth.state.user.nickname || auth.state.user.username || '管理员' }}</span>
            </span>
            <el-button text type="danger" @click="logout">退出登录</el-button>
          </template>
          <span v-else class="login-hint" @click="router.push('/login')">未登录，请先登录</span>
        </div>
      </div>
    </div>

    <!-- Nav bar - blue -->
    <header class="nav-bar">
      <div class="nav-bar-inner">
        <el-menu
          :default-active="activeMenu"
          mode="horizontal"
          router
          :popper-offset="12"
          popper-class="nav-dropdown"
          class="nav-menu"
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
      </div>
    </header>

    <!-- Full-width banner for park overview -->
    <div class="layout-banner" v-if="route.path === '/' || route.path.startsWith('/park-overview')">
      <el-carousel :interval="5000" height="400px" arrow="hover">
        <el-carousel-item v-for="b in layoutBanners" :key="b.id">
          <el-image :src="b.imageUrl" fit="cover" class="banner-img" />
        </el-carousel-item>
      </el-carousel>
    </div>

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
import { computed, onMounted, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { OfficeBuilding, Grid, Setting, Document, EditPen, UserFilled, Stamp } from '@element-plus/icons-vue'
import { useAuth } from '@/utils/auth'
import { getActiveBanners } from '@/api/public'
import permission from '@/directives/permission'
import NotificationBell from '@/components/NotificationBell.vue'
import FloatingMenu from '@/components/FloatingMenu.vue'

const route = useRoute()
const router = useRouter()
const auth = useAuth()
const layoutBanners = ref([])

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
  if (localStorage.getItem('token')) {
    await auth.loadAuth()
    permission.setAuth(auth)
  }
  try { const res = await getActiveBanners(); layoutBanners.value = res.data || [] } catch {}
})

const logout = () => {
  localStorage.removeItem('token')
  auth.clearAuth()
  router.push('/park-overview')
}
</script>

<style scoped>
.layout {
  display: flex;
  flex-direction: column;
  min-height: 100vh;
  overflow-x: hidden;
}

/* ====== Top Utility Bar ====== */
.top-bar {
  background: #fff;
  border-bottom: 1px solid #E2E8F0;
  flex-shrink: 0;
}
.top-bar-inner {
  max-width: 1280px;
  margin: 0 auto;
  height: 48px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 32px;
}
.top-left {
  display: flex;
  align-items: center;
  gap: 10px;
}
.logo-icon {
  width: 32px;
  height: 32px;
  border-radius: 6px;
  background: var(--primary);
  display: flex;
  align-items: center;
  justify-content: center;
  color: #fff;
  flex-shrink: 0;
}
.logo-text {
  font-size: 15px;
  font-weight: 600;
  color: #0F172A;
  white-space: nowrap;
}
.top-right {
  display: flex;
  align-items: center;
  gap: 16px;
}
.header-user {
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: 13px;
  color: #475569;
}
.header-user .el-icon { color: #94A3B8; }
.login-hint {
  font-size: 13px;
  color: var(--primary);
  cursor: pointer;
}
.login-hint:hover { color: #3D7AFF; }
.top-right :deep(.el-button) { font-size: 13px; padding: 4px 8px; }

/* ====== Layout Banner ====== */
.layout-banner {
  flex-shrink: 0;
}
.layout-banner :deep(.el-carousel__container) { height: 400px; }
.layout-banner .banner-img { width: 100%; height: 400px; }

/* ====== Navigation Bar ====== */
.nav-bar {
  background: #fff;
  flex-shrink: 0;
  box-shadow: 0 1px 3px rgba(15, 23, 42, 0.06);
  position: sticky;
  top: 0;
  z-index: 10;
  border-bottom: 1px solid var(--border);
}
.nav-bar-inner {
  max-width: 1280px;
  margin: 0 auto;
  padding: 0 32px;
}
.nav-menu {
  border-bottom: none !important;
  height: 52px;
  --el-menu-bg-color: transparent;
  --el-menu-hover-bg-color: var(--primary-light);
  --el-menu-text-color: var(--text-secondary);
  --el-menu-active-color: var(--primary);
}
.nav-menu :deep(.el-menu-item),
.nav-menu :deep(.el-sub-menu__title) {
  height: 52px;
  line-height: 52px;
  border-bottom: 2px solid transparent !important;
  color: var(--text-secondary);
  font-size: 14px;
  font-weight: 500;
  padding: 0 18px;
  transition: color var(--transition-fast), border-color var(--transition-fast);
}
.nav-menu :deep(.el-menu-item:hover),
.nav-menu :deep(.el-sub-menu__title:hover) {
  color: var(--primary);
  background: var(--primary-light);
}
.nav-menu :deep(.el-menu-item.is-active) {
  color: var(--primary);
  font-weight: 600;
  background: transparent;
  border-bottom-color: var(--primary) !important;
}

.main-content {
  flex: 1;
  padding: 32px 32px 48px;
  background: var(--bg-page);
  overflow-y: auto;
  max-width: 1280px;
  margin: 0 auto;
  width: 100%;
}

@media (max-width: 768px) {
  .top-bar-inner, .nav-bar-inner { padding: 0 16px; }
  .logo-text { display: none; }
  .nav-menu :deep(.el-menu-item span),
  .nav-menu :deep(.el-sub-menu__title span) { display: none; }
  .main-content { padding: 16px; }
  .layout-banner :deep(.el-carousel__container) { height: 200px; }
  .layout-banner .banner-img { height: 200px; }
}
</style>

<style>
.nav-dropdown {
  margin-top: 4px !important;
  border-radius: var(--radius-md) !important;
  box-shadow: var(--shadow-lg) !important;
  border: 1px solid var(--border) !important;
}
.nav-dropdown .el-menu-item {
  font-size: 13px;
  border-radius: var(--radius-sm);
  margin: 2px 6px;
}
.nav-dropdown .el-menu-item:hover {
  background: var(--primary-light) !important;
  color: var(--primary) !important;
}
</style>
