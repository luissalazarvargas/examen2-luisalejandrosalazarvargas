# `01-arquitectura-y-justificacion.md`

## 1. Enfoque Arquitectónico (MVVM)

Se utilizará el patrón arquitectónico **Model-View-ViewModel (MVVM)**. Este patrón permite desacoplar la lógica de presentación de la lógica de adquisición de datos, para que el sistema sea mantenible y extensible para futuros ingenieros, sin tomar otras arquitecturas más complejas para mayor simplicidad, facilidad de implementación por los ingenieros,etc.

La separación de responsabilidades se define de la siguiente manera:

*   **View (Jetpack Compose):** Capa de interfaz de usuario puramente declarativa. Observa el estado expuesto por el ViewModel y delega las acciones del usuario de manera directa.
*   **ViewModel:** Administra el estado de la pantalla y coordina las operaciones con la capa de datos.
*   **Repository:** La única fuente de verdad y el punto de acceso unificado a los datos. Abstrae el origen físico de la información.

```text
┌─────────────────────────────────┐
│        UI (Jetpack Compose)     │
└────────────────┬────────────────┘
                 │ (Acciones de usuario)
                 ▼
┌─────────────────────────────────┐
│           ViewModel             │
└────────────────┬────────────────┘
                 │ (Invoca operaciones)
                 ▼
┌─────────────────────────────────┐
│           Repository            │
└────────────────┬────────────────┘
                 │
        ┌────────┴────────┐
        ▼                 ▼
┌───────────────┐ ┌───────────────┐
│ Local Cache*  │ │  Network API  │
│  (Room DB)    │ │  (Retrofit)   │
└───────────────┘ └───────────────┘
*Preparado estructuralmente para futura implementación.
```

---

## 2. Organización del Proyecto y Estructura de Paquetes

Siguiendo el diseño modular probado en el último laboratorio, el código fuente en `com.examen.paniniticket` se organiza por capas funcionales para reducir acoplamiento:

```text
com.panini.tickets/
├── PaniniApplication.kt            # Punto de entrada; inicializa el contenedor de dependencias (Manual DI)
├── MainActivity.kt                 # Única Actividad que aloja el grafo de navegación
├── PaniniApp.kt                    # Composable de nivel superior, tema y NavHost
│
├── core/                           # Utilidades e infraestructura técnica global
│   ├── AppConstants.kt             # Constantes globales (Rutas de endpoints, etiquetas)
│   └── NetworkMonitor.kt           # Monitoreo reactivo del estado de red del dispositivo
│
├── data/                           # Capa de datos (Persistencia, servicios web y modelos)
│   ├── AppContainer.kt             # Contenedor de Inyección de Dependencias manual
│   ├── AuthSession.kt              # Singleton en memoria para almacenar la sesión activa
│   │
│   ├── remote/                     # Cliente de red y contratos externos (Retrofit)
│   │   ├── ApiService.kt           # Interfaz de llamadas HTTP/Retrofit
│   │   ├── RetrofitClient.kt       # Instancia configurada de OkHttpClient y serialización JSON
│   │   └── model/                  # Data Transfer Objects (DTOs)
│   │       ├── AuthModels.kt       # Requests y Responses de autenticación
│   │       └── TicketModels.kt     # Payloads de red para la entidad Ticket
│   │
│   └── repository/                 # Implementaciones de lógica de acceso a datos
│       ├── ApiResult.kt            # Monad / Contenedor genérico para resultados (Success / Error)
│       ├── AuthRepository.kt       # Manejo de credenciales y estados de sesión
│       └── TicketRepository.kt     # Repositorio centralizador de tickets de soporte
│
├── domain/                         # Lógica pura de negocio independiente del framework
│   ├── model/                      # Modelos de dominio limpios utilizados por la UI
│   │   └── Ticket.kt               # Representación interna del Ticket de soporte
│   └── mapper/                     # Funciones de extensión para conversión de modelos
│       └── TicketMapper.kt         # Mapeo bidireccional DTO <-> Dominio
│
└── ui/                             # Capa de Presentación (Jetpack Compose)
    ├── components/                 # Componentes gráficos reutilizables (Tarjetas, botones, etc.)
    ├── screens/                    # Pantallas de la aplicación organizadas por funcionalidad
    │   ├── login/                  # Autenticación simulada
    │   ├── list/                   # LazyColumn reactiva de tickets con ordenamiento por prioridad
    │   ├── detail/                 # Vista de detalles del ticket
    │   ├── create/                 # Creación de reportes de soporte
    │   └── status/                 # Componentes para actualización de estado del ticket
    └── theme/                      # Definición de tipografía, colores y formas (M3)
```

---

## 3. Justificación de Diseño del Modelo de Datos (DTOs y Dominio)

Esta separación, aunque aumenta la complejidad en abstracción de arquitectura, se decidió debido a ser la mejor practica en cuanto a mantenibilidad.
El costo de realizar esta separación es mínimo en términos de tiempo, y el beneficio a largo plazo es significativo, ya que permite una mayor flexibilidad para adaptarse a cambios futuros en la API o en los requisitos de la aplicación sin afectar la lógica de negocio ni la presentación.

### Estructura de un Ticket en Dominio (`Ticket.kt`)
Modelo de dominio con propiedades y tipos de datos tipados para facilitar operaciones en la aplicación móvil:

```kotlin
data class Ticket(
    val id: String,
    val title: String,
    val priority: TicketPriority,      // Enum: HIGH, MEDIUM, LOW
    val status: TicketStatus,          // Enum: OPEN, IN_PROGRESS, RESOLVED
    val provider: String,              // Nombre del proveedor afectado
    val createdAt: LocalDateTime,      // Fecha con soporte para ordenamiento cronológico
    val category: TicketCategory,      // Enum: DISTRIBUTION, INVENTORY, LOGISTICS, ADMINISTRATIVE
    val description: String
)
```

### Estructura de Red (`TicketModels.kt` - DTOs)
Los objetos de red (`TicketDto` y `CreateTicketRequestDto`) se diseñaron utilizando tipos de datos primitivos (`String`, `Int`). Esto simplifica la deserialización JSON y asegura compatibilidad con los estándares habituales de transporte de APIs RESTful:

```kotlin
data class TicketDto(
    @SerializedName("id") val id: String,
    @SerializedName("title") val title: String,
    @SerializedName("priority") val priority: String, // Recibido como String ("HIGH", "MEDIUM", "LOW")
    @SerializedName("status") val status: String,     // Recibido como String ("OPEN", etc.)
    @SerializedName("provider") val provider: String,
    @SerializedName("createdAt") val createdAt: String,
    @SerializedName("category") val category: String,
    @SerializedName("description") val description: String
)
```

### Justificación de esta estructura:
1.  **Seguridad de Tipos:** Los enums (`TicketPriority`, `TicketStatus`, `TicketCategory`) en el modelo de dominio evitan inconsistencias lógicas en la UI. Así obtenemos mayor seguridad en caso de recibir datos diferentes.
2.  **Operación de Fechas:** Mantener `createdAt` como `LocalDateTime` en el dominio esto para poder hacer un correcto formateo.
3.  **Desacoplamiento Estricto:** En caso de que en un futuro se cambie en el backend en nombre de algun atributo, de esta manera es más mantenible ya que solo tenemos que modificar el SerializedName.

---

## 4. Estrategia de Mocking y Preparación para Producción

Para cumplir con la restricción de presupuesto y tiempo, se ha implementado un sistema que simula la integración de red mediante Retrofit:

1.  **Interfaces de Contrato Comunes (`ApiService`):** Se declaran las firmas de métodos HTTP, con las firmas que luego utilizará el backend en la futura implementación de la empresa.
2.  **Mocking a nivel de Repositorio o Interceptor:** Se incluye una implementación del servicio de red que lee datos de prueba. Esto para evaluar los estados de manera realista.
3.  **Transición Simple:** Cuando el backend esté construido, el equipo técnico solo deberá modificar la provisión en el `AppContainer` para inyectar el cliente Retrofit real conectado a la URL del servidor, sin necesidad de alterar los ViewModels, ni logica interna.

---
