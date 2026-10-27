import {ACCESS} from './roles'
export const NAV_ITEMS=[
 {to:'/',label:'Inicio',access:[]},
 {to:'/dashboard',label:'Dashboard',access:ACCESS.DASHBOARD},
 {to:'/reception',label:'Recepción',access:ACCESS.RECEPTION},
 {to:'/patients',label:'Pacientes',access:ACCESS.PATIENTS},
 {to:'/appointments',label:'Citas',access:ACCESS.APPOINTMENTS},
 {to:'/staff',label:'Personal',access:ACCESS.STAFF},
 {to:'/structure',label:'Sedes y consultorios',access:ACCESS.STRUCTURE},
 {to:'/users',label:'Usuarios y roles',access:ACCESS.USERS},
 {to:'/attendance',label:'Control de asistencia',access:ACCESS.ATTENDANCE},
 {to:'/finance',label:'Tarifario y caja',access:ACCESS.FINANCE},
 {to:'/laboratory',label:'Laboratorio dental',access:ACCESS.LAB},
 {to:'/radiology',label:'Rayos X',access:ACCESS.XRAY},
 {to:'/reports',label:'Reportes',access:ACCESS.REPORTS},
 {to:'/website',label:'Página web',access:ACCESS.WEBSITE_REQUEST},
 {to:'/security-reports',label:'Reportes de seguridad',access:ACCESS.SECURITY_REPORTS},
 {to:'/cleaning-reports',label:'Reportes de limpieza',access:ACCESS.CLEANING_REPORTS},
 {to:'/platform',label:'Organizaciones',access:ACCESS.PLATFORM},
 {to:'/platform/web-requests',label:'Solicitudes web',access:ACCESS.PLATFORM}
]
