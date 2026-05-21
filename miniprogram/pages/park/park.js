const { getParkList, getParkOverview } = require('../../utils/api')

Page({
  data: {
    parks: [],
    parkNames: [],
    currentIdx: 0,
    currentPark: null,
    overview: null
  },

  async onLoad() {
    try {
      const parks = await getParkList()
      const parkNames = (parks || []).map(p => p.parkName)
      const defaultIdx = Math.max(0, (parks || []).findIndex(p => p.isDefault))
      const park = parks[defaultIdx]
      this.setData({ parks, parkNames, currentIdx: defaultIdx, currentPark: park })
      if (park) {
        const overview = await getParkOverview(park.id)
        this.setData({ overview })
      }
    } catch (e) { /* */ }
  },

  async onSwitch(e) {
    const idx = e.detail.value
    const park = this.data.parks[idx]
    this.setData({ currentIdx: idx, currentPark: park })
    try {
      const overview = await getParkOverview(park.id)
      this.setData({ overview })
    } catch (e) { /* */ }
  }
})
