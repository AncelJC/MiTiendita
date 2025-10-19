package com.example.mitiendita.ui

import android.R
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.mitiendita.database.CategoriaDAO
import com.example.mitiendita.databinding.FragmentCategoriaBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class CategoriaFragment : Fragment() {

    private var _binding: FragmentCategoriaBinding? = null
    private val binding get() = _binding!!

    // 🔴 CAMBIO: Usamos CategoriaDAO en lugar de DBHelper
    private lateinit var categoriaDAO: CategoriaDAO

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Iniciamos View Binding
        _binding = FragmentCategoriaBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 🔴 Inicializamos CategoriaDAO
        categoriaDAO = CategoriaDAO(requireContext())

        binding.btnGuardarCategoria.setOnClickListener { guardarCategoria() }

        // 🟢 Cargar categorías al iniciar el fragment
        cargarYMostrarCategorias()
    }

    private fun guardarCategoria() {
        val nombre = binding.etNombreCategoria.text.toString().trim()

        if (nombre.isEmpty()) {
            binding.tilNombreCategoria.error = "El nombre de la categoría es obligatorio"
            return
        } else {
            binding.tilNombreCategoria.error = null
        }

        // 2. Ejecutar la inserción en el hilo de IO (Base de datos)
        lifecycleScope.launch(Dispatchers.IO) {

            // 🔴 USAMOS CategoriaDAO para insertar
            val id = categoriaDAO.insertarCategoria(nombre)

            // 3. Volver al hilo principal (UI) para mostrar el resultado
            withContext(Dispatchers.Main) {
                if (id > 0) {
                    Toast.makeText(
                        requireContext(),
                        "✅ Categoría '$nombre' agregada con éxito (ID: $id)",
                        Toast.LENGTH_SHORT
                    ).show()
                    binding.etNombreCategoria.text?.clear()

                    // 🟢 Recargar la lista después de agregar
                    cargarYMostrarCategorias()

                } else {
                    Toast.makeText(
                        requireContext(),
                        "❌ Error al agregar la categoría. (Puede que ya exista)",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }
    }

    // 🟢 Función que usa corrutinas para cargar datos de forma asíncrona
    private fun cargarYMostrarCategorias() {
        // Lanzamos la corrutina en el ámbito del Fragment
        lifecycleScope.launch {
            // Ejecutar la operación de base de datos en un hilo de I/O
            val listaCategorias = withContext(Dispatchers.IO) {
                try {
                    // 🔴 USAMOS CategoriaDAO para obtener solo los nombres
                    categoriaDAO.obtenerNombresCategorias()
                } catch (e: Exception) {
                    Log.e("CategoriaFragment", "Error al cargar categorías", e)
                    emptyList<String>()
                }
            }

            // Actualizar la UI en el hilo principal
            if (listaCategorias.isEmpty()) {
                binding.txtMensaje.text = "No hay categorías registradas."
            } else {
                binding.txtMensaje.text = "Categorías disponibles:"
            }

            val adapter = ArrayAdapter(
                requireContext(),
                R.layout.simple_list_item_1,
                listaCategorias
            )

            // Asumo que tienes un ListView o similar con el ID 'listCategorias' en tu FragmentCategoriaBinding
            binding.listCategorias.adapter = adapter
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}