package com.example.mitiendita

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.textfield.TextInputEditText

class ListaCompras : AppCompatActivity() {

    private lateinit var tietProducto: TextInputEditText
    private lateinit var ivAgregar: ImageView
    private lateinit var lvCompras: ListView
    private lateinit var btnHistorial: Button

    private lateinit var btnAgregar: Button
    private lateinit var adapter: ArrayAdapter<String>
    private val listaCompras = mutableListOf<String>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.lista)

        // Inicializar vistas|
        tietProducto = findViewById(R.id.tietProducto)
        ivAgregar = findViewById(R.id.ivAgregar)
        lvCompras = findViewById(R.id.lvCompras)
        btnHistorial = findViewById(R.id.btnHistorial)
        btnAgregar = findViewById(R.id.btnAgregar)

        // Inicializar Adaptador
        adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, listaCompras) //Diseño de cada elemento de lista
        lvCompras.adapter = adapter //datos a utilizar

        // Evento: agregar productos
        ivAgregar.setOnClickListener {
            val producto = tietProducto.text.toString().trim()
            if (producto.isNotEmpty()) {
                listaCompras.add(producto)
                adapter.notifyDataSetChanged()
                tietProducto.text?.clear()
            } else {
                Toast.makeText(this, "Escribe un producto", Toast.LENGTH_SHORT).show()
            }
        }



        // Evento: historial (ejemplo)
        btnHistorial.setOnClickListener {
            if (listaCompras.isEmpty()) {
                Toast.makeText(this, "No hay productos en el historial", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Tienes ${listaCompras.size} productos en la lista", Toast.LENGTH_SHORT).show()
            }
        }

        // Ajuste de insets
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        btnAgregar.setOnClickListener {
            intent = Intent(this, Productos::class.java)
            startActivity(intent)
            Toast.makeText(this, "Agregar", Toast.LENGTH_SHORT).show()
        }

        lvCompras.setOnItemClickListener { _, _, position, _ ->
            val dialog =layoutInflater.inflate(R.layout.opciones, null)
            val alertDialog= AlertDialog.Builder(this)
                .setView(dialog)
                .create()

            val tvTitulo = dialog.findViewById<TextView>(R.id.tvTitulo)
            val btnEliminar = dialog.findViewById<Button>(R.id.btnEliminar)
            val btnCancelar = dialog.findViewById<Button>(R.id.btnCancelar)
            tvTitulo.text = "Opciones para" + tietProducto.text
            alertDialog.show()

            btnCancelar.setOnClickListener {
                alertDialog.dismiss()
            }
            btnEliminar.setOnClickListener {
                listaCompras.removeAt(position)
                adapter.notifyDataSetChanged()
                alertDialog.dismiss()
            }
//            var producto = listaCompras[position]
//            listaCompras.removeAt(position)
//            adapter.notifyDataSetChanged()
        }

    }

}
