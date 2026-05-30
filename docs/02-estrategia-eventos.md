# `02-estrategia-eventos.md`

## 1. Justificación del Modelo Reactivo Basado en StateFlow

Antes de entrar en los flujos concretos de datos, es importante explicar la decisión que tal vez más define el comportamiento de esta PoC: **¿por qué no se usó Room ni llamadas de red reales?**

Esencialmente, esta aplicación es una Prueba de Concepto bajo restricciones de tiempo. Integrar Room habría significado definir entidades, DAOs, migraciones y una base de datos local, lo cual duplica el tiempo de desarrollo sin aportar valor diferencial para una demostración funcional. De igual manera, conectar Retrofit a un backend real requeriría credenciales, servidores activos y manejo de autenticación real, todo fuera del alcance de esta entrega.

La solución adoptada es un **`MutableStateFlow<List<Ticket>>`** centralizado dentro de `TicketRepository`, que actúa como la fuente de verdad en memoria para toda la aplicación:

```kotlin
// TicketRepository.kt
private val _tickets = MutableStateFlow<List<Ticket>>(initialMockTickets())
val tickets: StateFlow<List<Ticket>> = _tickets.asStateFlow()
```

Este enfoque permite reproducir con exactitud el comportamiento reactivo que tendría una base de datos real o una conexión WebSocket: cualquier modificación al estado interno del repositorio se propaga de forma inmediata y automática a todas las pantallas que estén observando el flujo, **sin reinicios de pantalla, sin `LiveData` y sin llamadas manuales de recarga**.

La arquitectura queda estructuralmente preparada para que en una fase posterior se sustituya el `MutableStateFlow` en memoria por un `Flow` proveniente de Room, simplemente cambiando la implementación del repositorio. Los ViewModels y la UI no requerirán ninguna modificación.

---

## 2. Escenario 1: Creación de un Nuevo Ticket

El flujo de creación de tickets sigue el principio de **Unidirectional Data Flow (UDF)**, donde las acciones del usuario fluyen hacia abajo y el estado actualizado sube de regreso a la pantalla.

### Paso a Paso

**Paso 1 — La UI delega la acción:**
El usuario completa el formulario en `CreateTicketScreen` y presiona el botón "Generar Ticket". La pantalla no ejecuta ninguna lógica de negocio; simplemente construye un `CreateTicketRequestDto` con los datos del formulario y llama al método del ViewModel:

```kotlin
// CreateTicketScreen.kt
val request = CreateTicketRequestDto(title, priority, status, provider, category, description)
viewModel.createTicket(request)
```

**Paso 2 — El ViewModel coordina y actualiza su estado:**
`CreateTicketViewModel` actualiza el `UiState` a estado de carga (`isLoading = true`) y lanza la operación suspendida en el `viewModelScope`:

```kotlin
// CreateTicketViewModel.kt
viewModelScope.launch {
    when (val result = ticketRepository.createTicket(request)) {
        is ApiResult.Success -> _uiState.update { it.copy(isLoading = false, isSuccess = true) }
        is ApiResult.Error   -> _uiState.update { it.copy(isLoading = false, errorMessage = result.message) }
    }
}
```

**Paso 3 — El repositorio genera el ID y actualiza el flujo central:**
`TicketRepository.createTicket()` genera un identificador único (`UUID`), convierte el DTO al modelo de dominio mediante `TicketMapper`, y lo inserta en la lista interna. El punto crítico es que **inmediatamente después re-ordena por prioridad** y emite el nuevo estado:

```kotlin
// TicketRepository.kt
val newTicket = request.toDomain(newId)
_tickets.update { current ->
    (current + newTicket).sortedBy { it.priority.sortOrder }
}
```

**Paso 4 — La pantalla de listado reacciona sin recarga:**
`TicketListViewModel` ya estaba suscrito al `tickets` StateFlow del repositorio desde que se inició la pantalla. En el momento en que el repositorio emite la lista actualizada, el ViewModel recibe el nuevo valor de forma automática y actualiza su propio `UiState`:

```kotlin
// TicketListViewModel.kt
ticketRepository.tickets.collect { newTickets ->
    _uiState.update { it.copy(tickets = newTickets, isLoading = false) }
}
```

Compose re-compone únicamente los elementos que cambiaron. El nuevo ticket aparece en la posición correcta según su prioridad sin que el usuario necesite regresar o refrescar manualmente la pantalla.

---

## 3. Escenario 2: Actualización de Prioridad y Reordenamiento Reactivo

Este escenario demuestra el valor del enfoque reactivo. La eficiencia operativa de un equipo de soporte logístico depende de tener visibilidad inmediata de los incidentes más críticos. Un reordenamiento manual o diferido sería inaceptable en un contexto de operaciones en tiempo real como el álbum del Mundial 2026.

### Lógica de Ordenamiento

La prioridad de cada ticket se define mediante el enum `TicketPriority`, que incluye un campo `sortOrder` para controlar el ordenamiento numérico:

```kotlin
// Ticket.kt
enum class TicketPriority(val displayName: String, val sortOrder: Int) {
    HIGH("Alta prioridad",    0),
    MEDIUM("Prioridad media", 1),
    LOW("Baja prioridad",     2)
}
```

El valor `sortOrder = 0` para `HIGH` garantiza que los tickets críticos siempre aparezcan en la parte superior de la lista. Al delegar el criterio de ordenamiento al dominio (y no a la UI), cualquier futuro ingeniero puede agregar un nivel `CRITICAL` con `sortOrder = -1` sin tocar nada más que el enum.

### Paso a Paso

**Paso 1 — El usuario selecciona una nueva prioridad:**
En `UpdateStatusScreen`, el usuario selecciona un nivel diferente con los radio buttons y presiona "Guardar Cambios". El ViewModel invoca las operaciones de actualización de manera secuencial:

```kotlin
// UpdateStatusViewModel.kt
val priorityResult = ticketRepository.updateTicketPriority(ticketId, newPriority)
// Si tiene éxito, continúa con el estado:
val statusResult = ticketRepository.updateTicketStatus(ticketId, newStatus)
```

**Paso 2 — El repositorio modifica el ticket y reordena:**
`updateTicketPriority` recorre la lista, aplica `.copy()` únicamente al ticket afectado (el resto permanece intacto) y vuelve a ordenar toda la lista por `sortOrder`:

```kotlin
// TicketRepository.kt
_tickets.update { current ->
    current.map { ticket ->
        if (ticket.id == ticketId) ticket.copy(priority = newPriority) else ticket
    }.sortedBy { it.priority.sortOrder }
}
```

**Paso 3 — Todos los observadores reciben la lista reordenada:**
Como `TicketListViewModel` y `TicketDetailViewModel` comparten la misma fuente de datos (`TicketRepository`), ambas pantallas reciben simultáneamente la lista actualizada. Cuando el usuario regresa al listado, el ticket modificado ya está en su nueva posición sin operación adicional.

Este comportamiento es especialmente relevante para el operador de Panini: si un retraso de distribución escala de "Media" a "Alta", ese ticket sube automáticamente al tope de la lista, garantizando que el equipo atienda primero los incidentes más críticos sin depender de criterios manuales de ordenamiento.
