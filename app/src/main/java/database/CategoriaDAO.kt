package com.example.mitiendita.database

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import com.example.mitiendita.entity.Categoria
import com.example.mitiendita.database.DBHelper

class CategoriaDAO(context: Context) {

    private val dbHelper = DBHelper(context)

    fun insertarCategoria(nombre: String): Long {
        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put("nombre", nombre)
        }
        return try {
            val result = db.insertOrThrow("categorias", null, values)
            db.close()
            result
        } catch (e: Exception) {
            db.close()
            -1L
        }
    }


    // Obtiene una lista de los IDs y nombres de todas las categorías.

    fun obtenerCategoriasConId(): List<Pair<Int, String>> {
        val categorias = mutableListOf<Pair<Int, String>>()
        val db = dbHelper.readableDatabase

        db.rawQuery(
            "SELECT idCat, nombre FROM categorias ORDER BY nombre ASC", null
        ).use { cursor ->
            while (cursor.moveToNext()) {
                val id = cursor.getInt(cursor.getColumnIndexOrThrow("idCat"))
                val nombre = cursor.getString(cursor.getColumnIndexOrThrow("nombre"))
                categorias.add(Pair(id, nombre))
            }
        }
        db.close()
        return categorias
    }
    // Dentro de CategoriaDAO.kt
// (Asegúrate de que esta clase tenga las funciones insertarCategoria, etc.)

    /**
     * Obtiene todas las categorías (ID y nombre).
     * @return Una lista de objetos Categoria.
     */
    fun obtenerTodasLasCategorias(): List<Categoria> {
        val db = dbHelper.readableDatabase
        val lista = mutableListOf<Categoria>()

        val cursor = db.rawQuery(
            "SELECT idCat, nombre FROM categorias ORDER BY nombre ASC",
            null
        )

        while (cursor.moveToNext()) {
            val id = cursor.getInt(cursor.getColumnIndexOrThrow("idCat"))
            val nombre = cursor.getString(cursor.getColumnIndexOrThrow("nombre"))

            lista.add(Categoria(idCat = id, nombre = nombre))
        }

        cursor.close()
        db.close()
        return lista
    }


    //Obtiene una lista de solo los nombres de todas las categorías.

    // Dentro de CategoriaDAO.kt

    /**
     * Obtiene solo los nombres de las categorías.
     * @return Una lista de Strings (nombres de categorías).
     */
    fun obtenerNombresCategorias(): List<String> {
        val db = dbHelper.readableDatabase
        val lista = mutableListOf<String>()

        val cursor = db.rawQuery(
            "SELECT nombre FROM categorias ORDER BY nombre ASC",
            null
        )

        while (cursor.moveToNext()) {
            val nombre = cursor.getString(cursor.getColumnIndexOrThrow("nombre"))
            lista.add(nombre)
        }

        cursor.close()
        db.close()
        return lista
    }

//    fun obtenerNombresCategorias(): List<String> {
//        val listaCategorias = mutableListOf<String>()
//        val db = dbHelper.readableDatabase
//        val cursor: Cursor = db.rawQuery("SELECT nombre FROM categorias ORDER BY nombre", null)
//
//        if (cursor.moveToFirst()) {
//            do {
//                val nombre = cursor.getString(cursor.getColumnIndexOrThrow("nombre"))
//                listaCategorias.add(nombre)
//            } while (cursor.moveToNext())
//        }
//        cursor.close()
//        db.close()
//        return listaCategorias
//    }


     //Obtiene el ID de una categoría a partir de su nombre.

    fun obtenerIdCategoriaPorNombre(nombreCat: String): Int {
        val db = dbHelper.readableDatabase
        val cursor: Cursor = db.rawQuery(
            "SELECT idCat FROM categorias WHERE nombre=?",
            arrayOf(nombreCat)
        )
        var id = -1
        if (cursor.moveToFirst()) {
            id = cursor.getInt(cursor.getColumnIndexOrThrow("idCat"))
        }
        cursor.close()
        db.close()
        return id
    }


     //Verifica si una categoría ya existe

    fun existeCategoria(nombre: String): Boolean {
        val db = dbHelper.readableDatabase
        val cursor = db.rawQuery(
            "SELECT COUNT(*) FROM categorias WHERE nombre = ?",
            arrayOf(nombre)
        )
        cursor.moveToFirst()
        val count = cursor.getInt(0)
        cursor.close()
        db.close()
        return count > 0
    }




     //Elimina una categoría por ID

    fun eliminarCategoria(id: Int): Int {
        val db = dbHelper.writableDatabase
        val result = db.delete("categorias", "idCat = ?", arrayOf(id.toString()))
        db.close()
        return result
    }
}