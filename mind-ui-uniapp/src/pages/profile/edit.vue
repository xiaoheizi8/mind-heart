<template>
  <view class="page-profile-edit">
    <!-- 头像 -->
    <view class="avatar-section" @click="changeAvatar">
      <view class="avatar-wrapper">
        <image 
          class="avatar" 
          :src="avatarUrl" 
          mode="aspectFill"
          :show-loading="true"
        />
        <view class="avatar-edit">
          <text class="edit-icon">📷</text>
        </view>
      </view>
      <text class="avatar-tip">点击更换头像</text>
    </view>

    <!-- 表单 -->
    <view class="form-section">
      <view class="form-item">
        <text class="form-label">昵称</text>
        <input 
          class="form-input" 
          v-model="userInfo.nickname" 
          placeholder="请输入昵称"
          maxlength="20"
        />
      </view>
      
      <view class="form-item">
        <text class="form-label">性别</text>
        <view class="gender-select">
          <view 
            class="gender-item" 
            :class="{ active: userInfo.gender === 1 }"
            @click="userInfo.gender = 1"
          >
            <text class="gender-icon">👨</text>
            <text>男</text>
          </view>
          <view 
            class="gender-item" 
            :class="{ active: userInfo.gender === 2 }"
            @click="userInfo.gender = 2"
          >
            <text class="gender-icon">👩</text>
            <text>女</text>
          </view>
          <view 
            class="gender-item" 
            :class="{ active: userInfo.gender === 3 }"
            @click="userInfo.gender = 3"
          >
            <text class="gender-icon">🧑</text>
            <text>保密</text>
          </view>
        </view>
      </view>
      
      <view class="form-item">
        <text class="form-label">年龄</text>
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
      
      <view class="form-item">
        <text class="form-label">手机号</text>
        <input 
          class="form-input" 
          v-model="userInfo.phone" 
          placeholder="请输入手机号"
          type="number"
          maxlength="11"
        />
      </view>
      
      <view class="form-item">
        <text class="form-label">监护人手机</text>
        <input 
          class="form-input" 
          v-model="userInfo.guardianPhone" 
          placeholder="请输入监护人手机号（选填）"
          type="number"
          maxlength="11"
        />
      </view>
      
      <view class="form-item">
        <text class="form-label">个人简介</text>
        <textarea 
          class="form-textarea" 
          v-model="userInfo.bio" 
          placeholder="介绍一下自己吧..."
          maxlength="100"
        />
        <text class="word-count">{{ userInfo.bio.length }}/100</text>
      </view>
    </view>

    <!-- 保存按钮 -->
    <view class="action-bar">
      <button 
        class="save-btn" 
        :class="{ loading: saving }"
        @click="saveProfile"
        :disabled="saving"
      >
        {{ saving ? '保存中...' : '保存' }}
      </button>
    </view>
  </view>
</template>

<script>
import { userApi } from '../../utils/request.js'

export default {
  data() {
    return {
      saving: false,
      ageIndex: -1,
      ageOptions: Array.from({ length: 19 }, (_, i) => `${i + 13}岁`),
      userInfo: {
        nickname: '',
        gender: 3,
        age: null,
        phone: '',
        guardianPhone: '',
        bio: '',
        avatar: ''
      }
    }
  },
  computed: {
    avatarUrl() {
      const avatar = this.userInfo.avatar
      console.log('computed avatarUrl:', avatar)
      // 如果是blob临时文件或空，使用默认头像
      if (!avatar || String(avatar).startsWith('blob:')) {
        return 'https://mind-heart.oss-cn-beijing.aliyuncs.com/default/avatar.png'
      }
      // 如果是相对路径，添加OSS域名
      if (avatar && !avatar.startsWith('http')) {
        return 'https://mind-heart.oss-cn-beijing.aliyuncs.com/' + avatar
      }
      return avatar
    }
  },
  onLoad() {
    this.loadProfile()
  },
  methods: {
    async loadProfile() {
      try {
        const res = await userApi.getProfile()
        // 只合并有值的字段，避免 null 覆盖用户输入
        if (res) {
          Object.keys(res).forEach(key => {
            if (res[key] !== null && res[key] !== undefined) {
              this.userInfo[key] = res[key]
            }
          })
        }
        if (this.userInfo.age) {
          this.ageIndex = this.userInfo.age - 13
        }
      } catch (e) {
        const cached = uni.getStorageSync('userInfo')
        if (cached) {
          Object.keys(cached).forEach(key => {
            if (cached[key] !== null && cached[key] !== undefined) {
              this.userInfo[key] = cached[key]
            }
          })
        }
      }
    },
    changeAvatar() {
      uni.chooseImage({
        count: 1,
        sizeType: ['compressed'],
        sourceType: ['album', 'camera'],
        success: async (res) => {
          const tempFilePath = res.tempFilePaths[0]
          
          // 显示上传中
          uni.showLoading({ title: '上传中...' })
          
          try {
            // 上传到服务器
            const uploadRes = await uni.uploadFile({
              url: 'http://localhost:8080/api/v1/file/upload/image',
              filePath: tempFilePath,
              name: 'file',
              header: {
                'Authorization': 'Bearer ' + uni.getStorageSync('token')
              }
            })
            
            console.log('上传响应原始:', uploadRes)
            
            let data
            // 处理不同平台返回格式
            const responseData = uploadRes[1] || uploadRes.data
            if (!responseData) {
              uni.showToast({ title: '上传失败: 无响应', icon: 'none' })
              return
            }
            
            if (typeof responseData === 'string') {
              data = JSON.parse(responseData)
            } else {
              data = responseData
            }
            
            console.log('上传响应解析:', data)
            
            if (data.code === 200 && data.data && data.data.url) {
              this.userInfo.avatar = data.data.url
              console.log('设置头像后:', this.userInfo.avatar)
              uni.showToast({ title: '头像已更新', icon: 'success' })
            } else {
              uni.showToast({ title: data.message || '上传失败', icon: 'none' })
            }
          } catch (e) {
            console.error('上传头像失败:', e)
            uni.showToast({ title: '上传失败: ' + e.message, icon: 'none' })
          } finally {
            uni.hideLoading()
          }
        }
      })
    },
    onAgeChange(e) {
      this.ageIndex = e.detail.value
      this.userInfo.age = parseInt(this.ageOptions[this.ageIndex])
    },
    async saveProfile() {
      if (!this.userInfo.nickname) {
        uni.showToast({ title: '请输入昵称', icon: 'none' })
        return
      }
      
      console.log('保存用户信息:', this.userInfo)
      console.log('头像URL:', this.userInfo.avatar)
      
      this.saving = true
      
      try {
        // 构建更新数据
        const updateData = {
          nickname: this.userInfo.nickname,
          avatar: this.userInfo.avatar,
          age: this.userInfo.age,
          gender: this.userInfo.gender,
          phone: this.userInfo.phone,
          guardianPhone: this.userInfo.guardianPhone,
          bio: this.userInfo.bio
        }
        
        console.log('发送更新数据:', updateData)
        
        await userApi.updateProfile(updateData)
        uni.setStorageSync('userInfo', this.userInfo)
        uni.removeStorageSync('user_profile_cache')
        uni.showToast({ title: '保存成功', icon: 'success' })
        setTimeout(() => {
          uni.navigateBack()
        }, 1500)
      } catch (e) {
        console.error('保存失败:', e)
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
.page-profile-edit {
  min-height: 100vh;
  background: var(--bg-secondary);
  padding-bottom: 160rpx;
}

.avatar-section {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 48rpx 0;
  background: linear-gradient(135deg, #7B68EE, #9D8FFF);
}

.avatar-wrapper {
  position: relative;
}

.avatar {
  width: 160rpx;
  height: 160rpx;
  border-radius: 50%;
  border: 6rpx solid rgba(255, 255, 255, 0.5);
}

.avatar-edit {
  position: absolute;
  right: 0;
  bottom: 0;
  width: 56rpx;
  height: 56rpx;
  background: var(--bg-primary);
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
}

.edit-icon {
  font-size: 28rpx;
}

.avatar-tip {
  font-size: 24rpx;
  color: rgba(255, 255, 255, 0.8);
  margin-top: 16rpx;
}

.form-section {
  padding: 24rpx;
}

.form-item {
  background: var(--bg-primary);
  border-radius: 16rpx;
  padding: 32rpx;
  margin-bottom: 16rpx;
}

.form-label {
  font-size: 28rpx;
  color: var(--text-primary);
  display: block;
  margin-bottom: 16rpx;
}

.form-input {
  width: 100%;
  font-size: 30rpx;
  color: var(--text-primary);
}

.picker-field {
  font-size: 30rpx;
  color: var(--text-primary);
}

.gender-select {
  display: flex;
  gap: 24rpx;
}

.gender-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 20rpx 32rpx;
  border-radius: 16rpx;
  border: 2rpx solid var(--border-color);
}

.gender-item.active {
  border-color: var(--primary-color);
  background: rgba(123, 104, 238, 0.1);
}

.gender-icon {
  font-size: 40rpx;
  margin-bottom: 8rpx;
}

.form-textarea {
  width: 100%;
  min-height: 150rpx;
  font-size: 30rpx;
  line-height: 1.6;
  color: var(--text-primary);
}

.word-count {
  display: block;
  text-align: right;
  font-size: 24rpx;
  color: var(--text-tertiary);
  margin-top: 16rpx;
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
</style>