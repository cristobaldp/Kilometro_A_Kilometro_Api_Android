package com.ejemplo.kilometro_a_kilometro.data.remote.api

import com.ejemplo.kilometro_a_kilometro.data.remote.dto.*
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface ApiService {

    @POST("login")
    suspend fun login(
        @Body data: LoginRequestDto
    ): Response<UsuarioDto>

    @POST("register")
    suspend fun register(
        @Body data: RegisterRequestDto
    ): Response<UsuarioDto>
}
