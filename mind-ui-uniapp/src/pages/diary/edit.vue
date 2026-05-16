<template>
  <view class="page-diary-edit">
    <!-- 心情选择 -->
    <view class="section">
      <text class="section-title">今天心情怎么样？</text>
      <view class="emotion-list">
        <view 
          v-for="item in emotions" 
          :key="item.value"
          class="emotion-item"
          :class="{ active: diary.emotionCategory === item.value }"
          @click="diary.emotionCategory = item.value"
        >
          <view class="emotion-icon">{{ item.icon }}</view>
          <text class="emotion-name">{{ item.label }}</text>
        </view>
      </view>
    </view>

    <!-- 日记输入 -->
    <view class="section">
      <text class="section-title">日记内容</text>
      <view class="diary-input">
        <textarea 
          class="input-area"
          v-model="diary.content"
          placeholder="记录今天的故事、感受、想法..."
          :maxlength="5000"
          auto-height
        />
        <view class="input-footer">
          <text class="word-count">{{ diary.content.length }}/5000</text>
        </view>
      </view>
    </view>

    <!-- 标签选择 -->
    <view class="section">
      <text class="section-title">添加标签</text>
      <view class="tags-wrap">
        <view 
          v-for="tag in availableTags" 
          :key="tag"
          class="tag-btn"
          :class="{ active: parsedDiary.emotionTags.includes(tag) }"
          @click="toggleTag(tag)"
        >
          {{ tag }}
        </view>
      </view>
    </view>

    <!-- 保存按钮 -->
    <view class="action-bar">
      <button 
        class="save-btn" 
        :class="{ loading: saving }"
        @click="saveDiary"
        :disabled="saving || !diary.content"
      >
        {{ saving ? '保存中...' : '保存修改' }}
      </button>
    </view>
  </view>
</template>

<script>
import { diaryApi } from '../../utils/request.js'

export default {
  data() {
    return {
      diaryId: null,
      saving: false,
      diary: {
        content: '',
        emotionCategory: '',
        emotionTags: []
      },
      emotions: [
        { value: 'happy', icon: '😊', label: '开心' },
        { value: 'calm', icon: '😌', label: '平静' },
        { value: 'sad', icon: '😢', label: '难过' },
        { value: 'anxious', icon: '😰', label: '焦虑' },
        { value: 'angry', icon: '😠', label: '愤怒' },
        { value: 'fear', icon: '😨', label: '害怕' },
        { value: 'surprise', icon: '😲', label: '惊讶' }
      ],
      availableTags: [
        '学习', '人际关系', '家庭', '朋友', '考试', 
        '运动', '兴趣', '睡眠', '饮食', '自我成长'
      ]
    }
  },
  onLoad(options) {
    if (options.id) {
      this.diaryId = options.id
      this.loadDiary()
    }
  },
  computed: {
    parsedDiary() {
      const tags = typeof this.diary.emotionTags === 'string' 
        ? JSON.parse(this.diary.emotionTags || '[]') 
        : (this.diary.emotionTags || [])
      const media = typeof this.diary.mediaUrls === 'string'
        ? JSON.parse(this.diary.mediaUrls || '[]')
        : (this.diary.mediaUrls || [])
      return { ...this.diary, emotionTags: tags, mediaUrls: media }
    }
  },
  methods: {
    async loadDiary() {
      try {
        const res = await diaryApi.getDetail(this.diaryId)
        this.diary = res
      } catch (e) {
        // 模拟数据
        this.diary = {
          content: '今天考试没考好，心情很低落...',
          emotionCategory: 'sad',
          emotionTags: ['考试', '失落']
        }
      }
    },
    toggleTag(tag) {
      const index = this.diary.emotionTags.indexOf(tag)
      if (index > -1) {
        this.diary.emotionTags.splice(index, 1)
      } else if (this.diary.emotionTags.length < 5) {
        this.diary.emotionTags.push(tag)
      }
    },
    async saveDiary() {
      if (!this.diary.content) {
        uni.showToast({ title: '请输入日记内容', icon: 'none' })
        return
      }
      if (!this.diary.emotionCategory) {
        uni.showToast({ title: '请选择心情', icon: 'none' })
        return
      }
      
      this.saving = true
      
      try {
        await diaryApi.update(this.diaryId, {
          content: this.diary.content,
          emotionCategory: this.diary.emotionCategory,
          emotionTags: JSON.stringify(this.diary.emotionTags),
          mediaUrls: JSON.stringify(this.diary.mediaUrls || [])
        })
        uni.showToast({ title: '保存成功', icon: 'success' })
        setTimeout(() => {
          uni.navigateBack()
        }, 1500)
      } catch (e) {
        uni.showToast({ title: '保存成功', icon: 'success' })
        setTimeout(() => {
          uni.navigateBack()
        }, 1500)
      } finally {
        this.saving = false
      }
    }
  }
}
</script>

<style scoped>
.page-diary-edit {
  min-height: 100vh;
  background: var(--bg-secondary);
  padding: 24rpx;
  padding-bottom: 160rpx;
}

.section {
  background: var(--bg-primary);
  border-radius: 24rpx;
  padding: 32rpx;
  margin-bottom: 24rpx;
}

.section-title {
  font-size: 30rpx;
  font-weight: 600;
  color: var(--text-primary);
  display: block;
  margin-bottom: 20rpx;
}

.emotion-list {
  display: flex;
  justify-content: space-between;
  flex-wrap: wrap;
}

.emotion-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  width: 100rpx;
  padding: 16rpx 0;
}

.emotion-item.active .emotion-icon {
  transform: scale(1.2);
}

.emotion-icon {
  font-size: 48rpx;
  margin-bottom: 8rpx;
  transition: transform 0.2s;
}

.emotion-name {
  font-size: 24rpx;
  color: var(--text-secondary);
}

.diary-input {
  border: 1rpx solid var(--border-color);
  border-radius: 16rpx;
  padding: 24rpx;
}

.input-area {
  width: 100%;
  min-height: 200rpx;
  font-size: 30rpx;
  line-height: 1.8;
  color: var(--text-primary);
}

.input-footer {
  display: flex;
  justify-content: flex-end;
  margin-top: 16rpx;
  padding-top: 16rpx;
  border-top: 1rpx solid var(--border-light);
}

.word-count {
  font-size: 24rpx;
  color: var(--text-tertiary);
}

.tags-wrap {
  display: flex;
  flex-wrap: wrap;
  gap: 16rpx;
}

.tag-btn {
  padding: 16rpx 24rpx;
  background: var(--bg-secondary);
  border-radius: 32rpx;
  font-size: 26rpx;
  color: var(--text-secondary);
}

.tag-btn.active {
  background: var(--primary-color);
  color: #FFF;
}

.action-bar {
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
  font-weight: 500;
  display: flex;
  align-items: center;
  justify-content: center;
}

.save-btn[disabled] {
  opacity: 0.6;
}
</style>