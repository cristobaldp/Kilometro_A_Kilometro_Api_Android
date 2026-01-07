package com.ejemplo.kilometro_a_kilometro.data.local.database

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DatabaseHelper(context: Context) :
    SQLiteOpenHelper(context, "kilometro_kilometro.db", null, 3) {

    override fun onCreate(db: SQLiteDatabase) {

        // 🧑 Usuarios
        db.execSQL("""
            CREATE TABLE usuarios (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                nombre TEXT,
                apellidos TEXT,
                username TEXT UNIQUE,
                email TEXT UNIQUE,
                telefono TEXT,
                ciudad TEXT,
                fecha_nacimiento TEXT,
                password TEXT,
                vehiculo_activo_id INTEGER
            )
        """)

        // 🚗 Vehículos
        db.execSQL("""
            CREATE TABLE vehiculos (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                user_id INTEGER,
                tipo TEXT,
                marca TEXT,
                modelo TEXT,
                matricula TEXT UNIQUE,
                anio INTEGER,
                combustible TEXT,
                consumo REAL,
                FOREIGN KEY(user_id) REFERENCES usuarios(id)
            )
        """)

        // ⛽ Repostajes
        db.execSQL("""
            CREATE TABLE repostajes (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                vehiculo_id INTEGER,
                fecha TEXT,
                litros REAL,
                precio_total REAL,
                kilometros INTEGER,
                FOREIGN KEY(vehiculo_id) REFERENCES vehiculos(id)
            )
        """)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {

        if (oldVersion < 2) {
            db.execSQL("ALTER TABLE usuarios ADD COLUMN vehiculo_activo_id INTEGER")
        }

        if (oldVersion < 3) {
            db.execSQL("""
                CREATE TABLE repostajes (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    vehiculo_id INTEGER,
                    fecha TEXT,
                    litros REAL,
                    precio_total REAL,
                    kilometros INTEGER,
                    FOREIGN KEY(vehiculo_id) REFERENCES vehiculos(id)
                )
            """)
        }
    }
}