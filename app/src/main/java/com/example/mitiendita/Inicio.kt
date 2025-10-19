package com.example.mitiendita

import android.os.Bundle
import android.widget.ImageView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import com.example.mitiendita.ui.CarritoFragment
import com.example.mitiendita.ui.CategoriaFragment
import com.example.mitiendita.ui.InicioFragment
import com.example.mitiendita.ui.ListaProductosFragment
import com.example.mitiendita.ui.ProductosFragment
import com.google.android.material.navigation.NavigationView

class Inicio : AppCompatActivity() {

        private lateinit var dlayMenu: DrawerLayout
        private lateinit var nvMenu: NavigationView
        private lateinit var ivMenu: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_inicio)

        dlayMenu = findViewById(R.id.dlayMenu)
        nvMenu = findViewById(R.id.nvMenu)
        ivMenu = findViewById(R.id.ivMenu)

        // 💡 Ajuste: Usar el listener para aplicar padding a la IV del menú
        // Esto asegura que el icono no quede detrás de la barra de estado del sistema.
        ViewCompat.setOnApplyWindowInsetsListener(ivMenu) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.updatePadding(top = systemBars.top) // Aplicar padding superior
            insets
        }

        // 1. Abre el menú lateral al tocar el ícono
        ivMenu.setOnClickListener {
            dlayMenu.open()
        }

        // 2. Maneja la selección de ítems del Navigation View
        nvMenu.setNavigationItemSelectedListener{ menuItem ->
            menuItem.isChecked = true // Marca el ítem como seleccionado
            dlayMenu.closeDrawers()   // Cierra el menú

            // Maneja las selecciones
            when (menuItem.itemId) {
                R.id.itInicio -> replaceFragment(InicioFragment())
                R.id.itCategoria -> replaceFragment(CategoriaFragment())
                R.id.itCarrito -> replaceFragment(CarritoFragment())
                R.id.itAgregarProducto -> replaceFragment(ProductosFragment())
                R.id.itProductos -> replaceFragment(ListaProductosFragment())

                // Agrega más casos según tus necesidades
                // R.id.itPerfil -> replaceFragment(PerfilFragment())
            }
            true
        }

        // 3. Cargar el fragmento inicial al crear la actividad
        replaceFragment(InicioFragment())
    }

    /**
     * Función genérica para reemplazar fragmentos en el contenedor.
     */
    private fun replaceFragment(fragment : Fragment){
        supportFragmentManager.beginTransaction()
            .replace(R.id.contenedorFragment, fragment)
            .commit()
    }
}