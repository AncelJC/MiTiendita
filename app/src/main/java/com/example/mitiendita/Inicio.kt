package com.example.mitiendita

import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.widget.ImageView
import androidx.activity.OnBackPressedCallback
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
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
import com.example.mitiendita.viewmodel.CarritoViewModel
import com.google.android.material.navigation.NavigationView

class Inicio : AppCompatActivity() {

    private lateinit var dlayMenu: DrawerLayout
    private lateinit var nvMenu: NavigationView
    private lateinit var ivMenu: ImageView

    val carritoViewModel: CarritoViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_inicio)

        // Inicialización de vistas
        dlayMenu = findViewById(R.id.dlayMenu)
        nvMenu = findViewById(R.id.nvMenu)
        ivMenu = findViewById(R.id.ivMenu)

        // Ajuste del padding superior para el ícono del menú (modo edge-to-edge)
        ViewCompat.setOnApplyWindowInsetsListener(ivMenu) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.updatePadding(top = systemBars.top)
            insets
        }

        // Abrir menú lateral al tocar el ícono del menú
        ivMenu.setOnClickListener {
            dlayMenu.open()
        }

        // Navegación lateral
        nvMenu.setNavigationItemSelectedListener { menuItem ->
            menuItem.isChecked = true
            dlayMenu.closeDrawers()

            when (menuItem.itemId) {
                R.id.itInicio -> replaceFragment(InicioFragment(), "inicio")
                R.id.itCategoria -> replaceFragment(CategoriaFragment(), "categoria")
                R.id.itCarrito -> replaceFragment(CarritoFragment(), "carrito")
                R.id.itAgregarProducto -> replaceFragment(ProductosFragment(), "agregar_producto")
                R.id.itProductos -> replaceFragment(ListaProductosFragment(), "lista_productos")
            }
            true
        }

        // Control del botón "Atrás"
        setupBackPressHandler()

        // Fragment inicial
        if (savedInstanceState == null) {
            replaceFragment(InicioFragment(), "inicio")
        }

        // Observar cambios en el carrito para actualizar badge
        setupCarritoObserver()

        // Permiso de notificaciones y recordatorio
        solicitarPermisoNotificaciones()
    }

    private fun replaceFragment(fragment: Fragment, tag: String) {
        val currentFragment = supportFragmentManager.findFragmentByTag(tag)
        if (currentFragment != null && currentFragment.isVisible) return

        supportFragmentManager.beginTransaction()
            .replace(R.id.contenedorFragment, fragment, tag)
            .addToBackStack(tag)
            .commit()
    }

    private fun setupBackPressHandler() {
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                when {
                    dlayMenu.isDrawerOpen(GravityCompat.START) -> {
                        dlayMenu.closeDrawer(GravityCompat.START)
                    }

                    supportFragmentManager.backStackEntryCount > 1 -> {
                        supportFragmentManager.popBackStack()
                    }

                    supportFragmentManager.backStackEntryCount == 1 -> {
                        if (supportFragmentManager.findFragmentById(R.id.contenedorFragment) is InicioFragment) {
                            showExitConfirmation(this)
                        } else {
                            supportFragmentManager.popBackStack()
                        }
                    }

                    else -> finish()
                }
            }
        })
    }


    private fun setupCarritoObserver() {
        carritoViewModel.carritoItems.observe(this) { items ->
            val totalItems = items.sumOf { it.cantidad }
            actualizarBadgeCarrito(totalItems)
        }
    }

    private fun actualizarBadgeCarrito(totalItems: Int) {
        val carritoMenuItem = nvMenu.menu.findItem(R.id.itCarrito)

        if (totalItems > 0) {
            carritoMenuItem.title = "Carrito ($totalItems)"
        } else {
            carritoMenuItem.title = "Carrito"
        }
    }


    private fun showExitConfirmation(callback: OnBackPressedCallback) {
        android.app.AlertDialog.Builder(this)
            .setTitle("Salir")
            .setMessage("¿Deseas salir de MiTiendita?")
            .setPositiveButton("Sí") { _, _ ->
                callback.isEnabled = false
                onBackPressedDispatcher.onBackPressed()
            }
            .setNegativeButton("No", null)
            .show()
    }

    private fun solicitarPermisoNotificaciones() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    this,
                    android.Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                requestPermissions(
                    arrayOf(android.Manifest.permission.POST_NOTIFICATIONS),
                    1000
                )
            }
        }
    }


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 1000) {
            val mensaje = if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                "Permiso de notificaciones concedido ✅"
            } else {
                "Permiso de notificaciones denegado ❌"
            }
            android.widget.Toast.makeText(this, mensaje, android.widget.Toast.LENGTH_SHORT).show()
        }
    }
}