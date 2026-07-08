<template>
  <view class="page-container">
    <view class="filter-bar">
      <text class="filter-pill" :class="{ active: !riskFilter }" @click="riskFilter=null">全部</text>
      <text class="filter-pill" :class="{ active: riskFilter===3 }" @click="riskFilter=3">高风险</text>
      <text class="filter-pill" :class="{ active: riskFilter===2 }" @click="riskFilter=2">中风险</text>
      <text class="filter-pill" :class="{ active: riskFilter===1 }" @click="riskFilter=1">低风险</text>
    </view>

    <view class="filter-bar" style="padding-top:0;">
      <text class="filter-pill" :class="{ active: !handledFilter }" @click="handledFilter=null">全部状态</text>
      <text class="filter-pill" :class="{ active: handledFilter===0 }" @click="handledFilter=0">未处理</text>
      <text class="filter-pill" :class="{ active: handledFilter===1 }" @click="handledFilter=1">已处理</text>
    </view>

    <view v-if="loading" style="padding:24rpx;"><view class="skeleton skeleton-card"/><view class="skeleton skeleton-card"/></view>
    <empty-state v-else-if="list.length===0" icon="✅" text="暂无预警记录" />
    <view v-else>
      <view class="warn-card card card-sm" v-for="item in list" :key="item.id" @click="goDetail(item.id)">
        <view class="flex-between">
          <risk-level-badge :level="item.riskLevel" />
          <text class="text-sm" :style="{ color: item.handled ? '#52C41A' : '#FAAD14' }">{{ item.handled ? '已处理' : '未处理' }}</text>
        </view>
        <text class="text-ellipsis-2 text-base text-secondary mt-12">{{ item.content }}</text>
        <text class="text-sm text-hint mt-8" style="display:block;">{{ formatRel(item.createdAt) }}</text>
      </view>
      <load-more :status="loadStatus" @load="loadMore" />
    </view>
  </view>
</template>
<script>
import { parentApi } from '../../utils/request.js'
import { formatRelativeTime } from '../../utils/index.js'
import RiskLevelBadge from '../../components/risk-level-badge.vue'
import EmptyState from '../../components/empty-state.vue'
import LoadMore from '../../components/load-more.vue'
export default {
  components: { RiskLevelBadge, EmptyState, LoadMore },
  data() { return { childId: 0, list: [], page: 1, total: 0, loading: false, loadStatus: 'more', riskFilter: null, handledFilter: null } },
  onLoad(opt) { this.childId = Number(opt.childId); this.loadData() },
  methods: {
    async loadData() {
      if (this.loading) return
      this.loading = true; this.loadStatus = 'loading'
      try {
        const res = await parentApi.getChildWarnings(this.childId, { pageNum: this.page, pageSize: 10 })
        let records = res.records || []
        if (this.riskFilter) records = records.filter(r => r.riskLevel === this.riskFilter)
        if (this.handledFilter != null) records = records.filter(r => r.handled === this.handledFilter)
        this.list = this.page === 1 ? records : [...this.list, ...records]
        this.total = res.total || 0
        this.loadStatus = this.list.length >= this.total ? 'noMore' : 'more'
      } catch (e) { uni.showToast({ title: e.message, icon: 'none' }) }
      finally { this.loading = false }
    },
    loadMore() { this.page++; this.loadData() },
    formatRel: formatRelativeTime,
    goDetail(id) { uni.navigateTo({ url: `/pages/child/warning-detail?childId=${this.childId}&warningId=${id}` }) }
  },
  watch: { riskFilter() { this.page = 1; this.list = []; this.loadData() }, handledFilter() { this.page = 1; this.list = []; this.loadData() } }
}
</script>
<style scoped>
.warn-card { margin-bottom: 4rpx; }
</style>
