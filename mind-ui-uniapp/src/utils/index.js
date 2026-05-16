/**
 * 心域 - 工具函数
 */

/**
 * 格式化日期
 * @param {string|Date} date 日期
 * @param {string} format 格式
 * @returns {string}
 */
export function formatDate(date, format = 'YYYY-MM-DD') {
  if (!date) return ''
  
  const d = new Date(date)
  if (isNaN(d.getTime())) return ''
  
  const year = d.getFullYear()
  const month = String(d.getMonth() + 1).padStart(2, '0')
  const day = String(d.getDate()).padStart(2, '0')
  const hour = String(d.getHours()).padStart(2, '0')
  const minute = String(d.getMinutes()).padStart(2, '0')
  const second = String(d.getSeconds()).padStart(2, '0')
  
  return format
    .replace('YYYY', year)
    .replace('MM', month)
    .replace('DD', day)
    .replace('HH', hour)
    .replace('mm', minute)
    .replace('ss', second)
}

/**
 * 格式化日期为相对时间
 * @param {string|Date} date 日期
 * @returns {string}
 */
export function formatRelativeTime(date) {
  if (!date) return ''
  
  const d = new Date(date)
  const now = new Date()
  const diff = now - d
  
  const minute = 60 * 1000
  const hour = 60 * minute
  const day = 24 * hour
  const week = 7 * day
  
  if (diff < minute) {
    return '刚刚'
  } else if (diff < hour) {
    return Math.floor(diff / minute) + '分钟前'
  } else if (diff < day) {
    return Math.floor(diff / hour) + '小时前'
  } else if (diff < week) {
    return Math.floor(diff / day) + '天前'
  } else {
    return formatDate(date, 'MM-DD')
  }
}

/**
 * 手机号脱敏
 * @param {string} phone 手机号
 * @returns {string}
 */
export function maskPhone(phone) {
  if (!phone || phone.length !== 11) return phone
  return phone.replace(/(\d{3})\d{4}(\d{4})/, '$1****$2')
}

/**
 * 验证手机号
 * @param {string} phone 手机号
 * @returns {boolean}
 */
export function isValidPhone(phone) {
  return /^1[3-9]\d{9}$/.test(phone)
}

/**
 * 验证邮箱
 * @param {string} email 邮箱
 * @returns {boolean}
 */
export function isValidEmail(email) {
  return /^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}$/.test(email)
}

/**
 * 节流
 * @param {Function} fn 函数
 * @param {number} delay 延迟
 * @returns {Function}
 */
export function throttle(fn, delay = 300) {
  let timer = null
  return function(...args) {
    if (timer) return
    timer = setTimeout(() => {
      fn.apply(this, args)
      timer = null
    }, delay)
  }
}

/**
 * 防抖
 * @param {Function} fn 函数
 * @param {number} delay 延迟
 * @returns {Function}
 */
export function debounce(fn, delay = 300) {
  let timer = null
  return function(...args) {
    if (timer) clearTimeout(timer)
    timer = setTimeout(() => {
      fn.apply(this, args)
    }, delay)
  }
}

/**
 * 深度克隆
 * @param {any} obj 对象
 * @returns {any}
 */
export function deepClone(obj) {
  if (obj === null || typeof obj !== 'object') return obj
  if (obj instanceof Date) return new Date(obj)
  if (obj instanceof Array) return obj.map(item => deepClone(item))
  if (obj instanceof Object) {
    const clone = {}
    for (const key in obj) {
      if (obj.hasOwnProperty(key)) {
        clone[key] = deepClone(obj[key])
      }
    }
    return clone
  }
}

/**
 * 情绪分类映射
 */
export const EMOTION_MAP = {
  happy: { label: '开心', icon: '😊', color: '#FFD700' },
  calm: { label: '平静', icon: '😌', color: '#87CEEB' },
  sad: { label: '难过', icon: '😢', color: '#6B8E9F' },
  anxious: { label: '焦虑', icon: '😰', color: '#FF7F50' },
  angry: { label: '愤怒', icon: '😠', color: '#FF4500' },
  fear: { label: '害怕', icon: '😨', color: '#9370DB' },
  surprise: { label: '惊讶', icon: '😲', color: '#20B2AA' }
}

/**
 * 获取情绪信息
 * @param {string} category 情绪类别
 * @returns {object}
 */
export function getEmotionInfo(category) {
  return EMOTION_MAP[category] || EMOTION_MAP.calm
}

export default {
  formatDate,
  formatRelativeTime,
  maskPhone,
  isValidPhone,
  isValidEmail,
  throttle,
  debounce,
  deepClone,
  EMOTION_MAP,
  getEmotionInfo
}