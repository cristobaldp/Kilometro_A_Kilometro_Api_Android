package com.ejemplo.kilometro_a_kilometro.domain.model

data class Usuario(
    val id: Int,
    val nombre: String,
    val apellidos: String,
    val username: String,
    val email: String,
    val telefono: String,
    val ciudad: String,
    val fechaNacimiento: String,
    val vehiculoActivoId: Int? = null   // ⭐ IMPORTANTE
)