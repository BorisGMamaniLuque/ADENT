import React from 'react'
import {Navigate,Route,Routes} from 'react-router-dom'
import {useAuth} from '../auth/AuthContext'
import {ACCESS} from '../config/roles'
import ProtectedRoute from '../components/routing/ProtectedRoute'
import AppLayout from '../components/layout/AppLayout'
import LoginPage from '../features/identity/pages/LoginPage'
import RegisterPage from '../features/identity/pages/RegisterPage'
import ForgotPasswordPage from '../features/identity/pages/ForgotPasswordPage'
import UsersPage from '../features/identity/pages/UsersPage'
import PatientsPage from '../features/patients/pages/PatientsPage'
import PatientDetailPage from '../features/patients/pages/PatientDetailPage'
import ReceptionPage from '../features/reception/pages/ReceptionPage'
import AppointmentsPage from '../features/appointments/pages/AppointmentsPage'
import StaffPage from '../features/staff/pages/StaffPage'
import StructurePage from '../features/structure/pages/StructurePage'
import DashboardPage from '../features/reporting/pages/DashboardPage'
import ReportsPage from '../features/reporting/pages/ReportsPage'
import PlatformPage from '../features/platform/pages/PlatformPage'
import PublicSiteConfigPage from '../features/platform/pages/PublicSiteConfigPage'
import WebsiteRequestsAdminPage from '../features/platform/pages/WebsiteRequestsAdminPage'
import WebsiteRequestPage from '../features/publicsite/pages/WebsiteRequestPage'
import RoleHomePage from '../features/home/RoleHomePage'
import FinancePage from '../features/finance/pages/FinancePage'
import AttendancePage from '../features/attendance/pages/AttendancePage'
import LaboratoryPage from '../features/orders/pages/LaboratoryPage'
import RadiologyPage from '../features/orders/pages/RadiologyPage'
import OperationalReportsPage from '../features/operations/pages/OperationalReportsPage'

const Guard=({roles,children})=><ProtectedRoute roles={roles}>{children}</ProtectedRoute>
export default function AppRoutes(){
 const auth=useAuth()
 return <Routes>
  <Route path="/login" element={auth.isAuthenticated?<Navigate to="/" replace/>:<LoginPage/>}/>
  <Route path="/register" element={auth.isAuthenticated?<Navigate to="/" replace/>:<RegisterPage/>}/>
  <Route path="/forgot-password" element={auth.isAuthenticated?<Navigate to="/" replace/>:<ForgotPasswordPage/>}/>
  <Route element={<Guard><AppLayout/></Guard>}>
   <Route index element={<RoleHomePage/>}/>
   <Route path="dashboard" element={<Guard roles={ACCESS.DASHBOARD}><DashboardPage/></Guard>}/>
   <Route path="reception" element={<Guard roles={ACCESS.RECEPTION}><ReceptionPage/></Guard>}/>
   <Route path="patients" element={<Guard roles={ACCESS.PATIENTS}><PatientsPage/></Guard>}/>
   <Route path="patients/:id" element={<Guard roles={ACCESS.PATIENTS}><PatientDetailPage/></Guard>}/>
   <Route path="appointments" element={<Guard roles={ACCESS.APPOINTMENTS}><AppointmentsPage/></Guard>}/>
   <Route path="staff" element={<Guard roles={ACCESS.STAFF}><StaffPage/></Guard>}/>
   <Route path="structure" element={<Guard roles={ACCESS.STRUCTURE}><StructurePage/></Guard>}/>
   <Route path="users" element={<Guard roles={ACCESS.USERS}><UsersPage/></Guard>}/>
   <Route path="attendance" element={<Guard roles={ACCESS.ATTENDANCE}><AttendancePage/></Guard>}/>
   <Route path="finance" element={<Guard roles={ACCESS.FINANCE}><FinancePage/></Guard>}/>
   <Route path="laboratory" element={<Guard roles={ACCESS.LAB}><LaboratoryPage/></Guard>}/>
   <Route path="radiology" element={<Guard roles={ACCESS.XRAY}><RadiologyPage/></Guard>}/>
   <Route path="reports" element={<Guard roles={ACCESS.REPORTS}><ReportsPage/></Guard>}/>
   <Route path="security-reports" element={<Guard roles={ACCESS.SECURITY_REPORTS}><OperationalReportsPage type="SECURITY"/></Guard>}/>
   <Route path="cleaning-reports" element={<Guard roles={ACCESS.CLEANING_REPORTS}><OperationalReportsPage type="CLEANING"/></Guard>}/>
   <Route path="website" element={<Guard roles={ACCESS.WEBSITE_REQUEST}><WebsiteRequestPage/></Guard>}/>
   <Route path="platform" element={<Guard roles={ACCESS.PLATFORM}><PlatformPage/></Guard>}/>
   <Route path="platform/web-requests" element={<Guard roles={ACCESS.PLATFORM}><WebsiteRequestsAdminPage/></Guard>}/>
   <Route path="platform/public-site/:tenantId" element={<Guard roles={ACCESS.PLATFORM}><PublicSiteConfigPage/></Guard>}/>
  </Route>
  <Route path="*" element={<Navigate to="/" replace/>}/>
 </Routes>
}
