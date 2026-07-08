<template>
  <view class="page-container">
    <view class="overview-header">
      <text class="child-name">{{ childName }}</text>
    </view>

    <loading-box :visible="loading" />

    <view v-if="!loading">
      <view class="card">
        <text class="card-title">情绪概览</text>
        <view class="flex-center" style="padding:12px 0;">
          <text style="font-size:32px;">{{ overview.avgEmotionScore > 0.5 ? '😊' : overview.avgEmotionScore > 0 ? '🙂' : overview.avgEmotionScore > -0.5 ? '😐' : '😔' }}</text>
        </view>
        <text class="stat-label">近7天平均情绪分: {{ overview.avgEmotionScore }}</text>
      </view>

      <view class="card">
        <view class="flex-between">
          <text class="card-title">预警记录</text>
          <text class="link" @click="goWarnings">查看全部</text>
        </view>
        <text class="stat-label">共 {{ overview.warningCount || 0 }} 条</text>
        <view class="warning-item" v-for="w in (overview.recentWarnings || []).slice(0,3)" :key="w.id">
          <view class="flex-between">
            <risk-level-badge :level="w.riskLevel" />
            <text class="time-text">{{ formatDate(w.createdAt) }}</text>
          </view>
          <text class="ellipsis-2" style="font-size:13px;color:var(--text-secondary);margin-top:4px;">{{ w.content }}</text>
        </view>
        <empty-state v-if="!overview.warningCount" icon="✅" text="暂无预警" />
      </view>

      <view class="card">
        <view class="flex-between">
          <text class="card-title">最近日记</text>
          <text class="link" @click="goDiaries">查看全部</text>
        </view>
        <view class="diary-item" v-for="d in (overview.recentDiaries || []).slice(0,3)" :key="d.id" @click="goDiaryDetail(d.id)">
          <view class="flex-between">
            <emotion-badge :category="d.emotionCategory" :score="d.emotionScore" />
            <text class="time-text">{{ formatDate(d.createdAt) }}</text>
          </view>
          <text class="ellipsis-2" style="font-size:14px;margin-top:4px;">{{ d.content }}</text>
        </view>
        <empty-state v-if="(overview.recentDiaries||[]).length===0" icon="📔" text="暂无日记" />
      </view>

      <view style="display:flex;gap:12px;padding:0 16px 20px;">
        <button class="btn btn-primary" style="flex:1;" @click="goReports">情绪报告</button>
        <button class="btn" style="flex:1;background:#F5F5F5;" @click="goEmotionReport">实时情绪</button>
      </view>
    </view>
  </view>
</template>
<script>
import { parentApi } from '../../utils/request.js'
import EmotionBadge from '../../components/emotion-badge.vue'
import RiskLevelBadge from '../../components/risk-level-badge.vue'
import EmptyState from '../../components/empty-state.vue'
import LoadingBox from '../../components/loading-box.vue'
export default {
  components: { EmotionBadge, RiskLevelBadge, EmptyState, LoadingBox },
  data() { return { childId: 0, childName: '', loading: true, overview: {} } },
  onLoad(opt) { this.childId = Number(opt.childId); this.childName = opt.name || ''; this.loadOverview() },
  methods: {
    async loadOverview() {
      this.loading = true
      try { this.overview = await parentApi.getChildOverview(this.childId) || {} }
      catch (e) { uni.showToast({ title: e.message, icon: 'none' }) }
      finally { this.loading = false }
    },
    formatDate(d) { return d ? d.substring(0, 10) : '' },
    goWarnings() { uni.navigateTo({ url: `/pages/child/warnings?childId=${this.childId}` }) },
    goDiaries() { uni.navigateTo({ url: `/pages/child/diaries?childId=${this.childId}` }) },
    goDiaryDetail(id) { uni.navigateTo({ url: `/pages/child/diary-detail?childId=${this.childId}&diaryId=${id}` }) },
    goReports() { uni.navigateTo({ url: `/pages/child/reports?childId=${this.childId}` }) },
    goEmotionReport() { uni.navigateTo({ url: `/pages/child/report-detail?childId=${this.childId}&live=true` }) }
  }
}
</script>
<style scoped>
.overview-header { background: var(--primary-color); padding: 16px 20px; color: #fff; }
.child-name { font-size: 18px; font-weight: bold; }
.card-title { font-size: 16px; font-weight: 600; }
.stat-label { font-size: 14px; color: var(--text-secondary); text-align: center; display: block; margin-top: 8px; }
.time-text { font-size: 12px; color: var(--text-hint); }
.link { font-size: 13px; color: var(--primary-color); }
.warning-item, .diary-item { padding: 10px 0; border-bottom: 1px solid var(--border-color); }
.ellipsis-2 { display: -webkit-box; -webkit-line-clamp: 2; -webkit-box-orient: vertical; overflow: hidden; }
</style>
