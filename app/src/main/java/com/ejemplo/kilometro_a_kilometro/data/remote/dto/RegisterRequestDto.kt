package com.ejemplo.kilometro_a_kilometro.data.remote.dto

data class RegisterRequestDto(
    val nombre: String,
    val apellidos: String,
    val username: String,
    val email: String,
    val telefono: String,
    val ciudad: String,
    val fecha_nacimiento: String,
    val password: String
)
