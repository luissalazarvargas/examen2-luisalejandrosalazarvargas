package com.examen.paniniticke.data.repository

import com.examen.paniniticke.data.AuthSession
import com.examen.paniniticke.data.remote.ApiService
import com.examen.paniniticke.data.remote.model.LoginRequestDto
import kotlinx.coroutines.delay
import com.examen.paniniticke.core.AppConstants

/**
 * Repositorio de autenticación.
 * Simula validación de credenciales mockeadas y gestiona el ciclo de vida de la sesión.
 * Cuando el backend esté disponible, se utilizará [apiService] para llamadas reales.
 */
class AuthRepository(
    private val apiService: ApiService,
    private val authSession: AuthSession = AuthSession
) {

    /**
     * Valida credenciales contra usuarios mockeados de Panini Operations.
     * Simula latencia de red con un delay.
     */
    suspend fun login(email: String, password: String): ApiResult<Unit> {
        delay(AppConstants.MOCK_DELAY_MS)

        return when {
            email.isBlank() || password.isBlank() ->
                ApiResult.Error("El correo y la contraseña son obligatorios.")

            !MOCK_USERS.containsKey(email) ->
                ApiResult.Error("Usuario no encontrado.", 404)

            MOCK_USERS[email] != password ->
                ApiResult.Error("Contraseña incorrecta.", 401)

            else -> {
                val userName = MOCK_USER_NAMES[email] ?: email
                authSession.setSession(
                    userId = "usr_${email.hashCode()}",
                    token = "mock_token_${System.currentTimeMillis()}",
                    userName = userName
                )
                ApiResult.Success(Unit)
            }
        }
    }

    fun logout() {
        authSession.clearSession()
    }

    companion object {
        /** Credenciales mockeadas para el equipo de operaciones de Panini. */
        private val MOCK_USERS = mapOf(
            "admin@panini.com" to "panini2026",
            "operaciones@panini.com" to "ops2026",
            "logistica@panini.com" to "log2026"
        )

        private val MOCK_USER_NAMES = mapOf(
            "admin@panini.com" to "Administrador Panini",
            "operaciones@panini.com" to "Equipo Operaciones MX",
            "logistica@panini.com" to "Coordinador Logística"
        )
    }
}
