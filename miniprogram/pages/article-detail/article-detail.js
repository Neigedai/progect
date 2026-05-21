const { getArticleDetail } = require('../../utils/api')

Page({
  data: { article: null, content: '' },

  onLoad(options) {
    if (options.id) {
      getArticleDetail(options.id).then(a => {
        this.setData({ article: a, content: this.parseMarkdown(a.content || '') })
      }).catch(() => {})
    }
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
