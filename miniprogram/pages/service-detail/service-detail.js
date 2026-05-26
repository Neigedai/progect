const { getServiceDetail, getFloatingMenuItems, submitServiceApplication, getMe } = require('../../utils/api')

Page({
  data: {
    detail: null,
    showQr: false,
    qrImage: '',
    applyForm: { contactName: '', contactPhone: '' }
  },

  onLoad(options) {
    if (options.id) {
      getServiceDetail(options.id).then(d => this.setData({ detail: d })).catch(() => {})
    }
  },

  async handleApply() {
    try {
      const res = await getMe()
      const user = res && res.user ? res.user : {}
      this.setData({
        applyForm: {
          contactName: user.nickname || user.username || '',
          contactPhone: user.phone || ''
        }
      })
    } catch (e) { /* */ }
    try {
      const items = await getFloatingMenuItems()
      const banli = (items || []).find(i => i.title === '立即办理')
      this.setData({ qrImage: banli ? banli.imageUrl : '', showQr: true })
    } catch (e) {
      this.setData({ showQr: true })
    }
  },

  onApplyInput(e) {
    const field = e.currentTarget.dataset.field
    this.setData({ ['applyForm.' + field]: e.detail.value })
  },

  async handleApplySubmit() {
    const f = this.data.applyForm
    if (!f.contactName) { wx.showToast({ title: '请输入联系人', icon: 'none' }); return }
    if (!/^1[3-9]\d{9}$/.test(f.contactPhone)) { wx.showToast({ title: '手机号格式不正确', icon: 'none' }); return }
    if (!this.data.detail) return
    try {
      await submitServiceApplication({
        serviceId: this.data.detail.id,
        contactName: f.contactName,
        contactPhone: f.contactPhone
      })
      wx.showToast({ title: '申请已提交', icon: 'success' })
      this.setData({ showQr: false })
    } catch (e) { /* */ }
  },

  closeQr() { this.setData({ showQr: false }) }
})
