<template>
  <view class="page-register">
    <!-- 顶部返回 -->
    <view class="back-btn" @click="goBack">
      <text class="back-icon">←</text>
    </view>

    <!-- 标题 -->
    <view class="header">
      <text class="title">欢迎注册</text>
      <text class="subtitle">开启你的心理成长之旅</text>
    </view>

    <!-- 注册表单 -->
    <view class="register-form">
      <view class="input-item">
        <text class="input-icon">📧</text>
        <input 
          type="text" 
          v-model="email" 
          placeholder="请输入邮箱" 
          class="input-field"
        />
      </view>
      
      <view class="input-item">
        <text class="input-icon">🔢</text>
        <input 
          type="number" 
          v-model="code" 
          placeholder="请输入验证码" 
          maxlength="6"
          class="input-field"
        />
        <view 
          class="send-code" 
          :class="{ disabled: countdown > 0 }"
          @click="sendCode"
        >
          <text>{{ countdown > 0 ? `${countdown}s` : '获取验证码' }}</text>
        </view>
      </view>
      
      <view class="input-item">
        <text class="input-icon">👤</text>
        <input 
          v-model="nickname" 
          placeholder="请输入用户名" 
          maxlength="20"
          class="input-field"
        />
      </view>
      
      <view class="input-item">
        <text class="input-icon">🔒</text>
        <input 
          :type="showPassword ? 'text' : 'password'" 
          v-model="password" 
          placeholder="请设置密码（6-20位）" 
          class="input-field"
        />
        <text class="eye-icon" @click="togglePassword">
          {{ showPassword ? '👁' : '👁‍🗨' }}
        </text>
      </view>

      <!-- 年龄选择 -->
      <view class="input-item">
        <text class="input-icon">🎂</text>
        <picker 
          mode="selector" 
          :range="ageOptions" 
          :value="ageIndex"
          @change="onAgeChange"
        >
          <view class="picker-field">
            {{ ageIndex >= 0 ? ageOptions[ageIndex] : '请选择年龄' }}
          </view>
        </picker>
      </view>

      <!-- 性别选择 -->
      <view class="gender-select">
        <view 
          class="gender-item" 
          :class="{ active: gender === 1 }"
          @click="gender = 1"
        >
          <text class="gender-icon">👨</text>
          <text class="gender-label">男</text>
        </view>
        <view 
          class="gender-item" 
          :class="{ active: gender === 2 }"
          @click="gender = 2"
        >
          <text class="gender-icon">👩</text>
          <text class="gender-label">女</text>
        </view>
        <view 
          class="gender-item" 
          :class="{ active: gender === 3 }"
          @click="gender = 3"
        >
          <text class="gender-icon">🧑</text>
          <text class="gender-label">保密</text>
        </view>
      </view>

      <!-- 隐私协议 -->
      <view class="agreement">
        <view 
          class="checkbox" 
          :class="{ checked: agreed }"
          @click="agreed = !agreed"
        >
          <text v-if="agreed">✓</text>
        </view>
        <text class="agreement-text">
          我已阅读并同意
          <text class="link" @click.stop="showTerms">《用户协议》</text>
          和
          <text class="link" @click.stop="showPrivacy">《隐私政策》</text>
        </text>
      </view>

      <!-- 注册按钮 -->
      <button 
        class="register-btn" 
        :class="{ loading: loading }"
        @click="handleRegister"
        :disabled="loading || !agreed"
      >
        <text v-if="!loading">注册</text>
        <text v-else>注册中...</text>
      </button>
    </view>
  </view>
</template>

<script>
import { authApi } from '../../utils/request.js'

export default {
  data() {
    return {
      email: '',
      code: '',
      username: '',
      nickname: '',
      password: '',
      ageIndex: -1,
      ageOptions: Array.from({ length: 19 }, (_, i) => `${i + 13}岁`),
      gender: 3,
      showPassword: false,
      agreed: false,
      loading: false,
      countdown: 0
    }
  },
  methods: {
    goBack() {
      uni.navigateBack()
    },
    togglePassword() {
      this.showPassword = !this.showPassword
    },
    onAgeChange(e) {
      this.ageIndex = e.detail.value
    },
    async sendCode() {
      if (this.countdown > 0) return
      
      if (!this.email || !this.email.includes('@')) {
        uni.showToast({ title: '请输入正确的邮箱', icon: 'none' })
        return
      }
      
      try {
        await authApi.sendCode(this.email)
        uni.showToast({ title: '验证码已发送到邮箱', icon: 'success' })
        
        this.countdown = 60
        const timer = setInterval(() => {
          this.countdown--
          if (this.countdown <= 0) {
            clearInterval(timer)
          }
        }, 1000)
      } catch (e) {
        uni.showToast({ title: '发送失败', icon: 'none' })
      }
    },
    async handleRegister() {
      if (!this.email || !this.email.includes('@')) {
        uni.showToast({ title: '请输入邮箱', icon: 'none' })
        return
      }
      if (!this.code) {
        uni.showToast({ title: '请输入验证码', icon: 'none' })
        return
      }
      if (!this.nickname) {
        uni.showToast({ title: '请输入昵称', icon: 'none' })
        return
      }
      if (!this.password || this.password.length < 6) {
        uni.showToast({ title: '密码长度需6位以上', icon: 'none' })
        return
      }
      if (this.ageIndex < 0) {
        uni.showToast({ title: '请选择年龄', icon: 'none' })
        return
      }
      if (!this.agreed) {
        uni.showToast({ title: '请阅读并同意协议', icon: 'none' })
        return
      }
      
      this.loading = true
      
      try {
        await authApi.register({
          email: this.email,
          username: this.nickname,
          nickname: this.nickname,
          password: this.password,
          age: parseInt(this.ageOptions[this.ageIndex]),
          gender: this.gender
        })
        
        uni.showToast({ title: '注册成功', icon: 'success' })
        
        setTimeout(() => {
          uni.navigateBack()
        }, 1500)
      } catch (e) {
        uni.showToast({ title: '注册失败', icon: 'none' })
      } finally {
        this.loading = false
      }
    },
    showTerms() {
      uni.showModal({ title: '用户协议', content: '用户协议内容...' })
    },
    showPrivacy() {
      uni.showModal({ title: '隐私政策', content: '隐私政策内容...' })
    }
  }
}
</script>

<style scoped>
.page-register {
  min-height: 100vh;
  background: linear-gradient(180deg, #F8F6FF 0%, #FFF 50%);
  padding: 48rpx 48rpx 80rpx;
}

.back-btn {
  width: 80rpx;
  height: 80rpx;
  display: flex;
  align-items: center;
  justify-content: center;
}

.back-icon {
  font-size: 48rpx;
  color: var(--text-primary);
}

.header {
  margin: 32rpx 0 64rpx;
}

.title {
  font-size: 48rpx;
  font-weight: 700;
  color: var(--text-primary);
  display: block;
}

.subtitle {
  font-size: 28rpx;
  color: var(--text-tertiary);
  margin-top: 12rpx;
  display: block;
}

.register-form {
  background: var(--bg-primary);
  border-radius: 32rpx;
  padding: 48rpx 32rpx;
  box-shadow: var(--shadow-medium);
}

.input-item {
  display: flex;
  align-items: center;
  height: 96rpx;
  background: var(--bg-secondary);
  border-radius: 48rpx;
  padding: 0 24rpx;
  margin-bottom: 24rpx;
}

.input-icon {
  font-size: 36rpx;
  margin-right: 16rpx;
}

.input-field {
  flex: 1;
  height: 100%;
  font-size: 30rpx;
}

.picker-field {
  flex: 1;
  font-size: 30rpx;
  color: var(--text-primary);
}

.eye-icon {
  font-size: 36rpx;
  padding: 8rpx;
}

.send-code {
  padding: 16rpx 24rpx;
  background: var(--primary-color);
  border-radius: 24rpx;
  color: #FFF;
  font-size: 24rpx;
}

.send-code.disabled {
  background: var(--bg-tertiary);
  color: var(--text-disabled);
}

.gender-select {
  display: flex;
  justify-content: space-around;
  margin-bottom: 32rpx;
  padding: 24rpx 0;
}

.gender-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 24rpx 48rpx;
  border-radius: 24rpx;
  border: 2rpx solid var(--border-color);
}

.gender-item.active {
  border-color: var(--primary-color);
  background: rgba(123, 104, 238, 0.1);
}

.gender-icon {
  font-size: 48rpx;
}

.gender-label {
  font-size: 26rpx;
  color: var(--text-secondary);
  margin-top: 8rpx;
}

.agreement {
  display: flex;
  align-items: flex-start;
  margin-bottom: 32rpx;
}

.checkbox {
  width: 40rpx;
  height: 40rpx;
  border: 2rpx solid var(--border-color);
  border-radius: 8rpx;
  margin-right: 16rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}

.checkbox.checked {
  background: var(--primary-color);
  border-color: var(--primary-color);
  color: #FFF;
  font-size: 24rpx;
}

.agreement-text {
  font-size: 24rpx;
  color: var(--text-tertiary);
  line-height: 1.6;
}

.link {
  color: var(--primary-color);
}

.register-btn {
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
  box-shadow: 0 8rpx 24rpx rgba(123, 104, 238, 0.4);
}

.register-btn[disabled] {
  opacity: 0.6;
}
</style>