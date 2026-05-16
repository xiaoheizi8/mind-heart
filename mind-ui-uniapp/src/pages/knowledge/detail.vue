<template>
  <view class="page-knowledge-detail">
    <view v-if="loading" class="loading-state">
      <text>加载中...</text>
    </view>
    
    <view v-else class="detail-content">
      <!-- 头部 -->
      <view class="detail-header">
        <view class="category-tag">{{ article.category }}</view>
        <text class="detail-title">{{ article.title }}</text>
        <view class="meta-info">
          <text class="meta-author">{{ article.source }}</text>
          <text class="meta-time">{{ article.createdAt }}</text>
        </view>
      </view>
      
      <!-- 内容 -->
      <view class="detail-body">
        <text class="article-content">{{ article.content }}</text>
      </view>
      
      <!-- 收藏 -->
      <view class="action-bar">
        <view class="action-item" :class="{ collected: article.collected }" @click="toggleCollect">
          <text class="action-icon">{{ article.collected ? '❤️' : '🤍' }}</text>
          <text class="action-text">{{ article.collected ? '已收藏' : '收藏' }}</text>
        </view>
        <view class="action-item" @click="shareArticle">
          <text class="action-icon">📤</text>
          <text class="action-text">分享</text>
        </view>
      </view>
      
      <!-- 相关推荐 -->
      <view class="related-section">
        <view class="section-title">相关推荐</view>
        <view class="related-list">
          <view 
            v-for="item in relatedList" 
            :key="item.id"
            class="related-item"
            @click="goToDetail(item.id)"
          >
            <text class="related-title">{{ item.title }}</text>
            <text class="related-arrow">></text>
          </view>
        </view>
      </view>
    </view>
  </view>
</template>

<script>
import { knowledgeApi } from '@/utils/request.js'

export default {
  data() {
    return {
      articleId: null,
      loading: true,
      article: {},
      relatedList: [],
      isCollecting: false
    }
  },
  onLoad(options) {
    this.articleId = options.id
    this.loadDetail()
  },
  methods: {
    async loadDetail() {
      this.loading = true
      try {
        const data = await knowledgeApi.getDetail(this.articleId)
        if (data) {
          this.article = {
            ...data,
            createdAt: data.createdAt ? this.formatDate(data.createdAt) : '',
            tags: data.tags ? (typeof data.tags === 'string' ? JSON.parse(data.tags) : data.tags) : []
          }
        }
        this.loadRelated()
      } catch (e) {
        console.error('加载详情失败', e)
        uni.showToast({ title: '加载失败', icon: 'none' })
      } finally {
        this.loading = false
      }
    },
    async loadRelated() {
      try {
        const res = await knowledgeApi.getList({ pageNum: 1, pageSize: 4, category: this.article.category })
        if (res && res.records) {
          this.relatedList = res.records
            .filter(item => item.id != this.articleId)
            .slice(0, 3)
        } else if (Array.isArray(res)) {
          this.relatedList = res.filter(item => item.id != this.articleId).slice(0, 3)
        }
      } catch (e) {
        console.log('加载推荐失败', e)
      }
    },
    formatDate(dateStr) {
      if (!dateStr) return ''
      const date = new Date(dateStr)
      return `${date.getFullYear()}-${String(date.getMonth() + 1).padStart(2, '0')}-${String(date.getDate()).padStart(2, '0')}`
    },
    async toggleCollect() {
      if (this.isCollecting) return
      this.isCollecting = true
      
      try {
        if (this.article.collected) {
          await knowledgeApi.cancelCollect(this.articleId)
          this.article.collected = false
          uni.showToast({ title: '已取消收藏', icon: 'success' })
        } else {
          await knowledgeApi.collect(this.articleId)
          this.article.collected = true
          uni.showToast({ title: '收藏成功', icon: 'success' })
        }
      } catch (e) {
        console.error('收藏操作失败', e)
        uni.showToast({ title: '操作失败', icon: 'none' })
      } finally {
        this.isCollecting = false
      }
    },
    shareArticle() {
      uni.showToast({ title: '分享功能开发中', icon: 'none' })
    },
    goToDetail(id) {
      uni.navigateTo({ url: `/pages/knowledge/detail?id=${id}` })
    }
  }
}
</script>

<style scoped>
.page-knowledge-detail {
  min-height: 100vh;
  background: var(--bg-secondary);
  padding-bottom: 120rpx;
}

.loading-state {
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 200rpx;
  color: var(--text-tertiary);
}

.detail-header {
  background: var(--bg-primary);
  padding: 32rpx;
}

.category-tag {
  display: inline-block;
  padding: 8rpx 20rpx;
  background: rgba(123, 104, 238, 0.1);
  border-radius: 16rpx;
  font-size: 24rpx;
  color: var(--primary-color);
  margin-bottom: 20rpx;
}

.detail-title {
  font-size: 40rpx;
  font-weight: 600;
  color: var(--text-primary);
  display: block;
  line-height: 1.4;
  margin-bottom: 16rpx;
}

.meta-info {
  display: flex;
  gap: 24rpx;
}

.meta-author,
.meta-time {
  font-size: 24rpx;
  color: var(--text-tertiary);
}

.detail-body {
  background: var(--bg-primary);
  margin-top: 16rpx;
  padding: 32rpx;
}

.article-content {
  font-size: 30rpx;
  line-height: 2;
  color: var(--text-primary);
  white-space: pre-wrap;
}

.action-bar {
  display: flex;
  justify-content: center;
  gap: 80rpx;
  padding: 32rpx;
  background: var(--bg-primary);
  margin-top: 16rpx;
}

.action-item {
  display: flex;
  flex-direction: column;
  align-items: center;
}

.action-icon {
  font-size: 40rpx;
}

.action-text {
  font-size: 24rpx;
  color: var(--text-secondary);
  margin-top: 8rpx;
}

.related-section {
  margin-top: 24rpx;
  padding: 32rpx;
  background: var(--bg-primary);
}

.section-title {
  font-size: 30rpx;
  font-weight: 600;
  color: var(--text-primary);
  margin-bottom: 24rpx;
}

.related-list {
  display: flex;
  flex-direction: column;
}

.related-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 24rpx 0;
  border-bottom: 1rpx solid var(--border-light);
}

.related-item:last-child {
  border-bottom: none;
}

.related-title {
  font-size: 28rpx;
  color: var(--text-primary);
}

.related-arrow {
  font-size: 28rpx;
  color: var(--text-tertiary);
}
</style>