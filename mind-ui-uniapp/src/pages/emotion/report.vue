<template>
  <view class="page-emotion-report">
    <!-- 时间选择 -->
    <view class="time-selector">
      <view 
        v-for="period in periods" 
        :key="period.value"
        class="period-item"
        :class="{ active: currentPeriod === period.value }"
        @click="changePeriod(period.value)"
      >
        {{ period.label }}
      </view>
    </view>

    <!-- 情绪气象图 -->
    <view class="overview-card card">
      <view class="card-title">情绪气象图</view>
      <view class="emotion-chart">
        <canvas canvas-id="emotionCanvas" id="emotionCanvas" class="chart-canvas"></canvas>
      </view>
      
      <view class="emotion-stats">
        <view class="stat-item">
          <text class="stat-value">{{ overview.avgScore }}</text>
          <text class="stat-label">平均情绪值</text>
        </view>
        <view class="stat-item">
          <text class="stat-value">{{ overview.diaryCount }}</text>
          <text class="stat-label">记录天数</text>
        </view>
        <view class="stat-item">
          <text class="stat-value">{{ overview.dominantEmotion }}</text>
          <text class="stat-label">主要情绪</text>
        </view>
      </view>
    </view>

    <!-- 情绪分布 -->
    <view class="distribution-card card">
      <view class="card-title">情绪分布</view>
      <view class="distribution-list">
        <view v-for="item in emotionDist" :key="item.category" class="dist-item">
          <view class="dist-info">
            <text class="dist-emoji">{{ item.icon }}</text>
            <text class="dist-label">{{ item.category }}</text>
          </view>
          <view class="dist-bar-wrapper">
            <view class="dist-bar" :style="{ width: item.percent + '%' }"></view>
          </view>
          <text class="dist-percent">{{ item.percent }}%</text>
        </view>
      </view>
    </view>

    <!-- AI分析报告 -->
    <view class="analysis-card card">
      <view class="card-title">✨ AI情绪洞察</view>
      <view class="analysis-content">
        <text class="analysis-text">{{ analysisReport }}</text>
      </view>
    </view>

    <!-- 趋势与预测 -->
    <view class="insight-cards">
      <!-- 趋势分析 -->
      <view class="insight-card card" v-if="trendAnalysis">
        <view class="insight-header">
          <text class="insight-icon">{{ trendAnalysis.trendType === 'improving' ? '📈' : (trendAnalysis.trendType === 'declining' ? '📉' : '➡️') }}</text>
          <text class="insight-title">趋势分析</text>
        </view>
        <view class="insight-badge" :class="trendAnalysis.trendType">
          {{ trendAnalysis.trendType === 'improving' ? '上升中 ↑' : (trendAnalysis.trendType === 'declining' ? '下降中 ↓' : '平稳 →') }}
        </view>
        <text class="insight-desc">{{ trendAnalysis.description }}</text>
      </view>

      <!-- 情绪预测 -->
      <view class="insight-card card" v-if="prediction">
        <view class="insight-header">
          <text class="insight-icon">🔮</text>
          <text class="insight-title">情绪预测</text>
        </view>
        <view class="prediction-score">
          <text class="score-num">{{ Math.round(prediction.predictedScore * 100) }}</text>
          <text class="score-unit">分</text>
        </view>
        <view class="confidence-bar">
          <view class="confidence-fill" :style="{ width: (prediction.confidence * 100) + '%' }"></view>
        </view>
        <text class="insight-desc">{{ prediction.outlook }}</text>
      </view>
    </view>

    <!-- 标签关联 -->
    <view class="correlation-card card" v-if="correlation && correlation.tagCorrelation">
      <view class="card-title">🏷️ 情绪标签关联</view>
      <view class="correlation-list" v-if="Object.keys(correlation.tagCorrelation).length > 0">
        <view v-for="(score, tag) in correlation.tagCorrelation" :key="tag" class="correlation-item">
          <view class="correlation-info">
            <text class="correlation-tag">{{ tag }}</text>
            <text class="correlation-score">{{ Math.round(score * 100) }}分</text>
          </view>
          <view class="correlation-bar">
            <view class="correlation-fill" :style="{ width: (score * 100) + '%', background: getScoreColor(score) }"></view>
          </view>
        </view>
      </view>
      <view class="empty-correlation" v-else>
        <text>暂无标签数据，多记录情绪标签有助于分析</text>
      </view>
    </view>

    <!-- 建议 -->
    <view class="suggestion-card card">
      <view class="card-title">建议</view>
      <view class="suggestion-list">
        <view v-for="(item, index) in suggestions" :key="index" class="suggestion-item">
          <text class="suggestion-num">{{ index + 1 }}</text>
          <text class="suggestion-text">{{ item }}</text>
        </view>
      </view>
    </view>

    <!-- 生成报告 -->
    <view class="export-section">
      <button class="export-btn" @click="generateCurrentReport">
        <text class="export-icon">📊</text>
        <text>生成{{ currentPeriod === 'week' ? '周' : (currentPeriod === 'month' ? '月' : '季度') }}报告</text>
      </button>
    </view>
  </view>
</template>

<script>
import { diaryApi } from '../../utils/request.js'

const EMOTION_MAP = {
  'happy': { icon: '😊', color: '#4CAF50', label: '开心' },
  'sad': { icon: '😢', color: '#2196F3', label: '难过' },
  'angry': { icon: '😠', color: '#F44336', label: '愤怒' },
  'anxious': { icon: '😰', color: '#FF9800', label: '焦虑' },
  'calm': { icon: '😌', color: '#9C27B0', label: '平静' },
  'fear': { icon: '😨', color: '#607D8B', label: '害怕' }
}

const SCORE_MAP = { 'happy': 90, 'calm': 75, 'anxious': 40, 'sad': 30, 'angry': 25, 'fear': 20 }

export default {
  data() {
    return {
      currentPeriod: 'week',
      periods: [
        { value: 'week', label: '本周' },
        { value: 'month', label: '本月' },
        { value: 'quarter', label: '本季度' }
      ],
      trendData: [],
      trendAnalysis: null,
      prediction: null,
      correlation: null,
      overview: {
        avgScore: '--',
        diaryCount: 0,
        dominantEmotion: '--'
      },
      emotionDist: [],
      analysisReport: '',
      suggestions: []
    }
  },
  onLoad() {
    this.loadReport()
  },
  methods: {
    changePeriod(period) {
      this.currentPeriod = period
      this.loadReport()
    },
    async loadReport() {
      try {
        const days = this.currentPeriod === 'week' ? 7 : (this.currentPeriod === 'month' ? 30 : 90)
        const res = await diaryApi.getReport({ period: this.currentPeriod })
        const data = res?.data || res
        
        const trendData = data.trendData || []
        this.trendData = trendData
        
        if (trendData.length > 0) {
          const scores = trendData.map(d => d.score || SCORE_MAP[d.category] || 50)
          const avg = scores.reduce((a, b) => a + b, 0) / scores.length
          this.overview.avgScore = Math.round(avg)
          
          const categoryCount = {}
          trendData.forEach(d => {
            const cat = d.category || 'calm'
            categoryCount[cat] = (categoryCount[cat] || 0) + 1
          })
          let maxCat = 'calm', maxCount = 0
          Object.keys(categoryCount).forEach(cat => {
            if (categoryCount[cat] > maxCount) {
              maxCount = categoryCount[cat]
              maxCat = cat
            }
          })
          this.overview.dominantEmotion = EMOTION_MAP[maxCat]?.label || '平静'
        } else {
          this.overview.avgScore = '--'
          this.overview.dominantEmotion = '--'
        }
        
        this.overview.diaryCount = data.totalDiaries || trendData.length
        
        const distMap = {}
        trendData.forEach(d => {
          const cat = d.category || 'calm'
          distMap[cat] = (distMap[cat] || 0) + 1
        })
        const total = trendData.length || 1
        this.emotionDist = Object.keys(distMap).map(cat => ({
          category: EMOTION_MAP[cat]?.label || cat,
          icon: EMOTION_MAP[cat]?.icon || '😌',
          percent: Math.round((distMap[cat] / total) * 100)
        })).sort((a, b) => b.percent - a.percent)
        
        // 使用后端返回的分析数据
        this.analysisReport = data.aiReport || this.generateAnalysis(trendData)
        this.suggestions = data.suggestions || this.generateSuggestions(trendData)
        
        // 存储趋势分析用于展示
        this.trendAnalysis = data.trendAnalysis
        this.prediction = data.prediction
        this.correlation = data.correlation
        
        this.$nextTick(() => this.drawChart())
      } catch (e) {
        console.error('加载报告失败:', e)
        this.overview = { avgScore: '--', diaryCount: 0, dominantEmotion: '--' }
        this.emotionDist = []
        this.analysisReport = '还没有足够的日记数据，开始记录你的情绪吧~'
        this.suggestions = ['今天可以写下你的第一篇日记', '试着记录每天的感受']
      }
    },
    generateAnalysis(trendData) {
      if (!trendData.length) return '还没有足够的日记数据，开始记录你的情绪吧~'
      
      const scores = trendData.map(d => d.score || SCORE_MAP[d.category] || 50)
      const avg = scores.reduce((a, b) => a + b, 0) / scores.length
      
      let text = ''
      if (avg >= 70) {
        text = '这个时期你的情绪状态很不错呢！大部分时间都保持着积极乐观的心态。'
      } else if (avg >= 50) {
        text = '你的情绪整体平稳，偶尔有些波动这是很正常的。学会与情绪共处本身就是一种成长。'
      } else {
        text = '最近似乎有些艰难，但我看到了你愿意面对的勇气。每一段低谷都是成长的开始。'
      }
      
      if (trendData.length >= 7) {
        const first = scores.slice(0, Math.ceil(scores.length / 2)).reduce((a, b) => a + b, 0) / Math.ceil(scores.length)
        const second = scores.slice(Math.ceil(scores.length / 2)).reduce((a, b) => a + b, 0) / Math.floor(scores.length / 2 || 1)
        if (second - first > 10) {
          text += ' 你的情绪正在慢慢变好，继续保持哦~'
        } else if (first - second > 10) {
          text += ' 最近要注意照顾好自己，需要倾诉时我一直在。'
        }
      }
      
      return text
    },
    generateSuggestions(trendData) {
      const suggestions = []
      const categories = trendData.map(d => d.category)
      
      if (categories.includes('anxious')) {
        suggestions.push('焦虑时深呼吸，告诉自己：此刻的我，已经很棒了')
      }
      if (categories.includes('sad')) {
        suggestions.push('难过时允许自己脆弱，这是人之常情')
      }
      if (categories.includes('angry')) {
        suggestions.push('生气时可以暂时离开现场，给自己冷静的空间')
      }
      if (categories.includes('calm') || categories.includes('happy')) {
        suggestions.push('记得保持这份平和，记录下让你感到平静的时刻')
      }
      
      if (suggestions.length === 0) {
        suggestions.push('坚持记录日记，了解自己的情绪模式', '每天给自己一些安静的时间很重要')
      }
      
      return suggestions.slice(0, 3)
    },
    drawChart() {
      const canvas = uni.createCanvasContext('emotionCanvas', this)
      const width = 320
      const height = 140
      const padding = 20
      
      canvas.setFillStyle('#F8F6FF')
      canvas.fillRect(0, 0, width, height)
      
      if (!this.trendData || this.trendData.length === 0) {
        canvas.setFillStyle('#999')
        canvas.setFontSize(12)
        canvas.fillText('开始记录日记，绘制你的情绪曲线', width / 2 - 70, height / 2)
        canvas.draw()
        return
      }
      
      const data = this.trendData.map(d => d.score || SCORE_MAP[d.category] || 50)
      const max = Math.max(...data, 100)
      const min = Math.min(...data, 0)
      const range = max - min || 1
      
      const points = data.map((val, i) => ({
        x: padding + (i / (data.length - 1 || 1)) * (width - padding * 2),
        y: height - padding - ((val - min) / range) * (height - padding * 2)
      }))
      
      canvas.setStrokeStyle('#9D8FFF')
      canvas.setLineWidth(2)
      canvas.beginPath()
      points.forEach((p, i) => {
        if (i === 0) canvas.moveTo(p.x, p.y)
        else canvas.lineTo(p.x, p.y)
      })
      canvas.stroke()
      
      canvas.setFillStyle('rgba(157, 143, 255, 0.2)')
      canvas.beginPath()
      points.forEach((p, i) => {
        if (i === 0) canvas.moveTo(p.x, p.y)
        else canvas.lineTo(p.x, p.y)
      })
      canvas.lineTo(points[points.length - 1].x, height - padding)
      canvas.lineTo(points[0].x, height - padding)
      canvas.closePath()
      canvas.fill()
      
      points.forEach((p, i) => {
        canvas.setFillStyle('#7B68EE')
        canvas.beginPath()
        canvas.arc(p.x, p.y, 4, 0, 2 * Math.PI)
        canvas.fill()
      })
      
      canvas.draw()
    },
    getScoreColor(score) {
      if (score >= 0.7) return '#4CAF50'
      if (score >= 0.5) return '#9C27B0'
      if (score >= 0.3) return '#FF9800'
      return '#F44336'
    },
    generateCurrentReport() {
      const reportType = this.currentPeriod
      
      uni.showLoading({ title: '正在生成报告...' })
      
      diaryApi.generateReport(reportType)
        .then(res => {
          uni.hideLoading()
          if (res.code === 200) {
            uni.showToast({ title: '报告已生成', icon: 'success' })
            // 重新加载报告数据
            this.loadReport()
          } else {
            uni.showToast({ title: res.message || '生成失败', icon: 'none' })
          }
        })
        .catch(err => {
          uni.hideLoading()
          console.error('生成报告失败:', err)
          uni.showToast({ title: '生成失败', icon: 'none' })
        })
    }
  }
}
</script>

<style scoped>
.page-emotion-report {
  min-height: 100vh;
  background: var(--bg-secondary);
  padding: 24rpx;
  padding-bottom: 160rpx;
}

.time-selector {
  display: flex;
  justify-content: center;
  gap: 24rpx;
  margin-bottom: 24rpx;
}

.period-item {
  padding: 16rpx 32rpx;
  background: var(--bg-primary);
  border-radius: 32rpx;
  font-size: 28rpx;
  color: var(--text-secondary);
}

.period-item.active {
  background: var(--primary-color);
  color: #FFF;
}

.card {
  background: var(--bg-primary);
  border-radius: 24rpx;
  padding: 32rpx;
  margin-bottom: 24rpx;
}

.card-title {
  font-size: 32rpx;
  font-weight: 600;
  color: var(--text-primary);
  margin-bottom: 24rpx;
}

.emotion-chart {
  height: 280rpx;
  margin-bottom: 32rpx;
}

.chart-canvas {
  width: 100%;
  height: 100%;
}

.emotion-stats {
  display: flex;
  justify-content: space-around;
}

.stat-item {
  display: flex;
  flex-direction: column;
  align-items: center;
}

.stat-value {
  font-size: 36rpx;
  font-weight: 600;
  color: var(--primary-color);
}

.stat-label {
  font-size: 24rpx;
  color: var(--text-tertiary);
  margin-top: 8rpx;
}

.distribution-list {
  display: flex;
  flex-direction: column;
  gap: 24rpx;
}

.dist-item {
  display: flex;
  align-items: center;
}

.dist-info {
  display: flex;
  align-items: center;
  width: 160rpx;
}

.dist-emoji {
  font-size: 32rpx;
  margin-right: 12rpx;
}

.dist-label {
  font-size: 26rpx;
  color: var(--text-secondary);
}

.dist-bar-wrapper {
  flex: 1;
  height: 16rpx;
  background: var(--bg-tertiary);
  border-radius: 8rpx;
  margin: 0 16rpx;
  overflow: hidden;
}

.dist-bar {
  height: 100%;
  background: linear-gradient(90deg, var(--primary-color), var(--primary-light));
  border-radius: 8rpx;
  transition: width 0.3s ease;
}

.dist-percent {
  font-size: 24rpx;
  color: var(--text-tertiary);
  width: 60rpx;
  text-align: right;
}

.analysis-content {
  padding: 24rpx;
  background: var(--bg-secondary);
  border-radius: 16rpx;
}

.analysis-text {
  font-size: 28rpx;
  line-height: 1.8;
  color: var(--text-secondary);
}

.suggestion-list {
  display: flex;
  flex-direction: column;
  gap: 24rpx;
}

.suggestion-item {
  display: flex;
  align-items: flex-start;
}

.suggestion-num {
  width: 48rpx;
  height: 48rpx;
  background: var(--primary-color);
  color: #FFF;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 24rpx;
  margin-right: 16rpx;
  flex-shrink: 0;
}

.suggestion-text {
  flex: 1;
  font-size: 28rpx;
  line-height: 1.6;
  color: var(--text-secondary);
}

.export-section {
  padding: 24rpx 0;
}

.export-btn {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 16rpx;
  width: 100%;
  height: 96rpx;
  background: var(--bg-primary);
  border-radius: 48rpx;
  font-size: 30rpx;
  color: var(--primary-color);
  border: 2rpx solid var(--primary-color);
}

.export-icon {
  font-size: 32rpx;
}

/* 趋势与预测卡片 */
.insight-cards {
  display: flex;
  gap: 24rpx;
  margin-bottom: 24rpx;
}

.insight-card {
  flex: 1;
  text-align: center;
}

.insight-header {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 12rpx;
  margin-bottom: 16rpx;
}

.insight-icon {
  font-size: 36rpx;
}

.insight-title {
  font-size: 28rpx;
  color: var(--text-secondary);
}

.insight-badge {
  display: inline-block;
  padding: 8rpx 24rpx;
  border-radius: 24rpx;
  font-size: 24rpx;
  margin-bottom: 12rpx;
}

.insight-badge.improving {
  background: rgba(76, 175, 80, 0.15);
  color: #4CAF50;
}

.insight-badge.declining {
  background: rgba(244, 67, 54, 0.15);
  color: #F44336;
}

.insight-badge.stable {
  background: rgba(156, 39, 176, 0.15);
  color: #9C27B0;
}

.insight-desc {
  font-size: 24rpx;
  color: var(--text-tertiary);
  line-height: 1.5;
}

.prediction-score {
  display: flex;
  align-items: baseline;
  justify-content: center;
  margin-bottom: 16rpx;
}

.score-num {
  font-size: 56rpx;
  font-weight: 600;
  color: var(--primary-color);
}

.score-unit {
  font-size: 28rpx;
  color: var(--text-secondary);
  margin-left: 4rpx;
}

.confidence-bar {
  height: 8rpx;
  background: var(--bg-tertiary);
  border-radius: 4rpx;
  margin-bottom: 12rpx;
  overflow: hidden;
}

.confidence-fill {
  height: 100%;
  background: var(--primary-color);
  border-radius: 4rpx;
}

/* 标签关联 */
.correlation-list {
  display: flex;
  flex-direction: column;
  gap: 20rpx;
}

.correlation-item {
  display: flex;
  flex-direction: column;
  gap: 8rpx;
}

.correlation-info {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.correlation-tag {
  font-size: 26rpx;
  color: var(--text-primary);
}

.correlation-score {
  font-size: 24rpx;
  color: var(--text-secondary);
}

.correlation-bar {
  height: 12rpx;
  background: var(--bg-tertiary);
  border-radius: 6rpx;
  overflow: hidden;
}

.correlation-fill {
  height: 100%;
  border-radius: 6rpx;
  transition: width 0.3s ease;
}

.empty-correlation {
  text-align: center;
  padding: 32rpx;
  color: var(--text-tertiary);
  font-size: 26rpx;
}
</style>