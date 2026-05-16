<template>
  <view class="page-home">
    <!-- 顶部欢迎区域 -->
    <view class="home-header">
      <view class="welcome">
        <text class="greeting">{{ greeting }}</text>
        <text class="username">{{ userInfo.nickname || '朋友' }}</text>
      </view>
      <view class="date-info">
        <text class="weekday">{{ weekday }}</text>
        <text class="date">{{ dateStr }}</text>
      </view>
    </view>

    <!-- 今日心情卡片 -->
    <view class="emotion-card card" @click="goToDiary">
      <view class="card-header">
        <text class="card-title">今日心情</text>
        <text class="action-text" @click.stop="goToCreateDiary">写日记 ></text>
      </view>
      <view class="emotion-display">
        <view class="emotion-icon" :class="todayEmotion.class">
          <text class="icon">{{ todayEmotion.icon }}</text>
        </view>
        <view class="emotion-info">
          <text class="emotion-label">{{ todayEmotion.label }}</text>
          <text class="emotion-desc">{{ todayEmotion.desc }}</text>
        </view>
      </view>
    </view>

    <!-- 快捷入口 -->
    <view class="quick-entry">
      <view class="entry-item" @click="goToChat">
        <view class="entry-icon chat-icon">
          <text class="icon-text">AI</text>
        </view>
        <text class="entry-label">AI陪伴</text>
      </view>
      <view class="entry-item" @click="goToReport">
        <view class="entry-icon report-icon">
          <text class="icon-text">📊</text>
        </view>
        <text class="entry-label">情绪报告</text>
      </view>
      <view class="entry-item" @click="goToKnowledge">
        <view class="entry-icon know-icon">
          <text class="icon-text">📚</text>
        </view>
        <text class="entry-label">心理知识</text>
      </view>
    </view>

    <!-- 最近日记 -->
    <view class="recent-diary">
      <view class="section-header">
        <text class="section-title">最近日记</text>
        <text class="more-btn" @click="goToDiaryList">更多 ></text>
      </view>
      <view class="diary-list">
        <view v-if="diaryList.length === 0" class="empty-state">
          <text class="empty-icon">📝</text>
          <text class="empty-text">还没有日记，开始记录吧</text>
        </view>
        <view v-else v-for="(item, index) in diaryList" :key="index" 
              class="diary-item" @click="goToDiaryDetail(item.id)">
          <view class="diary-content">
            <text class="diary-text">{{ item.content }}</text>
            <view class="diary-meta">
              <text class="diary-time">{{ item.createdAt }}</text>
              <text class="diary-emotion" :class="item.emotionCategory">
                {{ item.emotionCategory }}
              </text>
            </view>
          </view>
        </view>
      </view>
    </view>

    <!-- 每日语录 -->
    <view class="daily-quote card">
      <text class="quote-icon">💬</text>
      <text class="quote-text">{{ dailyQuote }}</text>
      <text class="quote-author">—— 今日寄语</text>
    </view>
  </view>
</template>

<script>
import { userApi, diaryApi } from '@/utils/request.js'

export default {
  data() {
    return {
      userInfo: {
        nickname: ''
      },
      weekday: '',
      dateStr: '',
      greeting: '你好，',
      todayEmotion: {
        icon: '😊',
        label: '等待记录',
        desc: '开始记录今日心情吧',
        class: 'neutral'
      },
      diaryList: [],
      dailyQuote: '每一次的情绪都是内心的信号，学会倾听，与自己和解。'
    }
  },
  onLoad() {
    this.initDate()
    this.loadUserInfo()
    this.loadRecentDiary()
  },
  onShow() {
    this.loadUserInfo()
    this.loadRecentDiary()
  },
  methods: {
    initDate() {
      const now = new Date()
      const weekdays = ['周日', '周一', '周二', '周三', '周四', '周五', '周六']
      this.weekday = weekdays[now.getDay()]
      this.dateStr = `${now.getMonth() + 1}月${now.getDate()}日`
      
      const hour = now.getHours()
      if (hour < 12) {
        this.greeting = '早上好，'
      } else if (hour < 18) {
        this.greeting = '下午好，'
      } else {
        this.greeting = '晚上好，'
      }
    },
    async loadUserInfo() {
      try {
        const userInfo = uni.getStorageSync('userInfo')
        if (userInfo && userInfo.nickname) {
          this.userInfo = userInfo
        } else {
          const res = await userApi.getProfile()
          // request.js 已自动提取 data 层
          if (res && res.nickname) {
            this.userInfo = res
            uni.setStorageSync('userInfo', res)
          }
        }
      } catch (e) {
        console.log('获取用户信息失败', e)
      }
    },
    async loadRecentDiary() {
      try {
        const res = await diaryApi.getList({ page: 1, size: 3 })
        // request.js 已自动提取 data 层
        const records = res.records || res || []
        this.diaryList = Array.isArray(records) ? records : []
        
        if (this.diaryList.length > 0) {
          const today = new Date().toDateString()
          const todayDiary = this.diaryList.find(d => 
            new Date(d.createdAt).toDateString() === today
          )
          if (todayDiary) {
            this.updateTodayEmotion(todayDiary.emotionCategory, todayDiary.emotionScore)
          } else {
            this.todayEmotion = {
              icon: '😊',
              label: '等待记录',
              desc: '开始记录今日心情吧',
              class: 'neutral'
            }
          }
        }
      } catch (e) {
        console.log('获取日记失败', e)
      }
    },
    updateTodayEmotion(category, score) {
      const emotionMap = {
        'happy': { icon: '😊', label: '开心', desc: '保持好心情~', class: 'happy' },
        'calm': { icon: '😌', label: '平静', desc: '内心很宁静~', class: 'calm' },
        'sad': { icon: '😢', label: '难过', desc: '要坚强哦~', class: 'sad' },
        'anxious': { icon: '😰', label: '焦虑', desc: '深呼吸~', class: 'anxious' },
        'angry': { icon: '😠', label: '生气', desc: '冷静一下~', class: 'angry' },
        'neutral': { icon: '😐', label: '一般', desc: '还好啦~', class: 'neutral' }
      }
      
      const emotion = emotionMap[category] || emotionMap['neutral']
      if (category === 'happy' || category === 'calm') {
        emotion.desc = '继续保持哦'
      } else if (score < -0.3) {
        emotion.desc = '记得照顾好自己'
      }
      this.todayEmotion = emotion
    },
    goToDiary() {
      uni.switchTab({ url: '/pages/diary/list' })
    },
    goToCreateDiary() {
      uni.navigateTo({ url: '/pages/diary/create' })
    },
    goToChat() {
      uni.switchTab({ url: '/pages/chat/chat' })
    },
    goToReport() {
      uni.navigateTo({ url: '/pages/emotion/report' })
    },
    goToKnowledge() {
      uni.navigateTo({ url: '/pages/knowledge/list' })
    },
    goToDiaryList() {
      uni.switchTab({ url: '/pages/diary/list' })
    },
    goToDiaryDetail(id) {
      uni.navigateTo({ url: `/pages/diary/detail?id=${id}` })
    }
  }
}
</script>

<style scoped>
.page-home {
  min-height: 100vh;
  background: linear-gradient(180deg, #F8F6FF 0%, #F8F9FA 100%);
  padding: 24rpx;
  padding-bottom: 120rpx;
}

.home-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 32rpx 0;
}

.welcome {
  display: flex;
  flex-direction: column;
}

.greeting {
  font-size: 28rpx;
  color: var(--text-secondary);
}

.username {
  font-size: 44rpx;
  font-weight: 600;
  color: var(--text-primary);
  margin-top: 8rpx;
}

.date-info {
  display: flex;
  flex-direction: column;
  align-items: flex-end;
}

.weekday {
  font-size: 28rpx;
  color: var(--primary-color);
  font-weight: 500;
}

.date {
  font-size: 24rpx;
  color: var(--text-tertiary);
  margin-top: 4rpx;
}

.emotion-card {
  background: linear-gradient(135deg, #FFF5F5 0%, #F0F0FF 100%);
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.card-title {
  font-size: 32rpx;
  font-weight: 600;
  color: var(--text-primary);
}

.action-text {
  font-size: 26rpx;
  color: var(--primary-color);
}

.emotion-display {
  display: flex;
  align-items: center;
  margin-top: 16rpx;
}

.emotion-icon {
  width: 100rpx;
  height: 100rpx;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  background: rgba(123, 104, 238, 0.1);
}

.emotion-icon .icon {
  font-size: 48rpx;
}

.emotion-info {
  margin-left: 24rpx;
  display: flex;
  flex-direction: column;
}

.emotion-label {
  font-size: 32rpx;
  font-weight: 500;
  color: var(--text-primary);
}

.emotion-desc {
  font-size: 24rpx;
  color: var(--text-tertiary);
  margin-top: 8rpx;
}

.quick-entry {
  display: flex;
  justify-content: space-around;
  padding: 32rpx 0;
}

.entry-item {
  display: flex;
  flex-direction: column;
  align-items: center;
}

.entry-icon {
  width: 100rpx;
  height: 100rpx;
  border-radius: 24rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-bottom: 12rpx;
  box-shadow: var(--shadow-light);
}

.chat-icon {
  background: linear-gradient(135deg, #7B68EE, #9D8FFF);
}

.report-icon {
  background: linear-gradient(135deg, #FFB347, #FFCC33);
}

.know-icon {
  background: linear-gradient(135deg, #77DD77, #98FB98);
}

.icon-text {
  font-size: 32rpx;
  font-weight: 600;
  color: #FFF;
}

.entry-label {
  font-size: 26rpx;
  color: var(--text-secondary);
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24rpx;
}

.section-title {
  font-size: 32rpx;
  font-weight: 600;
  color: var(--text-primary);
}

.more-btn {
  font-size: 26rpx;
  color: var(--text-tertiary);
}

.diary-list {
  background: var(--bg-primary);
  border-radius: 24rpx;
  overflow: hidden;
}

.empty-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 48rpx;
}

.empty-icon {
  font-size: 64rpx;
}

.empty-text {
  font-size: 26rpx;
  color: var(--text-tertiary);
  margin-top: 16rpx;
}

.diary-item {
  padding: 24rpx;
  border-bottom: 1rpx solid var(--border-light);
}

.diary-item:last-child {
  border-bottom: none;
}

.diary-text {
  font-size: 28rpx;
  color: var(--text-primary);
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.diary-meta {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-top: 16rpx;
}

.diary-time {
  font-size: 24rpx;
  color: var(--text-tertiary);
}

.diary-emotion {
  font-size: 24rpx;
  padding: 4rpx 16rpx;
  border-radius: 16rpx;
}

.diary-emotion.happy {
  background: rgba(255, 215, 0, 0.2);
  color: #B8860B;
}

.diary-emotion.calm {
  background: rgba(135, 206, 235, 0.2);
  color: #4682B4;
}

.diary-emotion.sad {
  background: rgba(107, 142, 159, 0.2);
  color: #5A7A8A;
}

.daily-quote {
  display: flex;
  flex-direction: column;
  align-items: center;
  text-align: center;
  background: linear-gradient(135deg, #FDFBF7 0%, #F5F5F5 100%);
}

.quote-icon {
  font-size: 40rpx;
  margin-bottom: 16rpx;
}

.quote-text {
  font-size: 28rpx;
  color: var(--text-primary);
  line-height: 1.8;
  font-style: italic;
}

.quote-author {
  font-size: 24rpx;
  color: var(--text-tertiary);
  margin-top: 16rpx;
}
</style>