package com.ejemplo.kilometro_a_kilometro.data.repository

import com.ejemplo.kilometro_a_kilometro.data.remote.api.ApiClient
import com.ejemplo.kilometro_a_kilometro.data.remote.dto.LoginRequestDto
import com.ejemplo.kilometro_a_kilometro.data.remote.dto.RegisterRequestDto
import com.ejemplo.kilometro_a_kilometro.data.remote.dto.toDomain
import com.ejemplo.kilometro_a_kilometro.domain.model.Usuario

class UsuarioRepository {

    private val api = ApiClient.apiService

    suspend fun login(username: String, password: String): Usuario? {
        val response = api.login(
            LoginRequestDto(username, password)
        )

        if (!response.isSuccessful) {
            return null
        }

        val dto = response.body() ?: return null

        // 🔒 Protección extra
        if (dto.nombre.isNullOrBlank()) {
            return null
        }

        return dto.toDomain()
    }

    suspend fun register(
        nombre: String,
        apellidos: String,
        username: String,
        email: String,
        telefono: String,
        ciudad: String,
        fechaNacimiento: String,
        password: String
    ): Usuario? {

        val response = api.register(
            RegisterRequestDto(
                nombre,
                apellidos,
                username,
                email,
                telefono,
                ciudad,
                fechaNacimiento,
                password
            )
        )

        if (response.isSuccessful) {
            val dto = response.body() ?: return null
            return dto.toDomain()
        }

        return null
    }
}
