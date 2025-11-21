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
//USAR BINDING SIRVE ACCEDER A LAS DEMÁS VISTAS
    private var _binding: FragmentEscuelaDetalleBinding? = null
    private val binding get() = _binding!!

    // SE ENVIA EL OBJ ESCUELA
    private val args: FragmentEscuelaDetalleArgs by navArgs()

    /// CONVIERTE EL LAYOUT XML EN VISTAS
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentEscuelaDetalleBinding.inflate(inflater, container, false)
        return binding.root //lA VISA RAIZ ES DEVUELTA
    }

    // SE LLAMA LUEGO DE QUE LA VISTA FUE CREADA
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val escuela = args.escuelaSeleccionada
        binding.tvEscuelaNombreDetalle.text = escuela.nombre

        // LLAMADA A LA FUNCION PARA CREAR TURNOS
        mostrarBotonesDeTurno(escuela)
    }

    // FUNCION PARA CREAR TURNOS
    private fun mostrarBotonesDeTurno(escuela: Escuela) {
        // SE CREA UNA LISTA MODIFICABLE
        val turnos = mutableListOf<String>()
        when {
            // SE AÑADE EL TURNO DEPENDIENDO LO QUE CONTENGA JORNADA
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
            // SE AÑADEN ESTILOS PARA LOS BOTONES
            val boton = MaterialButton(requireContext(), null, MaterialR.attr.materialButtonOutlinedStyle).apply {
                text = nombreTurno
                textSize = 30f
                cornerRadius = 30
                // el color lo declare en la carpeta theme
                setBackgroundColor(requireContext().getColor(R.color.light_blue_button))
                setTextColor(requireContext().getColor(R.color.dark_green_text))

                layoutParams = ViewGroup.MarginLayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
                ).apply {
                    topMargin = 24
                }

                // ACCION AL HACER CLICK EN UN TURNO
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

        // ACA TIENEN QUE REEMPLAZAR CON LA URL QUE SACARON AL LEER NOTAS.TXT
        val url = "https://unseeking-acrimoniously-melodee.ngrok-free.dev/api/get_aulas.php?esc_id=$escuelaId"

        // SE CREA LA PETICION GET PARA OBTENER LAS AULAS
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

    // FUNCION PARA CREAR BTNES DE AULAS
    private fun crearBotonesDeAulas(aulas: List<Aula>) {
        binding.containerAulas.removeAllViews() // Limpia botones anteriores

        aulas.forEach { aula ->
            val botonAula = MaterialButton(requireContext()).apply {
                text = aula.num_aula.toString()

                // Estilo del botón de aula (colores de la carpeta theme)
                setBackgroundColor(requireContext().getColor(R.color.light_blue_button))
                setTextColor(requireContext().getColor(R.color.dark_green_text))
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
                    // Aqui le pasas el ID del aula para que te mande a otra pantalla
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
