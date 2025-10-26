package com.example.mitiendita.ui

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.mitiendita.R
import com.example.mitiendita.database.CategoriaDAO // ⬅️ CAMBIO: Importar el DAO de Categorías
import com.example.mitiendita.database.ProductoDAO
import com.example.mitiendita.databinding.FragmentProductosBinding
import com.example.mitiendita.entity.Producto
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ProductosFragment : Fragment() {

    private var _binding: FragmentProductosBinding? = null
    private val binding get() = _binding!!

    // Usamos CategoriaDAO para la lectura de categorías
    private lateinit var categoriaDAO: CategoriaDAO
    // Usamos ProductoDAO para la escritura de productos
    private lateinit var productoDAO: ProductoDAO
    // Mapa para mantener el ID de cada categoría
    private var imagenSeleccionada: String? = null
    private val categoriasMap = mutableMapOf<String, Int>() // Mapa: Nombre -> ID

    // Contract para seleccionar imagen
    private val seleccionarImagen = registerForActivityResult( // ⬅️ Usamos el contrato
        ActivityResultContracts.StartActivityForResult() // ⬅️ Usamos el contrato
    ) { result -> // ⬅️ Usamos el contrato
        if (result.resultCode == Activity.RESULT_OK) { // ⬅️ Usamos el contrato
            result.data?.data?.let { uri ->
                val contentResolver = requireContext().contentResolver
                val takeFlags: Int = Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION
                contentResolver.takePersistableUriPermission(uri, takeFlags)

                imagenSeleccionada = uri.toString()
                binding.ivProducto.setImageURI(uri)
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProductosBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 🔴 Inicialización de DAOs
        categoriaDAO = CategoriaDAO(requireContext()) // ⬅️ Usamos CategoriaDAO
        productoDAO = ProductoDAO(requireContext())

        inicializarSpinnerCategorias()
        configurarEventos()
    }

    private fun inicializarSpinnerCategorias() {
        lifecycleScope.launch {
            try {
                // 🔴 CORREGIDO: Usamos categoriaDAO.obtenerCategoriasConId()
                val categoriasConId = withContext(Dispatchers.IO) {
                    categoriaDAO.obtenerCategoriasConId()
                }

                withContext(Dispatchers.Main) {
                    if (categoriasConId.isNotEmpty()) {
                        val nombresCategorias = mutableListOf<String>()

                        // Llenar el mapa y la lista de nombres para el Spinner
                        categoriasConId.forEach { (id, nombre) ->
                            categoriasMap[nombre] = id
                            nombresCategorias.add(nombre)
                        }

                        val adapter = ArrayAdapter(
                            requireContext(),
                            android.R.layout.simple_spinner_item,
                            nombresCategorias
                        ).apply {
                            setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                        }

                        binding.spnCategoria.adapter = adapter
                    } else {
                        mostrarMensaje("No hay categorías disponibles. Crea una categoría primero.")
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    mostrarMensaje("Error al cargar categorías: ${e.message}")
                }
            }
        }
    }

    private fun configurarEventos() {
        binding.btnSeleccionarImagen.setOnClickListener {
            seleccionarImagenDesdeGaleria()
        }

        binding.btnGuardarProducto.setOnClickListener {
            validarYGuardarProducto()
        }
    }

    private fun seleccionarImagenDesdeGaleria() {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
            type = "image/*"
            addCategory(Intent.CATEGORY_OPENABLE)
            addFlags(Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }

        val chooser = Intent.createChooser(intent, "Seleccionar imagen del producto")
        seleccionarImagen.launch(chooser)
    }

    private fun validarYGuardarProducto() {
        val nombre = binding.tietNombreProd.text.toString().trim()
        val descripcion = binding.tietDescripcionProd.text.toString().trim()
        val precioTexto = binding.tietPrecioProd.text.toString().trim()
        val stockTexto = binding.tietStockProd.text.toString().trim()
        val categoriaSeleccionada = binding.spnCategoria.selectedItem as? String

        // ... (Validaciones)
        if (nombre.isEmpty()) {
            mostrarErrorCampo(binding.tietNombreProd, "El nombre del producto es obligatorio")
            return
        }
        binding.tietNombreProd.error = null

        if (precioTexto.isEmpty()) {
            mostrarErrorCampo(binding.tietPrecioProd, "El precio es obligatorio")
            return
        }

        if (stockTexto.isEmpty()) {
            mostrarErrorCampo(binding.tietStockProd, "El stock es obligatorio")
            return
        }

        if (categoriaSeleccionada == null) {
            mostrarMensaje("Selecciona una categoría")
            return
        }

        val precio = precioTexto.toDoubleOrNull()
        val stock = stockTexto.toIntOrNull()

        if (precio == null || precio <= 0) {
            mostrarErrorCampo(binding.tietPrecioProd, "Ingresa un precio válido")
            return
        }
        binding.tietPrecioProd.error = null

        if (stock == null || stock < 0) {
            mostrarErrorCampo(binding.tietStockProd, "Ingresa un stock válido")
            return
        }
        binding.tietStockProd.error = null


        // Obtener ID de la categoría seleccionada
        val idCategoria = categoriasMap[categoriaSeleccionada] ?: -1
        if (idCategoria == -1) {
            mostrarMensaje("Error: Categoría no válida (ID no encontrado)")
            return
        }

        // Guardar producto
        guardarProducto(nombre, descripcion, precio, stock, idCategoria, imagenSeleccionada)
    }

    private fun guardarProducto(
        nombre: String,
        descripcion: String,
        precio: Double,
        stock: Int,
        idCategoria: Int,
        imagen: String?
    ) {
        val nuevoProducto = Producto(
            nombre = nombre,
            descripcion = descripcion.ifEmpty { null },
            precio = precio,
            stock = stock,
            idCat = idCategoria,
            imagen = imagen,
            nombreCategoria = ""
        )

        lifecycleScope.launch(Dispatchers.IO) {
            try {
                // Usamos el PRODUCTODAO para la operación de escritura
                val id = productoDAO.insertarProducto(nuevoProducto)

                withContext(Dispatchers.Main) {
                    if (id > 0) {
                        mostrarMensaje("✅ Producto '$nombre' guardado exitosamente (ID: $id)")
                        limpiarFormulario()
                    } else {
                        mostrarMensaje("❌ Error al guardar el producto")
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    mostrarMensaje("❌ Error al guardar en BD: ${e.message}")
                }
            }
        }
    }

    private fun limpiarFormulario() {
        binding.tietNombreProd.text?.clear()
        binding.tietDescripcionProd.text?.clear()
        binding.tietPrecioProd.text?.clear()
        binding.tietStockProd.text?.clear()

        binding.ivProducto.setImageResource(R.drawable.ic_box)
        imagenSeleccionada = null

        limpiarErrores()
    }

    private fun limpiarErrores() {
        binding.tietNombreProd.error = null
        binding.tietPrecioProd.error = null
        binding.tietStockProd.error = null
    }

    private fun mostrarErrorCampo(campo: com.google.android.material.textfield.TextInputEditText, mensaje: String) {
        campo.error = mensaje
        campo.requestFocus()
    }

    private fun mostrarMensaje(mensaje: String) {
        Toast.makeText(requireContext(), mensaje, Toast.LENGTH_LONG).show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}