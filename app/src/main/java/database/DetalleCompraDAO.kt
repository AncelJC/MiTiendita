package database

import android.content.Context
import android.database.Cursor
import androidx.core.content.contentValuesOf
import com.example.mitiendita.database.DBHelper
import com.example.mitiendita.entity.DetalleCompra

class DetalleCompraDAO (context: Context){
    private val dbHelper = DBHelper(context)

    private  fun insertar(detalle: DetalleCompra): Long{
        val db = dbHelper.writableDatabase
        val valores = contentValuesOf().apply {
            put("producto", detalle.producto)
            put("unidad_medida", detalle.unidadMedida)
            put("cantidad", detalle.cantidad)
            put("precio_unitario", detalle.precioUnitario)
            put("precio_pagado", detalle.precioPagado)
            put("imagen", detalle.imagen)
            put("id_compra", detalle.idCompra)
        }

        return db.insert("detalleCompra", null, valores)
    }

    private fun obtenerDetalles(idCompra: Int): List<DetalleCompra>{
        val db= dbHelper.readableDatabase
        val lista = mutableListOf<DetalleCompra>()
        val cursor = db.rawQuery(
            "SELECT * FROM detalleCompra WHERE idCompra = ?",
            arrayOf(idCompra.toString())

        )

        while (cursor.moveToNext()){

            lista.add(
                DetalleCompra(
                    idDetalleComp = cursor.getInt(cursor.getColumnIndexOrThrow("idDetalleComp")),
                    producto = cursor.getString(cursor.getColumnIndexOrThrow("producto")),
                    unidadMedida = cursor.getString(cursor.getColumnIndexOrThrow("unidadMedida")),
                    cantidad = cursor.getInt(cursor.getColumnIndexOrThrow("cantidad")),
                    precioUnitario = cursor.getDouble(cursor.getColumnIndexOrThrow("precioUnitario")),
                    precioPagado = cursor.getDouble(cursor.getColumnIndexOrThrow("precioPagado")),
                    imagen = cursor.getString(cursor.getColumnIndexOrThrow("imagen")),
                    idCompra = cursor.getInt(cursor.getColumnIndexOrThrow("idCompra"))


            )
        )
    }
        cursor.close()
        db.close()
        return lista
    }



}