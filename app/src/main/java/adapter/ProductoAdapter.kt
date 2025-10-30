package com.example.mitiendita.adapter

import adapter.ProductoDiffCallback
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.mitiendita.R
import com.example.mitiendita.entity.Producto
import com.google.android.material.button.MaterialButton
import com.bumptech.glide.Glide
import java.text.NumberFormat
import java.util.Currency
import java.util.Locale

// 1. Hereda de ListAdapter y usa ProductoDiffCallback
class ProductoAdapter(
    // 2. Recibe la interfaz en el constructor
    private val actionListener: OnItemActionListener
) : ListAdapter<Producto, ProductoAdapter.ProductoViewHolder>(ProductoDiffCallback()) {

    // 3. Formato de moneda mejorado (2 decimales)
    private val formatoMoneda: NumberFormat =
        NumberFormat.getCurrencyInstance(Locale("es", "PE")).apply {
            maximumFractionDigits = 2
            currency = Currency.getInstance("PEN")
        }

    interface OnItemActionListener {
        fun onEditClick(producto: Producto)
        fun onDeleteClick(producto: Producto)
        fun onItemClick(producto: Producto)
    }

    inner class ProductoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        // Las referencias a las vistas se mantienen
        val ivProducto: ImageView = itemView.findViewById(R.id.ivProducto)
        val tvNombre: TextView = itemView.findViewById(R.id.tvNombre)
        val tvDescripcion: TextView = itemView.findViewById(R.id.tvDescripcion)
        val tvCategoria: TextView = itemView.findViewById(R.id.tvCategoria)
        val tvPrecio: TextView = itemView.findViewById(R.id.tvPrecio)
        val tvStock: TextView = itemView.findViewById(R.id.tvStock)
        val btnEditar: MaterialButton = itemView.findViewById(R.id.btnEditarProducto)
        val btnEliminar: MaterialButton = itemView.findViewById(R.id.btnEliminarProducto)

        init {
            itemView.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    // Usa getItem() de ListAdapter
                    actionListener.onItemClick(getItem(position))
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
        // Usa getItem() de ListAdapter
        val producto = getItem(position)
        val context = holder.itemView.context

        // 1. Datos de texto
        holder.tvNombre.text = producto.nombre
        holder.tvDescripcion.text = producto.descripcion ?: "Sin descripción"
        holder.tvCategoria.text = producto.nombreCategoria

        // Uso el formato de moneda mejorado
        holder.tvPrecio.text = formatoMoneda.format(producto.precio)
        holder.tvStock.text = "Stock: ${producto.stock}"

        // 2. Lógica de Stock (Cambio de color)
        val stockBajo = 10
        val color: Int = when {
            producto.stock == 0 -> ContextCompat.getColor(context, R.color.red_600)
            producto.stock <= stockBajo -> ContextCompat.getColor(context, R.color.orange_600)
            else -> ContextCompat.getColor(context, R.color.green_500)
        }
        // Cambiamos el color del texto
        holder.tvStock.setTextColor(color)

        // 3. Carga de Imagen
        val uriString = producto.imagen
        if (!uriString.isNullOrEmpty()) {
            try {
                Glide.with(context)
                    .load(Uri.parse(uriString))
                    .placeholder(R.drawable.ic_box)
                    .error(R.drawable.ic_error)
                    .into(holder.ivProducto)
            } catch (e: Exception) {
                holder.ivProducto.setImageResource(R.drawable.ic_error)
            }
        } else {
            holder.ivProducto.setImageResource(R.drawable.ic_box)
        }

        // 4. Listeners para botones de acción
        holder.btnEditar.setOnClickListener { actionListener.onEditClick(producto) }
        holder.btnEliminar.setOnClickListener { actionListener.onDeleteClick(producto) }
    }

    // 5. Los métodos getItemCount() y updateData() ya no son necesarios
}