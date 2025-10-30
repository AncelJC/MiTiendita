package com.example.mitiendita.dao

import android.content.ContentValues
import android.content.Context
import com.example.mitiendita.database.DBHelper
import com.example.mitiendita.entity.Producto
import com.example.mitiendita.entity.Productos

class ProductoDAO(context: Context) {

    private val dbHelper = DBHelper(context)





    fun insertarProducto(producto: Producto): Long {
        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put("nombre", producto.nombre)
            put("descripcion", producto.descripcion)
            put("precio", producto.precio)
            put("stock", producto.stock)
            put("idCat", producto.idCat)
            put("imagen", producto.imagen)
            put("unidadMedida", producto.unidadMedida)  // Nueva columna
            put("activo", if (producto.activo) 1 else 0)
        }
        val resultado = db.insert("productos", null, values)
        db.close()
        return resultado
    }

    fun actualizarProducto(producto: Producto): Int {
        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put("nombre", producto.nombre)
            put("descripcion", producto.descripcion)
            put("precio", producto.precio)
            put("stock", producto.stock)
            put("idCat", producto.idCat)
            put("imagen", producto.imagen)
            put("unidadMedida", producto.unidadMedida)  // Nueva columna
            put("activo", if (producto.activo) 1 else 0)
        }

        val filasAfectadas = db.update(
            "productos",
            values,
            "idProd = ?",
            arrayOf(producto.idProd.toString())
        )
        db.close()
        return filasAfectadas
    }



    fun obtenerProductosActivos(): List<Producto> {
        val db = dbHelper.readableDatabase
        val listaProductos = mutableListOf<Producto>()

        // CORREGIDO: cambiar "categoria" por "categorias"
        val query = """
            SELECT p.*, c.nombre as nombreCategoria 
            FROM productos p 
            LEFT JOIN categorias c ON p.idCat = c.idCat  -- ← AQUÍ EL CAMBIO
            WHERE p.activo = 1
            ORDER BY p.nombre
        """.trimIndent()

        val cursor = db.rawQuery(query, null)

        while (cursor.moveToNext()) {
            val producto = Producto(
                idProd = cursor.getInt(cursor.getColumnIndexOrThrow("idProd")),
                nombre = cursor.getString(cursor.getColumnIndexOrThrow("nombre")),
                descripcion = cursor.getString(cursor.getColumnIndexOrThrow("descripcion")),
                precio = cursor.getDouble(cursor.getColumnIndexOrThrow("precio")),
                stock = cursor.getInt(cursor.getColumnIndexOrThrow("stock")),
                imagen = cursor.getString(cursor.getColumnIndexOrThrow("imagen")),
                idCat = cursor.getInt(cursor.getColumnIndexOrThrow("idCat")),
                activo = cursor.getInt(cursor.getColumnIndexOrThrow("activo")) == 1,
                unidadMedida = cursor.getString(cursor.getColumnIndexOrThrow("unidadMedida")),
                nombreCategoria = cursor.getString(cursor.getColumnIndexOrThrow("nombreCategoria"))
            )
            listaProductos.add(producto)
        }

        cursor.close()
        db.close()
        return listaProductos
    }

    fun obtenerTodosLosProductosAdmi(): List<Producto> {
        val db = dbHelper.readableDatabase
        val listaProductos = mutableListOf<Producto>()

        // CORREGIDO: cambiar "categoria" por "categorias"
        val query = """
            SELECT p.*, c.nombre as nombreCategoria 
            FROM productos p 
            LEFT JOIN categorias c ON p.idCat = c.idCat  -- ← AQUÍ EL CAMBIO
            ORDER BY p.activo DESC, p.nombre
        """.trimIndent()

        val cursor = db.rawQuery(query, null)

        while (cursor.moveToNext()) {
            val producto = Producto(
                idProd = cursor.getInt(cursor.getColumnIndexOrThrow("idProd")),
                nombre = cursor.getString(cursor.getColumnIndexOrThrow("nombre")),
                descripcion = cursor.getString(cursor.getColumnIndexOrThrow("descripcion")),
                precio = cursor.getDouble(cursor.getColumnIndexOrThrow("precio")),
                stock = cursor.getInt(cursor.getColumnIndexOrThrow("stock")),
                imagen = cursor.getString(cursor.getColumnIndexOrThrow("imagen")),
                idCat = cursor.getInt(cursor.getColumnIndexOrThrow("idCat")),
                activo = cursor.getInt(cursor.getColumnIndexOrThrow("activo")) == 1,
                unidadMedida = cursor.getString(cursor.getColumnIndexOrThrow("unidadMedida")),
                nombreCategoria = cursor.getString(cursor.getColumnIndexOrThrow("nombreCategoria"))
            )
            listaProductos.add(producto)
        }

        cursor.close()
        db.close()
        return listaProductos
    }



    // Actualiza los métodos que obtienen productos para incluir unidadMedida






    fun cambiarEstadoProduct(idProd: Int, nuevoEstado: Boolean): Boolean {
        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put("activo", if (nuevoEstado) 1 else 0)
        }
        val result = db.update("productos", values, "idProd = ?", arrayOf(idProd.toString()))
        db.close()
        return result > 0
    }

    // ========================
    // ELIMINAR PRODUCTO
    // ========================
    fun eliminarProduct(idProd: Int): Int {
        val db = dbHelper.writableDatabase
        val result = db.delete("productos", "idProd = ?", arrayOf(idProd.toString()))
        db.close()
        return result
    }

    // ========================
    // OBTENER PRODUCTO POR ID
    // ========================
    fun obtenerProductoPorId(idProd: Int): Producto? {
        val db = dbHelper.readableDatabase
        val cursor = db.rawQuery(
            """
            SELECT p.*, c.nombre AS nombreCategoria
            FROM productos p
            LEFT JOIN categorias c ON p.idCat = c.idCat
            WHERE p.idProd = ?
            """, arrayOf(idProd.toString())
        )

        var producto: Producto? = null
        if (cursor.moveToFirst()) {
            producto = Producto(
                idProd = cursor.getInt(cursor.getColumnIndexOrThrow("idProd")),
                nombre = cursor.getString(cursor.getColumnIndexOrThrow("nombre")),
                descripcion = cursor.getString(cursor.getColumnIndexOrThrow("descripcion")),
                precio = cursor.getDouble(cursor.getColumnIndexOrThrow("precio")),
                stock = cursor.getInt(cursor.getColumnIndexOrThrow("stock")),
                idCat = cursor.getInt(cursor.getColumnIndexOrThrow("idCat")),
                imagen = cursor.getString(cursor.getColumnIndexOrThrow("imagen")),
                activo = cursor.getInt(cursor.getColumnIndexOrThrow("activo")) == 1,
                nombreCategoria = cursor.getString(cursor.getColumnIndexOrThrow("nombreCategoria"))
            )
        }

        cursor.close()
        db.close()
        return producto
    }

    fun actualizarStockProducto(idProd: Int, nuevoStock: Int): Boolean {
        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put("stock", nuevoStock)
        }

        val filasAfectadas = db.update(
            "productos",
            values,
            "idProd = ?",
            arrayOf(idProd.toString())
        )
        db.close()
        return filasAfectadas > 0
    }

    fun obtenerStockProducto(idProd: Int): Int {
        val db = dbHelper.readableDatabase
        val cursor = db.rawQuery("SELECT stock FROM productos WHERE idProd = ?", arrayOf(idProd.toString()))
        var stock = 0
        if (cursor.moveToFirst()) {
            stock = cursor.getInt(cursor.getColumnIndexOrThrow("stock"))
        }
        cursor.close()
        db.close()
        return stock
    }


}
