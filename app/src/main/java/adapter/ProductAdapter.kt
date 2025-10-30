package adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.mitiendita.R
import com.example.mitiendita.entity.Productos

class ProductAdapter (private val items: List<Productos>) :
    RecyclerView.Adapter<ProductAdapter.ProductViewHolder>(){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int    ): ProductViewHolder{
        val v = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_product, parent, false)
        return ProductViewHolder(v)
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int){
        val p = items[position]

        holder.tvTitle.text = p.title
        holder.tvPrice.text = "Precio: $${p.price}"
        holder.tvCategory.text = "Categoría: ${p.category}"
        Glide.with(holder.itemView.context)
            .load(p.image)
            .placeholder(R.drawable.ic_computer)
            .into(holder.ivImage)

        }

    override fun getItemCount(): Int = items.size

    class ProductViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val ivImage: ImageView = itemView.findViewById(R.id.ivFotoProducto)
        val tvTitle: TextView = itemView.findViewById(R.id.tvNombreProducto)
        val tvPrice: TextView = itemView.findViewById(R.id.tvPrecioProducto)
        val tvCategory: TextView = itemView.findViewById(R.id.tvCategoriaProducto)
    }
}