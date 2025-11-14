package com.example.alt_ruido

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.alt_ruido.databinding.FragmentGraficoBinding

class FragmentGrafico : Fragment() {

    private var _binding: FragmentGraficoBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentGraficoBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // Aquí puedes configurar tu gráfico
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}