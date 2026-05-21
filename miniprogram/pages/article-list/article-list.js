const { getArticles } = require('../../utils/api')

Page({
  data: {
    activeType: '',
    keyword: '',
    list: [],
    page: 1,
    hasMore: false
  },

  onLoad() {
    if (!wx.getStorageSync('token')) { wx.reLaunch({ url: '/pages/login/login' }); return }
    this.fetchList()
  },

  switchType(e) {
    this.setData({ activeType: e.currentTarget.dataset.type, page: 1, list: [] })
    this.fetchList()
  },

  onSearchInput(e) { this.setData({ keyword: e.detail.value }) },

  doSearch() { this.setData({ page: 1, list: [] }); this.fetchList() },

  async fetchList() {
    try {
      const { activeType, keyword, page } = this.data
      const params = { page, size: 10 }
      if (activeType) params.type = activeType
      if (keyword) params.keyword = keyword
      const res = await getArticles(params)
      const records = res.records || []
      this.setData({
        list: page === 1 ? records : [...this.data.list, ...records],
        hasMore: records.length === 10
      })
    } catch (e) { /* */ }
  },

  loadMore() {
    const page = this.data.page + 1
    this.setData({ page })
    this.fetchList()
  },

  goDetail(e) {
    wx.navigateTo({ url: '/pages/article-detail/article-detail?id=' + e.currentTarget.dataset.id })
  }
})
