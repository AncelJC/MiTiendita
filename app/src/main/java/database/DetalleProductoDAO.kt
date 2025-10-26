package com.example.mitiendita.database

import android.content.Context
import com.example.mitiendita.entity.Producto
import android.database.Cursor

class DetalleProductoDAO(context: Context) {

    private val dbHelper = DBHelper(context)

    /**
     * Obtiene un producto individual por su ID.
     */
    fun obtenerProductoPorId(idProducto: Int): Producto? {
        val db = dbHelper.readableDatabase
        val cursor: Cursor = db.rawQuery(
            """
            SELECT p.*, c.nombre as nombreCategoria
            FROM productos p
            LEFT JOIN categorias c ON p.idCat = c.idCat
            WHERE p.idProd = ?
            """.trimIndent(),
            arrayOf(idProducto.toString())
        )
        var producto: Producto? = null

        if (cursor.moveToFirst()) {
            val descripcion = cursor.getString(cursor.getColumnIndexOrThrow("descripcion"))
            val imagen = cursor.getString(cursor.getColumnIndexOrThrow("imagen"))
            val nombreCategoria = cursor.getString(cursor.getColumnIndexOrThrow("nombreCategoria"))

            producto = Producto(
                idProd = cursor.getInt(cursor.getColumnIndexOrThrow("idProd")),
                nombre = cursor.getString(cursor.getColumnIndexOrThrow("nombre")),
                descripcion = descripcion,
                precio = cursor.getDouble(cursor.getColumnIndexOrThrow("precio")),
                stock = cursor.getInt(cursor.getColumnIndexOrThrow("stock")),
                idCat = cursor.getInt(cursor.getColumnIndexOrThrow("idCat")),
                nombreCategoria = nombreCategoria ?: "",
                imagen = imagen
            )
        }
        cursor.close()
        db.close()
        return producto
    }

    /**
     * Obtiene la lista de TODOS los productos, incluyendo el nombre de su categoría.
     */
    fun obtenerTodosLosProductosConCategoria(): List<Producto> {
        val db = dbHelper.readableDatabase
        val lista = mutableListOf<Producto>()

        val cursor = db.rawQuery(
            """
            SELECT p.*, c.nombre as nombreCategoria 
            FROM productos p 
            LEFT JOIN categorias c ON p.idCat = c.idCat 
            ORDER BY p.nombre
            """.trimIndent(),
            null
        )

        while (cursor.moveToNext()) {
            val descripcion = cursor.getString(cursor.getColumnIndexOrThrow("descripcion"))
            val imagen = cursor.getString(cursor.getColumnIndexOrThrow("imagen"))
            val nombreCategoria = cursor.getString(cursor.getColumnIndexOrThrow("nombreCategoria"))

            lista.add(
                Producto(
                    idProd = cursor.getInt(cursor.getColumnIndexOrThrow("idProd")),
                    nombre = cursor.getString(cursor.getColumnIndexOrThrow("nombre")),
                    descripcion = descripcion,
                    precio = cursor.getDouble(cursor.getColumnIndexOrThrow("precio")),
                    stock = cursor.getInt(cursor.getColumnIndexOrThrow("stock")),
                    idCat = cursor.getInt(cursor.getColumnIndexOrThrow("idCat")),
                    nombreCategoria = nombreCategoria ?: "",
                    imagen = imagen
                )
            )
        }
        cursor.close()
        db.close()
        return lista
    }

    /**
     * Elimina un producto de la base de datos por su ID.
     * @return El número de filas eliminadas.
     */
    fun eliminarProducto(idProducto: Int): Int {
        val db = dbHelper.writableDatabase
        val filasAfectadas = db.delete(
            "productos",
            "idProd = ?",
            arrayOf(idProducto.toString())
        )
        db.close()
        return filasAfectadas
    }

    /**
     * Obtiene el conteo total de productos, stock bajo y sin stock para las estadísticas.
     * @return Un mapa con las claves "total", "stockBajo", "sinStock".
     */
    fun obtenerEstadisticasStock(umbralStockBajo: Int = 10): Map<String, Int> {
        val db = dbHelper.readableDatabase
        val stats = mutableMapOf<String, Int>()

        // 1. Conteo Total
        var cursor = db.rawQuery("SELECT COUNT(idProd) as total FROM productos", null)
        stats["total"] = if (cursor.moveToFirst()) cursor.getInt(0) else 0
        cursor.close()

        // 2. Sin Stock (Stock = 0)
        cursor = db.rawQuery("SELECT COUNT(idProd) FROM productos WHERE stock = 0", null)
        stats["sinStock"] = if (cursor.moveToFirst()) cursor.getInt(0) else 0
        cursor.close()

        // 3. Stock Bajo (0 < Stock <= Umbral)
        cursor = db.rawQuery("SELECT COUNT(idProd) FROM productos WHERE stock > 0 AND stock <= ?",
            arrayOf(umbralStockBajo.toString()))
        stats["stockBajo"] = if (cursor.moveToFirst()) cursor.getInt(0) else 0
        cursor.close()

        db.close()
        return stats
    }
}