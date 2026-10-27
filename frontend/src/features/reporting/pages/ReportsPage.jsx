import React,{useEffect,useState}from'react'
import{Link}from'react-router-dom'
import http,{apiError}from'../../../api/http'
import{useAuth}from'../../../auth/AuthContext'
import{ErrorBanner,PageHeader}from'../../../components/common/Ui'
export default function ReportsPage(){
 const auth=useAuth();const[patients,setPatients]=useState([]),[q,setQ]=useState(''),[error,setError]=useState('')
 async function load(){try{const{data}=await http.get('/patients',{params:{q,page:0,size:100}});setPatients(data.content||[])}catch(e){setError(apiError(e))}}
 useEffect(()=>{if(!auth.hasRole('ADMIN'))load()},[])
 async function download(p,kind){try{const res=await http.get(`/reports/patient/${p.id}/${kind}`,{responseType:'blob'});const url=URL.createObjectURL(res.data);const a=document.createElement('a');a.href=url;a.download=`ADENT_${p.historyNumber}.${kind==='pdf'?'pdf':'xlsx'}`;a.click();URL.revokeObjectURL(url)}catch(e){setError(apiError(e))}}
 if(auth.hasRole('ADMIN')&&!auth.hasRole('OWNER')) return <div><PageHeader title="Reportes administrativos" subtitle="Consulta el Dashboard para indicadores agregados del establecimiento. El rol Administración no accede al expediente clínico individual."/><section className="card"><Link className="primary inline" to="/dashboard">Abrir Dashboard</Link></section></div>
 return <div><PageHeader title="Reportes" subtitle="Genera, exporta e imprime información por paciente."/><ErrorBanner message={error}/><div className="toolbar"><input placeholder="Buscar paciente para reporte" value={q} onChange={e=>setQ(e.target.value)} onKeyDown={e=>e.key==='Enter'&&load()}/><button onClick={load}>Buscar</button></div><section className="card table-card"><div className="table-wrap"><table><thead><tr><th>HC</th><th>Paciente</th><th>Documento</th><th>Acciones</th></tr></thead><tbody>{patients.map(p=><tr key={p.id}><td>{p.historyNumber}</td><td><b>{p.lastNames}, {p.firstNames}</b></td><td>{p.documentType||''} {p.documentNumber||''}</td><td><Link to={`/patients/${p.id}`}>Expediente</Link> · <button className="small" onClick={()=>download(p,'pdf')}>PDF</button> <button className="small" onClick={()=>download(p,'xlsx')}>Excel</button></td></tr>)}</tbody></table></div></section></div>
}
