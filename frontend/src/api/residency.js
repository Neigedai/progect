import request from '@/utils/request'

export function submitResidencyApplication(data) {
  return request.post('/residency/applications', data)
}

export function getMyApplications(params) {
  return request.get('/residency/applications/my', { params })
}

export function getResidencyApplications(params) {
  return request.get('/admin/residency/applications', { params })
}

export function getResidencyApplicationDetail(id) {
  return request.get(`/admin/residency/applications/${id}`)
}

export function updateResidencyApplication(id, data) {
  return request.put(`/admin/residency/applications/${id}`, data)
}

export function approveResidencyApplication(id, data) {
  return request.patch(`/admin/residency/applications/${id}/approve`, data)
}
