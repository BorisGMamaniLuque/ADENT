# ADENT Frontend - Guía de modificación

## 1. Regla principal

El frontend está organizado por **módulo funcional**, de forma paralela al backend Spring Boot.

| Frontend | Backend Spring Boot |
|---|---|
| `features/identity` | `com.adent.identity` |
| `features/patients` | `com.adent.patient` |
| `features/appointments` | `com.adent.appointment` |
| `features/staff` | `com.adent.staff` |
| `features/structure` | `com.adent.structure` |
| `features/reporting` | `com.adent.reporting` |
| `features/platform` | `com.adent.platform` |

## 2. ¿Dónde modificar cada cosa?

- **Roles y permisos de interfaz:** `src/config/roles.js`
- **Menú lateral:** `src/config/navigation.js`
- **Rutas/pantallas:** `src/routes/AppRoutes.jsx`
- **Login y sesión:** `src/auth/AuthContext.jsx`
- **Conexión Axios/JWT:** `src/api/http.js`
- **Endpoints por módulo:** archivos `src/api/*Api.js`
- **Diseño global/colores:** `src/styles/tokens.css`
- **CSS de componentes:** `src/styles/app.css`
- **Layout general:** `src/components/layout/AppLayout.jsx`
- **Componentes reutilizables:** `src/components/common/Ui.jsx`

## 3. Roles actuales

- `SUPER_ADMIN`: administración global de organizaciones.
- `OWNER`: propietario del tenant.
- `ADMIN`: administración del tenant.
- `RECEPTION`: pacientes y citas; alta/edición de pacientes.
- `DENTIST`: pacientes, citas y atención clínica.
- `ASSISTANT`: lectura de pacientes y acceso clínico permitido por backend.

**Importante:** ocultar un botón en React no reemplaza la seguridad del backend. Spring Security sigue siendo la autoridad final.

## 4. Cómo agregar una funcionalidad nueva

Ejemplo: `billing`.

1. Crear `src/features/billing/pages/BillingPage.jsx`.
2. Crear `src/api/billingApi.js` con los endpoints Spring.
3. Agregar acceso en `src/config/roles.js`.
4. Agregar el menú en `src/config/navigation.js`.
5. Agregar la ruta en `src/routes/AppRoutes.jsx`.
6. Confirmar que Spring Security permita exactamente los mismos roles.

## 5. Cómo cambiar un rol

Para cambiar qué puede ver un rol en React, edita `ACCESS` dentro de `src/config/roles.js`.

Ejemplo:

```js
APPOINTMENTS: [ROLES.OWNER, ROLES.ADMIN, ROLES.RECEPTION, ROLES.DENTIST]
```

Si deseas permitir `ASSISTANT`, agrégalo ahí **y también valida/cambia el `@PreAuthorize` correspondiente del backend**.

## 6. Flujo de integración

```text
Pantalla JSX
   ↓
API del módulo (`src/api/...Api.js`)
   ↓
Axios (`src/api/http.js`)
   ↓ JWT Bearer
Nginx /api
   ↓
Spring Boot Controller
   ↓
Service
   ↓
Repository
   ↓
PostgreSQL
```

## 7. Multitenancy

El frontend conserva `tenantId`, `tenantSlug` y roles recibidos en el login. No se debe enviar un `tenantId` editable en formularios normales. El backend obtiene el tenant desde el JWT y aplica el aislamiento.

## 8. Archivos HTML, JS, JSX y CSS

- `index.html`: único HTML raíz de Vite.
- `.jsx`: pantallas y componentes visuales React.
- `.js`: API, reglas de permisos, utilidades y lógica sin JSX.
- `.css`: diseño y responsive.

No crear un HTML por cada pantalla; React Router se encarga de las vistas.
