import request from '@/utils/request'

export function getParkList() {
  return request.get('/parks')
}

export function getParkOverview(parkId) {
  return request.get(`/parks/${parkId}/overview`)
}
