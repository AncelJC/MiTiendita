package adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.mitiendita.R
import com.example.mitiendita.entity.Compra
import com.example.mitiendita.entity.Producto

class HistorialAdapter (val listaCompras : List<Compra>): RecyclerView.Adapter<HistorialAdapter.HistorialAdapterViewHolder>(){
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): HistorialAdapterViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.historial, parent, false)
        return HistorialAdapterViewHolder(view)
        TODO("Not yet implemented")
    }

    override fun onBindViewHolder(holder: HistorialAdapterViewHolder, position: Int){
        val compra : Compra = listaCompras[position]
        val producto = compra.producto
        val cantidad = compra.catidad
        val fecha = compra.fecha

        holder.tvProducto.text = producto
        holder.tvCantidad.text = "" + cantidad
        holder.tvFecha.text = fecha


    }

    override fun getItemCount(): Int {
        return listaCompras.size
        TODO("Not yet implemented")
    }

    inner class HistorialAdapterViewHolder(item: View): RecyclerView.ViewHolder(item) {
        val tvProducto : TextView = itemView.findViewById(R.id.tvProducto)
        val tvCantidad: TextView = itemView.findViewById(R.id.tvCantidad)
        val tvFecha : TextView = itemView.findViewById(R.id.tvFecha)
    }
}