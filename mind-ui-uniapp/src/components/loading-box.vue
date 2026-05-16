<template>
  <view class="loading-mask" v-if="show">
    <view class="loading-box">
      <view class="loading-spinner"></view>
      <text class="loading-text">{{ text }}</text>
    </view>
  </view>
</template>

<script>
export default {
  name: 'LoadingBox',
  props: {
    show: { type: Boolean, default: false },
    text: { type: String, default: '加载中...' }
  }
}
</script>

<style scoped>
.loading-mask {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.5);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 9999;
}

.loading-box {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 48rpx;
  background: var(--bg-primary);
  border-radius: 16rpx;
}

.loading-spinner {
  width: 64rpx;
  height: 64rpx;
  border: 6rpx solid var(--border-color);
  border-top-color: var(--primary-color);
  border-radius: 50%;
  animation: spin 0.8s linear infinite;
}

@keyframes spin {
  to { transform: rotate(360deg); }
}

.loading-text {
  font-size: 28rpx;
  color: var(--text-secondary);
  margin-top: 24rpx;
}
</style>