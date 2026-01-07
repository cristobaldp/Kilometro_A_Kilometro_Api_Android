package com.ejemplo.kilometro_a_kilometro.data.local.dao

import android.content.ContentValues
import android.content.Context
import com.ejemplo.kilometro_a_kilometro.data.local.database.DatabaseContract
import com.ejemplo.kilometro_a_kilometro.data.local.database.DatabaseHelper
import com.ejemplo.kilometro_a_kilometro.domain.model.Usuario

class UsuarioDao(context: Context) {

    private val dbHelper = DatabaseHelper(context)

    // INSERTAR USUARIO
    fun insertUsuario(usuario: Usuario): Boolean {
        val db = dbHelper.writableDatabase

        val values = ContentValues().apply {
            put(DatabaseContract.Usuarios.NOMBRE, usuario.nombre)
            put(DatabaseContract.Usuarios.APELLIDOS, usuario.apellidos)
            put(DatabaseContract.Usuarios.USERNAME, usuario.username)
            put(DatabaseContract.Usuarios.EMAIL, usuario.email)
            put(DatabaseContract.Usuarios.TELEFONO, usuario.telefono)
            put(DatabaseContract.Usuarios.CIUDAD, usuario.ciudad)
            put(DatabaseContract.Usuarios.FECHA_NACIMIENTO, usuario.fechaNacimiento)

        }

        val result = db.insert(
            DatabaseContract.Usuarios.TABLE_NAME,
            null,
            values
        )

        db.close()
        return result != -1L
    }

    // COMPROBAR SI EXISTE USERNAME O EMAIL
    fun existeUsuario(username: String, email: String): Boolean {
        val db = dbHelper.readableDatabase

        val query = """
            SELECT ${DatabaseContract.Usuarios.ID}
            FROM ${DatabaseContract.Usuarios.TABLE_NAME}
            WHERE ${DatabaseContract.Usuarios.USERNAME} = ?
               OR ${DatabaseContract.Usuarios.EMAIL} = ?
        """

        val cursor = db.rawQuery(query, arrayOf(username, email))
        val exists = cursor.count > 0

        cursor.close()
        db.close()

        return exists
    }

    // LOGIN (USERNAME O EMAIL + PASSWORD)
    fun login(identificador: String, password: String): Usuario? {
        val db = dbHelper.readableDatabase

        val query = """
            SELECT *
            FROM ${DatabaseContract.Usuarios.TABLE_NAME}
            WHERE (${DatabaseContract.Usuarios.USERNAME} = ?
               OR ${DatabaseContract.Usuarios.EMAIL} = ?)
              AND ${DatabaseContract.Usuarios.PASSWORD} = ?
        """

        val cursor = db.rawQuery(
            query,
            arrayOf(identificador, identificador, password)
        )

        var usuario: Usuario? = null

        if (cursor.moveToFirst()) {
            usuario = Usuario(
                id = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseContract.Usuarios.ID)),
                nombre = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseContract.Usuarios.NOMBRE)),
                apellidos = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseContract.Usuarios.APELLIDOS)),
                username = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseContract.Usuarios.USERNAME)),
                email = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseContract.Usuarios.EMAIL)),
                telefono = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseContract.Usuarios.TELEFONO)),
                ciudad = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseContract.Usuarios.CIUDAD)),
                fechaNacimiento = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseContract.Usuarios.FECHA_NACIMIENTO)),

            )
        }

        cursor.close()
        db.close()

        return usuario
    }
}