<template>
  <view class="load-more">
    <text v-if="status === 'loading'" class="load-text">加载中...</text>
    <text v-else-if="status === 'noMore'" class="load-text">没有更多了</text>
    <text v-else-if="status === 'more'" class="load-text load-more-btn" @click="$emit('load')">点击加载更多</text>
  </view>
</template>
<script>
export default { props: { status: { type: String, default: 'more' } }, emits: ['load'] }
</script>
<style scoped>
.load-more { display: flex; align-items: center; justify-content: center; padding: 16px; }
.load-text { font-size: 13px; color: var(--text-hint); }
.load-more-btn { color: var(--primary-color); }
</style>
