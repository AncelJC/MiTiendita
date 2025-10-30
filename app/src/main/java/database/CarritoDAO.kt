package database

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import com.example.mitiendita.database.DBHelper
import com.example.mitiendita.entity.CarritoItem
import com.example.mitiendita.entity.Compra
import com.example.mitiendita.entity.DetalleCompra
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class CarritoDAO(context: Context) {

    private val dbHelper = DBHelper(context)


    fun registrarCompra(total: Double, idUsua: Int): Long {
        val db = dbHelper.writableDatabase
        val fecha = obtenerFechaActual()
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
        val db = dbHelper.writableDatabase
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

    fun actualizarStockProducto(idProducto: Int, cantidadVendida: Int): Boolean {
        val db = dbHelper.writableDatabase
        try {
            // Primero obtener el stock actual
            val cursor = db.rawQuery("SELECT stock FROM productos WHERE idProd = ?", arrayOf(idProducto.toString()))
            var stockActual = 0
            if (cursor.moveToFirst()) {
                stockActual = cursor.getInt(cursor.getColumnIndexOrThrow("stock"))
            }
            cursor.close()

            if (stockActual < cantidadVendida) {
                return false // No hay suficiente stock
            }

            // Calcular nuevo stock y actualizar
            val nuevoStock = stockActual - cantidadVendida
            val values = ContentValues().apply {
                put("stock", nuevoStock)
            }
            val result = db.update("productos", values, "idProd = ?", arrayOf(idProducto.toString()))
            return result > 0
        } catch (e: Exception) {
            return false
        } finally {
            db.close()
        }
    }

    fun obtenerStockActual(idProducto: Int): Int {
        val db = dbHelper.readableDatabase
        val cursor = db.rawQuery("SELECT stock FROM productos WHERE idProd = ?", arrayOf(idProducto.toString()))
        var stockActual = 0
        if (cursor.moveToFirst()) {
            stockActual = cursor.getInt(cursor.getColumnIndexOrThrow("stock"))
        }
        cursor.close()
        db.close()
        return stockActual
    }

    fun validarStockDisponible(carritoItems: List<CarritoItem>): Boolean {
        for (item in carritoItems) {
            val stockActual = obtenerStockActual(item.idProducto)
            if (item.cantidad > stockActual) {
                return false
            }
        }
        return true
    }

    fun procesarVenta(carritoItems: List<CarritoItem>, idUsuario: Int): Pair<Boolean, Int> {
        val total = calcularTotalCarrito(carritoItems)

        try {
            // 1. Registrar la compra
            val idCompra = registrarCompra(total, idUsuario)
            if (idCompra == -1L) {
                return Pair(false, -1)
            }

            // 2. Registrar cada detalle de compra y actualizar stock
            for (item in carritoItems) {
                // Registrar detalle
                val successDetalle = registrarDetalleCompra(
                    idCompra = idCompra.toInt(),
                    idProd = item.idProducto,
                    producto = item.nombre,
                    unidadMedida =  item.unidadMedida,
                    cantidad = item.cantidad,
                    precioUnitario = item.precio,
                    precioPagado = item.getSubtotal(),
                    imagen = item.imagen
                )

                if (successDetalle == -1L) {
                    return Pair(false, -1)
                }

                // Actualizar stock
                val stockActualizado = actualizarStockProducto(item.idProducto, item.cantidad)
                if (!stockActualizado) {
                    return Pair(false, -1)
                }
            }

            return Pair(true, idCompra.toInt())
        } catch (e: Exception) {
            return Pair(false, -1)
        }
    }

    fun calcularTotalCarrito(carritoItems: List<CarritoItem>): Double {
        return carritoItems.sumOf { it.getSubtotal() }
    }


    private fun obtenerFechaActual(): String {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        return dateFormat.format(Date())
    }
}