package com.example.mitiendita.database

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.example.mitiendita.entity.Usuario

class DBHelper(context: Context) : SQLiteOpenHelper(context, "tiendita.db", null, 1) {

    override fun onCreate(db: SQLiteDatabase) {
        // Crear tabla usuarios
        db.execSQL(
            """
            CREATE TABLE usuarios (
                idUsua INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                dni TEXT NOT NULL,
                nombres TEXT,
                apellidoP TEXT,
                apellidoM TEXT,
                telefono TEXT,
                sexo TEXT,
                correo TEXT UNIQUE,
                clave TEXT,
                terminos INTEGER
            )
            """.trimIndent()
        )

        // Crear tabla categorias
        db.execSQL(
            """
            CREATE TABLE categorias (
                idCat INTEGER PRIMARY KEY AUTOINCREMENT,
                nombre TEXT NOT NULL UNIQUE
            )
            """.trimIndent()
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
                idCat INTEGER,
                imagen TEXT,
                FOREIGN KEY (idCat) REFERENCES categorias(idCat)
            )
            """.trimIndent()
        )

        // Crear tabla compras (Cabecera de la Venta)
        db.execSQL(
            """
            CREATE TABLE compras (
                idCompra INTEGER PRIMARY KEY AUTOINCREMENT,
                total REAL NOT NULL,
                fecha TEXT NOT NULL,
                idUsua INTEGER NOT NULL,
                FOREIGN KEY (idUsua) REFERENCES usuarios(idUsua)
            )
            """.trimIndent()
        )

        // Crear tabla detalleCompra (Detalle de cada Producto en la Venta)
        db.execSQL(
            """
            CREATE TABLE detalleCompra (
                idDetalleComp INTEGER PRIMARY KEY AUTOINCREMENT,
                idCompra INTEGER NOT NULL,
                idProd INTEGER NOT NULL,
                producto TEXT NOT NULL,
                unidadMedida TEXT,
                cantidad INTEGER NOT NULL,
                precioUnitario REAL NOT NULL,
                precioPagado REAL NOT NULL,
                imagen TEXT,
                FOREIGN KEY (idProd) REFERENCES productos(idProd),
                FOREIGN KEY (idCompra) REFERENCES compras(idCompra)
            )
            """.trimIndent()
        )

        // Insertar categorías iniciales
        insertarCategoriasIniciales(db)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS detalleCompra")
        db.execSQL("DROP TABLE IF EXISTS compras")
        db.execSQL("DROP TABLE IF EXISTS productos")
        db.execSQL("DROP TABLE IF EXISTS categorias")
        db.execSQL("DROP TABLE IF EXISTS usuarios")
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

    fun validarUsuario(correo: String, password: String): Usuario? {
        val db = this.readableDatabase
        val cursor: Cursor = db.rawQuery(
            "SELECT * FROM usuarios WHERE correo=? AND clave=?",
            arrayOf(correo, password)
        )
        var usuario: Usuario? = null

        if (cursor.moveToFirst()) {
            usuario = Usuario(
                idUsua = cursor.getInt(cursor.getColumnIndexOrThrow("idUsua")),
                dni = cursor.getString(cursor.getColumnIndexOrThrow("dni")),
                nombres = cursor.getString(cursor.getColumnIndexOrThrow("nombres")),
                apellidoP = cursor.getString(cursor.getColumnIndexOrThrow("apellidoP")),
                apellidoM = cursor.getString(cursor.getColumnIndexOrThrow("apellidoM")),
                telefono = cursor.getString(cursor.getColumnIndexOrThrow("telefono")),
                sexo = cursor.getString(cursor.getColumnIndexOrThrow("sexo")),
                correo = cursor.getString(cursor.getColumnIndexOrThrow("correo")),
                clave = cursor.getString(cursor.getColumnIndexOrThrow("clave")),
                terminos = cursor.getInt(cursor.getColumnIndexOrThrow("terminos")) == 1
            )
        }
        cursor.close()
        db.close()
        return usuario
    }



    //Inserta categorías de prueba si la tabla está vacía.

    private fun insertarCategoriasIniciales(db: SQLiteDatabase) {
        val categorias = listOf("Electrónica", "Ropa", "Alimentos", "Hogar", "Juguetes")

        for (nombre in categorias) {
            val values = ContentValues().apply {
                put("nombre", nombre)
            }
            // Insertar si no existe
            val cursor: Cursor = db.rawQuery("SELECT COUNT(*) FROM categorias WHERE nombre=?", arrayOf(nombre))
            cursor.moveToFirst()
            val count = cursor.getInt(0)
            cursor.close()

            if (count == 0) {
                db.insert("categorias", null, values)
            }
        }
    }


    // PRODUCTOS

//    fun insertarProducto(
//        nombre: String,
//        descripcion: String,
//        precio: Double,
//        stock: Int,
//        idCat: Int,
//        imagen: String? = null
//    ): Long {
//        val db = writableDatabase
//        val values = ContentValues().apply {
//            put("nombre", nombre)
//            put("descripcion", descripcion)
//            put("precio", precio)
//            put("stock", stock)
//            put("idCat", idCat)
//            put("imagen", imagen)
//        }
//        val result = db.insert("productos", null, values)
//        db.close()
//        return result
//    }

    fun obtenerProductos(): Cursor {
        val db = readableDatabase
        return db.rawQuery("SELECT * FROM productos", null)
    }

    fun obtenerProductosConCategoria(): Cursor {
        val db = readableDatabase
        return db.rawQuery(
            """
            SELECT p.*, c.nombre as nombreCategoria 
            FROM productos p 
            LEFT JOIN categorias c ON p.idCat = c.idCat 
            ORDER BY p.nombre
            """.trimIndent(), null
        )
    }

    fun actualizarProducto(
        id: Int,
        nombre: String,
        descripcion: String,
        precio: Double,
        stock: Int,
        idCat: Int,
        imagen: String? = null
    ): Int {
        val db = writableDatabase
        val values = ContentValues().apply {
            put("nombre", nombre)
            put("descripcion", descripcion)
            put("precio", precio)
            put("stock", stock)
            put("idCat", idCat)
            if (imagen != null) {
                put("imagen", imagen)
            }
        }
        val result = db.update("productos", values, "idProd = ?", arrayOf(id.toString()))
        db.close()
        return result
    }

    fun eliminarProducto(id: Int): Int {
        val db = writableDatabase
        val result = db.delete("productos", "idProd = ?", arrayOf(id.toString()))
        db.close()
        return result
    }

    // ============================
    // COMPRAS Y DETALLE COMPRA
    // ============================

    fun registrarCompra(total: Double, fecha: String, idUsua: Int): Long {
        val db = writableDatabase
        val values = ContentValues().apply {
            put("total", total)
            put("fecha", fecha)
            put("idUsua", idUsua)
        }
        val result = db.insert("compras", null, values)
        db.close()
        return result
    }

    fun registrarDetalleCompra(
        idCompra: Int,
        idProd: Int,
        producto: String,
        unidadMedida: String,
        cantidad: Int,
        precioUnitario: Double,
        precioPagado: Double,
        imagen: String? = null
    ): Long {
        val db = writableDatabase
        val values = ContentValues().apply {
            put("idCompra", idCompra)
            put("idProd", idProd)
            put("producto", producto)
            put("unidadMedida", unidadMedida)
            put("cantidad", cantidad)
            put("precioUnitario", precioUnitario)
            put("precioPagado", precioPagado)
            put("imagen", imagen)
        }
        val result = db.insert("detalleCompra", null, values)
        db.close()
        return result
    }


    //Obtiene el conteo de registros en una tabla

    fun obtenerConteoTabla(nombreTabla: String): Int {
        val db = readableDatabase
        val cursor = db.rawQuery("SELECT COUNT(*) FROM $nombreTabla", null)
        cursor.moveToFirst()
        val count = cursor.getInt(0)
        cursor.close()
        db.close()
        return count
    }


    //Verifica si la base de datos está vacía

    fun estaBaseDeDatosVacia(): Boolean {
        val tablas = listOf("usuarios", "categorias", "productos")
        return tablas.all { obtenerConteoTabla(it) == 0 }
    }
}