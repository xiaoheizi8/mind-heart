<template>
  <view class="page-container">
    <view v-if="list.length === 0 && !loading">
      <empty-state icon="✅" text="暂无预警记录" />
    </view>
    <view class="warning-item card" v-for="item in list" :key="item.id" @click="goDetail(item.id)">
      <view class="flex-between">
        <risk-level-badge :level="item.riskLevel" />
        <text style="font-size:12px;color:var(--text-hint);">{{ formatDate(item.createdAt) }}</text>
      </view>
      <text class="ellipsis-2" style="font-size:14px;color:var(--text-secondary);margin-top:8px;">{{ item.content }}</text>
      <text style="font-size:12px;color:var(--text-hint);margin-top:4px;">{{ item.handled ? '已处理' : '未处理' }}</text>
    </view>
    <load-more :status="loadStatus" @load="loadMore" />
  </view>
</template>
<script>
import { parentApi } from '../../utils/request.js'
import RiskLevelBadge from '../../components/risk-level-badge.vue'
import EmptyState from '../../components/empty-state.vue'
import LoadMore from '../../components/load-more.vue'
export default {
  components: { RiskLevelBadge, EmptyState, LoadMore },
  data() { return { childId: 0, list: [], page: 1, total: 0, loading: false, loadStatus: 'more' } },
  onLoad(opt) { this.childId = Number(opt.childId); this.loadData() },
  methods: {
    async loadData() {
      if (this.loading) return
      this.loading = true; this.loadStatus = 'loading'
      try {
        const res = await parentApi.getChildWarnings(this.childId, { pageNum: this.page, pageSize: 10 })
        this.list = this.page === 1 ? (res.records || []) : [...this.list, ...(res.records || [])]
        this.total = res.total || 0
        this.loadStatus = this.list.length >= this.total ? 'noMore' : 'more'
      } catch (e) { uni.showToast({ title: e.message, icon: 'none' }); this.loadStatus = 'more' }
      finally { this.loading = false }
    },
    loadMore() { this.page++; this.loadData() },
    formatDate(d) { return d ? d.substring(0, 16) : '' },
    goDetail(id) { uni.navigateTo({ url: `/pages/child/warning-detail?childId=${this.childId}&warningId=${id}` }) }
  }
}
</script>
<style scoped>
.warning-item { margin-bottom: 8px; }
.ellipsis-2 { display: -webkit-box; -webkit-line-clamp: 2; -webkit-box-orient: vertical; overflow: hidden; }
</style>
