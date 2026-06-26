import request from '@/utils/request'

export function addMonitorData(data: any) {
  return request.post('/monitor/data', data)
}

export function listMonitorData(params: any) {
  return request.get('/monitor/data/list', { params })
}

export function getLatestData(plotId: number) {
  return request.get(`/monitor/data/latest/${plotId}`)
}

export function getTrend(params: { plotId: number; dataType: string; startTime: string; endTime: string }) {
  return request.get('/monitor/data/trend', { params })
}
