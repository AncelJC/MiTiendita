// CarritoDAO.kt
package com.example.mitiendita.dao

import android.content.ContentValues
import android.content.Context
import com.example.mitiendita.database.DBHelper
import com.example.mitiendita.entity.CarritoItem
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class CarritoDAO(private val context: Context) {

    private val dbHelper = DBHelper(context)
    private val productoDAO = ProductoDAO(context)

    fun procesarVenta(carritoItems: List<CarritoItem>, idUsuario: Int): Pair<Boolean, Int> {
        val db = dbHelper.writableDatabase

        try {
            db.beginTransaction()

            // 1. Calcular total
            val total = carritoItems.sumOf { it.precio * it.cantidad }

            // 2. Registrar la compra (cabecera)
            val fecha = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date())
            val idCompra = registrarCompra(db, total, fecha, idUsuario)

            if (idCompra == -1L) {
                db.endTransaction()
                return Pair(false, -1)
            }

            // 3. Registrar detalles y actualizar stock
            for (item in carritoItems) {
                // Registrar detalle de compra
                val detalleExitoso = registrarDetalleCompra(
                    db,
                    idCompra.toInt(),
                    item,
                    item.precio * item.cantidad
                )

                if (!detalleExitoso) {
                    db.endTransaction()
                    return Pair(false, -1)
                }

                // Actualizar stock del producto
                val nuevoStock = productoDAO.obtenerStockProducto(item.idProducto) - item.cantidad
                val stockActualizado = productoDAO.actualizarStockProducto(item.idProducto, nuevoStock)

                if (!stockActualizado) {
                    db.endTransaction()
                    return Pair(false, -1)
                }
            }

            db.setTransactionSuccessful()
            return Pair(true, idCompra.toInt())

        } catch (e: Exception) {
            e.printStackTrace()
            return Pair(false, -1)
        } finally {
            db.endTransaction()
            db.close()
        }
    }

    private fun registrarCompra(db: android.database.sqlite.SQLiteDatabase, total: Double, fecha: String, idUsuario: Int): Long {
        val values = ContentValues().apply {
            put("total", total)
            put("fecha", fecha)
            put("idUsua", idUsuario)
        }
        return db.insert("compras", null, values)
    }

    private fun registrarDetalleCompra(db: android.database.sqlite.SQLiteDatabase, idCompra: Int, item: CarritoItem, precioPagado: Double): Boolean {
        val values = ContentValues().apply {
            put("idCompra", idCompra)
            put("idProd", item.idProducto)
            put("producto", item.nombre)
            put("unidadMedida", item.unidadMedida)
            put("cantidad", item.cantidad)
            put("precioUnitario", item.precio)
            put("precioPagado", precioPagado)
            put("imagen", item.imagen)
        }
        val resultado = db.insert("detalleCompra", null, values)
        return resultado != -1L
    }

    fun validarStockDisponible(carritoItems: List<CarritoItem>): Boolean {
        for (item in carritoItems) {
            val stockActual = productoDAO.obtenerStockProducto(item.idProducto)
            if (item.cantidad > stockActual) {
                return false
            }
        }
        return true
    }

    fun calcularTotalCarrito(carritoItems: List<CarritoItem>): Double {
        return carritoItems.sumOf { it.precio * it.cantidad }
    }
}