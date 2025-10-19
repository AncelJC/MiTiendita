package adapter

import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.mitiendita.R
import com.example.mitiendita.entity.Producto

class ProductoAdapter(
    private val productosList: List<Producto>
) : RecyclerView.Adapter<ProductoAdapter.ProductoViewHolder>() {

    private var onItemClickListener: ((Producto) -> Unit)? = null

    fun setOnItemClickListener(listener: (Producto) -> Unit) {
        onItemClickListener = listener
    }

    // 2. ViewHolder: Contiene las referencias a las vistas
    inner class ProductoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvNombre: TextView = itemView.findViewById(R.id.tvNombreProducto)
        val tvPrecio: TextView = itemView.findViewById(R.id.tvPrecioProducto)
        val ivImagen: ImageView = itemView.findViewById(R.id.ivImagenProducto)

        init {
            itemView.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    onItemClickListener?.invoke(productosList[position])
                }
            }
        }
    }

    // 3. Crea la vista (Inflar el Layout)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductoViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(
            R.layout.item_producto, // Asegúrate de que este layout exista
            parent,
            false
        )
        return ProductoViewHolder(view)
    }

    // 4. Bindea los datos a la vista
    override fun onBindViewHolder(holder: ProductoViewHolder, position: Int) {
        val producto = productosList[position]

        holder.tvNombre.text = producto.nombre
        // Usar formato de moneda adecuado
        holder.tvPrecio.text = holder.itemView.context.getString(
            R.string.precio_formato, // Asumiendo un string resource como "$%.2f"
            producto.precio
        )

        // 🔴 Lógica de Carga de Imagen NATIVA (sin Glide)
        val uriString = producto.imagen
        if (uriString != null) {
            try {
                val imageUri = Uri.parse(uriString)
                // ➡️ Usamos setImageURI para cargar la imagen directamente desde la URI
                holder.ivImagen.setImageURI(imageUri)

                // ⚠️ Importante: Si la imagen no se carga, verifica los permisos persistentes
                // en el ProductosFragment y el uso de ACTION_OPEN_DOCUMENT.
            } catch (e: Exception) {
                // Fallback si hay un error con la URI o permisos (ej. URI malformada)
                holder.ivImagen.setImageResource(R.drawable.ic_error)
            }
        } else {
            // Si no hay URI de imagen, usar el placeholder por defecto
            holder.ivImagen.setImageResource(R.drawable.ic_box)
        }
    }

    // 5. Retorna el número total de elementos
    override fun getItemCount(): Int = productosList.size
}