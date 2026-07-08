<template>
  <view class="page-container">
    <loading-box :visible="loading" />

    <view class="card" v-if="!loading && isLive && emotionReport">
      <text class="detail-title">实时情绪报告</text>
      <text class="stat-text">近{{ emotionReport.period }}日记数: {{ emotionReport.totalDiaries }}</text>
      <text class="stat-text">平均情绪分: {{ emotionReport.avgScore }}</text>
      <view class="stats-row" v-if="emotionReport.emotionStats">
        <text class="stat-item" v-for="(count, cat) in emotionReport.emotionStats" :key="cat">
          <emotion-badge :category="cat" /> x{{ count }}
        </text>
      </view>
    </view>

    <view class="card" v-if="!loading && !isLive && report">
      <text class="detail-title">情绪报告</text>
      <view class="detail-row"><text class="label">类型</text><text>{{ report.reportType || '--' }}</text></view>
      <view class="detail-row"><text class="label">日记数</text><text>{{ report.diaryCount || 0 }}</text></view>
      <view class="detail-row"><text class="label">平均分</text><text>{{ report.avgEmotionScore || '--' }}</text></view>
      <view class="detail-row"><text class="label">主要情绪</text><text>{{ report.mainEmotion || '--' }}</text></view>
      <view v-if="report.summary" style="margin-top:12px;">
        <text class="label">摘要</text>
        <text class="ai-text">{{ report.summary }}</text>
      </view>
      <view v-if="report.aiAnalysis" style="margin-top:12px;">
        <text class="label">AI分析</text>
        <text class="ai-text">{{ report.aiAnalysis }}</text>
      </view>
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
  onLoad(opt) {
    this.childId = Number(opt.childId)
    this.isLive = opt.live === 'true'
    if (this.isLive) this.reportId = 0
    else this.reportId = Number(opt.reportId || 0)
    this.loadDetail()
  },
  methods: {
    async loadDetail() {
      this.loading = true
      try {
        if (this.isLive) this.emotionReport = await parentApi.getChildEmotionReport(this.childId, 7)
        else this.report = await parentApi.getChildReportDetail(this.childId, this.reportId)
      } catch (e) { uni.showToast({ title: e.message, icon: 'none' }) }
      finally { this.loading = false }
    }
  }
}
</script>
<style scoped>
.detail-title { font-size: 18px; font-weight: bold; margin-bottom: 16px; display: block; }
.detail-row { display: flex; justify-content: space-between; padding: 8px 0; border-bottom: 1px solid var(--border-color); }
.label { font-size: 14px; color: var(--text-hint); }
.stat-text { font-size: 14px; display: block; margin: 8px 0; }
.stats-row { display: flex; flex-wrap: wrap; gap: 8px; margin-top: 12px; }
.stat-item { display: flex; align-items: center; gap: 4px; font-size: 13px; }
.ai-text { font-size: 14px; line-height: 1.6; color: var(--text-secondary); background: #F9F9F9; padding: 12px; border-radius: 8px; display: block; margin-top: 8px; }
</style>
