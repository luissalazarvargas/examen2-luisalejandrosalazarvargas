package com.examen.paniniticke.data.repository

import com.examen.paniniticke.core.AppConstants
import com.examen.paniniticke.data.remote.ApiService
import com.examen.paniniticke.data.remote.model.CreateTicketRequestDto
import com.examen.paniniticke.domain.mapper.toDomain
import com.examen.paniniticke.domain.model.Ticket
import com.examen.paniniticke.domain.model.TicketCategory
import com.examen.paniniticke.domain.model.TicketPriority
import com.examen.paniniticke.domain.model.TicketStatus
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import java.time.LocalDateTime
import java.util.UUID

/**
 * Repositorio central de tickets. Fuente única de verdad.
 *
 * Mantiene una [MutableStateFlow] interna de tickets ordenados por prioridad.
 * Cualquier cambio (creación, actualización de prioridad) emite automáticamente
 * la lista reordenada a todos los colectores.
 */
class TicketRepository(
    private val apiService: ApiService
) {

    /** Lista mutable interna ordenada por prioridad. */
    private val _tickets = MutableStateFlow<List<Ticket>>(initialMockTickets())

    /** Flujo público inmutable para la UI. Siempre ordenado HIGH → MEDIUM → LOW. */
    val tickets: StateFlow<List<Ticket>> = _tickets.asStateFlow()

    /**
     * Crea un nuevo ticket y lo inserta en el flujo reactivo.
     * La pantalla de listado reacciona automáticamente sin recarga manual.
     */
    suspend fun createTicket(request: CreateTicketRequestDto): ApiResult<Unit> {
        delay(AppConstants.MOCK_DELAY_MS)
        return try {
            val newId = "TKT-${UUID.randomUUID().toString().take(8).uppercase()}"
            val newTicket = request.toDomain(newId)
            _tickets.update { current ->
                (current + newTicket).sortedBy { it.priority.sortOrder }
            }
            ApiResult.Success(Unit)
        } catch (e: Exception) {
            ApiResult.Error("Error al crear el ticket: ${e.message}")
        }
    }

    /**
     * Actualiza la prioridad de un ticket y reordena la lista inmediatamente.
     * Los tickets HIGH suben automáticamente al tope del LazyColumn.
     */
    suspend fun updateTicketPriority(ticketId: String, newPriority: TicketPriority): ApiResult<Unit> {
        delay(AppConstants.MOCK_DELAY_MS)
        return try {
            _tickets.update { current ->
                current.map { ticket ->
                    if (ticket.id == ticketId) ticket.copy(priority = newPriority) else ticket
                }.sortedBy { it.priority.sortOrder }
            }
            ApiResult.Success(Unit)
        } catch (e: Exception) {
            ApiResult.Error("Error al actualizar la prioridad: ${e.message}")
        }
    }

    /**
     * Actualiza el estado de un ticket.
     */
    suspend fun updateTicketStatus(ticketId: String, newStatus: TicketStatus): ApiResult<Unit> {
        delay(AppConstants.MOCK_DELAY_MS / 2)
        return try {
            _tickets.update { current ->
                current.map { ticket ->
                    if (ticket.id == ticketId) ticket.copy(status = newStatus) else ticket
                }
            }
            ApiResult.Success(Unit)
        } catch (e: Exception) {
            ApiResult.Error("Error al actualizar el estado: ${e.message}")
        }
    }

    /** Retorna un ticket por ID, o null si no existe. */
    fun getTicketById(id: String): Ticket? = _tickets.value.find { it.id == id }

    // ── Datos Mock Realistas Panini 2026 ──────────────────────────────────────

    private fun initialMockTickets(): List<Ticket> = listOf(
        Ticket(
            id = "TKT-MX2026-001",
            title = "Faltante crítico de sobres en ruta norte — Zona Jalisco",
            priority = TicketPriority.HIGH,
            status = TicketStatus.OPEN,
            provider = "Transportes Noreste S.A. de C.V.",
            createdAt = LocalDateTime.of(2026, 5, 28, 8, 30, 0),
            category = TicketCategory.DISTRIBUTION,
            description = "El camión asignado a la ruta norte de Jalisco (folio TRANS-JL-0448) " +
                    "reporta ausencia de 3,200 sobres del lote FIFA World Cup 2026. La mercancía " +
                    "no fue cargada en el CEDIS Guadalajara. Se requiere reposición urgente para " +
                    "no interrumpir la cadena de puntos de venta en Zapopan, Tlaquepaque y Tonalá."
        ),
        Ticket(
            id = "TKT-MX2026-002",
            title = "Retraso en despacho hacia CEDIS Monterrey — 72 horas de atraso",
            priority = TicketPriority.HIGH,
            status = TicketStatus.IN_PROGRESS,
            provider = "Logística Express MX S.A.",
            createdAt = LocalDateTime.of(2026, 5, 27, 14, 15, 0),
            category = TicketCategory.LOGISTICS,
            description = "El embarque con código EMB-MTY-0219 que debía arribar al CEDIS Monterrey " +
                    "el día 25/05/2026 presenta un retraso acumulado de 72 horas. La unidad reporta " +
                    "problemas mecánicos en la carretera 57 a la altura de San Luis Potosí. El lote " +
                    "contiene 15,000 álbumes y 80,000 sobres destinados a distribuidores del noreste."
        ),
        Ticket(
            id = "TKT-MX2026-003",
            title = "Daño por humedad en cajas de álbumes — Bodega Toluca Recepción",
            priority = TicketPriority.MEDIUM,
            status = TicketStatus.OPEN,
            provider = "Almacenes del Centro S.A.",
            createdAt = LocalDateTime.of(2026, 5, 26, 9, 45, 0),
            category = TicketCategory.INVENTORY,
            description = "En la revisión de recepción del lote RECV-TOL-0581 se detectaron 45 cajas " +
                    "con daño severo por humedad. Los álbumes presentan deformación y manchas de agua " +
                    "en las tapas. Se estima afectación de 1,800 unidades. Se solicita proceso de " +
                    "devolución al proveedor y reposición inmediata para mantener el stock regional."
        ),
        Ticket(
            id = "TKT-MX2026-004",
            title = "Error en facturación lote FIFA 2026 — Región Sur Pacífico",
            priority = TicketPriority.LOW,
            status = TicketStatus.RESOLVED,
            provider = "Distribuidora Sur Pacífico S.C.",
            createdAt = LocalDateTime.of(2026, 5, 24, 11, 0, 0),
            category = TicketCategory.ADMINISTRATIVE,
            description = "La distribuidora reporta discrepancia en la factura FAC-2026-00842: se " +
                    "facturaron 5,000 sobres pero el pedido oficial era de 4,500. La diferencia de " +
                    "500 sobres (valor aprox. MXN 12,500) genera conflicto contable. Se requiere " +
                    "nota de crédito o ajuste de factura por parte del equipo administrativo central."
        ),
        Ticket(
            id = "TKT-MX2026-005",
            title = "Ruptura de stock en 8 tiendas — CDMX Zona Oriente",
            priority = TicketPriority.MEDIUM,
            status = TicketStatus.IN_PROGRESS,
            provider = "Red de Distribución CDMX",
            createdAt = LocalDateTime.of(2026, 5, 29, 7, 0, 0),
            category = TicketCategory.DISTRIBUTION,
            description = "Se confirma ruptura de stock de sobres FIFA 2026 en 8 puntos de venta " +
                    "de la zona oriente de CDMX (Iztapalapa, Iztacalco, Venustiano Carranza). " +
                    "Alta demanda post-lanzamiento de la colección especial México. Se solicita " +
                    "reabastecimiento prioritario desde CEDIS CDMX Norte en un plazo máximo de 48h."
        )
    ).sortedBy { it.priority.sortOrder }
}
