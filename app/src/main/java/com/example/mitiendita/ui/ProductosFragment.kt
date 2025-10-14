package com.example.mitiendita.ui

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ImageView
import android.widget.Spinner
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.mitiendita.R
import com.example.mitiendita.database.DBHelper
import com.google.android.material.textfield.TextInputEditText

class ProductosFragment : Fragment() {

    // Constante para la selección de imagen
    private val PICK_IMAGE_REQUEST = 100

    private lateinit var dbHelper: DBHelper
    private lateinit var tietNombre: TextInputEditText
    private lateinit var tietDescripcion: TextInputEditText
    private lateinit var tietPrecio: TextInputEditText
    private lateinit var tietStock: TextInputEditText
    private lateinit var spnCategoria: Spinner // 💡 AÑADIDO: Spinner
    private lateinit var ivProducto: ImageView  // 💡 AÑADIDO: ImageView para mostrar la imagen
    private lateinit var btnSeleccionarImagen: Button // 💡 AÑADIDO: Botón para seleccionar imagen
    private lateinit var btnGuardarProducto: Button

    // Variable para guardar la URI de la imagen seleccionada
    private var imagenUri: Uri? = null

    // Simulando categorías (deberías obtenerlas de la tabla 'categorias' en DBHelper)
    private val categorias = listOf("Electrónica", "Ropa", "Alimentos", "Hogar")


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_productos, container, false)
        dbHelper = DBHelper(requireContext())

        // 1. Referenciar vistas
        tietNombre = view.findViewById(R.id.tietNombreProd)
        tietDescripcion = view.findViewById(R.id.tietDescripcionProd)
        tietPrecio = view.findViewById(R.id.tietPrecioProd)
        tietStock = view.findViewById(R.id.tietStockProd)
        spnCategoria = view.findViewById(R.id.spnCategoria) // 💡 NUEVO
        ivProducto = view.findViewById(R.id.ivProducto)     // 💡 NUEVO
        btnSeleccionarImagen = view.findViewById(R.id.btnSeleccionarImagen) // 💡 NUEVO
        btnGuardarProducto = view.findViewById(R.id.btnGuardarProducto)

        // 2. Configurar Spinner de Categorías
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, categorias)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spnCategoria.adapter = adapter

        // 3. Definir acciones
        btnGuardarProducto.setOnClickListener {
            guardarProducto()
        }

        btnSeleccionarImagen.setOnClickListener {
            seleccionarImagen()
        }

        return view
    }

    // 💡 FUNCIÓN NUEVA: Iniciar el selector de imágenes
    private fun seleccionarImagen() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, PICK_IMAGE_REQUEST)
    }

    // 💡 FUNCIÓN NUEVA: Manejar el resultado de la selección de imagen
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null && data.data != null) {
            imagenUri = data.data
            ivProducto.setImageURI(imagenUri) // Muestra la imagen seleccionada
            Toast.makeText(requireContext(), "Imagen seleccionada.", Toast.LENGTH_SHORT).show()
        }
    }

    private fun guardarProducto() {
        // Obtener y validar datos
        val nombre = tietNombre.text.toString().trim()
        val descripcion = tietDescripcion.text.toString().trim()
        val precioStr = tietPrecio.text.toString().trim()
        val stockStr = tietStock.text.toString().trim()

        // 💡 NUEVO: Obtener la categoría seleccionada (el índice + 1 si la tabla empieza en ID 1)
        val idCategoria = spnCategoria.selectedItemPosition + 1

        // 💡 NUEVO: Obtener la ruta de la imagen
        val rutaImagen = imagenUri?.toString()

        if (nombre.isEmpty() || precioStr.isEmpty() || stockStr.isEmpty()) {
            Toast.makeText(requireContext(), "Nombre, Precio y Stock son obligatorios.", Toast.LENGTH_LONG).show()
            return
        }

        val precio = precioStr.toDoubleOrNull()
        val stock = stockStr.toIntOrNull()

        if (precio == null || stock == null || precio <= 0 || stock < 0) {
            Toast.makeText(requireContext(), "Precio o Stock tienen formatos inválidos.", Toast.LENGTH_LONG).show()
            return
        }

        // 3. Insertar en la base de datos (con los nuevos campos)
        val idInsertado = dbHelper.insertarProducto(
            nombre = nombre,
            descripcion = descripcion,
            precio = precio,
            stock = stock,
            idCat = idCategoria, // 💡 NUEVO
            imagen = rutaImagen  // 💡 NUEVO
        )

        if (idInsertado > 0) {
            Toast.makeText(requireContext(), "Producto '$nombre' guardado con éxito.", Toast.LENGTH_LONG).show()
            // Limpiar campos después de guardar
            tietNombre.setText("")
            tietDescripcion.setText("")
            tietPrecio.setText("")
            tietStock.setText("")
            ivProducto.setImageResource(R.drawable.ic_image_placeholder) // Restaura el placeholder
            imagenUri = null // Limpia la URI
        } else {
            Toast.makeText(requireContext(), "Error al guardar el producto.", Toast.LENGTH_LONG).show()
        }
    }
}