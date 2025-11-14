package com.example.alt_ruido

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.GridLayout
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.alt_ruido.databinding.FragmentEscuelaDetalleBinding
import com.google.android.material.button.MaterialButton
import com.google.android.material.R as MaterialR
import com.google.android.material.color.MaterialColors
import org.json.JSONArray

class FragmentEscuelaDetalle : Fragment() {

    private var _binding: FragmentEscuelaDetalleBinding? = null
    private val binding get() = _binding!!
    private val args: FragmentEscuelaDetalleArgs by navArgs()

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
        mostrarBotonesDeTurno(escuela)
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
                // (Estilo del botón como lo tenías)
                textSize = 18f
                cornerRadius = 30
                val colorPrimary = MaterialColors.getColor(requireContext(), com.google.android.material.R.attr.colorPrimary, "Error")
                setTextColor(colorPrimary)
                strokeColor = android.content.res.ColorStateList.valueOf(colorPrimary)
                strokeWidth = 4
                layoutParams = ViewGroup.MarginLayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
                ).apply {
                    topMargin = 24
                }

                // Acción al hacer clic en un turno
                setOnClickListener {
                    binding.containerTurnos.removeAllViews() // Borra los otros botones de turno
                    this.isClickable = false // Evita que se pueda volver a hacer clic
                    binding.containerTurnos.addView(this) // Muestra solo el botón presionado
                    obtenerYMostrarAulas(escuela.id) // Llama a la función para cargar las aulas
                }
            }
            binding.containerTurnos.addView(boton)
        }
    }

    private fun obtenerYMostrarAulas(escuelaId: Int) {
        binding.tvTituloAulas.isVisible = true // Muestra el título "Aulas:"

        // **IMPORTANTE**: Reemplaza con la IP de tu servidor
        val url = "https://unseeking-acrimoniously-melodee.ngrok-free.dev/api/get_aulas.php?esc_id=$escuelaId"

        val request = StringRequest(Request.Method.GET, url,
            { response ->
                try {
                    val jsonArray = JSONArray(response)
                    val listaAulas = mutableListOf<Aula>()
                    for (i in 0 until jsonArray.length()) {
                        val jsonObject = jsonArray.getJSONObject(i)
                        listaAulas.add(
                            Aula(
                                id = jsonObject.getInt("id"),
                                num_aula = jsonObject.getInt("num_aula"),
                                esc_id = jsonObject.getInt("esc_id")
                            )
                        )
                    }
                    crearBotonesDeAulas(listaAulas) // Llama a la función para crear los botones
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

    private fun crearBotonesDeAulas(aulas: List<Aula>) {
        binding.containerAulas.removeAllViews() // Limpia botones anteriores

        aulas.forEach { aula ->
            val botonAula = MaterialButton(requireContext()).apply {
                text = aula.num_aula.toString()

                // Estilo del botón de aula
                val colorPrimary = MaterialColors.getColor(requireContext(), MaterialR.attr.colorPrimary, "Error")
                setBackgroundColor(colorPrimary)
                setTextColor(MaterialColors.getColor(requireContext(), MaterialR.attr.colorOnPrimary, "Error"))
                cornerRadius = 24

                // Parámetros para que el botón funcione dentro de un GridLayout
                val params = GridLayout.LayoutParams().apply {
                    columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f) // Ocupa una columna, con peso 1
                    width = 0 // Ancho 0 con peso 1 para distribución equitativa
                    height = GridLayout.LayoutParams.WRAP_CONTENT
                    setMargins(16, 16, 16, 16)
                }
                layoutParams = params

                setOnClickListener {
                    Toast.makeText(context, "Aula seleccionada: ${aula.num_aula}", Toast.LENGTH_SHORT).show()
                    // Aquí puedes navegar a la siguiente pantalla, pasándole el ID del aula
                }
            }
            binding.containerAulas.addView(botonAula)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
