package com.ejemplo.kilometro_a_kilometro.data.remote.api

import com.ejemplo.kilometro_a_kilometro.data.remote.dto.*
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.DELETE
import retrofit2.http.PUT
interface ApiService {

    // =========================
    // 🔐 USUARIOS
    // =========================
    @POST("login")
    suspend fun login(
        @Body data: LoginRequestDto
    ): Response<UsuarioDto>

    @POST("register")
    suspend fun register(
        @Body data: RegisterRequestDto
    ): Response<UsuarioDto>

    // =========================
    // 🚗 VEHÍCULOS
    // =========================
    @GET("vehiculos/{user_id}")
    suspend fun getVehiculos(
        @Path("user_id") userId: Int
    ): Response<List<VehiculoDto>>


    @POST("vehiculos")
    suspend fun crearVehiculo(
        @Body data: VehiculoCreateDto
    ): Response<Unit>


    @DELETE("vehiculos/{id}")
    suspend fun borrarVehiculo(
        @Path("id") vehiculoId: Int
    ): Response<Unit>



    @PUT("usuarios/{userId}/vehiculo-activo/{vehiculoId}")
    suspend fun establecerVehiculoActivo(
        @Path("userId") userId: Int,
        @Path("vehiculoId") vehiculoId: Int
    ): Response<Map<String, Any>>


    @GET("usuarios/{userId}")
    suspend fun getUsuario(
        @Path("userId") userId: Int
    ): Response<UsuarioDto>




}
