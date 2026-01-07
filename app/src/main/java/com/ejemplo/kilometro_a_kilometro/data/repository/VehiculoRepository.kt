package com.ejemplo.kilometro_a_kilometro.data.repository

import android.content.ContentValues
import android.content.Context
import com.ejemplo.kilometro_a_kilometro.data.local.database.DatabaseHelper
import com.ejemplo.kilometro_a_kilometro.domain.model.Vehiculo

class VehiculoRepository(context: Context) {

    private val dbHelper = DatabaseHelper(context)

    /**
     * Obtiene todos los vehículos de un usuario concreto
     */
    fun obtenerVehiculosPorUsuario(userId: Int): List<Vehiculo> {
        val lista = mutableListOf<Vehiculo>()
        val db = dbHelper.readableDatabase

        val cursor = db.rawQuery(
            "SELECT * FROM vehiculos WHERE user_id = ?",
            arrayOf(userId.toString())
        )

        while (cursor.moveToNext()) {
            val vehiculo = Vehiculo(
                id = cursor.getInt(cursor.getColumnIndexOrThrow("id")),
                userId = cursor.getInt(cursor.getColumnIndexOrThrow("user_id")),
                tipo = cursor.getString(cursor.getColumnIndexOrThrow("tipo")),
                marca = cursor.getString(cursor.getColumnIndexOrThrow("marca")),
                modelo = cursor.getString(cursor.getColumnIndexOrThrow("modelo")),
                matricula = cursor.getString(cursor.getColumnIndexOrThrow("matricula")),
                anio = cursor.getInt(cursor.getColumnIndexOrThrow("anio")),
                combustible = cursor.getString(cursor.getColumnIndexOrThrow("combustible")),
                consumo = cursor.getDouble(cursor.getColumnIndexOrThrow("consumo"))
            )

            lista.add(vehiculo)
        }

        cursor.close()
        db.close()

        return lista
    }

    /**
     * Inserta un nuevo vehículo
     */
    fun insertarVehiculo(vehiculo: Vehiculo): Boolean {
        val db = dbHelper.writableDatabase

        val values = ContentValues().apply {
            put("user_id", vehiculo.userId)
            put("tipo", vehiculo.tipo)
            put("marca", vehiculo.marca)
            put("modelo", vehiculo.modelo)
            put("matricula", vehiculo.matricula)
            put("anio", vehiculo.anio)
            put("combustible", vehiculo.combustible)
            put("consumo", vehiculo.consumo)
        }

        val resultado = db.insert("vehiculos", null, values)
        db.close()

        return resultado != -1L
    }

    /**
     * Comprueba si una matrícula ya existe en la BD
     */
    fun existeMatricula(matricula: String): Boolean {
        val db = dbHelper.readableDatabase

        val cursor = db.rawQuery(
            "SELECT id FROM vehiculos WHERE matricula = ?",
            arrayOf(matricula)
        )

        val existe = cursor.moveToFirst()

        cursor.close()
        db.close()

        return existe
    }
    fun borrarVehiculo(id: Int) {
        val db = dbHelper.writableDatabase
        db.delete("vehiculos", "id = ?", arrayOf(id.toString()))
        db.close()
    }

}