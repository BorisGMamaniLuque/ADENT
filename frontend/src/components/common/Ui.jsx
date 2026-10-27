import React from 'react'

export function PageHeader({ title, subtitle, actions }) {
  return <div className="page-header">
    <div><h1>{title}</h1>{subtitle && <p>{subtitle}</p>}</div>
    {actions && <div className="page-actions">{actions}</div>}
  </div>
}

export function StatCard({ label, value, hint }) {
  return <article className="stat-card"><span>{label}</span><strong>{value ?? '—'}</strong>{hint && <small>{hint}</small>}</article>
}

export function EmptyState({ title='Sin registros', text='Aún no hay información para mostrar.' }) {
  return <div className="empty"><strong>{title}</strong><p>{text}</p></div>
}

export function Loading() { return <div className="loading">Cargando…</div> }

export function ErrorBanner({ message }) {
  if (!message) return null
  return <div className="alert error">{message}</div>
}

export function SuccessBanner({ message }) {
  if (!message) return null
  return <div className="alert success">{message}</div>
}

export function Field({ label, children, hint }) {
  return <label className="field"><span>{label}</span>{children}{hint && <small>{hint}</small>}</label>
}

export function Modal({ open, title, onClose, children }) {
  if (!open) return null
  return <div className="modal-backdrop" onMouseDown={onClose}>
    <div className="modal" onMouseDown={e=>e.stopPropagation()}>
      <div className="modal-head"><h2>{title}</h2><button className="icon-btn" onClick={onClose}>×</button></div>
      {children}
    </div>
  </div>
}
