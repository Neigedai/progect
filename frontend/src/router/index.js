import { createRouter, createWebHistory } from 'vue-router'

const routes = [
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/views/Login.vue')
  },
  {
    path: '/dashboard',
    name: 'Dashboard',
    component: () => import('@/views/Dashboard.vue')
  },
  {
    path: '/survey/:code',
    name: 'SurveyFill',
    component: () => import('@/views/SurveyFill.vue')
  },
  {
    path: '/',
    component: () => import('@/layout/MainLayout.vue'),
    children: [
      {
        path: '',
        redirect: '/park-overview'
      },
      {
        path: 'park-overview',
        name: 'ParkOverview',
        component: () => import('@/views/ParkOverview.vue')
      },
      {
        path: 'services',
        name: 'ServiceList',
        component: () => import('@/views/ServiceList.vue')
      },
      {
        path: 'services/:id',
        name: 'ServiceDetail',
        component: () => import('@/views/ServiceDetail.vue')
      },
      {
        path: 'apply/:serviceId',
        name: 'ApplyPlaceholder',
        component: () => import('@/views/ApplyPlaceholder.vue')
      },
      {
        path: 'apply/residency',
        name: 'ResidencyApply',
        component: () => import('@/views/ResidencyApply.vue')
      },
      {
        path: 'enterprise-auth',
        name: 'EnterpriseAuth',
        component: () => import('@/views/EnterpriseAuth.vue')
      },
      {
        path: 'admin/parks',
        name: 'ParkManage',
        component: () => import('@/views/ParkManage.vue')
      },
      {
        path: 'admin/articles',
        name: 'ArticleManage',
        component: () => import('@/views/ArticleManage.vue')
      },
      {
        path: 'admin/banners',
        name: 'BannerManage',
        component: () => import('@/views/BannerManage.vue')
      },
      {
        path: 'admin/services',
        name: 'ServiceManage',
        component: () => import('@/views/ServiceManage.vue')
      },
      {
        path: 'admin/service-applications',
        name: 'ServiceApplicationManage',
        component: () => import('@/views/ServiceApplicationManage.vue')
      },
      {
        path: 'admin/floating-menu',
        name: 'FloatingMenuManage',
        component: () => import('@/views/FloatingMenuManage.vue')
      },
      {
        path: 'admin/residency',
        name: 'ResidencyManage',
        component: () => import('@/views/ResidencyManage.vue')
      },
      {
        path: 'admin/roles',
        name: 'RoleManage',
        component: () => import('@/views/RoleManage.vue')
      },
      {
        path: 'admin/enterprise',
        name: 'EnterpriseAuthManage',
        component: () => import('@/views/EnterpriseAuthManage.vue')
      },
      {
        path: 'admin/notifications',
        name: 'NotificationManage',
        component: () => import('@/views/NotificationManage.vue')
      },
      {
        path: 'admin/users',
        name: 'UserManage',
        component: () => import('@/views/UserManage.vue')
      },
      {
        path: 'admin/survey',
        name: 'SurveyManage',
        component: () => import('@/views/SurveyManage.vue')
      },
      {
        path: 'articles',
        name: 'ArticleList',
        component: () => import('@/views/ArticleList.vue')
      },
      {
        path: 'articles/:id',
        name: 'ArticleDetail',
        component: () => import('@/views/ArticleDetail.vue')
      }
    ]
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

const protectedPaths = ['/apply/', '/enterprise-auth', '/admin/', '/dashboard']

router.beforeEach((to, from, next) => {
  const token = localStorage.getItem('token')
  if (to.path !== '/login' && !token && protectedPaths.some(p => to.path.startsWith(p))) {
    next('/login')
  } else {
    next()
  }
})

export default router
