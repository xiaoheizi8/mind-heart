<template>
  <view class="page-container">
    <view class="card">
      <text class="section-title">绑定孩子</text>
      <text class="hint">输入孩子的用户名或手机号发送绑定请求</text>
      <input class="input" v-model="childIdentifier" placeholder="请输入孩子的用户名或手机号" />
      <button class="btn btn-primary" style="width:100%;margin-top:12px;" @click="doBind" :disabled="loading">
        {{ loading ? '发送中...' : '发送绑定请求' }}
      </button>
    </view>

    <view class="card" v-if="pendingRequests.length > 0">
      <text class="section-title">待处理的请求</text>
      <view class="request-item" v-for="req in pendingRequests" :key="req.id">
        <text>等待确认 ({{ req.requestTime }})</text>
        <button class="btn btn-danger" style="padding:4px 12px;font-size:13px;" @click="doCancel(req.id)">取消</button>
      </view>
    </view>

    <empty-state v-if="pendingRequests.length === 0 && !loading" icon="📝" text="暂无待处理的请求" />
  </view>
</template>
<script>
import { parentApi } from '../../utils/request.js'
import EmptyState from '../../components/empty-state.vue'
export default {
  components: { EmptyState },
  data() { return { childIdentifier: '', loading: false, pendingRequests: [] } },
  onShow() { this.loadRequests() },
  methods: {
    async doBind() {
      if (!this.childIdentifier.trim()) { uni.showToast({ title: '请输入用户名或手机号', icon: 'none' }); return }
      this.loading = true
      try {
        await parentApi.sendBindRequest(this.childIdentifier.trim())
        uni.showToast({ title: '绑定请求已发送', icon: 'success' })
        this.childIdentifier = ''
        this.loadRequests()
      } catch (e) { uni.showToast({ title: e.message, icon: 'none' }) }
      finally { this.loading = false }
    },
    async loadRequests() { try { this.pendingRequests = await parentApi.getBindRequests() || [] } catch (e) {} },
    async doCancel(id) {
      try { await parentApi.cancelBind(id); uni.showToast({ title: '已取消', icon: 'success' }); this.loadRequests() }
      catch (e) { uni.showToast({ title: e.message, icon: 'none' }) }
    }
  }
}
</script>
<style scoped>
.section-title { font-size: 16px; font-weight: 600; margin-bottom: 8px; }
.hint { font-size: 13px; color: var(--text-hint); margin-bottom: 12px; display: block; }
.input { width: 100%; height: 44px; border: 1px solid var(--border-color); border-radius: 8px; padding: 0 14px; font-size: 15px; }
.request-item { display: flex; align-items: center; justify-content: space-between; padding: 10px 0; border-bottom: 1px solid var(--border-color); font-size: 13px; color: var(--text-secondary); }
</style>
