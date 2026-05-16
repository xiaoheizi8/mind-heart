/**
 * 心域 - 状态管理 (增强版)
 * 支持模块化、持久化、响应式更新
 */

const STORAGE_KEY = 'mind_realm_store'

// 初始状态
const initialState = {
  // 用户模块
  user: null,
  token: null,
  isLoggedIn: false,
  
  // 应用模块
  app: {
    theme: 'light',
    language: 'zh-CN',
    networkType: 'unknown'
  },
  
  // 日记模块
  diary: {
    lastCreateAt: null,
    draft: null,
    totalCount: 0
  },
  
  // 聊天模块
  chat: {
    persona: 'listener',
    lastMessageTime: null
  },
  
  // 情感模块
  emotion: {
    lastScore: 0,
    lastCategory: 'neutral'
  },
  
  // 设置模块
  settings: {
    diaryReminder: true,
    aiPush: true,
    weeklyReport: true,
    warningNotify: true,
    reminderTime: '20:00'
  },
  
  // 通知模块
  notification: {
    unreadCount: 0
  }
}

class Store {
  constructor() {
    this.state = JSON.parse(JSON.stringify(initialState))
    this.listeners = []
    this.init()
  }

  init() {
    try {
      const saved = uni.getStorageSync(STORAGE_KEY)
      if (saved) {
        this.state = { ...this.state, ...saved }
      }
    } catch (e) {
      console.error('Store init error:', e)
    }
  }

  save() {
    try {
      // 只持久化必要的数据
      const persistData = {
        user: this.state.user,
        token: this.state.token,
        isLoggedIn: this.state.isLoggedIn,
        app: this.state.app,
        settings: this.state.settings,
        chat: { persona: this.state.chat.persona }
      }
      uni.setStorageSync(STORAGE_KEY, persistData)
    } catch (e) {
      console.error('Store save error:', e)
    }
  }

  subscribe(listener) {
    this.listeners.push(listener)
    return () => {
      this.listeners = this.listeners.filter(l => l !== listener)
    }
  }

  notify() {
    this.listeners.forEach(l => {
      try {
        l(this.state)
      } catch (e) {
        console.error('Store listener error:', e)
      }
    })
  }

  getState() {
    return this.state
  }

  setState(newState, persist = true) {
    this.state = { ...this.state, ...newState }
    if (persist) {
      this.save()
    }
    this.notify()
  }

  // ========== 用户模块 ==========
  
  setUser(user) {
    this.setState({ 
      user,
      isLoggedIn: !!user 
    })
  }

  setToken(token) {
    this.setState({ token })
    if (token) {
      uni.setStorageSync('token', token)
    } else {
      uni.removeStorageSync('token')
    }
  }

  updateUser(updates) {
    if (this.state.user) {
      this.setState({ 
        user: { ...this.state.user, ...updates } 
      })
    }
  }

  clearUser() {
    this.setState({ 
      user: null, 
      token: null, 
      isLoggedIn: false 
    })
    uni.removeStorageSync('token')
  }

  isLoggedIn() {
    return this.state.isLoggedIn && !!this.state.token
  }

  // ========== 应用模块 ==========
  
  setAppSetting(key, value) {
    this.setState({
      app: { ...this.state.app, [key]: value }
    })
  }

  setNetworkType(type) {
    this.setState({
      app: { ...this.state.app, networkType: type }
    }, false)
  }

  // ========== 日记模块 ==========
  
  setDiaryDraft(draft) {
    this.setState({
      diary: { ...this.state.diary, draft }
    }, false)
  }

  clearDiaryDraft() {
    this.setState({
      diary: { ...this.state.diary, draft: null }
    }, false)
  }

  updateDiaryStats(totalCount) {
    this.setState({
      diary: { 
        ...this.state.diary, 
        totalCount,
        lastCreateAt: new Date().toISOString() 
      }
    })
  }

  // ========== 聊天模块 ==========
  
  setChatPersona(persona) {
    this.setState({
      chat: { ...this.state.chat, persona }
    })
  }

  updateChatTime() {
    this.setState({
      chat: { ...this.state.chat, lastMessageTime: new Date().toISOString() }
    }, false)
  }

  // ========== 情感模块 ==========
  
  updateEmotion(score, category) {
    this.setState({
      emotion: { lastScore: score, lastCategory: category }
    }, false)
  }

  // ========== 设置模块 ==========
  
  setSettings(settings) {
    this.setState({ settings: { ...this.state.settings, ...settings } })
  }

  getSettings() {
    return this.state.settings
  }

  // ========== 通知模块 ==========
  
  setUnreadCount(count) {
    this.setState({
      notification: { unreadCount: count }
    }, false)
  }

  decrementUnread() {
    const count = Math.max(0, this.state.notification.unreadCount - 1)
    this.setState({
      notification: { unreadCount: count }
    }, false)
  }

  // ========== 工具方法 ==========
  
  reset() {
    this.state = JSON.parse(JSON.stringify(initialState))
    uni.removeStorageSync(STORAGE_KEY)
    uni.removeStorageSync('token')
    this.notify()
  }
}

export const store = new Store()

// 导出便捷方法
export const useStore = () => store
export const getState = () => store.getState()
export const setState = (state) => store.setState(state)

export default store