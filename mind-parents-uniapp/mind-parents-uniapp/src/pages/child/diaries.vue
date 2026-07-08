<template>
  <view class="page-container">
    <view class="filter-tabs">
      <text class="filter-tab" :class="{ active: !filter }" @click="setFilter('')">全部</text>
      <text class="filter-tab happy" :class="{ active: filter==='happy' }" @click="setFilter('happy')">开心</text>
      <text class="filter-tab calm" :class="{ active: filter==='calm' }" @click="setFilter('calm')">平静</text>
      <text class="filter-tab sad" :class="{ active: filter==='sad' }" @click="setFilter('sad')">难过</text>
      <text class="filter-tab anxious" :class="{ active: filter==='anxious' }" @click="setFilter('anxious')">焦虑</text>
    </view>

    <view v-if="list.length === 0 && !loading">
      <empty-state icon="📔" text="暂无日记" />
    </view>

    <view class="diary-item card" v-for="d in list" :key="d.id" @click="goDetail(d.id)">
      <view class="flex-between">
        <emotion-badge :category="d.emotionCategory" :score="d.emotionScore" />
        <text class="time-text">{{ formatDate(d.createdAt) }}</text>
      </view>
      <text class="ellipsis-2" style="font-size:14px;color:var(--text-secondary);margin-top:8px;">{{ d.content }}</text>
    </view>
    <load-more :status="loadStatus" @load="loadMore" />
  </view>
</template>
<script>
import { parentApi } from '../../utils/request.js'
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
        const params = { pageNum: this.page, pageSize: 10 }
        if (this.filter) params.emotionCategory = this.filter
        const res = await parentApi.getChildDiaries(this.childId, params)
        this.list = this.page === 1 ? (res.records || []) : [...this.list, ...(res.records || [])]
        this.total = res.total || 0
        this.loadStatus = this.list.length >= this.total ? 'noMore' : 'more'
      } catch (e) { uni.showToast({ title: e.message, icon: 'none' }); this.loadStatus = 'more' }
      finally { this.loading = false }
    },
    loadMore() { this.page++; this.loadData() },
    setFilter(f) { this.filter = f; this.page = 1; this.list = []; this.loadData() },
    formatDate(d) { return d ? d.substring(0, 16) : '' },
    goDetail(id) { uni.navigateTo({ url: `/pages/child/diary-detail?childId=${this.childId}&diaryId=${id}` }) }
  }
}
</script>
<style scoped>
.filter-tabs { display: flex; padding: 12px 16px; gap: 8px; overflow-x: auto; white-space: nowrap; }
.filter-tab { padding: 4px 14px; border-radius: 16px; font-size: 13px; background: #F0F0F0; color: var(--text-secondary); }
.filter-tab.active { background: var(--primary-color); color: #fff; }
.diary-item { margin-bottom: 8px; }
.time-text { font-size: 12px; color: var(--text-hint); }
.ellipsis-2 { display: -webkit-box; -webkit-line-clamp: 2; -webkit-box-orient: vertical; overflow: hidden; }
</style>
