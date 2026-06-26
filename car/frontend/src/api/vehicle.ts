import request from '@/utils/request'

export function listDevices(params: any) {
  return request.get('/vehicle/device/list', { params })
}

export function getDevice(id: number) {
  return request.get(`/vehicle/device/${id}`)
}

export function registerDevice(data: any) {
  return request.post('/vehicle/device/register', data)
}

export function updateDevice(data: any) {
  return request.put('/vehicle/device', data)
}

export function deleteDevice(id: number) {
  return request.delete(`/vehicle/device/${id}`)
}

export function onlineDevice(id: number) {
  return request.post(`/vehicle/device/command/${id}/online`)
}

export function offlineDevice(id: number) {
  return request.post(`/vehicle/device/command/${id}/offline`)
}

export function startWork(id: number) {
  return request.post(`/vehicle/device/command/${id}/start-work`)
}

export function stopWork(id: number) {
  return request.post(`/vehicle/device/command/${id}/stop-work`)
}

export function emergencyStop(id: number) {
  return request.post(`/vehicle/device/command/${id}/emergency-stop`)
}

export function returnToBase(id: number) {
  return request.post(`/vehicle/device/command/${id}/return`)
}
