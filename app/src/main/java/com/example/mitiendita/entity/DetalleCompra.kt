package com.example.mitiendita.entity

data class DetalleCompra (
    val idDetalleComp: Int,
    val idCompra: Int,
    val idProd: Int,
    val producto: String,
    val unidadMedida: String,
    val cantidad: Int,
    val precioUnitario: Double,
    val precioPagado: Double,
    val imagen: String?,
    val descripcionProducto: String? = null
)