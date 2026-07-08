<template>
  <view class="launch-page flex-center">
    <view class="launch-content">
      <text class="launch-icon">🏠</text>
      <text class="launch-title">心域家长版</text>
      <text class="launch-subtitle">守护孩子的心灵成长</text>
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
    }, 1500)
  }
}
</script>
<style scoped>
.launch-page { min-height: 100vh; background: linear-gradient(135deg, #7B68EE 0%, #9D8FFF 100%); }
.launch-content { text-align: center; }
.launch-icon { font-size: 64px; display: block; margin-bottom: 20px; }
.launch-title { font-size: 28px; color: #fff; font-weight: bold; display: block; margin-bottom: 8px; }
.launch-subtitle { font-size: 16px; color: rgba(255,255,255,0.85); }
</style>
