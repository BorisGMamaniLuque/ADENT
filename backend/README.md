# ADENT - Backend MVP escalable

Proyecto base para **ADENT - Sistema Dental Digital**, alineado al APF1. Está construido como **monolito modular** para mantener simplicidad en esta etapa y permitir separar módulos en servicios independientes solo cuando la carga o el negocio lo justifiquen.

## Stack
- Java 21
- Spring Boot 3.5.x
- Spring Security + JWT
- Spring Data JPA
- PostgreSQL + Flyway
- OpenAPI / Swagger
- Docker Compose
- MinIO y Redis preparados como perfiles opcionales

## Módulos actuales
- `platform`: tenant y organización.
- `identity`: usuarios, membresías, roles y JWT.
- `structure`: sedes, pisos y consultorios.
- `staff`: personal.
- `patient`: pacientes, búsqueda y prevención de duplicados.
- `appointment`: citas.
- `clinical`: historia clínica y atenciones BORRADOR / FINALIZADA / ADENDA.
- `reporting`: dashboard MVP.
- `audit`: base para trazabilidad.
- Base de datos preparada para odontograma, tratamientos, recetas, referencias, documentos y finanzas.

## Arranque con Docker
```bash
copy .env.example .env
docker compose up -d --build
```

API: `http://localhost:8080`
Swagger: `http://localhost:8080/swagger-ui.html`
Health: `http://localhost:8080/actuator/health`

## Usuarios de demostración
Cambiar en `.env` antes de un entorno real.
- Super Admin: `superadmin / ChangeMe123!` (login sin tenantSlug)
- Owner: `owner / Owner123!` con tenantSlug `demo-dental`

### Login Owner
```json
POST /api/auth/login
{
  "username": "owner",
  "password": "Owner123!",
  "tenantSlug": "demo-dental"
}
```

## Principio de seguridad multi-tenant
El `tenantId` se toma del JWT. Los endpoints funcionales **no reciben tenantId desde el frontend** para decidir el ámbito de datos. Las consultas de paciente, citas, personal y atención siempre se ejecutan con el tenant autenticado.

## Estado del MVP
Funcional ahora: autenticación, tenant, organización semilla, estructura, personal, pacientes, citas, historia principal, atención clínica y dashboard básico.
Preparado en esquema: odontograma, diagnósticos, procedimientos, tratamiento, recetas, indicaciones, referencias, archivos, consentimientos, cargos, pagos, ingresos, egresos y auditoría.

Revisar `docs/IMPLEMENTATION_STATUS.md` y `docs/ROADMAP.md` antes de ampliar.
