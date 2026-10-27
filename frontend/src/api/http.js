import axios from 'axios'

const http = axios.create({
  baseURL: import.meta.env.VITE_API_URL || '/api',
  timeout: 15000,
  headers: { 'Content-Type': 'application/json' }
})

http.interceptors.request.use(config => {
  const raw = localStorage.getItem('adent.session')
  if (raw) {
    try {
      const session = JSON.parse(raw)
      if (session?.accessToken) config.headers.Authorization = `Bearer ${session.accessToken}`
    } catch {}
  }
  return config
})

http.interceptors.response.use(
  response => response,
  error => {
    if (error?.response?.status === 401) {
      localStorage.removeItem('adent.session')
      if (!location.pathname.startsWith('/login')) location.href = '/login'
    }
    return Promise.reject(error)
  }
)

export const apiError = (error) =>
  error?.response?.data?.message ||
  error?.response?.data?.detail ||
  error?.message ||
  'Ocurrió un error inesperado.'

export default http
