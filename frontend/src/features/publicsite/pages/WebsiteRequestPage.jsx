import React,{useEffect,useState} from 'react'
import http,{apiError} from '../../../api/http'
import {ErrorBanner,Field,PageHeader,SuccessBanner} from '../../../components/common/Ui'

const EMPTY={businessName:'',description:'',preferredColors:'',servicesText:'',phone:'',whatsapp:'',email:'',address:'',facebookUrl:'',instagramUrl:'',designNotes:''}
const LABELS={NOT_REQUESTED:'No solicitada',REQUESTED:'Solicitud enviada',UNDER_REVIEW:'En revisión',NEEDS_INFO:'Requiere información',APPROVED:'Aprobada',GENERATED:'Proyecto generado',IN_DEVELOPMENT:'En desarrollo',READY:'Lista para publicar',PUBLISHED:'Publicada',REJECTED:'Rechazada'}

export default function WebsiteRequestPage(){
 const[data,setData]=useState(null),[form,setForm]=useState(EMPTY),[error,setError]=useState(''),[success,setSuccess]=useState(''),[busy,setBusy]=useState(false)
 async function load(){try{const r=await http.get('/my/website-request');setData(r.data);setForm({...EMPTY,...r.data})}catch(e){setError(apiError(e))}}
 useEffect(()=>{load()},[])
 const locked=['GENERATED','IN_DEVELOPMENT','READY','PUBLISHED'].includes(data?.status)
 async function submit(e){e.preventDefault();setBusy(true);setError('');setSuccess('');try{const payload=Object.fromEntries(Object.keys(EMPTY).map(k=>[k,form[k]||'']));const r=await http.post('/my/website-request',payload);setData(r.data);setForm({...EMPTY,...r.data});setSuccess('Solicitud enviada al Super Administrador.')}catch(e){setError(apiError(e))}finally{setBusy(false)}}
 const set=(k,v)=>setForm(f=>({...f,[k]:v}))
 return <div><PageHeader title="Página web" subtitle="Solicita una página publicitaria para tu organización. El Super Administrador revisará la información y generará el proyecto editable."/>
 <ErrorBanner message={error}/><SuccessBanner message={success}/>
 <section className="card web-status-card"><div><b>{data?.organizationName||'Organización'}</b><p className="muted">Slug de referencia: {data?.slug||'-'}</p></div><div><b>Estado:</b> {LABELS[data?.status]||data?.status||'Cargando...'}</div></section>
 {data?.adminNotes&&<section className="card"><b>Observación del Super Admin</b><p>{data.adminNotes}</p></section>}
 {data?.projectPath&&<section className="card"><h2>Proyecto generado</h2><p>La web ya tiene una carpeta física independiente para desarrollo. El código es administrado por el desarrollador.</p><code>{data.projectPath}</code></section>}
 <form className="card" onSubmit={submit}><h2>Datos para la página publicitaria</h2><div className="form-grid">
  <Field label="Nombre comercial"><input disabled={locked} value={form.businessName||''} onChange={e=>set('businessName',e.target.value)}/></Field>
  <Field label="Colores preferidos"><input disabled={locked} placeholder="Ej. azul, blanco y dorado" value={form.preferredColors||''} onChange={e=>set('preferredColors',e.target.value)}/></Field>
  <Field label="Descripción de la empresa"><textarea disabled={locked} value={form.description||''} onChange={e=>set('description',e.target.value)}/></Field>
  <Field label="Servicios que desea mostrar"><textarea disabled={locked} placeholder="Ortodoncia, Endodoncia, Implantología..." value={form.servicesText||''} onChange={e=>set('servicesText',e.target.value)}/></Field>
  <Field label="Teléfono"><input disabled={locked} value={form.phone||''} onChange={e=>set('phone',e.target.value)}/></Field>
  <Field label="WhatsApp"><input disabled={locked} value={form.whatsapp||''} onChange={e=>set('whatsapp',e.target.value)}/></Field>
  <Field label="Correo"><input disabled={locked} value={form.email||''} onChange={e=>set('email',e.target.value)}/></Field>
  <Field label="Dirección"><input disabled={locked} value={form.address||''} onChange={e=>set('address',e.target.value)}/></Field>
  <Field label="Facebook"><input disabled={locked} value={form.facebookUrl||''} onChange={e=>set('facebookUrl',e.target.value)}/></Field>
  <Field label="Instagram"><input disabled={locked} value={form.instagramUrl||''} onChange={e=>set('instagramUrl',e.target.value)}/></Field>
  <Field label="Indicaciones para el diseño"><textarea disabled={locked} placeholder="Ej. portada con video, estilo moderno, WhatsApp flotante..." value={form.designNotes||''} onChange={e=>set('designNotes',e.target.value)}/></Field>
 </div>{!locked&&<div className="form-actions"><button className="primary" disabled={busy}>{busy?'Enviando…':'Enviar solicitud al Super Admin'}</button></div>}</form>
 </div>
}
