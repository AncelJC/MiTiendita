package com.example.mitiendita.entity

/**
 * Representa la cabecera de una Compra (transacción).
 * Coincide con la tabla 'compras' en tu DBHelper.
 */
//
data class Compra (
    val idCompra: Int,
    val fecha: String,
    val total: Double,
    val idUsua: Int
)