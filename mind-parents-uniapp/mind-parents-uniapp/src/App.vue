<script>
export default {
  onLaunch() {
    const token = uni.getStorageSync('token')
    if (token) {
      uni.switchTab({ url: '/pages/index/index' })
    } else {
      uni.redirectTo({ url: '/pages/launch/launch' })
    }
  }
}
</script>
<style>
@import './styles/index.css';
</style>
