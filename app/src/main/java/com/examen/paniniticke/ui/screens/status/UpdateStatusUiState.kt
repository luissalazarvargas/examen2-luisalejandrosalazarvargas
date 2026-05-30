package com.examen.paniniticke.ui.screens.status

import com.examen.paniniticke.domain.model.Ticket

/**
 * Estado inmutable de la pantalla de actualización de estado y prioridad de un ticket.
 */
data class UpdateStatusUiState(
    val isLoading: Boolean = true,
    val isUpdating: Boolean = false,
    val ticket: Ticket? = null,
    val isSuccess: Boolean = false,
    val errorMessage: String? = null
)
