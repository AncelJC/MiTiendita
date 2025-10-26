package com.example.mitiendita

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.mitiendita.database.DBHelper
import com.example.mitiendita.entity.Usuario
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout

class Acceso : AppCompatActivity() {


    private lateinit var btn_registrar: Button
    private lateinit var tv_recuperar: TextView
    private lateinit var btnIngresar: Button

    private lateinit var tietCorreo: TextInputEditText
    private lateinit var tietPassword: TextInputEditText

    private lateinit var liCorreo: TextInputLayout
    private lateinit var liPassword: TextInputLayout

    private lateinit var ivllamada: ImageView
    private lateinit var ivInternet: ImageView
    private lateinit var dbHelper: DBHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.acceso)

        // Inicializar BD
        dbHelper = DBHelper(this)

        // Enlazar vistas
        tv_recuperar = findViewById(R.id.tvRecuperar)
        btnIngresar = findViewById(R.id.btnIngresar)
        btn_registrar = findViewById(R.id.btnRegistrar)

        tietCorreo = findViewById(R.id.tietCorreo)
        tietPassword = findViewById(R.id.tietPassword) // Usando ID corregido del XML
        liCorreo = findViewById(R.id.liCorreo)
        liPassword = findViewById(R.id.liPassword)

        ivllamada = findViewById(R.id.ivllamada)
        ivInternet = findViewById(R.id.ivInternet)

        // ----------------------------------------------------
        // ACCIÓN CORREGIDA: Llama a la función validarCampos()
        // ----------------------------------------------------
        btnIngresar.setOnClickListener {
            validarCampos()
        }


        // Acción del botón Registrar
        btn_registrar.setOnClickListener {
            val intent = Intent(this, Registro::class.java)
            startActivity(intent)
            Log.d("AccesoActivity", "onClick: Iniciando Registro Activity.")
        }

        // Acción de Recuperar Contraseña
        tv_recuperar.setOnClickListener {
            val intent = Intent(this, Recuperar::class.java)
            startActivity(intent)
            Log.d("AccesoActivity", "onClick: Iniciando Recuperar Activity.")
        }


        // Ajuste de insets
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            Log.d("AccesoActivity", "onCreate: WindowInsetsListener configurado.")
            insets
        }

        // Acción del icono de Internet
        ivInternet.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW)
            intent.data = "https://github.com".toUri()
            startActivity(intent)
        }

        // Acción del icono de Llamada
        ivllamada.setOnClickListener {
            if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.CALL_PHONE)
                != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.CALL_PHONE), 1)
            } else {
                val intent = Intent(Intent.ACTION_CALL)
                intent.data = "tel:+51957633603".toUri()
                startActivity(intent)
            }
        }

        Log.d("AccesoActivity", "onCreate: Pantalla de Acceso cargada.")
    }

    // ----------------------------------------------------
    // FUNCIÓN DE VALIDACIÓN Y LOGIN (Lógica centralizada)
    // ----------------------------------------------------
    fun validarCampos() {
        val correo = tietCorreo.text.toString().trim()
        val clave = tietPassword.text.toString().trim()
        var error = false

        // Validación de campos vacíos
        if (correo.isEmpty()) {
            liCorreo.error = "Ingrese su correo"
            error = true
        } else {
            liCorreo.error = null
        }

        if (clave.isEmpty()) {
            liPassword.error = "Ingrese su contraseña"
            error = true
        } else {
            liPassword.error = null
        }

        if (error) return // Detener si hay errores de campos vacíos

        // Validar contra la base de datos
        val usuario: Usuario? = dbHelper.validarUsuario(correo, clave)

        if (usuario != null) {
            Toast.makeText(this, "✅ Bienvenido ${usuario.nombres}", Toast.LENGTH_SHORT).show()

            // 💡 CAMBIO CRÍTICO AQUÍ: Navegación a Activity_Inicio
            startActivity(Intent(this, Inicio::class.java))
            finish()
        } else {
            Toast.makeText(this, "❌ Correo o contraseña incorrectos", Toast.LENGTH_SHORT).show()
        }
    }

    // Manejo de la respuesta de solicitud de permisos
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 1 && grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            // Permiso concedido
            val intent = Intent(Intent.ACTION_CALL)
            intent.data = "tel:+51957633603".toUri()
            startActivity(intent)
        } else {
            Toast.makeText(this, "Permiso de llamada denegado. No se puede realizar la llamada.", Toast.LENGTH_SHORT).show()
        }
    }
}