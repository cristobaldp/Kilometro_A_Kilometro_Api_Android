package com.ejemplo.kilometro_a_kilometro.data.repository

import com.ejemplo.kilometro_a_kilometro.data.remote.api.ApiClient
import com.ejemplo.kilometro_a_kilometro.data.remote.dto.LoginRequestDto
import com.ejemplo.kilometro_a_kilometro.data.remote.dto.RegisterRequestDto
import com.ejemplo.kilometro_a_kilometro.data.remote.dto.toDomain
import com.ejemplo.kilometro_a_kilometro.domain.model.Usuario

class UsuarioRepository {

    private val api = ApiClient.apiService

    // =========================
    // 🔐 LOGIN
    // =========================
    suspend fun login(username: String, password: String): Usuario? {
        return try {
            val response = api.login(
                LoginRequestDto(username, password)
            )

            if (!response.isSuccessful) return null

            val dto = response.body() ?: return null

            // 🔒 Protección extra
            if (dto.nombre.isNullOrBlank()) return null

            dto.toDomain()

        } catch (e: Exception) {
            null
        }
    }

    // =========================
    // 📝 REGISTER
    // =========================
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
        return try {
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

            if (!response.isSuccessful) return null

            response.body()?.toDomain()

        } catch (e: Exception) {
            null
        }
    }

    // =========================
    // 👤 GET USUARIO (CLAVE)
    // =========================
    suspend fun getUsuario(userId: Int): Usuario? {
        return try {
            val response = api.getUsuario(userId)

            if (!response.isSuccessful) return null

            response.body()?.toDomain()

        } catch (e: Exception) {
            null
        }
    }
}
