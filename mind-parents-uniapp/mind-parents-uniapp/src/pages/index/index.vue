<template>
  <view class="page-container">
    <!-- 顶部横幅 -->
    <view class="hero-gradient">
      <view class="flex-between">
        <view>
          <text class="greeting-name">欢迎，{{ user.nickname || user.username || '家长' }}</text>
          <text class="greeting-stat mt-8">{{ children.length > 0 ? '已绑定 ' + children.length + ' 个孩子' + (alertCount > 0 ? '，' + alertCount + ' 个需要关注' : '') : '添加您的孩子开始关注' }}</text>
        </view>
        <view class="user-avatar">{{ (user.nickname || user.username || '?')[0] }}</view>
      </view>
    </view>

    <!-- 孩子卡片 -->
    <view v-if="loading" style="padding: 24rpx;">
      <view class="skeleton skeleton-card"></view>
      <view class="skeleton skeleton-card"></view>
    </view>

    <view v-else-if="children.length === 0">
      <view class="empty-state" style="padding:120rpx 48rpx;">
        <text class="empty-icon">👨‍👩‍👧</text>
        <text class="empty-text">还没有绑定孩子</text>
        <text class="empty-desc">添加您的孩子，实时关注他们的心理健康</text>
        <button class="btn btn-primary mt-32" @click="goBind">+ 绑定孩子</button>
      </view>
    </view>

    <view v-else class="child-list animate-fade-in">
      <view class="child-card card" v-for="child in children" :key="child.id" @click="goOverview(child)">
        <view class="flex-between">
          <view class="flex-center gap-12">
            <view class="child-avatar">{{ (child.nickname || child.username || '?')[0] }}</view>
            <view>
              <text class="child-name">{{ child.nickname || child.username }}</text>
              <text class="text-sm text-hint" style="display:block;">{{ child.age ? child.age + '岁' : '' }} {{ child.gender === 1 ? '♂' : child.gender === 2 ? '♀' : '' }}</text>
            </view>
          </view>
          <view class="flex-center gap-8">
            <emotion-badge v-if="child.latestEmotion" :category="child.latestEmotion" :score="child.latestEmotionScore" size="small" />
            <text v-if="child.hasHighRiskWarning" class="risk-dot animate-pulse">⚠️</text>
          </view>
        </view>
        <view class="child-meta mt-16 flex-between">
          <text class="text-sm text-hint">{{ child.lastDiaryDate ? '最后日记: ' + formatRelative(child.lastDiaryDate) : '暂无日记' }}</text>
          <text class="text-sm text-primary-color" style="font-weight:500;">查看详情 →</text>
        </view>
      </view>

      <view style="padding: 0 24rpx 40rpx;">
        <button class="btn btn-outline btn-block" @click="goBind">+ 绑定新孩子</button>
      </view>
    </view>
  </view>
</template>
<script>
import { parentApi, userApi } from '../../utils/request.js'
import { formatRelativeTime } from '../../utils/index.js'
import EmotionBadge from '../../components/emotion-badge.vue'
export default {
  components: { EmotionBadge },
  data() { return { user: {}, children: [], alertCount: 0, loading: true } },
  onShow() { this.loadData() },
  async onPullDownRefresh() {
    await this.loadData()
    uni.stopPullDownRefresh()
  },
  methods: {
    async loadData() {
      this.loading = true
      try {
        this.user = (await userApi.getProfile()) || {}
        this.children = (await parentApi.getChildren()) || []
        this.alertCount = this.children.filter(c => c.hasHighRiskWarning).length
      } catch (e) { console.error(e) }
      finally { this.loading = false }
    },
    formatRelative: formatRelativeTime,
    goOverview(child) { uni.navigateTo({ url: `/pages/child/overview?childId=${child.id}&name=${encodeURIComponent(child.nickname || child.username || '')}` }) },
    goBind() { uni.switchTab({ url: '/pages/bind/bind' }) }
  }
}
</script>
<style scoped>
.greeting-name { font-size: var(--font-2xl); font-weight: bold; display: block; }
.greeting-stat { font-size: var(--font-sm); opacity: 0.85; display: block; }
.user-avatar { width: 80rpx; height: 80rpx; border-radius: 50%; background: rgba(255,255,255,0.25); display: flex; align-items: center; justify-content: center; font-size: var(--font-xl); font-weight: bold; color: #fff; }
.child-card { margin-bottom: 4rpx; }
.child-avatar { width: 80rpx; height: 80rpx; border-radius: 50%; background: linear-gradient(135deg, #E8E0FF, #D5CCFF); display: flex; align-items: center; justify-content: center; font-size: var(--font-lg); font-weight: bold; color: var(--primary-color); }
.child-name { font-size: var(--font-lg); font-weight: 600; }
.child-meta { padding-top: 16rpx; border-top: 1rpx solid var(--border-light); }
.risk-dot { font-size: 28rpx; }
</style>
