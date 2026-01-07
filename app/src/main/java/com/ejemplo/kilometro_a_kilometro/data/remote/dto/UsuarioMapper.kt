package com.ejemplo.kilometro_a_kilometro.data.remote.dto

import com.ejemplo.kilometro_a_kilometro.domain.model.Usuario

fun UsuarioDto.toDomain(): Usuario {
    return Usuario(
        id = id ?: 0,
        nombre = nombre ?: "",
        apellidos = apellidos ?: "",
        username = username ?: "",
        email = email ?: "",
        telefono = telefono ?: "",
        ciudad = ciudad ?: "",
        fechaNacimiento = fecha_nacimiento ?: ""
    )
}
