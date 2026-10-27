import React, { useMemo, useState } from 'react'
import { NavLink, Outlet } from 'react-router-dom'
import { useAuth } from '../../auth/AuthContext'
import { NAV_ITEMS } from '../../config/navigation'
import { hasAnyRole, ROLE_LABELS } from '../../config/roles'

export default function AppLayout() {
  const auth = useAuth()
  const [open, setOpen] = useState(false)
  const orgName = auth.isPlatformAdmin ? 'ADENT' : (auth.session?.organizationName || 'ADENT')
  const logoText = auth.isPlatformAdmin ? 'A' : (auth.session?.organizationLogoText || orgName.slice(0, 2).toUpperCase())
  const logoUrl = auth.session?.organizationLogoUrl

  const menu = useMemo(
    () => NAV_ITEMS.filter(item => hasAnyRole(auth.roles, item.access)),
    [auth.roles]
  )

  return <div className="app-shell">
    <aside className={`sidebar ${open ? 'open' : ''}`}>
      <div className="brand">
        {logoUrl
          ? <img className="brand-image" src={logoUrl} alt={orgName}/>
          : <div className="brand-mark">{logoText}</div>}
        <div><b>{orgName}</b><span>{auth.isPlatformAdmin ? 'Plataforma ADENT' : 'Sistema Dental Digital'}</span></div>
      </div>

      <nav>
        {menu.map(item =>
          <NavLink
            key={item.to}
            to={item.to}
            end={item.to === '/'}
            onClick={() => setOpen(false)}
            className={({ isActive }) => isActive ? 'active' : ''}
          >
            {item.label}
          </NavLink>
        )}
      </nav>

      <div className="sidebar-foot">
        <small>{auth.isPlatformAdmin ? 'Administración de plataforma' : orgName}</small>
        <strong>{auth.session?.username}</strong>
        <span>{auth.roles.map(r=>ROLE_LABELS[r]||r).join(' · ')}</span>
        <button className="secondary" onClick={auth.logout}>Cerrar sesión</button>
      </div>
    </aside>

    <div className="main">
      <header className="topbar">
        <button className="menu-btn" onClick={() => setOpen(!open)}>☰</button>
        <div><b>{orgName}</b><span> · ADENT</span></div>
        <span className="top-user">{auth.session?.username}</span>
      </header>
      <main className="content"><Outlet /></main>
    </div>

    {open && <div className="scrim" onClick={() => setOpen(false)} />}
  </div>
}
