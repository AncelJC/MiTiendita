package com.example.mitiendita.entity

data class Usuario (
    val codigo : Int,
    val nombres : String = "",
    val apellidoP : String = "",
    val apellidoM : String = "",
    val correo : String = "",
    val contraseña : String = "",
    val sexo : String = "",
    val aceptaTerminos: Boolean

)