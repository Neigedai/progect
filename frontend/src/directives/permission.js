const map = {}

function check(el, binding) {
  const { state } = map._auth || {}
  if (!state || !state.loaded) {
    el.style.display = 'none'
    return
  }
  if (!state.permissions.includes(binding.value)) {
    el.style.display = 'none'
  } else {
    el.style.display = ''
  }
}

export default {
  beforeMount(el, binding) {
    check(el, binding)
  },
  updated(el, binding) {
    check(el, binding)
  },
  setAuth(authModule) {
    map._auth = authModule
  }
}
