package com.examen.paniniticke.data.repository

/**
 * Contenedor genérico para resultados de operaciones de repositorio.
 * Permite a los ViewModels manejar éxito y error de forma estructurada,
 * sin necesidad de manejar excepciones directamente.
 */
sealed class ApiResult<out T> {
    data class Success<T>(val data: T) : ApiResult<T>()
    data class Error(
        val message: String,
        val statusCode: Int? = null
    ) : ApiResult<Nothing>()
}
