import React, { createContext, useContext, useMemo, useState } from 'react'
import http from '../api/http'
import { hasAnyRole } from '../config/roles'

const AuthContext = createContext(null)
const STORAGE = 'adent.session'

function readSession() {
  try { return JSON.parse(localStorage.getItem(STORAGE)) } catch { return null }
}

export function AuthProvider({ children }) {
  const [session, setSession] = useState(readSession)

  function saveSession(data) {
    localStorage.setItem(STORAGE, JSON.stringify(data))
    setSession(data)
    return data
  }

  async function login({ username, password }) {
    const { data } = await http.post('/auth/login', { username, password })
    return saveSession(data)
  }

  async function register(payload) {
    const { data } = await http.post('/auth/register', payload)
    return saveSession(data)
  }

  function logout() {
    localStorage.removeItem(STORAGE)
    setSession(null)
  }

  const roles = session?.roles || []
  const value = useMemo(() => ({
    session,
    roles,
    tenantId: session?.tenantId || null,
    tenantSlug: session?.tenantSlug || null,
    isAuthenticated: Boolean(session?.accessToken),
    isPlatformAdmin: Boolean(session?.platformAdmin),
    hasRole: (...wanted) => hasAnyRole(roles, wanted),
    login,
    register,
    logout
  }), [session])

  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>
}

export const useAuth = () => useContext(AuthContext)
