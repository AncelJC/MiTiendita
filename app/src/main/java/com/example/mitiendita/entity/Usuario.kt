package com.example.mitiendita.entity

data class Usuario (
    val codigo : Int,
    val nombre : String = "",
    val apellidoP : String = "",
    val apellidoM : String = "",
    val correo : String = "",
    val clave : String = ""
)