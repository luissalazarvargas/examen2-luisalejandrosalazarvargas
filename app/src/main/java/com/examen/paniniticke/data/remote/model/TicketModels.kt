package com.examen.paniniticke.data.remote.model

import com.google.gson.annotations.SerializedName

// ── Response DTO ──────────────────────────────────────────────────────────────

/**
 * DTO de red para un Ticket de soporte. Todos los campos son primitivos (String/Int)
 * para simplicidad de deserialización JSON. Se mapea al modelo de dominio via TicketMapper.
 */
data class TicketDto(
    @SerializedName("id") val id: String,
    @SerializedName("title") val title: String,
    @SerializedName("priority") val priority: String,       // "HIGH" | "MEDIUM" | "LOW"
    @SerializedName("status") val status: String,           // "OPEN" | "IN_PROGRESS" | "RESOLVED"
    @SerializedName("provider") val provider: String,
    @SerializedName("createdAt") val createdAt: String,
    @SerializedName("category") val category: String,       // "DISTRIBUTION" | "INVENTORY" | "LOGISTICS" | "ADMINISTRATIVE"
    @SerializedName("description") val description: String
)

// ── Request DTOs ──────────────────────────────────────────────────────────────

data class CreateTicketRequestDto(
    @SerializedName("title") val title: String,
    @SerializedName("priority") val priority: String,
    @SerializedName("status") val status: String,
    @SerializedName("provider") val provider: String,
    @SerializedName("category") val category: String,
    @SerializedName("description") val description: String
)

data class UpdatePriorityRequestDto(
    @SerializedName("priority") val priority: String
)
