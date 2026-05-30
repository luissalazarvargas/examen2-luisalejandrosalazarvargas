package com.examen.paniniticke.data

import android.content.Context
import com.examen.paniniticke.core.NetworkMonitor
import com.examen.paniniticke.data.remote.RetrofitClient
import com.examen.paniniticke.data.repository.AuthRepository
import com.examen.paniniticke.data.repository.TicketRepository

/**
 * Contenedor de Inyección de Dependencias manual.
 * Inicializado una sola vez en [PaniniApplication] y accedido desde los ViewModels
 * a través del cast de `application` en las factories.
 *
 * Transición a producción: solo modificar las instancias aquí para inyectar
 * implementaciones reales sin tocar ViewModels ni UI.
 */
class AppContainer(context: Context) {

    /** Monitor de estado de red del dispositivo. */
    val networkMonitor = NetworkMonitor()

    /** Instancia compartida del cliente Retrofit. */
    private val apiService = RetrofitClient.instance

    /** Repositorio de autenticación con credenciales mockeadas. */
    val authRepository = AuthRepository(apiService, AuthSession)

    /** Repositorio central de tickets — fuente única de verdad. */
    val ticketRepository = TicketRepository(apiService)
}
