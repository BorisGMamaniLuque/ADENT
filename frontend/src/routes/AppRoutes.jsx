

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
