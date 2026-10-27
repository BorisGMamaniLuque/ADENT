import http from './http'
export const structureApi = {
  branches: () => http.get('/structure/branches'),
  createBranch: payload => http.post('/structure/branches', payload),
  floors: branchId => http.get('/structure/floors', { params: branchId ? { branchId } : {} }),
  createFloor: payload => http.post('/structure/floors', payload),
  rooms: floorId => http.get('/structure/rooms', { params: floorId ? { floorId } : {} }),
  createRoom: payload => http.post('/structure/rooms', payload)
}
