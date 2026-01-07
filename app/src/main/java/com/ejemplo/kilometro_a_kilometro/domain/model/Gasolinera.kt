package com.ejemplo.kilometro_a_kilometro.domain.model

data class Gasolinera(
    val nombre: String,
    val direccion: String,
    val municipio: String,
    val latitud: Double,
    val longitud: Double,

    val gasolina95: Double?,
    val gasolina98: Double?,
    val gasoleoA: Double?,
    val gasoleoPremium: Double?,
    val biodiesel: Double?,
    val bioetanol: Double?,
    val glp: Double?,
    val gnc: Double?,
    val gnl: Double?
)