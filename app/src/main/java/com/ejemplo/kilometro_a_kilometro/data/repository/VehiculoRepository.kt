package com.ejemplo.kilometro_a_kilometro.data.repository

import com.ejemplo.kilometro_a_kilometro.data.remote.api.ApiClient
import com.ejemplo.kilometro_a_kilometro.data.remote.dto.VehiculoCreateDto
import com.ejemplo.kilometro_a_kilometro.data.remote.dto.VehiculoDto
import com.ejemplo.kilometro_a_kilometro.data.remote.dto.toDomain
import com.ejemplo.kilometro_a_kilometro.domain.model.Vehiculo
import com.ejemplo.kilometro_a_kilometro.domain.model.Usuario
class VehiculoRepository {

    private val api = ApiClient.apiService

    /**
     * Obtener vehículos del usuario
     */
    suspend fun getVehiculos(userId: Int): List<Vehiculo> {
        return try {
            val response = api.getVehiculos(userId)

            if (response.isSuccessful) {
                val dtoList: List<VehiculoDto> = response.body() ?: emptyList()
                dtoList.map { it.toDomain() }
            } else {
                emptyList()
            }

        } catch (e: Exception) {
            emptyList()
        }
    }

    /**
     * Crear vehículo
     */
    suspend fun crearVehiculo(dto: VehiculoCreateDto): Boolean {
        return try {
            val response = api.crearVehiculo(dto)
            response.isSuccessful
        } catch (e: Exception) {
            false
        }
    }

    suspend fun borrarVehiculo(vehiculoId: Int): Boolean {
        return try {
            val response = api.borrarVehiculo(vehiculoId)
            response.isSuccessful
        } catch (e: Exception) {
            false
        }
    }

    suspend fun establecerVehiculoActivo(
        userId: Int,
        vehiculoId: Int
    ): Boolean {
        return try {
            val response = api.establecerVehiculoActivo(userId, vehiculoId)
            response.isSuccessful
        } catch (e: Exception) {
            false
        }
    }

    suspend fun getUsuario(userId: Int): Usuario? {
        return try {
            val response = api.getUsuario(userId)
            if (response.isSuccessful) {
                response.body()?.toDomain()
            } else {
                null
            }
        } catch (e: Exception) {
            null
        }
    }
}
