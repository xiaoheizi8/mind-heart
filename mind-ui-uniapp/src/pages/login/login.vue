<template>
  <view class="page-login">
    <!-- Logo区域 -->
    <view class="logo-area">
      <view class="logo">
        <text class="logo-icon">💜</text>
      </view>
      <text class="app-name">心域</text>
      <text class="app-desc">青少年心理数字孪生系统</text>
    </view>

    <!-- 登录表单 -->
    <view class="login-form">
      <!-- 切换登录方式 -->
      <view class="login-type-tabs">
        <view 
          class="tab-item" 
          :class="{ active: !showCodeLogin }"
          @click="switchLoginType('password')"
        >
          <text>密码登录</text>
        </view>
        <view 
          class="tab-item" 
          :class="{ active: showCodeLogin }"
          @click="switchLoginType('code')"
        >
          <text>验证码登录</text>
        </view>
      </view>

      <!-- 密码登录表单 -->
      <view v-if="!showCodeLogin" class="input-group">
        <view class="input-item">
          <text class="input-icon">👤</text>
          <input 
            type="text" 
            v-model="loginForm.account" 
            placeholder="手机号/用户名/邮箱" 
            class="input-field"
          />
        </view>
        
        <view class="input-item">
          <text class="input-icon">🔒</text>
          <input 
            :type="showPassword ? 'text' : 'password'" 
            v-model="loginForm.password" 
            placeholder="请输入密码" 
            class="input-field"
          />
          <text class="eye-icon" @click="togglePassword">
            {{ showPassword ? '👁' : '👁‍🗨' }}
          </text>
        </view>
      </view>

      <!-- 验证码登录表单 -->
      <view v-else class="input-group">
        <view class="input-item">
          <text class="input-icon">📱</text>
          <input 
            type="number" 
            v-model="loginForm.phone" 
            placeholder="请输入手机号" 
            maxlength="11"
            class="input-field"
          />
        </view>
        
        <view class="input-item">
          <text class="input-icon">🔢</text>
          <input 
            type="number" 
            v-model="loginForm.code" 
            placeholder="请输入验证码" 
            maxlength="6"
            class="input-field code-input"
          />
          <view 
            class="send-code" 
            :class="{ disabled: countdown > 0 }"
            @click="sendCode"
          >
            <text>{{ countdown > 0 ? `${countdown}s` : '获取验证码' }}</text>
          </view>
        </view>
      </view>

      <!-- 登录按钮 -->
      <button 
        class="login-btn" 
        :class="{ loading: loading }"
        @click="handleLogin"
        :disabled="loading"
      >
        <text v-if="!loading">登录</text>
        <text v-else>登录中...</text>
      </button>

      <!-- 注册入口 -->
      <view class="register-link">
        <text class="link-text">还没有账号？</text>
        <text class="link-btn" @click="goToRegister">立即注册</text>
      </view>
    </view>

    <!-- 第三方登录 -->
    <view class="third-party">
      <view class="divider">
        <view class="divider-line"></view>
        <text class="divider-text">其他登录方式</text>
        <view class="divider-line"></view>
      </view>
      
      <view class="third-list">
        <view class="third-item">
          <text class="third-icon">🍎</text>
          <text class="third-label">Apple</text>
        </view>
      </view>
    </view>
  </view>
</template>

<script>
import { authApi } from '../../utils/request.js'

export default {
  data() {
    return {
      loginForm: {
        account: '',
        password: '',
        phone: '',
        code: ''
      },
      showPassword: false,
      showCodeLogin: false,
      loading: false,
      countdown: 0,
      timer: null
    }
  },
  beforeDestroy() {
    if (this.timer) {
      clearInterval(this.timer)
    }
  },
  methods: {
    switchLoginType(type) {
      this.showCodeLogin = type === 'code'
      // 切换时清空表单
      if (type === 'code') {
        this.loginForm.account = ''
        this.loginForm.password = ''
      } else {
        this.loginForm.phone = ''
        this.loginForm.code = ''
      }
    },
    togglePassword() {
      this.showPassword = !this.showPassword
    },
    async sendCode() {
      if (this.countdown > 0) return
      
      if (!this.loginForm.phone) {
        uni.showToast({ title: '请输入手机号', icon: 'none' })
        return
      }
      
      // 验证手机号格式
      if (!/^1[3-9]\d{9}$/.test(this.loginForm.phone)) {
        uni.showToast({ title: '请输入正确的手机号', icon: 'none' })
        return
      }
      
      try {
        await authApi.sendCode(this.loginForm.phone)
        uni.showToast({ title: '验证码已发送到手机', icon: 'success' })
        
        this.countdown = 60
        this.timer = setInterval(() => {
          this.countdown--
          if (this.countdown <= 0) {
            clearInterval(this.timer)
            this.timer = null
          }
        }, 1000)
      } catch (e) {
        uni.showToast({ title: '发送失败', icon: 'none' })
      }
    },
    async handleLogin() {
      if (this.showCodeLogin) {
        // 验证码登录
        if (!this.loginForm.phone) {
          uni.showToast({ title: '请输入手机号', icon: 'none' })
          return
        }
        if (!this.loginForm.code || this.loginForm.code.length < 4) {
          uni.showToast({ title: '请输入验证码', icon: 'none' })
          return
        }
        
        this.loading = true
        try {
          const result = await authApi.loginByCode(this.loginForm.phone, this.loginForm.code)
          this.handleLoginSuccess(result)
        } catch (e) {
          uni.showToast({ title: '登录失败', icon: 'none' })
        } finally {
          this.loading = false
        }
      } else {
        // 密码登录
        if (!this.loginForm.account) {
          uni.showToast({ title: '请输入手机号/用户名/邮箱', icon: 'none' })
          return
        }
        if (!this.loginForm.password) {
          uni.showToast({ title: '请输入密码', icon: 'none' })
          return
        }
        
        this.loading = true
        try {
          const input = this.loginForm.account.trim()
          let data
          if (input.includes('@')) {
            data = { email: input, password: this.loginForm.password }
          } else if (/^1[3-9]\d{9}$/.test(input)) {
            data = { username: input, password: this.loginForm.password }
          } else {
            data = { username: input, password: this.loginForm.password }
          }
          const result = await authApi.login(data)
          this.handleLoginSuccess(result)
        } catch (e) {
          uni.showToast({ title: '登录失败', icon: 'none' })
        } finally {
          this.loading = false
        }
      }
    },
    handleLoginSuccess(result) {
      console.log('登录响应:', result)
      const token = result.token || result.data?.token
      const userInfo = result.data || result
      
      if (!token) {
        uni.showToast({ title: '登录失败：未获取到token', icon: 'none' })
        return
      }
      
      uni.setStorageSync('token', token)
      uni.setStorageSync('userInfo', userInfo)
      
      uni.showToast({ title: '登录成功', icon: 'success' })
      
      setTimeout(() => {
        uni.switchTab({ url: '/pages/index/index' })
      }, 1500)
    },
    goToRegister() {
      uni.navigateTo({ url: '/pages/register/register' })
    }
  }
}
</script>

<style scoped>
.page-login {
  min-height: 100vh;
  background: linear-gradient(180deg, #F8F6FF 0%, #FFF 50%);
  padding: 80rpx 48rpx;
}

.logo-area {
  display: flex;
  flex-direction: column;
  align-items: center;
  margin-bottom: 80rpx;
}

.logo {
  width: 160rpx;
  height: 160rpx;
  background: linear-gradient(135deg, #7B68EE, #9D8FFF);
  border-radius: 40rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  box-shadow: 0 16rpx 48rpx rgba(123, 104, 238, 0.3);
}

.logo-icon {
  font-size: 80rpx;
}

.app-name {
  font-size: 48rpx;
  font-weight: 700;
  color: var(--primary-color);
  margin-top: 24rpx;
}

.app-desc {
  font-size: 24rpx;
  color: var(--text-tertiary);
  margin-top: 8rpx;
}

.login-form {
  background: var(--bg-primary);
  border-radius: 32rpx;
  padding: 48rpx 32rpx;
  box-shadow: var(--shadow-medium);
}

.input-group {
  margin-bottom: 24rpx;
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

.login-type-tabs {
  display: flex;
  background: var(--bg-secondary);
  border-radius: 24rpx;
  padding: 8rpx;
  margin-bottom: 40rpx;
}

.tab-item {
  flex: 1;
  text-align: center;
  padding: 20rpx 0;
  border-radius: 20rpx;
  font-size: 28rpx;
  color: var(--text-secondary);
  transition: all 0.3s ease;
}

.tab-item.active {
  background: linear-gradient(135deg, var(--primary-color), var(--primary-dark));
  color: #FFF;
  font-weight: 500;
}

.code-input {
  width: 240rpx;
}

.login-btn {
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

.login-btn[disabled] {
  opacity: 0.6;
}

.register-link {
  display: flex;
  justify-content: center;
  align-items: center;
  margin-top: 32rpx;
}

.link-text {
  font-size: 26rpx;
  color: var(--text-tertiary);
}

.link-btn {
  font-size: 26rpx;
  color: var(--primary-color);
  margin-left: 8rpx;
  font-weight: 500;
}

.third-party {
  margin-top: 80rpx;
}

.divider {
  display: flex;
  align-items: center;
  justify-content: center;
  margin-bottom: 48rpx;
}

.divider-line {
  width: 120rpx;
  height: 1rpx;
  background: var(--border-color);
}

.divider-text {
  font-size: 24rpx;
  color: var(--text-tertiary);
  margin: 0 24rpx;
}

.third-list {
  display: flex;
  justify-content: center;
  gap: 80rpx;
}

.third-item {
  display: flex;
  flex-direction: column;
  align-items: center;
}

.third-icon {
  font-size: 64rpx;
}

.third-label {
  font-size: 24rpx;
  color: var(--text-secondary);
  margin-top: 12rpx;
}
</style>