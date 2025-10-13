package com.example.mitiendita.database

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DBHelper(context: Context) : SQLiteOpenHelper(context, "tiendita.db", null, 1) {

    override fun onCreate(db: SQLiteDatabase) {
        // Crear tabla Usuarios
        db.execSQL(
            """
            CREATE TABLE usuarios (
                idUsua INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                dni TEXT,
                nombres TEXT,
                apellidoP TEXT,
                apellidoM TEXT,
                sexo TEXT,
                telefono TEXT,
                correo TEXT UNIQUE,
                clave TEXT,
                terminos INTEGER
            )
            """.trimIndent() // Elimina espacios y saltos innecesarios
        )

        db.execSQL(
            """
            CREATE TABLE categorias (
                idCat INTEGER PRIMARY KEY AUTOINCREMENT,
                nombre TEXT NOT NULL
            )
            """.trimIndent() // Elimina espacios y saltos innecesarios
        )
        // Crear tabla Productos
        db.execSQL(
            """
            CREATE TABLE productos (
                idProd INTEGER PRIMARY KEY AUTOINCREMENT,
                nombre TEXT NOT NULL,
                descripcion TEXT,
                precio REAL NOT NULL,
                stock INTEGER NOT NULL,
                categoria TEX,
                imagen TEXT
            )
            """.trimIndent() // Elimina espacios y saltos innecesarios
        )
        db.execSQL(
            """
            CREATE TABLE compras (
                idCompra INTEGER PRIMARY KEY AUTOINCREMENT,
                producto TEXT NOT NULL,
                cantidad INTEGER NOT NULL,
                fecha TEXT NOT NULL,
                idUsua INTEGER NOT NULL,
                idProd INTEGER NOT NULL,
                FOREIGN KEY (idProd) REFERENCES productos(idProd),
                FOREIGN KEY (idUsua) REFERENCES usuario(idUsua)
            )
            """.trimIndent() // Elimina espacios y saltos innecesarios
        )
        db.execSQL(
            """
            CREATE TABLE detalleCompra (
                idDetalleComp INTEGER PRIMARY KEY AUTOINCREMENT,
                producto TEXT NOT NULL,
                unidadiMedida TEXT,
                cantidad INTEGER NOT NULL,
                precioUnitario REAL NOT NULL,
                precioPagado REAL NOT NULL,
                fecha TEXT NOT NULL,
                imagen TEXT,
                FOREIGN KEY (idProd) REFERENCES productos(idProd),
                FOREIGN KEY (idCompra) REFERENCES compras(idCompra)
        )
        """.trimIndent() // Elimina espacios y saltos innecesarios
        )

    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS compras")
        db.execSQL("DROP TABLE IF EXISTS detalleCompra")
        db.execSQL("DROP TABLE IF EXISTS usuarios")
        db.execSQL("DROP TABLE IF EXISTS productos")
        db.execSQL("DROP TABLE IF EXISTS categorias")
        onCreate(db)
    }

    // ============================
    // USUARIOS
    // ============================

    fun registrarUsuario(
        dni: String, nombres: String, apellidoP: String, apellidoM: String, sexo: String,
        telefono: String, correo: String, clave: String
    ): Boolean {
        val db = this.writableDatabase
        val values = ContentValues().apply {
            put("dni", dni)
            put("nombres", nombres)
            put("apellidoP", apellidoP)
            put("apellidoM", apellidoM)
            put("telefono", telefono)
            put("sexo", sexo)
            put("correo", correo)
            put("clave", clave)
            put("terminos", 1)
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
