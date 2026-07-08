<template>
  <view class="page-container">
    <view class="menu-list">
      <view class="menu-item" @click="changePassword">
        <text>修改密码</text><text class="arrow">></text>
      </view>
      <view class="menu-item" @click="clearCache">
        <text>清除缓存</text><text class="arrow">></text>
      </view>
      <view class="menu-item">
        <text>关于</text><text class="arrow">></text>
      </view>
      <view class="menu-item">
        <text>版本</text><text style="color:var(--text-hint);">1.0.0</text>
      </view>
    </view>
  </view>
</template>
<script>
import { userApi } from '../../utils/request.js'
export default {
  methods: {
    changePassword() {
      uni.showModal({
        title: '修改密码', editable: true, placeholderText: '请输入新密码',
        success: async (res) => {
          if (res.confirm && res.content) {
            try { await userApi.changePassword({ password: res.content }); uni.showToast({ title: '修改成功', icon: 'success' }) }
            catch (e) { uni.showToast({ title: e.message, icon: 'none' }) }
          }
        }
      })
    },
    clearCache() {
      uni.clearStorageSync()
      uni.showToast({ title: '缓存已清除', icon: 'success' })
    }
  }
}
</script>
<style scoped>
.menu-list { margin-top: 12px; }
.menu-item { display: flex; justify-content: space-between; align-items: center; padding: 14px 20px; background: #fff; border-bottom: 1px solid var(--border-color); font-size: 15px; }
.arrow { color: var(--text-hint); font-size: 16px; }
</style>
