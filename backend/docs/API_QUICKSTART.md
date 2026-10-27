# Secuencia rápida de prueba

1. `POST /api/auth/login` como `owner` con `tenantSlug=demo-dental`.
2. Copiar `accessToken` y usar `Authorization: Bearer <token>`.
3. Crear sede en `POST /api/structure/branches`.
4. Crear piso y consultorio.
5. Crear odontólogo en `POST /api/staff`.
6. Crear paciente en `POST /api/patients`.
7. Crear cita en `POST /api/appointments`.
8. Crear atención en `POST /api/clinical/encounters`.
9. Editar mientras está en `DRAFT`.
10. Cerrar con `POST /api/clinical/encounters/{id}/finalize`.
11. Si necesita corregirse luego, usar `POST /api/clinical/encounters/{id}/addendum`.

La atención finalizada no debe editarse directamente.
