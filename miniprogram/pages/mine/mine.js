const { getMe } = require('../../utils/api')
const { getUnreadCount } = require('../../utils/api')

Page({
  data: {
    user: null,
    unreadCount: 0,
    initial: '?',
    displayName: '未登录'
  },

  async onShow() {
    if (!wx.getStorageSync('token')) { wx.reLaunch({ url: '/pages/login/login' }); return }
    try {
      const data = await getMe()
      const u = data.user || data
      this.setData({
        user: u,
        initial: ((u && (u.nickname || u.username)) || '?')[0].toUpperCase(),
        displayName: (u && (u.nickname || u.username)) || '未登录'
      })
    } catch (e) { /* */ }
    try {
      const res = await getUnreadCount()
      this.setData({ unreadCount: (res && res.count) || 0 })
    } catch (e) { /* */ }
  },

  goNotifications() { wx.navigateTo({ url: '/pages/notification/notification' }) },
  goEnterprise() { wx.navigateTo({ url: '/pages/enterprise/enterprise' }) },
  goApply() { wx.navigateTo({ url: '/pages/apply/apply' }) },
  goPark() { wx.navigateTo({ url: '/pages/park/park' }) },

  handleAbout() {
    wx.showModal({
      title: '米库产业创新公共服务平台',
      content: '为企业提供一站式园区服务，包括政策申报、财税代办、法律咨询、人力资源、知识产权等全方位支持。',
      showCancel: false
    })
  },

  handleLogout() {
    wx.removeStorageSync('token')
    wx.reLaunch({ url: '/pages/login/login' })
  }
})
