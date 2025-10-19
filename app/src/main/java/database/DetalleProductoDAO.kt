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
            // Helper para obtener String, devolviendo null si el valor es DB Null
            val descripcion = cursor.getString(cursor.getColumnIndexOrThrow("descripcion"))
            val imagen = cursor.getString(cursor.getColumnIndexOrThrow("imagen"))
            val nombreCategoria = cursor.getString(cursor.getColumnIndexOrThrow("nombreCategoria"))

            producto = Producto(
                idProd = cursor.getInt(cursor.getColumnIndexOrThrow("idProd")),
                nombre = cursor.getString(cursor.getColumnIndexOrThrow("nombre")),
                // 💡 CORREGIDO: Usamos la variable local 'descripcion' (String? )
                descripcion = descripcion,
                precio = cursor.getDouble(cursor.getColumnIndexOrThrow("precio")),
                stock = cursor.getInt(cursor.getColumnIndexOrThrow("stock")),
                idCat = cursor.getInt(cursor.getColumnIndexOrThrow("idCat")),
                // 💡 CORREGIDO: Usamos el parámetro correcto 'nombreCategoria'
                nombreCategoria = nombreCategoria ?: "", // Maneja null si la categoría no existe
                // 💡 CORREGIDO: Usamos la variable local 'imagen' (String? )
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
            // Helper para obtener String, devolviendo null si el valor es DB Null
            val descripcion = cursor.getString(cursor.getColumnIndexOrThrow("descripcion"))
            val imagen = cursor.getString(cursor.getColumnIndexOrThrow("imagen"))
            val nombreCategoria = cursor.getString(cursor.getColumnIndexOrThrow("nombreCategoria"))

            lista.add(
                Producto(
                    idProd = cursor.getInt(cursor.getColumnIndexOrThrow("idProd")),
                    nombre = cursor.getString(cursor.getColumnIndexOrThrow("nombre")),
                    // 💡 CORREGIDO: Usamos la variable local 'descripcion' (String? )
                    descripcion = descripcion,
                    precio = cursor.getDouble(cursor.getColumnIndexOrThrow("precio")),
                    stock = cursor.getInt(cursor.getColumnIndexOrThrow("stock")),
                    idCat = cursor.getInt(cursor.getColumnIndexOrThrow("idCat")),
                    // 💡 CORREGIDO: Usamos el parámetro correcto 'nombreCategoria'
                    nombreCategoria = nombreCategoria ?: "", // Maneja null si la categoría no existe
                    // 💡 CORREGIDO: Usamos la variable local 'imagen' (String? )
                    imagen = imagen
                )
            )
        }
        cursor.close()
        db.close()
        return lista
    }
}