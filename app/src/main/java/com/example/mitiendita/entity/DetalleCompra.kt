package com.example.mitiendita.entity

data class DetalleCompra (
    val idDetalleComp: Int = 0,
    val producto: String,
    val unidadMedida: String = "",
    val cantidad: Int = 0,
    val precioUnitario: Double = 0.0,
    val precioPagado: Double = 0.0,
    val imagen: String,
    val idCompra: Int
)