class Store {
  constructor() {
    this.state = {
      user: null,
      token: null,
      isLoggedIn: false,
      children: [],
      currentChild: null,
      app: { theme: 'light' }
    }
    this.loadFromStorage()
  }

  loadFromStorage() {
    try {
      const saved = uni.getStorageSync('parent_store')
      if (saved) Object.assign(this.state, JSON.parse(saved))
    } catch (e) {}
  }

  save() {
    uni.setStorageSync('parent_store', JSON.stringify(this.state))
  }

  setUser(user) { this.state.user = user; this.state.isLoggedIn = true; this.save() }
  setToken(token) { this.state.token = token; uni.setStorageSync('token', token); this.save() }
  setChildren(children) { this.state.children = children; this.save() }
  setCurrentChild(child) { this.state.currentChild = child; this.save() }
  clearUser() {
    this.state.user = null; this.state.token = null;
    this.state.isLoggedIn = false; this.state.children = [];
    this.state.currentChild = null;
    uni.removeStorageSync('token'); uni.removeStorageSync('parent_store');
  }
  isLoggedIn() { return !!uni.getStorageSync('token') }
}

export default new Store()
