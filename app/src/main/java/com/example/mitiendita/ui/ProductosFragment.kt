package com.example.mitiendita.ui

import android.app.Activity
import android.content.Intent
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
import com.example.mitiendita.dao.ProductoDAO
import com.example.mitiendita.database.CategoriaDAO
import com.example.mitiendita.databinding.FragmentProductosBinding
import com.example.mitiendita.entity.Producto
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ProductosFragment : Fragment() {

    private var _binding: FragmentProductosBinding? = null
    private val binding get() = _binding!!

    private lateinit var categoriaDAO: CategoriaDAO
    private lateinit var productoDAO: ProductoDAO

    private var imagenSeleccionada: String? = null
    private val categoriasMap = mutableMapOf<String, Int>()

    // Producto actual (para edición)
    private var productoActual: Producto? = null

    private val seleccionarImagen = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            result.data?.data?.let { uri ->
                val contentResolver = requireContext().contentResolver
                val takeFlags: Int =
                    Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION
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

        categoriaDAO = CategoriaDAO(requireContext())
        productoDAO = ProductoDAO(requireContext())

        productoActual = arguments?.getSerializable("producto") as? Producto

        inicializarSpinnerCategorias()
        configurarEventos()
    }

    private fun inicializarSpinnerCategorias() {
        lifecycleScope.launch {
            try {
                val categoriasConId = withContext(Dispatchers.IO) {
                    categoriaDAO.obtenerCategoriasConId()
                }

                withContext(Dispatchers.Main) {
                    if (categoriasConId.isNotEmpty()) {
                        val nombresCategorias = mutableListOf<String>()
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

                        productoActual?.let { producto ->
                            cargarProductoParaEditar(producto)
                        }
                    } else {
                        mostrarMensaje("No hay categorías disponibles. Crea una categoría primero.")
                    }
                }
            } catch (e: Exception) {
                mostrarMensaje("Error al cargar categorías: ${e.message}")
            }
        }
    }

    private fun configurarEventos() {
        binding.btnSeleccionarImagen.setOnClickListener {
            seleccionarImagenDesdeGaleria()
        }

        if (productoActual == null) {
            binding.btnGuardarProducto.setOnClickListener {
                validarYGuardarProducto()
            }
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

        if (nombre.isEmpty()) {
            mostrarErrorCampo(binding.tietNombreProd, "El nombre del producto es obligatorio")
            return
        }
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
        if (stock == null || stock < 0) {
            mostrarErrorCampo(binding.tietStockProd, "Ingresa un stock válido")
            return
        }

        val idCategoria = categoriasMap[categoriaSeleccionada] ?: -1
        if (idCategoria == -1) {
            mostrarMensaje("Error: Categoría no válida")
            return
        }

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
            idProd = 0,
            nombre = nombre,
            descripcion = descripcion.ifEmpty { null },
            precio = precio,
            stock = stock,
            imagen = imagen,
            idCat = idCategoria,
            activo = true,
            unidadMedida = "unidad",
            nombreCategoria = ""
        )

        lifecycleScope.launch(Dispatchers.IO) {
            try {
                val id = productoDAO.insertarProducto(nuevoProducto)
                withContext(Dispatchers.Main) {
                    if (id > 0) {
                        mostrarMensaje("✅ Producto '$nombre' guardado exitosamente")
                        limpiarFormulario()
                    } else {
                        mostrarMensaje("❌ Error al guardar el producto")
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    mostrarMensaje("Error al guardar: ${e.message}")
                }
            }
        }
    }

    private fun validarYEditarProducto() {
        val nombre = binding.tietNombreProd.text.toString().trim()
        val descripcion = binding.tietDescripcionProd.text.toString().trim()
        val precioTexto = binding.tietPrecioProd.text.toString().trim()
        val stockTexto = binding.tietStockProd.text.toString().trim()
        val categoriaSeleccionada = binding.spnCategoria.selectedItem as? String

        if (productoActual == null) {
            mostrarMensaje("Error: No se encontró el producto a editar")
            return
        }

        // Validaciones mejoradas
        if (nombre.isEmpty()) {
            mostrarErrorCampo(binding.tietNombreProd, "El nombre del producto es obligatorio")
            return
        }
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
        val idCategoria = categoriasMap[categoriaSeleccionada] ?: -1

        if (precio == null || precio <= 0) {
            mostrarErrorCampo(binding.tietPrecioProd, "Ingresa un precio válido")
            return
        }
        if (stock == null || stock < 0) {
            mostrarErrorCampo(binding.tietStockProd, "Ingresa un stock válido")
            return
        }
        if (idCategoria == -1) {
            mostrarMensaje("Error: Categoría no válida")
            return
        }

        val productoEditado = productoActual!!.copy(
            nombre = nombre,
            descripcion = descripcion.ifEmpty { null },
            precio = precio,
            stock = stock,
            idCat = idCategoria,
            imagen = imagenSeleccionada ?: productoActual!!.imagen
        )

        editarProducto(productoEditado)
    }

    private fun editarProducto(producto: Producto) {
        lifecycleScope.launch(Dispatchers.IO) {
            try {
                val filas = productoDAO.actualizarProducto(producto)
                withContext(Dispatchers.Main) {
                    if (filas > 0) {
                        mostrarMensaje("✅ Producto '${producto.nombre}' actualizado correctamente")
                        limpiarFormulario()
                        // Regresar al listado después de actualizar
                        requireActivity().supportFragmentManager.popBackStack()
                    } else {
                        mostrarMensaje("❌ No se pudo actualizar el producto")
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    mostrarMensaje("Error al actualizar: ${e.message}")
                }
            }
        }
    }

    fun cargarProductoParaEditar(producto: Producto) {
        productoActual = producto

        binding.tietNombreProd.setText(producto.nombre)
        binding.tietDescripcionProd.setText(producto.descripcion ?: "")
        binding.tietPrecioProd.setText(producto.precio.toString())
        binding.tietStockProd.setText(producto.stock.toString())

        val categoriaNombre = producto.nombreCategoria
        if (!categoriaNombre.isNullOrEmpty()) {
            val adapter = binding.spnCategoria.adapter
            if (adapter is ArrayAdapter<*>) {
                val itemsCount = adapter.count
                for (i in 0 until itemsCount) {
                    val item = adapter.getItem(i) as? String
                    if (item == categoriaNombre) {
                        binding.spnCategoria.setSelection(i)
                        break
                    }
                }
            }
        }

        if (!producto.imagen.isNullOrEmpty()) {
            try {
                imagenSeleccionada = producto.imagen
                binding.ivProducto.setImageURI(android.net.Uri.parse(producto.imagen))
            } catch (e: Exception) {
                binding.ivProducto.setImageResource(R.drawable.ic_box)
            }
        } else {
            binding.ivProducto.setImageResource(R.drawable.ic_box)
        }

        cambiarAModoEdicion()
    }

    private fun cambiarAModoEdicion() {
        binding.btnGuardarProducto.text = "Actualizar Producto"
        binding.btnGuardarProducto.setOnClickListener {
            validarYEditarProducto()
        }
    }

    private fun cambiarAModoNuevoProducto() {
        productoActual = null
        binding.btnGuardarProducto.text = "Guardar Producto"
        binding.btnGuardarProducto.setOnClickListener {
            validarYGuardarProducto()
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

        cambiarAModoNuevoProducto()
    }

    private fun limpiarErrores() {
        binding.tietNombreProd.error = null
        binding.tietPrecioProd.error = null
        binding.tietStockProd.error = null
    }

    private fun mostrarErrorCampo(
        campo: com.google.android.material.textfield.TextInputEditText,
        mensaje: String
    ) {
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