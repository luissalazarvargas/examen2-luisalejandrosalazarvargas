package com.examen.paniniticke.ui.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.examen.paniniticke.domain.model.TicketStatus
import com.examen.paniniticke.ui.theme.StatusInProgress
import com.examen.paniniticke.ui.theme.StatusOpen
import com.examen.paniniticke.ui.theme.StatusResolved

/**
 * Componente visual para mostrar el estado operativo de un ticket, con icono.
 */
@Composable
fun StatusBadge(
    status: TicketStatus,
    modifier: Modifier = Modifier
) {
    val (icon, color) = when (status) {
        TicketStatus.OPEN -> Icons.Default.Info to StatusOpen
        TicketStatus.IN_PROGRESS -> Icons.Default.Refresh to StatusInProgress
        TicketStatus.RESOLVED -> Icons.Default.CheckCircle to StatusResolved
    }

    Row(
        modifier = modifier.padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = color,
            modifier = Modifier.size(16.dp)
        )
        Spacer(modifier = Modifier.width(6.dp))
        Text(
            text = status.displayName,
            style = MaterialTheme.typography.labelMedium,
            color = color
        )
    }
}
