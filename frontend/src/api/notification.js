import request from '@/utils/request'

export function getNotifications(params) {
  return request.get('/notifications', { params })
}
export function getUnreadCount() {
  return request.get('/notifications/unread-count')
}
export function markRead(id) {
  return request.put(`/notifications/read/${id}`)
}
export function markAllRead() {
  return request.put('/notifications/read-all')
}
export function createNotification(data) {
  return request.post('/admin/notifications', data)
}
export function getNotificationList(params) {
  return request.get('/admin/notifications', { params })
}
export function deleteNotification(id) {
  return request.delete(`/admin/notifications/${id}`)
}
