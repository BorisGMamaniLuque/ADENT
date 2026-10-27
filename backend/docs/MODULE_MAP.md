# Mapa de módulos ADENT

```mermaid
flowchart LR
    UI[React / PWA] --> API[Spring Boot REST]
    API --> ID[identity]
    API --> PL[platform]
    API --> ST[structure]
    API --> SF[staff]
    API --> PT[patient]
    API --> AP[appointment]
    API --> CL[clinical]
    API --> RP[reporting]
    API --> AU[audit]
    PT --> CL
    AP --> PT
    AP --> SF
    CL --> PT
    CL --> SF
    API --> PG[(PostgreSQL)]
    API -. futuro .-> OBJ[(MinIO/S3)]
    API -. opcional .-> RD[(Redis)]
```

## Regla para ampliar
Cada nueva funcionalidad debe entrar dentro de un módulo existente o crear un módulo nuevo con `domain`, `repository`, `service` y `api`. Evitar controladores que accedan directamente a tablas de varios dominios.
