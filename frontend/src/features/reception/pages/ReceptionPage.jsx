import React,{useEffect,useState} from 'react';
import {Link} from 'react-router-dom';
import http,{apiError} from '../../../api/http';
import {PageHeader,ErrorBanner} from '../../../components/common/Ui';
export default function ReceptionPage(){
 const [patients,setPatients]=useState([]),[q,setQ]=useState(''),[error,setError]=useState(''),[loading,setLoading]=useState(false);
 async function load(value=q){setLoading(true);setError('');try{const {data}=await http.get('/patients',{params:{q:value,page:0,size:100}});setPatients(data.content||[])}catch(err){setError(apiError(err))}finally{setLoading(false)}}
 useEffect(()=>{load('')},[]);
 return <div>
 <PageHeader title="Recepción" subtitle="Admisión, búsqueda de pacientes, historias clínicas y agenda de la clínica." actions={<Link className="primary inline" to="/patients">Registrar paciente</Link>}/>
 <ErrorBanner message={error}/>
 <div className="reception-metrics"><article><strong>{patients.length}</strong><span>Pacientes encontrados</span></article><article><strong>Agenda</strong><Link to="/appointments">Ver citas y disponibilidad</Link></article><article><strong>Admisión</strong><span>Información centralizada por paciente</span></article></div>
 <section className="card"><div className="toolbar"><input aria-label="Buscar paciente" placeholder="Nombre, DNI, RUT o número de historia" value={q} onChange={e=>setQ(e.target.value)} onKeyDown={e=>e.key==='Enter'&&load(q)}/><button className="primary" onClick={()=>load(q)} disabled={loading}>{loading?'Buscando…':'Buscar'}</button><Link to="/appointments">Programar cita</Link></div>
 <div className="table-wrap"><table><thead><tr><th>Historia clínica</th><th>Paciente</th><th>Documento</th><th>Contacto</th><th>Acción</th></tr></thead><tbody>{patients.map(p=><tr key={p.id}><td>{p.historyNumber||'—'}</td><td><strong>{p.lastNames}, {p.firstNames}</strong></td><td>{p.documentType||'—'} {p.documentNumber||''}</td><td>{p.phone||'—'}</td><td><Link to={`/patients/${p.id}`}>Abrir expediente</Link></td></tr>)}</tbody></table>{!patients.length&&!loading&&<p className="muted">No se encontraron pacientes. Puedes registrar uno desde Pacientes.</p>}</div></section>
 </div>
}
