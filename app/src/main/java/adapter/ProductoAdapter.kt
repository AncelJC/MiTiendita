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

class ProductoAdapter(
    private val actionListener: OnItemActionListener
) : ListAdapter<Producto, ProductoAdapter.ProductoViewHolder>(ProductoDiffCallback()) {

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
        val producto = getItem(position)
        val context = holder.itemView.context

        holder.tvNombre.text = producto.nombre
        holder.tvDescripcion.text = producto.descripcion ?: "Sin descripción"
        holder.tvCategoria.text = producto.nombreCategoria

        holder.tvPrecio.text = formatoMoneda.format(producto.precio)
        holder.tvStock.text = "Stock: ${producto.stock}"

        val stockBajo = 10
        val color: Int = when {
            producto.stock == 0 -> ContextCompat.getColor(context, R.color.red_600)
            producto.stock <= stockBajo -> ContextCompat.getColor(context, R.color.orange_600)
            else -> ContextCompat.getColor(context, R.color.green_500)
        }
        holder.tvStock.setTextColor(color)

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

        holder.btnEditar.setOnClickListener { actionListener.onEditClick(producto) }
        holder.btnEliminar.setOnClickListener { actionListener.onDeleteClick(producto) }
    }

}