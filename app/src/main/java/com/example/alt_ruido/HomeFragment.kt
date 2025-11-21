package com.example.alt_ruido

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.alt_ruido.databinding.FragmentHomeBinding

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // La única responsabilidad de este fragment es navegar al siguiente.
        // Asumiendo que tu botón en fragment_home.xml tiene el id 'btn_ver_escuelas'
        // o el que corresponda.
        binding.btnVerMapa.setOnClickListener {
            // Usamos NavController para navegar a la pantalla de la lista de tarjetas.
            findNavController().navigate(R.id.action_homeFragment_to_escuelasListFragment)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
