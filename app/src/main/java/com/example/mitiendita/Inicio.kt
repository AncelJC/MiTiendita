package com.example.mitiendita

import android.os.Bundle
import android.widget.ImageView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.example.mitiendita.ui.CarritoFragment
import com.example.mitiendita.ui.CategoriaFragment
import com.example.mitiendita.ui.InicioFragment
import com.google.android.material.navigation.NavigationView

class Inicio : AppCompatActivity() {

    private lateinit var dlayMenu: DrawerLayout
    private lateinit var navMenu: NavigationView
    private lateinit var ivMenu: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_inicio)

        dlayMenu = findViewById(R.id.dlayMenu)
        navMenu = findViewById(R.id.navMenu)
        ivMenu = findViewById(R.id.ivMenu)

        ivMenu.setOnClickListener {
            dlayMenu.open()
        }

        navMenu.setNavigationItemSelectedListener{ menuItem ->
            menuItem.isChecked = true // Define que se seleccione el item
            dlayMenu.closeDrawers()//Cierra el menu desplegable

            //Maneja las selecciones

            when (menuItem.itemId) {
                R.id.itInicio -> replaceFragment(InicioFragment())
                R.id.itCategoria -> replaceFragment(CategoriaFragment())
                R.id.itCarrito -> replaceFragment(CarritoFragment())
                R.id.itPerfil -> replaceFragment(PerfilFragment())
                else -> false
            }
        }



        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }
}