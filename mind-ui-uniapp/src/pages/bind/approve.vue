<template>
  <view class="page-bind">
    <view class="page-header">
      <text class="page-title">家长绑定请求</text>
      <text class="page-subtitle">确认或拒绝家长的绑定请求</text>
    </view>

    <view v-if="loading" class="loading-state">
      <text>加载中...</text>
    </view>

    <view v-else-if="requests.length === 0" class="empty-state">
      <text class="empty-icon">📋</text>
      <text class="empty-text">暂无待处理的绑定请求</text>
      <text class="empty-desc">当家长发起绑定请求时，会在这里显示</text>
    </view>

    <view v-else class="request-list">
      <view class="request-card" v-for="req in requests" :key="req.id">
        <view class="request-header">
          <text class="request-label">家长绑定请求</text>
          <text class="request-time">{{ formatTime(req.requestTime) }}</text>
        </view>

        <view class="request-actions">
          <button class="btn-reject" @click.stop="handleReject(req)" :disabled="!!handling">
            {{ handling === req.id + 'reject' ? '处理中...' : '拒绝' }}
          </button>
          <button class="btn-approve" @click.stop="handleApprove(req)" :disabled="!!handling">
            {{ handling === req.id + 'approve' ? '处理中...' : '同意' }}
          </button>
        </view>
      </view>
    </view>

    <!-- 已绑定列表 -->
    <view v-if="approvedList.length > 0" class="approved-section">
      <view class="section-title">已绑定的家长</view>
      <view class="approved-item" v-for="item in approvedList" :key="item.id">
        <text class="approved-icon">✅</text>
        <text>已绑定</text>
        <text class="approved-time">{{ formatTime(item.responseTime) }}</text>
      </view>
    </view>
  </view>
</template>

<script>
import { userApi } from '@/utils/request.js'

export default {
  data() {
    return {
      requests: [],
      approvedList: [],
      loading: true,
      handling: ''
    }
  },
  onLoad() { this.loadData() },
  onShow() { this.loadData() },
  methods: {
    async loadData() {
      this.loading = true
      try {
        const res = await userApi.getBindRequests()
        console.log('绑定请求原始响应:', res)
        const all = Array.isArray(res) ? res : (res?.data || res?.records || [])
        console.log('绑定请求列表:', all)
        this.requests = all.filter(r => r.status === 'PENDING')
        this.approvedList = all.filter(r => r.status === 'APPROVED')
      } catch (e) {
        console.error('加载绑定请求失败:', e)
        uni.showToast({ title: '加载失败', icon: 'none' })
      } finally {
        this.loading = false
      }
    },
    async handleApprove(req) {
      console.log('点击同意, bindingId:', req.id)
      this.handling = req.id + 'approve'
      try {
        await userApi.approveBind(req.id)
        uni.showToast({ title: '已同意绑定', icon: 'success' })
        this.loadData()
      } catch (e) {
        console.error('同意绑定失败:', e)
        uni.showToast({ title: typeof e === 'string' ? e : (e?.message || '操作失败'), icon: 'none' })
      } finally {
        this.handling = ''
      }
    },
    async handleReject(req) {
      console.log('点击拒绝, bindingId:', req.id)
      this.handling = req.id + 'reject'
      try {
        await userApi.rejectBind(req.id)
        uni.showToast({ title: '已拒绝', icon: 'none' })
        this.loadData()
      } catch (e) {
        console.error('拒绝绑定失败:', e)
        uni.showToast({ title: typeof e === 'string' ? e : (e?.message || '操作失败'), icon: 'none' })
      } finally {
        this.handling = ''
      }
    },
    formatTime(t) {
      return t ? t.substring(0, 16) : ''
    }
  }
}
</script>

<style scoped>
.page-bind { min-height: 100vh; background: var(--bg-secondary); }
.page-header { background: linear-gradient(135deg, var(--primary-color), var(--primary-light)); padding: 48rpx 32rpx 40rpx; color: #fff; }
.page-title { font-size: 36rpx; font-weight: bold; display: block; }
.page-subtitle { font-size: 26rpx; opacity: 0.85; margin-top: 8rpx; display: block; }
.loading-state, .empty-state { display: flex; flex-direction: column; align-items: center; padding: 120rpx; }
.empty-icon { font-size: 80rpx; }
.empty-text { font-size: 30rpx; color: var(--text-secondary); margin-top: 24rpx; }
.empty-desc { font-size: 24rpx; color: var(--text-tertiary); margin-top: 8rpx; }
.request-list { padding: 24rpx; }
.request-card { background: #fff; border-radius: 24rpx; padding: 32rpx; margin-bottom: 24rpx; box-shadow: 0 2rpx 12rpx rgba(0,0,0,0.06); }
.request-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 24rpx; }
.request-label { font-size: 30rpx; font-weight: 600; color: var(--text-primary); }
.request-time { font-size: 24rpx; color: var(--text-tertiary); }
.request-actions { display: flex; gap: 16rpx; }
.btn-reject, .btn-approve { flex: 1; height: 80rpx; border-radius: 40rpx; font-size: 28rpx; font-weight: 500; display: flex; align-items: center; justify-content: center; border: none; }
.btn-reject { background: #f5f5f5; color: var(--text-secondary); }
.btn-approve { background: linear-gradient(135deg, var(--primary-color), var(--primary-light)); color: #fff; }
.approved-section { padding: 0 24rpx 40rpx; }
.section-title { font-size: 28rpx; font-weight: 600; color: var(--text-secondary); margin-bottom: 16rpx; }
.approved-item { display: flex; align-items: center; gap: 12rpx; padding: 16rpx 0; border-bottom: 1rpx solid var(--border-light); font-size: 28rpx; }
.approved-icon { font-size: 32rpx; }
.approved-time { margin-left: auto; font-size: 24rpx; color: var(--text-tertiary); }
</style>
