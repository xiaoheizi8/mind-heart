<template>
  <view class="page-container">
    <view class="card">
      <text class="card-title mb-24">编辑资料</text>
      <view class="input-group"><text class="input-label">昵称</text><input class="input" v-model="form.nickname" placeholder="请输入昵称" /></view>
      <view class="input-group"><text class="input-label">手机号</text><input class="input" v-model="form.phone" type="number" maxlength="11" placeholder="请输入手机号" /></view>
      <view class="input-group"><text class="input-label">邮箱</text><input class="input" v-model="form.email" placeholder="请输入邮箱" /></view>
      <button class="btn btn-primary btn-block mt-24" @click="doSave" :disabled="saving">{{ saving ? '保存中...' : '保存' }}</button>
    </view>
  </view>
</template>
<script>
import { userApi } from '../../utils/request.js'
export default {
  data() { return { form: { nickname: '', phone: '', email: '' }, saving: false } },
  onLoad() { this.loadProfile() },
  methods: {
    async loadProfile() { try { const u = await userApi.getProfile(); if (u) this.form = { nickname: u.nickname || '', phone: u.phone || '', email: u.email || '' } } catch (e) {} },
    async doSave() {
      if (this.form.phone && !/^1\d{10}$/.test(this.form.phone)) { uni.showToast({ title: '手机号格式不正确', icon: 'none' }); return }
      if (this.form.email && !/@/.test(this.form.email)) { uni.showToast({ title: '邮箱格式不正确', icon: 'none' }); return }
      this.saving = true
      try { await userApi.updateProfile(this.form); uni.showToast({ title: '保存成功', icon: 'success' }); setTimeout(() => uni.navigateBack(), 1000) }
      catch (e) { uni.showToast({ title: e.message, icon: 'none' }) }
      finally { this.saving = false }
    }
  }
}
</script>
