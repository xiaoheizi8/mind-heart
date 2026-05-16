<template>
  <view class="page-favorites">
    <!-- 标签筛选 -->
    <view class="tabs">
      <view 
        v-for="tab in tabs" 
        :key="tab.value"
        class="tab-item"
        :class="{ active: currentTab === tab.value }"
        @click="changeTab(tab.value)"
      >
        {{ tab.label }}
      </view>
    </view>

    <!-- 列表 -->
    <scroll-view class="favorites-list" scroll-y @scrolltolower="loadMore">
      <view v-if="list.length === 0" class="empty-state">
        <text class="empty-icon">❤️</text>
        <text class="empty-text">还没有收藏内容</text>
      </view>
      
      <view 
        v-for="item in list" 
        :key="item.id"
        class="fav-item"
        @click="goToDetail(item)"
      >
        <view class="item-header">
          <text class="item-type">{{ item.type }}</text>
          <text class="item-time">{{ item.createdAt }}</text>
        </view>
        <text class="item-title">{{ item.title }}</text>
        <text class="item-desc">{{ item.summary }}</text>
        <view class="item-action">
          <text class="cancel-btn" @click.stop="cancelCollect(item.id)">取消收藏</text>
        </view>
      </view>
    </scroll-view>
  </view>
</template>

<script>
import { knowledgeApi } from '@/utils/request.js'

export default {
  data() {
    return {
      currentTab: 'all',
      tabs: [
        { value: 'all', label: '全部' },
        { value: 'diary', label: '日记' },
        { value: 'knowledge', label: '知识' }
      ],
      list: [],
      page: 1,
      pageSize: 20,
      loading: false,
      hasMore: true
    }
  },
  onLoad() {
    this.loadFavorites()
  },
  onShow() {
    this.loadFavorites()
  },
  methods: {
    changeTab(tab) {
      this.currentTab = tab
      this.page = 1
      this.list = []
      this.hasMore = true
      this.loadFavorites()
    },
    async loadFavorites() {
      if (this.loading || !this.hasMore) return
      this.loading = true
      try {
        const data = await knowledgeApi.getFavorites({ pageNum: this.page, pageSize: this.pageSize })
        if (data) {
          const records = data.records || data || []
          if (this.currentTab === 'all' || this.currentTab === 'knowledge') {
            if (this.page === 1) {
              this.list = records
            } else {
              this.list = this.list.concat(records)
            }
            this.hasMore = this.list.length < (data.total || records.length)
          } else {
            this.list = []
            this.hasMore = false
          }
        } else {
          this.list = []
          this.hasMore = false
        }
      } catch (e) {
        console.error('加载收藏失败', e)
        uni.showToast({ title: '加载失败', icon: 'none' })
      } finally {
        this.loading = false
      }
    },
    loadMore() {
      if (!this.hasMore || this.loading) return
      this.page++
      this.loadFavorites()
    },
    goToDetail(item) {
      if (item.category || item.type === 'knowledge') {
        uni.navigateTo({ url: `/pages/knowledge/detail?id=${item.id}` })
      }
    },
    cancelCollect(item) {
      uni.showModal({
        title: '提示',
        content: '确定要取消收藏吗？',
        success: (res) => {
          if (res.confirm) {
            knowledgeApi.cancelCollect(item.id).then(() => {
              this.list = this.list.filter(i => i.id !== item.id)
              uni.showToast({ title: '已取消收藏', icon: 'success' })
            }).catch(() => {
              uni.showToast({ title: '取消失败', icon: 'none' })
            })
          }
        }
      })
    }
  }
}
</script>

<style scoped>
.page-favorites {
  min-height: 100vh;
  background: var(--bg-secondary);
}

.tabs {
  display: flex;
  background: var(--bg-primary);
  padding: 24rpx;
  gap: 24rpx;
}

.tab-item {
  padding: 16rpx 32rpx;
  background: var(--bg-secondary);
  border-radius: 32rpx;
  font-size: 28rpx;
  color: var(--text-secondary);
}

.tab-item.active {
  background: var(--primary-color);
  color: #FFF;
}

.favorites-list {
  height: calc(100vh - 120rpx);
  padding: 24rpx;
}

.empty-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 120rpx;
}

.empty-icon {
  font-size: 80rpx;
}

.empty-text {
  font-size: 28rpx;
  color: var(--text-tertiary);
  margin-top: 24rpx;
}

.fav-item {
  background: var(--bg-primary);
  border-radius: 24rpx;
  padding: 32rpx;
  margin-bottom: 24rpx;
}

.item-header {
  display: flex;
  justify-content: space-between;
  margin-bottom: 16rpx;
}

.item-type {
  padding: 8rpx 16rpx;
  background: rgba(123, 104, 238, 0.1);
  border-radius: 16rpx;
  font-size: 22rpx;
  color: var(--primary-color);
}

.item-time {
  font-size: 22rpx;
  color: var(--text-tertiary);
}

.item-title {
  font-size: 32rpx;
  font-weight: 600;
  color: var(--text-primary);
  display: block;
  margin-bottom: 12rpx;
}

.item-desc {
  font-size: 26rpx;
  color: var(--text-secondary);
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
  margin-bottom: 16rpx;
}

.item-action {
  display: flex;
  justify-content: flex-end;
}

.cancel-btn {
  font-size: 26rpx;
  color: var(--text-tertiary);
}
</style>