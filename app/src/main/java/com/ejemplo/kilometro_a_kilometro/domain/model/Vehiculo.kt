package com.ejemplo.kilometro_a_kilometro.domain.model

data class Vehiculo(
    val id: Int,
    val tipo: String,
    val marca: String,
    val modelo: String,
    val matricula: String,
    val anio: Int,
    val combustible: String,
    val consumo: Double
)
