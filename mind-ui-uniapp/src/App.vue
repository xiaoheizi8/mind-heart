<script>
export default {
  onLaunch: function() {
    console.log('App Launch')
    // 跳转到启动页
    uni.reLaunch({ url: '/pages/launch/launch' })
  },
  onShow: function() {
    console.log('App Show')
  },
  onHide: function() {
    console.log('App Hide')
  }
}
</script>

<style>
@import './styles/index.css';
</style>