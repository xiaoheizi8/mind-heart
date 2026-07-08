<template>
  <text class="risk-tag" :class="cls" :style="{ animation: level >= 3 ? 'pulse 2s ease-in-out infinite' : 'none' }">
    {{ icon }} {{ label }}
  </text>
</template>
<script>
import { RISK_MAP } from '../utils/index.js'
export default {
  props: { level: { type: Number, default: 1 } },
  computed: {
    info() { return RISK_MAP[this.level] || { label: '--', icon: '⚪', color: '#999' } },
    icon() { return this.info.icon },
    label() { return this.info.label },
    cls() { return this.level >= 3 ? 'high' : this.level === 2 ? 'medium' : 'low' }
  }
}
</script>
