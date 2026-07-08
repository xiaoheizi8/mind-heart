<template>
  <view class="page-container">
    <view class="home-header">
      <text class="greeting">欢迎回来</text>
      <text class="greeting-sub">关注孩子的心理健康</text>
    </view>

    <view class="section-title">已绑定的孩子</view>
    <view v-if="children.length === 0" style="padding: 20px;">
      <empty-state icon="👨‍👩‍👧" text="还没有绑定孩子" desc="添加您的孩子，关注他们的心理健康" />
      <button class="btn btn-primary" style="margin: 20px auto; width: 200px;" @click="goBind">绑定孩子</button>
    </view>

    <view v-else class="child-list">
      <view class="child-card card" v-for="child in children" :key="child.id" @click="goOverview(child)">
        <view class="flex-between">
          <view>
            <text class="child-name">{{ child.nickname || child.username }}</text>
            <text class="child-info">{{ child.age ? child.age + '岁' : '' }}</text>
          </view>
          <view class="flex-center">
            <emotion-badge v-if="child.latestEmotion" :category="child.latestEmotion" :score="child.latestEmotionScore" />
            <text v-if="child.hasHighRiskWarning" style="margin-left:8px;color:var(--risk-high);">⚠️</text>
          </view>
        </view>
      </view>
    </view>

    <view style="padding: 20px;">
      <button class="btn btn-primary" style="width:100%;" @click="goBind">+ 绑定新孩子</button>
    </view>
  </view>
</template>
<script>
import { parentApi } from '../../utils/request.js'
import EmotionBadge from '../../components/emotion-badge.vue'
import EmptyState from '../../components/empty-state.vue'
export default {
  components: { EmotionBadge, EmptyState },
  data() { return { children: [] } },
  onShow() { this.loadChildren() },
  methods: {
    async loadChildren() {
      try { this.children = await parentApi.getChildren() || [] }
      catch (e) { console.error('加载失败:', e) }
    },
    goOverview(child) { uni.navigateTo({ url: `/pages/child/overview?childId=${child.id}&name=${child.nickname || child.username}` }) },
    goBind() { uni.navigateTo({ url: '/pages/bind/bind' }) }
  }
}
</script>
<style scoped>
.home-header { background: linear-gradient(135deg, #7B68EE, #9D8FFF); padding: 40px 24px 30px; color: #fff; }
.greeting { font-size: 24px; font-weight: bold; display: block; }
.greeting-sub { font-size: 14px; opacity: 0.85; margin-top: 6px; display: block; }
.section-title { font-size: 16px; font-weight: 600; padding: 16px 20px 8px; }
.child-card { cursor: pointer; }
.child-name { font-size: 16px; font-weight: 600; margin-right: 8px; }
.child-info { font-size: 13px; color: var(--text-hint); }
</style>
