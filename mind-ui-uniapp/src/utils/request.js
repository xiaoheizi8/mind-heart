/**
 * 心域 - API 请求封装 (增强版)
 * 基于 uni.request 封装统一的请求拦截器
 * 支持: 统一响应格式、重试机制、请求缓存、SSE流式对话
 */

// 导入SSE流式请求库
import { fetchEventSource } from '@microsoft/fetch-event-source'

const BASE_URL = 'http://localhost:8080/api/v1'
const TIMEOUT = 15000
const MAX_RETRY = 3
const RETRY_DELAY = 1000

// 简单的内存缓存
const cache = new Map()
const CACHE_EXPIRE = 5 * 60 * 1000 // 5分钟

class Request {
  constructor() {
    this.header = {
      'Content-Type': 'application/json'
    }
  }

  /**
   * 生成缓存key
   */
  getCacheKey(method, url, data) {
    return `${method}:${url}:${JSON.stringify(data || {})}`
  }

  /**
   * 从缓存获取
   */
  getFromCache(key) {
    const cached = cache.get(key)
    if (cached && Date.now() - cached.time < CACHE_EXPIRE) {
      return cached.data
    }
    cache.delete(key)
    return null
  }

  /**
   * 写入缓存
   */
  setCache(key, data) {
    cache.set(key, { data, time: Date.now() })
    // 清理过期缓存
    if (cache.size > 100) {
      const now = Date.now()
      for (const [k, v] of cache.entries()) {
        if (now - v.time > CACHE_EXPIRE) {
          cache.delete(k)
        }
      }
    }
  }

  /**
   * 延迟函数
   */
  delay(ms) {
    return new Promise(resolve => setTimeout(resolve, ms))
  }

  /**
   * 设置认证Token
   */
  setToken(token) {
    uni.setStorageSync('token', token)
    this.header['Authorization'] = `Bearer ${token}`
  }

  /**
   * 移除Token
   */
  removeToken() {
    uni.removeStorageSync('token')
    delete this.header['Authorization']
  }

  /**
   * 获取Token
   */
  getToken() {
    const token = uni.getStorageSync('token')
    if (token) {
      this.header['Authorization'] = `Bearer ${token}`
    }
    return token
  }

  /**
   * 请求拦截
   */
  requestInterceptor(options) {
    const token = this.getToken()
    if (token) {
      options.header['Authorization'] = `Bearer ${token}`
    }
    
    // 显示加载中
    if (!options.hideLoading) {
      uni.showLoading({ title: '加载中...', mask: true })
    }
    
    return options
  }

  /**
   * 响应拦截
   */
  responseInterceptor(response) {
    uni.hideLoading()
    
    const statusCode = response.statusCode
    const data = response.data
    
    // 处理后端统一响应格式 Result<T>
    if (statusCode === 200 && data) {
      // 兼容两种格式: { code, message, data } 和直接返回数据
      if (data.code !== undefined) {
        if (data.code === 200) {
          return data.data
        } else {
          // 业务错误
          const errorMsg = data.message || '请求失败'
          uni.showToast({ title: errorMsg, icon: 'none' })
          return Promise.reject(errorMsg)
        }
      }
      return data
    }
    
    if (statusCode === 401) {
      this.removeToken()
      uni.showToast({ title: '请先登录', icon: 'none' })
      setTimeout(() => {
        uni.navigateTo({ url: '/pages/login/login' })
      }, 1500)
      return Promise.reject('未授权')
    } else if (statusCode === 403) {
      uni.showToast({ title: '没有权限', icon: 'none' })
      return Promise.reject('禁止访问')
    } else if (statusCode === 404) {
      uni.showToast({ title: '资源不存在', icon: 'none' })
      return Promise.reject('资源不存在')
    } else if (statusCode >= 500) {
      uni.showToast({ title: '服务器错误', icon: 'none' })
      return Promise.reject('服务器错误')
    }
    
    return data
  }

  /**
   * 核心请求方法 (支持重试)
   */
  async requestWithRetry(requestOptions, retryCount = 0) {
    return new Promise((resolve, reject) => {
      uni.request({
        ...requestOptions,
        success: (res) => {
          resolve(this.responseInterceptor(res))
        },
        fail: async (err) => {
          uni.hideLoading()
          // 网络错误时尝试重试
          if (retryCount < MAX_RETRY && requestOptions.retry !== false) {
            await this.delay(RETRY_DELAY)
            uni.showToast({ title: `重试中(${retryCount + 1}/${MAX_RETRY})`, icon: 'none', duration: 1000 })
            try {
              const result = await this.requestWithRetry(requestOptions, retryCount + 1)
              resolve(result)
            } catch (e) {
              reject(e)
            }
          } else {
            uni.showToast({ title: '网络错误', icon: 'none' })
            reject(err)
          }
        }
      })
    })
  }

  /**
   * GET 请求 (支持缓存)
   */
  async get(url, data = {}, options = {}) {
    const cacheKey = this.getCacheKey('GET', url, data)
    
    // 使用缓存
    if (options.cache !== false) {
      const cached = this.getFromCache(cacheKey)
      if (cached) {
        return cached
      }
    }
    
    const requestOptions = this.requestInterceptor({
      url: BASE_URL + url,
      method: 'GET',
      data,
      header: { ...this.header, ...options.header },
      timeout: TIMEOUT,
      ...options
    })
    
    const result = await this.requestWithRetry(requestOptions)
    
    // 写入缓存
    if (options.cache !== false) {
      this.setCache(cacheKey, result)
    }
    
    return result
  }

  /**
   * POST 请求
   */
  async post(url, data = {}, options = {}) {
    const requestOptions = this.requestInterceptor({
      url: BASE_URL + url,
      method: 'POST',
      data,
      header: { ...this.header, ...options.header },
      timeout: TIMEOUT,
      ...options
    })
    
    return this.requestWithRetry(requestOptions)
  }

  /**
   * PUT 请求
   */
  async put(url, data = {}, options = {}) {
    const requestOptions = this.requestInterceptor({
      url: BASE_URL + url,
      method: 'PUT',
      data,
      header: { ...this.header, ...options.header },
      timeout: TIMEOUT,
      ...options
    })
    
    return this.requestWithRetry(requestOptions)
  }

  /**
   * DELETE 请求
   */
  async delete(url, data = {}, options = {}) {
    const requestOptions = this.requestInterceptor({
      url: BASE_URL + url,
      method: 'DELETE',
      data,
      header: { ...this.header, ...options.header },
      timeout: TIMEOUT,
      ...options
    })
    
    return this.requestWithRetry(requestOptions)
  }

  /**
   * 上传文件 (uni.uploadFile)
   */
  async upload(url, filePath, name = 'file', formData = {}) {
    const token = this.getToken()
    return new Promise((resolve, reject) => {
      uni.uploadFile({
        url: BASE_URL + url,
        filePath,
        name,
        formData,
        header: {
          'Authorization': token ? `Bearer ${token}` : ''
        },
        success: (res) => {
          if (res.statusCode === 200) {
            try {
              const data = JSON.parse(res.data)
              if (data.code === 200) {
                resolve(data.data)
              } else {
                uni.showToast({ title: data.message || '上传失败', icon: 'none' })
                reject(data.message || '上传失败')
              }
            } catch (e) {
              reject('解析响应失败')
            }
          } else if (res.statusCode === 401) {
            uni.showToast({ title: '请先登录', icon: 'none' })
            reject('未授权')
          } else {
            uni.showToast({ title: '上传失败', icon: 'none' })
            reject('上传失败')
          }
        },
        fail: (err) => {
          uni.showToast({ title: '网络错误', icon: 'none' })
          reject(err)
        }
      })
    })
  }
}

export const request = new Request()

/**
 * 认证相关 API
 */
export const authApi = {
  login: (data) => request.post('/auth/login', data),
  loginByCode: (email, code) => request.post('/auth/loginByCode', { email, code }),
  register: (data) => request.post('/auth/register', data),
  sendCode: (email) => request.post('/auth/sendCode', { email }),
  logout: () => request.post('/auth/logout'),
  refreshToken: () => request.post('/auth/refreshToken')
}

/**
 * 用户相关 API
 */
export const userApi = {
  getProfile: () => request.get('/user/profile'),
  updateProfile: (data) => request.put('/user/profile', data),
  changePassword: (data) => request.put('/user/password', data),
  resetPassword: (data) => request.post('/user/resetPassword', data),
  bindEmail: (data) => request.post('/user/bindEmail', data),
  bindPhone: (phone) => request.post('/user/bindPhone', { phone }),
  setGuardian: (phone) => request.post('/user/guardian', { guardianPhone: phone })
}

/**
 * 日记相关 API
 */
export const diaryApi = {
  getList: (params) => request.get('/diary/list', params),
  getDetail: (id) => request.get(`/diary/${id}`),
  create: (data) => request.post('/diary/create', data),
  update: (id, data) => request.put(`/diary/${id}`, data),
  delete: (id) => request.delete(`/diary/${id}`),
  getReport: (params) => request.get('/diary/report', params),
  getStats: () => request.get('/diary/stats'),
  // 情绪报告相关
  generateReport: (reportType = 'week') => request.post('/diary/report/generate', null, { params: { reportType } }),
  getReportList: (reportType) => request.get('/diary/report/list', reportType ? { reportType } : {}),
  getReportDetail: (reportId) => request.get(`/diary/report/${reportId}`)
}

/**
 * 文件上传 API
 */
export const fileApi = {
  upload: (filePath) => request.upload('/file/upload', filePath)
}

/**
 * AI对话相关 API
 */
export const chatApi = {
  send: (data) => request.post('/chat/send', data),
  setPersona: (persona) => request.post('/chat/persona', { persona }),
  getPersona: () => request.get('/chat/persona'),
  clearHistory: () => request.delete('/chat/history')
}

/**
 * 情感分析 API
 */
export const emotionApi = {
  analyze: (data) => request.post('/emotion/analyze', data),
  getProfile: (params) => request.get('/emotion/profile', params),
  getStatus: () => request.get('/emotion/profile/status')
}

/**
 * 知识库相关 API
 */
export const knowledgeApi = {
  getList: (params) => request.get('/knowledge/list', params),
  getTags: () => request.get('/knowledge/tags'),
  getByTag: (tag, params) => request.get(`/knowledge/tag/${tag}`, params),
  getDetail: (id) => request.get(`/knowledge/${id}`),
  collect: (id) => request.post(`/knowledge/${id}/collect`),
  cancelCollect: (id) => request.delete(`/knowledge/${id}/collect`),
  getFavorites: (params) => request.get('/knowledge/favorites', params),
  getAllFavorites: () => request.get('/knowledge/favorites', { page: 1, size: 100 })
}

/**
 * 预警相关 API
 */
export const warningApi = {
  getStatus: () => request.get('/warning/status'),
  getHistory: (params) => request.get('/warning/list', params)
}

/**
 * 通知相关 API
 */
export const notificationApi = {
  getList: (params) => request.get('/notification/list', params),
  getUnreadCount: () => request.get('/notification/unreadCount'),
  markRead: (id) => request.post(`/notification/${id}/read`),
  markAllRead: () => request.post('/notification/readAll')
}

/**
 * AI对话流式输出 (SSE)
 * 支持自动重连、连接状态管理、心跳检测
 */
export const streamChat = {
  /**
   * 流式对话请求
   * @param {Object} options 配置项
   *   - message: 用户消息
   *   - persona: AI人格
   *   - onStart: 开始回调
   *   - onChunk: 收到内容块回调 (content)
   *   - onDone: 完成回调 (fullContent)
   *   - onError: 错误回调 (error)
   *   - onClose: 关闭回调
   *   - maxRetries: 最大重连次数 (默认2)
   * @returns {Object} { abort: Function } 可调用abort取消请求
   */
  connect(options) {
    const {
      message,
      persona = 'listener',
      onStart,
      onChunk,
      onDone,
      onError,
      onClose,
      maxRetries = 0  // 默认不重试,避免完成后重复连接
    } = options

    const token = uni.getStorageSync('token')
    if (!token) {
      onError && onError(new Error('未登录'))
      return { abort: () => {} }
    }

    let retryCount = 0
    let aborted = false
    let abortController = null
    let fullContent = ''

    const url = `${BASE_URL}/chat/stream?message=${encodeURIComponent(message)}&persona=${persona}`

    const startConnection = () => {
      if (aborted) return

      console.log(`[SSE] 连接尝试 ${retryCount + 1}/${maxRetries + 1}`)

      abortController = new AbortController()

      fetchEventSource(url, {
        method: 'POST',
        headers: {
          'Authorization': 'Bearer ' + token,
          'Content-Type': 'application/json'
        },
        body: JSON.stringify({}),
        signal: abortController.signal,

        onopen: async (response) => {
          if (aborted) return
          if (response.ok) {
            console.log('[SSE] 连接成功')
            retryCount = 0 // 重置重连计数
          } else {
            const errorText = await response.text()
            throw new Error(`HTTP ${response.status}: ${errorText}`)
          }
        },

        onmessage: (event) => {
          if (aborted) return

          try {
            switch (event.event) {
              case 'start':
                fullContent = ''
                onStart && onStart()
                break

              case 'chunk':
                const json = JSON.parse(event.data)
                if (json.content) {
                  fullContent += json.content
                  onChunk && onChunk(json.content, fullContent)
                }
                break

              case 'done':
                // 收到完成事件后，标记为已中止，防止重连
                aborted = true
                onDone && onDone(fullContent)
                break

              case 'error':
                const errData = JSON.parse(event.data)
                throw new Error(errData.error || '服务端错误')
            }
          } catch (e) {
            console.error('[SSE] 消息处理错误:', e)
            onError && onError(e)
          }
        },

        onerror: (error) => {
          if (aborted) return
          console.error('[SSE] 连接错误:', error)

          if (retryCount < maxRetries) {
            retryCount++
            console.log(`[SSE] ${retryCount}秒后尝试重连...`)
            setTimeout(startConnection, retryCount * 1000)
          } else {
            onError && onError(error)
          }
          // 返回error以阻止fetch-event-source自动重连（我们自己控制）
          return
        },

        onclose: () => {
          if (aborted) {
            console.log('[SSE] 连接已关闭(已中止)')
            onClose && onClose()
            return
          }
          console.log('[SSE] 连接关闭,防止自动重连')
          // 标记为已中止,防止fetch-event-source自动重连
          aborted = true
          onClose && onClose()
        }
      })
    }

    startConnection()

    return {
      abort: () => {
        aborted = true
        if (abortController) {
          abortController.abort()
        }
        console.log('[SSE] 用户取消请求')
      }
    }
  }
}

/**
 * 设置相关 API
 */
export const settingsApi = {
  get: () => request.get('/user/settings'),
  update: (data) => request.put('/user/settings', data)
}

export default {
  request,
  authApi,
  userApi,
  diaryApi,
  chatApi,
  emotionApi,
  knowledgeApi,
  warningApi,
  notificationApi,
  settingsApi
}