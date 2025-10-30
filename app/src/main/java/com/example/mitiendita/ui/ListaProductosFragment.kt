package com.example.mitiendita.ui

import adapter.ProductoAdminAdapter
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.mitiendita.R
import com.example.mitiendita.dao.ProductoDAO
import com.example.mitiendita.entity.Producto
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText

class ListaProductosFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var tvVacio: TextView
    private lateinit var tvContador: TextView
    private lateinit var etBuscar: TextInputEditText
    private lateinit var btnTodos: MaterialButton
    private lateinit var btnActivos: MaterialButton
    private lateinit var btnInactivos: MaterialButton

    private lateinit var productoAdapter: ProductoAdminAdapter
    private lateinit var productoDAO: ProductoDAO
    private var listaProductosCompleta = mutableListOf<Producto>()
    private var listaProductosFiltrada = mutableListOf<Producto>()

    private var filtroEstado: String = "TODOS" // TODOS, ACTIVOS, INACTIVOS

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_lista_productos, container, false)
        initViews(view)
        setupListeners()
        cargarProductos()
        return view
    }

    override fun onResume() {
        super.onResume()
        cargarProductos() // Recargar cuando el fragment vuelva a ser visible
    }

    private fun initViews(view: View) {
        recyclerView = view.findViewById(R.id.recyclerViewProductos)
        tvVacio = view.findViewById(R.id.tvListaVacia)
        tvContador = view.findViewById(R.id.tvContador)
        etBuscar = view.findViewById(R.id.etBuscar)
        btnTodos = view.findViewById(R.id.btnTodos)
        btnActivos = view.findViewById(R.id.btnActivos)
        btnInactivos = view.findViewById(R.id.btnInactivos)

        productoDAO = ProductoDAO(requireContext())
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
    }

    private fun setupListeners() {
        // Búsqueda en tiempo real
        etBuscar.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                filtrarProductos()
            }
        })

        // Filtros por estado
        btnTodos.setOnClickListener { aplicarFiltroEstado("TODOS") }
        btnActivos.setOnClickListener { aplicarFiltroEstado("ACTIVOS") }
        btnInactivos.setOnClickListener { aplicarFiltroEstado("INACTIVOS") }
    }

    private fun cargarProductos() {
        listaProductosCompleta = productoDAO.obtenerTodosLosProductosAdmi().toMutableList()
        aplicarFiltroEstado("TODOS")
    }

    private fun aplicarFiltroEstado(estado: String) {
        filtroEstado = estado
        actualizarBotonesFiltro()
        filtrarProductos()
    }

    private fun actualizarBotonesFiltro() {
        val colorPrimario = ContextCompat.getColor(requireContext(), R.color.colorPrimary)
        val colorBlanco = ContextCompat.getColor(requireContext(), R.color.white)
        val colorGris = ContextCompat.getColor(requireContext(), R.color.gray)

        // Resetear todos los botones
        listOf(btnTodos, btnActivos, btnInactivos).forEach {
            it.setBackgroundColor(colorGris)
            it.setTextColor(colorBlanco)
        }

        // Activar el botón seleccionado
        when (filtroEstado) {
            "TODOS" -> btnTodos.setBackgroundColor(colorPrimario)
            "ACTIVOS" -> btnActivos.setBackgroundColor(colorPrimario)
            "INACTIVOS" -> btnInactivos.setBackgroundColor(colorPrimario)
        }
    }

    private fun filtrarProductos() {
        val textoBusqueda = etBuscar.text.toString().trim().lowercase()

        listaProductosFiltrada = listaProductosCompleta.filter { producto ->
            val cumpleEstado = when (filtroEstado) {
                "ACTIVOS" -> producto.activo
                "INACTIVOS" -> !producto.activo
                else -> true
            }

            val cumpleBusqueda = textoBusqueda.isEmpty() ||
                    producto.nombre.lowercase().contains(textoBusqueda) ||
                    producto.descripcion?.lowercase()?.contains(textoBusqueda) == true ||
                    producto.nombreCategoria?.lowercase()?.contains(textoBusqueda) == true

            cumpleEstado && cumpleBusqueda
        }.toMutableList()

        mostrarProductos()
    }

    private fun mostrarProductos() {
        if (listaProductosFiltrada.isEmpty()) {
            tvVacio.visibility = View.VISIBLE
            recyclerView.visibility = View.GONE
        } else {
            tvVacio.visibility = View.GONE
            recyclerView.visibility = View.VISIBLE

            productoAdapter = ProductoAdminAdapter(
                productos = listaProductosFiltrada,
                onEditar = { producto -> mostrarDialogoEditar(producto) },
                onEliminar = { producto -> mostrarDialogoEliminar(producto) },
                onCambiarEstado = { producto, nuevoEstado -> cambiarEstadoProducto(producto, nuevoEstado) }
            )
            recyclerView.adapter = productoAdapter
        }

        tvContador.text = "${listaProductosFiltrada.size} productos"
    }

    private fun mostrarDialogoEditar(producto: Producto) {
        val fragment = ProductosFragment().apply {
            arguments = Bundle().apply {
                putSerializable("producto", producto)
            }
        }

        // Usar el método público para cargar el producto en el formulario
        fragment.cargarProductoParaEditar(producto)

        parentFragmentManager.beginTransaction()
            .replace(R.id.contenedorFragment, fragment)
            .addToBackStack("editar_producto")
            .commit()
    }

    private fun mostrarDialogoEliminar(producto: Producto) {
        android.app.AlertDialog.Builder(requireContext())
            .setTitle("Eliminar Producto")
            .setMessage("¿Estás seguro de eliminar \"${producto.nombre}\"?")
            .setPositiveButton("Eliminar") { _, _ ->
                eliminarProducto(producto)
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    private fun eliminarProducto(producto: Producto) {
        val resultado = productoDAO.eliminarProduct(producto.idProd)
        if (resultado > 0) {
            listaProductosCompleta.removeAll { it.idProd == producto.idProd }
            listaProductosFiltrada.removeAll { it.idProd == producto.idProd }

            productoAdapter.actualizarLista(listaProductosFiltrada)
            actualizarVistaVacia()
            android.widget.Toast.makeText(requireContext(), "Producto eliminado", android.widget.Toast.LENGTH_SHORT).show()
        } else {
            android.widget.Toast.makeText(requireContext(), "Error al eliminar producto", android.widget.Toast.LENGTH_SHORT).show()
        }
    }

    private fun cambiarEstadoProducto(producto: Producto, nuevoEstado: Boolean) {
        val resultado = productoDAO.cambiarEstadoProduct(producto.idProd, nuevoEstado)
        if (resultado) {
            val indexCompleta = listaProductosCompleta.indexOfFirst { it.idProd == producto.idProd }
            val indexFiltrada = listaProductosFiltrada.indexOfFirst { it.idProd == producto.idProd }

            if (indexCompleta != -1) {
                listaProductosCompleta[indexCompleta] = producto.copy(activo = nuevoEstado)
            }
            if (indexFiltrada != -1) {
                listaProductosFiltrada[indexFiltrada] = producto.copy(activo = nuevoEstado)
            }

            productoAdapter.actualizarLista(listaProductosFiltrada)
            val estado = if (nuevoEstado) "habilitado" else "deshabilitado"
            android.widget.Toast.makeText(requireContext(), "Producto $estado", android.widget.Toast.LENGTH_SHORT).show()
        } else {
            android.widget.Toast.makeText(requireContext(), "Error al cambiar estado", android.widget.Toast.LENGTH_SHORT).show()
        }
    }

    private fun actualizarVistaVacia() {
        if (listaProductosFiltrada.isEmpty()) {
            tvVacio.visibility = View.VISIBLE
            recyclerView.visibility = View.GONE
        } else {
            tvVacio.visibility = View.GONE
            recyclerView.visibility = View.VISIBLE
        }
    }
}