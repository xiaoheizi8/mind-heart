<template>
  <view class="page-container flex-center" style="flex-direction:column;padding:40rpx;">
    <view class="reg-card animate-fade-in">
      <text class="reg-title">家长注册</text>
      <view class="input-group"><input class="input" v-model="form.username" placeholder="用户名" /></view>
      <view class="input-group"><input class="input" v-model="form.phone" placeholder="手机号" type="number" maxlength="11" /></view>
      <view class="input-group"><view class="input-wrap"><input class="input" :type="showPwd?'text':'password'" v-model="form.password" placeholder="密码(6-20位)" /><text class="input-icon" @click="showPwd=!showPwd">{{ showPwd?'🙈':'👁️' }}</text></view></view>
      <view class="input-group"><input class="input" :type="showPwd?'text':'password'" v-model="form.confirmPassword" placeholder="确认密码" /></view>
      <view class="input-group"><input class="input" v-model="form.email" placeholder="邮箱(选填)" /></view>
      <button class="btn btn-primary btn-block mt-16" @click="doRegister" :disabled="loading">
        <text v-if="!loading">注 册</text><text v-else>注册中...</text>
      </button>
      <view class="reg-footer"><text class="link" @click="goLogin">已有账号？去登录</text></view>
    </view>
  </view>
</template>
<script>
import { authApi } from '../../utils/request.js'
export default {
  data() { return { form: { username: '', phone: '', password: '', confirmPassword: '', email: '' }, showPwd: false, loading: false } },
  methods: {
    async doRegister() {
      const f = this.form
      if (!f.username || !f.password) { uni.showToast({ title: '请填写用户名和密码', icon: 'none' }); return }
      if (f.password.length < 6) { uni.showToast({ title: '密码至少6位', icon: 'none' }); return }
      if (f.password !== f.confirmPassword) { uni.showToast({ title: '两次密码不一致', icon: 'none' }); return }
      if (f.phone && !/^1\d{10}$/.test(f.phone)) { uni.showToast({ title: '手机号格式不正确', icon: 'none' }); return }
      this.loading = true
      try {
        await authApi.register({ username: f.username, password: f.password, phone: f.phone, email: f.email })
        uni.showToast({ title: '注册成功', icon: 'success' })
        setTimeout(async () => {
          try {
            const res = await authApi.login({ username: f.username, password: f.password })
            uni.setStorageSync('token', res.token)
            uni.setStorageSync('userInfo', res.userInfo || res)
            uni.switchTab({ url: '/pages/index/index' })
          } catch (e) { uni.redirectTo({ url: '/pages/login/login' }) }
        }, 800)
      } catch (e) { uni.showToast({ title: e.message || '注册失败', icon: 'none' }) }
      finally { this.loading = false }
    },
    goLogin() { uni.redirectTo({ url: '/pages/login/login' }) }
  }
}
</script>
<style scoped>
.reg-card { width: 85%; max-width: 640rpx; background: #fff; border-radius: var(--radius-xl); padding: 48rpx 40rpx; box-shadow: var(--shadow-medium); }
.reg-title { font-size: var(--font-2xl); font-weight: bold; color: var(--primary-color); text-align: center; display: block; margin-bottom: 32rpx; }
.reg-footer { margin-top: 24rpx; text-align: center; }
.link { font-size: var(--font-base); color: var(--primary-color); }
</style>
