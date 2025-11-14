package com.example.alt_ruido

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.alt_ruido.databinding.FragmentEscuelasListBinding

class EscuelasListFragment : Fragment() {

    private var _binding: FragmentEscuelasListBinding? = null
    private val binding get() = _binding!!

    // Usamos el mismo ViewModel para obtener los datos
    private val viewModel: EscuelasViewModel by viewModels()
    private lateinit var escuelasAdapter: EscuelasAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentEscuelasListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //  Configurar el RecyclerView y el Adapter (inicialmente con una lista vacía)
        setupRecyclerView()

        //   Observar los datos de las escuelas desde el ViewModel
        viewModel.escuelas.observe(viewLifecycleOwner) { listaDeEscuelas ->
            // Cuando llegan los datos, se los pasamos al adaptador
            escuelasAdapter.updateData(listaDeEscuelas)
            Toast.makeText(requireContext(), "${listaDeEscuelas.size} escuelas cargadas", Toast.LENGTH_SHORT).show()
        }

        //   Observar los errores
        viewModel.error.observe(viewLifecycleOwner) { mensajeDeError ->
            Toast.makeText(requireContext(), "Error: $mensajeDeError", Toast.LENGTH_LONG).show()
        }

        //  Iniciar la carga de datos (esto es lo que activa todo)
        viewModel.cargarEscuelas(requireContext())
    }

    private fun setupRecyclerView() {
        escuelasAdapter = EscuelasAdapter(emptyList())
        binding.recyclerViewEscuelas.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = escuelasAdapter
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
