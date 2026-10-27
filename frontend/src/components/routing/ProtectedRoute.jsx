import { Navigate, useLocation } from 'react-router-dom'
import { useAuth } from '../../auth/AuthContext'
import { hasAnyRole } from '../../config/roles'

export default function ProtectedRoute({ children, roles = [] }) {
  const auth = useAuth()
  const location = useLocation()

  if (!auth.isAuthenticated) {
    return <Navigate to="/login" replace state={{ from: location }} />
  }

  if (!hasAnyRole(auth.roles, roles)) {
    return <Navigate to="/" replace />
  }

  return children
}
