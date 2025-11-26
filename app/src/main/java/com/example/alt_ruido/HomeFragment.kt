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

    private fun setupFavoritesRecyclerView() {
        favoritesAdapter = EscuelasAdapter(emptyList())
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
            viewModel.cargarEscuelas(requireContext()) // Carga todas las escuelas
        }
    }

    private fun observeViewModel() {
        viewModel.escuelas.observe(viewLifecycleOwner) { allSchools ->
            // Filtra la lista para obtener solo las favoritas
            val favoriteIds = SessionManager.getFavoriteIds(requireContext()) // Necesitamos este método
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
            // Opcional: manejar errores si la carga de escuelas falla
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
