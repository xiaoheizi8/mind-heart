<template>
  <view class="page-launch">
    <view class="logo-area">
      <view class="logo">
        <text class="logo-icon">💜</text>
      </view>
      <text class="app-name">心域</text>
      <text class="app-desc">守护每一颗年轻的心</text>
    </view>
    
    <view class="loading-dots">
      <view class="dot"></view>
      <view class="dot"></view>
      <view class="dot"></view>
    </view>
  </view>
</template>

<script>
export default {
  onLoad() {
    this.checkGuideStatus()
  },
  methods: {
    checkGuideStatus() {
      const hasSeenGuide = uni.getStorageSync('hasSeenGuide')
      
      setTimeout(() => {
        if (hasSeenGuide) {
          this.checkLoginStatus()
        } else {
          uni.redirectTo({ url: '/pages/guide/guide' })
        }
      }, 1500)
    },
    checkLoginStatus() {
      const token = uni.getStorageSync('token')
      
      if (token) {
        uni.switchTab({ url: '/pages/index/index' })
      } else {
        uni.redirectTo({ url: '/pages/login/login' })
      }
    }
  }
}
</script>

<style scoped>
.page-launch {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  height: 100vh;
  background: linear-gradient(180deg, #7B68EE 0%, #9D8FFF 100%);
}

.logo-area {
  display: flex;
  flex-direction: column;
  align-items: center;
}

.logo {
  width: 180rpx;
  height: 180rpx;
  background: rgba(255, 255, 255, 0.2);
  border-radius: 45rpx;
  display: flex;
  align-items: center;
  justify-content: center;
}

.logo-icon {
  font-size: 90rpx;
}

.app-name {
  font-size: 56rpx;
  font-weight: 700;
  color: #FFF;
  margin-top: 32rpx;
}

.app-desc {
  font-size: 28rpx;
  color: rgba(255, 255, 255, 0.8);
  margin-top: 12rpx;
}

.loading-dots {
  position: absolute;
  bottom: 200rpx;
  display: flex;
  gap: 16rpx;
}

.dot {
  width: 16rpx;
  height: 16rpx;
  background: rgba(255, 255, 255, 0.5);
  border-radius: 50%;
  animation: pulse 1.4s ease-in-out infinite;
}

.dot:nth-child(1) { animation-delay: 0s; }
.dot:nth-child(2) { animation-delay: 0.2s; }
.dot:nth-child(3) { animation-delay: 0.4s; }

@keyframes pulse {
  0%, 80%, 100% { transform: scale(0.8); opacity: 0.5; }
  40% { transform: scale(1); opacity: 1; }
}
</style>