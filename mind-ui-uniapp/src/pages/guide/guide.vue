<template>
  <view class="page-guide">
    <swiper class="swiper" :indicator-dots="false" :autoplay="false" @change="onChange">
      <swiper-item v-for="(item, index) in pages" :key="index">
        <view class="page-content">
          <image :src="item.image" mode="aspectFit" class="guide-image" />
          <view class="text-area">
            <text class="title">{{ item.title }}</text>
            <text class="desc">{{ item.desc }}</text>
          </view>
        </view>
      </swiper-item>
    </swiper>

    <!-- 底部控制 -->
    <view class="footer">
      <view class="dots">
        <view 
          v-for="(item, index) in pages" 
          :key="index"
          class="dot"
          :class="{ active: current === index }"
        ></view>
      </view>
      
      <view class="action-area">
        <text v-if="current < pages.length - 1" class="skip-btn" @click="skipGuide">
          跳过
        </text>
        <button v-if="current < pages.length - 1" class="next-btn" @click="nextPage">
          下一页
        </button>
        <button v-else class="start-btn" @click="startApp">
          开始使用
        </button>
      </view>
    </view>
  </view>
</template>

<script>
export default {
  data() {
    return {
      current: 0,
      pages: [
        {
          image: '/static/guide/diary.png',
          title: '记录情绪日记',
          desc: '用文字记录每一天的心情，AI智能分析你的情绪状态'
        },
        {
          image: '/static/guide/chat.png',
          title: 'AI陪伴倾听',
          desc: '随时随地倾诉，获得专业的心理陪伴和支持'
        },
        {
          image: '/static/guide/report.png',
          title: '情绪报告分析',
          desc: '可视化了解自己的情绪变化，更认识自己'
        },
        {
          image: '/static/guide/knowledge.png',
          title: '心理知识库',
          desc: '学习科学的心理健康知识，成为更好的自己'
        }
      ]
    }
  },
  methods: {
    onChange(e) {
      this.current = e.detail.current
    },
    nextPage() {
      if (this.current < this.pages.length - 1) {
        this.current++
      }
    },
    skipGuide() {
      this.enterApp()
    },
    startApp() {
      this.enterApp()
    },
    enterApp() {
      uni.setStorageSync('hasSeenGuide', true)
      uni.redirectTo({ url: '/pages/login/login' })
    }
  }
}
</script>

<style scoped>
.page-guide {
  display: flex;
  flex-direction: column;
  height: 100vh;
  background: #F8F6FF;
}

.swiper {
  flex: 1;
  height: 100%;
}

.page-content {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  height: 100%;
  padding: 64rpx;
}

.guide-image {
  width: 400rpx;
  height: 400rpx;
  margin-bottom: 64rpx;
}

.text-area {
  display: flex;
  flex-direction: column;
  align-items: center;
  text-align: center;
}

.title {
  font-size: 44rpx;
  font-weight: 600;
  color: var(--text-primary);
  margin-bottom: 24rpx;
}

.desc {
  font-size: 28rpx;
  color: var(--text-secondary);
  line-height: 1.6;
}

.footer {
  padding: 48rpx 32rpx;
  background: var(--bg-primary);
}

.dots {
  display: flex;
  justify-content: center;
  gap: 16rpx;
  margin-bottom: 48rpx;
}

.dot {
  width: 16rpx;
  height: 16rpx;
  background: var(--border-color);
  border-radius: 8rpx;
  transition: all 0.3s;
}

.dot.active {
  width: 48rpx;
  background: var(--primary-color);
}

.action-area {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.skip-btn {
  font-size: 28rpx;
  color: var(--text-tertiary);
}

.next-btn,
.start-btn {
  width: 240rpx;
  height: 88rpx;
  background: linear-gradient(135deg, var(--primary-color), var(--primary-dark));
  border-radius: 44rpx;
  color: #FFF;
  font-size: 30rpx;
  display: flex;
  align-items: center;
  justify-content: center;
}
</style>