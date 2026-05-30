package com.examen.paniniticke.domain.mapper

import com.examen.paniniticke.data.remote.model.CreateTicketRequestDto
import com.examen.paniniticke.data.remote.model.TicketDto
import com.examen.paniniticke.domain.model.Ticket
import com.examen.paniniticke.domain.model.TicketCategory
import com.examen.paniniticke.domain.model.TicketPriority
import com.examen.paniniticke.domain.model.TicketStatus
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

private val ISO_FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE_TIME

// ── DTO → Dominio ─────────────────────────────────────────────────────────────

fun TicketDto.toDomain(): Ticket = Ticket(
    id = id,
    title = title,
    priority = TicketPriority.valueOf(priority.uppercase()),
    status = TicketStatus.valueOf(status.uppercase()),
    provider = provider,
    createdAt = LocalDateTime.parse(createdAt, ISO_FORMATTER),
    category = TicketCategory.valueOf(category.uppercase()),
    description = description
)

// ── Dominio → DTO ─────────────────────────────────────────────────────────────

fun Ticket.toDto(): TicketDto = TicketDto(
    id = id,
    title = title,
    priority = priority.name,
    status = status.name,
    provider = provider,
    createdAt = createdAt.format(ISO_FORMATTER),
    category = category.name,
    description = description
)

fun CreateTicketRequestDto.toDomain(generatedId: String): Ticket = Ticket(
    id = generatedId,
    title = title,
    priority = TicketPriority.valueOf(priority.uppercase()),
    status = TicketStatus.valueOf(status.uppercase()),
    provider = provider,
    createdAt = LocalDateTime.now(),
    category = TicketCategory.valueOf(category.uppercase()),
    description = description
)
