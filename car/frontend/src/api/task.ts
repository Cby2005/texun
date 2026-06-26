import request from '@/utils/request'

export function listTasks(params: any) {
  return request.get('/task/list', { params })
}

export function getTask(id: number) {
  return request.get(`/task/${id}`)
}

export function createTask(data: any) {
  return request.post('/task', data)
}

export function updateTask(data: any) {
  return request.put('/task', data)
}

export function cancelTask(id: number) {
  return request.post(`/task/${id}/cancel`)
}

export function assignVehicle(taskId: number, vehicleId: number) {
  return request.post(`/task/${taskId}/assign/${vehicleId}`)
}

export function sendCommand(taskId: number, commandType: string, commandData?: string) {
  return request.post('/command/send', null, { params: { taskId, commandType, commandData } })
}

export function listCommands(params: any) {
  return request.get('/command/list', { params })
}

export function getCommandsByTask(taskId: number) {
  return request.get(`/command/task/${taskId}`)
}
