import http from './http'
export const staffApi = {
  list: () => http.get('/staff'),
  get: id => http.get(`/staff/${id}`),
  create: payload => http.post('/staff', payload),
  update: (id, payload) => http.put(`/staff/${id}`, payload)
}
