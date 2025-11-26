package com.example.alt_ruido

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.alt_ruido.databinding.FragmentEscuelasListBinding

class EscuelasListFragment : Fragment() {

    private var _binding: FragmentEscuelasListBinding? = null
    private val binding get() = _binding!!

    private var listaCompletaDeEscuelas = listOf<Escuela>()
    private lateinit var adapter: EscuelasAdapter
    private val viewModel: EscuelasViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentEscuelasListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        setupSearchView()
        observeViewModel()
        viewModel.cargarEscuelas(requireContext())
    }

    private fun setupRecyclerView() {
        adapter = EscuelasAdapter(listOf())
        binding.recyclerViewEscuelas.layoutManager = LinearLayoutManager(context)
        binding.recyclerViewEscuelas.adapter = adapter
    }

    private fun setupSearchView() {
        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                binding.searchView.clearFocus()
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                filtrarLista(newText)
                return true
            }
        })
    }

    private fun filtrarLista(texto: String?) {
        val listaParaMostrar = if (texto.isNullOrEmpty()) {
            listaCompletaDeEscuelas
        } else {
            val textoBusqueda = texto.lowercase().trim()
            listaCompletaDeEscuelas.filter {
                it.nombre.lowercase().contains(textoBusqueda)
            }
        }
        adapter.updateData(listaParaMostrar)
    }

    private fun observeViewModel() {
        binding.progressBar.visibility = View.VISIBLE
        binding.contentContainer.visibility = View.GONE

        viewModel.escuelas.observe(viewLifecycleOwner) {
            escuelas ->
            binding.progressBar.visibility = View.GONE
            binding.contentContainer.visibility = View.VISIBLE
            listaCompletaDeEscuelas = escuelas
            adapter.updateData(escuelas)
        }

        viewModel.error.observe(viewLifecycleOwner) {
            error ->
            binding.progressBar.visibility = View.GONE
            Toast.makeText(context, error, Toast.LENGTH_LONG).show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
