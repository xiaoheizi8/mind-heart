<template>
  <view class="page-container">
    <loading-box :visible="loading" />
    <view v-if="item" class="animate-fade-in">
      <view class="hero-gradient-sm" :style="{ background: item.riskLevel >= 3 ? 'linear-gradient(135deg,#FF6B6B,#FF8E53)' : 'linear-gradient(135deg,var(--primary-color),var(--primary-light))' }">
        <view class="flex-center" style="flex-direction:column;">
          <text style="font-size:64rpx;">{{ riskInfo.icon }}</text>
          <text class="text-xl font-bold mt-8">{{ riskInfo.label }}</text>
          <text class="text-sm mt-4" style="opacity:0.85;">{{ item.triggerType || '关键词' }}触发</text>
        </view>
      </view>

      <view class="card">
        <view class="detail-row"><text class="detail-label">处理状态</text><text :style="{ color: item.handled ? '#52C41A' : '#FAAD14', fontWeight: 500 }">{{ item.handled ? '已处理' : '待处理' }}</text></view>
        <view class="detail-row"><text class="detail-label">触发时间</text><text>{{ formatDate(item.createdAt) }}</text></view>
        <view class="detail-row" v-if="item.handledAt"><text class="detail-label">处理时间</text><text>{{ formatDate(item.handledAt) }}</text></view>
        <view class="detail-row" v-if="item.handlerNote"><text class="detail-label">处理备注</text><text>{{ item.handlerNote }}</text></view>
      </view>

      <view class="card"><text class="card-title mb-16">触发内容</text><text class="content-text">{{ item.content }}</text></view>

      <view v-if="!item.handled" style="padding:0 24rpx 40rpx;">
        <button class="btn btn-primary btn-block" @click="handleWarning" :disabled="handling">{{ handling ? '处理中...' : '标记为已处理' }}</button>
      </view>
    </view>
  </view>
</template>
<script>
import { parentApi } from '../../utils/request.js'
import { RISK_MAP, formatDate } from '../../utils/index.js'
import LoadingBox from '../../components/loading-box.vue'
export default {
  components: { LoadingBox },
  data() { return { childId: 0, warningId: 0, item: null, loading: false, handling: false } },
  onLoad(opt) { this.childId = Number(opt.childId); this.warningId = Number(opt.warningId); this.load() },
  computed: { riskInfo() { return RISK_MAP[this.item?.riskLevel] || RISK_MAP[1] } },
  methods: {
    async load() {
      this.loading = true
      try { this.item = await parentApi.getChildWarningDetail(this.childId, this.warningId) } catch (e) { uni.showToast({ title: e.message, icon: 'none' }) }
      finally { this.loading = false }
    },
    async handleWarning() {
      this.handling = true
      try { await parentApi.handleWarning(this.childId, this.warningId); uni.showToast({ title: '处理成功', icon: 'success' }); this.item.handled = 1; this.item.handledAt = new Date().toISOString() }
      catch (e) { uni.showToast({ title: e.message, icon: 'none' }) }
      finally { this.handling = false }
    },
    formatDate
  }
}
</script>
<style scoped>
.detail-row { display: flex; justify-content: space-between; padding: 16rpx 0; border-bottom: 1rpx solid var(--border-light); }
.detail-label { color: var(--text-tertiary); }
.content-text { font-size: var(--font-base); line-height: 1.8; background: var(--bg-secondary); padding: 24rpx; border-radius: var(--radius-md); display: block; }
</style>
