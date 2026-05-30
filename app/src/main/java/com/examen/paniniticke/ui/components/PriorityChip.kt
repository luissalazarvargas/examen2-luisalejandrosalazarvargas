package com.examen.paniniticke.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.examen.paniniticke.domain.model.TicketPriority
import com.examen.paniniticke.ui.theme.PriorityHigh
import com.examen.paniniticke.ui.theme.PriorityHighContainer
import com.examen.paniniticke.ui.theme.PriorityLow
import com.examen.paniniticke.ui.theme.PriorityLowContainer
import com.examen.paniniticke.ui.theme.PriorityMedium
import com.examen.paniniticke.ui.theme.PriorityMediumContainer

/**
 * Componente visual para mostrar la prioridad de un ticket con colores semánticos.
 */
@Composable
fun PriorityChip(
    priority: TicketPriority,
    modifier: Modifier = Modifier
) {
    val (containerColor, contentColor) = when (priority) {
        TicketPriority.HIGH -> PriorityHighContainer to PriorityHigh
        TicketPriority.MEDIUM -> PriorityMediumContainer to PriorityMedium
        TicketPriority.LOW -> PriorityLowContainer to PriorityLow
    }

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(containerColor)
            .padding(horizontal = 12.dp, vertical = 6.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = priority.displayName.uppercase(),
            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
            color = contentColor
        )
    }
}
