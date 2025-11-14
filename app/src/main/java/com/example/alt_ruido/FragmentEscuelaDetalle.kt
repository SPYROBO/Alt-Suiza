package com.example.alt_ruido

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import com.example.alt_ruido.databinding.FragmentEscuelaDetalleBinding
import com.google.android.material.button.MaterialButton
import com.google.android.material.R as MaterialR // Alias para el estilo del botón
import com.google.android.material.color.MaterialColors // Para obtener colores del tema

class FragmentEscuelaDetalle : Fragment() {

    // ViewBinding para acceder a las vistas del layout de forma segura
    private var _binding: FragmentEscuelaDetalleBinding? = null
    private val binding get() = _binding!!

    // SafeArgs para recibir el objeto 'Escuela' de forma segura
    private val args: FragmentEscuelaDetalleArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Infla el layout del fragmento y lo asigna al binding
        _binding = FragmentEscuelaDetalleBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Obtiene la escuela pasada como argumento
        val escuela = args.escuelaSeleccionada

        // Asigna el nombre de la escuela al TextView correspondiente
        binding.tvEscuelaNombreDetalle.text = escuela.nombre

        // Llama a la función para crear los botones de turno basados en la jornada
        mostrarBotonesDeTurno(escuela.jornada)
    }

    /**
     * Crea y muestra dinámicamente los botones de turno (Mañana, Tarde, Noche)
     * basados en la jornada de la escuela.
     */
    private fun mostrarBotonesDeTurno(jornada: String) {
        val turnos = mutableListOf<String>()

        // Determina qué turnos mostrar según el string de la jornada
        when {
            jornada.contains("Completa", ignoreCase = true) -> {
                turnos.add("Turno Mañana")
                turnos.add("Turno Tarde")
            }
            jornada.contains("Noche", ignoreCase = true) -> turnos.add("Turno Noche")
            jornada.contains("Tarde", ignoreCase = true) -> turnos.add("Turno Tarde")
            jornada.contains("Mañana", ignoreCase = true) -> turnos.add("Turno Mañana")
            else -> { // Si no se especifica, muestra todos como opción por defecto
                turnos.add("Turno Mañana")
                turnos.add("Turno Tarde")
                turnos.add("Turno Noche")
            }
        }

        // Itera sobre la lista de turnos y crea un botón para cada uno
        turnos.forEach { nombreTurno ->
            // Crea una instancia de MaterialButton con el estilo "Outlined"
            val boton = MaterialButton(requireContext(), null, MaterialR.attr.materialButtonOutlinedStyle).apply {
                text = nombreTurno
                textSize = 18f
                cornerRadius = 30

                // --- LÓGICA DE ESTILO DEL BOTÓN (CORREGIDA Y LIMPIA) ---

                // 1. Obtiene el color primario del tema actual de tu app.
                val colorPrimary = MaterialColors.getColor(requireContext(), com.google.android.material.R.attr.colorPrimary, "Color primario no encontrado")

                // 2. Aplica el color al texto y al borde del botón.
                setTextColor(colorPrimary)
                // Forma directa y recomendada para asignar el color del borde.
                strokeColor = android.content.res.ColorStateList.valueOf(colorPrimary)
                // Asegúrate de que el borde tenga un ancho para que sea visible.
                strokeWidth = 4

                // 3. Configura los márgenes del botón para que no estén pegados.
                layoutParams = ViewGroup.MarginLayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, // Ancho completo
                    ViewGroup.LayoutParams.WRAP_CONTENT  // Alto ajustado al contenido
                ).apply {
                    topMargin = 24 // Margen superior de 24 píxeles
                }
            }
            // Añade el botón recién creado al contenedor en tu layout
            binding.containerTurnos.addView(boton)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        // Limpia la referencia al binding para evitar fugas de memoria
        _binding = null
    }
}
