package com.example.mitiendita.entity

data class Productos(
    val id: Int,
    val title: String,
    val price: Double,
    val description: String,
    val category: String,
    val image: String,
    val rating: Rating?     // rating puede venir como objeto
)