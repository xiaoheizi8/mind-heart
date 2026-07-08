<template>
  <view class="loading-overlay" v-if="visible">
    <view class="loading-spinner"></view>
    <text class="loading-text">{{ text || '加载中...' }}</text>
  </view>
</template>
<script>
export default { props: { visible: Boolean, text: String } }
</script>
<style scoped>
.loading-overlay { position: fixed; top: 0; left: 0; right: 0; bottom: 0; background: rgba(255,255,255,0.9); display: flex; flex-direction: column; align-items: center; justify-content: center; z-index: 999; }
.loading-spinner { width: 40px; height: 40px; border: 3px solid var(--border-color); border-top-color: var(--primary-color); border-radius: 50%; animation: spin 0.8s linear infinite; margin-bottom: 12px; }
@keyframes spin { to { transform: rotate(360deg); } }
.loading-text { font-size: 14px; color: var(--text-hint); }
</style>
