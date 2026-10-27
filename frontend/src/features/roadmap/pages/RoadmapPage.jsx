import React from 'react'
import { PageHeader } from '../../../components/common/Ui'
const groups=[
 ['MVP funcional actual',['Autenticación JWT y roles','Multi-tenant','Pacientes','Personal','Sedes y consultorios','Citas','Historia clínica','Ficha de atención','Dashboard','Docker + PostgreSQL']],
 ['Siguiente incremento clínico',['Odontograma gráfico e histórico','Diagnósticos estructurados','Planes y sesiones de tratamiento','Recetas e indicaciones','Referencias','Consentimientos']],
 ['Siguiente incremento documental',['MinIO/S3 para fotos, RX, tomografías y PDF','Autorización de descarga por tenant','Galería antes/durante/después','Checksum y metadatos']],
 ['Escala y producción',['Refresh tokens','Rate limiting','Observabilidad','Backups automatizados','Redis cuando exista necesidad','Nginx/Traefik + HTTPS','CI/CD','Extracción selectiva de servicios']]
]
export default function RoadmapPage(){return <div><PageHeader title="Evolución de ADENT" subtitle="Crecer sin rehacer el núcleo."/><div className="roadmap-grid">{groups.map(([t,items],i)=><section className="card" key={t}><span className="step">0{i+1}</span><h2>{t}</h2><ul className="check-list">{items.map(x=><li key={x}>{x}</li>)}</ul></section>)}</div></div>}
