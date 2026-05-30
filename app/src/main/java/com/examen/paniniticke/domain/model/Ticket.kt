package com.examen.paniniticke.domain.model

import java.time.LocalDateTime


enum class TicketPriority(val displayName: String, val sortOrder: Int) {
    HIGH("Alta", 0),
    MEDIUM("Media", 1),
    LOW("Baja", 2)
}

enum class TicketStatus(val displayName: String) {
    OPEN("Abierto"),
    IN_PROGRESS("En Proceso"),
    RESOLVED("Resuelto")
}


enum class TicketCategory(val displayName: String) {
    DISTRIBUTION("Distribución"),
    INVENTORY("Inventario"),
    LOGISTICS("Logística"),
    ADMINISTRATIVE("Administrativo")
}

data class Ticket(
    val id: String,
    val title: String,
    val priority: TicketPriority,
    val status: TicketStatus,
    val provider: String,
    val createdAt: LocalDateTime,
    val category: TicketCategory,
    val description: String
)
