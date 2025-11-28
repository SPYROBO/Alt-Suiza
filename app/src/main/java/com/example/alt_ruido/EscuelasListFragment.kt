package com.example.alt_ruido

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.volley.Request
import com.android.volley.toolbox.JsonArrayRequest
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
        fetchFavoritesAndThenSchools()
    }

    private fun setupRecyclerView() {
        adapter = EscuelasAdapter(emptyList()) { escuela ->
            val action = EscuelasListFragmentDirections.actionEscuelasListFragmentToFragmentEscuelaDetalle(escuela)
            findNavController().navigate(action)
        }
        binding.recyclerViewEscuelas.layoutManager = LinearLayoutManager(context)
        binding.recyclerViewEscuelas.adapter = adapter
    }

    private fun fetchFavoritesAndThenSchools() {
        if (!SessionManager.isLoggedIn(requireContext())) {
            viewModel.cargarEscuelas(requireContext())
            return
        }

        val userId = SessionManager.getUserId(requireContext())
        val url = "${ApiConfig.BASE_URL}/get_favoritos.php?usuario_id=$userId"

        val jsonArrayRequest = JsonArrayRequest(Request.Method.GET, url, null,
            { response ->
                try {
                    val favoriteIds = mutableSetOf<String>()
                    for (i in 0 until response.length()) {
                        favoriteIds.add(response.getInt(i).toString())
                    }
                    SessionManager.setFavorites(requireContext(), favoriteIds)
                } catch (e: Exception) {
                    // No hacer nada si falla, se usarán los favoritos locales
                } finally {
                    viewModel.cargarEscuelas(requireContext())
                }
            },
            { _ -> // Ignorar error de red de favoritos
                viewModel.cargarEscuelas(requireContext())
            }
        )
        VolleySingleton.getInstance(requireContext()).addToRequestQueue(jsonArrayRequest)
    }

    private fun setupSearchView() {
        binding.searchView.setOnClickListener {
            binding.searchView.isIconified = false
        }

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
            
            // --- CORRECCIÓN AQUÍ ---
            // Re-aplica el filtro con el texto que ya está en la barra de búsqueda
            filtrarLista(binding.searchView.query.toString())
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
