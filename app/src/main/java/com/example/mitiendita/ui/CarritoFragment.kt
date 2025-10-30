// CarritoFragment.kt
package com.example.mitiendita.ui

import adapter.CarritoAdapter
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.mitiendita.R
import com.example.mitiendita.entity.CarritoItem
import com.example.mitiendita.viewmodel.CarritoViewModel
import com.google.android.material.snackbar.Snackbar

class CarritoFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var tvTotal: TextView
    private lateinit var tvCarritoVacio: TextView
    private lateinit var btnFinalizarCompra: Button
    private lateinit var btnSeguirComprando: Button

    private lateinit var carritoAdapter: CarritoAdapter

    // Usar el mismo ViewModel que InicioFragment
    private val carritoViewModel: CarritoViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_carrito, container, false)
        initViews(view)
        setupRecyclerView()
        setupListeners()
        setupObservers()
        return view
    }

    private fun initViews(view: View) {
        recyclerView = view.findViewById(R.id.recyclerViewCarrito)
        tvTotal = view.findViewById(R.id.tvTotalCarrito)
        tvCarritoVacio = view.findViewById(R.id.tvCarritoVacio)
        btnFinalizarCompra = view.findViewById(R.id.btnFinalizarCompra)
        btnSeguirComprando = view.findViewById(R.id.btnSeguirComprando)
    }

    private fun setupRecyclerView() {
        carritoAdapter = CarritoAdapter(
            mutableListOf(), // Inicialmente vacío, se llenará con el Observer
            onCantidadChanged = { position, nuevaCantidad ->
                val item = carritoAdapter.getCarritoItems()[position]
                carritoViewModel.actualizarCantidad(item.idProducto, nuevaCantidad)
            },
            onItemRemoved = { position ->
                val item = carritoAdapter.getCarritoItems()[position]
                carritoViewModel.eliminarDelCarrito(item.idProducto)
                Snackbar.make(requireView(), "${item.nombre} eliminado", Snackbar.LENGTH_SHORT).show()
            }
        )

        recyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = carritoAdapter
        }
    }

    private fun setupObservers() {
        // Observar cambios en los items del carrito
        carritoViewModel.carritoItems.observe(viewLifecycleOwner, Observer { items ->
            carritoAdapter.actualizarLista(items.toMutableList())
            actualizarVistaCarrito()
        })

        // Observar cambios en el total
        carritoViewModel.totalCarrito.observe(viewLifecycleOwner, Observer { total ->
            tvTotal.text = "Total: S/ ${String.format("%.2f", total)}"
        })
    }

    private fun setupListeners() {
        btnFinalizarCompra.setOnClickListener {
            finalizarCompra()
        }

        btnSeguirComprando.setOnClickListener {
            requireActivity().supportFragmentManager.popBackStack()
        }
    }

    private fun finalizarCompra() {
        val items = carritoAdapter.getCarritoItems()

        if (items.isEmpty()) {
            mostrarMensaje("El carrito está vacío")
            return
        }

        // Validar stock
        val productosSinStock = items.filter { it.cantidad > it.stock }
        if (productosSinStock.isNotEmpty()) {
            val nombresProductos = productosSinStock.joinToString { it.nombre }
            mostrarMensaje("Stock insuficiente para: $nombresProductos")
            return
        }

        mostrarDialogoConfirmacionCompra()
    }

    private fun mostrarDialogoConfirmacionCompra() {
        val total = carritoViewModel.totalCarrito.value ?: 0.0

        android.app.AlertDialog.Builder(requireContext())
            .setTitle("Confirmar Compra")
            .setMessage("¿Estás seguro de que quieres finalizar la compra?\n\nTotal: S/ ${String.format("%.2f", total)}")
            .setPositiveButton("Confirmar") { _, _ ->
                procesarVenta()
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    private fun procesarVenta() {
        val total = carritoViewModel.totalCarrito.value ?: 0.0
        val idCompra = (1000..9999).random()

        mostrarComprobanteVenta(idCompra, total)
        carritoViewModel.limpiarCarrito()
    }

    private fun actualizarVistaCarrito() {
        val tieneItems = carritoAdapter.itemCount > 0

        tvCarritoVacio.visibility = if (tieneItems) View.GONE else View.VISIBLE
        recyclerView.visibility = if (tieneItems) View.VISIBLE else View.GONE
        btnFinalizarCompra.visibility = if (tieneItems) View.VISIBLE else View.GONE
    }

    private fun mostrarComprobanteVenta(idCompra: Int, total: Double) {
        android.app.AlertDialog.Builder(requireContext())
            .setTitle("¡Compra Exitosa!")
            .setMessage("N° de Compra: #$idCompra\nTotal: S/ ${String.format("%.2f", total)}\n\nGracias por tu compra en MiTiendita")
            .setPositiveButton("Aceptar") { _, _ ->
                requireActivity().supportFragmentManager.popBackStack()
            }
            .setCancelable(false)
            .show()
    }

    private fun mostrarMensaje(mensaje: String) {
        Snackbar.make(requireView(), mensaje, Snackbar.LENGTH_SHORT).show()
    }
}