package adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.mitiendita.R
import com.example.mitiendita.entity.Compra // Usamos la entidad Compra (Cabecera)

// Nota: No se necesita importar Producto

class HistorialAdapter (val listaCompras : List<Compra>): RecyclerView.Adapter<HistorialAdapter.HistorialAdapterViewHolder>(){

    // El ID del layout debe ser el de la fila individual, no el de la actividad
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): HistorialAdapterViewHolder {
        // CORRECCIÓN CRÍTICA: Cambiado R.layout.historial por el nombre del layout del ítem
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_historial_compra, parent, false)
        return HistorialAdapterViewHolder(view)
        // Eliminado TODO("Not yet implemented")
    }

    override fun onBindViewHolder(holder: HistorialAdapterViewHolder, position: Int){
        val compra : Compra = listaCompras[position]

        // CORRECCIÓN: Usamos las propiedades correctas de la entidad Compra
        val idCompra = compra.idCompra
        val total = compra.total // Usamos el total de la compra
        val fecha = compra.fecha

        // Asignación a los TextViews
        holder.tvIdCompra.text = "Compra #${idCompra}"
        holder.tvFecha.text = fecha
        holder.tvTotal.text = String.format("Total: $%.2f", total)


    }

    override fun getItemCount(): Int {
        return listaCompras.size
        // Eliminado TODO("Not yet implemented")
    }

    inner class HistorialAdapterViewHolder(item: View): RecyclerView.ViewHolder(item) {
        // CORRECCIÓN: Renombradas las variables para que coincidan con el nuevo layout
        val tvIdCompra : TextView = itemView.findViewById(R.id.tv_historial_id)
        val tvFecha : TextView = itemView.findViewById(R.id.tv_historial_fecha)
        val tvTotal: TextView = itemView.findViewById(R.id.tv_historial_total) // Nuevo campo para el total
    }
}