import http from './http'
export const authApi = {
  login: payload => http.post('/auth/login', payload),
  register: payload => http.post('/auth/register', payload),
  requestRecovery: payload => http.post('/auth/password-recovery/request', payload),
  resetPassword: payload => http.post('/auth/password-recovery/reset', payload),
  profile: () => http.get('/profile')
}
