package com.example.mitiendita

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.CheckBox
import android.widget.RadioGroup
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.mitiendita.database.DBHelper
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.radiobutton.MaterialRadioButton

class Registro : AppCompatActivity() {

    private lateinit var dbHelper: DBHelper

    // Usamos valores por defecto ya que los campos de 'edad' y 'dirección' no están en el XML final.
    private val EDAD_DEFAULT = 18
    private val DIRECCION_DEFAULT = "No especificada"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.registro)

        // Ajuste de barras del sistema
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Inicializar la base de datos
        dbHelper = DBHelper(this)

        // 1. Referencias a los campos UI (¡Coinciden con tu XML!)
        val tietDni = findViewById<TextInputEditText>(R.id.tietDni)
        val tietNombres = findViewById<TextInputEditText>(R.id.tietNombres)
        val tietApellidoPaterno = findViewById<TextInputEditText>(R.id.tietApellidoP) // tilApellidoP -> tietApellidoP
        val tietApellidoMaterno = findViewById<TextInputEditText>(R.id.tietApellidoM) // tilApellidoM -> tietApellidoM
        val tietCelular = findViewById<TextInputEditText>(R.id.tietCelular)
        val tietCorreo = findViewById<TextInputEditText>(R.id.tietCorreo)
        val tietClave = findViewById<TextInputEditText>(R.id.tietClave)
        val tietConfirmarClave = findViewById<TextInputEditText>(R.id.tietClave2) // Clave de Confirmación

        val rgSexo = findViewById<RadioGroup>(R.id.rgSexo)
        val chkTyc = findViewById<CheckBox>(R.id.cbTerminos) // Checkbox de Términos (cbTerminos)
        val btnRegistrar = findViewById<Button>(R.id.btnGuardar) // Botón de Guardar (btnGuardar)


        // 2. Acción del botón Registrar
        btnRegistrar.setOnClickListener {
            // Obtener datos
            val dni = tietDni.text.toString().trim()
            val nombres = tietNombres.text.toString().trim()
            val apellidoPaterno = tietApellidoPaterno.text.toString().trim()
            val apellidoMaterno = tietApellidoMaterno.text.toString().trim()
            val celular = tietCelular.text.toString().trim()
            val correo = tietCorreo.text.toString().trim()
            val clave = tietClave.text.toString()
            val claveConfirmar = tietConfirmarClave.text.toString()
            val aceptoTerminos = chkTyc.isChecked

            // Obtener el sexo seleccionado
            val idSexoSeleccionado = rgSexo.checkedRadioButtonId
            val sexo = if (idSexoSeleccionado != -1) {
                findViewById<MaterialRadioButton>(idSexoSeleccionado)?.text?.toString() ?: ""
            } else {
                "" // Vacío si no se seleccionó nada
            }


            // 3. Cadena de validaciones (Implementación completa)

            // Validar campos vacíos
            if (dni.isEmpty() || nombres.isEmpty() || apellidoPaterno.isEmpty() || apellidoMaterno.isEmpty() || celular.isEmpty() || correo.isEmpty() || clave.isEmpty() || claveConfirmar.isEmpty()){
                Toast.makeText(this, "Debe completar todos los campos obligatorios.", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }

            // Validar selección de sexo
            if (sexo.isEmpty()){
                Toast.makeText(this, "Debe seleccionar una opción de sexo.", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }

            // Validar coincidencia de contraseñas
            if (clave != claveConfirmar){
                Toast.makeText(this, "Las contraseñas no coinciden. Verifique.", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }

            // Validar aceptación de términos
            if (!aceptoTerminos){
                Toast.makeText(this, "Debe aceptar los términos y condiciones.", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }

            // 4. Registro en la base de datos
            val registroExitoso = dbHelper.registrarUsuario(
                dni = dni,
                nombres = nombres,
                apellidoP = apellidoPaterno,
                apellidoM = apellidoMaterno,
                // Usando valores por defecto
                sexo = sexo,
                telefono = celular,
                correo = correo,
                clave = clave
            )

            if (registroExitoso) {
                Toast.makeText(this, "¡Registro exitoso! Ahora puedes iniciar sesión.", Toast.LENGTH_LONG).show()

                // Navegación (Asumiendo que 'Acceso' es la Activity de Login)
                val intent = Intent(this, Acceso::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
                startActivity(intent)
                finish()
            } else {
                // Posible error: Correo ya existe (UNIQUE constraint en la BD)
                Toast.makeText(this, "Error al registrar. El correo podría estar ya en uso o el DNI ser duplicado.", Toast.LENGTH_LONG).show()
            }
        }
    }
}