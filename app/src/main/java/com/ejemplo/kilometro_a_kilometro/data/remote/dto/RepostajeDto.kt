package com.ejemplo.kilometro_a_kilometro.data.remote.dto

import com.google.gson.annotations.SerializedName

data class RepostajeDto(
    val id: Int?,

    @SerializedName("vehiculo_id")
    val vehiculoId: Int?,

    val fecha: String?,

    val litros: Double?,

    @SerializedName("precio_total")
    val precioTotal: Double?,

    val kilometros: Int?
)