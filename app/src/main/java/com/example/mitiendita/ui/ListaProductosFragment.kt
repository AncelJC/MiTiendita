package com.example.mitiendita.ui

import com.example.mitiendita.adapter.ProductoAdapter
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.mitiendita.R
import com.example.mitiendita.database.CategoriaDAO // Asumiendo que existen
import com.example.mitiendita.database.DetalleProductoDAO // Asumiendo que existen
import com.example.mitiendita.databinding.FragmentListaProductosBinding
import com.example.mitiendita.entity.Producto
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

// 1. Implementa la interfaz OnItemActionListener
class ListaProductosFragment : Fragment(R.layout.fragment_lista_productos), ProductoAdapter.OnItemActionListener {

    private var _binding: FragmentListaProductosBinding? = null
    private val binding get() = _binding!!

    private lateinit var productoAdapter: ProductoAdapter

    // Lista completa, ya no mutable (usada como fuente de datos)
    private var productosOriginales: List<Producto> = emptyList()

    private lateinit var detalleProductoDAO: DetalleProductoDAO
    private lateinit var categoriaDAO: CategoriaDAO
    private val categoriasMap = mutableMapOf<String, Int>() // Nombre -> ID

    // Estado del botón de filtro de Stock
    private var estaFiltrandoPorStockBajo: Boolean = false
    private val UMBRAL_STOCK_BAJO = 10

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
        // 2. Inicializar el Adapter, pasándole 'this' como listener
        productoAdapter = ProductoAdapter(this)
        binding.rvProductos.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = productoAdapter
        }
    }

    private fun cargarDatosIniciales() {
        cargarCategoriasSpinner()
        cargarProductos()
    }

    private fun configurarEventos() {
        binding.fabAgregarProducto.setOnClickListener {
            Toast.makeText(requireContext(), "Navegar a Agregar Producto", Toast.LENGTH_SHORT).show()
            // findNavController().navigate(R.id.action_listaProductos_to_crearProducto)
        }

        // 3. Listener de búsqueda en tiempo real
        binding.etBuscarProducto.doAfterTextChanged {
            aplicarFiltros()
        }

        // 4. Listener del Spinner de categoría
        binding.spnFiltroCategoria.onItemSelectedListener = object : android.widget.AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: android.widget.AdapterView<*>?, view: View?, position: Int, id: Long) {
                aplicarFiltros()
            }
            override fun onNothingSelected(parent: android.widget.AdapterView<*>?) {}
        }

        // 5. Listener del botón de stock (alterna el estado)
        binding.btnFiltrarStock.setOnClickListener {
            estaFiltrandoPorStockBajo = !estaFiltrandoPorStockBajo
            binding.btnFiltrarStock.text = if (estaFiltrandoPorStockBajo) "Ver Todos" else "Stock Bajo"
            aplicarFiltros()
        }
    }

    // --- LÓGICA DE DATOS Y FILTROS ---

    private fun cargarCategoriasSpinner() {
        lifecycleScope.launch(Dispatchers.IO) {
            val categoriasConId = categoriaDAO.obtenerCategoriasConId() // Asumiendo List<Pair<Int, String>>
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
                productosOriginales = productos // Actualiza la lista original
                actualizarEstadisticas(productos)
                aplicarFiltros()
            }
        }
    }

    private fun aplicarFiltros() {
        val busqueda = binding.etBuscarProducto.text.toString().trim().lowercase()
        val categoriaSeleccionadaNombre = binding.spnFiltroCategoria.selectedItem as? String ?: "Todas las categorías"
        val categoriaSeleccionadaId = categoriasMap[categoriaSeleccionadaNombre] ?: 0

        val listaFiltrada = productosOriginales.filter { producto ->
            // 1. Filtro de búsqueda (nombre, descripción, código)
            val coincideBusqueda = busqueda.isEmpty() ||
                    producto.nombre.lowercase().contains(busqueda) ||
                    producto.descripcion?.lowercase()?.contains(busqueda) == true ||
                    producto.codigo.lowercase().contains(busqueda)

            // 2. Filtro de categoría
            val coincideCategoria = categoriaSeleccionadaId == 0 || producto.idCat == categoriaSeleccionadaId

            // 3. Filtro de stock bajo (usa el estado del botón)
            val coincideStock = if (estaFiltrandoPorStockBajo) {
                producto.stock > 0 && producto.stock <= UMBRAL_STOCK_BAJO
            } else {
                true // Si no está filtrando, coincide con todos
            }

            coincideBusqueda && coincideCategoria && coincideStock
        }

        // 6. CAMBIO CRUCIAL: Usar submitList()
        productoAdapter.submitList(listaFiltrada)

        binding.tvMensajeVacio.visibility = if (listaFiltrada.isEmpty()) View.VISIBLE else View.GONE
        binding.rvProductos.visibility = if (listaFiltrada.isEmpty()) View.GONE else View.VISIBLE
    }

    private fun actualizarEstadisticas(productos: List<Producto>) {
        val total = productos.size
        val stockBajo = productos.count { it.stock > 0 && it.stock <= UMBRAL_STOCK_BAJO }
        val sinStock = productos.count { it.stock == 0 }

        binding.tvTotalProductos.text = total.toString()
        binding.tvStockBajo.text = stockBajo.toString()
        binding.tvSinStock.text = sinStock.toString()
    }

    // --- IMPLEMENTACIÓN DE LA INTERFAZ ProductoAdapter.OnItemActionListener ---

    override fun onItemClick(producto: Producto) {
        mostrarDetallesProducto(producto)
    }

    override fun onEditClick(producto: Producto) {
        Toast.makeText(requireContext(), "Abrir Edición de ${producto.nombre}", Toast.LENGTH_SHORT).show()
        // findNavController().navigate(R.id.action_listaProductos_to_editarProducto, bundle)
    }

    override fun onDeleteClick(producto: Producto) {
        mostrarDialogoEliminar(producto)
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
            // Usando 'idProducto' corregido
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