import http from './http'
export const userApi = {
  list: () => http.get('/users'),
  create: payload => http.post('/users', payload)
}
