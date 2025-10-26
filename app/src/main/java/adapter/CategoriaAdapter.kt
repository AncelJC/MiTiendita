package adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.mitiendita.R
import com.example.mitiendita.entity.Categoria
import com.google.android.material.button.MaterialButton

class CategoriaAdapter(
    private val categoriasList: MutableList<Categoria>
) : RecyclerView.Adapter<CategoriaAdapter.CategoriaViewHolder>() {

    // Interfaces para manejar eventos de clic fuera del Adapter
    interface OnItemActionListener {
        fun onEditClick(categoria: Categoria)
        fun onDeleteClick(categoria: Categoria)
    }

    private var actionListener: OnItemActionListener? = null

    fun setOnItemActionListener(listener: OnItemActionListener) {
        actionListener = listener
    }

    inner class CategoriaViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvNombre: TextView = itemView.findViewById(R.id.tvNombreCategoria)
        val btnEditar: MaterialButton = itemView.findViewById(R.id.btnEditarCategoria)
        val btnEliminar: MaterialButton = itemView.findViewById(R.id.btnEliminarCategoria)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoriaViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(
            R.layout.item_categoria,
            parent,
            false
        )
        return CategoriaViewHolder(view)
    }

    override fun onBindViewHolder(holder: CategoriaViewHolder, position: Int) {
        val categoria = categoriasList[position]

        holder.tvNombre.text = categoria.nombre

        // 🟢 Configurar listeners para los botones de acción
        holder.btnEditar.setOnClickListener {
            actionListener?.onEditClick(categoria)
        }

        holder.btnEliminar.setOnClickListener {
            actionListener?.onDeleteClick(categoria)
        }
    }

    override fun getItemCount(): Int = categoriasList.size

    // Función de utilidad para actualizar la lista
    fun updateData(newCategories: List<Categoria>) {
        categoriasList.clear()
        categoriasList.addAll(newCategories)
        notifyDataSetChanged()
    }
}