<template>
  <view class="story-detail" v-if="story">
    <!-- 情绪标签 -->
    <view class="detail-header">
      <view class="emotion-tag" :style="{ backgroundColor: getEmotionColor(story.emotionType) }">
        {{ getEmotionLabel(story.emotionType) }}
      </view>
      <text class="story-time">{{ formatTime(story.createdAt) }}</text>
    </view>

    <!-- 标题 -->
    <view class="story-title">{{ story.title }}</view>

    <!-- 作者信息 -->
    <view class="author-bar">
      <view class="author-info">
        <text class="nickname">{{ story.displayNickname }}</text>
        <text class="anonymous" v-if="story.isAnonymous">匿名发布</text>
      </view>
      <view class="author-actions">
        <view class="action-btn" @tap="onLike">
          <text class="icon" :class="{ liked: story.liked }">♥</text>
          <text class="count">{{ story.likeCount || 0 }}</text>
        </view>
        <view class="action-btn" @tap="onFavorite">
          <text class="icon" :class="{ favorited: story.favorited }">★</text>
          <text>收藏</text>
        </view>
      </view>
    </view>

    <!-- 正文 -->
    <view class="story-content">
      <text>{{ story.content }}</text>
    </view>

    <!-- 标签 -->
    <view class="story-tags" v-if="story.tags && story.tags.length">
      <text class="tag-item" v-for="tag in story.tags" :key="tag">#{{ tag }}</text>
    </view>

    <!-- 评论区 -->
    <view class="comment-section">
      <view class="section-title">评论区 ({{ story.commentCount || 0 }})</view>
      
      <!-- 加载评论 -->
      <view v-if="loadingComments" class="loading">加载中...</view>
      <view v-else-if="comments.length === 0" class="empty-comment">暂无评论,来发表第一条评论吧~</view>
      <view v-else class="comment-list">
        <view v-for="comment in comments" :key="comment.id" class="comment-item">
          <view class="comment-content">{{ comment.content }}</view>
          <view class="comment-footer">
            <text class="comment-time">{{ formatTime(comment.createdAt) }}</text>
          </view>
        </view>
      </view>
    </view>

    <!-- 底部输入框 -->
    <view class="bottom-bar">
      <input 
        v-model="commentText" 
        class="comment-input" 
        placeholder="写下你的温暖回复..."
        confirm-type="send"
        @confirm="onSubmitComment"
      />
      <button class="send-btn" @tap="onSubmitComment">发送</button>
    </view>

    <!-- 温暖回复模板 -->
    <view class="template-bar" v-if="templates.length">
      <view class="template-title">温暖回复</view>
      <scroll-view scroll-x>
        <view class="template-list">
          <view 
            v-for="t in templates" 
            :key="t.id"
            class="template-item"
            @tap="useTemplate(t.content)"
          >
            {{ t.content }}
          </view>
        </view>
      </scroll-view>
    </view>
  </view>
  
  <view v-else class="loading-page">
    <text>加载中...</text>
  </view>
</template>

<script>
import { storyApi } from '@/utils/request'

export default {
  data() {
    return {
      storyId: null,
      story: null,
      comments: [],
      templates: [],
      commentText: '',
      loadingComments: false,
      commentPage: 1,
      commentSize: 20
    }
  },
  
  onLoad(options) {
    this.storyId = options.id
    this.loadDetail()
    this.loadComments()
    this.loadTemplates()
  },
  
  methods: {
    async loadDetail() {
      try {
        const res = await storyApi.getDetail(this.storyId)
        this.story = res
      } catch (e) {
        console.error('加载详情失败', e)
        uni.showToast({ title: '加载失败', icon: 'none' })
      }
    },
    
    async loadComments() {
      this.loadingComments = true
      try {
        const res = await storyApi.getComments(this.storyId, {
          page: this.commentPage,
          size: this.commentSize
        })
        this.comments = res.records || []
      } catch (e) {
        console.error('加载评论失败', e)
      } finally {
        this.loadingComments = false
      }
    },
    
    async loadTemplates() {
      try {
        const res = await storyApi.getTemplates({ emotionType: this.story?.emotionType })
        this.templates = res || []
      } catch (e) {
        console.error('加载模板失败', e)
      }
    },
    
    async onLike() {
      try {
        if (this.story.liked) {
          await storyApi.unlike(this.storyId)
          this.story.liked = false
          this.story.likeCount--
        } else {
          await storyApi.like(this.storyId)
          this.story.liked = true
          this.story.likeCount++
        }
      } catch (e) {
        console.error('操作失败', e)
      }
    },
    
    async onFavorite() {
      try {
        if (this.story.favorited) {
          uni.showToast({ title: '已收藏', icon: 'none' })
          return
        }
        await storyApi.favorite(this.storyId)
        this.story.favorited = true
        uni.showToast({ title: '已收藏', icon: 'success' })
      } catch (e) {
        console.error('收藏失败', e)
      }
    },
    
    async onSubmitComment() {
      if (!this.commentText.trim()) {
        uni.showToast({ title: '请输入内容', icon: 'none' })
        return
      }
      
      try {
        await storyApi.addComment(this.storyId, {
          content: this.commentText.trim(),
          parentId: null
        })
        this.commentText = ''
        uni.showToast({ title: '评论成功', icon: 'success' })
        this.loadComments()
        this.story.commentCount++
      } catch (e) {
        console.error('评论失败', e)
      }
    },
    
    useTemplate(content) {
      this.commentText = content
    },
    
    getEmotionLabel(type) {
      const map = {
        happy: '开心', calm: '平静', sad: '难过',
        anxious: '焦虑', angry: '愤怒', fear: '害怕'
      }
      return map[type] || '其他'
    },
    
    getEmotionColor(type) {
      const map = {
        happy: '#52c41a', calm: '#1890ff', sad: '#8c8c8c',
        anxious: '#faad14', angry: '#ff4d4f', fear: '#722ed1'
      }
      return map[type] || '#8c8c8c'
    },
    
    formatTime(time) {
      if (!time) return ''
      const date = new Date(time)
      return `${date.getMonth() + 1}/${date.getDate()} ${date.getHours()}:${String(date.getMinutes()).padStart(2, '0')}`
    }
  }
}
</script>

<style lang="scss" scoped>
.story-detail {
  min-height: 100vh;
  background: #fff;
  padding: 32rpx;
  padding-bottom: 200rpx;
}

.detail-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24rpx;
}

.emotion-tag {
  padding: 8rpx 24rpx;
  border-radius: 24rpx;
  font-size: 24rpx;
  color: #fff;
}

.story-time {
  font-size: 24rpx;
  color: #999;
}

.story-title {
  font-size: 40rpx;
  font-weight: bold;
  color: #333;
  line-height: 1.4;
  margin-bottom: 32rpx;
}

.author-bar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding-bottom: 32rpx;
  border-bottom: 1px solid #f0f0f0;
  margin-bottom: 32rpx;
}

.author-info {
  display: flex;
  align-items: center;
  gap: 12rpx;
}

.nickname {
  font-size: 28rpx;
  color: #333;
  font-weight: 500;
}

.anonymous {
  font-size: 22rpx;
  color: #999;
}

.author-actions {
  display: flex;
  gap: 32rpx;
}

.action-btn {
  display: flex;
  align-items: center;
  gap: 8rpx;
}

.icon {
  font-size: 32rpx;
  color: #ccc;
  
  &.liked { color: #ff6b6b; }
  &.favorited { color: #ffc107; }
}

.count {
  font-size: 24rpx;
  color: #999;
}

.story-content {
  font-size: 32rpx;
  color: #333;
  line-height: 1.8;
  margin-bottom: 32rpx;
}

.story-tags {
  display: flex;
  flex-wrap: wrap;
  gap: 16rpx;
  margin-bottom: 48rpx;
}

.tag-item {
  font-size: 26rpx;
  color: #7B68EE;
}

.comment-section {
  padding-top: 32rpx;
  border-top: 16rpx solid #f5f5f5;
}

.section-title {
  font-size: 32rpx;
  font-weight: bold;
  margin-bottom: 24rpx;
}

.loading, .empty-comment {
  text-align: center;
  color: #999;
  padding: 48rpx 0;
}

.comment-list {
  display: flex;
  flex-direction: column;
  gap: 24rpx;
}

.comment-item {
  padding: 24rpx;
  background: #f9f9f9;
  border-radius: 16rpx;
}

.comment-content {
  font-size: 28rpx;
  color: #333;
  line-height: 1.6;
  margin-bottom: 12rpx;
}

.comment-footer {
  text-align: right;
}

.comment-time {
  font-size: 22rpx;
  color: #999;
}

.bottom-bar {
  position: fixed;
  left: 0;
  right: 0;
  bottom: 0;
  padding: 24rpx 32rpx;
  background: #fff;
  border-top: 1px solid #f0f0f0;
  display: flex;
  gap: 16rpx;
  align-items: center;
}

.comment-input {
  flex: 1;
  height: 80rpx;
  padding: 0 24rpx;
  background: #f5f5f5;
  border-radius: 40rpx;
  font-size: 28rpx;
}

.send-btn {
  width: 140rpx;
  height: 80rpx;
  line-height: 80rpx;
  background: #7B68EE;
  color: #fff;
  border-radius: 40rpx;
  font-size: 28rpx;
}

.template-bar {
  position: fixed;
  left: 0;
  right: 0;
  bottom: 120rpx;
  background: #fff;
  padding: 16rpx 32rpx;
  border-top: 1px solid #f0f0f0;
}

.template-title {
  font-size: 24rpx;
  color: #999;
  margin-bottom: 12rpx;
}

.template-list {
  display: flex;
  gap: 16rpx;
}

.template-item {
  flex-shrink: 0;
  padding: 12rpx 24rpx;
  background: #f0f0f0;
  border-radius: 24rpx;
  font-size: 24rpx;
  color: #666;
}

.loading-page {
  display: flex;
  align-items: center;
  justify-content: center;
  height: 100vh;
  color: #999;
}
</style>