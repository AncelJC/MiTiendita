// CarritoAdapter.kt
package adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.mitiendita.R
import com.example.mitiendita.entity.CarritoItem
import com.google.android.material.button.MaterialButton
import com.google.android.material.snackbar.Snackbar

class CarritoAdapter(
    private val carritoItems: MutableList<CarritoItem>,
    private val onCantidadChanged: (Int, Int) -> Unit,
    private val onItemRemoved: (Int) -> Unit
) : RecyclerView.Adapter<CarritoAdapter.CarritoViewHolder>() {

    inner class CarritoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imgProducto: ImageView = itemView.findViewById(R.id.imgProductoCarrito)
        val tvNombre: TextView = itemView.findViewById(R.id.tvNombreProductoCarrito)
        val tvPrecio: TextView = itemView.findViewById(R.id.tvPrecioProductoCarrito)
        val tvCantidad: TextView = itemView.findViewById(R.id.tvCantidad)
        val btnMenos: MaterialButton = itemView.findViewById(R.id.btnMenos)
        val btnMas: MaterialButton = itemView.findViewById(R.id.btnMas)
        val btnEliminar: MaterialButton = itemView.findViewById(R.id.btnEliminarCarrito)
        val tvSubtotal: TextView = itemView.findViewById(R.id.tvSubtotal)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CarritoViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_carrito, parent, false)
        return CarritoViewHolder(view)
    }

    override fun onBindViewHolder(holder: CarritoViewHolder, position: Int) {
        val item = carritoItems[position]
        val context = holder.itemView.context

        // Configurar datos del producto
        holder.tvNombre.text = item.nombre
        holder.tvPrecio.text = "S/ ${String.format("%.2f", item.precio)}"
        holder.tvCantidad.text = item.cantidad.toString()
        holder.tvSubtotal.text = "S/ ${String.format("%.2f", item.getSubtotal())}"

        // Cargar imagen
        if (!item.imagen.isNullOrEmpty()) {
            try {
                Glide.with(context)
                    .load(item.imagen)
                    .placeholder(R.drawable.ic_product_placeholder)
                    .error(R.drawable.ic_product_placeholder)
                    .into(holder.imgProducto)
            } catch (e: Exception) {
                holder.imgProducto.setImageResource(R.drawable.ic_product_placeholder)
            }
        } else {
            holder.imgProducto.setImageResource(R.drawable.ic_product_placeholder)
        }

        // Configurar botones de cantidad con validación de stock
        holder.btnMenos.isEnabled = item.cantidad > 1
        holder.btnMas.isEnabled = item.cantidad < item.stock

        holder.btnMenos.setOnClickListener {
            if (item.decrementarCantidad()) {
                holder.tvCantidad.text = item.cantidad.toString()
                holder.tvSubtotal.text = "S/ ${String.format("%.2f", item.getSubtotal())}"
                holder.btnMenos.isEnabled = item.cantidad > 1
                holder.btnMas.isEnabled = true
                onCantidadChanged(position, item.cantidad)
            }
        }

        holder.btnMas.setOnClickListener {
            if (item.incrementarCantidad()) {
                holder.tvCantidad.text = item.cantidad.toString()
                holder.tvSubtotal.text = "S/ ${String.format("%.2f", item.getSubtotal())}"
                holder.btnMas.isEnabled = item.cantidad < item.stock
                holder.btnMenos.isEnabled = true
                onCantidadChanged(position, item.cantidad)
            } else {
                Snackbar.make(holder.itemView, "No hay más stock disponible", Snackbar.LENGTH_SHORT).show()
            }
        }

        // Botón eliminar
        holder.btnEliminar.setOnClickListener {
            onItemRemoved(position)
        }
    }

    override fun getItemCount(): Int = carritoItems.size

    fun eliminarItem(position: Int) {
        notifyItemRemoved(position)
    }

    fun actualizarItem(position: Int, item: CarritoItem) {
        carritoItems[position] = item
        notifyItemChanged(position)
    }

    fun getTotalCarrito(): Double {
        return carritoItems.sumOf { it.getSubtotal() }
    }

    fun actualizarLista(nuevaLista: List<CarritoItem>) {
        carritoItems.clear()
        carritoItems.addAll(nuevaLista)
        notifyDataSetChanged()
    }
    fun getCarritoItems(): List<CarritoItem> {
        return carritoItems.toList()
    }
}