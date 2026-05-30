package com.examen.paniniticke

import android.app.Application
import com.examen.paniniticke.data.AppContainer

/**
 * Clase Application personalizada.
 * Punto de entrada principal de la app antes de cualquier Activity.
 * Inicializa el contenedor de dependencias (DI manual) como un singleton atado al
 * ciclo de vida de la aplicación.
 */
class PaniniApplication : Application() {

    /** Contenedor de dependencias manual. */
    lateinit var container: AppContainer
        private set

    override fun onCreate() {
        super.onCreate()
        container = AppContainer(this)
    }
}
