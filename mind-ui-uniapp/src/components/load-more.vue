<template>
  <view class="loading-more" v-if="loading">
    <view class="loading-spinner"></view>
    <text class="loading-text">加载中...</text>
  </view>
  <view class="no-more" v-else-if="finished">
    <text class="no-more-text">没有更多了</text>
  </view>
</template>

<script>
export default {
  name: 'LoadMore',
  props: {
    loading: { type: Boolean, default: false },
    finished: { type: Boolean, default: false }
  }
}
</script>

<style scoped>
.loading-more {
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 32rpx;
}

.loading-spinner {
  width: 32rpx;
  height: 32rpx;
  border: 4rpx solid var(--border-color);
  border-top-color: var(--primary-color);
  border-radius: 50%;
  animation: spin 0.8s linear infinite;
}

@keyframes spin {
  to { transform: rotate(360deg); }
}

.loading-text {
  font-size: 26rpx;
  color: var(--text-tertiary);
  margin-left: 16rpx;
}

.no-more {
  text-align: center;
  padding: 32rpx;
}

.no-more-text {
  font-size: 26rpx;
  color: var(--text-tertiary);
}
</style>