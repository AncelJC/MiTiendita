package adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.mitiendita.R
import com.example.mitiendita.entity.Producto
import com.google.android.material.button.MaterialButton

class ProductoAdminAdapter(
    private var productos: List<Producto>,
    private val onEditar: (Producto) -> Unit,
    private val onEliminar: (Producto) -> Unit,
    private val onCambiarEstado: (Producto, Boolean) -> Unit
) : RecyclerView.Adapter<ProductoAdminAdapter.ProductoAdminViewHolder>() {

    inner class ProductoAdminViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val ivProducto: ImageView = itemView.findViewById(R.id.ivProductoAdmin)
        val tvNombre: TextView = itemView.findViewById(R.id.tvNombre)
        val tvDescripcion: TextView = itemView.findViewById(R.id.tvDescripcion)
        val tvCategoria: TextView = itemView.findViewById(R.id.tvCategoria)
        val tvPrecio: TextView = itemView.findViewById(R.id.tvPrecio)
        val tvStock: TextView = itemView.findViewById(R.id.tvStock)
        val tvEstado: TextView = itemView.findViewById(R.id.tvEstado)
        val btnEditar: MaterialButton = itemView.findViewById(R.id.btnEditarProducto)
        val btnEliminar: MaterialButton = itemView.findViewById(R.id.btnEliminarProducto)
        val btnEstado: MaterialButton = itemView.findViewById(R.id.btnEstadoProducto)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductoAdminViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_producto_admin, parent, false)
        return ProductoAdminViewHolder(view)
    }

    override fun onBindViewHolder(holder: ProductoAdminViewHolder, position: Int) {
        val producto = productos[position]
        val context = holder.itemView.context

        holder.tvNombre.text = producto.nombre
        holder.tvDescripcion.text = producto.descripcion ?: "Sin descripción"
        holder.tvCategoria.text = producto.nombreCategoria ?: "Sin categoría"
        holder.tvPrecio.text = "S/ ${String.format("%.2f", producto.precio)}"
        holder.tvStock.text = "Stock: ${producto.stock}"

        // Estado
        if (producto.activo) {
            holder.tvEstado.text = "ACTIVO"
            holder.tvEstado.setTextColor(ContextCompat.getColor(context, R.color.green_500))
            holder.btnEstado.text = "Deshabilitar"
            holder.btnEstado.setBackgroundColor(ContextCompat.getColor(context, R.color.orange_500))
        } else {
            holder.tvEstado.text = "INACTIVO"
            holder.tvEstado.setTextColor(ContextCompat.getColor(context, R.color.red_500))
            holder.btnEstado.text = "Habilitar"
            holder.btnEstado.setBackgroundColor(ContextCompat.getColor(context, R.color.green_500))
        }

        // Imagen
        if (!producto.imagen.isNullOrEmpty()) {
            Glide.with(context)
                .load(producto.imagen)
                .placeholder(R.drawable.ic_box)
                .error(R.drawable.ic_error)
                .into(holder.ivProducto)
        } else {
            holder.ivProducto.setImageResource(R.drawable.ic_box)
        }

        holder.btnEditar.setOnClickListener { onEditar(producto) }
        holder.btnEliminar.setOnClickListener { onEliminar(producto) }
        holder.btnEstado.setOnClickListener { onCambiarEstado(producto, !producto.activo) }
    }

    override fun getItemCount(): Int = productos.size

    fun actualizarLista(nuevaLista: List<Producto>) {
        productos = nuevaLista
        notifyDataSetChanged()
    }
}
