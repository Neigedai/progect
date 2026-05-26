const { getParkList, getParkOverview } = require('../../utils/api')

Page({
  data: {
    parks: [],
    activeParkId: null,
    overview: null
  },

  async onLoad() {
    try {
      const parks = await getParkList()
      if (parks && parks.length > 0) {
        const def = parks.find(p => p.isDefault) || parks[0]
        this.setData({ parks, activeParkId: def.id })
        const overview = await getParkOverview(def.id)
        this.setData({ overview })
      }
    } catch (e) { /* */ }
  },

  async onSwitch(e) {
    const id = e.currentTarget.dataset.id
    if (id === this.data.activeParkId) return
    this.setData({ activeParkId: id })
    try {
      const overview = await getParkOverview(id)
      this.setData({ overview })
    } catch (e) { /* */ }
  }
})
