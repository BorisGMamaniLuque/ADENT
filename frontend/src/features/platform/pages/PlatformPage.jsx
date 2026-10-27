import React,{useEffect,useState} from 'react'
import {Link} from 'react-router-dom'
import http,{apiError} from '../../../api/http'
import {EmptyState,ErrorBanner,Field,Modal,PageHeader} from '../../../components/common/Ui'
export default function PlatformPage(){
 const[rows,setRows]=useState([]),[open,setOpen]=useState(false),[error,setError]=useState('')
 const[form,setForm]=useState({slug:'',name:'',legalName:'',tradeName:'',ruc:''})
 async function load(){try{setRows((await http.get('/platform/tenants')).data||[])}catch(e){setError(apiError(e))}}
 useEffect(()=>{load()},[])
 async function save(e){e.preventDefault();try{await http.post('/platform/tenants',form);setOpen(false);setForm({slug:'',name:'',legalName:'',tradeName:'',ruc:''});load()}catch(e){setError(apiError(e))}}
 async function status(id,value){try{await http.patch(`/platform/tenants/${id}/status`,null,{params:{status:value}});load()}catch(e){setError(apiError(e))}}
 async function toggleWeb(id,enabled){try{await http.patch(`/platform/public-sites/tenant/${id}/enabled`,null,{params:{enabled}});load()}catch(e){setError(apiError(e))}}
 return <div><PageHeader title="Organizaciones" subtitle="Gobierno multi-tenant de ADENT y control de páginas web publicitarias." actions={<button className="primary" onClick={()=>setOpen(true)}>+ Nueva organización</button>}/><ErrorBanner message={error}/>
 <section className="card table-card">{rows.length===0?<EmptyState/>:<div className="table-wrap"><table><thead><tr><th>Organización</th><th>Slug</th><th>Estado</th><th>ADENT</th><th>Web pública</th><th>Publicación</th><th>Acción</th></tr></thead><tbody>{rows.map(t=><tr key={t.id}><td><b>{t.name}</b></td><td>{t.slug}</td><td><select value={t.status} onChange={e=>status(t.id,e.target.value)}><option>ACTIVE</option><option>SUSPENDED</option><option>BLOCKED</option></select></td><td>{t.active?'Activo':'Inactivo'}</td><td><label className="switch-line"><input type="checkbox" checked={!!t.webEnabled} onChange={e=>toggleWeb(t.id,e.target.checked)}/> {t.webEnabled?'Habilitada':'No habilitada'}</label></td><td>{t.webPublished?'Publicada':'Borrador'}</td><td><Link className="secondary inline small" to={`/platform/public-site/${t.id}`}>Configurar web</Link></td></tr>)}</tbody></table></div>}</section>
 <Modal open={open} title="Nueva organización" onClose={()=>setOpen(false)}><form className="form-grid" onSubmit={save}>
 {Object.entries(form).map(([k,v])=><Field key={k} label={{slug:'Slug',name:'Nombre visible',legalName:'Razón social',tradeName:'Nombre comercial',ruc:'RUC'}[k]}><input required={['slug','name','legalName'].includes(k)} value={v} onChange={e=>setForm({...form,[k]:e.target.value})}/></Field>)}
 <div className="form-actions"><button type="button" onClick={()=>setOpen(false)}>Cancelar</button><button className="primary">Crear organización</button></div></form></Modal></div>
}
