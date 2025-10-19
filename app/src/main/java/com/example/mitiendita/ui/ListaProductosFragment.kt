package com.example.mitiendita.ui

import adapter.ProductoAdapter
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.mitiendita.R
import com.example.mitiendita.database.CategoriaDAO
import com.example.mitiendita.database.DetalleProductoDAO
import com.example.mitiendita.databinding.FragmentListaProductosBinding // Asumiendo este nombre
import com.example.mitiendita.entity.Producto
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ListaProductosFragment : Fragment(R.layout.fragment_lista_productos) {

    private var _binding: FragmentListaProductosBinding? = null
    private val binding get() = _binding!!

    private lateinit var productoAdapter: ProductoAdapter
    private val productosOriginales = mutableListOf<Producto>() // Lista completa
    private val productosFiltrados = mutableListOf<Producto>()  // Lista mostrada en el RecyclerView

    private lateinit var detalleProductoDAO: DetalleProductoDAO
    private lateinit var categoriaDAO: CategoriaDAO
    private val categoriasMap = mutableMapOf<String, Int>() // Nombre -> ID

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentListaProductosBinding.bind(view)

        detalleProductoDAO = DetalleProductoDAO(requireContext())
        categoriaDAO = CategoriaDAO(requireContext())

        inicializarRecyclerView()
        cargarDatosIniciales()
        configurarEventos()
    }

    private fun inicializarRecyclerView() {
        productoAdapter = ProductoAdapter(productosFiltrados)
        binding.rvProductos.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = productoAdapter
        }

        productoAdapter.setOnItemActionListener(object : ProductoAdapter.OnItemActionListener {
            override fun onItemClick(producto: Producto) {
                mostrarDetallesProducto(producto)
            }
            override fun onEditClick(producto: Producto) {
                Toast.makeText(requireContext(), "Abrir Edición de ${producto.nombre}", Toast.LENGTH_SHORT).show()
                // Implementar navegación a ProductosFragment con los datos del producto
            }
            override fun onDeleteClick(producto: Producto) {
                mostrarDialogoEliminar(producto)
            }
        })
    }

    private fun cargarDatosIniciales() {
        // Cargar Categorías para el Spinner
        cargarCategoriasSpinner()

        // Cargar Productos
        cargarProductos()
    }

    private fun configurarEventos() {
        // Implementar listeners para la búsqueda, el Spinner y el botón de stock
        binding.fabAgregarProducto.setOnClickListener {
            Toast.makeText(requireContext(), "Navegar a Agregar Producto", Toast.LENGTH_SHORT).show()
            // Implementar navegación al ProductosFragment
        }

        // Lógica de búsqueda (ej. al presionar ENTER)
        binding.etBuscarProducto.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == android.view.inputmethod.EditorInfo.IME_ACTION_SEARCH) {
                aplicarFiltros()
                true
            } else {
                false
            }
        }

        // Lógica del Spinner (se activa al seleccionar un elemento)
        // binding.spnFiltroCategoria.onItemSelectedListener = ...

        // Lógica del botón de stock
        binding.btnFiltrarStock.setOnClickListener {
            aplicarFiltros(filtrarStockBajo = true)
        }
    }

    // --- LÓGICA DE DATOS Y FILTROS ---

    private fun cargarCategoriasSpinner() {
        lifecycleScope.launch(Dispatchers.IO) {
            val categoriasConId = categoriaDAO.obtenerCategoriasConId() // Asumiendo este método
            withContext(Dispatchers.Main) {
                val nombres = mutableListOf("Todas las categorías")
                categoriasMap.clear()
                categoriasMap["Todas las categorías"] = 0

                categoriasConId.forEach { (id, nombre) ->
                    categoriasMap[nombre] = id
                    nombres.add(nombre)
                }

                val adapter = ArrayAdapter(
                    requireContext(),
                    android.R.layout.simple_spinner_dropdown_item,
                    nombres
                )
                binding.spnFiltroCategoria.adapter = adapter
            }
        }
    }

    private fun cargarProductos() {
        binding.tvMensajeVacio.visibility = View.GONE
        lifecycleScope.launch(Dispatchers.IO) {
            val productos = detalleProductoDAO.obtenerTodosLosProductosConCategoria()

            withContext(Dispatchers.Main) {
                productosOriginales.clear()
                productosOriginales.addAll(productos)

                actualizarEstadisticas(productos)
                aplicarFiltros() // Mostrar todos los productos por defecto
            }
        }
    }

    private fun aplicarFiltros(filtrarStockBajo: Boolean = false) {
        val busqueda = binding.etBuscarProducto.text.toString().trim().lowercase()
        // Obtener ID de la categoría seleccionada del Spinner
        // val categoriaSeleccionadaId = categoriasMap[binding.spnFiltroCategoria.selectedItem as String] ?: 0

        val listaFiltrada = productosOriginales.filter { producto ->
            // 1. Filtro de búsqueda por nombre/descripción
            val coincideBusqueda = busqueda.isEmpty() ||
                    producto.nombre.lowercase().contains(busqueda) ||
                    producto.descripcion?.lowercase()?.contains(busqueda) == true

            // 2. Filtro de categoría (si es necesario)
            // val coincideCategoria = categoriaSeleccionadaId == 0 || producto.idCat == categoriaSeleccionadaId

            // 3. Filtro de stock bajo
            val coincideStock = if (filtrarStockBajo) producto.stock <= 10 else true

            // Combine los filtros (ejemplo solo con búsqueda y stock bajo)
            coincideBusqueda && coincideStock // && coincideCategoria
        }

        productosFiltrados.clear()
        productosFiltrados.addAll(listaFiltrada)
        productoAdapter.notifyDataSetChanged()

        // Mostrar mensaje si la lista filtrada está vacía
        binding.tvMensajeVacio.visibility = if (productosFiltrados.isEmpty()) View.VISIBLE else View.GONE
        binding.rvProductos.visibility = if (productosFiltrados.isEmpty()) View.GONE else View.VISIBLE
    }

    private fun actualizarEstadisticas(productos: List<Producto>) {
        val total = productos.size
        val stockBajo = productos.count { it.stock > 0 && it.stock <= 10 }
        val sinStock = productos.count { it.stock == 0 }

        binding.tvTotalProductos.text = total.toString()
        binding.tvStockBajo.text = stockBajo.toString()
        binding.tvSinStock.text = sinStock.toString()
    }

    // --- LÓGICA DE INTERACCIÓN ---

    private fun mostrarDetallesProducto(producto: Producto) {
        AlertDialog.Builder(requireContext())
            .setTitle("Detalles de ${producto.nombre}")
            .setMessage("Precio: S/ ${String.format("%.2f", producto.precio)}\nStock: ${producto.stock}\nCategoría: ${producto.nombreCategoria}\nDescripción: ${producto.descripcion ?: "N/A"}")
            .setPositiveButton("Aceptar", null)
            .show()
    }

    private fun mostrarDialogoEliminar(producto: Producto) {
        AlertDialog.Builder(requireContext())
            .setTitle("Confirmar Eliminación")
            .setMessage("¿Estás seguro de eliminar el producto '${producto.nombre}'?")
            .setPositiveButton("Eliminar") { _, _ ->
                eliminarProducto(producto)
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    private fun eliminarProducto(producto: Producto) {
        lifecycleScope.launch(Dispatchers.IO) {
            // Asumiendo que tienes una función 'eliminarProducto' en tu ProductoDAO
            val filasAfectadas = producto.idProd.let { detalleProductoDAO.eliminarProducto(it) }

            withContext(Dispatchers.Main) {
                if (filasAfectadas > 0) {
                    Toast.makeText(requireContext(), "✅ Producto '${producto.nombre}' eliminado.", Toast.LENGTH_SHORT).show()
                    cargarProductos() // Recargar la lista
                } else {
                    Toast.makeText(requireContext(), "❌ Error al eliminar el producto.", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}