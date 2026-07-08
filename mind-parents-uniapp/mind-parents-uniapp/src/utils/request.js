const BASE_URL = 'http://localhost:8080'

class Request {
  constructor() {
    this.baseUrl = BASE_URL
    this._cache = new Map()
    this._cacheExpire = 5 * 60 * 1000
  }

  getToken() {
    return uni.getStorageSync('token') || ''
  }

  request(options) {
    return new Promise((resolve, reject) => {
      const token = this.getToken()
      const headers = { 'Content-Type': 'application/json' }
      if (token) headers['Authorization'] = 'Bearer ' + token

      uni.request({
        url: options.url.startsWith('http') ? options.url : this.baseUrl + options.url,
        method: options.method || 'GET',
        data: options.data,
        header: { ...headers, ...options.header },
        timeout: 15000,
        success: (res) => {
          if (res.statusCode === 200) {
            const data = res.data
            if (data.code === 200) resolve(data.data)
            else reject(new Error(data.message || '请求失败'))
          } else if (res.statusCode === 401) {
            uni.removeStorageSync('token')
            uni.reLaunch({ url: '/pages/login/login' })
            reject(new Error('请先登录'))
          } else if (res.statusCode === 403) {
            reject(new Error('没有权限'))
          } else {
            reject(new Error(res.data?.message || '服务器错误'))
          }
        },
        fail: (err) => reject(new Error(err.errMsg || '网络错误'))
      })
    })
  }

  get(url, params = {}) { return this.request({ url, method: 'GET', data: params }) }
  post(url, data = {}) { return this.request({ url, method: 'POST', data }) }
  put(url, data = {}) { return this.request({ url, method: 'PUT', data }) }
  delete(url, data = {}) { return this.request({ url, method: 'DELETE', data }) }
}

const request = new Request()

// Auth API (uses /api/v1)
export const authApi = {
  login: (data) => request.post('/api/v1/auth/login', data),
  register: (data) => request.post('/api/v1/auth/register/parent', data),
  sendCode: (data) => request.post('/api/v1/auth/sendCode', data),
  logout: () => request.post('/api/v1/auth/logout')
}

// Parent API (uses /api/v1/parent)
export const parentApi = {
  getChildren: () => request.get('/api/v1/parent/children'),
  getChildOverview: (childId) => request.get(`/api/v1/parent/child/${childId}/overview`),
  getChildWarnings: (childId, params) => request.get(`/api/v1/parent/child/${childId}/warnings`, params),
  getChildWarningDetail: (childId, warningId) => request.get(`/api/v1/parent/child/${childId}/warnings/${warningId}`),
  getChildDiaries: (childId, params) => request.get(`/api/v1/parent/child/${childId}/diaries`, params),
  getChildDiaryDetail: (childId, diaryId) => request.get(`/api/v1/parent/child/${childId}/diaries/${diaryId}`),
  getChildReports: (childId) => request.get(`/api/v1/parent/child/${childId}/reports`),
  getChildReportDetail: (childId, reportId) => request.get(`/api/v1/parent/child/${childId}/reports/${reportId}`),
  getChildEmotionReport: (childId, days) => request.get(`/api/v1/parent/child/${childId}/emotion-report`, { days }),
  sendBindRequest: (childIdentifier) => request.post('/api/v1/parent/bind', { childIdentifier }),
  getBindRequests: () => request.get('/api/v1/parent/bind/requests'),
  cancelBind: (id) => request.delete(`/api/v1/parent/bind/${id}`),
  // 新增
  generateReport: (childId, type = 'week') => request.post(`/api/v1/parent/child/${childId}/reports/generate?type=${type}`),
  handleWarning: (childId, warningId) => request.post(`/api/v1/parent/child/${childId}/warnings/${warningId}/handle`),
  getEmotionTrend: (childId, days = 7) => request.get(`/api/v1/parent/child/${childId}/emotion-trend`, { days }),
  getActivitySummary: (childId) => request.get(`/api/v1/parent/child/${childId}/activity-summary`),
  getDashboard: () => request.get('/api/v1/parent/dashboard')
}

// User API (uses /api/v1/user)
export const userApi = {
  getProfile: () => request.get('/api/v1/user/profile'),
  updateProfile: (data) => request.put('/api/v1/user/profile', data),
  changePassword: (data) => request.put('/api/v1/user/password', data)
}

export default request
