<template>
  <view class="page-settings-notification">
    <view class="setting-section">
      <view class="section-title">推送通知</view>
      <view class="setting-list">
        <view class="setting-item">
          <view class="setting-left">
            <text class="setting-icon">🔔</text>
            <text class="setting-label">日记提醒</text>
          </view>
          <switch :checked="settings.diaryReminder" @change="settings.diaryReminder = !settings.diaryReminder" color="#7B68EE" />
        </view>
        <view class="setting-item">
          <view class="setting-left">
            <text class="setting-icon">💬</text>
            <text class="setting-label">AI回复推送</text>
          </view>
          <switch :checked="settings.aiPush" @change="settings.aiPush = !settings.aiPush" color="#7B68EE" />
        </view>
        <view class="setting-item">
          <view class="setting-left">
            <text class="setting-icon">📊</text>
            <text class="setting-label">周报推送</text>
          </view>
          <switch :checked="settings.weeklyReport" @change="settings.weeklyReport = !settings.weeklyReport" color="#7B68EE" />
        </view>
        <view class="setting-item">
          <view class="setting-left">
            <text class="setting-icon">⚠️</text>
            <text class="setting-label">风险预警通知</text>
          </view>
          <switch :checked="settings.warningNotify" @change="settings.warningNotify = !settings.warningNotify" color="#7B68EE" />
        </view>
      </view>
    </view>

    <view class="setting-section">
      <view class="section-title">提醒时间</view>
      <view class="setting-item" @click="showTimePicker = true">
        <view class="setting-left">
          <text class="setting-icon">⏰</text>
          <text class="setting-label">每日提醒时间</text>
        </view>
        <view class="setting-right">
          <text>{{ reminderTime }}</text>
          <text class="arrow">></text>
        </view>
      </view>
    </view>

    <view class="save-bar">
      <button class="save-btn" @click="saveSettings">保存设置</button>
    </view>

    <!-- 时间选择器 -->
    <view v-if="showTimePicker" class="time-picker-mask" @click="showTimePicker = false">
      <view class="time-picker-panel" @click.stop>
        <view class="picker-header">
          <text class="picker-title">选择提醒时间</text>
        </view>
        <picker-view class="picker-view" :value="timeIndex" @change="onTimeChange">
          <picker-view-column>
            <view v-for="time in timeOptions" :key="time" class="picker-item">
              {{ time }}
            </view>
          </picker-view-column>
        </picker-view>
        <button class="confirm-btn" @click="confirmTime">确定</button>
      </view>
    </view>
  </view>
</template>

<script>
import { settingsApi } from '@/utils/request.js'

export default {
  data() {
    return {
      showTimePicker: false,
      timeIndex: [20],
      timeOptions: Array.from({ length: 24 }, (_, i) => `${i.toString().padStart(2, '0')}:00`),
      reminderTime: '20:00',
      settings: {
        diaryReminder: true,
        aiPush: true,
        weeklyReport: true,
        warningNotify: true
      }
    }
  },
  onLoad() {
    this.loadSettings()
  },
  methods: {
    onTimeChange(e) {
      this.timeIndex = e.detail.value
    },
    confirmTime() {
      this.reminderTime = this.timeOptions[this.timeIndex[0]]
      this.showTimePicker = false
    },
    async loadSettings() {
      try {
        const res = await settingsApi.get()
        if (res.code === 200 && res.data) {
          this.settings.diaryReminder = res.data.diaryReminder !== false
          this.settings.aiPush = res.data.aiPush !== false
          this.settings.weeklyReport = res.data.weeklyReport !== false
          this.settings.warningNotify = res.data.warningNotify !== false
          if (res.data.reminderTime) {
            this.reminderTime = res.data.reminderTime
            const idx = this.timeOptions.indexOf(res.data.reminderTime)
            if (idx !== -1) this.timeIndex = [idx]
          }
        }
      } catch (e) {
        console.log('加载设置失败', e)
      }
    },
    async saveSettings() {
      try {
        const res = await settingsApi.update({
          ...this.settings,
          reminderTime: this.reminderTime
        })
        if (res.code === 200) {
          uni.showToast({ title: '设置已保存', icon: 'success' })
        }
      } catch (e) {
        uni.showToast({ title: '保存失败', icon: 'none' })
      }
    }
  }
}
</script>

<style scoped>
.page-settings-notification {
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

.setting-left {
  display: flex;
  align-items: center;
}

.setting-icon {
  font-size: 36rpx;
  margin-right: 20rpx;
}

.setting-label {
  font-size: 28rpx;
  color: var(--text-primary);
}

.setting-right {
  display: flex;
  align-items: center;
  color: var(--text-secondary);
  font-size: 28rpx;
}

.arrow {
  margin-left: 8rpx;
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

.time-picker-mask {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.5);
  display: flex;
  align-items: flex-end;
  justify-content: center;
}

.time-picker-panel {
  width: 100%;
  background: var(--bg-primary);
  border-radius: 32rpx 32rpx 0 0;
  padding: 32rpx;
}

.picker-header {
  text-align: center;
  margin-bottom: 24rpx;
}

.picker-title {
  font-size: 30rpx;
  font-weight: 500;
  color: var(--text-primary);
}

.picker-view {
  height: 300rpx;
}

.picker-item {
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 32rpx;
  color: var(--text-primary);
}

.confirm-btn {
  width: 100%;
  height: 88rpx;
  background: var(--primary-color);
  border-radius: 44rpx;
  color: #FFF;
  font-size: 30rpx;
  margin-top: 24rpx;
}
</style>