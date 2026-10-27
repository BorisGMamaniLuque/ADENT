import http from './http'
export const appointmentApi = {
  list: (from, to) => http.get('/appointments', { params: { from, to } }),
  create: payload => http.post('/appointments', payload),
  changeStatus: (id, status) => http.patch(`/appointments/${id}/status`, null, { params: { status } })
}
