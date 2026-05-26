const { submitResidency, getMe, getMyResidency } = require('../../utils/api')
const { upload } = require('../../utils/request')

Page({
  data: {
    industries: ['信息技术', '生物医药', '智能制造', '新材料', '新能源', '现代服务', '文化创意', '其他'],
    industryIdx: -1,
    form: { legalPersonName: '', legalPersonPhone: '', legalPersonIdFront: '', legalPersonIdBack: '', area: '', industryType: '', expectedEntryDate: '', additionalInfo: '' },
    myList: [],
    showForm: false
  },

  async onLoad() {
    try {
      const res = await getMe()
      if (res && res.user) {
        const u = res.user
        this.setData({
          ['form.legalPersonName']: u.nickname || u.username || '',
          ['form.legalPersonPhone']: u.phone || ''
        })
      }
    } catch (e) { /* */ }
    await this.fetchMyList()
    this.setData({ showForm: this.data.myList.length === 0 })
  },

  async fetchMyList() {
    try {
      const res = await getMyResidency()
      this.setData({ myList: res.records || [] })
    } catch (e) { /* */ }
  },

  onInput(e) {
    const field = e.currentTarget.dataset.field
    this.setData({ ['form.' + field]: e.detail.value })
  },

  onIndustryChange(e) {
    const idx = e.detail.value
    this.setData({ industryIdx: idx, ['form.industryType']: this.data.industries[idx] })
  },

  onDateChange(e) {
    this.setData({ ['form.expectedEntryDate']: e.detail.value })
  },

  onNewApply() {
    this.setData({ showForm: true })
  },

  chooseIdFront() {
    wx.chooseImage({
      count: 1,
      sizeType: ['compressed'],
      sourceType: ['album', 'camera'],
      success: res => {
        const filePath = res.tempFilePaths[0]
        wx.showLoading({ title: '上传中...' })
        upload('/admin/upload/image', filePath)
          .then(result => {
            this.setData({ ['form.legalPersonIdFront']: result.data.url })
          })
          .catch(() => wx.showToast({ title: '上传失败', icon: 'none' }))
          .finally(() => wx.hideLoading())
      }
    })
  },

  chooseIdBack() {
    wx.chooseImage({
      count: 1,
      sizeType: ['compressed'],
      sourceType: ['album', 'camera'],
      success: res => {
        const filePath = res.tempFilePaths[0]
        wx.showLoading({ title: '上传中...' })
        upload('/admin/upload/image', filePath)
          .then(result => {
            this.setData({ ['form.legalPersonIdBack']: result.data.url })
          })
          .catch(() => wx.showToast({ title: '上传失败', icon: 'none' }))
          .finally(() => wx.hideLoading())
      }
    })
  },

  async handleSubmit() {
    const f = this.data.form
    if (!f.legalPersonName) { wx.showToast({ title: '请输入法人姓名', icon: 'none' }); return }
    if (!/^1[3-9]\d{9}$/.test(f.legalPersonPhone)) { wx.showToast({ title: '手机号格式不正确', icon: 'none' }); return }
    if (!f.area) { wx.showToast({ title: '请输入面积需求', icon: 'none' }); return }
    if (!f.industryType) { wx.showToast({ title: '请选择行业类型', icon: 'none' }); return }
    try {
      await submitResidency(f)
      wx.showToast({ title: '提交成功', icon: 'success' })
      this.setData({
        industryIdx: -1,
        form: {
          legalPersonName: this.data.form.legalPersonName,
          legalPersonPhone: this.data.form.legalPersonPhone,
          legalPersonIdFront: '', legalPersonIdBack: '',
          area: '', industryType: '', expectedEntryDate: '', additionalInfo: ''
        }
      })
      this.fetchMyList()
    } catch (e) { /* */ }
  }
})
