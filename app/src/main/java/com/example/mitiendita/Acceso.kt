// En Acceso.kt
package com.example.mitiendita

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log // Para depuración
import android.widget.Button
import android.widget.EditText
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
    private lateinit var btn_recuperar: Button
    private lateinit var btnIngresar: Button

    private lateinit var tietCorreo: TextInputEditText
    private lateinit var tvPassword: TextInputEditText

    private lateinit var liCorreo: TextInputLayout
    private lateinit var liPassword: TextInputLayout
    var tvRecuperar : TextView? = null

    private lateinit var ivllamada: ImageView
    private lateinit var ivInternet: ImageView

    private val listaUsuarios = mutableListOf(
        Usuario( 1, "Jose Martin", "Sanchez", "Flores", "jsanchez@cibertec.edu.pe", "123456", "Masculino", true),
        Usuario( 2, "Jose Martin", "Sanchez", "Flores", "jsanchez", "123456", "Masculino", true),
        Usuario(3, "admin", "admin", "admin", "admin", "123", "Masculino", true)

    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.acceso)

        val db = DBHelper(this)

        val etCorreo = findViewById<EditText>(R.id.tietCorreo)
        val etPassword = findViewById<EditText>(R.id.tvPassword)
        val btnIngresar = findViewById<Button>(R.id.btnLogin)
        val btnRegistrar = findViewById<Button>(R.id.btnRegistrar)

        tietCorreo = findViewById(R.id.tietCorreo)
        tvPassword = findViewById(R.id.tvPassword)
        liCorreo = findViewById(R.id.liCorreo)
        liPassword = findViewById(R.id.liPassword)



        ivllamada = findViewById(R.id.ivllamada)
        ivInternet = findViewById(R.id.ivInternet)

        btnIngresar.setOnClickListener {
            validarCampos()
        }


//        btnIngresar.setOnClickListener {
//            var correo = tietCorreo.text.toString()
//            var password = etPassword.text.toString()
//            val valido = db.validarUsuario(correo, password)
//            if (valido) {
//                Toast.makeText(this, "✅ Bienvenido", Toast.LENGTH_SHORT).show()
//                startActivity(Intent(this, MainActivity::class.java))
//            } else {
//                Toast.makeText(this, "❌ Correo o contraseña incorrectos", Toast.LENGTH_SHORT).show()
//            }
//        }

        btnRegistrar.setOnClickListener {
            startActivity(Intent(this, Registro::class.java))
        }



        Log.d("AccesoActivity", "onCreate: Pantalla de Acceso cargada.")

        btn_registrar = findViewById(R.id.btnRegistrar) // Asegúrate que este ID sea el de tu botón en acceso.xml
        //Log.d("AccesoActivity", "onCreate: Botón btn_registrar encontrado.")

        btn_registrar.setOnClickListener {
            //Log.d("AccesoActivity", "onClick: Botón btn_registrar presionado!")
            val intent = Intent(this, Registro::class.java)
            var correo : String ="" + tietCorreo.text
            intent.putExtra("", correo)
            startActivity(intent)
            Log.d("AccesoActivity", "onClick: Iniciando Registro Activity.")
//            try {
//                startActivity(intent)
//                Log.d("AccesoActivity", "onClick: Iniciando Registro Activity.")
//            } catch (e: Exception) {
//                Log.e("AccesoActivity", "onClick: Error al iniciar Registro Activity", e)
//            }
        }

        tvRecuperar = findViewById(R.id.tvRecuperar)
        tvRecuperar?.setOnClickListener {
            val intent = Intent(this, Recuperar::class.java)
            startActivity(intent)
            Log.d("AccesoActivity", "onClick: Iniciando Recuperar Activity.")
        }


        // Asegúrate que R.id.tvAcceso sea el ID del layout raíz o principal en acceso.xml
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            Log.d("AccesoActivity", "onCreate: WindowInsetsListener configurado.")
            insets
        }

        ivInternet.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW)
            intent.setData("https://github.com".toUri())
            startActivity(intent)
        }

//        ivllamada.setOnClickListener {
//            val intent = Intent(Intent.ACTION_CALL)
//            intent.data= "tel:+51957633603".toUri()
//            startActivity(intent)
//        }
//
        ivllamada.setOnClickListener {
            if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.CALL_PHONE)
            != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.CALL_PHONE), 1)
            }
            else {
                val intent = Intent(Intent.ACTION_CALL)
                intent.data = "tel:+51957633603".toUri()
                startActivity(intent)
            }


        }



    }

    fun validarCampos() {
        val correo = tietCorreo.text.toString().trim()
        val clave = tvPassword.text.toString().trim()
        var error = false

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

        if (error) return

        // Validación con lista de usuarios "quemada"
        var usuario: Usuario? = null
        for (u in listaUsuarios) {
            if ((u.correo == correo || u.correo == "$correo@cibertec.edu.pe") && u.contraseña == clave) {
                usuario = u
                break
            }
        }

        if (usuario != null) {
            Toast.makeText(this, "✅ Bienvenido ${usuario.nombres}", Toast.LENGTH_SHORT).show()
            startActivity(Intent(this, Inicio::class.java))
        } else {
            Toast.makeText(this, "❌ Usuario o contraseña incorrectos", Toast.LENGTH_SHORT).show()
        }
    }


//    fun validarCampos(){
//        val correo = tietCorreo.text.toString()
//        val clave = tvPassword.text.toString()
//        var error : Boolean = false
//
//        if (correo.isEmpty()){
//            liCorreo.error = "Ingrese su correo"
//        } else{
//            liCorreo.error = null
//        }
//        if (clave.isEmpty()){
//            liPassword.error = "Ingrese su contraseña"
//        } else{
//            liPassword.error = null
//        }
//        if(!error){
//            startActivity(Intent(this, Productos::class.java))
//        }
//
//        var usuario : Usuario ?= null
////            for (i in 0 until listaUsuarios.size){
////             if (listaUsuarios[i].correo == correo){
////                 usuario = listaUsuarios[i]
////             }
//
//
////                }
//        for (u in listaUsuarios){
//            if(u.correo == (correo + "@cibertec.edu.pe")&& u.clave == clave )
//                usuario = u
//        }
//        if (usuario != null){
//            startActivity(Intent(this, Productos::class.java))
//            Toast.makeText(this, "✅ Bienvenido" + usuario.nombre, Toast.LENGTH_SHORT).show()
//            Toast.makeText(this, "✅ Bienvenido" + usuario.nombre, Toast.LENGTH_SHORT).show()
//        }
//        else{
//            Toast.makeText(this, "El usuario o contraseña son incorrectos", Toast.LENGTH_SHORT).show()
//        }
//    }




}

