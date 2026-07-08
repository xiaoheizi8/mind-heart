<template>
  <view class="page-notifications">
    <!-- 全部已读按钮 -->
    <view class="header-action">
      <text class="read-all" @click="markAllRead">全部已读</text>
    </view>

    <!-- 通知列表 -->
    <scroll-view class="notify-list" scroll-y>
      <view v-if="list.length === 0" class="empty-state">
        <text class="empty-icon">🔔</text>
        <text class="empty-text">暂无通知</text>
      </view>
      
      <view 
        v-for="item in list" 
        :key="item.id"
        class="notify-item"
        :class="{ unread: !item.isRead }"
        @click="handleClick(item)"
      >
        <view class="notify-icon">
          <text>{{ item.icon }}</text>
        </view>
        <view class="notify-content">
          <view class="notify-header">
            <text class="notify-title">{{ item.title }}</text>
            <text class="notify-time">{{ item.createdAt }}</text>
          </view>
          <text class="notify-desc">{{ item.content }}</text>
        </view>
        <view v-if="!item.isRead" class="unread-dot"></view>
      </view>
    </scroll-view>
  </view>
</template>

<script>
import { notificationApi } from '@/utils/request.js'

export default {
  data() {
    return {
      list: [],
      page: 1,
      pageSize: 20,
      loading: false,
      noMore: false
    }
  },
  onLoad() {
    this.loadNotifications()
  },
  onShow() {
    this.loadNotifications()
  },
  methods: {
    async loadNotifications() {
      if (this.loading) return
      this.loading = true

      try {
        const res = await notificationApi.getList({ page: this.page, size: this.pageSize })
        // 拦截器已解包为 PageResult: { records, total, current, size, pages }
        const records = res?.records || res?.data?.records || []
        const iconMap = {
          'reminder': '⏰',
          'system': '💜',
          'warning': '💡',
          'emotion': '😊'
        }
        this.list = records.map(item => ({
          ...item,
          icon: iconMap[item.type] || '🔔'
        }))
      } catch (e) {
        console.log('获取通知失败', e)
      } finally {
        this.loading = false
      }
    },
    handleClick(item) {
      if (!item.isRead) {
        item.isRead = true
        notificationApi.markRead(item.id).catch(() => {})
        // 立即更新未读计数和角标
        const cur = parseInt(uni.getStorageSync('unreadNotificationCount') || 0)
        const newCount = Math.max(0, cur - 1)
        uni.setStorageSync('unreadNotificationCount', newCount)
        if (newCount > 0) {
          uni.setTabBarBadge({ index: 4, text: String(newCount > 99 ? '99+' : newCount) }).catch(() => {})
        } else {
          uni.removeTabBarBadge({ index: 4 }).catch(() => {})
        }
      }

      // 系统通知/绑定请求 → 跳转绑定审批页
      const title = (item.title || '')
      if (item.type === 'system' && (title.includes('绑定') || title.includes('家长'))) {
        uni.navigateTo({ url: '/pages/bind/approve' })
      } else if (item.type === 'reminder') {
        uni.navigateTo({ url: '/pages/diary/create' })
      }
    },
    markAllRead() {
      notificationApi.markAllRead().then(() => {
        this.list.forEach(item => { item.isRead = true })
        // 清除未读计数和角标
        uni.setStorageSync('unreadNotificationCount', 0)
        uni.removeTabBarBadge({ index: 4 }).catch(() => {})
        uni.showToast({ title: '已全部标记为已读', icon: 'success' })
      }).catch(() => {
        uni.showToast({ title: '操作失败', icon: 'none' })
      })
    }
  }
}
</script>

<style scoped>
.page-notifications {
  min-height: 100vh;
  background: var(--bg-secondary);
}

.header-action {
  display: flex;
  justify-content: flex-end;
  padding: 24rpx;
  background: var(--bg-primary);
}

.read-all {
  font-size: 28rpx;
  color: var(--primary-color);
}

.notify-list {
  height: calc(100vh - 80rpx);
  padding: 16rpx;
}

.empty-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 120rpx;
}

.empty-icon {
  font-size: 80rpx;
}

.empty-text {
  font-size: 28rpx;
  color: var(--text-tertiary);
  margin-top: 24rpx;
}

.notify-item {
  display: flex;
  align-items: flex-start;
  background: var(--bg-primary);
  border-radius: 24rpx;
  padding: 32rpx;
  margin-bottom: 16rpx;
  position: relative;
}

.notify-item.unread {
  background: linear-gradient(135deg, #F8F6FF 0%, #FFF 100%);
}

.notify-icon {
  width: 80rpx;
  height: 80rpx;
  background: var(--bg-secondary);
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 40rpx;
  margin-right: 24rpx;
  flex-shrink: 0;
}

.notify-content {
  flex: 1;
}

.notify-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 12rpx;
}

.notify-title {
  font-size: 30rpx;
  font-weight: 600;
  color: var(--text-primary);
}

.notify-time {
  font-size: 24rpx;
  color: var(--text-tertiary);
}

.notify-desc {
  font-size: 26rpx;
  color: var(--text-secondary);
  line-height: 1.6;
}

.unread-dot {
  position: absolute;
  top: 40rpx;
  right: 40rpx;
  width: 16rpx;
  height: 16rpx;
  background: var(--primary-color);
  border-radius: 50%;
}
</style>