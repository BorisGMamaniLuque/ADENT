import http from './http'

export const publicSiteAdminApi={
  get: tenantId => http.get(`/platform/public-sites/tenant/${tenantId}`),
  save: (tenantId,payload) => http.put(`/platform/public-sites/tenant/${tenantId}`,payload),
  toggle: (tenantId,enabled) => http.patch(`/platform/public-sites/tenant/${tenantId}/enabled`,null,{params:{enabled}}),
  generateProject: tenantId => http.post(`/platform/public-sites/tenant/${tenantId}/generate-project`),
  projectStatus: tenantId => http.get(`/platform/public-sites/tenant/${tenantId}/project`)
}
