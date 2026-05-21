const BASE_URL = 'http://localhost:8080/api'

function request(options) {
  return new Promise((resolve, reject) => {
    const token = wx.getStorageSync('token')
    const header = { 'Content-Type': 'application/json' }
    if (token) header['Authorization'] = 'Bearer ' + token

    wx.request({
      url: BASE_URL + options.url,
      method: options.method || 'GET',
      data: options.data,
      header,
      timeout: 15000,
      success(res) {
        const d = res.data
        if (d.code === 200) {
          resolve(d.data)
        } else if (d.code === 401) {
          wx.removeStorageSync('token')
          wx.reLaunch({ url: '/pages/login/login' })
          reject(new Error(d.message || '未登录'))
        } else {
          wx.showToast({ title: d.message || '请求失败', icon: 'none' })
          reject(new Error(d.message))
        }
      },
      fail(err) {
        wx.showToast({ title: '网络异常，请重试', icon: 'none' })
        reject(err)
      }
    })
  })
}

function get(url, params) {
  const query = params
    ? '?' + Object.entries(params)
        .filter(([, v]) => v !== undefined && v !== null && v !== '')
        .map(([k, v]) => k + '=' + encodeURIComponent(v))
        .join('&')
    : ''
  return request({ url: url + query, method: 'GET' })
}

function post(url, data) { return request({ url, method: 'POST', data }) }
function patch(url, data) { return request({ url, method: 'PATCH', data }) }
function put(url, data) { return request({ url, method: 'PUT', data }) }

function upload(url, filePath, formData = {}) {
  return new Promise((resolve, reject) => {
    const token = wx.getStorageSync('token')
    wx.uploadFile({
      url: BASE_URL + url,
      filePath,
      name: 'file',
      formData,
      header: token ? { 'Authorization': 'Bearer ' + token } : {},
      success(res) {
        try {
          const d = JSON.parse(res.data)
          if (d.code === 200) resolve(d.data)
          else { wx.showToast({ title: d.message, icon: 'none' }); reject(new Error(d.message)) }
        } catch { reject(new Error('解析失败')) }
      },
      fail(err) { wx.showToast({ title: '上传失败', icon: 'none' }); reject(err) }
    })
  })
}

module.exports = { get, post, patch, put, upload }
