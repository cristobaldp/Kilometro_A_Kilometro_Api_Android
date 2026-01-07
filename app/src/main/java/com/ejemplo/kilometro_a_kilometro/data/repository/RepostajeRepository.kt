package com.ejemplo.kilometro_a_kilometro.data.repository

import android.content.ContentValues
import android.content.Context
import com.ejemplo.kilometro_a_kilometro.data.local.database.DatabaseHelper
import com.ejemplo.kilometro_a_kilometro.domain.model.Repostaje

class RepostajeRepository(context: Context) {

    private val dbHelper = DatabaseHelper(context)

    // ➕ Guardar repostaje
    fun insertarRepostaje(repostaje: Repostaje) {
        val db = dbHelper.writableDatabase

        val values = ContentValues().apply {
            put("vehiculo_id", repostaje.vehiculoId)
            put("fecha", repostaje.fecha)
            put("litros", repostaje.litros)
            put("precio_total", repostaje.precioTotal)
            put("kilometros", repostaje.kilometros)
        }

        db.insert("repostajes", null, values)
        db.close()
    }

    // 📄 Obtener repostajes de un vehículo
    fun obtenerRepostajesPorVehiculo(vehiculoId: Int): List<Repostaje> {
        val db = dbHelper.readableDatabase
        val lista = mutableListOf<Repostaje>()

        val cursor = db.rawQuery(
            """
            SELECT * FROM repostajes
            WHERE vehiculo_id = ?
            ORDER BY kilometros ASC
            """,
            arrayOf(vehiculoId.toString())
        )

        if (cursor.moveToFirst()) {
            do {
                lista.add(
                    Repostaje(
                        id = cursor.getInt(cursor.getColumnIndexOrThrow("id")),
                        vehiculoId = cursor.getInt(cursor.getColumnIndexOrThrow("vehiculo_id")),
                        fecha = cursor.getString(cursor.getColumnIndexOrThrow("fecha")),
                        litros = cursor.getDouble(cursor.getColumnIndexOrThrow("litros")),
                        precioTotal = cursor.getDouble(cursor.getColumnIndexOrThrow("precio_total")),
                        kilometros = cursor.getInt(cursor.getColumnIndexOrThrow("kilometros"))
                    )
                )
            } while (cursor.moveToNext())
        }

        cursor.close()
        db.close()
        return lista
    }
}