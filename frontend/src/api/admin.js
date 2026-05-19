import request from '@/utils/request'

export function getParkList(params) {
  return request({ url: '/admin/parks', method: 'get', params })
}

export function getParkDetail(id) {
  return request({ url: `/admin/parks/${id}`, method: 'get' })
}

export function createPark(data) {
  return request({ url: '/admin/parks', method: 'post', data })
}

export function updatePark(id, data) {
  return request({ url: `/admin/parks/${id}`, method: 'put', data })
}

export function deletePark(id) {
  return request({ url: `/admin/parks/${id}`, method: 'delete' })
}

export function updateParkStatus(id, status) {
  return request({ url: `/admin/parks/${id}/status`, method: 'patch', params: { status } })
}

export function getArticleList(params) {
  return request({ url: '/admin/articles', method: 'get', params })
}

export function getArticleDetail(id) {
  return request({ url: `/admin/articles/${id}`, method: 'get' })
}

export function createArticle(data) {
  return request({ url: '/admin/articles', method: 'post', data })
}

export function updateArticle(id, data) {
  return request({ url: `/admin/articles/${id}`, method: 'put', data })
}

export function deleteArticle(id) {
  return request({ url: `/admin/articles/${id}`, method: 'delete' })
}

export function updateArticleStatus(id, status) {
  return request({ url: `/admin/articles/${id}/status`, method: 'patch', params: { status } })
}

export function getBannerList(params) {
  return request({ url: '/admin/banners', method: 'get', params })
}

export function getBannerDetail(id) {
  return request({ url: `/admin/banners/${id}`, method: 'get' })
}

export function createBanner(data) {
  return request({ url: '/admin/banners', method: 'post', data })
}

export function updateBanner(id, data) {
  return request({ url: `/admin/banners/${id}`, method: 'put', data })
}

export function deleteBanner(id) {
  return request({ url: `/admin/banners/${id}`, method: 'delete' })
}

export function updateBannerStatus(id, status) {
  return request({ url: `/admin/banners/${id}/status`, method: 'patch', params: { status } })
}

export function getFacilities(parkId) {
  return request({ url: `/admin/parks/${parkId}/facilities`, method: 'get' })
}

export function createFacility(parkId, data) {
  return request({ url: `/admin/parks/${parkId}/facilities`, method: 'post', data })
}

export function updateFacility(parkId, id, data) {
  return request({ url: `/admin/parks/${parkId}/facilities/${id}`, method: 'put', data })
}

export function deleteFacility(parkId, id) {
  return request({ url: `/admin/parks/${parkId}/facilities/${id}`, method: 'delete' })
}

export function getHonors(parkId) {
  return request({ url: `/admin/parks/${parkId}/honors`, method: 'get' })
}

export function createHonor(parkId, data) {
  return request({ url: `/admin/parks/${parkId}/honors`, method: 'post', data })
}

export function updateHonor(parkId, id, data) {
  return request({ url: `/admin/parks/${parkId}/honors/${id}`, method: 'put', data })
}

export function deleteHonor(parkId, id) {
  return request({ url: `/admin/parks/${parkId}/honors/${id}`, method: 'delete' })
}

export function uploadImage(file) {
  const formData = new FormData()
  formData.append('file', file)
  return request({ url: '/admin/upload/image', method: 'post', data: formData, headers: { 'Content-Type': 'multipart/form-data' } })
}
