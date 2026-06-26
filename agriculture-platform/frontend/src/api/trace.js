import request from '@/utils/request'

// 产品管理
export function getProducts(params) { return request.get('/trace/products', { params }) }
export function getProduct(id) { return request.get('/trace/products/' + id) }
export function addProduct(data) { return request.post('/trace/products', data) }
export function updateProduct(id, data) { return request.put('/trace/products/' + id, data) }
export function deleteProduct(id) { return request.delete('/trace/products/' + id) }
export function queryByCode(code) { return request.get('/trace/products/public/' + code) }

// 批次管理
export function getBatches(params) { return request.get('/trace/batches', { params }) }
export function getBatch(id) { return request.get('/trace/batches/' + id) }
export function addBatch(data) { return request.post('/trace/batches', data) }
export function updateBatch(id, data) { return request.put('/trace/batches/' + id, data) }
export function deleteBatch(id) { return request.delete('/trace/batches/' + id) }

// 通用溯源记录管理 (type: production/processing/storage/logistics/quality/sales)
export function getRecords(type, params) { return request.get('/trace/records/' + type, { params }) }
export function getRecord(type, id) { return request.get('/trace/records/' + type + '/' + id) }
export function addRecord(type, data) { return request.post('/trace/records/' + type, data) }
export function updateRecord(type, id, data) { return request.put('/trace/records/' + type + '/' + id, data) }
export function deleteRecord(type, id) { return request.delete('/trace/records/' + type + '/' + id) }

// 全链路溯源
export function getChainTrace(batchNo) { return request.get('/trace/chain/' + batchNo) }
export function getChainBlocks(batchNo) { return request.get('/trace/chain/blocks/' + batchNo) }
