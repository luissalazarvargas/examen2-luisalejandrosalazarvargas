package com.examen.paniniticke.ui.screens.detail

import com.examen.paniniticke.domain.model.Ticket

/**
 * Estado inmutable de la pantalla de detalle de un ticket.
 */
data class TicketDetailUiState(
    val isLoading: Boolean = true,
    val ticket: Ticket? = null,
    val errorMessage: String? = null
)
