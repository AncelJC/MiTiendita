// CarritoViewModel.kt
package com.example.mitiendita.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.mitiendita.entity.CarritoItem

class CarritoViewModel : ViewModel() {

    private val _carritoItems = MutableLiveData<MutableList<CarritoItem>>(mutableListOf())
    val carritoItems: LiveData<MutableList<CarritoItem>> = _carritoItems

    private val _totalCarrito = MutableLiveData<Double>(0.0)
    val totalCarrito: LiveData<Double> = _totalCarrito

    fun agregarAlCarrito(item: CarritoItem) {
        val currentList = _carritoItems.value ?: mutableListOf()
        val existingItem = currentList.find { it.idProducto == item.idProducto }

        if (existingItem != null) {
            if (existingItem.incrementarCantidad()) {
            } else {
                return
            }
        } else {
            currentList.add(item)
        }

        _carritoItems.value = currentList
        calcularTotal()
    }

    fun actualizarCantidad(idProducto: Int, nuevaCantidad: Int) {
        val currentList = _carritoItems.value ?: return
        val item = currentList.find { it.idProducto == idProducto }
        item?.let {
            it.cantidad = nuevaCantidad
            _carritoItems.value = currentList
            calcularTotal()
        }
    }

    fun eliminarDelCarrito(idProducto: Int) {
        val currentList = _carritoItems.value ?: return
        currentList.removeAll { it.idProducto == idProducto }
        _carritoItems.value = currentList
        calcularTotal()
    }

    fun limpiarCarrito() {
        _carritoItems.value = mutableListOf()
        _totalCarrito.value = 0.0
    }

    private fun calcularTotal() {
        val total = _carritoItems.value?.sumOf { it.precio * it.cantidad } ?: 0.0
        _totalCarrito.value = total
    }

    fun getCantidadTotal(): Int {
        return _carritoItems.value?.sumOf { it.cantidad } ?: 0
    }
}