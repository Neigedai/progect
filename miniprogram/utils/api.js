const { get, post, patch, put } = require('./request')

// Auth
const login = (data) => post('/auth/login', data)
const phoneLogin = (data) => post('/auth/phone-login', data)
const sendCode = (phone) => post('/auth/send-code', { phone })
const getMe = () => get('/auth/me')

// Park
const getParkList = () => get('/parks')
const getParkOverview = (id) => get('/parks/' + id + '/overview')

// Article
const getBanners = () => get('/banners')
const getArticles = (params) => get('/articles', params)
const getArticleDetail = (id) => get('/articles/' + id)

// Service
const getCategories = () => get('/services/categories')
const getServiceItems = (params) => get('/services/items', params)
const getServiceDetail = (id) => get('/services/items/' + id)
const getFloatingMenuItems = () => get('/floating-menu/items')
const submitServiceApplication = (data) => post('/services/applications', data)

// Residency
const submitResidency = (data) => post('/residency/applications', data)
const getMyResidency = () => get('/residency/applications/my')

// Enterprise
const submitEnterpriseAuth = (data) => post('/enterprise/submit', data)
const getEnterpriseAuthStatus = () => get('/enterprise/status')

// Notification
const getNotifications = (params) => get('/notifications', params)
const getUnreadCount = () => get('/notifications/unread-count')
const markRead = (id) => put('/notifications/read/' + id)
const markAllRead = () => put('/notifications/read-all')

module.exports = {
  login, phoneLogin, sendCode, getMe,
  getParkList, getParkOverview,
  getBanners, getArticles, getArticleDetail,
  getCategories, getServiceItems, getServiceDetail, getFloatingMenuItems, submitServiceApplication,
  submitResidency, getMyResidency,
  submitEnterpriseAuth, getEnterpriseAuthStatus,
  getNotifications, getUnreadCount, markRead, markAllRead
}
