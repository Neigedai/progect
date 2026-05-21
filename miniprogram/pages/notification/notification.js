const { getNotifications, markRead, markAllRead } = require('../../utils/api')

Page({
  data: { list: [], page: 1, hasMore: false },

  onLoad() { this.fetchList() },

  async fetchList() {
    try {
      const res = await getNotifications({ page: this.data.page, size: 20 })
      const records = res.records || []
      this.setData({
        list: this.data.page === 1 ? records : [...this.data.list, ...records],
        hasMore: records.length === 20
      })
    } catch (e) { /* */ }
  },

  async handleRead(e) {
    const n = e.currentTarget.dataset.item
    if (!n.isRead) {
      try { await markRead(n.id) } catch (e) { /* */ }
      const list = this.data.list.map(i => i.id === n.id ? { ...i, isRead: 1 } : i)
      this.setData({ list })
    }
  },

  async handleMarkAll() {
    try {
      await markAllRead()
      this.setData({ list: this.data.list.map(i => ({ ...i, isRead: 1 })) })
      wx.showToast({ title: '已全部已读', icon: 'success' })
    } catch (e) { /* */ }
  },

  loadMore() { this.setData({ page: this.data.page + 1 }); this.fetchList() },

  formatTime(t) {
    if (!t) return ''
    const d = new Date(t), now = new Date(), diff = now - d
    if (diff < 60000) return '刚刚'
    if (diff < 3600000) return Math.floor(diff / 60000) + '分钟前'
    if (diff < 86400000) return Math.floor(diff / 3600000) + '小时前'
    return (d.getMonth() + 1) + '/' + d.getDate() + ' ' + String(d.getHours()).padStart(2, '0') + ':' + String(d.getMinutes()).padStart(2, '0')
  }
})
