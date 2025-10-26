package com.example.mitiendita.entity

data class Producto(
    val idProd: Int = 0,
    val nombre: String,
    val descripcion: String? = null, // Debe ser nullable
    val precio: Double,
    val stock: Int,
    val idCat: Int,
    val codigo: String = "",
    val nombreCategoria: String = "", // Debe tener valor por defecto
    val imagen: String? = null // Debe ser nullable
)