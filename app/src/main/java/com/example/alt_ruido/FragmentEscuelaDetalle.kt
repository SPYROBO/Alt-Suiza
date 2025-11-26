package com.example.alt_ruido

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.GridLayoutManager
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.alt_ruido.databinding.FragmentEscuelaDetalleBinding
import com.google.android.material.R as MaterialR
import com.google.android.material.button.MaterialButton
import org.json.JSONArray

class FragmentEscuelaDetalle : Fragment() {
    private var _binding: FragmentEscuelaDetalleBinding? = null
    private val binding get() = _binding!!

    private val args: FragmentEscuelaDetalleArgs by navArgs()
    private lateinit var aulasAdapter: AulasAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentEscuelaDetalleBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val escuela = args.escuelaSeleccionada
        binding.tvEscuelaNombreDetalle.text = escuela.nombre

        setupRecyclerView()
        mostrarBotonesDeTurno(escuela)
    }

    private fun setupRecyclerView() {
        aulasAdapter = AulasAdapter(emptyList())
        binding.recyclerViewAulas.apply {
            layoutManager = GridLayoutManager(context, 3)
            adapter = aulasAdapter
        }
    }

    private fun mostrarBotonesDeTurno(escuela: Escuela) {
        val turnos = mutableListOf<String>()
        when {
            escuela.jornada.contains("Completa", ignoreCase = true) -> {
                turnos.add("Turno Mañana")
                turnos.add("Turno Tarde")
            }
            escuela.jornada.contains("Noche", ignoreCase = true) -> turnos.add("Turno Noche")
            escuela.jornada.contains("Tarde", ignoreCase = true) -> turnos.add("Turno Tarde")
            escuela.jornada.contains("Mañana", ignoreCase = true) -> turnos.add("Turno Mañana")
            else -> {
                turnos.add("Turno Mañana")
                turnos.add("Turno Tarde")
                turnos.add("Turno Noche")
            }
        }

        turnos.forEach { nombreTurno ->
            val boton = MaterialButton(requireContext(), null, MaterialR.attr.materialButtonOutlinedStyle).apply {
                text = nombreTurno
                textSize = 30f
                cornerRadius = 30
                setBackgroundColor(requireContext().getColor(R.color.light_blue_button))
                setTextColor(requireContext().getColor(R.color.dark_green_text))

                layoutParams = ViewGroup.MarginLayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
                ).apply {
                    topMargin = 24
                }

                setOnClickListener {
                    binding.containerTurnos.removeAllViews()
                    this.isClickable = false
                    binding.containerTurnos.addView(this)
                    obtenerYMostrarAulas(escuela.id)
                }
            }
            binding.containerTurnos.addView(boton)
        }
    }

    private fun obtenerYMostrarAulas(escuelaId: Int) {
        binding.tvTituloAulas.isVisible = true
        val url = "https://gangliar-chet-promptly.ngrok-free.dev/api/get_aulas.php?esc_id=$escuelaId"

        val request = StringRequest(Request.Method.GET, url,
            { response ->
                try {
                    val jsonArray = JSONArray(response)
                    val listaAulas = mutableListOf<Aula>()
                    for (i in 0 until jsonArray.length()) {
                        val jsonObject = jsonArray.getJSONObject(i)
                        val numAula = jsonObject.optInt("num_aula", 0)
                        if (numAula > 0) { // Solo añadir aulas con número válido
                            listaAulas.add(
                                Aula(
                                    id = jsonObject.optInt("id", 0),
                                    num_aula = numAula,
                                    esc_id = jsonObject.optInt("esc_id", 0)
                                )
                            )
                        }
                    }

                    if (listaAulas.isEmpty()) {
                        binding.tvTituloAulas.text = "No se encontraron aulas para mostrar."
                    } else {
                        aulasAdapter.updateData(listaAulas)
                    }

                } catch (e: Exception) {
                    Toast.makeText(context, "Error al procesar la respuesta del servidor", Toast.LENGTH_LONG).show()
                }
            },
            { error ->
                Toast.makeText(context, "Error de red: ${error.message}", Toast.LENGTH_LONG).show()
            }
        )
        Volley.newRequestQueue(requireContext()).add(request)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
