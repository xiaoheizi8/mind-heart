<template>
  <view class="page-chat">
    <!-- AI人格选择 -->
    <view class="persona-selector">
      <view 
        v-for="persona in personas" 
        :key="persona.value"
        class="persona-item"
        :class="{ active: currentPersona === persona.value }"
        @click="switchPersona(persona.value)"
      >
        <text class="persona-icon">{{ persona.icon }}</text>
        <text class="persona-name">{{ persona.label }}</text>
      </view>
    </view>

    <!-- 对话区域 -->
    <scroll-view 
      class="chat-scroll" 
      scroll-y 
      :scroll-into-view="scrollIntoView"
      @scrolltoupper="loadMoreHistory"
    >
      <view v-if="loadingHistory" class="loading-history">
        <text>正在加载我们的回忆...</text>
      </view>
      
      <view class="chat-list">
        <!-- AI开场白 -->
        <view v-if="messages.length === 0" class="welcome-msg">
          <text class="welcome-icon">💜</text>
          <text class="welcome-text">{{ welcomeMsg }}</text>
        </view>
        
        <!-- 消息列表 -->
        <view 
          v-for="(msg, index) in messages" 
          :key="index"
          :id="'msg-' + index"
          class="message-item"
          :class="msg.role"
        >
          <view v-if="msg.role === 'assistant'" class="avatar">
            <text>AI</text>
          </view>
          <view class="msg-content">
            <text class="msg-text">{{ msg.content }}</text>
          </view>
        </view>
        
        <!-- 连接状态 -->
        <view v-if="connectionStatus === 'connecting'" class="status-indicator">
          <text class="status-text">正在连接AI...</text>
        </view>
        <view v-if="connectionStatus === 'reconnecting'" class="status-indicator">
          <text class="status-text">连接断开，正在重连...</text>
        </view>

        <!-- 正在输入 -->
        <view v-if="loading" class="typing-indicator">
          <view class="typing-dots">
            <text class="dot"></text>
            <text class="dot"></text>
            <text class="dot"></text>
          </view>
          <text class="typing-text">正在认真回复你...</text>
        </view>
      </view>
    </scroll-view>

    <!-- 输入区域 -->
    <view class="input-area">
      <view class="input-wrapper">
        <textarea 
          class="msg-input"
          v-model="inputText"
          placeholder="输入你想说的话..."
          :adjust-position="true"
          :show-confirm-bar="false"
          @focus="onInputFocus"
          @blur="onInputBlur"
        />
        <view class="input-tools">
          <text class="tool-icon" @click="showEmotionPanel">😊</text>
        </view>
      </view>
      <view
        v-if="loading"
        class="send-btn cancel"
        @click="cancelChat"
      >
        <text>停止</text>
      </view>
      <view
        v-else
        class="send-btn"
        :class="{ active: inputText.trim() }"
        @click="sendMessage"
      >
        <text>发送</text>
      </view>
    </view>

    <!-- 情绪选择面板 -->
    <view v-if="showEmotionPanel" class="emotion-panel">
      <view class="panel-header">
        <text class="panel-title">告诉AI你的心情</text>
        <text class="close-btn" @click="showEmotionPanel = false">×</text>
      </view>
      <view class="emotion-grid">
        <view 
          v-for="emotion in quickEmotions" 
          :key="emotion.value"
          class="emotion-btn"
          @click="selectEmotion(emotion)"
        >
          <text class="emotion-emoji">{{ emotion.icon }}</text>
          <text class="emotion-label">{{ emotion.label }}</text>
        </view>
      </view>
    </view>
  </view>
</template>

<script>
import { chatApi, streamChat } from '../../utils/request.js'

export default {
  data() {
    return {
      currentPersona: 'listener',
      inputText: '',
      messages: [],
      loading: false,
      loadingHistory: false,
      scrollIntoView: '',
      showEmotionPanel: false,
      currentEventType: '', // SSE事件类型
      connectionStatus: '', // 连接状态: connecting, streaming, reconnecting, error
      sseController: null, // SSE控制器（用于取消）
      welcomeMsg: '你好呀，很高兴你来了。你可以对我倾诉任何事情，我会一直在这里用心倾听你、陪伴你。',
      personas: [
        { value: 'listener', icon: '👂', label: '倾听者' },
        { value: 'analyst', icon: '🧠', label: '分析师' },
        { value: 'healer', icon: '💝', label: '治愈者' }
      ],
      quickEmotions: [
        { value: 'happy', icon: '😊', label: '开心', text: '我今天很开心，因为...' },
        { value: 'sad', icon: '😢', label: '难过', text: '我有点难过，因为...' },
        { value: 'anxious', icon: '😰', label: '焦虑', text: '我感到很焦虑...' },
        { value: 'angry', icon: '😠', label: '生气', text: '我很生气，因为...' },
        { value: 'fear', icon: '😨', label: '害怕', text: '我有点害怕...' },
        { value: 'confused', icon: '😵', label: '困惑', text: '我有点困惑...' }
      ]
    }
  },
  onLoad() {
    this.loadHistory()
  },
  onUnload() {
    // 保存对话上下文
  },
  methods: {
    switchPersona(value) {
      this.currentPersona = value
      uni.showToast({ title: '已切换人格', icon: 'none' })
    },
    async loadHistory() {
      try {
        const res = await chatApi.getHistory({ limit: 20 })
        this.messages = res.list || []
      } catch (e) {
        // 模拟数据
        this.messages = []
      }
    },
    loadMoreHistory() {
      if (this.loadingHistory) return
      this.loadingHistory = true
      setTimeout(() => {
        this.loadingHistory = false
      }, 1000)
    },
    selectEmotion(emotion) {
      this.inputText = emotion.text
      this.showEmotionPanel = false
    },
    // 取消当前对话
    cancelChat() {
      if (this.sseController) {
        this.sseController.abort()
        this.sseController = null
        this.loading = false
        this.connectionStatus = ''
        uni.showToast({ title: '已取消', icon: 'none' })
      }
    },

    async sendMessage() {
      if (!this.inputText.trim() || this.loading) return

      // 如果已经有SSE连接，先强制取消它，防止重复请求
      if (this.sseController) {
        console.warn('⚠️ 取消之前的SSE连接，防止重复请求')
        this.sseController.abort()
        this.sseController = null
        // 等待一小段时间确保连接完全关闭
        await new Promise(resolve => setTimeout(resolve, 100))
      }

      const userMsg = {
        role: 'user',
        content: this.inputText.trim()
      }
      this.messages.push(userMsg)

      const sendText = this.inputText
      this.inputText = ''

      this.scrollToBottom()
      this.loading = true
      this.connectionStatus = 'connecting'

      // 添加占位消息用于流式更新
      const assistantMsg = {
        role: 'assistant',
        content: ''
      }
      this.messages.push(assistantMsg)

      const msgIndex = this.messages.length - 1

      try {
        console.log('🚀 准备调用streamChat，消息索引:', msgIndex)

        this.sseController = streamChat.connect({
          message: sendText,
          persona: this.currentPersona,
          // maxRetries使用默认值0,不重试

          onStart: () => {
            this.connectionStatus = 'streaming'
            console.log('🎬 流式对话开始')
          },

          onChunk: (chunk, fullContent) => {
            this.$set(this.messages[msgIndex], 'content', fullContent)
            this.scrollToBottom()
          },

          onDone: (fullContent) => {
            console.log('✅ 流式对话完成，长度:', fullContent.length)
            this.loading = false
            this.connectionStatus = ''
            this.scrollToBottom()
            // 完成后立即中止连接并清理，防止重连
            if (this.sseController) {
              console.log('🛑 完成时中止SSE连接')
              this.sseController.abort()
              this.sseController = null
            }
          },

          onError: (error) => {
            console.error('❌ 流式对话错误:', error)
            this.loading = false
            this.connectionStatus = 'error'
            this.sseController = null

            if (!this.messages[msgIndex].content) {
              const errorMsg = '抱歉，对话服务暂时不可用。错误: ' + (error.message || '未知错误')
              this.$set(this.messages[msgIndex], 'content', errorMsg)
            }
            this.scrollToBottom()
          },

          onClose: () => {
            console.log('🔌 SSE连接已关闭')
            if (this.loading) {
              this.loading = false
              this.connectionStatus = ''
              this.sseController = null
            }
          }
        })
      } catch (e) {
        console.error('流式对话失败:', e)
        this.loading = false
        this.connectionStatus = 'error'
        this.sseController = null

        if (!this.messages[msgIndex].content) {
          const errorMsg = '抱歉，对话服务暂时不可用。错误: ' + (e.message || '未知错误')
          this.$set(this.messages[msgIndex], 'content', errorMsg)
        }
        this.scrollToBottom()
      }
    },

    scrollToBottom() {
      this.$nextTick(() => {
        this.scrollIntoView = 'msg-' + (this.messages.length - 1)
      })
    },
    onInputFocus() {},
    onInputBlur() {},
    getDefaultReply(text) {
      const replies = {
        'listener': '谢谢你愿意告诉我，我在这里用心倾听你。',
        'analyst': '我很想陪你一起看看，一起梳理一下心里的感受。',
        'healer': '我一直在这里，会一直陪伴你度过。'
      }
      return replies[this.currentPersona] || '我在这里陪你。'
    }
  }
}
</script>

<style scoped>
.page-chat {
  display: flex;
  flex-direction: column;
  height: 100vh;
  background: var(--bg-secondary);
}

.persona-selector {
  display: flex;
  justify-content: space-around;
  padding: 24rpx;
  background: var(--bg-primary);
  border-bottom: 1rpx solid var(--border-light);
}

.persona-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 16rpx 32rpx;
  border-radius: 16rpx;
}

.persona-item.active {
  background: rgba(123, 104, 238, 0.1);
}

.persona-icon {
  font-size: 40rpx;
}

.persona-name {
  font-size: 24rpx;
  color: var(--text-secondary);
  margin-top: 8rpx;
}

.chat-scroll {
  flex: 1;
  padding: 24rpx;
}

.loading-history {
  text-align: center;
  padding: 24rpx;
  color: var(--text-tertiary);
  font-size: 24rpx;
}

.welcome-msg {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 64rpx 32rpx;
  text-align: center;
}

.welcome-icon {
  font-size: 80rpx;
  margin-bottom: 24rpx;
}

.welcome-text {
  font-size: 28rpx;
  color: var(--text-secondary);
  line-height: 1.8;
}

.message-item {
  display: flex;
  margin-bottom: 24rpx;
}

.message-item.user {
  flex-direction: row-reverse;
}

.message-item.assistant {
  flex-direction: row;
}

.avatar {
  width: 72rpx;
  height: 72rpx;
  background: linear-gradient(135deg, #7B68EE, #9D8FFF);
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #FFF;
  font-size: 24rpx;
  font-weight: 600;
  flex-shrink: 0;
}

.msg-content {
  max-width: 70%;
  padding: 24rpx 32rpx;
  border-radius: 24rpx;
}

.user .msg-content {
  background: var(--primary-color);
  color: #FFF;
  margin-right: 16rpx;
}

.assistant .msg-content {
  background: var(--bg-primary);
  color: var(--text-primary);
  margin-left: 16rpx;
}

.msg-text {
  font-size: 28rpx;
  line-height: 1.6;
  word-break: break-word;
}

.typing-indicator {
  display: flex;
  align-items: center;
  padding: 16rpx 0;
}

.typing-dots {
  display: flex;
  gap: 8rpx;
}

.dot {
  width: 12rpx;
  height: 12rpx;
  background: var(--text-tertiary);
  border-radius: 50%;
  animation: bounce 1.4s infinite ease-in-out;
}

.dot:nth-child(1) { animation-delay: 0s; }
.dot:nth-child(2) { animation-delay: 0.2s; }
.dot:nth-child(3) { animation-delay: 0.4s; }

@keyframes bounce {
  0%, 80%, 100% { transform: scale(0.6); }
  40% { transform: scale(1); }
}

.typing-text {
  font-size: 24rpx;
  color: var(--text-tertiary);
  margin-left: 16rpx;
}

.input-area {
  display: flex;
  align-items: center;
  padding: 24rpx;
  background: var(--bg-primary);
  border-top: 1rpx solid var(--border-light);
  position: relative;
  z-index: 100;
}

.input-wrapper {
  flex: 1;
  display: flex;
  align-items: center;
  background: var(--bg-secondary);
  border-radius: 48rpx;
  padding: 16rpx 24rpx;
  min-width: 0;
}

.msg-input {
  flex: 1;
  min-height: 48rpx;
  max-height: 200rpx;
  font-size: 28rpx;
  line-height: 1.5;
  min-width: 0;
  width: 100%;
}

.input-tools {
  display: flex;
  gap: 16rpx;
}

.tool-icon {
  font-size: 40rpx;
  padding: 8rpx;
}

.send-btn {
  margin-left: 16rpx;
  padding: 20rpx 40rpx;
  background: var(--bg-tertiary);
  border-radius: 48rpx;
  font-size: 28rpx;
  color: #999;
  border: 2rpx solid #DDD;
  z-index: 10;
  position: relative;
}

.send-btn.active {
  background: var(--primary-color);
  color: #FFF;
  border: 2rpx solid var(--primary-color);
}

.send-btn.cancel {
  background: #FF6B6B;
  color: #FFF;
  border: 2rpx solid #FF6B6B;
}

.status-indicator {
  display: flex;
  align-items: center;
  padding: 12rpx 24rpx;
  background: rgba(123, 104, 238, 0.1);
  border-radius: 16rpx;
  margin: 16rpx 0;
}

.status-text {
  font-size: 24rpx;
  color: var(--primary-color);
}

.emotion-panel {
  position: fixed;
  bottom: 0;
  left: 0;
  right: 0;
  background: var(--bg-primary);
  border-radius: 32rpx 32rpx 0 0;
  padding: 32rpx;
  box-shadow: 0 -8rpx 24rpx rgba(0, 0, 0, 0.1);
}

.panel-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24rpx;
}

.panel-title {
  font-size: 28rpx;
  font-weight: 500;
  color: var(--text-primary);
}

.close-btn {
  font-size: 48rpx;
  color: var(--text-tertiary);
}

.emotion-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 24rpx;
}

.emotion-btn {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 24rpx;
  background: var(--bg-secondary);
  border-radius: 16rpx;
}

.emotion-emoji {
  font-size: 48rpx;
  margin-bottom: 8rpx;
}

.emotion-label {
  font-size: 24rpx;
  color: var(--text-secondary);
}
</style>