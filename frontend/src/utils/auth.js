import { reactive, ref } from 'vue'

const state = reactive({
  user: null,
  roles: [],
  permissions: [],
  loaded: false
})

export function useAuth() {
  const hasPermission = (code) => {
    if (!state.loaded) return false
    return state.permissions.includes(code)
  }

  const hasAnyPermission = (...codes) => codes.some(c => hasPermission(c))

  const hasRole = (roleCode) => state.roles.some(r => r.roleCode === roleCode)

  const loadAuth = async () => {
    try {
      const { default: request } = await import('@/utils/request')
      const res = await request.get('/auth/me')
      if (res.code === 200) {
        state.user = res.data.user
        state.roles = res.data.roles || []
        state.permissions = res.data.permissions || []
        state.loaded = true
      }
    } catch {
      state.loaded = false
    }
  }

  const clearAuth = () => {
    state.user = null
    state.roles = []
    state.permissions = []
    state.loaded = false
  }

  return { state, hasPermission, hasAnyPermission, hasRole, loadAuth, clearAuth }
}
