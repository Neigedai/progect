import request from '@/utils/request'

export function submitEnterpriseAuth(data) {
  return request.post('/enterprise/submit', data)
}

export function getEnterpriseAuthStatus() {
  return request.get('/enterprise/status')
}

export function getEnterpriseAuthList(params) {
  return request.get('/admin/enterprise', { params })
}

export function getEnterpriseAuthDetail(id) {
  return request.get(`/admin/enterprise/${id}`)
}

export function reviewEnterpriseAuth(id, data) {
  return request.patch(`/admin/enterprise/${id}/review`, data)
}

export function updateEnterpriseTag(id, tag) {
  return request.patch(`/admin/enterprise/${id}/tag`, { tag })
}
