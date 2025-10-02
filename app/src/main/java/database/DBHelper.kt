package com.example.mitiendita.database

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DBHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    override fun onCreate(db: SQLiteDatabase) {
        // Crear tabla Usuarios
        db.execSQL(
            """
            CREATE TABLE usuarios (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                nombre TEXT,
                apellido TEXT,
                edad INTEGER,
                sexo TEXT,
                telefono TEXT,
                direccion TEXT,
                correo TEXT UNIQUE,
                password TEXT
            )
            """
        )

        // Crear tabla Productos
        db.execSQL(
            """
            CREATE TABLE productos (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                nombre TEXT NOT NULL,
                descripcion TEXT,
                precio REAL NOT NULL,
                stock INTEGER NOT NULL
            )
            """
        )
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS usuarios")
        db.execSQL("DROP TABLE IF EXISTS productos")
        onCreate(db)
    }

    // ============================
    // USUARIOS
    // ============================

    fun registrarUsuario(
        nombre: String, apellido: String, edad: Int, sexo: String,
        telefono: String, direccion: String, correo: String, password: String
    ): Boolean {
        val db = this.writableDatabase
        val values = ContentValues().apply {
            put("nombre", nombre)
            put("apellido", apellido)
            put("edad", edad)
            put("sexo", sexo)
            put("telefono", telefono)
            put("direccion", direccion)
            put("correo", correo)
            put("password", password)
        }
        val result = db.insert("usuarios", null, values)
        db.close()
        return result != -1L
    }

    fun validarUsuario(correo: String, password: String): Boolean {
        val db = this.readableDatabase
        val cursor: Cursor = db.rawQuery(
            "SELECT * FROM usuarios WHERE correo=? AND password=?",
            arrayOf(correo, password)
        )
        val existe = cursor.moveToFirst()
        cursor.close()
        db.close()
        return existe
    }

    // ============================
    // PRODUCTOS
    // ============================

    fun insertarProducto(nombre: String, descripcion: String, precio: Double, stock: Int): Long {
        val db = writableDatabase
        val values = ContentValues().apply {
            put("nombre", nombre)
            put("descripcion", descripcion)
            put("precio", precio)
            put("stock", stock)
        }
        val result = db.insert("productos", null, values)
        db.close()
        return result
    }

    fun obtenerProductos(): Cursor {
        val db = readableDatabase
        return db.rawQuery("SELECT * FROM productos", null)
    }

    fun actualizarProducto(id: Int, nombre: String, descripcion: String, precio: Double, stock: Int): Int {
        val db = writableDatabase
        val values = ContentValues().apply {
            put("nombre", nombre)
            put("descripcion", descripcion)
            put("precio", precio)
            put("stock", stock)
        }
        val result = db.update("productos", values, "id = ?", arrayOf(id.toString()))
        db.close()
        return result
    }

    fun eliminarProducto(id: Int): Int {
        val db = writableDatabase
        val result = db.delete("productos", "id = ?", arrayOf(id.toString()))
        db.close()
        return result
    }

    companion object {
        private const val DATABASE_NAME = "tienda.db"
        private const val DATABASE_VERSION = 1
    }
}
