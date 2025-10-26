package com.example.mitiendita.database

import android.content.ContentValues
import android.content.Context
import com.example.mitiendita.entity.Producto // Asegúrate de tener esta entidad

class ProductoDAO (context: Context){

    private val dbHelper = DBHelper(context)

    /**
     * Inserta un nuevo producto en la base de datos.
     */
    fun insertarProducto(producto: Producto): Long {
        val db = dbHelper.writableDatabase
        val valores = ContentValues().apply {
            put("nombre", producto.nombre)
            put("descripcion", producto.descripcion)
            put("precio", producto.precio)
            put("stock", producto.stock)
            // Asumiendo que producto.idCat existe en tu entidad Producto para el FK
            put("idCat", producto.idCat)
            put("imagen", producto.imagen)
        }
        val result = db.insert("productos", null, valores)
        db.close()
        return result
    }

    /**
     * Actualiza un producto existente en la base de datos.
     */
    fun actualizarProducto(producto: Producto): Int {
        val db = dbHelper.writableDatabase
        val valores = ContentValues().apply {
            put("nombre", producto.nombre)
            put("descripcion", producto.descripcion)
            put("precio", producto.precio)
            put("stock", producto.stock)
            put("idCat", producto.idCat)
            put("imagen", producto.imagen)
        }
        val result = db.update(
            "productos",
            valores,
            "idProd = ?",
            arrayOf(producto.idProd.toString()) // Asumiendo que producto.idProd es el ID
        )
        db.close()
        return result
    }

    /**
     * Elimina un producto por su ID.
     */
    fun eliminarProducto(id: Int): Int {
        val db = dbHelper.writableDatabase
        val result = db.delete("productos", "idProd = ?", arrayOf(id.toString()))
        db.close()
        return result
    }
}