package com.example.mitiendita.database

import android.content.Context
import android.database.Cursor
import com.example.mitiendita.entity.DetalleCompra

class DetalleCompraDAO(context: Context) {

    private val dbHelper = DBHelper(context)

    // Obtiene los detalles de una compra por su ID

    fun obtenerDetallesPorCompra(idCompra: Int): List<DetalleCompra> {
        val detalles = mutableListOf<DetalleCompra>()
        val db = dbHelper.readableDatabase

        val cursor: Cursor = db.rawQuery(
            """
            SELECT d.*, p.descripcion as descripcion_producto
            FROM detalleCompra d 
            LEFT JOIN productos p ON d.idProd = p.idProd 
            WHERE d.idCompra = ? 
            ORDER BY d.idDetalleComp
            """.trimIndent(),
            arrayOf(idCompra.toString())
        )

        if (cursor.moveToFirst()) {
            do {
                val detalle = DetalleCompra(
                    idDetalleComp = cursor.getInt(cursor.getColumnIndexOrThrow("idDetalleComp")),
                    idCompra = cursor.getInt(cursor.getColumnIndexOrThrow("idCompra")),
                    idProd = cursor.getInt(cursor.getColumnIndexOrThrow("idProd")),
                    producto = cursor.getString(cursor.getColumnIndexOrThrow("producto")),
                    unidadMedida = cursor.getString(cursor.getColumnIndexOrThrow("unidadMedida")),
                    cantidad = cursor.getInt(cursor.getColumnIndexOrThrow("cantidad")),
                    precioUnitario = cursor.getDouble(cursor.getColumnIndexOrThrow("precioUnitario")),
                    precioPagado = cursor.getDouble(cursor.getColumnIndexOrThrow("precioPagado")),
                    imagen = cursor.getString(cursor.getColumnIndexOrThrow("imagen")),
                    descripcionProducto = cursor.getString(cursor.getColumnIndexOrThrow("descripcion_producto"))
                )
                detalles.add(detalle)
            } while (cursor.moveToNext())
        }
        cursor.close()
        db.close()
        return detalles
    }

    // Obtiene las ventas por categoría

    fun obtenerVentasPorCategoria(): List<Pair<String, Int>> {
        val ventas = mutableListOf<Pair<String, Int>>()
        val db = dbHelper.readableDatabase

        val cursor: Cursor = db.rawQuery(
            """
            SELECT c.nombre as categoria, SUM(d.cantidad) as total_vendido
            FROM detalleCompra d
            JOIN productos p ON d.idProd = p.idProd
            JOIN categorias c ON p.idCat = c.idCat
            GROUP BY c.nombre
            ORDER BY total_vendido DESC
            """.trimIndent(),
            null
        )

        if (cursor.moveToFirst()) {
            do {
                val categoria = cursor.getString(cursor.getColumnIndexOrThrow("categoria"))
                val totalVendido = cursor.getInt(cursor.getColumnIndexOrThrow("total_vendido"))
                ventas.add(Pair(categoria, totalVendido))
            } while (cursor.moveToNext())
        }
        cursor.close()
        db.close()
        return ventas
    }

    // Obtiene los productos más vendidos

    fun obtenerProductosMasVendidos(limite: Int = 10): List<Triple<String, Int, Double>> {
        val productos = mutableListOf<Triple<String, Int, Double>>()
        val db = dbHelper.readableDatabase

        val cursor: Cursor = db.rawQuery(
            """
            SELECT d.producto, SUM(d.cantidad) as total_vendido, SUM(d.precioPagado) as ingreso_total
            FROM detalleCompra d
            GROUP BY d.producto
            ORDER BY total_vendido DESC
            LIMIT ?
            """.trimIndent(),
            arrayOf(limite.toString())
        )

        if (cursor.moveToFirst()) {
            do {
                val producto = cursor.getString(cursor.getColumnIndexOrThrow("producto"))
                val totalVendido = cursor.getInt(cursor.getColumnIndexOrThrow("total_vendido"))
                val ingresoTotal = cursor.getDouble(cursor.getColumnIndexOrThrow("ingreso_total"))
                productos.add(Triple(producto, totalVendido, ingresoTotal))
            } while (cursor.moveToNext())
        }
        cursor.close()
        db.close()
        return productos
    }

    // Obtiene las ventas por fecha
    fun obtenerVentasPorFecha(fechaInicio: String, fechaFin: String): List<Pair<String, Double>> {
        val ventas = mutableListOf<Pair<String, Double>>()
        val db = dbHelper.readableDatabase

        val cursor: Cursor = db.rawQuery(
            """
            SELECT c.fecha, SUM(c.total) as total_dia
            FROM compras c
            WHERE c.fecha BETWEEN ? AND ?
            GROUP BY c.fecha
            ORDER BY c.fecha
            """.trimIndent(),
            arrayOf(fechaInicio, fechaFin)
        )

        if (cursor.moveToFirst()) {
            do {
                val fecha = cursor.getString(cursor.getColumnIndexOrThrow("fecha"))
                val totalDia = cursor.getDouble(cursor.getColumnIndexOrThrow("total_dia"))
                ventas.add(Pair(fecha, totalDia))
            } while (cursor.moveToNext())
        }
        cursor.close()
        db.close()
        return ventas
    }

    // Obtiene estadísticas detalladas
    fun obtenerEstadisticasDetalladas(): Map<String, Any> {
        val db = dbHelper.readableDatabase
        val estadisticas = mutableMapOf<String, Any>()

        // Total de ventas
        val cursorVentas: Cursor = db.rawQuery(
            """
            SELECT 
                COUNT(DISTINCT idCompra) as total_compras,
                SUM(cantidad) as total_productos_vendidos,
                SUM(precioPagado) as ingreso_total,
                AVG(precioPagado) as promedio_venta
            FROM detalleCompra
            """.trimIndent(),
            null
        )

        if (cursorVentas.moveToFirst()) {
            estadisticas["total_compras"] = cursorVentas.getInt(cursorVentas.getColumnIndexOrThrow("total_compras"))
            estadisticas["total_productos_vendidos"] = cursorVentas.getInt(cursorVentas.getColumnIndexOrThrow("total_productos_vendidos"))
            estadisticas["ingreso_total"] = cursorVentas.getDouble(cursorVentas.getColumnIndexOrThrow("ingreso_total"))
            estadisticas["promedio_venta"] = cursorVentas.getDouble(cursorVentas.getColumnIndexOrThrow("promedio_venta"))
        }
        cursorVentas.close()

        // Producto más vendido
        val cursorTopProducto: Cursor = db.rawQuery(
            """
            SELECT producto, SUM(cantidad) as total_vendido
            FROM detalleCompra
            GROUP BY producto
            ORDER BY total_vendido DESC
            LIMIT 1
            """.trimIndent(),
            null
        )

        if (cursorTopProducto.moveToFirst()) {
            estadisticas["producto_mas_vendido"] = cursorTopProducto.getString(cursorTopProducto.getColumnIndexOrThrow("producto"))
            estadisticas["cantidad_producto_top"] = cursorTopProducto.getInt(cursorTopProducto.getColumnIndexOrThrow("total_vendido"))
        }
        cursorTopProducto.close()

        db.close()
        return estadisticas
    }

    // Obtiene las compras de un usuario

    fun obtenerComprasUsuario(idUsuario: Int): List<Map<String, Any>> {
        val compras = mutableListOf<Map<String, Any>>()
        val db = dbHelper.readableDatabase

        val cursor: Cursor = db.rawQuery(
            """
            SELECT 
                c.idCompra,
                c.fecha,
                c.total,
                COUNT(d.idDetalleComp) as total_productos,
                GROUP_CONCAT(d.producto) as productos
            FROM compras c
            JOIN detalleCompra d ON c.idCompra = d.idCompra
            WHERE c.idUsua = ?
            GROUP BY c.idCompra
            ORDER BY c.fecha DESC
            """.trimIndent(),
            arrayOf(idUsuario.toString())
        )

        if (cursor.moveToFirst()) {
            do {
                val compra = mapOf(
                    "idCompra" to cursor.getInt(cursor.getColumnIndexOrThrow("idCompra")),
                    "fecha" to cursor.getString(cursor.getColumnIndexOrThrow("fecha")),
                    "total" to cursor.getDouble(cursor.getColumnIndexOrThrow("total")),
                    "total_productos" to cursor.getInt(cursor.getColumnIndexOrThrow("total_productos")),
                    "productos" to cursor.getString(cursor.getColumnIndexOrThrow("productos"))
                )
                compras.add(compra)
            } while (cursor.moveToNext())
        }
        cursor.close()
        db.close()
        return compras
    }

    // Verifica si un producto ha sido vendido

    fun productoHaSidoVendido(idProducto: Int): Boolean {
        val db = dbHelper.readableDatabase
        val cursor: Cursor = db.rawQuery(
            "SELECT COUNT(*) as total FROM detalleCompra WHERE idProd = ?",
            arrayOf(idProducto.toString())
        )

        var total = 0
        if (cursor.moveToFirst()) {
            total = cursor.getInt(cursor.getColumnIndexOrThrow("total"))
        }
        cursor.close()
        db.close()
        return total > 0
    }

    // Obtiene el ingreso por producto

    fun obtenerIngresoPorProducto(): List<Pair<String, Double>> {
        val ingresos = mutableListOf<Pair<String, Double>>()
        val db = dbHelper.readableDatabase

        val cursor: Cursor = db.rawQuery(
            """
            SELECT producto, SUM(precioPagado) as ingreso_total
            FROM detalleCompra
            GROUP BY producto
            ORDER BY ingreso_total DESC
            """.trimIndent(),
            null
        )

        if (cursor.moveToFirst()) {
            do {
                val producto = cursor.getString(cursor.getColumnIndexOrThrow("producto"))
                val ingresoTotal = cursor.getDouble(cursor.getColumnIndexOrThrow("ingreso_total"))
                ingresos.add(Pair(producto, ingresoTotal))
            } while (cursor.moveToNext())
        }
        cursor.close()
        db.close()
        return ingresos
    }
}