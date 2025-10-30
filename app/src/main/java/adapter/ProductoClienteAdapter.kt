// ProductoClienteAdapter.kt
package adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.mitiendita.R
import com.example.mitiendita.entity.Producto

class ProductoClienteAdapter(
    private val productosList: MutableList<Producto>
) : RecyclerView.Adapter<ProductoClienteAdapter.ProductoViewHolder>() {

    private var onAgregarCarritoListener: ((Producto) -> Unit)? = null

    fun setOnAgregarCarritoListener(listener: (Producto) -> Unit) {
        this.onAgregarCarritoListener = listener
    }

    inner class ProductoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        // Verifica que estos IDs existan en tu item_producto.xml
        val imgProducto: ImageView = itemView.findViewById(R.id.imgProducto)
        val tvNombreProducto: TextView = itemView.findViewById(R.id.tvNombreProducto)
        val tvCategoriaProducto: TextView = itemView.findViewById(R.id.tvCategoriaProducto)
        val tvPrecioProducto: TextView = itemView.findViewById(R.id.tvPrecioProducto)
        val tvStockProducto: TextView = itemView.findViewById(R.id.tvStockProducto)
        val btnAgregarCarrito: Button = itemView.findViewById(R.id.btnAgregarCarrito)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductoViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(
            R.layout.item_producto, // Asegúrate de que este layout existe
            parent,
            false
        )
        return ProductoViewHolder(view)
    }

    override fun onBindViewHolder(holder: ProductoViewHolder, position: Int) {
        val producto = productosList[position]
        val context = holder.itemView.context

        // 1. Datos del producto
        holder.tvNombreProducto.text = producto.nombre
        holder.tvCategoriaProducto.text = producto.nombreCategoria ?: "Sin categoría"
        holder.tvPrecioProducto.text = "S/ ${String.format("%.2f", producto.precio)}"
        holder.tvStockProducto.text = "Stock: ${producto.stock}"

        // 2. Lógica de stock y disponibilidad
        val estaDisponible = producto.stock > 0 && producto.activo

        holder.btnAgregarCarrito.isEnabled = estaDisponible
        holder.btnAgregarCarrito.alpha = if (estaDisponible) 1.0f else 0.5f

        // 3. Carga de imagen
        val uriString = producto.imagen
        if (uriString != null && uriString.isNotEmpty()) {
            try {
                Glide.with(context)
                    .load(uriString)
                    .placeholder(R.drawable.ic_product_placeholder)
                    .error(R.drawable.ic_product_placeholder)
                    .into(holder.imgProducto)
            } catch (e: Exception) {
                holder.imgProducto.setImageResource(R.drawable.ic_product_placeholder)
            }
        } else {
            holder.imgProducto.setImageResource(R.drawable.ic_product_placeholder)
        }

        // 4. Listener para agregar al carrito
        holder.btnAgregarCarrito.setOnClickListener {
            if (estaDisponible) {
                onAgregarCarritoListener?.invoke(producto)
            }
        }

        // 5. Listener para hacer clic en el producto
        holder.itemView.setOnClickListener {
            if (estaDisponible) {
                onAgregarCarritoListener?.invoke(producto)
            }
        }
    }

    override fun getItemCount(): Int = productosList.size

    fun updateData(newProducts: List<Producto>) {
        productosList.clear()
        productosList.addAll(newProducts)
        notifyDataSetChanged()
    }
}