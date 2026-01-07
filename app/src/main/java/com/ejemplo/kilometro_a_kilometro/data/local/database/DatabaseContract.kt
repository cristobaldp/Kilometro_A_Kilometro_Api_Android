package com.ejemplo.kilometro_a_kilometro.data.local.database

object DatabaseContract {

    object Usuarios {
        const val TABLE_NAME = "usuarios"
        const val ID = "id"
        const val NOMBRE = "nombre"
        const val APELLIDOS = "apellidos"
        const val USERNAME = "username"
        const val EMAIL = "email"
        const val TELEFONO = "telefono"
        const val CIUDAD = "ciudad"
        const val FECHA_NACIMIENTO = "fecha_nacimiento"
        const val PASSWORD = "password"
    }

    object Vehiculos {
        const val TABLE_NAME = "vehiculos"
        const val ID = "id"
        const val USER_ID = "user_id"
        const val TIPO = "tipo"
        const val MARCA = "marca"
        const val MODELO = "modelo"
        const val MATRICULA = "matricula"
        const val ANIO = "anio"
        const val COMBUSTIBLE = "combustible"
        const val CONSUMO = "consumo"
    }
}