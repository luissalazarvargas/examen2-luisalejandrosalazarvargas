package com.examen.paniniticke.data.remote.model

import com.google.gson.annotations.SerializedName

// ── Request ──────────────────────────────────────────────────────────────────

data class LoginRequestDto(
    @SerializedName("email") val email: String,
    @SerializedName("password") val password: String
)

// ── Response ──────────────────────────────────────────────────────────────────

data class LoginResponseDto(
    @SerializedName("userId") val userId: String,
    @SerializedName("token") val token: String,
    @SerializedName("userName") val userName: String,
    @SerializedName("role") val role: String
)
