<template>
  <view class="page-container">
    <view class="card" v-if="diary">
      <view class="flex-between" style="margin-bottom:16px;">
        <emotion-badge :category="diary.emotionCategory" :score="diary.emotionScore" />
        <text class="time-text">{{ formatDate(diary.createdAt) }}</text>
      </view>
      <text class="content-text">{{ diary.content }}</text>
      <view v-if="diary.aiAnalysis" style="margin-top:16px;">
        <text class="label">AI分析</text>
        <text class="ai-text">{{ diary.aiAnalysis }}</text>
      </view>
    </view>
    <loading-box :visible="loading" />
  </view>
</template>
<script>
import { parentApi } from '../../utils/request.js'
import EmotionBadge from '../../components/emotion-badge.vue'
import LoadingBox from '../../components/loading-box.vue'
export default {
  components: { EmotionBadge, LoadingBox },
  data() { return { childId: 0, diaryId: 0, diary: null, loading: false } },
  onLoad(opt) { this.childId = Number(opt.childId); this.diaryId = Number(opt.diaryId); this.load() },
  methods: {
    async load() {
      this.loading = true
      try { this.diary = await parentApi.getChildDiaryDetail(this.childId, this.diaryId) }
      catch (e) { uni.showToast({ title: e.message, icon: 'none' }) }
      finally { this.loading = false }
    },
    formatDate(d) { return d ? d.substring(0, 16) : '' }
  }
}
</script>
<style scoped>
.time-text { font-size: 12px; color: var(--text-hint); }
.content-text { font-size: 15px; line-height: 1.7; color: var(--text-primary); }
.label { font-size: 14px; font-weight: 600; display: block; margin-bottom: 8px; }
.ai-text { font-size: 14px; line-height: 1.6; color: var(--text-secondary); background: #F9F9F9; padding: 12px; border-radius: 8px; display: block; }
</style>
