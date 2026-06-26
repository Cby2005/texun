import request from '@/utils/request'

export function getSimulatorVehicles() {
  return request.get('/simulator/vehicles')
}

export function getSimulatorVehicle(id: number) {
  return request.get(`/simulator/vehicle/${id}`)
}

export function startSimulatorVehicle(id: number) {
  return request.post(`/simulator/vehicle/${id}/start`)
}

export function stopSimulatorVehicle(id: number) {
  return request.post(`/simulator/vehicle/${id}/stop`)
}

export function startSimulatorTask(id: number, targetLat: number, targetLng: number) {
  return request.post(`/simulator/vehicle/${id}/start-task`, null, { params: { targetLat, targetLng } })
}

export function stopSimulatorTask(id: number) {
  return request.post(`/simulator/vehicle/${id}/stop-task`)
}

export function emergencyStopSimulator(id: number) {
  return request.post(`/simulator/vehicle/${id}/emergency-stop`)
}

export function startAllSimulators() {
  return request.post('/simulator/start-all')
}

export function stopAllSimulators() {
  return request.post('/simulator/stop-all')
}

export function getSimulatorRunningCount() {
  return request.get('/simulator/running-count')
}
