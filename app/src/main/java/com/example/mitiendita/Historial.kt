package com.example.mitiendita

import adapter.HistorialAdapter
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.mitiendita.entity.Compra

class Historial : AppCompatActivity() {
    private lateinit var rvHistorial: RecyclerView
    lateinit var historialAdapter: HistorialAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()
        setContentView(R.layout.historial)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val imeInsets =insets.getInsets(WindowInsetsCompat.Type.ime())
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            v.setPadding(
                systemBars.left,
                systemBars.top,
                systemBars.right,
                maxOf(systemBars.bottom, imeInsets.bottom)
            )
            insets
        }

        rvHistorial = findViewById(R.id.rvHistorial)

        val compras = listOf(
            Compra ("Leche", 4, "12/05/2023"),
            Compra ("Pan", 2, "12/05/2023",) ,
            Compra ("Gaseosa", 1, "12/05/2023")
        )
        // Inicializa el adaptador
        rvHistorial.layoutManager = LinearLayoutManager(this)
        // Orientación del adaptador
        historialAdapter = HistorialAdapter(compras)
        // Asigna el adaptador al RecyclerView
        rvHistorial.adapter = historialAdapter
        //rvHistorial.layoutManager = GridLayoutManager(this, 1))
    }
}