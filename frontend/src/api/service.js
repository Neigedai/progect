import request from '@/utils/request'

export function getCategories() {
  return request.get('/services/categories')
}

export function getServiceItems(params) {
  return request.get('/services/items', { params })
}

export function getServiceDetail(id) {
  return request.get(`/services/items/${id}`)
}
