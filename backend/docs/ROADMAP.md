# Roadmap técnico sugerido

## Incremento 1 - cerrar el MVP clínico
- CRUD Super Admin de organizaciones, límites y módulos.
- Usuarios por tenant, asignación de staff y roles.
- Alergias, condiciones, medicamentos y antecedentes.
- Odontograma visual + versiones.
- Diagnósticos y procedimientos estructurados.
- Plan de tratamiento y evolución.
- Recetas, indicaciones y referencias.

## Incremento 2 - documentos y evidencias
- `ObjectStoragePort` + adaptador MinIO/S3.
- Upload seguro con autorización por tenant.
- Consentimientos y versionado documental.
- Límites de tamaño, tipos MIME y antivirus en despliegue real.

## Incremento 3 - gestión
- Catálogo de servicios.
- Cargos por atención, pagos, ingresos y egresos.
- Dashboard por médico, consultorio, sede y periodo.
- Exportación PDF/Excel.

## Incremento 4 - calidad y producción
- Auditoría automática mediante eventos de dominio/interceptores.
- Testcontainers PostgreSQL.
- Observabilidad, métricas y logs estructurados.
- Rate limiting en proxy/API.
- Backups verificados y recuperación documentada.
- CI/CD GitHub Actions.

## Incremento 5 - escala real
Primero optimizar índices, queries, caché y almacenamiento. Solo si existen razones medibles, extraer módulos de alta carga (por ejemplo archivos/notificaciones/reporting) como servicios independientes.
