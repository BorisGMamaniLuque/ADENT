import React,{useEffect,useState} from 'react'
import http,{apiError} from '../../../api/http'
import {EmptyState,ErrorBanner,Field,Modal,PageHeader} from '../../../components/common/Ui'
export default function StructurePage(){
 const[branches,setBranches]=useState([]),[floors,setFloors]=useState([]),[rooms,setRooms]=useState([]),[error,setError]=useState('')
 const[modal,setModal]=useState(null),[form,setForm]=useState({})
 async function load(){try{const[b,f,r]=await Promise.all([http.get('/structure/branches'),http.get('/structure/floors'),http.get('/structure/rooms')]);setBranches(b.data||[]);setFloors(f.data||[]);setRooms(r.data||[])}catch(e){setError(apiError(e))}}
 useEffect(()=>{load()},[])
 function add(type){setModal(type);setForm(type==='branch'?{name:'',address:''}:type==='floor'?{branchId:branches[0]?.id||'',name:'',floorNumber:1}:{floorId:floors[0]?.id||'',name:'',code:''})}
 async function save(e){e.preventDefault();try{await http.post(`/structure/${modal==='branch'?'branches':modal==='floor'?'floors':'rooms'}`,form);setModal(null);load()}catch(e){setError(apiError(e))}}
 return <div><PageHeader title="Sedes y consultorios" subtitle="Organización → Sede → Piso → Consultorio/Ambiente."/><ErrorBanner message={error}/>
 <div className="three-grid">
  <section className="card"><div className="section-title"><h2>Sedes</h2><button onClick={()=>add('branch')}>+</button></div>{branches.length?branches.map(x=><div className="list-row" key={x.id}><b>{x.name}</b><span>{x.address||'Sin dirección'}</span></div>):<EmptyState/>}</section>
  <section className="card"><div className="section-title"><h2>Pisos</h2><button disabled={!branches.length} onClick={()=>add('floor')}>+</button></div>{floors.length?floors.map(x=><div className="list-row" key={x.id}><b>{x.name}</b><span>Nivel {x.floorNumber??'—'}</span></div>):<EmptyState/>}</section>
  <section className="card"><div className="section-title"><h2>Consultorios</h2><button disabled={!floors.length} onClick={()=>add('room')}>+</button></div>{rooms.length?rooms.map(x=><div className="list-row" key={x.id}><b>{x.name}</b><span>{x.code||'Sin código'}</span></div>):<EmptyState/>}</section>
 </div>
 <Modal open={!!modal} title={`Nuevo ${modal==='branch'?'sede':modal==='floor'?'piso':'consultorio'}`} onClose={()=>setModal(null)}><form className="form-grid" onSubmit={save}>
 {modal==='floor'&&<Field label="Sede"><select required value={form.branchId||''} onChange={e=>setForm({...form,branchId:e.target.value})}>{branches.map(x=><option value={x.id} key={x.id}>{x.name}</option>)}</select></Field>}
 {modal==='room'&&<Field label="Piso"><select required value={form.floorId||''} onChange={e=>setForm({...form,floorId:e.target.value})}>{floors.map(x=><option value={x.id} key={x.id}>{x.name}</option>)}</select></Field>}
 <Field label="Nombre"><input required value={form.name||''} onChange={e=>setForm({...form,name:e.target.value})}/></Field>
 {modal==='branch'&&<Field label="Dirección"><input value={form.address||''} onChange={e=>setForm({...form,address:e.target.value})}/></Field>}
 {modal==='floor'&&<Field label="Número de piso"><input type="number" value={form.floorNumber??1} onChange={e=>setForm({...form,floorNumber:Number(e.target.value)})}/></Field>}
 {modal==='room'&&<Field label="Código"><input value={form.code||''} onChange={e=>setForm({...form,code:e.target.value})}/></Field>}
 <div className="form-actions"><button type="button" onClick={()=>setModal(null)}>Cancelar</button><button className="primary">Guardar</button></div>
 </form></Modal></div>
}
