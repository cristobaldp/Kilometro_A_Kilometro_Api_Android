package com.ejemplo.kilometro_a_kilometro.data.repository

import com.ejemplo.kilometro_a_kilometro.data.remote.api.ApiClient
import com.ejemplo.kilometro_a_kilometro.data.remote.dto.toDomain
import com.ejemplo.kilometro_a_kilometro.domain.model.Repostaje
import com.ejemplo.kilometro_a_kilometro.data.remote.dto.RepostajeCreateDto
class RepostajeRepository {

    private val api = ApiClient.apiService

    suspend fun getRepostajes(vehiculoId: Int): List<Repostaje> {
        return try {
            val response = api.getRepostajes(vehiculoId)
            if (response.isSuccessful) {
                response.body()?.map { it.toDomain() } ?: emptyList()
            } else {
                emptyList()
            }
        } catch (e: Exception) {
            emptyList()
        }
    }
    suspend fun crearRepostaje(
        vehiculoId: Int,
        fecha: String,
        litros: Double,
        precioTotal: Double,
        kilometros: Int
    ): Boolean {
        return try {
            val response = api.crearRepostaje(
                RepostajeCreateDto(
                    vehiculo_id = vehiculoId,
                    fecha = fecha,
                    litros = litros,
                    precio_total = precioTotal,
                    kilometros = kilometros
                )
            )
            response.isSuccessful
        } catch (e: Exception) {
            false
        }
    }

    suspend fun borrarRepostaje(repostajeId: Int): Boolean {
        return try {
            val response = api.borrarRepostaje(repostajeId)
            response.isSuccessful
        } catch (e: Exception) {
            false
        }
    }
}
