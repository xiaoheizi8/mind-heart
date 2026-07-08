<template>
  <view class="page-container">
    <view class="profile-header">
      <view class="avatar-circle">{{ (user.nickname || user.username || '?')[0] }}</view>
      <text class="profile-name">{{ user.nickname || user.username || '--' }}</text>
      <text class="profile-role">家长</text>
    </view>

    <view class="menu-list">
      <view class="menu-item" @click="goEdit">
        <text>编辑资料</text><text class="arrow">></text>
      </view>
      <view class="menu-item" @click="goSettings">
        <text>设置</text><text class="arrow">></text>
      </view>
    </view>

    <view style="padding:20px;">
      <button class="btn btn-danger" style="width:100%;" @click="doLogout">退出登录</button>
    </view>
  </view>
</template>
<script>
import { userApi } from '../../utils/request.js'
export default {
  data() { return { user: {} } },
  onShow() { this.loadProfile() },
  methods: {
    async loadProfile() {
      try { this.user = await userApi.getProfile() || {} }
      catch (e) { console.error('加载失败:', e) }
    },
    goEdit() { uni.navigateTo({ url: '/pages/profile/edit' }) },
    goSettings() { uni.navigateTo({ url: '/pages/settings/index' }) },
    doLogout() {
      uni.showModal({
        title: '退出登录', content: '确定要退出吗？',
        success: (res) => {
          if (res.confirm) {
            uni.removeStorageSync('token')
            uni.removeStorageSync('userInfo')
            uni.reLaunch({ url: '/pages/login/login' })
          }
        }
      })
    }
  }
}
</script>
<style scoped>
.profile-header { background: var(--primary-color); padding: 40px 20px 30px; display: flex; flex-direction: column; align-items: center; color: #fff; }
.avatar-circle { width: 64px; height: 64px; border-radius: 50%; background: rgba(255,255,255,0.3); display: flex; align-items: center; justify-content: center; font-size: 24px; font-weight: bold; margin-bottom: 12px; }
.profile-name { font-size: 18px; font-weight: bold; }
.profile-role { font-size: 13px; opacity: 0.8; margin-top: 4px; }
.menu-list { margin-top: 12px; }
.menu-item { display: flex; justify-content: space-between; align-items: center; padding: 14px 20px; background: #fff; border-bottom: 1px solid var(--border-color); font-size: 15px; }
.arrow { color: var(--text-hint); font-size: 16px; }
</style>
