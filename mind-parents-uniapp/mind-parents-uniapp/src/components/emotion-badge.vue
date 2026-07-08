<template>
  <text class="emotion-tag" :class="category || 'neutral'" :style="{ fontSize: size === 'small' ? '22rpx' : '26rpx', padding: size === 'small' ? '4rpx 14rpx' : '8rpx 20rpx' }">
    {{ icon }} {{ label }}
  </text>
</template>
<script>
import { EMOTION_MAP } from '../utils/index.js'
export default {
  props: { category: { type: String, default: 'neutral' }, score: Number, size: { type: String, default: 'default' } },
  computed: {
    info() { return EMOTION_MAP[this.category] || { label: '其他', icon: '❓', color: '#999' } },
    icon() { return this.info.icon },
    label() { return this.score != null ? `${this.info.label} ${this.score>0?'+':''}${this.score}` : this.info.label }
  }
}
</script>
