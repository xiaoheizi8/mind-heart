<template>
  <view class="page-container">
    <view class="card card-sm" style="margin-top:24rpx;">
      <text class="card-title mb-16">通知设置</text>
      <view class="setting-row" v-for="s in settings" :key="s.key">
        <view><text>{{ s.label }}</text><text class="text-sm text-hint" style="display:block;">{{ s.desc }}</text></view>
        <switch :checked="s.value" @change="e => toggleSetting(s, e.detail.value)" color="#7B68EE" />
      </view>
    </view>

    <view class="card card-sm mt-24">
      <text class="card-title mb-16">账号安全</text>
      <view class="menu-item" @click="showPwdModal=true"><text>修改密码</text><text class="menu-arrow">›</text></view>
      <view class="menu-item" @click="clearCache"><text>清除缓存</text><text class="menu-arrow">›</text></view>
    </view>

    <view class="card card-sm mt-24">
      <view class="menu-item"><text>关于我们</text><text class="menu-arrow">›</text></view>
      <view class="menu-item"><text>用户协议</text><text class="menu-arrow">›</text></view>
      <view class="menu-item"><text>隐私政策</text><text class="menu-arrow">›</text></view>
      <view class="menu-item"><text>当前版本</text><text style="color:var(--text-tertiary);">1.0.0</text></view>
    </view>

    <!-- 修改密码弹窗 -->
    <view class="modal-overlay" v-if="showPwdModal" @click="showPwdModal=false">
      <view class="modal-card" @click.stop>
        <text class="card-title">修改密码</text>
        <input class="input mt-16" type="password" v-model="pwdForm.oldPassword" placeholder="当前密码" />
        <input class="input mt-12" type="password" v-model="pwdForm.newPassword" placeholder="新密码(6-20位)" />
        <input class="input mt-12" type="password" v-model="pwdForm.confirm" placeholder="确认新密码" />
        <view class="flex-between mt-24 gap-16">
          <button class="btn btn-ghost btn-block" @click="showPwdModal=false">取消</button>
          <button class="btn btn-primary btn-block" @click="doChangePwd">确认</button>
        </view>
      </view>
    </view>
  </view>
</template>
<script>
import { userApi } from '../../utils/request.js'
export default {
  data() {
    return {
      showPwdModal: false,
      pwdForm: { oldPassword: '', newPassword: '', confirm: '' },
      settings: [
        { key: 'warning', label: '预警提醒', desc: '孩子触发高风险预警时通知', value: true },
        { key: 'daily', label: '日报推送', desc: '每日推送孩子的情绪摘要', value: false }
      ]
    }
  },
  methods: {
    toggleSetting(s, val) { s.value = val; uni.showToast({ title: '已' + (val ? '开启' : '关闭'), icon: 'none' }) },
    async doChangePwd() {
      const { oldPassword, newPassword, confirm } = this.pwdForm
      if (!oldPassword || !newPassword || !confirm) { uni.showToast({ title: '请填写完整', icon: 'none' }); return }
      if (newPassword.length < 6) { uni.showToast({ title: '新密码至少6位', icon: 'none' }); return }
      if (newPassword !== confirm) { uni.showToast({ title: '两次密码不一致', icon: 'none' }); return }
      try {
        await userApi.changePassword({ oldPassword, newPassword })
        uni.showToast({ title: '修改成功', icon: 'success' })
        this.showPwdModal = false; this.pwdForm = { oldPassword: '', newPassword: '', confirm: '' }
      } catch (e) { uni.showToast({ title: e.message, icon: 'none' }) }
    },
    clearCache() { uni.clearStorageSync(); uni.showToast({ title: '缓存已清除', icon: 'success' }) }
  }
}
</script>
<style scoped>
.setting-row { display: flex; align-items: center; justify-content: space-between; padding: 16rpx 0; border-bottom: 1rpx solid var(--border-light); }
.setting-row:last-child { border-bottom: none; }
.menu-item { display: flex; align-items: center; justify-content: space-between; padding: 20rpx 0; border-bottom: 1rpx solid var(--border-light); }
.menu-item:last-child { border-bottom: none; }
.menu-arrow { font-size: 28rpx; color: var(--text-disabled); }
.modal-overlay { position: fixed; top: 0; left: 0; right: 0; bottom: 0; background: rgba(0,0,0,0.5); display: flex; align-items: center; justify-content: center; z-index: 1000; padding: 48rpx; }
.modal-card { background: #fff; border-radius: var(--radius-lg); padding: var(--spacing-xl); width: 100%; max-width: 600rpx; }
</style>
