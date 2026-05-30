package com.examen.paniniticke.ui.screens.list

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.examen.paniniticke.ui.components.EmptyState
import com.examen.paniniticke.ui.components.PaniniTopBar
import com.examen.paniniticke.ui.components.TicketCard
import com.examen.paniniticke.util.FeatureFlags

/**
 * Pantalla que muestra el listado de tickets activos.
 */
@Composable
fun TicketListScreen(
    viewModel: TicketListViewModel,
    onNavigateToDetail: (String) -> Unit,
    onNavigateToCreate: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            PaniniTopBar(title = "Tickets de Soporte")
        },
        floatingActionButton = {
            if (FeatureFlags.enableTicketCreation.value) {
                FloatingActionButton(onClick = onNavigateToCreate) {
                    Icon(imageVector = Icons.Default.Add, contentDescription = "Nuevo Ticket")
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when {
                uiState.isLoading -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }
                uiState.tickets.isEmpty() -> {
                    EmptyState(
                        title = "Todo en orden",
                        message = "No hay incidencias logísticas reportadas en este momento."
                    )
                }
                else -> {
                    Column(modifier = Modifier.fillMaxSize()) {
                        // Switch para simular activación/desactivación del Feature Flag de Creación
                        androidx.compose.foundation.layout.Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            androidx.compose.material3.Text("Habilitar Creación (Feature Flag)")
                            androidx.compose.material3.Switch(
                                checked = FeatureFlags.enableTicketCreation.value,
                                onCheckedChange = { FeatureFlags.enableTicketCreation.value = it }
                            )
                        }

                        // Switch para simular activación/desactivación del Feature Flag de Actualización
                        androidx.compose.foundation.layout.Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            androidx.compose.material3.Text("Habilitar Edición de Prioridad")
                            androidx.compose.material3.Switch(
                                checked = FeatureFlags.enablePriorityUpdate.value,
                                onCheckedChange = { FeatureFlags.enablePriorityUpdate.value = it }
                            )
                        }
                        
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(16.dp),
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            items(uiState.tickets, key = { it.id }) { ticket ->
                                TicketCard(
                                    ticket = ticket,
                                    onClick = { onNavigateToDetail(ticket.id) }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
