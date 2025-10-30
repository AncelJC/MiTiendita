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
) : Serializable {

    // Constructor secundario para facilitar la creación de productos
    constructor(
        nombre: String,
        descripcion: String?,
        precio: Double,
        stock: Int,
        imagen: String?,
        idCat: Int,
        activo: Boolean = true,
        unidadMedida: String = "unidad",
        nombreCategoria: String? = null
    ) : this(0, nombre, descripcion, precio, stock, imagen, idCat, activo, unidadMedida, nombreCategoria)

    // Método para verificar si el producto está disponible
    fun estaDisponible(): Boolean {
        return activo && stock > 0
    }

    // Método para verificar si el stock es bajo
    fun tieneStockBajo(umbral: Int = 10): Boolean {
        return stock <= umbral && stock > 0
    }

    // Método para verificar si está agotado
    fun estaAgotado(): Boolean {
        return stock == 0
    }

    // Método para formatear el precio
    fun getPrecioFormateado(): String {
        return "S/ ${String.format("%.2f", precio)}"
    }

    // Método para obtener información de stock formateada
    fun getStockFormateado(): String {
        return when {
            estaAgotado() -> "Agotado"
            tieneStockBajo() -> "Stock bajo: $stock"
            else -> "Stock: $stock"
        }
    }

    // Método para obtener el estado del producto
    fun getEstado(): String {
        return if (activo) "Activo" else "Inactivo"
    }

    // Método para clonar el producto (útil para edición)
    fun clone(): Producto {
        return this.copy()
    }

    override fun toString(): String {
        return "Producto(idProd=$idProd, nombre='$nombre', precio=$precio, stock=$stock, activo=$activo, categoria=$nombreCategoria)"
    }

    companion object {
        // Producto vacío para usar como valor por defecto
        fun empty(): Producto {
            return Producto(
                idProd = 0,
                nombre = "",
                descripcion = null,
                precio = 0.0,
                stock = 0,
                imagen = null,
                idCat = 0,
                activo = true,
                unidadMedida = "unidad",
                nombreCategoria = null
            )
        }

        // Lista de unidades de medida disponibles
        fun getUnidadesMedida(): List<String> {
            return listOf(
                "unidad",
                "kg",
                "litro",
                "gramo",
                "metro",
                "caja",
                "juego",
                "paquete",
                "botella",
                "lata",
                "bolsa",
                "docena"
            )
        }
    }
}