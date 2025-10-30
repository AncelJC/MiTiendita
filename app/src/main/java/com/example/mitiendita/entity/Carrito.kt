package com.example.mitiendita.entity

data class Carrito(
    val idProducto: Int,
    val nombre: String,
    val precio: Double,
    var cantidad: Int,
    val imagen: String?,
    val stock: Int
)