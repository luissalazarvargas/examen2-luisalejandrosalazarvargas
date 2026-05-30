# `04-flujo-sistema.md`

## 1. Arquitectura de Navegación: Single Activity con Navigation Compose

La aplicación completa se ejecuta dentro de una única `MainActivity`. Práctica recomendada explícitamente por el equipo de Android Jetpack y tiene ventajas concretas en términos de rendimiento y mantenibilidad.

En el modelo tradicional de múltiples `Activity`, cada transición implica que el sistema operativo crea, asigna memoria y destruye objetos pesados del sistema. Con el modelo de **Single Activity**, el sistema crea la actividad una sola vez y la navegación entre pantallas es simplemente un reemplazo de composables en memoria, lo que resulta en transiciones considerablemente más rápidas y en un consumo de recursos significativamente menor.

El grafo de navegación está centralizado en `PaniniApp.kt`, que aloja el `NavHost` y define todos los destinos de la aplicación:

```kotlin
// PaniniApp.kt
NavHost(navController = navController, startDestination = "login") {
    composable("login")              { /* LoginScreen */ }
    composable("list")               { /* TicketListScreen */ }
    composable("detail/{ticketId}")  { /* TicketDetailScreen */ }
    composable("create")             { /* CreateTicketScreen */ }
    composable("status/{ticketId}")  { /* UpdateStatusScreen */ }
}
```

Este enfoque centralizado tiene una ventaja práctica para el mantenimiento: un futuro ingeniero que necesite entender el flujo completo de la aplicación puede leer el grafo de navegación en un solo archivo y tener una visión global de todas las rutas disponibles sin tener que rastrear intents entre múltiples actividades.

---

## 2. Estructura del Estado de UI (`UiState`)

Cada pantalla posee su propio objeto de estado inmutable que agrupa toda la información que la pantalla necesita para renderizarse correctamente. Por ejemplo:

```kotlin
// TicketListUiState.kt
data class TicketListUiState(
    val isLoading: Boolean = true,
    val tickets: List<Ticket> = emptyList(),
    val errorMessage: String? = null
)
```

La razón de agrupar estas propiedades en un solo objeto, en lugar de tener múltiples `StateFlow` separados, es evitar condiciones de carrera en la UI. Si `isLoading` y `tickets` fueran flujos independientes, existiría un instante en que `isLoading = false` pero `tickets` aún estuviera vacío, lo que podría mostrar brevemente el empty state antes de que los datos lleguen. Al tener un único `UiState` actualizado atómicamente con `.update { ... }`, la transición entre estados siempre es consistente.

Esto permite que cada pantalla maneje de manera limpia y predecible sus tres estados posibles:

```
┌─────────────────────────────────────────────────────┐
│                 Estado de la Pantalla                │
├──────────────────┬──────────────────┬───────────────┤
│   isLoading=true │ tickets.isEmpty  │ tickets.isNotE│
│   (Cargando)     │ (Empty State)    │ (Lista normal) │
│   Spinner        │ Mensaje vacío    │ LazyColumn     │
└──────────────────┴──────────────────┴───────────────┘
```

Esta estructura aplica a todas las pantallas del proyecto, con variaciones menores según la funcionalidad: `LoginUiState` agrega `isSuccess: Boolean` para indicar autenticación exitosa, `TicketDetailUiState` agrega `ticket: Ticket?` como dato central, y `UpdateStatusUiState` incluye `isUpdating: Boolean` para diferenciar la carga inicial de la operación de guardado.

---

## 3. Flujos de Usuario Clave

### Flujo 1: Autenticación Simulada

El usuario inicia la aplicación y se encuentra con la pantalla de login. Esta pantalla solicita correo y contraseña. Al presionar "Ingresar", `LoginViewModel` delega la validación a `AuthRepository`:

```
LoginScreen → LoginViewModel.login() → AuthRepository.login()
           → Valida credenciales contra datos hardcodeados
           → Si válido: guarda token en AuthSession (Singleton en memoria)
           → Emite ApiResult.Success → UiState(isSuccess=true)
           → LoginScreen navega a "list" y se elimina del back stack
```

Es importante notar que al navegar hacia el listado se usa `popUpTo("login") { inclusive = true }`. Esto asegura que al presionar "Atrás" desde el listado, el usuario salga de la aplicación en lugar de regresar al login, comportamiento estándar esperado en cualquier aplicación con autenticación.

### Flujo 2: Navegación al Listado General

Una vez autenticado, el usuario llega a `TicketListScreen`. Esta pantalla inicia inmediatamente la observación del `StateFlow` de tickets a través de `TicketListViewModel`:

```
TicketListScreen → TicketListViewModel (init) → ticketRepository.tickets.collect { }
               → Emite lista inicial de 5 tickets ordenados por prioridad
               → UiState(isLoading=false, tickets=[...])
               → LazyColumn renderiza los tickets visibles
```

Desde aquí el usuario puede:
- **Tocar una tarjeta** → navegar a `detail/{ticketId}`
- **Tocar el FAB `+`** (si el flag está activo) → navegar a `create`
- **Usar los switches** → controlar los Feature Flags en tiempo real

### Flujo 3: Detalle con Paso Seguro de Argumentos

Navigation Compose permite pasar argumentos entre destinos de forma tipada, evitando la fragilidad de los `Bundle` tradicionales:

```kotlin
// Definición del destino con argumento
composable(
    route = "detail/{ticketId}",
    arguments = listOf(navArgument("ticketId") { type = NavType.StringType })
) { backStackEntry ->
    val ticketId = backStackEntry.arguments?.getString("ticketId") ?: return@composable
    // El ViewModel se instancia con el ticketId ya validado
    val viewModel = viewModel(factory = TicketDetailViewModelFactory(ticketId, ...))
}
```

El `ticketId` llega al `TicketDetailViewModel` antes de que la pantalla se componga, lo que garantiza que nunca se renderice un detalle sin un identificador válido. El ViewModel busca el ticket en el repositorio y si no lo encuentra emite un `errorMessage`, caso que en condiciones normales no debería ocurrir.

Desde el detalle, si el flag `enablePriorityUpdate` está activo, el usuario puede navegar a `status/{ticketId}` para modificar la prioridad y el estado. Al confirmar los cambios en `UpdateStatusScreen`, el ViewModel invoca las actualizaciones en el repositorio y navega de regreso al detalle usando `popBackStack()`. Gracias a la reactividad del `StateFlow`, el detalle ya muestra los datos actualizados sin necesidad de ninguna recarga explícita.
