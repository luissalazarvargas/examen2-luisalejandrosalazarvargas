package com.examen.paniniticke.ui.screens.status

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.examen.paniniticke.data.repository.ApiResult
import com.examen.paniniticke.data.repository.TicketRepository
import com.examen.paniniticke.domain.model.TicketPriority
import com.examen.paniniticke.domain.model.TicketStatus
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * ViewModel para gestionar la actualización del estado y prioridad de un ticket.
 */
class UpdateStatusViewModel(
    private val ticketId: String,
    private val ticketRepository: TicketRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(UpdateStatusUiState())
    val uiState: StateFlow<UpdateStatusUiState> = _uiState.asStateFlow()

    init {
        loadTicket()
    }

    private fun loadTicket() {
        val ticket = ticketRepository.getTicketById(ticketId)
        if (ticket != null) {
            _uiState.update { it.copy(isLoading = false, ticket = ticket) }
        } else {
            _uiState.update { it.copy(isLoading = false, errorMessage = "Ticket no encontrado") }
        }
    }

    fun updateTicket(newPriority: TicketPriority, newStatus: TicketStatus) {
        _uiState.update { it.copy(isUpdating = true, errorMessage = null) }

        viewModelScope.launch {
            // Actualizar prioridad
            val priorityResult = ticketRepository.updateTicketPriority(ticketId, newPriority)
            if (priorityResult is ApiResult.Error) {
                _uiState.update { it.copy(isUpdating = false, errorMessage = priorityResult.message) }
                return@launch
            }

            // Actualizar estado
            val statusResult = ticketRepository.updateTicketStatus(ticketId, newStatus)
            if (statusResult is ApiResult.Error) {
                _uiState.update { it.copy(isUpdating = false, errorMessage = statusResult.message) }
                return@launch
            }

            // Éxito en ambos
            _uiState.update { it.copy(isUpdating = false, isSuccess = true) }
        }
    }

    fun dismissError() {
        _uiState.update { it.copy(errorMessage = null) }
    }
}

class UpdateStatusViewModelFactory(
    private val ticketId: String,
    private val ticketRepository: TicketRepository
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(UpdateStatusViewModel::class.java)) {
            return UpdateStatusViewModel(ticketId, ticketRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
