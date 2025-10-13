package com.example.mitiendita.entity

data class Usuario (
    val idUsua : Int,
    val dni : String = "",
    val nombres : String = "",
    val apellidoP : String = "",
    val apellidoM : String = "",
    val telefono : String = "",
    val sexo : String = "",
    val correo : String = "",
    val clave : String = "",
    val terminos : Boolean
)