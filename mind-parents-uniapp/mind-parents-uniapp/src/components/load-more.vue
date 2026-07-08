<template>
  <view class="load-more">
    <view v-if="status === 'loading'" class="load-spinner"></view>
    <text v-if="status === 'loading'" class="load-text">加载中...</text>
    <text v-else-if="status === 'noMore'" class="load-text">— 没有更多了 —</text>
    <text v-else-if="status === 'more'" class="load-text load-more-btn" @click="$emit('load')">点击加载更多</text>
  </view>
</template>
<script>
export default { props: { status: { type: String, default: 'more' } }, emits: ['load'] }
</script>
<style scoped>
.load-more { display: flex; align-items: center; justify-content: center; padding: 24rpx; gap: 12rpx; }
.load-spinner { width: 28rpx; height: 28rpx; border: 3rpx solid var(--border-color); border-top-color: var(--primary-color); border-radius: 50%; animation: spin 0.8s linear infinite; }
.load-text { font-size: var(--font-sm); color: var(--text-tertiary); }
.load-more-btn { color: var(--primary-color); }
</style>
