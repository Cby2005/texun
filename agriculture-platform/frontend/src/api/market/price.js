import request from '../../utils/request'

/**
 * 草莓市场价格 API
 */
export function getMarketPriceSummary() {
  return request.get('/market/price/summary')
}

export function getMarketPriceTrend(days = 7) {
  return request.get('/market/price/trend', { params: { days } })
}

export function getMarketPriceCompare() {
  return request.get('/market/price/compare')
}

export function getMarketPriceAlerts() {
  return request.get('/market/price/alerts')
}

// ===== 前端 fallback 数据（接口失败时使用） =====
export const fallbackSummary = {
  avgPrice: 18.60,
  maxPrice: 22.00,
  minPrice: 15.00,
  unit: '元/公斤',
  changeRate: 3.2,
  priceStatus: 'HIGH',
  updateTime: new Date().toLocaleString('zh-CN'),
  suggestion: '当前草莓价格高于近7日均价，建议安排成熟草莓采摘销售。'
}

export const fallbackTrend = [
  { date: '06-22', avgPrice: 16.8 },
  { date: '06-23', avgPrice: 17.2 },
  { date: '06-24', avgPrice: 17.5 },
  { date: '06-25', avgPrice: 18.1 },
  { date: '06-26', avgPrice: 18.3 },
  { date: '06-27', avgPrice: 18.0 },
  { date: '06-28', avgPrice: 18.6 }
]

export const fallbackCompare = [
  { marketName: '郑州万邦市场', avgPrice: 18.60 },
  { marketName: '北京新发地', avgPrice: 22.00 },
  { marketName: '许昌批发市场', avgPrice: 17.20 },
  { marketName: '洛阳宏进市场', avgPrice: 18.00 }
]

export const fallbackAlerts = [
  { alertContent: '当前草莓价格平稳，无异常预警', alertLevel: 'NORMAL', createTime: new Date().toLocaleString('zh-CN') }
]
