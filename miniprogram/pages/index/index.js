const { getBanners, getArticles, getServiceItems } = require('../../utils/api')

Page({
  data: {
    banners: [],
    hotServices: [],
    policyArticles: []
  },

  onLoad() {
    if (!wx.getStorageSync('token')) {
      wx.reLaunch({ url: '/pages/login/login' })
      return
    }
    this.loadData()
  },

  async loadData() {
    try {
      const [b, s, a] = await Promise.all([
        getBanners().catch(() => []),
        getServiceItems({ page: 1, size: 4 }).catch(() => ({ records: [] })),
        getArticles({ page: 1, size: 3, type: 'policy' }).catch(() => ({ records: [] }))
      ])
      this.setData({
        banners: b || [],
        hotServices: (s && s.records) ? s.records : [],
        policyArticles: (a && a.records) ? a.records : []
      })
    } catch (e) { /* */ }
  },

  goPark() { wx.navigateTo({ url: '/pages/park/park' }) },
  goService() { wx.switchTab({ url: '/pages/service-list/service-list' }) },
  goApply() { wx.navigateTo({ url: '/pages/apply/apply' }) },
  goEnterprise() { wx.navigateTo({ url: '/pages/enterprise/enterprise' }) },
  goServiceDetail(e) {
    const id = e.currentTarget.dataset.id
    wx.navigateTo({ url: '/pages/service-detail/service-detail?id=' + id })
  },
  goArticleDetail(e) {
    const id = e.currentTarget.dataset.id
    wx.navigateTo({ url: '/pages/article-detail/article-detail?id=' + id })
  },
  goMoreService() { wx.switchTab({ url: '/pages/service-list/service-list' }) },
  goMoreArticle() { wx.switchTab({ url: '/pages/article-list/article-list' }) }
})
