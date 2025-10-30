// CarritoItem.kt
package com.example.mitiendita.entity


data class CarritoItem(
    val idProducto: Int,
    val nombre: String,
    val precio: Double,
    var cantidad: Int,
    var unidadMedida: String,  // Ya está incluida
    val imagen: String?,
    val stock: Int
) {

    fun incrementarCantidad(): Boolean {
        if (cantidad < stock) {
            cantidad++
            return true
        }
        return false
    }

    fun decrementarCantidad(): Boolean {
        if (cantidad > 1) {
            cantidad--
            return true
        }
        return false
    }

    fun getSubtotal(): Double {
        return precio * cantidad
    }
}