<template>
  <view class="page-diary-list">
    <!-- 顶部统计 -->
    <view class="stats-card">
      <view class="stats-info">
        <text class="stats-label">本月日记</text>
        <text class="stats-num">{{ totalCount }}篇</text>
      </view>
      <view class="stats-info">
        <text class="stats-label">平均情绪</text>
        <text class="stats-emotion" :class="avgEmotion.class">{{ avgEmotion.label }}</text>
      </view>
    </view>

    <!-- 情绪筛选 -->
    <view class="filter-bar">
      <view 
        v-for="item in emotionFilters" 
        :key="item.value"
        class="filter-item"
        :class="{ active: currentFilter === item.value }"
        @click="changeFilter(item.value)"
      >
        {{ item.label }}
      </view>
    </view>

    <!-- 日记列表 -->
    <scroll-view 
      class="diary-scroll" 
      scroll-y 
      @scrolltolower="loadMore"
    >
      <view v-if="parsedDiaryList.length === 0" class="empty-state">
        <text class="empty-icon">📝</text>
        <text class="empty-text">还没有日记哦</text>
        <text class="empty-hint">点击下方按钮开始记录</text>
      </view>
      
      <view 
        v-for="(item, index) in parsedDiaryList" 
        :key="index"
        class="diary-card"
        @click="goToDetail(item.id)"
      >
        <view class="card-header">
          <text class="card-date">{{ item.createdAt }}</text>
          <view class="emotion-tag" :class="getEmotionClass(item.emotionCategory)">
            {{ item.emotionCategory }}
          </view>
        </view>
        
        <text class="card-content">{{ item.content }}</text>
        
        <view class="card-footer">
          <view class="tags-list" v-if="item.emotionTags && item.emotionTags.length">
            <text v-for="tag in item.emotionTags" :key="tag" class="tag-item">
              #{{ tag }}
            </text>
          </view>
          <text class="ai-hint" v-if="item.aiAnalysis">AI解读 ></text>
        </view>
      </view>
      
      <view v-if="loading" class="loading-more">
        <text>加载中...</text>
      </view>
      <view v-if="noMore" class="no-more">
        <text>没有更多了</text>
      </view>
    </scroll-view>

    <!-- 悬浮写日记按钮 -->
    <view class="fab-btn" @click="goToCreate">
      <text class="fab-icon">+</text>
    </view>
  </view>
</template>

<script>
import { diaryApi } from '../../utils/request.js'

export default {
  data() {
    return {
      totalCount: 0,
      avgEmotion: { label: '平静', class: 'calm' },
      currentFilter: 'all',
      emotionFilters: [
        { label: '全部', value: 'all' },
        { label: '开心', value: 'happy' },
        { label: '平静', value: 'calm' },
        { label: '难过', value: 'sad' },
        { label: '焦虑', value: 'anxious' },
        { label: '愤怒', value: 'angry' }
      ],
      diaryList: [],
      page: 1,
      pageSize: 10,
      loading: false,
      noMore: false
    }
  },
  onLoad() {
    this.loadDiaryList()
  },
  onPullDownRefresh() {
    this.refreshList()
  },
  computed: {
    parsedDiaryList() {
      return this.diaryList.map(item => ({
        ...item,
        emotionTags: typeof item.emotionTags === 'string' 
          ? JSON.parse(item.emotionTags || '[]') 
          : (item.emotionTags || []),
        mediaUrls: typeof item.mediaUrls === 'string'
          ? JSON.parse(item.mediaUrls || '[]')
          : (item.mediaUrls || [])
      }))
    }
  },
  methods: {
    async loadDiaryList() {
      if (this.loading) return
      this.loading = true
      
      try {
        const params = {
          pageNum: this.page,
          pageSize: this.pageSize,
          emotionCategory: this.currentFilter === 'all' ? undefined : this.currentFilter
        }
        
        const res = await diaryApi.getList(params)
        
        if (this.page === 1) {
          this.diaryList = res.records || []
        } else {
          this.diaryList = [...this.diaryList, ...(res.records || [])]
        }
        
        this.totalCount = res.total || 0
        this.noMore = res.records?.length < this.pageSize
      } catch (e) {
        // 模拟数据
        this.diaryList = this.getMockDiaryList()
      } finally {
        this.loading = false
      }
    },
    refreshList() {
      this.page = 1
      this.noMore = false
      this.loadDiaryList()
      uni.stopPullDownRefresh()
    },
    loadMore() {
      if (!this.noMore) {
        this.page++
        this.loadDiaryList()
      }
    },
    changeFilter(value) {
      this.currentFilter = value
      this.refreshList()
    },
    getEmotionClass(category) {
      const map = {
        'happy': 'happy',
        'calm': 'calm',
        'sad': 'sad',
        'anxious': 'anxious',
        'angry': 'angry'
      }
      return map[category] || 'calm'
    },
    goToDetail(id) {
      uni.navigateTo({ url: `/pages/diary/detail?id=${id}` })
    },
    goToCreate() {
      uni.navigateTo({ url: '/pages/diary/create' })
    },
    getMockDiaryList() {
      return [
        {
          id: 1,
          createdAt: '今天 15:30',
          content: '今天考试没考好，心情很低落。但是想了想，一次考试并不能决定什么，下次继续努力吧。',
          emotionCategory: 'sad',
          emotionTags: ['考试', '失落', '自我安慰']
        },
        {
          id: 2,
          createdAt: '昨天 20:00',
          content: '和朋友们一起看了电影，笑得很开心！享受当下最简单的快乐。',
          emotionCategory: 'happy',
          emotionTags: ['朋友', '电影', '快乐']
        },
        {
          id: 3,
          createdAt: '3月30日 22:00',
          content: '今天进行了期末汇报，虽然有点紧张但整体表现还不错，给自己点个赞！',
          emotionCategory: 'happy',
          emotionTags: ['汇报', '成长', '自信']
        }
      ]
    }
  }
}
</script>

<style scoped>
.page-diary-list {
  min-height: 100vh;
  background: var(--bg-secondary);
  padding: 24rpx;
  padding-bottom: 160rpx;
}

.stats-card {
  display: flex;
  justify-content: space-around;
  background: linear-gradient(135deg, #7B68EE, #9D8FFF);
  border-radius: 24rpx;
  padding: 32rpx;
  margin-bottom: 24rpx;
}

.stats-info {
  display: flex;
  flex-direction: column;
  align-items: center;
}

.stats-label {
  font-size: 24rpx;
  color: rgba(255, 255, 255, 0.8);
}

.stats-num {
  font-size: 40rpx;
  font-weight: 600;
  color: #FFF;
  margin-top: 8rpx;
}

.stats-emotion {
  font-size: 28rpx;
  font-weight: 500;
  color: #FFF;
  margin-top: 8rpx;
}

.filter-bar {
  display: flex;
  gap: 16rpx;
  margin-bottom: 24rpx;
  overflow-x: auto;
  padding-bottom: 8rpx;
}

.filter-item {
  padding: 12rpx 24rpx;
  background: var(--bg-primary);
  border-radius: 32rpx;
  font-size: 26rpx;
  color: var(--text-secondary);
  white-space: nowrap;
}

.filter-item.active {
  background: var(--primary-color);
  color: #FFF;
}

.diary-scroll {
  height: calc(100vh - 280rpx);
}

.empty-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 120rpx 0;
}

.empty-icon {
  font-size: 96rpx;
}

.empty-text {
  font-size: 32rpx;
  color: var(--text-primary);
  margin-top: 24rpx;
}

.empty-hint {
  font-size: 26rpx;
  color: var(--text-tertiary);
  margin-top: 12rpx;
}

.diary-card {
  background: var(--bg-primary);
  border-radius: 24rpx;
  padding: 32rpx;
  margin-bottom: 24rpx;
  box-shadow: var(--shadow-light);
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16rpx;
}

.card-date {
  font-size: 24rpx;
  color: var(--text-tertiary);
}

.emotion-tag {
  padding: 8rpx 20rpx;
  border-radius: 20rpx;
  font-size: 24rpx;
}

.emotion-tag.happy {
  background: rgba(255, 215, 0, 0.2);
  color: #B8860B;
}

.emotion-tag.calm {
  background: rgba(135, 206, 235, 0.2);
  color: #4682B4;
}

.emotion-tag.sad {
  background: rgba(107, 142, 159, 0.2);
  color: #5A7A8A;
}

.emotion-tag.anxious {
  background: rgba(255, 127, 80, 0.2);
  color: #CD5C5C;
}

.emotion-tag.angry {
  background: rgba(255, 69, 0, 0.2);
  color: #B22222;
}

.card-content {
  font-size: 28rpx;
  color: var(--text-primary);
  line-height: 1.8;
  display: -webkit-box;
  -webkit-line-clamp: 3;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.card-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-top: 20rpx;
}

.tags-list {
  display: flex;
  flex-wrap: wrap;
  gap: 12rpx;
}

.tag-item {
  font-size: 22rpx;
  color: var(--primary-color);
}

.ai-hint {
  font-size: 22rpx;
  color: var(--text-tertiary);
}

.loading-more,
.no-more {
  text-align: center;
  padding: 24rpx;
  font-size: 24rpx;
  color: var(--text-tertiary);
}

.fab-btn {
  position: fixed;
  right: 32rpx;
  bottom: 120rpx;
  width: 112rpx;
  height: 112rpx;
  background: linear-gradient(135deg, var(--primary-color), var(--primary-dark));
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  box-shadow: 0 8rpx 24rpx rgba(123, 104, 238, 0.4);
}

.fab-icon {
  font-size: 64rpx;
  color: #FFF;
  font-weight: 300;
}
</style>