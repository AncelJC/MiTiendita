package adapter

import androidx.recyclerview.widget.DiffUtil
import com.example.mitiendita.entity.Producto

// Se asume que Producto es una data class con un idProducto único.
class ProductoDiffCallback : DiffUtil.ItemCallback<Producto>() {
    override fun areItemsTheSame(oldItem: Producto, newItem: Producto): Boolean {
        // Comprueba si son el mismo producto (usando la clave primaria)
        return oldItem.idProd == newItem.idProd
    }

    override fun areContentsTheSame(oldItem: Producto, newItem: Producto): Boolean {
        // Comprueba si los datos del producto han cambiado
        return oldItem == newItem
    }
}