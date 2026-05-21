const { submitResidency } = require('../../utils/api')

Page({
  data: {
    industries: ['信息技术', '生物医药', '智能制造', '新材料', '新能源', '现代服务', '文化创意', '其他'],
    industryIdx: -1,
    form: { contactName: '', contactPhone: '', area: '', industryType: '', expectedEntryDate: '', additionalInfo: '' }
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

  async handleSubmit() {
    const f = this.data.form
    if (!f.contactName) { wx.showToast({ title: '请输入联系人', icon: 'none' }); return }
    if (!/^1[3-9]\d{9}$/.test(f.contactPhone)) { wx.showToast({ title: '手机号格式不正确', icon: 'none' }); return }
    if (!f.area) { wx.showToast({ title: '请输入面积需求', icon: 'none' }); return }
    if (!f.industryType) { wx.showToast({ title: '请选择行业类型', icon: 'none' }); return }
    try {
      await submitResidency(f)
      wx.showToast({ title: '提交成功', icon: 'success' })
      setTimeout(() => wx.navigateBack(), 1500)
    } catch (e) { /* */ }
  }
})
