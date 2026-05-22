<template>
  <view class="create-story">
    <view class="form-group">
      <!-- 情绪类型 -->
      <view class="form-item">
        <view class="form-label">当前心情</view>
        <view class="emotion-picker">
          <view 
            v-for="item in emotionOptions" 
            :key="item.value"
            :class="['emotion-btn', { active: form.emotionType === item.value }]"
            :style="{ backgroundColor: form.emotionType === item.value ? item.color : '' }"
            @tap="form.emotionType = item.value"
          >
            {{ item.label }}
          </view>
        </view>
      </view>

      <!-- 标题 -->
      <view class="form-item">
        <view class="form-label">标题 <text class="required">*</text></view>
        <input 
          v-model="form.title" 
          class="form-input" 
          placeholder="给故事起个标题(不超过50字)"
          maxlength="50"
        />
      </view>

      <!-- 内容 -->
      <view class="form-item">
        <view class="form-label">故事内容 <text class="required">*</text></view>
        <textarea 
          v-model="form.content" 
          class="form-textarea" 
          placeholder="写下你的故事..."
          maxlength="2000"
        />
        <text class="char-count">{{ form.content.length }}/2000</text>
      </view>

      <!-- 标签 -->
      <view class="form-item">
        <view class="form-label">标签</view>
        <input 
          v-model="form.tags" 
          class="form-input" 
          placeholder="输入标签,用逗号分隔"
        />
        <view class="tag-hint">例如: 职场,人际,成长</view>
      </view>

      <!-- 匿名设置 -->
      <view class="form-item">
        <view class="form-label">发布方式</view>
        <view class="anonymous-toggle">
          <switch 
            :checked="form.isAnonymous" 
            @change="form.isAnonymous = $event.detail.value"
            color="#7B68EE"
          />
          <text class="toggle-label">{{ form.isAnonymous ? '匿名发布' : '公开发布' }}</text>
        </view>
      </view>
    </view>

    <!-- 发布按钮 -->
    <view class="bottom-bar">
      <button class="publish-btn" :disabled="submitting" @tap="onSubmit">
        {{ submitting ? '发布中...' : '发布故事' }}
      </button>
    </view>
  </view>
</template>

<script>
import { storyApi } from '@/utils/request'

export default {
  data() {
    return {
      form: {
        emotionType: '',
        title: '',
        content: '',
        tags: '',
        isAnonymous: true
      },
      submitting: false,
      
      emotionOptions: [
        { value: 'happy', label: '开心', color: '#52c41a' },
        { value: 'calm', label: '平静', color: '#1890ff' },
        { value: 'sad', label: '难过', color: '#8c8c8c' },
        { value: 'anxious', label: '焦虑', color: '#faad14' },
        { value: 'angry', label: '愤怒', color: '#ff4d4f' },
        { value: 'fear', label: '害怕', color: '#722ed1' }
      ]
    }
  },
  
  methods: {
    async onSubmit() {
      // 校验
      if (!this.form.title.trim()) {
        uni.showToast({ title: '请输入标题', icon: 'none' })
        return
      }
      if (!this.form.content.trim()) {
        uni.showToast({ title: '请输入内容', icon: 'none' })
        return
      }
      
      this.submitting = true
      
      try {
        const res = await storyApi.publish({
          title: this.form.title.trim(),
          content: this.form.content.trim(),
          emotionType: this.form.emotionType,
          tags: this.form.tags.trim(),
          isAnonymous: this.form.isAnonymous
        })
        
        uni.showToast({ title: '发布成功,待审核', icon: 'success' })
        setTimeout(() => {
          uni.navigateBack()
        }, 1500)
      } catch (e) {
        console.error('发布失败', e)
      } finally {
        this.submitting = false
      }
    }
  }
}
</script>

<style lang="scss" scoped>
.create-story {
  min-height: 100vh;
  background: #f5f5f5;
  padding: 32rpx;
  padding-bottom: 160rpx;
}

.form-group {
  display: flex;
  flex-direction: column;
  gap: 32rpx;
}

.form-item {
  background: #fff;
  border-radius: 24rpx;
  padding: 32rpx;
}

.form-label {
  font-size: 28rpx;
  font-weight: 500;
  color: #333;
  margin-bottom: 16rpx;
}

.required {
  color: #ff4d4f;
}

.emotion-picker {
  display: flex;
  flex-wrap: wrap;
  gap: 16rpx;
}

.emotion-btn {
  padding: 16rpx 24rpx;
  border-radius: 32rpx;
  font-size: 26rpx;
  background: #f5f5f5;
  color: #666;
  
  &.active {
    color: #fff;
  }
}

.form-input {
  height: 88rpx;
  padding: 0 24rpx;
  background: #f9f9f9;
  border-radius: 16rpx;
  font-size: 28rpx;
}

.form-textarea {
  width: 100%;
  height: 400rpx;
  padding: 24rpx;
  background: #f9f9f9;
  border-radius: 16rpx;
  font-size: 28rpx;
  box-sizing: border-box;
}

.char-count {
  display: block;
  text-align: right;
  font-size: 24rpx;
  color: #999;
  margin-top: 12rpx;
}

.tag-hint {
  font-size: 24rpx;
  color: #999;
  margin-top: 12rpx;
}

.anonymous-toggle {
  display: flex;
  align-items: center;
  gap: 16rpx;
}

.toggle-label {
  font-size: 28rpx;
  color: #666;
}

.bottom-bar {
  position: fixed;
  left: 0;
  right: 0;
  bottom: 0;
  padding: 24rpx 32rpx;
  background: #fff;
  border-top: 1px solid #f0f0f0;
}

.publish-btn {
  width: 100%;
  height: 96rpx;
  line-height: 96rpx;
  background: linear-gradient(135deg, #7B68EE, #9c8bff);
  color: #fff;
  border-radius: 48rpx;
  font-size: 32rpx;
  font-weight: bold;
  
  &[disabled] {
    opacity: 0.6;
  }
}
</style>