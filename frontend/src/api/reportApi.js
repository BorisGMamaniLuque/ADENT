import http from './http'

export const reportApi = {
  dashboard: (params = {}) => http.get('/reports/dashboard', { params })
}
