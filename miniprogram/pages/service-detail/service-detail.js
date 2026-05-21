const { getServiceDetail, getFloatingMenuItems } = require('../../utils/api')

Page({
  data: {
    detail: null,
    showQr: false,
    qrImage: ''
  },

  onLoad(options) {
    if (options.id) {
      getServiceDetail(options.id).then(d => this.setData({ detail: d })).catch(() => {})
    }
  },

  async handleApply() {
    try {
      const items = await getFloatingMenuItems()
      const banli = (items || []).find(i => i.title === '立即办理')
      this.setData({ qrImage: banli ? banli.imageUrl : '', showQr: true })
    } catch (e) {
      this.setData({ showQr: true })
    }
  },

  closeQr() { this.setData({ showQr: false }) }
})
