package com.examen.paniniticke.ui.screens.list

import com.examen.paniniticke.domain.model.Ticket

/**
 * Estado inmutable de la pantalla de lista de tickets.
 */
data class TicketListUiState(
    val isLoading: Boolean = true,
    val tickets: List<Ticket> = emptyList(),
    val errorMessage: String? = null
)
