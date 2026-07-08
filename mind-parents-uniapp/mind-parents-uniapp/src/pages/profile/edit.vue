<template>
  <view class="page-container">
    <view class="card">
      <text class="section-title">编辑资料</text>
      <view class="form-item"><text class="form-label">昵称</text><input class="input" v-model="form.nickname" placeholder="请输入昵称" /></view>
      <view class="form-item"><text class="form-label">手机号</text><input class="input" v-model="form.phone" placeholder="请输入手机号" /></view>
      <view class="form-item"><text class="form-label">邮箱</text><input class="input" v-model="form.email" placeholder="请输入邮箱" /></view>
      <button class="btn btn-primary" style="width:100%;margin-top:16px;" @click="doSave" :disabled="saving">{{ saving ? '保存中...' : '保存' }}</button>
    </view>
  </view>
</template>
<script>
import { userApi } from '../../utils/request.js'
export default {
  data() { return { form: { nickname: '', phone: '', email: '' }, saving: false } },
  onLoad() { this.loadProfile() },
  methods: {
    async loadProfile() {
      try { const u = await userApi.getProfile(); if (u) this.form = { nickname: u.nickname || '', phone: u.phone || '', email: u.email || '' } }
      catch (e) {}
    },
    async doSave() {
      this.saving = true
      try { await userApi.updateProfile(this.form); uni.showToast({ title: '保存成功', icon: 'success' }); setTimeout(() => uni.navigateBack(), 1000) }
      catch (e) { uni.showToast({ title: e.message, icon: 'none' }) }
      finally { this.saving = false }
    }
  }
}
</script>
<style scoped>
.section-title { font-size: 16px; font-weight: 600; margin-bottom: 16px; display: block; }
.form-item { margin-bottom: 14px; }
.form-label { font-size: 13px; color: var(--text-hint); display: block; margin-bottom: 6px; }
.input { width: 100%; height: 42px; border: 1px solid var(--border-color); border-radius: 8px; padding: 0 12px; font-size: 14px; }
</style>
