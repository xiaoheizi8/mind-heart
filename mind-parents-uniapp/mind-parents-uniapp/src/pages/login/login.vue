<template>
  <view class="launch-page flex-center" style="flex-direction:column;">
    <view class="login-card animate-fade-in">
      <view class="login-logo">🏠</view>
      <text class="login-title">欢迎回来</text>
      <text class="login-subtitle">登录查看孩子的情绪动态</text>
      <view class="input-group"><input class="input" v-model="username" placeholder="用户名/手机号/邮箱" /></view>
      <view class="input-group"><view class="input-wrap"><input class="input" :type="showPwd ? 'text' : 'password'" v-model="password" placeholder="密码" /><text class="input-icon" @click="showPwd=!showPwd">{{ showPwd ? '🙈' : '👁️' }}</text></view></view>
      <button class="btn btn-primary btn-block mt-16" @click="doLogin" :disabled="loading">
        <text v-if="!loading">登 录</text>
        <text v-else>登录中...</text>
      </button>
      <view class="login-footer"><text class="link" @click="goRegister">注册家长账号</text></view>
    </view>
  </view>
</template>
<script>
import { authApi } from '../../utils/request.js'
export default {
  data() { return { username: '', password: '', showPwd: false, loading: false } },
  methods: {
    async doLogin() {
      if (!this.username || !this.password) { uni.showToast({ title: '请填写完整', icon: 'none' }); return }
      this.loading = true
      try {
        const res = await authApi.login({ username: this.username, password: this.password })
        uni.setStorageSync('token', res.token)
        uni.setStorageSync('userInfo', res.userInfo || res)
        uni.switchTab({ url: '/pages/index/index' })
      } catch (e) { uni.showToast({ title: e.message || '登录失败', icon: 'none' }) }
      finally { this.loading = false }
    },
    goRegister() { uni.navigateTo({ url: '/pages/register/register' }) }
  }
}
</script>
<style scoped>
.login-card { width: 85%; max-width: 640rpx; background: #fff; border-radius: var(--radius-xl); padding: 56rpx 40rpx; box-shadow: var(--shadow-medium); }
.login-logo { width: 96rpx; height: 96rpx; background: linear-gradient(135deg, var(--primary-color), var(--primary-light)); border-radius: 50%; display: flex; align-items: center; justify-content: center; font-size: 44rpx; margin: 0 auto 20rpx; }
.login-title { font-size: var(--font-2xl); font-weight: bold; color: var(--text-primary); text-align: center; display: block; }
.login-subtitle { font-size: var(--font-sm); color: var(--text-tertiary); text-align: center; display: block; margin: 8rpx 0 40rpx; }
.login-footer { margin-top: 32rpx; text-align: center; }
.link { font-size: var(--font-base); color: var(--primary-color); }
</style>
