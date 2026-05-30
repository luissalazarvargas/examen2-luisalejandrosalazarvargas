package com.examen.paniniticke.data.remote

import com.examen.paniniticke.data.remote.model.CreateTicketRequestDto
import com.examen.paniniticke.data.remote.model.LoginRequestDto
import com.examen.paniniticke.data.remote.model.LoginResponseDto
import com.examen.paniniticke.data.remote.model.TicketDto
import com.examen.paniniticke.data.remote.model.UpdatePriorityRequestDto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

/**
 * Contrato de la API REST de Panini Support.
 * Los métodos son suspend functions para uso con Coroutines.
 * Cuando el backend esté disponible, solo se actualiza BASE_URL en AppConstants.
 */
interface ApiService {

    @POST("auth/login")
    suspend fun login(
        @Body request: LoginRequestDto
    ): Response<LoginResponseDto>

    @GET("tickets")
    suspend fun getTickets(): Response<List<TicketDto>>

    @POST("tickets")
    suspend fun createTicket(
        @Body request: CreateTicketRequestDto
    ): Response<TicketDto>

    @PUT("tickets/{id}/priority")
    suspend fun updateTicketPriority(
        @Path("id") ticketId: String,
        @Body request: UpdatePriorityRequestDto
    ): Response<TicketDto>
}
