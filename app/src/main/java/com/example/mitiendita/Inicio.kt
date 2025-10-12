package com.example.mitiendita

import android.os.Bundle
import android.view.Gravity
import android.widget.ImageView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import com.example.mitiendita.ui.CarritoFragment
import com.example.mitiendita.ui.CategoriaFragment
import com.example.mitiendita.ui.InicioFragment
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


        ivMenu.setOnClickListener {
            dlayMenu.open()
        }

        nvMenu.setNavigationItemSelectedListener{ menuItem ->
            menuItem.isChecked = true // Define que se seleccione el item
            dlayMenu.closeDrawers()//Cierra el menu desplegable

//            Maneja las selecciones

            when (menuItem.itemId) {
                R.id.itInicio -> replaceFragment(InicioFragment())
                R.id.itCategoria -> replaceFragment(CategoriaFragment())
                R.id.itCarrito -> replaceFragment(CarritoFragment())
//                R.id.itPerfil -> replaceFragment(PerfilFragment())

            }
            true
        }

        //Hace que el teclado del dispositivo no tape a las Views (EditText, TextImputEditText, etc)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.dlayMenu)) { v, insets ->
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
        replaceFragment(InicioFragment())
    }

    private fun replaceFragment(fragment : Fragment){
        supportFragmentManager.beginTransaction()
            .replace(R.id.contenedorFragment, fragment)
            .commit()
    }
}