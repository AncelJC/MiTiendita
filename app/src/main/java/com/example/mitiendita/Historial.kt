package com.example.mitiendita

import adapter.HistorialAdapter
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.mitiendita.entity.Compra // Importamos la entidad correcta

class Historial : AppCompatActivity() {
    private lateinit var rvHistorial: RecyclerView
    private lateinit var historialAdapter: HistorialAdapter // 'private' por buena práctica

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()
        setContentView(R.layout.historial)

        // Configuración de Insets (mantenemos tu código)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val imeInsets = insets.getInsets(WindowInsetsCompat.Type.ime())
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(
                systemBars.left,
                systemBars.top,
                systemBars.right,
                maxOf(systemBars.bottom, imeInsets.bottom)
            )
            insets
        }

        rvHistorial = findViewById(R.id.rvHistorial)

        // --- CORRECCIÓN: Lista de prueba alineada con la entidad Compra (Cabecera) ---
        // Se recomienda obtener los datos reales desde la base de datos usando un DAO aquí.
        val comprasHistorial: List<Compra> = listOf(
            Compra(idCompra = 101, fecha = "12/05/2023", total = 45.50, idUsua = 1),
            Compra(idCompra = 102, fecha = "15/05/2023", total = 120.99, idUsua = 1),
            Compra(idCompra = 103, fecha = "01/06/2023", total = 8.00, idUsua = 1)
        )
        // -----------------------------------------------------------------------------

        // 1. Inicializa el adaptador
        historialAdapter = HistorialAdapter(comprasHistorial)

        // 2. Asigna el LayoutManager (LinearLayoutManager es correcto para una lista vertical)
        rvHistorial.layoutManager = LinearLayoutManager(this)

        // 3. Asigna el adaptador al RecyclerView
        rvHistorial.adapter = historialAdapter

        // Nota: Si quieres cambiar a GridLayoutManager, debes descomentar y asegurarte
        // de que el número de columnas (1) tenga sentido para tu diseño.
        // rvHistorial.layoutManager = GridLayoutManager(this, 1)
    }
}