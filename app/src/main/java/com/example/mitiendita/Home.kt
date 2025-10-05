package com.example.mitiendita

import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.mitiendita.databinding.ActivityHomeBinding

class   Home : AppCompatActivity() {
    private var productos = ArrayList<String>()
    lateinit var ArrayAdapter: ArrayAdapter<*>
    lateinit var binding: ActivityHomeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val productos = arrayOf("producto1", "producto2",
            "producto3", "producto4", "producto5", "producto6", "producto7", "producto8", "producto9", "producto10")
        var itemAdapter = ArrayAdapter(Home@this, android.R.layout.simple_list_item_1, productos)
        binding.listap.adapter = itemAdapter

        binding.regresar.setOnClickListener (
            {
                startActivity(Intent(this, MainActivity::class.java))

            }
        )
        enableEdgeToEdge()
        setContentView(R.layout.activity_home)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }
}