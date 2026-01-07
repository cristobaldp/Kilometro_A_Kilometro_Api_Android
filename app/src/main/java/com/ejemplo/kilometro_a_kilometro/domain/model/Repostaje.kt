package com.ejemplo.kilometro_a_kilometro.domain.model

data class Repostaje(
    val id: Int = 0,
    val vehiculoId: Int,
    val fecha: String,
    val litros: Double,
    val precioTotal: Double,
    val kilometros: Int
)