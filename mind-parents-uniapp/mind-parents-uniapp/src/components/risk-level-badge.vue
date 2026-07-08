<template>
  <text class="risk-tag" :class="levelClass">{{ label }}</text>
</template>
<script>
const LABELS = { 1: '低风险', 2: '中风险', 3: '高风险' }
const CLASSES = { 1: 'low', 2: 'medium', 3: 'high' }
export default {
  props: { level: { type: Number, default: 1 } },
  computed: {
    label() { return LABELS[this.level] || '--' },
    levelClass() { return CLASSES[this.level] || 'low' }
  }
}
</script>
<style scoped>
.risk-tag { display: inline-block; padding: 2px 10px; border-radius: 12px; font-size: 12px; font-weight: 500; }
.risk-tag.high { background: #FFEBEE; color: #E53935; }
.risk-tag.medium { background: #FFF8E1; color: #F9A825; }
.risk-tag.low { background: #E8F5E9; color: #43A047; }
</style>
