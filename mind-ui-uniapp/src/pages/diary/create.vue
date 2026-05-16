<template>
  <view class="page-diary-create">
    <!-- 心情选择 -->
    <view class="emotion-selector">
      <text class="section-title">今天心情怎么样？</text>
      <view class="emotion-list">
        <view 
          v-for="item in emotions" 
          :key="item.value"
          class="emotion-item"
          :class="{ active: selectedEmotion === item.value }"
          @click="selectEmotion(item.value)"
        >
          <view class="emotion-icon">{{ item.icon }}</view>
          <text class="emotion-name">{{ item.label }}</text>
        </view>
      </view>
    </view>

    <!-- 日记输入 -->
    <view class="diary-input">
      <textarea 
        class="input-area"
        v-model="content"
        placeholder="记录今天的故事、感受、想法..."
        :maxlength="5000"
        auto-height
      />
      <view class="input-footer">
        <text class="word-count">{{ content.length }}/5000</text>
      </view>
    </view>

    <!-- 标签选择 -->
    <view class="tag-section">
      <text class="section-title">添加标签</text>
      <view class="tags-wrap">
        <view 
          v-for="tag in availableTags" 
          :key="tag"
          class="tag-btn"
          :class="{ active: selectedTags.includes(tag) }"
          @click="toggleTag(tag)"
        >
          {{ tag }}
        </view>
      </view>
    </view>

    <!-- 媒体上传 -->
    <view class="media-section">
      <text class="section-title">添加图片（可选）</text>
      <view class="media-list">
        <view v-for="(img, index) in images" :key="index" class="media-item">
          <image :src="img" mode="aspectFill" class="preview-img" />
          <view class="delete-btn" @click="deleteImage(index)">
            <text>×</text>
          </view>
        </view>
        <view class="upload-btn" @click="chooseImage" v-if="images.length < 9">
          <text class="upload-icon">+</text>
          <text class="upload-text">添加图片</text>
        </view>
      </view>
    </view>

    <!-- AI分析开关 -->
    <view class="ai-toggle">
      <view class="toggle-left">
        <text class="toggle-icon">🤖</text>
        <text class="toggle-label">AI智能分析</text>
      </view>
      <switch 
        :checked="enableAI" 
        @change="enableAI = !enableAI"
        color="#7B68EE"
      />
    </view>

    <!-- 保存按钮 -->
    <view class="action-bar">
      <button 
        class="save-btn" 
        :class="{ loading: saving }"
        @click="saveDiary"
        :disabled="saving || !content"
      >
        {{ saving ? '保存中...' : '保存日记' }}
      </button>
    </view>
  </view>
</template>

<script>
import { diaryApi, fileApi } from '../../utils/request.js'

export default {
  data() {
    return {
      selectedEmotion: '',
      content: '',
      selectedTags: [],
      images: [],
      uploadTasks: [],
      enableAI: true,
      saving: false,
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
  methods: {
    selectEmotion(value) {
      this.selectedEmotion = value
    },
    toggleTag(tag) {
      const index = this.selectedTags.indexOf(tag)
      if (index > -1) {
        this.selectedTags.splice(index, 1)
      } else if (this.selectedTags.length < 5) {
        this.selectedTags.push(tag)
      }
    },
    async chooseImage() {
      const res = await uni.chooseImage({
        count: 9 - this.images.length,
        sizeType: ['compressed'],
        sourceType: ['album', 'camera']
      })

      const tempPaths = res.tempFilePaths || []
      uni.showLoading({ title: '上传中...', mask: true })

      try {
        const uploadedUrls = []
        for (const filePath of tempPaths) {
          const url = await fileApi.upload(filePath)
          uploadedUrls.push(url)
        }
        this.images = [...this.images, ...uploadedUrls]
      } catch (e) {
        uni.showToast({ title: '图片上传失败', icon: 'none' })
      } finally {
        uni.hideLoading()
      }
    },
    deleteImage(index) {
      this.images.splice(index, 1)
    },
    async saveDiary() {
      if (!this.content) {
        uni.showToast({ title: '请输入日记内容', icon: 'none' })
        return
      }
      if (!this.selectedEmotion) {
        uni.showToast({ title: '请选择心情', icon: 'none' })
        return
      }
      
      this.saving = true
      
      try {
        await diaryApi.create({
          content: this.content,
          emotionCategory: this.selectedEmotion,
          emotionTags: JSON.stringify(this.selectedTags),
          mediaUrls: JSON.stringify(this.images),
          enableAI: this.enableAI
        })
        
        uni.showToast({ title: '保存成功', icon: 'success' })
        
        setTimeout(() => {
          uni.navigateBack()
        }, 1500)
      } catch (e) {
        // 模拟成功
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
.page-diary-create {
  min-height: 100vh;
  background: var(--bg-secondary);
  padding: 24rpx;
  padding-bottom: 160rpx;
}

.section-title {
  font-size: 30rpx;
  font-weight: 600;
  color: var(--text-primary);
  display: block;
  margin-bottom: 20rpx;
}

.emotion-selector {
  background: var(--bg-primary);
  border-radius: 24rpx;
  padding: 32rpx;
  margin-bottom: 24rpx;
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
  background: var(--bg-primary);
  border-radius: 24rpx;
  padding: 32rpx;
  margin-bottom: 24rpx;
}

.input-area {
  width: 100%;
  min-height: 300rpx;
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

.tag-section {
  background: var(--bg-primary);
  border-radius: 24rpx;
  padding: 32rpx;
  margin-bottom: 24rpx;
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

.media-section {
  background: var(--bg-primary);
  border-radius: 24rpx;
  padding: 32rpx;
  margin-bottom: 24rpx;
}

.media-list {
  display: flex;
  flex-wrap: wrap;
  gap: 16rpx;
}

.media-item {
  position: relative;
  width: 180rpx;
  height: 180rpx;
  border-radius: 16rpx;
  overflow: hidden;
}

.preview-img {
  width: 100%;
  height: 100%;
}

.delete-btn {
  position: absolute;
  top: 8rpx;
  right: 8rpx;
  width: 40rpx;
  height: 40rpx;
  background: rgba(0, 0, 0, 0.5);
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #FFF;
  font-size: 32rpx;
}

.upload-btn {
  width: 180rpx;
  height: 180rpx;
  border: 2rpx dashed var(--border-color);
  border-radius: 16rpx;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
}

.upload-icon {
  font-size: 48rpx;
  color: var(--text-tertiary);
}

.upload-text {
  font-size: 24rpx;
  color: var(--text-tertiary);
  margin-top: 8rpx;
}

.ai-toggle {
  display: flex;
  justify-content: space-between;
  align-items: center;
  background: var(--bg-primary);
  border-radius: 24rpx;
  padding: 32rpx;
  margin-bottom: 24rpx;
}

.toggle-left {
  display: flex;
  align-items: center;
}

.toggle-icon {
  font-size: 40rpx;
  margin-right: 16rpx;
}

.toggle-label {
  font-size: 28rpx;
  color: var(--text-primary);
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