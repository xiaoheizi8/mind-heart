<template>
  <view class="page-container">
    <view v-if="list.length === 0 && !loading">
      <empty-state icon="📊" text="暂无情绪报告" />
    </view>
    <view class="report-item card" v-for="r in list" :key="r.id" @click="goDetail(r.id)">
      <view class="flex-between">
        <text class="report-type">{{ r.reportType === 'week' ? '周报' : r.reportType === 'month' ? '月报' : '季报' }}</text>
        <text class="time-text">{{ formatDate(r.createdAt) }}</text>
      </view>
      <view class="flex-between" style="margin-top:8px;">
        <text>平均情绪分: {{ r.avgEmotionScore }}</text>
        <text>{{ r.mainEmotion || '--' }}</text>
      </view>
    </view>
    <loading-box :visible="loading" />
  </view>
</template>
<script>
import { parentApi } from '../../utils/request.js'
import EmptyState from '../../components/empty-state.vue'
import LoadingBox from '../../components/loading-box.vue'
export default {
  components: { EmptyState, LoadingBox },
  data() { return { childId: 0, list: [], loading: false } },
  onLoad(opt) { this.childId = Number(opt.childId); this.loadData() },
  methods: {
    async loadData() {
      this.loading = true
      try { this.list = await parentApi.getChildReports(this.childId) || [] }
      catch (e) { uni.showToast({ title: e.message, icon: 'none' }) }
      finally { this.loading = false }
    },
    formatDate(d) { return d ? d.substring(0, 10) : '' },
    goDetail(id) { uni.navigateTo({ url: `/pages/child/report-detail?childId=${this.childId}&reportId=${id}` }) }
  }
}
</script>
<style scoped>
.report-item { margin-bottom: 8px; }
.report-type { font-size: 15px; font-weight: 600; }
.time-text { font-size: 12px; color: var(--text-hint); }
</style>
