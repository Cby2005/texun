import request from '@/utils/request'

export function listFarms(params: any) {
  return request.get('/field/farm/list', { params })
}

export function getFarm(id: number) {
  return request.get(`/field/farm/${id}`)
}

export function createFarm(data: any) {
  return request.post('/field/farm', data)
}

export function updateFarm(data: any) {
  return request.put('/field/farm', data)
}

export function deleteFarm(id: number) {
  return request.delete(`/field/farm/${id}`)
}

export function listPlots(params: any) {
  return request.get('/field/plot/list', { params })
}

export function getPlot(id: number) {
  return request.get(`/field/plot/${id}`)
}

export function createPlot(data: any) {
  return request.post('/field/plot', data)
}

export function updatePlot(data: any) {
  return request.put('/field/plot', data)
}

export function deletePlot(id: number) {
  return request.delete(`/field/plot/${id}`)
}

export function getPlotsByFarm(farmId: number) {
  return request.get(`/field/plot/farm/${farmId}`)
}
