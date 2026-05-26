const { getArticleDetail } = require('../../utils/api')

const BASE_HOST = 'http://localhost:8080'

function fixUrl(url) {
  if (!url) return ''
  if (url.startsWith('http')) return url
  return BASE_HOST + url
}

Page({
  data: { article: null, content: '', videoLocalPath: '' },

  onLoad(options) {
    if (options.id) {
      getArticleDetail(options.id).then(a => {
        if (a) {
          a.coverImage = fixUrl(a.coverImage)
          a.videoUrl = fixUrl(a.videoUrl)
        }
        const data = { article: a, content: this.parseMarkdown(a.content || '') }
        this.setData(data)
        if (a.videoUrl) {
          this.downloadVideo(a.videoUrl)
        }
      }).catch(() => {})
    }
  },

  downloadVideo(url) {
    wx.showLoading({ title: '加载视频...' })
    wx.downloadFile({
      url,
      success: res => {
        if (res.statusCode === 200) {
          this.setData({ videoLocalPath: res.tempFilePath })
        }
      },
      fail: () => wx.showToast({ title: '视频加载失败', icon: 'none' }),
      complete: () => wx.hideLoading()
    })
  },

  parseMarkdown(md) {
    return md
      .replace(/###\s+(.+)/g, '<h4 style="font-size:30rpx;font-weight:600;margin:24rpx 0 12rpx">$1</h4>')
      .replace(/##\s+(.+)/g, '<h3 style="font-size:34rpx;font-weight:700;margin:32rpx 0 16rpx">$1</h3>')
      .replace(/\*\*(.+?)\*\*/g, '<strong>$1</strong>')
      .replace(/- (.+)/g, '<li style="margin-left:32rpx;line-height:1.8">$1</li>')
      .replace(/\n/g, '<br/>')
  }
})
