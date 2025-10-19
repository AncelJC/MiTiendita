package com.example.mitiendita.ui

import adapter.ProductoAdapter
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.mitiendita.R
import com.example.mitiendita.database.DetalleProductoDAO // ⬅️ Nuevo: DAO de lectura
import com.example.mitiendita.database.CategoriaDAO // ⬅️ Nuevo: DAO para obtener nombres de categoría
import com.example.mitiendita.entity.Producto
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ListaProductosFragment : Fragment(R.layout.fragment_lista_productos) {

    private lateinit var rvHistorial: RecyclerView
    private lateinit var ProductoAdapter: ProductoAdapter

    // 🔴 Inicializada en onCreate para evitar el error lateinit
    private lateinit var listaProducto: MutableList<Producto>

    // 🔴 Inicialización de DAOs
    private lateinit var detalleProductoDAO: DetalleProductoDAO
    private lateinit var categoriaDAO: CategoriaDAO


    // 🔴 CORRECCIÓN: Usamos el método correcto para inicializar variables no-vista
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Inicialización de la lista de datos
        listaProducto = mutableListOf()

        // Inicialización de DAOs
        detalleProductoDAO = DetalleProductoDAO(requireContext())
        categoriaDAO = CategoriaDAO(requireContext())
    }

    // 🔴 CORRECCIÓN CLAVE: Usamos el override correcto (sin el super.onCreate(view, savedInstanceState) )
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Inicialización de Vistas y Adapter
        rvHistorial = view.findViewById(R.id.rvHistorial)
        rvHistorial.layoutManager = LinearLayoutManager(requireContext())

        // El adaptador usa la lista inicializada
        ProductoAdapter = ProductoAdapter(listaProducto)
        rvHistorial.adapter = ProductoAdapter

        // Listener para el clic
        ProductoAdapter.setOnItemClickListener { productoSeleccionado ->
            // Usamos el idProd correcto de la entidad Producto
            mostrarDetalleProducto(productoSeleccionado.idProd)
        }

        // Cargar datos
        cargarProductos()
    }

    // ===================================
    // LÓGICA DE CARGA DE DATOS (CON DAO)
    // ===================================

    private fun cargarProductos() {
        // Usamos Corrutinas para no bloquear la UI
        lifecycleScope.launch(Dispatchers.IO) {
            try {
                // 🔴 USANDO DAO: Llamamos a la función con JOIN para obtener todos los productos
                val productos = detalleProductoDAO.obtenerTodosLosProductosConCategoria()

                withContext(Dispatchers.Main) {
                    // Limpiamos y añadimos los nuevos datos
                    listaProducto.clear()
                    listaProducto.addAll(productos)
                    ProductoAdapter.notifyDataSetChanged()

                    if (productos.isEmpty()) {
                        Toast.makeText(requireContext(), "No hay productos registrados.", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                Log.e("ListaProdFragment", "Error al cargar productos: ${e.message}", e)
                withContext(Dispatchers.Main) {
                    Toast.makeText(requireContext(), "Error al cargar productos.", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    // =====================================
    // LÓGICA PARA MOSTRAR DETALLE (CON DAO)
    // =====================================

    private fun mostrarDetalleProducto(idProd: Int) {
        lifecycleScope.launch(Dispatchers.IO) {
            try {
                // 🔴 USANDO DAO: Obtenemos el producto por ID (con nombre de categoría incluido)
                val producto = detalleProductoDAO.obtenerProductoPorId(idProd)

                withContext(Dispatchers.Main) {
                    if (producto != null) {
                        // Construir el mensaje de detalle
                        val detalleTexto = StringBuilder()
                        detalleTexto.append("Nombre: ${producto.nombre}\n")
                        detalleTexto.append("Descripción: ${producto.descripcion ?: "N/A"}\n")
                        detalleTexto.append("Precio: $${String.format("%.2f", producto.precio)}\n")
                        detalleTexto.append("Stock: ${producto.stock}\n")
                        detalleTexto.append("Categoría: ${producto.nombreCategoria}\n")

                        // Mostrar el AlertDialog
                        AlertDialog.Builder(requireContext())
                            .setTitle("Detalles del Producto")
                            .setMessage(detalleTexto.toString())
                            .setPositiveButton("Aceptar", null)
                            .show()

                    } else {
                        Toast.makeText(requireContext(), "Producto no encontrado.", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                Log.e("ListaProdFragment", "Error al mostrar detalle: ${e.message}", e)
                withContext(Dispatchers.Main) {
                    Toast.makeText(requireContext(), "Error al obtener detalles.", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    // 🔴 NOTA: La lógica en tu código original para el `else` de `mostrarDetalleLista`
    //          era errónea ya que el `if (detalles.isNotEmpty())` manejaba el caso
    //          donde el producto EXISTE. El bloque `else` solo se ejecuta si el producto NO existe.
    //          He simplificado la lógica para mostrar el detalle del producto encontrado.
}