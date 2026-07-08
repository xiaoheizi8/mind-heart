<template>
  <view class="page-container flex-center" style="flex-direction:column;">
    <view class="login-card">
      <text class="login-title">心域家长版</text>
      <text class="login-subtitle">登录以查看孩子动态</text>
      <input class="input" v-model="username" placeholder="用户名/手机号/邮箱" />
      <input class="input" v-model="password" type="password" placeholder="密码" />
      <button class="btn btn-primary login-btn" @click="doLogin" :disabled="loading">{{ loading ? '登录中...' : '登 录' }}</button>
      <view class="login-links">
        <text class="link" @click="goRegister">注册家长账号</text>
      </view>
    </view>
  </view>
</template>
<script>
import { authApi } from '../../utils/request.js'
export default {
  data() { return { username: '', password: '', loading: false } },
  methods: {
    async doLogin() {
      if (!this.username || !this.password) { uni.showToast({ title: '请填写完整', icon: 'none' }); return }
      this.loading = true
      try {
        const res = await authApi.login({ username: this.username, password: this.password })
        uni.setStorageSync('token', res.token)
        uni.setStorageSync('userInfo', res.userInfo || res)
        uni.switchTab({ url: '/pages/index/index' })
      } catch (e) {
        uni.showToast({ title: e.message || '登录失败', icon: 'none' })
      } finally { this.loading = false }
    },
    goRegister() { uni.navigateTo({ url: '/pages/register/register' }) }
  }
}
</script>
<style scoped>
.login-card { width: 85%; max-width: 360px; background: #fff; border-radius: 16px; padding: 40px 30px; box-shadow: var(--shadow); }
.login-title { font-size: 24px; font-weight: bold; color: var(--primary-color); text-align: center; display: block; margin-bottom: 8px; }
.login-subtitle { font-size: 14px; color: var(--text-hint); text-align: center; display: block; margin-bottom: 30px; }
.input { width: 100%; height: 46px; border: 1px solid var(--border-color); border-radius: 8px; padding: 0 14px; font-size: 15px; margin-bottom: 16px; }
.login-btn { width: 100%; height: 46px; margin-top: 8px; }
.login-links { margin-top: 20px; text-align: center; }
.link { font-size: 14px; color: var(--primary-color); }
</style>
