<script>
import { notificationApi } from '@/utils/request.js'

let pollTimer = null

export default {
  onLaunch() {
    uni.reLaunch({ url: '/pages/launch/launch' })
  },
  onShow() {
    this.startPolling()
  },
  onHide() {
    this.stopPolling()
  },
  methods: {
    startPolling() {
      this.stopPolling()
      this.fetchUnreadCount()
      pollTimer = setInterval(() => { this.fetchUnreadCount() }, 30000)
    },
    stopPolling() {
      if (pollTimer) { clearInterval(pollTimer); pollTimer = null }
    },
    async fetchUnreadCount() {
      try {
        const res = await notificationApi.getUnreadCount()
        // 兼容: 拦截器已解包为 { count: n } 或直接返回数字
        const count = typeof res === 'number' ? res : (res?.count ?? res?.data?.count ?? 0)
        uni.setStorageSync('unreadNotificationCount', count)
        // 更新tabBar角标（"我的"是第5个tab，index=4）
        if (count > 0) {
          uni.setTabBarBadge({ index: 4, text: String(count > 99 ? '99+' : count) }).catch(() => {})
        } else {
          uni.removeTabBarBadge({ index: 4 }).catch(() => {})
        }
      } catch (e) { /* silent */ }
    }
  }
}
</script>
<style>
@import './styles/index.css';
</style>
