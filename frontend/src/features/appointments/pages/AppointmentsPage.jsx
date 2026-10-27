import React, { useEffect, useState } from 'react'
import http, { apiError } from '../../../api/http'
import { EmptyState, ErrorBanner, Field, Modal, PageHeader } from '../../../components/common/Ui'

const empty={patientId:'',staffId:'',roomId:'',startsAt:'',endsAt:'',reason:'',status:'SCHEDULED'}
export default function AppointmentsPage(){
 const [rows,setRows]=useState([]),[patients,setPatients]=useState([]),[staff,setStaff]=useState([]),[rooms,setRooms]=useState([])
 const [open,setOpen]=useState(false),[form,setForm]=useState(empty),[error,setError]=useState('')
 async function load(){
  setError('')
  const now=new Date(),to=new Date();to.setDate(to.getDate()+45)
  try{
   const [a,p]=await Promise.all([http.get('/appointments',{params:{from:now.toISOString(),to:to.toISOString()}}),http.get('/patients',{params:{q:'',page:0,size:100}})])
   setRows(a.data||[]);setPatients(p.data.content||[])
   Promise.all([http.get('/directory/staff'),http.get('/directory/rooms')]).then(([s,r])=>{setStaff(s.data||[]);setRooms(r.data||[])}).catch(()=>{})
  }catch(e){setError(apiError(e))}
 }
 useEffect(()=>{load()},[])
 async function save(e){
  e.preventDefault()
  try{await http.post('/appointments',{...form,roomId:form.roomId||null,startsAt:new Date(form.startsAt).toISOString(),endsAt:form.endsAt?new Date(form.endsAt).toISOString():null});setOpen(false);setForm(empty);load()}
  catch(e){setError(apiError(e))}
 }
 async function status(id,value){try{await http.patch(`/appointments/${id}/status`,null,{params:{status:value}});load()}catch(e){setError(apiError(e))}}
 return <div>
  <PageHeader title="Agenda y citas" subtitle="Agenda operativa por paciente, profesional y consultorio." actions={<button className="primary" onClick={()=>setOpen(true)}>+ Nueva cita</button>}/>
  <ErrorBanner message={error}/>
  <section className="card table-card">{rows.length===0?<EmptyState title="Sin citas próximas"/>:<div className="table-wrap"><table><thead><tr><th>Fecha</th><th>Paciente</th><th>Motivo</th><th>Estado</th><th>Acción</th></tr></thead><tbody>{rows.map(a=>{
   const p=patients.find(x=>x.id===a.patientId)
   return <tr key={a.id}><td>{new Date(a.startsAt).toLocaleString()}</td><td>{p?`${p.lastNames}, ${p.firstNames}`:a.patientId}</td><td>{a.reason||'—'}</td><td><span className="badge">{a.status}</span></td><td><select value={a.status} onChange={e=>status(a.id,e.target.value)}><option>SCHEDULED</option><option>CONFIRMED</option><option>IN_PROGRESS</option><option>COMPLETED</option><option>CANCELLED</option><option>NO_SHOW</option></select></td></tr>})}</tbody></table></div>}</section>
  <Modal open={open} title="Nueva cita" onClose={()=>setOpen(false)}><form className="form-grid" onSubmit={save}>
   <Field label="Paciente"><select required value={form.patientId} onChange={e=>setForm({...form,patientId:e.target.value})}><option value="">Seleccionar…</option>{patients.map(p=><option value={p.id} key={p.id}>{p.lastNames}, {p.firstNames}</option>)}</select></Field>
   <Field label="Profesional"><select required value={form.staffId} onChange={e=>setForm({...form,staffId:e.target.value})}><option value="">Seleccionar…</option>{staff.map(s=><option value={s.id} key={s.id}>{s.lastNames}, {s.firstNames}</option>)}</select></Field>
   <Field label="Consultorio"><select value={form.roomId} onChange={e=>setForm({...form,roomId:e.target.value})}><option value="">Sin asignar</option>{rooms.map(r=><option value={r.id} key={r.id}>{r.name}</option>)}</select></Field>
   <Field label="Inicio"><input required type="datetime-local" value={form.startsAt} onChange={e=>setForm({...form,startsAt:e.target.value})}/></Field>
   <Field label="Fin"><input type="datetime-local" value={form.endsAt} onChange={e=>setForm({...form,endsAt:e.target.value})}/></Field>
   <Field label="Motivo"><textarea value={form.reason} onChange={e=>setForm({...form,reason:e.target.value})}/></Field>
   <div className="form-actions"><button type="button" onClick={()=>setOpen(false)}>Cancelar</button><button className="primary">Guardar cita</button></div>
  </form></Modal>
 </div>
}
