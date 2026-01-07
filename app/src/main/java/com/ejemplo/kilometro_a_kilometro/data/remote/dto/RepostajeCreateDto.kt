package com.ejemplo.kilometro_a_kilometro.data.remote.dto

data class RepostajeCreateDto(
    val vehiculo_id: Int,
    val fecha: String,
    val litros: Double,
    val precio_total: Double,
    val kilometros: Int
)
