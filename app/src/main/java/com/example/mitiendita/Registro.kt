package com.example.mitiendita

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.textfield.TextInputEditText


class Registro : AppCompatActivity() {

    private lateinit var tvCorreo : TextInputEditText
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.registro)

        tvCorreo = findViewById(R.id.tietCorreo)
        val intent = this.intent
        if(intent != null){
            var correo = intent.getStringExtra("correo")
        }


        // Ajuste de barras del sistema
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Acción del botón Registrar -> mostrar Toast y volver a Acceso
        val btnRegistrar = findViewById<Button>(R.id.btnRegistrar)
        btnRegistrar.setOnClickListener {
            Toast.makeText(this, "✅ Registro exitoso", Toast.LENGTH_SHORT).show()

            val intent = Intent(this, Acceso::class.java)
            startActivity(intent)
            finish()
        }
    }
}
