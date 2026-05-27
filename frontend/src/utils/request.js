import axios from 'axios'
import { ElMessage } from 'element-plus'

const request = axios.create({
  baseURL: '/api',
  timeout: 10000
})

request.interceptors.request.use(config => {
  const token = localStorage.getItem('token')
  if (token) {
    config.headers.Authorization = `Bearer ${token}`
  }
  return config
})

request.interceptors.response.use(
  response => {
    const res = response.data
    if (res.code !== 200) {
      ElMessage.error(res.message || '请求失败')
      return Promise.reject(new Error(res.message))
    }
    return res
  },
  error => {
    const silent = error.config?.silent
    const status = error.response?.status
    if ((status === 401 || status === 403) && window.location.pathname !== '/login') {
      if (!silent) ElMessage.error('请先登录')
      localStorage.removeItem('token')
      window.location.href = '/login'
      return Promise.reject(error)
    }
    if (!silent) {
      const msg = error.response?.data?.message || '网络错误'
      ElMessage.error(msg)
    }
    return Promise.reject(error)
  }
)

export default request
