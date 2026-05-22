<template>
  <view class="favorites">
    <scroll-view scroll-y class="favorites-scroll" @scrolltolower="onLoadMore">
      <view class="favorites-list" v-if="list.length">
        <view 
          v-for="story in list" 
          :key="story.id"
          class="story-card"
          @tap="goDetail(story.id)"
        >
          <view class="story-header">
            <view class="emotion-tag" :style="{ backgroundColor: getEmotionColor(story.emotionType) }">
              {{ getEmotionLabel(story.emotionType) }}
            </view>
            <text class="story-time">{{ formatTime(story.createdAt) }}</text>
          </view>
          <view class="story-title">{{ story.title }}</view>
          <view class="story-footer">
            <text class="nickname">{{ story.displayNickname }}</text>
            <text class="action-icon liked">★</text>
          </view>
        </view>
      </view>
      <empty-state v-else text="暂无收藏的故事~" />
      <load-more :status="loadStatus" />
    </scroll-view>
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
      page: 1,
      size: 10,
      loading: false,
      loadStatus: 'more'
    }
  },
  
  onLoad() {
    this.loadData()
  },
  
  methods: {
    async loadData() {
      if (this.loading) return
      this.loading = true
      
      try {
        const res = await storyApi.getFavorites({ page: this.page, size: this.size })
        if (this.page === 1) {
          this.list = res.records || []
        } else {
          this.list = [...this.list, ...(res.records || [])]
        }
        this.loadStatus = this.list.length >= res.total ? 'noMore' : 'more'
      } catch (e) {
        console.error('加载失败', e)
      } finally {
        this.loading = false
      }
    },
    
    onLoadMore() {
      if (this.loadStatus === 'noMore' || this.loading) return
      this.page++
      this.loadData()
    },
    
    goDetail(id) {
      uni.navigateTo({ url: `/pages/story/detail?id=${id}` })
    },
    
    getEmotionLabel(type) {
      const map = { happy: '开心', calm: '平静', sad: '难过', anxious: '焦虑', angry: '愤怒', fear: '害怕' }
      return map[type] || '其他'
    },
    
    getEmotionColor(type) {
      const map = { happy: '#52c41a', calm: '#1890ff', sad: '#8c8c8c', anxious: '#faad14', angry: '#ff4d4f', fear: '#722ed1' }
      return map[type] || '#8c8c8c'
    },
    
    formatTime(time) {
      if (!time) return ''
      const date = new Date(time)
      return `${date.getMonth() + 1}/${date.getDate()}`
    }
  }
}
</script>

<style lang="scss" scoped>
.favorites { min-height: 100vh; background: #f5f5f5; }
.favorites-scroll { height: 100vh; padding: 24rpx; }
.favorites-list { display: flex; flex-direction: column; gap: 24rpx; }
.story-card {
  background: #fff; border-radius: 24rpx; padding: 32rpx;
  box-shadow: 0 4rpx 20rpx rgba(0,0,0,0.05);
}
.story-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 16rpx; }
.emotion-tag {
  padding: 4rpx 16rpx; border-radius: 20rpx; font-size: 22rpx; color: #fff;
}
.story-time { font-size: 24rpx; color: #999; }
.story-title {
  font-size: 32rpx; font-weight: bold; color: #333; margin-bottom: 16rpx;
  line-height: 1.4; display: -webkit-box; -webkit-line-clamp: 2; -webkit-box-orient: vertical; overflow: hidden;
}
.story-footer { display: flex; justify-content: space-between; align-items: center; padding-top: 16rpx; border-top: 1px solid #f0f0f0; }
.nickname { font-size: 24rpx; color: #666; }
.action-icon { font-size: 28rpx; color: #ffc107; }
</style>