package com.examen.paniniticke.util

import androidx.compose.runtime.mutableStateOf

/**
 * Feature Flags para control rápido de funcionalidades en pruebas.
 * Cambiar a `false` para deshabilitar la funcionalidad correspondiente en toda la app.
 */
object FeatureFlags {
    /** Habilita/deshabilita el botón FAB y la pantalla de creación de tickets. */
    var enableTicketCreation = mutableStateOf(true)

    /** Habilita/deshabilita la edición de la prioridad de un ticket. */
    var enablePriorityUpdate = mutableStateOf(true)
}
