package com.example.alt_ruido

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.alt_ruido.databinding.FragmentEscuelasListBinding
import org.json.JSONArray

// SE ENCARGA DE MOSTRAR LA LISTA DE ESCUELAS
class EscuelasListFragment : Fragment() {

    private var _binding: FragmentEscuelasListBinding? = null
    private val binding get() = _binding!!

    // SE GUARDA UNA COPIA DE LA LISTA, para q el buscador tenga acceso más fácil
    private var listaCompletaDeEscuelas = listOf<Escuela>()
    private lateinit var adapter: EscuelasAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentEscuelasListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView() // configura la lista
        setupSearchView() // Configura el buscador
        obtenerEscuelas()//pide los datos al sv
    }

    private fun setupRecyclerView() {
        adapter = EscuelasAdapter(listOf())
        // le decimos que agregue las tarjetas una abajo de la otra
        binding.recyclerViewEscuelas.layoutManager = LinearLayoutManager(context)
        // se conecta el recyclerView con el adapter
        binding.recyclerViewEscuelas.adapter = adapter
    }

    private fun setupSearchView() {
        binding.recyclerViewEscuelas.isFocusable = false
        binding.searchView.isFocusable = true
        binding.searchView.isIconified = false
        binding.searchView.clearFocus()

        // avisa de los cambios en el search
        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                // se oculta el teclado cuando se presiona 'buscar'
                binding.searchView.clearFocus()
                return true
            }
// se llama cada que el teclado cambia
            override fun onQueryTextChange(newText: String?): Boolean {
                filtrarLista(newText)
                return true
            }
        })
    }

    private fun filtrarLista(texto: String?) {

        // si el buscador está vacio, la lista se muestra entera
        val listaParaMostrar = if (texto.isNullOrEmpty()) {
            listaCompletaDeEscuelas
        } else {
            // se realiza comparaciones en minuscula y sin espacios
            val textoBusqueda = texto.lowercase().trim()
            listaCompletaDeEscuelas.filter { escuela ->
                escuela.nombre.lowercase().contains(textoBusqueda)
            }
        }
        adapter.updateData(listaParaMostrar)
    }

    private fun obtenerEscuelas() {
        // muestra el ProgressBar y ocultar el contenedor principal del contenido.
        binding.progressBar.visibility = View.VISIBLE
        binding.contentContainer.visibility = View.GONE

        val url = "https://unseeking-acrimoniously-melodee.ngrok-free.dev/api/get_escuelas.php"
        val request = StringRequest(
            Request.Method.GET, url,
            { response ->
                //  Al recibir datos, ocultar el ProgressBar y mostrar el contenedor principal.
                binding.progressBar.visibility = View.GONE
                binding.contentContainer.visibility = View.VISIBLE

                try {
                    val jsonArray = JSONArray(response)
                    val tempList = mutableListOf<Escuela>()
                    for (i in 0 until jsonArray.length()) {
                        val jsonObject = jsonArray.getJSONObject(i)
                        tempList.add(
                            Escuela(
                                id = jsonObject.getInt("id"),
                                nombre = jsonObject.getString("nombre"),
                                cue = jsonObject.getInt("cue"),
                                point_x = jsonObject.getDouble("point_x"),
                                point_y = jsonObject.getDouble("point_y"),
                                num_calle = jsonObject.getInt("num_calle"),
                                calle = jsonObject.getString("calle"),
                                jornada = jsonObject.getString("jornada"),
                                sector = jsonObject.getString("sector"),
                                barrio = jsonObject.getString("barrio"),
                                comuna = jsonObject.getInt("comuna"),
                                clave_rama = jsonObject.getString("clave_rama"),
                                mail = jsonObject.getString("mail")
                            )
                        )
                    }
                    listaCompletaDeEscuelas = tempList
                    adapter.updateData(listaCompletaDeEscuelas)

                } catch (e: Exception) {
                    Toast.makeText(context, "Error al procesar datos: ${e.message}", Toast.LENGTH_LONG).show()
                    // Si hay error al procesar, también mostramos la pantalla vacía (sin el loading)
                    binding.progressBar.visibility = View.GONE
                }
            },
            { error ->
                // Ocultar el ProgressBar también si hay un error de red.
                binding.progressBar.visibility = View.GONE
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
