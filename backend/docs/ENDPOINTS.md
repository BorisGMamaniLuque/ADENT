# Endpoints implementados

| Método | Ruta | Rol principal | Función |
|---|---|---|---|
| POST | `/api/auth/login` | Público | Obtener JWT |
| GET/POST | `/api/platform/tenants` | SUPER_ADMIN | Administrar organizaciones base |
| PATCH | `/api/platform/tenants/{id}/status` | SUPER_ADMIN | Activar/suspender/bloquear |
| GET/POST | `/api/users` | OWNER/ADMIN | Usuarios del tenant y roles |
| GET/POST | `/api/structure/branches` | OWNER/ADMIN | Sedes |
| POST | `/api/structure/floors` | OWNER/ADMIN | Pisos |
| GET/POST | `/api/structure/rooms` | OWNER/ADMIN | Consultorios |
| GET/POST | `/api/staff` | OWNER/ADMIN | Personal |
| GET/POST/PUT | `/api/patients` | Según rol | Pacientes y búsqueda |
| GET/POST/PATCH | `/api/appointments` | Según rol | Agenda |
| GET/POST/PUT | `/api/clinical/encounters` | Clínico | Ficha de atención |
| POST | `/api/clinical/encounters/{id}/finalize` | DENTIST/OWNER | Cerrar ficha |
| POST | `/api/clinical/encounters/{id}/addendum` | DENTIST/OWNER | Corrección trazable |
| GET | `/api/reports/dashboard` | OWNER/ADMIN | Indicadores iniciales |
