package com.example.mitiendita.dao

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
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

            val total = carritoItems.sumOf { it.precio * it.cantidad }

            val fecha = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date())
            val idCompra = registrarCompra(db, total, fecha, idUsuario)

            if (idCompra == -1L) {
                throw Exception("Error al registrar la cabecera de la compra.")
            }

            for (item in carritoItems) {

                val stockActual = productoDAO.obtenerStockProducto(db, item.idProducto)

                if (item.cantidad > stockActual) {
                    throw Exception("Stock insuficiente (${item.cantidad} > $stockActual) para el producto ${item.nombre}.")
                }

                val detalleExitoso = registrarDetalleCompra(
                    db,
                    idCompra.toInt(),
                    item,
                    item.precio * item.cantidad
                )

                if (!detalleExitoso) {
                    throw Exception("Error al registrar el detalle de la compra para ${item.nombre}.")
                }

                val nuevoStock = stockActual - item.cantidad
                val stockActualizado = productoDAO.actualizarStockProducto(db, item.idProducto, nuevoStock)

                if (!stockActualizado) {
                    throw Exception("Error al actualizar stock para ${item.nombre}.")
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


    private fun registrarCompra(db: SQLiteDatabase, total: Double, fecha: String, idUsuario: Int): Long {
        val values = ContentValues().apply {
            put("total", total)
            put("fecha", fecha)
            put("idUsua", idUsuario)
        }
        return db.insert("compras", null, values)
    }

    private fun registrarDetalleCompra(db: SQLiteDatabase, idCompra: Int, item: CarritoItem, precioPagado: Double): Boolean {
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
            val stockActual = productoDAO.obtenerStockProductoIndependiente(item.idProducto)
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