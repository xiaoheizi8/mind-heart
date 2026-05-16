<template>
  <view class="page-profile">
    <!-- 用户信息头部 -->
    <view class="profile-header">
      <view class="avatar-area">
        <image 
          class="avatar" 
          :src="avatarUrl" 
          mode="aspectFill"
          :show-loading="true"
        />
        <view class="edit-avatar" @click="changeAvatar">
          <text class="edit-icon">📷</text>
        </view>
      </view>
      <view class="user-info">
        <text class="nickname">{{ userInfo.nickname || '未设置昵称' }}</text>
        <text class="user-desc">{{ userInfo.bio || '点击编辑个人资料' }}</text>
      </view>
      <view class="edit-btn" @click="editProfile">
        <text class="edit-text">编辑</text>
      </view>
    </view>

    <!-- 统计数据 -->
    <view class="stats-row">
      <view class="stat-item" @click="goToPage('/pages/diary/list')">
        <text class="stat-num">{{ stats.diaryCount }}</text>
        <text class="stat-label">日记</text>
      </view>
      <view class="stat-item" @click="goToPage('/pages/chat/chat')">
        <text class="stat-num">{{ stats.chatDays }}</text>
        <text class="stat-label">陪伴天数</text>
      </view>
      <view class="stat-item" @click="goToPage('/pages/emotion/report')">
        <text class="stat-num">{{ stats.streak }}</text>
        <text class="stat-label">连续打卡</text>
      </view>
    </view>

    <!-- 功能菜单 -->
    <view class="menu-section">
      <view class="menu-title">我的记录</view>
      <view class="menu-list">
        <view class="menu-item" @click="goToPage('/pages/diary/list')">
          <text class="menu-icon">📝</text>
          <text class="menu-label">我的日记</text>
          <text class="menu-arrow">></text>
        </view>
        <view class="menu-item" @click="goToPage('/pages/emotion/report')">
          <text class="menu-icon">📊</text>
          <text class="menu-label">情绪报告</text>
          <text class="menu-arrow">></text>
        </view>
        <view class="menu-item" @click="goToPage('/pages/knowledge/favorites')">
          <text class="menu-icon">📚</text>
          <text class="menu-label">知识收藏</text>
          <text class="menu-arrow">></text>
        </view>
      </view>
    </view>

    <view class="menu-section">
      <view class="menu-title">消息</view>
      <view class="menu-list">
        <view class="menu-item" @click="goToPage('/pages/notification/list')">
          <text class="menu-icon">🔔</text>
          <text class="menu-label">通知中心</text>
          <text class="menu-arrow">></text>
        </view>
      </view>
    </view>

    <view class="menu-section">
      <view class="menu-title">设置</view>
      <view class="menu-list">
        <view class="menu-item" @click="goToPage('/pages/settings/notification')">
          <text class="menu-icon">⚙️</text>
          <text class="menu-label">通知设置</text>
          <text class="menu-arrow">></text>
        </view>
        <view class="menu-item" @click="goToPage('/pages/settings/privacy')">
          <text class="menu-icon">🔒</text>
          <text class="menu-label">隐私设置</text>
          <text class="menu-arrow">></text>
        </view>
        <view class="menu-item" @click="goToPage('/pages/settings/about')">
          <text class="menu-icon">ℹ️</text>
          <text class="menu-label">关于心域</text>
          <text class="menu-arrow">></text>
        </view>
      </view>
    </view>

    <!-- 紧急求助 -->
    <view class="emergency-section">
      <view class="emergency-card" @click="callHelpline">
        <text class="emergency-icon">🆘</text>
        <view class="emergency-info">
          <text class="emergency-title">紧急求助</text>
          <text class="emergency-desc">24小时心理援助热线</text>
        </view>
        <text class="emergency-phone">400-161-9995</text>
      </view>
    </view>

    <!-- 退出登录 -->
    <view class="logout-section">
      <button class="logout-btn" @click="handleLogout">退出登录</button>
    </view>
  </view>
</template>

<script>
import { authApi, userApi } from '../../utils/request.js'

export default {
  data() {
    return {
      userInfo: {},
      stats: {
        diaryCount: 0,
        chatDays: 0,
        streak: 0
      }
    }
  },
  computed: {
    avatarUrl() {
      const avatar = this.userInfo.avatar
      // 如果是blob临时文件或空，使用默认头像
      if (!avatar || avatar.startsWith('blob:')) {
        return 'https://mind-heart.oss-cn-beijing.aliyuncs.com/default/avatar.png'
      }
      // 如果是相对路径，添加OSS域名
      if (avatar && !avatar.startsWith('http')) {
        return 'https://mind-heart.oss-cn-beijing.aliyuncs.com/' + avatar
      }
      return avatar
    }
  },
  onShow() {
    this.loadUserInfo()
  },
  methods: {
    async loadUserInfo() {
      try {
        // 不使用缓存，确保获取最新数据
        const res = await userApi.getProfile({}, { cache: false })
        this.userInfo = res || {}
      } catch (e) {
        // 使用缓存数据
        const cached = uni.getStorageSync('userInfo')
        this.userInfo = cached || {}
      }
      
      // 模拟统计数据
      this.stats = {
        diaryCount: 12,
        chatDays: 8,
        streak: 5
      }
    },
    changeAvatar() {
      uni.chooseImage({
        count: 1,
        sizeType: ['compressed'],
        sourceType: ['album', 'camera'],
        success: (res) => {
          // TODO: 上传头像
          uni.showToast({ title: '头像已更新', icon: 'success' })
        }
      })
    },
    editProfile() {
      uni.navigateTo({ url: '/pages/profile/edit' })
    },
    // goToPage(url) {
    //   uni.navigateTo({ url })
    // },
    goToPage(url) {
  // 1. 定义你的 TabBar 页面路径列表
  // 请务必根据你 pages.json 里 tabBar -> list 中配置的 pagePath 来填写
  const tabBarPages = [
    '/pages/diary/list', 
    '/pages/chat/chat', 
    '/pages/emotion/report',
    '/pages/notification/list'
    // 如果还有其他 TabBar 页面，请继续添加
  ];

  // 2. 判断并跳转
  if (tabBarPages.includes(url)) {
    // 如果是 TabBar 页面，使用 switchTab
    uni.switchTab({ 
      url: url 
    });
  } else {
    // 如果是普通页面，使用 navigateTo
    uni.navigateTo({ 
      url: url 
    });
  }
},
    callHelpline() {
      uni.makePhoneCall({
        phoneNumber: '400-161-9995',
        fail: () => {
          uni.showToast({ title: '拨打失败', icon: 'none' })
        }
      })
    },
    handleLogout() {
      uni.showModal({
        title: '提示',
        content: '确定要退出登录吗？',
        success: async (res) => {
          if (res.confirm) {
            try {
              await authApi.logout()
            } catch (e) {}
            
            uni.clearStorageSync()
            uni.reLaunch({ url: '/pages/login/login' })
          }
        }
      })
    }
  }
}
</script>

<style scoped>
.page-profile {
  min-height: 100vh;
  background: var(--bg-secondary);
  padding-bottom: 120rpx;
}

.profile-header {
  display: flex;
  align-items: center;
  padding: 48rpx 32rpx;
  background: linear-gradient(135deg, #7B68EE, #9D8FFF);
}

.avatar-area {
  position: relative;
}

.avatar {
  width: 120rpx;
  height: 120rpx;
  border-radius: 50%;
  border: 4rpx solid rgba(255, 255, 255, 0.5);
}

.edit-avatar {
  position: absolute;
  right: 0;
  bottom: 0;
  width: 48rpx;
  height: 48rpx;
  background: var(--bg-primary);
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
}

.edit-icon {
  font-size: 24rpx;
}

.user-info {
  flex: 1;
  margin-left: 24rpx;
}

.nickname {
  font-size: 36rpx;
  font-weight: 600;
  color: #FFF;
  display: block;
}

.user-desc {
  font-size: 26rpx;
  color: rgba(255, 255, 255, 0.8);
  margin-top: 8rpx;
  display: block;
}

.edit-btn {
  padding: 16rpx 32rpx;
  background: rgba(255, 255, 255, 0.2);
  border-radius: 32rpx;
}

.edit-text {
  font-size: 26rpx;
  color: #FFF;
}

.stats-row {
  display: flex;
  justify-content: space-around;
  padding: 32rpx;
  background: var(--bg-primary);
  margin-bottom: 24rpx;
}

.stat-item {
  display: flex;
  flex-direction: column;
  align-items: center;
}

.stat-num {
  font-size: 40rpx;
  font-weight: 600;
  color: var(--primary-color);
}

.stat-label {
  font-size: 24rpx;
  color: var(--text-tertiary);
  margin-top: 8rpx;
}

.menu-section {
  margin-bottom: 24rpx;
}

.menu-title {
  font-size: 26rpx;
  color: var(--text-tertiary);
  padding: 16rpx 32rpx;
}

.menu-list {
  background: var(--bg-primary);
}

.menu-item {
  display: flex;
  align-items: center;
  padding: 32rpx;
  border-bottom: 1rpx solid var(--border-light);
}

.menu-item:last-child {
  border-bottom: none;
}

.menu-icon {
  font-size: 40rpx;
  margin-right: 24rpx;
}

.menu-label {
  flex: 1;
  font-size: 28rpx;
  color: var(--text-primary);
}

.menu-arrow {
  font-size: 28rpx;
  color: var(--text-tertiary);
}

.emergency-section {
  padding: 0 24rpx;
  margin-bottom: 24rpx;
}

.emergency-card {
  display: flex;
  align-items: center;
  padding: 32rpx;
  background: linear-gradient(135deg, #FF6B6B, #FF8E53);
  border-radius: 24rpx;
}

.emergency-icon {
  font-size: 48rpx;
}

.emergency-info {
  flex: 1;
  margin-left: 24rpx;
}

.emergency-title {
  font-size: 28rpx;
  font-weight: 500;
  color: #FFF;
  display: block;
}

.emergency-desc {
  font-size: 24rpx;
  color: rgba(255, 255, 255, 0.8);
  margin-top: 4rpx;
  display: block;
}

.emergency-phone {
  font-size: 28rpx;
  font-weight: 600;
  color: #FFF;
}

.logout-section {
  padding: 24rpx 32rpx;
}

.logout-btn {
  width: 100%;
  height: 96rpx;
  background: var(--bg-primary);
  border-radius: 48rpx;
  color: var(--error-color);
  font-size: 32rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  border: none;
}
</style>