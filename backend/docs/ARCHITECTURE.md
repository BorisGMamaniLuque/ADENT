# Arquitectura

```text
React / PWA
     |
 REST/HTTPS
     |
Spring Boot - ADENT modular
+-------------------------------+
| platform | identity | patient |
| staff    | appointment        |
| clinical | documents          |
| finance  | reporting | audit  |
+-------------------------------+
     |                 |
 PostgreSQL        Object Storage
                   (MinIO/S3)
```

## Regla de dependencia
Los módulos exponen servicios y DTOs; no deben acceder directamente a tablas ajenas salvo relaciones nucleares controladas. Para nuevas funciones, preferir puertos/interfaces antes que dependencias circulares.

## Estrategia multi-tenant
Modelo compartido con `tenant_id`. Es apropiado para el MVP y para muchos establecimientos pequeños/medianos. En una escala futura puede evolucionar a particionado, RLS de PostgreSQL o separación física por cliente de alto requerimiento sin cambiar el modelo funcional del frontend.
