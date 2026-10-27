# Notas para incorporar código de un backend anterior

Esta versión está diseñada para ser el nuevo tronco del proyecto ADENT. Si el backend anterior contiene controladores o servicios que quieras conservar:

1. No copiar configuraciones antiguas de `ddl-auto=update`; mantener Flyway.
2. Mover lógica clínica al módulo correspondiente (`patient`, `clinical`, `appointment`, etc.).
3. No aceptar `tenantId` del frontend como criterio de seguridad; usar el tenant del JWT.
4. Reutilizar DTOs solo después de verificar que no expongan entidades completas o contraseñas.
5. Mantener rutas bajo `/api/...` y documentarlas en Swagger.
6. Agregar una migración `V2__...sql` para cualquier cambio de esquema, sin editar V1 después de desplegarla.
7. Para archivos, implementar `ObjectStoragePort` con MinIO/S3; no guardar imágenes como `bytea` en PostgreSQL.
8. Antes de extraer microservicios, medir carga y dependencias. El diseño actual permite esa evolución sin asumirla desde el inicio.
