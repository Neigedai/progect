const { getCategories, getServiceItems } = require('../../utils/api')

Page({
  data: {
    categories: [],
    activeCategory: '',
    keyword: '',
    list: [],
    page: 1,
    hasMore: false
  },

  async onLoad() {
    if (!wx.getStorageSync('token')) { wx.reLaunch({ url: '/pages/login/login' }); return }
    try { this.setData({ categories: await getCategories() }) } catch (e) { /* */ }
    this.fetchList()
  },

  switchCategory(e) {
    const id = e.currentTarget.dataset.id
    this.setData({ activeCategory: id, page: 1, list: [] })
    this.fetchList()
  },

  onSearchInput(e) { this.setData({ keyword: e.detail.value }) },

  doSearch() {
    this.setData({ page: 1, list: [] })
    this.fetchList()
  },

  async fetchList() {
    try {
      const { activeCategory, keyword, page } = this.data
      const params = { page, size: 10 }
      if (activeCategory) params.categoryId = activeCategory
      if (keyword) params.keyword = keyword
      const res = await getServiceItems(params)
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
    wx.navigateTo({ url: '/pages/service-detail/service-detail?id=' + e.currentTarget.dataset.id })
  }
})
