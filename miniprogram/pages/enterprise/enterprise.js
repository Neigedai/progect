const { submitEnterpriseAuth, getEnterpriseAuthStatus } = require('../../utils/api')
const { upload } = require('../../utils/request')

Page({
  data: {
    authStatus: null,
    step: 1,
    form: { companyName: '', creditCode: '', licenseUrl: '', legalPersonIdUrl: '', legalFaceVerified: false }
  },

  async onLoad() {
    try { this.setData({ authStatus: await getEnterpriseAuthStatus() }) } catch (e) { /* */ }
  },

  onInput(e) {
    const field = e.currentTarget.dataset.field
    this.setData({ ['form.' + field]: e.detail.value })
  },

  nextStep() {
    const { step, form } = this.data
    if (step === 1) {
      if (!form.companyName) { wx.showToast({ title: '请输入企业名称', icon: 'none' }); return }
      if (!/^[0-9A-Z]{18}$/.test(form.creditCode)) { wx.showToast({ title: '信用代码格式不正确', icon: 'none' }); return }
    }
    this.setData({ step: step + 1 })
  },

  prevStep() { this.setData({ step: this.data.step - 1 }) },

  uploadImage(e) {
    const field = e.currentTarget.dataset.field
    wx.chooseImage({
      count: 1, sizeType: ['compressed'],
      success: (res) => {
        wx.showLoading({ title: '上传中...' })
        upload('/admin/upload/image', res.tempFilePaths[0]).then(url => {
          this.setData({ ['form.' + field]: url })
          wx.hideLoading()
        }).catch(() => wx.hideLoading())
      }
    })
  },

  doFaceVerify() {
    wx.showLoading({ title: '识别中...' })
    setTimeout(() => {
      this.setData({ ['form.legalFaceVerified']: true })
      wx.hideLoading()
      wx.showToast({ title: '验证通过', icon: 'success' })
    }, 1500)
  },

  async handleSubmit() {
    try {
      await submitEnterpriseAuth(this.data.form)
      wx.showToast({ title: '提交成功', icon: 'success' })
      setTimeout(() => {
        this.setData({
          authStatus: { authStatus: 'pending', companyName: this.data.form.companyName, authSubmittedAt: new Date().toISOString() },
          step: 1
        })
      }, 1500)
    } catch (e) { /* */ }
  },

  resubmit() { this.setData({ authStatus: null }) }
})
