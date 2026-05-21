const { phoneLogin, login, sendCode } = require('../../utils/api')

Page({
  data: {
    tab: 'phone',
    phone: '',
    code: '',
    username: '',
    password: '',
    counting: false,
    countdown: 60
  },

  switchTab(e) {
    this.setData({ tab: e.currentTarget.dataset.tab })
  },

  onPhoneInput(e) { this.setData({ phone: e.detail.value }) },
  onCodeInput(e) { this.setData({ code: e.detail.value }) },
  onUsernameInput(e) { this.setData({ username: e.detail.value }) },
  onPasswordInput(e) { this.setData({ password: e.detail.value }) },

  async handleSendCode() {
    if (this.data.counting) return
    if (!/^1[3-9]\d{9}$/.test(this.data.phone)) {
      wx.showToast({ title: '请输入正确的手机号', icon: 'none' })
      return
    }
    try {
      await sendCode(this.data.phone)
      wx.showToast({ title: '验证码已发送', icon: 'success' })
      this.setData({ counting: true })
      const timer = setInterval(() => {
        const cd = this.data.countdown - 1
        if (cd <= 0) { clearInterval(timer); this.setData({ counting: false, countdown: 60 }) }
        else this.setData({ countdown: cd })
      }, 1000)
    } catch (e) { /* handled */ }
  },

  async handlePhoneLogin() {
    const { phone, code } = this.data
    if (!/^1[3-9]\d{9}$/.test(phone)) { wx.showToast({ title: '请输入正确的手机号', icon: 'none' }); return }
    if (code.length < 4) { wx.showToast({ title: '请输入验证码', icon: 'none' }); return }
    try {
      const res = await phoneLogin({ phone, code })
      wx.setStorageSync('token', res.token)
      wx.reLaunch({ url: '/pages/index/index' })
    } catch (e) { /* handled */ }
  },

  async handlePasswordLogin() {
    const { username, password } = this.data
    if (!username) { wx.showToast({ title: '请输入用户名', icon: 'none' }); return }
    if (!password) { wx.showToast({ title: '请输入密码', icon: 'none' }); return }
    try {
      const res = await login({ username, password })
      wx.setStorageSync('token', res.token)
      wx.reLaunch({ url: '/pages/index/index' })
    } catch (e) { /* handled */ }
  }
})
