package com.examen.paniniticke.ui.screens.create

/**
 * Estado inmutable de la pantalla de creación de ticket.
 */
data class CreateTicketUiState(
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false,
    val errorMessage: String? = null
)
