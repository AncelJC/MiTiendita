package com.example.mitiendita.ui

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ImageView
import android.widget.Spinner
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputEditText
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import com.example.mitiendita.R
import com.example.mitiendita.dao.ProductoDAO
import com.example.mitiendita.database.CategoriaDAO
import com.example.mitiendita.entity.Producto
import java.io.InputStream

class EditarProductoFragment : Fragment() {

    private lateinit var tietNombreProdEdit: TextInputEditText
    private lateinit var tietDescripcionProdEdit: TextInputEditText
    private lateinit var tietPrecioProdEdit: TextInputEditText
    private lateinit var tietStockProdEdit: TextInputEditText
    private lateinit var spnCategoriaEdit: Spinner
    private lateinit var ivProductoEdit: ImageView
    private lateinit var btnCambiarImagen: Button
    private lateinit var btnActualizarProducto: Button

    private lateinit var productoDAO: ProductoDAO
    private lateinit var categoriaDAO: CategoriaDAO
    private var producto: Producto? = null
    private var imagenUri: Uri? = null

    // Seleccionar imagen desde la galería
    private val seleccionarImagen =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val data: Intent? = result.data
                imagenUri = data?.data
                try {
                    val inputStream: InputStream? =
                        imagenUri?.let { requireContext().contentResolver.openInputStream(it) }
                    val bitmap = BitmapFactory.decodeStream(inputStream)
                    ivProductoEdit.setImageBitmap(bitmap)
                } catch (e: Exception) {
                    e.printStackTrace()
                    Snackbar.make(requireView(), "Error al cargar la imagen", Snackbar.LENGTH_LONG).show()
                }
            }
        }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_editar_producto, container, false)
        inicializarComponentes(view)

        productoDAO = ProductoDAO(requireContext())
        categoriaDAO = CategoriaDAO(requireContext())

        // Obtener producto desde argumentos
        producto = arguments?.getSerializable("producto") as? Producto
        producto?.let { cargarDatosProducto(it) }

        btnCambiarImagen.setOnClickListener { abrirGaleria() }
        btnActualizarProducto.setOnClickListener { actualizarProducto() }

        return view
    }

    private fun inicializarComponentes(view: View) {
        tietNombreProdEdit = view.findViewById(R.id.tietNombreProdEdit)
        tietDescripcionProdEdit = view.findViewById(R.id.tietDescripcionProdEdit)
        tietPrecioProdEdit = view.findViewById(R.id.tietPrecioProdEdit)
        tietStockProdEdit = view.findViewById(R.id.tietStockProdEdit)
        spnCategoriaEdit = view.findViewById(R.id.spnCategoriaEdit)
        ivProductoEdit = view.findViewById(R.id.ivProductoEdit)
        btnCambiarImagen = view.findViewById(R.id.btnCambiarImagen)
        btnActualizarProducto = view.findViewById(R.id.btnActualizarProducto)
    }

    private fun cargarDatosProducto(p: Producto) {
        tietNombreProdEdit.setText(p.nombre)
        tietDescripcionProdEdit.setText(p.descripcion)
        tietPrecioProdEdit.setText(p.precio.toString())
        tietStockProdEdit.setText(p.stock.toString())

        // Cargar imagen del producto
        if (!p.imagen.isNullOrEmpty()) {
            try {
                ivProductoEdit.setImageURI(Uri.parse(p.imagen))
            } catch (e: Exception) {
                ivProductoEdit.setImageResource(R.drawable.ic_box)
            }
        } else {
            ivProductoEdit.setImageResource(R.drawable.ic_box)
        }

        // Cargar las categorías
        val categorias = categoriaDAO.obtenerNombresCategorias()
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, categorias)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spnCategoriaEdit.adapter = adapter

        // Seleccionar la categoría actual del producto
        val posicion = categorias.indexOf(p.nombreCategoria)
        if (posicion >= 0) {
            spnCategoriaEdit.setSelection(posicion)
        }
    }

    private fun abrirGaleria() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        seleccionarImagen.launch(intent)
    }

    private fun actualizarProducto() {
        val nombre = tietNombreProdEdit.text.toString().trim()
        val descripcion = tietDescripcionProdEdit.text.toString().trim()
        val precio = tietPrecioProdEdit.text.toString().toDoubleOrNull()
        val stock = tietStockProdEdit.text.toString().toIntOrNull()
        val nombreCategoria = spnCategoriaEdit.selectedItem?.toString() ?: ""

        if (nombre.isEmpty() || descripcion.isEmpty() || precio == null || stock == null || nombreCategoria.isEmpty()) {
            Snackbar.make(requireView(), "Por favor, completa todos los campos.", Snackbar.LENGTH_LONG).show()
            return
        }

        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Confirmar actualización")
        builder.setMessage("¿Deseas guardar los cambios del producto?")
        builder.setPositiveButton("Sí") { _, _ ->
            producto?.let {
                it.nombre = nombre
                it.descripcion = descripcion
                it.precio = precio
                it.stock = stock
                it.nombreCategoria = nombreCategoria

                // Obtener el ID de la categoría seleccionada
                val idCat = categoriaDAO.obtenerIdCategoriaPorNombre(nombreCategoria)
                it.idCat = idCat

                if (imagenUri != null) {
                    it.imagen = imagenUri.toString()
                }

                val filasAfectadas = productoDAO.actualizarProducto(it)
                if (filasAfectadas > 0) {
                    Snackbar.make(requireView(), "Producto actualizado correctamente", Snackbar.LENGTH_LONG).show()
                    parentFragmentManager.popBackStack() // Regresar al listado
                } else {
                    Snackbar.make(requireView(), "Error al actualizar el producto", Snackbar.LENGTH_LONG).show()
                }
            }
        }

        builder.setNegativeButton("Cancelar", null)
        builder.show()
    }
}
