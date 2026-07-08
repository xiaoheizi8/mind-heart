<template>
  <view class="page-container">
    <loading-box :visible="loading" />
    <view v-if="diary" class="animate-fade-in">
      <view class="card">
        <view class="flex-between mb-24"><emotion-badge :category="diary.emotionCategory" :score="diary.emotionScore" /><text class="text-sm text-hint">{{ formatDate(diary.createdAt) }}</text></view>
        <text class="diary-content">{{ diary.content }}</text>
      </view>
      <view v-if="diary.aiAnalysis" class="card hero-warm">
        <view class="flex-center gap-8 mb-16"><text style="font-size:32rpx;">🤖</text><text class="card-title">AI 解读</text></view>
        <text class="ai-text">{{ diary.aiAnalysis }}</text>
      </view>
    </view>
  </view>
</template>
<script>
import { parentApi } from '../../utils/request.js'
import { formatDate } from '../../utils/index.js'
import EmotionBadge from '../../components/emotion-badge.vue'
import LoadingBox from '../../components/loading-box.vue'
export default {
  components: { EmotionBadge, LoadingBox },
  data() { return { childId: 0, diaryId: 0, diary: null, loading: false } },
  onLoad(opt) { this.childId = Number(opt.childId); this.diaryId = Number(opt.diaryId); this.load() },
  methods: {
    async load() { this.loading = true; try { this.diary = await parentApi.getChildDiaryDetail(this.childId, this.diaryId) } catch (e) {} finally { this.loading = false } },
    formatDate
  }
}
</script>
<style scoped>
.diary-content { font-size: 30rpx; line-height: 1.8; color: var(--text-primary); display: block; }
.ai-text { font-size: var(--font-base); line-height: 1.7; color: var(--text-secondary); display: block; }
</style>
