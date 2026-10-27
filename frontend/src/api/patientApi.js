import http from './http'
export const patientApi = {
  search: (q = '', page = 0, size = 20) => http.get('/patients', { params: { q, page, size } }),
  get: id => http.get(`/patients/${id}`),
  create: payload => http.post('/patients', payload),
  update: (id, payload) => http.put(`/patients/${id}`, payload)
}
