<template>
  <text class="emotion-tag" :class="category || 'neutral'">{{ label }}</text>
</template>
<script>
const LABELS = { happy: '开心', calm: '平静', sad: '难过', anxious: '焦虑', angry: '生气', neutral: '--' }
export default {
  props: { category: { type: String, default: 'neutral' }, score: Number },
  computed: {
    label() { return this.score != null ? `${LABELS[this.category] || this.category} ${this.score > 0 ? '+' : ''}${this.score}` : (LABELS[this.category] || this.category) }
  }
}
</script>
<style scoped>
.emotion-tag { display: inline-block; padding: 2px 10px; border-radius: 12px; font-size: 12px; font-weight: 500; }
.emotion-tag.happy { background: #FFF8E1; color: #F9A825; }
.emotion-tag.calm { background: #E3F2FD; color: #1E88E5; }
.emotion-tag.sad { background: #ECEFF1; color: #546E7A; }
.emotion-tag.anxious { background: #FFF3E0; color: #EF6C00; }
.emotion-tag.angry { background: #FFEBEE; color: #E53935; }
.emotion-tag.neutral { background: #F5F5F5; color: #999; }
</style>
