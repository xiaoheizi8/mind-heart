<template>
  <view class="page-container">
    <view class="filter-bar">
      <text class="filter-pill" :class="{ active: !filter }" @click="setFilter('')">全部</text>
      <text class="filter-pill" :class="{ active: filter==='happy' }" @click="setFilter('happy')">😊 开心</text>
      <text class="filter-pill" :class="{ active: filter==='calm' }" @click="setFilter('calm')">😌 平静</text>
      <text class="filter-pill" :class="{ active: filter==='sad' }" @click="setFilter('sad')">😢 难过</text>
      <text class="filter-pill" :class="{ active: filter==='anxious' }" @click="setFilter('anxious')">😰 焦虑</text>
      <text class="filter-pill" :class="{ active: filter==='angry' }" @click="setFilter('angry')">😠 生气</text>
    </view>

    <view v-if="loading" style="padding:24rpx;"><view class="skeleton skeleton-card"/><view class="skeleton skeleton-card"/></view>
    <empty-state v-else-if="list.length===0" icon="📔" text="暂无日记" />
    <view v-else>
      <view class="diary-card card card-sm" v-for="d in list" :key="d.id" @click="goDetail(d.id)">
        <view class="flex-between">
          <emotion-badge :category="d.emotionCategory" :score="d.emotionScore" size="small" />
          <text class="text-sm text-hint">{{ formatRel(d.createdAt) }}{{ ' ' + getDay(d.createdAt) }}</text>
        </view>
        <text class="text-ellipsis-2 text-base mt-12" style="line-height:1.8;">{{ d.content }}</text>
      </view>
      <load-more :status="loadStatus" @load="loadMore" />
    </view>
  </view>
</template>
<script>
import { parentApi } from '../../utils/request.js'
import { formatRelativeTime, getDayOfWeek } from '../../utils/index.js'
import EmotionBadge from '../../components/emotion-badge.vue'
import EmptyState from '../../components/empty-state.vue'
import LoadMore from '../../components/load-more.vue'
export default {
  components: { EmotionBadge, EmptyState, LoadMore },
  data() { return { childId: 0, list: [], page: 1, total: 0, filter: '', loading: false, loadStatus: 'more' } },
  onLoad(opt) { this.childId = Number(opt.childId); this.loadData() },
  methods: {
    async loadData() {
      if (this.loading) return
      this.loading = true; this.loadStatus = 'loading'
      try {
        const params = { pageNum: this.page, pageSize: 10 }; if (this.filter) params.emotionCategory = this.filter
        const res = await parentApi.getChildDiaries(this.childId, params)
        this.list = this.page === 1 ? (res.records || []) : [...this.list, ...(res.records || [])]
        this.total = res.total || 0; this.loadStatus = this.list.length >= this.total ? 'noMore' : 'more'
      } catch (e) { uni.showToast({ title: e.message, icon: 'none' }) }
      finally { this.loading = false }
    },
    loadMore() { this.page++; this.loadData() },
    setFilter(f) { this.filter = f; this.page = 1; this.list = []; this.loadData() },
    formatRel: formatRelativeTime, getDay: getDayOfWeek,
    goDetail(id) { uni.navigateTo({ url: `/pages/child/diary-detail?childId=${this.childId}&diaryId=${id}` }) }
  }
}
</script>
<style scoped>
.diary-card { margin-bottom: 4rpx; }
</style>
