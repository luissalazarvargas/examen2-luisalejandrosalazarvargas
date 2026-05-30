package com.examen.paniniticke.ui.screens.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.examen.paniniticke.data.repository.TicketRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * ViewModel para gestionar el estado de los detalles de un ticket.
 */
class TicketDetailViewModel(
    private val ticketId: String,
    private val ticketRepository: TicketRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(TicketDetailUiState())
    val uiState: StateFlow<TicketDetailUiState> = _uiState.asStateFlow()

    init {
        loadTicket()
    }

    private fun loadTicket() {
        viewModelScope.launch {
            // El repositorio expone el StateFlow actualizado, lo que nos permite reaccionar
            // a los cambios si se hace un update desde otra pantalla o servicio.
            ticketRepository.tickets.collect { tickets ->
                val ticket = tickets.find { it.id == ticketId }
                if (ticket != null) {
                    _uiState.update { it.copy(isLoading = false, ticket = ticket, errorMessage = null) }
                } else {
                    _uiState.update { it.copy(isLoading = false, errorMessage = "Ticket no encontrado") }
                }
            }
        }
    }
}

class TicketDetailViewModelFactory(
    private val ticketId: String,
    private val ticketRepository: TicketRepository
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(TicketDetailViewModel::class.java)) {
            return TicketDetailViewModel(ticketId, ticketRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
