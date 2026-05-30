package com.examen.paniniticke.ui.screens.detail

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.examen.paniniticke.domain.model.Ticket
import com.examen.paniniticke.ui.components.PaniniTopBar
import com.examen.paniniticke.ui.components.PriorityChip
import com.examen.paniniticke.ui.components.StatusBadge
import com.examen.paniniticke.util.FeatureFlags
import java.time.format.DateTimeFormatter

private val DATE_FORMATTER = DateTimeFormatter.ofPattern("dd MMMM yyyy, HH:mm")

/**
 * Pantalla que muestra el detalle completo de un ticket.
 */
@Composable
fun TicketDetailScreen(
    viewModel: TicketDetailViewModel,
    onNavigateBack: () -> Unit,
    onNavigateToStatus: (String) -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            PaniniTopBar(
                title = "Detalle de Ticket",
                canNavigateBack = true,
                onNavigateBack = onNavigateBack
            )
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
                uiState.errorMessage != null -> {
                    Text(
                        text = uiState.errorMessage ?: "Error desconocido",
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
                uiState.ticket != null -> {
                    TicketDetailContent(
                        ticket = uiState.ticket!!,
                        onUpdatePriorityClick = { onNavigateToStatus(uiState.ticket!!.id) }
                    )
                }
            }
        }
    }
}

@Composable
private fun TicketDetailContent(
    ticket: Ticket,
    onUpdatePriorityClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(24.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = ticket.id,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary
            )
            PriorityChip(priority = ticket.priority)
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = ticket.title,
            style = MaterialTheme.typography.headlineSmall
        )

        Spacer(modifier = Modifier.height(24.dp))
        HorizontalDivider()
        Spacer(modifier = Modifier.height(16.dp))

        DetailRow(label = "Estado", content = { StatusBadge(status = ticket.status) })
        DetailRow(label = "Categoría", value = ticket.category.displayName)
        DetailRow(label = "Proveedor", value = ticket.provider)
        DetailRow(label = "Fecha de reporte", value = ticket.createdAt.format(DATE_FORMATTER))

        Spacer(modifier = Modifier.height(16.dp))
        HorizontalDivider()
        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Descripción del Incidente",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = ticket.description,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(32.dp))

        if (FeatureFlags.enablePriorityUpdate.value) {
            Button(
                onClick = onUpdatePriorityClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
            ) {
                Text("Actualizar Prioridad / Estado")
            }
        }
    }
}

@Composable
private fun DetailRow(
    label: String,
    value: String? = null,
    content: (@Composable () -> Unit)? = null
) {
    Column(modifier = Modifier.padding(vertical = 8.dp)) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.outline
        )
        Spacer(modifier = Modifier.height(4.dp))
        if (content != null) {
            content()
        } else if (value != null) {
            Text(
                text = value,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}
