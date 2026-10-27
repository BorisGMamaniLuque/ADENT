# Estado de implementación ADENT

## Entregado en esta etapa
| Área | Estado | Comentario |
|---|---|---|
| Arquitectura modular | Implementado | Paquetes por dominio, sin microservicios prematuros. |
| Multi-tenant | Implementado | Tenant en JWT y filtros explícitos en repositorios del MVP. |
| Autenticación | Implementado | JWT, BCrypt y roles. |
| Super Admin | Base | Login y modelo de plataforma; CRUD global de tenants queda como siguiente incremento. |
| Estructura clínica | Implementado | Sede, piso, consultorio. |
| Personal | Implementado | Registro/listado base. |
| Pacientes | Implementado | CRUD esencial, búsqueda, historia automática, unicidad por tenant+documento. |
| Citas | Implementado | Registro, consulta por rango y cambio de estado. |
| Historia clínica | Implementado | Una historia principal por paciente. |
| Ficha de atención | Implementado MVP | BORRADOR, FINALIZADA y ADENDA con secciones clínicas principales. |
| Odontograma | Esquema listo | Tablas creadas; falta API y frontend gráfico. |
| Diagnóstico/procedimiento | Esquema listo | Tablas creadas; falta API dedicada. |
| Tratamientos/evolución | Esquema listo | Falta API dedicada. |
| Recetas/indicaciones | Esquema listo | Falta API dedicada. |
| Archivos/consentimientos | Esquema listo | Falta adaptador MinIO/S3 y endpoints. |
| Finanzas | Esquema listo | Falta API, reglas y reportes. |
| Auditoría | Base | Tabla/entidad lista; falta interceptación automática de eventos. |
| PDF/Excel | Pendiente | Siguiente incremento. |
| Frontend React/PWA | Fuera de este archivo | Diseñar contra esta API. |

## Decisiones que facilitan mejoras
1. Flyway controla el esquema; no se depende de `ddl-auto=update`.
2. UUID en claves primarias.
3. `tenant_id` está presente en datos de negocio.
4. Archivos pesados solo guardarán metadatos en PostgreSQL.
5. Redis y MinIO están desacoplados y opcionales.
6. La API puede crecer por módulos sin romper rutas existentes.
