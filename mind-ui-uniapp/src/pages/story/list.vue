<template>
  <view class="story-list">
    <!-- 顶部筛选 -->
    <view class="filter-bar">
      <view class="filter-tabs">
        <view 
          v-for="item in emotionOptions" 
          :key="item.value"
          :class="['filter-tab', { active: query.emotionType === item.value }]"
          @tap="onEmotionChange(item.value)"
        >
          {{ item.label }}
        </view>
      </view>
      <view class="sort-tabs">
        <text 
          :class="['sort-tab', { active: query.sortBy === 'new' }]"
          @tap="onSortChange('new')"
        >最新</text>
        <text 
          :class="['sort-tab', { active: query.sortBy === 'hot' }]"
          @tap="onSortChange('hot')"
        >最热</text>
        <text 
          :class="['sort-tab', { active: query.sortBy === 'random' }]"
          @tap="onSortChange('random')"
        >随机</text>
      </view>
    </view>

    <!-- 故事列表 -->
    <scroll-view 
      scroll-y 
      class="story-scroll"
      @scrolltolower="onLoadMore"
      refresher-enabled
      :refresher-triggered="refreshing"
      @refresherrefresh="onRefresh"
    >
      <view class="story-cards">
        <view 
          v-for="story in list" 
          :key="story.id"
          class="story-card"
          @tap="goDetail(story.id)"
        >
          <!-- 情绪标签 -->
          <view class="story-header">
            <view class="emotion-tag" :style="{ backgroundColor: getEmotionColor(story.emotionType) }">
              {{ getEmotionLabel(story.emotionType) }}
            </view>
            <text class="story-time">{{ formatTime(story.createdAt) }}</text>
          </view>
          
          <!-- 内容 -->
          <view class="story-title">{{ story.title }}</view>
          
          <!-- 标签 -->
          <view class="story-tags" v-if="story.tags && story.tags.length">
            <text class="tag-item" v-for="tag in story.tags" :key="tag">#{{ tag }}</text>
          </view>
          
          <!-- 底部数据 -->
          <view class="story-footer">
            <view class="author">
              <text class="nickname">{{ story.displayNickname }}</text>
              <text class="anonymous" v-if="story.isAnonymous">匿名</text>
            </view>
            <view class="actions">
              <view class="action-item" @tap.stop="onLike(story)">
                <text class="icon" :class="{ liked: story.liked }">♥</text>
                <text class="count">{{ story.likeCount || 0 }}</text>
              </view>
              <view class="action-item" @tap.stop="onFavorite(story)">
                <text class="icon" :class="{ favorited: story.favorited }">★</text>
                <text class="count">{{ story.commentCount || 0 }}</text>
              </view>
            </view>
          </view>
        </view>
      </view>

      <!-- 加载更多 -->
      <load-more :status="loadStatus" />

      <!-- 空状态 -->
      <empty-state v-if="!loading && list.length === 0" text="暂无故事,去发布第一条吧~" />
    </scroll-view>

    <!-- 发布按钮 -->
    <view class="fab" @tap="goCreate">
      <text class="fab-icon">+</text>
    </view>
  </view>
</template>

<script>
import { storyApi } from '@/utils/request'
import LoadMore from '@/components/load-more.vue'
import EmptyState from '@/components/empty-state.vue'

export default {
  components: { LoadMore, EmptyState },
  
  data() {
    return {
      list: [],
      query: {
        emotionType: '',
        sortBy: 'new',
        page: 1,
        size: 10
      },
      loading: false,
      refreshing: false,
      loadStatus: 'more',
      total: 0,
      
      emotionOptions: [
        { value: '', label: '全部' },
        { value: 'happy', label: '开心' },
        { value: 'calm', label: '平静' },
        { value: 'sad', label: '难过' },
        { value: 'anxious', label: '焦虑' },
        { value: 'angry', label: '愤怒' },
        { value: 'fear', label: '害怕' }
      ],
      
      emotionColors: {
        happy: '#52c41a',
        calm: '#1890ff',
        sad: '#8c8c8c',
        anxious: '#faad14',
        angry: '#ff4d4f',
        fear: '#722ed1'
      }
    }
  },
  
  onLoad() {
    this.loadData()
  },
  
  onShow() {
    // 返回时刷新
  },
  
  methods: {
    async loadData() {
      if (this.loading) return
      this.loading = true
      
      try {
        const res = await storyApi.getList(this.query)
        if (this.query.page === 1) {
          this.list = res.records || []
        } else {
          this.list = [...this.list, ...(res.records || [])]
        }
        this.total = res.total || 0
        this.loadStatus = this.list.length >= this.total ? 'noMore' : 'more'
      } catch (e) {
        console.error('加载失败', e)
      } finally {
        this.loading = false
        this.refreshing = false
      }
    },
    
    onRefresh() {
      this.refreshing = true
      this.query.page = 1
      this.loadData()
    },
    
    onLoadMore() {
      if (this.loadStatus === 'noMore' || this.loading) return
      this.query.page++
      this.loadData()
    },
    
    onEmotionChange(type) {
      this.query.emotionType = type
      this.query.page = 1
      this.loadData()
    },
    
    onSortChange(sort) {
      this.query.sortBy = sort
      this.query.page = 1
      this.loadData()
    },
    
    goDetail(id) {
      uni.navigateTo({ url: `/pages/story/detail?id=${id}` })
    },
    
    goCreate() {
      uni.navigateTo({ url: '/pages/story/create' })
    },
    
    async onLike(story) {
      try {
        if (story.liked) {
          await storyApi.unlike(story.id)
          story.liked = false
          story.likeCount--
        } else {
          await storyApi.like(story.id)
          story.liked = true
          story.likeCount++
        }
      } catch (e) {
        console.error('点赞失败', e)
      }
    },
    
    async onFavorite(story) {
      try {
        if (story.favorited) return
        await storyApi.favorite(story.id)
        story.favorited = true
        uni.showToast({ title: '已收藏', icon: 'success' })
      } catch (e) {
        console.error('收藏失败', e)
      }
    },
    
    getEmotionLabel(type) {
      const item = this.emotionOptions.find(e => e.value === type)
      return item ? item.label : '其他'
    },
    
    getEmotionColor(type) {
      return this.emotionColors[type] || '#8c8c8c'
    },
    
    formatTime(time) {
      if (!time) return ''
      const date = new Date(time)
      const now = new Date()
      const diff = now - date
      if (diff < 60000) return '刚刚'
      if (diff < 3600000) return Math.floor(diff / 60000) + '分钟前'
      if (diff < 86400000) return Math.floor(diff / 3600000) + '小时前'
      return `${date.getMonth() + 1}/${date.getDate()}`
    }
  }
}
</script>

<style lang="scss" scoped>
.story-list {
  min-height: 100vh;
  background: #f5f5f5;
}

.filter-bar {
  background: #fff;
  padding: 24rpx 32rpx;
  position: sticky;
  top: 0;
  z-index: 10;
}

.filter-tabs {
  display: flex;
  gap: 16rpx;
  overflow-x: auto;
  margin-bottom: 16rpx;
}

.filter-tab {
  flex-shrink: 0;
  padding: 8rpx 24rpx;
  border-radius: 32rpx;
  font-size: 24rpx;
  color: #666;
  background: #f0f0f0;
  
  &.active {
    background: #7B68EE;
    color: #fff;
  }
}

.sort-tabs {
  display: flex;
  gap: 32rpx;
  justify-content: center;
}

.sort-tab {
  font-size: 28rpx;
  color: #999;
  
  &.active {
    color: #7B68EE;
    font-weight: bold;
  }
}

.story-scroll {
  height: calc(100vh - 200rpx);
  padding: 24rpx;
}

.story-cards {
  display: flex;
  flex-direction: column;
  gap: 24rpx;
}

.story-card {
  background: #fff;
  border-radius: 24rpx;
  padding: 32rpx;
  box-shadow: 0 4rpx 20rpx rgba(0, 0, 0, 0.05);
}

.story-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16rpx;
}

.emotion-tag {
  padding: 4rpx 16rpx;
  border-radius: 20rpx;
  font-size: 22rpx;
  color: #fff;
}

.story-time {
  font-size: 24rpx;
  color: #999;
}

.story-title {
  font-size: 32rpx;
  font-weight: bold;
  color: #333;
  margin-bottom: 16rpx;
  line-height: 1.4;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.story-tags {
  display: flex;
  flex-wrap: wrap;
  gap: 12rpx;
  margin-bottom: 16rpx;
}

.tag-item {
  font-size: 24rpx;
  color: #7B68EE;
}

.story-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding-top: 16rpx;
  border-top: 1px solid #f0f0f0;
}

.author {
  display: flex;
  align-items: center;
  gap: 8rpx;
}

.nickname {
  font-size: 24rpx;
  color: #666;
}

.anonymous {
  font-size: 20rpx;
  color: #999;
}

.actions {
  display: flex;
  gap: 32rpx;
}

.action-item {
  display: flex;
  align-items: center;
  gap: 8rpx;
}

.icon {
  font-size: 28rpx;
  color: #ccc;
  
  &.liked {
    color: #ff6b6b;
  }
  
  &.favorited {
    color: #ffc107;
  }
}

.count {
  font-size: 24rpx;
  color: #999;
}

.fab {
  position: fixed;
  right: 40rpx;
  bottom: 120rpx;
  width: 100rpx;
  height: 100rpx;
  border-radius: 50%;
  background: linear-gradient(135deg, #7B68EE, #9c8bff);
  display: flex;
  align-items: center;
  justify-content: center;
  box-shadow: 0 8rpx 24rpx rgba(123, 104, 238, 0.4);
}

.fab-icon {
  font-size: 56rpx;
  color: #fff;
  font-weight: bold;
}
</style>