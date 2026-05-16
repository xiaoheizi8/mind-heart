<template>
  <view class="page-settings-privacy">
    <view class="setting-section">
      <view class="section-title">隐私设置</view>
      <view class="setting-list">
        <view class="setting-item">
          <view class="setting-left">
            <text class="setting-icon">🔒</text>
            <view class="setting-content">
              <text class="setting-label">日记加密</text>
              <text class="setting-desc">开启后日记内容需要密码查看</text>
            </view>
          </view>
          <switch :checked="settings.diaryPassword" @change="settings.diaryPassword = !settings.diaryPassword" color="#7B68EE" />
        </view>
        
        <view class="setting-item">
          <view class="setting-left">
            <text class="setting-icon">👥</text>
            <view class="setting-content">
              <text class="setting-label">展示状态</text>
              <text class="setting-desc">允许他人查看我的情绪趋势</text>
            </view>
          </view>
          <switch :checked="settings.showMood" @change="settings.showMood = !settings.showMood" color="#7B68EE" />
        </view>
        
        <view class="setting-item">
          <view class="setting-left">
            <text class="setting-icon">📊</text>
            <view class="setting-content">
              <text class="setting-label">匿名数据分析</text>
              <text class="setting-desc">允许匿名提交数据用于研究</text>
            </view>
          </view>
          <switch :checked="settings.anonymousData" @change="settings.anonymousData = !settings.anonymousData" color="#7B68EE" />
        </view>
      </view>
    </view>

    <view class="setting-section">
      <view class="section-title">数据管理</view>
      <view class="setting-list">
        <view class="setting-item" @click="exportData">
          <view class="setting-left">
            <text class="setting-icon">📥</text>
            <text class="setting-label">导出我的数据</text>
          </view>
          <text class="arrow">></text>
        </view>
        
        <view class="setting-item" @click="clearCache">
          <view class="setting-left">
            <text class="setting-icon">🗑️</text>
            <text class="setting-label">清除缓存</text>
          </view>
          <text class="arrow">></text>
        </view>
        
        <view class="setting-item danger" @click="deleteAccount">
          <view class="setting-left">
            <text class="setting-icon">⚠️</text>
            <text class="setting-label">注销账户</text>
          </view>
          <text class="arrow">></text>
        </view>
      </view>
    </view>

    <view class="save-bar">
      <button class="save-btn" @click="saveSettings">保存设置</button>
    </view>
  </view>
</template>

<script>
export default {
  data() {
    return {
      settings: {
        diaryPassword: false,
        showMood: true,
        anonymousData: false
      }
    }
  },
  onShow() {
    const saved = uni.getStorageSync('privacySettings')
    if (saved) {
      this.settings = saved
    }
  },
  methods: {
    saveSettings() {
      uni.setStorageSync('privacySettings', this.settings)
      uni.showToast({ title: '设置已保存', icon: 'success' })
    },
    exportData() {
      uni.showToast({ title: '数据导出中...', icon: 'none' })
    },
    clearCache() {
      uni.showModal({
        title: '提示',
        content: '确定要清除缓存吗？',
        success: (res) => {
          if (res.confirm) {
            uni.clearStorageSync()
            uni.showToast({ title: '清除成功', icon: 'success' })
          }
        }
      })
    },
    deleteAccount() {
      uni.showModal({
        title: '注销账户',
        content: '注销后将清除所有数据，且无法恢复。确定要注销吗？',
        success: (res) => {
          if (res.confirm) {
            uni.showToast({ title: '功能开发中', icon: 'none' })
          }
        }
      })
    }
  }
}
</script>

<style scoped>
.page-settings-privacy {
  min-height: 100vh;
  background: var(--bg-secondary);
  padding-bottom: 160rpx;
}

.setting-section {
  background: var(--bg-primary);
  margin-bottom: 24rpx;
}

.section-title {
  font-size: 26rpx;
  color: var(--text-tertiary);
  padding: 24rpx 32rpx 16rpx;
}

.setting-list {
  padding: 0 32rpx;
}

.setting-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 24rpx 0;
  border-bottom: 1rpx solid var(--border-light);
}

.setting-item:last-child {
  border-bottom: none;
}

.setting-item.danger .setting-label {
  color: var(--error-color);
}

.setting-left {
  display: flex;
  align-items: center;
}

.setting-icon {
  font-size: 36rpx;
  margin-right: 20rpx;
}

.setting-content {
  display: flex;
  flex-direction: column;
}

.setting-label {
  font-size: 28rpx;
  color: var(--text-primary);
}

.setting-desc {
  font-size: 24rpx;
  color: var(--text-tertiary);
  margin-top: 4rpx;
}

.arrow {
  font-size: 28rpx;
  color: var(--text-tertiary);
}

.save-bar {
  position: fixed;
  bottom: 0;
  left: 0;
  right: 0;
  padding: 24rpx;
  background: var(--bg-primary);
  box-shadow: 0 -4rpx 16rpx rgba(0, 0, 0, 0.05);
}

.save-btn {
  width: 100%;
  height: 96rpx;
  background: linear-gradient(135deg, var(--primary-color), var(--primary-dark));
  border-radius: 48rpx;
  color: #FFF;
  font-size: 32rpx;
  display: flex;
  align-items: center;
  justify-content: center;
}
</style>