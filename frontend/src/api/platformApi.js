import http from './http'
export const platformApi = {
  listTenants: () => http.get('/platform/tenants'),
  createTenant: payload => http.post('/platform/tenants', payload),
  changeStatus: (id, status) => http.patch(`/platform/tenants/${id}/status`, null, { params: { status } })
}
