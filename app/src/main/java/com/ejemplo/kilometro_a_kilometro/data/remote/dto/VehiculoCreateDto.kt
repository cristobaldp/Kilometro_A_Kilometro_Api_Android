package com.ejemplo.kilometro_a_kilometro.data.remote.dto

data class VehiculoCreateDto(
    val user_id: Int,
    val tipo: String,
    val marca: String,
    val modelo: String,
    val matricula: String,
    val anio: Int,
    val combustible: String,
    val consumo: Double
)
