package com.examen.paniniticke.ui.screens.login

/**
 * Estado inmutable de la pantalla de Login.
 */
data class LoginUiState(
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val isSuccess: Boolean = false
)
