import http from './http'
export const clinicalApi = {
  byPatient: patientId => http.get(`/clinical/encounters/patient/${patientId}`),
  create: payload => http.post('/clinical/encounters', payload),
  update: (id, payload) => http.put(`/clinical/encounters/${id}`, payload),
  finalize: id => http.post(`/clinical/encounters/${id}/finalize`),
  addendum: (id, payload) => http.post(`/clinical/encounters/${id}/addendum`, payload)
}
