<template>
  <view class="empty-state">
    <text class="empty-icon">{{ icon || '📭' }}</text>
    <text class="empty-text">{{ text || '暂无数据' }}</text>
    <text class="empty-desc" v-if="desc">{{ desc }}</text>
  </view>
</template>
<script>
export default {
  props: { icon: String, text: String, desc: String }
}
</script>
<style scoped>
.empty-state { display: flex; flex-direction: column; align-items: center; justify-content: center; padding: 60px 40px; }
.empty-icon { font-size: 48px; margin-bottom: 16px; }
.empty-text { font-size: 16px; color: var(--text-secondary); margin-bottom: 8px; }
.empty-desc { font-size: 13px; color: var(--text-hint); text-align: center; }
</style>
