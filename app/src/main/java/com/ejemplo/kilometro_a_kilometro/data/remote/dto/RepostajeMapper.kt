package com.ejemplo.kilometro_a_kilometro.data.remote.dto

import com.ejemplo.kilometro_a_kilometro.domain.model.Repostaje

fun RepostajeDto.toDomain(): Repostaje {
    return Repostaje(
        id = id ?: 0,
        vehiculoId = vehiculoId ?: 0,
        fecha = fecha ?: "",
        litros = litros ?: 0.0,
        precioTotal = precioTotal ?: 0.0,
        kilometros = kilometros ?: 0
    )
}
