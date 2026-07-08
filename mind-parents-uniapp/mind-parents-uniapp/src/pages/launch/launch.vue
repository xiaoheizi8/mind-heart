<template>
  <view class="launch-page flex-center">
    <view class="launch-content animate-fade-up">
      <view class="launch-logo">🏠</view>
      <text class="launch-title">心域家长版</text>
      <text class="launch-subtitle">守护孩子的心灵成长</text>
      <text class="launch-version">v1.0.0</text>
    </view>
  </view>
</template>
<script>
export default {
  onLoad() {
    setTimeout(() => {
      const token = uni.getStorageSync('token')
      if (token) uni.switchTab({ url: '/pages/index/index' })
      else uni.redirectTo({ url: '/pages/login/login' })
    }, 2000)
  }
}
</script>
<style scoped>
.launch-page { min-height: 100vh; background: linear-gradient(135deg, #7B68EE 0%, #9D8FFF 100%); }
.launch-content { text-align: center; }
.launch-logo { width: 120rpx; height: 120rpx; background: rgba(255,255,255,0.2); border-radius: 50%; display: flex; align-items: center; justify-content: center; font-size: 56rpx; margin: 0 auto 24rpx; }
.launch-title { font-size: 40rpx; color: #fff; font-weight: bold; display: block; margin-bottom: 8rpx; }
.launch-subtitle { font-size: 28rpx; color: rgba(255,255,255,0.85); display: block; margin-bottom: 24rpx; }
.launch-version { font-size: 22rpx; color: rgba(255,255,255,0.5); }
</style>
