package com.example.mitiendita.database

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.example.mitiendita.entity.Usuario // Asumiendo que esta clase existe

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
                nombre TEXT NOT NULL
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
                idCat INTEGER, -- CORRECCIÓN: Usar idCat para la Categoría
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
                total REAL NOT NULL, -- Agregado campo Total
                fecha TEXT NOT NULL,
                idUsua INTEGER NOT NULL,
                FOREIGN KEY (idUsua) REFERENCES usuarios(idUsua) -- CORRECCIÓN: Referencia a la tabla 'usuarios'
            )
            """.trimIndent()
        )

        // Crear tabla detalleCompra (Detalle de cada Producto en la Venta)
        db.execSQL(
            """
            CREATE TABLE detalleCompra (
                idDetalleComp INTEGER PRIMARY KEY AUTOINCREMENT,
                idCompra INTEGER NOT NULL, -- CORRECCIÓN: Necesita la FK a la Compra
                idProd INTEGER NOT NULL,  -- CORRECCIÓN: Necesita la FK al Producto
                producto TEXT NOT NULL,
                unidadMedida TEXT, -- CORRECCIÓN: Nombre de columna corregido
                cantidad INTEGER NOT NULL,
                precioUnitario REAL NOT NULL,
                precioPagado REAL NOT NULL,
                imagen TEXT,
                FOREIGN KEY (idProd) REFERENCES productos(idProd),
                FOREIGN KEY (idCompra) REFERENCES compras(idCompra)
            )
            """.trimIndent()
        )
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
            put("terminos", 1) // Se asume que 1 es 'aceptado'
        }
        val result = db.insert("usuarios", null, values)
        db.close()
        return result != -1L
    }

    // CORRECCIÓN LÓGICA: Validar y retornar un objeto Usuario
    fun validarUsuario(correo: String, password: String): Usuario? {
        val db = this.readableDatabase
        val cursor: Cursor = db.rawQuery(
            "SELECT * FROM usuarios WHERE correo=? AND clave=?", // CORRECCIÓN: Usar 'clave' en lugar de 'password'
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
                // NOTA: Asegúrate de que tu clase Usuario tenga un constructor que acepte todos estos campos.
            )
        }
        cursor.close()
        db.close()
        return usuario
    }

    // ============================
    // PRODUCTOS (Corregida la referencia del WHERE en UPDATE/DELETE)
    // ============================

    fun insertarProducto(nombre: String, descripcion: String, precio: Double, stock: Int): Long {
        val db = writableDatabase
        val values = ContentValues().apply {
            put("nombre", nombre)
            put("descripcion", descripcion)
            put("precio", precio)
            put("stock", stock)
            // Falta: put("idCat", ...) y put("imagen", ...)
        }
        val result = db.insert("productos", null, values)
        db.close()
        return result
    }

    fun obtenerProductos(): Cursor {
        val db = readableDatabase
        // Se puede usar un alias en las columnas si es necesario para evitar ambigüedad en futuros JOINs
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
        // CORRECCIÓN: La columna clave es 'idProd', no 'id'
        val result = db.update("productos", values, "idProd = ?", arrayOf(id.toString()))
        db.close()
        return result
    }

    fun eliminarProducto(id: Int): Int {
        val db = writableDatabase
        // CORRECCIÓN: La columna clave es 'idProd', no 'id'
        val result = db.delete("productos", "idProd = ?", arrayOf(id.toString()))
        db.close()
        return result
    }




    // NO es necesario, ya lo define el constructor primario:
    /*
    companion object {
        private const val DATABASE_NAME = "tienda.db"
        private const val DATABASE_VERSION = 1
    }
    */
}