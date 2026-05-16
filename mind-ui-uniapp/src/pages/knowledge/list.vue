<template>
  <view class="page-knowledge-list">
    <!-- 搜索 -->
    <view class="search-bar">
      <view class="search-input-wrap">
        <text class="search-icon">🔍</text>
        <input 
          class="search-input" 
          v-model="keyword" 
          placeholder="搜索心理知识"
          @confirm="search"
        />
      </view>
    </view>

    <!-- 分类 -->
    <view class="category-tabs">
      <view 
        v-for="cat in categories" 
        :key="cat.value"
        class="tab-item"
        :class="{ active: currentCategory === cat.value }"
        @click="changeCategory(cat.value)"
      >
        {{ cat.label }}
      </view>
    </view>

    <!-- 标签 -->
    <view class="tag-scroll" v-if="tags.length > 0">
      <view 
        v-for="tag in tags" 
        :key="tag"
        class="tag-item"
        :class="{ active: currentTag === tag }"
        @click="changeTag(tag)"
      >
        {{ tag }}
      </view>
    </view>

    <!-- 知识列表 -->
    <scroll-view 
      class="knowledge-scroll" 
      scroll-y 
      @scrolltolower="loadMore"
    >
      <view v-if="list.length === 0" class="empty-state">
        <text class="empty-icon">📚</text>
        <text class="empty-text">暂无相关内容</text>
      </view>
      
      <view 
        v-for="item in list" 
        :key="item.id"
        class="knowledge-card"
        @click="goToDetail(item.id)"
      >
        <view class="card-header">
          <text class="card-category">{{ item.category }}</text>
          <text class="card-time">{{ item.createdAt }}</text>
        </view>
        <text class="card-title">{{ item.title }}</text>
        <text class="card-desc">{{ item.summary }}</text>
        <view class="card-footer">
          <view class="card-tags">
            <text v-for="(tag, index) in (item._tags || [])" :key="index" class="tag-item">
              {{ tag }}
            </text>
          </view>
          <text class="read-more">阅读全文 ></text>
        </view>
      </view>
      
      <view v-if="loading" class="loading-more">
        <text>加载中...</text>
      </view>
    </scroll-view>
  </view>
</template>

<script>
import { knowledgeApi } from '@/utils/request.js'

export default {
  data() {
    return {
      keyword: '',
      currentCategory: 'all',
      currentTag: '',
      categories: [
        { value: 'all', label: '全部' },
        { value: 'knowledge', label: '心理知识' },
        { value: 'skill', label: '调节技巧' },
        { value: 'case', label: '案例故事' }
      ],
      tags: [],
      list: [],
      page: 1,
      pageSize: 10,
      loading: false,
      hasMore: true
    }
  },
  onLoad() {
    this.loadTags()
    this.loadList()
  },
  methods: {
    search() {
      this.page = 1
      this.list = []
      this.hasMore = true
      this.loadList()
    },
    changeCategory(cat) {
      this.currentCategory = cat
      this.currentTag = '' // 清空标签筛选
      this.page = 1
      this.list = []
      this.hasMore = true
      this.loadList()
    },
    changeTag(tag) {
      this.currentTag = this.currentTag === tag ? '' : tag // 点击已选中的标签取消筛选
      this.page = 1
      this.list = []
      this.hasMore = true
      this.loadList()
    },
    async loadList() {
      if (this.loading || !this.hasMore) return
      this.loading = true
      
      try {
        const params = {
          pageNum: this.page,
          pageSize: this.pageSize,
          keyword: this.keyword || undefined,
          category: this.currentCategory === 'all' ? undefined : this.currentCategory,
          tag: this.currentTag || undefined
        }
        
        const data = await knowledgeApi.getList(params)
        
        if (data && data.records) {
          if (this.page === 1) {
            this.list = data.records
          } else {
            this.list = this.list.concat(data.records)
          }
          this.list = this.list.map(item => ({
            ...item,
            _tags: this.parseTags(item.tags)
          }))
          this.hasMore = this.list.length < data.total
        } else if (Array.isArray(data)) {
          if (this.page === 1) {
            this.list = data
          } else {
            this.list = this.list.concat(data)
          }
          this.hasMore = false
        }
      } catch (e) {
        console.error('加载知识列表失败', e)
        uni.showToast({ title: '加载失败', icon: 'none' })
      } finally {
        this.loading = false
      }
    },
    loadMore() {
      if (!this.hasMore || this.loading) return
      this.page++
      this.loadList()
    },
    goToDetail(id) {
      uni.navigateTo({ url: `/pages/knowledge/detail?id=${id}` })
    },
    parseTags(tags) {
      if (!tags) return []
      if (Array.isArray(tags)) return tags
      try {
        return JSON.parse(tags)
      } catch (e) {
        return []
      }
    },
    async loadTags() {
      try {
        const tags = await knowledgeApi.getTags()
        this.tags = tags || []
      } catch (e) {
        console.error('加载标签失败', e)
      }
    }
  }
}
</script>

<style scoped>
.page-knowledge-list {
  min-height: 100vh;
  background: var(--bg-secondary);
}

.search-bar {
  padding: 24rpx;
  background: var(--bg-primary);
}

.search-input-wrap {
  display: flex;
  align-items: center;
  height: 80rpx;
  background: var(--bg-secondary);
  border-radius: 40rpx;
  padding: 0 24rpx;
}

.search-icon {
  font-size: 32rpx;
  margin-right: 16rpx;
}

.search-input {
  flex: 1;
  font-size: 28rpx;
}

.category-tabs {
  display: flex;
  gap: 16rpx;
  padding: 0 24rpx 24rpx;
  background: var(--bg-primary);
  overflow-x: auto;
}

.tab-item {
  padding: 16rpx 32rpx;
  background: var(--bg-secondary);
  border-radius: 32rpx;
  font-size: 26rpx;
  color: var(--text-secondary);
  white-space: nowrap;
}

.tab-item.active {
  background: var(--primary-color);
  color: #FFF;
}

.tag-scroll {
  display: flex;
  gap: 12rpx;
  padding: 0 24rpx 24rpx;
  background: var(--bg-primary);
  overflow-x: auto;
}

.tag-item {
  padding: 12rpx 24rpx;
  background: var(--bg-secondary);
  border-radius: 24rpx;
  font-size: 24rpx;
  color: var(--text-secondary);
  white-space: nowrap;
  border: 2rpx solid transparent;
}

.tag-item.active {
  background: rgba(123, 104, 238, 0.15);
  color: var(--primary-color);
  border-color: var(--primary-color);
}

.knowledge-scroll {
  height: calc(100vh - 220rpx);
  padding: 24rpx;
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
  font-size: 28rpx;
  color: var(--text-tertiary);
  margin-top: 24rpx;
}

.knowledge-card {
  background: var(--bg-primary);
  border-radius: 24rpx;
  padding: 32rpx;
  margin-bottom: 24rpx;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16rpx;
}

.card-category {
  padding: 8rpx 16rpx;
  background: rgba(123, 104, 238, 0.1);
  border-radius: 16rpx;
  font-size: 22rpx;
  color: var(--primary-color);
}

.card-time {
  font-size: 22rpx;
  color: var(--text-tertiary);
}

.card-title {
  font-size: 32rpx;
  font-weight: 600;
  color: var(--text-primary);
  display: block;
  margin-bottom: 12rpx;
}

.card-desc {
  font-size: 26rpx;
  color: var(--text-secondary);
  line-height: 1.6;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
  margin-bottom: 16rpx;
}

.card-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.card-tags {
  display: flex;
  gap: 12rpx;
}

.tag-item {
  font-size: 22rpx;
  color: var(--text-tertiary);
}

.read-more {
  font-size: 24rpx;
  color: var(--primary-color);
}

.loading-more {
  text-align: center;
  padding: 24rpx;
  font-size: 24rpx;
  color: var(--text-tertiary);
}
</style>