package com.examen.paniniticke.ui.screens.list

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
 * ViewModel para gestionar el estado de la lista de tickets.
 * Se suscribe al StateFlow del repositorio para reaccionar a cambios en tiempo real.
 */
class TicketListViewModel(
    private val ticketRepository: TicketRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(TicketListUiState())
    val uiState: StateFlow<TicketListUiState> = _uiState.asStateFlow()

    init {
        observeTickets()
    }

    private fun observeTickets() {
        viewModelScope.launch {
            // Recolectar cambios reactivos desde el repositorio
            ticketRepository.tickets.collect { newTickets ->
                _uiState.update { it.copy(
                    isLoading = false,
                    tickets = newTickets
                )}
            }
        }
    }
}

class TicketListViewModelFactory(
    private val ticketRepository: TicketRepository
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(TicketListViewModel::class.java)) {
            return TicketListViewModel(ticketRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
