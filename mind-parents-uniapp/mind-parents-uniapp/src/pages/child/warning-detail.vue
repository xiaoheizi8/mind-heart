<template>
  <view class="page-container">
    <view class="card" v-if="item">
      <text class="detail-title">预警详情</text>
      <view class="detail-row"><text class="label">风险等级</text><risk-level-badge :level="item.riskLevel" /></view>
      <view class="detail-row"><text class="label">触发类型</text><text>{{ item.triggerType || '--' }}</text></view>
      <view class="detail-row"><text class="label">处理状态</text><text :style="{color: item.handled ? '#52C41A' : '#FAAD14'}">{{ item.handled ? '已处理' : '未处理' }}</text></view>
      <view class="detail-row"><text class="label">时间</text><text>{{ formatDate(item.createdAt) }}</text></view>
      <view class="detail-row" v-if="item.handlerNote"><text class="label">处理备注</text><text>{{ item.handlerNote }}</text></view>
      <text class="label" style="margin-top:12px;">触发内容</text>
      <text class="content-text">{{ item.content }}</text>
    </view>
    <loading-box :visible="loading" />
  </view>
</template>
<script>
import { parentApi } from '../../utils/request.js'
import RiskLevelBadge from '../../components/risk-level-badge.vue'
import LoadingBox from '../../components/loading-box.vue'
export default {
  components: { RiskLevelBadge, LoadingBox },
  data() { return { childId: 0, warningId: 0, item: null, loading: false } },
  onLoad(opt) { this.childId = Number(opt.childId); this.warningId = Number(opt.warningId); this.loadDetail() },
  methods: {
    async loadDetail() {
      this.loading = true
      try { this.item = await parentApi.getChildWarningDetail(this.childId, this.warningId) }
      catch (e) { uni.showToast({ title: e.message, icon: 'none' }) }
      finally { this.loading = false }
    },
    formatDate(d) { return d ? d.substring(0, 16) : '' }
  }
}
</script>
<style scoped>
.detail-title { font-size: 18px; font-weight: bold; margin-bottom: 16px; display: block; }
.detail-row { display: flex; justify-content: space-between; align-items: center; padding: 8px 0; border-bottom: 1px solid var(--border-color); }
.label { font-size: 14px; color: var(--text-hint); }
.content-text { font-size: 14px; line-height: 1.6; color: var(--text-primary); background: #F9F9F9; padding: 12px; border-radius: 8px; margin-top: 8px; }
</style>
