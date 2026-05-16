<template>
  <view class="empty-state" :class="[size]">
    <image v-if="image" :src="image" class="empty-image" mode="aspectFit" />
    <text v-else class="empty-icon">{{ icon }}</text>
    <text class="empty-text">{{ text }}</text>
    <text v-if="desc" class="empty-desc">{{ desc }}</text>
    <view v-if="$slots.extra || extraText" class="empty-extra">
      <slot name="extra"></slot>
      <button v-if="extraText" class="extra-btn" @click="handleClick">{{ extraText }}</button>
    </view>
  </view>
</template>

<script>
export default {
  name: 'EmptyState',
  props: {
    icon: { type: String, default: '📭' },
    image: { type: String, default: '' },
    text: { type: String, default: '暂无数据' },
    desc: { type: String, default: '' },
    extraText: { type: String, default: '' },
    size: { type: String, default: 'default' }
  },
  methods: {
    handleClick() {
      this.$emit('click')
    }
  }
}
</script>

<style scoped>
.empty-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 80rpx 32rpx;
}

.empty-state.small {
  padding: 40rpx 32rpx;
}

.empty-state.small .empty-icon {
  font-size: 64rpx;
}

.empty-state.small .empty-text {
  font-size: 26rpx;
}

.empty-image {
  width: 200rpx;
  height: 200rpx;
}

.empty-icon {
  font-size: 96rpx;
  margin-bottom: 24rpx;
}

.empty-text {
  font-size: 28rpx;
  color: var(--text-secondary);
  margin-top: 16rpx;
}

.empty-desc {
  font-size: 24rpx;
  color: var(--text-tertiary);
  margin-top: 8rpx;
}

.empty-extra {
  margin-top: 32rpx;
}

.extra-btn {
  padding: 16rpx 48rpx;
  background: var(--primary-color);
  border-radius: 32rpx;
  color: #FFF;
  font-size: 28rpx;
}
</style>