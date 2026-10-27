import React,{useEffect,useState} from 'react'
import {Link,useParams} from 'react-router-dom'
import {publicSiteAdminApi} from '../../../api/publicSiteAdminApi'
import {apiError} from '../../../api/http'
import {ErrorBanner,Field,PageHeader,SuccessBanner} from '../../../components/common/Ui'

const blankService=()=>({name:'',summary:'',description:'',imageUrl:'',priceFrom:'',displayOrder:0,featured:false,active:true})
const blankTeam=()=>({fullName:'',professionalTitle:'',specialty:'',licenseNumber:'',bio:'',imageUrl:'',displayOrder:0,featured:false,active:true})
const blankGallery=()=>({title:'',description:'',category:'',imageUrl:'',displayOrder:0,active:true})
const empty={enabled:false,published:false,siteSlug:'',templateKey:'MODERN',title:'',businessName:'',logoUrl:'',primaryColor:'#0f766e',secondaryColor:'#0f172a',heroTitle:'',heroSubtitle:'',heroImageUrl:'',aboutTitle:'Sobre nosotros',aboutText:'',mission:'',vision:'',phone:'',whatsapp:'',email:'',address:'',scheduleText:'',mapUrl:'',facebookUrl:'',instagramUrl:'',tiktokUrl:'',services:[],team:[],gallery:[]}

function ArrayEditor({title,items,setItems,type}){
 const add=()=>setItems([...items,(type==='service'?blankService():type==='team'?blankTeam():blankGallery())])
 const update=(i,key,value)=>setItems(items.map((x,n)=>n===i?{...x,[key]:value}:x))
 const remove=i=>setItems(items.filter((_,n)=>n!==i))
 return <section className="card web-editor-section"><div className="section-title"><h2>{title}</h2><button type="button" onClick={add}>+ Agregar</button></div>
  {items.length===0?<p className="muted">Sin elementos. Puedes agregarlos cuando prepares la web de esta organización.</p>:items.map((item,i)=><div className="web-repeat-row" key={i}>
   {type==='service'&&<>
    <Field label="Servicio"><input value={item.name||''} onChange={e=>update(i,'name',e.target.value)}/></Field>
    <Field label="Resumen"><input value={item.summary||''} onChange={e=>update(i,'summary',e.target.value)}/></Field>
    <Field label="Imagen URL"><input value={item.imageUrl||''} onChange={e=>update(i,'imageUrl',e.target.value)}/></Field>
   </>}
   {type==='team'&&<>
    <Field label="Nombre"><input value={item.fullName||''} onChange={e=>update(i,'fullName',e.target.value)}/></Field>
    <Field label="Cargo / título"><input value={item.professionalTitle||''} onChange={e=>update(i,'professionalTitle',e.target.value)}/></Field>
    <Field label="Especialidad"><input value={item.specialty||''} onChange={e=>update(i,'specialty',e.target.value)}/></Field>
    <Field label="Foto URL"><input value={item.imageUrl||''} onChange={e=>update(i,'imageUrl',e.target.value)}/></Field>
   </>}
   {type==='gallery'&&<>
    <Field label="Título"><input value={item.title||''} onChange={e=>update(i,'title',e.target.value)}/></Field>
    <Field label="Categoría"><input value={item.category||''} onChange={e=>update(i,'category',e.target.value)}/></Field>
    <Field label="Imagen URL"><input value={item.imageUrl||''} onChange={e=>update(i,'imageUrl',e.target.value)}/></Field>
   </>}
   <button type="button" className="danger-link" onClick={()=>remove(i)}>Eliminar</button>
  </div>)}</section>
}

export default function PublicSiteConfigPage(){
 const{tenantId}=useParams();const[form,setForm]=useState(empty),[error,setError]=useState(''),[success,setSuccess]=useState(''),[busy,setBusy]=useState(false),[project,setProject]=useState(null),[generating,setGenerating]=useState(false)
 useEffect(()=>{Promise.all([publicSiteAdminApi.get(tenantId),publicSiteAdminApi.projectStatus(tenantId)]).then(([site,status])=>{setForm({...empty,...site.data});setProject(status.data)}).catch(e=>setError(apiError(e)))},[tenantId])
 const set=(key,value)=>setForm(f=>({...f,[key]:value}))
 async function save(){setBusy(true);setError('');setSuccess('');try{const payload={...form,services:(form.services||[]).map(x=>({...x,priceFrom:x.priceFrom===''?null:Number(x.priceFrom)}))};const r=await publicSiteAdminApi.save(tenantId,payload);setForm({...empty,...r.data.site});setProject({siteSlug:r.data.site.siteSlug,projectPath:r.data.projectPath,exists:r.data.projectGenerated});setSuccess(`Configuración guardada y proyecto editable generado en ${r.data.projectPath}`)}catch(e){setError(apiError(e))}finally{setBusy(false)}}
 async function generateProject(){setGenerating(true);setError('');setSuccess('');try{const r=await publicSiteAdminApi.generateProject(tenantId);setProject({siteSlug:r.data.siteSlug,projectPath:r.data.projectPath,exists:r.data.generated});setSuccess(`Proyecto editable disponible en ${r.data.projectPath}`)}catch(e){setError(apiError(e))}finally{setGenerating(false)}}
 const publicUrl=`http://localhost:3001/${form.siteSlug||'slug'}`
 return <div className="public-site-admin"><PageHeader title="Página web publicitaria" subtitle="Configuración independiente para esta organización. No expone información clínica." actions={<><Link className="secondary inline" to="/platform">← Organizaciones</Link><button className="primary" disabled={busy} onClick={save}>{busy?'Guardando…':'Guardar cambios'}</button></>}/>
 <ErrorBanner message={error}/><SuccessBanner message={success}/>
 <section className="card web-project-card"><div><b>Proyecto editable del cliente</b><p className="muted">Al guardar la configuración ADENT crea una copia física de la plantilla para esta organización. Esa carpeta puede abrirse en VS Code o IntelliJ y modificarse sin alterar la plantilla maestra ni otras organizaciones.</p><div className="project-path"><code>{project?.projectPath||`public-web/generated-sites/${form.siteSlug||'slug'}`}</code></div></div><div><span className={project?.exists?'status-ok':'status-warn'}>{project?.exists?'Carpeta creada':'Carpeta aún no creada'}</span><button type="button" className="secondary" disabled={generating} onClick={generateProject}>{generating?'Generando…':'Generar / verificar carpeta'}</button></div></section>
 <section className="card web-status-card"><div><b>Estado de la web</b><p className="muted">Habilitar permite preparar el sitio. Publicar lo hace visible en el frontend público.</p></div><label><input type="checkbox" checked={!!form.enabled} onChange={e=>set('enabled',e.target.checked)}/> Habilitada</label><label><input type="checkbox" checked={!!form.published} disabled={!form.enabled} onChange={e=>set('published',e.target.checked)}/> Publicada</label><a href={publicUrl} target="_blank" rel="noreferrer">Vista pública ↗</a></section>
 <div className="web-editor-grid">
  <section className="card web-editor-section"><h2>Identidad y diseño</h2><div className="form-grid">
   <Field label="Slug público"><input value={form.siteSlug||''} onChange={e=>set('siteSlug',e.target.value)}/></Field>
   <Field label="Nombre comercial"><input value={form.businessName||''} onChange={e=>set('businessName',e.target.value)}/></Field>
   <Field label="Plantilla"><select value={form.templateKey||'MODERN'} onChange={e=>set('templateKey',e.target.value)}><option value="MODERN">Moderna</option><option value="MINIMAL">Minimal</option><option value="PREMIUM">Premium</option></select></Field>
   <Field label="Logo URL"><input value={form.logoUrl||''} onChange={e=>set('logoUrl',e.target.value)}/></Field>
   <Field label="Color principal"><input type="color" value={form.primaryColor||'#0f766e'} onChange={e=>set('primaryColor',e.target.value)}/></Field>
   <Field label="Color secundario"><input type="color" value={form.secondaryColor||'#0f172a'} onChange={e=>set('secondaryColor',e.target.value)}/></Field>
  </div></section>
  <section className="card web-editor-section"><h2>Inicio</h2><div className="form-grid">
   <Field label="Título principal"><input value={form.heroTitle||''} onChange={e=>set('heroTitle',e.target.value)}/></Field>
   <Field label="Imagen de portada"><input value={form.heroImageUrl||''} onChange={e=>set('heroImageUrl',e.target.value)}/></Field>
   <Field label="Subtítulo"><textarea value={form.heroSubtitle||''} onChange={e=>set('heroSubtitle',e.target.value)}/></Field>
  </div></section>
  <section className="card web-editor-section"><h2>Nosotros</h2><div className="form-grid">
   <Field label="Título"><input value={form.aboutTitle||''} onChange={e=>set('aboutTitle',e.target.value)}/></Field>
   <Field label="Presentación"><textarea value={form.aboutText||''} onChange={e=>set('aboutText',e.target.value)}/></Field>
   <Field label="Misión"><textarea value={form.mission||''} onChange={e=>set('mission',e.target.value)}/></Field>
   <Field label="Visión"><textarea value={form.vision||''} onChange={e=>set('vision',e.target.value)}/></Field>
  </div></section>
  <section className="card web-editor-section"><h2>Contacto</h2><div className="form-grid">
   {['phone','whatsapp','email','address','scheduleText','mapUrl','facebookUrl','instagramUrl','tiktokUrl'].map(k=><Field key={k} label={{phone:'Teléfono',whatsapp:'WhatsApp',email:'Correo',address:'Dirección',scheduleText:'Horario',mapUrl:'Mapa / URL',facebookUrl:'Facebook',instagramUrl:'Instagram',tiktokUrl:'TikTok'}[k]}><input value={form[k]||''} onChange={e=>set(k,e.target.value)}/></Field>)}
  </div></section>
 </div>
 <ArrayEditor title="Servicios" items={form.services||[]} setItems={v=>set('services',v)} type="service"/>
 <ArrayEditor title="Equipo" items={form.team||[]} setItems={v=>set('team',v)} type="team"/>
 <ArrayEditor title="Galería" items={form.gallery||[]} setItems={v=>set('gallery',v)} type="gallery"/>
 <section className="card"><h2>Estructura pública incluida</h2><div className="public-structure">Inicio <span>→</span> Nosotros <span>→</span> Servicios <span>→</span> Equipo <span>→</span> Galería <span>→</span> Contacto <span>→</span> Reservar cita</div><p className="muted">La plantilla maestra está en <b>public-web/template-base</b>. Para personalizar este cliente trabaja únicamente en <b>{project?.projectPath||`public-web/generated-sites/${form.siteSlug||'slug'}`}</b>.</p></section>
 </div>
}
