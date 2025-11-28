package com.example.alt_ruido

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.volley.Request
import com.android.volley.toolbox.JsonArrayRequest
import com.example.alt_ruido.databinding.FragmentHomeBinding

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private val viewModel: EscuelasViewModel by viewModels()
    private lateinit var favoritesAdapter: EscuelasAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupFavoritesRecyclerView()
        setupButtons()
        checkSessionStatus()
    }

    override fun onResume() {
        super.onResume()
        // Sincronizar favoritos cada vez que la pantalla se vuelve visible
        if (SessionManager.isLoggedIn(requireContext())) {
            fetchFavoritesAndThenSchools()
        }
    }

    private fun setupFavoritesRecyclerView() {
        favoritesAdapter = EscuelasAdapter(emptyList()) { escuela ->
            val action = HomeFragmentDirections.actionHomeFragmentToFragmentEscuelaDetalle(escuela)
            findNavController().navigate(action)
        }
        binding.recyclerViewFavorites.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = favoritesAdapter
        }
    }

    private fun setupButtons() {
        binding.btnVerMapa.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_escuelasListFragment)
        }
    }

    private fun checkSessionStatus() {
        val isLoggedIn = SessionManager.isLoggedIn(requireContext())
        binding.favoritesSection.isVisible = isLoggedIn

        if (isLoggedIn) {
            observeViewModel()
        }
    }

    private fun fetchFavoritesAndThenSchools() {
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

    private fun observeViewModel() {
        viewModel.escuelas.observe(viewLifecycleOwner) { allSchools ->
            val favoriteIds = SessionManager.getFavoriteIds(requireContext())
            val favoriteSchools = allSchools.filter { it.id in favoriteIds }

            if (favoriteSchools.isEmpty()) {
                binding.tvNoFavorites.isVisible = true
                binding.recyclerViewFavorites.isVisible = false
            } else {
                binding.tvNoFavorites.isVisible = false
                binding.recyclerViewFavorites.isVisible = true
                favoritesAdapter.updateData(favoriteSchools)
            }
        }

        viewModel.error.observe(viewLifecycleOwner) {
            binding.tvNoFavorites.text = "Error al cargar las escuelas."
            binding.tvNoFavorites.isVisible = true
            binding.recyclerViewFavorites.isVisible = false
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
