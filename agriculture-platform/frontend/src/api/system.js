import request from '@/utils/request'

export function login(data) { return request.post('/auth/login', data) }
export function register(data) { return request.post('/auth/register', data) }
export function getUserInfo() { return request.get('/auth/userinfo') }
export function getRouters() { return request.get('/auth/routers') }

export function getUsers(params) { return request.get('/users', { params }) }
export function getUser(id) { return request.get('/users/' + id) }
export function addUser(data) { return request.post('/users', data) }
export function updateUser(id, data) { return request.put('/users/' + id, data) }
export function deleteUser(id) { return request.delete('/users/' + id) }
export function updateProfile(data) { return request.put('/users/profile', data) }
export function updatePassword(oldPwd, newPwd) { return request.put('/users/password', null, { params: { oldPwd, newPwd } }) }

export function getRoles() { return request.get('/roles') }
export function addRole(data) { return request.post('/roles', data) }
export function updateRole(id, data) { return request.put('/roles/' + id, data) }
export function deleteRole(id) { return request.delete('/roles/' + id) }

export function getMenus() { return request.get('/menus') }
export function getMenuTree() { return request.get('/menus/tree') }
export function addMenu(data) { return request.post('/menus', data) }
export function updateMenu(id, data) { return request.put('/menus/' + id, data) }
export function deleteMenu(id) { return request.delete('/menus/' + id) }
