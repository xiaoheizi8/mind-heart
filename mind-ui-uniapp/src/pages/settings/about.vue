<template>
  <view class="page-settings-about">
    <!-- App信息 -->
    <view class="app-info">
      <view class="app-logo">
        <text class="logo-icon">💜</text>
      </view>
      <text class="app-name">心域</text>
      <text class="app-version">版本 1.0.0</text>
    </view>

    <!-- 功能介绍 -->
    <view class="intro-section">
      <view class="section-title">关于我们</view>
      <text class="intro-text">
        心域——青少年心理数字孪生系统。基于多模态情感计算技术，为青少年提供情绪日记、AI陪伴、心理知识等服务。我们致力于守护青少年心理健康，让每一次情绪都被温柔以待。
      </text>
    </view>

    <!-- 功能列表 -->
    <view class="feature-section">
      <view class="section-title">功能介绍</view>
      <view class="feature-list">
        <view class="feature-item">
          <text class="feature-icon">📝</text>
          <view class="feature-content">
            <text class="feature-title">情绪日记</text>
            <text class="feature-desc">记录每一天的心情，AI智能分析情绪状态</text>
          </view>
        </view>
        <view class="feature-item">
          <text class="feature-icon">💬</text>
          <view class="feature-content">
            <text class="feature-title">AI陪伴</text>
            <text class="feature-desc">随时随地倾诉，获得专业的心理支持</text>
          </view>
        </view>
        <view class="feature-item">
          <text class="feature-icon">📊</text>
          <view class="feature-content">
            <text class="feature-title">情绪报告</text>
            <text class="feature-desc">可视化了解自己的情绪变化趋势</text>
          </view>
        </view>
        <view class="feature-item">
          <text class="feature-icon">📚</text>
          <view class="feature-content">
            <text class="feature-title">心理知识</text>
            <text class="feature-desc">专业的心理学知识，科学的自我调节方法</text>
          </view>
        </view>
      </view>
    </view>

    <!-- 链接列表 -->
    <view class="link-section">
      <view class="link-item" @click="openLink('privacy')">
        <text class="link-label">隐私政策</text>
        <text class="link-arrow">></text>
      </view>
      <view class="link-item" @click="openLink('terms')">
        <text class="link-label">用户协议</text>
        <text class="link-arrow">></text>
      </view>
      <view class="link-item" @click="openLink('help')">
        <text class="link-label">帮助中心</text>
        <text class="link-arrow">></text>
      </view>
    </view>

    <!-- 联系方式 -->
    <view class="contact-section">
      <text class="contact-title">联系我们</text>
      <text class="contact-item">邮箱: spring_dev_ljw@163.com</text>
<!--      <text class="contact-item">热线: 400-161-9995</text>-->
    </view>

    <!-- 底部 -->
    <view class="footer">
      <text class="footer-text">© 2026 心域 MindRealm</text>
    </view>
  </view>
</template>

<script>
export default {
  data() {
    return {}
  },
  methods: {
    openLink(type) {
      uni.showModal({
        title: type === 'privacy' ? '隐私政策' : type === 'terms' ? '用户协议' : '帮助中心',
        content: '内容开发中...'
      })
    }
  }
}
</script>

<style scoped>
.page-settings-about {
  min-height: 100vh;
  background: var(--bg-secondary);
}

.app-info {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 64rpx 0;
  background: linear-gradient(135deg, #7B68EE, #9D8FFF);
}

.app-logo {
  width: 120rpx;
  height: 120rpx;
  background: rgba(255, 255, 255, 0.2);
  border-radius: 30rpx;
  display: flex;
  align-items: center;
  justify-content: center;
}

.logo-icon {
  font-size: 60rpx;
}

.app-name {
  font-size: 40rpx;
  font-weight: 600;
  color: #FFF;
  margin-top: 24rpx;
}

.app-version {
  font-size: 26rpx;
  color: rgba(255, 255, 255, 0.8);
  margin-top: 8rpx;
}

.intro-section,
.feature-section,
.link-section,
.contact-section {
  background: var(--bg-primary);
  margin-bottom: 24rpx;
  padding: 32rpx;
}

.section-title {
  font-size: 30rpx;
  font-weight: 600;
  color: var(--text-primary);
  margin-bottom: 20rpx;
}

.intro-text {
  font-size: 28rpx;
  line-height: 1.8;
  color: var(--text-secondary);
}

.feature-list {
  display: flex;
  flex-direction: column;
  gap: 24rpx;
}

.feature-item {
  display: flex;
  align-items: flex-start;
}

.feature-icon {
  font-size: 40rpx;
  margin-right: 20rpx;
}

.feature-content {
  flex: 1;
}

.feature-title {
  font-size: 28rpx;
  font-weight: 500;
  color: var(--text-primary);
  display: block;
}

.feature-desc {
  font-size: 24rpx;
  color: var(--text-tertiary);
  margin-top: 4rpx;
  display: block;
}

.link-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 24rpx 0;
  border-bottom: 1rpx solid var(--border-light);
}

.link-item:last-child {
  border-bottom: none;
}

.link-label {
  font-size: 28rpx;
  color: var(--text-primary);
}

.link-arrow {
  font-size: 28rpx;
  color: var(--text-tertiary);
}

.contact-section {
  text-align: center;
}

.contact-title {
  font-size: 28rpx;
  font-weight: 500;
  color: var(--text-primary);
  display: block;
  margin-bottom: 20rpx;
}

.contact-item {
  font-size: 26rpx;
  color: var(--text-secondary);
  display: block;
  margin-top: 12rpx;
}

.footer {
  padding: 48rpx 0;
  text-align: center;
}

.footer-text {
  font-size: 24rpx;
  color: var(--text-tertiary);
}
</style>