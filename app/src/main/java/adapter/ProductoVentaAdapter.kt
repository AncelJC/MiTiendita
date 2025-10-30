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

class ProductoVentaAdapter(  // ✅ Nuevo adaptador para ventas
    private val items: List<Producto>,
    private val onAgregarCarrito: (Producto) -> Unit
) : RecyclerView.Adapter<ProductoVentaAdapter.ProductoViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductoViewHolder {
        val v = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_producto_inicio, parent, false)
        return ProductoViewHolder(v)
    }

    override fun onBindViewHolder(holder: ProductoViewHolder, position: Int) {
        val producto = items[position]

        holder.tvNombre.text = producto.nombre
        holder.tvPrecio.text = "$${producto.precio}"
        holder.tvStock.text = "Stock: ${producto.stock}"
        holder.tvCategoria.text = producto.nombreCategoria ?: "Sin categoría"

        // Cargar imagen si existe
        if (!producto.imagen.isNullOrEmpty()) {
            Glide.with(holder.itemView.context)
                .load(producto.imagen)
                .placeholder(R.drawable.ic_product_placeholder)
                .into(holder.ivImagen)
        } else {
            holder.ivImagen.setImageResource(R.drawable.ic_product_placeholder)
        }

        // Configurar botón de agregar al carrito
        holder.btnAgregarCarrito.setOnClickListener {
            if (producto.stock > 0) {
                onAgregarCarrito(producto)
            } else {
                android.widget.Toast.makeText(
                    holder.itemView.context,
                    "Producto sin stock disponible",
                    android.widget.Toast.LENGTH_SHORT
                ).show()
            }
        }

        // Deshabilitar botón si no hay stock
        holder.btnAgregarCarrito.isEnabled = producto.stock > 0
        holder.btnAgregarCarrito.text = if (producto.stock > 0) "Agregar al Carrito" else "Sin Stock"
    }

    override fun getItemCount(): Int = items.size

    class ProductoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val ivImagen: ImageView = itemView.findViewById(R.id.imgProducto)
        val tvNombre: TextView = itemView.findViewById(R.id.tvNombreProducto)
        val tvPrecio: TextView = itemView.findViewById(R.id.tvPrecioProducto)
        val tvStock: TextView = itemView.findViewById(R.id.tvStockProducto)
        val tvCategoria: TextView = itemView.findViewById(R.id.tvCategoriaProducto)
        val btnAgregarCarrito: Button = itemView.findViewById(R.id.btnAgregarCarrito)
    }
}