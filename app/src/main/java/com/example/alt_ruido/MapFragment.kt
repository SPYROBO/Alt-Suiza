package com.example.alt_ruido

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.alt_ruido.databinding.FragmentMapBinding
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import org.json.JSONArray
import org.json.JSONException

//OnMapReadyCallback para que el sistema avise cuando el mapa esté listo.
class MapFragment : Fragment(), OnMapReadyCallback {

    private var _binding: FragmentMapBinding? = null
    private val binding get() = _binding!!

    // Se guarda una referencia al mapa para poder usarlo en toda la clase.
    private var mapa: GoogleMap? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentMapBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Busca el SupportMapFragment en el XML y le pedimos que nos
        // notifique de forma asíncrona cuando esté listo para ser usado.
        val mapFragment = childFragmentManager.findFragmentById(R.id.map_fragment_container) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    /**
     * Este es el método callback que se ejecuta cuando el mapa finalmente está cargado.
     */
    override fun onMapReady(googleMap: GoogleMap) {
        mapa = googleMap // Se guarda la referencia al mapa.

        // Se configura la posición inicial de la cámara.
        // apuntando al centro aproximado de CABA y estableciendo un nivel de zoom.
        val camaraInicial = LatLng(-34.6037, -58.3816)
        mapa?.moveCamera(CameraUpdateFactory.newLatLngZoom(camaraInicial, 12f))

        // Ahora que el mapa está listo, llamamos a nuestra función para pedir los datos al servidor.
        obtenerYMarcarEscuelas()
    }

    /**
     * Usa Volley para hacer una petición de red, obtener la lista de escuelas
     * y dibujar un marcador para cada una en el mapa.
     */
    private fun obtenerYMarcarEscuelas() {
        val url = "https://unseeking-acrimoniously-melodee.ngrok-free.dev/api/get_escuelas.php"

        // log de verificacion del llamado
        Log.d("MapDebug", "Iniciando petición a Volley a la URL: $url")

        val request = StringRequest(
            Request.Method.GET, url,
            { response ->
                // --> log respuesta del servidor
                Log.d("MapDebug", "Respuesta del servidor recibida.")

                try {
                    val jsonArray = JSONArray(response)
                    // --> verificar el tamaño del JSON.
                    Log.d("MapDebug", "Se encontraron ${jsonArray.length()} escuelas en el JSON.")

                    if (jsonArray.length() == 0) {
                        Toast.makeText(context, "El servidor no devolvió ninguna escuela.", Toast.LENGTH_LONG).show()
                        return@StringRequest // Sale de la función si no hay datos.
                    }

                    for (i in 0 until jsonArray.length()) {
                        val jsonObject = jsonArray.getJSONObject(i)
                        val nombre = jsonObject.getString("nombre")

                        // Verificacion de las coordenadas.
                        if (jsonObject.has("point_y") && jsonObject.has("point_x") && !jsonObject.isNull("point_y") && !jsonObject.isNull("point_x")) {
                            val latitud = jsonObject.getDouble("point_y")
                            val longitud = jsonObject.getDouble("point_x")

                            // --> log de verificacion de datos.
                            Log.d("MapDebug", "Añadiendo marcador: '$nombre' en ($latitud, $longitud)")

                            val posicion = LatLng(latitud, longitud)

                            // operaciones sobre la UI del mapa deben hacerse en el hilo principal.
                            activity?.runOnUiThread {
                                mapa?.addMarker(
                                    MarkerOptions()
                                        .position(posicion)
                                        .title(nombre) // Texto con el nombre de la escuela al tocar el marcador.
                                )
                            }
                        } else {
                            // --> log cordenadas nulas.
                            Log.w("MapDebug", "La escuela '$nombre' no tiene coordenadas (point_x/point_y) o son nulas.")
                        }
                    }
                } catch (e: JSONException) {
                    // --> log de error en el json.
                    Log.e("MapDebug", "Error fatal al procesar JSON: ${e.message}", e)
                    Toast.makeText(context, "Error al procesar los datos del servidor.", Toast.LENGTH_LONG).show()
                }
            },
            { error ->
                // --> log de fallo de red.
                Log.e("MapDebug", "Error de red con Volley: ${error.message}", error)
                Toast.makeText(context, "Error de red. No se pudo conectar.", Toast.LENGTH_LONG).show()
            }
        )

        // Se añade la peticion a la cola de Volley para que se ejecute.
        Volley.newRequestQueue(requireContext()).add(request)
    }

    /**
     * Limpiamos la referencia al binding cuando la vista del fragmento se destruye
     * para evitar fugas de memoria (memory leaks).
     */
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
