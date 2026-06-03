const { submitResidency, getMe, getMyResidency } = require('../../utils/api')
const { upload } = require('../../utils/request')

const locations = [
  '沈阳市皇姑区（网易沈阳数字产业中心）',
  '沈阳市自贸区',
  '沈阳市沈河区（马官桥街道办事处）',
  '沈阳市浑南区',
  '沈阳市铁西区',
  '沈阳市沈北新区',
  '辽阳市文圣区（网易辽阳联合创新中心）',
  '锦州市滨海新区'
]
const enterpriseTypes = ['科技类', '游戏动漫类', '电商贸易类', '咨询服务及其他']
const tracksMap = {
  0: ['硬科技', '数智科技', '消费升级', '航空低空', '生物医药', '新材料', '新能源', '其他'],
  1: ['游戏开发', '游戏制作', '动漫制作', '动漫原创', '赛事活动', '技术服务', '其他'],
  2: ['跨境电商', '国内电商', '传统贸易', '供应链', '技术服务', '其他'],
  3: ['建筑工程', '咨询服务', '人力派遣', '生产制造', '技术服务', '其他']
}

Page({
  data: {
    locations,
    enterpriseTypes,
    enterpriseTracks: [],
    locationIdx: -1,
    enterpriseTypeIdx: -1,
    enterpriseTrackIdx: -1,
    form: {
      location: '',
      companyName: '',
      businessLicenseUrl: '',
      legalPersonName: '',
      legalPersonPhone: '',
      legalPersonIdFront: '',
      legalPersonIdBack: '',
      emergencyContactName: '',
      emergencyContactPhone: '',
      enterpriseType: null,
      enterpriseTrack: '',
      agreed: false
    },
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

  onLocationChange(e) {
    const idx = e.detail.value
    this.setData({ locationIdx: idx, ['form.location']: locations[idx] })
  },

  onEnterpriseTypeChange(e) {
    const idx = e.detail.value
    const type = idx + 1
    this.setData({
      enterpriseTypeIdx: idx,
      enterpriseTracks: tracksMap[idx] || [],
      enterpriseTrackIdx: -1,
      ['form.enterpriseType']: type,
      ['form.enterpriseTrack']: ''
    })
  },

  onEnterpriseTrackChange(e) {
    const idx = e.detail.value
    this.setData({
      enterpriseTrackIdx: idx,
      ['form.enterpriseTrack']: this.data.enterpriseTracks[idx]
    })
  },

  onNewApply() {
    this.setData({ showForm: true })
  },

  toggleAgree() {
    this.setData({ ['form.agreed']: !this.data.form.agreed })
  },

  uploadFile(cb) {
    wx.chooseImage({
      count: 1,
      sizeType: ['compressed'],
      sourceType: ['album', 'camera'],
      success: res => {
        const filePath = res.tempFilePaths[0]
        wx.showLoading({ title: '上传中...' })
        upload('/admin/upload/image', filePath)
          .then(result => { cb(result.data.url) })
          .catch(() => wx.showToast({ title: '上传失败', icon: 'none' }))
          .finally(() => wx.hideLoading())
      }
    })
  },

  chooseLicense() {
    this.uploadFile(url => this.setData({ ['form.businessLicenseUrl']: url }))
  },

  chooseIdFront() {
    this.uploadFile(url => this.setData({ ['form.legalPersonIdFront']: url }))
  },

  chooseIdBack() {
    this.uploadFile(url => this.setData({ ['form.legalPersonIdBack']: url }))
  },

  async handleSubmit() {
    const f = this.data.form
    if (!f.location) { wx.showToast({ title: '请选择入驻地点', icon: 'none' }); return }
    if (!f.companyName) { wx.showToast({ title: '请输入企业名称', icon: 'none' }); return }
    if (!f.legalPersonName) { wx.showToast({ title: '请输入法人姓名', icon: 'none' }); return }
    if (!/^1[3-9]\d{9}$/.test(f.legalPersonPhone)) { wx.showToast({ title: '手机号格式不正确', icon: 'none' }); return }
    if (!f.emergencyContactName) { wx.showToast({ title: '请输入应急联系人姓名', icon: 'none' }); return }
    if (!/^1[3-9]\d{9}$/.test(f.emergencyContactPhone)) { wx.showToast({ title: '应急联系人手机号不正确', icon: 'none' }); return }
    if (f.enterpriseType === null) { wx.showToast({ title: '请选择企业类型', icon: 'none' }); return }
    if (!f.enterpriseTrack) { wx.showToast({ title: '请选择企业赛道', icon: 'none' }); return }
    if (!f.agreed) { wx.showToast({ title: '请阅读并同意申请承诺', icon: 'none' }); return }
    try {
      const data = { ...f, agreed: undefined }
      await submitResidency(data)
      wx.showToast({ title: '提交成功', icon: 'success' })
      this.setData({
        locationIdx: -1,
        enterpriseTypeIdx: -1,
        enterpriseTrackIdx: -1,
        enterpriseTracks: [],
        form: {
          location: '',
          companyName: '',
          businessLicenseUrl: '',
          legalPersonName: f.legalPersonName,
          legalPersonPhone: f.legalPersonPhone,
          legalPersonIdFront: '',
          legalPersonIdBack: '',
          emergencyContactName: '',
          emergencyContactPhone: '',
          enterpriseType: null,
          enterpriseTrack: '',
          agreed: false
        },
        showForm: false
      })
      this.fetchMyList()
    } catch (e) { /* */ }
  }
})
