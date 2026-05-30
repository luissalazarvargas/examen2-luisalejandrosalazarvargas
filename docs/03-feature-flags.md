# `03-feature-flags.md`

## 1. Justificación del Enfoque Local y Desacoplado

La implementación de los Feature Flags en esta PoC se diseñó siguiendo un criterio muy específico: **máxima utilidad con mínima complejidad operativa**.

Una solución como Firebase Remote Config es ideal para producción porque permite activar o desactivar características sin publicar una nueva versión de la app. Sin embargo, implica un costo real: requiere una cuenta de Firebase configurada, integración del SDK (que agrega peso al APK), dependencia de red para obtener los valores actualizados y tiempo de configuración en el panel de Firebase. Para una PoC en la que los evaluadores y el equipo de pruebas son el mismo grupo de personas, este esfuerzo no se justifica.

La decisión fue implementar `FeatureFlags.kt` como un **objeto Kotlin singleton con estados observables (`mutableStateOf`)** locales a la aplicación:

```kotlin
// util/FeatureFlags.kt
object FeatureFlags {
    var enableTicketCreation = mutableStateOf(true)
    var enablePriorityUpdate = mutableStateOf(true)
}
```

Esto resuelve tres cosas al mismo tiempo: los flags son accesibles desde cualquier punto de la aplicación sin inyección de dependencias, son reactivos (Compose los observa automáticamente sin `collectAsStateWithLifecycle`), y pueden ser modificados en tiempo de ejecución desde la propia interfaz de usuario para demostrar su funcionamiento.

---

## 2. Análisis de los Flags Implementados

### `enableTicketCreation`

Este flag controla si el usuario tiene la capacidad de registrar nuevas incidencias logísticas en el sistema.

**Impacto visual cuando está activo (`true`):**
- Aparece el botón flotante (FAB) con el ícono `+` en la esquina inferior derecha de la pantalla de listado.
- La pantalla de creación (`CreateTicketScreen`) es navegable desde el grafo de navegación.

**Impacto visual cuando está inactivo (`false`):**
- El FAB desaparece completamente. El usuario no tiene ninguna señal visual de que la funcionalidad existe.
- No se requiere ningún bloqueo adicional en el destino de navegación porque el punto de entrada (el botón) simplemente no se renderiza.

El control se aplica en dos lugares: en la condición de renderizado del FAB y en la visualización del toggle switch dentro del listado:

```kotlin
// TicketListScreen.kt
floatingActionButton = {
    if (FeatureFlags.enableTicketCreation.value) {
        FloatingActionButton(onClick = onNavigateToCreate) { ... }
    }
}
```

### `enablePriorityUpdate`

Este flag controla si los operadores pueden modificar la prioridad y el estado de un ticket existente.

**Impacto visual cuando está activo (`true`):**
- En la pantalla de detalle de ticket, se renderiza el botón "Actualizar Prioridad / Estado" al final del contenido.
- El botón navega hacia `UpdateStatusScreen`, donde se puede cambiar tanto la prioridad como el estado del ticket.

**Impacto visual cuando está inactivo (`false`):**
- El botón de actualización desaparece del detalle sin afectar ninguna otra parte de la pantalla.
- El resto de la información del ticket (título, descripción, proveedor, categoría, fecha) permanece visible y legible.

```kotlin
// TicketDetailScreen.kt
if (FeatureFlags.enablePriorityUpdate.value) {
    Button(onClick = onUpdatePriorityClick) {
        Text("Actualizar Prioridad / Estado")
    }
}
```

---

## 3. Interacción con la UI: El Panel de Control en el Listado

Para hacer demostrables los flags durante la evaluación, se agregaron dos interruptores (`Switch`) en la parte superior de la pantalla de listado, visibles sobre la lista de tickets:

```
┌──────────────────────────────────────────────┐
│  Habilitar Creación (Feature Flag)   [ ● ]   │
│  Habilitar Edición de Prioridad      [ ● ]   │
├──────────────────────────────────────────────┤
│  TKT-MX2026-001 · Alta prioridad             │
│  Faltante crítico de sobres en ruta norte    │
│  ...                                         │
└──────────────────────────────────────────────┘
```

Al desactivar cualquiera de los switches, el cambio se refleja inmediatamente en la UI correspondiente sin recompilar ni reiniciar la aplicación, lo cual demuestra de forma visual y tangible el propósito de los feature flags.

---

## 4. Escalabilidad Futura: Conexión a Remote Config

La transición hacia Firebase Remote Config u otro proveedor de configuración dinámica fue contemplada en el diseño de la clase. Actualmente `FeatureFlags` provee sus propios valores por defecto; en una siguiente fase, esos valores simplemente serían obtenidos desde un servicio externo.

El camino de migración sería el siguiente:

**Fase actual (PoC):**
```kotlin
object FeatureFlags {
    var enableTicketCreation = mutableStateOf(true)
    var enablePriorityUpdate = mutableStateOf(true)
}
```

**Fase de producción (con Remote Config):**
```kotlin
class RemoteFeatureFlagProvider(private val remoteConfig: FirebaseRemoteConfig) {
    suspend fun fetchFlags() {
        remoteConfig.fetchAndActivate().await()
        FeatureFlags.enableTicketCreation.value =
            remoteConfig.getBoolean("enable_ticket_creation")
        FeatureFlags.enablePriorityUpdate.value =
            remoteConfig.getBoolean("enable_priority_update")
    }
}
```

La clave de esta estrategia es que **ningún ViewModel, ninguna pantalla y ningún componente Compose necesitaría ser modificado**. Solo se agrega el `RemoteFeatureFlagProvider` al `AppContainer`, se invoca `fetchFlags()` al iniciar la aplicación en `PaniniApplication`, y los estados reactivos de `FeatureFlags` se actualizan automáticamente. Compose detecta el cambio y re-compone las secciones afectadas por sí solo.

Esto es posible precisamente porque los flags son `mutableStateOf` (observables por Compose) y están desacoplados de cualquier pantalla o ViewModel específico.
