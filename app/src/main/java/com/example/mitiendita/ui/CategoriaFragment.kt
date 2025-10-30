package com.example.mitiendita.ui

import adapter.CategoriaAdapter // ⬅️ Nuevo Import
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.mitiendita.database.CategoriaDAO
import com.example.mitiendita.databinding.FragmentCategoriaBinding
import com.example.mitiendita.entity.Categoria
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class CategoriaFragment : Fragment() {

    private var _binding: FragmentCategoriaBinding? = null
    private val binding get() = _binding!!

    private lateinit var categoriaDAO: CategoriaDAO

    private lateinit var categoriaAdapter: CategoriaAdapter
    private val categoriasList = mutableListOf<Categoria>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCategoriaBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        categoriaDAO = CategoriaDAO(requireContext())

        inicializarRecyclerView()

        binding.btnGuardarCategoria.setOnClickListener { guardarCategoria() }

        cargarYMostrarCategorias()
    }

    private fun inicializarRecyclerView() {
        categoriaAdapter = CategoriaAdapter(categoriasList)
        binding.rvCategorias.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = categoriaAdapter
        }

        categoriaAdapter.setOnItemActionListener(object : CategoriaAdapter.OnItemActionListener {
            override fun onEditClick(categoria: Categoria) {
                // Lógica para abrir diálogo de edición
                Toast.makeText(requireContext(), "Editar: ${categoria.nombre}", Toast.LENGTH_SHORT).show()
            }

            override fun onDeleteClick(categoria: Categoria) {
                mostrarDialogoEliminar(categoria)
            }
        })
    }


    private fun guardarCategoria() {
        val nombre = binding.etNombreCategoria.text.toString().trim()

        if (nombre.isEmpty()) {
            binding.tilNombreCategoria.error = "El nombre de la categoría es obligatorio"
            return
        } else {
            binding.tilNombreCategoria.error = null
        }

        lifecycleScope.launch(Dispatchers.IO) {
            val id = categoriaDAO.insertarCategoria(nombre)

            withContext(Dispatchers.Main) {
                if (id > 0) {
                    Toast.makeText(
                        requireContext(),
                        "✅ Categoría '$nombre' agregada con éxito (ID: $id)",
                        Toast.LENGTH_SHORT
                    ).show()
                    binding.etNombreCategoria.text?.clear()
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

    private fun cargarYMostrarCategorias() {
        lifecycleScope.launch {
            binding.txtMensaje.visibility = View.VISIBLE // Mostrar mensaje de carga
            binding.rvCategorias.visibility = View.GONE

            val lista = withContext(Dispatchers.IO) {
                try {
                    categoriaDAO.obtenerTodasLasCategorias()
                } catch (e: Exception) {
                    Log.e("CategoriaFragment", "Error al cargar categorías", e)
                    emptyList<Categoria>()
                }
            }

            withContext(Dispatchers.Main) {
                categoriaAdapter.updateData(lista)

                if (lista.isEmpty()) {
                    binding.txtMensaje.text = "No hay categorías registradas."
                    binding.txtMensaje.visibility = View.VISIBLE
                    binding.rvCategorias.visibility = View.GONE
                } else {
                    binding.txtMensaje.visibility = View.GONE
                    binding.rvCategorias.visibility = View.VISIBLE
                }
            }
        }
    }

    private fun mostrarDialogoEliminar(categoria: Categoria) {
        AlertDialog.Builder(requireContext())
            .setTitle("Confirmar Eliminación")
            .setMessage("¿Estás seguro de que deseas eliminar la categoría '${categoria.nombre}'?")
            .setPositiveButton("Eliminar") { dialog, which ->
                eliminarCategoria(categoria)
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    private fun eliminarCategoria(categoria: Categoria) {
        lifecycleScope.launch(Dispatchers.IO) {
            // Asumiendo que tienes una función 'eliminarCategoria' en tu DAO
            val filasAfectadas = categoriaDAO.eliminarCategoria(categoria.idCat)

            withContext(Dispatchers.Main) {
                if (filasAfectadas > 0) {
                    Toast.makeText(
                        requireContext(),
                        "✅ Categoría '${categoria.nombre}' eliminada.",
                        Toast.LENGTH_SHORT
                    ).show()
                    cargarYMostrarCategorias() // Recargar la lista
                } else {
                    Toast.makeText(
                        requireContext(),
                        "❌ Error al eliminar la categoría.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}