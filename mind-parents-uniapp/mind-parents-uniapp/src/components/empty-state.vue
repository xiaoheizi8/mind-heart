<template>
  <view class="empty-state">
    <text class="empty-icon">{{ icon || '📭' }}</text>
    <text class="empty-text">{{ text || '暂无数据' }}</text>
    <text class="empty-desc" v-if="desc">{{ desc }}</text>
    <slot name="action">
      <button v-if="actionText" class="btn btn-primary btn-sm mt-24" @click="$emit('action')">{{ actionText }}</button>
    </slot>
  </view>
</template>
<script>
export default {
  props: { icon: String, text: String, desc: String, actionText: String },
  emits: ['action']
}
</script>
