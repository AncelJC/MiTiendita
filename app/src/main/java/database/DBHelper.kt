package com.example.mitiendita.database

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.example.mitiendita.entity.Usuario

class DBHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object{
        private const val DATABASE_NAME = "usuarios.db"
        private const val DATABASE_VERSION = 1
        private const val TABLE_NAME = "usuario"
        private const val COL_ID = "id"
        private const val COL_NOMBRES = "nombres"
        private const val COL_APELLIDOSP = "apellidosP"
        private const val COL_APELLIDOSM = "apellidosM"
        private const val COL_CORREO = "correo"
        private const val COL_CONTRASEÑA = "contraseña"
        private const val COL_SEXO = "sexo"
        private const val COL_TERMINOS = "aceptaTerminos"

    }


    override fun onCreate(db: SQLiteDatabase) {
        // Crear tabla Usuarios
        val createTable = ("CREATE TABLE $TABLE_NAME("
                + "$COL_ID INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "$COL_NOMBRES TEXT,"
                + "$COL_APELLIDOSP TEXT,"
                + "$COL_APELLIDOSM TEXT,"
                + "$COL_CORREO TEXT,"
                + "$COL_CONTRASEÑA TEXT,"
                + "$COL_SEXO TEXT,"
                + "$COL_TERMINOS INTEGER)")

        // Crear tabla Productos
        db.execSQL(createTable)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_NAME")
        onCreate(db)
    }

    // ============================
    // INSERTAR USUARIOS
    // ============================

    fun registrarUsuario(usuario: Usuario): Long {
        val db = this.writableDatabase
        val values = ContentValues().apply {
            put(COL_NOMBRES, usuario.nombres)
            put(COL_APELLIDOSP, usuario.apellidoP)
            put(COL_APELLIDOSM, usuario.apellidoM)
            put(COL_CORREO, usuario.correo)
            put(COL_CONTRASEÑA, usuario.contraseña)
            put(COL_SEXO, usuario.sexo)
            put(COL_TERMINOS, if (usuario.aceptaTerminos)1 else 0)
        }
        return db.insert( TABLE_NAME, null, values)
    }


    fun validarUsuario(correo: String, contraseña: String): Boolean {
        val db = this.readableDatabase
        val cursor: Cursor = db.rawQuery(
            "SELECT * FROM $TABLE_NAME WHERE $COL_CORREO = ? AND $COL_CONTRASEÑA = ?",
            arrayOf(correo, contraseña)
        )
        val existe = cursor.moveToFirst()
        cursor.close()
        db.close()
        return existe
    }

    fun obtenertNombreUsuario (correo: String, clave: String): String? {
        val db = this.readableDatabase
        val cursor = db.rawQuery(
            "SELECT $COL_NOMBRES FROM $TABLE_NAME WHERE $COL_CORREO = ? AND $COL_CONTRASEÑA = ?" ,
            arrayOf(correo, clave)
        )
        var nombre : String? = null
        if (cursor.moveToFirst()){
            nombre = cursor.getString(0)//obtiene el valor de "nomnbre"
        }
        cursor.close()
        db.close()
        return nombre
    }
//
//    // ============================
//    // PRODUCTOS
//    // ============================
//
//    fun insertarProducto(nombre: String, descripcion: String, precio: Double, stock: Int): Long {
//        val db = writableDatabase
//        val values = ContentValues().apply {
//            put("nombre", nombre)
//            put("descripcion", descripcion)
//            put("precio", precio)
//            put("stock", stock)
//        }
//        val result = db.insert("productos", null, values)
//        db.close()
//        return result
//    }
//
//    fun obtenerProductos(): Cursor {
//        val db = readableDatabase
//        return db.rawQuery("SELECT * FROM productos", null)
//    }
//
//    fun actualizarProducto(id: Int, nombre: String, descripcion: String, precio: Double, stock: Int): Int {
//        val db = writableDatabase
//        val values = ContentValues().apply {
//            put("nombre", nombre)
//            put("descripcion", descripcion)
//            put("precio", precio)
//            put("stock", stock)
//        }
//        val result = db.update("productos", values, "id = ?", arrayOf(id.toString()))
//        db.close()
//        return result
//    }
//
//    fun eliminarProducto(id: Int): Int {
//        val db = writableDatabase
//        val result = db.delete("productos", "id = ?", arrayOf(id.toString()))
//        db.close()
//        return result
//    }

}
