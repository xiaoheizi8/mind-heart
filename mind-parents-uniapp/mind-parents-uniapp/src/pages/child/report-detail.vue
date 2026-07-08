<template>
  <view class="page-container">
    <loading-box :visible="loading" />
    <view v-if="!loading && (report || emotionReport)" class="animate-fade-in">
      <!-- 实时情绪报告 -->
      <template v-if="isLive && emotionReport">
        <view class="card">
          <text class="card-title mb-16">📊 实时情绪报告 ({{ emotionReport.period }})</text>
          <view class="flex-between mb-16"><text class="text-lg">总日记数</text><text class="text-lg font-bold text-primary-color">{{ emotionReport.totalDiaries }}</text></view>
          <view class="flex-between mb-16"><text class="text-lg">平均情绪分</text><text class="text-lg font-bold text-primary-color">{{ emotionReport.avgScore }}</text></view>
        </view>
        <view class="card" v-if="emotionReport.emotionStats">
          <text class="card-title mb-16">情绪分布</text>
          <view v-for="(count, cat) in emotionReport.emotionStats" :key="cat" class="dist-row">
            <emotion-badge :category="cat" size="small" />
            <view class="progress-bar" style="flex:1;margin:0 12rpx;"><view class="progress-fill" :style="{ width: pct(count, emotionReport.totalDiaries) + '%' }"></view></view>
            <text class="text-sm text-hint">{{ count }}篇</text>
          </view>
        </view>
      </template>

      <!-- 历史报告 -->
      <template v-if="!isLive && report">
        <view class="card">
          <text class="card-title mb-16">{{ report.reportType === 'week' ? '周报' : report.reportType === 'month' ? '月报' : '季报' }}</text>
          <view class="detail-row"><text class="detail-label">日记数</text><text>{{ report.diaryCount || 0 }}篇</text></view>
          <view class="detail-row"><text class="detail-label">平均分</text><text class="font-bold text-primary-color">{{ report.avgEmotionScore || '--' }}</text></view>
          <view class="detail-row"><text class="detail-label">主要情绪</text><emotion-badge :category="report.mainEmotion" size="small" /></view>
        </view>
        <view v-if="report.summary" class="card"><text class="card-title mb-16">摘要</text><text class="body-text">{{ report.summary }}</text></view>
        <view v-if="report.aiAnalysis" class="card hero-warm"><text class="card-title mb-16">🤖 AI分析</text><text class="body-text">{{ report.aiAnalysis }}</text></view>
      </template>
    </view>
  </view>
</template>
<script>
import { parentApi } from '../../utils/request.js'
import EmotionBadge from '../../components/emotion-badge.vue'
import LoadingBox from '../../components/loading-box.vue'
export default {
  components: { EmotionBadge, LoadingBox },
  data() { return { childId: 0, reportId: 0, isLive: false, report: null, emotionReport: null, loading: false } },
  onLoad(opt) { this.childId = Number(opt.childId); this.isLive = opt.live === 'true'; if (!this.isLive) this.reportId = Number(opt.reportId || 0); this.load() },
  methods: {
    async load() { this.loading = true; try { if (this.isLive) this.emotionReport = await parentApi.getChildEmotionReport(this.childId, 7); else this.report = await parentApi.getChildReportDetail(this.childId, this.reportId) } catch (e) {} finally { this.loading = false } },
    pct(count, total) { return total ? Math.round(count / total * 100) : 0 }
  }
}
</script>
<style scoped>
.detail-row { display: flex; justify-content: space-between; align-items: center; padding: 16rpx 0; border-bottom: 1rpx solid var(--border-light); }
.detail-label { color: var(--text-tertiary); }
.dist-row { display: flex; align-items: center; padding: 10rpx 0; }
.body-text { font-size: var(--font-base); line-height: 1.8; color: var(--text-secondary); display: block; }
</style>
