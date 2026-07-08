<template>
  <view class="page-container">
    <view class="card">
      <text class="card-title mb-16">🔗 绑定孩子</text>
      <text class="text-sm text-hint mb-16" style="display:block;">输入孩子的用户名或手机号发送绑定请求，孩子同意后即可查看数据</text>
      <view class="input-wrap"><input class="input" v-model="identifier" placeholder="用户名或手机号" /></view>
      <button class="btn btn-primary btn-block mt-24" @click="doBind" :disabled="sending">{{ sending ? '发送中...' : '发送绑定请求' }}</button>
    </view>

    <view class="card" v-if="pendingList.length > 0">
      <text class="card-title mb-16">⏳ 待确认的请求</text>
      <view class="req-item" v-for="r in pendingList" :key="r.id">
        <view><text class="text-sm">等待确认</text><text class="text-sm text-hint mt-4" style="display:block;">{{ (r.requestTime || '').substring(0,16) }}</text></view>
        <button class="btn btn-sm btn-ghost" style="color:var(--error-color);" @click="doCancel(r.id)">取消</button>
      </view>
    </view>

    <view class="card" v-if="boundList.length > 0">
      <text class="card-title mb-16">✅ 已绑定的孩子</text>
      <view class="req-item" v-for="c in boundList" :key="c.id">
        <view class="flex-center gap-12">
          <view class="child-avatar-sm">{{ (c.nickname || c.username || '?')[0] }}</view>
          <text>{{ c.nickname || c.username }}</text>
        </view>
      </view>
    </view>

    <empty-state v-if="boundList.length===0 && pendingList.length===0 && !sending" icon="🔗" text="暂无绑定关系" desc="在上方输入框中搜索孩子" />
  </view>
</template>
<script>
import { parentApi } from '../../utils/request.js'
import EmptyState from '../../components/empty-state.vue'
export default {
  components: { EmptyState },
  data() { return { identifier: '', sending: false, pendingList: [], boundList: [] } },
  onShow() { this.loadAll() },
  methods: {
    async loadAll() {
      try {
        this.pendingList = (await parentApi.getBindRequests()) || []
        this.boundList = (await parentApi.getChildren()) || []
      } catch (e) {}
    },
    async doBind() {
      if (!this.identifier.trim()) { uni.showToast({ title: '请输入用户名或手机号', icon: 'none' }); return }
      this.sending = true
      try { await parentApi.sendBindRequest(this.identifier.trim()); uni.showToast({ title: '请求已发送', icon: 'success' }); this.identifier = ''; this.loadAll() }
      catch (e) { uni.showToast({ title: e.message, icon: 'none' }) }
      finally { this.sending = false }
    },
    async doCancel(id) {
      try { await parentApi.cancelBind(id); uni.showToast({ title: '已取消', icon: 'success' }); this.loadAll() }
      catch (e) { uni.showToast({ title: e.message, icon: 'none' }) }
    }
  }
}
</script>
<style scoped>
.req-item { display: flex; align-items: center; justify-content: space-between; padding: 16rpx 0; border-bottom: 1rpx solid var(--border-light); }
.req-item:last-child { border-bottom: none; }
.child-avatar-sm { width: 64rpx; height: 64rpx; border-radius: 50%; background: linear-gradient(135deg,#E8E0FF,#D5CCFF); display: flex; align-items: center; justify-content: center; font-size: 28rpx; font-weight: bold; color: var(--primary-color); }
</style>
