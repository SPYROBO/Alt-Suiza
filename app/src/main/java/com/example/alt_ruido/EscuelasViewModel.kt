package com.example.alt_ruido

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.android.volley.DefaultRetryPolicy
import com.android.volley.Request
import com.android.volley.toolbox.JsonArrayRequest
import org.json.JSONException

// ACA SE OBTIENEN LOS DATOS DE LA BD Y SON ENVIADOS A EscuelasAdapter
class EscuelasViewModel : ViewModel() {

    // CONTIENE LA LISTA DE ESCUELAS (privada para que solo el ViewModel pueda modificarla)
    // '_escuelas' está escrito así por una convencion de Kotlin: variable interna
    private val _escuelas = MutableLiveData<List<Escuela>>() // mutablelivedata porque se puede cambiar el valor que contiene

    // LISTA SOLO DE LECTURA (no mutable)
    val escuelas: LiveData<List<Escuela>> get() = _escuelas

    private val _error = MutableLiveData<String>()
    val error: LiveData<String> get() = _error

    // --- URL PARA ACCEDER A LA API---
    private val url = "https://unseeking-acrimoniously-melodee.ngrok-free.dev/api/get_escuelas.php"

    // FUNCION PARA CARGAR ESCUELAS
    fun cargarEscuelas(context: Context) {
        // Crea una peticion de red, si tiene exito procesa el JSON
        val jsonArrayRequest = JsonArrayRequest(
            Request.Method.GET, url, null,
            { response ->
                try {
                    val listaMutable = mutableListOf<Escuela>()
                    for (i in 0 until response.length()) {
                        val jsonObject = response.getJSONObject(i)
                        val escuela = Escuela(
                            id = jsonObject.optInt("id"),
                            nombre = jsonObject.optString("nombre", "Sin nombre"),
                            cue = jsonObject.optInt("cue"),
                            point_x = jsonObject.optDouble("point_x"),
                            point_y = jsonObject.optDouble("point_y"),
                            calle = jsonObject.optString("calle", "Sin calle"),
                            num_calle = jsonObject.optInt("num_calle"),
                            jornada = jsonObject.optString("jornada", "No especificada"),
                            sector = jsonObject.optString("sector", "No especificado"),
                            barrio = jsonObject.optString("barrio", "No especificado"),
                            comuna = jsonObject.optInt("comuna"),
                            clave_rama = jsonObject.optString("clave_rama", "N/A"),
                            mail = jsonObject.optString("mail", "Sin email")
                        )
                        listaMutable.add(escuela)
                    }
                    _escuelas.value = listaMutable
                } catch (e: JSONException) {
                    _error.value = "Error al procesar la respuesta del servidor: ${e.message}"
                }
            },
            { error ->
                _error.value = "Fallo de conexión con ngrok: ${error.message}"
            }
        )

        // Le damos 10 segundos de tiempo de espera, por si la conexión es lenta.
        jsonArrayRequest.retryPolicy = DefaultRetryPolicy(
            10000,
            DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
            DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        )

        // SE ENVIA LA PETICION
        VolleySingleton.getInstance(context).addToRequestQueue(jsonArrayRequest)
    }
}
