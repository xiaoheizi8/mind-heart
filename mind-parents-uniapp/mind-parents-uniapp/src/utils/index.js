// ======== 工具函数 & 常量 ========

/** 情感映射表 */
export const EMOTION_MAP = {
  happy: { label: '开心', icon: '😊', color: '#FFD700' },
  calm: { label: '平静', icon: '😌', color: '#87CEEB' },
  sad: { label: '难过', icon: '😢', color: '#6B8E9F' },
  anxious: { label: '焦虑', icon: '😰', color: '#FF7F50' },
  angry: { label: '生气', icon: '😠', color: '#FF4500' },
  fear: { label: '害怕', icon: '😨', color: '#9370DB' }
}

/** 情绪标签名 */
export function getEmotionLabel(cat) { return EMOTION_MAP[cat]?.label || '未知' }
/** 情绪图标 */
export function getEmotionIcon(cat) { return EMOTION_MAP[cat]?.icon || '❓' }
/** 情绪颜色 */
export function getEmotionColor(cat) { return EMOTION_MAP[cat]?.color || '#999' }

/** 风险等级映射 */
export const RISK_MAP = {
  1: { label: '低风险', icon: '🟢', color: '#52C41A' },
  2: { label: '中风险', icon: '🟡', color: '#FAAD14' },
  3: { label: '高风险', icon: '🔴', color: '#FF4D4F' }
}

/** 风险等级标签 */
export function getRiskLabel(level) { return RISK_MAP[level]?.label || '未知' }
export function getRiskIcon(level) { return RISK_MAP[level]?.icon || '⚪' }

/** 日期格式化 */
export function formatDate(dateStr, fmt = 'yyyy-MM-dd HH:mm') {
  if (!dateStr) return ''
  const d = new Date(dateStr)
  if (isNaN(d.getTime())) return dateStr
  const pad = n => String(n).padStart(2, '0')
  return fmt.replace('yyyy', d.getFullYear())
    .replace('MM', pad(d.getMonth() + 1))
    .replace('dd', pad(d.getDate()))
    .replace('HH', pad(d.getHours()))
    .replace('mm', pad(d.getMinutes()))
}

/** 相对时间 */
export function formatRelativeTime(dateStr) {
  if (!dateStr) return ''
  const now = Date.now()
  const d = new Date(dateStr).getTime()
  if (isNaN(d)) return dateStr
  const diff = Math.floor((now - d) / 1000)
  if (diff < 60) return '刚刚'
  if (diff < 3600) return Math.floor(diff / 60) + '分钟前'
  if (diff < 86400) return Math.floor(diff / 3600) + '小时前'
  if (diff < 604800) return Math.floor(diff / 86400) + '天前'
  return formatDate(dateStr, 'yyyy-MM-dd')
}

/** 星期几 */
export function getDayOfWeek(dateStr) {
  const days = ['日', '一', '二', '三', '四', '五', '六']
  const d = new Date(dateStr)
  return '周' + days[d.getDay()]
}

/** 手机号脱敏 */
export function maskPhone(phone) {
  if (!phone) return ''
  return phone.replace(/(\d{3})\d{4}(\d{4})/, '$1****$2')
}

/** 绘制Canvas折线图 (复用主应用逻辑) */
export function drawChart(canvasId, data, width, height) {
  const ctx = uni.createCanvasContext(canvasId)
  if (!data || data.length === 0) {
    ctx.setFillStyle('#999')
    ctx.setFontSize(14)
    ctx.fillText('暂无数据', width / 2 - 28, height / 2)
    ctx.draw()
    return
  }

  // 背景
  ctx.setFillStyle('#F8F6FF')
  ctx.fillRect(0, 0, width, height)

  const padding = { top: 12, right: 12, bottom: 24, left: 12 }
  const chartW = width - padding.left - padding.right
  const chartH = height - padding.top - padding.bottom

  // 计算范围
  const scores = data.map(d => d.score)
  const minScore = Math.min(...scores, -1)
  const maxScore = Math.max(...scores, 1)
  const range = maxScore - minScore || 1

  // 绘制面积
  ctx.beginPath()
  data.forEach((d, i) => {
    const x = padding.left + (i / Math.max(data.length - 1, 1)) * chartW
    const y = padding.top + ((maxScore - d.score) / range) * chartH
    if (i === 0) ctx.moveTo(x, y)
    else ctx.lineTo(x, y)
  })
  const lastX = padding.left + chartW
  ctx.lineTo(lastX, padding.top + chartH)
  ctx.lineTo(padding.left, padding.top + chartH)
  ctx.closePath()
  ctx.setFillStyle('rgba(157,143,255,0.15)')
  ctx.fill()

  // 绘制线条
  ctx.beginPath()
  data.forEach((d, i) => {
    const x = padding.left + (i / Math.max(data.length - 1, 1)) * chartW
    const y = padding.top + ((maxScore - d.score) / range) * chartH
    if (i === 0) ctx.moveTo(x, y)
    else ctx.lineTo(x, y)
  })
  ctx.setStrokeStyle('#9D8FFF')
  ctx.setLineWidth(2)
  ctx.stroke()

  // 绘制数据点
  data.forEach((d, i) => {
    const x = padding.left + (i / Math.max(data.length - 1, 1)) * chartW
    const y = padding.top + ((maxScore - d.score) / range) * chartH
    ctx.beginPath()
    ctx.arc(x, y, 3, 0, 2 * Math.PI)
    ctx.setFillStyle('#7B68EE')
    ctx.fill()
  })

  // 零线参考
  const zeroY = padding.top + ((maxScore - 0) / range) * chartH
  ctx.beginPath()
  ctx.moveTo(padding.left, zeroY)
  ctx.lineTo(padding.left + chartW, zeroY)
  ctx.setStrokeStyle('#E5E5E5')
  ctx.setLineWidth(1)
  ctx.setLineDash([4, 4])
  ctx.stroke()

  // 日期标签
  ctx.setFillStyle('#999')
  ctx.setFontSize(10)
  if (data.length > 0) {
    ctx.fillText(data[0].date || '', padding.left, height - 4)
    if (data.length > 1) {
      const last = data[data.length - 1]
      const text = last.date || ''
      const tw = ctx.measureText(text).width
      ctx.fillText(text, padding.left + chartW - tw, height - 4)
    }
  }

  ctx.draw()
}

/** Canvas饼图/环形图 */
export function drawPieChart(canvasId, items, cx, cy, radius) {
  const ctx = uni.createCanvasContext(canvasId)
  const total = items.reduce((s, i) => s + i.count, 0)
  if (total === 0) {
    ctx.setFillStyle('#999')
    ctx.setFontSize(14)
    ctx.fillText('暂无数据', cx - 28, cy)
    ctx.draw()
    return
  }
  let startAngle = -Math.PI / 2
  items.forEach(item => {
    const sliceAngle = (item.count / total) * 2 * Math.PI
    ctx.beginPath()
    ctx.moveTo(cx, cy)
    ctx.arc(cx, cy, radius, startAngle, startAngle + sliceAngle)
    ctx.closePath()
    ctx.setFillStyle(item.color || '#7B68EE')
    ctx.fill()
    startAngle += sliceAngle
  })
  // 中心白色圆 (环形)
  ctx.beginPath()
  ctx.arc(cx, cy, radius * 0.5, 0, 2 * Math.PI)
  ctx.setFillStyle('#FFFFFF')
  ctx.fill()
  // 中心文字
  ctx.setFillStyle('#333')
  ctx.setFontSize(13)
  ctx.setTextAlign('center')
  ctx.fillText(total + '条', cx, cy + 4)
  ctx.draw()
}
