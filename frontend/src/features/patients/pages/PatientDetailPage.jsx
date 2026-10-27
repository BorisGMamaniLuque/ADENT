import React, { useEffect, useState } from 'react'
import { Link, useParams } from 'react-router-dom'
import http, { apiError } from '../../../api/http'
import { useAuth } from '../../../auth/AuthContext'
import { ErrorBanner, Field, Modal, PageHeader } from '../../../components/common/Ui'
import JsonClinicalForm from '../../../components/clinical/JsonClinicalForm'
import OdontogramEditor from '../../../components/clinical/OdontogramEditor'


const med = [
  {key:'medicalTreatment',label:'¿Recibe tratamiento médico?',type:'check'},
  {key:'medication',label:'Medicamentos actuales'},
  {key:'drugAllergy',label:'Alergia a medicamentos'},
  {key:'diabetes',label:'Diabetes',type:'check'},
  {key:'hypertension',label:'Presión arterial alta',type:'check'},
  {key:'hypotension',label:'Presión arterial baja',type:'check'},
  {key:'tbc',label:'TBC',type:'check'},
  {key:'vih',label:'VIH',type:'check'},
  {key:'anemia',label:'Anemia',type:'check'},
  {key:'asthma',label:'Asma',type:'check'},
  {key:'cardiac',label:'Problemas cardíacos',type:'check'},
  {key:'renal',label:'Problemas renales',type:'check'},
  {key:'hepatitis',label:'Hepatitis',type:'check'},
  {key:'hospitalized',label:'Hospitalizaciones / motivo',full:true},
  {key:'surgery',label:'Intervenciones quirúrgicas',full:true},
  {key:'habits',label:'Hábitos',type:'textarea',full:true}
]

const history = [
  {key:'chiefComplaint',label:'Motivo de consulta',type:'textarea',full:true},
  {key:'consultType',label:'Tipo de consulta',type:'select',options:['Emergencia','Tratamiento integral','Tratamiento específico']},
  {key:'lastDentalVisit',label:'Última consulta odontológica'},
  {key:'previousComplication',label:'Complicaciones previas',type:'textarea',full:true},
  {key:'anestheticAllergy',label:'Alergia a anestésico dental',type:'check'},
  {key:'gumBleeding',label:'Sangrado de encías',type:'check'},
  {key:'bruxism',label:'Aprieta los dientes',type:'check'},
  {key:'brushingFrequency',label:'Cepillado por día'},
  {key:'previousTreatments',label:'Tratamientos anteriores',type:'textarea',full:true}
]

const exam = [
  {key:'generalState',label:'Apreciación general',type:'select',options:['ABEG','ABEN','ABEH']},
  {key:'collaboration',label:'Colaboración',type:'select',options:['Receptivo','No receptivo']},
  {key:'weight',label:'Peso (kg)',type:'number'},
  {key:'height',label:'Talla (m)',type:'number'},
  {key:'temperature',label:'Temperatura',type:'number'},
  {key:'pulse',label:'Pulso',type:'number'},
  {key:'respiratoryRate',label:'Frecuencia respiratoria',type:'number'},
  {key:'bloodPressure',label:'Presión arterial'},
  {key:'extraoral',label:'Examen extraoral',type:'textarea',full:true},
  {key:'intraoral',label:'Examen intraoral: labios, carrillos, paladar, lengua, encías, frenillos, dientes, oclusión e higiene',type:'textarea',full:true}
]

export default function PatientDetailPage() {
  const { id } = useParams()
  const auth = useAuth()
  const [patient,setPatient] = useState(null)
  const [tab,setTab] = useState('Resumen')
  const [encounters,setEncounters] = useState([])
  const [lab,setLab] = useState([])
  const [rx,setRx] = useState([])
  const [payments,setPayments] = useState([])
  const [error,setError] = useState('')
  const [staff,setStaff] = useState([])
  const [rooms,setRooms] = useState([])
  const [encOpen,setEncOpen] = useState(false)
  const [encForm,setEncForm] = useState({professionalStaffId:'',roomId:'',chiefComplaint:'',anamnesis:'',clinicalExam:'',dentalAssessment:'',treatmentPlanSummary:'',proceduresSummary:'',indications:'',evolution:''})
  const [orderOpen,setOrderOpen] = useState(null)
  const [orderForm,setOrderForm] = useState({studyType:'Panorámica',toothCodes:'',instructions:'',workType:'',toothCode:'',dueDate:''})
  const [payOpen,setPayOpen] = useState(false)
  const [payForm,setPayForm] = useState({amount:'',paymentMethod:'Efectivo',notes:''})

  const clinical = auth.hasRole('DENTIST','DENTAL_ASSISTANT')
  const canPay = auth.hasRole('OWNER','RECEPTION')
  const tabs = auth.hasRole('DENTIST')
    ? ['Resumen','Antecedentes','Historia clínica','Examen clínico','Odontograma','Exámenes complementarios','Atenciones','Tratamientos','Radiología','Laboratorio','Reportes']
    : auth.hasRole('DENTAL_ASSISTANT')
      ? ['Resumen','Antecedentes','Historia clínica','Atenciones']
      : auth.hasRole('LAB_TECHNICIAN')
        ? ['Resumen','Laboratorio']
        : auth.hasRole('XRAY_TECHNICIAN')
          ? ['Resumen','Radiología']
          : auth.hasRole('RECEPTION')
            ? ['Resumen','Pagos','Reportes']
            : ['Resumen','Antecedentes','Historia clínica','Examen clínico','Odontograma','Exámenes complementarios','Atenciones','Tratamientos','Radiología','Laboratorio','Pagos','Reportes']

  async function load() {
    setError('')
    try {
      const [p,e,l,r,pa] = await Promise.all([
        http.get(`/patients/${id}`),
        http.get(`/clinical/encounters/patient/${id}`).catch(()=>({data:[]})),
        http.get(`/laboratory/patient/${id}`).catch(()=>({data:[]})),
        http.get(`/radiology/patient/${id}`).catch(()=>({data:[]})),
        http.get(`/finance/payments/patient/${id}`).catch(()=>({data:[]}))
      ])
      setPatient(p.data); setEncounters(e.data||[]); setLab(l.data||[]); setRx(r.data||[]); setPayments(pa.data||[])
    } catch(e) { setError(apiError(e)) }
  }
  useEffect(()=>{load(); if(auth.hasRole('DENTIST')) Promise.all([http.get('/directory/staff'),http.get('/directory/rooms')]).then(([s,r])=>{setStaff(s.data||[]);setRooms(r.data||[])}).catch(()=>{})},[id])


  async function createEncounter(e) {
    e.preventDefault()
    try {
      await http.post('/clinical/encounters',{...encForm,patientId:id,roomId:encForm.roomId||null,assistantStaffId:null,appointmentId:null,encounteredAt:new Date().toISOString(),nextAttentionAt:null})
      setEncOpen(false); setEncForm({professionalStaffId:'',roomId:'',chiefComplaint:'',anamnesis:'',clinicalExam:'',dentalAssessment:'',treatmentPlanSummary:'',proceduresSummary:'',indications:'',evolution:''}); load()
    } catch(e) { setError(apiError(e)) }
  }

  async function createOrder(e) {
    e.preventDefault()
    try {
      if (orderOpen==='rx') {
        await http.post('/radiology',{patientId:id,studyType:orderForm.studyType,toothCodes:orderForm.toothCodes,instructions:orderForm.instructions})
      } else {
        await http.post('/laboratory',{patientId:id,workType:orderForm.workType,toothCode:orderForm.toothCode,instructions:orderForm.instructions,dueDate:orderForm.dueDate||null})
      }
      setOrderOpen(null); setOrderForm({studyType:'Panorámica',toothCodes:'',instructions:'',workType:'',toothCode:'',dueDate:''}); load()
    } catch(e) { setError(apiError(e)) }
  }

  async function savePayment(e) {
    e.preventDefault()
    try {
      await http.post('/finance/payments',{patientId:id,amount:Number(payForm.amount),paymentMethod:payForm.paymentMethod,notes:payForm.notes})
      setPayOpen(false); setPayForm({amount:'',paymentMethod:'Efectivo',notes:''}); load()
    } catch(e) { setError(apiError(e)) }
  }

  async function download(kind) {
    try {
      const res = await http.get(`/reports/patient/${id}/${kind}`,{responseType:'blob'})
      const url = URL.createObjectURL(res.data)
      const a = document.createElement('a')
      a.href=url; a.download=`ADENT_${patient.historyNumber}.${kind==='pdf'?'pdf':'xlsx'}`; a.click(); URL.revokeObjectURL(url)
    } catch(e) { setError(apiError(e)) }
  }

  if (!patient) return <div><PageHeader title="Patient Master Record"/><ErrorBanner message={error}/><p>Cargando…</p></div>

  return <div>
    <PageHeader title={`${patient.firstNames} ${patient.lastNames}`} subtitle={`${patient.historyNumber} · ${patient.country||''} · ${patient.documentType||''} ${patient.documentNumber||''}`} actions={<Link className="secondary inline" to="/patients">← Pacientes</Link>}/>
    <ErrorBanner message={error}/>
    {patient.clinicalAlerts && <div className="alert warning"><b>Alertas clínicas:</b> {patient.clinicalAlerts}</div>}

    <div className="pmr-tabs">{tabs.map(t=><button className={tab===t?'active':''} onClick={()=>setTab(t)} key={t}>{t}</button>)}</div>

    {tab==='Resumen' && <div className="pmr-grid">
      <section className="card"><h2>Identificación</h2><dl className="detail-list">
        <div><dt>Edad</dt><dd>{patient.age??'—'}</dd></div><div><dt>Sexo</dt><dd>{patient.sex||'—'}</dd></div>
        <div><dt>Teléfono</dt><dd>{patient.phone||'—'}</dd></div><div><dt>Correo</dt><dd>{patient.email||'—'}</dd></div>
        <div><dt>Dirección</dt><dd>{patient.address||'—'}</dd></div><div><dt>Procedencia</dt><dd>{patient.provenance||'—'}</dd></div>
        <div><dt>Ocupación</dt><dd>{patient.occupation||'—'}</dd></div><div><dt>Emergencia</dt><dd>{patient.emergencyContactName||'—'} {patient.emergencyContactPhone||''}</dd></div>
      </dl></section>
      <section className="card"><h2>Resumen longitudinal</h2><div className="mini-kpis"><div className="kpi-mini"><b>{encounters.length}</b><span>Atenciones</span></div><div className="kpi-mini"><b>{rx.length}</b><span>Órdenes RX</span></div><div className="kpi-mini"><b>{lab.length}</b><span>Órdenes laboratorio</span></div><div className="kpi-mini"><b>{payments.length}</b><span>Pagos</span></div></div></section>
    </div>}

    {tab==='Antecedentes' && <JsonClinicalForm patientId={id} type="MEDICAL_HISTORY" title="Antecedentes médicos" fields={med} canEdit={clinical}/>} 
    {tab==='Historia clínica' && <JsonClinicalForm patientId={id} type="DENTAL_HISTORY" title="Historia clínica estomatológica" fields={history} canEdit={clinical}/>} 
    {tab==='Examen clínico' && <JsonClinicalForm patientId={id} type="CLINICAL_EXAM" title="Examen clínico dental" fields={exam} canEdit={auth.hasRole('DENTIST')}/>} 
    {tab==='Odontograma' && <section className="card"><h2>Odontograma interactivo</h2><p className="muted">Cada guardado crea una nueva versión. Compatible con mouse, touch y stylus.</p><OdontogramEditor patientId={id} canEdit={auth.hasRole('DENTIST')}/></section>}

    {tab==='Exámenes complementarios' && <JsonClinicalForm patientId={id} type="COMPLEMENTARY_EXAMS" title="Exámenes complementarios, diagnóstico y plan" fields={[{key:'periapical',label:'Radiografía periapical',type:'check'},{key:'bitewing',label:'Bitewing',type:'check'},{key:'panoramic',label:'Panorámica',type:'check'},{key:'tomography',label:'Tomografía',type:'check'},{key:'photos',label:'Fotografías',type:'check'},{key:'radiographicReport',label:'Informe radiográfico',type:'textarea',full:true},{key:'diagnosis',label:'Diagnóstico',type:'textarea',full:true},{key:'prognosis',label:'Pronóstico',type:'select',options:['Favorable','Reservado','Desfavorable']},{key:'plan',label:'Plan de tratamiento',type:'textarea',full:true}]} canEdit={auth.hasRole('DENTIST')}/>}

    {tab==='Atenciones' && <section className="card"><div className="section-title"><h2>Atenciones</h2>{auth.hasRole('DENTIST')&&<button className="primary small" onClick={()=>setEncOpen(true)}>+ Nueva atención</button>}</div>{encounters.length===0?<p className="muted">Sin atenciones registradas.</p>:encounters.map(e=><div className="record-row" key={e.id}><b>{new Date(e.encounteredAt).toLocaleString()}</b><span>{e.chiefComplaint||'Atención odontológica'}</span><span>{e.status}</span></div>)}</section>}


    {tab==='Tratamientos' && <section className="card"><h2>Tratamientos y evolución</h2><p className="muted">Se alimenta de las atenciones registradas. El plan, procedimientos y evolución se conservan por fecha.</p>{encounters.length===0?<p className="muted">Sin tratamientos registrados.</p>:encounters.map(e=><div className="record-row" key={e.id}><b>{new Date(e.encounteredAt).toLocaleDateString()}</b><span>{e.treatmentPlanSummary||e.proceduresSummary||'Sin detalle de tratamiento'}</span><span>{e.status}</span></div>)}</section>}

    {tab==='Radiología' && <section className="card"><div className="section-title"><h2>Radiología</h2>{auth.hasRole('DENTIST')&&<button className="primary small" onClick={()=>setOrderOpen('rx')}>+ Orden RX</button>}</div>{rx.length===0?<p className="muted">Sin órdenes.</p>:rx.map(o=><div className="record-row" key={o.id}><b>{o.studyType}</b><span>Piezas: {o.toothCodes||'—'}</span><span>{o.status}</span>{o.resultUrl&&<a href={o.resultUrl} target="_blank">Resultado</a>}</div>)}</section>}

    {tab==='Laboratorio' && <section className="card"><div className="section-title"><h2>Laboratorio dental</h2>{auth.hasRole('DENTIST')&&<button className="primary small" onClick={()=>setOrderOpen('lab')}>+ Orden laboratorio</button>}</div>{lab.length===0?<p className="muted">Sin órdenes.</p>:lab.map(o=><div className="record-row" key={o.id}><b>{o.workType}</b><span>Pieza: {o.toothCode||'—'}</span><span>{o.status}</span></div>)}</section>}

    {tab==='Pagos' && <section className="card"><div className="section-title"><h2>Pagos</h2>{canPay&&<button className="primary small" onClick={()=>setPayOpen(true)}>+ Registrar pago</button>}</div>{payments.length===0?<p className="muted">Sin pagos registrados.</p>:payments.map(p=><div className="record-row" key={p.id}><b>S/ {p.amount}</b><span>{p.payment_method||p.paymentMethod}</span><span>{new Date(p.paid_at||p.paidAt).toLocaleString()}</span></div>)}</section>}

    {tab==='Reportes' && <section className="card"><h2>Reportes del paciente</h2><p>Genera y exporta el expediente resumido con datos y atenciones registradas.</p><div className="report-actions"><button className="primary" onClick={()=>download('pdf')}>Exportar PDF</button><button onClick={()=>download('xlsx')}>Exportar Excel</button><button onClick={()=>window.print()}>Imprimir</button></div></section>}


    <Modal open={encOpen} title="Nueva atención clínica" onClose={()=>setEncOpen(false)}><form className="form-grid wide" onSubmit={createEncounter}>
      <Field label="Odontólogo responsable"><select required value={encForm.professionalStaffId} onChange={e=>setEncForm({...encForm,professionalStaffId:e.target.value})}><option value="">Seleccionar…</option>{staff.map(s=><option key={s.id} value={s.id}>{s.lastNames}, {s.firstNames}</option>)}</select></Field>
      <Field label="Consultorio"><select value={encForm.roomId} onChange={e=>setEncForm({...encForm,roomId:e.target.value})}><option value="">Sin asignar</option>{rooms.map(r=><option key={r.id} value={r.id}>{r.name}</option>)}</select></Field>
      <Field label="Motivo de consulta"><textarea required value={encForm.chiefComplaint} onChange={e=>setEncForm({...encForm,chiefComplaint:e.target.value})}/></Field>
      <Field label="Anamnesis"><textarea value={encForm.anamnesis} onChange={e=>setEncForm({...encForm,anamnesis:e.target.value})}/></Field>
      <Field label="Examen clínico"><textarea value={encForm.clinicalExam} onChange={e=>setEncForm({...encForm,clinicalExam:e.target.value})}/></Field>
      <Field label="Evaluación / diagnóstico"><textarea value={encForm.dentalAssessment} onChange={e=>setEncForm({...encForm,dentalAssessment:e.target.value})}/></Field>
      <Field label="Plan de tratamiento"><textarea value={encForm.treatmentPlanSummary} onChange={e=>setEncForm({...encForm,treatmentPlanSummary:e.target.value})}/></Field>
      <Field label="Procedimientos"><textarea value={encForm.proceduresSummary} onChange={e=>setEncForm({...encForm,proceduresSummary:e.target.value})}/></Field>
      <Field label="Indicaciones"><textarea value={encForm.indications} onChange={e=>setEncForm({...encForm,indications:e.target.value})}/></Field>
      <Field label="Evolución"><textarea value={encForm.evolution} onChange={e=>setEncForm({...encForm,evolution:e.target.value})}/></Field>
      <div className="form-actions"><button type="button" onClick={()=>setEncOpen(false)}>Cancelar</button><button className="primary">Guardar atención</button></div>
    </form></Modal>

    <Modal open={Boolean(orderOpen)} title={orderOpen==='rx'?'Nueva orden de Rayos X':'Nueva orden de laboratorio'} onClose={()=>setOrderOpen(null)}>
      <form className="form-grid" onSubmit={createOrder}>
        {orderOpen==='rx' ? <>
          <Field label="Tipo de estudio"><select value={orderForm.studyType} onChange={e=>setOrderForm({...orderForm,studyType:e.target.value})}><option>Periapical</option><option>Bitewing</option><option>Panorámica</option><option>ATM</option><option>Oclusal</option><option>Perfil</option><option>CBCT / Cone Beam</option></select></Field>
          <Field label="Piezas"><input placeholder="Ej. 11, 12, 26" value={orderForm.toothCodes} onChange={e=>setOrderForm({...orderForm,toothCodes:e.target.value})}/></Field>
        </> : <>
          <Field label="Trabajo solicitado"><input required value={orderForm.workType} onChange={e=>setOrderForm({...orderForm,workType:e.target.value})}/></Field>
          <Field label="Pieza"><input value={orderForm.toothCode} onChange={e=>setOrderForm({...orderForm,toothCode:e.target.value})}/></Field>
          <Field label="Fecha requerida"><input type="date" value={orderForm.dueDate} onChange={e=>setOrderForm({...orderForm,dueDate:e.target.value})}/></Field>
        </>}
        <Field label="Indicaciones"><textarea value={orderForm.instructions} onChange={e=>setOrderForm({...orderForm,instructions:e.target.value})}/></Field>
        <div className="form-actions"><button type="button" onClick={()=>setOrderOpen(null)}>Cancelar</button><button className="primary">Crear orden</button></div>
      </form>
    </Modal>

    <Modal open={payOpen} title="Registrar pago" onClose={()=>setPayOpen(false)}><form className="form-grid" onSubmit={savePayment}>
      <Field label="Monto (S/)"><input required type="number" min="0.01" step="0.01" value={payForm.amount} onChange={e=>setPayForm({...payForm,amount:e.target.value})}/></Field>
      <Field label="Método"><select value={payForm.paymentMethod} onChange={e=>setPayForm({...payForm,paymentMethod:e.target.value})}><option>Efectivo</option><option>Yape</option><option>Plin</option><option>Transferencia</option><option>Tarjeta</option><option>Otro</option></select></Field>
      <Field label="Observación"><textarea value={payForm.notes} onChange={e=>setPayForm({...payForm,notes:e.target.value})}/></Field>
      <div className="form-actions"><button type="button" onClick={()=>setPayOpen(false)}>Cancelar</button><button className="primary">Guardar pago</button></div>
    </form></Modal>
  </div>
}
