// InicioFragment.kt
package com.example.mitiendita.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.mitiendita.R
import com.example.mitiendita.dao.ProductoDAO
import com.example.mitiendita.entity.CarritoItem
import com.example.mitiendita.entity.Producto
import com.example.mitiendita.viewmodel.CarritoViewModel
import com.google.android.material.snackbar.Snackbar
import adapter.ProductoClienteAdapter

class InicioFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var tvVacio: TextView
    private lateinit var productoAdapter: ProductoClienteAdapter
    private lateinit var productoDAO: ProductoDAO
    private var listaProductos = mutableListOf<Producto>()
    private val carritoViewModel: CarritoViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_inicio, container, false)
        initViews(view)
        cargarProductos()
        return view
    }

    private fun initViews(view: View) {
        recyclerView = view.findViewById(R.id.recyclerViewProductos)
        tvVacio = view.findViewById(R.id.tvListaVacia)

        productoDAO = ProductoDAO(requireContext())
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        productoAdapter = ProductoClienteAdapter(listaProductos)
        productoAdapter.setOnAgregarCarritoListener { producto ->
            agregarAlCarrito(producto)
        }
        recyclerView.adapter = productoAdapter
    }

    private fun cargarProductos() {
        listaProductos = productoDAO.obtenerProductosActivos().toMutableList()
        mostrarProductos()
    }

    private fun mostrarProductos() {
        if (listaProductos.isEmpty()) {
            tvVacio.visibility = View.VISIBLE
            recyclerView.visibility = View.GONE
        } else {
            tvVacio.visibility = View.GONE
            recyclerView.visibility = View.VISIBLE
            productoAdapter.updateData(listaProductos)
        }
    }

    private fun agregarAlCarrito(producto: Producto) {
        val carritoItem = CarritoItem(
            idProducto = producto.idProd,
            nombre = producto.nombre,
            precio = producto.precio,
            cantidad = 1,
            imagen = producto.imagen,
            stock = producto.stock,
            unidadMedida = producto.unidadMedida
        )

        carritoViewModel.agregarAlCarrito(carritoItem)
        Snackbar.make(requireView(), "Agregado al carrito: ${producto.nombre}", Snackbar.LENGTH_SHORT).show()
    }


}