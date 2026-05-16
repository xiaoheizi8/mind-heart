<template>
  <view class="page-diary-detail">
    <!-- 加载中 -->
    <view v-if="loading" class="loading-state">
      <text>加载中...</text>
    </view>
    
    <view v-else class="detail-content">
      <!-- 顶部信息 -->
      <view class="detail-header">
        <view class="emotion-badge" :class="diary.emotionCategory">
          {{ diary.emotionCategory }}
        </view>
        <text class="detail-time">{{ diary.createdAt }}</text>
      </view>

      <!-- 日记内容 -->
      <view class="diary-content">
        <text class="content-text">{{ diary.content }}</text>
      </view>

      <!-- 标签 -->
      <view class="tags-section" v-if="parsedDiary.emotionTags && parsedDiary.emotionTags.length">
        <view v-for="tag in parsedDiary.emotionTags" :key="tag" class="tag-item">
          #{{ tag }}
        </view>
      </view>

      <!-- AI分析 -->
      <view class="ai-analysis" v-if="diary.aiAnalysis">
        <view class="analysis-header">
          <text class="ai-icon">🤖</text>
          <text class="ai-title">AI解读</text>
        </view>
        <text class="analysis-content">{{ diary.aiAnalysis }}</text>
      </view>

      <!-- 图片 -->
      <view class="media-section" v-if="parsedDiary.mediaUrls && parsedDiary.mediaUrls.length">
        <view class="media-grid">
          <image 
            v-for="(img, index) in parsedDiary.mediaUrls" 
            :key="index"
            :src="img" 
            mode="aspectFill" 
            class="media-img"
            @click="previewImage(index)"
          />
        </view>
      </view>
    </view>

    <!-- 底部操作 -->
    <view class="action-bar">
      <view class="action-item" @click="shareDiary">
        <text class="action-icon">📤</text>
        <text class="action-text">分享</text>
      </view>
      <view class="action-item" @click="editDiary">
        <text class="action-icon">✏️</text>
        <text class="action-text">编辑</text>
      </view>
      <view class="action-item delete" @click="deleteDiary">
        <text class="action-icon">🗑️</text>
        <text class="action-text">删除</text>
      </view>
    </view>
  </view>
</template>

<script>
import { diaryApi } from '../../utils/request.js'

export default {
  data() {
    return {
      diaryId: null,
      loading: true,
      diary: {}
    }
  },
  onLoad(options) {
    this.diaryId = options.id
    this.loadDetail()
  },
  computed: {
    parsedDiary() {
      return {
        ...this.diary,
        emotionTags: typeof this.diary.emotionTags === 'string' 
          ? JSON.parse(this.diary.emotionTags || '[]') 
          : (this.diary.emotionTags || []),
        mediaUrls: typeof this.diary.mediaUrls === 'string'
          ? JSON.parse(this.diary.mediaUrls || '[]')
          : (this.diary.mediaUrls || [])
      }
    }
  },
  methods: {
    async loadDetail() {
      try {
        const res = await diaryApi.getDetail(this.diaryId)
        this.diary = res
      } catch (e) {
        // 模拟数据
        this.diary = {
          id: this.diaryId,
          createdAt: '2026年4月3日 15:30',
          content: '今天考试没考好，心情很低落。但是想了想，一次考试并不能决定什么，下次继续努力吧。人生还有很长的路要走，不应该被一次失败打倒。',
          emotionCategory: 'sad',
          emotionTags: ['考试', '失落', '自我安慰'],
          aiAnalysis: '从你的日记中，我感受到你正在经历考试失利的挫折。这是一个很常见的情绪体验。值得注意的是，你在文字中已经展现出了很好的自我调节能力，能够客观地看待这次经历。继续保持这种积极的思维方式，相信你会在下一次考试中做得更好。',
          mediaUrls: []
        }
      } finally {
        this.loading = false
      }
    },
    previewImage(index) {
      uni.previewImage({
        urls: this.diary.mediaUrls,
        current: index
      })
    },
    shareDiary() {
      uni.showToast({ title: '分享功能开发中', icon: 'none' })
    },
    editDiary() {
      uni.navigateTo({ url: `/pages/diary/edit?id=${this.diaryId}` })
    },
    deleteDiary() {
      uni.showModal({
        title: '确认删除',
        content: '确定要删除这篇日记吗？删除后无法恢复。',
        success: async (res) => {
          if (res.confirm) {
            try {
              await diaryApi.delete(this.diaryId)
              uni.showToast({ title: '删除成功', icon: 'success' })
              setTimeout(() => {
                uni.navigateBack()
              }, 1500)
            } catch (e) {
              uni.showToast({ title: '删除失败', icon: 'none' })
            }
          }
        }
      })
    }
  }
}
</script>

<style scoped>
.page-diary-detail {
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

.detail-content {
  padding: 32rpx 24rpx;
}

.detail-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 32rpx;
}

.emotion-badge {
  padding: 12rpx 24rpx;
  border-radius: 24rpx;
  font-size: 26rpx;
}

.emotion-badge.happy {
  background: rgba(255, 215, 0, 0.2);
  color: #B8860B;
}

.emotion-badge.sad {
  background: rgba(107, 142, 159, 0.2);
  color: #5A7A8A;
}

.emotion-badge.calm {
  background: rgba(135, 206, 235, 0.2);
  color: #4682B4;
}

.emotion-badge.anxious {
  background: rgba(255, 127, 80, 0.2);
  color: #CD5C5C;
}

.detail-time {
  font-size: 26rpx;
  color: var(--text-tertiary);
}

.diary-content {
  background: var(--bg-primary);
  border-radius: 24rpx;
  padding: 32rpx;
  margin-bottom: 24rpx;
}

.content-text {
  font-size: 30rpx;
  line-height: 1.8;
  color: var(--text-primary);
}

.tags-section {
  display: flex;
  flex-wrap: wrap;
  gap: 16rpx;
  margin-bottom: 24rpx;
}

.tag-item {
  padding: 12rpx 20rpx;
  background: rgba(123, 104, 238, 0.1);
  border-radius: 24rpx;
  font-size: 24rpx;
  color: var(--primary-color);
}

.ai-analysis {
  background: linear-gradient(135deg, #F0F0FF 0%, #F8F6FF 100%);
  border-radius: 24rpx;
  padding: 32rpx;
  margin-bottom: 24rpx;
}

.analysis-header {
  display: flex;
  align-items: center;
  margin-bottom: 16rpx;
}

.ai-icon {
  font-size: 32rpx;
  margin-right: 12rpx;
}

.ai-title {
  font-size: 28rpx;
  font-weight: 500;
  color: var(--primary-color);
}

.analysis-content {
  font-size: 28rpx;
  line-height: 1.8;
  color: var(--text-secondary);
}

.media-section {
  margin-bottom: 24rpx;
}

.media-grid {
  display: flex;
  flex-wrap: wrap;
  gap: 16rpx;
}

.media-img {
  width: 200rpx;
  height: 200rpx;
  border-radius: 16rpx;
}

.action-bar {
  position: fixed;
  bottom: 0;
  left: 0;
  right: 0;
  display: flex;
  justify-content: space-around;
  padding: 24rpx;
  background: var(--bg-primary);
  box-shadow: 0 -4rpx 16rpx rgba(0, 0, 0, 0.05);
}

.action-item {
  display: flex;
  flex-direction: column;
  align-items: center;
}

.action-item.delete .action-icon,
.action-item.delete .action-text {
  color: var(--error-color);
}

.action-icon {
  font-size: 40rpx;
}

.action-text {
  font-size: 24rpx;
  color: var(--text-secondary);
  margin-top: 8rpx;
}
</style>