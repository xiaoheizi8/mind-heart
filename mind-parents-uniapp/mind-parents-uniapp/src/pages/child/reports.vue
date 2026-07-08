<template>
  <view class="page-container">
    <view style="padding:24rpx 24rpx 0;"><button class="btn btn-primary btn-sm" @click="generateReport">📝 生成新报告</button></view>
    <view v-if="loading" style="padding:24rpx;"><view class="skeleton skeleton-card"/><view class="skeleton skeleton-card"/></view>
    <empty-state v-else-if="list.length===0" icon="📊" text="暂无情绪报告" desc="点击上方按钮生成第一份报告" />
    <view v-else>
      <view class="report-card card card-sm" v-for="r in list" :key="r.id" @click="goDetail(r.id)">
        <view class="flex-between">
          <text class="font-bold text-lg">{{ r.reportType === 'week' ? '📅 周报' : r.reportType === 'month' ? '📆 月报' : '📋 季报' }}</text>
          <emotion-badge v-if="r.mainEmotion" :category="r.mainEmotion" size="small" />
        </view>
        <view class="flex-between mt-16">
          <text class="text-sm text-hint">平均分 {{ r.avgEmotionScore || '--' }} · {{ r.diaryCount || 0 }}篇日记</text>
          <text class="text-sm text-hint">{{ (r.createdAt || '').substring(0,10) }}</text>
        </view>
      </view>
    </view>
  </view>
</template>
<script>
import { parentApi } from '../../utils/request.js'
import EmotionBadge from '../../components/emotion-badge.vue'
import EmptyState from '../../components/empty-state.vue'
export default {
  components: { EmotionBadge, EmptyState },
  data() { return { childId: 0, list: [], loading: false } },
  onLoad(opt) { this.childId = Number(opt.childId); this.loadData() },
  methods: {
    async loadData() { this.loading = true; try { this.list = (await parentApi.getChildReports(this.childId)) || [] } catch (e) {} finally { this.loading = false } },
    async generateReport() { try { await parentApi.generateReport(this.childId, 'week'); uni.showToast({ title: '生成成功', icon: 'success' }); this.loadData() } catch (e) { uni.showToast({ title: e.message, icon: 'none' }) } },
    goDetail(id) { uni.navigateTo({ url: `/pages/child/report-detail?childId=${this.childId}&reportId=${id}` }) }
  }
}
</script>
<style scoped>
.report-card { margin-bottom: 4rpx; }
</style>
