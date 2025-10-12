package com.example.mitiendavirtual.adapters // Ajusta tu paquete

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide // NECESITAS ESTA LIBRERÍA para cargar imágenes (ver notas abajo)
import com.example.mitiendavirtual.R
import com.example.mitiendavirtual.data.Product // Asegúrate de que esta ruta sea correcta
import com.example.mitiendavirtual.databinding.ItemProductGridBinding
import java.text.NumberFormat
import java.util.Locale

// 1. Define la interfaz para manejar los clics en los productos
interface OnProductClickListener {
    fun onProductClick(product: Product)
    fun onAddToCartClick(product: Product)
}

class ProductAdapter(
    private val productList: List<Product>,
    private val listener: OnProductClickListener // Recibe el listener de clics
) : RecyclerView.Adapter<ProductAdapter.ProductViewHolder>() {

    // 2. ViewHolder: Contiene las referencias a las vistas (ImageView, TextViews, Button)
    inner class ProductViewHolder(private val binding: ItemProductGridBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(product: Product) {
            // Cargar imagen usando Glide (requiere dependencia)
            Glide.with(binding.productImageView.context)
                .load(product.imageUrl) // Carga la URL de la imagen del producto
                .placeholder(R.drawable.placeholder_product) // Muestra un placeholder mientras carga
                .into(binding.productImageView)

            // Asignar el nombre del producto
            binding.productNameTextView.text = product.name

            // Formatear y asignar el precio (ejemplo: a formato de dólar)
            val format = NumberFormat.getCurrencyInstance(Locale("es", "PE")) // Puedes cambiar el Locale
            binding.productPriceTextView.text = format.format(product.price)

            // Manejar clic en el producto (para ir a la pantalla de detalles)
            binding.root.setOnClickListener {
                listener.onProductClick(product)
            }

            // Manejar clic en el botón "Añadir"
            binding.addToCartButton.setOnClickListener {
                listener.onAddToCartClick(product)
            }
        }
    }

    // 3. Crea la vista (Infla el layout XML)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        val binding = ItemProductGridBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ProductViewHolder(binding)
    }

    // 4. Asigna los datos a la vista
    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        holder.bind(productList[position])
    }

    // 5. Devuelve el número total de elementos en la lista
    override fun getItemCount(): Int = productList.size
}