package com.example.mitiendita.entity

import java.io.Serializable

data class Producto(
    val idProd: Int,
    var nombre: String,
    var descripcion: String?,
    var precio: Double,
    var stock: Int,
    var imagen: String?,
    var idCat: Int,
    var activo: Boolean,
    var unidadMedida: String = "unidad",
    var nombreCategoria: String? = null
) : Serializable