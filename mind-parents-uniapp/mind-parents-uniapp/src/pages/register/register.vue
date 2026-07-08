<template>
  <view class="page-container flex-center" style="flex-direction:column;">
    <view class="reg-card">
      <text class="reg-title">家长注册</text>
      <input class="input" v-model="form.username" placeholder="用户名" />
      <input class="input" v-model="form.password" type="password" placeholder="密码(6-20位)" />
      <input class="input" v-model="form.confirmPassword" type="password" placeholder="确认密码" />
      <input class="input" v-model="form.email" placeholder="邮箱(选填)" />
      <button class="btn btn-primary reg-btn" @click="doRegister" :disabled="loading">{{ loading ? '注册中...' : '注 册' }}</button>
      <view class="reg-link"><text class="link" @click="goLogin">已有账号？去登录</text></view>
    </view>
  </view>
</template>
<script>
import { authApi } from '../../utils/request.js'
export default {
  data() { return { form: { username: '', password: '', confirmPassword: '', email: '' }, loading: false } },
  methods: {
    async doRegister() {
      const { username, password, confirmPassword } = this.form
      if (!username || !password) { uni.showToast({ title: '请填写完整', icon: 'none' }); return }
      if (password.length < 6) { uni.showToast({ title: '密码至少6位', icon: 'none' }); return }
      if (password !== confirmPassword) { uni.showToast({ title: '两次密码不一致', icon: 'none' }); return }
      this.loading = true
      try {
        await authApi.register({ username, password: password, email: this.form.email })
        uni.showToast({ title: '注册成功', icon: 'success' })
        setTimeout(() => uni.redirectTo({ url: '/pages/login/login' }), 1000)
      } catch (e) {
        uni.showToast({ title: e.message || '注册失败', icon: 'none' })
      } finally { this.loading = false }
    },
    goLogin() { uni.redirectTo({ url: '/pages/login/login' }) }
  }
}
</script>
<style scoped>
.reg-card { width: 85%; max-width: 360px; background: #fff; border-radius: 16px; padding: 40px 30px; box-shadow: var(--shadow); }
.reg-title { font-size: 22px; font-weight: bold; color: var(--primary-color); text-align: center; display: block; margin-bottom: 24px; }
.input { width: 100%; height: 46px; border: 1px solid var(--border-color); border-radius: 8px; padding: 0 14px; font-size: 15px; margin-bottom: 14px; }
.reg-btn { width: 100%; height: 46px; margin-top: 8px; }
.reg-link { margin-top: 16px; text-align: center; }
.link { font-size: 14px; color: var(--primary-color); }
</style>
