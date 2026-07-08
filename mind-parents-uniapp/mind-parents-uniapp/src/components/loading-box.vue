<template>
  <view class="loading-overlay" v-if="visible">
    <view class="loading-card animate-fade-in">
      <view class="loading-spinner"></view>
      <text class="loading-text">{{ text || '加载中...' }}</text>
    </view>
  </view>
</template>
<script>
export default { props: { visible: Boolean, text: String } }
</script>
<style scoped>
.loading-overlay { position: fixed; top: 0; left: 0; right: 0; bottom: 0; background: rgba(255,255,255,0.85); display: flex; align-items: center; justify-content: center; z-index: 999; }
.loading-card { background: #fff; border-radius: 16rpx; padding: 48rpx 64rpx; box-shadow: var(--shadow-medium); display: flex; flex-direction: column; align-items: center; gap: 16rpx; }
.loading-spinner { width: 48rpx; height: 48rpx; border: 4rpx solid var(--border-color); border-top-color: var(--primary-color); border-radius: 50%; animation: spin 0.8s linear infinite; }
.loading-text { font-size: var(--font-sm); color: var(--text-tertiary); }
</style>
