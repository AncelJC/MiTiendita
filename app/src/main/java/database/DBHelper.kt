package com.example.mitiendita.database

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.example.mitiendita.entity.Usuario

class DBHelper(context: Context) : SQLiteOpenHelper(context, "tiendita.db", null, 1) { // ✅ Cambiado a versión 3

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

        // Crear tabla Productos CON COLUMNA ACTIVO Y UNIDADMEDIDA
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
                unidadMedida TEXT DEFAULT 'unidad',
                activo INTEGER DEFAULT 1,
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

        // Insertar datos iniciales
        insertarDatosIniciales(db)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        when (oldVersion) {
            1 -> {
                // Agregar columna activo si la versión anterior era 1
                db.execSQL("ALTER TABLE productos ADD COLUMN activo INTEGER DEFAULT 1")
                // Agregar columna unidadMedida
                db.execSQL("ALTER TABLE productos ADD COLUMN unidadMedida TEXT DEFAULT 'unidad'")
            }
            2 -> {
                // Agregar columna unidadMedida si la versión anterior era 2
                db.execSQL("ALTER TABLE productos ADD COLUMN unidadMedida TEXT DEFAULT 'unidad'")
            }
        }
    }

    // ============================
    // DATOS INICIALES
    // ============================

    private fun insertarDatosIniciales(db: SQLiteDatabase) {
        insertarCategoriasIniciales(db)
        insertarProductosIniciales(db)
        insertarUsuarioPrueba(db)
    }

    private fun insertarCategoriasIniciales(db: SQLiteDatabase) {
        val categorias = listOf("Electrónica", "Ropa", "Alimentos", "Hogar", "Juguetes")

        for (nombre in categorias) {
            val values = ContentValues().apply {
                put("nombre", nombre)
            }

            val cursor: Cursor = db.rawQuery("SELECT COUNT(*) FROM categorias WHERE nombre=?", arrayOf(nombre))
            cursor.moveToFirst()
            val count = cursor.getInt(0)
            cursor.close()

            if (count == 0) {
                db.insert("categorias", null, values)
            }
        }
    }

    private fun insertarProductosIniciales(db: SQLiteDatabase) {
        val productos = listOf(
            arrayOf("Smartphone Samsung", "Teléfono inteligente de última generación", 899.99, 10, 1, null, "unidad"),
            arrayOf("Laptop HP", "Laptop para trabajo y estudio", 1299.99, 5, 1, null, "unidad"),
            arrayOf("Camiseta Básica", "Camiseta de algodón 100%", 19.99, 50, 2, null, "unidad"),
            arrayOf("Jeans Clásicos", "Jeans de corte recto", 49.99, 30, 2, null, "unidad"),
            arrayOf("Arroz Integral", "Arroz integral orgánico 1kg", 3.99, 100, 3, null, "kg"),
            arrayOf("Aceite de Oliva", "Aceite extra virgen 500ml", 8.99, 40, 3, null, "litro"),
            arrayOf("Juego de Sábanas", "Sábanas de algodón king size", 39.99, 20, 4, null, "juego"),
            arrayOf("Lego Classic", "Set de construcción para niños", 29.99, 25, 5, null, "caja")
        )

        for (producto in productos) {
            val values = ContentValues().apply {
                put("nombre", producto[0] as String)
                put("descripcion", producto[1] as String)
                put("precio", producto[2] as Double)
                put("stock", producto[3] as Int)
                put("idCat", producto[4] as Int)
                put("imagen", producto[5] as String?)
                put("unidadMedida", producto[6] as String)  // Nueva columna
                put("activo", 1)
            }

            val cursor: Cursor = db.rawQuery("SELECT COUNT(*) FROM productos WHERE nombre=?", arrayOf(producto[0] as String))
            cursor.moveToFirst()
            val count = cursor.getInt(0)
            cursor.close()

            if (count == 0) {
                db.insert("productos", null, values)
            }
        }
    }

    private fun insertarUsuarioPrueba(db: SQLiteDatabase) {
        val values = ContentValues().apply {
            put("dni", "12345678")
            put("nombres", "Usuario")
            put("apellidoP", "Prueba")
            put("apellidoM", "Test")
            put("telefono", "999888777")
            put("sexo", "Masculino")
            put("correo", "test@test.com")
            put("clave", "123456")
            put("terminos", 1)
        }

        val cursor: Cursor = db.rawQuery("SELECT COUNT(*) FROM usuarios WHERE correo=?", arrayOf("test@test.com"))
        cursor.moveToFirst()
        val count = cursor.getInt(0)
        cursor.close()

        if (count == 0) {
            db.insert("usuarios", null, values)
        }
    }

    // ============================
    // USUARIOS - MÉTODOS BÁSICOS
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

    // ============================
    // PRODUCTOS - MÉTODOS BÁSICOS
    // ============================

    fun insertarProducto(
        nombre: String,
        descripcion: String,
        precio: Double,
        stock: Int,
        idCat: Int,
        imagen: String? = null,
        unidadMedida: String = "unidad"  // Nuevo parámetro
    ): Long {
        val db = writableDatabase
        val values = ContentValues().apply {
            put("nombre", nombre)
            put("descripcion", descripcion)
            put("precio", precio)
            put("stock", stock)
            put("idCat", idCat)
            put("imagen", imagen)
            put("unidadMedida", unidadMedida)  // Nueva columna
            put("activo", 1)
        }
        val result = db.insert("productos", null, values)
        db.close()
        return result
    }

    fun obtenerProductos(): Cursor {
        val db = readableDatabase
        return db.rawQuery("SELECT * FROM productos WHERE activo = 1 ORDER BY nombre", null)
    }

    fun obtenerProductosConCategoria(): Cursor {
        val db = readableDatabase
        return db.rawQuery(
            """
            SELECT p.*, c.nombre as nombreCategoria 
            FROM productos p 
            LEFT JOIN categorias c ON p.idCat = c.idCat 
            WHERE p.activo = 1
            ORDER BY p.nombre
            """.trimIndent(), null
        )
    }

    fun obtenerTodosLosProductosAdmin(): Cursor {
        val db = readableDatabase
        return db.rawQuery(
            """
            SELECT p.*, c.nombre as nombreCategoria 
            FROM productos p 
            LEFT JOIN categorias c ON p.idCat = c.idCat 
            ORDER BY p.activo DESC, p.nombre
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
        imagen: String? = null,
        unidadMedida: String = "unidad"  // Nuevo parámetro
    ): Int {
        val db = writableDatabase
        val values = ContentValues().apply {
            put("nombre", nombre)
            put("descripcion", descripcion)
            put("precio", precio)
            put("stock", stock)
            put("idCat", idCat)
            put("unidadMedida", unidadMedida)  // Nueva columna
            if (imagen != null) {
                put("imagen", imagen)
            }
        }
        val result = db.update("productos", values, "idProd = ?", arrayOf(id.toString()))
        db.close()
        return result
    }

    fun cambiarEstadoProducto(idProd: Int, activo: Boolean): Boolean {
        val db = writableDatabase
        val values = ContentValues().apply {
            put("activo", if (activo) 1 else 0)
        }
        val result = db.update("productos", values, "idProd = ?", arrayOf(idProd.toString()))
        db.close()
        return result > 0
    }

    fun eliminarProducto(id: Int): Int {
        val db = writableDatabase
        val result = db.delete("productos", "idProd = ?", arrayOf(id.toString()))
        db.close()
        return result
    }

    // ============================
    // CATEGORÍAS - MÉTODOS BÁSICOS
    // ============================

    fun obtenerCategorias(): Cursor {
        val db = readableDatabase
        return db.rawQuery("SELECT * FROM categorias ORDER BY nombre", null)
    }

    // ============================
    // COMPRAS - MÉTODOS BÁSICOS (para uso de DAOs)
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

    // ============================
    // MÉTODOS UTILITARIOS
    // ============================

    fun obtenerConteoTabla(nombreTabla: String): Int {
        val db = readableDatabase
        val cursor = db.rawQuery("SELECT COUNT(*) FROM $nombreTabla", null)
        cursor.moveToFirst()
        val count = cursor.getInt(0)
        cursor.close()
        db.close()
        return count
    }

    fun estaBaseDeDatosVacia(): Boolean {
        val tablas = listOf("usuarios", "categorias", "productos")
        return tablas.all { obtenerConteoTabla(it) == 0 }
    }

    // ============================
    // MÉTODOS PARA OBTENER UNIDADES DE MEDIDA DISPONIBLES
    // ============================

    fun obtenerUnidadesDeMedida(): List<String> {
        return listOf("unidad", "kg", "litro", "gramo", "metro", "caja", "juego", "paquete", "botella", "lata")
    }

    // ============================
    // MÉTODOS PARA ACTUALIZAR STOCK
    // ============================

    fun actualizarStockProducto(idProd: Int, nuevoStock: Int): Boolean {
        val db = writableDatabase
        val values = ContentValues().apply {
            put("stock", nuevoStock)
        }
        val result = db.update("productos", values, "idProd = ?", arrayOf(idProd.toString()))
        db.close()
        return result > 0
    }

//    fun obtenerStockProducto(idProd: Int): Int {
//        val db = readableDatabase
//        val cursor = db.rawQuery("SELECT stock FROM productos WHERE idProd = ?", arrayOf(idProd.toString()))
//        var stock = 0
//        if (cursor.moveToFirst()) {
//            stock = cursor.getInt(cursor.getColumnIndexOrThrow("stock"))
//        }
//        cursor.close()
//        db.close()
//        return stock
//    }

    // ============================
    // MÉTODOS PARA VALIDACIONES
    // ============================

    fun existeProducto(nombre: String): Boolean {
        val db = readableDatabase
        val cursor = db.rawQuery("SELECT COUNT(*) FROM productos WHERE nombre = ?", arrayOf(nombre))
        cursor.moveToFirst()
        val count = cursor.getInt(0)
        cursor.close()
        db.close()
        return count > 0
    }

    fun existeCorreo(correo: String): Boolean {
        val db = readableDatabase
        val cursor = db.rawQuery("SELECT COUNT(*) FROM usuarios WHERE correo = ?", arrayOf(correo))
        cursor.moveToFirst()
        val count = cursor.getInt(0)
        cursor.close()
        db.close()
        return count > 0
    }
}