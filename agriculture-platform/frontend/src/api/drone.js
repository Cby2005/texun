import request from '@/utils/request'

// 无人机设备管理
export function getDeviceList(params) { return request.get('/drone/device/list', { params }) }
export function getDevice(id) { return request.get('/drone/device/' + id) }
export function addDevice(data) { return request.post('/drone/device/add', data) }
export function updateDevice(id, data) { return request.put('/drone/device/update/' + id, data) }
export function deleteDevice(id) { return request.delete('/drone/device/delete/' + id) }
export function getAvailableDevices(params) { return request.get('/drone/device/available', { params }) }
export function updateDeviceStatus(id, data) { return request.put(`/drone/device/${id}/status`, data) }
export function getDeviceTasks(id) { return request.get(`/drone/device/${id}/inspection-tasks`) }
export function updateDeviceRuntime(id, data) { return request.put(`/drone/device/update-runtime/${id}`, data) }

// 温室巡检点管理
export function getPointList(params) { return request.get('/drone/point/list', { params }) }
export function getPoint(id) { return request.get('/drone/point/' + id) }
export function addPoint(data) { return request.post('/drone/point/add', data) }
export function updatePoint(id, data) { return request.put('/drone/point/update/' + id, data) }
export function deletePoint(id) { return request.delete('/drone/point/delete/' + id) }
export function initDefaultPoints(greenhouseId) { return request.post('/drone/point/initDefault', null, { params: { greenhouseId } }) }
export function getPointsByGreenhouse(greenhouseId) { return request.get('/drone/point/by-greenhouse/' + greenhouseId) }
export function generatePointCoordinate(data) { return request.post('/drone/point/generate-coordinate', data) }

// 巡检路径规划（旧）
export function generateRoute(data) { return request.post('/drone/route/generate', data) }
export function saveRoute(data) { return request.post('/drone/route/save', data) }
export function getRouteList(params) { return request.get('/drone/route/list', { params }) }
export function getRoute(id) { return request.get('/drone/route/' + id) }
export function deleteRoute(id) { return request.delete('/drone/route/delete/' + id) }

// 巡检路径规划（新 - 基于地块）
export function getRoutePlots(greenhouseId) { return request.get('/drone/inspection-route/plots', { params: { greenhouseId } }) }
export function getRoutePoints(greenhouseId) { return request.get('/drone/inspection-route/points', { params: { greenhouseId } }) }
export function generateInspectionRoute(data) { return request.post('/drone/inspection-route/generate', data) }
export function getInspectionRouteList(params) { return request.get('/drone/inspection-route/list', { params }) }
export function getInspectionRoute(id) { return request.get('/drone/inspection-route/' + id) }
export function setRouteCommon(id) { return request.put('/drone/inspection-route/' + id + '/set-common') }
export function deleteInspectionRoute(id) { return request.delete('/drone/inspection-route/' + id) }

// 巡检任务管理
export function createTask(data) { return request.post('/drone/task/create', data) }
export function startTask(id) { return request.post('/drone/task/start/' + id) }
export function finishTask(id, data) { return request.post('/drone/task/finish/' + id, data) }
export function cancelTask(id) { return request.post('/drone/task/cancel/' + id) }
export function getTaskList(params) { return request.get('/drone/task/list', { params }) }
export function getTask(id) { return request.get('/drone/task/' + id) }

// 巡检图像记录
export function getImageList(params) { return request.get('/drone/image/list', { params }) }
export function addImage(data) { return request.post('/drone/image/add', data) }
export function detectImage(id) { return request.post('/drone/image/detect/' + id) }

// 巡检报告管理
export function generateReport(taskId) { return request.post('/drone/report/generate/' + taskId) }
export function getReportList(params) { return request.get('/drone/report/list', { params }) }
export function getReport(id) { return request.get('/drone/report/' + id) }

// 温室分区 + 巡检点生成
export function getGreenhouseSceneData(greenhouseId = 1) { return request.get('/greenhouse/scene/data', { params: { greenhouseId } }) }
export function getGreenhouseAreaList(greenhouseId) { return request.get('/greenhouse/area/list', { params: { greenhouseId } }) }
export function saveGreenhouseArea(data) { return request.post('/greenhouse/area/save', data) }
export function deleteGreenhouseArea(id) { return request.delete('/greenhouse/area/delete/' + id) }
export function generateInspectionPoints(areaId) { return request.post('/greenhouse/inspection-point/generate/' + areaId) }
export function generateAllInspectionPoints() { return request.post('/greenhouse/inspection-point/generateAll') }
export function getSceneInspectionPoints(params) { return request.get('/greenhouse/inspection-point/list', { params }) }
export function generateSceneRoute(data) { return request.post('/drone/route/generateSceneRoute', data) }

// ==================== 草莓种植管理 ====================
export function addStrawberryPlanting(data) { return request.post('/digital-twin/strawberry-planting', data) }
export function getStrawberryPlantingList(params) { return request.get('/digital-twin/strawberry-planting/list', { params }) }
export function getStrawberryPlantingStatistics() { return request.get('/digital-twin/strawberry-planting/statistics') }
export function getAreaPlantingStatistics() { return request.get('/digital-twin/strawberry-planting/area-statistics') }
export function updateStrawberryPlanting(id, data) { return request.put('/digital-twin/strawberry-planting/' + id, data) }
export function deleteStrawberryPlanting(id) { return request.delete('/digital-twin/strawberry-planting/' + id) }

// ==================== 数字农田孪生 ====================
export function getDigitalTwinPlots() { return request.get('/digital-twin/plots') }
export function initDigitalTwinPlots() { return request.post('/digital-twin/plots/init') }
export function getDigitalTwinPlotDetail(id) { return request.get('/digital-twin/plots/' + id) }
export function getAvailableRobots() { return request.get('/digital-twin/picking-robots/available') }
export function createHarvest(data) { return request.post('/digital-twin/harvest', data) }
export function createSale(data) { return request.post('/digital-twin/sale', data) }

// 异常植株图片
export function saveAbnormalImage(data) { return request.post('/digital-twin/abnormal-images/save', data) }
export function getAbnormalImages(plotId) { return request.get('/digital-twin/plots/' + plotId + '/abnormal-images') }
export function getSaleBatches() { return request.get('/digital-twin/sale-batches') }
export function getDigitalTwinStatistics() { return request.get('/digital-twin/statistics') }
