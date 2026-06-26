import request from '@/utils/request'

export function getEnterprises(params) { return request.get('/farm/enterprises', { params }) }
export function getAllEnterprises() { return request.get('/farm/enterprises/all') }
export function getEnterprise(id) { return request.get('/farm/enterprises/' + id) }
export function addEnterprise(data) { return request.post('/farm/enterprises', data) }
export function updateEnterprise(id, data) { return request.put('/farm/enterprises/' + id, data) }
export function deleteEnterprise(id) { return request.delete('/farm/enterprises/' + id) }

export function getLands(params) { return request.get('/farm/lands', { params }) }
export function getLand(id) { return request.get('/farm/lands/' + id) }
export function addLand(data) { return request.post('/farm/lands', data) }
export function updateLand(id, data) { return request.put('/farm/lands/' + id, data) }
export function deleteLand(id) { return request.delete('/farm/lands/' + id) }

export function getCrops(params) { return request.get('/farm/crops', { params }) }
export function addCrop(data) { return request.post('/farm/crops', data) }
export function updateCrop(id, data) { return request.put('/farm/crops/' + id, data) }
export function deleteCrop(id) { return request.delete('/farm/crops/' + id) }

export function getDevices(params) { return request.get('/farm/devices', { params }) }
export function addDevice(data) { return request.post('/farm/devices', data) }
export function updateDevice(id, data) { return request.put('/farm/devices/' + id, data) }

export function getEnvData(landId, params) { return request.get('/farm/env/data/' + landId, { params }) }
export function getLatestEnv(landId) { return request.get('/farm/env/latest/' + landId) }
export function saveEnvData(data) { return request.post('/farm/env/data', data) }
export function getLatestEnvAll() { return request.get('/farm/env/latest') }

export function getDashboardStats() { return request.get('/dashboard/stats') }
export function getEnvSummary() { return request.get('/dashboard/env/summary') }
export function getRoleDashboard() { return request.get('/dashboard/role') }
