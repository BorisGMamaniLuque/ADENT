import React, { useState } from 'react'
import { Link, useNavigate } from 'react-router-dom'
import { useAuth } from '../../../auth/AuthContext'
import { apiError } from '../../../api/http'

const initial = {
  establishmentName:'', legalName:'', establishmentType:'CLINIC', ruc:'', address:'',
  establishmentPhone:'', establishmentEmail:'', ownerFirstNames:'', ownerLastNames:'',
  ownerDocumentType:'DNI', ownerDocumentNumber:'', ownerSpecialty:'', username:'', email:'', phone:'', password:'', confirmPassword:''
}

export default function RegisterPage(){
  const auth=useAuth(), navigate=useNavigate()
  const[form,setForm]=useState(initial),[error,setError]=useState(''),[busy,setBusy]=useState(false)
  const initials=(form.establishmentName||'ADENT').trim().split(/\s+/).filter(Boolean).slice(0,2).map(x=>x[0]?.toUpperCase()).join('')||'A'
  const change=e=>setForm({...form,[e.target.name]:e.target.value})

  async function submit(e){
    e.preventDefault(); setError('')
    if(form.password!==form.confirmPassword){setError('Las contraseñas no coinciden');return}
    setBusy(true)
    try{
      const {confirmPassword,...payload}=form
      await auth.register(payload)
      navigate('/',{replace:true})
    }catch(e){setError(apiError(e))}finally{setBusy(false)}
  }

  return <div className="auth-page">
    <div className="register-shell">
      <div className="register-head"><div className="logo-big">{initials}</div><div><h1>{form.establishmentName || 'Crear cuenta en ADENT'}</h1><p>{form.establishmentName ? 'Así se identificará tu establecimiento dentro de ADENT.' : 'Registra tu establecimiento y crea el usuario propietario.'}</p></div></div>
      {error&&<div className="alert error">{error}</div>}
      <form onSubmit={submit} className="register-form">
        <section className="form-section"><div className="form-section-title"><b>1. Establecimiento</b><span>Identidad de la clínica o consultorio</span></div><div className="form-grid">
          <label className="field full-span"><span>Nombre del establecimiento *</span><input required name="establishmentName" value={form.establishmentName} onChange={change} placeholder="Ej. Clínica Dental Villanueva"/><small>Este nombre aparecerá como marca dentro de la cuenta.</small></label>
          <label className="field"><span>Razón social</span><input name="legalName" value={form.legalName} onChange={change}/></label>
          <label className="field"><span>Tipo</span><select name="establishmentType" value={form.establishmentType} onChange={change}><option value="CLINIC">Clínica dental</option><option value="CONSULTING_ROOM">Consultorio dental</option><option value="DENTAL_CENTER">Centro odontológico</option></select></label>
          <label className="field"><span>RUC</span><input name="ruc" value={form.ruc} onChange={change} maxLength="20"/></label>
          <label className="field"><span>Teléfono del establecimiento</span><input name="establishmentPhone" value={form.establishmentPhone} onChange={change}/></label>
          <label className="field"><span>Correo del establecimiento</span><input type="email" name="establishmentEmail" value={form.establishmentEmail} onChange={change}/></label>
          <label className="field full-span"><span>Dirección</span><input name="address" value={form.address} onChange={change}/></label>
        </div></section>

        <section className="form-section"><div className="form-section-title"><b>2. Responsable / propietario</b><span>Primer miembro del personal</span></div><div className="form-grid">
          <label className="field"><span>Nombres *</span><input required name="ownerFirstNames" value={form.ownerFirstNames} onChange={change}/></label>
          <label className="field"><span>Apellidos *</span><input required name="ownerLastNames" value={form.ownerLastNames} onChange={change}/></label>
          <label className="field"><span>Tipo de documento</span><select name="ownerDocumentType" value={form.ownerDocumentType} onChange={change}><option>DNI</option><option>CE</option><option>PASSPORT</option></select></label>
          <label className="field"><span>Número de documento</span><input name="ownerDocumentNumber" value={form.ownerDocumentNumber} onChange={change}/></label>
          <label className="field full-span"><span>Especialidad / cargo</span><input name="ownerSpecialty" value={form.ownerSpecialty} onChange={change} placeholder="Ej. Cirujano dentista / Propietario"/></label>
        </div></section>

        <section className="form-section"><div className="form-section-title"><b>3. Acceso y recuperación</b><span>Datos privados para iniciar sesión y recuperar la cuenta</span></div><div className="form-grid">
          <label className="field"><span>Usuario *</span><input required minLength="4" name="username" value={form.username} onChange={change} autoComplete="username"/></label>
          <label className="field"><span>Correo personal *</span><input required type="email" name="email" value={form.email} onChange={change}/></label>
          <label className="field"><span>Celular personal *</span><input required name="phone" value={form.phone} onChange={change} placeholder="Ej. 987654321"/></label>
          <div></div>
          <label className="field"><span>Contraseña *</span><input required minLength="8" type="password" name="password" value={form.password} onChange={change} autoComplete="new-password"/></label>
          <label className="field"><span>Confirmar contraseña *</span><input required minLength="8" type="password" name="confirmPassword" value={form.confirmPassword} onChange={change} autoComplete="new-password"/></label>
        </div></section>

        <div className="register-actions"><Link to="/login">Ya tengo una cuenta</Link><button className="primary" disabled={busy}>{busy?'Creando establecimiento…':'Crear cuenta y continuar'}</button></div>
      </form>
    </div>
  </div>
}
