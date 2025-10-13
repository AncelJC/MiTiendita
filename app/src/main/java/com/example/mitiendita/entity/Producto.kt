package com.example.mitiendita.entity

data class Producto (
    val idProd: Int,
    val nombre: String,
    val descripcion: String,
    val precio: Double,
    val catecoria: String,
    val imagen: String,
    val stock: Int

)