package com.ejemplo.kilometro_a_kilometro.data.remote.dto

import com.ejemplo.kilometro_a_kilometro.domain.model.Vehiculo

fun VehiculoDto.toDomain(): Vehiculo {
    return Vehiculo(
        id = id,
        tipo = tipo,
        marca = marca,
        modelo = modelo,
        matricula = matricula,
        anio = anio,
        combustible = combustible,
        consumo = consumo
    )
}
