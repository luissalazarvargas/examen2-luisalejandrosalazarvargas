# AI Context — Examen II: Panini Support Tickets PoC

Este documento debe usarse como contexto base para trabajar con IA en el desarrollo del examen. La solución debe ser clara, mantenible, técnicamente justificada y coherente con una prueba de concepto móvil empresarial.

## 1. Contexto general del examen

La aplicación móvil corresponde a una prueba de concepto para la empresa Panini, relacionada con la distribución y soporte operativo del álbum oficial de la Copa Mundial FIFA 2026.

El problema central es que actualmente los reportes de soporte relacionados con proveedores, distribución, inventario y logística se gestionan mediante correos, hojas de cálculo y mensajes informales. Esto provoca pérdida de seguimiento, duplicidad de solicitudes, retrasos en la resolución y dificultad para coordinar entre equipos internos.

La solución solicitada es una aplicación móvil para centralizar y gestionar tickets de soporte interno relacionados con proveedores.

La evaluación no busca una aplicación enorme ni una arquitectura innecesariamente compleja. Se prioriza:

- criterio técnico;
- arquitectura clara;
- mantenibilidad;
- comprensión de MVVM;
- comunicación basada en eventos;
- Feature Flags;
- preparación para integración futura con backend;
- documentación clara y no genérica;
- código fácil de continuar por otros ingenieros.

## 2. Alcance real de la prueba de concepto

La aplicación es solamente frontend móvil con datos simulados.

No se debe implementar backend.
No se debe consumir una API real.
No se debe agregar complejidad innecesaria.

Sin embargo, aunque no exista backend, el proyecto debe dejar preparado el terreno para una integración futura. Por eso sí deben existir:

- Retrofit;
- ApiService;
- DTOs;
- Networking Layer;
- Repository Pattern;
- API Contracts en YAML;
- mocks realistas;
- separación entre modelo de dominio y DTOs.

La app debe poder funcionar completamente con mock data, pero su estructura debe permitir reemplazar los mocks por una API real sin reorganizar todo el proyecto.

## 3. Requerimientos funcionales obligatorios

La aplicación debe incluir como mínimo las siguientes pantallas:

### 3.1 Login Screen

Autenticación simulada. No requiere backend real.

Puede validar campos básicos como correo y contraseña. Si la entrada es válida, permite navegar al listado de tickets.

### 3.2 Ticket List Screen

Debe mostrar un listado de tickets usando `LazyColumn`.

Cada ticket debe mostrar:

- título;
- prioridad;
- estado;
- proveedor relacionado;
- fecha de creación;
- categoría o tipo de incidente.

El listado debe ordenarse dando prioridad visual a los tickets más importantes.

### 3.3 Ticket Detail Screen

Debe mostrar información ampliada del ticket seleccionado.

Puede incluir:

- título;
- descripción;
- proveedor;
- prioridad;
- estado;
- fecha de creación;
- categoría;
- persona o área reportante;
- ubicación o punto de venta relacionado.

Desde esta pantalla debe poder actualizarse el estado del ticket.

### 3.4 Create Ticket Screen

Debe permitir crear nuevos tickets de soporte.

La creación debe actualizar automáticamente el listado principal mediante `Flow` o `StateFlow`, sin recargar manualmente la pantalla.

### 3.5 Ticket Status Update

Debe existir capacidad para cambiar el estado de un ticket.

Estados sugeridos:

- `OPEN`;
- `IN_PROGRESS`;
- `RESOLVED`;
- `CLOSED`.

También puede mostrarse con texto amigable en UI:

- Open;
- In Progress;
- Resolved;
- Closed.

## 4. Requerimientos técnicos obligatorios

La solución debe incluir:

- Kotlin;
- Android Studio;
- Jetpack Compose;
- Material 3;
- MVVM;
- Navigation Compose;
- Retrofit;
- DTOs;
- Networking Layer;
- API Contracts en YAML dentro de `/contracts`;
- Mock Integration;
- manejo de estados de carga, error y éxito;
- arquitectura limpia, simple y coherente.

## 5. Estructura mínima del repositorio

El repositorio debe ser público y mantener como mínimo esta estructura:

```text
/
├── app/
├── contracts/
├── docs/
├── video/
└── README.md
```

### `/app`

Contiene el proyecto Android con Jetpack Compose.

### `/contracts`

Contiene los contratos de API en formato YAML.

Archivo obligatorio sugerido:

```text
/contracts/tickets-api.yaml
```

### `/docs`

Contiene documentación técnica breve y útil.

Archivos sugeridos:

```text
/docs/ARCHITECTURE.md
/docs/EVENT_FLOW.md
/docs/FEATURE_FLAGS.md
```

La documentación debe explicar decisiones reales del proyecto. No debe ser relleno genérico.

### `/video`

Contiene un archivo `.md` con el enlace al video demo.

Archivo sugerido:

```text
/video/demo-link.md
```

### `README.md`

Debe incluir:

- descripción general;
- instrucciones de ejecución;
- tecnologías utilizadas;
- estructura del proyecto;
- consideraciones técnicas importantes.

## 6. Arquitectura recomendada

Usar MVVM con una estructura simple, mantenible y preparada para backend futuro.

No usar Clean Architecture completa con demasiadas capas.
No agregar Room.
No agregar Firebase.
No agregar Hilt/Dagger si no es necesario.
No crear casos de uso innecesarios para una PoC corta.

La estructura recomendada es:

```text
com.panini.support/
├── PaniniSupportApplication.kt
├── MainActivity.kt
├── AppNavigation.kt
│
├── core/
│   └── AppConstants.kt
│
├── data/
│   ├── Ticket.kt
│   ├── AppContainer.kt
│   │
│   ├── remote/
│   │   ├── ApiService.kt
│   │   ├── RetrofitClient.kt
│   │   └── model/
│   │       └── TicketModels.kt
│   │
│   ├── mock/
│   │   └── MockTickets.kt
│   │
│   └── repository/
│       ├── ApiResult.kt
│       └── TicketRepository.kt
│
├── ui/
│   ├── components/
│   │   ├── TicketCard.kt
│   │   ├── PriorityBadge.kt
│   │   ├── StatusBadge.kt
│   │   └── EmptyState.kt
│   │
│   ├── screens/
│   │   ├── login/
│   │   │   ├── LoginScreen.kt
│   │   │   └── LoginViewModel.kt
│   │   │
│   │   ├── ticketlist/
│   │   │   ├── TicketListScreen.kt
│   │   │   └── TicketListViewModel.kt
│   │   │
│   │   ├── ticketdetail/
│   │   │   ├── TicketDetailScreen.kt
│   │   │   └── TicketDetailViewModel.kt
│   │   │
│   │   └── createticket/
│   │       ├── CreateTicketScreen.kt
│   │       └── CreateTicketViewModel.kt
│   │
│   └── theme/
│
└── util/
    └── FeatureFlags.kt
```

## 7. Responsabilidades por capa

### UI / Screens

Los Composables solo deben encargarse de mostrar estado y enviar acciones al ViewModel.

La UI no debe llamar directamente al repositorio.
La UI no debe contener lógica de negocio.
La UI no debe modificar listas de tickets directamente.

### ViewModel

Cada ViewModel expone un `StateFlow<UiState>` y recibe acciones desde la UI.

Ejemplos:

```kotlin
val uiState: StateFlow<TicketListUiState>
fun loadTickets()
fun createTicket(...)
fun updateTicketStatus(...)
fun updateTicketPriority(...)
```

El ViewModel no debe conocer detalles de Retrofit.
El ViewModel no debe construir mock data directamente.

### Repository

`TicketRepository` actúa como fuente central de datos para tickets.

En esta PoC debe trabajar con datos mockeados, pero con una estructura compatible con una futura API.

Debe exponer flujos reactivos, por ejemplo:

```kotlin
val ticketsFlow: StateFlow<List<Ticket>>
fun getTicketFlow(ticketId: String): Flow<Ticket?>
suspend fun createTicket(request: CreateTicketRequest): ApiResult<Ticket>
suspend fun updateTicketStatus(ticketId: String, status: TicketStatus): ApiResult<Ticket>
suspend fun updateTicketPriority(ticketId: String, priority: TicketPriority): ApiResult<Ticket>
```

### Remote / Networking

Aunque no se consuma backend real, debe existir la estructura de Retrofit:

- `ApiService.kt`;
- `RetrofitClient.kt`;
- DTOs en `data/remote/model/`.

Esto demuestra cómo se conectaría la app a un backend posteriormente.

### Mock

`MockTickets.kt` contiene datos realistas relacionados con:

- proveedores;
- distribución;
- faltantes de inventario;
- errores logísticos;
- paquetes dañados;
- puntos de venta;
- categorías de soporte.

No usar datos como `Ticket 1`, `Test`, `Lorem ipsum`, `Proveedor X` sin contexto.

## 8. Comunicación basada en eventos con Flow / StateFlow

La comunicación basada en eventos debe implementarse con `Flow` o `StateFlow` de forma simple.

No crear un EventBus complejo.
No agregar librerías externas para eventos.

La opción recomendada es que `TicketRepository` mantenga una lista interna de tickets como `MutableStateFlow<List<Ticket>>`.

Los ViewModels observan ese flujo y la UI se recompone automáticamente.

### Flujo 1 — creación de tickets

Cuando se crea un nuevo ticket:

1. `CreateTicketScreen` envía la acción al `CreateTicketViewModel`.
2. `CreateTicketViewModel` llama a `TicketRepository.createTicket()`.
3. `TicketRepository` agrega el nuevo ticket al `MutableStateFlow`.
4. `TicketListViewModel`, que observa `ticketsFlow`, recibe automáticamente la nueva lista.
5. `TicketListScreen` se recompone y muestra el ticket sin recargar manualmente la pantalla.

### Flujo 2 — actualización de prioridad

Cuando se modifica la prioridad de un ticket:

1. `TicketDetailScreen` envía la acción al `TicketDetailViewModel`.
2. `TicketDetailViewModel` llama a `TicketRepository.updateTicketPriority()`.
3. `TicketRepository` actualiza el ticket dentro del `MutableStateFlow`.
4. El listado se vuelve a emitir ordenado por prioridad.
5. `TicketListScreen` recibe el nuevo estado y reposiciona automáticamente el ticket.

Este enfoque cumple el requerimiento de reacción automática sin complejidad innecesaria.

## 9. Manejo de estado UI

Cada pantalla debe tener su propio `UiState`.

Ejemplo para listado:

```kotlin
data class TicketListUiState(
    val isLoading: Boolean = false,
    val tickets: List<Ticket> = emptyList(),
    val errorMessage: String? = null
)
```

Ejemplo para detalle:

```kotlin
data class TicketDetailUiState(
    val isLoading: Boolean = false,
    val ticket: Ticket? = null,
    val errorMessage: String? = null,
    val canUpdatePriority: Boolean = FeatureFlags.ENABLE_PRIORITY_UPDATE
)
```

La UI debe representar claramente:

- loading;
- error;
- success/content;
- empty state si aplica.

## 10. Feature Flags

Implementar al menos dos Feature Flags simples y coherentes.

Archivo sugerido:

```text
util/FeatureFlags.kt
```

Ejemplo:

```kotlin
object FeatureFlags {
    const val ENABLE_TICKET_CREATION = true
    const val ENABLE_PRIORITY_UPDATE = true
    const val ENABLE_CATEGORY_FILTER = false
}
```

Flags recomendados:

### `ENABLE_TICKET_CREATION`

Controla si la aplicación permite crear tickets.

Si está en `false`:

- ocultar o deshabilitar el botón de crear ticket;
- mostrar un mensaje como: `Ticket creation is currently disabled for internal testing.`

### `ENABLE_PRIORITY_UPDATE`

Controla si se puede modificar la prioridad de un ticket.

Si está en `false`:

- ocultar o deshabilitar el selector de prioridad;
- mantener visible la prioridad actual.

### Posible tercer flag opcional

`ENABLE_CATEGORY_FILTER`

Controla si el listado muestra filtros por categoría.

No implementarlo si compromete el tiempo. Es opcional.

## 11. API Contracts YAML

Aunque no exista backend, los contratos deben estar definidos en `/contracts`.

Archivo requerido sugerido:

```text
/contracts/tickets-api.yaml
```

Debe ser coherente con lo que realmente hace la app.

Endpoints recomendados:

```text
POST   /auth/login
GET    /tickets
GET    /tickets/{ticketId}
POST   /tickets
PATCH  /tickets/{ticketId}/status
PATCH  /tickets/{ticketId}/priority
```

Schemas recomendados:

- `LoginRequest`;
- `LoginResponse`;
- `TicketResponse`;
- `CreateTicketRequest`;
- `CreateTicketResponse`;
- `UpdateTicketStatusRequest`;
- `UpdateTicketPriorityRequest`;
- `ErrorResponse`.

Enums recomendados:

```text
TicketPriority:
- LOW
- MEDIUM
- HIGH
- CRITICAL

TicketStatus:
- OPEN
- IN_PROGRESS
- RESOLVED
- CLOSED

TicketCategory:
- INVENTORY
- DISTRIBUTION
- SUPPLIER
- DAMAGED_PRODUCT
- DELIVERY_DELAY
- OTHER
```

Los contratos no deben inventar funcionalidades que la app no implementa.

## 12. Mock data realista

Los tickets mockeados deben parecer casos reales del contexto Panini.

Ejemplos de proveedores y casos:

```text
Proveedor: Distribuidora La Sabana
Categoría: INVENTORY
Título: Faltante de paquetes en lote regional
Descripción: El punto de venta reporta 120 paquetes menos respecto al despacho confirmado para la ruta GAM-03.
Prioridad: HIGH
Estado: OPEN
```

```text
Proveedor: Logística Caribe Norte
Categoría: DELIVERY_DELAY
Título: Retraso en entrega hacia puntos de venta de Sarapiquí
Descripción: La ruta programada para la mañana no llegó a tres comercios registrados, afectando la disponibilidad de sobres del álbum.
Prioridad: CRITICAL
Estado: IN_PROGRESS
```

```text
Proveedor: Central de Distribución Heredia
Categoría: DAMAGED_PRODUCT
Título: Cajas con paquetes dañados por humedad
Descripción: Se detectaron cajas con daño físico en el empaque durante la recepción de inventario para reposición semanal.
Prioridad: MEDIUM
Estado: OPEN
```

```text
Proveedor: Punto Venta Alajuela Centro
Categoría: SUPPLIER
Título: Proveedor no confirma reposición solicitada
Descripción: El comercio reporta falta de respuesta ante una solicitud de reposición generada hace más de 48 horas.
Prioridad: HIGH
Estado: OPEN
```

## 13. Modelo de dominio sugerido

```kotlin
data class Ticket(
    val id: String,
    val title: String,
    val description: String,
    val providerName: String,
    val category: TicketCategory,
    val priority: TicketPriority,
    val status: TicketStatus,
    val createdAt: String,
    val reportedBy: String,
    val location: String
)
```

Enums:

```kotlin
enum class TicketPriority {
    LOW,
    MEDIUM,
    HIGH,
    CRITICAL
}

enum class TicketStatus {
    OPEN,
    IN_PROGRESS,
    RESOLVED,
    CLOSED
}

enum class TicketCategory {
    INVENTORY,
    DISTRIBUTION,
    SUPPLIER,
    DAMAGED_PRODUCT,
    DELIVERY_DELAY,
    OTHER
}
```

## 14. API Result

Usar una sealed class simple para representar éxito o error:

```kotlin
sealed class ApiResult<out T> {
    data class Success<T>(val data: T) : ApiResult<T>()
    data class Error(val message: String, val statusCode: Int? = null) : ApiResult<Nothing>()
}
```

Esto evita que los ViewModels dependan directamente de excepciones o de detalles de Retrofit.

## 15. AppContainer / DI manual

Usar inyección de dependencias manual mediante `AppContainer`.

No usar Hilt para esta PoC, salvo que ya esté configurado y no agregue complejidad.

Ejemplo de intención arquitectónica:

```kotlin
object AppContainer {
    private val apiService: ApiService = RetrofitClient.apiService

    val ticketRepository: TicketRepository by lazy {
        TicketRepository(apiService = apiService)
    }
}
```

El repositorio puede usar mocks internamente mientras no exista backend.

## 16. Retrofit y preparación para backend futuro

`RetrofitClient` debe existir aunque la app use mocks.

Ejemplo conceptual:

```kotlin
object RetrofitClient {
    val apiService: ApiService by lazy {
        Retrofit.Builder()
            .baseUrl(AppConstants.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)
    }
}
```

La URL puede ser simulada:

```kotlin
const val BASE_URL = "https://api.panini-support.local/"
```

El punto no es consumir una API real. El punto es dejar clara la estructura de integración.

## 17. Navigation

Rutas sugeridas:

```text
login
tickets
ticketDetail/{ticketId}
createTicket
```

Flujo:

```text
LoginScreen -> TicketListScreen -> TicketDetailScreen
                         └──> CreateTicketScreen
```

Después de crear un ticket, se puede volver al listado con `popBackStack()`. El listado debe actualizarse solo por el flujo reactivo del repositorio.

## 18. Documentación requerida en `/docs`

La documentación debe ser breve, clara y conectada con el código real.

### `/docs/ARCHITECTURE.md`

Debe explicar:

- por qué se usó MVVM;
- responsabilidades de UI, ViewModel y Repository;
- por qué se usa AppContainer;
- cómo se prepara la app para backend futuro;
- por qué no se usó backend real;
- por qué no se agregó arquitectura excesiva.

### `/docs/EVENT_FLOW.md`

Debe explicar:

- cómo se usa `StateFlow`;
- cómo se actualiza el listado al crear tickets;
- cómo se reposicionan tickets al cambiar prioridad;
- por qué esto evita recargas manuales.

### `/docs/FEATURE_FLAGS.md`

Debe explicar:

- qué flags existen;
- qué controla cada una;
- por qué son útiles en pruebas internas;
- cómo podrían evolucionar hacia Remote Config o backend en una fase futura.

## 19. Video demo

El video debe presentarse como handoff técnico para otros ingenieros.

Duración máxima sugerida: 5 a 10 minutos.

Debe incluir:

- explicación breve del problema;
- pantallas principales;
- creación de ticket;
- actualización de estado;
- actualización de prioridad;
- explicación de `Flow` / `StateFlow`;
- explicación de Feature Flags;
- explicación de contratos YAML;
- cómo se podría conectar backend real en una fase futura.

No debe sonar como lectura genérica. Debe explicar el proyecto implementado.

## 20. Qué NO hacer

No hacer backend.
No agregar Room.
No agregar Firebase.
No agregar autenticación real.
No crear Clean Architecture completa con `domain/usecase` si no se justifica.
No crear documentación enorme.
No generar datos mock genéricos.
No poner lógica de negocio dentro de Composables.
No conectar la app a endpoints inexistentes como si fueran reales.
No implementar patrones que no se puedan explicar en el video.

## 21. Criterio general de implementación

La solución debe verse como una PoC profesional:

- pequeña;
- entendible;
- compilable;
- coherente con el enunciado;
- fácil de continuar;
- preparada para backend;
- sin sobreingeniería.

La prioridad es que otro ingeniero pueda abrir el repositorio, entender la estructura, ejecutar el proyecto y saber dónde conectar el backend real posteriormente.

## 22. Prompt recomendado para IA al trabajar el código

Usar este contexto al pedir cambios a Qwen, GPT u otra IA:

```text
Actúa como un desarrollador Android senior. Estoy desarrollando una prueba de concepto móvil para Panini Support Tickets usando Kotlin, Jetpack Compose, MVVM, Navigation Compose, Retrofit, DTOs, mock data y StateFlow.

No hay backend real. La app debe funcionar con mocks, pero debe dejar preparada la estructura para integrar backend posteriormente.

Evita sobreingeniería. No agregues Room, Firebase, Hilt, Clean Architecture completa ni capas innecesarias. Usa AppContainer como DI manual.

Respeta esta estructura base:
com.panini.support/
- core/
- data/
- data/remote/
- data/remote/model/
- data/mock/
- data/repository/
- ui/components/
- ui/screens/login/
- ui/screens/ticketlist/
- ui/screens/ticketdetail/
- ui/screens/createticket/
- util/FeatureFlags.kt

La comunicación reactiva debe funcionar con Flow o StateFlow:
1. al crear un ticket, el listado se actualiza automáticamente;
2. al cambiar la prioridad, el ticket se reposiciona automáticamente en el listado.

Implementa Feature Flags simples para habilitar/deshabilitar creación de tickets y actualización de prioridades.

Los mocks deben ser realistas y relacionados con proveedores, distribución, inventario, logística y soporte del álbum Panini Mundial 2026.

Antes de modificar código, explica brevemente qué archivos vas a tocar y por qué. Luego genera el código mínimo necesario, coherente y fácil de explicar en el video demo.
```