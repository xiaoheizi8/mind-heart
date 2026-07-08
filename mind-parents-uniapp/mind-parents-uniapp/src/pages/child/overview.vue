<template>
  <view class="page-container">
    <!-- 头部 -->
    <view class="hero-gradient-sm">
      <text class="overview-name">{{ childName }}</text>
      <text class="text-sm mt-8" style="opacity:0.85;">{{ activity.streak > 0 ? '已连续写日记 ' + activity.streak + ' 天 🔥' : '鼓励孩子开始记录心情' }}</text>
    </view>

    <loading-box :visible="loading" />

    <view v-if="!loading">
      <!-- 情绪趋势图 -->
      <view class="card">
        <view class="card-header">
          <text class="card-title">📈 情绪走势</text>
          <view class="filter-bar" style="padding:0;">
            <text class="filter-pill" :class="{ active: period === 7 }" @click="switchPeriod(7)">7天</text>
            <text class="filter-pill" :class="{ active: period === 14 }" @click="switchPeriod(14)">14天</text>
            <text class="filter-pill" :class="{ active: period === 30 }" @click="switchPeriod(30)">30天</text>
          </view>
        </view>
        <canvas canvas-id="emotionChart" id="emotionChart" style="width:100%;height:280rpx;" />
        <text class="text-center text-sm text-hint mt-16" style="display:block;">
          平均情绪分 {{ avgScore }} · {{ avgScore > 0.5 ? '整体积极 😊' : avgScore > 0 ? '偏平稳 😐' : avgScore > -0.5 ? '略有低落 😔' : '需关注 🚨' }}
        </text>
      </view>

      <!-- 情绪分布 -->
      <view class="card">
        <text class="card-title mb-16">🎨 情绪分布</text>
        <view v-if="distItems.length > 0">
          <view class="dist-row" v-for="item in distItems" :key="item.cat">
            <text class="dist-icon">{{ item.icon }}</text>
            <text class="dist-label">{{ item.label }}</text>
            <view class="progress-bar" style="flex:1;margin:0 12rpx;"><view class="progress-fill" :style="{ width: item.pct + '%', background: item.color }"></view></view>
            <text class="dist-pct">{{ item.pct }}%</text>
          </view>
        </view>
        <empty-state v-else icon="📊" text="暂无情绪数据" />
      </view>

      <!-- 预警概览 -->
      <view class="card">
        <view class="card-header">
          <text class="card-title">⚠️ 预警记录</text>
          <text class="card-link" @click="goWarnings">查看全部 →</text>
        </view>
        <view class="warn-summary mb-16">
          <text class="risk-tag high mr-8">高风险 {{ warnStats.high }}</text>
          <text class="risk-tag medium mr-8">中风险 {{ warnStats.medium }}</text>
          <text class="risk-tag low">低风险 {{ warnStats.low }}</text>
        </view>
        <view class="warn-item" v-for="w in recentWarnings.slice(0,3)" :key="w.id" @click="goWarningDetail(w.id)">
          <view class="flex-between"><risk-level-badge :level="w.riskLevel" /><text class="text-sm text-hint">{{ formatRel(w.createdAt) }}</text></view>
          <text class="text-ellipsis-2 text-sm text-secondary mt-8">{{ w.content }}</text>
        </view>
        <empty-state v-if="!warnStats.total" icon="✅" text="暂无预警记录" />
      </view>

      <!-- 最近日记 -->
      <view class="card">
        <view class="card-header">
          <text class="card-title">📔 最近日记</text>
          <text class="card-link" @click="goDiaries">查看全部 →</text>
        </view>
        <view class="diary-item" v-for="d in recentDiaries.slice(0,3)" :key="d.id" @click="goDiaryDetail(d.id)">
          <view class="flex-between"><emotion-badge :category="d.emotionCategory" :score="d.emotionScore" size="small" /><text class="text-sm text-hint">{{ formatRel(d.createdAt) }}</text></view>
          <text class="text-ellipsis-2 text-base mt-8">{{ d.content }}</text>
        </view>
        <empty-state v-if="!recentDiaries.length" icon="📝" text="暂无日记" desc="鼓励孩子开始记录吧" />
      </view>

      <!-- 操作按钮 -->
      <view class="flex-center gap-16" style="padding:0 24rpx 40rpx;">
        <button class="btn btn-outline btn-sm" @click="goReports">📊 情绪报告</button>
        <button class="btn btn-primary btn-sm" @click="generateReport">📝 生成周报</button>
      </view>
    </view>
  </view>
</template>
<script>
import { parentApi } from '../../utils/request.js'
import { drawChart, formatRelativeTime } from '../../utils/index.js'
import EmotionBadge from '../../components/emotion-badge.vue'
import RiskLevelBadge from '../../components/risk-level-badge.vue'
import EmptyState from '../../components/empty-state.vue'
import LoadingBox from '../../components/loading-box.vue'
export default {
  components: { EmotionBadge, RiskLevelBadge, EmptyState, LoadingBox },
  data() { return { childId: 0, childName: '', loading: true, period: 7, overview: {}, recentDiaries: [], recentWarnings: [], avgScore: 0, distItems: [], warnStats: { total: 0, high: 0, medium: 0, low: 0 }, activity: { streak: 0 } } },
  onLoad(opt) { this.childId = Number(opt.childId); this.childName = decodeURIComponent(opt.name || ''); this.loadAll() },
  methods: {
    async loadAll() {
      this.loading = true
      try {
        const [overview, emotionData] = await Promise.all([
          parentApi.getChildOverview(this.childId),
          parentApi.getChildEmotionReport(this.childId, this.period)
        ])
        this.overview = overview || {}
        this.recentDiaries = this.overview.recentDiaries || []
        this.recentWarnings = this.overview.recentWarnings || []
        this.avgScore = this.overview.avgEmotionScore || 0
        // 计算预警统计
        const allW = this.recentWarnings
        this.warnStats = { total: this.overview.warningCount || 0, high: allW.filter(w => w.riskLevel >= 3).length, medium: allW.filter(w => w.riskLevel === 2).length, low: allW.filter(w => w.riskLevel === 1).length }
        // 情绪分布
        if (emotionData && emotionData.emotionStats) {
          const total = Object.values(emotionData.emotionStats).reduce((s, v) => s + v, 0)
          const map = { happy: { label: '开心', icon: '😊', color: 'linear-gradient(90deg,#FFD700,#FFC107)' }, calm: { label: '平静', icon: '😌', color: 'linear-gradient(90deg,#87CEEB,#64B5F6)' }, sad: { label: '难过', icon: '😢', color: 'linear-gradient(90deg,#90A4AE,#78909C)' }, anxious: { label: '焦虑', icon: '😰', color: 'linear-gradient(90deg,#FFAB91,#FF8A65)' }, angry: { label: '生气', icon: '😠', color: 'linear-gradient(90deg,#EF5350,#E53935)' } }
          this.distItems = Object.entries(emotionData.emotionStats).map(([cat, count]) => ({ cat, icon: map[cat]?.icon || '❓', label: map[cat]?.label || cat, color: map[cat]?.color || '#999', count, pct: total ? Math.round(count / total * 100) : 0 }))
          this.distItems.sort((a, b) => b.count - a.count)
        }
        // 绘制图表
        this.$nextTick(() => {
          const trend = (overview && overview.emotionTrend) || []
          const chartData = trend.map(d => ({ date: (d.date || '').substring(5, 10), score: d.score != null ? d.score : 0 }))
          if (chartData.length === 0) {
            const now = new Date(); for (let i = this.period - 1; i >= 0; i--) { const dt = new Date(now); dt.setDate(dt.getDate() - i); chartData.push({ date: (dt.getMonth()+1)+'/'+dt.getDate(), score: 0 }) }
          }
          setTimeout(() => drawChart('emotionChart', chartData, 320, 120), 300)
        })
      } catch (e) { uni.showToast({ title: e.message, icon: 'none' }) }
      finally { this.loading = false }
    },
    async switchPeriod(p) { this.period = p; await this.loadAll() },
    async generateReport() {
      try { await parentApi.generateReport(this.childId, 'week'); uni.showToast({ title: '报告生成成功', icon: 'success' }) }
      catch (e) { uni.showToast({ title: e.message || '生成失败', icon: 'none' }) }
    },
    formatRel: formatRelativeTime,
    goWarnings() { uni.navigateTo({ url: `/pages/child/warnings?childId=${this.childId}` }) },
    goWarningDetail(id) { uni.navigateTo({ url: `/pages/child/warning-detail?childId=${this.childId}&warningId=${id}` }) },
    goDiaries() { uni.navigateTo({ url: `/pages/child/diaries?childId=${this.childId}` }) },
    goDiaryDetail(id) { uni.navigateTo({ url: `/pages/child/diary-detail?childId=${this.childId}&diaryId=${id}` }) },
    goReports() { uni.navigateTo({ url: `/pages/child/reports?childId=${this.childId}` }) }
  }
}
</script>
<style scoped>
.overview-name { font-size: var(--font-xl); font-weight: bold; }
.dist-row { display: flex; align-items: center; padding: 10rpx 0; }
.dist-icon { width: 48rpx; font-size: 28rpx; }
.dist-label { width: 80rpx; font-size: var(--font-sm); color: var(--text-secondary); }
.dist-pct { width: 64rpx; text-align: right; font-size: var(--font-sm); color: var(--text-tertiary); }
.warn-summary { display: flex; flex-wrap: wrap; }
.warn-item, .diary-item { padding: 16rpx 0; border-bottom: 1rpx solid var(--border-light); }
.warn-item:last-child, .diary-item:last-child { border-bottom: none; }
.mr-8 { margin-right: 8rpx; }
</style>
