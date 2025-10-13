package com.example.mitiendita.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.mitiendita.R
import com.example.mitiendita.databinding.FragmentCarritoBinding

class CarritoFragment : Fragment() {

    // 1. Usando View Binding para acceder a las vistas de forma segura
    private var _binding: FragmentCarritoBinding? = null
    // Esta propiedad solo es válida entre onCreateView y onDestroyView.
    private  val binding get() = _binding!!

//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        arguments?.let {
//        }
//    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflamos el layout usando View Binding
            _binding = FragmentCarritoBinding.inflate(inflater, container, false)
            return binding.root
        }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Aquí iría la lógica para configurar el RecyclerView,
        // cargar los datos del carrito (por ejemplo, desde una base de datos o ViewModel)
        // y configurar el botón de pago.

        setupRecyclerView()
        loadCartData()
        setupCheckoutButton()
    }

    private fun setupRecyclerView() {
        // TODO: Inicializar y configurar el Adapter y el LayoutManager para el RecyclerView.
        // Ejemplo:
        // val adapter = CartItemAdapter(listaDeArticulos)
        // binding.recyclerViewCartItems.adapter = adapter
    }

    private fun loadCartData() {
        // TODO: Lógica para obtener los artículos del carrito y actualizar la UI.
        // Esto generalmente se hace a través de un ViewModel para mantener la arquitectura limpia.

        // Simulación de actualización de total:
        val simulatedTotal = 150.75
        binding.tvCartTotal.text = "Total: $${"%.2f".format(simulatedTotal)}"
    }

    private fun setupCheckoutButton() {
        binding.btnCheckout.setOnClickListener {
            // TODO: Lógica para ir a la pantalla de pago (Checkout)
            // Ejemplo:
            // parentFragmentManager.beginTransaction()
            //     .replace(R.id.fragment_container, CheckoutFragment())
            //     .addToBackStack(null)
            //     .commit()

            // Un mensaje simple de prueba
            println("Procediendo al pago...")
        }
    }

    // 3. Limpieza de View Binding
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


}