package adapter

import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.mitiendita.R
import com.example.mitiendita.entity.Producto
import com.google.android.material.button.MaterialButton
import com.bumptech.glide.Glide // Asumo que prefieres usar Glide para una mejor gestión de imágenes

class ProductoAdapter(
    private val productosList: MutableList<Producto>
) : RecyclerView.Adapter<ProductoAdapter.ProductoViewHolder>() {

    // Define las acciones que el Fragment puede manejar
    interface OnItemActionListener {
        fun onEditClick(producto: Producto)
        fun onDeleteClick(producto: Producto)
        fun onItemClick(producto: Producto)
    }

    private var actionListener: OnItemActionListener? = null

    fun setOnItemActionListener(listener: OnItemActionListener) {
        actionListener = listener
    }

    inner class ProductoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val ivProducto: ImageView = itemView.findViewById(R.id.ivProducto)
        val tvNombre: TextView = itemView.findViewById(R.id.tvNombre)
        val tvDescripcion: TextView = itemView.findViewById(R.id.tvDescripcion)
        val tvCategoria: TextView = itemView.findViewById(R.id.tvCategoria)
        val tvPrecio: TextView = itemView.findViewById(R.id.tvPrecio)
        val tvStock: TextView = itemView.findViewById(R.id.tvStock)
        val btnEditar: MaterialButton = itemView.findViewById(R.id.btnEditar)
        val btnEliminar: MaterialButton = itemView.findViewById(R.id.btnEliminar)

        init {
            // Listener para el clic general en el ítem (e.g., para ver detalles)
            itemView.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    actionListener?.onItemClick(productosList[position])
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductoViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(
            R.layout.item_producto,
            parent,
            false
        )
        return ProductoViewHolder(view)
    }

    override fun onBindViewHolder(holder: ProductoViewHolder, position: Int) {
        val producto = productosList[position]
        val context = holder.itemView.context

        // 1. Datos de texto
        holder.tvNombre.text = producto.nombre
        // Usar operador Elvis para manejar descripciones nulas
        holder.tvDescripcion.text = producto.descripcion ?: "Sin descripción"
        holder.tvCategoria.text = producto.nombreCategoria

        // Formato de precio (usando un string resource si está definido, si no, directo)
        holder.tvPrecio.text = "S/ ${String.format("%.2f", producto.precio)}"
        holder.tvStock.text = "Stock: ${producto.stock}"

        // 2. Lógica de Stock (Cambio de color)
        val stockBajo = 10 // Define el umbral de stock bajo
        val color: Int
        if (producto.stock == 0) {
            color = ContextCompat.getColor(context, R.color.red_600)
        } else if (producto.stock <= stockBajo) {
            color = ContextCompat.getColor(context, R.color.orange_600)
        } else {
            color = ContextCompat.getColor(context, R.color.green_500)
        }
        holder.tvStock.setBackgroundColor(color)

        // 3. Carga de Imagen (Usando Glide o nativo)
        val uriString = producto.imagen
        if (uriString != null && uriString.isNotEmpty()) {
            try {
                // Si tienes Glide, úsalo:
                Glide.with(context)
                    .load(Uri.parse(uriString))
                    .placeholder(R.drawable.ic_box)
                    .error(R.drawable.ic_error)
                    .into(holder.ivProducto)

                // Si NO quieres Glide, usa:
                // holder.ivProducto.setImageURI(Uri.parse(uriString))

            } catch (e: Exception) {
                holder.ivProducto.setImageResource(R.drawable.ic_error)
            }
        } else {
            holder.ivProducto.setImageResource(R.drawable.ic_box)
        }

        // 4. Listeners para botones de acción
        holder.btnEditar.setOnClickListener {
            actionListener?.onEditClick(producto)
        }
        holder.btnEliminar.setOnClickListener {
            actionListener?.onDeleteClick(producto)
        }
    }

    override fun getItemCount(): Int = productosList.size

    // Función para actualizar la lista y aplicar filtros/búsqueda
    fun updateData(newProducts: List<Producto>) {
        productosList.clear()
        productosList.addAll(newProducts)
        notifyDataSetChanged()
    }
}