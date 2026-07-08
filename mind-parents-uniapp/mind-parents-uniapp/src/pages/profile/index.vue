<template>
  <view class="page-container">
    <!-- 头部 -->
    <view class="hero-gradient">
      <view class="flex-center" style="flex-direction:column;padding:24rpx 0;">
        <view class="profile-avatar">{{ (user.nickname || user.username || '?')[0] }}</view>
        <text class="profile-name mt-16">{{ user.nickname || user.username || '--' }}</text>
        <text class="profile-role">家长</text>
        <text class="text-sm mt-8" style="opacity:0.7;">{{ user.phone || '未绑定手机号' }}</text>
        <button class="btn btn-sm mt-16" style="background:rgba(255,255,255,0.2);color:#fff;border:2rpx solid rgba(255,255,255,0.4);" @click="goEdit">编辑资料</button>
      </view>
    </view>

    <!-- 统计 -->
    <view class="stats-bar card">
      <view class="stat-item" @click="goBind">
        <text class="stat-num">{{ childrenCount }}</text><text class="stat-label">绑定孩子</text>
      </view>
      <view class="stat-divider"></view>
      <view class="stat-item">
        <text class="stat-num">{{ totalWarnings || 0 }}</text><text class="stat-label">待处理预警</text>
      </view>
    </view>

    <!-- 菜单 -->
    <view class="card card-sm" style="margin-top:0;border-radius:0 0 24rpx 24rpx;">
      <view class="menu-item" @click="goBind"><text>👨‍👩‍👧</text><text class="menu-label">绑定管理</text><text class="menu-arrow">›</text></view>
      <view class="menu-item"><text>📋</text><text class="menu-label">消息通知</text><text class="menu-arrow">›</text></view>
      <view class="menu-item" @click="goSettings"><text>🔔</text><text class="menu-label">预警设置</text><text class="menu-arrow">›</text></view>
      <view class="menu-item"><text>📖</text><text class="menu-label">使用帮助</text><text class="menu-arrow">›</text></view>
      <view class="menu-item"><text>ℹ️</text><text class="menu-label">关于我们</text><text class="menu-text">v1.0.0</text></view>
    </view>

    <!-- 紧急援助 -->
    <view class="card hero-emergency mt-24" @click="callHotline">
      <view class="flex-center gap-12">
        <text style="font-size:40rpx;">🚨</text>
        <view>
          <text style="font-weight:600;display:block;">心理援助热线</text>
          <text style="font-size:22rpx;opacity:0.85;">青少年心理援助: 12355</text>
        </view>
      </view>
    </view>

    <!-- 退出 -->
    <view style="padding: 32rpx 24rpx;">
      <button class="btn btn-block" style="color:var(--error-color);background:#fff;" @click="doLogout">退出登录</button>
    </view>
  </view>
</template>
<script>
import { userApi, parentApi } from '../../utils/request.js'
export default {
  data() { return { user: {}, childrenCount: 0, totalWarnings: 0 } },
  onShow() { this.loadProfile() },
  methods: {
    async loadProfile() {
      try {
        this.user = (await userApi.getProfile()) || {}
        const children = (await parentApi.getChildren()) || []
        this.childrenCount = children.length
        this.totalWarnings = children.filter(c => c.hasHighRiskWarning).length
      } catch (e) {}
    },
    goEdit() { uni.navigateTo({ url: '/pages/profile/edit' }) },
    goSettings() { uni.navigateTo({ url: '/pages/settings/index' }) },
    goBind() { uni.switchTab({ url: '/pages/bind/bind' }) },
    callHotline() { uni.makePhoneCall({ phoneNumber: '12355' }) },
    doLogout() {
      uni.showModal({ title: '退出登录', content: '确定要退出吗？', success: (res) => {
        if (res.confirm) { uni.removeStorageSync('token'); uni.removeStorageSync('userInfo'); uni.reLaunch({ url: '/pages/login/login' }) }
      }})
    }
  }
}
</script>
<style scoped>
.profile-avatar { width: 128rpx; height: 128rpx; border-radius: 50%; background: rgba(255,255,255,0.25); border: 4rpx solid rgba(255,255,255,0.4); display: flex; align-items: center; justify-content: center; font-size: 52rpx; font-weight: bold; color: #fff; }
.profile-name { font-size: var(--font-xl); font-weight: bold; }
.profile-role { font-size: var(--font-sm); opacity: 0.8; margin-top: 4rpx; }
.stats-bar { display: flex; border-radius: 24rpx 24rpx 0 0; margin-bottom: 0; }
.stat-item { flex: 1; text-align: center; padding: 12rpx 0; }
.stat-num { font-size: var(--font-2xl); font-weight: bold; color: var(--primary-color); display: block; }
.stat-label { font-size: var(--font-xs); color: var(--text-tertiary); margin-top: 4rpx; display: block; }
.stat-divider { width: 1rpx; background: var(--border-light); margin: 8rpx 0; }
.menu-item { display: flex; align-items: center; gap: 16rpx; padding: 20rpx 0; border-bottom: 1rpx solid var(--border-light); font-size: var(--font-base); }
.menu-item:last-child { border-bottom: none; }
.menu-label { flex: 1; }
.menu-arrow { font-size: 28rpx; color: var(--text-disabled); }
.menu-text { color: var(--text-tertiary); font-size: var(--font-sm); }
</style>
