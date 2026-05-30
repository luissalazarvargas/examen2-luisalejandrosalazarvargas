package com.examen.paniniticke.data

/**
 * Singleton en memoria para almacenar la sesión activa del usuario.
 * No persiste entre reinicios de la aplicación (por diseño en esta versión simulada).
 */
object AuthSession {

    private var _userId: String? = null
    private var _token: String? = null
    private var _userName: String? = null

    val userId: String? get() = _userId
    val token: String? get() = _token
    val userName: String? get() = _userName

    val isLoggedIn: Boolean get() = _token != null

    fun setSession(userId: String, token: String, userName: String) {
        _userId = userId
        _token = token
        _userName = userName
    }

    fun clearSession() {
        _userId = null
        _token = null
        _userName = null
    }
}
